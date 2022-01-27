package com.gmail.bodziowaty6978.view.addactivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmail.bodziowaty6978.adapters.QueryMealRecyclerAdapter
import com.gmail.bodziowaty6978.databinding.FragmentAddBinding
import com.gmail.bodziowaty6978.functions.getDateInAppFormat
import com.gmail.bodziowaty6978.interfaces.OnAdapterItemClickListener
import com.gmail.bodziowaty6978.model.Product
import com.gmail.bodziowaty6978.singleton.CurrentDate
import com.gmail.bodziowaty6978.viewmodel.AddViewModel


class AddFragment : Fragment(), OnAdapterItemClickListener {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddViewModel

    private var productList: MutableList<Product> = mutableListOf()
    private var idList: MutableList<String> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AddViewModel::class.java)

        binding.rvAdd.layoutManager = LinearLayoutManager(activity)
        binding.rvAdd.adapter = QueryMealRecyclerAdapter(productList, this)

        observeMealName()
        observeDate()
        observeIds()
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
            viewModel.search(binding.etSearchAdd.text.toString().trim().toLowerCase())
        }

        return binding.root
    }

    override fun onAdapterItemClickListener(position: Int) {
        val clickedProduct = productList[position]

        viewModel.mClickedProduct.value = Pair(idList[position], clickedProduct)
    }

    private fun observeDate() {
        CurrentDate.date.observe(viewLifecycleOwner, {
            binding.tvDayAdd.text = getDateInAppFormat(it)
        })
    }

    private fun observeIds() {
        viewModel.getIds().observe(viewLifecycleOwner, {
            idList.clear()
            idList.addAll(it)
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeSearchResult(){
        viewModel.getSearchResult().observe(viewLifecycleOwner, {
            productList.clear()
            productList.addAll(it)
            binding.rvAdd.adapter?.notifyDataSetChanged()
        })
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