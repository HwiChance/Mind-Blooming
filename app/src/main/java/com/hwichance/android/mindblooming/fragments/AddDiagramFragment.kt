package com.hwichance.android.mindblooming.fragments

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.hwichance.android.mindblooming.R

class AddDiagramFragment : BottomSheetDialogFragment() {
    private lateinit var addMindMapBtn: LinearLayout
    private lateinit var addFlowChartBtn: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_diagram, container, false)
        addMindMapBtn = view.findViewById(R.id.addMindMapBtn)
        addFlowChartBtn = view.findViewById(R.id.addFlowChartBtn)

        setBtnClickListener()
        return view
    }

    private fun setBtnClickListener() {
        addMindMapBtn.setOnClickListener {
            Toast.makeText(view?.context, "mindmap", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        addFlowChartBtn.setOnClickListener {
            Toast.makeText(view?.context, "flowchart", Toast.LENGTH_SHORT).show()
            dismiss()
        }
    }
}