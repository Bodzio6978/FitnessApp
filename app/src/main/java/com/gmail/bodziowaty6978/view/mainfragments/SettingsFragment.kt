package com.gmail.bodziowaty6978.view.mainfragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.databinding.FragmentSettingsBinding
import com.gmail.bodziowaty6978.model.User
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)

        binding.rlLogOutSettings.setOnClickListener {
            logOutUser()
        }

        observeUser()

        binding.rlGoalSettings

        return binding.root
    }

    private fun observeUser(){
        lifecycleScope.launchWhenStarted {
            viewModel.userInformation.observe(this@SettingsFragment,{
                initializeCaloriesGoals(it)
            })
        }
    }

    private fun initializeCaloriesGoals(user:User){
        val nutritionValues = user.nutritionValues!!

        val caloriesValue = nutritionValues["wantedCalories"]!!.toInt()
        val carbohydratesValue = nutritionValues["wantedCarbohydrates"]!!.toInt()
        val proteinValue = nutritionValues["wantedProtein"]!!.toInt()
        val fatValue = nutritionValues["wantedFat"]!!.toInt()

        binding.tvValuesGoal.text = "$caloriesValue kcal, Carbs: ${carbohydratesValue}g, Prot: ${proteinValue}g, Fat:${fatValue}g "
    }

    private fun logOutUser(){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(activity,LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}