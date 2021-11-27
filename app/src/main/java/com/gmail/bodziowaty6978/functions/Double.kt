package com.gmail.bodziowaty6978.functions

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()