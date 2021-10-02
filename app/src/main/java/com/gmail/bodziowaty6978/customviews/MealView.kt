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

    var binding: MealViewBinding = MealViewBinding.inflate(LayoutInflater.from(context))
    private var mealList: MutableList<Meal> = mutableListOf()

    init {
        addView(binding.root)


        binding.rvMeal.layoutManager = LinearLayoutManager(context)
        binding.rvMeal.adapter = CaloriesRecyclerAdapter(mealList)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MealView)
        binding.tvNameMeal.text = attributes.getString(R.styleable.MealView_mealName)
        binding.tvKcalValueMeal.text = attributes.getInteger(R.styleable.MealView_kcalValue, 0).toString()
        binding.tvCarbsValueMeal.text = attributes.getInteger(R.styleable.MealView_carbValue, 0).toString()
        binding.tvProteinValueMeal.text = attributes.getInteger(R.styleable.MealView_protValue, 0).toString()
        binding.tvFatValueMeal.text = attributes.getInteger(R.styleable.MealView_fatValue, 0).toString()

        attributes.recycle()



        binding.fabMeal.setOnClickListener {
            val intent = Intent(context, AddActivity::class.java).putExtra("mealName",binding.tvNameMeal.text.toString())
            context.startActivity(intent)

        }
    }

    fun addMeal(item:Meal){
        mealList.add(item)
        binding.rvMeal.adapter?.notifyDataSetChanged()
    }

}