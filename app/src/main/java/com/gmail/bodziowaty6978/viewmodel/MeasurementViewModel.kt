package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.functions.toShortString
import com.gmail.bodziowaty6978.model.MeasurementEntity
import com.gmail.bodziowaty6978.other.StandardDispatchers
import com.gmail.bodziowaty6978.repository.MeasurementRepository
import com.gmail.bodziowaty6978.state.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MeasurementViewModel @Inject constructor(val repository: MeasurementRepository,
val dispatchers:StandardDispatchers):ViewModel() {

    val addingState = MutableLiveData<DataState>()
    var lastMeasurement:MeasurementEntity? = null

    fun addMeasurementEntry(hips:String,waist:String,thigh:String,bust:String,biceps:String,calf:String){
        viewModelScope.launch(dispatchers.default) {
            val calendar = Calendar.getInstance()

            if (hips.isNotBlank()&&waist.isNotBlank()&&thigh.isNotBlank()&&bust.isNotBlank()&&biceps.isNotBlank()&&calf.isNotBlank()&&lastMeasurement!=null){
                val hipsValue = hips.trim().replace(",",".").toDouble()
                val waistValue = hips.trim().replace(",",".").toDouble()
                val thighValue = hips.trim().replace(",",".").toDouble()
                val bustValue = hips.trim().replace(",",".").toDouble()
                val bicepsValue = hips.trim().replace(",",".").toDouble()
                val calfValue = hips.trim().replace(",",".").toDouble()

                withContext(dispatchers.io){
                    repository.addMeasurementEntity(
                        MeasurementEntity(0,calendar.timeInMillis,calendar.toShortString(),hipsValue,waistValue,thighValue,bustValue,bicepsValue,calfValue)
                    )
                }
                
            }else{
                if (hips.isBlank()||waist.isBlank()||thigh.isBlank()||bust.isBlank()||biceps.isBlank()||calf.isBlank()){
                    addingState.postValue(DataState.Error("If you haven't entered any measurement before you have to fill every field"))
                }else{

                    repository.addMeasurementEntity()
                }
            }
        }
    }

}