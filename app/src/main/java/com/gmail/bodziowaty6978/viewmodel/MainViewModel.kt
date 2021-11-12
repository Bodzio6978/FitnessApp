package com.gmail.bodziowaty6978.viewmodel

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.view.mainfragments.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
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
    private val database = Firebase.database("https://fitness-app-fa608-default-rtdb.europe-west1.firebasedatabase.app/")
    private val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private val mUserState = MutableLiveData<UserState>()

    fun getCalories():Fragment = calories
    fun getTraining():Fragment = training
    fun getRecipes():Fragment = recipes
    fun getShopping():Fragment = shopping
    fun getSettings():Fragment = settings

    fun getUserState():MutableLiveData<UserState> = mUserState

    fun checkInformation() {
        if (instance.currentUser==null){
            mUserState.value = UserState(UserState.USER_NOT_LOGGED)
        }

        val usernameRef = database.reference.child("users").child(userId).child("username")
        usernameRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value==null){
                    mUserState.value = UserState(UserState.USER_NO_USERNAME)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel",error.message)
            }
        })

        val informationRef = database.reference.child("users").child(userId).child("information").child("age")
        informationRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null){
                    mUserState.value = UserState(UserState.USER_NO_INFORMATION)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainViewModel",error.message)
            }
        })
    }
}

class UserState(val value: Int) {

    companion object {
        const val USER_NOT_LOGGED = 0
        const val USER_NO_USERNAME = 1
        const val USER_NO_INFORMATION = 2
    }
}