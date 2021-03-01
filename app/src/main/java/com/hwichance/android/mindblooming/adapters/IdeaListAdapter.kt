package com.hwichance.android.mindblooming.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.utils.DateTimeUtils

class IdeaListAdapter : RecyclerView.Adapter<IdeaListAdapter.IdeaListViewHolder>() {
    interface IdeaClickListener {
        fun onClick(idea: IdeaData)
        fun onLongClick(idea: IdeaData)
    }

    private var ideaList = listOf<IdeaData>()
    private var showList = listOf<IdeaData>()
    private lateinit var ideaClickListener: IdeaClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IdeaListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.idea_list_view, parent, false)
        return IdeaListViewHolder(view)
    }

    override fun onBindViewHolder(holder: IdeaListViewHolder, position: Int) {
        holder.setViews(showList[position].ideaTitle, showList[position].modifiedDate)
        holder.itemView.setOnClickListener {
            ideaClickListener.onClick(showList[position])
        }
        holder.itemView.setOnLongClickListener {
            ideaClickListener.onLongClick(showList[position])
            true
        }
    }

    override fun getItemCount(): Int = showList.size

    fun setIdeaList(ideas: List<IdeaData>) {
        ideaList = ideas
        showList = ideaList
        notifyDataSetChanged()
    }

    fun filtering(text: String?) {
        showList = if (text != null && text.isNotEmpty()) {
            ideaList.filter { data ->
                data.ideaTitle.toLowerCase().contains(text.toLowerCase())
            }
        } else {
            ideaList
        }
        notifyDataSetChanged()
    }

    fun setIdeaClickListener(listener: IdeaClickListener) {
        ideaClickListener = listener
    }

    class IdeaListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView = itemView.findViewById<TextView>(R.id.ideaListTitleView)
        private val dateView = itemView.findViewById<TextView>(R.id.ideaListModifiedDateView)
        private val prefix = itemView.context.getString(R.string.idea_list_date)

        fun setViews(title: String, date: Long) {
            titleView.text = title
            dateView.text = DateTimeUtils.convertDateToString(prefix, date)
        }
    }
}