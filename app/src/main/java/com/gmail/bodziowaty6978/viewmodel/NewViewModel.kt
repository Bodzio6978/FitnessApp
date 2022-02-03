package com.gmail.bodziowaty6978.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.view.newproduct.NewScannerFragment
import com.gmail.bodziowaty6978.view.newproduct.ProductFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class NewViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().uid.toString()

    private val mAction = MutableLiveData<NewProductState>()
    private val currentKey = MutableLiveData<String>()

    private val productFragment = ProductFragment()
    private val scannerFragment = NewScannerFragment()

    fun addNewProduct(product:Product) {

        val searchKeywords = product.name?.let { generateKeyWords(it) }

        val batch = db.batch()
        val productsRef = db.collection("products").document()

        batch.set(productsRef, product, SetOptions.merge())

        batch.set(productsRef, mapOf("searchKeywords" to searchKeywords), SetOptions.merge())

        batch.commit().addOnSuccessListener {
            currentKey.value = productsRef.id
            mAction.value = NewProductState(NewProductState.MEAL_ADDED)
        }

    }

    private fun generateKeyWords(text: String): MutableList<String> {
        var mealName = text.lowercase(Locale.ROOT)
        val keywords = mutableListOf<String>()

        val words = mealName.split(" ")

        for (word in words) {
            var appendString = ""

            for (charPosition in mealName.indices) {
                appendString += mealName[charPosition].toString()
                keywords.add(appendString)
            }

            mealName = mealName.replace("$word ", "")

        }

        return keywords
    }

    fun getAction(): MutableLiveData<NewProductState> = mAction
    fun getKey(): String = currentKey.value.toString()

    fun getProductFragment(): Fragment = productFragment
    fun getScannerFragment(): Fragment = scannerFragment
}

class NewProductState(val value: Int) {

    companion object {
        const val ADDING_MEAL = 0
        const val MEAL_ADDED = 1
        const val ERROR_ADDING_MEAL = 2
    }
}