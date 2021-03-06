package com.hwichance.android.mindblooming.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R

class ColorPaletteAdapter(private var paletteColors: IntArray) :
    RecyclerView.Adapter<ColorPaletteAdapter.ColorPaletteViewHolder>() {

    interface ItemClickListener {
        fun onClick(color: Int)
    }

    interface SeeMoreBtnClickListener {
        fun onClick()
    }

    private lateinit var itemClickListener: ItemClickListener
    private lateinit var seeMoreBtnClickListener: SeeMoreBtnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorPaletteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.color_palette_view, parent, false)
        return ColorPaletteViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorPaletteViewHolder, position: Int) {
        if (position != paletteColors.size) {
            holder.setBackgroundColor(paletteColors[position])
            holder.itemView.setOnClickListener {
                itemClickListener.onClick(paletteColors[position])
            }
        } else {
            holder.setSeeMoreBtn()
            holder.itemView.setOnClickListener {
                seeMoreBtnClickListener.onClick()
            }
        }
    }

    override fun getItemCount(): Int = (paletteColors.size + 1)

    fun setItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    fun setSeeMoreBtnClickListener(listener: SeeMoreBtnClickListener) {
        seeMoreBtnClickListener = listener
    }

    class ColorPaletteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var colorPickBtn = itemView.findViewById<View>(R.id.colorPickBtn)
        private val btnShape =
            getDrawable(itemView.context, R.drawable.color_palette_view_shape) as GradientDrawable

        fun setBackgroundColor(color: Int) {
            btnShape.setColor(color)
            colorPickBtn.background = btnShape
        }

        fun setSeeMoreBtn() {
            val seeMoreBtnShape = getDrawable(itemView.context, R.drawable.ic_add_circle_32dp)
            colorPickBtn.background = seeMoreBtnShape
        }
    }
}