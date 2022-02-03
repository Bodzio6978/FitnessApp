package com.gmail.bodziowaty6978.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.toShortString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.LogEntry
import com.gmail.bodziowaty6978.model.WeightEntry
import com.gmail.bodziowaty6978.other.BaseViewModel
import com.gmail.bodziowaty6978.repository.MainRepository
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.gmail.bodziowaty6978.state.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.*
import java.util.*

class MainViewModel(
) : BaseViewModel<UiState>() {
    val mHasPermissionBeenSet = MutableLiveData<Boolean>()
    val mHasWeightBeenSet = MutableLiveData<Boolean>()
    val mHasTodayWeightBeenEntered = MutableLiveData<Boolean>()

    private val repository = MainRepository()

    val mJournalEntries = MutableLiveData<MutableMap<String, MutableMap<String, JournalEntry>>>()

    val mLastWeights = MutableLiveData<MutableList<WeightEntry>>()

    val mLastLogEntry = MutableLiveData<LogEntry>()

    val mCurrentLogStrike = MutableLiveData<Int>()

    //WEIGHT************************************************************************************************

    fun isUserLogged(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    private suspend fun checkUser(id:String) {
        withContext(Dispatchers.IO) {
            val userTask = async{UserInformation.getUser(id)}
            userTask.await()
            val user = UserInformation.user().value!!
            if (user.nutritionValues==null||user.userInformation==null) uiState.postValue(UiState.NoInformation)
        }
    }

    fun requireData() {
        viewModelScope.launch {
            val userId = setUserId()
            
            val user = async{checkUser(userId)}
            user.await()

            val weight = async { fetchWeightEntries() }
            val journal = async { fetchJournalEntries(CurrentDate.date().value!!.toShortString()) }
            val log = async { fetchLogEntries() }

            awaitAll(weight, journal, log)
            uiState.postValue(UiState.Success)
        }
    }

    private fun setUserId():String {
        return repository.setUserId()
    }

    private suspend fun fetchLogEntries() {
//        val lastLogEntryList = repository.getLastLogEntry()
//
//        if (lastLogEntryList.isEmpty()){
//
//        }else{
//            val lastLogEntry = lastLogEntryList[0]
//            mLastLogEntry.postValue(lastLogEntry)
//        }
//

    }

    private suspend fun fetchWeightEntries() {
        withContext(Dispatchers.IO) {
            mLastWeights.postValue(repository.getWeightEntries())
        }
    }

    private suspend fun fetchJournalEntries(date: String) {
        withContext(Dispatchers.IO) {
            mapEntries(repository.getJournalEntries(date))
        }
    }

    fun setDialogPermission(areEnabled: Boolean) {
        Log.e(TAG, "Dialog permission")
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.setWeightDialogPermission(areEnabled)

            if (result is UiState.Error) uiState.postValue(result)
        }
    }

    fun checkIfWeightHasBeenEnteredToday() {
        Log.d(TAG, "Check if weight has been entered today")
//        viewModelScope.launch {
//            db.collection("users").document(userId).collection("weight")
//                .whereEqualTo("date", Calendar.getInstance().time.toString("dd-MM-yyyy")).get()
//                .addOnSuccessListener {
//                    mHasTodayWeightBeenEntered.value = !it.isEmpty
//                }.addOnFailureListener {
//                    Log.e(TAG, it.message.toString())
//                }
//        }
    }


    fun setWeightEntry(value: Double) {
        Log.d(TAG, "Set today weight")

        val weightEntry = WeightEntry(
            time = Calendar.getInstance().timeInMillis,
            value = value,
            date = Calendar.getInstance().toShortString()
        )

        viewModelScope.launch(Dispatchers.IO) {
            val isSuccessful = repository.addWeightEntry(weightEntry)

            if (isSuccessful is UiState.Success) addNewWeightEntry(weightEntry)
            else if (isSuccessful is UiState.Error) uiState.postValue(isSuccessful)

        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    private suspend fun addNewWeightEntry(entry: WeightEntry) {
        withContext(Dispatchers.Main) {
            val weightEntries = mLastWeights.value
            weightEntries?.add(entry)

            if (weightEntries != null) {
                mLastWeights.postValue(weightEntries)
                mHasWeightBeenSet.postValue(true)
            }
        }
    }

    //WEIGHT************************************************************************************************


    //JOURNAL************************************************************************************************


    private suspend fun mapEntries(snapshots: List<DocumentSnapshot>) {
        withContext(Dispatchers.Default) {
            val entriesList = mutableMapOf<String, JournalEntry>()

            for (document in snapshots) {
                entriesList[document.id] = document.toObject(JournalEntry::class.java)!!
            }

            sortProducts(entriesList)
        }

    }

    private suspend fun sortProducts(list: MutableMap<String, JournalEntry>) {
        withContext(Dispatchers.Default) {
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
            mJournalEntries.postValue(
                mutableMapOf<String, MutableMap<String, JournalEntry>>(
                    "Breakfast" to breakfast,
                    "Lunch" to lunch,
                    "Dinner" to dinner,
                    "Supper" to supper
                )
            )
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
    fun removeItem(entry: JournalEntry, mealName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val entryList = mJournalEntries.value

            if (entryList != null) {

                val mealList = entryList[mealName]

                if (mealList != null) {
                    for (key in mealList.keys) {

                        if (mealList[key] == entry) {

                            withContext(Dispatchers.IO) {
                                repository.removeJournalEntry(key)
                            }


                        }
                    }
                }
            }


        }
    }


    //JOURNAL************************************************************************************************

    //SUMMARY************************************************************************************************

    fun createLogEntry(strike: Int = 1) {
        val currentTime = Calendar.getInstance().timeInMillis
        val logEntry = LogEntry(currentTime, strike)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = repository.addLogEntry(logEntry)
                if (result == UiState.Success) mCurrentLogStrike.postValue(strike)
            }
        }
    }


    //SUMMARY************************************************************************************************

}
