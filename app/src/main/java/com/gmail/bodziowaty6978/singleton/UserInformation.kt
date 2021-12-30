package com.gmail.bodziowaty6978.singleton

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.gmail.bodziowaty6978.functions.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object UserInformation {
//    val isLogged = MutableLiveData<Boolean>()
//    val mValuesState = MutableLiveData<Boolean>()
//    val mUsernameState = MutableLiveData<Boolean>()

    val mInformationState = MutableLiveData<InformationState>()

    val mValues = MutableLiveData<Map<String, Double>>()
    var mUsername = MutableLiveData<String>()
    var userId: String? = null


    fun getUserId() {
        if (userId == null) {

            val instance = FirebaseAuth.getInstance()

            if (instance.currentUser == null) {
                mInformationState.value = InformationState(InformationState.USER_NOT_LOGGED)
            } else {
                mInformationState.value = InformationState(InformationState.USER_LOGGED)
                userId = instance.currentUser!!.uid
            }

        } else {
            mInformationState.value = InformationState(InformationState.USER_LOGGED)
        }

    }

    fun getValues() {
        if (mValues.value == null|| mUsername.value==null) {
            val db = Firebase.firestore

            Log.e(TAG,"Getting values")

            db.collection("users").document(userId!!).get().addOnSuccessListener {
                if (it.exists()) {

                    if (it.data?.get("nutritionValues") != null) {
                        setUpValues(it.data?.get("nutritionValues") as Map<*, *>)
                    } else {
                        mInformationState.value = InformationState(InformationState.USER_NO_INFORMATION)
                    }

                    if (it.data?.get("username") != null) {
                        mUsername.value = it.data?.get("username") as String
                    } else {
                        mInformationState.value = InformationState(InformationState.USER_NO_USERNAME)
                    }
                } else {
                    mInformationState.value = InformationState(InformationState.USER_NO_INFORMATION)
                }
            }
        }
    }
    @Suppress("UNCHECKED_CAST")
    private fun setUpValues(map: Map<*, *>) {
        val values = map as Map<String,Double>

        mValues.value = values

        Log.e(TAG,values.toString())
    }
}

class InformationState(val value: Int) {

    companion object {
        const val USER_NOT_LOGGED = 0
        const val USER_LOGGED = 1
        const val USER_NO_USERNAME = 2
        const val USER_NO_INFORMATION = 3
    }
}