package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddViewModel:ViewModel() {
    private val db = Firebase.firestore

    private val searchResult = MutableLiveData<ArrayList<Product>>()
    private val idsResult = MutableLiveData<ArrayList<String>>()

    fun initializeHistory(){

    }


    fun search(text: String){

        db.collection("products").orderBy("name").startAt(text).endAt("$text\\uf8ff").limit(20).get().addOnSuccessListener {
            val meals = ArrayList<Product>()
            val ids = ArrayList<String>()

            for(meal in it.documents){
                meals.add(meal.toObject(Product::class.java)!!)
                ids.add(meal.id)
            }
            searchResult.value = meals
            idsResult.value = ids
        }
    }

    fun getSearchResult():MutableLiveData<ArrayList<Product>> = searchResult
    fun getIds():MutableLiveData<ArrayList<String>> = idsResult


}