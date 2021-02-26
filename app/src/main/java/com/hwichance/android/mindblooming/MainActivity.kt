package com.hwichance.android.mindblooming

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.hwichance.android.mindblooming.adapters.IdeaListAdapter
import com.hwichance.android.mindblooming.enums.DiagramClassEnum
import com.hwichance.android.mindblooming.enums.FilterCaller
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.fragments.AddDiagramFragment
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var mainToolbar: MaterialToolbar
    private lateinit var mainDrawerLayout: DrawerLayout
    private lateinit var mainNavigationDrawer: NavigationView
    private lateinit var mainFab: FloatingActionButton
    private lateinit var versionTextView: TextView
    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var mainRecyclerView: RecyclerView
    private val ideaViewModel: IdeaViewModel by viewModels()
    private var ideaListAdapter = IdeaListAdapter()
    private var classFilter = DiagramClassEnum.ALL
    private var sortFilter = SortEnum.TITLE

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("class", classFilter)
        outState.putSerializable("sort", sortFilter)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        classFilter = savedInstanceState.getSerializable("class") as DiagramClassEnum
        sortFilter = savedInstanceState.getSerializable("sort") as SortEnum
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ideaViewModel.getAllIdeas().observe(this, { ideas ->
            ideaListAdapter.setIdeaList(ideas)
        })
        bindViews()
    }

    private val startForFilterResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                classFilter = result.data?.getSerializableExtra("classFilter") as DiagramClassEnum
                sortFilter = result.data?.getSerializableExtra("sortFilter") as SortEnum
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
        ideaListAdapter.setIdeaClickListener(object : IdeaListAdapter.IdeaClickListener {
            override fun onClick(idea: IdeaData) {
                val intent = Intent(this@MainActivity, MindMapEditActivity::class.java)
                intent.putExtra("groupId", idea.ideaId)
                startActivity(intent)
            }

            override fun onLongClick(idea: IdeaData) {
                ideaViewModel.delete(idea)
            }
        })
        mainRecyclerView.adapter = ideaListAdapter
        mainRecyclerView.layoutManager = LinearLayoutManager(this)

        setVersionText()
        setSearchAction()
        setDrawerAction()
        setToolbarListener()
        setFabListener()
    }

    private fun setVersionText() {
        val versionText = getString(R.string.version_text)
        versionTextView.text = (versionText + BuildConfig.VERSION_NAME)
    }

    private fun setSearchAction() {
        val searchEditFrame = searchView.findViewById<LinearLayout>(R.id.search_edit_frame)
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
            when (menuItem.itemId) {
                R.id.mainDrawerExport -> {

                }
                R.id.mainDrawerStarred -> {
                    startActivity(Intent(this, StarredActivity::class.java))
                }
                R.id.mainDrawerSetting -> {

                }
                else -> {
                    val seriesIntent = Intent(this, SeriesActivity::class.java)
                    seriesIntent.putExtra("seriesTitle", menuItem.title)
                    startActivity(seriesIntent)
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
        mainToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mainFilterMenu -> {
                    val filterActIntent = Intent(this, IdeaFilterActivity::class.java)
                    filterActIntent.putExtra("caller", FilterCaller.MAIN)
                    filterActIntent.putExtra("class", classFilter)
                    filterActIntent.putExtra("sort", sortFilter)
                    startForFilterResult.launch(filterActIntent)
                    overridePendingTransition(R.anim.up, R.anim.no_animation)
                }
            }
            true
        }
    }

    private fun setFabListener() {
        mainFab.setOnClickListener {
            val addDiagramFragment = AddDiagramFragment()
            addDiagramFragment.show(supportFragmentManager, "ADD_DIAGRAM_FRAGMENT")
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