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
import com.gmail.bodziowaty6978.model.MeasurementEntity
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
    val weightState = MutableLiveData<DataState>()

    val journalEntries =
        MutableLiveData<Resource<MutableMap<String, MutableMap<String, JournalEntry>>>>()
    val weightEntries = MutableLiveData<MutableList<WeightEntity>>()
    val logEntries = MutableLiveData<MutableList<LogEntity>>()
    val measurementEntries = MutableLiveData<MutableList<MeasurementEntity>>()

    val currentLogStrike = MutableStateFlow<Int>(1)

    fun isUserLogged(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun requireData(date: String) {
        viewModelScope.launch {
            val journal = async { fetchJournalEntries(date) }
            val weight = async { fetchWeightEntries() }
            val log = async { fetchLogEntries() }
            val measurement = async { fetchMeasurementEntries() }

            awaitAll(journal, weight, log, measurement)

            dataState.emit(DataState.Success)
        }
    }

    private suspend fun fetchMeasurementEntries() {
        viewModelScope.launch(dispatchers.io) {
            val result = repository.getMeasurementEntries()
            measurementEntries.postValue(result)
        }
    }

    private suspend fun fetchJournalEntries(date: String) {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                when (val result = repository.getJournalEntries(date)) {
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

    private suspend fun mapEntries(resource: Resource.Success<List<DocumentSnapshot>>) {
        withContext(dispatchers.default) {
            val mappedEntries = resource.data!!.map {
                it.id to it.toObject(JournalEntry::class.java)!!
            }.toMap().toMutableMap()

            journalEntries.postValue(Resource.Success(sortProducts(mappedEntries)))
        }
    }

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


    private suspend fun fetchWeightEntries() {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                val result = repository.getWeightEntries()
                weightEntries.postValue(result)
            }
        }
    }

    private suspend fun fetchLogEntries() {
        viewModelScope.launch {
            withContext(dispatchers.io) {
                logEntries.postValue(repository.getLastLogEntry())
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
            if (!checkIfWeightHasBeenEnteredToday(weightEntries.value!!)) {
                val result = repository.addWeightEntry(weightEntry)

                if (result is DataState.Success) addNewWeightEntry(weightEntry)
                else if (result is DataState.Error) weightState.postValue(result)
            }else{
                weightState.postValue(DataState.Error("Weight has been already entered today"))
            }
        }
    }


    private suspend fun addNewWeightEntry(entry: WeightEntity) {
        viewModelScope.launch {
            withContext(dispatchers.default) {
                val entries = weightEntries.value

                if (entries != null) {
                    entries.add(entry)
                    weightEntries.postValue(entries)
                }

            }
        }
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

                                withContext(dispatchers.default) {
                                    if (result is DataState.Success) {
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

    fun getEntryId(entry: JournalEntry): String? {
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

    fun calculateProgress(measurementEntities: MutableList<MeasurementEntity>): MutableMap<String, String> {
        val olderEntity = measurementEntities[0]
        val latestEntity = measurementEntities[1]

        val hipProgress = (latestEntity.hips - olderEntity.hips).toString()
        val waistProgress = (latestEntity.waist - olderEntity.waist).toString()
        val thighProgress = (latestEntity.thigh - olderEntity.thigh).toString()
        val bustProgress = (latestEntity.bust - olderEntity.bust).toString()
        val bicepsProgress = (latestEntity.biceps - olderEntity.biceps).toString()
        val calfProgress = (latestEntity.calf - olderEntity.calf).toString()

        val progressMap = mutableMapOf<String, String>(
            "hips" to hipProgress,
            "waist" to waistProgress,
            "thigh" to thighProgress,
            "bust" to bustProgress,
            "biceps" to bicepsProgress,
            "calf" to calfProgress
        )

        progressMap.forEach {
            if (it.value.toDouble() > 0) progressMap[it.key] = "+${it.value}"
        }

        return progressMap

    }

    suspend fun createLogEntry(strike: Int = 1) {
        viewModelScope.launch(dispatchers.io) {
            val currentTime = Calendar.getInstance().timeInMillis
            val logEntry = LogEntity(id = 0, time = currentTime, strike = strike)

            val result = repository.addLogEntry(logEntry)
            if (result is DataState.Success) currentLogStrike.emit(strike)
            else if (result is DataState.Error) dataState.emit(result)

        }
    }
}

