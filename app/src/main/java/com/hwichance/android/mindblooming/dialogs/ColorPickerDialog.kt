package com.hwichance.android.mindblooming.dialogs

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.adapters.ColorPickerAdapter
import com.hwichance.android.mindblooming.adapters.ColorPickerAdapter.ColorItemClickListener
import com.hwichance.android.mindblooming.rooms.data.ColorData
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.data.MindMapItemData
import com.hwichance.android.mindblooming.rooms.view_model.ColorViewModel
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel
import com.hwichance.android.mindblooming.rooms.view_model.MindMapViewModel
import top.defaults.colorpicker.*

class ColorPickerDialog(
    private val mindMapItemData: MindMapItemData,
    private val ideaData: IdeaData,
    private var pickedColor: Int,
    private var isBackground: Boolean
) : DialogFragment() {
    private lateinit var colorPicker: ColorPickerView
    private lateinit var pickedColorView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var negativeBtn: Button
    private lateinit var positiveBtn: Button
    private lateinit var btnShape: GradientDrawable
    private var colorList = listOf<ColorData>()
    private val colorPickerAdapter: ColorPickerAdapter by lazy {
        ColorPickerAdapter()
    }
    private val mindMapViewModel: MindMapViewModel by activityViewModels()
    private val ideaViewModel: IdeaViewModel by activityViewModels()
    private val colorViewModel: ColorViewModel by viewModels()

    override fun onResume() {
        super.onResume()

        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        params?.width = (size.x * 0.9).toInt()
        params?.height = (size.y * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.color_picker_dialog, container, false)
        colorPicker = view.findViewById(R.id.colorPicker)
        pickedColorView = view.findViewById(R.id.pickedColorView)
        recyclerView = view.findViewById(R.id.pickedColorRecyclerView)
        negativeBtn = view.findViewById(R.id.colorPickerCancel)
        positiveBtn = view.findViewById(R.id.colorPickerApply)

        btnShape = ContextCompat.getDrawable(
            view.context,
            R.drawable.color_palette_view_stroke_shape
        ) as GradientDrawable

        colorViewModel.getData().observe(this, { list ->
            colorList = list
            if (colorList.isNotEmpty()) {
                colorPickerAdapter.setPickedColorList(colorList)
            }
        })

        setColorPicker()
        setRecyclerView()
        setBtnClick()
        return view
    }

    private fun setColorPicker() {
        colorPicker.setInitialColor(pickedColor);
        colorPicker.subscribe { color, _, _ ->
            pickedColor = color
            btnShape.setColor(pickedColor)
            pickedColorView.background = btnShape
        }
    }

    private fun setRecyclerView() {
        colorPickerAdapter.setColorItemClickListener(object : ColorItemClickListener {
            override fun onClick(color: Int) {
                pickedColor = color
                applyColorChange()
                dismiss()
            }
        })
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        recyclerView.adapter = colorPickerAdapter
    }

    private fun setBtnClick() {
        negativeBtn.setOnClickListener {
            dismiss()
        }

        positiveBtn.setOnClickListener {
            applyColorChange()
            dismiss()
        }
    }

    private fun applyColorChange() {
        val colorData = ColorData(pickedColor, System.currentTimeMillis())
        if (colorList.size > 8) {
            colorViewModel.insertNewDeleteOld(colorData, colorList.last())
        } else {
            colorViewModel.insert(colorData)
        }
        if (isBackground) {
            mindMapItemData.backgroundColor = pickedColor
            mindMapViewModel.update(mindMapItemData)
        } else {
            mindMapItemData.textColor = pickedColor
            mindMapViewModel.update(mindMapItemData)
        }
        ideaData.modifiedDate = System.currentTimeMillis()
        ideaViewModel.update(ideaData)
    }
}