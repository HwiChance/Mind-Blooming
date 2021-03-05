package com.hwichance.android.mindblooming.listeners

import android.widget.EditText
import android.widget.TextView
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class SeriesAppBarListener(
    private var titleView: TextView,
    private var titleEdit: EditText,
    private var description: EditText,
    private var titleLabel: TextView,
    private var descriptionLabel: TextView
) : AppBarLayout.OnOffsetChangedListener {

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (appBarLayout?.totalScrollRange == 0 || verticalOffset == 0) {
            titleView.alpha = 0f
            titleEdit.alpha = 1f
            description.alpha = 1f
            titleLabel.alpha = 1f
            descriptionLabel.alpha = 1f
            return
        }
        val maxRange = appBarLayout?.totalScrollRange ?: abs(verticalOffset)
        val percentage = abs(verticalOffset).toFloat() / maxRange.toFloat()

        titleView.alpha = percentage
        titleEdit.alpha = 1f - percentage
        description.alpha = 1f - percentage
        titleLabel.alpha = 1f - percentage
        descriptionLabel.alpha = 1f - percentage
    }
}