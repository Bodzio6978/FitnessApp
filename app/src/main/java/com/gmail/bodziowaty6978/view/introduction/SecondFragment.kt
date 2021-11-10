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
import com.gmail.bodziowaty6978.interfaces.OnDataPassIntroduction
import com.gmail.bodziowaty6978.interfaces.OnRequestFragmentChange

class SecondFragment : Fragment() {

    private var _binding : FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataPasser: OnDataPassIntroduction
    private lateinit var fragmentChanger: OnRequestFragmentChange

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        _binding = FragmentSecondBinding.inflate(inflater,container,false)

        val context = requireActivity().applicationContext

        val typesOfWork = resources.getStringArray(R.array.works)
        val trainingsPerWeek = resources.getStringArray(R.array.trainingsPerWeek)
        val activity = resources.getStringArray(R.array.activity)


        val workAdapter = ArrayAdapter(context,R.layout.dropdown_item,typesOfWork)
        val trainingAdapter = ArrayAdapter(context,R.layout.dropdown_item,trainingsPerWeek)
        val activityAdapter = ArrayAdapter(context,R.layout.dropdown_item,activity)


        binding.actvWorkIntroduction.setAdapter(workAdapter)
        binding.actvTrainingIntroduction.setAdapter((trainingAdapter))
        binding.actvActivityIntroduction.setAdapter(activityAdapter)

        binding.btBacktIntroduction.setOnClickListener {
            fragmentChanger.onRequest(0)
        }
        binding.btFinishIntroduction.setOnClickListener {
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

        data.putIfAbsent("type",binding.actvWorkIntroduction.text.toString())
        data.putIfAbsent("workouts",binding.actvTrainingIntroduction.text.toString())
        data.putIfAbsent("activity",binding.actvActivityIntroduction.text.toString())
        data.putIfAbsent("height",binding.etHeightIntroduction.text.toString())

        return data
    }
}