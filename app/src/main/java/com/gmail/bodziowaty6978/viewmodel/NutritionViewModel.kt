package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.other.DataStoreManager
import com.gmail.bodziowaty6978.state.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
):ViewModel() {

    val userInformation = dataStoreManager.getUserInformation.asLiveData()
    val settingsState = MutableLiveData<DataState>()

    fun saveSettings(calories:String,percentages:Map<String,Int>){
        viewModelScope.launch {
            settingsState.postValue(DataState.Loading)
            if (calories.isBlank()){
                settingsState.postValue(DataState.Error("Please enter your desired calories"))
            }else if (percentages.values.sum()!=100){
                settingsState.postValue(DataState.Error("Please make sure your total is at 100%"))
            }else{
                val caloriesValue = calories.toInt().toDouble()
                val carbs = caloriesValue*percentages["carbs"]!!/100.0/4.0
                val protein = caloriesValue*percentages["protein"]!!/100.0/4.0
                val fat = caloriesValue*percentages["fat"]!!/100.0/9.0

                dataStoreManager.saveNutritionValues(mapOf("wantedCalories" to caloriesValue,
                    "wantedCarbohydrates" to carbs,
                    "wantedProtein" to protein,
                    "wantedFat" to fat)
                )
                settingsState.postValue(DataState.Success)
            }
        }
    }

}