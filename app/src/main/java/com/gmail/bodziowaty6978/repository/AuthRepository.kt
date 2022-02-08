package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.state.DataState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val db = Firebase.firestore
    private val userCollection = db.collection("users")
    private val instance = FirebaseAuth.getInstance()

    fun getUserId():String{
        return instance.currentUser!!.uid
    }

    suspend fun logInUser(email:String,password: String){
        instance.signInWithEmailAndPassword(email,password).await()
    }

    suspend fun registerUser(email:String,password:String):DataState{
        return try {
            var result :DataState = DataState.Success
            instance.createUserWithEmailAndPassword(email,password).addOnFailureListener {
                result = DataState.Error(it.message.toString())
            }.await()
            return result
        }catch (e:Exception){
            DataState.Error(e.message.toString())
        }
    }

    suspend fun setUsername(username:String,userId:String):DataState{
        return try {
            userCollection.document(userId).set(mapOf("username" to username)).await()
            DataState.Success
        }catch (e:Exception){
            DataState.Error("An error has occurred when setting your username")
        }
    }

}