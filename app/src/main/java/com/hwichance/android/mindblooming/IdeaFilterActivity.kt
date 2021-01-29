package com.hwichance.android.mindblooming

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hwichance.android.mindblooming.enums.DiagramClassEnum
import com.hwichance.android.mindblooming.enums.FilterCaller
import com.hwichance.android.mindblooming.enums.SortEnum

class IdeaFilterActivity : AppCompatActivity() {
    private lateinit var filterToolbar: MaterialToolbar
    private lateinit var classChipGroup: ChipGroup
    private lateinit var sortChipGroup: ChipGroup
    private lateinit var allChip: Chip
    private lateinit var mindMapChip: Chip
    private lateinit var flowChart: Chip
    private lateinit var titleChip: Chip
    private lateinit var dateChip: Chip
    private lateinit var starredChip: Chip
    private lateinit var seriesChip: Chip
    private lateinit var resetBtn: Button
    private lateinit var caller: FilterCaller
    private lateinit var classFilter: DiagramClassEnum
    private lateinit var sortFilter: SortEnum

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_idea_filter)

        bindViews()

        caller = intent?.getSerializableExtra("caller") as FilterCaller
        classFilter = intent?.getSerializableExtra("class") as DiagramClassEnum
        sortFilter = intent?.getSerializableExtra("sort") as SortEnum
        setChips()
    }

    private fun bindViews() {
        filterToolbar = findViewById(R.id.filterToolbar)
        classChipGroup = findViewById(R.id.classChipGroup)
        sortChipGroup = findViewById(R.id.sortChipGroup)
        allChip = findViewById(R.id.classAllChip)
        mindMapChip = findViewById(R.id.classMindMapChip)
        flowChart = findViewById(R.id.classFlowChartChip)
        titleChip = findViewById(R.id.sortTitleChip)
        dateChip = findViewById(R.id.sortDateChip)
        starredChip = findViewById(R.id.sortStarredChip)
        seriesChip = findViewById(R.id.sortSeriesChip)
        resetBtn = findViewById(R.id.filterResetBtn)

        setToolbarListener()
        setResetClickListener()
    }

    private fun setToolbarListener() {
        filterToolbar.setNavigationOnClickListener {
            closeActivity()
        }

        filterToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.filterApplyMenu -> {
                    applyAndClose(getCheckedClass(), getCheckedSort())
                }
            }
            true
        }
    }

    private fun getCheckedClass(): DiagramClassEnum {
        return when (classChipGroup.checkedChipId) {
            R.id.classMindMapChip -> DiagramClassEnum.MIND_MAP
            R.id.classFlowChartChip -> DiagramClassEnum.FLOW_CHART
            else -> DiagramClassEnum.ALL
        }
    }

    private fun getCheckedSort(): SortEnum {
        return when (sortChipGroup.checkedChipId) {
            R.id.sortDateChip -> SortEnum.LAST_MODIFIED_DATE
            R.id.sortStarredChip -> SortEnum.STARRED_DATE
            R.id.sortSeriesChip -> SortEnum.SERIES_ADDED_DATE
            else -> SortEnum.TITLE
        }
    }

    private fun setChips() {
        when (caller) {
            FilterCaller.MAIN -> {
                starredChip.visibility = View.GONE
                seriesChip.visibility = View.GONE
            }
            FilterCaller.STARRED -> seriesChip.visibility = View.GONE
            FilterCaller.SERIES -> starredChip.visibility = View.GONE
        }

        when (classFilter) {
            DiagramClassEnum.ALL -> classChipGroup.check(R.id.classAllChip)
            DiagramClassEnum.MIND_MAP -> classChipGroup.check(R.id.classMindMapChip)
            DiagramClassEnum.FLOW_CHART -> classChipGroup.check(R.id.classFlowChartChip)
        }

        when (sortFilter) {
            SortEnum.TITLE -> sortChipGroup.check(R.id.sortTitleChip)
            SortEnum.LAST_MODIFIED_DATE -> sortChipGroup.check(R.id.sortDateChip)
            SortEnum.STARRED_DATE -> sortChipGroup.check(R.id.sortStarredChip)
            SortEnum.SERIES_ADDED_DATE -> sortChipGroup.check(R.id.sortSeriesChip)
        }
    }

    private fun setResetClickListener() {
        resetBtn.setOnClickListener {
            classChipGroup.check(R.id.classAllChip)
            sortChipGroup.check(R.id.sortTitleChip)
        }
    }

    private fun applyAndClose(checkedClass: DiagramClassEnum, checkedSort: SortEnum) {
        val applyIntent = Intent()
        applyIntent.putExtra("classFilter", checkedClass)
        applyIntent.putExtra("sortFilter", checkedSort)

        setResult(RESULT_OK, applyIntent)
        finish()
    }

    private fun closeActivity() {
        val checkedClass = getCheckedClass()
        val checkedSort = getCheckedSort()
        if (classFilter == checkedClass && sortFilter == checkedSort) {
            setResult(RESULT_CANCELED)
            finish()
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.filter_dialog_title))
                .setMessage(getString(R.string.filter_dialog_msg))
                .setNeutralButton(getString(R.string.dialog_cancel)) { dialog, which ->
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.dialog_discard)) { dialog, which ->
                    setResult(RESULT_CANCELED)
                    finish()
                }
                .setPositiveButton(getString(R.string.dialog_apply)) { dialog, which ->
                    applyAndClose(checkedClass, checkedSort)
                }
                .show()
        }
    }

    override fun onBackPressed() {
        closeActivity()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_animation, R.anim.down)
    }
}