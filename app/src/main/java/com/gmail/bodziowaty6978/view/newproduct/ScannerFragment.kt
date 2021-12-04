package com.gmail.bodziowaty6978.view.newproduct

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.gmail.bodziowaty6978.databinding.FragmentScannerBinding
import com.gmail.bodziowaty6978.interfaces.OnFragmentChangeRequest
import com.gmail.bodziowaty6978.interfaces.OnStringPassed


class ScannerFragment : Fragment() {

    private var _binding : FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var codeScanner: CodeScanner

    private lateinit var barcodePasser:OnStringPassed
    private lateinit var fragmentChanger: OnFragmentChangeRequest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScannerBinding.inflate(inflater, container, false)

        codeScanner = CodeScanner(requireContext(),binding.csvNew)

        codeScanner.startPreview()

        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread{
                barcodePasser.onStringPass(it.text.toString())
                fragmentChanger.onRequest(0)
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        barcodePasser = context as OnStringPassed
        fragmentChanger = context as OnFragmentChangeRequest
    }


}