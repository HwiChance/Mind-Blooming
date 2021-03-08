package com.hwichance.android.mindblooming

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.hwichance.android.mindblooming.adapters.IdeaListAdapter
import com.hwichance.android.mindblooming.adapters.IdeaListAdapter.*
import com.hwichance.android.mindblooming.enums.SortCaller
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.fragments.SortFragment
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.data.SortData
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel
import com.hwichance.android.mindblooming.rooms.view_model.MindMapViewModel
import com.hwichance.android.mindblooming.rooms.view_model.SeriesViewModel
import com.hwichance.android.mindblooming.rooms.view_model.SortViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mainToolbar: MaterialToolbar
    private lateinit var mainDrawerLayout: DrawerLayout
    private lateinit var mainNavigationDrawer: NavigationView
    private lateinit var mainFab: FloatingActionButton
    private lateinit var versionTextView: TextView
    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var sortData: SortData
    private val ideaViewModel: IdeaViewModel by viewModels()
    private val mindMapViewModel: MindMapViewModel by viewModels()
    private val seriesViewModel: SeriesViewModel by viewModels()
    private val sortViewModel: SortViewModel by viewModels()
    private var ideaListAdapter = IdeaListAdapter()
    private val sortStrings by lazy {
        resources.getStringArray(R.array.sortStrings)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindViews()

        ideaViewModel.getAllIdeas().observe(this, { ideas ->
            ideaListAdapter.setIdeaList(ideas)
        })

        seriesViewModel.getAll().observe(this, { seriesList ->
            ideaListAdapter.setSeriesList(seriesList)
        })

        seriesViewModel.findFavoriteSeries(true).observe(this, { seriesList ->
            val container = mainNavigationDrawer.menu.findItem(R.id.seriesGroupTitle).subMenu
            container.clear()
            container.add(getString(R.string.drawer_series_list))
                .setIcon(R.drawable.ic_bookmarks_24dp)
                .setOnMenuItemClickListener {
                    startActivity(Intent(this, SeriesListActivity::class.java))
                    false
                }
            for (series in seriesList) {
                container.add(series.seriesTitle)
                    .setIcon(R.drawable.ic_bookmark_24dp)
                    .setOnMenuItemClickListener {
                        val seriesIntent = Intent(this, SeriesActivity::class.java)
                        seriesIntent.putExtra("seriesId", series.seriesId)
                        startActivity(seriesIntent)
                        false
                    }
            }
        })

        sortViewModel.getDataByCaller(SortCaller.MAIN).observe(this, { data ->
            if (data != null) {
                sortData = data
                val sortText = when (sortData.sortEnum) {
                    SortEnum.TITLE -> sortStrings[0]
                    SortEnum.CREATED_DATE -> sortStrings[1]
                    SortEnum.LAST_MODIFIED_DATE -> sortStrings[2]
                    SortEnum.STARRED_DATE -> sortStrings[3]
                    SortEnum.SERIES_ADDED_DATE -> sortStrings[4]
                }
                ideaListAdapter.sortingData(sortText, sortData)
            }
        })
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.toolbar_action_mode_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val count = ideaListAdapter.getCheckedItemCount()
            val selectAllIcon = menu?.findItem(R.id.actionModeSelectAll)
            val deleteIcon = menu?.findItem(R.id.actionModeDelete)

            mode?.title = count.toString() + getString(R.string.selected_idea_count)

            when (count) {
                ideaListAdapter.itemCount -> {
                    selectAllIcon?.setIcon(R.drawable.ic_select_all_colored_24dp)
                    deleteIcon?.isEnabled = true
                }
                0 -> {
                    selectAllIcon?.setIcon(R.drawable.ic_select_all_24dp)
                    deleteIcon?.isEnabled = false
                }
                else -> {
                    selectAllIcon?.setIcon(R.drawable.ic_select_all_24dp)
                    deleteIcon?.isEnabled = true
                }
            }
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.actionModeSelectAll -> {
                    if (ideaListAdapter.getCheckedItemCount() == ideaListAdapter.itemCount) {
                        ideaListAdapter.initializeChecked(false)
                    } else {
                        ideaListAdapter.initializeChecked(true)
                    }
                    mode?.invalidate()
                    true
                }
                R.id.actionModeDelete -> {
                    val deleteList = ideaListAdapter.getCheckedItemIds()
                    mindMapViewModel.deleteItems(deleteList)
                    ideaViewModel.deleteIdeas(deleteList)
                    mode?.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            ideaListAdapter.setActionMode(false)
            ideaListAdapter.initializeChecked(false)
            mainFab.show()
        }
    }

    private fun bindViews() {
        mainToolbar = findViewById(R.id.mainToolbar)
        mainDrawerLayout = findViewById(R.id.mainDrawerLayout)
        mainNavigationDrawer = findViewById(R.id.mainNavigationDrawer)
        mainFab = findViewById(R.id.mainFab)

        versionTextView = mainNavigationDrawer.getHeaderView(0).findViewById(R.id.versionTextView)
        searchItem = mainToolbar.menu.findItem(R.id.mainSearchMenu)
        searchView = searchItem.actionView as SearchView

        mainRecyclerView = findViewById(R.id.mainRecyclerView)
        ideaListAdapter.setIdeaClickListener(object : IdeaClickListener {
            var actionMode: ActionMode? = null
            override fun onClick(idea: IdeaData, isActionMode: Boolean, position: Int) {
                if (isActionMode) {
                    ideaListAdapter.toggleItemChecked(position)
                    actionMode?.invalidate()
                } else {
                    val intent = Intent(this@MainActivity, MindMapEditActivity::class.java)
                    intent.putExtra("groupId", idea.ideaId)
                    startActivity(intent)
                }
            }

            override fun onLongClick(isActionMode: Boolean, position: Int) {
                if (!isActionMode) {
                    actionMode = startSupportActionMode(actionModeCallback)
                    ideaListAdapter.setActionMode(true)
                    ideaListAdapter.toggleItemChecked(position)
                    actionMode?.invalidate()
                    mainFab.hide()
                }
            }
        })
        ideaListAdapter.setStarredBtnClickListener(object : StarredBtnClickListener {
            override fun onClick(isChecked: Boolean, idea: IdeaData) {
                idea.isStarred = isChecked
                idea.starredDate = if (isChecked) {
                    System.currentTimeMillis()
                } else {
                    null
                }
                ideaViewModel.update(idea)
            }
        })
        ideaListAdapter.setSortBtnClickListener {
            SortFragment(SortCaller.MAIN, sortData)
                .show(supportFragmentManager, "SORT_FRAGMENT")
        }

        mainRecyclerView.adapter = ideaListAdapter
        mainRecyclerView.layoutManager = LinearLayoutManager(this)

        mainRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    mainFab.hide()
                } else if (dy < 0) {
                    mainFab.show()
                }
            }
        })

        setVersionText()
        setSearchAction()
        setDrawerAction()
        setToolbarListener()
        setFabListener()
    }

    private fun setVersionText() {
        val versionText = getString(R.string.version_text)
        val pi = packageManager.getPackageInfo("com.hwichance.android.mindblooming", 0)
        versionTextView.text = (versionText + pi.versionName)
    }

    private fun setSearchAction() {
        val searchEditFrame = searchView.findViewById<LinearLayout>(R.id.search_edit_frame)
        (searchEditFrame.layoutParams as LinearLayout.LayoutParams).leftMargin = 0
        searchView.queryHint = getString(R.string.main_toolbar_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                ideaListAdapter.filtering(newText)
                return true
            }
        })
    }

    private fun setDrawerAction() {
        mainNavigationDrawer.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mainDrawerStarred -> {
                    startActivity(Intent(this, StarredActivity::class.java))
                }
            }
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            mainDrawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setToolbarListener() {
        mainToolbar.setNavigationOnClickListener {
            mainDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setFabListener() {
        mainFab.setOnClickListener {
            val date = System.currentTimeMillis()
            val title = getString(R.string.new_mind_map)
            val newIdea = IdeaData(null, title, date, date, true, null)
            ideaViewModel.insert(newIdea) { groupId ->
                val intent = Intent(this, MindMapEditActivity::class.java)
                intent.putExtra("groupId", groupId)
                intent.putExtra("isNewIdea", true)
                startActivity(intent)
            }
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
            else -> super.onBackPressed()
        }
    }
}