package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CaloriesViewModel: ViewModel() {
    private val userId = FirebaseAuth.getInstance().uid.toString()
    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")

    private val mValues = MutableLiveData<HashMap<*,*>>()

    fun setUpValues(){
        val valuesRef = database.reference.child("users").child(userId).child("nutritionValues")
        valuesRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val values = snapshot.value as HashMap<*,*>
                mValues.value = values
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun getValues():MutableLiveData<HashMap<*,*>> = mValues

}
