package com.hwichance.android.mindblooming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.hwichance.android.mindblooming.custom_views.FlexibleLayout
import com.hwichance.android.mindblooming.custom_views.flexible_view_use.ItemPosEnum
import com.hwichance.android.mindblooming.custom_views.mind_map_item.MindMapItem
import com.hwichance.android.mindblooming.fragments.MindMapEditToolFragment
import com.hwichance.android.mindblooming.listeners.MindMapItemClick
import com.hwichance.android.mindblooming.listeners.OnEditTextDialogBtnClick
import com.hwichance.android.mindblooming.utils.DialogUtils

class MindMapEditActivity : AppCompatActivity() {
    private lateinit var mindMapEditToolbar: Toolbar
    private lateinit var mindMapTitleTextView: TextView
    private lateinit var editFlexibleLayout: FlexibleLayout

    private val itemClickListener = object : MindMapItemClick {
        override fun onClick(item: MindMapItem) {
            MindMapEditToolFragment(this@MindMapEditActivity, item, this, editFlexibleLayout)
                .show(supportFragmentManager, "MIND_MAP_EDIT_TOOL_FRAGMENT")
        }
    }

    private val dialogBtnClickListener = object : OnEditTextDialogBtnClick {
        override fun onClick(text: CharSequence) {
            mindMapTitleTextView.text = text
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_map_edit)

        bindViews()
    }

    private fun bindViews() {
        mindMapEditToolbar = findViewById(R.id.mindMapEditToolbar)
        mindMapTitleTextView = findViewById(R.id.mindMapTitleTextView)
        editFlexibleLayout = findViewById(R.id.editFlexibleLayout)

        setInitialState()
        setToolbarListener()
    }

    private fun setInitialState() {
        mindMapTitleTextView.text = getString(R.string.new_mind_map)

        val primaryItem = MindMapItem(this, ItemPosEnum.PRIMARY, "hello world", true)
        editFlexibleLayout.addPrimaryItem(primaryItem)
        val item1 = MindMapItem(this, ItemPosEnum.LEFT, "child 1", true)
        val item2 = MindMapItem(this, ItemPosEnum.LEFT, "child 2", true)
        val item3 = MindMapItem(this, ItemPosEnum.LEFT, "child 3", true)
        val item4 = MindMapItem(this, ItemPosEnum.LEFT, "child 4", true)
        val item5 = MindMapItem(this, ItemPosEnum.RIGHT, "child 5", true)
        val item6 = MindMapItem(this, ItemPosEnum.RIGHT, "child 6", true)
        val item7 = MindMapItem(this, ItemPosEnum.RIGHT, "child 7", true)
        val item8 = MindMapItem(this, ItemPosEnum.RIGHT, "child 8", true)
        val item9 = MindMapItem(
            this,
            ItemPosEnum.RIGHT,
            "child 9 asdkasdjkasjdkasjdkasjdkasjdkajdkajsdkajsdkajsdkajsdkajsdkjasdkajkdjaksjdaskdj",
            true
        )
        val item10 = MindMapItem(this, ItemPosEnum.RIGHT, "child 10\nhi\nhello\nzz", true)
        primaryItem.setOnItemClick(itemClickListener)
        editFlexibleLayout.addItem(item1, primaryItem)
        editFlexibleLayout.addItem(item2, primaryItem)
        editFlexibleLayout.addItem(item3, primaryItem)
        editFlexibleLayout.addItem(item4, primaryItem)
        editFlexibleLayout.addItem(item5, primaryItem)
        editFlexibleLayout.addItem(item6, primaryItem)
        editFlexibleLayout.addItem(item7, item6)
        editFlexibleLayout.addItem(item10, item7)
        editFlexibleLayout.addItem(item8, item6)
        editFlexibleLayout.addItem(item9, item6)
    }

    private fun setToolbarListener() {
        mindMapEditToolbar.setNavigationOnClickListener {

        }

        mindMapEditToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ideaTitleEditMenu -> {
                    DialogUtils.showEditTitleDialog(
                        this,
                        resources.getString(R.string.edit_title_dialog_title),
                        resources.getString(R.string.title_edit_hint),
                        mindMapTitleTextView.text,
                        dialogBtnClickListener
                    )
                }
                R.id.ideaSaveMenu -> {

                }
            }
            true
        }
    }
}