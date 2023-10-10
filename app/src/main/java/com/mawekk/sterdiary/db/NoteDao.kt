package com.mawekk.sterdiary.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert
    fun insertNote(note: Note)
    @Delete
    fun deleteNote(note: Note)
    @Query("SELECT * FROM notes_table")
    fun getAllNotes(): List<Note>
    @Query("SELECT * FROM notes_table WHERE id = :id")
    fun getNoteById(id: Int): Note
}