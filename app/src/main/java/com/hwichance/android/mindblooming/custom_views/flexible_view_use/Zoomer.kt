package com.hwichance.android.mindblooming.custom_views.flexible_view_use

import android.R.integer
import android.content.Context
import android.os.SystemClock

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator


class Zoomer(context: Context) {
    /**
     * The interpolator, used for making zooms animate 'naturally.'
     */
    private var mInterpolator: Interpolator = DecelerateInterpolator()

    /**
     * The total animation duration for a zoom.
     */
    private var mAnimationDurationMillis = 0

    /**
     * Whether or not the current zoom has finished.
     */
    private var mFinished = true

    /**
     * The current zoom value; computed by [.computeZoom].
     */
    private var mCurrentZoom = 0f

    /**
     * The time the zoom started, computed using [android.os.SystemClock.elapsedRealtime].
     */
    private var mStartRTC: Long = 0

    /**
     * The destination zoom factor.
     */
    private var mEndZoom = 0f

    init {
        mAnimationDurationMillis = context.resources.getInteger(
            integer.config_shortAnimTime
        )
    }

    /**
     * Forces the zoom finished state to the given value. Unlike [.abortAnimation], the
     * current zoom value isn't set to the ending value.
     *
     * @see android.widget.Scroller.forceFinished
     */
    fun forceFinished(finished: Boolean) {
        mFinished = finished
    }

    /**
     * Aborts the animation, setting the current zoom value to the ending value.
     *
     * @see android.widget.Scroller.abortAnimation
     */
    fun abortAnimation() {
        mFinished = true
        mCurrentZoom = mEndZoom
    }

    /**
     * Starts a zoom from 1.0 to (1.0 + endZoom). That is, to zoom from 100% to 125%, endZoom should
     * by 0.25f.
     *
     * @see android.widget.Scroller.startScroll
     */
    fun startZoom(endZoom: Float) {
        mStartRTC = SystemClock.elapsedRealtime()
        mEndZoom = endZoom
        mFinished = false
        mCurrentZoom = 1f
    }

    /**
     * Computes the current zoom level, returning true if the zoom is still active and false if the
     * zoom has finished.
     *
     * @see android.widget.Scroller.computeScrollOffset
     */
    fun computeZoom(): Boolean {
        if (mFinished) {
            return false
        }
        val tRTC = SystemClock.elapsedRealtime() - mStartRTC
        if (tRTC >= mAnimationDurationMillis) {
            mFinished = true
            mCurrentZoom = mEndZoom
            return false
        }
        val t = tRTC * 1f / mAnimationDurationMillis
        mCurrentZoom = mEndZoom * mInterpolator.getInterpolation(t)
        return true
    }

    /**
     * Returns the current zoom level.
     *
     * @see android.widget.Scroller.getCurrX
     */
    fun getCurrZoom(): Float {
        return mCurrentZoom
    }
}