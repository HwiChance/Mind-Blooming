package com.hwichance.android.mindblooming

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.hwichance.android.mindblooming.adapters.IntroViewPagerAdapter
import com.hwichance.android.mindblooming.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setViewPager()
        setButton()
        setTab()
    }

    /**
     *   Callback to set buttons when the page changes
     */
    private var pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            when (position) {
                0, 1 -> {
                    binding.introSkipBtn.visibility = View.VISIBLE
                    binding.introNextBtn.text = getText(R.string.next)
                }
                2 -> {
                    binding.introSkipBtn.visibility = View.INVISIBLE
                    binding.introNextBtn.text = getText(R.string.start)
                }
            }
            super.onPageSelected(position)
        }
    }

    /**
     *   Set the ViewPager's adapter and register the PageChangeCallback
     */
    private fun setViewPager() {
        binding.introViewPager.adapter = IntroViewPagerAdapter(this)
        binding.introViewPager.registerOnPageChangeCallback(pageChangeCallback)
    }

    /**
     *   Set button's click listener
     */
    private fun setButton() {
        binding.introSkipBtn.setOnClickListener { moveNextActivity() }
        binding.introNextBtn.setOnClickListener {
            when (binding.introViewPager.currentItem) {
                0, 1 -> binding.introViewPager.currentItem++
                2 -> moveNextActivity()
            }
        }
    }

    /**
     *   Set the tab layout mediator
     */
    private fun setTab() {
        TabLayoutMediator(binding.introTab, binding.introViewPager) { _, _ -> }.attach()
    }

    /**
     *   Move to main activity
     */
    private fun moveNextActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /**
     *   Unregister the PageChangeCallback before the activity is destroyed
     */
    override fun onDestroy() {
        binding.introViewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }
}