package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.google.firebase.auth.FirebaseAuth


class LoginViewModel : ViewModel() {

    private val instance = FirebaseAuth.getInstance()
    private val isUserLoggedIn : MutableLiveData<Boolean> = MutableLiveData(false)

    fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            NotificationText.text.value = "fields"
            NotificationText.state.value = true
        }else{
            instance.signInWithEmailAndPassword(email,password).addOnFailureListener {
                NotificationText.text.value = it.message.toString()
                NotificationText.state.value = true
            }.addOnSuccessListener {
                isUserLoggedIn.value = true
            }
        }
    }

    fun getState():MutableLiveData<Boolean> = isUserLoggedIn
}




