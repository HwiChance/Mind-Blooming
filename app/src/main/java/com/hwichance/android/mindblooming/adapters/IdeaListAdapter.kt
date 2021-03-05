package com.hwichance.android.mindblooming.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.enums.DiagramClassEnum
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.data.SeriesData
import com.hwichance.android.mindblooming.utils.DateTimeUtils
import java.util.*
import kotlin.collections.ArrayList

class IdeaListAdapter : RecyclerView.Adapter<IdeaListAdapter.IdeaListViewHolder>() {
    interface IdeaClickListener {
        fun onClick(idea: IdeaData, isActionMode: Boolean, position: Int)
        fun onLongClick(isActionMode: Boolean, position: Int)
    }

    interface StarredBtnClickListener {
        fun onClick(isChecked: Boolean, idea: IdeaData)
    }

    private var isActionMode = false
    private var seriesList = listOf<SeriesData>()
    private var ideaList = listOf<IdeaData>()
    private var showList = listOf<IdeaData>()
    private var isCheckedList = mutableListOf<Boolean>()
    private var defaultSeriesText: String = ""
    private lateinit var ideaClickListener: IdeaClickListener
    private lateinit var starredBtnClickListener: StarredBtnClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.idea_list_view, parent, false)
        defaultSeriesText = parent.context.getString(R.string.no_series)
        return IdeaListViewHolder(view)
    }

    override fun onBindViewHolder(holder: IdeaListViewHolder, position: Int) {
        val seriesTitle = if (showList[position].seriesId == null) {
            defaultSeriesText
        } else {
            val series =
                seriesList.findLast { seriesData -> seriesData.seriesId == showList[position].seriesId }
            series?.seriesTitle!!
        }
        holder.setViews(seriesTitle, showList[position].ideaTitle, showList[position].modifiedDate)
        holder.setStarredBtn(showList[position].isStarred)
        holder.itemView.setOnClickListener {
            ideaClickListener.onClick(showList[position], isActionMode, position)
        }
        holder.itemView.setOnLongClickListener {
            ideaClickListener.onLongClick(isActionMode, position)
            true
        }
        holder.setOnStarredBtnListener(starredBtnClickListener, showList[position])

        if (isActionMode) {
            holder.itemView.isLongClickable = false
            holder.setStarredBtnClickable(false)
        } else {
            holder.itemView.isLongClickable = true
            holder.setStarredBtnClickable(true)
        }
        holder.setCover(isCheckedList[position])
    }

    override fun getItemCount(): Int = showList.size

    fun setIdeaList(ideas: List<IdeaData>) {
        ideaList = ideas.reversed()
        showList = ideaList
        notifyChange()
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

    fun filtering(classFilter: DiagramClassEnum, sortFilter: SortEnum) {
        showList = when (classFilter) {
            DiagramClassEnum.ALL -> ideaList
            DiagramClassEnum.MIND_MAP -> ideaList.filter { data -> data.isMindMap }
            DiagramClassEnum.FLOW_CHART -> ideaList.filter { data -> !data.isMindMap }
        }
        showList = when (sortFilter) {
            SortEnum.CREATED_DATE -> showList.sortedByDescending { data -> data.createdDate }
            SortEnum.LAST_MODIFIED_DATE -> showList.sortedByDescending { data -> data.modifiedDate }
            SortEnum.TITLE -> showList.sortedBy { data -> data.ideaTitle }
            SortEnum.STARRED_DATE -> showList.sortedByDescending { data -> data.starredDate }
            SortEnum.SERIES_ADDED_DATE -> showList.sortedByDescending { data -> data.seriesAddedDate }
        }
        notifyChange()
    }

    private fun notifyChange() {
        isCheckedList = MutableList(showList.size) { false }
        notifyDataSetChanged()
    }

    fun toggleItemChecked(position: Int) {
        isCheckedList[position] = !isCheckedList[position]
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
}