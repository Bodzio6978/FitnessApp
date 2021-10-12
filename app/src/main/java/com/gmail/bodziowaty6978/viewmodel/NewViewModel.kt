package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NewViewModel:ViewModel() {

    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")
    private val instance = FirebaseAuth.getInstance()

    private val notification = MutableLiveData<String>()
    private val mAction = MutableLiveData<Action>()

    fun addNewProduct(name: String, brand: String, weight: String, position: Int, calories: String, carbs: String, protein: String, fat: String, barCode: String){
        val mealRef = database.reference.child("meals")
        val key = mealRef.push().key

        var caloriesValue = calories
        var carbsValue = carbs
        var proteinValue = protein
        var fatValue = fat

        var isPortion = false

        when(position){
            0 -> {
            }
            else -> {
                caloriesValue = (calories.toInt()/weight.toInt()*100).toString()
                carbsValue = (carbs.toDouble()/weight.toDouble()*100).toString()
                proteinValue = (protein.toDouble()/weight.toDouble()*100).toString()
                fatValue = (fat.toDouble()/weight.toDouble()*100).toString()
                if(position==2) isPortion=true
            }
        }

        mealRef.child(key.toString()).setValue(Meal(getUserId(), name, brand, weight, isPortion, caloriesValue, carbsValue, proteinValue, fatValue, barCode))

    }

    private fun getUserId():String = instance.uid.toString()

    fun getAction():MutableLiveData<Action> = mAction

    class Action(val value: Int) {

        companion object {
            const val SHOW_ADDING = 0
            const val SHOW_INVALID_PASSWARD_OR_LOGIN = 1
        }
    }

}