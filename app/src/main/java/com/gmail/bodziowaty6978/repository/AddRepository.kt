package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.state.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AddRepository {

    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

    suspend fun getHistory():Resource<List<JournalEntry>>{
        return try {
            Resource.Success(db.collection("users").document(userId).collection("journal").orderBy("time",
                Query.Direction.DESCENDING).limit(30).get().await().toObjects(JournalEntry::class.java))
        }catch (e:Exception){
            Resource.Error("Error occurred when initializing history")
        }
    }

    suspend fun search(text:String):Resource<QuerySnapshot>{
        return try {
            Resource.Success(db.collection("products").whereArrayContains("searchKeywords", text).limit(20).get().await())
        }catch (e:Exception){
            Resource.Error("Error occurred when initializing history")
        }
    }

    suspend fun checkBarcode(barcode:String):Resource<QuerySnapshot>{
        return try {
            Resource.Success(db.collection("products").whereEqualTo("barcode", barcode).get().await())
        }catch (e:Exception){
            Resource.Error("Error occurred when trying to check your barcode")
        }
    }
}