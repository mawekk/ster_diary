package com.mawekk.sterdiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mawekk.sterdiary.databinding.NoteBinding
import com.mawekk.sterdiary.db.entities.Note

class NoteAdapter(val listener: (Note) -> Unit) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private var notes = emptyList<Note>()
    private lateinit var onClickListener: OnClickListener

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = NoteBinding.bind(itemView)
        fun bind(nextNote: Note, previousNote: Note?) {
            binding.apply {
                val previousDateString = when (previousNote) {
                    null -> ""
                    else -> previousNote.date.drop(3)
                }
                val nextDateString = nextNote.date.trimStart('0')
                date.text = nextDateString.dropLast(5) + ", " + nextNote.time
                situation.text = nextNote.situation
                divider.text = nextNote.date.drop(3)
                divider.isVisible =
                    !(divider.text.toString() == previousDateString || previousNote == null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.note, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val previousNote = when (position > 0) {
            true -> notes[position - 1]
            else -> null
        }
        holder.bind(notes[position], previousNote)
        holder.itemView.setOnClickListener { listener(notes[position]) }
    }

    fun setList(list: List<Note>) {
        notes = list.reversed()
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(note: Note)
    }

}
