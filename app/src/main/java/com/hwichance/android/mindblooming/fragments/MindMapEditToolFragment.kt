package com.hwichance.android.mindblooming.fragments

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.adapters.ColorPaletteAdapter
import com.hwichance.android.mindblooming.custom_views.FlexibleLayout
import com.hwichance.android.mindblooming.custom_views.flexible_view_use.ItemPosEnum
import com.hwichance.android.mindblooming.custom_views.mind_map_item.MindMapItem
import com.hwichance.android.mindblooming.dialogs.ColorPaletteDialog
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
    private lateinit var mindMapEditBtn: ImageButton
    private lateinit var mindMapBgColorBtn: ImageButton
    private lateinit var mindMapTxtColorBtn: ImageButton

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
                        MindMapItem(mContext, ItemPosEnum.LEFT, "", true)
                    } else {
                        MindMapItem(mContext, ItemPosEnum.RIGHT, "", true)
                    }
                }
                ItemPosEnum.LEFT -> {
                    MindMapItem(mContext, ItemPosEnum.LEFT, "", true)
                }
                ItemPosEnum.RIGHT -> {
                    MindMapItem(mContext, ItemPosEnum.RIGHT, "", true)
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
                    val parentItem = mItem.getItemParent()!!
                    when (mItem.itemPosition) {
                        ItemPosEnum.LEFT -> {
                            var changes = -(mItem.leftTotalHeight + mLayout.verInterval)
                            if (parentItem.getLeftChildSize() == 1) {
                                changes = parentItem.leftTotalHeight - parentItem.measuredHeight
                            }
                            if (changes != 0) {
                                mLayout.changeParentLeftHeight(mItem, changes)
                            }
                            parentItem.getLeftChild().remove(mItem)
                        }
                        ItemPosEnum.RIGHT -> {
                            var changes = -(mItem.rightTotalHeight + mLayout.verInterval)
                            if (parentItem.getRightChildSize() == 1) {
                                changes = parentItem.rightTotalHeight - parentItem.measuredHeight
                            }
                            if (changes != 0) {
                                mLayout.changeParentRightHeight(mItem, changes)
                            }
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

        mindMapEditBtn.setOnClickListener {
            val dialogBtnClickListener = object : OnEditTextDialogBtnClick {
                override fun onClick(text: CharSequence) {
                    val prevHeight = mItem.measuredHeight
                    mItem.setItemText(text)
                    mItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    val nowHeight = mItem.measuredHeight
                    when (mItem.itemPosition) {
                        ItemPosEnum.LEFT -> {
                            var heightIncrease = 0
                            if (mItem.leftTotalHeight == prevHeight) {
                                if (prevHeight > nowHeight && mItem.getLeftChildSize() == 0) {
                                    heightIncrease = nowHeight - prevHeight
                                } else if (prevHeight < nowHeight) {
                                    heightIncrease = nowHeight - prevHeight
                                }
                            } else if (mItem.leftTotalHeight > prevHeight) {
                                if (mItem.leftTotalHeight < nowHeight) {
                                    heightIncrease = nowHeight - mItem.leftTotalHeight
                                }
                            }

                            if (heightIncrease != 0) {
                                mLayout.changeParentLeftHeight(mItem, heightIncrease)
                            }
                        }
                        ItemPosEnum.RIGHT -> {
                            var heightIncrease = 0
                            if (mItem.rightTotalHeight == prevHeight) {
                                if (prevHeight > nowHeight && mItem.getRightChildSize() == 0) {
                                    heightIncrease = nowHeight - prevHeight
                                } else if (prevHeight < nowHeight) {
                                    heightIncrease = nowHeight - prevHeight
                                }
                            } else if (mItem.rightTotalHeight > prevHeight) {
                                if (mItem.rightTotalHeight < nowHeight) {
                                    heightIncrease = nowHeight - mItem.rightTotalHeight
                                }
                            }

                            if (heightIncrease != 0) {
                                mLayout.changeParentRightHeight(mItem, heightIncrease)
                            }
                        }
                        else -> {

                        }
                    }
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
            val shape = if (mItem.itemPosition == ItemPosEnum.PRIMARY) {
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.primary_mind_map_item_shape
                ) as GradientDrawable
            } else {
                ContextCompat.getDrawable(
                    mContext,
                    R.drawable.mind_map_item_shape
                ) as GradientDrawable
            }
            val builder = ColorPaletteDialog(mContext)
            val dialog = builder.setTitle(R.string.background_color_dialog_title)
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
            builder.adapter.setItemClickListener(object : ColorPaletteAdapter.ItemClickListener {
                override fun onClick(color: Int) {
                    shape.setColor(color)
                    mItem.background = shape
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
                    mItem.getItemTextView().setTextColor(color)
                    dialog.dismiss()
                    dismiss()
                }
            })
            dialog.show()
        }
    }
}