package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.model.WeightEntity
import com.gmail.bodziowaty6978.repository.WeightChartRepository
import com.gmail.bodziowaty6978.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightChartViewModel @Inject constructor(
    private val repository:WeightChartRepository,
    private val dispatchers:DispatcherProvider):ViewModel() {

    val weightEntries = MutableLiveData<Resource<List<WeightEntity>>>()

    fun getWeightEntries(){
        viewModelScope.launch(dispatchers.io) {
            val entriesResult = repository.getAllWeightEntries()
            weightEntries.postValue(entriesResult)
        }
    }
}