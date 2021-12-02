package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().uid.toString()

    private val mAction = MutableLiveData<NewProductState>()
    private val currentKey = MutableLiveData<String>()

    fun addNewProduct(name: String, brand: String, weight: String, position: Int, unit: String, calories: String, carbs: String, protein: String, fat: String, barCode: String) {

        if ((name.isEmpty() || calories.isEmpty() || carbs.isEmpty() || protein.isEmpty() || fat.isEmpty())) {
            NotificationText.setText("fields")
            NotificationText.startAnimation()
        } else if (position != 0 && weight.isEmpty()) {
            NotificationText.setText("fields")
            NotificationText.startAnimation()
        } else {
            val caloriesValue = calories.toDouble()
            val carbohydratesValue = carbs.replace(",", ".").toDouble()
            val proteinValue = protein.replace(",", ".").toDouble()
            val fatValue = fat.replace(",", ".").toDouble()
            val weightValue = weight.toDouble()

            mAction.value = NewProductState(NewProductState.ADDING_MEAL)
            val meal = when (position) {
                0 -> {
                    Product(userId, name, brand, weightValue, position, unit, caloriesValue.toInt(), carbohydratesValue, proteinValue, fatValue, barCode)
                }
                else -> {
                    Product(userId, name, brand, weightValue, position, unit,
                            (caloriesValue / weightValue * 100.0).toInt(),
                            (carbohydratesValue / weightValue * 100.0).round(2),
                            (proteinValue / weightValue * 100.0).round(2),
                            (fatValue / weightValue * 100.0).round(2),
                            barCode)
                }

            }

            val searchKeywords = meal.name?.let { generateKeyWords(it) }

            val batch = db.batch()
            val productsRef = db.collection("products").document()

            batch.set(productsRef, meal, SetOptions.merge())

            batch.set(productsRef, mapOf("searchKeywords" to searchKeywords), SetOptions.merge())

            batch.commit().addOnSuccessListener {
                currentKey.value = productsRef.id
                mAction.value = NewProductState(NewProductState.MEAL_ADDED)
            }
        }
    }

    private fun generateKeyWords(text: String): MutableList<String> {
        var mealName = text.toLowerCase()
        val keywords = mutableListOf<String>()

        val words = mealName.split(" ")

        for (word in words) {
            var appendString = ""

            for (charPosition in mealName.indices) {
                appendString += mealName[charPosition].toString()
                keywords.add(appendString)
            }

            mealName = mealName.replace("$word ", "")

        }

        return keywords
    }

    fun getAction(): MutableLiveData<NewProductState> = mAction
    fun getKey(): String = currentKey.value.toString()
}

class NewProductState(val value: Int) {

    companion object {
        const val ADDING_MEAL = 0
        const val MEAL_ADDED = 1
        const val ERROR_ADDING_MEAL = 2
    }
}