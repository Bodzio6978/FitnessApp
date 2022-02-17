package com.gmail.bodziowaty6978.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val author: String? = "",
    val name: String? = "",
    val brand: String? = "",
    val containerWeight: Double = 0.0,
    val position: Int = 0,
    val unit: String? = "",
    val calories: Int = 0,
    val carbs: Double = 0.0,
    val protein: Double = 0.0,
    val fat: Double = 0.0,
    val barcode: String? = "",
    var prices: List<Price> = emptyList()
) : Parcelable




