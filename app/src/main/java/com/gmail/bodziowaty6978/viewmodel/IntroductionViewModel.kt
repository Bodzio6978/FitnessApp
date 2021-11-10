package com.gmail.bodziowaty6978.viewmodel

import android.util.Log
import androidx.collection.ArrayMap
import androidx.collection.SimpleArrayMap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.view.introduction.FirstFragment
import com.gmail.bodziowaty6978.view.introduction.SecondFragment

class IntroductionViewModel:ViewModel() {

    private val fragment1 = FirstFragment()
    private val fragment2 = SecondFragment()

    private val userInformation = ArrayMap<String,String>()

    fun getFragments():ArrayList<Fragment>{
        val list = ArrayList<Fragment>()
        list.add(fragment1)
        list.add(fragment2)
        return list
    }

    fun addInformation(data: SimpleArrayMap<String, String>){
        userInformation.putAll(data)
        for (value in userInformation){
            Log.e("huj",value.toString())
        }
    }
}