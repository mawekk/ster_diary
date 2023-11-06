package com.mawekk.sterdiary.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentNoteBinding
import com.mawekk.sterdiary.db.DiaryViewModel
import com.mawekk.sterdiary.db.entities.Note


class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNoteBinding
    private val viewModel: DiaryViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        setTopAppBarActions()
        disableAllFields()
        showNote()
        return binding.root
    }

    private fun setTopAppBarActions() {
        val activity = activity as MainActivity
        activity.binding.apply {
            viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
                noteTopBar.title = (note.date + ", " + note.time)

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
                            showDeleteDialog(activity, note)
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
                feelingsText.setText(it.feelings)
                actionsText.setText(it.actions)
                answerText.setText(it.answer)
                seekBarBefore.progress = it.discomfortBefore.dropLast(1).toInt()
                seekBarAfter.progress = it.discomfortAfter.dropLast(1).toInt()
                percentsBefore.text = it.discomfortBefore
                percentsAfter.text = it.discomfortAfter
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
                    binding.emotionsGroup.addView(chip)

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

    private fun showDeleteDialog(activity: MainActivity, note: Note) {
        val builder = AlertDialog.Builder(activity)
        val dialogLayout = layoutInflater.inflate(R.layout.delete_dialog, null)
        builder.setView(dialogLayout)
        builder.setTitle(R.string.delete_dialog_title)


        val dialog = builder.create()
        val deleteButton = dialogLayout.findViewById<Button>(R.id.deleteButton)
        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancelDelButton)

        deleteButton.setOnClickListener {
            activity.binding.editNoteTopBar.isVisible = false
            viewModel.deleteNote(note)
            activity.onBackPressed()
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnShowListener {
            val titleId = resources.getIdentifier("alertTitle", "id", "android")
            val dialogTitle = dialog.findViewById<View>(titleId) as TextView
            dialogTitle.setTextColor(ContextCompat.getColor(activity, R.color.dark_blue))
        }
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}
