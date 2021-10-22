package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.Meal
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class NewViewModel : ViewModel() {

    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")
    private val instance = FirebaseAuth.getInstance()

    private val mAction = MutableLiveData<Action>()
    private val currentKey = MutableLiveData<String>()

    fun addNewProduct(name: String, brand: String, weight: String, position: Int, calories: String, carbs: String, protein: String, fat: String, barCode: String) {

        if ((name.isEmpty() || calories.isEmpty() || carbs.isEmpty() || protein.isEmpty() || fat.isEmpty())) {
            NotificationText.setText("fields")
            NotificationText.startAnimation()
        } else if (position != 0 && weight.isEmpty()) {
            NotificationText.setText("fields")
            NotificationText.startAnimation()
        } else {
            mAction.value = Action(Action.ADDING_MEAL)
            val mealRef = database.reference.child("meals")
            val key = mealRef.push().key
            val meal = when (position) {
                0 -> {
                    Meal(getUserId(), name, brand, weight, position, calories, carbs, protein, fat)
                }
                else -> {
                    Meal(getUserId(), name, brand, weight, position,
                            (calories.toInt() / weight.toInt() * 100).toString(),
                            (carbs.toDouble() / weight.toDouble() * 100).toString(),
                            (protein.toDouble() / weight.toDouble() * 100).toString(),
                            (fat.toDouble() / weight.toDouble() * 100).toString())
                }

            }

            if (barCode.isNotEmpty()) {
                val barcodeRef = database.reference.child("barCodes")
                barcodeRef.child(barCode).setValue(key.toString())
            }

            mealRef.child(key.toString()).setValue(meal).addOnCompleteListener {
                if (it.isSuccessful) {
                    currentKey.value = key.toString()
                    mAction.value = Action(Action.MEAL_ADDED)
                } else {
                    mAction.value = Action(Action.ERROR_ADDING_MEAL)

                }
            }
        }
    }

    private fun getUserId(): String = instance.uid.toString()

    fun getAction(): MutableLiveData<Action> = mAction
    fun getKey(): String = currentKey.value.toString()
}

class Action(val value: Int) {

    companion object {
        const val ADDING_MEAL = 0
        const val MEAL_ADDED = 1
        const val ERROR_ADDING_MEAL = 2
    }
}