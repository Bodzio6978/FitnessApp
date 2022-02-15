package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieEntry
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.functions.toShortString
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.repository.ProductRepository
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider
) : ViewModel() {

    val productsState = MutableLiveData<Resource<Product>>()
    private val repository = ProductRepository()
    val addingState = MutableLiveData<DataState>()

    fun addProduct(product: Product, id: String, weight: String, mealName: String) {
        addingState.value = DataState.Loading
        viewModelScope.launch {
            withContext(dispatchers.default) {
                val weightValue = weight.replace(",", ".").toDouble()

                val calories = (product.calories.toDouble() * weightValue / 100.0).toInt()
                val carbohydrates = (product.carbs * weightValue / 100.0).round(2)
                val protein = (product.protein * weightValue / 100.0).round(2)
                val fat = (product.fat * weightValue / 100.0).round(2)

                val entry = JournalEntry(
                    product.name!!,
                    id,
                    mealName,
                    CurrentDate.date().value!!.toShortString(),
                    CurrentDate.date().value!!.timeInMillis,
                    product.brand!!,
                    weightValue,
                    product.unit!!,
                    calories,
                    carbohydrates,
                    protein,
                    fat
                )
                withContext(dispatchers.io) {
                    addingState.postValue(repository.addEntry(entry))
                }
            }

        }
    }

    fun getProduct(id: String) {
        viewModelScope.launch {
            productsState.postValue(repository.getProduct(id))
        }
    }

    fun getData(weight: Double): MutableList<PieEntry> {
        val product = productsState.value?.data

        val ratio = weight / 100.0

        if (product != null) {
            val sum = (product.carbs + product.protein + product.fat) * ratio

            val carbohydratesValue = (((product.carbs / sum * 100.0) * ratio).round(2)).toFloat()
            val proteinValue = (((product.protein / sum * 100.0) * ratio).round(2)).toFloat()
            val fatValue = (((product.fat / sum * 100.0) * ratio).round(0)).toFloat()

            return mutableListOf(
                PieEntry(carbohydratesValue, "Carbohydrates"),
                PieEntry(1F,""),
                PieEntry(proteinValue, "Protein"),
                PieEntry(1F,""),
                PieEntry(fatValue, "Fat"),
                PieEntry(1F,""),
                PieEntry(product.calories*ratio.toFloat(),"calories")
            )
        }

        return emptyList<PieEntry>().toMutableList()
    }
}