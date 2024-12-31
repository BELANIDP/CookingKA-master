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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.gone
import core.utils.visible


/**
 * File        : core.jbase.abstractViewHolders.AbstractExploreFeaturesGuideViewHolder
 * Brief       : Appliance explore features guide view holder
 * Author      : Nikki Gharde
 * Created On  : 04/Sep/2024
 * Details     : acts as skinnable fragment to give functionality to populate features guide
 */
abstract class AbstractUnboxingExploreFeaturesGuideViewHolder : View.OnTouchListener,
    ViewTreeObserver.OnScrollChangedListener,
    ViewTreeObserver.OnPreDrawListener,
    HMIKnobInteractionListener {

    private val tag = "AbstractExploreFeaturesGuideViewHolder"

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
        generateExploreFeaturesGuideDescriptionText()
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

    @Suppress("SameParameterValue")
    private fun ghostButtonVisibility(isEnable: Boolean) {
        if (isEnable) {
            provideGhostButtonTextView().isEnabled = true
            provideGhostButtonTextView().setTextColor(
                provideResources().getColor(
                    R.color.color_white,
                    null
                )
            )
            return
        }
        provideGhostButtonTextView().isEnabled = false
        provideGhostButtonTextView().setTextColor(
            provideResources().getColor(
                R.color.text_button_disabled_grey,
                null
            )
        )
    }

    /**
     * Required - generateExploreFeaturesGuideDescriptionText is used to set the text to DescriptionTextView
     *
     * @return [CharSequence]
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun generateExploreFeaturesGuideDescriptionText() {
        provideDescriptionTextScrollView().setOnTouchListener(this)
        provideDescriptionTextScrollView().viewTreeObserver.addOnPreDrawListener(this)
        provideDescriptionTextScrollView().viewTreeObserver.addOnScrollChangedListener(this)
        if(!SettingsManagerUtils.isUnboxing){
            nextStepProcess(isIncremental = true)
        }
        else {
            populateTitleAndDescription()
        }
        skipButtonClickListener()
        nextButtonClickListener()
        headerBackArrowClickListener()
    }

    /**
     * skip bottom button on click listener
     */
    private fun skipButtonClickListener() {
        provideGhostButtonTextView().setOnClickListener {
            PopUpBuilderUtils.exploreFeaturesExitPopup(provideFragment(), onContinueButtonClick = {
                HMILogHelper.Logd("Unboxing", "skip: Unboxing: Navigate to connect network page")
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.start_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                if(!SettingsManagerUtils.isUnboxing) {
                    NavigationUtils.navigateSafely(
                        provideFragment(),
                        R.id.action_unBoxingExploreFeaturesGuideFragment_to_clockFragment,
                        null,
                        null
                    )
                }
                else{
                    NavigationUtils.navigateSafely(
                        provideFragment(),
                        R.id.action_unboxingExploreFeaturesInfoFragment_to_unboxingConnectToNetworkFragment,
                        null,
                        null
                    )
                }
            })
            return@setOnClickListener
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
            resetCTABackground()
            when (provideStepperView().currentStep) {
                0 -> {
                    //send back result to previous fragment - Appliance features
                    provideFragment().parentFragmentManager.setFragmentResult(
                        BundleKeys.BUNDLE_EXTRA_COMING_FROM_APPLIANCE_EXPLORE_FLOW,
                        bundleOf(BundleKeys.BUNDLE_EXTRA_COMING_FROM_APPLIANCE_EXPLORE_FLOW to true)
                    )
                    NavigationUtils.navigateSafely(
                        provideFragment(),
                        R.id.action_unboxingExploreFeaturesInfoFragment_to_unboxingApplianceFeaturesInfoFragment,
                        null,
                        null
                    )
                }

                1 -> {
                    provideStepperView().setStepperCurrentStep(
                        provideStepperView().currentStep.minus(
                            1
                        )
                    )
                    if(!SettingsManagerUtils.isUnboxing){
                        NavigationUtils.navigateSafely(
                            provideFragment(),
                            R.id.action_unBoxingExploreFeaturesGuideFragment_to_clockFragment,
                            null,
                            null
                        )
                    }
                    else {
                        populateTitleAndDescription()
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
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            if (provideStepperView().currentStep >= provideStepperView().noOfStepCount) {
                if(!SettingsManagerUtils.isUnboxing){
                    HMILogHelper.Logd("Unboxing", "Unboxing: Remove notification and Navigate to Clock")
                    // Remove notification: Get to know your product.
                    NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_GET_TO_KNOW_YOUR_APPLIANCE)
                    NotificationManagerUtils.removeNotificationFromNotificationCenter(NotificationJsonKeys.NOTIFICATION_GET_TO_KNOW_YOUR_APPLIANCE)
                    NavigationUtils.navigateSafely(
                        provideFragment(),
                        R.id.action_unBoxingExploreFeaturesGuideFragment_to_clockFragment,
                        null,
                        null
                    )
                } else {
                    HMILogHelper.Logd("Unboxing", "Unboxing: Navigate to connect network page")
                    NavigationUtils.navigateSafely(
                        provideFragment(),
                        R.id.action_unboxingExploreFeaturesInfoFragment_to_unboxingConnectToNetworkFragment,
                        null,
                        null
                    )
                }
                return@setOnClickListener
            }
            if (KnobNavigationUtils.knobForwardTrace)
                KnobNavigationUtils.knobForwardTrace = false else resetCTABackground()
            nextStepProcess(isIncremental = true)
        }
    }

    private fun nextStepProcess(isIncremental: Boolean) {
        if (isIncremental) {
            provideStepperView().setStepperCurrentStep(provideStepperView().currentStep.plus(1))
        } else {
            provideStepperView().setStepperCurrentStep(provideStepperView().currentStep.minus(1))
        }
        ghostButtonVisibility(isEnable = true)
        when (provideStepperView().currentStep) {
            1 -> {
                updateExploreFeatureGif(R.drawable.quick_start)
                provideFeaturesGuideTitleTextView().text = provideResources().getString(
                    getPopupDataTitleForExploreFeaturesGuide(provideStepperView().currentStep)
                )
                provideFeaturesGuideDescriptionTextView().text = provideResources().getString(
                    getPopupDataDesciptionForExploreFeaturesGuide(provideStepperView().currentStep)
                )
            }

            2 -> {
                updateExploreFeatureGif(R.drawable.far_view)
                provideFeaturesGuideTitleTextView().text = provideResources().getString(
                    getPopupDataTitleForExploreFeaturesGuide(provideStepperView().currentStep)
                )
                provideFeaturesGuideDescriptionTextView().text = provideResources().getString(
                    getPopupDataDesciptionForExploreFeaturesGuide(provideStepperView().currentStep)
                )
            }

            3 -> {
                provideGifImageView().setImageResource(R.drawable.assisted_unboxing)
                provideFeaturesGuideTitleTextView().text = provideResources().getString(
                    getPopupDataTitleForExploreFeaturesGuide(provideStepperView().currentStep)
                )
                provideFeaturesGuideDescriptionTextView().text = provideResources().getString(
                    getPopupDataDesciptionForExploreFeaturesGuide(provideStepperView().currentStep)
                )
                ghostButtonVisibility(isEnable = false)
            }
        }
        provideDescriptionTextScrollView().fullScroll(ScrollView.FOCUS_UP)
        provideFeaturesGuideTitleTextView().visible()
        provideGhostButtonTextView().visible()
        provideGhostButtonTextView().text = provideResources().getString(R.string.text_button_SKIP)
        provideTitleTextView().text =
            provideResources().getString(R.string.text_header_explore_features)
    }

    private fun updateExploreFeatureGif(featureImage: Int) {
        Glide.with(provideFragment())
            .asGif()
            .load(featureImage)
            .into(provideGifImageView())
    }
    /**
     * Method responsible for populate the title and description text
     */
    private fun populateTitleAndDescription() {
        provideGifImageView().setImageResource(R.drawable.cinnamon_rolls)
        provideTitleTextView().text = provideTitleText()
        //Appliance explore feature guide title populate
        provideFeaturesGuideTitleTextView().gone()
        provideGhostButtonTextView().gone()
        provideFeaturesGuideTitleTextView().text = ""
        //Appliance explore feature guide description populate
        provideFeaturesGuideDescriptionTextView().text =
            provideResources().getString(R.string.text_description_celebration)
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
    private fun getPopupDataTitleForExploreFeaturesGuide(commonMessagePosition: Int): Int {
        return CookingAppUtils.getResIdFromResName(
            provideContext(),
            AppConstants.TEXT_TITLE_APPLIANCE_EXPLORE_FEATURES_GUIDE + commonMessagePosition,
            AppConstants.RESOURCE_TYPE_STRING
        )
    }

    /**
     * Method to get features guide Popup title
     */
    private fun getPopupDataDesciptionForExploreFeaturesGuide(commonMessagePosition: Int): Int {
        return CookingAppUtils.getResIdFromResName(
            provideContext(),
            AppConstants.TEXT_DESCRIPTION_APPLIANCE_EXPLORE_FEATURES_GUIDE + commonMessagePosition,
            AppConstants.RESOURCE_TYPE_STRING
        )
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
        //Do nothing
    }

    /** back navigation listener when click event detected from appliance explore fragment
     */
    private fun backNavigationFragmentResultListener() {
        provideFragment().let {
            it.viewLifecycleOwner.let { it1 ->
                it.parentFragmentManager.setFragmentResultListener(
                    BundleKeys.BUNDLE_EXTRA_COMING_FROM_CONNECT_NETWORK_FLOW,
                    it1
                ) { _, bundle ->
                    val result = bundle.getBoolean(BundleKeys.BUNDLE_EXTRA_COMING_FROM_CONNECT_NETWORK_FLOW)
                    HMILogHelper.Logd(tag,"get back navigation result from connect network flow")
                    if (result) {
                        provideStepperView().setStepperCurrentStep(2)
                        nextStepProcess(isIncremental = true)
                    }
                }
            }
        }
    }
    fun removeObserver() {
        provideDescriptionTextScrollView().viewTreeObserver.removeOnPreDrawListener(this)
        provideDescriptionTextScrollView().viewTreeObserver.removeOnScrollChangedListener(this)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    private fun resetCTABackground() {
        KnobNavigationUtils.knobForwardTrace = false
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