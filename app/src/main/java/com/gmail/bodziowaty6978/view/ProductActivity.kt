package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityProductBinding
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.functions.onError
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
            initializeUi(product)
        }

        binding.btAddNew.setOnClickListener {
            if (product != null) {
                viewModel.addProduct(
                    product,
                    id,
                    binding.etWeightMeal.text.toString(),
                    mealName.toString()
                )
            } else {
                val currentProduct = viewModel.productsState.value?.data
                if (currentProduct != null) {
                    viewModel.addProduct(
                        currentProduct,
                        id,
                        binding.etWeightMeal.text.toString().replace(",", ".").trim(),
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
                        startActivity(Intent(this@ProductActivity,MainActivity::class.java).putExtra("position",1))
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

    private fun onLoading(){
        binding.rlProduct.visibility = View.GONE
        binding.pbProduct.visibility = View.VISIBLE
    }

    private fun initializeUi(product: Product) {
        binding.tvProductNameMeal.text = product.name
        binding.tvFatValueMeal.text = product.fat.toString()
        binding.tvCarbsValueMeal.text = product.carbs.toString()
        binding.tvProteinValueMeal.text = product.protein.toString()
        binding.tvCaloriesValueMeal.text = product.calories.toString()
        binding.tvBrandMeal.text = product.brand
        binding.tvUnitMeal.text = product.unit
    }

    private fun initializeDate() {
        binding.tvDateMeal.text = getDateInAppFormat(CurrentDate.date().value!!)
    }
}