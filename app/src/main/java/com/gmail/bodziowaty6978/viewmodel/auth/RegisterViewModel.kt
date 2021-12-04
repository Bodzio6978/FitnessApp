package com.gmail.bodziowaty6978.viewmodel.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel: ViewModel() {
    private val instance = FirebaseAuth.getInstance()
    private val isUserRegistered : MutableLiveData<Boolean> = MutableLiveData(false)

    private val snackbarText = MutableLiveData<String>()

    fun registerUser(email:String, password:String){

            instance.createUserWithEmailAndPassword(email,password).addOnFailureListener {
                snackbarText.value = it.message.toString()
            }.addOnSuccessListener {
                isUserRegistered.value = true
            }

    }
    fun getState():MutableLiveData<Boolean> = isUserRegistered
    fun getSnackbarText():MutableLiveData<String> = snackbarText
}