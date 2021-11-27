package com.gmail.bodziowaty6978.model

data class Product(val author: String = "",
                   val name: String = "",
                   val brand: String = "",
                   val containerWeight: String = "",
                   val position: Int = -1,
                   val unit: String = "",
                   val calories: String = "",
                   val carbs: String = "",
                   val protein: String = "",
                   val fat: String = "")

data class JournalEntry(val name: String = "",
                        val id: String = "",
                        val mealName:String = "",
                        val date:String = "",
                        val brand: String = "",
                        val weight: String = "",
                        val unit: String = "",
                        val calories: String = "",
                        val carbs: String = "",
                        val protein: String = "",
                        val fat: String = "")


