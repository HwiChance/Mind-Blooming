package com.hwichance.android.mindblooming.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R

class ColorPaletteAdapter(private var paletteColors: IntArray) :
    RecyclerView.Adapter<ColorPaletteAdapter.ColorPaletteViewHolder>() {

    interface ItemClickListener {
        fun onClick(color: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

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
        }
    }

    override fun getItemCount(): Int = (paletteColors.size + 1)

    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }

    class ColorPaletteViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {
        private var colorPickBtn = view.findViewById<View>(R.id.colorPickBtn)

        fun setBackgroundColor(color: Int) {
            val btnShape =
                getDrawable(view.context, R.drawable.color_palette_view_shape) as GradientDrawable
            btnShape.setColor(color)
            colorPickBtn.background = btnShape
        }

        fun setSeeMoreBtn() {
            val btnShape = getDrawable(view.context, R.drawable.ic_add_circle_32dp)
            colorPickBtn.background = btnShape
        }
    }
}