package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityAddBinding
import com.gmail.bodziowaty6978.view.addactivity.AddFragment
import com.gmail.bodziowaty6978.view.addactivity.AddScannerFragment
import com.gmail.bodziowaty6978.view.newproduct.NewActivity
import com.gmail.bodziowaty6978.viewmodel.AddViewModel
import com.google.android.material.snackbar.Snackbar

class AddActivity : AppCompatActivity(),LifecycleOwner {

    lateinit var binding:ActivityAddBinding
    lateinit var viewModel:AddViewModel

    private val scannerFragment = AddScannerFragment()
    private val addFragment = AddFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AddViewModel::class.java)

        viewModel.initializeHistory()

        observeButtonPressed()
        observeClickedProduct()
        observeScannedBarcode()
        observeSnackbarMessage()

        setUpMealName()

        setFragment(addFragment,"ADD_FRAGMENT")
    }

    private fun observeClickedProduct(){

        viewModel.mClickedProduct.observe(this,{
            val product = it.second
            val id = it.first

            val intent = Intent(this, ProductActivity::class.java).putExtra("mealName",viewModel.mMealName.value)

            if(product.barcode=="fakeProduct"){
                intent .putExtra("id",product.author)
                startActivity(intent)
            }else{
                intent.putExtra("id",id).putExtra("product",product)
                startActivity(intent)
            }
        })
    }

    private fun setFragment(fragment: Fragment,tag:String) {
        supportFragmentManager.apply {
            beginTransaction().replace(R.id.fcvAdd, fragment,tag).commit()
        }
    }

    private fun observeScannedBarcode(){
        viewModel.mScannedBarcode.observe(this,{
            viewModel.checkIfBarcodeExists(it)
        })
    }

    private fun observeButtonPressed(){
        viewModel.mButtonPressed.observe(this,{
            when(it){
                1 -> startActivity(Intent(this, NewActivity::class.java).putExtra("mealName",viewModel.mMealName.value))
                2 -> super.onBackPressed()
                3 -> setFragment(scannerFragment,"SCANNER_FRAGMENT")
            }
        })
    }

    private fun observeSnackbarMessage(){
        viewModel.mSnackbarMessage.observe(this,{
            Snackbar.make(binding.clAdd,it,Snackbar.LENGTH_LONG).show()
        })
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("SCANNER_FRAGMENT")!=null){
            setFragment(addFragment,"ADD_FRAGMENT")
        }else{
            super.onBackPressed()
        }
    }

    private fun setUpMealName(){
        val mealName = intent.getStringExtra("mealName")

        if (mealName!=null){
            viewModel.mMealName.value = mealName.toString()
        }

    }
}