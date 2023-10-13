package com.mawekk.sterdiary.db.emotions

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Emotion::class], version = 2)
abstract class EmotionDatabase: RoomDatabase() {
    abstract fun emotionDao(): EmotionDao
}
