package com.gmail.bodziowaty6978.singleton

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object UserInformation {
    val mInformationState = MutableLiveData<InformationState>()

    val mUser = MutableLiveData(User())

    val currentCalories = MutableLiveData<Int>()

    fun getUserId() {
        if (mUser.value?.userId == null) {

            val instance = FirebaseAuth.getInstance()

            if (instance.currentUser == null) {
                mInformationState.value = InformationState(InformationState.USER_NOT_LOGGED)
            } else {
                mInformationState.value = InformationState(InformationState.USER_LOGGED)
                mUser.value?.userId = instance.currentUser!!.uid
            }

        } else {
            mInformationState.value = InformationState(InformationState.USER_LOGGED)
        }

    }

    @Suppress("UNCHECKED_CAST")
    fun getValues() {
        if (mUser.value?.nutritionValues == null || mUser.value?.userInformation==null || mUser.value?.username==null) {
            val db = Firebase.firestore

            db.collection("users").document(mUser.value?.userId!!).get().addOnSuccessListener {
                if (it.exists()) {
                    mUser.value?.nutritionValues = it.data?.get("nutritionValues") as Map<String, Double>
                    mUser.value?.userInformation = it.data?.get("userInformation") as Map<String, String>
                    mUser.value?.username = it.data?.get("username") as String
                    mInformationState.value = InformationState(InformationState.USER_INFORMATION_REQUIRED)
                } else {
                    mInformationState.value = InformationState(InformationState.USER_NO_INFORMATION)
                }
            }
        }else{
            Log.e(TAG,"User has all information")
            mInformationState.value = InformationState(InformationState.USER_INFORMATION_REQUIRED)
        }
    }

    fun checkUser(){
        if (mUser.value?.username==null){
            mInformationState.value = InformationState(InformationState.USER_NO_USERNAME)
        }else if(mUser.value?.userInformation==null||mUser.value?.nutritionValues==null){
            mInformationState.value = InformationState(InformationState.USER_NO_INFORMATION)
        }else{
            mInformationState.value = InformationState(InformationState.USER_HAS_EVERYTHING)
        }
    }

}

class InformationState(val value: Int) {

    companion object {
        const val USER_NOT_LOGGED = 0
        const val USER_LOGGED = 1
        const val USER_NO_USERNAME = 2
        const val USER_INFORMATION_REQUIRED = 3
        const val USER_NO_INFORMATION = 4
        const val USER_HAS_EVERYTHING = 5
    }
}