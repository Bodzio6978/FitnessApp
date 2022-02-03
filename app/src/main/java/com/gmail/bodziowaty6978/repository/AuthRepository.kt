package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.state.UiState
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val db = Firebase.firestore
    private val userCollection = db.collection("users")
    private val instance = FirebaseAuth.getInstance()

    suspend fun registerUser(email:String,password:String):AuthResult?{
        return try {
            instance.createUserWithEmailAndPassword(email,password).await()
        }catch (e:Exception){
            null
        }
    }

    suspend fun setUsername(username:String,userId:String):UiState{
        return try {
            userCollection.document(userId).set(mapOf("username" to username)).await()
            UiState.Success
        }catch (e:Exception){
            UiState.Error("An error has occurred when setting your username")
        }
    }

}