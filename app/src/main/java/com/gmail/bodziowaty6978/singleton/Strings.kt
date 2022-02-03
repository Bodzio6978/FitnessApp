package com.gmail.bodziowaty6978.singleton

import android.app.Application
import androidx.annotation.StringRes
import com.gmail.bodziowaty6978.functions.getCurrentDateTime

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return App.instance.getString(stringRes, *formatArgs)
    }
}

class App : Application() {
    companion object {
        lateinit var instance: App private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CurrentDate.date().value = getCurrentDateTime()
    }
}

