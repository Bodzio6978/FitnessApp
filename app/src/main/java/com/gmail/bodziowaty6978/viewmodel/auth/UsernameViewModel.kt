package com.gmail.bodziowaty6978.viewmodel.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.singleton.Strings
import com.gmail.bodziowaty6978.functions.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UsernameViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private val isUsernameSet: MutableLiveData<Boolean> = MutableLiveData(false)

    private val snackbarText = MutableLiveData<String>()

    fun addUsername(username: String) {

        db.collection("users").whereEqualTo("username", username).get().addOnSuccessListener {
            if (it.isEmpty) {

                db.collection("users").document(userId).set(mapOf("username" to username), SetOptions.merge()).addOnSuccessListener {
                    isUsernameSet.value = true
                }

            } else {
                snackbarText.value = Strings.get(R.string.username_exists_notification)
            }
        }.addOnFailureListener {
            Log.d(TAG, it.message.toString())
        }

    }

    fun getState(): MutableLiveData<Boolean> = isUsernameSet
    fun getSnackbarText():MutableLiveData<String> = snackbarText


}