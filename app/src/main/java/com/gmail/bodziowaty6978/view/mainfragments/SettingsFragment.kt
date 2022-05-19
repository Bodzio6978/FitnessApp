package com.gmail.bodziowaty6978.view.mainfragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gmail.bodziowaty6978.R
import com.gmail.bodziowaty6978.databinding.FragmentSettingsBinding
import com.gmail.bodziowaty6978.model.User
import com.gmail.bodziowaty6978.view.WeightChartActivity
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.gmail.bodziowaty6978.view.settings.NutritionActivity
import com.gmail.bodziowaty6978.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        binding.rlLogOutSettings.setOnClickListener {
            logOutUser()
        }

        lifecycleScope.launch {
            binding.rlGoalSettings.setOnClickListener {
                val intent = Intent(requireContext(), NutritionActivity::class.java)
                startActivity(intent)
            }
        }

        lifecycleScope.launch {
            binding.rlWeightProgress.setOnClickListener {
                val intent = Intent(requireContext(),WeightChartActivity::class.java)
                startActivity(intent)
            }
        }




        observeUser()
        binding.rlGoalSettings

        return binding.root
    }

    private fun observeUser() {
        lifecycleScope.launchWhenStarted {
            viewModel.userInformation.observe(this@SettingsFragment, {
                initializeCaloriesGoals(it)
            })
        }
    }

    private fun initializeCaloriesGoals(user: User) {
        val nutritionValues = user.nutritionValues!!

        val caloriesValue = nutritionValues["wantedCalories"]!!.toInt().toString()
        val carbohydratesValue = nutritionValues["wantedCarbohydrates"]!!.toInt().toString()
        val proteinValue = nutritionValues["wantedProtein"]!!.toInt().toString()
        val fatValue = nutritionValues["wantedFat"]!!.toInt().toString()

        binding.tvValuesGoal.text = String.format(resources.getString(R.string.settings_nutrition_values),caloriesValue,carbohydratesValue,proteinValue,fatValue)
    }

    private fun logOutUser() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}