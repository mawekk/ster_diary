package com.mawekk.sterdiary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mawekk.sterdiary.databinding.DividerBinding
import com.mawekk.sterdiary.databinding.NoteBinding
import com.mawekk.sterdiary.db.entities.Note

open class RecyclerViewItem
class NoteItem(val note: Note) : RecyclerViewItem() 
class DividerItem(val text: String) : RecyclerViewItem()

class NoteAdapter(val listener: (NoteItem) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data = emptyList<RecyclerViewItem>()

    private val viewTypeNote = 1
    private val viewTypeDivider = 2

    override fun getItemViewType(position: Int): Int {
        if (data[position] is NoteItem) {
            return viewTypeNote
        }
        return viewTypeDivider
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = NoteBinding.bind(itemView)
        fun bind(item: NoteItem) {
            binding.apply {
                val dateString = item.note.date.trimStart('0')
                date.text = dateString.dropLast(5) + ", " + item.note.time
                situation.text = item.note.situation
            }
        }
    }

    class DividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = DividerBinding.bind(itemView)
        fun bind(item: DividerItem) {
            binding.divider.text = item.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            viewTypeNote -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.note, parent, false)
                NoteViewHolder(itemView)
            }

            else -> {
                val itemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.divider, parent, false)
                DividerViewHolder(itemView)
            }
        }
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = data[position]
        if (holder is NoteViewHolder && item is NoteItem) {
            holder.itemView.setOnClickListener { listener(item) }
            holder.bind(item)
        }
        if (holder is DividerViewHolder && item is DividerItem) {
            holder.bind(item)
        }
    }
    
    fun setList(list: List<RecyclerViewItem>) {
        data = list.reversed()
        notifyDataSetChanged()
    }
}
