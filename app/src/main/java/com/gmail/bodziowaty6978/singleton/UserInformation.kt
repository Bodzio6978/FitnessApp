package com.gmail.bodziowaty6978.singleton

import androidx.lifecycle.MutableLiveData
import com.gmail.bodziowaty6978.model.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object UserInformation {
    private var hasBeenCalled = false
    private var user = MutableLiveData<User>()

    fun user() = user

    @Suppress("UNCHECKED_CAST")
    suspend fun getUser(userId: String):Boolean {
        if (!hasBeenCalled) {
            val db = Firebase.firestore

            return try {
                user.postValue(db.collection("users").document(userId).get().await().toObject(User::class.java))
                true
            } catch (e: Exception) {
                false
            }
        }
        return false
    }
}

