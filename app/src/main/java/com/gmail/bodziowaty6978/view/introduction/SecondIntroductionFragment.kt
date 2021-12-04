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
import com.gmail.bodziowaty6978.databinding.FragmentSecondBinding
import com.gmail.bodziowaty6978.interfaces.OnClearDataRequest
import com.gmail.bodziowaty6978.interfaces.OnFragmentChangeRequest
import com.gmail.bodziowaty6978.interfaces.OnMapPassed
import com.google.android.material.snackbar.Snackbar

class SecondIntroductionFragment : Fragment() {

    private var _binding : FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataPasser: OnMapPassed
    private lateinit var fragmentChanger: OnFragmentChangeRequest
    private lateinit var dataClear: OnClearDataRequest

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = FragmentSecondBinding.inflate(inflater,container,false)

        binding.btBacktIntroduction.setOnClickListener {
            fragmentChanger.onRequest(0)
            dataClear.onClearDataRequest()
        }
        binding.btFinishIntroduction.setOnClickListener {
            dataPasser.onDataPass(getData(),true)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        val typesOfWork = resources.getStringArray(R.array.works)
        val trainingsPerWeek = resources.getStringArray(R.array.trainingsPerWeek)
        val activity = resources.getStringArray(R.array.activity)

        val workAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,typesOfWork)
        val trainingAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,trainingsPerWeek)
        val activityAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item,activity)

        binding.actvWorkIntroduction.setAdapter(workAdapter)
        binding.actvTrainingIntroduction.setAdapter((trainingAdapter))
        binding.actvActivityIntroduction.setAdapter(activityAdapter)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnMapPassed
        fragmentChanger = context as OnFragmentChangeRequest
        dataClear = context as OnClearDataRequest
    }

    private fun getData(): ArrayMap<String, String> {
        val data = ArrayMap<String,String>()

        val type = binding.actvWorkIntroduction.text.toString()
        val workouts = binding.actvTrainingIntroduction.text.toString()
        val activity = binding.actvActivityIntroduction.text.toString()
        val height = binding.etHeightIntroduction.text.toString().trim()

        if(height.isEmpty()){
            Snackbar.make(binding.clIntroductionSecond,R.string.please_make_sure_you_have_entered_your_height, Snackbar.LENGTH_LONG).show()
        }else{
            data.putIfAbsent("type",type)
            data.putIfAbsent("workouts",workouts)
            data.putIfAbsent("activity",activity)
            data.putIfAbsent("height",height)
        }
        return data
    }
}