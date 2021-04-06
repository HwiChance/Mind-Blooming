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
import com.hwichance.android.mindblooming.adapters.ColorPaletteAdapter.*
import com.hwichance.android.mindblooming.enums.ItemPosEnum
import com.hwichance.android.mindblooming.dialogs.ColorPaletteDialog
import com.hwichance.android.mindblooming.dialogs.ColorPickerDialog
import com.hwichance.android.mindblooming.listeners.OnEditTextDialogBtnClick
import com.hwichance.android.mindblooming.rooms.data.MindMapItemData
import com.hwichance.android.mindblooming.utils.DialogUtils

class MindMapEditToolFragment : BottomSheetDialogFragment() {
    interface ToolListener {
        fun onAdd(text: String)
        fun onDelete()
        fun onEditText(text: String)
        fun onEditBackgroundColor(color: Int)
        fun onEditTextColor(color: Int)
    }

    private lateinit var itemData: MindMapItemData
    private lateinit var mindMapAddBtn: ImageButton
    private lateinit var mindMapRemoveBtn: ImageButton
    private lateinit var mindMapEditBtn: ImageButton
    private lateinit var mindMapBgColorBtn: ImageButton
    private lateinit var mindMapTxtColorBtn: ImageButton

    companion object {
        lateinit var toolListener: ToolListener
        fun newInstance(
            itemData: MindMapItemData,
            listener: ToolListener
        ): MindMapEditToolFragment {
            toolListener = listener
            return MindMapEditToolFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("itemData", itemData)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemData = it.getSerializable("itemData") as MindMapItemData
        }
    }

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

        if (itemData.itemPos == ItemPosEnum.PRIMARY) {
            mindMapRemoveBtn.visibility = View.GONE
        }

        setBtnClickListener(view.context)

        return view
    }

    private fun setBtnClickListener(context: Context) {
        mindMapAddBtn.setOnClickListener {
            val dialogBtnClickListener = object : OnEditTextDialogBtnClick {
                override fun onClick(text: CharSequence) {
                    toolListener.onAdd(text.toString())
                    dismiss()
                }
            }

            DialogUtils.showEditTitleDialog(
                context,
                resources.getString(R.string.add_item_dialog_title),
                resources.getString(R.string.item_text_edit_hint),
                "",
                dialogBtnClickListener
            )
        }

        mindMapRemoveBtn.setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setMessage(resources.getString(R.string.item_delete_dialog_msg))
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_delete) { _, _ ->
                    toolListener.onDelete()
                    dismiss()
                }
                .create()
                .show()
        }

        mindMapEditBtn.setOnClickListener {
            val dialogBtnClickListener = object : OnEditTextDialogBtnClick {
                override fun onClick(text: CharSequence) {
                    toolListener.onEditText(text.toString())
                    dismiss()
                }
            }

            DialogUtils.showEditTitleDialog(
                context,
                resources.getString(R.string.edit_item_text_dialog_title),
                resources.getString(R.string.item_text_edit_hint),
                itemData.itemText,
                dialogBtnClickListener
            )
        }

        mindMapBgColorBtn.setOnClickListener {
            val builder = ColorPaletteDialog(context)
            val dialog = builder.setTitle(R.string.background_color_dialog_title)
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
            builder.adapter.setItemClickListener(object : ItemClickListener {
                override fun onClick(color: Int) {
                    toolListener.onEditBackgroundColor(color)
                    dialog.dismiss()
                    dismiss()
                }
            })
            builder.adapter.setSeeMoreBtnClickListener(object : SeeMoreBtnClickListener {
                override fun onClick() {
                    ColorPickerDialog().apply {
                        arguments = Bundle().apply {
                            putInt("color", itemData.backgroundColor)
                            putBoolean("isBackground", true)
                        }

                        setColorSelectListener(object : ColorPickerDialog.ColorSelectListener {
                            override fun onSelect(color: Int) {
                                toolListener.onEditBackgroundColor(color)
                                this@MindMapEditToolFragment.dismiss()
                            }
                        })
                    }.show(
                        parentFragmentManager,
                        "COLOR_PICKER_DIALOG"
                    )
                    dialog.dismiss()
                }
            })
            dialog.show()
        }

        mindMapTxtColorBtn.setOnClickListener {
            val builder = ColorPaletteDialog(context)
            val dialog = builder.setTitle(R.string.text_color_dialog_title)
                .setNegativeButton(R.string.dialog_cancel, null)
                .create()
            builder.adapter.setItemClickListener(object : ItemClickListener {
                override fun onClick(color: Int) {
                    toolListener.onEditTextColor(color)
                    dialog.dismiss()
                    dismiss()
                }
            })
            builder.adapter.setSeeMoreBtnClickListener(object : SeeMoreBtnClickListener {
                override fun onClick() {
                    ColorPickerDialog().apply {
                        arguments = Bundle().apply {
                            putInt("color", itemData.textColor)
                            putBoolean("isBackground", false)
                        }

                        setColorSelectListener(object : ColorPickerDialog.ColorSelectListener {
                            override fun onSelect(color: Int) {
                                toolListener.onEditTextColor(color)
                                this@MindMapEditToolFragment.dismiss()
                            }
                        })
                    }.show(
                        parentFragmentManager,
                        "COLOR_PICKER_DIALOG"
                    )
                    dialog.dismiss()
                }
            })
            dialog.show()
        }
    }
}