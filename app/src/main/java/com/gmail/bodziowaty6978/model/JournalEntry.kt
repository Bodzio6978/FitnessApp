package com.gmail.bodziowaty6978.model

import com.gmail.bodziowaty6978.functions.toString
import java.util.*

data class JournalEntry(
    val name: String = "",
    val id: String = "",
    val mealName: String = "",
    val date: String = Calendar.getInstance().time.toString("yyyy-MM-dd"),
    val time:Long = Calendar.getInstance().timeInMillis,
    val brand: String = "",
    val weight: Double = 0.0,
    val unit: String = "",
    val calories: Int = 0,
    val carbs: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0)