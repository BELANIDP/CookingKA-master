/***Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL***/
package android.presenter.customviews.widgets.headerBarNumpad

import android.view.View
import android.view.View.OnClickListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.HeaderBarNumpadBinding
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView
import core.utils.HMILogHelper

/*
 * File : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.widgets.headerBarNumpad.HeaderBarNumPadImpl
 * Author : SINGHA80.
 * Created On : 3/21/24
 * Details : Implementation class of HeaderBarNumPadInterface
 */
class HeaderBarNumPadImpl(
    private var binding: HeaderBarNumpadBinding?
) : HeaderBarNumPadInterface, OnClickListener {

    private var onBackIconClick: (() -> Unit?)? = null
    private var onTumblerIconClick: (() -> Unit?)? = null
    private var onCancelIconClick: (() -> Unit?)? = null

    init {
        binding?.ivBackIcon?.setOnClickListener(this)
        binding?.ivCancelIcon?.setOnClickListener(this)
        binding?.ivTumblerIcon?.setOnClickListener(this)
        binding?.leftIconWithTouchImprovement?.setOnClickListener(this)
    }


    override fun setBackIconOnClick(onBackIconClick: () -> Unit) {
        this.onBackIconClick = onBackIconClick
    }

    override fun setTumblerIconOnClick(onTumblerIconClick: () -> Unit) {
        this.onTumblerIconClick = onTumblerIconClick
    }

    override fun setCancelIconOnClick(onCancelIconClick: () -> Unit) {
        this.onCancelIconClick = onCancelIconClick
    }


    /**
     * set tumbler icon visibility visible/gone
     * @param isVisible true/false
     */
    override fun setTumblerIconVisibility(isVisible: Boolean) {
        hideUnHideView(ivTumblerIcon, isVisible)
        hideUnHideView(tumblerParentView, isVisible)
    }

    /**
     * set ttitle textview visibility visible/gone
     * @param isVisible true/false
     */
    override fun setTitleTextViewVisibility(isVisible: Boolean) {
        hideUnHideView(textViewCookTime, isVisible)
    }

    /**
     * set ttitle textview visibility visible/gone
     * @param isVisible true/false
     */
    override fun setKeypadTextViewVisibility(isVisible: Boolean) {
        hideUnHideView(keypadTextView, isVisible)
    }

    /**
     * set keypad date time textview visibility visible/gone
     * @param isVisible true/false
     */
    override fun setKeypadTextViewForDateTimeVisibility(isVisible: Boolean) {
        hideUnHideView(keypadTextViewForDateTime, isVisible)
    }

    /**
     * set cancel icon visibility visible/gone visible/gone
     * @param isVisible true/false
     */
    override fun setCancelIconVisibility(isVisible: Boolean) {
        hideUnHideView(ivCancelIcon, isVisible)
        hideUnHideView(cancelParentView, isVisible)
    }

    /**
     * set back icon visibility visible/gone visible/gone
     * @param isVisible true/false
     */
    override fun setBackIconVisibility(isVisible: Boolean) {
        hideUnHideView(ivBackIcon, isVisible)
        hideUnHideView(clBackIcon, isVisible)
    }

    /**
     * @return textViewCookTime instance
     */
    override fun getTvCookTime(): TextView? {
        return textViewCookTime
    }
    /**
     * @return KeypadTextView instance
     */
    override fun getHeaderKeypadTextView(): KeypadTextView? {
        return keypadTextView
    }
    /**
     * @return KeypadTextView instance for date and time
     */
    override fun getHeaderKeypadTextViewForDateTime(): KeypadTextView? {
        return keypadTextViewForDateTime
    }
    /**
     * @return Keypad Cancel View instance
     */
    override fun getCancelView(): ImageView? {
        return ivCancelIcon
    }


    /**
     * @return Back button View instance
     */
    override fun getBackButtonView(): View? {
        return ivBackIcon
    }

    /**
     * sets cancel icon enable/disable
     * @param isEnable true/false
     */
    override fun setCancelIconEnable(isEnable: Boolean) {
        ivCancelIcon?.isEnabled = isEnable
    }

    /**
     * sets tumbler icon enable/disable
     * @param isEnable true/false
     */
    override fun setTumblerIconEnable(isEnable: Boolean) {
        ivTumblerIcon?.isEnabled = isEnable
    }


    /**
     * @param view that is to be used for the visibility
     * @param isVisible true/false for the visibility
     */
    private fun hideUnHideView(view: View?, isVisible: Boolean) {
        view?.apply {
            if (isVisible) {
                if (visibility == View.GONE) {
                    visibility = View.VISIBLE
                }
            } else {
                if (visibility == View.VISIBLE) {
                    visibility = View.GONE
                }
            }
        }
    }

    /**
     * provides android default view onClick
     * @param view that is to be used for the click
     */
    override fun onClick(view: View?) {
        when (view?.id) {
            binding?.ivBackIcon?.id -> {
                if (onBackIconClick != null) {
                    onBackIconClick?.let { it() }
                } else {
                    HMILogHelper.Loge(this.javaClass.simpleName, "default back icon click")
                }

            }

            binding?.ivCancelIcon?.id -> {
                if (onCancelIconClick != null) {
                    onCancelIconClick?.let { it() }
                } else {
                    HMILogHelper.Loge(this.javaClass.simpleName, "default cancel icon click")
                }
            }

            binding?.ivTumblerIcon?.id -> {
                if (onTumblerIconClick != null) {
                    onTumblerIconClick?.let { it() }
                } else {
                    HMILogHelper.Loge(this.javaClass.simpleName, "default tumbler icon click")
                }
            }

            binding?.leftIconWithTouchImprovement?.id -> {
                if (onBackIconClick != null) {
                    onBackIconClick?.let { it() }
                } else {
                    HMILogHelper.Loge(this.javaClass.simpleName, "default back icon click")
                }

            }
        }

    }

    /**
     * @return getter for back icon ImageView instance
     */
    private val ivBackIcon: ImageView?
        get() = binding?.ivBackIcon

    /**
     * @return getter for cancel icon ImageView instance
     */
    private val ivCancelIcon: ImageView?
        get() = binding?.ivCancelIcon

    /**
     * @return getter for tumbler icon ImageView instance
     */
    private val ivTumblerIcon: ImageView?
        get() = binding?.ivTumblerIcon

    /**
     * @return getter for tumbler parent framelayout instance
     */
    private val tumblerParentView: FrameLayout?
        get() = binding?.tumblerParentView

    /**
     * @return getter for cancel parent framelayout instance
     */
    private val cancelParentView: FrameLayout?
        get() = binding?.cancelParentView
    /**
     * @return getter for textViewCookTime instance
     */
    private val textViewCookTime: TextView?
        get() = binding?.tvCookTime

    /**
     * @return getter for KeypadTextView instance
     */
    private val keypadTextView: KeypadTextView?
        get() = binding?.keypadTextView

    /**
     * @return getter for KeypadTextView instance for date and time
     */
    private val keypadTextViewForDateTime: KeypadTextView?
        get() = binding?.keypadTextViewForDateTime
    /**
     * clears the resources used
     */
    override fun clearResources() {
        binding = null
        onBackIconClick = null
        onTumblerIconClick = null
        onCancelIconClick = null
    }

    private val clBackIcon: ConstraintLayout?
        get() = binding?.leftIconWithTouchImprovement

}