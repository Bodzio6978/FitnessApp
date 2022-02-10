package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMainBinding
import com.gmail.bodziowaty6978.functions.*
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.UserInformationState
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.gmail.bodziowaty6978.view.introduction.IntroductionActivity
import com.gmail.bodziowaty6978.view.mainfragments.DiaryFragment
import com.gmail.bodziowaty6978.view.mainfragments.SplashFragment
import com.gmail.bodziowaty6978.view.mainfragments.SummaryFragment
import com.gmail.bodziowaty6978.view.mainfragments.TrainingFragment
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivityMainBinding

    val viewModel: MainViewModel by viewModels()

    private val summary = SummaryFragment()
    private val diary = DiaryFragment()
    private val training = TrainingFragment()
    private val splashFragment = SplashFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(splashFragment)



        if (viewModel.isUserLogged()) {
            viewModel.checkUser()
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.userInformationState.collect {
                when (it) {
                    is UserInformationState.HasInformation -> {
                        viewModel.requireData(CurrentDate.date().value!!.toShortString())
                    }
                    is UserInformationState.NoInformation -> onNoInformation()
                    else -> Log.e(TAG, "Getting user information")
                }
            }
        }

        observeUiState()
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.dataState.collect {
                if (it is DataState.Success) onSuccess()
            }
        }
    }

    private fun onNoInformation() {
        startActivity(Intent(this, IntroductionActivity::class.java))
        finish()
    }

    private fun onError(state: DataState.Error) {
        Snackbar.make(binding.clMain, state.errorMessage, Snackbar.LENGTH_SHORT).show()
    }

    private fun onSuccess() {
        setFragment(summary)
        binding.rlCalendar.visibility = View.VISIBLE
        binding.bnvMain.visibility = View.VISIBLE
        setUpBottomNav()
        setUpCalendar()
        observeDate()
        observeUser()
    }


    private fun setUpCalendar() {
        binding.ibNextCalendar.setOnClickListener {
            CurrentDate.addDay()
        }

        binding.ibBackCalendar.setOnClickListener {
            CurrentDate.deductDay()
        }
    }


    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.apply {
            beginTransaction().replace(R.id.main_fl, fragment).commit()
        }
    }

    private fun setUpBottomNav() {
        lifecycleScope.launch {
            binding.bnvMain.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_summary -> {
                        setFragment(summary)
                        binding.rlCalendar.visibility = View.GONE
                    }
                    R.id.menu_diary -> {
                        setFragment(diary)
                        binding.rlCalendar.visibility = View.VISIBLE
                    }
                    R.id.menu_training -> {
                        setFragment(training)
                        binding.rlCalendar.visibility = View.VISIBLE
                    }
                }
                true
            }

            when (intent.getIntExtra("position", 0)) {
                0 -> binding.bnvMain.selectedItemId = R.id.menu_summary
                1 -> binding.bnvMain.selectedItemId = R.id.menu_diary
            }
        }
    }

    private fun observeUser() {
        lifecycleScope.launch {
            UserInformation.user.observe(this@MainActivity, {
                checkIfWeightDialogEnabled(it.areWeightDialogsEnabled)
            })
        }
    }

    private fun observeDate() {
        lifecycleScope.launch {
            CurrentDate.date().observe(this@MainActivity, {
                binding.tvDateCalendar.text = getDateInAppFormat(it)
                val dateString = it.toShortString()
                viewModel.refreshJournalEntries(dateString)
            })
        }
    }


    private fun checkIfWeightDialogEnabled(areEnabled: Boolean?) {
        if (areEnabled == null) {
            askForWeightDialogs()
        } else if (areEnabled) {
            observeWeightToday()
        }
    }

    private fun askForWeightDialogs() {
        MaterialAlertDialogBuilder(this@MainActivity).apply {
            setTitle(R.string.do_you_want_us_to_help_you_track_your_weight)
            setMessage(R.string.we_will_ask_you_everyday_about_your_weight_automatically)
            setCancelable(false)
            setPositiveButton(R.string.accept) { _, _ ->
                viewModel.setDialogPermission(true)
            }

            setNegativeButton(R.string.decline) { _, _ ->
                viewModel.setDialogPermission(false)
            }
            show()
        }


    }

    private fun observeWeightToday() {
        lifecycleScope.launch {
            viewModel.weightEntries.observe(this@MainActivity, { weightEntries ->
                if (!viewModel.checkIfWeightHasBeenEnteredToday(weightEntries)) {

                    val value: Double =
                        if (weightEntries.isEmpty()) {
                            UserInformation.user.value!!.userInformation!!["currentWeight"]!!.toDouble()
                        } else {
                            weightEntries.sortByDescending { it.time }
                            weightEntries[0].value
                        }

                    showNumberPickerDialog(
                        value = value, // in kilograms
                        formatToString = { "${it.round(1)} kg" }
                    )

                }
            })
        }
    }

    private fun showNumberPickerDialog(
        value: Double,
        formatToString: (Double) -> String
    ) {
        val inflater = this.layoutInflater

        val layout = inflater.inflate(R.layout.weight_picker_layout, null)

        val builder = MaterialAlertDialogBuilder(this).apply {
            setView(layout)
            setCancelable(false)
        }

        val picker = layout.findViewById(R.id.npWeightPicker) as NumberPicker
        picker.apply {
            setFormatter { formatToString(it.toDouble() * 0.1) }
            wrapSelectorWheel = false

            minValue = (10.0 / 0.1).toInt()
            maxValue = (200.0 / 0.1).toInt()
            this.value = (value / 0.1).toInt()

            (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }
                .get(this) as EditText).filters = emptyArray()
        }

        val dialog = builder.show()

        layout.findViewById<Button>(R.id.btSaveWeightPicker).apply {
            setOnClickListener {
                val currentValue = (picker.value.toDouble() * 0.1).round(1)
                viewModel.setWeightEntry(currentValue)
                dialog.dismiss()
            }
        }

        layout.findViewById<Button>(R.id.btCancelWeightPicker).apply {
            val currentValue = (picker.value.toDouble() * 0.1).round(1)
            setOnClickListener {
                viewModel.setWeightEntry(currentValue)
                dialog.dismiss()
            }
        }
    }
}