package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AddViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val userId = Firebase.auth.currentUser?.uid

    private val searchResult = MutableLiveData<List<Product>>()
    private val idsResult = MutableLiveData<List<String>>()

    val mSnackbarMessage = MutableLiveData<String>()

    val mButtonPressed = MutableLiveData<Int>()
    val mMealName = MutableLiveData<String>()
    val mClickedProduct = MutableLiveData<Pair<String,Product>>()
    val mScannedBarcode = MutableLiveData<String>()

    fun initializeHistory() {

        if (userId != null) {
            db.collection("users").document(userId).collection("journal").orderBy("time",Query.Direction.DESCENDING).limit(30).get().addOnSuccessListener {
                if (!it.isEmpty){
                    val entryHistory = it.toObjects(JournalEntry::class.java)
                    searchResult.value = convertEntries(entryHistory)
                }
            }
        }

    }

    fun search(text: String) {

        db.collection("products").whereArrayContains("searchKeywords", text).limit(20).get().addOnSuccessListener {
            val products = mutableListOf<Product>()
            val ids = mutableListOf<String>()

            for (product in it.documents) {
                products.add(product.toObject(Product::class.java)!!)
                ids.add(product.id)
            }
            searchResult.value = products
            idsResult.value = ids
        }
    }

    private fun convertEntries(list: List<JournalEntry>): List<Product> {

        val products = mutableListOf<Product>()
        val idList = mutableListOf<String>()

        for (entry in list) {
            if (!idList.contains(entry.id)){
                products.add(
                    Product(
                        entry.id,
                        entry.name,
                        entry.brand,
                        entry.weight,
                        0,
                        entry.unit,
                        entry.calories,
                        entry.carbs,
                        entry.protein,
                        entry.fat,
                        "fakeProduct"
                    )
                )
                idList.add(entry.id)
            }
        }

        return products
    }

    fun checkIfBarcodeExists(barcode:String){
        db.collection("products").whereEqualTo("barcode",barcode).get().addOnSuccessListener {
            if (it.isEmpty){
                mSnackbarMessage.value = "There is no product with this barcode"
            }
        }
    }

    fun getSearchResult(): MutableLiveData<List<Product>> = searchResult
    fun getIds(): MutableLiveData<List<String>> = idsResult


}