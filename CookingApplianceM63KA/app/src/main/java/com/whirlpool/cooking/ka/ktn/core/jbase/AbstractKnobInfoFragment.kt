/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import com.whirlpool.hmi.utils.ContextProvider
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.handleTextScrollOnKnobRotateEvent
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


/**
 * File        : core.jbase.AbstractKobInfoFragment
 * Brief       : AbstractKnobInfo abstract class
 * Author      : Rajendra Paymode
 * Created On  : 11/OCT/2024
 */
abstract class AbstractKnobInfoFragment : SuperAbstractTimeoutEnableFragment(),
    View.OnTouchListener,
    ViewTreeObserver.OnScrollChangedListener,
    ViewTreeObserver.OnPreDrawListener,
    HMIKnobInteractionListener {

    private val tag = "AbstractKnobFunctionInfoViewHolder"
    private var rotator = listOf(0, 1) // Instruction Widget, Right CTA
    private var selectedRotator = rotator[0]
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)
    private var isInstructionWidgetSelected = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        generateFeaturesGuideDescriptionText()
        backNavigationFragmentResultListener()
        primaryButtonVisibility(false)
    }

    /**
     * Used to provide resources
     *
     * @return [Resources]
     */
    @Suppress("unused")
    abstract fun provideResources(): Resources

    /**
     * Required - provideTitleText is used to set the text to TitleTextView
     *
     * @return [CharSequence]
     */
    abstract fun provideTitleText(): CharSequence

    /**
     * Required - provideTitleTextView is used to show the Title text on the screen
     *
     * @return [TextView]
     */
    abstract fun provideTitleTextView(): TextView


    /**
     * Required - provideDescriptionTextScrollView is used to show long text in scroll view
     *
     * @return [TextView]
     */
    abstract fun provideDescriptionTextScrollView(): ScrollView

    /**
     * Required - provideHeaderBackView is used to show header back icon
     *
     * @return [FrameLayout]
     */
    abstract fun provideHeaderBackView(): FrameLayout

    // We want to detect scroll and not touch,
    // so returning false in this member function
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }

    // Member function to detect Scroll,
    // when detected 0, it means bottom is reached
    override fun onScrollChanged() {
        val view =
            provideDescriptionTextScrollView().getChildAt(provideDescriptionTextScrollView().childCount - 1)
        val bottomDetector: Int =
            view.bottom - (provideDescriptionTextScrollView().height + provideDescriptionTextScrollView().scrollY)
        if (bottomDetector <= 0) {
            primaryButtonVisibility(true)
        }
    }

    override fun onPreDraw(): Boolean {
        val lineCount = provideFeaturesGuideDescriptionTextView().lineCount
        HMILogHelper.Logd(tag, "TextView LineCount for Scroll $lineCount")
        if (!provideDescriptionTextScrollView().canScrollVertically(1))
            primaryButtonVisibility(true)
        provideDescriptionTextScrollView().viewTreeObserver.removeOnPreDrawListener(this)
        return true
    }

    @Suppress("SameParameterValue")
    private fun primaryButtonVisibility(isEnable: Boolean) {
        if (isEnable) {
            providePrimaryButtonTextView().isEnabled = true
            providePrimaryButtonTextView().setTextColor(
                provideResources().getColor(
                    R.color.color_white,
                    null
                )
            )
            return
        }
        providePrimaryButtonTextView().isEnabled = false
        providePrimaryButtonTextView().setTextColor(
            provideResources().getColor(
                R.color.text_button_disabled_grey,
                null
            )
        )
    }

    /**
     * Required - provideFeaturesGuideDescriptionText is used to set the text to DescriptionTextView
     *
     * @return [CharSequence]
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun generateFeaturesGuideDescriptionText() {

        provideDescriptionTextScrollView().setOnTouchListener(this)
        provideDescriptionTextScrollView().viewTreeObserver.addOnPreDrawListener(this)
        provideDescriptionTextScrollView().viewTreeObserver.addOnScrollChangedListener(this)
        provideStepperView().setStepperCurrentStep(1)

        populateTitleAndDescription()

        nextButtonClickListener()
        headerBackArrowClickListener()
    }

    /**
     * header bar back button on click listener
     */
    private fun headerBackArrowClickListener() {
        provideHeaderBackView().setOnClickListener {
            AudioManagerUtils.playOneShotSound(
                view?.context,
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            when (provideStepperView().currentStep) {
                1 -> {
                    NavigationUtils.navigateSafely(
                        this,
                        R.id.action_settingsKnobInfoFragment_to_settingsKnobFragment,
                        null,
                        null
                    )
                    removeObserver()
                    KnobNavigationUtils.setBackPress()
                }
                else -> {
                    nextStepProcess(isIncremental = false)
                }
            }
        }
    }

    /**
     * Next bottom button on click listener
     */
    private fun nextButtonClickListener() {
        providePrimaryButtonTextView().setOnClickListener {
            if (provideStepperView().currentStep == provideResources().getInteger(R.integer.integer_range_2)) {
                // navigate to start thank you for purchase flow
                HMILogHelper.Logd(
                    "Knob",
                    "Knob: navigate to start appliance explore flow"
                )
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.start_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_settingsKnobInfoFragment_to_settingsKnobFragment,
                    null,
                    null
                )
                return@setOnClickListener
            }else
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
            nextStepProcess(isIncremental = true)
        }
    }

    /**
     * Method responsible for next flow or go to previous flow
     */
    private fun nextStepProcess(isIncremental: Boolean) {
        if (isIncremental) {
            provideStepperView().setStepperCurrentStep(provideStepperView().currentStep.plus(1))
        } else {
            resetToDefaultSelection()
            provideStepperView().setStepperCurrentStep(provideStepperView().currentStep.minus(1))
            if (provideDescriptionTextScrollView().canScrollVertically(-1))
                provideDescriptionTextScrollView().fullScroll(ScrollView.FOCUS_UP)
        }
        primaryButtonVisibility(false)
        when (provideStepperView().currentStep) {
            1 -> {
                populateLeftKnobData()
                providePrimaryButtonTextView().text =
                    provideResources().getString(R.string.text_button_next)
            }

            2 -> {
                populateRightKnobData()
                providePrimaryButtonTextView().text =
                    provideResources().getString(R.string.text_button_done)
            }
        }
        provideDescriptionTextScrollView().fullScroll(ScrollView.FOCUS_UP)
    }

    private fun populateRightKnobData() {
        if (AppConstants.RIGHT_KNOB_ID == 1) {
            updateKnobGif(R.drawable.right_knob_cooking)
            provideFeaturesGuideTitleTextView().text = provideResources().getString(
                getPopupDataTitleForFeaturesGuide(2)
            )
            provideFeaturesGuideDescriptionTextView().text =
                addBulletPointsWithDescription(2)
        } else {
            updateKnobGif(R.drawable.right_knob_settings)
            provideFeaturesGuideTitleTextView().text = provideResources().getString(
                getPopupDataTitleForFeaturesGuide(7)
            )
            provideFeaturesGuideDescriptionTextView().text =
                addBulletPointsWithDescription(5)
        }
    }

    private fun populateLeftKnobData() {
        if (AppConstants.LEFT_KNOB_ID == 0) {
            updateKnobGif(R.drawable.left_knob_settings)
            provideFeaturesGuideTitleTextView().text = provideResources().getString(
                getPopupDataTitleForFeaturesGuide(6)
            )
            provideFeaturesGuideDescriptionTextView().text =
                addBulletPointsWithDescription(1)
        } else {
            updateKnobGif(R.drawable.left_knob_cooking)
            provideFeaturesGuideTitleTextView().text = provideResources().getString(
                getPopupDataTitleForFeaturesGuide(4)
            )
            provideFeaturesGuideDescriptionTextView().text =
                addBulletPointsWithDescription(4)
        }
    }

    private fun updateKnobGif(knobImage: Int) {
        Glide.with(this)
            .asGif()
            .load(knobImage)
            .into(provideGifImageView())
    }

    /**
     * Method responsible for populate the title and description text
     */
    private fun populateTitleAndDescription() {
        populateLeftKnobData()
    }

    /**
     * Required - provideFeaturesGuideDescriptionTextView is used to show the Description text on the screen
     *
     * @return [AppCompatTextView]
     */
    abstract fun provideFeaturesGuideDescriptionTextView(): AppCompatTextView

    /**
     * Required - provideFeaturesGuideTitleTextView is used to show the Description text on the screen
     *
     * @return [AppCompatTextView]
     */
    abstract fun provideFeaturesGuideTitleTextView(): AppCompatTextView

    /**
     * Required - provideStepperView is used to show the dots indicating how many pages are there
     *
     * @return [Stepper]
     */
    abstract fun provideStepperView(): Stepper


    /**
     * Required - GifImageView is used to show GifImageView icon
     *
     * @return [GifImageView]
     */
    abstract fun provideGifImageView(): ImageView

    /**
     * Required - providePrimaryButtonText is used to set the text on the primary button
     *
     * @return [CharSequence]
     */
    abstract fun providePrimaryButtonText(): CharSequence

    /**
     * Required - providePrimaryButtonTextView is used to show the action to perform
     *
     * @return [TextView]
     */
    abstract fun providePrimaryButtonTextView(): TextView

    /**
     * Required - provideGhostButtonTextView is used to show the action to perform
     *
     * @return [TextView]
     */
    abstract fun provideGhostButtonTextView(): TextView

    /**
     * Required - provideCookingViewModel is used to get the recipe information
     *
     * @return [TextView]
     */
    abstract fun provideCookingViewModel(): CookingViewModel

    /**
     * Required - provideContext is used to context
     *
     * @return [TextView]
     */
    abstract fun provideContext(): Context

    /**
     * Required - provideView is used to get the view
     *
     * @return [TextView]
     */
    abstract fun provideView(): View


    /**
     * Method to get features guide Popup title
     */
    private fun getPopupDataTitleForFeaturesGuide(commonMessagePosition: Int): Int {
        return CookingAppUtils.getResIdFromResName(
            provideContext(),
            AppConstants.TEXT_TITLE_APPLIANCE_FEATURES_GUIDE + commonMessagePosition,
            AppConstants.RESOURCE_TYPE_STRING
        )
    }

    /**
     * add bullets point each \n statement
     */
    private fun addBulletPointsWithDescription(arrayPosition: Int): SpannableStringBuilder {
        // List of statements to add bullets
        var arrayList =
            provideResources().getStringArray(R.array.appliance_features_guide_common_message_1)
        when (arrayPosition) {
            1 -> {
                arrayList =
                    provideResources().getStringArray(R.array.appliance_features_guide_common_message_1)
            }

            2 -> {
                arrayList =
                    provideResources().getStringArray(R.array.appliance_features_guide_common_message_2)
            }

            4 -> {
                arrayList =
                    provideResources().getStringArray(R.array.appliance_features_guide_common_message_4)
            }

            5 -> {
                arrayList =
                    provideResources().getStringArray(R.array.appliance_features_guide_common_message_5)
            }
        }

        // Create a SpannableStringBuilder to hold the text with bullets
        val spannableStringBuilder = SpannableStringBuilder()

        // Define bullet gap width (space between bullet and text)
        val bulletGapWidth = AppConstants.BULLET_POINTS_GAP

        // Loop through each statement and add a BulletSpan
        for (statement in arrayList) {
            val start = spannableStringBuilder.length
            spannableStringBuilder.append(statement.toString()).append("\n")
            spannableStringBuilder.setSpan(
                BulletSpan(
                    bulletGapWidth,
                    ResourcesCompat.getColor(provideResources(), R.color.color_white, null),
                    AppConstants.BULLET_RADIUS
                ),
                start,
                spannableStringBuilder.length,
                SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableStringBuilder
    }

    /** back navigation listener when click event detected from appliance explore fragment
     */
    private fun backNavigationFragmentResultListener() {
        this.let {
            it.viewLifecycleOwner.let { it1 ->
                it.parentFragmentManager.setFragmentResultListener(
                    BundleKeys.BUNDLE_EXTRA_COMING_FROM_APPLIANCE_EXPLORE_FLOW,
                    it1
                ) { _, bundle ->
                    val result =
                        bundle.getBoolean(BundleKeys.BUNDLE_EXTRA_COMING_FROM_APPLIANCE_EXPLORE_FLOW)
                    if (result) {
                        provideStepperView().setStepperCurrentStep(2)
                        nextStepProcess(isIncremental = true)
                    }
                }
            }
        }
    }

    /******************* KNOB Interaction methods ********************/

    override fun onHMILeftKnobClick() {
        HMILogHelper.Logd("Knob", "onHMILeftKnobClick() called  : $selectedRotator")
        when (selectedRotator) {
            0 -> {
                if(providePrimaryButtonTextView().isEnabled) {
                    isInstructionWidgetSelected = false
                    selectedRotator = 1
                    providePrimaryButtonTextView().background = provideResources().let {
                        ResourcesCompat.getDrawable(
                            it, R.drawable.selector_textview_walnut, null
                        )
                    }
                }
            }
            1 -> {
                if (providePrimaryButtonTextView().isEnabled) {
                    if (provideStepperView().currentStep == provideResources().getInteger(R.integer.integer_range_2))
                        KnobNavigationUtils.knobForwardTrace = true
                    else
                        resetToDefaultSelection()
                    providePrimaryButtonTextView().callOnClick()
                }
            }
        }
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (isInstructionWidgetSelected) {
                selectedRotator = 0
                // Use the custom coroutine scope to launch the coroutine
                coroutineScope.launch {
                    val scrollView = provideDescriptionTextScrollView()
                    val scrollViewContent = provideFeaturesGuideDescriptionTextView()
                    val lineHeight = scrollViewContent.measuredHeight / scrollViewContent.lineHeight
                    handleTextScrollOnKnobRotateEvent(scrollView, lineHeight, knobDirection)
                }
            } else {
                selectedRotator = 1
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        resetToDefaultSelection()
    }

    fun removeObserver() {
        provideDescriptionTextScrollView().viewTreeObserver.removeOnPreDrawListener(this)
        provideDescriptionTextScrollView().viewTreeObserver.removeOnScrollChangedListener(this)
    }

    private fun resetToDefaultSelection(){
        isInstructionWidgetSelected = true
        selectedRotator = 0
        providePrimaryButtonTextView().background = provideResources().let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeObserver()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }
}