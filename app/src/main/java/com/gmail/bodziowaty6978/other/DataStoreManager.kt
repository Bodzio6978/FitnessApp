package com.gmail.bodziowaty6978.other

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
        val nutritionValuesKey = stringPreferencesKey("nutritionValues")
        val userInformationKey = stringPreferencesKey("userInformation")
        val areWeightDialogsEnabled = booleanPreferencesKey("areWeightDialogsEnabled")
        val username = stringPreferencesKey("username")
    }

    suspend fun saveUsername(username:String){
        settingsDataStore.edit {
            it[Keys.username] = username
        }
    }

    suspend fun saveNutritionValues(nutritionValues:Map<String,Double>){
        val gson = Gson()
        val data = gson.toJson(nutritionValues)
        settingsDataStore.edit {
            it[Keys.nutritionValuesKey] = data
        }
    }

    suspend fun saveUserInformation(userInformation:Map<String,String>){
        val gson = Gson()
        val data = gson.toJson(userInformation)
        settingsDataStore.edit {
            it[Keys.userInformationKey] = data
        }
    }

    suspend fun saveWeightDialogsPermission(areEnabled:Boolean){
        settingsDataStore.edit {
            it[Keys.areWeightDialogsEnabled] = areEnabled
        }
    }

    val getUserInformation:Flow<User> = settingsDataStore.data.catch { exception ->
        if (exception is IOException){
            Log.e(TAG,exception.message.toString())
            emit(emptyPreferences())
        }else{
            throw exception
        }
    }.map {
        val gson = Gson()
        val areWeightDialogsEnabled = it[Keys.areWeightDialogsEnabled]
        val nutritionValuesData = it[Keys.nutritionValuesKey]

        val nutritionValues : Map<String,Double>? = if (nutritionValuesData!=null)
            gson.fromJson(nutritionValuesData,object : TypeToken<Map<String,Double>>() {}.type) else null

        val userInformationData = it[Keys.userInformationKey]

        val userInformation : Map<String,String>? = if (userInformationData!=null)
            gson.fromJson(userInformationData,object : TypeToken<Map<String,String>>() {}.type) else null

        val username = it[Keys.username]

        User(username,nutritionValues,userInformation,areWeightDialogsEnabled)
    }
}