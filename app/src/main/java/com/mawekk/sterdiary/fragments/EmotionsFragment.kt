package com.mawekk.sterdiary.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.Chip
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentEmotionsBinding
import com.mawekk.sterdiary.db.Emotion

class EmotionsFragment : Fragment() {
    lateinit var binding: FragmentEmotionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmotionsBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    fun init(){
        val list = listOf<Emotion>(
            Emotion(0, "mmamm"),
            Emotion(0, "mmmm"),
            Emotion(0, "mmaaamm"),
            Emotion(0, "mmaamm"),
            Emotion(0, "mmmmaaaaaaaaaaaa"),
        )
        list.forEach{
            val chip = Chip(context)
            chip.setBackgroundColor(resources.getColor(R.color.green))
            chip.text = it.name
            binding.emotionsChipGroup.addView(chip)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = EmotionsFragment()
    }
}