package com.hwichance.android.mindblooming.fragments

import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import com.hwichance.android.mindblooming.MindMapEditActivity
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel

class AddDiagramFragment : BottomSheetDialogFragment() {
    private lateinit var addMindMapBtn: LinearLayout
    private lateinit var addFlowChartBtn: LinearLayout
    private val ideaViewModel: IdeaViewModel by activityViewModels()

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
            val date = System.currentTimeMillis()
            val title = getString(R.string.new_mind_map)
            val newIdea = IdeaData(null, title, date, date, true)
            ideaViewModel.insert(newIdea) { groupId ->
                val intent = Intent(context, MindMapEditActivity::class.java)
                intent.putExtra("groupId", groupId)
                intent.putExtra("isNewIdea", true)
                startActivity(intent)
                dismiss()
            }
        }
        addFlowChartBtn.setOnClickListener {
            val date = System.currentTimeMillis()
            val title = getString(R.string.new_flow_chart)
            val newIdea = IdeaData(null, title, date, date, false)
            ideaViewModel.insert(newIdea) {
                // TODO: go flow chart edit activity
            }
            dismiss()
        }
    }
}