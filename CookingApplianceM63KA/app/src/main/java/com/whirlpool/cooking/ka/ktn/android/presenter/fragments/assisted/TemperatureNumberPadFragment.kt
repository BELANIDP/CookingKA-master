/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.assisted

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import core.jbase.AbstractTemperatureNumberPadFragment
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.NavigationUtils

/**
 * File        : android.presenter.fragments.doubleoven.TemperatureNumberPadFragment.
 * Brief       : Temperature number-pad screen for double oven variant
 * Author      : GHARDNS/Nikki
 * Created On  : 18-03-2024
 */
class TemperatureNumberPadFragment : AbstractTemperatureNumberPadFragment(),
    AbstractTemperatureNumberPadFragment.ButtonClickListenerInterface {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonInteractionListener(this)
    }
    /**
     * @return TargetTemperatureOptionRange of primary cooking view model.
     */
    override fun getTemperatureRange(): LiveData<IntegerRange> {
        @Suppress("UNCHECKED_CAST")
        return cookingViewModel.recipeExecutionViewModel.targetTemperatureOptions as LiveData<IntegerRange>
    }
    override fun provideCurrentTargetTemperature(): Int? {
        return cookingViewModel.recipeExecutionViewModel?.targetTemperature?.value
    }
    override fun onRightButtonClick() {
        //Bottom right button click event
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }

    override fun onLeftButtonClick() {
        //Bottom left button click event
    }

    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }

    override fun manageLeftButton() {
        temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
    }

    override fun manageRightButton() {
        super.manageRightButton()
        val rightButtonText = when (arguments?.getString(BundleKeys.BUNDLE_IS_FROM_PREVIEW_SCREEN)) {
            BundleKeys.BUNDLE_VALUE_POP_TO_PREVIEW -> resources.getString(R.string.text_button_update)
            else -> resources.getString(R.string.text_button_next)
        }
        temperatureNumberPadViewHolderHelper?.getRightTextButton()
            ?.setTextButtonText(rightButtonText)
    }

    override fun switchToTumblerScreen() {
        val bundle = if (arguments == null) Bundle() else arguments
        bundle?.putInt(
            BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE,
            (numericValue as String).toInt()
        )
        NavigationUtils.navigateSafely(
            this,
            R.id.action_assistedTemperatureNumPad_to_assistedTemperatureTumblerFragment,
            bundle,
            null
        )
    }
    override fun onHMIRightKnobClick() {
        onClick(temperatureNumberPadViewHolderHelper?.getRightTextButton() as View)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}
