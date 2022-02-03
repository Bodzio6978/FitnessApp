package com.gmail.bodziowaty6978.viewmodel.auth

import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.other.BaseViewModel
import com.gmail.bodziowaty6978.repository.AuthRepository
import com.gmail.bodziowaty6978.singleton.Strings
import com.gmail.bodziowaty6978.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterViewModel : BaseViewModel<UiState>() {

    private val userRepository = AuthRepository()


    fun registerUser(username: String, email: String, password: String, confirm: String) {
        uiState.value = UiState.Loading
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (email.isEmpty() || password.isEmpty() || confirm.isEmpty() || username.isEmpty()) {
                    uiState.postValue(UiState.Error(Strings.get(R.string.please_make_sure_all_fields_are_filled_in_correctly)))
                } else if (password != confirm) {
                    uiState.postValue(UiState.Error(Strings.get(R.string.please_make_sure_both_passwords_are_the_same)))
                } else if (password.length < 6 || password.length > 24) {
                    uiState.postValue(UiState.Error(Strings.get(R.string.please_make_sure_your_password_is_at_least_6_characters_and_is_not_longer_than_24_characters)))
                } else {
                    withContext(Dispatchers.IO) {

                        val result = userRepository.registerUser(email,password)

                        if (result==null){
                            uiState.postValue(UiState.Error(Strings.get(R.string.error)))
                        }else{
                            val userId = result.user?.uid

                            if (userId != null) {
                                val finalResult = userRepository.setUsername(username,userId)
                                uiState.postValue(finalResult)
                            }else{
                                uiState.postValue(UiState.Error(Strings.get(R.string.error)))
                            }
                        }


                    }
                }
            }
        }


    }
}