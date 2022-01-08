package com.gmail.bodziowaty6978.view

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMainBinding
import com.gmail.bodziowaty6978.functions.getCurrentDateTime
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.view.mainfragments.DiaryFragment
import com.gmail.bodziowaty6978.view.mainfragments.SummaryFragment
import com.gmail.bodziowaty6978.view.mainfragments.TrainingFragment
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    private val summary = SummaryFragment()
    private val diary = DiaryFragment()
    private val training = TrainingFragment()

    private val WEIGHT_PREF = "appWeightPref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        CurrentDate.date.value = getCurrentDateTime()

        checkIfWeightDialogEnabled()

        showNumberPickerDialog(
                value = 75.0, // in kilograms
                range = 10.0 .. 300.0,
                stepSize = 0.1,
                formatToString = { "${it.round(1)} kg" }
        )

        binding.ibNextCalendar.setOnClickListener {
            CurrentDate.addDay()
        }

        binding.ibBackCalendar.setOnClickListener {
            CurrentDate.deductDay()
        }

        CurrentDate.date.observe(this, {
            binding.tvDateCalendar.text = getDateInAppFormat(it)
        })

        setUpBottomNav()

    }

    private fun setUpBottomNav() {
        binding.rlCalendar.visibility = View.GONE

        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_summary -> {
                    binding.rlCalendar.visibility = View.GONE
                    setFragment(summary)
                }
                R.id.menu_diary -> {
                    binding.rlCalendar.visibility = View.VISIBLE
                    setFragment(diary)
                }
                R.id.menu_training -> {
                    binding.rlCalendar.visibility = View.VISIBLE
                    setFragment(training)
                }
            }
            true
        }

        binding.bnvMain.selectedItemId = R.id.menu_diary
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.apply {
            beginTransaction().replace(R.id.main_fl, fragment).commit()
        }
    }

    private fun checkIfWeightDialogEnabled() {
        val areEnabled = UserInformation.getUser().value?.areWeightDialogsEnabled

        if (areEnabled == null){
            askForWeightDialogs()
        }else if(areEnabled){
            checkIfWeightHasBeenEnteredToday()
        }

    }

    private fun askForWeightDialogs() {
        MaterialAlertDialogBuilder(this).apply {
            setTitle(R.string.do_you_want_us_to_help_you_track_your_weight)
            setMessage(R.string.we_will_ask_you_everyday_about_your_weight_automatically)
            setCancelable(false)
            setPositiveButton(R.string.accept) { _, _ ->
                viewModel.setDialogPermission(true)

                viewModel.getHasBeenSet().observe(this@MainActivity,{
                    if (it){
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

    private fun checkIfWeightHasBeenEnteredToday(){

    }


    private fun showNumberPickerDialog(
        value: Double,
        range: ClosedRange<Double>,
        stepSize: Double,
        formatToString: (Double) -> String
    ) {
        val inflater = this.layoutInflater

        val layout = inflater.inflate(R.layout.weight_picker_layout, null)

        val dialog = MaterialAlertDialogBuilder(this).apply {
            setView(layout)
            setCancelable(true)
        }

        val picker = layout.findViewById(R.id.npWeightPicker) as NumberPicker
        picker.apply {
            setFormatter { formatToString(it.toDouble() * stepSize) }
            wrapSelectorWheel = false

            minValue = (range.start / stepSize).toInt()
            maxValue = (range.endInclusive / stepSize).toInt()
            this.value = (value / stepSize).toInt()

            (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }
                .get(this) as EditText).filters = emptyArray()
        }

        dialog.show()
    }

}