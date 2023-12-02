package com.mawekk.sterdiary.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentPinCodeBinding

class PinCodeFragment : Fragment() {
    private lateinit var binding: FragmentPinCodeBinding
    private lateinit var keys: List<TextView>
    private lateinit var dots: List<ImageView>
    private var pinCode = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPinCodeBinding.inflate(inflater, container, false)
        binding.apply {
            keys = listOf(key0, key1, key2, key3, key4, key5, key6, key7, key8, key9)
            dots = listOf(dot1, dot2, dot3, dot4)
        }

        setKeysOnClickListener()
        setDeleteButton()

        return binding.root
    }

    private fun setKeysOnClickListener() {
        keys.forEach { textView ->
            textView.setOnClickListener {
                if (pinCode.length < 4) {
                    pinCode += textView.text
                    dots[pinCode.length - 1].setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.green
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    binding.pinText.text = pinCode
                }
            }
        }
    }

    private fun setDeleteButton() {
        binding.deleteLastButton.setOnClickListener {
            if (pinCode.isNotEmpty()) {
                dots[pinCode.length - 1].setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.gray
                    ), android.graphics.PorterDuff.Mode.SRC_IN
                )
                pinCode = pinCode.dropLast(1)
                binding.pinText.text = pinCode
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = PinCodeFragment()
    }
}