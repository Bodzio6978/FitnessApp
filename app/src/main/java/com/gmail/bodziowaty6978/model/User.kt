package com.gmail.bodziowaty6978.model

data class User(
    val username:String? = null,
    val nutritionValues:Map<String, Double>? = null,
    val userInformation:Map<String, String>? = null,
    val areWeightDialogsEnabled: Boolean? = null
)
