package com.mawekk.sterdiary.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import app.futured.donut.DonutSection
import com.mawekk.sterdiary.databinding.FragmentStatisticsBinding
import com.mawekk.sterdiary.db.DiaryViewModel
import com.mawekk.sterdiary.db.entities.Note


class StatisticsFragment : Fragment() {
    private lateinit var binding: FragmentStatisticsBinding
    private lateinit var colors: IntArray
    private lateinit var distortionsNames: Array<String>
    private lateinit var names: List<TextView>
    private lateinit var counts: List<TextView>
    private lateinit var emotions: List<TextView>
    private val viewModel: DiaryViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)

        colors = resources.getIntArray(com.mawekk.sterdiary.R.array.donut_colors)
        distortionsNames =
            resources.getStringArray(com.mawekk.sterdiary.R.array.cognitive_distortions_names)

        binding.apply {
            names = listOf(name1, name2, name3, name4, name5, name6, name7, name8, name9)
            counts =
                listOf(
                    count1,
                    count2,
                    count3,
                    count4,
                    count5,
                    count6,
                    count7,
                    count8,
                    count9,
                    count01,
                    count02,
                    count03,
                    count04,
                    count05
                )
            emotions = listOf(emotion1, emotion2, emotion3, emotion4, emotion5)
        }

        initDistortionsStatistic()
        initEmotionsStatistic()
        return binding.root
    }

    private fun initDistortionsStatistic() {
        viewModel.getAllNotes().observe(viewLifecycleOwner) {
            val results = countDistortions(it)
            names.forEachIndexed { i, view ->
                view.text = distortionsNames[i]
                setDrawable(view, colors[i])
                counts[i].text = results[distortionsNames[i]].toString()
            }
            createDistortionsDonut(results)
        }
    }

    private fun countDistortions(notes: List<Note>): MutableMap<String, Int> {
        val results = mutableMapOf<String, Int>()
        distortionsNames.forEach {
            results[it] = 0
        }

        notes.forEach { note ->
            note.distortions.split(';').forEach { name ->
                results[name] = results[name]!! + 1
            }
        }

        return results
    }

    private fun createDistortionsDonut(results: MutableMap<String, Int>) {
        val sections = mutableListOf<DonutSection>()
        var sum = 0f
        distortionsNames.forEachIndexed { i, name ->
            val amount = results[name]!!.toFloat()
            if (amount > 0) {
                sections.add(DonutSection(name, colors[i], amount))
                sum += amount
            }
        }

        binding.distorionsDonut.apply {
            cap = sum
            if (cap > 0) {
                submitData(sections)
            }
        }

    }

    private fun initEmotionsStatistic() {
        viewModel.getNotesEmotions().observe(viewLifecycleOwner) { list ->
            val allEmotions = mutableListOf<String>()
            list.forEach {
                allEmotions.add(it.name)
            }
            val emotionsTop =
                countEmotions(allEmotions).toList().sortedBy { (_, value) -> value }.reversed()
                    .take(emotions.size)

            for (i in emotionsTop.size until emotions.size) {
                emotions[i].isVisible = false
                counts[i + 9].isVisible = false
            }

            emotionsTop.forEachIndexed { i, pair ->
                emotions[i].text = pair.first
                counts[i + 9].text = pair.second.toString()
                setDrawable(emotions[i], colors[i + 2])
            }
            createEmotionsDonut(emotionsTop)
        }
    }


    private fun countEmotions(names: List<String>): MutableMap<String, Int> {
        val emotionsTop = mutableMapOf<String, Int>()
        names.forEach {
            if (emotionsTop[it] == null) {
                emotionsTop[it] = 1
            } else {
                emotionsTop[it] = emotionsTop[it]!! + 1
            }
        }
        return emotionsTop
    }

    private fun createEmotionsDonut(emotionsTop: List<Pair<String, Int>>) {
        val sections = mutableListOf<DonutSection>()
        var sum = 0f
        emotionsTop.forEachIndexed { i, pair ->
            val amount = pair.second.toFloat()
            if (amount > 0) {
                sections.add(DonutSection(pair.first, colors[i + 2], amount))
                sum += amount
            }
        }

        binding.emotionsDonut.apply {
            cap = sum
            submitData(sections)
        }
    }

    private fun setDrawable(view: TextView, color: Int) {
        view.setCompoundDrawablesWithIntrinsicBounds(
            com.mawekk.sterdiary.R.drawable.baseline_circle_24,
            0,
            0,
            0
        )
        view.compoundDrawablePadding = 8
        view.compoundDrawables[0].setTint(color)
    }

    companion object {
        @JvmStatic
        fun newInstance() = StatisticsFragment()
    }
}
