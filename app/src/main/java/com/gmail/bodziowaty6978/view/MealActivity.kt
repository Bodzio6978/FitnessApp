package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMealBinding
import com.gmail.bodziowaty6978.model.Meal
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

        viewModel.getCurrentMeal().observe(this,{
            initializeUi(it)
        })

        binding.tvMealNameMeal.text = intent.getStringExtra("mealName")

        viewModel.getMeal(intent.getStringExtra("mealId").toString())

        binding.ibBackMeal.setOnClickListener {
            super.onBackPressed()
        }

    }

    private fun initializeUi(meal: Meal){
        binding.tvProductNameMeal.text = meal.name
        binding.tvFatValueMeal.text = meal.fat
        binding.tvCarbsValueMeal.text = meal.carbs
        binding.tvProteinValueMeal.text = meal.protein
        binding.tvCaloriesValueMeal.text = meal.calories
        binding.tvBrandMeal.text = meal.brand
    }
}