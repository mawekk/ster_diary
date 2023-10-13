package com.mawekk.sterdiary.db.notes

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mawekk.sterdiary.db.notes.Note
import com.mawekk.sterdiary.db.notes.NoteDao

@Database(entities = [Note::class], version = 3)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}