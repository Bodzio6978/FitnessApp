package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.google.firebase.auth.FirebaseAuth

class RegisterViewModel: ViewModel() {
    private val instance = FirebaseAuth.getInstance()
    private val isUserRegistered : MutableLiveData<Boolean> = MutableLiveData(false)

    fun registerUser(email:String, password:String, confirm:String){
        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            NotificationText.text.value = "fields"
        }else if(password != confirm){
            NotificationText.text.value = "confirm"
        }else{
            instance.createUserWithEmailAndPassword(email,password).addOnFailureListener {
                NotificationText.text.value = it.message.toString()
            }.addOnSuccessListener {
                isUserRegistered.value = true
            }
        }
    }
    fun getState():MutableLiveData<Boolean> = isUserRegistered
}