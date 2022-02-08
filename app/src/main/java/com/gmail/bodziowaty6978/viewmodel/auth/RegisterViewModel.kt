package com.gmail.bodziowaty6978.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.repository.AuthRepository
import com.gmail.bodziowaty6978.singleton.Strings
import com.gmail.bodziowaty6978.state.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel() : ViewModel() {

    private val userRepository = AuthRepository()
    val dataState = MutableStateFlow<DataState>(DataState.Started)


    fun registerUser(username: String, email: String, password: String, confirm: String) {
        viewModelScope.launch {
            dataState.emit(DataState.Loading)
            withContext(Dispatchers.Default) {
                if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || username.isEmpty()) {
                    dataState.emit(DataState.Error(Strings.get(R.string.please_make_sure_all_fields_are_filled_in_correctly)))
                } else if (password != confirm) {
                    dataState.emit(DataState.Error(Strings.get(R.string.please_make_sure_both_passwords_are_the_same)))
                } else if (password.length < 6 || password.length > 24) {
                    dataState.emit(DataState.Error(Strings.get(R.string.please_make_sure_your_password_is_at_least_6_characters_and_is_not_longer_than_24_characters)))
                } else {
                    withContext(Dispatchers.IO) {

                        val result = userRepository.registerUser(email, password)

                        if (result is DataState.Error) {
                            dataState.emit(result)
                        } else {
                            userRepository.logInUser(email, password)
                            val userId = userRepository.getUserId()
                            val finalResult = userRepository.setUsername(username, userId)
                            dataState.emit(finalResult)

                        }


                    }
                }
            }
        }


    }
}