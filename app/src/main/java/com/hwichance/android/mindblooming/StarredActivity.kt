package com.hwichance.android.mindblooming

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hwichance.android.mindblooming.adapters.IdeaListAdapter
import com.hwichance.android.mindblooming.adapters.IdeaListAdapter.*
import com.hwichance.android.mindblooming.enums.SortCaller
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.fragments.SortFragment
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.data.SortData
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel
import com.hwichance.android.mindblooming.rooms.view_model.SeriesViewModel
import com.hwichance.android.mindblooming.rooms.view_model.SortViewModel

class StarredActivity : AppCompatActivity() {
    private lateinit var starredToolbar: MaterialToolbar
    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var starredRecyclerView: RecyclerView
    private lateinit var sortData: SortData
    private val starredListAdapter = IdeaListAdapter()
    private val ideaViewModel: IdeaViewModel by viewModels()
    private val seriesViewModel: SeriesViewModel by viewModels()
    private val sortViewModel: SortViewModel by viewModels()
    private val sortStrings by lazy {
        resources.getStringArray(R.array.sortStrings)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starred)

        ideaViewModel.findStarredIdea(true).observe(this, { ideas ->
            starredListAdapter.setIdeaList(ideas)
        })

        seriesViewModel.getAll().observe(this, { seriesList ->
            starredListAdapter.setSeriesList(seriesList)
        })

        sortViewModel.getDataByCaller(SortCaller.STARRED).observe(this, { data ->
            if (data != null) {
                sortData = data
                val sortText = when (sortData.sortEnum) {
                    SortEnum.TITLE -> sortStrings[0]
                    SortEnum.CREATED_DATE -> sortStrings[1]
                    SortEnum.LAST_MODIFIED_DATE -> sortStrings[2]
                    SortEnum.STARRED_DATE -> sortStrings[3]
                    SortEnum.SERIES_ADDED_DATE -> sortStrings[4]
                }
                starredListAdapter.sortingData(sortText, sortData)
            }
        })

        bindViews()
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.toolbar_action_mode_menu, menu)
            menu?.findItem(R.id.actionModeDelete)?.setIcon(R.drawable.ic_star_remove_24dp)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            starredToolbar.menu.findItem(R.id.starredSearchMenu).isEnabled = false
            val count = starredListAdapter.getCheckedItemCount()
            val selectAllIcon = menu?.findItem(R.id.actionModeSelectAll)
            val deleteIcon = menu?.findItem(R.id.actionModeDelete)

            mode?.title = count.toString() + getString(R.string.selected_idea_count)

            when (count) {
                starredListAdapter.itemCount - 1 -> {
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
                    if (starredListAdapter.getCheckedItemCount() == starredListAdapter.itemCount - 1) {
                        starredListAdapter.initializeChecked(false)
                    } else {
                        starredListAdapter.initializeChecked(true)
                    }
                    mode?.invalidate()
                    true
                }
                R.id.actionModeDelete -> {
                    MaterialAlertDialogBuilder(this@StarredActivity)
                        .setMessage(R.string.unstarred_selected_dialog_msg)
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                            val deleteList = starredListAdapter.getCheckedItemIds()
                            ideaViewModel.updateStar(false, deleteList)
                            dialog.dismiss()
                            mode?.finish()
                        }
                        .create()
                        .show()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            starredToolbar.menu.findItem(R.id.starredSearchMenu).isEnabled = true
            starredListAdapter.setActionMode(false)
            starredListAdapter.initializeChecked(false)
        }
    }

    private fun bindViews() {
        starredToolbar = findViewById(R.id.starredToolbar)
        searchItem = starredToolbar.menu.findItem(R.id.starredSearchMenu)
        searchView = searchItem.actionView as SearchView

        starredRecyclerView = findViewById(R.id.starredRecyclerView)
        starredListAdapter.setIdeaClickListener(object : IdeaClickListener {
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
                    starredListAdapter.setActionMode(true)
                    starredListAdapter.toggleItemChecked(position)
                    actionMode = startSupportActionMode(actionModeCallback)
                    actionMode?.invalidate()
                }
            }
        })
        starredListAdapter.setStarredBtnClickListener(object : StarredBtnClickListener {
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
        starredListAdapter.setSortBtnClickListener {
            SortFragment(SortCaller.STARRED, sortData)
                .show(supportFragmentManager, "SORT_FRAGMENT")
        }
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