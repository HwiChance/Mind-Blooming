package com.hwichance.android.mindblooming.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.enums.OrderEnum
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.rooms.data.SortData

class SortListAdapter : RecyclerView.Adapter<SortListAdapter.SortViewHolder>() {
    interface SortItemClickListener {
        fun onClick(sortEnum: SortEnum, orderEnum: OrderEnum, position: Int)
    }

    private var sortOrderList = ArrayList<Pair<SortEnum, String>>()
    private var isSelectedList = mutableListOf<Boolean>()
    private var selectedIdx = 0
    private lateinit var sortData: SortData
    private lateinit var sortItemClickListener: SortItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sort_item_view, parent, false)
        return SortViewHolder(view)
    }

    override fun onBindViewHolder(holder: SortViewHolder, position: Int) {
        holder.setSortTextView(sortOrderList[position].second)
        holder.setSortItemClickListener(
            sortItemClickListener,
            isSelectedList[position],
            sortOrderList[position].first,
            position
        )
        if (isSelectedList[position]) {
            holder.setItemSelect(true)
            holder.setOrder(sortData.orderEnum)
            holder.setArrowVisibility(true)
        } else {
            holder.setItemSelect(false)
            holder.setOrder(OrderEnum.ASC)
            holder.setArrowVisibility(false)
        }
    }

    override fun getItemCount(): Int = sortOrderList.size

    fun setSortOrderList(list: ArrayList<Pair<SortEnum, String>>, data: SortData) {
        sortOrderList = list
        isSelectedList = MutableList(sortOrderList.size) { false }
        selectedIdx = sortOrderList.indexOfFirst { pair -> pair.first == data.sortEnum }
        isSelectedList[selectedIdx] = true
        sortData = data
    }

    fun setSelect(position: Int) {
        isSelectedList = MutableList(sortOrderList.size) { false }
        isSelectedList[position] = true
    }

    fun setSortItemClickListener(listener: SortItemClickListener) {
        sortItemClickListener = listener
    }

    class SortViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sortItemLayout = itemView.findViewById<LinearLayout>(R.id.sortItemLayout)
        private val sortTextView = itemView.findViewById<TextView>(R.id.sortTextView)
        private val sortImageView = itemView.findViewById<ImageView>(R.id.sortImageView)
        private val upArrow = getDrawable(itemView.context, R.drawable.ic_arrow_upward_20dp)
        private val downArrow = getDrawable(itemView.context, R.drawable.ic_arrow_downward_20dp)
        private val unselectedBGColor = getColor(itemView.context, R.color.unselected_sort_bg_color)
        private val unselectedTColor = getColor(itemView.context, R.color.unselected_sort_txt_color)
        private val selectedTColor = getColor(itemView.context, R.color.selected_sort_txt_color)
        private val selectedBGColor = getColor(itemView.context, R.color.selected_sort_bg_color)
        private var sortOrder: OrderEnum = OrderEnum.ASC

        fun setItemSelect(isSelected: Boolean) {
            if (isSelected) {
                sortItemLayout.setBackgroundColor(selectedBGColor)
                sortTextView.setTextColor(selectedTColor)
            } else {
                sortItemLayout.setBackgroundColor(unselectedBGColor)
                sortTextView.setTextColor(unselectedTColor)
            }
        }

        fun setSortTextView(text: String) {
            sortTextView.text = text
        }

        fun setOrder(order: OrderEnum) {
            sortOrder = order
            when (sortOrder) {
                OrderEnum.ASC -> sortImageView.setImageDrawable(upArrow)
                OrderEnum.DES -> sortImageView.setImageDrawable(downArrow)
            }
        }

        fun setArrowVisibility(visible: Boolean) {
            if (visible) {
                sortImageView.visibility = View.VISIBLE
            } else {
                sortImageView.visibility = View.INVISIBLE
            }
        }

        fun setSortItemClickListener(
            listener: SortItemClickListener,
            isSelected: Boolean,
            sortEnum: SortEnum,
            position: Int
        ) {
            sortItemLayout.setOnClickListener {
                if (isSelected) {
                    sortOrder = if (sortOrder == OrderEnum.ASC) {
                        OrderEnum.DES
                    } else {
                        OrderEnum.ASC
                    }
                }
                listener.onClick(sortEnum, sortOrder, position)
            }
        }
    }
}