package com.mawekk.sterdiary.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentNewNoteBinding
import com.mawekk.sterdiary.db.DiaryViewModel
import java.text.SimpleDateFormat

class EditNoteFragment : Fragment() {
    private lateinit var binding: FragmentNewNoteBinding
    private var boxes = mutableListOf<CheckBox>()
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy")
    private val timeFormat = SimpleDateFormat("HH:mm")
    private val viewModel: DiaryViewModel by activityViewModels()

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
        setAddEmotionButton()
        addCheckBoxes()
        loadNote()
        viewModel.loadMode.observe(viewLifecycleOwner) {
            selectEmotionsAndDistortions(it)
            setTopAppBarActions(it)
        }
        showSelectedEmotions()
        return binding.root
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

        val timeSetListener =
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
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


    private fun setTopAppBarActions(isLoadMode: Boolean) {
        val activity = activity as MainActivity
        activity.binding.editNoteTopBar.apply {
            setNavigationOnClickListener {
                activity.onBackPressed()
                viewModel.changeLoadMode(false)
            }
            setOnMenuItemClickListener {
                selectEmotionsAndDistortions(isLoadMode)
                val emotions = viewModel.selectedEmotions.value ?: emptyList()
                val note =
                    viewModel.assembleNote(
                        binding,
                        viewModel.selectedNote.value!!.id,
                        checkSelectedDistortions().joinToString(separator = ";")
                    )
                if (viewModel.updateNote(note, emotions)) {
                    viewModel.selectNote(note)
                    viewModel.changeLoadMode(false)
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

    private fun loadNote() {
        viewModel.selectedNote.observe(viewLifecycleOwner) {
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
        viewModel.selectedEmotions.observe(viewLifecycleOwner) { emotions ->
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
                    viewModel.deselectEmotion(it)
                }

                binding.selectedEmotions.addView(chip)
            }
        }
    }

    private fun selectEmotionsAndDistortions(isLoadMode: Boolean) {
        if (isLoadMode) {
            viewModel.selectedNote.observe(viewLifecycleOwner) { note ->
                note.distortions.split(";").forEach { text ->
                    boxes.find { it.text.toString() == text }?.isChecked = true
                }
                viewModel.getNoteEmotionsById(note.id).observe(viewLifecycleOwner) {
                    viewModel.selectEmotions(it)
                }
            }
            viewModel.changeLoadMode(false)
        }
    }

    private fun checkSelectedDistortions(): List<String> =
        boxes.map { if (it.isChecked) it.text.toString() else "#" }.filter { it != "#" }

    private fun addCheckBoxes() {
        binding.apply {
            boxes.add(checkBox1)
            boxes.add(checkBox2)
            boxes.add(checkBox3)
            boxes.add(checkBox4)
            boxes.add(checkBox5)
            boxes.add(checkBox6)
            boxes.add(checkBox7)
            boxes.add(checkBox8)
            boxes.add(checkBox9)
            boxes.add(checkBox10)
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = EditNoteFragment()
    }
}