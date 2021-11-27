package com.gmail.bodziowaty6978.customviews

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.adapters.CaloriesRecyclerAdapter
import com.gmail.bodziowaty6978.databinding.MealViewBinding
import com.gmail.bodziowaty6978.interfaces.OnAdapterItemClickListener
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.view.AddActivity
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class MealView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs),OnAdapterItemClickListener{

    var binding: MealViewBinding = MealViewBinding.inflate(LayoutInflater.from(context))
    private var entriesList:MutableList<JournalEntry> = mutableListOf()

    private val data = MutableLiveData<ArrayList<Int>>()

    init {
        addView(binding.root)


        binding.rvMeal.layoutManager = LinearLayoutManager(context)
        binding.rvMeal.adapter = CaloriesRecyclerAdapter(entriesList,this)


        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MealView)
        binding.tvNameMealView.text = attributes.getString(R.styleable.MealView_mealName)
        binding.tvKcalValueMeal.text = attributes.getInteger(R.styleable.MealView_kcalValue, 0).toString()
        binding.tvCarbsValueMeal.text = attributes.getInteger(R.styleable.MealView_carbValue, 0).toString()
        binding.tvProteinValueMeal.text = attributes.getInteger(R.styleable.MealView_protValue, 0).toString()
        binding.tvFatValueMeal.text = attributes.getInteger(R.styleable.MealView_fatValue, 0).toString()

        attributes.recycle()



        binding.fabMeal.setOnClickListener {
            val intent = Intent(context, AddActivity::class.java).putExtra("mealName",binding.tvNameMealView.text.toString())
            context.startActivity(intent)
        }

    }

    fun addProducts(list:MutableList<JournalEntry>){
        entriesList.clear()
        entriesList.addAll(list)
        binding.rvMeal.adapter?.notifyDataSetChanged()
        calculateValues()
    }

    private fun calculateValues(){
        var calories = 0
        var carbs = 0.0
        var protein = 0.0
        var fat = 0.0
        for(meal in entriesList){
            calories += meal.calories.toInt()
            carbs += meal.carbs.toDouble()
            protein += meal.protein.toDouble()
            fat += meal.fat.toDouble()
        }
        updateValues(calories,carbs ,protein,fat)
    }

    private fun updateValues(calories:Int,carbs:Double,protein:Double,fat:Double){
        binding.tvKcalValueMeal.text = calories.toString()
        binding.tvCarbsValueMeal.text = carbs.toInt().toString()
        binding.tvProteinValueMeal.text = protein.toInt().toString()
        binding.tvFatValueMeal.text = fat.toInt().toString()

        data.value = arrayListOf(calories,carbs.toInt(),protein.toInt(),fat.toInt())
    }

    fun getValues():MutableLiveData<ArrayList<Int>> = data

    override fun onAdapterItemClickListener(position: Int) {
        entriesList.removeAt(position)
        binding.rvMeal.adapter?.notifyDataSetChanged()
        calculateValues()
    }
}