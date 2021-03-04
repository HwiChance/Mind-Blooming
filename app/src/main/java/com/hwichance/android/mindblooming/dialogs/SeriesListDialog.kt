package com.hwichance.android.mindblooming.dialogs

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.adapters.SeriesListDialogAdapter
import com.hwichance.android.mindblooming.rooms.data.IdeaData
import com.hwichance.android.mindblooming.rooms.data.SeriesData
import com.hwichance.android.mindblooming.rooms.view_model.IdeaViewModel
import com.hwichance.android.mindblooming.rooms.view_model.SeriesViewModel

class SeriesListDialog(private val ideaData: IdeaData) : DialogFragment() {
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var editText: TextInputEditText
    private lateinit var seriesAddBtn: ImageButton
    private lateinit var seriesSetBtn: Button
    private lateinit var recyclerView: RecyclerView
    private val seriesDialogAdapter = SeriesListDialogAdapter()
    private val seriesViewModel: SeriesViewModel by activityViewModels()
    private val ideaViewModel: IdeaViewModel by activityViewModels()

    override fun onResume() {
        super.onResume()

        val windowManager = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        params?.width = (size.x * 0.9).toInt()
        params?.height = (size.y * 0.7).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.series_list_dialog, container, false)
        textInputLayout = view.findViewById(R.id.seriesDialogInputLayout)
        editText = view.findViewById(R.id.seriesDialogEditText)
        seriesAddBtn = view.findViewById(R.id.seriesDialogAddBtn)
        seriesSetBtn = view.findViewById(R.id.seriesSetBtn)
        recyclerView = view.findViewById(R.id.seriesDialogRecyclerView)

        seriesViewModel.getAll().observe(this, { seriesList ->
            seriesDialogAdapter.setSeriesList(seriesList)
            seriesDialogAdapter.setInitialSelect(ideaData.seriesId)
        })

        seriesDialogAdapter.setClickListener(object :
            SeriesListDialogAdapter.SeriesDialogItemClickListener {
            override fun onClick(position: Int) {
                seriesDialogAdapter.setSelection(position)
                editText.clearFocus()
                textInputLayout.error = null
            }
        })

        recyclerView.adapter = seriesDialogAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        setListeners()
        return view
    }

    private fun setListeners() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        editText.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val manager =
                    context?.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(v.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        seriesAddBtn.setOnClickListener {
            val seriesTile = editText.text.toString().trim()
            if (seriesTile.isEmpty()) {
                textInputLayout.error = getString(R.string.no_character)
            } else {
                editText.setText("")
                editText.clearFocus()
                val newSeries = SeriesData(null, seriesTile, seriesTile, System.currentTimeMillis())
                seriesViewModel.insert(newSeries) {

                }
            }
        }

        seriesSetBtn.setOnClickListener {
            val selectedSeriesId = seriesDialogAdapter.getSelectedSeriesId()
            ideaData.seriesId = selectedSeriesId
            ideaData.seriesAddedDate = System.currentTimeMillis()
            ideaViewModel.update(ideaData)
            dismiss()
        }
    }
}