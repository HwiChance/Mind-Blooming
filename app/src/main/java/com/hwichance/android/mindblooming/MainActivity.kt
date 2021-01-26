package com.hwichance.android.mindblooming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var mainToolbar: MaterialToolbar
    private lateinit var mainDrawerLayout: DrawerLayout
    private lateinit var mainNavigationDrawer: NavigationView
    private lateinit var versionTextView: TextView
    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()
    }

    private fun bindViews() {
        mainToolbar = findViewById(R.id.mainToolbar)
        mainDrawerLayout = findViewById(R.id.mainDrawerLayout)
        mainNavigationDrawer = findViewById(R.id.mainNavigationDrawer)

        versionTextView = mainNavigationDrawer.getHeaderView(0).findViewById(R.id.versionTextView)
        searchItem = mainToolbar.menu.findItem(R.id.mainSearchMenu)
        searchView = searchItem.actionView as SearchView

        setVersionText()
        setSearchAction()
        setDrawerAction()
        setToolbarListener()
    }

    private fun setVersionText() {
        val versionText = getString(R.string.version_text)
        versionTextView.text = (versionText + BuildConfig.VERSION_NAME)
    }

    private fun setSearchAction() {
        val searchEditFrame = searchView?.findViewById<LinearLayout>(R.id.search_edit_frame)
        (searchEditFrame.layoutParams as LinearLayout.LayoutParams).leftMargin = 0
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

    private fun setDrawerAction() {
        mainNavigationDrawer.setNavigationItemSelectedListener { menuItem ->

            true
        }
    }

    private fun setToolbarListener() {
        mainToolbar.setNavigationOnClickListener {
            mainDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        when {
            searchItem.isActionViewExpanded -> {
                searchItem.collapseActionView()
            }
            mainDrawerLayout.isDrawerOpen(GravityCompat.START) -> {
                mainDrawerLayout.closeDrawer(GravityCompat.START)
            }
            else -> {
                super.onBackPressed()
            }
        }
    }
}