package com.hwichance.android.mindblooming

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hwichance.android.mindblooming.utils.DialogUtils

class MindMapEditActivity : AppCompatActivity() {
    private lateinit var mindMapEditToolbar: Toolbar
    private lateinit var mindMapTitleTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mind_map_edit)

        bindViews()
    }

    private fun bindViews() {
        mindMapEditToolbar = findViewById(R.id.mindMapEditToolbar)
        mindMapTitleTextView = findViewById(R.id.mindMapTitleTextView)

        setInitialState()
        setToolbarListener()
    }

    private fun setInitialState() {
        mindMapTitleTextView.text = getString(R.string.new_mind_map)
    }

    private fun setToolbarListener() {
        mindMapEditToolbar.setNavigationOnClickListener {

        }

        mindMapEditToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.ideaTitleEditMenu -> {
                    DialogUtils.showEditTitleDialog(
                        this,
                        mindMapTitleTextView.text,
                        mindMapTitleTextView
                    )
                }
                R.id.ideaSaveMenu -> {

                }
            }
            true
        }
    }
}