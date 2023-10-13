package com.mawekk.sterdiary.db.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mawekk.sterdiary.DiaryApp
import com.mawekk.sterdiary.databinding.FragmentNewNoteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao: NoteDao
    private val mutableSelectedNote = MutableLiveData<Note>()
    val selectedNote: LiveData<Note> get() = mutableSelectedNote

    init {
        noteDao = (application as DiaryApp).noteDatabase.noteDao()
    }

    fun selectNote(note: Note) {
        mutableSelectedNote.value = note
    }

    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNotes()
    }

    fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.addNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.deleteNote(note)
        }
    }

    private fun checkNote(note: Note): Boolean {
//        note.apply {
//            return (situation.isNotEmpty() && thoughts.isNotEmpty() && feelings.isNotEmpty()
//                    && actions.isNotEmpty() && answer.isNotEmpty() && emotions.isNotEmpty())
//        }
        return true
    }

    fun assembleNote(binding: FragmentNewNoteBinding, emotions: String, id: Int): Note {
        binding.apply {
            val date = dateText.text.toString()
            val time = timeText.text.toString()
            val situation = situationText.text.toString()
            val discomfortBefore = percentsBefore.text.toString()
            val thoughts = thoughtsText.text.toString()
            val feelings = feelingsText.text.toString()
            val actions = actionsText.text.toString()
            val answer = answerText.text.toString()
            val discomfortAfter = percentsAfter.text.toString()

            return Note(
                id,
                date,
                time,
                situation,
                discomfortBefore,
                thoughts,
                emotions,
                feelings,
                actions,
                answer,
                discomfortAfter
            )
        }
    }

    fun saveNote(note: Note): Boolean {

        return if (checkNote(note)) {
            addNote(note)
            true
        } else {
            false
        }
    }

    fun updateNote(note: Note): Boolean {
        return if (checkNote(note)) {
            viewModelScope.launch(Dispatchers.IO) {
                noteDao.updateNote(note)
            }
            true
        } else {
            false
        }
    }
}