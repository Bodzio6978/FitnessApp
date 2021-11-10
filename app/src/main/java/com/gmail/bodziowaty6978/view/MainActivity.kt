package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMainBinding
import com.gmail.bodziowaty6978.view.introduction.IntroductionActivity
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val intent = Intent(this, IntroductionActivity::class.java)
        startActivity(intent)

        setFragment(viewModel.getCalories())

        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_calories -> {
                    setFragment(viewModel.getCalories())
                }
                R.id.menu_training -> {
                    setFragment(viewModel.getTraining())
                }
                R.id.menu_recipes -> {
                    setFragment(viewModel.getRecipes())
                }
                R.id.menu_shopping -> {
                    setFragment(viewModel.getShopping())

                }
                R.id.menu_settings -> {
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