package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMealBinding
import com.gmail.bodziowaty6978.viewmodel.MealViewModel

class MealActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivityMealBinding
    lateinit var viewModel: MealViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal)

        binding = ActivityMealBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(MealViewModel::class.java)

    }
}