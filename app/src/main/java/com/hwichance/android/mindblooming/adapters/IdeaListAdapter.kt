package com.hwichance.android.mindblooming.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.enums.DiagramClassEnum
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.utils.DateTimeUtils
import java.util.*
import kotlin.collections.ArrayList

class IdeaListAdapter : RecyclerView.Adapter<IdeaListAdapter.IdeaListViewHolder>() {
    interface IdeaClickListener {
        fun onClick(idea: IdeaData, isActionMode: Boolean, position: Int)
        fun onLongClick(isActionMode: Boolean, position: Int)
    }

    private var isActionMode = false
    private var ideaList = listOf<IdeaData>()
    private var showList = listOf<IdeaData>()
    private var isCheckedList = mutableListOf<Boolean>()
    private lateinit var ideaClickListener: IdeaClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.idea_list_view, parent, false)
        return IdeaListViewHolder(view)
    }

    override fun onBindViewHolder(holder: IdeaListViewHolder, position: Int) {
        holder.setViews(showList[position].ideaTitle, showList[position].modifiedDate)
        holder.itemView.setOnClickListener {
            ideaClickListener.onClick(showList[position], isActionMode, position)
        }
        if (isActionMode) {
            holder.itemView.isLongClickable = false
        } else {
            holder.itemView.isLongClickable = true
            holder.itemView.setOnLongClickListener {
                ideaClickListener.onLongClick(isActionMode, position)
                true
            }
        }
        holder.setCover(isCheckedList[position])
    }

    override fun getItemCount(): Int = showList.size

    fun setIdeaList(ideas: List<IdeaData>) {
        ideaList = ideas.reversed()
        showList = ideaList
        notifyChange()
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
        for(idea in ideaList) {
            ids.add(idea.ideaId!!)
        }
        return ids.toList()
    }

    fun setIdeaClickListener(listener: IdeaClickListener) {
        ideaClickListener = listener
    }

    class IdeaListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView = itemView.findViewById<TextView>(R.id.ideaListTitleView)
        private val dateView = itemView.findViewById<TextView>(R.id.ideaListModifiedDateView)
        private val cover = itemView.findViewById<RelativeLayout>(R.id.itemViewCover)
        private val prefix = itemView.context.getString(R.string.idea_list_date)

        fun setViews(title: String, date: Long) {
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
    }
}