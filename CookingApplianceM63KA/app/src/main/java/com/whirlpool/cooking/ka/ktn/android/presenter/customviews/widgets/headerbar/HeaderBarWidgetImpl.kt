@file:Suppress("unused")

package android.presenter.customviews.widgets.headerbar

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.HeaderBarWidgetBinding
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AudioManagerUtils

/*
Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL
*/
/**
 * HeaderBarWidgetImpl class implements methods of HeaderBarWidgetInterface interface & android view OnClickListener
 * interface
 */
class HeaderBarWidgetImpl(
    private var binding: HeaderBarWidgetBinding?,
    private var context: Context?
) : HeaderBarWidgetInterface, View.OnClickListener {
    private var onClickListener: HeaderBarWidgetInterface.CustomClickListenerInterface? = null
    private var onClickListenerIconRight: HeaderBarWidgetInterface.RightIconClickListenerInterface? = null
    private var onClickListenerIconLeft: HeaderBarWidgetInterface.LeftIconClickListenerInterface? = null

    /**
     * @param resource of the drawable
     * @return true/false according to the drawable resource exist or not
     */
    private fun checkIfResourceExist(resource: Int): Drawable? {
        return ContextCompat.getDrawable(context!!, resource)
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
     * @param text to set title
     */
    override fun setTitleText(text: String?) {
        tvTitle?.text = text
    }

    /**
     * @param resId to set title based on resource id
     */
    override fun setTitleText(@StringRes resId: Int?) {
        tvTitle?.setText(resId!!)
    }

    /**
     * @return string text
     */
    override fun getTitleText(): String {
        return tvTitle?.text.toString()
    }

    /**
     * @param text to set oven cavity title text
     */
    override fun setOvenCavityTitleText(text: String?) {
        tvOvenCavityName?.text = text
    }

    /**
     * @param resId to set oven cavity title text based on resource id
     */
    override fun setOvenCavityTitleText(@StringRes resId: Int?) {
        tvOvenCavityName?.setText(resId!!)
    }

    /**
     * @return string text for oven cavity title
     */
    override fun getOvenCavityTitleText(): String {
        return tvOvenCavityName?.text.toString()
    }

    /**
     * @return rerun title view text for header
     */
    override fun getHeaderTitleTextView(): ResourceTextView? {
        return tvTitleResource
    }

    /**
     * @return rerun title right image view header
     */
    override fun getRightImageView(): ImageView? {
        return ivRightIcon
    }

    /**
     * @return rerun title left image view header
     */
    override fun getLeftImageView(): ImageView? {
        return ivLeftIcon
    }


    override fun getOvenCavityTitleTextView(): TextView? {
        return tvOvenCavityName
    }

    /**
     * @param onClickListener to set custom click listeners
     */
    override fun setCustomOnClickListener(onClickListener: HeaderBarWidgetInterface.CustomClickListenerInterface?) {
        this.onClickListener = onClickListener
    }

    override fun setRightIconClickListener(onClickListener: HeaderBarWidgetInterface.RightIconClickListenerInterface?) {
        this.onClickListenerIconRight = onClickListener
    }

    override fun setLeftIconClickListener(onClickListener: HeaderBarWidgetInterface.LeftIconClickListenerInterface?) {
        this.onClickListenerIconLeft = onClickListener
    }

    override fun onClick(view: View) {
        val id = view.id
        if (onClickListener != null) {
            AudioManagerUtils.playOneShotSound(
                view.context,
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            when (id) {
                flOvenCavityIcon?.id -> {
                    onClickListener?.ovenCavityOnClick()
                }
                flLeftIcon?.id -> {
                    onClickListener?.leftIconOnClick()
                }
                flRightIcon?.id -> {
                    onClickListener?.rightIconOnClick()
                }
                ivInfo?.id -> {
                    onClickListener?.infoIconOnClick()
                }
                clLeftIcon?.id -> {
                    onClickListener?.leftIconOnClick()
                }
                clRightIcon?.id -> {
                    onClickListener?.rightIconOnClick()
                }
                clInfo?.id -> {
                    onClickListener?.infoIconOnClick()
                }
            }
        }
        if (onClickListenerIconRight != null) {
            when (id) {
                flRightIcon?.id -> {
                    AudioManagerUtils.playOneShotSound(
                        view.context,
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    onClickListenerIconRight!!.rightIconOnClick()
                }
            }
        }
        if (onClickListenerIconLeft != null) {
            when (id) {
                flLeftIcon?.id -> {
                    AudioManagerUtils.playOneShotSound(
                        view.context,
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    onClickListenerIconLeft!!.leftIconOnClick()
                }
            }

        }
    }

    /**
     * @param view that is to be used for the image background
     * @param resource drawable image background
     */
    override fun setIconImage(view: View, resource: Int) {
        if (checkIfResourceExist(resource) != null) {
            val id = view.id
            when (id) {
                ivOvenCavity?.id -> {
                    ivOvenCavity?.setImageResource(resource)
                }
                ivLeftIcon?.id -> {
                    ivLeftIcon?.setImageResource(resource)
                }
                ivRightIcon?.id -> {
                    ivRightIcon?.setImageResource(resource)
                }
                ivInfo?.id -> {
                    ivInfo?.setImageResource(resource)
                }
            }
        }
    }

    /**
     * @param view that is to be used for the visibility
     * @param isVisible true/false for the visibility
     */
    override fun setIconVisibility(view: View, isVisible: Boolean) {
        val id = view.id
        when (id) {
            binding?.clOvenCavity?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.flOvenCavityIcon?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.ivLeftIcon?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.ivRightIcon?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.rightIconKnobLine?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.leftIconKnobLine?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.ivInfo?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.tvOvenCavityName?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.leftIconWithTouchImprovement?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.clInfo?.id -> {
                hideUnHideView(view, isVisible)
            }
            binding?.rightIconWithTouchImprovement?.id -> {
                hideUnHideView(view, isVisible)
            }
        }
    }

    /**
     * set default on click listener
     */
    fun setDefaultOnClickListener() {
        flOvenCavityIcon?.setOnClickListener(this)
        flLeftIcon?.setOnClickListener(this)
        flRightIcon?.setOnClickListener(this)
        ivInfo?.setOnClickListener(this)
        clLeftIcon?.setOnClickListener(this)
        clRightIcon?.setOnClickListener(this)
        clInfo?.setOnClickListener(this)
    }

    /**
     * @return frame layout for oven cavity
     */
    private val flOvenCavityIcon: FrameLayout?
        get() = binding?.flOvenCavityIcon

    /**
     * @return constraint layout for oven cavity
     */
    val clOvenCavity: ConstraintLayout?
        get() = binding?.clOvenCavity

    /**
     * @return image view for oven cavity
     */
    val ivOvenCavity: ImageView?
        get() = binding?.ivOvenCavity


    /**
     * @return frame layout for left icon
     */
    private val flLeftIcon: FrameLayout?
        get() = binding?.flLeftIcon

    /**
     * @return image view for left icon
     */
    val ivLeftIcon: ImageView?
        get() = binding?.ivLeftIcon

    /**
     * @return constraint layout for text header bar
     */
    val clTextHeaderBar: ConstraintLayout?
        get() = binding?.clTextHeaderBar

    /**
     * @return string text of title
     */
    val tvTitle: TextView?
        get() = binding?.tvTitle

    /**
     * @return resource header text of title
     */
    private val tvTitleResource: ResourceTextView?
        get() = binding?.tvTitleResource

    /**
     * @return image view for info icon
     */
    val ivInfo: ImageView?
        get() = binding?.ivInfo

    /**
     * @return frame layout for right icon
     */
    private val flRightIcon: FrameLayout?
        get() = binding?.flRightIcon

    /**
     * @return image view for right icon
     */
    val ivRightIcon: ImageView?
        get() = binding?.ivRightIcon
    /**
     * @return image view for right icon
     */
    val ivRightIconUnderline: View?
        get() = binding?.rightIconKnobLine/**
     * @return image view for right icon
     */
    val ivLeftIconUnderline: View?
        get() = binding?.leftIconKnobLine
    /**
     * @return text view for oven caity name
     */
    val tvOvenCavityName: TextView?
        get() = binding?.tvOvenCavityName

    /**
     * clear used resources
     */
    override fun clearResources() {
        binding = null
        context = null
        onClickListener = null
    }

    val clLeftIcon: ConstraintLayout?
        get() = binding?.leftIconWithTouchImprovement

    val clRightIcon: ConstraintLayout?
        get() = binding?.rightIconWithTouchImprovement

    val clInfo: ConstraintLayout?
        get() = binding?.clInfo
}
