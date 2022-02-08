package com.gmail.bodziowaty6978.state

sealed class DataState {
    object Started : DataState()
    object Loading : DataState()
    object Success : DataState()
    data class Error(val errorMessage: String) : DataState()
}