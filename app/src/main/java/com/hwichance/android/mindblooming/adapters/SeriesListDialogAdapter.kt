package com.hwichance.android.mindblooming.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.rooms.data.SeriesData

class SeriesListDialogAdapter :
    RecyclerView.Adapter<SeriesListDialogAdapter.SeriesDialogViewHolder>() {
    interface SeriesDialogItemClickListener {
        fun onClick(position: Int)
    }

    private lateinit var clickListener: SeriesDialogItemClickListener
    private var seriesList = listOf<SeriesData>()
    private var selectedIdx: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesDialogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.series_list_dialog_item_view, parent, false)

        return SeriesDialogViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeriesDialogViewHolder, position: Int) {
        if (position == 0) {
            holder.setNoSeriesText()
        } else {
            holder.setTextViewText(seriesList[position - 1].seriesTitle)
        }
        if (position == selectedIdx) {
            holder.setClickedView()
        } else {
            holder.resetView()
        }
        holder.itemView.setOnClickListener {
            clickListener.onClick(position)
        }
    }

    override fun getItemCount(): Int = seriesList.size + 1

    fun setSeriesList(list: List<SeriesData>) {
        seriesList = list
        notifyDataSetChanged()
    }

    fun setSelection(position: Int) {
        val prevIdx = selectedIdx
        selectedIdx = position
        notifyItemChanged(prevIdx)
        notifyItemChanged(selectedIdx)
    }

    fun setInitialSelect(id: Long?) {
        selectedIdx = when (id) {
            null -> 0
            else -> seriesList.indexOfFirst { data -> data.seriesId == id } + 1
        }
        notifyItemChanged(selectedIdx)
    }

    fun setClickListener(listener: SeriesDialogItemClickListener) {
        clickListener = listener
    }

    fun getSelectedSeriesId(): Long? {
        return if (selectedIdx == 0) {
            null
        } else {
            seriesList[selectedIdx - 1].seriesId
        }
    }

    class SeriesDialogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemTextView = itemView.findViewById<TextView>(R.id.seriesDialogItemTextView)

        fun setClickedView() {
            itemTextView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blube))
            itemTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
        }

        fun resetView() {
            itemTextView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.white))
            itemTextView.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
        }

        fun setTextViewText(text: String) {
            itemTextView.text = text
        }

        fun setNoSeriesText() {
            itemTextView.text = itemView.context.getString(R.string.no_series)
        }
    }
}