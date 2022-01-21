package com.gmail.bodziowaty6978.state

class UserInformationState(val value: Int) {

    companion object {
        const val USER_HAS_USERNAME = 1
        const val USER_NO_USERNAME = 2
        const val USER_INFORMATION_REQUIRED = 3
        const val USER_NO_INFORMATION = 4
        const val USER_HAS_INFORMATION = 5
    }
}