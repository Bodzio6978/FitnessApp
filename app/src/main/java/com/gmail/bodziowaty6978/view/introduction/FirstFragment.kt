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
import com.gmail.bodziowaty6978.singleton.NotificationText
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataPasser: OnDataPassIntroduction
    private lateinit var fragmentChanger: OnRequestFragmentChange

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        binding.btNavigationNextIntroduction.setOnClickListener {
            dataPasser.onDataPass(getData(), false)
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

    override fun onResume() {
        super.onResume()
        val gender = resources.getStringArray(R.array.gender)
        val genderAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, gender)
        binding.actvGenderIntroduction.setAdapter(genderAdapter)
    }

    private fun getData(): ArrayMap<String, String> {

        val data = ArrayMap<String, String>()

        val gender = binding.actvGenderIntroduction.text.toString()
        val age = binding.etAgeIntroduction.text.toString().trim()
        val current = binding.etCurrentIntroduction.text.toString().trim()
        val wanted = binding.etDesiredIntroduction.text.toString().trim()

        if (age.isEmpty() || current.isEmpty() || wanted.isEmpty()) {
            NotificationText.setText(getString(R.string.please_make_sure_all_fields_are_filled_in_correctly))
            NotificationText.startAnimation()
        } else {
            data.putIfAbsent("gender",gender)
            data.putIfAbsent("age",age)
            data.putIfAbsent("current",current)
            data.putIfAbsent("desired",wanted)
            fragmentChanger.onRequest(1)
        }
        return data
    }
}