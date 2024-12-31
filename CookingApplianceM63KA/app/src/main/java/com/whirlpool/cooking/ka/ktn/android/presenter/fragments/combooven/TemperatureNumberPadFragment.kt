/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.combooven

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import core.jbase.AbstractTemperatureNumberPadFragment
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
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

    override fun manageLeftButton() {
        if (cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.visibility =
            View.GONE
        else super.manageLeftButton()
    }

    override fun provideCurrentTargetTemperature(): Int? {
        return cookingViewModel.recipeExecutionViewModel?.targetTemperature?.value
    }

    override fun onHMIRightKnobClick() {
        onClick(temperatureNumberPadViewHolderHelper?.getRightTextButton() as View)
    }

    override fun onRightButtonClick() {
        //Bottom right button click event
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }

    override fun onLeftButtonClick() {
        val recipeErrorResponse =
            cookingViewModel.recipeExecutionViewModel?.setTargetTemperature(numericValue.toString().toFloat())
        HMILogHelper.Logd(
            tag,
            "delay clicked with temperature $numericValue recipeErrorResponse ${recipeErrorResponse?.description}"
        )
        if (recipeErrorResponse?.isError == false) NavigationUtils.navigateToDelayScreen(this)
    }

    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }
}
