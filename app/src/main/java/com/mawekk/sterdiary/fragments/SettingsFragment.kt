package com.mawekk.sterdiary.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.STRUCTURE
import com.mawekk.sterdiary.TAG
import com.mawekk.sterdiary.THEME
import com.mawekk.sterdiary.databinding.FragmentSettingsBinding
import com.mawekk.sterdiary.db.DiaryViewModel



class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var boxes: List<CheckBox>
    private val viewModel: DiaryViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding.apply {
            boxes = listOf(levelBox, feelingsBox, actionsBox, answerBox)
        }

        setSavedSettings()
        setTopAppBarActions()
        setEditEmotionsButton()
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        saveChanges()
    }

    private fun setSavedSettings() {
        val settings = activity?.getSharedPreferences(TAG, Context.MODE_PRIVATE)

        if (settings != null) {
            val theme = settings.getString(THEME, "system")
            val structure = mutableListOf<Boolean>()
            STRUCTURE.forEach {
                structure.add(settings.getBoolean(it, true))
            }
            binding.apply {
                when (theme) {
                    "system" -> systemTheme.isChecked = true
                    "light" -> lightTheme.isChecked = true
                    "dark" -> darkTheme.isChecked = true
                }
                structure.forEachIndexed { index, value ->
                    boxes[index].isChecked = value
                }
            }
        }
    }

    private fun saveChanges() {
        val settings = activity?.getSharedPreferences(TAG, Context.MODE_PRIVATE)
        if (settings != null) {
            val editor = settings.edit()

            val theme = when (binding.themeGroup.checkedRadioButtonId) {
                R.id.lightTheme -> "light"
                R.id.darkTheme -> "dark"
                else -> "system"
            }

            editor.apply {
                putString(THEME, theme)
                boxes.forEachIndexed { index, box ->
                    putBoolean(STRUCTURE[index], box.isChecked)
                }
                apply()
            }
        }
    }

    private fun setEditEmotionsButton() {
        val activity = activity as MainActivity
        binding.apply {
            editEmotionsButton.setOnClickListener {
                activity.apply {
                    binding.emotionsTopBar.isVisible = true
                    binding.settingsTopBar.isVisible = false
                    viewModel.changeEditMode(true)
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
        activity.binding.settingsTopBar.apply {
            setOnMenuItemClickListener {
                activity.onBackPressed()
                true
            }
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}
