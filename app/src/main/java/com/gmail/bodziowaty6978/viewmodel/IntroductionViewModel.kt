package com.gmail.bodziowaty6978.viewmodel

import android.util.Log
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

    private val userInformation = ArrayMap<String, String>()

    private val addingInformation = MutableLiveData<Boolean>(false)

    private var isEverythingReady = false

    fun getFragments(): ArrayList<Fragment> {
        val list = ArrayList<Fragment>()
        list.add(fragment1)
        list.add(fragment2)
        return list
    }

    fun clearData() {
        userInformation.clear()
    }

    fun addInformation(data: SimpleArrayMap<String, String>, isFinished: Boolean) {
        userInformation.putAll(data)

        if (isFinished) {

            calculateNutritionNeeds()

            db.collection("users").document(userId)
                    .set(mapOf("userInformation" to userInformation as Map<String, Any>), SetOptions.merge())
                    .addOnSuccessListener {
                        if (isEverythingReady) {
                            addingInformation.value = true
                        } else {
                            isEverythingReady = true
                        }
                    }.addOnFailureListener {
                        Log.d(TAG,it.message.toString())
                    }

        }
    }

    private fun calculateNutritionNeeds() {
        val height = userInformation["height"]?.toDouble()
        val current = userInformation["current"]?.toDouble()
        val wanted = userInformation["desired"]?.toDouble()
        val age = userInformation["age"]?.toDouble()

        if (current != null || height != null || wanted != null || age != null) {
            val ppm = when (userInformation["gender"]) {
                "male" -> {
                    (10.0 * current!!) + (6.25 * height!!) - (5.0 * age!!) + 5.0
                }
                else -> {
                    (10.0 * current!!) + (6.25 * height!!) - (5.0 * age!!) - 161.0
                }
            }
            val workType = when (userInformation["type"]) {
                "Sedentary" -> 1.0 / 7.0
                "Mixed" -> 2.0 / 7.0
                else -> 3.0 / 7.0
            }
            val workouts = when (userInformation["workouts"]) {
                "none" -> 0.0
                "1–2" -> 1.0 / 14.0
                "3–4" -> 2.0 / 14.0
                "5-6" -> 3.0 / 14.0
                else -> 4.0 / 14.0
            }
            val activity = when (userInformation["activity"]) {
                "Low" -> 1.0 / 14.0
                "Moderate" -> 2.0 / 14.0
                "High" -> 3.0 / 14.0
                else -> 4.0 / 14.0
            }

            val pal = 1.1 + activity + workType + workouts

            val cpm = ppm * pal

            val wantedCalories = when {
                wanted!! > current -> {
                    cpm + 500.0
                }
                wanted < current -> {
                    cpm - 500.0
                }
                else -> {
                    cpm + 0.0
                }
            }

            val wantedProtein = current*2.0*0.87
            val wantedFat = current*0.93
            val wantedCarbs = (wantedCalories - ((9.0 * wantedFat) + (4.0 * wantedProtein))) / 4.0

            sendNutritionValues(wantedCalories, wantedCarbs.round(2), wantedProtein.round(2), wantedFat.round(2))

        }
    }

    private fun sendNutritionValues(calories: Double, carbohydrates: Double, protein: Double, fat: Double) {
        val map:Map<String,String> = mapOf(
                "calories" to calories.toInt().toString(),
                "carbohydrates" to carbohydrates.toString(),
                "protein" to protein.toString(),
                "fat" to fat.toString()
        )

        db.collection("users").document(userId)
                .set(mapOf("nutritionValues" to map), SetOptions.merge()).addOnSuccessListener {
                    if (isEverythingReady) {
                        addingInformation.value = true
                    } else {
                        isEverythingReady = true
                    }
                }
                .addOnFailureListener {
                    Log.w(TAG,it.message.toString())
                }
    }

    fun getInformationStatus(): MutableLiveData<Boolean> = addingInformation
}