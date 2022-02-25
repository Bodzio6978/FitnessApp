package com.gmail.bodziowaty6978.other

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore("settings")

@Singleton //You can ignore this annotation as return `datastore` from `preferencesDataStore` is singletone
class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

    private val settingsDataStore = appContext.dataStore

    private object Keys{
        val areWeightDialogsEnabled = booleanPreferencesKey("areWeightDialogsEnabled")
        val username = stringPreferencesKey("username")
        val calories = doublePreferencesKey("wantedCalories")
        val carbs = doublePreferencesKey("wantedCarbohydrates")
        val protein = doublePreferencesKey("wantedProtein")
        val fat = doublePreferencesKey("wantedFat")
        val activity = stringPreferencesKey("activityInDay")
        val currentWeight = stringPreferencesKey("currentWeight")
        val gender = stringPreferencesKey("gender")
        val height = stringPreferencesKey("height")
        val wantedWeight = stringPreferencesKey("wantedWeight")
        val workType = stringPreferencesKey("workType")
        val workoutInWeek = stringPreferencesKey("workoutInWeek")
    }

    suspend fun saveUsername(username:String){
        val key = stringPreferencesKey("username")
        settingsDataStore.edit {
            it[key] = username
        }
    }

    val getData:Flow<User> = settingsDataStore.data.catch { exception ->
        if (exception is IOException){
            Log.e(TAG,exception.message.toString())
            emit(emptyPreferences())
        }else{
            throw exception
        }
    }.map {
        val areWeightDialogsEnabled = it[Keys.areWeightDialogsEnabled]
        val calories = it[Keys.calories]
        val carbs = it[Keys.carbs]
        val protein = it[Keys.protein]
        val fat = it[Keys.fat]
        val activity = it[Keys.activity]
        val currentWeight = it[Keys.currentWeight]
        val gender = it[Keys.gender]
        val height = it[Keys.height]
        val wantedWeight = it[Keys.wantedWeight]
        val workType = it[Keys.workType]
        val workoutInWeek = it[Keys.workoutInWeek]
        val username = it[Keys.username]

        val nutritionValues = if (calories!=null||protein!=null||fat!=null||carbs!=null){
            mapOf<String,Double>("wantedCalories" to calories!!,
                "wantedCarbohydrates" to carbs!!,
                "wantedProtein" to protein!!,
                "wantedFat" to fat!!,) }else null

        val userInformation = if (activity!=null||currentWeight!=null||gender!=null||height!=null||wantedWeight!=null||workType!=null||workoutInWeek!=null){
            mapOf<String,String>("activityInDay" to activity!!,
                "currentWeight" to currentWeight!!,
                "gender" to gender!!,
                "height" to height!!,
                "wantedWeight" to wantedWeight!!,
                "workType" to workType!!,
                "workoutInWeek" to workoutInWeek!!)}else null

        User(username,
        nutritionValues)
    }

    suspend fun saveNutritionValues(values:Map<String,Double>){
        settingsDataStore.edit { settings ->
            values.forEach {
                val key = doublePreferencesKey(it.key)

                settings[key] = it.value
            }
        }
    }

    suspend fun getNutritionValues(values:Map<String,Double>){

    }

}