package com.hwichance.android.mindblooming.dialogs

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.adapters.ColorPaletteAdapter

class ColorPaletteDialog(context: Context) : MaterialAlertDialogBuilder(context) {
    private var recyclerView: RecyclerView
    var adapter: ColorPaletteAdapter

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.color_palette_dialog, null)
        recyclerView = view.findViewById(R.id.colorPaletteRecyclerView)
        adapter = ColorPaletteAdapter(context.resources.getIntArray(R.array.palettes))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        setView(view)
    }
}