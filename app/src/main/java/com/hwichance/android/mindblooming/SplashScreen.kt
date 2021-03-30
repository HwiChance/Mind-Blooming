package com.hwichance.android.mindblooming

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.preference.PreferenceManager
import com.hwichance.android.mindblooming.utils.ThemeUtils

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setTheme()
    }

    private fun setTheme() {
        with(PreferenceManager.getDefaultSharedPreferences(this)) {
            when {
                getBoolean("systemMode", true) -> {
                    ThemeUtils.changeTheme("systemMode")
                }
                getBoolean("lightMode", false) -> {
                    ThemeUtils.changeTheme("lightMode")
                }
                getBoolean("darkMode", false) -> {
                    ThemeUtils.changeTheme("darkMode")
                }
            }
        }
        moveNextActivity()
    }

    private fun moveNextActivity() {
        val intent = if (checkFirstRun()) {
            Intent(this, IntroActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        Handler().postDelayed({
            startActivity(intent)
            finish()
        }, 1000)
    }

    private fun checkFirstRun(): Boolean {
        val pref = getPreferences(Context.MODE_PRIVATE)
        val isFirstRun = pref.getBoolean("isFirstRun", true)

        return when {
            isFirstRun -> {
                pref.edit().putBoolean("isFirstRun", false).apply()
                true
            }
            else -> false
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_animation, R.anim.fadeout)
    }
}