package com.mawekk.sterdiary.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.NoteWorker
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentNewNoteBinding
import com.mawekk.sterdiary.db.DiaryViewModel


class NewNoteFragment : Fragment() {
    private lateinit var binding: FragmentNewNoteBinding
    private lateinit var worker: NoteWorker
    private val viewModel: DiaryViewModel by activityViewModels()
    private var startEmotions = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewNoteBinding.inflate(layoutInflater, container, false)
        worker = NoteWorker(
            requireActivity(),
            activity as MainActivity,
            viewModel,
            viewLifecycleOwner,
            binding
        )

        worker.setDateAndTime()
        worker.setActions()
        worker.showSelectedEmotions(context)

        setTopAppBarActions()

        if (!startEmotions) {
            viewModel.selectEmotions(emptyList())
            startEmotions = true
        }

        return binding.root
    }

    private fun setTopAppBarActions() {
        val activity = activity as MainActivity
        activity.binding.newNoteTopBar.apply {
            setNavigationOnClickListener {
                worker.showBackDialog(layoutInflater.inflate(R.layout.back_dialog, null))
            }
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.save_item -> {
                        val emotions = viewModel.selectedEmotions.value ?: emptyList()
                        val distortions =
                            worker.checkSelectedDistortions().joinToString(separator = ";")
                        var id: Long
                        viewModel.getMaxId().observe(viewLifecycleOwner) {
                            id = it ?: 0L
                            if (viewModel.saveNote(
                                    viewModel.assembleNote(
                                        binding,
                                        id + 1,
                                        distortions
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
                    }

                    R.id.help_item -> worker.showHelpDialog(
                        layoutInflater.inflate(
                            R.layout.help,
                            null
                        )
                    )
                }
                true
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NewNoteFragment()
    }
}
