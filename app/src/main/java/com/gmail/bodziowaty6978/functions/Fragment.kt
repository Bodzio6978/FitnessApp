package com.gmail.bodziowaty6978.functions

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun Fragment.showSnackbar(view: View, text:String){
    Snackbar.make(view,text, Snackbar.LENGTH_LONG).show()}