package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.singleton.InformationState
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.gmail.bodziowaty6978.view.auth.UsernameActivity
import com.gmail.bodziowaty6978.view.introduction.IntroductionActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        CurrentDate.date.value = getCurrentDateTime()

//        showNumberPickerDialog(
//                value = 75.0, // in kilograms
//                range = 10.0 .. 300.0,
//                stepSize = 0.1,
//                formatToString = { "${it.round(1)} kg" }
//        )

        binding.ibNextCalendar.setOnClickListener {
            CurrentDate.addDay()
        }

        binding.ibBackCalendar.setOnClickListener {
            CurrentDate.deductDay()
        }

        CurrentDate.date.observe(this,{
            binding.tvDateCalendar.text = getDateInAppFormat(it)
        })

        checkUserInformation()
    }

    private fun setUpBottomNav(){
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

    private fun checkUserInformation(){
        UserInformation.getUserId()

        UserInformation.mInformationState.observe(this,{
            when(it.value){
                InformationState.USER_NOT_LOGGED -> {
                    startActivity(Intent(this,LoginActivity::class.java))
                    finish()
                }

                InformationState.USER_LOGGED -> UserInformation.getValues()

                InformationState.USER_INFORMATION_REQUIRED -> UserInformation.checkUser()

                InformationState.USER_NO_USERNAME -> {
                    startActivity(Intent(this,UsernameActivity::class.java))
                    finish()
                }

                InformationState.USER_NO_INFORMATION -> {
                    startActivity(Intent(this,IntroductionActivity::class.java))
                    finish()
                }

                InformationState.USER_HAS_EVERYTHING -> setUpBottomNav()

            }
        })
    }

    private fun showNumberPickerDialog(
            value: Double,
            range: ClosedRange<Double>,
            stepSize: Double,
            formatToString: (Double) -> String
    ) {
        val inflater = this.layoutInflater

        val layout = inflater.inflate(R.layout.weight_picker_layout,null)

        val dialog = MaterialAlertDialogBuilder(this).apply {
            background = ColorDrawable(Color.TRANSPARENT)
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

            (NumberPicker::class.java.getDeclaredField("mInputText").apply { isAccessible = true }.get(this) as EditText).filters = emptyArray()
        }

        dialog.show()
    }

}