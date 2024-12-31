/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.kitchentimer

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.AbstractKitchenTimerNumberPadFragment
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils

/**
 * File       : android.presenter.fragments.kitchentimer.KitchenTimerNumpadFragment
 * Brief      : Manually select kitchen timer
 * Author     : PANDES18
 * Created On : 6/28/2024
 * Details    : Details about the file
 */
class KitchenTimerNumpadFragment : AbstractKitchenTimerNumberPadFragment(),
    AbstractKitchenTimerNumberPadFragment.ButtonClickListenerInterface {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
        setButtonInteractionListener(this)
        kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setTumblerIconOnClickListener {
                navigateToVerticalTimeTumblerScreen(isKitchenTimerModify)
            }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun manageRightButton() {
        isKitchenTimerModify =
            arguments?.getString(BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER) != null && arguments?.getString(
                BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER
            )?.isEmpty() == false
        super.manageRightButton()
    }

    override fun updateRightButtonText(): String {
        return if (isKitchenTimerModify) getString(R.string.text_button_update) else getString(R.string.text_button_start)
    }

    override fun provideIntegerRange(): IntegerRange {
        // min 1 minute and max is 24 hours
        val maxTime = 86400.0
        val minTime = 60.0
        val integerRange = IntegerRange()
        integerRange.setMax(maxTime)
        integerRange.setMin(minTime)
        return integerRange
    }

    private fun initButton() {
        kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.visibility = View.VISIBLE
        kitchenTimerNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
        kitchenTimerNumberPadViewHolderHelper?.getLeftPowerTextButton()?.visibility = View.GONE
    }

    override fun onRightButtonClick() {
        if (validateCookTime()) {
            val cookTimerStringAsSeconds =
                CookingAppUtils.getCookTimerStringAsSeconds(cookTimeText, true)
        if (isKitchenTimerModify) {
            val modifyKitchenTimerName =
                arguments?.getString(BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER)
            HMILogHelper.Logd(tag, "kitchenTimer modify name $modifyKitchenTimerName")
            KitchenTimerUtils.modifyKitchenTimer(
                modifyKitchenTimerName,
                cookTimerStringAsSeconds,
                onSuccessKTAdded = {
                    NavigationUtils.navigateSafely(
                        this, R.id.action_setManualKTFragment_to_kitchenTimerFragment, null, null
                    )
                })
            return
        }
            CookingAppUtils.stopGattServer()
            KitchenTimerUtils.addKitchenTimer(this, cookTimerStringAsSeconds, onSuccessKTAdded = {
                NavigationUtils.navigateSafely(
                this, R.id.action_setManualKTFragment_to_kitchenTimerFragment, null, null
                )
            })
        }
    }

    override fun onLeftButtonClick() {
        NavigationViewModel.popBackStack(findNavController())
    }

    override fun onMiddleButtonClick() {
    }

    override fun onLeftPowerButtonClick() {
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(CookingViewModelFactory.getInScopeViewModel() == null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, cookingViewModel)
            return
        }
        if (cookingViewModel != null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            launchProbeDetectedPopupAsPerVariant(cookingViewModel)
        }
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            KnobNavigationUtils.knobForwardTrace = true
            navigateToVerticalTimeTumblerScreen(isKitchenTimerModify)
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            //Meat probe removed dialog shown here and updating the screen time out. Once probe inserted then reset the screen timeout as expected.
            updateTimeoutValue(TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_STARTED,resources.getInteger(R.integer.session_short_timeout))
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel, onMeatProbeInsertionCallback = {
                updateTimeoutValue(TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_STARTED,provideScreenTimeoutValueInSeconds())
            })
        }
    }
}