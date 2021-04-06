package com.hwichance.android.mindblooming.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hwichance.android.mindblooming.R

class IntroFragment : Fragment() {
    private var layout = R.layout.fragment_intro_first

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            layout = it.getInt("layout")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layout, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(mLayout: Int) = IntroFragment().apply {
            arguments = Bundle().apply {
                putInt("layout", mLayout)
            }
        }
    }
}