package com.gmail.bodziowaty6978.repository

import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.model.User
import com.gmail.bodziowaty6978.singleton.Strings
import com.gmail.bodziowaty6978.state.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class UserData {

    private val db = Firebase.firestore

    private fun getUserId() = FirebaseAuth.getInstance().currentUser!!.uid

    suspend fun getUserInformation() : Resource<User>{
        return try {
            Resource.Success(db.collection("users").document(getUserId()).get().await().toObject(User::class.java)!!)
        }catch (e:Exception){
            Resource.Error(Strings.get(R.string.error))
        }
    }

}