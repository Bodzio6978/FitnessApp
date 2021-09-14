package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMainBinding
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
        viewModel.getFragment().observe(this,{
            setFragment(it)
        })

        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_calories -> {
                    viewModel.setFragment(viewModel.getCalories())
                }
                R.id.menu_training -> {
                    viewModel.setFragment(viewModel.getTraining())
                }
                R.id.menu_recipes -> {
                    viewModel.setFragment(viewModel.getRecipes())
                }
                R.id.menu_shopping -> {
                    viewModel.setFragment(viewModel.getShopping())

                }
                R.id.menu_settings -> {
                    viewModel.setFragment(viewModel.getSettings())
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