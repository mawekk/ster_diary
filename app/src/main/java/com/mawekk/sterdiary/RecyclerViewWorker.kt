package com.mawekk.sterdiary

import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mawekk.sterdiary.db.DiaryViewModel
import com.mawekk.sterdiary.db.entities.Note
import com.mawekk.sterdiary.fragments.NoteFragment
import java.text.SimpleDateFormat

class RecyclerViewWorker(
    private val recyclerView: RecyclerView,
    private val activity: FragmentActivity,
    private val viewModel: DiaryViewModel,
    private val owner: LifecycleOwner
) {
    private val noteAdapter = NoteAdapter { item: NoteItem -> noteOnClick(item) }

    fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = noteAdapter
        }
    }

    private fun sortNotes(notes: List<Note>): List<Note> {
        return notes.sortedBy { note ->
            val dateAndTimeString = note.date + ' ' + note.time
            val formatter = SimpleDateFormat("dd MMMM yyyy HH:mm")
            val dateAndTime = formatter.parse(dateAndTimeString)

            dateAndTime
        }
    }

    fun setRecyclerViewItemsForArchive() {
        viewModel.getAllNotes()
            .observe(owner) {
                val items = mutableListOf<RecyclerViewItem>()
                val sortedNotes = sortNotes(it)

                if (sortedNotes.isNotEmpty()) {
                    items.add(NoteItem(sortedNotes[0]))
                    for (i in 1 until sortedNotes.size) {
                        val dividerText = sortedNotes[i - 1].date.drop(3)
                        val nextText = sortedNotes[i].date.drop(3)
                        if (dividerText != nextText) {
                            items.add(DividerItem(dividerText))
                        }
                        items.add(NoteItem(sortedNotes[i]))
                    }
                }

                noteAdapter.setList(items)
            }
    }

    fun setRecyclerViewItemsForSearch() {
        viewModel.getAllNotes()
            .observe(owner) {
                val items = mutableListOf<RecyclerViewItem>()
                val sortedNotes = sortNotes(it)

                sortedNotes.forEach { note -> items.add(NoteItem(note)) }

                noteAdapter.setList(items)
            }
    }

    fun filterNotes(query: String?) {
        viewModel.getAllNotes()
            .observe(owner) {
                if (query != null) {
                    val filteredItems = mutableListOf<RecyclerViewItem>()
                    val sortedNotes = sortNotes(it)
                    if (sortedNotes.isNotEmpty()) {
                        sortedNotes.forEach { note ->
                            if (note.situation.lowercase().contains(query)) {
                                filteredItems.add(NoteItem(note))
                            }
                        }
                    }

                    noteAdapter.setList(filteredItems)
                }
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
}
