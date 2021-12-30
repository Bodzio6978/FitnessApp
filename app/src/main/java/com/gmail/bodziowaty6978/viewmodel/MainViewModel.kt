package com.gmail.bodziowaty6978.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.gmail.bodziowaty6978.view.mainfragments.DiaryFragment
import com.gmail.bodziowaty6978.view.mainfragments.SummaryFragment
import com.gmail.bodziowaty6978.view.mainfragments.TrainingFragment

class MainViewModel : ViewModel() {
    private val summary = SummaryFragment()
    private val diary = DiaryFragment()
    private val training = TrainingFragment()

    fun getDiary(): Fragment = diary
    fun getTraining(): Fragment = training
    fun getSummary(): Fragment = summary


}
