package com.gmail.bodziowaty6978.model

data class Product(val author: String = "",
                   val name: String = "",
                   val brand: String = "",
                   val containerWeight: Double = 0.0,
                   val position: Int = 0,
                   val unit: String = "",
                   val calories: Int = 0,
                   val carbs: Double = 0.0,
                   val protein: Double = 0.0,
                   val fat: Double = 0.0)

data class JournalEntry(val name: String = "",
                        val id: String = "",
                        val mealName:String = "",
                        val date:String = "",
                        val brand: String = "",
                        val weight: Double = 0.0,
                        val unit: String = "",
                        val calories: Int = 0,
                        val carbs: Double = 0.0,
                        val protein: Double = 0.0,
                        val fat: Double = 0.0)


