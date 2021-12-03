package com.gmail.bodziowaty6978.view.newproduct

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.FragmentProductBinding
import com.gmail.bodziowaty6978.functions.round
import com.gmail.bodziowaty6978.interfaces.OnFragmentChangeRequest
import com.gmail.bodziowaty6978.interfaces.OnProductPassed
import com.gmail.bodziowaty6978.model.Product
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class ProductFragment : Fragment(), AdapterView.OnItemClickListener {

    private var _binding : FragmentProductBinding? = null
    private val binding get() = _binding!!

    private lateinit var fragmentChanger:OnFragmentChangeRequest
    private lateinit var productPasser:OnProductPassed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductBinding.inflate(inflater, container, false)



        binding.actvUnitIntroduction.onItemClickListener = this

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


        binding.btSaveNew.setOnClickListener {
            checkData()
        }

        binding.ibBackNew.setOnClickListener {
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(position){
            0 -> binding.tlNew.getTabAt(0)?.text = getString(R.string.in_100g)
            1 -> binding.tlNew.getTabAt(0)?.text = getString(R.string.in_100ml)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        productPasser = context as OnProductPassed
        fragmentChanger = context as OnFragmentChangeRequest
    }

    override fun onResume() {
        super.onResume()
        val unit = resources.getStringArray(R.array.unit)
        val unitAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, unit)
        binding.actvUnitIntroduction.setAdapter(unitAdapter)
    }

    private fun checkData(){
        val name = binding.etNameNew.text.toString().trim()
        val brand = binding.etBrandNew.text.toString().trim()

        val containerWeight = binding.etWeightNew.text.toString().replace(",",".").trim()
        val position = binding.tlNew.selectedTabPosition
        val unit = binding.actvUnitIntroduction.text.toString()

        val calories = binding.etCaloriesNew.text.toString().replace(",",".")
        val carbohydrates = binding.etCarbsNew.text.toString().replace(",",".")
        val protein = binding.etProteinNew.text.toString().replace(",",".")
        val fat = binding.etFatNew.text.toString().replace(",",".")

        val barcode = binding.etBarCodeNew.text.toString().trim()

        if((name.isEmpty()||calories.isEmpty()||carbohydrates.isEmpty()||protein.isEmpty()||fat.isEmpty())
            ||(position!=0&&containerWeight.isEmpty())){
            Snackbar.make(binding.clProduct,R.string.please_make_sure_all_fields_are_filled_in_correctly,Snackbar.LENGTH_LONG).show()
        }else{
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            productPasser.onProductPass(Product(
                userId,
                name,
                brand,
                containerWeight.toDouble().round(),
                position,
                unit,
                calories.toInt(),
                carbohydrates.toDouble(),
                protein.toDouble(),
                fat.toDouble(),
                barcode
            ))
        }

    }
}