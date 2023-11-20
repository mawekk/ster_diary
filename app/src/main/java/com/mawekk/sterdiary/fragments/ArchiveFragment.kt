package com.mawekk.sterdiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mawekk.sterdiary.DividerItem
import com.mawekk.sterdiary.MainActivity
import com.mawekk.sterdiary.NoteAdapter
import com.mawekk.sterdiary.NoteItem
import com.mawekk.sterdiary.R
import com.mawekk.sterdiary.RecyclerViewItem
import com.mawekk.sterdiary.databinding.FragmentArchiveBinding
import com.mawekk.sterdiary.db.DiaryViewModel
import java.text.SimpleDateFormat


class ArchiveFragment : Fragment() {
    private lateinit var binding: FragmentArchiveBinding
    private val viewModel: DiaryViewModel by activityViewModels()
    private val noteAdapter = NoteAdapter { item: NoteItem -> noteOnClick(item) }

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
            .observe(viewLifecycleOwner) {
                val items = mutableListOf<RecyclerViewItem>()
                val notes = it.sortedBy { note ->
                    val dateAndTimeString = note.date + ' ' + note.time
                    val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm")
                    val dateAndTime = formatter.parse(dateAndTimeString)
                    dateAndTime
                }

                if (notes.isNotEmpty()) {
                    items.add(NoteItem(notes[0]))
                    for (i in 1 until notes.size) {
                        val dividerText = notes[i - 1].date.drop(3)
                        val nextText = notes[i].date.drop(3)
                        if (dividerText != nextText) {
                            items.add(DividerItem(dividerText))
                        }
                        items.add(NoteItem(notes[i]))
                    }
                }

                noteAdapter.setList(items)
            }
    }

    private fun noteOnClick(item: NoteItem) {
        val activity = activity as MainActivity
        activity.binding.apply {
            topAppBar.isVisible = false
            noteTopBar.isVisible = true
        }
        activity.hideBottomNavigation()
        viewModel.selectNote(item.note)
        activity.showFragment(NoteFragment.newInstance(), R.id.addNoteButton)

    }

    companion object {
        @JvmStatic
        fun newInstance() = ArchiveFragment()
    }
}
