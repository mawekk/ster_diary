package com.mawekk.sterdiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.NoteAdapter
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.databinding.FragmentArchiveBinding
import com.mawekk.sterdiary.db.entities.Note
import com.mawekk.sterdiary.db.viewmodels.NoteViewModel


class ArchiveFragment : Fragment() {
    private lateinit var binding: FragmentArchiveBinding
    private val viewModel: NoteViewModel by activityViewModels()
    private val noteAdapter = NoteAdapter { note: Note -> noteOnClick(note) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArchiveBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }

    private fun initRecyclerView() {
        binding.noteRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = noteAdapter
        }
        viewModel.getAllNotes()
            .observe(viewLifecycleOwner) { noteAdapter.setList(it) }
    }

    private fun noteOnClick(note: Note) {
        val activity = activity as MainActivity
        activity.binding.apply {
            topAppBar.isVisible = false
            noteTopBar.isVisible = true
        }
        activity.hideBottomNavigation()
        viewModel.selectNote(note)
        activity.showFragment(NoteFragment.newInstance(), R.id.addNoteButton)
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArchiveFragment()
    }
}