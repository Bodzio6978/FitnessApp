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

    private val mBreakfastProducts = MutableLiveData<MutableMap<String,JournalEntry>>()
    private val mLunchProducts = MutableLiveData<MutableMap<String,JournalEntry>>()
    private val mDinnerProducts = MutableLiveData<MutableMap<String,JournalEntry>>()
    private val mSupperProducts = MutableLiveData<MutableMap<String,JournalEntry>>()

    private val mDeletedProduct = MutableLiveData<ArrayList<String>>()

    fun setUpValues() {
        db.collection("users").document(userId).get().addOnSuccessListener {
            if (it.exists()) {
                mValues.value = it.data?.get("nutritionValues") as Map<*, *>
            }
        }
    }

    fun removeItem(position:Int,mealName:String){
        val mealList = when(mealName){
            "Breakfast" -> mBreakfastProducts.value
            "Lunch" -> mLunchProducts.value
            "Dinner" -> mDinnerProducts.value
            else -> mSupperProducts.value
        }
        val key = mealList?.keys?.toList()?.get(position)

        db.collection("users").document(userId).collection("journal").document(key.toString()).delete().addOnSuccessListener {
            mDeletedProduct.value = arrayListOf(position.toString(),mealName)
        }

    }


    fun getJournalEntries() {
        db.collection("users").document(userId).collection("journal").whereEqualTo("date",CurrentDate.getDate()!!.time.toString("EEEE, dd-MM-yyyy"))
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty){
                        val journalProductList = mutableMapOf<String,JournalEntry>()

                        for (document in it.documents){
                            journalProductList[document.id] = document.toObject(JournalEntry::class.java)!!
                        }

                        getProducts(journalProductList)
                    }
                }

    }

    private fun getProducts(list:MutableMap<String,JournalEntry>){

        if (list.isNotEmpty()){
            val breakfast = mutableMapOf<String,JournalEntry>()
            val lunch = mutableMapOf<String,JournalEntry>()
            val dinner = mutableMapOf<String,JournalEntry>()
            val supper = mutableMapOf<String,JournalEntry>()

            for (key in list.keys){
                when(list[key]?.mealName){
                    "Breakfast" -> breakfast[key] = list[key]!!
                    "Lunch" -> lunch[key] = list[key]!!
                    "Dinner" -> dinner[key] = list[key]!!
                    "Supper" -> supper[key] = list[key]!!
                }
            }
            mBreakfastProducts.value = breakfast
            mLunchProducts.value = lunch
            mDinnerProducts.value = dinner
            mSupperProducts.value = supper
        }

    }

    fun getValues(): MutableLiveData<Map<*, *>> = mValues

    fun getBreakfastProducts():MutableLiveData<MutableMap<String,JournalEntry>> = mBreakfastProducts
    fun getLunchProducts():MutableLiveData<MutableMap<String,JournalEntry>> = mLunchProducts
    fun getDinnerProducts():MutableLiveData<MutableMap<String,JournalEntry>> = mDinnerProducts
    fun getSupperProducts():MutableLiveData<MutableMap<String,JournalEntry>> = mSupperProducts

    fun getDeleteProduct():MutableLiveData<ArrayList<String>> = mDeletedProduct

}
