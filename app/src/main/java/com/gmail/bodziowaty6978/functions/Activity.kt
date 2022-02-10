package com.gmail.bodziowaty6978.functions

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

fun AppCompatActivity.onError(view:View,text:String){Snackbar.make(view,text,Snackbar.LENGTH_LONG).show()}