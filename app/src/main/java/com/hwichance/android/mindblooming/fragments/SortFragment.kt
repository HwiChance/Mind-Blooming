package com.hwichance.android.mindblooming.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.adapters.SortListAdapter
import com.hwichance.android.mindblooming.enums.OrderEnum
import com.hwichance.android.mindblooming.enums.SortCaller
import com.hwichance.android.mindblooming.enums.SortEnum
import com.hwichance.android.mindblooming.rooms.data.SortData
import com.hwichance.android.mindblooming.rooms.view_model.SortViewModel

class SortFragment(private val caller: SortCaller, private val sortData: SortData) :
    BottomSheetDialogFragment() {
    private lateinit var sortRecyclerView: RecyclerView
    private val sortListAdapter = SortListAdapter()
    private val sortViewModel: SortViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sort, container, false)

        val sortStrings = view.context.resources.getStringArray(R.array.sortStrings)
        val sortList = ArrayList<Pair<SortEnum, String>>()

        sortList.add(Pair(SortEnum.TITLE, sortStrings[0]))
        sortList.add(Pair(SortEnum.CREATED_DATE, sortStrings[1]))
        sortList.add(Pair(SortEnum.LAST_MODIFIED_DATE, sortStrings[2]))
        if (caller == SortCaller.STARRED) {
            sortList.add(Pair(SortEnum.STARRED_DATE, sortStrings[3]))
        } else if (caller == SortCaller.SERIES) {
            sortList.add(Pair(SortEnum.SERIES_ADDED_DATE, sortStrings[4]))
        }

        sortListAdapter.setSortOrderList(sortList, sortData)
        sortListAdapter.setSortItemClickListener(object : SortListAdapter.SortItemClickListener {
            override fun onClick(sortEnum: SortEnum, orderEnum: OrderEnum, position: Int) {
                sortData.sortEnum = sortEnum
                sortData.orderEnum = orderEnum
                sortViewModel.insert(sortData)
                sortListAdapter.setSelect(position)
                dismiss()
            }
        })

        sortRecyclerView = view.findViewById(R.id.sortRecyclerView)
        sortRecyclerView.layoutManager = LinearLayoutManager(view.context)
        sortRecyclerView.adapter = sortListAdapter
        return view
    }
}