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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.adapters.ColorPickerAdapter
import com.hwichance.android.mindblooming.adapters.ColorPickerAdapter.ColorItemClickListener
import com.hwichance.android.mindblooming.rooms.data.ColorData
import com.hwichance.android.mindblooming.rooms.view_model.ColorViewModel
import top.defaults.colorpicker.*

class ColorPickerDialog : DialogFragment() {
    interface ColorSelectListener {
        fun onSelect(color: Int)
    }

    private var colorSelectListener: ColorSelectListener? = null
    private var pickedColor: Int = 0
    private var isBackground: Boolean = true
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
    private val colorViewModel: ColorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pickedColor = it.getInt("color")
            isBackground = it.getBoolean("isBackground")
        }
    }

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
        colorPicker.setInitialColor(pickedColor)
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
                saveColorUsage()
                colorSelectListener?.onSelect(pickedColor)
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
            saveColorUsage()
            colorSelectListener?.onSelect(pickedColor)
            dismiss()
        }
    }

    fun setColorSelectListener(listener: ColorSelectListener) {
        colorSelectListener = listener
    }

    private fun saveColorUsage() {
        val colorData = ColorData(pickedColor, System.currentTimeMillis())
        if (colorList.size > 8) {
            colorViewModel.insertNewDeleteOld(colorData, colorList.last())
        } else {
            colorViewModel.insert(colorData)
        }
    }
}