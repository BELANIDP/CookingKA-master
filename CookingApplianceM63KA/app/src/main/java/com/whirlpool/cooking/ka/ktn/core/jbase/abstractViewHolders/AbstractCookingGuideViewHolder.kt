package core.jbase.abstractViewHolders

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.handleTextScrollOnKnobRotateEvent
import core.utils.DoorEventUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.PopUpBuilderUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * File        : core.jbase.abstractViewHolders.AbstractCookingGuideViewHolder
 * Brief       : Assisted Cooking guide view holder
 * Author      : Hiren
 * Created On  : 05/20/2024
 * Details     : acts as skinnable fragment to give functionality to populate cooking guide
 */
abstract class AbstractCookingGuideViewHolder: View.OnTouchListener, ViewTreeObserver.OnScrollChangedListener, ViewTreeObserver.OnPreDrawListener, HMIKnobInteractionListener{
    private val tag = "AssistedCookingGuide"
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)
    private var rotator = listOf(0, 1) // Instruction Widget, Right CTA
    private var selectedRotator = rotator[0]
    private var isInstructionWidgetSelected = true
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
        generateCookingGuideDescriptionText()
        providePrimaryButtonTextView().postDelayed({
            primaryButtonVisibility(!provideDescriptionTextScrollView().canScrollVertically(1))
            isInstructionWidgetSelected = provideDescriptionTextScrollView().canScrollVertically(1)
            if(KnobNavigationUtils.knobForwardTrace){
                KnobNavigationUtils.knobForwardTrace = false
                resetToDefaultSelection(true)
            }
        }, 50)

        providePrimaryButtonConstraint().postDelayed({
            primaryButtonVisibility(!provideDescriptionTextScrollView().canScrollVertically(1))
            isInstructionWidgetSelected = provideDescriptionTextScrollView().canScrollVertically(1)
            if(KnobNavigationUtils.knobForwardTrace){
                KnobNavigationUtils.knobForwardTrace = false
                resetToDefaultSelection()
            }
        }, 50)
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
    abstract fun provideTitleTextView(): TextView?

    /**
     * Required - provideTitleTextView is used to show the Title text on the screen
     *
     * @return [TextView]
     */
    abstract fun provideHeaderView(): HeaderBarWidget?


    /**
     * Required - provideDescriptionTextScrollView is used to show long text in scroll view
     *
     * @return [TextView]
     */
    abstract fun provideDescriptionTextScrollView(): ScrollView

    // We want to detect scroll and not touch,
    // so returning false in this member function
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }
    // Member function to detect Scroll,
    // when detected 0, it means bottom is reached
    override fun onScrollChanged() {
        val view = provideDescriptionTextScrollView().getChildAt(provideDescriptionTextScrollView().childCount - 1)
        val bottomDetector: Int = view.bottom - (provideDescriptionTextScrollView().height + provideDescriptionTextScrollView().scrollY)
        if (bottomDetector <= 0) {
            primaryButtonVisibility(true)
        }
    }

    override fun onPreDraw(): Boolean {
        val lineCount = provideCookingGuideDescriptionTextView().lineCount
        HMILogHelper.Logd(tag, "TextView LineCount for Scroll $lineCount")
        if(!provideDescriptionTextScrollView().canScrollVertically(1))
            primaryButtonVisibility(true)
        provideDescriptionTextScrollView().viewTreeObserver.removeOnPreDrawListener(this)
        return true
    }

    private fun primaryButtonVisibility(isEnable: Boolean){
        if(isEnable){
            providePrimaryButtonTextView().isEnabled = true
            providePrimaryButtonConstraint().isEnabled = true
            providePrimaryButtonTextView().setTextColor(
                provideResources().getColor(
                    com.whirlpool.hmi.cooking.R.color.color_white,
                    null
                )
            )
            return
        }
        providePrimaryButtonTextView().isEnabled = false
        providePrimaryButtonConstraint().isEnabled = false
        providePrimaryButtonTextView().setTextColor(
            provideResources().getColor(
                R.color.text_button_disabled_grey,
                null
            )
        )
    }
    /**
     * Required - provideCookingGuideDescriptionText is used to set the text to DescriptionTextView
     *
     * @return [CharSequence]
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun generateCookingGuideDescriptionText() {
        HMILogHelper.Logd(
            tag,
            "recipeName ${provideCookingViewModel().recipeExecutionViewModel.recipeName.value}"
        )
        provideStepperView().noOfStepCount = CookingAppUtils.getCookingGuideListSize()
        provideDescriptionTextScrollView().setOnTouchListener(this)
        primaryButtonVisibility(!provideDescriptionTextScrollView().canScrollVertically(1))
        provideDescriptionTextScrollView().viewTreeObserver.addOnPreDrawListener(this)
        provideDescriptionTextScrollView().viewTreeObserver.addOnScrollChangedListener(this)
        resetToDefaultSelection()
        when (CookingAppUtils.cookingGuideList.first()) {
            AppConstants.TEXT_COOK_GUIDE -> {
                provideHeaderView()?.apply {
                    setTitleText(R.string.text_header_cooking_guide)
                }
                setRecipeImage(
                    provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: "",
                    AppConstants.TEXT_COOK_GUIDE
                )
                provideCookingGuideDescriptionTextView().text = provideResources().getString(
                    CookingAppUtils.getPopupDataForCookGuide(
                        provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: ""
                    )
                )
                CookingAppUtils.cookingGuideList.removeFirst()
            }

            AppConstants.TEXT_ACCESSORY_GUIDE -> {
                provideHeaderView()?.apply {
                    setTitleText(R.string.text_header_accessory_guide)
                }
                setRecipeImage(
                    provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: "",
                    AppConstants.TEXT_ACCESSORY_GUIDE
                )
                provideCookingGuideDescriptionTextView().text = provideResources().getString(
                    CookingAppUtils.getPopupDataForAccessoryGuide(
                        provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: ""
                    )
                )
                CookingAppUtils.cookingGuideList.removeFirst()
            }
        }
        provideStepperView().setStepperCurrentStep(1)

        if(provideStepperView().currentStep >= provideStepperView().noOfStepCount){
            val delayTime = provideFragment().arguments?.getLong(BundleKeys.BUNDLE_NAVIGATED_ASSISTED_DELAY_COOKING_GUIDE)
            if (provideCookingViewModel().recipeExecutionViewModel.isRunning){
                providePrimaryButtonTextView().text = provideResources().getString(R.string.text_button_ok)
            }else{
                if (delayTime != null && delayTime > 0)
                    providePrimaryButtonTextView().text = provideResources().getString(R.string.text_button_start_delay)
                else
                    providePrimaryButtonTextView().text = provideResources().getString(R.string.text_button_start)
            }
        }
        providePrimaryButtonTextView().setOnClickListener {
            resetToDefaultSelection()
            if (provideStepperView().currentStep >= provideStepperView().noOfStepCount) {
                if(provideCookingViewModel().recipeExecutionViewModel.isRunning){
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    CookingAppUtils.navigateToStatusOrClockScreen(provideFragment())
                }
                else {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.start_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    // navigate to start assisted recipe
                    startAssistedRecipe()
                }
                return@setOnClickListener
            }
            provideStepperView().setStepperCurrentStep(provideStepperView().currentStep + 1)

            when (CookingAppUtils.cookingGuideList.first()) {
                 AppConstants.TEXT_COOK_GUIDE -> {
                     provideHeaderView()?.apply {
                         setTitleText(R.string.text_header_cooking_guide)
                     }
                     setRecipeImage(
                         provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: "",
                         AppConstants.TEXT_COOK_GUIDE
                     )
                     provideCookingGuideDescriptionTextView().text = provideResources().getString(
                         CookingAppUtils.getPopupDataForCookGuide(
                             provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: ""
                         )
                     )
                     CookingAppUtils.cookingGuideList.removeFirst()
                 }

                AppConstants.TEXT_ACCESSORY_GUIDE -> {
                    provideHeaderView()?.apply {
                        setTitleText(R.string.text_header_accessory_guide)
                    }
                    setRecipeImage(
                        provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: "",
                        AppConstants.TEXT_ACCESSORY_GUIDE
                    )
                    provideCookingGuideDescriptionTextView().text = provideResources().getString(
                        CookingAppUtils.getPopupDataForAccessoryGuide(
                            provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: ""
                        )
                    )
                    CookingAppUtils.cookingGuideList.removeFirst()
                }
            }
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            provideDescriptionTextScrollView().fullScroll(ScrollView.FOCUS_UP)
            if(provideStepperView().currentStep >= provideStepperView().noOfStepCount){
                val delayTime = provideFragment().arguments?.getLong(BundleKeys.BUNDLE_NAVIGATED_ASSISTED_DELAY_COOKING_GUIDE)
                if (provideCookingViewModel().recipeExecutionViewModel.isRunning){
                    providePrimaryButtonTextView().text = provideResources().getString(R.string.text_button_ok)
                }else{
                    if (delayTime != null && delayTime > 0)
                        providePrimaryButtonTextView().text = provideResources().getString(R.string.text_button_start_delay)
                    else
                        providePrimaryButtonTextView().text = provideResources().getString(R.string.text_button_start)
                }
            }
        }
        providePrimaryButtonConstraint().setOnClickListener {
            resetToDefaultSelection()
            if (provideStepperView().currentStep >= provideStepperView().noOfStepCount) {
                if(provideCookingViewModel().recipeExecutionViewModel.isRunning){
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    CookingAppUtils.navigateToStatusOrClockScreen(provideFragment())
                }
                else {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.start_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    // navigate to start assisted recipe
                    startAssistedRecipe()
                }
                return@setOnClickListener
            }
            provideStepperView().setStepperCurrentStep(provideStepperView().currentStep + 1)

            when (CookingAppUtils.cookingGuideList.first()) {
                AppConstants.TEXT_COOK_GUIDE -> {
                    provideHeaderView()?.apply {
                        setTitleText(R.string.text_header_cooking_guide)
                    }
                    setRecipeImage(
                        provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: "",
                        AppConstants.TEXT_COOK_GUIDE
                    )
                    provideCookingGuideDescriptionTextView().text = provideResources().getString(
                        CookingAppUtils.getPopupDataForCookGuide(
                            provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: ""
                        )
                    )
                    CookingAppUtils.cookingGuideList.removeFirst()
                }

                AppConstants.TEXT_ACCESSORY_GUIDE -> {
                    provideHeaderView()?.apply {
                        setTitleText(R.string.text_header_accessory_guide)
                    }
                    setRecipeImage(
                        provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: "",
                        AppConstants.TEXT_ACCESSORY_GUIDE
                    )
                    provideCookingGuideDescriptionTextView().text = provideResources().getString(
                        CookingAppUtils.getPopupDataForAccessoryGuide(
                            provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: ""
                        )
                    )
                    CookingAppUtils.cookingGuideList.removeFirst()
                }
            }
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            provideDescriptionTextScrollView().fullScroll(ScrollView.FOCUS_UP)
            if(provideStepperView().currentStep >= provideStepperView().noOfStepCount){
                val delayTime = provideFragment().arguments?.getLong(BundleKeys.BUNDLE_NAVIGATED_ASSISTED_DELAY_COOKING_GUIDE)
                if (provideCookingViewModel().recipeExecutionViewModel.isRunning){
                    providePrimaryButtonTextView().text = provideResources().getString(R.string.text_button_ok)
                }else{
                    if (delayTime != null && delayTime > 0)
                        providePrimaryButtonTextView().text = provideResources().getString(R.string.text_button_start_delay)
                    else
                        providePrimaryButtonTextView().text = provideResources().getString(R.string.text_button_start)
                }
            }
        }
    }

    protected open fun startAssistedRecipe() {
        if(provideCookingViewModel().isOfTypeMicrowaveOven){
            DoorEventUtils.startMicrowaveRecipeOrShowPopup(provideFragment(), provideCookingViewModel())
            return
        }
        if(provideCookingViewModel().recipeExecutionViewModel.isProbeBasedRecipe && !MeatProbeUtils.isMeatProbeConnected(provideCookingViewModel())){
            PopUpBuilderUtils.insertMeatProbe(provideFragment(), provideCookingViewModel(), onMeatProbeConditionMet = { startAssistedRecipe() })
            return
        }
        val recipeExecuteErrorResponse =
            provideCookingViewModel().recipeExecutionViewModel.execute()
        HMILogHelper.Logd(tag, "start recipe from assistedPreview ${recipeExecuteErrorResponse.description}")
        if (recipeExecuteErrorResponse.isError) {
            CookingAppUtils.handleCookingError(
                provideFragment(),
                provideCookingViewModel(),
                recipeExecuteErrorResponse,
                false
            )
            return
        }else{
            HMIExpansionUtils.startOrStopKnobLEDFadeInLightAnimation(true)
        }
        CookingAppUtils.navigateToStatusOrClockScreen(provideFragment())
    }

    abstract fun provideFragment(): Fragment


    /**
     * Required - provideCookingGuideDescriptionTextView is used to show the Description text on the screen
     *
     * @return [TextView]
     */
    abstract fun provideCookingGuideDescriptionTextView(): TextView

    /**
     * Required - provideStepperView is used to show the dots indicating how many pages are there
     *
     * @return [TextView]
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
     * Required - provideCookingViewModel is used to get the recipe information
     *
     * @return [TextView]
     */
    abstract fun providePrimaryButtonConstraint(): ConstraintLayout

    /**
     * Required - provideCookingViewModel is used to get the recipe information
     *
     * @return [ConstraintLayout]
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
     * Set Recipe Image
     * @param recipeName: - selected recipe name
     * */
    abstract fun setRecipeImage(recipeName: String, currentStep: String)

    /**
     * Method to get Utensil guide Popup Message
     *
     * @param recipeName current programming recipe
     */
    @Suppress("unused")
    private fun getPopupDataForUtensilGuide(recipeName: String): Int {
        val commonMessagePosition: Int = CookingAppUtils.getTextPosition(
            provideView(),
            R.array.assisted_utensil_guide_common_messages,
            recipeName
        )
        return if (commonMessagePosition > 0) {
            CookingAppUtils.getResIdFromResName(
                provideContext(),
                AppConstants.TEXT_COMMON_MESSAGE_UTENSIL_GUIDE + commonMessagePosition,
                AppConstants.RESOURCE_TYPE_STRING
            )
        } else {
            CookingAppUtils.getResIdFromResName(
                provideContext(),
                recipeName + AppConstants.TEXT_UTENSIL_GUIDE,
                AppConstants.RESOURCE_TYPE_STRING
            )
        }
    }

    /******************* KNOB Interaction methods ********************/

    override fun onHMILeftKnobClick() {
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
        HMILogHelper.Logd("Knob", "onHMILeftKnobClick() called  : $selectedRotator")
        when (selectedRotator) {
            0 -> {
                if (providePrimaryButtonTextView().isEnabled) {
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
                    if (provideStepperView().currentStep >= provideStepperView().noOfStepCount)
                        KnobNavigationUtils.knobForwardTrace = true
                    else
                        resetToDefaultSelection(true)
                    providePrimaryButtonTextView().performClick()
                }
            }
        }
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            HMILogHelper.Logd("AbstractCookingGuideViewHolder onKnobRotateEvent $isInstructionWidgetSelected $selectedRotator")
            if (isInstructionWidgetSelected) {
                selectedRotator = 0// Use the custom coroutine scope to launch the coroutine
                coroutineScope.launch {
                    val scrollView = provideDescriptionTextScrollView()
                    val scrollViewContent = provideCookingGuideDescriptionTextView()
                    val lineHeight = scrollViewContent.lineHeight
                    handleTextScrollOnKnobRotateEvent(scrollView, lineHeight, knobDirection)
                }
            } else {
                selectedRotator = 1
                providePrimaryButtonTextView().background = provideResources().let {
                    ResourcesCompat.getDrawable(
                        it, R.drawable.selector_textview_walnut, null
                    )
                }
            }
        }
    }

    private fun resetToDefaultSelection(isFromKnobClick: Boolean = false) {
        val backgroundResource = when {
            provideDescriptionTextScrollView().canScrollVertically(1) -> {
                isInstructionWidgetSelected = true
                selectedRotator = 0
                R.drawable.text_view_ripple_effect
            }
            isFromKnobClick -> {
                isInstructionWidgetSelected = false
                selectedRotator = 1
                R.drawable.selector_textview_walnut
            }
            else -> {
                isInstructionWidgetSelected = false
                selectedRotator = 1
                R.drawable.text_view_ripple_effect
            }
        }

        // Set the background for the primary button
        providePrimaryButtonTextView().background = ResourcesCompat.getDrawable(
            provideResources(), backgroundResource, null
        )
    }


    fun removeObserver() {
        provideDescriptionTextScrollView().viewTreeObserver.removeOnPreDrawListener(this)
        provideDescriptionTextScrollView().viewTreeObserver.removeOnScrollChangedListener(this)
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            isInstructionWidgetSelected = provideDescriptionTextScrollView().canScrollVertically(1)
            selectedRotator = 0
            providePrimaryButtonTextView().background = provideResources().let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.text_view_ripple_effect, null
                )
            }
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}