package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.functions.toShortString
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.model.MeasurementEntity
import com.gmail.bodziowaty6978.repository.MeasurementRepository
import com.gmail.bodziowaty6978.state.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MeasurementViewModel @Inject constructor(
    val repository: MeasurementRepository,
    val dispatchers: DispatcherProvider
) : ViewModel() {

    val addingState = MutableLiveData<DataState>()
    var lastMeasurement: MeasurementEntity? = null

    fun checkMeasurementEntry(
        hips: String,
        waist: String,
        thigh: String,
        bust: String,
        biceps: String,
        calf: String
    ) {
        addingState.value = DataState.Loading
        viewModelScope.launch(dispatchers.default) {
            var hipsValue = hips.trim().replace(",", ".")
            var waistValue = waist.trim().replace(",", ".")
            var thighValue = thigh.trim().replace(",", ".")
            var bustValue = bust.trim().replace(",", ".")
            var bicepsValue = biceps.trim().replace(",", ".")
            var calfValue = calf.trim().replace(",", ".")

            val allFieldsNotBlank = checkIfFieldsAreBlank(
                hipsValue,
                waistValue,
                thighValue,
                bustValue,
                bicepsValue,
                calfValue
            )

            if (lastMeasurement != null) {
                hipsValue =
                    if (hipsValue.isBlank()) lastMeasurement!!.hips.toString() else hipsValue
                waistValue =
                    if (waistValue.isBlank()) lastMeasurement!!.hips.toString() else waistValue
                thighValue =
                    if (thighValue.isBlank()) lastMeasurement!!.hips.toString() else thighValue
                bustValue =
                    if (bustValue.isBlank()) lastMeasurement!!.hips.toString() else bustValue
                bicepsValue =
                    if (bicepsValue.isBlank()) lastMeasurement!!.hips.toString() else bicepsValue
                calfValue =
                    if (calfValue.isBlank()) lastMeasurement!!.hips.toString() else calfValue

                addMeasurementEntry(
                    hipsValue,
                    waistValue,
                    thighValue,
                    bustValue,
                    bicepsValue,
                    calfValue
                )

            } else {
                if (allFieldsNotBlank) {
                    addMeasurementEntry(
                        hipsValue,
                        waistValue,
                        thighValue,
                        bustValue,
                        bicepsValue,
                        calfValue
                    )
                } else {
                    addingState.postValue(DataState.Error("If you are entering your measurements for the first time, please fill every field"))
                }

            }
        }
    }

    private suspend fun addMeasurementEntry(
        hips: String,
        waist: String,
        thigh: String,
        bust: String,
        biceps: String,
        calf: String
    ) {
        val hipsValue = hips.toDouble()
        val waistValue = waist.toDouble()
        val thighValue = thigh.toDouble()
        val bustValue = bust.toDouble()
        val bicepsValue = biceps.toDouble()
        val calfValue = calf.toDouble()

        val calendar = Calendar.getInstance()

        val measurementEntity = MeasurementEntity(
            0,
            calendar.timeInMillis,
            calendar.toShortString(),
            hipsValue,
            waistValue,
            thighValue,
            bustValue,
            bicepsValue,
            calfValue,
        )

        withContext(dispatchers.io) {
            val result = repository.addMeasurementEntity(measurementEntity)
            if (result is DataState.Success) addingState.postValue(DataState.Success) else if (result is DataState.Error) addingState.postValue(
                DataState.Error(result.errorMessage)
            )
        }
    }

    private fun checkIfFieldsAreBlank(
        hips: String,
        waist: String,
        thigh: String,
        bust: String,
        biceps: String,
        calf: String,
    ): Boolean {
        return hips.isNotBlank() && waist.isNotBlank() && thigh.isNotBlank() && bust.isNotBlank() && biceps.isNotBlank() && calf.isNotBlank()
    }

}