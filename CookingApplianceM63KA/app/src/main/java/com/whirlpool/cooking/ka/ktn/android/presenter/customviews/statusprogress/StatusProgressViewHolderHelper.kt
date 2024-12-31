// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL
package android.presenter.customviews.statusprogress

import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.cooking.ka.databinding.ItemProgressStatusBinding


// Created by SINGHA80 on 3/3/2024.
/**
 * StatusProgressViewHolderHelper class provides view instance {@link ItemProgressStatusBinding}
 */
class StatusProgressViewHolderHelper {
    private var binding: ItemProgressStatusBinding? = null

    /**
     * @param binding set view binding of ItemProgressStatusBinding
     */
    fun setViewBinding(binding: ItemProgressStatusBinding?) {
        this.binding = binding
    }

    /**
     * make ItemProgressStatusBinding null
     */
    @Suppress("unused")
    fun destroyView() {
        binding = null
    }

    /**
     * @return TextView for temperature data
     */
    fun getTemperatureTextView(): TextView? {
        return binding?.tvTemperature
    }

    /**
     * @return TextView for cook time
     */
    fun getCookTimeTextView(): TextView? {
        return binding?.tvCookTime
    }

    /**
     * @return ProgressBar for the status progress
     */
    fun getProgressBarCookTime(): ProgressBar? {
        return binding?.progressBarCookTime
    }

    /**
     * @return LottieProgressBar for the status progress
     */
    fun getLottieProgressBarCookTime(): LottieAnimationView? {
        return binding?.lottieProgressBarCookTime
    }

    /**
     * @return ImageView for oven locked icon
     */
    fun getOvenCavityImageView(): ImageView? {
        return binding?.ivOvenCavity
    }

    /**
     * @return TextView for Start Now button
     */
    fun getStartNowTextView(): TextView? {
        return binding?.tvStartNow
    }

    fun getStatusTopTextContentView(): ConstraintLayout? {
        return binding?.clModeLayout
    }

    fun getStatusBottomOptionsView(): TextView? {
        return binding?.tvStartNow
    }
}