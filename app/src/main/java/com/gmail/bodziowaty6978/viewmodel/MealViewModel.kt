package com.gmail.bodziowaty6978.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.model.Meal
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MealViewModel : ViewModel() {

    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val currentMeal = MutableLiveData<Meal>()

    private val addingState = MutableLiveData<Boolean>(false)

    fun getMeal(id: String) {
        val productRef = database.reference.child("meals").child(id)

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!=null){
                    val data = snapshot.value as HashMap<*, *>
                    currentMeal.value = Meal(data["author"] as String,
                            data["name"] as String,
                            data["brand"] as String,
                            data["weight"] as String,
                            (data["position"] as Long).toInt(),
                            data["unit"] as String,
                            data["calories"] as String,
                            data["carbs"] as String,
                            data["protein"] as String,
                            data["fat"] as String
                    )
                }


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MealViewModel", error.message)
            }
        })

    }

    fun addMeal(id:String,weight:String,mealName:String){
        val journalRef = database.reference.child("users").child(userId).child("journal").child(CurrentDate.getDate()!!.time.toString("EEEE, dd-MM-yyyy")).child(mealName)
        val pushKey = journalRef.push().key
        journalRef.child(pushKey.toString()).child(id).setValue(weight).addOnCompleteListener {
            if(it.isSuccessful){
                addingState.value = true
            }
        }


    }

    fun getCurrentMeal():MutableLiveData<Meal> = currentMeal
    fun getAddingState():MutableLiveData<Boolean> = addingState

}