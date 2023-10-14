package com.mawekk.sterdiary.db.notes

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 3)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}