package com.gmail.bodziowaty6978.functions

import java.math.RoundingMode

fun Double.round(decimals: Int = 2): Double = this.toBigDecimal().setScale(decimals, RoundingMode.HALF_EVEN).toDouble()