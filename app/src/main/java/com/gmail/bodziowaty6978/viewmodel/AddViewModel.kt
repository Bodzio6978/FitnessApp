package com.gmail.bodziowaty6978.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.bodziowaty6978.interfaces.DispatcherProvider
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.repository.AddRepository
import com.gmail.bodziowaty6978.state.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val dispatchers: DispatcherProvider,
    private val repository: AddRepository
)  : ViewModel() {
    val barcodeState = MutableLiveData<Resource<Pair<String,Product>>>()
    val searchResultState = MutableLiveData<Resource<Map<String,Product>>>()

    val clickedProduct = MutableLiveData<Resource<Pair<String,Product>>>()

    val historyState = MutableStateFlow<Resource<List<JournalEntry>>>(Resource.Loading())

    val mButtonPressed = MutableLiveData<Int>()
    val mMealName = MutableLiveData<String>()
    val scannedBarcode = MutableLiveData<String>()

    fun initializeHistory() {
        viewModelScope.launch {
            val history = repository.getHistory()
            historyState.emit(history)
        }
    }

    fun searchProduct(product:Product){
        viewModelScope.launch {
            withContext(dispatchers.default){
                try {
                    val searchResult = searchResultState.value!!.data
                    searchResult?.keys?.forEach {
                        if (searchResult[it] == product){
                            clickedProduct.postValue(Resource.Success(Pair(it,product)))
                        }
                    }
                }catch (e:Exception){
                    clickedProduct.postValue(Resource.Error("Error occurred after clicking product from the list"))
                }
            }
        }

    }

    fun search(text: String) {
        viewModelScope.launch {
            withContext(dispatchers.io){
                val result = repository.search(text)
                if (result is Resource.Success){
                    val finalResult = Resource.Success(result.data!!.map {
                        it.id to it.toObject(Product::class.java)
                    }.toMap())
                    searchResultState.postValue(finalResult)
                }else{
                    searchResultState.postValue(Resource.Error("Error occurred when searching product"))
                }
            }
        }
    }

    fun convertEntries(list: List<JournalEntry>){
        viewModelScope.launch {
            withContext(dispatchers.default){
                val products = mutableMapOf<String,Product>()
                val idList = mutableListOf<String>()

                list.forEach{
                    if (!idList.contains(it.id)){
                        products[it.id] = Product(
                            it.id,
                            it.name,
                            it.brand,
                            it.weight,
                            0,
                            it.unit,
                            it.calories,
                            it.carbs,
                            it.protein,
                            it.fat,
                            "fakeProduct"
                        )
                    }
                    idList.add(it.id)
                }

                searchResultState.postValue(Resource.Success(products))
            }
        }
    }

    fun checkIfBarcodeExists(barcode: String) {
        viewModelScope.launch {
            val result = repository.checkBarcode(barcode)
            if (result is Resource.Success){
                val data = result.data!!
                if (!data.isEmpty){
                    data.forEach {
                        barcodeState.postValue(Resource.Success(Pair(it.id,it.toObject(Product::class.java))))
                        return@forEach
                    }
                }else{
                    barcodeState.postValue(Resource.Error("There is no product with this barcode"))
                }
            }else{
                barcodeState.postValue(Resource.Error("Error occurred when trying to scan barcode"))
            }
        }
    }
}