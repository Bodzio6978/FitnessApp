package com.gmail.bodziowaty6978.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.toShortString
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.LogEntry
import com.gmail.bodziowaty6978.model.WeightEntry
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class MainViewModel : ViewModel() {
    val mHasPermissionBeenSet = MutableLiveData<Boolean>()
    val mHasWeightBeenSet = MutableLiveData<Boolean>()
    val mHasTodayWeightBeenEntered = MutableLiveData<Boolean>()

    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    val mJournalEntries = MutableLiveData<MutableMap<String, MutableMap<String, JournalEntry>>>()

    val mLastWeights = MutableLiveData<MutableList<WeightEntry>>()

    val mLastLogEntry = MutableLiveData<LogEntry>()

    val mCurrentLogStrike = MutableLiveData<Int>()

    //WEIGHT************************************************************************************************

    fun setDialogPermission(areEnabled: Boolean) {
        Log.e(TAG, "Dialog permission")
        db.collection("users").document(userId).set(
                mapOf("areWeightDialogsEnabled" to areEnabled),
                SetOptions.merge()
        ).addOnSuccessListener {
            UserInformation.mAreWeightDialogsEnabled.value = areEnabled
            mHasPermissionBeenSet.value = true
        }.addOnFailureListener {
            mHasPermissionBeenSet.value = false
        }
    }

    fun checkIfWeightHasBeenEnteredToday() {
        Log.e(TAG, "Check if weight has been entered today")
        db.collection("users").document(userId).collection("weight")
                .whereEqualTo("date", Calendar.getInstance().time.toString("dd-MM-yyyy")).get().addOnSuccessListener {
                    mHasTodayWeightBeenEntered.value = !it.isEmpty
                }.addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun setTodayWeight(value: Double) {
        val weightEntry = WeightEntry(time = Calendar.getInstance().timeInMillis, value = value, date = Calendar.getInstance().toShortString())
        Log.e(TAG, "Set today weight")
        db.collection("users").document(userId).collection("weight").add(
                WeightEntry()
        ).addOnSuccessListener {
            val weightEntries = mLastWeights.value
            weightEntries?.add(weightEntry)

            if (weightEntries!=null){
                mLastWeights.value = weightEntries
            }

            mHasWeightBeenSet.value = true
        }.addOnFailureListener {
            mHasWeightBeenSet.value = false
            Log.e(TAG, it.message.toString())
        }
    }

    //WEIGHT************************************************************************************************


    //JOURNAL************************************************************************************************

    fun downloadJournalEntries(date: String) {
        db.collection("users").document(userId).collection("journal").whereEqualTo("date", date).orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    val entriesList = mutableMapOf<String, JournalEntry>()

                    for (document in it.documents) {
                        entriesList[document.id] = document.toObject(JournalEntry::class.java)!!
                    }

                    sortProducts(entriesList)
                }

    }

    private fun sortProducts(list: MutableMap<String, JournalEntry>) {
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
        mJournalEntries.value = mutableMapOf<String, MutableMap<String, JournalEntry>>(
                "Breakfast" to breakfast,
                "Lunch" to lunch,
                "Dinner" to dinner,
                "Supper" to supper
        )
    }

    fun refresh() {
        if (CurrentDate.date.value != null) {
            downloadJournalEntries(CurrentDate.date.value!!.time.toString("dd-MM-yyyy"))
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun removeItem(entry: JournalEntry, mealName: String) {
        val entryList = mJournalEntries.value
        if (entryList != null) {

            val mealList = entryList[mealName]

            if (mealList != null) {
                for (key in mealList.keys) {

                    if (mealList[key] == entry) {

                        db.collection("users").document(userId).collection("journal").document(key).delete().addOnSuccessListener {
                            mealList.remove(key)
                            entryList[mealName] = mealList
                            mJournalEntries.value = entryList
                        }

                    }
                }
            }
        }
    }


    //JOURNAL************************************************************************************************

    //SUMMARY************************************************************************************************

    fun getWeightEntries() {
        db.collection("users").document(userId).collection("weight")
                .limit(14).orderBy("time", Query.Direction.DESCENDING)
                .get().addOnSuccessListener {
                    val weightDocuments = mutableListOf<WeightEntry>()

                    for (document in it.documents) {
                        if (document != null) {
                            weightDocuments.add(document.toObject(WeightEntry::class.java)!!)
                        }
                    }

                    mLastWeights.value = weightDocuments

                }.addOnFailureListener {
                    Log.e(TAG, it.message.toString())
                }
    }

    fun addLogEntry(calendar:Calendar = Calendar.getInstance(), strike:Int = 1){

        db.collection("users").document(userId).collection("logEntries").add(LogEntry(calendar.timeInMillis,strike)).addOnSuccessListener {
            mCurrentLogStrike.value = strike
        }.addOnFailureListener {
            Log.e(TAG,it.message.toString())
        }
    }

    fun getLastTimeLogged(limit:Long){
        Log.e(TAG,"Getting log entries")
        db.collection("users").document(userId).collection("logEntries").limit(limit).orderBy("time", Query.Direction.DESCENDING).get().addOnSuccessListener {
            if (!it.isEmpty){
                var lastEntry = LogEntry()

                for(document in it.documents){
                    lastEntry = document.toObject(LogEntry::class.java)!!
                }

                mLastLogEntry.value = lastEntry

            }else{
                addLogEntry()
            }

        }.addOnFailureListener {
            Log.e(TAG,it.message.toString())
        }
    }


    //SUMMARY************************************************************************************************

}
