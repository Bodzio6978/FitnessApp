package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.Meal

class MealViewModel: ViewModel() {

    val meals = MutableLiveData<List<Meal>>()
    val mealLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

}