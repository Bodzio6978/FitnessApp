package com.gmail.bodziowaty6978.view.mainfragments


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gmail.bodziowaty6978.databinding.FragmentSettingBinding
import com.gmail.bodziowaty6978.view.GoalActivity
import com.gmail.bodziowaty6978.view.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.DelicateCoroutinesApi


class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    lateinit var instance: FirebaseAuth

    @DelicateCoroutinesApi
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentSettingBinding.inflate(inflater,container,false)
        instance = FirebaseAuth.getInstance()

        binding.rlLogOutSettings.setOnClickListener {
            instance.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.rlGoalSettings.setOnClickListener {
            val intent = Intent(activity, GoalActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}