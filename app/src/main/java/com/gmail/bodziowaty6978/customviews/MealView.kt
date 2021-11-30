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
import com.gmail.bodziowaty6978.singleton.Strings
import com.gmail.bodziowaty6978.view.AddActivity
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
class MealView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs),OnAdapterItemClickListener{

    var binding: MealViewBinding = MealViewBinding.inflate(LayoutInflater.from(context))

    private var entriesList:MutableList<JournalEntry> = mutableListOf()

    private val data = MutableLiveData<ArrayList<Int>>()

    private var deletedProduct = MutableLiveData<Pair<Int,String>>()

    init {
        addView(binding.root)

        binding.rvMeal.layoutManager = LinearLayoutManager(context)
        binding.rvMeal.adapter = CaloriesRecyclerAdapter(entriesList,this)


        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MealView)
        binding.tvNameMealView.text = attributes.getString(R.styleable.MealView_mealName)
        (attributes.getInteger(R.styleable.MealView_kcalValue, 0).toString()+Strings.get(R.string.kcal)).also { binding.tvKcalValueMeal.text = it }
        (attributes.getInteger(R.styleable.MealView_carbValue, 0).toString()+Strings.get(R.string.g)).also { binding.tvCarbsValueMeal.text = it }
        (attributes.getInteger(R.styleable.MealView_protValue, 0).toString()+Strings.get(R.string.g)).also { binding.tvProteinValueMeal.text = it }
        (attributes.getInteger(R.styleable.MealView_fatValue, 0).toString()+Strings.get(R.string.g)).also { binding.tvFatValueMeal.text = it }

        attributes.recycle()

        binding.fabMeal.setOnClickListener {
            val intent = Intent(context, AddActivity::class.java).putExtra("mealName",binding.tvNameMealView.text.toString())
            context.startActivity(intent)
        }

    }

    fun addProducts(list:MutableMap<String,JournalEntry>){
        entriesList.clear()
        entriesList.addAll(list.values)
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
        (calories.toString()+Strings.get(R.string.kcal)).also { binding.tvKcalValueMeal.text = it }
        (carbs.toInt().toString()+Strings.get(R.string.g)).also { binding.tvCarbsValueMeal.text = it }
        (protein.toInt().toString()+Strings.get(R.string.g)).also { binding.tvProteinValueMeal.text = it }
        (fat.toInt().toString()+Strings.get(R.string.g)).also { binding.tvFatValueMeal.text = it }

        data.value = arrayListOf(calories,carbs.toInt(),protein.toInt(),fat.toInt())
    }

    fun removeProduct(position: Int){
        entriesList.removeAt(position)
        binding.rvMeal.adapter?.notifyDataSetChanged()
        calculateValues()
    }

    fun getValues():MutableLiveData<ArrayList<Int>> = data

    fun getDeletedProduct():MutableLiveData<Pair<Int,String>> = deletedProduct

    override fun onAdapterItemClickListener(position: Int) {
        deletedProduct.value = Pair(position,binding.tvNameMealView.text.toString())
    }
}