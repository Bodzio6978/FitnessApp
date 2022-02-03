package com.gmail.bodziowaty6978.state

sealed class UiState {
    object Loading : UiState()
    object Success : UiState()
    data class Error(val errorMessage: String) : UiState()
    object NoInformation : UiState()

}