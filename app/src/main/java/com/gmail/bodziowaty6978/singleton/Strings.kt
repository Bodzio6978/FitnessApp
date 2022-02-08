package com.gmail.bodziowaty6978.singleton

import androidx.annotation.StringRes
import com.gmail.bodziowaty6978.FitnessApplication

object Strings {
    fun get(@StringRes stringRes: Int, vararg formatArgs: Any = emptyArray()): String {
        return FitnessApplication.instance.getString(stringRes, *formatArgs)
    }
}


