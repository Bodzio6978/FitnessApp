package com.gmail.bodziowaty6978.view.addactivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.gmail.bodziowaty6978.databinding.FragmentAddScannerBinding
import com.gmail.bodziowaty6978.viewmodel.AddViewModel

class AddScannerFragment : Fragment() {

    private var _binding: FragmentAddScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddViewModel

    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddScannerBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(AddViewModel::class.java)

        codeScanner = CodeScanner(requireContext(),binding.csvAdd)

        codeScanner.startPreview()

        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread{
                viewModel.scannedBarcode.value = it.text
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            activity?.runOnUiThread {
                Toast.makeText(activity, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

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