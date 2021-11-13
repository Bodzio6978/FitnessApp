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
import com.gmail.bodziowaty6978.model.Meal
import com.gmail.bodziowaty6978.viewmodel.AddViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class AddActivity : AppCompatActivity(),LifecycleOwner {

    lateinit var binding:ActivityAddBinding
    lateinit var viewModel:AddViewModel

    private var mealList: MutableList<Meal> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)

        binding.rvAdd.layoutManager = LinearLayoutManager(this)
        binding.rvAdd.adapter = QueryMealRecyclerAdapter(mealList)

        viewModel.getSearchResult().observe(this, {
            mealList.clear()
            mealList.addAll(it)
            binding.rvAdd.adapter?.notifyDataSetChanged()
        })
        val mealName = intent.getStringExtra("mealName")
        binding.tvMealNameAdd.text = mealName

        binding.ibAdd.setOnClickListener {
            val intent = Intent(this, NewActivity::class.java).putExtra("mealName",mealName)
            startActivity(intent)
        }
        binding.ibBackAdd.setOnClickListener {
            super.onBackPressed()
        }

        binding.fabSearchAdd.setOnClickListener {
            viewModel.search(binding.etSearchAdd.text.toString().trim())
        }

    }
}