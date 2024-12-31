// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL
package android.presenter.customviews.statusprogress

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.ItemProgressStatusBinding
import core.utils.CommonAnimationUtils

// Created by SINGHA80 on 3/3/2024.
/**
 * StatusProgressWidget class inflate layout and provides getters of the view[ItemProgressStatusBinding]
 */
class StatusProgressWidget : ConstraintLayout {
    private var statusProgressViewHolderHelper: StatusProgressViewHolderHelper? = null
    private var contextRef: Context

    constructor(context: Context) : super(context) {
        this.contextRef = context
        inflateLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.contextRef = context
        inflateLayout()
    }

    private fun inflateLayout() {
        if (statusProgressViewHolderHelper == null) {
            statusProgressViewHolderHelper = StatusProgressViewHolderHelper()
            val binding =
                ItemProgressStatusBinding.inflate(LayoutInflater.from(context), this, true)
            statusProgressViewHolderHelper!!.setViewBinding(binding)
        }
    }
        val temperatureTextView: TextView?
        get() = statusProgressViewHolderHelper?.getTemperatureTextView()
    val cookTimeTextView: TextView?
        get() = statusProgressViewHolderHelper?.getCookTimeTextView()
    val progressBarCookTime: ProgressBar?
        get() = statusProgressViewHolderHelper?.getProgressBarCookTime()
    val startNowTextView: TextView?
        get() = statusProgressViewHolderHelper?.getStartNowTextView()

    val ovenCavityImageView: ImageView?
        get() = statusProgressViewHolderHelper?.getOvenCavityImageView()

    val lottieProgressBarCookTime: LottieAnimationView?
        get() = statusProgressViewHolderHelper?.getLottieProgressBarCookTime()

    val getStatusTopTextContentView:ConstraintLayout?
        get() = statusProgressViewHolderHelper?.getStatusTopTextContentView()
    val getStatusBottomOptionsView:TextView?
        get() = statusProgressViewHolderHelper?.getStatusBottomOptionsView()
    fun setProgressBarStatusBackground(isProgressStarted: Boolean) {
        if (!isProgressStarted) {
            statusProgressViewHolderHelper
                ?.getLottieProgressBarCookTime()?.visibility = GONE
            statusProgressViewHolderHelper
                ?.getProgressBarCookTime()?.visibility = VISIBLE
        } else {
            statusProgressViewHolderHelper
                ?.getProgressBarCookTime()?.visibility = INVISIBLE
            CommonAnimationUtils.playLottieAnimation(statusProgressViewHolderHelper?.getLottieProgressBarCookTime(),R.raw.loop_fill_progress_bar_amber_white)
        }
    }
}