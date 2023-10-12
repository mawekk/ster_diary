package com.mawekk.sterdiary

import android.app.Application
import androidx.room.Room
import com.mawekk.sterdiary.db.NoteDatabase

class DiaryApp : Application() {
    lateinit var db: NoteDatabase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java, "diary-db"
        )
            .fallbackToDestructiveMigration().build()
    }
}