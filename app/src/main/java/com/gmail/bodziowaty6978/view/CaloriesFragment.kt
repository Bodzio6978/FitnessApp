package com.gmail.bodziowaty6978.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gmail.bodziowaty6978.databinding.FragmentCaloriesBinding
import com.gmail.bodziowaty6978.viewmodel.CaloriesViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class CaloriesFragment() : Fragment() {

    private var _binding : FragmentCaloriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel:CaloriesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentCaloriesBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(CaloriesViewModel::class.java)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}