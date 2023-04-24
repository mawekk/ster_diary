package com.mawekk.sterdiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mawekk.sterdiary.databinding.NoteBinding

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    val note = Note("meow", "kek")
    val notes = arrayListOf(note, note, note, note, note, note, note, note, note, note, note, note)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = NoteBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.note, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.date.text = notes[position].date
        holder.binding.situation.text = notes[position].situation
    }
}