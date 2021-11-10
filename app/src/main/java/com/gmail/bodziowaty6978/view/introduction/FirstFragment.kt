package com.gmail.bodziowaty6978.view.introduction

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.collection.ArrayMap
import androidx.fragment.app.Fragment
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.FragmentFirstBinding
import com.gmail.bodziowaty6978.interfaces.OnDataPassIntroduction
import com.gmail.bodziowaty6978.interfaces.OnRequestFragmentChange


class FirstFragment : Fragment() {

    private var _binding : FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataPasser: OnDataPassIntroduction
    private lateinit var fragmentChanger: OnRequestFragmentChange


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = FragmentFirstBinding.inflate(inflater,container,false)

        val context = requireActivity().applicationContext

        val gender = resources.getStringArray(R.array.gender)

        val genderAdapter = ArrayAdapter(context, R.layout.dropdown_item,gender)

        binding.actvGenderIntroduction.setAdapter(genderAdapter)

        binding.btNavigationNextIntroduction.setOnClickListener {
            fragmentChanger.onRequest(1)
            dataPasser.onDataPass(getData())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPassIntroduction
        fragmentChanger = context as OnRequestFragmentChange
    }

    private fun getData(): ArrayMap<String, String> {
        val data = ArrayMap<String,String>()

        data.putIfAbsent("gender",binding.actvGenderIntroduction.text.toString())
        data.putIfAbsent("age",binding.etAgeIntroduction.text.toString())
        data.putIfAbsent("current",binding.etCurrentIntroduction.text.toString())
        data.putIfAbsent("desired",binding.etDesiredIntroduction.text.toString())

        return data
    }


}