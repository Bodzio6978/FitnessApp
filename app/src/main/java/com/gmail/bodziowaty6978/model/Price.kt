package com.gmail.bodziowaty6978.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Price(
    val price:Double = 0.0,
    val forWhat:String = ""
) : Parcelable
