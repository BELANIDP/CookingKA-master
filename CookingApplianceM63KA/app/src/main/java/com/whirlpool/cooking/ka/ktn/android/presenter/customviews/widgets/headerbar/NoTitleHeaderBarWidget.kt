package android.presenter.customviews.widgets.headerbar

import android.content.Context
import android.presenter.customviews.topsheet.TopSheetView
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.NoTitleHeaderBarBinding
import com.whirlpool.hmi.kitchentimer.uicomponents.widgets.KitchenTimerTextView
import com.whirlpool.hmi.uicomponents.widgets.clock.ClockTextView
import core.utils.CookingAppUtils
import core.utils.SettingsManagerUtils
import core.utils.TimeUtils.TEXT_TIME_FORMAT_24_HOUR
import core.utils.TimeUtils.TEXT_TIME_FORMAT_HOUR

// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL.
// Created by SINGHA80 on 05-Feb-24.
/**
 * NoTitleHeaderBarWidget class uses no_title_header_bar layout and
 * provides common functionalities of the views used in the layout.
 */
class NoTitleHeaderBarWidget : ConstraintLayout {
    private var contextRef: Context
    private var binding: NoTitleHeaderBarBinding? = null
    private var noTitleHeaderBarWidgetImpl: NoTitleHeaderBarWidgetImpl? = null

    constructor(context: Context) : super(context) {
        this.contextRef = context
        createView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.contextRef = context
        createView()
    }

    /**
     * inflates layout no_title_header_bar and creates NoTitleHeaderBarWidgetImpl object.
     */
    private fun createView() {
        if (binding == null) {
            binding = NoTitleHeaderBarBinding.inflate(LayoutInflater.from(context), this, true)
            initHeaderbarImpl()
            initClockTextFormat()
            //do not enable or show Header bar when Sabbath Mode is enabled
            if(CookingAppUtils.isSabbathMode())
                manageTopSheetVisibilityWithDrag(isTopSheetVisible = false, isDrag = false)
            else
                manageTopSheetVisibilityWithDrag(isTopSheetVisible = true, isDrag = true)
        }
    }

    /**
     * header bar clock text view time format
     */
    private fun initClockTextFormat() {
        if(SettingsManagerUtils.getTimeFormat() ==  SettingsManagerUtils.TimeFormatSettings.H_12) {
            binding?.clockTextView?.set12HourTimeFormat(true)
            binding?.clockTextView?.set12HourFormatString(TEXT_TIME_FORMAT_HOUR)
        }else{
            binding?.clockTextView?.set12HourTimeFormat(false)
            binding?.clockTextView?.set24HourFormatString(TEXT_TIME_FORMAT_24_HOUR)
        }
    }

    /**
     * creates NoTitleHeaderBarWidgetImpl object
     */
    private fun initHeaderbarImpl() {
        noTitleHeaderBarWidgetImpl = NoTitleHeaderBarWidgetImpl(
            getBinding(),
            context
        )
    }

    /**
     * manage top sheet horizontal line visibility
     * @param isTopSheetVisible boolean value to show/hide
     * @param isDrag boolean value to enable/disable drag
     */
    fun manageTopSheetVisibilityWithDrag(isTopSheetVisible: Boolean, isDrag: Boolean) {
        noTitleHeaderBarWidgetImpl?.setHeaderBarHorizontalLineVisibility(isTopSheetVisible)
        noTitleHeaderBarWidgetImpl?.getTopSheetView()?.topSheetBehavior?.setAllowUserDragging(isDrag)
    }


    /**
     * @return ClockTextView api of SDK
     */
    fun getClockTextView(): ClockTextView? {
        return noTitleHeaderBarWidgetImpl?.getClockTextView()
    }

    /**
     * @return ClockTextView api of SDK
     */
    fun getKitchenTimerTextView(): KitchenTimerTextView? {
        return noTitleHeaderBarWidgetImpl?.getKitchenTimerTextView()
    }

    /**
     * @return topSheet view
     */
    fun getTopSheetView(): TopSheetView? {
        return noTitleHeaderBarWidgetImpl?.getTopSheetView()
    }

    /**
     * Note: method name must be changed when status name is available
     * @param resource @DrawableRes int placeholder for the status icon <name>
     * <name> status icon name
    </name></name> */
    fun setStatusIcon1Resource(@DrawableRes resource: Int) {
        noTitleHeaderBarWidgetImpl?.getIvStatusIcon1()
            ?.let { noTitleHeaderBarWidgetImpl?.setHeaderBarStatusIcon(it, resource) }
    }

    /**
     * @param isVisible true/false to set visibility of the first status icon
     */
    fun setStatusIcon1Visibility(isVisible: Boolean) {
        noTitleHeaderBarWidgetImpl?.setViewVisibility(getBinding().ivStatusIcon1, isVisible)
    }

    /**
     * @param isVisible true/false to set visibility of the second status icon
     */
    fun setStatusIcon2Visibility(isVisible: Boolean) {
        noTitleHeaderBarWidgetImpl?.setViewVisibility(getBinding().ivStatusIcon2, isVisible)
    }

    /**
     * @param isVisible true/false to set visibility of the third status icon
     */
    fun setStatusIcon3Visibility(isVisible: Boolean) {
        noTitleHeaderBarWidgetImpl?.setViewVisibility(getBinding().ivStatusIcon3, isVisible)
    }

    /**
     * @param isVisible true/false to set visibility of the fourth status icon
     */
    fun setStatusIcon4Visibility(isVisible: Boolean) {
        noTitleHeaderBarWidgetImpl?.setViewVisibility(getBinding().ivStatusIcon4, isVisible)
    }

    /**
     * @param isVisible true/false to set visibility of the header drawer image
     */
    fun setHeaderDrawerVisibility(isVisible: Boolean) {
        noTitleHeaderBarWidgetImpl?.setViewVisibility(getBinding().ivHorizontalLine, isVisible)
    }

    /**
     * @return NoTitleHeaderBarBinding layout
     */
    fun getBinding(): NoTitleHeaderBarBinding {
        return binding!!
    }

    /**
     * clears the used resources
     */
    @Suppress("unused")
    fun clearResources() {
        noTitleHeaderBarWidgetImpl?.clearResources()
    }
}
