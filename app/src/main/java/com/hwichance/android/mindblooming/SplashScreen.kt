package com.hwichance.android.mindblooming

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        setPermissions()
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            run {
                if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
                    && permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
                ) {
                    moveNextActivity()
                } else {
                    Snackbar.make(
                        findViewById(R.id.splashScreenLayout),
                        R.string.permission_denied,
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(R.string.ok) {
                        finish()
                    }.show()
                }
            }
        }

    private fun setPermissions() {
        if (checkGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
            && checkGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            moveNextActivity()
        } else {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun checkGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun moveNextActivity() {
        val intent: Intent = if (checkFirstRun()) {
            Intent(this, IntroActivity::class.java)
        } else {
            Intent(this, MainActivity::class.java)
        }

        Handler().postDelayed({
            startActivity(intent)
            overridePendingTransition(R.anim.fadein, R.anim.no_animation)
            finish()
        }, 1000)
    }

    private fun checkFirstRun(): Boolean {
        val firstRunPref =
            getSharedPreferences(getString(R.string.preference_first_run), Context.MODE_PRIVATE)
        val isFirstRun = firstRunPref.getBoolean(getString(R.string.is_first_run), true);
        return if (isFirstRun) {
            firstRunPref.edit()
                .putBoolean(getString(R.string.is_first_run), false)
                .apply()
            true
        } else {
            false
        }
    }
}