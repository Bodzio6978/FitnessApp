package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityAddBinding
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.onError
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.other.Constants
import com.gmail.bodziowaty6978.state.Resource
import com.gmail.bodziowaty6978.view.addactivity.AddFragment
import com.gmail.bodziowaty6978.view.addactivity.AddScannerFragment
import com.gmail.bodziowaty6978.view.newproduct.NewActivity
import com.gmail.bodziowaty6978.viewmodel.AddViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddActivity : AppCompatActivity(),LifecycleOwner {

    lateinit var binding:ActivityAddBinding
    val viewModel:AddViewModel by viewModels()

    private val scannerFragment = AddScannerFragment()
    private val addFragment = AddFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.initializeHistory()

        setupPermissions()

        observeButtonPressed()
        observeClickedProduct()
        observeScannedBarcode()

        setUpMealName()

        setFragment(addFragment,"ADD_FRAGMENT")

        lifecycleScope.launch {
            viewModel.scannedBarcode.observe(this@AddActivity,{
                viewModel.checkIfBarcodeExists(it)
            })
        }
    }

    private fun observeClickedProduct(){
        lifecycleScope.launch {
            viewModel.clickedProduct.observe(this@AddActivity,{
                when(it){
                    is Resource.Success -> onClickedSuccess(it)
                    else -> onError(binding.clAdd,it.uiText.toString())
                }
            })
        }
    }

    private fun onClickedSuccess(resource:Resource.Success<Pair<String,Product>>){
        lifecycleScope.launch {
            val product = resource.data!!.second
            val id = resource.data.first

            val intent = Intent(this@AddActivity, ProductActivity::class.java).putExtra("mealName",viewModel.mMealName.value)

            if(product.barcode=="fakeProduct"){
                intent .putExtra("id",product.author)
                startActivity(intent)
            }else{
                intent.putExtra("id",id).putExtra("product",product)
                startActivity(intent)
            }
        }
    }

    private fun setFragment(fragment: Fragment,tag:String) {
        supportFragmentManager.apply {
            beginTransaction().replace(R.id.fcvAdd, fragment,tag).commit()
        }
    }

    private fun observeScannedBarcode(){
        lifecycleScope.launch {
            viewModel.barcodeState.observe(this@AddActivity,{
                when(it){
                    is Resource.Success -> onClickedSuccess(it)
                    else -> onError(binding.clAdd,it.uiText.toString())
                }
            })
        }
    }

    private fun observeButtonPressed(){
        lifecycleScope.launch {
            viewModel.mButtonPressed.observe(this@AddActivity,{
                when(it){
                    1 -> startActivity(Intent(this@AddActivity, NewActivity::class.java).putExtra("mealName",viewModel.mMealName.value))
                    2 -> super.onBackPressed()
                    3 -> setFragment(scannerFragment,"SCANNER_FRAGMENT")
                }
            })
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("SCANNER_FRAGMENT")!=null){
            setFragment(addFragment,"ADD_FRAGMENT")
        }else{
            super.onBackPressed()
        }
    }

    private fun setUpMealName(){
        lifecycleScope.launch {
            val mealName = intent.getStringExtra("mealName")

            if (mealName!=null){
                viewModel.mMealName.value = mealName.toString()
            }
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            Constants.CAMERA_REQUEST_CODE
        )
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(
                        binding.clAdd,
                        R.string.camera_permission_is_required_to_scan_a_product_barcode,
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    //successful request
                    Log.e(TAG, "Successful request")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}