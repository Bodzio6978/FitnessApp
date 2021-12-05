package com.gmail.bodziowaty6978.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.gmail.bodziowaty6978.R

class EntryDialogFragment:DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)

            builder.setView(layoutInflater.inflate(R.layout.entry_dialog_layout,null))
            builder.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }




}