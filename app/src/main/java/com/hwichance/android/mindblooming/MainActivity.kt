package com.hwichance.android.mindblooming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar

class MainActivity : AppCompatActivity() {
    private lateinit var mainToolbar: MaterialToolbar
    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
    }

    private fun bindViews() {
        mainToolbar = findViewById(R.id.mainToolbar)
        searchItem = mainToolbar.menu.findItem(R.id.mainSearchMenu)
        searchView = searchItem.actionView as SearchView

        setSearchAction()
        setToolbarListener()
    }

    private fun setSearchAction() {
        searchView.queryHint = getString(R.string.main_toolbar_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // TODO("Not yet implemented")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // TODO("Not yet implemented")
                return true
            }
        })
    }

    private fun setToolbarListener() {
        mainToolbar.setNavigationOnClickListener {

        }
    }

    override fun onBackPressed() {
        if (searchItem.isActionViewExpanded) {
            searchItem.collapseActionView()
        } else {
            super.onBackPressed()
        }
    }
}