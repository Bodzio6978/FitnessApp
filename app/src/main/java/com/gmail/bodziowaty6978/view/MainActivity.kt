package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMainBinding
import com.gmail.bodziowaty6978.functions.*
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.state.UserInformationState
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.gmail.bodziowaty6978.view.auth.UsernameActivity
import com.gmail.bodziowaty6978.view.introduction.IntroductionActivity
import com.gmail.bodziowaty6978.view.mainfragments.DiaryFragment
import com.gmail.bodziowaty6978.view.mainfragments.SummaryFragment
import com.gmail.bodziowaty6978.view.mainfragments.TrainingFragment
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.*

class MainActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    private val summary = SummaryFragment()
    private val diary = DiaryFragment()
    private val training = TrainingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        observeUserInformationState()

        val currentUser = FirebaseAuth.getInstance().currentUser
        checkIfUserIsLogged(currentUser)
    }

    private fun checkIfUserIsLogged(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            UserInformation.getValues(currentUser.uid)
        }
    }

    private fun observeUserInformationState(){
        UserInformation.mUserInformationState.observe(this, {
            when (it.value) {
                UserInformationState.USER_INFORMATION_REQUIRED -> UserInformation.checkInformation()
                UserInformationState.USER_HAS_INFORMATION -> UserInformation.checkUsername()

                UserInformationState.USER_NO_INFORMATION -> {
                    startActivity(Intent(this, IntroductionActivity::class.java))
                    finish()
                }

                UserInformationState.USER_NO_USERNAME -> {
                    startActivity(Intent(this, UsernameActivity::class.java))
                    finish()
                }

                UserInformationState.USER_HAS_USERNAME -> {
                    setUpBottomNav()
                    setUpCalendar()
                    observeDate()
                    checkIfWeightDialogEnabled()
                }
            }
        })
    }

    private fun setUpCalendar() {
        CurrentDate.date.value = getCurrentDateTime()

        binding.ibNextCalendar.setOnClickListener {
            CurrentDate.addDay()
        }

        binding.ibBackCalendar.setOnClickListener {
            CurrentDate.deductDay()
        }

        CurrentDate.date.observe(this, {
            binding.tvDateCalendar.text = getDateInAppFormat(it)
        })
    }


    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.apply {
            beginTransaction().replace(R.id.main_fl, fragment).commit()
        }
    }

    private fun setUpBottomNav() {
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

        binding.bnvMain.selectedItemId = R.id.menu_summary
    }

    private fun observeDate(){
        CurrentDate.date.observe(this,{
            val dateString = it.time.toString("dd-MM-yyyy")

            viewModel.downloadJournalEntries(dateString)
        })
    }


    private fun checkIfWeightDialogEnabled() {
        val areEnabled = UserInformation.mAreWeightDialogsEnabled.value

        if (areEnabled == null) {
            askForWeightDialogs()
        } else if (areEnabled) {
            viewModel.checkIfWeightHasBeenEnteredToday()
            observeWeightToday()
        }

    }

    private fun askForWeightDialogs() {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(R.string.do_you_want_us_to_help_you_track_your_weight)
            setMessage(R.string.we_will_ask_you_everyday_about_your_weight_automatically)
            setCancelable(false)
            setPositiveButton(R.string.accept) { _, _ ->
                viewModel.setDialogPermission(true)

                viewModel.mHasPermissionBeenSet.observe(this@MainActivity, {
                    if (it) {
                        checkIfWeightDialogEnabled()
                    }
                })
            }

            setNegativeButton(R.string.decline) { _, _ ->
                viewModel.setDialogPermission(false)
            }
            show()
        }
    }

    private fun observeWeightToday() {
        viewModel.mHasTodayWeightBeenEntered.observe(this, { hasBeenEnteredToday ->
            if (!hasBeenEnteredToday) {
                val lastWeightEntries = viewModel.mLastWeights.value

                val value: Double = if (lastWeightEntries==null||lastWeightEntries.isEmpty()){
                    UserInformation.mUserInformation.value!!["currentWeight"]!!.toDouble()
                }else{
                    lastWeightEntries.sortByDescending { it.time }
                    lastWeightEntries[0].value
                }
                
                showNumberPickerDialog(
                        value = value, // in kilograms
                        formatToString = { "${it.round(1)} kg" }
                )
            }
        })


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

        layout.findViewById<Button>(R.id.btSaveWeightPicker).apply {
            setOnClickListener {
                val currentValue = picker.value.toDouble()*0.1
                viewModel.setTodayWeight(currentValue.round(1))
            }
        }

        val dialog = builder.show()

        viewModel.mHasWeightBeenSet.observe(this@MainActivity,{
            Log.e(TAG,it.toString())
            if (it){
                dialog.dismiss()
            }else{
                dialog.dismiss()
                Snackbar.make(binding.clMain,R.string.something_went_wrong, Snackbar.LENGTH_LONG).show()
            }
        })

        layout.findViewById<Button>(R.id.btCancelWeightPicker).apply {
            setOnClickListener {
                dialog.dismiss()

            }
        }

    }

}