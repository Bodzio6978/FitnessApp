package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMainBinding
import com.gmail.bodziowaty6978.functions.getCurrentDateTime
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.gmail.bodziowaty6978.view.auth.UsernameActivity
import com.gmail.bodziowaty6978.view.introduction.IntroductionActivity
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import com.gmail.bodziowaty6978.viewmodel.UserState
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
class MainActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.checkInformation()

        binding.ibNextCalendar.setOnClickListener {
            CurrentDate.addDay()
        }

        binding.ibBackCalendar.setOnClickListener {
            CurrentDate.deductDay()
        }

        CurrentDate.date.observe(this,{
            binding.tvDateCalendar.text = getDateInAppFormat(it)
        })

        CurrentDate.date.value = getCurrentDateTime()

        viewModel.getUserState().observe(this,{
            when(it.value){
                UserState.USER_NOT_LOGGED -> {
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                UserState.USER_NO_USERNAME -> {
                    val intent = Intent(this,UsernameActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                UserState.USER_NO_INFORMATION -> {
                    val intent = Intent(this,IntroductionActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        })

        setFragment(viewModel.getCalories())

        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_calories -> {
                    binding.rlCalendar.visibility = View.VISIBLE
                    setFragment(viewModel.getCalories())
                }
                R.id.menu_training -> {
                    binding.rlCalendar.visibility = View.VISIBLE
                    setFragment(viewModel.getTraining())
                }
                R.id.menu_recipes -> {
                    binding.rlCalendar.visibility = View.GONE
                    setFragment(viewModel.getRecipes())
                }
                R.id.menu_shopping -> {
                    binding.rlCalendar.visibility = View.VISIBLE
                    setFragment(viewModel.getShopping())

                }
                R.id.menu_settings -> {
                    binding.rlCalendar.visibility = View.GONE
                    setFragment(viewModel.getSettings())
                }
            }
            true
        }

    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.apply {
            beginTransaction().replace(R.id.main_fl, fragment).commit()
        }
    }
}