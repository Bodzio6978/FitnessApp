package com.gmail.bodziowaty6978.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.view.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class MainViewModel: ViewModel() {
    private val calories = CaloriesFragment()
    private val training = TrainingFragment()
    private val recipes = RecipesFragment()
    private val shopping = ShoppingFragment()
    private val settings = SettingFragment()

    fun getCalories():Fragment = calories
    fun getTraining():Fragment = training
    fun getRecipes():Fragment = recipes
    fun getShopping():Fragment = shopping
    fun getSettings():Fragment = settings
}