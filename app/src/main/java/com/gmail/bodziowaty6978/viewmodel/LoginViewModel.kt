package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth


class LoginViewModel : ViewModel() {

    private val instance = FirebaseAuth.getInstance()
    private val mAction: MutableLiveData<Action> = MutableLiveData()
    private val notificationText:MutableLiveData<String> = MutableLiveData()



    fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            notificationText.value = "please"
        }else{
            notificationText.value = "Successful"
        }
    }

    fun getAction():MutableLiveData<Action> = mAction
    fun getNotificationText():MutableLiveData<String> = notificationText
}

class Action(val value: Int) {

    companion object {
        const val SHOW_ = 0
        const val SHOW_INVALID_PASSWARD_OR_LOGIN = 1
    }
}



