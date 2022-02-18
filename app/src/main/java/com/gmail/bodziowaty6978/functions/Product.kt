package com.gmail.bodziowaty6978.functions

import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate

fun Product.toJournalEntry(weight: Double, id:String, mealName: String):JournalEntry{
    val calories = (this.calories.toDouble() * weight / 100.0).toInt()
    val carbohydrates = (this.carbs * weight / 100.0).round(2)
    val protein = (this.protein * weight / 100.0).round(2)
    val fat = (this.fat * weight / 100.0).round(2)

    return JournalEntry(
        this.name!!,
        id,
        mealName,
        CurrentDate.date().value!!.toShortString(),
        CurrentDate.date().value!!.timeInMillis,
        this.brand!!,
        weight,
        this.unit!!,
        calories,
        carbohydrates,
        protein,
        fat
    )
}