package com.hwichance.android.mindblooming

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hwichance.android.mindblooming.enums.DiagramClassEnum
import com.hwichance.android.mindblooming.enums.FilterCaller
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.listeners.SeriesAppBarListener

class SeriesActivity : AppCompatActivity() {
    private lateinit var seriesToolbar: MaterialToolbar
    private lateinit var searchItem: MenuItem
    private lateinit var searchView: SearchView
    private lateinit var seriesRecyclerView: RecyclerView
    private lateinit var seriesFab: FloatingActionButton
    private lateinit var seriesToolbarLayout: AppBarLayout
    private lateinit var seriesTitleEditText: EditText
    private lateinit var seriesDescriptionEditText: EditText
    private lateinit var seriesTitleLabel: TextView
    private lateinit var seriesDescriptionLabel: TextView
    private lateinit var seriesTitle: TextView
    private lateinit var seriesTitleText: String
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
        setContentView(R.layout.activity_series)

        seriesTitleText = intent?.getCharSequenceExtra("seriesTitle").toString()
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
        seriesToolbar = findViewById(R.id.seriesToolbar)
        seriesRecyclerView = findViewById(R.id.seriesRecyclerView)
        seriesToolbarLayout = findViewById(R.id.seriesToolbarLayout)
        seriesTitleEditText = findViewById(R.id.seriesTitleEditText)
        seriesDescriptionEditText = findViewById(R.id.seriesDescriptionEditText)
        seriesDescriptionLabel = findViewById(R.id.seriesDescriptionLabel)
        seriesTitleLabel = findViewById(R.id.seriesTitleLabel)
        seriesTitle = findViewById(R.id.seriesTitle)
        seriesFab = findViewById(R.id.seriesFab)

        searchItem = seriesToolbar.menu.findItem(R.id.seriesSearchMenu)
        searchView = searchItem.actionView as SearchView

        setInitialState()
        setRecyclerView()
        setSearchAction()
        setToolbarListener()
    }

    private fun setInitialState() {
        seriesTitle.text = seriesTitleText
        seriesTitleEditText.setText(seriesTitleText)
        seriesDescriptionEditText.setText(seriesTitleText)
        seriesDescriptionEditText.imeOptions = EditorInfo.IME_ACTION_DONE;
        seriesDescriptionEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
    }

    private fun setRecyclerView() {

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

    private fun setToolbarListener() {
        seriesToolbarLayout.addOnOffsetChangedListener(
            SeriesAppBarListener(
                seriesTitle,
                seriesTitleEditText,
                seriesDescriptionEditText,
                seriesTitleLabel,
                seriesDescriptionLabel
            )
        )
        seriesToolbar.setNavigationOnClickListener {
            finish()
        }
        seriesToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.seriesFilterMenu -> {
                    val filterActIntent = Intent(this, IdeaFilterActivity::class.java)
                    filterActIntent.putExtra("caller", FilterCaller.SERIES)
                    filterActIntent.putExtra("class", classFilter)
                    filterActIntent.putExtra("sort", sortFilter)
                    startForFilterResult.launch(filterActIntent)
                    overridePendingTransition(R.anim.up, R.anim.no_animation)
                }
                R.id.seriesDeleteMenu -> {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.series_delete_dialog_title))
                        .setMessage(getString(R.string.series_delete_dialog_msg))
                        .setNegativeButton(getString(R.string.dialog_cancel)) { dialog, which ->
                            dialog.dismiss()
                        }
                        .setPositiveButton(getString(R.string.dialog_ok)) { dialog, which ->
                            // TODO: delete series at database
                            Toast.makeText(this, "deleted", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .show()
                }
            }
            true
        }
    }

    private fun setFabListener() {
        seriesFab.setOnClickListener {

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