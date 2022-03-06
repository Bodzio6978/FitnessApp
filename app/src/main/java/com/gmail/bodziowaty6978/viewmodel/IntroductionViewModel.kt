package com.gmail.bodziowaty6978.viewmodel

import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.functions.toShortString
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.model.WeightEntity
import com.gmail.bodziowaty6978.other.DataStoreManager
import com.gmail.bodziowaty6978.room.AppDatabase
import com.gmail.bodziowaty6978.view.introduction.FirstIntroductionFragment
import com.gmail.bodziowaty6978.view.introduction.SecondIntroductionFragment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val dataStoreManager: DataStoreManager,
    private val roomDatabase: AppDatabase
) : ViewModel() {

    private val fragment1 = FirstIntroductionFragment()
    private val fragment2 = SecondIntroductionFragment()

    private val userInformationMap = ArrayMap<String, String>()

    val addingInformation = MutableLiveData<InformationState>()

    fun getFragments(): ArrayList<Fragment> {
        val list = ArrayList<Fragment>()
        list.add(fragment1)
        list.add(fragment2)
        return list
    }

    fun clearData() {
        userInformationMap.clear()
    }

    fun addInformation(data: Map<String, String>, isFinished: Boolean) {
        userInformationMap.putAll(data)

        if (isFinished && userInformationMap.size == 8) {

            viewModelScope.launch(dispatchers.default) {
                addingInformation.postValue(InformationState(InformationState.ADDING_INFORMATION))

                val gender = userInformationMap["gender"]!!
                val age = userInformationMap["age"]?.toInt()!!
                val currentWeight =
                    userInformationMap["current"]!!.replace(",", ".").toDouble().round()
                val wantedWeight =
                    userInformationMap["desired"]!!.replace(",", ".").toDouble().round()

                val workType = userInformationMap["type"]!!
                val workoutInWeek = userInformationMap["workouts"]!!
                val activityInDay = userInformationMap["activity"]!!

                val height = userInformationMap["height"]!!.replace(",", ".").toDouble().round()

                val userInformation = mapOf<String, String>(
                    "gender" to gender,
                    "currentWeight" to currentWeight.toString(),
                    "wantedWeight" to wantedWeight.toString(),
                    "workType" to workType,
                    "workoutInWeek" to workoutInWeek,
                    "activityInDay" to activityInDay,
                    "height" to height.toString(),
                )

                val nutritionValues = calculateNutritionNeeds(
                    gender,
                    age,
                    currentWeight,
                    wantedWeight,
                    workType,
                    activityInDay,
                    workoutInWeek,
                    height
                )

                dataStoreManager.saveNutritionValues(nutritionValues)
                dataStoreManager.saveUserInformation(userInformation)

                addInitialWeightDialog(currentWeight)

                addingInformation.postValue(InformationState(InformationState.INFORMATION_ADDED))
            }
        }
    }

    private suspend fun addInitialWeightDialog(weight:Double){
        val calendar = Calendar.getInstance()
        val weightEntry = WeightEntity(0,calendar.timeInMillis,weight,calendar.toShortString())
        roomDatabase.weightDao().addWeightEntry(weightEntry)
    }

    private fun calculateNutritionNeeds(
        gender: String,
        age: Int,
        currentWeight: Double,
        wantedWeight: Double,
        workType: String,
        activityInDay: String,
        workoutsInWeek: String,
        height: Double
    ): Map<String, Double> {

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

        return mapOf<String, Double>(
            "wantedCalories" to wantedCalories.round(),
            "wantedCarbohydrates" to wantedCarbs.round(),
            "wantedProtein" to wantedProtein.round(),
            "wantedFat" to wantedFat.round()
        )
    }
}

class InformationState(val value: Int) {

    companion object {
        const val ADDING_INFORMATION = 0
        const val INFORMATION_ADDED = 1
    }
}