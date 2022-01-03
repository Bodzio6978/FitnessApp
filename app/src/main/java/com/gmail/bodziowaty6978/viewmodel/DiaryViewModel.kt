package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DiaryViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val products = MutableLiveData<MutableMap<String, MutableMap<String, JournalEntry>>>()

    fun refresh() {
        if (CurrentDate.date.value != null) {
            getJournalEntries(CurrentDate.date.value!!.time.toString("yyyy-MM-dd"))
        }
    }

    fun removeItem(entry: JournalEntry, mealName: String) {
        val entryList = products.value
        if (entryList != null) {

            val mealList = entryList[mealName]

            if (mealList != null) {
                for (key in mealList.keys) {

                    if (mealList[key] == entry) {

                        db.collection("users").document(UserInformation.userId!!).collection("journal").document(key).delete().addOnSuccessListener {
                            mealList.remove(key)
                            entryList[mealName] = mealList
                            products.value = entryList!!
                        }

                    }
                }
            }
        }
    }

    fun getJournalEntries(date: String) {
        db.collection("users").document(UserInformation.userId!!).collection("journal").whereEqualTo("date", date).orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {

                    val journalProductList = mutableMapOf<String, JournalEntry>()

                    for (document in it.documents) {
                        journalProductList[document.id] = document.toObject(JournalEntry::class.java)!!
                    }

                    sortProducts(journalProductList)
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
        products.value = mutableMapOf<String, MutableMap<String, JournalEntry>>(
                "Breakfast" to breakfast,
                "Lunch" to lunch,
                "Dinner" to dinner,
                "Supper" to supper
        )
    }

    fun getProducts(): MutableLiveData<MutableMap<String, MutableMap<String, JournalEntry>>> = products

}
