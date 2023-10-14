package com.mawekk.sterdiary.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentNewNoteBinding
import com.mawekk.sterdiary.db.DiaryViewModel
import java.text.SimpleDateFormat


class NewNoteFragment : Fragment() {
    private lateinit var binding: FragmentNewNoteBinding
    private var boxes = mutableListOf<CheckBox>()
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy")
    private val timeFormat = SimpleDateFormat("HH:mm")
    private val viewModel: DiaryViewModel by activityViewModels()
    private var startEmotions = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewNoteBinding.inflate(layoutInflater, container, false)
        binding.apply {
            dateText.setText(dateFormat.format(System.currentTimeMillis()))
            timeText.setText(timeFormat.format(System.currentTimeMillis()))
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
        if (!startEmotions) {
            viewModel.selectEmotions(emptyList())
            startEmotions = true
        }
        setAddEmotionButton()
        setTopAppBarActions()
        showSelectedEmotions()
        addCheckBoxes()

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
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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

    private fun setAddEmotionButton() {
        val activity = activity as MainActivity
        binding.apply {
            addEmotionButton.setOnClickListener {
                activity.apply {
                    binding.emotionsTopBar.isVisible = true
                    binding.newNoteTopBar.isVisible = false
                    showFragment(
                        EmotionsFragment.newInstance(),
                        R.id.addEmotionButton
                    )
                }
            }
        }
    }

    private fun setTopAppBarActions() {
        val activity = activity as MainActivity
        activity.binding.newNoteTopBar.apply {
            setNavigationOnClickListener {
                activity.onBackPressed()
            }
            setOnMenuItemClickListener {
                val emotions = viewModel.selectedEmotions.value ?: emptyList()
                var id: Long
                viewModel.getMaxId().observe(viewLifecycleOwner) {
                    id = it ?: 0L
                    if (viewModel.saveNote(
                            viewModel.assembleNote(
                                binding,
                                id + 1,
                                checkSelectedDistortions().joinToString(separator = ";")
                            ),
                            emotions
                        )
                    ) {
                        activity.onBackPressed()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            R.string.all_fields_must_be_filled,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                true
            }
        }
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
        fun newInstance() = NewNoteFragment()
    }
}