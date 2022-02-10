package com.gmail.bodziowaty6978.view.addactivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.bodziowaty6978.adapters.QueryMealRecyclerAdapter
import com.gmail.bodziowaty6978.databinding.FragmentAddBinding
import com.gmail.bodziowaty6978.functions.TAG
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.functions.hideKeyboard
import com.gmail.bodziowaty6978.interfaces.OnAdapterItemClickListener
import com.gmail.bodziowaty6978.model.JournalEntry
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.state.Resource
import com.gmail.bodziowaty6978.viewmodel.AddViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class AddFragment : Fragment(), OnAdapterItemClickListener {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddViewModel

    private var productList: MutableList<Product> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[AddViewModel::class.java]

        binding.rvAdd.layoutManager = LinearLayoutManager(activity)
        binding.rvAdd.adapter = QueryMealRecyclerAdapter(productList, this)

        lifecycleScope.launchWhenStarted {
            viewModel.historyState.collect {
                when (it) {
                    is Resource.Error -> onError(it.uiText!!)
                    is Resource.Success -> initializeHistory(it)
                    else -> Log.e(TAG, "Initialized stateFlow of journal entries list")
                }
            }
        }

        observeMealName()
        setUpDate()
        observeSearchResult()

        binding.ibAdd.setOnClickListener {
            viewModel.mButtonPressed.value = 1
        }

        binding.ibBackAdd.setOnClickListener {
            viewModel.mButtonPressed.value = 2
        }

        binding.ibBarCodeAdd.setOnClickListener {
            viewModel.mButtonPressed.value = 3
        }

        binding.btSearchAdd.setOnClickListener {
            viewModel.search(binding.etSearchAdd.text.toString().trim().lowercase())
            hideKeyboard()
        }

        return binding.root
    }

    private fun onError(text: String) {
        Snackbar.make(binding.clAddFragment, text, Snackbar.LENGTH_LONG)
            .show()
    }

    private fun initializeHistory(resource: Resource.Success<List<JournalEntry>>) {
        val data = resource.data
        if (data != null) {
            viewModel.convertEntries(data)
        }
    }

    override fun onAdapterItemClickListener(position: Int) {
        val clickedProduct = productList[position]
        viewModel.searchProduct(clickedProduct)
    }

    private fun setUpDate() {
        binding.tvDayAdd.text = getDateInAppFormat(CurrentDate.date().value!!)
    }


    private fun observeSearchResult() {
        lifecycleScope.launch {
            viewModel.searchResultState.observe(viewLifecycleOwner,{
                when(it){
                    is Resource.Success -> onSearchSuccess(it)
                    else  -> onError(it.uiText.toString())
                }
            })
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onSearchSuccess(resource:Resource.Success<Map<String,Product>>){
        val products = resource.data!!.values
        productList.clear()
        productList.addAll(products)
        binding.rvAdd.adapter?.notifyDataSetChanged()
    }

    private fun observeMealName() {
        viewModel.mMealName.observe(viewLifecycleOwner, {
            binding.tvMealNameAdd.text = it
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}