package com.hwichance.android.mindblooming.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.rooms.data.ColorData

class ColorPickerAdapter : RecyclerView.Adapter<ColorPickerAdapter.ColorPickerViewHolder>() {
    interface ColorItemClickListener {
        fun onClick(color: Int)
    }

    private var colorList = listOf<ColorData>()
    private var defaultColor: Int = 0
    private lateinit var colorItemClickListener: ColorItemClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPickerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.color_picker_view, parent, false)
        defaultColor = ContextCompat.getColor(view.context, R.color.white)
        return ColorPickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorPickerViewHolder, position: Int) {
        if (position < colorList.size) {
            holder.setBackgroundColor(colorList[position].color)
            holder.itemView.isClickable = true
            holder.itemView.setOnClickListener {
                colorItemClickListener.onClick(colorList[position].color)
            }
        } else {
            holder.setBackgroundColor(defaultColor)
            holder.itemView.isClickable = false
        }
    }

    override fun getItemCount(): Int = 8

    fun setPickedColorList(list: List<ColorData>) {
        colorList = list
        notifyDataSetChanged()
    }

    fun setColorItemClickListener(listener: ColorItemClickListener) {
        colorItemClickListener = listener
    }

    class ColorPickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var pickedColorBtn = itemView.findViewById<View>(R.id.pickedColorBtn)
        private val btnShape =
            getDrawable(itemView.context, R.drawable.color_palette_view_stroke_shape) as GradientDrawable

        fun setBackgroundColor(color: Int) {
            btnShape.setColor(color)
            pickedColorBtn.background = btnShape
        }
    }
}