package com.mawekk.sterdiary.db.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class Note(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "time") var time: String,
    @ColumnInfo(name = "situation") var situation: String,
    @ColumnInfo(name = "discomfort_before") var discomfortBefore: String,
    @ColumnInfo(name = "thoughts") var thoughts: String,
    @ColumnInfo(name = "emotions")var emotions: String,
    @ColumnInfo(name = "feelings") var feelings: String,
    @ColumnInfo(name = "actions") var actions: String,
    //var distortions: String,
    @ColumnInfo(name = "answer") var answer: String,
    @ColumnInfo(name = "discomfort_after") var discomfortAfter: String
)
