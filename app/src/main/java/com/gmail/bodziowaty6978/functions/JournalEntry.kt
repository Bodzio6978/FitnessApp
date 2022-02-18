package com.gmail.bodziowaty6978.functions

import com.gmail.bodziowaty6978.model.JournalEntry

fun JournalEntry.recalculateValues(weight:Double):JournalEntry {
    val ogWeight = this.weight
    val calories100 = (this.calories/ogWeight*100.0)
    val carbohydrates100 = (this.carbs / weight * 100.0)
    val protein100 = (this.protein / weight * 100.0)
    val fat100 = (this.fat / weight * 100.0)

    val calories = (calories100 * weight / 100.0).toInt()
    val carbohydrates = (carbohydrates100 * weight / 100.0).round(2)
    val protein = (protein100 * weight / 100.0).round(2)
    val fat = (fat100 * weight / 100.0).round(2)

    return JournalEntry(
        this.name,
        this.id,
        this.mealName,
        this.date,
        this.time,
        this.brand,
        weight,
        this.unit,
        calories,
        carbohydrates,
        protein,
        fat
    )
}