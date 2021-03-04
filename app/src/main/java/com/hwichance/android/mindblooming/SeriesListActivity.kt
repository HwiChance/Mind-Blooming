package com.hwichance.android.mindblooming

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hwichance.android.mindblooming.adapters.SeriesListAdapter
import com.hwichance.android.mindblooming.adapters.SeriesListAdapter.*
import com.hwichance.android.mindblooming.rooms.data.SeriesData
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel
import com.hwichance.android.mindblooming.rooms.view_model.SeriesViewModel

class SeriesListActivity : AppCompatActivity() {
    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private val seriesListAdapter = SeriesListAdapter()
    private val seriesViewModel: SeriesViewModel by viewModels()
    private val ideaViewModel: IdeaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series_list)

        bindViews()

        seriesViewModel.getAll().observe(this, { seriesList ->
            seriesListAdapter.setSeriesList(seriesList)
        })
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.toolbar_action_mode_menu, menu)
            menu?.findItem(R.id.actionModeDelete)?.setIcon(R.drawable.ic_bookmark_remove_24dp)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val count = seriesListAdapter.getCheckedItemCount()
            val selectAllIcon = menu?.findItem(R.id.actionModeSelectAll)
            val deleteIcon = menu?.findItem(R.id.actionModeDelete)

            mode?.title = count.toString() + getString(R.string.selected_idea_count)

            when (count) {
                seriesListAdapter.itemCount -> {
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
                    if (seriesListAdapter.getCheckedItemCount() == seriesListAdapter.itemCount) {
                        seriesListAdapter.initializeChecked(false)
                    } else {
                        seriesListAdapter.initializeChecked(true)
                    }
                    mode?.invalidate()
                    true
                }
                R.id.actionModeDelete -> {
                    showDeleteDialog(seriesListAdapter.getCheckedItemIds())
                    mode?.finish()
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            seriesListAdapter.setActionMode(false)
            seriesListAdapter.initializeChecked(false)
        }
    }

    private fun bindViews() {
        toolbar = findViewById(R.id.seriesListToolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.seriesListAddMenu) {
                val newSeries = SeriesData(
                    null,
                    getString(R.string.new_series),
                    getString(R.string.new_series),
                    System.currentTimeMillis()
                )
                seriesViewModel.insert(newSeries) { seriesId ->
                    val intent = Intent(this, SeriesActivity::class.java)
                    intent.putExtra("seriesId", seriesId)
                    startActivity(intent)
                }
            }
            true
        }

        seriesListAdapter.setOnItemClickListener(object : ItemClickListener {
            var actionMode: ActionMode? = null
            override fun onClick(seriesId: Long, isActionMode: Boolean, position: Int) {
                if (isActionMode) {
                    seriesListAdapter.toggleItemChecked(position)
                    actionMode?.invalidate()
                } else {
                    val intent = Intent(this@SeriesListActivity, SeriesActivity::class.java)
                    intent.putExtra("seriesId", seriesId)
                    startActivity(intent)
                }
            }

            override fun onLongClick(isActionMode: Boolean, position: Int) {
                if (!isActionMode) {
                    actionMode = startSupportActionMode(actionModeCallback)
                    seriesListAdapter.setActionMode(true)
                    seriesListAdapter.toggleItemChecked(position)
                    actionMode?.invalidate()
                }
            }
        })

        seriesListAdapter.setOnToggleBtnClickListener(object : ToggleBtnClickListener {
            override fun onClick(isChecked: Boolean, series: SeriesData) {
                series.isFavorite = isChecked
                series.seriesFavoriteDate = if (isChecked) {
                    System.currentTimeMillis()
                } else {
                    null
                }
                seriesViewModel.update(series)
            }
        })

        seriesListAdapter.setOnDeleteBtnClickListener(object : DeleteBtnClickListener {
            override fun onClick(series: SeriesData) {
                showDeleteDialog(listOf(series.seriesId!!))
            }
        })

        recyclerView = findViewById(R.id.seriesListRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = seriesListAdapter
    }

    fun showDeleteDialog(seriesIds: List<Long>) {
        MaterialAlertDialogBuilder(this)
            .setMessage(R.string.delete_dialog_msg)
            .setNegativeButton(R.string.dialog_cancel, null)
            .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                ideaViewModel.updateSeriesDelete(seriesIds)
                seriesViewModel.deleteSeriesByIds(seriesIds)
                dialog.dismiss()
            }
            .create()
            .show()
    }
}