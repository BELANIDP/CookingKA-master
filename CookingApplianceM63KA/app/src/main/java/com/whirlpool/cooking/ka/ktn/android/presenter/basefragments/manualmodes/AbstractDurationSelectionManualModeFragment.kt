/***Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL***/
package android.presenter.basefragments.manualmodes

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.adapters.manualMode.ManualModeStringTumblerAdapterItem
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.TemperatureMap
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.abstractViewHolders.DurationSelectionManualModeViewHolderHelper
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CavityStateUtils.getProgrammingStateTimeoutValue
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.rotateTumblerOnKnobEvents
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateToShowInstructionFragment
import core.utils.setListObjectWithDefaultSelection


/*
 * File : com.whirlpool.cooking.ka.ktn.android.presenter.basefragments.manualmodes.AbstractDurationSelectionManualModeFragment
 * Author : SINGHA80.
 * Created On : 3/22/24
 * Details : Provides base methods for AbstractDurationSelectionManualModeFragment
 */


abstract class AbstractDurationSelectionManualModeFragment : SuperAbstractTimeoutEnableFragment(),
    View.OnClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener {

    protected val viewHolderHelper: DurationSelectionManualModeViewHolderHelper? by lazy { DurationSelectionManualModeViewHolderHelper() }
    val inScopeViewModel: CookingViewModel? by lazy { CookingViewModelFactory.getInScopeViewModel() }
    val productVariantEnum: CookingViewModelFactory.ProductVariantEnum? by lazy { CookingViewModelFactory.getProductVariantEnum() }
    private var rotator = listOf(0, 1) // Tumbler, CTA's,
    private var selectedRotator = rotator[0]
    private var isTumblerSelected = true
    private var isGhostButtonVisible: Boolean = false
    private var isPrimaryCTAButtonVisible: Boolean = false
    private var knobRotationCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        viewHolderHelper?.onCreateView(inflater, container, savedInstanceState)
        setHmiKnobListener()
        setViewsByProductVariant()
        setTumblerData()
        return viewHolderHelper?.fragmentStringTumblerBinding()?.root
    }

    /**
     * sets setHmiKnobListener
     */
    private fun setHmiKnobListener() {
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
    }

    /**
     * sets the layout view with the data
     */
    open fun setViewsByProductVariant() {
        viewHolderHelper?.providePrimaryButton()?.setOnClickListener(this)
        viewHolderHelper?.providePrimaryButtonConstraint()?.setOnClickListener(this)
        viewHolderHelper?.provideHeaderBarWidget()?.setCustomOnClickListener(this)
        val checkIfInstructionAvailable = CookingAppUtils.checkIfInstructionAvailable(this,CookingViewModelFactory.getInScopeViewModel())
        viewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(checkIfInstructionAvailable)
        viewHolderHelper?.provideHeaderBarWidget()?.setRightIconVisibility(false)
        viewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(false)
        CookingAppUtils.setHeaderTitleAsRecipeName(
            viewHolderHelper?.provideHeaderBarWidget(),
            inScopeViewModel
        )
        updateCtaRightButton()
        updateCtaLeftButton()
        when (productVariantEnum) {
            CookingViewModelFactory.ProductVariantEnum.COMBO,
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                viewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(false)
                if (inScopeViewModel?.isPrimaryCavity == true) {
                    viewHolderHelper?.provideHeaderBarWidget()
                        ?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
                } else {
                    viewHolderHelper?.provideHeaderBarWidget()
                        ?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
                }
            }

            else -> {
                viewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(false)
            }
        }
        enableHMIKeys()
    }

    /**
     * manage visibility of left button, to check if any particular loaded recipe provides delay option or not
     *
     */
    open fun updateCtaLeftButton() {
        if ((inScopeViewModel?.recipeExecutionViewModel?.let {
                CookingAppUtils.isRecipeOptionAvailable(
                    it, RecipeOptions.DELAY_TIME) } == true) && (inScopeViewModel?.recipeExecutionViewModel?.
                recipeExecutionState?.value == RecipeExecutionState.DELAYED ||
                        inScopeViewModel?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.IDLE)) {
            viewHolderHelper?.provideGhostButton()?.visibility = View.VISIBLE
            viewHolderHelper?.provideGhostButton()?.setOnClickListener(this)
            viewHolderHelper?.provideGhostButtonConstraint()?.setOnClickListener(this)
            viewHolderHelper?.provideGhostButton()?.text = getString(R.string.text_button_delay)
        } else {
            viewHolderHelper?.provideGhostButton()?.visibility = View.GONE
            viewHolderHelper?.provideGhostButtonConstraint()?.visibility = View.GONE
        }
    }


    /**
     * setTumblerData for the loaded recipe
     */
    private fun setTumblerData() {
        val tempMap = inScopeViewModel?.recipeExecutionViewModel?.targetTemperatureOptions?.value
        val targetTemp = inScopeViewModel?.recipeExecutionViewModel?.targetTemperature?.value
        if (tempMap != null) {
            if (tempMap is TemperatureMap) {
                var defaultItem = tempMap.defaultString
               val temperatureMap = tempMap.temperatureMap
                for ((key, value) in temperatureMap) {
                    HMILogHelper.Loge("temperature data $key $value ")
                    if (targetTemp != 0 && targetTemp == value) {
                        defaultItem = key
                        break
                    }
                }

                viewHolderHelper?.provideDurationTumbler()?.itemViewHolder =
                    ManualModeStringTumblerAdapterItem(tempMap, CookingAppUtils.isTimeBasedPreheatRecipe(inScopeViewModel))
                viewHolderHelper?.provideDurationTumbler()
                    ?.setListObjectWithDefaultSelection(tempMap, defaultItem)
            }

        } else {
            HMILogHelper.Loge("temperature data is null ")

        }
    }

    override fun onClick(view: View?) {
        val id = view?.id
        if (id == viewHolderHelper?.providePrimaryButton()?.id || id == viewHolderHelper?.providePrimaryButtonConstraint()?.id) {
            AudioManagerUtils.playOneShotSound(
                view?.context,
                R.raw.start_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            NavigationUtils.navigateAndSetTemperature(
                this, inScopeViewModel, getSelectedTemperatureValue()
            )
        } else if (id == viewHolderHelper?.provideGhostButton()?.id || id == viewHolderHelper?.provideGhostButtonConstraint()?.id)  {
            val recipeErrorResponse =
                inScopeViewModel?.recipeExecutionViewModel?.setTargetTemperature(
                    getSelectedTemperatureValue()
                )

            HMILogHelper.Logd(
                tag,
                "delay clicked with duration temperature recipeErrorResponse ${recipeErrorResponse?.description}"
            )
            if (recipeErrorResponse?.isError == false) {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                NavigationUtils.navigateToDelayScreen(this)
            }
        }
    }

    private fun getSelectedTemperatureValue(): Float {
        val selectedDegreeValue =
            viewHolderHelper?.provideDurationTumbler()?.listObject?.getValue(viewHolderHelper?.provideDurationTumbler()?.selectedIndex!!) as Int
        val selectedValue = viewHolderHelper?.provideDurationTumbler()?.selectedValue
        HMILogHelper.Logd(tag, "duration selectedDegreeValue $selectedDegreeValue selectedValue $selectedValue")
        return selectedDegreeValue.toFloat()
    }

    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(
                    this
                ) ?: requireView()
            )
        )
    }

    override fun infoIconOnClick() {
        super.infoIconOnClick()
        navigateToShowInstructionFragment(activity)
    }

    /**
     * update button state as per
     */
    private fun updateCtaRightButton() {
        viewHolderHelper?.providePrimaryButton()?.text =
            NavigationUtils.getRightButtonTextForRecipeOption(
                context,
                inScopeViewModel,
                provideRecipeOption()
            )
    }

    /**
     * Override this method if child class is not meant for TARGET_TEMPERATURE
     *
     * @return recipe option
     */
    open fun provideRecipeOption(): RecipeOptions {
        return RecipeOptions.TARGET_TEMPERATURE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }

    /**
     * Method to manage knob rotation
     * @param knobId: Int
     * @param knobDirection: String?
     * */
    protected fun manageKnobRotation(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            if (isTumblerSelected) {
                // Handle tumbler selection and knob events
                selectedRotator = 0
                val tumbler =  viewHolderHelper?.provideDurationTumbler() ?: return
                rotateTumblerOnKnobEvents(this, tumbler, knobDirection)
            } else {
                when {
                    isPrimaryCTAButtonVisible && isGhostButtonVisible -> {
                        adjustKnobRotation(knobDirection)
                        updateButtonBackgrounds(knobRotationCount)
                    }

                    isPrimaryCTAButtonVisible -> updateButtonBackgroundForSingleButton(
                        R.drawable.selector_textview_walnut,
                        isPrimaryButton = true
                    )

                    isGhostButtonVisible -> updateButtonBackgroundForSingleButton(
                        R.drawable.selector_textview_walnut,
                        isPrimaryButton = false
                    )
                }
            }
        }
    }

    /**
     * Adjust the knob rotation count based on the knob direction.
     */
    private fun adjustKnobRotation(knobDirection: String) {
        when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION -> {
                if (knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
            }
            KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                if (knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            }
        }
    }

    /**
     * Update both buttons' backgrounds based on knobRotationCount.
     */
    private fun updateButtonBackgrounds(knobRotationCount: Int) {
        val (primaryButtonRes, ghostButtonRes) = when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> Pair(R.drawable.selector_textview_walnut, R.drawable.text_view_ripple_effect)
            AppConstants.KNOB_COUNTER_TWO -> Pair(R.drawable.text_view_ripple_effect, R.drawable.selector_textview_walnut)
            else -> Pair(R.drawable.selector_textview_walnut, R.drawable.selector_textview_walnut)
        }
        setButtonBackgrounds(ghostButtonRes, primaryButtonRes)
    }

    /**
     * Helper function to set the background for a single button (either primary or ghost).
     */
    private fun updateButtonBackgroundForSingleButton(drawableRes: Int, isPrimaryButton: Boolean) {
        val targetButton = if (isPrimaryButton) {
            viewHolderHelper?.providePrimaryButton()
        } else {
            viewHolderHelper?.provideGhostButton()
        }

        targetButton?.background = ResourcesCompat.getDrawable(resources, drawableRes, null)
    }

    /**
     * Helper function to set the backgrounds for both buttons.
     */
    private fun setButtonBackgrounds(ghostButtonRes: Int, primaryButtonRes: Int) {
        val resources = resources
        viewHolderHelper?.provideGhostButton()?.background =
            ResourcesCompat.getDrawable(resources, ghostButtonRes, null)
        viewHolderHelper?.providePrimaryButton()?.background =
            ResourcesCompat.getDrawable(resources, primaryButtonRes, null)
    }

    /**
     * Reset the backgrounds for both primary and ghost buttons to the ripple effect.
     */
    private fun resetButtonBackgrounds() {
        updateButtonBackgroundForSingleButton(R.drawable.text_view_ripple_effect, isPrimaryButton = true)
        updateButtonBackgroundForSingleButton(R.drawable.text_view_ripple_effect, isPrimaryButton = false)
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILeftKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        resetButtonBackgrounds()
    }

    override fun onHMIRightKnobClick() {
        onHMIKnobRightOrLeftClick()
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        manageKnobRotation(knobId, knobDirection)
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return getProgrammingStateTimeoutValue(resources)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    /**
     * Handle knob clicks and update the state of selectedRotator and knobRotationCount.
     */
    fun onHMIKnobRightOrLeftClick() {
        HMILogHelper.Logd("Knob", "onHMIKnobRightOrLeftClick() called  : $selectedRotator")
        when (selectedRotator) {
            0 -> {
                isTumblerSelected = false
                selectedRotator = 1
                knobRotationCount = 1
                updateButtonBackgroundForSingleButton(
                    R.drawable.selector_textview_walnut,
                    isPrimaryButton = true
                )
            }

            1 -> {
                KnobNavigationUtils.knobForwardTrace = true
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        if (viewHolderHelper?.providePrimaryButton()?.isEnabled == true)
                            onClick(viewHolderHelper?.providePrimaryButton())
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        if (viewHolderHelper?.provideGhostButton()?.isEnabled == true)
                            onClick(viewHolderHelper?.provideGhostButton())
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        enableHMIKeys()
    }
    private fun enableHMIKeys(){
        HMILogHelper.Logd("HMI_KEY","Is Self Clean For on Duration Selection ${CookingAppUtils.isSelfCleanFlow()}")
        if (CookingAppUtils.isSelfCleanFlow()) {
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SELF_CLEAN)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SELF_CLEAN)
        } else {
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        }
    }
}
