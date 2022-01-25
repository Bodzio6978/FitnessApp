package com.gmail.bodziowaty6978.singleton

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.state.UserInformationState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object UserInformation {
    private val mUsername = MutableLiveData<String>()
    val mNutritionValues = MutableLiveData<Map<String, Double>>()
    val mUserInformation = MutableLiveData<Map<String, String>>()

    val mAreWeightDialogsEnabled = MutableLiveData<Boolean>()
    val mUserInformationState = MutableLiveData<UserInformationState>()

    private var hasBeenCalled = false

    @Suppress("UNCHECKED_CAST")
    fun getValues(userId: String) {
        if (!hasBeenCalled){
            val db = Firebase.firestore

            db.collection("users").document(userId).addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, error.message.toString())
                    return@addSnapshotListener
                }

                hasBeenCalled = true

                if (snapshot != null) {
                    if (snapshot.exists()) {
                        mUsername.value = snapshot.get("username") as String?
                        mNutritionValues.value = snapshot.get("nutritionValues") as Map<String, Double>?
                        mUserInformation.value = snapshot.get("userInformation") as Map<String, String>?
                        mAreWeightDialogsEnabled.value = snapshot.get("areWeightDialogsEnabled") as Boolean?
                        mUserInformationState.value = UserInformationState(UserInformationState.USER_INFORMATION_REQUIRED)
                    }else{
                        mUserInformationState.value = UserInformationState(UserInformationState.USER_NO_INFORMATION)
                    }
                } else {
                    mUserInformationState.value = UserInformationState(UserInformationState.USER_NO_INFORMATION)
                }
            }
        }else{
            mUserInformationState.value = UserInformationState(UserInformationState.USER_INFORMATION_REQUIRED)
        }
    }

    fun checkUsername(){
        val username = mUsername.value

        if (username==null){
            mUserInformationState.value = UserInformationState(UserInformationState.USER_NO_USERNAME)
        }else{
            mUserInformationState.value = UserInformationState(UserInformationState.USER_HAS_USERNAME)
        }
    }

    fun checkInformation(){
        val nutritionValues = mNutritionValues.value
        val userInformation = mUserInformation.value

        if (nutritionValues==null||userInformation==null){
            mUserInformationState.value = UserInformationState(UserInformationState.USER_NO_INFORMATION)
        }else{
            mUserInformationState.value = UserInformationState(UserInformationState.USER_HAS_INFORMATION)
        }
    }

}

