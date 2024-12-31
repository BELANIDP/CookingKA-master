/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase.abstractViewHolders

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.BundleKeys.Companion.BUNDLE_CUSTOMIZE_KNOB_STEPPER_TYPE
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.SharedPreferenceManager


/**
 * File        : core.jbase.abstractViewHolders.AbstractFeaturesGuideViewHolder
 * Brief       : Appliance features guide view holder
 * Author      : Nikki Gharde
 * Created On  : 04/Sep/2024
 * Details     : acts as skinnable fragment to give functionality to populate features guide
 */
abstract class AbstractUnboxingApplianceFeaturesGuideViewHolder : View.OnTouchListener,
    ViewTreeObserver.OnScrollChangedListener,
    ViewTreeObserver.OnPreDrawListener,
    HMIKnobInteractionListener {

    private val tag = "AbstractFeaturesGuideViewHolder"
    private var rotator = listOf(0, 1) // Instruction Widget, CTA's
    var selectedRotator = rotator[0]
    var isInstructionWidgetSelected = true

    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     *
     * @param inflater           [LayoutInflater]
     * @param container          [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */
    abstract fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View

    /**
     * Optional - provides the ability to add custom behaviors after the view has been created
     *
     * @param ignoredView               [View]
     * @param ignoredSavedInstanceState [Bundle]
     */
    open fun onViewCreated(ignoredView: View, ignoredSavedInstanceState: Bundle?) {
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        generateFeaturesGuideDescriptionText()
        backNavigationFragmentResultListener()
    }

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    abstract fun onDestroyView()

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

    /**
     * Required - GifImageView is used to show GifImageView icon
     *
     * @return [GifImageView]
     */
    abstract fun provideGifImageView(): ImageView

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
        customizeKnobButtonClickListener()
        nextButtonClickListener()
        headerBackArrowClickListener()
    }

    /**
     * Customize knob bottom button on click listener
     */
    private fun customizeKnobButtonClickListener() {
        provideGhostButtonTextView().setOnClickListener {
            HMILogHelper.Logd("Unboxing", "Unboxing: navigate to start customize knob flow")
            val bundle = Bundle()
            bundle.putInt(BUNDLE_CUSTOMIZE_KNOB_STEPPER_TYPE,provideStepperView().currentStep)
            NavigationUtils.navigateSafely(
                provideFragment(),
                R.id.action_unboxingApplianceFeaturesInfoFragment_to_settingsKnobSwapFragment,
                bundle,
                null
            )
        }
    }
    /**
     * header bar back button on click listener
     */
    private fun headerBackArrowClickListener() {
        provideHeaderBackView().setOnClickListener {
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            provideGhostButtonTextView().background = provideResources().let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.text_view_ripple_effect, null
                )
            }
            providePrimaryButtonTextView().background = provideResources().let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.text_view_ripple_effect, null
                )
            }
            resetToUnBoxingApplianceFeaturesDefaultSelection()
            when (provideStepperView().currentStep) {
                1 -> {
                    if (SharedPreferenceManager.getTechnicianTestDoneStatusIntoPreference() == AppConstants.TRUE_CONSTANT) {
                        NavigationUtils.navigateSafely(
                            provideFragment(),
                            R.id.action_unboxingApplianceFeaturesInfoFragment_to_unboxingUserRoleListFragment,
                            null,
                            null
                        )
                    } else {
                        NavigationUtils.navigateSafely(
                            provideFragment(),
                            R.id.action_unboxingApplianceFeaturesInfoFragment_to_fragment_language,
                            null,
                            null
                        )
                    }
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
            resetToUnBoxingApplianceFeaturesDefaultSelection()
            if (provideStepperView().currentStep >= provideStepperView().noOfStepCount) {
                // navigate to start thank you for purchase flow
                HMILogHelper.Logd(
                    "Unboxing",
                    "Unboxing: navigate to start appliance explore flow"
                )
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                NavigationUtils.navigateSafely(
                    provideFragment(),
                    R.id.action_unboxingApplianceFeaturesInfoFragment_to_unboxingExploreFeaturesInfoFragment,
                    null,
                    null
                )
                return@setOnClickListener
            }
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
    private fun nextStepProcess(isIncremental:Boolean) {
        if (isIncremental) {
            provideStepperView().setStepperCurrentStep(provideStepperView().currentStep.plus(1))
        } else {
            provideStepperView().setStepperCurrentStep(provideStepperView().currentStep.minus(1))
        }
        when (provideStepperView().currentStep) {
            1 -> {
                populateLeftKnobData()
            }

            2 -> {
                populateRightKnobData()
            }

            3 -> {
                populateSwapKnobData()
            }
            else -> {
                populateSwapKnobData()
            }
        }
        provideDescriptionTextScrollView().fullScroll(ScrollView.FOCUS_UP)
    }

    private fun populateSwapKnobData() {
        updateKnobGif(R.drawable.knob_swap)
        provideFeaturesGuideTitleTextView().text = provideResources().getString(
            getPopupDataTitleForFeaturesGuide(provideStepperView().currentStep)
        )
        provideFeaturesGuideDescriptionTextView().text =
            addBulletPointsWithDescription(provideStepperView().currentStep)
    }

    /**
     * Method responsible for populate the title and description text
     */
    private fun populateTitleAndDescription() {
        var currentStepper: Int? = provideStepperView().currentStep
        if (provideFragment().arguments != null &&
            provideFragment().arguments?.containsKey(BUNDLE_CUSTOMIZE_KNOB_STEPPER_TYPE) == true
        ) {
            currentStepper = provideFragment().arguments?.getInt(BUNDLE_CUSTOMIZE_KNOB_STEPPER_TYPE)
        }
        provideStepperView().setStepperCurrentStep(currentStepper ?: 0)
        when (currentStepper) {
            1 -> {
                populateLeftKnobData()
            }

            2 -> {
                populateRightKnobData()
            }

            3 -> {
                populateSwapKnobData()
            }

            else -> {
                //Do nothing
            }
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
                getPopupDataTitleForFeaturesGuide(1)
            )
            provideFeaturesGuideDescriptionTextView().text =
                addBulletPointsWithDescription(4)
        }
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

    private fun updateKnobGif(knobImage: Int) {
        Glide.with(provideFragment())
            .asGif()
            .load(knobImage)
            .into(provideGifImageView())
    }

    abstract fun provideFragment(): Fragment


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
    private fun addBulletPointsWithDescription(arrayPosition: Int):SpannableStringBuilder {
        // List of statements to add bullets
        var arrayList = provideResources().getStringArray(R.array.appliance_features_guide_common_message_1)
        when (arrayPosition) {
            1 -> arrayList = provideResources().getStringArray(R.array.appliance_features_guide_common_message_1)
            2 -> arrayList = provideResources().getStringArray(R.array.appliance_features_guide_common_message_2)
            3 -> arrayList = provideResources().getStringArray(R.array.appliance_features_guide_common_message_3)
            4 -> arrayList = provideResources().getStringArray(R.array.appliance_features_guide_common_message_4)
            5 -> arrayList = provideResources().getStringArray(R.array.appliance_features_guide_common_message_5)
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
                BulletSpan(bulletGapWidth,
                    ResourcesCompat.getColor(provideResources(), R.color.color_white, null),AppConstants.BULLET_RADIUS),
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
       provideFragment().let {
           it.viewLifecycleOwner.let { it1 ->
               it.parentFragmentManager.setFragmentResultListener(
                   BundleKeys.BUNDLE_EXTRA_COMING_FROM_APPLIANCE_EXPLORE_FLOW,
                   it1
               ) { _, bundle ->
                   val result = bundle.getBoolean(BundleKeys.BUNDLE_EXTRA_COMING_FROM_APPLIANCE_EXPLORE_FLOW)
                   HMILogHelper.Logd(tag,"get back navigation result from appliance explore flow")
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
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
        if (providePrimaryButtonTextView().isEnabled)
            providePrimaryButtonTextView().performClick()
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (provideDescriptionTextScrollView().canScrollVertically(1))
            provideDescriptionTextScrollView().fullScroll(ScrollView.FOCUS_DOWN)
    }

    override fun onKnobSelectionTimeout(knobId: Int) {

    }
    fun removeObserver() {
        provideDescriptionTextScrollView().viewTreeObserver.removeOnPreDrawListener(this)
        provideDescriptionTextScrollView().viewTreeObserver.removeOnScrollChangedListener(this)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    fun resetToUnBoxingApplianceFeaturesDefaultSelection() {
        selectedRotator = 0
        isInstructionWidgetSelected = true
        provideGhostButtonTextView().background = provideResources().let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
        providePrimaryButtonTextView().background = provideResources().let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
    }
}