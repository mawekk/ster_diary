package com.mawekk.sterdiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentNoteBinding
import com.mawekk.sterdiary.db.notes.NoteViewModel


class NoteFragment : Fragment() {
    lateinit var binding: FragmentNoteBinding
    private val viewModel: NoteViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        setTopAppBarActions()
        showNote()
        showEmotions()
        disableAllFields()
        return binding.root
    }

    private fun disableAllFields() {
        binding.apply {
            situationText.isEnabled = false
            thoughtsText.isEnabled = false
            feelingsText.isEnabled = false
            actionsText.isEnabled = false
            answerText.isEnabled = false

            seekBarBefore.setOnTouchListener { _, _ -> true }
            seekBarBefore.setOnTouchListener { _, _ -> true }
        }
    }

    private fun setTopAppBarActions() {
        val activity = activity as MainActivity
        activity.binding.apply {
            viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
                noteTopBar.title = (note.time + ", " + note.date)

                noteTopBar.setNavigationOnClickListener {
                    activity.onBackPressed()
                }
                noteTopBar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit -> {
                            editNoteTopBar.isVisible = true
                            noteTopBar.isVisible = false
                            activity.showFragment(EditNoteFragment.newInstance(), R.id.edit)
                        }

                        R.id.delete -> {
                            editNoteTopBar.isVisible = false
                            viewModel.deleteNote(note)
                            activity.onBackPressed()
                            true
                        }

                        R.id.help -> {
                            activity.onBackPressed()
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }

    private fun showNote() {
        viewModel.selectedNote.observe(viewLifecycleOwner) {
            binding.apply {
                situationText.setText(it.situation)
                thoughtsText.setText(it.thoughts)
                feelingsText.setText(it.feelings)
                actionsText.setText(it.actions)
                answerText.setText(it.answer)
                seekBarBefore.progress = it.discomfortBefore.dropLast(1).toInt()
                seekBarAfter.progress = it.discomfortAfter.dropLast(1).toInt()
                percentsBefore.text = it.discomfortBefore
                percentsAfter.text = it.discomfortAfter
            }
        }
    }

    private fun showEmotions() {
        viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
            note.emotions.split(" ").forEach {
                if (it.isNotEmpty()) {
                    val chip = Chip(context)
                    chip.setChipBackgroundColorResource(R.color.light_gray)
                    chip.setChipStrokeColorResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
                    chip.setTextAppearance(R.style.ChipTextAppearance)
                    chip.text = it
                    chip.isEnabled = false
                    binding.emotionsGroup.addView(chip)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}