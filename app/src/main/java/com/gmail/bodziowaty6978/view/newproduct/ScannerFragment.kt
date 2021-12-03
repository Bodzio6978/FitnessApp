package com.gmail.bodziowaty6978.view.newproduct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.gmail.bodziowaty6978.databinding.FragmentScannerBinding



class ScannerFragment : Fragment() {

    private var _binding : FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScannerBinding.inflate(inflater, container, false)

        codeScanner = CodeScanner(requireContext(),binding.csvNew)
        

        return binding.root
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}