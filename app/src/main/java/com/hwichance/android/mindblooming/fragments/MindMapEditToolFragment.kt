package com.hwichance.android.mindblooming.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.adapters.ColorPaletteAdapter
import com.hwichance.android.mindblooming.custom_views.FlexibleLayout
import com.hwichance.android.mindblooming.enums.ItemPosEnum
import com.hwichance.android.mindblooming.custom_views.MindMapItem
import com.hwichance.android.mindblooming.dialogs.ColorPaletteDialog
import com.hwichance.android.mindblooming.listeners.OnEditTextDialogBtnClick
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.data.MindMapItemData
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel
import com.hwichance.android.mindblooming.rooms.view_model.MindMapViewModel
import com.hwichance.android.mindblooming.utils.DialogUtils

class MindMapEditToolFragment(
    private val mContext: Context,
    private val mItem: MindMapItem,
    private val mLayout: FlexibleLayout,
    private val groupId: Long
) : BottomSheetDialogFragment() {
    private lateinit var ideaData: IdeaData
    private lateinit var mindMapAddBtn: ImageButton
    private lateinit var mindMapRemoveBtn: ImageButton
    private lateinit var mindMapEditBtn: ImageButton
    private lateinit var mindMapBgColorBtn: ImageButton
    private lateinit var mindMapTxtColorBtn: ImageButton
    private val mindMapViewModel: MindMapViewModel by activityViewModels()
    private val ideaViewModel: IdeaViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mind_map_edit_tool, container, false)
        mindMapAddBtn = view.findViewById(R.id.mindMapAddBtn)
        mindMapRemoveBtn = view.findViewById(R.id.mindMapRemoveBtn)
        mindMapEditBtn = view.findViewById(R.id.mindMapEditBtn)
        mindMapBgColorBtn = view.findViewById(R.id.mindMapBgColorBtn)
        mindMapTxtColorBtn = view.findViewById(R.id.mindMapTxtColorBtn)

        ideaViewModel.findOneIdeaById(groupId).observe(this, { idea ->
            ideaData = idea
        })

        when (mItem.itemPosition) {
            ItemPosEnum.PRIMARY -> {
                mindMapRemoveBtn.visibility = View.GONE
            }
            else -> {

            }
        }

        setBtnClickListener()

        return view
    }

    private fun setBtnClickListener() {
        mindMapAddBtn.setOnClickListener {
            val itemData = MindMapItemData(
                parentId = mItem.getItemData().itemId,
                itemPos = ItemPosEnum.LEFT,
                backgroundColor = getColor(mContext, R.color.crestor),
                textColor = getColor(mContext, R.color.black),
                itemGroup = groupId
            )
            when (mItem.itemPosition) {
                ItemPosEnum.PRIMARY -> {
                    if (mItem.leftChildHeight <= mItem.rightChildHeight) {
                        itemData.itemPos = ItemPosEnum.LEFT
                    } else {
                        itemData.itemPos = ItemPosEnum.RIGHT
                    }
                }
                ItemPosEnum.LEFT -> {
                    itemData.itemPos = ItemPosEnum.LEFT
                }
                ItemPosEnum.RIGHT -> {
                    itemData.itemPos = ItemPosEnum.RIGHT
                }
            }

            val dialogBtnClickListener = object : OnEditTextDialogBtnClick {
                override fun onClick(text: CharSequence) {
                    itemData.itemText = text.toString()
                    mindMapViewModel.insert(itemData) { id ->
                        itemData.itemId = id
                    }
                    updateChangesAndModifiedDate()
                    dismiss()
                }
            }

            DialogUtils.showEditTitleDialog(
                mContext,
                resources.getString(R.string.add_item_dialog_title),
                resources.getString(R.string.item_text_edit_hint),
                "",
                dialogBtnClickListener
            )
        }

        mindMapRemoveBtn.setOnClickListener {
            MaterialAlertDialogBuilder(mContext)
                .setMessage(resources.getString(R.string.item_delete_dialog_msg))
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_delete) { _, _ ->
                    val parentItem = mItem.getItemParent()!!
                    when (mItem.itemPosition) {
                        ItemPosEnum.LEFT -> {
                            var changes = -mItem.leftTotalHeight
                            if (parentItem.getLeftChildSize() > 1) {
                                changes -= mLayout.verInterval
                            }
                            parentItem.leftChildHeight += changes
                            mLayout.changeParentLeftHeight(parentItem)
                            parentItem.getLeftChild().remove(mItem)
                        }
                        ItemPosEnum.RIGHT -> {
                            var changes = -mItem.rightTotalHeight
                            if (parentItem.getRightChildSize() > 1) {
                                changes -= mLayout.verInterval
                            }
                            parentItem.rightChildHeight += changes
                            mLayout.changeParentRightHeight(parentItem)
                            parentItem.getRightChild().remove(mItem)
                        }
                        else -> {

                        }
                    }
                    removeFromDB(mItem)
                    updateChangesAndModifiedDate()
                    mLayout.removeChildViews(mItem)

                    dismiss()
                }
                .create()
                .show()
        }

        mindMapEditBtn.setOnClickListener {
            val dialogBtnClickListener = object : OnEditTextDialogBtnClick {
                override fun onClick(text: CharSequence) {
                    mItem.getItemData().itemText = text.toString()
                    mindMapViewModel.update(mItem.getItemData())
                    updateChangesAndModifiedDate()
                    dismiss()
                }
            }

            DialogUtils.showEditTitleDialog(
                mContext,
                resources.getString(R.string.edit_item_text_dialog_title),
                resources.getString(R.string.item_text_edit_hint),
                mItem.getItemText(),
                dialogBtnClickListener
            )
        }

        mindMapBgColorBtn.setOnClickListener {
            val builder = ColorPaletteDialog(mContext)
            val dialog = builder.setTitle(R.string.background_color_dialog_title)
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
            builder.adapter.setItemClickListener(object : ColorPaletteAdapter.ItemClickListener {
                override fun onClick(color: Int) {
                    mItem.getItemData().backgroundColor = color
                    mindMapViewModel.update(mItem.getItemData())
                    updateChangesAndModifiedDate()
                    dialog.dismiss()
                    dismiss()
                }
            })
            dialog.show()
        }

        mindMapTxtColorBtn.setOnClickListener {
            val builder = ColorPaletteDialog(mContext)
            val dialog = builder.setTitle(R.string.text_color_dialog_title)
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
            builder.adapter.setItemClickListener(object : ColorPaletteAdapter.ItemClickListener {
                override fun onClick(color: Int) {
                    mItem.getItemData().textColor = color
                    mindMapViewModel.update(mItem.getItemData())
                    updateChangesAndModifiedDate()
                    dialog.dismiss()
                    dismiss()
                }
            })
            dialog.show()
        }
    }

    private fun removeFromDB(item: MindMapItem) {
        for (child in item.getLeftChild()) {
            removeFromDB(child)
        }
        for (child in item.getRightChild()) {
            removeFromDB(child)
        }
        mLayout.getItemList().remove(item)
        mindMapViewModel.delete(item.getItemData())
    }

    private fun updateChangesAndModifiedDate() {
        ideaData.modifiedDate = System.currentTimeMillis()
        ideaViewModel.update(ideaData)
    }
}