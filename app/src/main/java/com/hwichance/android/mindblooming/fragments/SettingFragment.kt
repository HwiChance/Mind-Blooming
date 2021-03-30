package com.hwichance.android.mindblooming.fragments

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.SettingActivity
import com.hwichance.android.mindblooming.custom_views.RadioButtonPreference
import com.hwichance.android.mindblooming.utils.ThemeUtils

class SettingFragment : PreferenceFragmentCompat() {
    private val defaultPref by lazy { PreferenceManager.getDefaultSharedPreferences(activity) }
    private var prevPref: RadioButtonPreference? = null
    private val prefClickListener = Preference.OnPreferenceClickListener { pref ->
        if (pref is RadioButtonPreference) {
            updateRadioButton(pref)
            ThemeUtils.changeTheme(pref.key)
        }
        true
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preferences, rootKey)

        when (rootKey) {
            "theme" -> {
                setPref("systemMode", true)
                setPref("lightMode", false)
                setPref("darkMode", false)
            }
        }
    }

    private fun setPref(key: String, defValue: Boolean) {
        findPreference<RadioButtonPreference>(key).apply {
            if (this != null) {
                onPreferenceClickListener = prefClickListener
                isChecked = defaultPref.getBoolean(key, defValue)
                if (isChecked) {
                    prevPref = this
                }
            }
        }
    }

    private fun updateRadioButton(radioPref: RadioButtonPreference) {
        prevPref?.isChecked = false
        radioPref.isChecked = true
        prevPref = radioPref
    }

    override fun onNavigateToScreen(preferenceScreen: PreferenceScreen?) {
        super.onNavigateToScreen(preferenceScreen)
        with(Intent(activity, SettingActivity::class.java)) {
            putExtra("targetScreen", preferenceScreen?.key)
            putExtra("targetTitle", preferenceScreen?.title)
            startActivity(this)
        }
    }
}