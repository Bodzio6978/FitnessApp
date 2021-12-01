package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MealViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val currentMeal = MutableLiveData<Product>()

    private val addingState = MutableLiveData<Boolean>(false)

    fun getMeal(id: String) {

        db.collection("products").document(id).get().addOnSuccessListener {
            if (it.exists()){
                currentMeal.value = it.toObject(Product::class.java)
            }
        }

    }

    fun addMeal(id: String, weight: String, mealName: String) {

        if(currentMeal.value!=null){

            val currentMeal = currentMeal.value!!

            val weightValue = weight.replace(",",".").toDouble()

            val calories = (currentMeal.calories.toDouble()*weightValue/100.0).toInt()
            val carbohydrates = (currentMeal.carbs*weightValue/100.0).round(2)
            val protein = (currentMeal.protein*weightValue/100.0).round(2)
            val fat = (currentMeal.fat*weightValue/100.0).round(2)

            val journalEntry = JournalEntry(currentMeal.name,
                    id,
                    mealName,
                    CurrentDate.getDate()!!.time.toString("EEEE, dd-MM-yyyy"),
                    currentMeal.brand,
                    weightValue,
                    currentMeal.unit,
                    calories,
                    carbohydrates,
                    protein,
                    fat
            )

            db.collection("users").document(userId).collection("journal")
                    .add(journalEntry)
                    .addOnSuccessListener {

                        addingState.value = true

                    }
        }





    }

    fun getCurrentMeal(): MutableLiveData<Product> = currentMeal
    fun getAddingState(): MutableLiveData<Boolean> = addingState

}