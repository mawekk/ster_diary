package com.mawekk.sterdiary.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.forEach
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentEmotionsBinding
import com.mawekk.sterdiary.db.emotions.Emotion
import com.mawekk.sterdiary.db.emotions.EmotionViewModel

class EmotionsFragment : Fragment() {
    private lateinit var binding: FragmentEmotionsBinding
    private val viewModel: EmotionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEmotionsBinding.inflate(inflater, container, false)
        showEmotions()
        setNewEmotionButton()
        setTopAppBarActions()
        return binding.root
    }

    private fun showEmotions() {
        viewModel.getAllEmotions().observe(viewLifecycleOwner) { emotions ->
            binding.emotionsChipGroup.removeAllViews()
            emotions.forEach {
                val chip = Chip(context)
                if (viewModel.isEmotionSelected(it)) {
                    chip.isActivated = true
                }
                chip.setChipStrokeColorResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
                chip.setTextAppearance(R.style.ChipTextAppearance)
                chip.text = it.name
                chip.setOnClickListener {
                    emotionOnClick(chip)
                }

                checkChip(chip)
                binding.emotionsChipGroup.addView(chip)
            }
        }
    }

    private fun checkChip(chip: Chip) {
        if (chip.isActivated) {
            chip.setChipBackgroundColorResource(R.color.green)
        } else {
            chip.setChipBackgroundColorResource(R.color.light_gray)
        }
    }

    private fun emotionOnClick(chip: Chip) {
        chip.isActivated = !chip.isActivated
        checkChip(chip)
    }

    private fun setNewEmotionButton() {
        val activity = activity as MainActivity
        binding.apply {
            addNewEmotionButton.setOnClickListener {
                val builder = AlertDialog.Builder(activity)
                val dialogLayout = layoutInflater.inflate(R.layout.new_emotion, null)
                builder.setView(dialogLayout)

                val dialog = builder.create()
                val name = dialogLayout.findViewById<EditText>(R.id.emotionText)
                val saveButton = dialogLayout.findViewById<Button>(R.id.saveButton)
                val cancelButton = dialogLayout.findViewById<Button>(R.id.cancelButton)

                saveButton.setOnClickListener {
                    if (name.text.isNotEmpty())
                        viewModel.addEmotion(Emotion(name.text.toString()))
                    dialog.dismiss()
                }
                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    private fun checkSelectedEmotions(): List<Emotion> {
        val emotions = mutableListOf<Emotion>()
        binding.emotionsChipGroup.forEach {
            val chip = it as Chip
            if (chip.isActivated) {
                emotions.add(Emotion(chip.text.toString()))
            }
        }
        return emotions
    }

    private fun setTopAppBarActions() {
        val activity = activity as MainActivity
        activity.binding.emotionsTopBar.apply {
            setNavigationOnClickListener {
                activity.onBackPressed()
            }
            setOnMenuItemClickListener {
                viewModel.selectEmotions(checkSelectedEmotions())
                activity.onBackPressed()
                true
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = EmotionsFragment()
    }
}