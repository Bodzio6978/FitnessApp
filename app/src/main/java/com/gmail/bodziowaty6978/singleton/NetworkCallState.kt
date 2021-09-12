package com.gmail.bodziowaty6978.singleton

import androidx.lifecycle.MutableLiveData

object NetworkCallState {
    val status:MutableLiveData<Int> = MutableLiveData()
    val NETWORK_ERROR = 1
}