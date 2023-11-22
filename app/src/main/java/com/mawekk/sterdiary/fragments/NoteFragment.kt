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
import com.mawekk.sterdiary.NoteWorker
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentNewNoteBinding
import com.mawekk.sterdiary.db.DiaryViewModel


class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNewNoteBinding
    private lateinit var worker: NoteWorker
    private val viewModel: DiaryViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        worker = NoteWorker(
            requireActivity(),
            activity as MainActivity,
            viewModel,
            viewLifecycleOwner,
            binding
        )

        worker.setDistortions()
        worker.clearStructure()
        setTopAppBarActions()
        disableAllFields()
        showNote()
        return binding.root
    }

    private fun setTopAppBarActions() {
        val activity = activity as MainActivity
        activity.binding.apply {
            viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
                noteTopBar.title = (note.date.trimStart('0') + ", " + note.time)

                noteTopBar.setNavigationOnClickListener {
                    activity.onBackPressed()
                }
                noteTopBar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.edit -> {
                            editNoteTopBar.isVisible = true
                            noteTopBar.isVisible = false
                            activity.showFragment(EditNoteFragment.newInstance(), R.id.edit)
                            viewModel.changeLoadMode(true)
                            true
                        }

                        R.id.delete -> {
                            worker.showDeleteDialog(note, layoutInflater.inflate(R.layout.delete_dialog, null))
                            true
                        }

                        else -> false
                    }
                }
            }
        }
    }

    private fun disableAllFields() {
        binding.apply {
            dateLayout.isVisible = false
            timeLayout.isVisible = false
            worker.boxes.forEach {
                it.isVisible = false
            }
            addEmotionButton.isVisible = false

            situationText.isEnabled = false
            thoughtsText.isEnabled = false
            feelingsText.isEnabled = false
            actionsText.isEnabled = false
            answerText.isEnabled = false

            seekBarBefore.setOnTouchListener { _, _ -> true }
            seekBarBefore.setOnTouchListener { _, _ -> true }
        }
    }

    private fun showNote() {
        viewModel.selectedNote.observe(viewLifecycleOwner) {
            binding.apply {
                situationText.setText(it.situation)
                thoughtsText.setText(it.thoughts)

                worker.showFilledFields(it)

                showDistortions(it.distortions.split(";"))
            }
        }
        showEmotions()
    }

    private fun showEmotions() {
        viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
            viewModel.getNoteEmotionsById(note.id).observe(viewLifecycleOwner) { emotions ->
                emotions.forEach {
                    val chip = Chip(context)
                    chip.setChipBackgroundColorResource(R.color.light_gray)
                    chip.setChipStrokeColorResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
                    chip.setTextAppearance(R.style.ChipTextAppearance)
                    chip.text = it.name
                    chip.isEnabled = false
                    binding.selectedEmotions.addView(chip)

                }
            }
        }
    }

    private fun showDistortions(distortions: List<String>) {
        binding.distortionsText.apply {
            text = distortions.map { "•   $it" }.joinToString(separator = "\n")
            setLineSpacing(1F, 1.5F)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}
