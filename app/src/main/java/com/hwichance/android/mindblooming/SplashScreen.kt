package com.hwichance.android.mindblooming

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.hwichance.android.mindblooming.utils.ThemeUtils
import kotlinx.coroutines.*

class SplashScreen : AppCompatActivity() {
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setThemeMode()
    }

    /**
     *   Check app's theme mode
     */
    private fun setThemeMode() {
        with(PreferenceManager.getDefaultSharedPreferences(this)) {
            when {
                getBoolean("systemMode", true) -> ThemeUtils.changeTheme("systemMode")
                getBoolean("lightMode", false) -> ThemeUtils.changeTheme("lightMode")
                getBoolean("darkMode", false) -> ThemeUtils.changeTheme("darkMode")
            }
        }

        moveNextActivity()
    }

    /**
     *  Use coroutine for delay
     */
    private fun moveNextActivity() {
        val intent = if (checkFirstRun()) {
            Intent(this, IntroActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        activityScope.launch {
            delay(1500)
            startActivity(intent)
            finish()
        }
    }

    /**
     *  Check that the app is first launched
     */
    private fun checkFirstRun(): Boolean {
        val pref = getPreferences(Context.MODE_PRIVATE)
        return pref.getBoolean("isFirstRun", true).also { isFirstRun ->
            if (isFirstRun) pref.edit().putBoolean("isFirstRun", false).apply()
        }
    }

    /**
     *  Prevent memory leaks or unintended behavior when hardware back button is pressed
     */
    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}