package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityProductBinding
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.viewmodel.ProductViewModel


class ProductActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var binding: ActivityProductBinding
    lateinit var viewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        binding = ActivityProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ProductViewModel::class.java)

        observeIfAddedProduct()

        initializeDate()

        binding.ibBackMeal.setOnClickListener {
            super.onBackPressed()
        }

        val mealName = intent.getStringExtra("mealName")
        binding.tvMealNameMeal.text = mealName

        val id = intent.getStringExtra("id")!!

        val product = intent.getParcelableExtra<Product>("product")

        if (product==null){
            viewModel.getProduct(id)

            viewModel.getCurrentProduct().observe(this,{
                if (it!=null){
                    initializeUi(it)
                }
            })

        }else{
            initializeUi(product)
        }

        binding.btAddNew.setOnClickListener{
            if (product != null) {
                viewModel.addProduct(product,id,binding.etWeightMeal.text.toString(),mealName.toString())
            }else{
                if (viewModel.getAddingState().value!=null){
                    viewModel.addProduct(viewModel.getCurrentProduct().value!!,id,binding.etWeightMeal.text.toString().replace(",",".").trim(),mealName.toString())
                }
            }
        }

    }

    private fun observeIfAddedProduct(){
        viewModel.getAddingState().observe(this,{
            if (it){
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun initializeUi(product: Product){
        binding.tvProductNameMeal.text = product.name
        binding.tvFatValueMeal.text = product.fat.toString()
        binding.tvCarbsValueMeal.text = product.carbs.toString()
        binding.tvProteinValueMeal.text = product.protein.toString()
        binding.tvCaloriesValueMeal.text = product.calories.toString()
        binding.tvBrandMeal.text = product.brand
        binding.tvUnitMeal.text = product.unit
    }

    private fun initializeDate(){
            binding.tvDateMeal.text = CurrentDate.date().value?.let { getDateInAppFormat(it) }
    }
}