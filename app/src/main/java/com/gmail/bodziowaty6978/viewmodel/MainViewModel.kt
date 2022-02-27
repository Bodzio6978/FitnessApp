package com.gmail.bodziowaty6978.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.functions.toCalendar
import com.gmail.bodziowaty6978.functions.toShortString
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.LogEntity
import com.gmail.bodziowaty6978.model.WeightEntity
import com.gmail.bodziowaty6978.other.DataStoreManager
import com.gmail.bodziowaty6978.repository.MainRepository
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.Resource
import com.gmail.bodziowaty6978.state.UserInformationState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val repository: MainRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    val userInformation = dataStoreManager.getUserInformation.asLiveData()

    val userInformationState = MutableLiveData<UserInformationState>()
    val dataState = MutableStateFlow<DataState>(DataState.Loading)

    val journalEntries = MutableLiveData<Resource<MutableMap<String, MutableMap<String, JournalEntry>>>>()
    val weightEntries = MutableLiveData<MutableList<WeightEntity>>()
    val logEntries = MutableLiveData<MutableList<LogEntity>>()

    val currentLogStrike = MutableStateFlow<Int>(1)

    //WEIGHT************************************************************************************************

    fun isUserLogged(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun requireData(date: String) {
        viewModelScope.launch {
            val journal = async { fetchJournalEntries(date) }
            val weight = async { fetchWeightEntries() }
            val log = async { fetchLogEntries() }

            awaitAll(journal, weight, log)

            dataState.emit(DataState.Success)
        }
    }

    private suspend fun fetchJournalEntries(date: String) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                when(val result = repository.getJournalEntries(date)){
                    is Resource.Success -> {
                        mapEntries(result)
                    }
                    else -> {
                        journalEntries.postValue(Resource.Error("Error occurred when trying to download journal entries"))
                    }
                }
            }
        }
    }

    private suspend fun mapEntries(resource:Resource.Success<List<DocumentSnapshot>>){
        withContext(dispatchers.default){
            val mappedEntries = resource.data!!.map {
                it.id to it.toObject(JournalEntry::class.java)!!
            }.toMap().toMutableMap()

            journalEntries.postValue(Resource.Success(sortProducts(mappedEntries)))
        }
    }

    private suspend fun fetchWeightEntries() {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                val result = repository.getWeightEntries()
                Log.e(TAG,result.toString())
                weightEntries.postValue(result)
            }
        }
    }

    private suspend fun fetchLogEntries() {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                logEntries.postValue(repository.getLastLogEntry())
                Log.e(TAG,logEntries.value.toString())
            }
        }
    }

    fun getEntries(journalEntries: List<Map<String, JournalEntry>>): List<JournalEntry> {
        val allEntries = mutableListOf<JournalEntry>()
        for (map in journalEntries) {
            allEntries.addAll(map.values)
        }
        return allEntries
    }

    fun checkUser() {
        viewModelScope.launch {
            if (userInformation.value?.nutritionValues == null || userInformation.value?.userInformation == null) {
                userInformationState.value = UserInformationState.NoInformation
            } else {
                userInformationState.value = UserInformationState.HasInformation
            }
        }
    }

    fun calculateStrike(entry: LogEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                val currentDate = Calendar.getInstance()
                val lastDateCalendar = toCalendar(Date(entry.time))

                Log.e(TAG+"huj",logEntries.value.toString())

                if (lastDateCalendar != null) {

                    val lastTimeLogged = lastDateCalendar.toShortString()

                    if (lastTimeLogged != currentDate.toShortString()) {
                        lastDateCalendar.add(Calendar.DAY_OF_MONTH, 1)

                        if (lastDateCalendar.toShortString() == currentDate.toShortString()) {
                            Log.e(TAG, "User logged for another day in a row")
                            createLogEntry(entry.strike + 1)
                        } else {
                            Log.e(TAG, "User logged again but not for another day in a row")
                            createLogEntry()
                        }
                    }
                }
            }
        }
    }

    fun setDialogPermission(areEnabled: Boolean) {
        viewModelScope.launch {
            Log.e(TAG, "Dialog permission")
            withContext(dispatchers.io) {
                dataStoreManager.saveWeightDialogsPermission(areEnabled)
            }
        }
    }


    fun calculateWeightProgress(weightLogs: MutableList<WeightEntity>): String {
        if (weightLogs.size > 1) {
            weightLogs.sortByDescending { it.time }
            val size = weightLogs.size

            val firstHalf = weightLogs.toTypedArray().copyOfRange(0, (size + 1) / 2)
            val secondHalf = weightLogs.toTypedArray().copyOfRange((size + 1) / 2, size)

            val firstAverage =
                firstHalf.toMutableList().sumOf { it.value } / firstHalf.size.toDouble()
            val secondAverage =
                secondHalf.toMutableList().sumOf { it.value } / secondHalf.size.toDouble()

            val difference = (firstAverage - secondAverage).round(2)
            val sign = if (firstAverage > secondAverage) "+" else ""

            return if (difference != 0.0) ("$sign${difference}kg") else ""
        }
        return ""
    }

    fun checkIfWeightHasBeenEnteredToday(weights: MutableList<WeightEntity>): Boolean {
        Log.d(TAG, "Check if weight has been entered today")

        for (entry in weights.toMutableList()) {
            if (entry.date == Calendar.getInstance().toShortString()) return true
        }

        return false
    }


    fun setWeightEntry(value: Double) {
        Log.d(TAG, "Set today weight")

        val weightEntry = WeightEntity(
            id = 0,
            time = Calendar.getInstance().timeInMillis,
            value = value,
            date = Calendar.getInstance().toShortString()
        )

        viewModelScope.launch(dispatchers.io) {
            if (!checkIfWeightHasBeenEnteredToday(weightEntries.value!!)){
                val isSuccessful = repository.addWeightEntry(weightEntry)
                Log.e(TAG,isSuccessful.toString())

                if (isSuccessful is DataState.Success) addNewWeightEntry(weightEntry)
                else if (isSuccessful is DataState.Error) dataState.value = isSuccessful
            }
        }
    }


    private suspend fun addNewWeightEntry(entry: WeightEntity) {
        viewModelScope.launch {
            withContext(dispatchers.default) {
                val entries = weightEntries.value

                if (entries != null) {
                    entries.add(entry)
                    weightEntries.postValue(entries!!)
                }

            }
        }
    }


    //WEIGHT************************************************************************************************


    //JOURNAL************************************************************************************************


    private fun sortProducts(list: MutableMap<String, JournalEntry>): MutableMap<String, MutableMap<String, JournalEntry>> {
        val breakfast = mutableMapOf<String, JournalEntry>()
        val lunch = mutableMapOf<String, JournalEntry>()
        val dinner = mutableMapOf<String, JournalEntry>()
        val supper = mutableMapOf<String, JournalEntry>()

        for (key in list.keys) {
            when (list[key]?.mealName) {
                "Breakfast" -> breakfast[key] = list[key]!!
                "Lunch" -> lunch[key] = list[key]!!
                "Dinner" -> dinner[key] = list[key]!!
                "Supper" -> supper[key] = list[key]!!
            }
        }
        return mutableMapOf<String, MutableMap<String, JournalEntry>>(
            "Breakfast" to breakfast,
            "Lunch" to lunch,
            "Dinner" to dinner,
            "Supper" to supper
        )
    }

    fun refreshJournalEntries(date: String) {
        viewModelScope.launch {
            if (CurrentDate.date().value != null) {
                fetchJournalEntries(date)
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun removeItem(entry: JournalEntry) {
        viewModelScope.launch(dispatchers.default) {
            val entryList = journalEntries.value?.data

            if (entryList != null) {

                val mealList = entryList[entry.mealName]

                if (mealList != null) {
                    for (key in mealList.keys) {

                        if (mealList[key] == entry) {

                            withContext(dispatchers.io) {
                                val result = repository.removeJournalEntry(key)

                                withContext(dispatchers.default){
                                    if (result is DataState.Success){
                                        mealList.remove(key)
                                        entryList[entry.mealName] = mealList
                                        journalEntries.postValue(Resource.Success(entryList))
                                    }
                                }
                            }
                        }
                    }
                }
            }


        }
    }

    suspend fun getEntryId(entry:JournalEntry):String?{
        val entryList = journalEntries.value?.data

        if (entryList != null) {

            val mealList = entryList[entry.mealName]

            if (mealList != null) {
                for (key in mealList.keys) {

                    if (mealList[key] == entry) {

                        return key
                    }
                }
            }
        }
        return null
    }


    //JOURNAL************************************************************************************************

    //SUMMARY************************************************************************************************

    suspend fun createLogEntry(strike: Int = 1) {
        viewModelScope.launch(dispatchers.io) {
            val currentTime = Calendar.getInstance().timeInMillis
            val logEntry = LogEntity(id = 0, time = currentTime, strike = strike)

            val result = repository.addLogEntry(logEntry)
            if (result is DataState.Success) currentLogStrike.emit(strike)
            else if (result is DataState.Error) dataState.emit(result)

        }
    }


    //SUMMARY************************************************************************************************
}

