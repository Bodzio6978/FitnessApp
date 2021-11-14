package com.gmail.bodziowaty6978.functions

import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.singleton.Strings
import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Calendar {
    return Calendar.getInstance()
}

fun getDateInAppFormat(date:Calendar):String{
    return if(date.time.toString("EEEE, dd-MM-yyyy")== getCurrentDateTime().time.toString("EEEE, dd-MM-yyyy")){
        Strings.get(R.string.today)
    }else{
        date.time.toString("EEEE, dd-MM-yyyy")
    }
}