package com.mawekk.sterdiary

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mawekk.sterdiary.databinding.ActivityMainBinding
import com.mawekk.sterdiary.db.viewmodels.EmotionViewModel
import com.mawekk.sterdiary.db.viewmodels.NoteViewModel
import com.mawekk.sterdiary.fragments.ArchiveFragment
import com.mawekk.sterdiary.fragments.NewNoteFragment
import com.mawekk.sterdiary.fragments.SearchFragment
import com.mawekk.sterdiary.fragments.SettingsFragment
import com.mawekk.sterdiary.fragments.StatisticsFragment
import java.util.Stack

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val idStack = Stack<Int>()
    private val noteViewModel: NoteViewModel by viewModels()
    private val emotionViewModel: EmotionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreateOptionsMenu(binding.noteTopBar.menu)
        setContentView(binding.root)
        showFragment(ArchiveFragment.newInstance(), R.id.archive_item)
        setBottomBarNavigation()
        setTopBarNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.note_top_bar, menu)
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val id = idStack.pop()
        if (idStack.isNotEmpty()) {
            when (id) {
                R.id.archive_item, R.id.statistics_item -> {
                    binding.bottomNavigation.setOnItemSelectedListener(backListenerBottom)
                    binding.bottomNavigation.selectedItemId = idStack.peek()
                    binding.bottomNavigation.setOnItemSelectedListener(mainListenerBottom)
                }

                R.id.search_item -> {
                    binding.topAppBar.isVisible = true
                    showBottomNavigation()
                }

                R.id.topAppBar -> {
                    binding.topAppBar.isVisible = true
                    binding.settingsTopBar.isVisible = false
                    showBottomNavigation()
                }

                R.id.plus_item -> {
                    binding.topAppBar.isVisible = true
                    binding.newNoteTopBar.isVisible = false
                    showBottomNavigation()
                }

                R.id.addEmotionButton -> {
                    if (idStack.peek() == R.id.plus_item) {
                        binding.newNoteTopBar.isVisible = true
                    } else if (idStack.peek() == R.id.topAppBar) {
                        binding.settingsTopBar.isVisible = true
                    } else {
                        binding.editNoteTopBar.isVisible = true
                    }
                    binding.emotionsTopBar.isVisible = false
                }

                R.id.addNoteButton -> {
                    binding.topAppBar.isVisible = true
                    binding.noteTopBar.isVisible = false
                    showBottomNavigation()
                }

                R.id.edit -> {
                    binding.noteTopBar.isVisible = true
                    binding.editNoteTopBar.isVisible = false
                }
            }
        } else {
            finish()
        }
    }


    fun showFragment(fragment: Fragment, @IdRes buttonId: Int): Boolean {
        supportFragmentManager.commit {
            replace(R.id.place_holder, fragment)
            setReorderingAllowed(true)
            addToBackStack(null)
            idStack.push(buttonId)
        }
        return true
    }

    fun hideBottomNavigation() {
        binding.apply {
            bottomNavigation.isVisible = false
            bottomShadow.isVisible = false
            addNoteButton.isVisible = false
        }
    }

    private fun showBottomNavigation() {
        binding.apply {
            bottomNavigation.isVisible = true
            bottomShadow.isVisible = true
            addNoteButton.isVisible = true
        }
    }

    private val mainListenerBottom: (item: MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.archive_item -> {
                binding.topAppBar.setTitle(R.string.archive)
                binding.topAppBar.isVisible = true
                showFragment(ArchiveFragment.newInstance(), it.itemId)
            }

            R.id.statistics_item -> {
                binding.topAppBar.setTitle(R.string.statistics)
                binding.topAppBar.isVisible = true
                showFragment(
                    StatisticsFragment.newInstance(),
                    it.itemId
                )
            }

            else -> false
        }
    }

    private val backListenerBottom: (item: MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.archive_item -> {
                binding.topAppBar.setTitle(R.string.archive)
                true
            }

            R.id.statistics_item -> {
                binding.topAppBar.setTitle(R.string.statistics)
                true
            }

            else -> false
        }
    }

    private fun setBottomBarNavigation() {
        binding.apply {
            bottomNavigation.setOnItemSelectedListener(mainListenerBottom)
            addNoteButton.setOnClickListener {
                hideBottomNavigation()
                topAppBar.isVisible = false
                newNoteTopBar.isVisible = true
                showFragment(
                    NewNoteFragment.newInstance(),
                    R.id.plus_item
                )
            }
        }
    }

    private fun setTopBarNavigation() {
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                hideBottomNavigation()
                showFragment(SettingsFragment.newInstance(), R.id.topAppBar)
                topAppBar.isVisible = false
                settingsTopBar.isVisible = true
            }
            topAppBar.setOnMenuItemClickListener {
                hideBottomNavigation()
                showFragment(SearchFragment.newInstance(), R.id.search_item)
                topAppBar.isVisible = false
                true
            }
        }
    }
}