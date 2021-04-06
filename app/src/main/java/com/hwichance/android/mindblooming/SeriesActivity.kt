package com.hwichance.android.mindblooming

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hwichance.android.mindblooming.adapters.IdeaListAdapter
import com.hwichance.android.mindblooming.adapters.IdeaListAdapter.*
import com.hwichance.android.mindblooming.enums.SortCaller
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.fragments.SortFragment
import com.hwichance.android.mindblooming.listeners.SeriesAppBarListener
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.data.SeriesData
import com.hwichance.android.mindblooming.rooms.data.SortData
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel
import com.hwichance.android.mindblooming.rooms.view_model.SeriesViewModel
import com.hwichance.android.mindblooming.rooms.view_model.SortViewModel

class SeriesActivity : AppCompatActivity() {
    private lateinit var seriesToolbar: MaterialToolbar
    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var seriesRecyclerView: RecyclerView
    private lateinit var seriesToolbarLayout: AppBarLayout
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var seriesTitleLabel: TextView
    private lateinit var seriesDescriptionLabel: TextView
    private lateinit var editBtn: Button
    private lateinit var seriesTitle: TextView
    private lateinit var newSeriesText: String
    private var seriesData: SeriesData? = null
    private lateinit var sortData: SortData
    private val ideaListAdapter = IdeaListAdapter()
    private val seriesViewModel: SeriesViewModel by viewModels()
    private val ideaViewModel: IdeaViewModel by viewModels()
    private val sortViewModel: SortViewModel by viewModels()
    private val sortStrings by lazy {
        resources.getStringArray(R.array.sortStrings)
    }
    private var seriesId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)

        newSeriesText = getString(R.string.new_series)
        seriesId = intent?.getLongExtra("seriesId", -1L) ?: -1L

        bindViews()

        if (seriesId != -1L) {
            seriesViewModel.findSeriesById(seriesId).observe(this, { series ->
                if (series != null) {
                    seriesData = series
                    seriesTitle.text = series.seriesTitle
                    titleEditText.setText(series.seriesTitle)
                    descriptionEditText.setText(series.seriesDescription)
                }
            })

            seriesViewModel.getAll().observe(this, { seriesList ->
                ideaListAdapter.setSeriesList(seriesList)
            })

            ideaViewModel.findIdeaInSeries(seriesId).observe(this, { ideas ->
                ideaListAdapter.setIdeaList(ideas)
            })

            sortViewModel.getDataByCaller(SortCaller.SERIES).observe(this, { data ->
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
    }

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.toolbar_action_mode_menu, menu)
            menu?.findItem(R.id.actionModeDelete)?.setIcon(R.drawable.ic_bookmark_remove_24dp)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            seriesToolbar.menu.findItem(R.id.seriesSearchMenu).isEnabled = false
            seriesToolbar.menu.findItem(R.id.seriesDeleteMenu).isEnabled = false
            val count = ideaListAdapter.getCheckedItemCount()
            val selectAllIcon = menu?.findItem(R.id.actionModeSelectAll)
            val deleteIcon = menu?.findItem(R.id.actionModeDelete)

            mode?.title = count.toString() + getString(R.string.selected_idea_count)

            when (count) {
                ideaListAdapter.itemCount - 1 -> {
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
                    if (ideaListAdapter.getCheckedItemCount() == ideaListAdapter.itemCount - 1) {
                        ideaListAdapter.initializeChecked(false)
                    } else {
                        ideaListAdapter.initializeChecked(true)
                    }
                    mode?.invalidate()
                    true
                }
                R.id.actionModeDelete -> {
                    MaterialAlertDialogBuilder(this@SeriesActivity)
                        .setMessage(R.string.remove_selected_from_series_dialog_msg)
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setPositiveButton(R.string.dialog_ok) { dialog, _ ->
                            val deleteList = ideaListAdapter.getCheckedItemIds()
                            ideaViewModel.updateSeries(deleteList)
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
            seriesToolbar.menu.findItem(R.id.seriesSearchMenu).isEnabled = true
            seriesToolbar.menu.findItem(R.id.seriesDeleteMenu).isEnabled = true
            ideaListAdapter.setActionMode(false)
            ideaListAdapter.initializeChecked(false)
        }
    }

    private fun bindViews() {
        seriesToolbar = findViewById(R.id.seriesToolbar)
        seriesRecyclerView = findViewById(R.id.seriesRecyclerView)
        seriesToolbarLayout = findViewById(R.id.seriesToolbarLayout)
        titleEditText = findViewById(R.id.seriesTitleEditText)
        descriptionEditText = findViewById(R.id.seriesDescriptionEditText)
        seriesDescriptionLabel = findViewById(R.id.seriesDescriptionLabel)
        seriesTitleLabel = findViewById(R.id.seriesTitleLabel)
        seriesTitle = findViewById(R.id.seriesTitle)
        editBtn = findViewById(R.id.seriesEditBtn)

        searchItem = seriesToolbar.menu.findItem(R.id.seriesSearchMenu)
        searchView = searchItem.actionView as SearchView

        setInitialState()
        setRecyclerView()
        setEditTextListener()
        setSearchAction()
        setToolbarListener()
        setBtnListener()
    }

    private fun setInitialState() {
        seriesTitle.text = newSeriesText

        titleEditText.setText(newSeriesText)
        titleEditText.imeOptions = EditorInfo.IME_ACTION_DONE

        descriptionEditText.setText(newSeriesText)
        descriptionEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        descriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT)
    }

    private fun setRecyclerView() {
        seriesRecyclerView.layoutManager = LinearLayoutManager(this)
        ideaListAdapter.setIdeaClickListener(object : IdeaClickListener {
            var actionMode: ActionMode? = null
            override fun onClick(idea: IdeaData, isActionMode: Boolean, position: Int) {
                if (isActionMode) {
                    ideaListAdapter.toggleItemChecked(position)
                    actionMode?.invalidate()
                } else {
                    val intent = Intent(this@SeriesActivity, MindMapEditActivity::class.java)
                    intent.putExtra("groupId", idea.ideaId)
                    startActivity(intent)
                }
            }

            override fun onLongClick(isActionMode: Boolean, position: Int) {
                if (!isActionMode) {
                    ideaListAdapter.setActionMode(true)
                    ideaListAdapter.toggleItemChecked(position)
                    actionMode = startSupportActionMode(actionModeCallback)
                    actionMode?.invalidate()
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
            SortFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("caller", SortCaller.SERIES)
                    putSerializable("sortData", sortData)
                }
            }.show(supportFragmentManager, "SORT_FRAGMENT")
        }
        seriesRecyclerView.adapter = ideaListAdapter
    }

    private fun setEditTextListener() {
        titleEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
            }
            false
        }

        titleEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !descriptionEditText.hasFocus()) {
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    v.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }

        titleEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(seriesData != null) {
                    editBtn.isEnabled = !(isSameString(seriesData!!.seriesTitle, s)
                            && isSameString(seriesData!!.seriesDescription, descriptionEditText.text))
                }
            }
        })

        descriptionEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    v.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
            false
        }

        descriptionEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus && !titleEditText.hasFocus()) {
                val manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    v.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }

        descriptionEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if(seriesData != null) {
                    editBtn.isEnabled = !(isSameString(seriesData!!.seriesDescription, s)
                            && isSameString(seriesData!!.seriesTitle, titleEditText.text))
                }
            }
        })
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

    private fun setToolbarListener() {
        seriesToolbarLayout.addOnOffsetChangedListener(
            SeriesAppBarListener(
                seriesTitle,
                titleEditText,
                descriptionEditText,
                seriesTitleLabel,
                seriesDescriptionLabel
            )
        )
        seriesToolbar.setNavigationOnClickListener {
            finish()
        }
        seriesToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.seriesDeleteMenu -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.series_delete_dialog_title))
                        .setMessage(getString(R.string.series_delete_dialog_msg))
                        .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                            ideaViewModel.updateSeries(ideaListAdapter.getItemIds())
                            seriesViewModel.delete(seriesData!!)
                            finish()
                        }
                        .show()
                }
            }
            true
        }
    }


    private fun setBtnListener() {
        editBtn.setOnClickListener {
            seriesData?.seriesTitle = titleEditText.text.toString().trim()
            seriesData?.seriesDescription = descriptionEditText.text.toString().trim()
            if(seriesData != null) {
                seriesViewModel.update(seriesData!!)
            }

            titleEditText.clearFocus()
            descriptionEditText.clearFocus()

            Toast.makeText(this, R.string.saved_series, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isSameString(origin: String, newString: Editable?): Boolean {
        return origin == newString.toString().trim()
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