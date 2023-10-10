package com.mawekk.sterdiary

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.appbar.MaterialToolbar
import com.mawekk.sterdiary.databinding.ActivityMainBinding
import com.mawekk.sterdiary.fragments.ArchiveFragment
import com.mawekk.sterdiary.fragments.NewNoteFragment
import com.mawekk.sterdiary.fragments.SearchFragment
import com.mawekk.sterdiary.fragments.SettingsFragment
import com.mawekk.sterdiary.fragments.StatisticsFragment
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val idStack = Stack<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showFragment(ArchiveFragment.newInstance(), R.id.archive_item)
        setBottomBarNavigation(binding)
        setTopBarNavigation(binding)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val id = idStack.pop()
        if (idStack.isNotEmpty()) {
            binding.bottomNavigation.setOnItemSelectedListener(backListener)
            binding.bottomNavigation.selectedItemId = idStack.peek()
            binding.bottomNavigation.setOnItemSelectedListener(mainListener)
            if (id == R.id.search_item || id == R.id.plus_item) {
                showBottomNavigation()
            }
            else if (id == R.id.addEmotionButton) {
                binding.topAppBar.setTitle(R.string.new_note)
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

    private fun hideBottomNavigation() {
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

    private fun setTopBarNavigation(binding: ActivityMainBinding) {
        binding.apply {
            topAppBar.setNavigationOnClickListener {
                topAppBar.setTitle(R.string.settings)
                hideBottomNavigation()
                showFragment(SettingsFragment.newInstance(), R.id.search_item)
            }
            topAppBar.setOnMenuItemClickListener {
                topAppBar.setTitle(R.string.search)
                hideBottomNavigation()
                showFragment(SearchFragment.newInstance(), R.id.search_item)
            }
        }
    }

    private val mainListener: (item: MenuItem) -> Boolean = {
        when (it.itemId) {
            R.id.archive_item -> {
                binding.topAppBar.setTitle(R.string.archive)
                showFragment(ArchiveFragment.newInstance(), it.itemId)
            }

            R.id.statistics_item -> {
                binding.topAppBar.setTitle(R.string.statistics)
                showFragment(
                    StatisticsFragment.newInstance(),
                    it.itemId
                )
            }

            else -> false
        }
    }

    private val backListener: (item: MenuItem) -> Boolean = {
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

    private fun setBottomBarNavigation(binding: ActivityMainBinding) {
        binding.apply {
            bottomNavigation.setOnItemSelectedListener(mainListener)
            addNoteButton.setOnClickListener {
                topAppBar.setTitle(R.string.new_note)
                hideBottomNavigation()
                showFragment(
                    NewNoteFragment.newInstance(),
                    R.id.plus_item
                )
            }
        }
    }
}