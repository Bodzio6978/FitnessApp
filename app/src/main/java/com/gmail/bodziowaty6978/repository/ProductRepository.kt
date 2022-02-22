package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Price
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid


    suspend fun getProduct(id:String):Resource<Product>{
        return try {
            Resource.Success(db.collection("products").document(id).get().await().toObject(Product::class.java))
        }catch (e:Exception){
            Resource.Error("Failed to get product information")
        }
    }

    suspend fun addEntry(entry: JournalEntry):DataState{
        return try {
            db.collection("users").document(userId).collection("journal")
                .add(entry).await()
            DataState.Success
        }catch (e:Exception){
            DataState.Error("Error occurred when trying to add product to your journal")
        }
    }

    suspend fun addPrices(prices:List<Price>,productId:String):DataState{
        return try {
            db.collection("products").document(productId).set(mapOf("prices" to prices),SetOptions.merge()).await()
            DataState.Success
        }catch (e:Exception){
            DataState.Error("Error occurred when trying to add prices to this product")
        }
    }

    suspend fun updateEntry(entryId:String,entry:JournalEntry):DataState{
        return try {
            db.collection("users").document(userId).collection("journal").document(entryId).set(entry).await()
            DataState.Success
        }catch (e:Exception){
            DataState.Error("Error occurred when trying edit entry")
        }
    }

}