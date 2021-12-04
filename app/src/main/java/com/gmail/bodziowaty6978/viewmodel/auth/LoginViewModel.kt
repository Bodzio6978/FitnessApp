package com.gmail.bodziowaty6978.viewmodel.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


class LoginViewModel : ViewModel() {

    private val instance = FirebaseAuth.getInstance()
    private val isUserLoggedIn : MutableLiveData<Boolean> = MutableLiveData(false)

    private val snackbarText = MutableLiveData<String>()

    fun loginUser(email: String, password: String) {
            instance.signInWithEmailAndPassword(email,password).addOnFailureListener {
                snackbarText.value = it.message.toString()
            }.addOnSuccessListener {
                isUserLoggedIn.value = true
            }
    }

    fun getState():MutableLiveData<Boolean> = isUserLoggedIn
    fun getSnackbarText():MutableLiveData<String> = snackbarText
}




