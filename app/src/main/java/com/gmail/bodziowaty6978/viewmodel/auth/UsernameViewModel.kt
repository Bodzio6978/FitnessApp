package com.gmail.bodziowaty6978.viewmodel.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.gmail.bodziowaty6978.singleton.Strings
import com.gmail.bodziowaty6978.viewmodel.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UsernameViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val isUsernameSet: MutableLiveData<Boolean> = MutableLiveData(false)

    fun addUsername(username: String) {
        if (username.isEmpty()) {
            NotificationText.setText(Strings.get(R.string.please_enter_your_username))
            NotificationText.startAnimation()
        } else if (username.length < 6 || username.length > 24) {
            NotificationText.setText(Strings.get(R.string.username_length_notification))
            NotificationText.startAnimation()
        } else {
            checkIfUserExists(username)
        }

    }

    private fun checkIfUserExists(username: String) {

        db.collection("users").whereEqualTo("username", username).get().addOnSuccessListener {
            if (it.isEmpty) {

                db.collection("users").document(userId).set(mapOf("username" to username), SetOptions.merge()).addOnSuccessListener {
                    isUsernameSet.value = true
                }

            } else {

                NotificationText.setText(Strings.get(R.string.username_exists_notification))
                NotificationText.startAnimation()
            }
        }.addOnFailureListener {
            Log.d(TAG, it.message.toString())
        }

    }

    fun getState(): MutableLiveData<Boolean> = isUsernameSet


}