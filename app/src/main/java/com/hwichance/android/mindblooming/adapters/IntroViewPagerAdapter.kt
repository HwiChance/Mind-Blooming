package com.hwichance.android.mindblooming.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hwichance.android.mindblooming.R
import com.hwichance.android.mindblooming.fragments.IntroFragment

class IntroViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                IntroFragment.newInstance(R.layout.fragment_intro_first)
            }
            1-> {
                IntroFragment.newInstance(R.layout.fragment_intro_second)
            }
            else-> {
                IntroFragment.newInstance(R.layout.fragment_intro_third)
            }
        }
    }
}