package com.mawekk.sterdiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mawekk.sterdiary.MainActivity
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
        activity.binding.noteTopBar.apply {
            viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
                title = (note.time + ", " + note.date)
            }
            setNavigationOnClickListener {
                isVisible = false
                isVisible = true
                activity.onBackPressed()
            }
            setOnMenuItemClickListener {
                true
            }
        }
    }

    private fun showNote() {
        viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
            binding.apply {
                situationText.setText(note.situation)
                thoughtsText.setText(note.thoughts)
                feelingsText.setText(note.feelings)
                actionsText.setText(note.actions)
                answerText.setText(note.answer)
                seekBarBefore.progress = note.discomfortBefore.dropLast(1).toInt()
                seekBarAfter.progress = note.discomfortAfter.dropLast(1).toInt()
                percentsBefore.text = note.discomfortBefore
                percentsAfter.text = note.discomfortAfter
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}