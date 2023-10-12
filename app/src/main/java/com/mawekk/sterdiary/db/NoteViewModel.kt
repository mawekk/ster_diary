package com.mawekk.sterdiary.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mawekk.sterdiary.DiaryApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao: NoteDao
    private val mutableSelectedNote = MutableLiveData<Note>()
    val selectedNote: LiveData<Note> get() = mutableSelectedNote

    init {
        noteDao = (application as DiaryApp).db.noteDao()
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
}