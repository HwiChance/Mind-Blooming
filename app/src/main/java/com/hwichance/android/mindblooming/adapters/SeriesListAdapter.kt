package com.hwichance.android.mindblooming.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.rooms.data.SeriesData

class SeriesListAdapter : RecyclerView.Adapter<SeriesListAdapter.SeriesListViewHolder>() {
    interface ItemClickListener {
        fun onClick(seriesId: Long, isActionMode: Boolean, position: Int)
        fun onLongClick(isActionMode: Boolean, position: Int)
    }

    interface ToggleBtnClickListener {
        fun onClick(isChecked: Boolean, series: SeriesData)
    }

    interface DeleteBtnClickListener {
        fun onClick(series: SeriesData)
    }

    private var seriesList = listOf<SeriesData>()
    private var isActionMode: Boolean = false
    private var isCheckedList = mutableListOf<Boolean>()
    private lateinit var itemClickListener: ItemClickListener
    private lateinit var toggleBtnClickListener: ToggleBtnClickListener
    private lateinit var deleteBtnClickListener: DeleteBtnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.series_list_item_view, parent, false)
        return SeriesListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeriesListViewHolder, position: Int) {
        holder.setView(seriesList[position].seriesTitle, seriesList[position].seriesDescription)
        holder.setCardView(isCheckedList[position])
        holder.setFavoriteBtn(seriesList[position].isFavorite)

        holder.setFavoriteBtnListener(toggleBtnClickListener, seriesList[position])
        holder.setDeleteBtnListener(deleteBtnClickListener, seriesList[position])
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(seriesList[position].seriesId!!, isActionMode, position)
        }
        holder.itemView.setOnLongClickListener {
            itemClickListener.onLongClick(isActionMode, position)
            true
        }

        if (isActionMode) {
            holder.itemView.isLongClickable = false
            holder.setBtnClickable(false)
        } else {
            holder.itemView.isLongClickable = true
            holder.setBtnClickable(true)
        }
    }

    override fun getItemCount(): Int = seriesList.size

    fun setSeriesList(list: List<SeriesData>) {
        seriesList = list
        isCheckedList = MutableList(seriesList.size) { false }
        notifyDataSetChanged()
    }

    fun toggleItemChecked(position: Int) {
        isCheckedList[position] = !isCheckedList[position]
        notifyItemChanged(position)
    }

    fun setActionMode(isMode: Boolean) {
        isActionMode = isMode
        notifyDataSetChanged()
    }

    fun initializeChecked(isChecked: Boolean) {
        for (i in 0 until isCheckedList.size) {
            isCheckedList[i] = isChecked
        }
        notifyDataSetChanged()
    }

    fun getCheckedItemCount(): Int {
        return isCheckedList.count { it }
    }

    fun getCheckedItemIds(): List<Long> {
        val ids = ArrayList<Long>()
        for(i in 0 until isCheckedList.size) {
            if(isCheckedList[i]) {
                ids.add(seriesList[i].seriesId!!)
            }
        }
        return ids.toList()
    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    fun setOnToggleBtnClickListener(listener: ToggleBtnClickListener) {
        toggleBtnClickListener = listener
    }

    fun setOnDeleteBtnClickListener(listener: DeleteBtnClickListener) {
        deleteBtnClickListener = listener
    }

    class SeriesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView = itemView.findViewById<MaterialCardView>(R.id.seriesCard)
        private val titleView = itemView.findViewById<TextView>(R.id.seriesTitleView)
        private val descriptionView = itemView.findViewById<TextView>(R.id.seriesDescriptionView)
        private val favoriteBtn = itemView.findViewById<ToggleButton>(R.id.favoriteBtn)
        private val seriesDeleteBtn = itemView.findViewById<ImageButton>(R.id.seriesDeleteBtn)

        fun setView(title: String, description: String) {
            titleView.text = title
            descriptionView.text = description
        }

        fun setCardView(isSelected: Boolean) {
            cardView.isChecked = isSelected
        }

        fun setFavoriteBtn(isFavorite: Boolean) {
            favoriteBtn.isChecked = isFavorite
        }

        fun setFavoriteBtnListener(listener: ToggleBtnClickListener, series: SeriesData) {
            favoriteBtn.setOnClickListener {
                listener.onClick(favoriteBtn.isChecked, series)
            }
        }

        fun setDeleteBtnListener(listener: DeleteBtnClickListener, series: SeriesData) {
            seriesDeleteBtn.setOnClickListener {
                listener.onClick(series)
            }
        }

        fun setBtnClickable(clickable: Boolean) {
            favoriteBtn.isClickable = clickable
            seriesDeleteBtn.isClickable = clickable
        }
    }
}