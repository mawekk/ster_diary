package com.mawekk.sterdiary.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.sql.Time

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "date")var date: String,
    @ColumnInfo(name = "time") var time: String,
    @ColumnInfo(name = "situation")var situation: String,
    /*var discomfortBefore: Int,
    var thoughts: String,
    var emotions: List<String>,
    var feelings: String,
    var actions: String,
    var distortions: List<String>,
    var answer: String,
    var discomfortAfter: Int*/
)
