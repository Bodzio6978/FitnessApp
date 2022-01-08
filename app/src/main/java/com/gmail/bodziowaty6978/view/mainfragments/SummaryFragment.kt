package com.gmail.bodziowaty6978.view.mainfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gmail.bodziowaty6978.databinding.FragmentSummaryBinding
import com.gmail.bodziowaty6978.singleton.UserInformation


class SummaryFragment : Fragment() {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    private var wantedCalories:Double? = null
    private var currentCalories:Double? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)

        observeCalories()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeCalories(){
        UserInformation.currentCalories.observe(viewLifecycleOwner,{
            binding.tvCurrentCaloriesSummary.text = it.toString()
            currentCalories = it.toDouble()
            updateCaloriesProgress()
        })

        UserInformation.getUser().observe(viewLifecycleOwner,{ user ->
            ("/"+user.nutritionValues?.get("wantedCalories")?.toInt().toString()+" kcal").also { binding.tvWantedCaloriesSummary.text = it }
            wantedCalories = user.nutritionValues?.get("wantedCalories")
            updateCaloriesProgress()
        })
    }

    private fun updateCaloriesProgress(){
        if (wantedCalories!=null&&currentCalories!=null){
            val progress = (currentCalories!!/wantedCalories!!).toInt()
            ("$progress%").also { binding.tvCaloriesProgress.text = it }
            binding.pbCaloriesSummary.progress = progress
        }else{
            binding.pbCaloriesSummary.progress = 0
            ("0%").also { binding.tvCaloriesProgress.text = it }
        }
    }
}
