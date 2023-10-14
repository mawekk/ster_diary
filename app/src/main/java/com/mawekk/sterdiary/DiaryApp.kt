package com.mawekk.sterdiary

import android.app.Application
import androidx.room.Room
import com.mawekk.sterdiary.db.emotions.Emotion
import com.mawekk.sterdiary.db.emotions.EmotionDatabase
import com.mawekk.sterdiary.db.notes.NoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DiaryApp : Application() {
    lateinit var noteDatabase: NoteDatabase
    lateinit var emotionDatabase: EmotionDatabase

    override fun onCreate() {
        super.onCreate()
        noteDatabase = Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java, "diary-notes-db"
        )
            .fallbackToDestructiveMigration().build()

        emotionDatabase = Room.databaseBuilder(
            applicationContext,
            EmotionDatabase::class.java, "diary-emotion-db"
        ).fallbackToDestructiveMigration().build()

        fillEmotionsDb()
    }

    private fun fillEmotionsDb() {
        MainScope().launch(Dispatchers.IO) {
            val defaultEmotions = resources.getStringArray(R.array.default_emotions)
            if (emotionDatabase.emotionDao().getEmotionsCount() == 0) {
                defaultEmotions.forEach {
                    emotionDatabase.emotionDao().addEmotion(Emotion(it))
                }
            }
        }
    }
}