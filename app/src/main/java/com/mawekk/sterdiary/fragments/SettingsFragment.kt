package com.mawekk.sterdiary.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentSettingsBinding
import com.mawekk.sterdiary.db.NoteViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: NoteViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        setTopAppBarActions()
        setEditEmotionsButton()
        return binding.root
    }

    private fun setEditEmotionsButton() {
        val activity = activity as MainActivity
        binding.apply {
            editEmotionsButton.setOnClickListener {
                activity.apply {
                    binding.emotionsTopBar.isVisible = true
                    binding.settingsTopBar.isVisible = false
                    viewModel.changeMode(true)
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