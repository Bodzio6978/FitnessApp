package com.gmail.bodziowaty6978.singleton

import androidx.lifecycle.MutableLiveData

object NotificationText{
    val text : MutableLiveData<String> = MutableLiveData()
    val state: MutableLiveData<Boolean> = MutableLiveData()

}