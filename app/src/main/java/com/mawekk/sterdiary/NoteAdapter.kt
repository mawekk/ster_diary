package com.mawekk.sterdiary

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mawekk.sterdiary.databinding.NoteBinding
import com.mawekk.sterdiary.db.Note

class NoteAdapter(val listener: (Note) -> Unit) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    var notes = emptyList<Note>()
    private lateinit var onClickListener: OnClickListener

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = NoteBinding.bind(itemView)
        fun bind(note: Note) {
            binding.apply {
                date.text = note.time + ", " + note.date
                situation.text = note.situation
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.note, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notes[position])
        holder.itemView.setOnClickListener { listener(notes[position]) }
    }

    fun setList(list: List<Note>) {
        notes = list
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(note: Note)
    }

}