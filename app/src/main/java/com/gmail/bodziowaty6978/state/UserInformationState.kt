package com.gmail.bodziowaty6978.state

sealed class UserInformationState() {
    object GettingInformation : UserInformationState()
    object HasInformation : UserInformationState()
    object NoInformation : UserInformationState()
}