package com.hwichance.android.mindblooming

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        moveNextActivity()
    }

    private fun moveNextActivity() {
        val intent: Intent = if (checkFirstRun()) {
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
        val firstRunPref =
            getSharedPreferences(getString(R.string.preference_first_run), Context.MODE_PRIVATE)
        val isFirstRun = firstRunPref.getBoolean(getString(R.string.is_first_run), true)
        return if (isFirstRun) {
            firstRunPref.edit()
                .putBoolean(getString(R.string.is_first_run), false)
                .apply()
            true
        } else {
            false
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.no_animation, R.anim.fadeout)
    }
}