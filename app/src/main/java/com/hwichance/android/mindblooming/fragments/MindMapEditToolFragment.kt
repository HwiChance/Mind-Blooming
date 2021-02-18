package com.hwichance.android.mindblooming.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.custom_views.FlexibleLayout
import com.hwichance.android.mindblooming.custom_views.flexible_view_use.ItemPosEnum
import com.hwichance.android.mindblooming.custom_views.mind_map_item.MindMapItem
import com.hwichance.android.mindblooming.listeners.MindMapItemClick
import com.hwichance.android.mindblooming.listeners.OnEditTextDialogBtnClick
import com.hwichance.android.mindblooming.utils.DialogUtils

class MindMapEditToolFragment(
    private val mContext: Context,
    private val mItem: MindMapItem,
    private val listener: MindMapItemClick,
    private val mLayout: FlexibleLayout
) : BottomSheetDialogFragment() {
    private lateinit var mindMapAddBtn: ImageButton
    private lateinit var mindMapRemoveBtn: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mind_map_edit_tool, container, false)
        mindMapAddBtn = view.findViewById(R.id.mindMapAddBtn)
        mindMapRemoveBtn = view.findViewById(R.id.mindMapRemoveBtn)

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
            val item = when (mItem.itemPosition) {
                ItemPosEnum.PRIMARY -> {
                    if (mItem.leftTotalHeight < mItem.rightTotalHeight) {
                        MindMapItem(mContext, ItemPosEnum.LEFT, "new child", true)
                    } else {
                        MindMapItem(mContext, ItemPosEnum.RIGHT, "new child", true)
                    }
                }
                ItemPosEnum.LEFT -> {
                    MindMapItem(mContext, ItemPosEnum.LEFT, "new child", true)
                }
                ItemPosEnum.RIGHT -> {
                    MindMapItem(mContext, ItemPosEnum.RIGHT, "new child", true)
                }
            }

            val dialogBtnClickListener = object : OnEditTextDialogBtnClick {
                override fun onClick(text: CharSequence) {
                    item.setItemText(text)
                    item.setOnItemClick(listener)
                    mLayout.addItem(item, mItem)

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
                .setMessage(resources.getString(R.string.delete_dialog_msg))
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_delete) { dialog, which ->
                    when (mItem.itemPosition) {
                        ItemPosEnum.LEFT -> {
                            mLayout.changeParentLeftHeight(
                                mItem,
                                -1 * mItem.leftTotalHeight
                            )
                            mItem.getItemParent()?.getLeftChild()?.remove(mItem)
                        }
                        ItemPosEnum.RIGHT -> {
                            mLayout.changeParentRightHeight(
                                mItem,
                                -1 * mItem.rightTotalHeight
                            )
                            mItem.getItemParent()?.getRightChild()?.remove(mItem)
                        }
                        else -> {

                        }
                    }
                    mLayout.removeChildViews(mItem)
                    dismiss()
                }
                .create()
                .show()
        }
    }
}