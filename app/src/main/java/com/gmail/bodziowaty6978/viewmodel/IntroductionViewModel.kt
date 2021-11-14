package com.gmail.bodziowaty6978.viewmodel

import androidx.collection.ArrayMap
import androidx.collection.SimpleArrayMap
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.view.introduction.FirstFragment
import com.gmail.bodziowaty6978.view.introduction.SecondFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class IntroductionViewModel:ViewModel() {

    private val fragment1 = FirstFragment()
    private val fragment2 = SecondFragment()

    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val userInformation = ArrayMap<String,String>()

    private val addingInformation = MutableLiveData<Boolean>(false)

    private var isEverythingReady = false

    fun getFragments():ArrayList<Fragment>{
        val list = ArrayList<Fragment>()
        list.add(fragment1)
        list.add(fragment2)
        return list
    }

    fun clearData(){
        userInformation.clear()
    }

    fun addInformation(data: SimpleArrayMap<String, String>,isFinished:Boolean){
        userInformation.putAll(data)

        if(isFinished){

            calculateNutritionNeeds()

            val userRef = database.reference.child("users").child(userId).child("information")
            userRef.setValue(userInformation).addOnCompleteListener {
                if(it.isSuccessful){
                    if(isEverythingReady){
                        addingInformation.value = true
                    }else{
                        isEverythingReady = true
                    }
                }
            }
        }
    }

    private fun calculateNutritionNeeds(){
        val height = userInformation["height"]?.toDouble()
        val current = userInformation["current"]?.toDouble()
        val wanted = userInformation["desired"]?.toDouble()
        val age = userInformation["age"]?.toDouble()

        if(current!=null||height!=null||wanted!=null||age!=null){
            val ppm = when(userInformation["gender"]){
                "male" ->{
                    (10.0* current!!)+(6.25*height!!)-(5.0*age!!)+5.0
                }
                else -> {
                    (10.0* current!!)+(6.25*height!!)-(5.0*age!!)-161.0
                }
            }
            val workType = when(userInformation["type"]){
                "Sedentary" -> 1.0/7.0
                "Mixed" -> 2.0/7.0
                else -> 3.0/7.0
            }
            val workouts = when(userInformation["workouts"]){
                "none" -> 0.0
                "1–2" -> 1.0/14.0
                "3–4" -> 2.0/14.0
                "5-6" -> 3.0/14.0
                else -> 4.0/14.0
            }
            val activity = when(userInformation["activity"]){
                "Low" -> 1.0/14.0
                "Moderate" -> 2.0/14.0
                "High" -> 3.0/14.0
                else -> 4.0/14.0
            }

            val pal = 1.1+activity+workType+workouts

            val cpm = ppm*pal

            val wantedCalories = when {
                wanted!! >current -> {
                    cpm+500.0
                }
                wanted <current -> {
                    cpm-500.0
                }
                else -> {
                    cpm+0.0
                }
            }

            val wantedCarbs = (wantedCalories-((9.0*current)+(4.0*current*2.0)))/4.0

            sendNutritionValues(wantedCalories,wantedCarbs,current*2.0,current)

        }
    }

    private fun sendNutritionValues(calories:Double,carbohydrates:Double,protein:Double,fat:Double){
        val userRef = database.reference.child("users").child(userId).child("nutritionValues")
        val map = ArrayMap<String,String>()
        map["calories"] = calories.toInt().toString()
        map["carbohydrates"] = carbohydrates.toInt().toString()
        map["protein"] = protein.toInt().toString()
        map["fat"] = fat.toInt().toString()
        userRef.setValue(map).addOnCompleteListener {
            if(isEverythingReady){
                addingInformation.value = true
            }else{
                isEverythingReady = true
            }
        }
    }

    fun getInformationStatus():MutableLiveData<Boolean> = addingInformation
}