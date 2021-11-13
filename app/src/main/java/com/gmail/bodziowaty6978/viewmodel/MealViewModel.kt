package com.gmail.bodziowaty6978.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.Meal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MealViewModel : ViewModel() {

    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")

    private val currentMeal = MutableLiveData<Meal>()

    fun getMeal(id: String) {
        val productRef = database.reference.child("meals").child(id)

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = snapshot.value as HashMap<*, *>
                currentMeal.value = Meal(data["author"] as String,
                        data["name"] as String,
                        data["brand"] as String,
                        data["weight"] as String,
                        (data["position"] as Long).toInt(),
                        data["calories"] as String,
                        data["carbs"] as String,
                        data["protein"] as String,
                        data["fat"] as String
                )

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MealViewModel", error.message)
            }
        })

    }

    fun getCurrentMeal():MutableLiveData<Meal> = currentMeal

}