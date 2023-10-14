package com.mawekk.sterdiary.db.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mawekk.sterdiary.DiaryApp
import com.mawekk.sterdiary.db.dao.EmotionDao
import com.mawekk.sterdiary.db.entities.Emotion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class EmotionViewModel(application: Application) : AndroidViewModel(application) {
    private val emotionDao: EmotionDao
    private val mutableSelectedEmotions = MutableLiveData<List<Emotion>>()
    private val mutableEditMode = MutableLiveData(false)
    val selectedEmotions: LiveData<List<Emotion>> get() = mutableSelectedEmotions
    val editMode: LiveData<Boolean> get() = mutableEditMode

    init {
        emotionDao = (application as DiaryApp).diaryDatabase.emotionDao()
    }

    fun selectEmotions(emotions: List<Emotion>) {
        mutableSelectedEmotions.value = emotions
    }

    fun changeMode(value: Boolean) {
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
        }
    }

    fun getEmotionsCount(): Int {
        return emotionDao.getEmotionsCount()
    }
}