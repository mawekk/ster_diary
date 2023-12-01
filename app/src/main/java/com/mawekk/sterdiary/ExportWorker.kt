package com.mawekk.sterdiary

import android.content.Context
import android.os.Environment
import androidx.lifecycle.LifecycleOwner
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.mawekk.sterdiary.db.DiaryViewModel
import com.mawekk.sterdiary.db.entities.Note
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class ExportWorker(
    private val startDate: Date,
    private val endDate: Date,
    private val viewModel: DiaryViewModel,
    private val owner: LifecycleOwner,
    private val context: Context
) {
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy")
    private val fileName =
            dateFormat.format(System.currentTimeMillis()).replace(" ", "_")

    private fun getNotesToExport(notes: List<Note>): MutableList<Note> {
        val notesToExport = mutableListOf<Note>()
        notes.forEach {
            val date = dateFormat.parse(it.date)
            if (startDate <= date && date <= endDate) {
                notesToExport.add(it)
            }
        }

        return notesToExport
    }

    private fun getFile(fileName: String): File {
        return File(
            context.getExternalFilesDir(
                Environment.DIRECTORY_DOCUMENTS
            ), fileName
        )
    }

    fun exportToCSV(): File {
        val file = getFile("$fileName.csv")
        val res = context.resources

        viewModel.getAllNotes().observe(owner) {
            val notes = getNotesToExport(it)

            val headers = listOf(
                res.getString(R.string.date),
                res.getString(R.string.time),
                res.getString(R.string.situation),
                res.getString(R.string.discomfort_before),
                res.getString(R.string.thoughts),
                res.getString(R.string.emotions).dropLast(1),
                res.getString(R.string.distortions).dropLast(1),
                res.getString(R.string.discomfort_after)
            )

            csvWriter().open(file) {
                writeRow(headers)
                notes.forEach { note ->
                    writeRow(noteToList(note))
                }
            }
        }

        return file
    }

    private fun noteToList(note: Note): List<String> {
        var emotionsString = ""
        viewModel.getNoteEmotionsById(note.id).value?.forEach {
            emotionsString += it.name + " "
        }
        emotionsString.dropLast(1)

        note.apply {
            return listOf(
                date,
                time,
                situation,
                discomfortBefore,
                thoughts,
                emotionsString,
                distortions,
                discomfortAfter
            )
        }
    }
}