package com.gmail.bodziowaty6978.customviews

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class MealView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs){

//    lateinit var binding: MealViewBinding
//
//    init {
//        val binding = MealViewBinding.inflate(LayoutInflater.from(context))
//        addView(binding.root)
//
//        binding.mealViewRecycler.layoutManager = LinearLayoutManager(context)
//        binding.mealViewRecycler.adapter = CaloriesRecyclerAdapter(meals)
//
//        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MealView)
//        mealName.text = attributes.getString(R.styleable.MealView_mealName)
//        kcalValue.text = attributes.getInteger(R.styleable.MealView_kcalValue, 0).toString()
//        carbValue.text = attributes.getInteger(R.styleable.MealView_carbValue, 0).toString()
//        protValue.text = attributes.getInteger(R.styleable.MealView_protValue, 0).toString()
//        fatValue.text = attributes.getInteger(R.styleable.MealView_fatValue, 0).toString()
//
//        attributes.recycle()
//
//
//
//        addButton.setOnClickListener {
//            val intent = Intent(context, AddActivity::class.java).putExtra("name",mealName.text.toString())
//            context.startActivity(intent)
//
//        }
//    }
//
//    fun getMeals():List<Meal>{
//        return meals
//
//    }
//
//    fun addMeal(meal: Meal) {
//        meals.add(meal)
//        kcalValue.text = (Integer.valueOf(kcalValue.text.toString())+meal.calories).toString()
//        carbValue.text = (Integer.valueOf(carbValue.text.toString())+meal.carbs).toString()
//        protValue.text = (Integer.valueOf(protValue.text.toString())+meal.protein).toString()
//        fatValue.text = (Integer.valueOf(fatValue.text.toString())+meal.fats).toString()
//        recycler.adapter?.notifyDataSetChanged()
//    }



}