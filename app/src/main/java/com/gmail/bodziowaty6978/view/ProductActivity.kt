package com.gmail.bodziowaty6978.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.functions.onError
import com.gmail.bodziowaty6978.functions.round
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeIfAddedProduct()
        observeProductState()

        initializeDate()

        binding.ibBackMeal.setOnClickListener {
            super.onBackPressed()
        }

        val mealName = intent.getStringExtra("mealName")
        binding.tvMealNameMeal.text = mealName

        val id = intent.getStringExtra("id")!!

        val product = intent.getParcelableExtra<Product>("product")

        if (product == null) {
            viewModel.getProduct(id)
        } else {
            viewModel.productsState.value = Resource.Success(product)
        }

        binding.btAddNew.setOnClickListener {
            val weight = binding.etWeightMeal.text.toString().replace(",", ".").trim()
            if (product != null) {
                viewModel.addProduct(
                    product,
                    id,
                    weight,
                    mealName.toString()
                )
            } else {
                val currentProduct = viewModel.productsState.value?.data
                if (currentProduct != null) {
                    viewModel.addProduct(
                        currentProduct,
                        id,
                        weight,
                        mealName.toString()
                    )
                }

            }
        }

    }

    private fun observeProductState() {
        lifecycleScope.launch {
            viewModel.productsState.observe(this@ProductActivity, {
                when (it) {
                    is Resource.Success -> initializeUi(it.data!!)
                    else -> onError(binding.clProduct, it.uiText.toString())
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
                        onError(binding.clProduct, it.errorMessage)
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
                val weight =
                    binding.etWeightMeal.text.toString().replace(",", ".").trim().toDouble()

                val data = viewModel.getData(weight)
                Log.e(TAG,data.toString())

                refreshChartData(data)
                refreshNutritionValues(product,weight)
            }
            binding.etWeightMeal.setText("100")
        }
        binding.tvProductNameMeal.text = product.name
        if (!product.brand.isNullOrBlank()) {
            binding.tvBrandMeal.text = product.brand
            binding.tvBrandMeal.visibility = View.VISIBLE
        }

        binding.tvUnitMeal.text = product.unit
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