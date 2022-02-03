package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.functions.toString
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val addingState = MutableLiveData<Boolean>(false)

    private val currentProduct = MutableLiveData<Product>()

    fun addProduct(product: Product, id: String, weight: String, mealName: String) {


        val weightValue = weight.replace(",", ".").toDouble()

        val calories = (product.calories.toDouble() * weightValue / 100.0).toInt()
        val carbohydrates = (product.carbs * weightValue / 100.0).round(2)
        val protein = (product.protein * weightValue / 100.0).round(2)
        val fat = (product.fat * weightValue / 100.0).round(2)

        val journalEntry = JournalEntry(
            product.name!!,
            id,
            mealName,
            CurrentDate.date().value!!.time.toString("yyyy-MM-dd"),
            CurrentDate.date().value!!.timeInMillis,
            product.brand!!,
            weightValue,
            product.unit!!,
            calories,
            carbohydrates,
            protein,
            fat
        )

        db.collection("users").document(userId).collection("journal")
            .add(journalEntry)
            .addOnSuccessListener {

                addingState.value = true

            }


    }

    fun getProduct(id: String) {

        db.collection("products").document(id).get().addOnSuccessListener {
            currentProduct.value = it.toObject(Product::class.java)
        }
    }

    fun getAddingState(): MutableLiveData<Boolean> = addingState
    fun getCurrentProduct(): MutableLiveData<Product> = currentProduct

}