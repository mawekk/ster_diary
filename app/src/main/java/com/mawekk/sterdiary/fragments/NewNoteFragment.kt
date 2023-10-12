package com.mawekk.sterdiary.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentNewNoteBinding
import java.text.SimpleDateFormat


class NewNoteFragment : Fragment() {
    lateinit var binding: FragmentNewNoteBinding
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy")
    private val timeFormat = SimpleDateFormat("HH:mm")
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
            setAddEmotionButton()
        }
        return binding.root
    }

    private fun setAddEmotionButton() {
        binding.apply {
            addEmotionButton.setOnClickListener {
                (activity as MainActivity).findViewById<MaterialToolbar>(R.id.emotionsTopBar).isVisible = true
                (activity as MainActivity).findViewById<MaterialToolbar>(R.id.newNoteTopBar).isVisible = false
                (activity as MainActivity).showFragment(
                    EmotionsFragment.newInstance(),
                    R.id.addEmotionButton
                )
            }
        }
    }


    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)

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

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
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

    companion object {
        @JvmStatic
        fun newInstance() = NewNoteFragment()
    }
}