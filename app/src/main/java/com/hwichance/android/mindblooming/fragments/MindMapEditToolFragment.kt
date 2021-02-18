package com.hwichance.android.mindblooming.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.custom_views.FlexibleLayout
import com.hwichance.android.mindblooming.custom_views.flexible_view_use.ItemPosEnum
import com.hwichance.android.mindblooming.custom_views.mind_map_item.MindMapItem
import com.hwichance.android.mindblooming.listeners.MindMapItemClick

class MindMapEditToolFragment(
    private val mContext: Context,
    private val parentItem: MindMapItem,
    private val listener: MindMapItemClick,
    private val layout: FlexibleLayout
) :
    BottomSheetDialogFragment() {
    private lateinit var mindMapLeftAddBtn: ImageButton
    private lateinit var mindMapRightAddBtn: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mind_map_edit_tool, container, false)
        mindMapLeftAddBtn = view.findViewById(R.id.mindMapLeftAddBtn)
        mindMapRightAddBtn = view.findViewById(R.id.mindMapRightAddBtn)

        setBtnClickListener()

        when (parentItem.itemPosition) {
            ItemPosEnum.LEFT -> mindMapRightAddBtn.visibility = View.GONE
            ItemPosEnum.RIGHT -> mindMapLeftAddBtn.visibility = View.GONE
            else -> {
            }
        }
        return view
    }

    private fun setBtnClickListener() {
        mindMapLeftAddBtn.setOnClickListener {
            val item = MindMapItem(mContext, ItemPosEnum.LEFT, "new child", true)
            item.setOnItemClick(listener)
            layout.addItem(item, parentItem, 150, 10)
            dismiss()
        }
        mindMapRightAddBtn.setOnClickListener {
            val item = MindMapItem(mContext, ItemPosEnum.RIGHT, "new child", true)
            item.setOnItemClick(listener)
            layout.addItem(item, parentItem, 150, 10)
            dismiss()
        }
    }
}