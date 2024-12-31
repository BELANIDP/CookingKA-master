/*
 * ************************************************************************************************
 * ***** Copyright (c) 2019. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.step_progress_bar

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Keep
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import com.whirlpool.cooking.ka.R
import java.util.Random
import kotlin.math.ceil
import kotlin.math.max

//TODO : Need to revisit to cleanup and refactoring
class StepProgressBar : View {
    /**
     * Paint used to draw circle
     */
    private var circlePaint: Paint? = null

    /**
     * List of [Paint] objects used to draw the circle for each step.
     */
    private var stepsCirclePaintList: MutableList<Paint>? = null

    /**
     * The radius for the circle which describes an step.
     *
     *
     * This is either declared via XML or default is used.
     *
     */
    private var circleRadius = 0f

    /**
     *
     *
     * Flag indicating if the steps should be displayed with an number instead of empty circles and current animated
     * with bullet.
     *
     */
    private var showStepTextNumber = false

    /**
     * Paint used to draw the number indicator for all steps.
     */
    private var stepTextNumberPaint: Paint? = null

    /**
     * List of [Paint] objects used to draw the number indicator for each step.
     */
    private var stepsTextNumberPaintList: MutableList<Paint>? = null

    /**
     * Paint used to draw the indicator circle for the current and cleared steps
     */
    private var indicatorPaint: Paint? = null

    /**
     * List of [Paint] objects used by each step indicating the current and cleared steps.
     *
     *  If this is set, it will override the default.
     */
    private var stepsIndicatorPaintList: MutableList<Paint>? = null

    /**
     * Paint used to draw the line between steps - as default.
     */
    private var linePaint: Paint? = null

    /**
     * Paint used to draw the line between steps when done.
     */
    private var lineDonePaint: Paint? = null

    /**
     * Paint used to draw the line between steps when animated.
     */
    private var lineDoneAnimatedPaint: Paint? = null

    /**
     * List of [Path] for each line between steps
     */
    private val linePathList: MutableList<Path> = ArrayList()

    /**
     * The progress of the animation.
     * DO NOT DELETE OR RENAME: Will be used by animations logic.
     */
    @Suppress("unused")
    private var animProgress = 0f

    /**
     * The radius for the animated indicator.
     * DO NOT DELETE OR RENAME: Will be used by animations logic.
     */
    private var animIndicatorRadius = 0f

    /**
     * The radius for the animated check mark.
     * DO NOT DELETE OR RENAME: Will be used by animations logic.
     */
    private var animCheckRadius = 0f
    private var alphaAnimCheckRadius = 0f

    /**
     * "Constant" size of the lines between steps
     */
    private var lineLength = 0f

    // Values retrieved from xml (or default values)
    private var checkRadius = 0f
    private var indicatorRadius = 0f
    private var lineMargin = 0f

    /**
     * Click areas for each of the steps supported by the StepProgressBar widget.
     */
    private var stepsClickAreas: MutableList<RectF>? = null
    private var stepCount = 0
    private var currentStepProgress = 0
    private var previousStep = 0

    // X position of each step indicator's center
    private var indicators: FloatArray? = null
    private lateinit var cloneIndicator: FloatArray

    // Utils to avoid object instantiation during onDraw
    private val stepAreaRect = Rect()
    private val stepAreaRectF = RectF()
    private var doneIcon: Drawable? = null
    private var showDoneIcon = false

    // If viewpager is attached, viewpager's page titles are used when {@code showLabels} equals true
    private var labelPaint: TextPaint? = null
    private var labels: Array<CharSequence?>? = null
    private var showLabels = false
    private var labelMarginTop = 0f
    private lateinit var labelLayouts: Array<StaticLayout?>
    private var maxLabelHeight = 0f

    // Running animations
    private var animatorSet: AnimatorSet? = null
    private var lineAnimator: ObjectAnimator? = null
    private var indicatorAnimator: ObjectAnimator? = null
    private var checkAnimator: ObjectAnimator? = null
    private val checkAnimator1: ObjectAnimator? = null
    private var count = 0
    private var isLastStep = false
    private var isLineAnimatorRunning = false

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs, defStyleAttr)
    }

    @Suppress("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int?,
        defStyleRes: Int?
    ) : super(context, attrs, defStyleAttr!!, defStyleRes!!) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val progressBarColor = ContextCompat.getColor(context, R.color.amber_primary)
        val defaultCircleRadius = resources.getDimension(R.dimen.step_progress_bar_circle_radius)
        val defaultCircleStrokeWidth =
            resources.getDimension(R.dimen.step_progress_bar_circle_stroke_width)
        indicatorRadius = resources.getDimension(R.dimen.step_progress_bar_indicator_radius)
        val lineStrokeWidth = resources.getDimension(R.dimen.step_progress_bar_line_stroke_width)
        lineMargin = resources.getDimension(R.dimen.step_progress_bar_default_line_margin)
        val lineColor = ContextCompat.getColor(context, R.color.black)

        /* Customize the widget based on the properties set on XML, or use default if not provided */
        val a = context.obtainStyledAttributes(attrs, R.styleable.StepProgressBar, defStyleAttr, 0)

        //TODO: Need to refactor
        circlePaint = Paint()
        circlePaint!!.strokeWidth = a.getDimension(
            R.styleable.StepProgressBar_stpi_circleStrokeWidth,
            defaultCircleStrokeWidth
        )
        circlePaint!!.style = Paint.Style.STROKE
        circlePaint!!.color =
            a.getColor(R.styleable.StepProgressBar_stpi_circleColor, progressBarColor)
        circlePaint!!.isAntiAlias = true

        // Call this as early as possible as other properties are configured based on the number of steps
        setStepCount(a.getInteger(R.styleable.StepProgressBar_stpi_stepCount, 2))
        val stepsCircleColorsResId =
            a.getResourceId(R.styleable.StepProgressBar_stpi_stepsCircleColors, 0)
        if (stepsCircleColorsResId != 0) {
            stepsCirclePaintList = ArrayList(stepCount)
            for (i in 0 until stepCount) {
                // Based on the main indicator paint object, we create the customized one
                val circlePaint = Paint(circlePaint)
                if (isInEditMode) {
                    // Fallback for edit mode - to show something in the preview
                    circlePaint.color = randomColor
                } else {
                    // Get the array of attributes for the colors
                    val colorResValues = context.resources.obtainTypedArray(stepsCircleColorsResId)
                    if (stepCount > colorResValues.length()) {
                        throw IllegalArgumentException(
                            "Invalid number of colors for the circles. Please provide a list " +
                                    "of colors with as many items as the number of steps required!"
                        )
                    }
                    circlePaint.color = colorResValues.getColor(i, 0) // specific color
                    // No need for the array anymore, recycle it
                    colorResValues.recycle()
                }
                (stepsCirclePaintList as ArrayList<Paint>).add(circlePaint)
            }
        }
        indicatorPaint = Paint(circlePaint)
        indicatorPaint!!.style = Paint.Style.FILL
        indicatorPaint!!.color =
            a.getColor(R.styleable.StepProgressBar_stpi_indicatorColor, progressBarColor)
        indicatorPaint!!.isAntiAlias = true
        stepTextNumberPaint = Paint(indicatorPaint)
        stepTextNumberPaint!!.textSize = resources.getDimension(R.dimen.stpi_default_text_size)
        showStepTextNumber =
            a.getBoolean(R.styleable.StepProgressBar_stpi_showStepNumberInstead, false)

        // Get the resource from the context style properties
        val stepsIndicatorColorsResId = a
            .getResourceId(R.styleable.StepProgressBar_stpi_stepsIndicatorColors, 0)
        if (stepsIndicatorColorsResId != 0) {
            // init the list of colors with the same size as the number of steps
            stepsIndicatorPaintList = ArrayList(stepCount)
            if (showStepTextNumber) {
                stepsTextNumberPaintList = ArrayList(stepCount)
            }
            for (i in 0 until stepCount) {
                val indicatorPaint = Paint(indicatorPaint)
                val textNumberPaint = if (showStepTextNumber) Paint(stepTextNumberPaint) else null
                if (isInEditMode) {
                    // Fallback for edit mode - to show something in the preview
                    indicatorPaint.color = randomColor // random color
                    if (null != textNumberPaint) {
                        textNumberPaint.color = indicatorPaint.color
                    }
                } else {
                    // Get the array of attributes for the colors
                    val colorResValues =
                        context.resources.obtainTypedArray(stepsIndicatorColorsResId)
                    if (stepCount > colorResValues.length()) {
                        throw IllegalArgumentException(
                            "Invalid number of colors for the indicators. Please provide a list " +
                                    "of colors with as many items as the number of steps required!"
                        )
                    }
                    indicatorPaint.color = colorResValues.getColor(i, 0) // specific color
                    if (null != textNumberPaint) {
                        textNumberPaint.color = indicatorPaint.color
                    }
                    // No need for the array anymore, recycle it
                    colorResValues.recycle()
                }
                (stepsIndicatorPaintList as ArrayList<Paint>).add(indicatorPaint)
                if (showStepTextNumber && null != textNumberPaint) {
                    stepsTextNumberPaintList!!.add(textNumberPaint)
                }
            }
        }
        linePaint = Paint()
        linePaint!!.strokeWidth = lineStrokeWidth
        linePaint!!.strokeCap = Paint.Cap.ROUND
        linePaint!!.style = Paint.Style.STROKE
        linePaint!!.color = a.getColor(R.styleable.StepProgressBar_stpi_lineColor, lineColor)
        linePaint!!.isAntiAlias = true
        lineDonePaint = Paint(linePaint)
        lineDonePaint!!.color =
            a.getColor(R.styleable.StepProgressBar_stpi_lineDoneColor, progressBarColor)
        lineDoneAnimatedPaint = Paint(lineDonePaint)
        circleRadius =
            a.getDimension(R.styleable.StepProgressBar_stpi_circleRadius, defaultCircleRadius)
        checkRadius = circleRadius + circlePaint!!.strokeWidth / 2f
        animIndicatorRadius = indicatorRadius
        animCheckRadius = checkRadius
        val animDuration =
            a.getInteger(R.styleable.StepProgressBar_stpi_animDuration, DEFAULT_ANIMATION_DURATION)
        showDoneIcon = a.getBoolean(R.styleable.StepProgressBar_stpi_showDoneIcon, true)
        doneIcon = a.getDrawable(R.styleable.StepProgressBar_stpi_doneIconDrawable)

        // Labels Configuration
        labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        labelPaint!!.textAlign = Paint.Align.CENTER
        val defaultLabelSize = resources.getDimension(R.dimen.stpi_default_label_size)
        val labelSize = a.getDimension(R.styleable.StepProgressBar_stpi_labelSize, defaultLabelSize)
        labelPaint!!.textSize = labelSize
        val defaultLabelMarginTop = resources.getDimension(R.dimen.stpi_default_label_margin_top)
        labelMarginTop =
            a.getDimension(R.styleable.StepProgressBar_stpi_labelMarginTop, defaultLabelMarginTop)
        showLabels(a.getBoolean(R.styleable.StepProgressBar_stpi_showLabels, false))
        setLabels(a.getTextArray(R.styleable.StepProgressBar_stpi_labels))
        if (a.hasValue(R.styleable.StepProgressBar_stpi_labelColor)) {
            setLabelColor(a.getColor(R.styleable.StepProgressBar_stpi_labelColor, 0))
        } else {
            setLabelColor(getTextColorSecondary(getContext()))
        }
        if (isInEditMode && showLabels && labels == null) {
            labels = arrayOf("First", "Second", "Third", "Fourth", "Fifth")
        }
        if (!a.hasValue(R.styleable.StepProgressBar_stpi_stepCount) && labels != null) {
            setStepCount(labels!!.size)
        }
        a.recycle()
        if (showDoneIcon && doneIcon == null) {
            doneIcon = ContextCompat.getDrawable(context, R.drawable.icon_done)
        }
        if (doneIcon != null) {
            val size = resources.getDimensionPixelSize(R.dimen.stpi_done_icon_size)
            doneIcon!!.setBounds(0, 0, size, size)
        }

        // Display at least 1 cleared step for preview in XML editor
        if (isInEditMode) {
            currentStepProgress = max(ceil((stepCount / 2f).toDouble()).toInt().toDouble(), 1.0)
                .toInt()
        }
    }

    private val randomPaint: Paint
        /**
         * Get an random color [Paint] object.
         *
         * @return [Paint] object with the same attributes as [.circlePaint] and with an random color.
         * @see .circlePaint
         *
         * @see .getRandomColor
         */
        private get() {
            val paint = Paint(indicatorPaint)
            paint.color = randomColor
            return paint
        }
    private val randomColor: Int
        /**
         * Get an random color value.
         *
         * @return The color value as AARRGGBB
         */
        private get() {
            val rnd = Random()
            return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        compute() // for setting up the indicator based on the new position
    }

    /**
     * Make calculations for establishing the exact positions of each step component, for the line dividers, for
     * bottom indicators, etc.
     *
     *
     * Call this whenever there is an layout change for the widget.
     *
     */
    private fun compute() {
        if (null == circlePaint) {
            throw IllegalArgumentException(
                "circlePaint is invalid! Make sure you setup the field circlePaint " +
                        "before calling compute() method!"
            )
        }
        indicators = FloatArray(stepCount)
        cloneIndicator = FloatArray(stepCount)
        linePathList.clear()
        var startX = circleRadius * EXPAND_MARK + circlePaint!!.strokeWidth / 2f
        if (showLabels) {
            // gridWidth is the width of the grid assigned for the step indicator
            val gridWidth = measuredWidth / stepCount
            startX = gridWidth / 2f
        }

        // Compute position of indicators and line length
        val divider = (measuredWidth - startX * 2f) / (stepCount - 1)
        lineLength = /* 2*/
            divider - (circleRadius * 1.5f /* 2f*/ + circlePaint!!.strokeWidth) - lineMargin * 1.5f

        // Compute position of circles and lines once
        for (i in indicators!!.indices) {
            indicators!![i] = startX + divider * i
            cloneIndicator[i] = startX + divider * i
        }
        for (i in 0 until indicators!!.size - 1) {
            val position = (indicators!![i] + indicators!![i + 1]) / 2 - lineLength / 2
            val linePath = Path()
            val lineY = stepCenterY
            linePath.moveTo(position, lineY)
            linePath.lineTo(position + lineLength, lineY)
            linePathList.add(linePath)
        }
        computeStepsClickAreas() // update the position of the steps click area also
    }

    /**
     *
     *
     * Calculate the area for each step. This ensure the correct step is detected when an click event is detected.
     *
     *
     *
     * Whenever [.compute] method is called, make sure to call this method also so that the steps click
     * area is updated.
     *
     */
    fun computeStepsClickAreas() {
        if (stepCount == STEP_INVALID) {
            throw IllegalArgumentException(
                "stepCount wasn't setup yet. Make sure you call setStepCount() " +
                        "before computing the steps click area!"
            )
        }
        if (null == indicators) {
            throw IllegalArgumentException(
                "indicators wasn't setup yet. Make sure the indicators are " +
                        "initialized and setup correctly before trying to compute the click " +
                        "area for each step!"
            )
        }

        // Initialize the list for the steps click area
        stepsClickAreas = ArrayList(stepCount)

        // Compute the clicked area for each step
        for (indicator: Float in indicators!!) {
            // Get the indicator position
            // Calculate the bounds for the step
            val left = indicator - circleRadius * 2
            val right = indicator + circleRadius * 2
            val top = stepCenterY - circleRadius * 2
            val bottom = stepCenterY + circleRadius

            // Store the click area for the step
            val area = RectF(left, top, right, bottom)
            (stepsClickAreas as ArrayList<RectF>).add(area)
        }
    }

    private fun getMaxLabelHeight(): Float {
        return if (showLabels) maxLabelHeight + labelMarginTop else 0f
    }

    private fun calculateMaxLabelHeight(measuredWidth: Int) {
        if (!showLabels) return

        // gridWidth is the width of the grid assigned for the step indicator
        val twoDp = context.resources.getDimensionPixelSize(R.dimen.stpi_two_dp)
        val gridWidth = measuredWidth / stepCount - twoDp
        if (gridWidth <= 0) return

        // Compute StaticLayout for the labels
        labelLayouts = arrayOfNulls(labels!!.size)
        maxLabelHeight = 0f
        val labelSingleLineHeight = labelPaint!!.descent() - labelPaint!!.ascent()
        for (i in labels!!.indices) {
            if (labels!![i] == null) continue
            labelLayouts[i] = StaticLayout(
                labels!![i], labelPaint, gridWidth,
                Layout.Alignment.ALIGN_NORMAL, 1f, 0f, false
            )
            maxLabelHeight = max(
                maxLabelHeight.toDouble(),
                (labelLayouts[i]!!.lineCount * labelSingleLineHeight).toDouble()
            )
                .toFloat()
        }
    }

    private val stepCenterY: Float
        private get() = (measuredHeight - getMaxLabelHeight()) / 2f

    override fun onDraw(canvas: Canvas) {
        val centerY = stepCenterY

        // Currently Drawing animation from step n-1 to n, or back from n+1 to n
        val inAnimation = animatorSet != null && animatorSet!!.isRunning
        val inLineAnimation = lineAnimator != null && lineAnimator!!.isRunning
        val inIndicatorAnimation = indicatorAnimator != null && indicatorAnimator!!.isRunning
        val inCheckAnimation = checkAnimator != null && checkAnimator!!.isRunning
        val drawToNext = previousStep == currentStepProgress - 1
        val drawFromNext = previousStep == currentStepProgress + 1
        for (i in indicators!!.indices) {
            val indicator = indicators!![i]

            // We draw the "done" check if previous step, or if we are going back (if going back, animated value will reduce radius to 0)
            val drawCheck = i < currentStepProgress || (drawFromNext && i == currentStepProgress)

            // Draw back circle
            canvas.drawCircle(indicator, centerY, circleRadius, (getStepCirclePaint(i))!!)
            if (!isLastStep) {
                // Show the current step indicator as bullet
                // If current step, or coming back from next step and still animating
                if ((i == currentStepProgress && !drawFromNext) || ((i == previousStep) && drawFromNext && inAnimation)) {
                    // Draw animated indicator
                    if ((isLineAnimatorRunning) /*|| (currentStep!=i)*/) {
                        canvas.drawCircle(
                            indicator, centerY, circleRadius,
                            (getStepIndicatorPaint(i))!!
                        )
                        canvas.drawPath(linePathList[i - 1], (lineDonePaint)!!)
                    }
                }

                // Draw check mark
                if (drawCheck) {
                    var radius = checkRadius
                    var alpha = 255f
                    // Use animated radius value?
                    if ((i == previousStep && drawToNext) || (i == currentStepProgress && drawFromNext)) {
                        radius = animCheckRadius
                        alpha = alphaAnimCheckRadius
                    }
                    val paint = getStepIndicatorPaint(i)
                    paint!!.alpha = alpha.toInt()
                    canvas.drawCircle(indicator, centerY, radius, (paint))
                    // Draw check bitmap
                    if (!isInEditMode && showDoneIcon) {
                        if ((i != previousStep && i != currentStepProgress) ||
                            (!inCheckAnimation && !(i == currentStepProgress && !inAnimation))
                        ) {
                            canvas.save()
                            canvas.translate(
                                indicator - (doneIcon!!.intrinsicWidth / 2),
                                centerY - (doneIcon!!.intrinsicHeight / 2)
                            )
                            doneIcon!!.draw(canvas)
                            canvas.restore()
                        }
                    }
                }
                // Draw lines
                if (i < linePathList.size) {
                    if (i >= currentStepProgress) {
                        canvas.drawPath(linePathList[i], (linePaint)!!)
                        if ((i == currentStepProgress) && drawFromNext && (inLineAnimation || inIndicatorAnimation)) {
                            // Coming back from n+1
                            canvas.drawPath(linePathList[i], (lineDoneAnimatedPaint)!!)
                        }
                    } else {
                        if ((i == currentStepProgress - 1) && drawToNext && inLineAnimation) {
                            // Going to n+1
                            canvas.drawPath(linePathList[i], (linePaint)!!)
                            canvas.drawPath(linePathList[i], (lineDoneAnimatedPaint)!!)
                        } else {
                            if (i != previousStep) {
                                canvas.drawPath(linePathList[i], (lineDonePaint)!!)
                            }
                        }
                    }
                }
            }
        }
        if (currentStepProgress == indicators!!.size) {
            isLastStep = true
            if (count < 10) {
                circleRadius = circleRadius + 3.5f
                val centerWidth = measuredWidth / 2
                indicators!![0] = indicators!![0] + (centerWidth - cloneIndicator[0]) / 10
                indicators!![1] = indicators!![1] + (centerWidth - cloneIndicator[1]) / 10
                indicators!![2] = indicators!![2] - (cloneIndicator[2] - centerWidth) / 10
                indicators!![3] = indicators!![3] - (cloneIndicator[3] - centerWidth) / 10
                canvas.drawCircle(
                    indicators!![0], centerY, circleRadius,
                    (getStepIndicatorPaint(0))!!
                )
                canvas.drawCircle(
                    indicators!![1], centerY, circleRadius,
                    (getStepIndicatorPaint(1))!!
                )
                canvas.drawCircle(
                    indicators!![2], centerY, circleRadius,
                    (getStepIndicatorPaint(2))!!
                )
                canvas.drawCircle(
                    indicators!![3], centerY, circleRadius,
                    (getStepIndicatorPaint(3))!!
                )
                count++
                invalidate()
            }
        }
    }

    /**
     * Get the [Paint] object which should be used for displaying the current step indicator.
     *
     * @param stepPosition The step position for which to retrieve the [Paint] object
     * @return The [Paint] object for the specified step position
     */
    private fun getStepIndicatorPaint(stepPosition: Int): Paint? {
        return getPaint(stepPosition, stepsIndicatorPaintList, indicatorPaint)
    }

    /**
     * Get the [Paint] object which should be used for drawing the text number the current step.
     *
     * @param stepPosition The step position for which to retrieve the [Paint] object
     * @return The [Paint] object for the specified step position
     */
    private fun getStepTextNumberPaint(stepPosition: Int): Paint? {
        return getPaint(stepPosition, stepsTextNumberPaintList, stepTextNumberPaint)
    }

    /**
     * Get the [Paint] object which should be used for drawing the circle for the step.
     *
     * @param stepPosition The step position for which to retrieve the [Paint] object
     * @return The [Paint] object for the specified step position
     */
    private fun getStepCirclePaint(stepPosition: Int): Paint? {
        return getPaint(stepPosition, stepsCirclePaintList, circlePaint)
    }

    /**
     * Get the [Paint] object based on the step position and the source list of [Paint] objects.
     *
     *
     * If none found, will try to use the provided default. If not valid also, an random [Paint] object
     * will be returned instead.
     *
     *
     * @param stepPosition The step position for which the [Paint] object is needed
     * @param sourceList   The source list of [Paint] object.
     * @param defaultPaint The default [Paint] object which will be returned if the source list does not
     * contain the specified step.
     * @return [Paint] object for the specified step position.
     */
    private fun getPaint(
        stepPosition: Int,
        sourceList: List<Paint>?,
        defaultPaint: Paint?
    ): Paint? {
        isStepValid(stepPosition) // it will throw an error if not valid
        var paint: Paint? = null
        if (null != sourceList && !sourceList.isEmpty()) {
            try {
                paint = sourceList[stepPosition]
            } catch (e: IndexOutOfBoundsException) {
                // We use an random color as this usually should not happen, maybe in edit mode
                Log.d(
                    TAG,
                    "getPaint: could not find the specific step paint to use! Try to use default instead!"
                )
            }
        }
        if (null == paint && null != defaultPaint) {
            // Try to use the default
            paint = defaultPaint
        }
        if (null == paint) {
            Log.d(
                TAG,
                "getPaint: could not use default paint for the specific step! Using random Paint instead!"
            )
            // If we reached this point, not even the default is setup, rely on some random color
            paint = randomPaint
        }
        return paint
    }

    /**
     * Check if the step position provided is an valid and supported step.
     *
     *
     * This method ensured the widget doesn't try to use invalid steps. It will throw an exception whenever an
     * invalid step is detected. Catch the exception if it is expected or it doesn't affect the flow.
     *
     *
     * @param stepPos The step position to verify
     * @return `true` if the step is valid, otherwise it will throw an exception.
     */
    private fun isStepValid(stepPos: Int): Boolean {
        if (stepPos < 0 || stepPos > stepCount - 1) {
            throw IllegalArgumentException(
                ("Invalid step position. " + stepPos + " is not a valid position! it " +
                        "should be between 0 and stepCount(" + stepCount + ")")
            )
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val width = if (widthMode == MeasureSpec.EXACTLY) widthSize else suggestedMinimumWidth
        calculateMaxLabelHeight(width)

        // Compute the necessary height for the widget
        val desiredHeight = ceil(
            ((circleRadius * EXPAND_MARK * 2) +
                    circlePaint!!.strokeWidth +
                    getMaxLabelHeight())
                .toDouble()
        ).toInt()
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val height = if (heightMode == MeasureSpec.EXACTLY) heightSize else desiredHeight
        setMeasuredDimension(width, height)
    }

    @Suppress("unused")
    fun getStepCount(): Int {
        return stepCount
    }

    fun setStepCount(stepCount: Int) {
        if (stepCount < 2) {
            throw IllegalArgumentException("stepCount must be >= 2")
        }
        this.stepCount = stepCount
        currentStepProgress = 0
        compute()
        invalidate()
    }

    /**
     * Sets the current step
     *
     * @param currentStep a value between 0 (inclusive) and stepCount (inclusive)
     */
    @UiThread
    fun setCurrentStep(currentStep: Int) {
        if (currentStep < 0 || currentStep > stepCount) {
            throw IllegalArgumentException("Invalid step value $currentStep")
        }
        previousStep = this.currentStepProgress
        this.currentStepProgress = currentStep

        // Cancel any running animations
        if (animatorSet != null) {
            animatorSet!!.cancel()
        }
        animatorSet = null
        lineAnimator = null
        indicatorAnimator = null
        if (currentStep == previousStep + 1) {
            // Going to next step
            animatorSet = AnimatorSet()

            // pop check mark
            checkAnimator = ObjectAnimator.ofFloat(
                this@StepProgressBar, "alphaAnimCheckRadius",
                255f, 50f, 255f, 50f, 255f
            )


            // Finally, pop current step indicator
            animIndicatorRadius = 0f
            indicatorAnimator = ObjectAnimator.ofFloat(
                this@StepProgressBar, "animIndicatorRadius", 0f,
                indicatorRadius * 1.4f, indicatorRadius
            )
            lineAnimator = ObjectAnimator.ofFloat(this@StepProgressBar, "animProgress", 1.0f, 0.0f)
            animatorSet!!.playSequentially(checkAnimator, indicatorAnimator, lineAnimator)
        } else if (currentStep == previousStep - 1) {
            // Going back to previous step
            animatorSet = AnimatorSet()

            // First, pop out current step indicator
            indicatorAnimator = ObjectAnimator
                .ofFloat(this@StepProgressBar, "animIndicatorRadius", indicatorRadius, 0f)

            // Then delete line
            animProgress = 1.0f
            lineDoneAnimatedPaint!!.setPathEffect(null)
            lineAnimator = ObjectAnimator.ofFloat(this@StepProgressBar, "animProgress", 0.0f, 1.0f)

            // Finally, pop out check mark to display step indicator
            animCheckRadius = checkRadius
            checkAnimator = ObjectAnimator
                .ofFloat(this@StepProgressBar, "animCheckRadius", checkRadius, indicatorRadius)
            animatorSet!!.playSequentially(indicatorAnimator, lineAnimator, checkAnimator)
        }
        if (animatorSet != null) {
            // Max 500 ms for the animation
            lineAnimator!!.setDuration(1000)
            lineAnimator!!.interpolator = DecelerateInterpolator()
            checkAnimator!!.setDuration(500)
            indicatorAnimator!!.startDelay = 200
            indicatorAnimator!!.setDuration(100)
            checkAnimator!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                    isLineAnimatorRunning = false
                }

                override fun onAnimationEnd(animator: Animator) {}
                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
            lineAnimator!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {
                    isLineAnimatorRunning = false
                }

                override fun onAnimationEnd(animator: Animator) {
                    isLineAnimatorRunning = true
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
            animatorSet!!.start()
        }
        invalidate()
    }

    /**
     *
     *
     * Setter method for the animation progress.
     *
     * <font color="red">DO NOT CALL, DELETE OR RENAME</font>: Will be used by animation.
     */
    @Keep
    @Suppress("unused")
    fun setAnimProgress(animProgress: Float) {
        this.animProgress = animProgress
        lineDoneAnimatedPaint!!.setPathEffect(createPathEffect(lineLength, animProgress, 0.0f))
        invalidate()
    }

    /**
     *
     *
     * Setter method for the indicator radius animation.
     *
     * <font color="red">DO NOT CALL, DELETE OR RENAME</font>: Will be used by animation.
     */
    @Keep
    @Suppress("unused")
    fun setAnimIndicatorRadius(animIndicatorRadius: Float) {
        this.animIndicatorRadius = animIndicatorRadius
        invalidate()
    }

    /**
     *
     *
     * Setter method for the checkmark radius animation.
     *
     * <font color="red">DO NOT CALL, DELETE OR RENAME</font>: Will be used by animation.
     */
    @Keep
    @Suppress("unused")
    fun setAnimCheckRadius(animCheckRadius: Float) {
        this.animCheckRadius = animCheckRadius
        invalidate()
    }

    @Keep
    @Suppress("unused")
    fun setAlphaAnimCheckRadius(alphaAnimCheckRadius: Float) {
        this.alphaAnimCheckRadius = alphaAnimCheckRadius
        invalidate()
    }

    /**
     * Pass a labels array of Char sequence that is greater than or equal to the `stepCount`.
     * Never pass `null` to this manually. Call `showLabels(false)` to hide labels.
     *
     * @param labelsArray Non-null array of CharSequence
     */
    fun setLabels(labelsArray: Array<CharSequence?>?) {
        if (labelsArray == null) {
            labels = null
            return
        }
        if (stepCount > labelsArray.size) {
            throw IllegalArgumentException(
                "Invalid number of labels for the indicators. Please provide a list " +
                        "of labels with at least as many items as the number of steps required!"
            )
        }
        labels = labelsArray
        showLabels(true)
    }

    fun setLabelColor(color: Int) {
        labelPaint!!.color = color
        requestLayout()
        invalidate()
    }

    /**
     * Shows the labels if true is passed. Else hides them.
     *
     * @param show Boolean to show or hide the labels
     */
    fun showLabels(show: Boolean) {
        showLabels = show
        requestLayout()
        invalidate()
    }

    fun setDoneIcon(doneIcon: Drawable?) {
        this.doneIcon = doneIcon
        if (doneIcon != null) {
            showDoneIcon = true
            val size = context.resources.getDimensionPixelSize(R.dimen.stpi_done_icon_size)
            doneIcon.setBounds(0, 0, size, size)
        }
        invalidate()
    }

    fun setShowDoneIcon(showDoneIcon: Boolean) {
        this.showDoneIcon = showDoneIcon
        invalidate()
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        // Try to restore the current step
        currentStepProgress = savedState.mCurrentStep
        requestLayout()
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        // Store current stop so that it can be resumed when restored
        savedState.mCurrentStep = currentStepProgress
        return savedState
    }

    /**
     * Contract used by the StepProgressBar widget to notify any listener of steps interaction events.
     */
    interface OnStepClickListener {
        /**
         * Step was clicked
         *
         * @param step The step position which was clicked. (starts from 0, as the ViewPager bound to the widget)
         */
        fun onStepClicked(step: Int)
    }

    /**
     * Saved state in which information about the state of the widget is stored.
     *
     *
     * Use this whenever you want to store or restore some information about the state of the widget.
     *
     */
    private class SavedState : BaseSavedState {
        var mCurrentStep = 0

        internal constructor(superState: Parcelable?) : super(superState)
        private constructor(`in`: Parcel) : super(`in`) {
            mCurrentStep = `in`.readInt()
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeInt(mCurrentStep)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        override fun describeContents(): Int {
            return 0
        }

        object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        private val TAG = "StepProgressBar"

        /**
         * Duration of the line drawing animation (ms)
         */
        private val DEFAULT_ANIMATION_DURATION = 200

        /**
         * Max multiplier of the radius when a step is being animated to the "done" state before going to it's normal radius
         */
        private val EXPAND_MARK = 1.3f
        private val STEP_INVALID = -1
        fun getTextColorSecondary(context: Context): Int {
            val t = context.obtainStyledAttributes(intArrayOf(android.R.attr.textColorSecondary))
            val color = t.getColor(0, ContextCompat.getColor(context, R.color.black))
            t.recycle()
            return color
        }

        private fun createPathEffect(pathLength: Float, phase: Float, offset: Float): PathEffect {
            // Create a PathEffect to set on a Paint to only draw some part of the line
            return DashPathEffect(
                floatArrayOf(pathLength, pathLength),
                max((phase * pathLength).toDouble(), offset.toDouble()).toFloat()
            )
        }
    }
}
