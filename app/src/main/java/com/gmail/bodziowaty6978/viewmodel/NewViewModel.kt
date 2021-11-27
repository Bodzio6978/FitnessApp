package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NewViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().uid.toString()

    private val mAction = MutableLiveData<Action>()
    private val currentKey = MutableLiveData<String>()

    fun addNewProduct(name: String, brand: String, weight: String, position: Int, unit: String, calories: String, carbs: String, protein: String, fat: String, barCode: String) {

        if ((name.isEmpty() || calories.isEmpty() || carbs.isEmpty() || protein.isEmpty() || fat.isEmpty())) {
            NotificationText.setText("fields")
            NotificationText.startAnimation()
        } else if (position != 0 && weight.isEmpty()) {
            NotificationText.setText("fields")
            NotificationText.startAnimation()
        } else {
            mAction.value = Action(Action.ADDING_MEAL)
            val meal = when (position) {
                0 -> {
                    Product(userId, name, brand, weight, position, unit, calories, carbs, protein, fat)
                }
                else -> {
                    Product(userId, name, brand, weight, position, unit,
                            (calories.toDouble() / weight.toDouble() * 100).toInt().toString(),
                            (carbs.toDouble() / weight.toDouble() * 100).round(2).toString(),
                            (protein.toDouble() / weight.toDouble() * 100).round(2).toString(),
                            (fat.toDouble() / weight.toDouble() * 100).round(2).toString())
                }

            }

            db.collection("products")
                    .add(meal).addOnSuccessListener {
                        currentKey.value = it.id
                        if (barCode.isNotEmpty()) {
                            db.collection("barcodes").add(
                                    mapOf(barCode to it.id)
                            )
                        }
                        mAction.value = Action(Action.MEAL_ADDED)

                    }
        }
    }

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