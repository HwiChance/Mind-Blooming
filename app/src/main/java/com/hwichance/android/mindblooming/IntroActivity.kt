package com.hwichance.android.mindblooming

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hwichance.android.mindblooming.adapters.IntroViewPagerAdapter

class IntroActivity : AppCompatActivity() {
    private lateinit var introViewPager: ViewPager2
    private lateinit var introSkipBtn: Button
    private lateinit var introNextBtn: Button
    private lateinit var introTab: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        bindViews()

        TabLayoutMediator(introTab, introViewPager) { _, _ -> }.attach()
    }

    private var pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            when (position) {
                2 -> {
                    introSkipBtn.visibility = View.INVISIBLE
                    introNextBtn.text = getText(R.string.start)
                }
                else -> {
                    introSkipBtn.visibility = View.VISIBLE
                    introNextBtn.text = getText(R.string.next)
                }
            }
            super.onPageSelected(position)
        }
    }

    private fun bindViews() {
        introViewPager = findViewById(R.id.introViewPager)
        introSkipBtn = findViewById(R.id.introSkipBtn)
        introNextBtn = findViewById(R.id.introNextBtn)
        introTab = findViewById(R.id.introTab)

        introViewPager.adapter = IntroViewPagerAdapter(this)
        introViewPager.registerOnPageChangeCallback(pageChangeCallback)

        introSkipBtn.setOnClickListener {
            moveNextActivity()
        }

        introNextBtn.setOnClickListener {
            when (introViewPager.currentItem) {
                2 -> moveNextActivity()
                else -> introViewPager.currentItem++
            }
        }
    }

    private fun moveNextActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(R.anim.no_animation, R.anim.fadeout)
        finish()
    }

    override fun onDestroy() {
        introViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }
}