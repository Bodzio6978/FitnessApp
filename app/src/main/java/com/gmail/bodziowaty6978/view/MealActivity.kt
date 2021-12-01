package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityMealBinding
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.viewmodel.MealViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
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

        viewModel.getAddingState().observe(this,{
            if (it){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
        })

        CurrentDate.date.observe(this,{
            binding.tvDateMeal.text = getDateInAppFormat(it)
        })

        val mealName = intent.getStringExtra("mealName")

        binding.tvMealNameMeal.text = mealName

        val id = intent.getStringExtra("key")

        viewModel.getMeal(id.toString())

        binding.ibBackMeal.setOnClickListener {
            super.onBackPressed()
        }

        binding.btAddNew.setOnClickListener{
            viewModel.addMeal(id.toString(),binding.etWeightMeal.text.toString(),mealName.toString())
        }

    }

    private fun initializeUi(meal: Product){
        binding.tvProductNameMeal.text = meal.name
        binding.tvFatValueMeal.text = meal.fat.toString()
        binding.tvCarbsValueMeal.text = meal.carbs.toString()
        binding.tvProteinValueMeal.text = meal.protein.toString()
        binding.tvCaloriesValueMeal.text = meal.calories.toString()
        binding.tvBrandMeal.text = meal.brand
    }
}