// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL
package android.presenter.customviews.statusprogress

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.whirlpool.cooking.ka.databinding.ItemProgressStatusBinding


// Created by SINGHA80 on 3/3/2024.
/**
 * AbstractStatusProgressViewHolder provides abstract functionalities of the layout item_progress_status.xml
 */
@Suppress("unused")
interface AbstractStatusProgressViewHolder {
    fun setViewBinding(binding: ItemProgressStatusBinding?)
    fun destroyView()
    fun getProgressStatusTextView(): TextView?
    fun getTemperatureTextView(): TextView?
    fun getCookTimeTextView(): TextView?
    fun getProgressBarCookTime(): ProgressBar?
    fun getOvenLockedTextView(): TextView?
    fun getOvenLockedImageView(): ImageView?
    fun getOvenLockedFrameLayout(): FrameLayout?
    fun getStartNowTextView(): TextView?
}