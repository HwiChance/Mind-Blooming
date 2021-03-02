package com.hwichance.android.mindblooming

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.hwichance.android.mindblooming.adapters.IdeaListAdapter
import com.hwichance.android.mindblooming.enums.DiagramClassEnum
import com.hwichance.android.mindblooming.enums.FilterCaller
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel
import com.hwichance.android.mindblooming.rooms.view_model.MindMapViewModel

class StarredActivity : AppCompatActivity() {
    private lateinit var starredToolbar: MaterialToolbar
    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var starredRecyclerView: RecyclerView
    private val starredListAdapter = IdeaListAdapter()
    private val ideaViewModel: IdeaViewModel by viewModels()
    private val mindMapViewModel: MindMapViewModel by viewModels()
    private var classFilter = DiagramClassEnum.ALL
    private var sortFilter = SortEnum.CREATED_DATE

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
        setContentView(R.layout.activity_starred)

        ideaViewModel.findStarredIdea(true).observe(this, { ideas ->
            starredListAdapter.setIdeaList(ideas)
        })

        bindViews()
    }

    private val startForFilterResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                classFilter = result.data?.getSerializableExtra("classFilter") as DiagramClassEnum
                sortFilter = result.data?.getSerializableExtra("sortFilter") as SortEnum
                starredListAdapter.filtering(classFilter, sortFilter)
            }
        }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.toolbar_action_mode_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val count = starredListAdapter.getCheckedItemCount()
            val selectAllIcon = menu?.findItem(R.id.actionModeSelectAll)
            val deleteIcon = menu?.findItem(R.id.actionModeDelete)

            mode?.title = count.toString() + getString(R.string.selected_idea_count)

            when (count) {
                starredListAdapter.itemCount -> {
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
                    if (starredListAdapter.getCheckedItemCount() == starredListAdapter.itemCount) {
                        starredListAdapter.initializeChecked(false)
                    } else {
                        starredListAdapter.initializeChecked(true)
                    }
                    mode?.invalidate()
                    true
                }
                R.id.actionModeDelete -> {
                    val deleteList = starredListAdapter.getCheckedItemIds()
                    mindMapViewModel.deleteItems(deleteList)
                    ideaViewModel.deleteIdeas(deleteList)
                    mode?.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            starredListAdapter.setActionMode(false)
            starredListAdapter.initializeChecked(false)
        }
    }

    private fun bindViews() {
        starredToolbar = findViewById(R.id.starredToolbar)
        searchItem = starredToolbar.menu.findItem(R.id.starredSearchMenu)
        searchView = searchItem.actionView as SearchView

        starredRecyclerView = findViewById(R.id.starredRecyclerView)
        starredListAdapter.setIdeaClickListener(object : IdeaListAdapter.IdeaClickListener {
            var actionMode: ActionMode? = null
            override fun onClick(idea: IdeaData, isActionMode: Boolean, position: Int) {
                if (isActionMode) {
                    starredListAdapter.toggleItemChecked(position)
                    actionMode?.invalidate()
                } else {
                    val intent = Intent(this@StarredActivity, MindMapEditActivity::class.java)
                    intent.putExtra("groupId", idea.ideaId)
                    startActivity(intent)
                }
            }

            override fun onLongClick(isActionMode: Boolean, position: Int) {
                if (!isActionMode) {
                    actionMode = startSupportActionMode(actionModeCallback)
                    starredListAdapter.setActionMode(true)
                    starredListAdapter.toggleItemChecked(position)
                    actionMode?.invalidate()
                }
            }
        })
        starredRecyclerView.adapter = starredListAdapter
        starredRecyclerView.layoutManager = LinearLayoutManager(this)

        setSearchAction()
        setToolbarListener()
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
                starredListAdapter.filtering(newText)
                return true
            }
        })
    }

    private fun setToolbarListener() {
        starredToolbar.setNavigationOnClickListener {
            finish()
        }
        starredToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.starredFilterMenu -> {
                    val filterActIntent = Intent(this, IdeaFilterActivity::class.java)
                    filterActIntent.putExtra("caller", FilterCaller.STARRED)
                    filterActIntent.putExtra("class", classFilter)
                    filterActIntent.putExtra("sort", sortFilter)
                    startForFilterResult.launch(filterActIntent)
                    overridePendingTransition(R.anim.up, R.anim.no_animation)
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        when {
            searchItem.isActionViewExpanded -> {
                searchItem.collapseActionView()
            }
            else -> super.onBackPressed()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fadein, R.anim.no_animation)
    }
}