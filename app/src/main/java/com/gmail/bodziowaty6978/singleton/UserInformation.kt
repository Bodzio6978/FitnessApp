package com.gmail.bodziowaty6978.singleton

import androidx.lifecycle.MutableLiveData
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.model.User
import com.gmail.bodziowaty6978.repository.UserData
import com.gmail.bodziowaty6978.state.Resource

object UserInformation {
    private var hasBeenCalled = false
    private var userData = UserData()
    val user = MutableLiveData<User>()

    @Suppress("UNCHECKED_CAST")
    suspend fun fetchUser(): Resource<User> {
        if (!hasBeenCalled) {
            return try {
                val user = userData.getUserInformation().data
                if (user != null) {
                    this.user.postValue(user)
                }
                Resource.Success(user)
            } catch (e: Exception) {
                Resource.Error(Strings.get(R.string.error))
            }
        }
        return Resource.Error(Strings.get(R.string.error))
    }
}

