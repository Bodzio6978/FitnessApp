package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.model.JournalProduct
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CaloriesViewModel : ViewModel() {
    private val userId = FirebaseAuth.getInstance().uid.toString()

    private val db = Firebase.firestore

    private val mValues = MutableLiveData<Map<*, *>>()

    private val mProducts = MutableLiveData<MutableMap<JournalProduct,Product>>()
    private val idList = ArrayList<String>()

    fun setUpValues() {
        db.collection("users").document(userId).get().addOnSuccessListener {
            if (it.exists()) {
                mValues.value = it.data?.get("nutritionValues") as Map<*, *>
            }
        }
    }


    fun getJournalProducts() {
        db.collection("users").document(userId).collection("journal").whereEqualTo("date",CurrentDate.getDate()!!.time.toString("EEEE, dd-MM-yyyy"))
                .get()
                .addOnSuccessListener {

                    val journalProductList = ArrayList<JournalProduct>()

                    for (document in it.documents){
                        journalProductList.add(document.toObject(JournalProduct::class.java)!!)
                        idList.add(document.id)
                    }

                    getProducts(journalProductList)
                }

    }

    private fun getProducts(list:ArrayList<JournalProduct>){
        val map = mutableMapOf<JournalProduct,Product>()

        val productRef = db.collection("products")

        for (item in list){
            productRef.document(item.id).get().addOnSuccessListener {
                map[item] = it.toObject(Product::class.java) as Product
            }
        }

        mProducts.value = map
    }

    fun getValues(): MutableLiveData<Map<*, *>> = mValues

    fun getMutableJournalProducts(): MutableLiveData<MutableMap<JournalProduct,Product>> = mProducts


}
