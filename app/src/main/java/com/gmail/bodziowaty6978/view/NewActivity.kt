package com.gmail.bodziowaty6978.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityNewBinding
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.gmail.bodziowaty6978.viewmodel.NewViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class NewActivity : AppCompatActivity(), LifecycleOwner {

    lateinit var viewModel: NewViewModel
    lateinit var binding: ActivityNewBinding

    private var items = arrayOf<String>("g","kg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        binding = ActivityNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(NewViewModel::class.java)

        viewModel.getAction().observe(this,{

        })



        binding.btSaveNew.setOnClickListener {
            addNewMeal()
        }

        binding.ibBackNew.setOnClickListener {
            super.onBackPressed()
        }

        binding.tlNew.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position){
                    0 -> binding.tvWeightNew.text = getString(R.string.container_weight)
                    1 -> binding.tvWeightNew.text = getString(R.string.container_weight_star)
                    2 -> binding.tvWeightNew.text = getString(R.string.portion_weight_star)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun addNewMeal() {
        NotificationText.text.value = "huj"
//        viewModel.addNewProduct(
//                name = binding.etNameNew.text.toString().trim(),
//                brand = binding.etBrandNew.text.toString().trim(),
//                weight = binding.etWeightNew.text.toString().trim(),
//                position = binding.tlNew.selectedTabPosition,
//                calories = binding.etCaloriesNew.text.toString().trim(),
//                carbs = binding.etCarbsNew.text.toString().trim(),
//                protein = binding.etProteinNew.text.toString().trim(),
//                fat = binding.etFatNew.text.toString().trim(),
//                barCode = binding.etBarCodeNew.text.toString().trim()
//        )
    }
}