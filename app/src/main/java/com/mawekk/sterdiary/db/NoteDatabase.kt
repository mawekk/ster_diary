package com.mawekk.sterdiary.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun getDao(): NoteDao

    companion object {
        private var database: NoteDatabase? = null

        @Synchronized
        fun getInstance(contex: Context): NoteDatabase {
            return if (database == null) {
                database =
                    Room.databaseBuilder(contex, NoteDatabase::class.java, "notes_db").build()
                database as NoteDatabase
            } else {
                database as NoteDatabase
            }
        }
    }
}