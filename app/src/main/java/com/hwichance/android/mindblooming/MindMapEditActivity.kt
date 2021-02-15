package com.hwichance.android.mindblooming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.hwichance.android.mindblooming.custom_views.FlexibleLayout
import com.hwichance.android.mindblooming.custom_views.flexible_view_use.ItemPosEnum
import com.hwichance.android.mindblooming.custom_views.mind_map_item.MindMapItem
import com.hwichance.android.mindblooming.utils.DialogUtils

class MindMapEditActivity : AppCompatActivity() {
    private lateinit var mindMapEditToolbar: Toolbar
    private lateinit var mindMapTitleTextView: TextView
    private lateinit var editFlexibleLayout: FlexibleLayout

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
        val item9 = MindMapItem(this, ItemPosEnum.RIGHT, "child 9 asdkasdjkasjdkasjdkasjdkasjdkajdkajsdkajsdkajsdkajsdkajsdkjasdkajkdjaksjdaskdj", true)
        val item10 = MindMapItem(this, ItemPosEnum.RIGHT, "child 10\nhi\nhello\nzz", true)

        editFlexibleLayout.addItem(item1, primaryItem, 150, 10)
        editFlexibleLayout.addItem(item2, primaryItem, 150, 10)
        editFlexibleLayout.addItem(item3, primaryItem, 150, 10)
        editFlexibleLayout.addItem(item4, primaryItem, 150, 10)
        editFlexibleLayout.addItem(item5, primaryItem, 150, 10)
        editFlexibleLayout.addItem(item6, primaryItem, 150, 10)
        editFlexibleLayout.addItem(item7, item6, 150, 10)
        editFlexibleLayout.addItem(item10, item7, 150, 10)
        editFlexibleLayout.addItem(item8, item6, 150, 10)
        editFlexibleLayout.addItem(item9, item6, 150, 10)
    }

    private fun setToolbarListener() {
        mindMapEditToolbar.setNavigationOnClickListener {

        }

        mindMapEditToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ideaTitleEditMenu -> {
                    DialogUtils.showEditTitleDialog(
                        this,
                        mindMapTitleTextView.text,
                        mindMapTitleTextView
                    )
                }
                R.id.ideaSaveMenu -> {

                }
            }
            true
        }
    }
}