package com.gmail.bodziowaty6978

import android.app.Application
import com.gmail.bodziowaty6978.functions.getCurrentDateTime
import com.gmail.bodziowaty6978.singleton.CurrentDate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FitnessApplication:Application() {
    companion object {
        lateinit var instance: FitnessApplication private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CurrentDate.date().value = getCurrentDateTime()
    }
}