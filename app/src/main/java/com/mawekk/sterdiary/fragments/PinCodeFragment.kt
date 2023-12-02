package com.mawekk.sterdiary.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mawekk.sterdiary.databinding.FragmentPinCodeBinding

class PinCodeFragment : Fragment() {
    private lateinit var binding: FragmentPinCodeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPinCodeBinding.inflate(inflater, container, false)

        return binding.root
    }
    companion object {
        @JvmStatic
        fun newInstance() = PinCodeFragment()
    }
}