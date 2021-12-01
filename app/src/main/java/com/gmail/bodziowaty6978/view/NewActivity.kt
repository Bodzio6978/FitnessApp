package com.gmail.bodziowaty6978.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.*
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.ActivityNewBinding
import com.gmail.bodziowaty6978.singleton.NotificationText
import com.gmail.bodziowaty6978.viewmodel.NewProductState
import com.gmail.bodziowaty6978.viewmodel.NewViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi

private const val CAMERA_REQUEST_CODE = 101

@DelicateCoroutinesApi
class NewActivity : AppCompatActivity(), LifecycleOwner, AdapterView.OnItemClickListener {

    private lateinit var viewModel: NewViewModel
    lateinit var binding: ActivityNewBinding
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)

        setupPermissions()

        binding = ActivityNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(NewViewModel::class.java)

        viewModel.getAction().observe(this, {
            when (it.value) {
                NewProductState.ADDING_MEAL -> {
                    NotificationText.setText("Adding new meal")
                }
                NewProductState.MEAL_ADDED -> {
                    NotificationText.setText("Meal has been added")
                    startMealActivity()
                }
                NewProductState.ERROR_ADDING_MEAL -> {
                    NotificationText.setText("An error has occurred during adding your product")
                }
            }
            NotificationText.startAnimation()
        })

        codeScanner = CodeScanner(this, binding.csvNew)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {
                    binding.etBarCodeNew.setText(it.text)
                    changeScannerVisibility()
                }
            }
            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("NewActivity", it.message.toString())
                }
            }
        }

        val unit = resources.getStringArray(R.array.unit)
        val unitAdapter = ArrayAdapter(this, R.layout.dropdown_item, unit)
        binding.actvUnitIntroduction.setAdapter(unitAdapter)

        binding.actvUnitIntroduction.onItemClickListener = this

        binding.csvNew.setOnClickListener {
            codeScanner.startPreview()
        }

        binding.tlNew.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
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

        binding.ibBarCodeNew.setOnClickListener {
            changeScannerVisibility()
        }

        binding.btSaveNew.setOnClickListener {
            addNewMeal()
        }

        binding.ibBackNew.setOnClickListener {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(position){
            0 -> binding.tlNew.getTabAt(0)?.text = getString(R.string.in_100g)
            1 -> binding.tlNew.getTabAt(0)?.text = getString(R.string.in_100ml)
        }
    }



    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onBackPressed() {
        if (binding.flLayoutNew.visibility == View.VISIBLE) {
            binding.flLayoutNew.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    private fun addNewMeal() {
        viewModel.addNewProduct(
                name = binding.etNameNew.text.toString().trim(),
                brand = binding.etBrandNew.text.toString().trim(),
                weight = binding.etWeightNew.text.toString().trim(),
                position = binding.tlNew.selectedTabPosition,
                unit = binding.actvUnitIntroduction.text.toString(),
                calories = binding.etCaloriesNew.text.toString().trim(),
                carbs = binding.etCarbsNew.text.toString().trim(),
                protein = binding.etProteinNew.text.toString().trim(),
                fat = binding.etFatNew.text.toString().trim(),
                barCode = binding.etBarCodeNew.text.toString().trim()
        )
    }

    private fun startMealActivity() {
        val key = viewModel.getKey()
        val intent = Intent(this, MealActivity::class.java).putExtra("key", key).putExtra("mealName", intent.getStringExtra("mealName"))
        startActivity(intent)
        finish()
    }

    private fun changeScannerVisibility() {
        if (binding.flLayoutNew.visibility == View.VISIBLE) {
            binding.flLayoutNew.visibility = View.GONE
        } else {
            binding.flLayoutNew.visibility = View.VISIBLE
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    NotificationText.setText("You need the camera permission in order to scan the barcode")
                    NotificationText.startAnimation()
                } else {
                    //successful request
                    Log.e("NewActivity", "Successful request")
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}