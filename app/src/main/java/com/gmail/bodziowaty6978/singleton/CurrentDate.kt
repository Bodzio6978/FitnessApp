package com.gmail.bodziowaty6978.singleton

import androidx.lifecycle.MutableLiveData
import java.util.*

object CurrentDate {
    private val date = MutableLiveData<Calendar>()

    fun date() = date

    fun addDay(){
        val calendar: Calendar = date.value!!
        calendar.run {
            add(Calendar.DAY_OF_MONTH,1)
            date.value = this
        }

    }

    fun deductDay(){
        val calendar: Calendar = date.value!!
        calendar.run {
            add(Calendar.DAY_OF_MONTH,-1)
            date.value = this
        }

    }
}