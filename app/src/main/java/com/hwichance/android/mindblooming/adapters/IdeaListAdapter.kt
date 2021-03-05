package com.hwichance.android.mindblooming.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.enums.OrderEnum
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.data.SeriesData
import com.hwichance.android.mindblooming.rooms.data.SortData
import com.hwichance.android.mindblooming.utils.DateTimeUtils
import java.util.*
import kotlin.collections.ArrayList

class IdeaListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface IdeaClickListener {
        fun onClick(idea: IdeaData, isActionMode: Boolean, position: Int)
        fun onLongClick(isActionMode: Boolean, position: Int)
    }

    interface StarredBtnClickListener {
        fun onClick(isChecked: Boolean, idea: IdeaData)
    }

    private val sortViewType = 0
    private val listViewType = 1
    private var isActionMode = false
    private var seriesList = listOf<SeriesData>()
    private var ideaList = listOf<IdeaData>()
    private var showList = listOf<IdeaData>()
    private var isCheckedList = mutableListOf<Boolean>()
    private var defaultSeriesText = ""
    private var sortText = ""
    private var sortData = SortData(null)
    private lateinit var sortBtnClickListener: View.OnClickListener
    private lateinit var ideaClickListener: IdeaClickListener
    private lateinit var starredBtnClickListener: StarredBtnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        defaultSeriesText = parent.context.getString(R.string.no_series)
        return if (viewType == sortViewType) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.idea_list_sort_view, parent, false)
            IdeaListSortViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.idea_list_view, parent, false)
            IdeaListViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is IdeaListSortViewHolder) {
            holder.bindViews(sortText, sortData.orderEnum)
            holder.setSortBtnListener(sortBtnClickListener)
        } else if (holder is IdeaListViewHolder) {
            val seriesTitle = if (showList[position - 1].seriesId == null) {
                defaultSeriesText
            } else {
                val series =
                    seriesList.findLast { seriesData -> seriesData.seriesId == showList[position - 1].seriesId }
                series?.seriesTitle!!
            }
            holder.setViews(
                seriesTitle,
                showList[position - 1].ideaTitle,
                showList[position - 1].modifiedDate
            )
            holder.setStarredBtn(showList[position - 1].isStarred)
            holder.itemView.setOnClickListener {
                ideaClickListener.onClick(showList[position - 1], isActionMode, position)
            }
            holder.itemView.setOnLongClickListener {
                ideaClickListener.onLongClick(isActionMode, position)
                true
            }
            holder.setOnStarredBtnListener(starredBtnClickListener, showList[position - 1])

            if (isActionMode) {
                holder.itemView.isLongClickable = false
                holder.setStarredBtnClickable(false)
            } else {
                holder.itemView.isLongClickable = true
                holder.setStarredBtnClickable(true)
            }
            holder.setCover(isCheckedList[position - 1])
        }
    }

    override fun getItemCount(): Int = showList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            sortViewType
        } else {
            listViewType
        }
    }

    fun sortingData(text: String, data: SortData) {
        sortText = text
        sortData = data
        filtering(sortData.sortEnum, sortData.orderEnum)
    }

    fun setIdeaList(ideas: List<IdeaData>) {
        ideaList = ideas
        showList = ideaList
        filtering(sortData.sortEnum, sortData.orderEnum)
    }

    fun setSeriesList(list: List<SeriesData>) {
        seriesList = list
        notifyDataSetChanged()
    }

    fun setActionMode(mode: Boolean) {
        isActionMode = mode
        notifyChange()
    }

    fun filtering(text: String?) {
        showList = if (text != null && text.isNotEmpty()) {
            ideaList.filter { data ->
                data.ideaTitle.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            }
        } else {
            ideaList
        }
        notifyChange()
    }

    private fun filtering(sort: SortEnum, order: OrderEnum) {
        showList = when (sort) {
            SortEnum.CREATED_DATE -> {
                when (order) {
                    OrderEnum.ASC -> showList.sortedBy { data -> data.createdDate }
                    OrderEnum.DES -> showList.sortedByDescending { data -> data.createdDate }
                }
            }
            SortEnum.LAST_MODIFIED_DATE -> {
                when (order) {
                    OrderEnum.ASC -> showList.sortedBy { data -> data.modifiedDate }
                    OrderEnum.DES -> showList.sortedByDescending { data -> data.modifiedDate }
                }
            }
            SortEnum.TITLE -> {
                when (order) {
                    OrderEnum.ASC -> showList.sortedBy { data -> data.ideaTitle }
                    OrderEnum.DES -> showList.sortedByDescending { data -> data.ideaTitle }
                }
            }
            SortEnum.STARRED_DATE -> {
                when (order) {
                    OrderEnum.ASC -> showList.sortedBy { data -> data.starredDate }
                    OrderEnum.DES -> showList.sortedByDescending { data -> data.starredDate }
                }
            }
            SortEnum.SERIES_ADDED_DATE -> {
                when (order) {
                    OrderEnum.ASC -> showList.sortedBy { data -> data.seriesAddedDate }
                    OrderEnum.DES -> showList.sortedByDescending { data -> data.seriesAddedDate }
                }
            }
        }
        notifyChange()
    }

    private fun notifyChange() {
        isCheckedList = MutableList(showList.size) { false }
        notifyDataSetChanged()
    }

    fun toggleItemChecked(position: Int) {
        isCheckedList[position - 1] = !isCheckedList[position - 1]
        notifyItemChanged(position)
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
        for (i in 0 until isCheckedList.size) {
            if (isCheckedList[i]) {
                ids.add(showList[i].ideaId!!)
            }
        }
        return ids.toList()
    }

    fun getItemIds(): List<Long> {
        val ids = ArrayList<Long>()
        for (idea in ideaList) {
            ids.add(idea.ideaId!!)
        }
        return ids.toList()
    }

    fun setSortBtnClickListener(listener: View.OnClickListener) {
        sortBtnClickListener = listener
    }

    fun setIdeaClickListener(listener: IdeaClickListener) {
        ideaClickListener = listener
    }

    fun setStarredBtnClickListener(listener: StarredBtnClickListener) {
        starredBtnClickListener = listener
    }

    class IdeaListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val seriesView = itemView.findViewById<TextView>(R.id.ideaListSeriesView)
        private val titleView = itemView.findViewById<TextView>(R.id.ideaListTitleView)
        private val dateView = itemView.findViewById<TextView>(R.id.ideaListModifiedDateView)
        private val starredBtn = itemView.findViewById<ToggleButton>(R.id.starredBtn)
        private val cover = itemView.findViewById<RelativeLayout>(R.id.itemViewCover)
        private val prefix = itemView.context.getString(R.string.idea_list_date)

        fun setViews(series: String, title: String, date: Long) {
            seriesView.text = series
            titleView.text = title
            dateView.text = DateTimeUtils.convertDateToString(prefix, date)
        }

        fun setCover(visible: Boolean) {
            if (visible) {
                cover.visibility = View.VISIBLE
            } else {
                cover.visibility = View.INVISIBLE
            }
        }

        fun setStarredBtn(isStarred: Boolean) {
            starredBtn.isChecked = isStarred
        }

        fun setStarredBtnClickable(clickable: Boolean) {
            starredBtn.isClickable = clickable
        }

        fun setOnStarredBtnListener(listener: StarredBtnClickListener, idea: IdeaData) {
            starredBtn.setOnClickListener {
                listener.onClick(starredBtn.isChecked, idea)
            }
        }
    }

    class IdeaListSortViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sortLayout = itemView.findViewById<LinearLayout>(R.id.sortLayout)
        private val sortText = itemView.findViewById<TextView>(R.id.sortText)
        private val sortImage = itemView.findViewById<ImageView>(R.id.sortImage)
        private val upArrow = getDrawable(itemView.context, R.drawable.ic_arrow_upward_16dp)
        private val downArrow = getDrawable(itemView.context, R.drawable.ic_arrow_downward_16dp)

        fun bindViews(text: String, order: OrderEnum) {
            sortText.text = text
            when (order) {
                OrderEnum.ASC -> sortImage.setImageDrawable(upArrow)
                OrderEnum.DES -> sortImage.setImageDrawable(downArrow)
            }
        }

        fun setSortBtnListener(listener: View.OnClickListener) {
            sortLayout.setOnClickListener(listener)
        }
    }
}