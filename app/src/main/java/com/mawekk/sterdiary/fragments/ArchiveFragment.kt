package com.mawekk.sterdiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mawekk.sterdiary.DiaryApp
import com.mawekk.sterdiary.NoteAdapter
import com.mawekk.sterdiary.databinding.FragmentArchiveBinding
import com.mawekk.sterdiary.db.Note

class ArchiveFragment : Fragment() {
    lateinit var binding: FragmentArchiveBinding
    private val noteAdapter = NoteAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArchiveBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        binding.noteRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireActivity())
            adapter = noteAdapter
            //(activity?.application as DiaryApp).db.noteDao().getAllNotes()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ArchiveFragment()
    }
}