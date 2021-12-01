package com.gmail.bodziowaty6978.viewmodel

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.view.mainfragments.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class MainViewModel: ViewModel() {
    private val calories = CaloriesFragment()
    private val training = TrainingFragment()
    private val recipes = RecipesFragment()
    private val shopping = ShoppingFragment()
    private val settings = SettingFragment()

    private val instance = FirebaseAuth.getInstance()

    private val db = Firebase.firestore
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val mUserState = MutableLiveData<UserState>()

    private var errorMessage = ""

    fun getCalories():Fragment = calories
    fun getTraining():Fragment = training
    fun getRecipes():Fragment = recipes
    fun getShopping():Fragment = shopping
    fun getSettings():Fragment = settings

    fun getUserState():MutableLiveData<UserState> = mUserState

    fun getErrorMessage():String = errorMessage

    fun checkInformation() {

        if (instance.currentUser==null){
            mUserState.value = UserState(UserState.USER_NOT_LOGGED)
        }

        val userCollectionRef = db.collection("users").document(userId)

        userCollectionRef.get().addOnSuccessListener {
            if (it.getString("username")==null){
                mUserState.value = UserState(UserState.USER_NO_USERNAME)
            }else if(it.get("nutritionValues")==null){
                mUserState.value = UserState(UserState.USER_NO_INFORMATION)
            }else if(it.get("userInformation")==null){
                mUserState.value = UserState(UserState.USER_NO_INFORMATION)
            }
        }.addOnFailureListener {
            Log.e(TAG,it.message.toString())
        }

    }
}

class UserState(val value: Int) {

    companion object {
        const val USER_NOT_LOGGED = 0
        const val USER_NO_USERNAME = 1
        const val USER_NO_INFORMATION = 2
    }
}