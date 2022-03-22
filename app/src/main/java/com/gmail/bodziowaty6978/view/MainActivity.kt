package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMainBinding
import com.gmail.bodziowaty6978.functions.*
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.UserInformationState
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.gmail.bodziowaty6978.view.introduction.IntroductionActivity
import com.gmail.bodziowaty6978.view.mainfragments.*
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivityMainBinding

    val viewModel: MainViewModel by viewModels()

    private val summary = SummaryFragment()
    private val diary = DiaryFragment()
    private val settings = SettingsFragment()
    private val splashFragment = SplashFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(splashFragment)

        if (viewModel.isUserLogged()) {
            lifecycleScope.launchWhenStarted {
                viewModel.userInformation.observe(this@MainActivity,{
                    viewModel.checkUser()
                })
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        lifecycleScope.launch {
            viewModel.userInformationState.observe(this@MainActivity, {
                when (it) {
                    is UserInformationState.HasInformation -> {
                        viewModel.requireData(CurrentDate.date().value!!.toShortString())
                    }
                    is UserInformationState.NoInformation -> onNoInformation()
                    else -> Log.e(TAG, "Getting user information")
                }
            })
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

    private fun onSuccess() {
        setFragment(summary)
        binding.rlCalendar.visibility = View.VISIBLE
        binding.bnvMain.visibility = View.VISIBLE
        setUpBottomNav()
        setUpCalendar()
        observeDate()
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
                    R.id.menu_settings -> {
                        setFragment(settings)
                        binding.rlCalendar.visibility = View.GONE
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



    private fun observeDate() {
        lifecycleScope.launch {
            CurrentDate.date().observe(this@MainActivity, {
                binding.tvDateCalendar.text = getDateInAppFormat(it)
                val dateString = it.toShortString()
                viewModel.refreshJournalEntries(dateString)
            })
        }
    }









}