package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CaloriesViewModel : ViewModel() {
    private val userId = FirebaseAuth.getInstance().uid.toString()

    private val db = Firebase.firestore

    private val mValues = MutableLiveData<Map<*, *>>()

    private val mBreakfastProducts = MutableLiveData<MutableList<JournalEntry>>()
    private val mLunchProducts = MutableLiveData<MutableList<JournalEntry>>()
    private val mDinnerProducts = MutableLiveData<MutableList<JournalEntry>>()
    private val mSupperProducts = MutableLiveData<MutableList<JournalEntry>>()

    private val idList = ArrayList<String>()

    fun setUpValues() {
        db.collection("users").document(userId).get().addOnSuccessListener {
            if (it.exists()) {
                mValues.value = it.data?.get("nutritionValues") as Map<*, *>
            }
        }
    }


    fun getJournalEntries() {
        db.collection("users").document(userId).collection("journal").whereEqualTo("date",CurrentDate.getDate()!!.time.toString("EEEE, dd-MM-yyyy"))
                .get()
                .addOnSuccessListener {

                    val journalProductList = ArrayList<JournalEntry>()

                    for (document in it.documents){
                        journalProductList.add(document.toObject(JournalEntry::class.java)!!)
                        idList.add(document.id)
                    }

                    getProducts(journalProductList)
                }

    }

    private fun getProducts(list:ArrayList<JournalEntry>){

        if (list.isNotEmpty()){
            val breakfast = mutableListOf<JournalEntry>()
            val lunch = mutableListOf<JournalEntry>()
            val dinner = mutableListOf<JournalEntry>()
            val supper = mutableListOf<JournalEntry>()

            for (entry in list){
                when(entry.mealName){
                    "Breakfast" -> breakfast.add(entry)
                    "Lunch" -> lunch.add(entry)
                    "Dinner" -> dinner.add(entry)
                    "Supper" -> supper.add(entry)
                }
            }
            mBreakfastProducts.value = breakfast
            mLunchProducts.value = lunch
            mDinnerProducts.value = dinner
            mSupperProducts.value = supper
        }

    }

    fun getValues(): MutableLiveData<Map<*, *>> = mValues

    fun getBreakfastProducts():MutableLiveData<MutableList<JournalEntry>> = mBreakfastProducts
    fun getLunchProducts():MutableLiveData<MutableList<JournalEntry>> = mLunchProducts
    fun getDinnerProducts():MutableLiveData<MutableList<JournalEntry>> = mDinnerProducts
    fun getSupperProducts():MutableLiveData<MutableList<JournalEntry>> = mSupperProducts

}
