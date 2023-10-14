package com.mawekk.sterdiary.db.emotions

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EmotionDao {
    @Insert
    suspend fun addEmotion(emotion: Emotion)

    @Delete
    suspend fun deleteEmotion(emotion: Emotion)

    @Query("SELECT * FROM emotions_table")
    fun getAllEmotions(): LiveData<List<Emotion>>

    @Query("SELECT * FROM emotions_table WHERE name IN (:emotionsNames)")
    fun findEmotionsByNames(emotionsNames: List<String>): LiveData<List<Emotion>>

    @Query("SELECT COUNT(*) FROM emotions_table")
    fun getEmotionsCount(): Int
}