package com.hwichance.android.mindblooming

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.appbar.MaterialToolbar
import com.hwichance.android.mindblooming.fragments.SettingFragment

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val toolbar = findViewById<MaterialToolbar>(R.id.settingToolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        intent.getStringExtra("targetTitle").let { title ->
            if (title != null) {
                toolbar.title = title
            }
        }

        val settingFragment = SettingFragment()

        intent.getStringExtra("targetScreen").let { rootKey ->
            if (rootKey != null) {
                settingFragment.arguments = Bundle().apply {
                    putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, rootKey)
                }
            }
        }

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingFragment, settingFragment)
            .commit()
    }
}