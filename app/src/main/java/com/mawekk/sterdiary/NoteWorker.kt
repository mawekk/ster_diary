package com.mawekk.sterdiary

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.chip.Chip
import com.mawekk.sterdiary.databinding.FragmentNewNoteBinding
import com.mawekk.sterdiary.db.DiaryViewModel
import com.mawekk.sterdiary.db.entities.Note
import com.mawekk.sterdiary.fragments.EmotionsFragment
import java.text.SimpleDateFormat

class NoteWorker(
    private val fragmentActivity: FragmentActivity,
    private val activity: MainActivity,
    private val viewModel: DiaryViewModel,
    private val owner: LifecycleOwner,
    private val binding: FragmentNewNoteBinding
) {
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy")
    private val timeFormat = SimpleDateFormat("HH:mm")
    lateinit var boxes: List<CheckBox>


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
            fragmentActivity,
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
            fragmentActivity,
            R.style.Dialog_Theme,
            timeSetListener,
            hour,
            minute,
            true
        )
            .show()
    }

    fun setDateAndTime() {
        binding.apply {
            dateText.setText(dateFormat.format(System.currentTimeMillis()))
            timeText.setText(timeFormat.format(System.currentTimeMillis()))
        }
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

    fun setActions() {
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

            distortionsText.isVisible = false

            setAddEmotionButton()
            setDistortions()
            setStructure()
        }
    }

    private fun setAddEmotionButton() {
        binding.apply {
            addEmotionButton.setOnClickListener {
                activity.apply {
                    binding.emotionsTopBar.isVisible = true
                    binding.editNoteTopBar.isVisible = false
                    binding.newNoteTopBar.isVisible = false
                    showFragment(
                        EmotionsFragment.newInstance(),
                        R.id.addEmotionButton
                    )
                }
            }
        }
    }

    fun setDistortions() {
        binding.apply {
            boxes = listOf(
                checkBox1,
                checkBox2,
                checkBox3,
                checkBox4,
                checkBox5,
                checkBox6,
                checkBox7,
                checkBox8,
                checkBox9
            )
            val distortionsNames =
                fragmentActivity.resources.getStringArray(R.array.cognitive_distortions_names)
            distortionsNames.zip(boxes) { name, box -> box.text = name }
        }
    }

    fun showSelectedEmotions(context: Context?) {
        viewModel.selectedEmotions.observe(owner) { emotions ->
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

    fun checkSelectedDistortions(): List<String> =
        boxes.map { if (it.isChecked) it.text.toString() else "#" }.filter { it != "#" }

    fun showBackDialog(dialogLayout: View) {
        val builder = AlertDialog.Builder(activity, R.style.Dialog_Theme)
        builder.setView(dialogLayout)
        builder.setTitle(R.string.back_dialog_title)

        val dialog = builder.create()
        val okButton = dialogLayout.findViewById<Button>(R.id.okButton)
        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancelBackButton)

        okButton.setOnClickListener {
            activity.onBackPressed()
            viewModel.changeLoadMode(false)
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnShowListener {
            val titleId = fragmentActivity.resources.getIdentifier("alertTitle", "id", "android")
            val dialogTitle = dialog.findViewById<View>(titleId) as TextView
            dialogTitle.setTextColor(ContextCompat.getColor(activity, R.color.dark_blue))
        }
        dialog.show()
    }

    fun showHelpDialog(dialogLayout: View) {
        val builder = AlertDialog.Builder(activity, R.style.Dialog_Theme)
        builder.setView(dialogLayout)
        builder.setTitle(R.string.help)

        val dialog = builder.create()
        val okButton = dialogLayout.findViewById<Button>(R.id.closeHelpButton)

        okButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnShowListener {
            val titleId = fragmentActivity.resources.getIdentifier("alertTitle", "id", "android")
            val dialogTitle = dialog.findViewById<View>(titleId) as TextView
            dialogTitle.setTextColor(ContextCompat.getColor(activity, R.color.dark_blue))
        }
        dialog.show()
    }

    private fun setStructure() {
        val settings = activity.getSharedPreferences(TAG, Context.MODE_PRIVATE)

        val level = settings.getBoolean(STRUCTURE[0], true)
        val feelings = settings.getBoolean(STRUCTURE[1], true)
        val actions = settings.getBoolean(STRUCTURE[2], true)
        val answer = settings.getBoolean(STRUCTURE[3], true)
        binding.apply {
            levelBefore.isVisible = level
            levelBeforeLayout.isVisible = level
            levelAfter.isVisible = level
            levelAfterLayout.isVisible = level
            feelingsLayout.isVisible = feelings
            actionsLayout.isVisible = actions
            answerLayout.isVisible = answer
        }
    }

    fun clearStructure() {
        binding.apply {
            levelBefore.isVisible = false
            levelBeforeLayout.isVisible = false
            levelAfter.isVisible = false
            levelAfterLayout.isVisible = false
            feelingsLayout.isVisible = false
            actionsLayout.isVisible = false
            answerLayout.isVisible = false
        }
    }

    fun showDeleteDialog(note: Note, dialogLayout: View) {
        val builder = AlertDialog.Builder(activity, R.style.Dialog_Theme)
        builder.setView(dialogLayout)
        builder.setTitle(R.string.delete_dialog_title)


        val dialog = builder.create()
        val deleteButton = dialogLayout.findViewById<Button>(R.id.deleteButton)
        val cancelButton = dialogLayout.findViewById<Button>(R.id.cancelDelButton)

        deleteButton.setOnClickListener {
            activity.binding.noteTopBar.isVisible = false
            viewModel.deleteNote(note)
            activity.onBackPressed()
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnShowListener {
            val titleId = fragmentActivity.resources.getIdentifier("alertTitle", "id", "android")
            val dialogTitle = dialog.findViewById<View>(titleId) as TextView
            dialogTitle.setTextColor(ContextCompat.getColor(activity, R.color.dark_blue))
        }
        dialog.show()
    }

    fun showFilledFields(note: Note) {
        binding.apply {
            note.apply {
                if (discomfortBefore != "0%") {
                    seekBarBefore.progress = discomfortBefore.dropLast(1).toInt()
                    seekBarAfter.progress = discomfortAfter.dropLast(1).toInt()
                    percentsBefore.text = discomfortBefore
                    percentsAfter.text = discomfortAfter
                    levelBefore.isVisible = true
                    levelBeforeLayout.isVisible = true
                    levelAfter.isVisible = true
                    levelAfterLayout.isVisible = true
                }
                if (feelings != "") {
                    feelingsText.setText(feelings)
                    feelingsLayout.isVisible = true
                }
                if (actions != "") {
                    actionsText.setText(actions)
                    actionsLayout.isVisible = true
                }
                if (answer != "") {
                    answerText.setText(answer)
                    answerLayout.isVisible = true
                }
            }
        }
    }
}
