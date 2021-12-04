package com.gmail.bodziowaty6978.view.newproduct

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityNewBinding
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.interfaces.OnFragmentChangeRequest
import com.gmail.bodziowaty6978.interfaces.OnProductPassed
import com.gmail.bodziowaty6978.interfaces.OnStringPassed
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.view.MealActivity
import com.gmail.bodziowaty6978.viewmodel.NewProductState
import com.gmail.bodziowaty6978.viewmodel.NewViewModel

import com.google.android.material.snackbar.Snackbar

const val CAMERA_REQUEST_CODE = 101

class NewActivity : AppCompatActivity(), LifecycleOwner,OnFragmentChangeRequest,OnProductPassed,OnStringPassed {

    private lateinit var viewModel: NewViewModel
    lateinit var binding: ActivityNewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        binding = ActivityNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(NewViewModel::class.java)

        setupPermissions()

        setFragment(viewModel.getProductFragment(),"PRODUCT_FRAGMENT")

        viewModel.getAction().observe(this, {
            when (it.value) {
                NewProductState.ADDING_MEAL -> {
                    Snackbar.make(binding.clNew,R.string.adding_new_meal, Snackbar.LENGTH_LONG).show()
                }
                NewProductState.MEAL_ADDED -> {
                    startMealActivity()
                }
                NewProductState.ERROR_ADDING_MEAL -> {
                    Snackbar.make(binding.clNew,R.string.an_error_has_occurred_during_adding_your_product, Snackbar.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun setFragment(fragment: Fragment,tag:String) {
        supportFragmentManager.apply {
            beginTransaction().replace(R.id.fcvNew, fragment,tag).commit()
        }
    }

    private fun startMealActivity() {
        val key = viewModel.getKey()
        val intent = Intent(this, MealActivity::class.java).putExtra("id", key).putExtra("mealName", intent.getStringExtra("mealName"))
        startActivity(intent)
        finish()
    }

    override fun onRequest(position: Int) {
        when(position){
            1 -> setFragment(viewModel.getScannerFragment(),"SCANNER_FRAGMENT")
            0 -> setFragment(viewModel.getProductFragment(),"PRODUCT_FRAGMENT")
        }
    }

    override fun onProductPass(product: Product) {
        viewModel.addNewProduct(product)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentByTag("SCANNER_FRAGMENT")!=null){
            setFragment(viewModel.getProductFragment(),"PRODUCT_FRAGMENT")
        }else{
            super.onBackPressed()
        }
    }

    override fun onStringPass(text: String) {
        val bundle = Bundle()
        bundle.putString("barcode",text)
        viewModel.getProductFragment().arguments = bundle
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
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
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(binding.clNew,R.string.camera_permission_is_required_to_scan_a_product_barcode,
                        Snackbar.LENGTH_LONG).show()
                } else {
                    //successful request
                    Log.e(TAG, "Successful request")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}