package com.gmail.bodziowaty6978.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityProductBinding
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.functions.showSnackbar
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Price
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.state.DataState
import com.gmail.bodziowaty6978.state.Resource
import com.gmail.bodziowaty6978.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivityProductBinding
    val viewModel: ProductViewModel by viewModels()

    lateinit var productId:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeIfAddedProduct()
        observeProductState()

        initializeDate()

        observePriceState()

        binding.ibBackMeal.setOnClickListener {
            super.onBackPressed()
        }

        val mealName = intent.getStringExtra("mealName")
        binding.tvMealNameMeal.text = mealName

        productId = intent.getStringExtra("id")!!

        val entryId = intent.getStringExtra("entryId")

        if (entryId!=null){
            val entry = intent.getParcelableExtra<JournalEntry>("entry")!!

            binding.etWeightMeal.setText(entry.weight.toString())
            binding.btAddNew.text = "Edit"

            viewModel.getProduct(entry.id)

            observeEditState()

            binding.btAddNew.setOnClickListener {
                viewModel.editJournalEntry(entryId,
                    entry,
                binding.etWeightMeal.text.toString())
            }
        }else{
            binding.etWeightMeal.setText("100")
            val product = intent.getParcelableExtra<Product>("product")

            if (product == null) {
                viewModel.getProduct(productId)
            } else {
                viewModel.productsState.value = Resource.Success(product)
            }

            binding.btAddNew.setOnClickListener {
                val weight = binding.etWeightMeal.text.toString()
                if (product != null) {
                    viewModel.addProduct(
                        product,
                        productId,
                        weight,
                        mealName.toString()
                    )
                } else {
                    val currentProduct = viewModel.productsState.value?.data
                    if (currentProduct != null) {
                        viewModel.addProduct(
                            currentProduct,
                            productId,
                            weight,
                            mealName.toString()
                        )
                    }

                }
            }
        }
    }

    private fun observeProductState() {
        lifecycleScope.launch {
            viewModel.productsState.observe(this@ProductActivity, {
                when (it) {
                    is Resource.Success -> initializeUi(it.data!!)
                    else -> showSnackbar(binding.clProduct, it.uiText.toString())
                }
            })
        }
    }

    private fun observeEditState(){
        lifecycleScope.launch {
            viewModel.editingState.observe(this@ProductActivity,{
                when(it){
                    is DataState.Success -> startActivity(Intent(this@ProductActivity,MainActivity::class.java).putExtra("position",1))
                    is DataState.Error -> {
                        binding.rlProduct.visibility = View.VISIBLE
                        binding.pbProduct.visibility = View.GONE
                        showSnackbar(binding.clProduct,it.errorMessage)
                    }
                    else -> {
                        onLoading()
                    }
                }
            })
        }
    }

    private fun observePriceState(){
        lifecycleScope.launch {
            viewModel.priceState.observe(this@ProductActivity,{
                when(it){
                    is DataState.Loading -> {
                        binding.rlProgressPrice.visibility = View.VISIBLE
                        binding.rlAddPrice.visibility = View.GONE
                    }
                    is DataState.Error -> {
                        showSnackbar(binding.clProduct, it.errorMessage)
                        binding.rlProgressPrice.visibility = View.GONE
                        binding.rlAddPrice.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.rlProgressPrice.visibility = View.GONE
                        binding.rlAddPrice.visibility = View.VISIBLE
                        showSnackbar(binding.clProduct, "Prices have been successfully updated")
                    }
                }
            })
        }
    }


    private fun observeIfAddedProduct() {
        lifecycleScope.launch {
            viewModel.addingState.observe(this@ProductActivity, {
                when (it) {
                    is DataState.Success -> {
                        startActivity(
                            Intent(
                                this@ProductActivity,
                                MainActivity::class.java
                            ).putExtra("position", 1)
                        )
                        finish()
                    }

                    is DataState.Error -> {
                        binding.rlProduct.visibility = View.VISIBLE
                        binding.pbProduct.visibility = View.GONE
                        showSnackbar(binding.clProduct, it.errorMessage)
                    }
                    is DataState.Loading -> onLoading()
                }

            })
        }
    }

    private fun onLoading() {
        binding.rlProduct.visibility = View.GONE
        binding.pbProduct.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun initializeUi(product: Product) {
        lifecycleScope.launch {
            binding.etWeightMeal.addTextChangedListener {
                if (!it.isNullOrBlank()){
                    val weight =
                        binding.etWeightMeal.text.toString().replace(",", ".").trim().toDouble()

                    val data = viewModel.getData(weight)

                    refreshChartData(data)
                    refreshNutritionValues(product,weight)
                }
            }

            binding.etWeightMeal.setText(binding.etWeightMeal.text)
        }

        binding.tvProductNameMeal.text = product.name
        if (!product.brand.isNullOrBlank()) {
            binding.tvBrandMeal.text = product.brand
            binding.tvBrandMeal.visibility = View.VISIBLE
        }

        initializePrices(product.prices)

        binding.btSubmitPriceProduct.setOnClickListener {
            viewModel.calculateNewPrices(
                binding.etPriceValue.text.trim().toString(),
                binding.etPriceFor.text.trim().toString(),
                productId
            )
        }

        binding.btAddNew.visibility = View.VISIBLE

        binding.tvUnitMeal.text = product.unit
    }

    private fun initializePrices(prices:List<Price>){
        if (prices.isNotEmpty()){
            for (price in prices){
                when(price.forWhat){
                    "100g of product" -> {
                        "${price.price}zł".also { binding.tvAveragePriceValue.text = it }
                    }
                    "10g of protein" -> {
                        if (price.price!=0.0){
                            "${price.price}zł".also { binding.tvPriceProteinValue.text = it }
                        }
                    }
                    "100 kcal" -> {
                        if (price.price!=0.0){
                            "${price.price}zł".also { binding.tvPriceCaloriesValue.text = it }
                        }
                    }
                }
            }
        }
    }

    private fun refreshNutritionValues(product: Product,weight:Double){
        binding.tvCarbsValueMeal.text = (product.carbs*weight/100.0).round(2).toString()
        binding.tvProteinValueMeal.text = (product.protein*weight/100.0).round(2).toString()
        binding.tvFatValueMeal.text = (product.fat*weight/100.0).round(2).toString()
    }

    private fun refreshChartData(data: MutableList<PieEntry>) {
        val chart = binding.pcProduct

        val colors = getChartColors()

        val calories = data[data.size - 1].value.toInt()
        data.removeAt(data.size - 1)

        val dataSet = PieDataSet(data, "")
        dataSet.colors = colors

        val pieData = PieData(dataSet).apply {
            setDrawValues(false)
            setValueFormatter(PercentFormatter(chart))
            setValueTextColor(Color.WHITE)
            setValueTextSize(10f)
        }

        chart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            dragDecelerationFrictionCoef = 0.95f

            extraRightOffset = 5F

            isDrawHoleEnabled = true

            minAngleForSlices = 5F

            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)

            isClickable = false
            isFocusable = false
            isContextClickable = false

            holeRadius = 82f
            setHoleColor(ContextCompat.getColor(this@ProductActivity, R.color.grey1))
            centerText = "$calories kcal"
            setCenterTextColor(Color.WHITE)
            setCenterTextSize(18F)
            transparentCircleRadius = 0f

            setDrawCenterText(true)

            setEntryLabelColor(Color.WHITE)
            setEntryLabelTextSize(0f)

            setData(pieData)
            invalidate()
        }

        val legend = chart.legend
        legend.apply {
            isEnabled = false
            textColor = Color.WHITE
            textSize = 10F

        }
    }

    private fun getChartColors(): List<Int> {
        return listOf(
            ContextCompat.getColor(this@ProductActivity, R.color.chartCarbohydrates),
            ContextCompat.getColor(this@ProductActivity, R.color.grey1),
            ContextCompat.getColor(this@ProductActivity, R.color.chartProtein),
            ContextCompat.getColor(this@ProductActivity, R.color.grey1),
            ContextCompat.getColor(this@ProductActivity, R.color.chartFat),
            ContextCompat.getColor(this@ProductActivity, R.color.grey1)
        )
    }

    private fun initializeDate() {
        binding.tvDateMeal.text = getDateInAppFormat(CurrentDate.date().value!!)
    }
}