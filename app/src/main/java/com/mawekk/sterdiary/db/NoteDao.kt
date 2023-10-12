package com.mawekk.sterdiary.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert
    suspend fun addNote(note: Note)
    @Delete
    suspend fun deleteNote(note: Note)
    @Query("SELECT * FROM notes_table")
    fun getAllNotes(): LiveData<List<Note>>
    @Query("SELECT * FROM notes_table WHERE id = :id")
    fun getNoteById(id: Int): Note
}