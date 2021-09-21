package com.gmail.bodziowaty6978.customviews

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.adapters.CaloriesRecyclerAdapter
import com.gmail.bodziowaty6978.databinding.MealViewBinding
import com.gmail.bodziowaty6978.model.Meal
import com.gmail.bodziowaty6978.view.AddActivity

class MealView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){

    lateinit var binding: MealViewBinding

    init {
        val binding = MealViewBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)

        binding.rvMeal.layoutManager = LinearLayoutManager(context)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MealView)
        binding.tvNameMeal.text = attributes.getString(R.styleable.MealView_mealName)
        binding.tvKcalValueMeal.text = attributes.getInteger(R.styleable.MealView_kcalValue, 0).toString()
        binding.tvCarbsValueMeal.text = attributes.getInteger(R.styleable.MealView_carbValue, 0).toString()
        binding.tvProteinValueMeal.text = attributes.getInteger(R.styleable.MealView_protValue, 0).toString()
        binding.tvFatValueMeal.text = attributes.getInteger(R.styleable.MealView_fatValue, 0).toString()

        attributes.recycle()



        binding.fabMeal.setOnClickListener {
            val intent = Intent(context, AddActivity::class.java)
            context.startActivity(intent)

        }
    }

    fun setAdapter(list:MutableList<Meal>){
        binding.rvMeal.adapter = CaloriesRecyclerAdapter(list)
    }

}