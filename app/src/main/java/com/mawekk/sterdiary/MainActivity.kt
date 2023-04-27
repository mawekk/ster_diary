package com.mawekk.sterdiary

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mawekk.sterdiary.databinding.ActivityMainBinding
import com.mawekk.sterdiary.fragments.ArchiveFragment
import com.mawekk.sterdiary.fragments.NewNoteFragment
import com.mawekk.sterdiary.fragments.NoteFragment
import com.mawekk.sterdiary.fragments.SearchFragment
import com.mawekk.sterdiary.fragments.SettingsFragment
import com.mawekk.sterdiary.fragments.StatisticsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showFragment(ArchiveFragment.newInstance())
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                showFragment(SettingsFragment.newInstance())
                bottomNavigation.isVisible = false
                bottomShadow.isVisible = false
                topAppBar.setTitle(R.string.settings)
            }
            topAppBar.setOnMenuItemClickListener {
                showFragment(SearchFragment.newInstance())
            }
            bottomNavigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.archive_item -> showFragment(ArchiveFragment.newInstance())
                    R.id.plus_item -> showFragment(NoteFragment.newInstance())
                    R.id.statistics_item -> showFragment(StatisticsFragment.newInstance())
                    else -> false
                }
            }
        }

    }

    private fun showFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.place_holder, fragment).commit()
        return true
    }
}