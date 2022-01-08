package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.singleton.UserInformation
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    private val mHasBeenSet = MutableLiveData<Boolean>()

    fun setDialogPermission(areEnabled: Boolean) {
        val db = Firebase.firestore

        db.collection("users").document(UserInformation.getUser().value?.userId!!).set(
            mapOf("areWeightDialogsEnabled" to areEnabled),
            SetOptions.merge()
        ).addOnSuccessListener {
            UserInformation.getUser().value!!.areWeightDialogsEnabled = areEnabled
            mHasBeenSet.value = true
        }.addOnFailureListener {
            mHasBeenSet.value = false
        }
    }

    fun getHasBeenSet(): MutableLiveData<Boolean> = mHasBeenSet

}
