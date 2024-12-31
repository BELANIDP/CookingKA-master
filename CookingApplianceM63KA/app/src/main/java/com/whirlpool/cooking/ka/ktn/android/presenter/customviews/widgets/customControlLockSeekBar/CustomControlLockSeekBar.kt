package com.whirlpool.cooking.widgets

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.presenter.customviews.topsheet.TopSheetBehavior
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import com.whirlpool.cooking.ka.R
import core.utils.HMILogHelper

/**
 * File       : com.whirlpool.cooking.widgets.CustomControlLockSeekBar.
 * Brief      : Class for Custom seekbar widget to unlock the Control Lock.
 * Author     : Sainadh Adam / Bosch.
 * Created On : 28.08.2023.
 */
class CustomControlLockSeekBar @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.seekBarStyle
) :
    View(context, attrs, defStyleAttr) {
    private var max = 0
    private var progressValue = 0
    private var thumbDrawable: Drawable? = null
    private var tickMarkDrawable: Drawable? = null
    private var spacing = 0f
    private var isDragging = false
    private var paddingLeft = 0f
    private var paddingRight = 0f
    private var onSeekBarChangeListener: OnSeekBarChangeListener? = null
     var forwardValueAnimator: ValueAnimator? = null
    private var reverseValueAnimator: ValueAnimator? = null

    /**
     * Construct a new seek bar with a default style determined by the given theme attribute,
     * overriding specific style attributes as requested.
     *```````````````````````````````````````````````````````````````````````````````````````````
     * @param context      The Context that will determine this widget's .
     * @param attrs        Specification of attributes that should deviate from the default styling.
     * @param defStyleAttr An attribute in the current theme that contains a
     * reference to a style resource that supplies default values for
     */
    /**
     * Construct a new seek bar with default styling, overriding specific style
     * attributes as requested.
     *
     * @param context The Context that will determine this widget's .
     * @param attrs   Specification of attributes that should deviate from default styling.
     */
    /**
     * Construct a new Seek bar with default styling.
     *
     * @param context The Context that will determine this widget's .
     */
    init {
        applyAttributes(attrs, defStyleAttr)
    }

    /**
     * Function to retrieve attributes defined in the layout file and assign them to variables
     *
     * @param attrs        attributes
     * @param defStyleAttr style
     */
    private fun applyAttributes(attrs: AttributeSet?, defStyleAttr: Int) {
        val attributes = context.obtainStyledAttributes(
            attrs, R.styleable.CustomSeekBar,
            defStyleAttr, 0
        )
        try {
            tickMarkDrawable = attributes.getDrawable(R.styleable.CustomSeekBar_tick)
            thumbDrawable = attributes.getDrawable(R.styleable.CustomSeekBar_thumb_drawable)
            max = attributes.getInt(R.styleable.CustomSeekBar_seek_bar_max, max)
            paddingRight = attributes.getDimension(
                R.styleable.CustomSeekBar_padding_horizontal,
                paddingLeft
            )
            paddingLeft = paddingRight
        } finally {
            attributes.recycle()
        }
    }


    /**
     * Interface for level change (in between dragging)
     */
    interface OnSeekBarChangeListener {
        fun updateControlLockView(progressValues: Int)
        fun unlockControlLock()
    }

    /**
     * Function to return the upper limit of seek bar
     *
     * @return upper limit of seek bar
     */
    private fun getMax(): Int {
        return max
    }

    var progress: Int
        /**
         * Function to return the current index of slider
         *
         * @return upper limit of seek bar
         */
        get() = progressValue
        /**
         * Function to set the index of the thumb
         *
         * @param progress index at which thumb should be placed
         */
        set(progress) {
            HMILogHelper.Logd("drawThumb: setProgress $progress")
            progressValue = progress
            isDragging = false
            invalidate()
        }

    /**
     * Function to set upper limit of range bae
     *
     * @param max value to be set as upper limit
     */
    fun setMax(max: Int) {
        if (max != this.max) {
            this.max = max
            invalidate()
        }
    }

    /**
     * Function to initialize  [CustomControlLockSeekBar.OnSeekBarChangeListener]
     *
     * @param seekBarListener interface object
     */
    fun setOnSeekBarChangeListener(seekBarListener: OnSeekBarChangeListener?) {
        onSeekBarChangeListener = seekBarListener
    }


    /**
     * @see View.onTouchEvent
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val mTouchDownX = event.x

        when (event.action) {
            MotionEvent.ACTION_MOVE -> if (isDragging) {
                val curX = (mTouchDownX / width) * getMax()
                if (curX < 0 || curX > getMax()) {
                    return false
                }
                progressValue = Math.round(curX)
                thumbDrawable =
                    ResourcesCompat.getDrawable(resources, com.whirlpool.cooking.ka.R.drawable.control_lock_slider, null)
                invalidate()
                onSeekBarChangeListener!!.updateControlLockView(progressValue)
            }

            MotionEvent.ACTION_DOWN -> {
                if (mTouchDownX <= paddingLeft + paddingRight) {
                    isDragging = true
                }
                HMILogHelper.Logd("No action required on down & cancel events")
            }

            MotionEvent.ACTION_CANCEL -> HMILogHelper.Logd("No action required on down & cancel events")
            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    rootView.performClick()
                    thumbDrawable =
                        ResourcesCompat.getDrawable(resources, R.drawable.control_lock_slider, null)
                    if (progressValue < 55) {
                        handleReverseAnimation()
                    } else {
                        if (progressValue < getMax()) {
                            handleForwardAnimation()
                        } else {
                            unlockControlLock()
                        }
                    }
                }
                isDragging = false
            }
        }
        return true
    }


    /**
     * Function to move slider forward if user releases it >=55%
     */
     fun handleForwardAnimation() {
        forwardValueAnimator = ValueAnimator.ofInt(progressValue, getMax())
        forwardValueAnimator?.setDuration(200)
        forwardValueAnimator?.addUpdateListener(AnimatorUpdateListener { valueAnimator: ValueAnimator ->
            val progress = valueAnimator.animatedValue as Int
            this.progress = progress
            onSeekBarChangeListener?.updateControlLockView(progressValue)
        })
        forwardValueAnimator?.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                unlockControlLock()
            }
        })
        forwardValueAnimator?.start()
    }

    /**
     * Function to move slider reverse if user releases it <55%
     */
    private fun handleReverseAnimation() {
        reverseValueAnimator = ValueAnimator.ofInt(progressValue, 0)
        reverseValueAnimator?.setDuration(300)
        reverseValueAnimator?.addUpdateListener(AnimatorUpdateListener { valueAnimator: ValueAnimator ->
            val progress = valueAnimator.animatedValue as Int
            this.progress = progress
            onSeekBarChangeListener?.updateControlLockView(progressValue)
        })
        reverseValueAnimator?.start()
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    /**
     * @see View.onDraw
     */
    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawTickMark(canvas)
        drawThumb(canvas)
    }

    /**
     * Function to draw tick marks
     *
     * @param canvas canvas
     */
    private fun drawTickMark(canvas: Canvas) {
        if (tickMarkDrawable != null) {
            val count = getMax()
            if (count > 1) {
                val w = tickMarkDrawable!!.intrinsicWidth
                val h = tickMarkDrawable!!.intrinsicHeight
                val halfW = if (w >= 0) w / 2 else 1
                val halfH = if (h >= 0) h / 2 else 1
                tickMarkDrawable!!.setBounds(-halfW, -halfH, halfW, halfH)
                spacing = (width - paddingLeft - paddingRight) / count.toFloat()
                val saveCount = canvas.save()
                canvas.translate(paddingLeft, height / 2f)
                for (i in 0..count) {
                    canvas.translate(spacing, 0f)
                }
                canvas.restoreToCount(saveCount)
            }
        }
    }

    /**
     * Function to draw thumb
     *
     * @param canvas canvas
     */
    private fun drawThumb(canvas: Canvas) {
        val saveCount = canvas.save()
        if (thumbDrawable != null) {
            val w = thumbDrawable!!.intrinsicWidth
            val h = thumbDrawable!!.intrinsicHeight
            val halfW = if (w >= 0) w / 2 else 1
            val halfH = if (h >= 0) h / 2 else 1
            thumbDrawable!!.setBounds(-halfW, -halfH, halfW, halfH)
            canvas.translate(paddingLeft + spacing * progressValue, height / 2f)
            thumbDrawable!!.draw(canvas)
            canvas.restoreToCount(saveCount)
        }
    }

    /**
     * Function to unlock Control Lock
     */
    private fun unlockControlLock() {
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener!!.unlockControlLock()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (reverseValueAnimator != null) {
            reverseValueAnimator!!.cancel()
        }
        if (forwardValueAnimator != null) {
            forwardValueAnimator!!.cancel()
        }
    }
}