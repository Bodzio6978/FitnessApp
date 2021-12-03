package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.adapters.QueryMealRecyclerAdapter
import com.gmail.bodziowaty6978.databinding.ActivityAddBinding
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.interfaces.OnAdapterItemClickListener
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.viewmodel.AddViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class AddActivity : AppCompatActivity(),LifecycleOwner, OnAdapterItemClickListener {

    lateinit var binding:ActivityAddBinding
    lateinit var viewModel:AddViewModel

    private var productList: MutableList<Product> = mutableListOf()
    private var idList:MutableList<String> = mutableListOf()

    private lateinit var mealName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)

        binding.rvAdd.layoutManager = LinearLayoutManager(this)
        binding.rvAdd.adapter = QueryMealRecyclerAdapter(productList,this)

        viewModel.initializeHistory()

        observeSearchResult()
        observeIds()

        mealName = intent.getStringExtra("mealName").toString()

        CurrentDate.date.observe(this,{
            binding.tvDayAdd.text = getDateInAppFormat(it)
        })

        binding.tvMealNameAdd.text = mealName

        binding.ibAdd.setOnClickListener {
            val intent = Intent(this, NewActivity::class.java).putExtra("mealName",mealName)
            startActivity(intent)
        }
        binding.ibBackAdd.setOnClickListener {
            super.onBackPressed()
        }

        binding.fabSearchAdd.setOnClickListener {
            viewModel.search(binding.etSearchAdd.text.toString().trim().toLowerCase(Locale.ROOT))
        }

    }

    override fun onAdapterItemClickListener(position: Int) {
        val clickedProduct = productList[position]

        val intent = Intent(this,MealActivity::class.java).putExtra("mealName",mealName)

        if(clickedProduct.barcode=="fakeProduct"){
            intent .putExtra("id",clickedProduct.author)
            startActivity(intent)
        }else{
            intent.putExtra("id",idList[position]).putExtra("product",clickedProduct)
            startActivity(intent)
        }
    }

    private fun observeIds(){
        viewModel.getIds().observe(this,{
            idList.clear()
            idList.addAll(it)
        })
    }

    private fun observeSearchResult(){
        viewModel.getSearchResult().observe(this, {
            productList.clear()
            productList.addAll(it)
            binding.rvAdd.adapter?.notifyDataSetChanged()
        })
    }
}