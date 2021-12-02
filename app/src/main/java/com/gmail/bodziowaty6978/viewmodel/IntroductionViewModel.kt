package com.gmail.bodziowaty6978.viewmodel

import androidx.collection.ArrayMap
import androidx.collection.SimpleArrayMap
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.view.introduction.FirstFragment
import com.gmail.bodziowaty6978.view.introduction.SecondFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

@DelicateCoroutinesApi
class IntroductionViewModel : ViewModel() {

    private val fragment1 = FirstFragment()
    private val fragment2 = SecondFragment()

    private val db = Firebase.firestore

    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val userInformationMap = ArrayMap<String, String>()

    private val addingInformation = MutableLiveData<InformationState>()

    fun getFragments(): ArrayList<Fragment> {
        val list = ArrayList<Fragment>()
        list.add(fragment1)
        list.add(fragment2)
        return list
    }

    fun clearData() {
        userInformationMap.clear()
    }

    fun addInformation(data: SimpleArrayMap<String, String>, isFinished: Boolean) {
        userInformationMap.putAll(data)

        if (isFinished && userInformationMap.size == 8) {

            addingInformation.value = InformationState(InformationState.ADDING_INFORMATION)

            val gender = userInformationMap["gender"]!!
            val age = userInformationMap["age"]?.toInt()!!
            val currentWeight = userInformationMap["current"]!!.replace(",",".").toDouble().round()
            val wantedWeight = userInformationMap["desired"]!!.replace(",",".").toDouble().round()

            val workType = userInformationMap["type"]!!
            val workoutInWeek = userInformationMap["workouts"]!!
            val activityInDay = userInformationMap["activity"]!!

            val height = userInformationMap["height"]!!.replace(",",".").toDouble().round()

            val userInformation = mapOf<String,String>("gender" to gender,
                    "currentWeight" to currentWeight.toString(),
                    "wantedWeight" to wantedWeight.toString(),
                    "workType" to workType,
                    "workoutInWeek" to workoutInWeek,
                    "activityInDay" to activityInDay,
                    "height" to height.toString(),)

            val nutritionValues = calculateNutritionNeeds(gender,age,currentWeight,wantedWeight,workType,activityInDay,workoutInWeek,height)

            val batch = db.batch()

            val userRef = db.collection("users").document(userId)

            batch.set(userRef, mapOf("userInformation" to userInformation),SetOptions.merge())
            batch.set(userRef,mapOf("nutritionValues" to nutritionValues),SetOptions.merge())

            batch.commit().addOnSuccessListener {
                addingInformation.value = InformationState(InformationState.INFORMATION_ADDED)
            }
        }
    }

    private fun calculateNutritionNeeds(gender: String,age:Int, currentWeight: Double, wantedWeight: Double,workType:String,activityInDay:String,workoutsInWeek:String, height: Double): Map<String,Double> {

        val ppm = when (gender) {
            "Male" -> {
                (10.0 * currentWeight) + (6.25 * height) - (5.0 * age) + 5.0
            }
            else -> {
                (10.0 * currentWeight) + (6.25 * height) - (5.0 * age) - 161.0
            }
        }

        val firstFactor = when (workType) {
            "Sedentary" -> 1.0 / 7.0
            "Mixed" -> 2.0 / 7.0
            else -> 3.0 / 7.0
        }

        val secondFactor = when (workoutsInWeek) {
            "none" -> 0.0
            "1–2" -> 1.0 / 14.0
            "3–4" -> 2.0 / 14.0
            "5-6" -> 3.0 / 14.0
            else -> 4.0 / 14.0
        }
        val thirdFactor = when (activityInDay) {
            "Low" -> 1.0 / 14.0
            "Moderate" -> 2.0 / 14.0
            "High" -> 3.0 / 14.0
            else -> 4.0 / 14.0
        }

        val pal = 1.1 + firstFactor + secondFactor + thirdFactor

        val cpm = ppm * pal

        val wantedCalories = when {
            wantedWeight > currentWeight -> {
                cpm + 500.0
            }
            wantedWeight < currentWeight -> {
                cpm - 500.0
            }
            else -> {
                cpm + 0.0
            }
        }

        val wantedProtein = currentWeight * 2.0 * 0.87
        val wantedFat = currentWeight * 0.93
        val wantedCarbs = ((wantedCalories - ((9.0 * wantedFat) + (4.0 * wantedProtein))) / 4.0)

        return mapOf<String,Double>("wantedCalories" to wantedCalories.round(),
                "wantedCarbohydrates" to wantedCarbs.round(),
                "wantedProtein" to wantedProtein.round(),
                "wantedFat" to wantedFat.round())
    }

    fun getInformationStatus(): MutableLiveData<InformationState> = addingInformation
}

class InformationState(val value: Int) {

    companion object {
        const val ADDING_INFORMATION = 0
        const val INFORMATION_ADDED = 1
    }
}