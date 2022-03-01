package com.gmail.bodziowaty6978.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.repository.AuthRepository
import com.gmail.bodziowaty6978.singleton.Strings
import com.gmail.bodziowaty6978.state.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val dispatchers:DispatcherProvider
): ViewModel() {
    val dataState = MutableStateFlow<DataState>(DataState.Started)


    fun registerUser(username: String, email: String, password: String, confirm: String) {
        viewModelScope.launch {
            dataState.emit(DataState.Loading)
            withContext(dispatchers.default) {
                if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || username.isEmpty()) {
                    dataState.emit(DataState.Error(Strings.get(R.string.please_make_sure_all_fields_are_filled_in_correctly)))
                } else if (password != confirm) {
                    dataState.emit(DataState.Error(Strings.get(R.string.please_make_sure_both_passwords_are_the_same)))
                } else if (password.length < 6 || password.length > 24) {
                    dataState.emit(DataState.Error(Strings.get(R.string.please_make_sure_your_password_is_at_least_6_characters_and_is_not_longer_than_24_characters)))
                } else {
                    withContext(dispatchers.io) {

                        val result = repository.registerUser(email, password)

                        if (result is DataState.Error) {
                            dataState.emit(result)
                        } else {
                            repository.logInUser(email, password)
                            val userId = repository.getUserId()
                            val finalResult = repository.setUsername(username, userId)
                            dataState.emit(finalResult)

                        }


                    }
                }
            }
        }


    }
}