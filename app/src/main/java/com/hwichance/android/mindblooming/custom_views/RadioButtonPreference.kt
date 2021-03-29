package com.hwichance.android.mindblooming.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioButton
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceViewHolder
import com.hwichance.android.mindblooming.R

class RadioButtonPreference(context: Context?, attrs: AttributeSet?) :
    CheckBoxPreference(context, attrs) {
    init {
        widgetLayoutResource = R.layout.preference_widget_radiobutton
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        holder?.itemView?.findViewById<RadioButton>(R.id.prefRadioButton).apply {
            this?.isChecked = isChecked
        }
    }

    override fun onClick() {
        if (isChecked)
            return
        super.onClick()
    }
}