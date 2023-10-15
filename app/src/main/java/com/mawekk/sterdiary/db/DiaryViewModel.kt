package com.mawekk.sterdiary.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mawekk.sterdiary.DiaryApp
import com.mawekk.sterdiary.databinding.FragmentNewNoteBinding
import com.mawekk.sterdiary.db.dao.EmotionDao
import com.mawekk.sterdiary.db.dao.NoteDao
import com.mawekk.sterdiary.db.entities.Emotion
import com.mawekk.sterdiary.db.entities.Note
import com.mawekk.sterdiary.db.entities.NoteEmotion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val noteDao: NoteDao
    private val mutableSelectedNote = MutableLiveData<Note>()
    val selectedNote: LiveData<Note> get() = mutableSelectedNote

    private val emotionDao: EmotionDao
    private val mutableSelectedEmotions = MutableLiveData<List<Emotion>>()
    private val mutableEditMode = MutableLiveData(false)
    private val mutableLoadMode = MutableLiveData(false)
    val selectedEmotions: LiveData<List<Emotion>> get() = mutableSelectedEmotions
    val editMode: LiveData<Boolean> get() = mutableEditMode
    val loadMode: LiveData<Boolean> get() = mutableLoadMode

    init {
        val app = application as DiaryApp
        noteDao = app.diaryDatabase.noteDao()
        emotionDao = app.diaryDatabase.emotionDao()
    }

    fun changeLoadMode(value: Boolean) {
        mutableLoadMode.value = value
    }

    fun selectNote(note: Note) {
        mutableSelectedNote.value = note
    }

    fun getAllNotes(): LiveData<List<Note>> {
        return noteDao.getAllNotes()
    }

    private fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.addNote(note)
        }
    }

    private fun addEmotionToNote(noteEmotion: NoteEmotion) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.addEmotionToNote(noteEmotion)
        }
    }

    fun deleteNote(note: Note) {
        deleteNoteEmotionsById(note.id)
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.deleteNote(note)
        }
    }

    private fun checkNote(note: Note): Boolean {
        note.apply {
            return (situation.isNotEmpty() && thoughts.isNotEmpty() && feelings.isNotEmpty()
                    && actions.isNotEmpty() && answer.isNotEmpty() && distortions.isNotEmpty())
        }
    }

    fun assembleNote(
        binding: FragmentNewNoteBinding, id: Long, distortions: String
    ): Note {
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
                feelings,
                actions,
                distortions,
                answer,
                discomfortAfter
            )
        }
    }

    private fun updateNoteEmotions(noteId: Long, emotions: List<Emotion>) {
        deleteNoteEmotionsById(noteId)
        emotions.forEach {
            addEmotionToNote(NoteEmotion(0, noteId, it.name))
        }
    }

    fun saveNote(note: Note, emotions: List<Emotion>): Boolean {
        return if (checkNote(note)) {
            addNote(note)
            updateNoteEmotions(note.id, emotions)
            true
        } else {
            false
        }
    }

    fun updateNote(note: Note, emotions: List<Emotion>): Boolean {
        return if (checkNote(note)) {
            viewModelScope.launch(Dispatchers.IO) {
                noteDao.updateNote(note)
            }
            updateNoteEmotions(note.id, emotions)
            true
        } else {
            false
        }
    }

    fun getNoteEmotionsById(id: Long): LiveData<List<Emotion>> {
        return noteDao.getNoteEmotionsById(id)
    }

    private fun deleteNoteEmotionsById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDao.deleteNoteEmotionsById(id)
        }
    }

    fun getMaxId(): LiveData<Long> {
        return noteDao.getMaxId()
    }

    fun selectEmotions(emotions: List<Emotion>) {
        mutableSelectedEmotions.value = emotions
    }

    fun changeEditMode(value: Boolean) {
        mutableEditMode.value = value
    }

    fun deselectEmotion(emotion: Emotion) {
        mutableSelectedEmotions.value =
            mutableSelectedEmotions.value?.filter { it.name != emotion.name }
    }

    fun isEmotionSelected(emotion: Emotion): Boolean {
        return mutableSelectedEmotions.value?.contains(emotion) ?: false
    }

    fun getAllEmotions(): LiveData<List<Emotion>> {
        return emotionDao.getAllEmotions()
    }

    fun addEmotion(emotion: Emotion) {
        viewModelScope.launch(Dispatchers.IO) {
            emotionDao.addEmotion(emotion)
        }
    }

    fun deleteEmotion(emotion: Emotion) {
        viewModelScope.launch(Dispatchers.IO) {
            emotionDao.deleteEmotion(emotion)
            noteDao.deleteNoteEmotionsByName(emotion.name)
        }
    }

    fun getEmotionsCount(): Int {
        return emotionDao.getEmotionsCount()
    }
}