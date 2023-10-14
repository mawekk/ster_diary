package com.mawekk.sterdiary.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentNewNoteBinding
import com.mawekk.sterdiary.db.viewmodels.EmotionViewModel
import com.mawekk.sterdiary.db.viewmodels.NoteViewModel
import java.text.SimpleDateFormat

class EditNoteFragment : Fragment() {
    private lateinit var binding: FragmentNewNoteBinding
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy")
    private val timeFormat = SimpleDateFormat("HH:mm")
    private val noteViewModel: NoteViewModel by activityViewModels()
    private val emotionViewModel: EmotionViewModel by activityViewModels()
    private var noteLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        binding.apply {
            dateText.setOnClickListener {
                showDatePickerDialog()
            }
            timeText.setOnClickListener {
                showTimePickerDialog()
            }
            showSeekBarProgress(seekBarBefore, percentsBefore)
            showSeekBarProgress(seekBarAfter, percentsAfter)
            percentsBefore.text = "0%"
            percentsAfter.text = "0%"
        }
        setTopAppBarActions()
        loadNote()
        selectEmotions()
        showSelectedEmotions()
        setAddEmotionButton()
        return binding.root
    }

    private fun selectEmotions() {
        if (!noteLoaded) {
            noteViewModel.selectedNote.observe(viewLifecycleOwner) { note ->
                noteViewModel.getNoteEmotionsById(note.id).observe(viewLifecycleOwner) {
                    emotionViewModel.selectEmotions(it)
                }
            }
            noteLoaded = true
        }
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                binding.dateText.setText(dateFormat.format(calendar.time))
            }

        DatePickerDialog(
            requireActivity(),
            R.style.Dialog_Theme,
            dateSetListener,
            year,
            month,
            day
        ).show()

    }

    private fun showTimePickerDialog() {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            binding.timeText.setText(timeFormat.format(calendar.time))
        }

        TimePickerDialog(
            requireActivity(),
            R.style.Dialog_Theme,
            timeSetListener,
            hour,
            minute,
            true
        )
            .show()
    }


    private fun showSeekBarProgress(seekBar: SeekBar, textView: TextView) {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStopTrackingTouch(bar: SeekBar) {}
            override fun onStartTrackingTouch(bar: SeekBar) {}
            override fun onProgressChanged(
                bar: SeekBar,
                progress: Int, fromUser: Boolean
            ) {
                textView.text = "$progress%"
            }
        })
    }

    private fun loadNote() {
        noteViewModel.selectedNote.observe(viewLifecycleOwner) {
            binding.apply {
                dateText.setText(it.date)
                timeText.setText(it.time)
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
        showSelectedEmotions()
    }

    private fun showSelectedEmotions() {
        emotionViewModel.selectedEmotions.observe(viewLifecycleOwner) { emotions ->
            binding.selectedEmotions.removeAllViews()
            emotions.forEach {
                val chip = Chip(context)
                chip.setChipBackgroundColorResource(R.color.light_gray)
                chip.setChipStrokeColorResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)
                chip.setTextAppearance(R.style.ChipTextAppearance)
                chip.text = it.name
                chip.isCloseIconVisible = true

                chip.setOnCloseIconClickListener { view ->
                    binding.selectedEmotions.removeView(view)
                    emotionViewModel.deselectEmotion(it)
                }

                binding.selectedEmotions.addView(chip)

            }
        }
    }

    private fun setTopAppBarActions() {
        val activity = activity as MainActivity
        activity.binding.editNoteTopBar.apply {
            setNavigationOnClickListener {
                activity.onBackPressed()
            }
            setOnMenuItemClickListener {
                selectEmotions()
                val emotions = emotionViewModel.selectedEmotions.value ?: emptyList()
                val note =
                    noteViewModel.assembleNote(binding, noteViewModel.selectedNote.value!!.id)
                if (noteViewModel.updateNote(note, emotions)) {
                    noteViewModel.selectNote(note)
                    activity.onBackPressed()
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.all_fields_must_be_filled,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                true
            }
        }
    }

    private fun setAddEmotionButton() {
        val activity = activity as MainActivity
        binding.apply {
            addEmotionButton.setOnClickListener {
                activity.apply {
                    binding.emotionsTopBar.isVisible = true
                    binding.editNoteTopBar.isVisible = false
                    showFragment(
                        EmotionsFragment.newInstance(),
                        R.id.addEmotionButton
                    )
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditNoteFragment()
    }
}