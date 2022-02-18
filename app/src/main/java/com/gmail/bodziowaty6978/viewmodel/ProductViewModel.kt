package com.gmail.bodziowaty6978.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieEntry
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.recalculateValues
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.functions.toJournalEntry
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Price
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.repository.ProductRepository
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
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
    val editingState = MutableLiveData<DataState>()

    val priceState = MutableLiveData<DataState>()

    fun addProduct(product: Product, id: String, weight: String, mealName: String) {
        addingState.value = DataState.Loading
        viewModelScope.launch {
            withContext(dispatchers.default) {
                if (weight.isBlank()) addingState.postValue(DataState.Error("Please enter entry weight"))
                else{
                    val weightValue = weight.replace(",", ".").trim().toDouble()
                    val journalEntry = product.toJournalEntry(weightValue,id,mealName)

                    withContext(dispatchers.io) {
                        addingState.postValue(repository.addEntry(journalEntry))
                    }
                }
            }
        }
    }

    fun editJournalEntry(entryId:String,entry:JournalEntry,weight: String){
        editingState.value = DataState.Loading
        viewModelScope.launch(dispatchers.default) {
            if (weight.isBlank()) editingState.value = DataState.Error("Please enter entry weight")
            else{
                val weightValue = weight.replace(",", ".").trim().toDouble()
                val newEntry = entry.recalculateValues(weightValue)
                withContext(dispatchers.io){
                    val result = repository.updateEntry(entryId,newEntry)
                    editingState.postValue(result)
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
                PieEntry(1F, ""),
                PieEntry(proteinValue, "Protein"),
                PieEntry(1F, ""),
                PieEntry(fatValue, "Fat"),
                PieEntry(1F, ""),
                PieEntry(product.calories * ratio.toFloat(), "calories")
            )
        }

        return emptyList<PieEntry>().toMutableList()
    }

    fun calculateNewPrices(value: String, forWhat: String, productId: String) {
        priceState.value = DataState.Loading

        viewModelScope.launch(dispatchers.default) {
            if (value.isBlank() || forWhat.isBlank()) priceState.postValue(DataState.Error("Please make sure all fields are filled in"))
            else {
                val doubleValue = value.replace(",", ".").toDouble()
                val doubleForWhat = forWhat.replace(",", ".").toDouble()

                val prices = calculatePrices(doubleValue, doubleForWhat)

                Log.e(TAG,prices.toString())
                withContext(dispatchers.io) {
                    val result = repository.addPrices(prices, productId)
                    if (result is DataState.Success) {
                        updateCurrentProduct(prices)
                    }
                    priceState.postValue(result)
                }
            }

            this.cancel()
        }
    }

    private suspend fun updateCurrentProduct(prices: List<Price>) {
        withContext(dispatchers.default) {
            val currentProduct = productsState.value?.data

            if (currentProduct != null) {
                currentProduct.prices = prices
                productsState.postValue(Resource.Success(currentProduct))
            }
        }
    }

    private fun calculatePrices(value: Double, forWhat: Double): List<Price> {
        val product = productsState.value?.data

        Log.e(TAG,value.toString())
        Log.e(TAG,forWhat.toString())

        if (product != null) {
            var for100G = (value / forWhat * 100.0).round(2)
            var for10Protein = (10.0 / product.protein * forWhat * for100G / 100.0).round(2)
            var for100Kcal = (100.0 / product.calories * forWhat * for100G / 100.0).round(2)

            Log.e(TAG,for100G.toString())

            for (price in product.prices) {
                when (price.forWhat) {
                    "100g of product" -> {
                        if (price.price != 0.0) {
                            for100G += price.price
                        }
                    }
                    "10g of protein" -> {
                        if (price.price != 0.0) {
                            for10Protein += price.price
                        }
                    }
                    "100 kcal" -> {
                        if (price.price != 0.0) {
                            for100Kcal += price.price
                        }
                    }
                }
            }

            return listOf(
                Price(for100G, "100g of product"),
                Price(for10Protein, "10g of protein"),
                Price(for100Kcal, "100 kcal")
            )
        }
        return emptyList()
    }
}
