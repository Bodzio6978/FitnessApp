package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.Meal

class CaloriesViewModel: ViewModel() {
    private val mealList = ArrayList<Meal>()
    private val meals = MutableLiveData<ArrayList<Meal>>(mealList)

}