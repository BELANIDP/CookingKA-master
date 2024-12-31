package android.presenter.customviews.widgets.headerbar

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.drawable.Drawable
import android.presenter.customviews.topsheet.TopSheetView
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.whirlpool.cooking.ka.databinding.NoTitleHeaderBarBinding
import com.whirlpool.hmi.kitchentimer.uicomponents.widgets.KitchenTimerTextView
import com.whirlpool.hmi.uicomponents.widgets.clock.ClockTextView


// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL.
// Created by SINGHA80 on 05-Feb-24.
// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL.
// Created by SINGHA80 on 05-Feb-24.
class NoTitleHeaderBarWidgetImpl(binding: NoTitleHeaderBarBinding?, context: Context?) :
    NoTitleHeaderBarWidgetInterface {
    private var binding: NoTitleHeaderBarBinding?
    private var context: Context?

    init {
        this.binding = binding
        this.context = context
    }

    /**
     * handles icon visibility of the status icons.
     * @param view status icon image
     * @param isVisible boolean value true/false to hide/unhide
     */
    override fun setViewVisibility(view: View, isVisible: Boolean) {
        val id = view.id
        if (id == binding?.ivStatusIcon1?.id) {
            if (isVisible) {
                unHideView(view)
            } else {
                hideView(view)
            }
        } else if (id == binding?.ivStatusIcon2?.id) {
            if (isVisible) {
                unHideView(view)
            } else {
                hideView(view)
            }
        } else if (id == binding?.ivStatusIcon3?.id) {
            if (isVisible) {
                unHideView(view)
            } else {
                hideView(view)
            }
        } else if (id == binding?.ivStatusIcon4?.id) {
            if (isVisible) {
                unHideView(view)
            } else {
                hideView(view)
            }
        } else if (id == binding?.ivHorizontalLine?.id) {
            if (isVisible) {
                unHideView(view)
            } else {
                hideView(view)
            }
        } else if (id == binding?.topSheetView?.id) {
            if (isVisible) {
                unHideView(view)
            } else {
                hideView(view)
            }
        }

    }

    /**
     * @param view status icon image
     * @param resource @DrawableRes int placeholder for the status icons
     */
    override fun setHeaderBarStatusIcon(view: View, @DrawableRes resource: Int) {
        val id = view.id
        if (id == getIvStatusIcon1()?.id) {
            if (checkIfResourceExist(resource) != null) {
                getIvStatusIcon1()?.setImageResource(resource)
            } else throw NotFoundException("Please check if resource exist...")
        } else if (id == getIvStatusIcon2()?.id) {
            if (checkIfResourceExist(resource) != null) {
                getIvStatusIcon2()?.setImageResource(resource)
            } else throw NotFoundException("Please check if resource exist...")
        } else if (id == getIvStatusIcon3()?.id) {
            if (checkIfResourceExist(resource) != null) {
                getIvStatusIcon3()?.setImageResource(resource)
            } else throw NotFoundException("Please check if resource exist...")
        } else if (id == getIvStatusIcon4()?.id) {
            if (checkIfResourceExist(resource) != null) {
                getIvStatusIcon4()?.setImageResource(resource)
            } else throw NotFoundException("Please check if resource exist...")
        }
    }

    /**
     * @param view to set visibility gone if its already visible
     */
    private fun hideView(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
        }
    }

    /**
     * @param view to set visibility visible if its already gone
     */
    private fun unHideView(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        }
    }

    /**
     * @param resource of the drawable
     * @return true/false according to the drawable resource exist or not
     */
    private fun checkIfResourceExist(resource: Int): Drawable? {
        return ContextCompat.getDrawable(context!!, resource)
    }

    /**
     * manage top sheet horizontal line visibility
     * @param isVisible boolean value to show/hide
     */
    fun setHeaderBarHorizontalLineVisibility(isVisible: Boolean) {
        getTopSheetView()?.let {
            setViewVisibility(it, isVisible)
        }
    }

    /**
     * @return image view for status icon 1
     * Note: icon 1 name needs to changed when the status is known
     */
    fun getIvStatusIcon1(): ImageView? {
        return binding?.ivStatusIcon1
    }

    /**
     * @return image view for status icon 2
     * Note: icon 2 name needs to changed when the status is known
     */
    private fun getIvStatusIcon2(): ImageView? {
        return binding?.ivStatusIcon2
    }

    /**
     * @return image view for status icon 3
     * Note: icon 3 name needs to changed when the status is known
     */
    private fun getIvStatusIcon3(): ImageView? {
        return binding?.ivStatusIcon3
    }

    /**
     * @return image view for status icon 4
     * Note: icon 4 name needs to changed when the status is known
     */
    private fun getIvStatusIcon4(): ImageView? {
        return binding?.ivStatusIcon4
    }

    /**
     * @return ClockTextView api of SDK
     */
    fun getClockTextView(): ClockTextView? {
        return binding?.clockTextView
    }

    /**
     * @return ClockTextView api of SDK
     */
    fun getKitchenTimerTextView(): KitchenTimerTextView? {
        return binding?.tvKitchenTimeRemaining
    }

    fun getTopSheetView(): TopSheetView? {
        return binding?.topSheetView
    }

    /**
     * clears the used resources
     */
    override fun clearResources() {
        binding = null
        context = null
    }
}
