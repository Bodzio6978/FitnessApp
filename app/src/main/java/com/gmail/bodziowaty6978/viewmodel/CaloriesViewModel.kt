package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.Meal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate

class CaloriesViewModel: ViewModel() {
    private val breakfast = ArrayList<Meal>()
    private val lunch = ArrayList<Meal>()
    private val dinner = ArrayList<Meal>()
    private val supper = ArrayList<Meal>()

    private val liveBreakfast = MutableLiveData<ArrayList<Meal>>(breakfast)
    private val liveLunch = MutableLiveData<ArrayList<Meal>>(lunch)
    private val liveDinner = MutableLiveData<ArrayList<Meal>>(dinner)
    private val liveSupper = MutableLiveData<ArrayList<Meal>>(supper)

    private val userId = FirebaseAuth.getInstance().uid.toString()
    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")

    private val currentDate = LocalDate.now()

//    fun getData(){
//        val ref = database.reference.child("users").child(currentDate.toString())
//        for (i in 1..4){
//            var mealRef = ref
//            when(i){
//                1 -> mealRef = ref.child("breakfast")
//                2 -> mealRef = ref.child("lunch")
//                3-> mealRef = ref.child("dinner")
//                4 -> mealRef = ref.child("supper")
//            }
//            mealRef.
//        }
//    }




}
