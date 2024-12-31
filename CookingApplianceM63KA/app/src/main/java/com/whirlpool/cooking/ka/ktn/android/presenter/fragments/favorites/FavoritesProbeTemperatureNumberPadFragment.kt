/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.favorites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.navigation.NavOptions
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import core.jbase.AbstractTemperatureNumberPadFragment
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.NavigationUtils

/**
 * File        : android.presenter.fragments.favorites.FavoritesProbeTemperatureNumberPadFragment.
 * Brief       : Temperature number-pad screen for probe
 * Author      : VYASM
 * Created On  : 23/10/2024
 */
class FavoritesProbeTemperatureNumberPadFragment : AbstractTemperatureNumberPadFragment(),
    AbstractTemperatureNumberPadFragment.ButtonClickListenerInterface {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonInteractionListener(this)
    }

    /**
     * @return TargetTemperatureOptionRange of primary cooking view model.
     */
    override fun getTemperatureRange(): LiveData<IntegerRange> {
        return cookingViewModel.recipeExecutionViewModel.meatProbeTargetTemperatureOption
    }
    override fun provideCurrentTargetTemperature(): Int? {
        return cookingViewModel.recipeExecutionViewModel?.meatProbeTargetTemperature?.value
    }

    override fun switchToTumblerScreen() {
        val bundle = Bundle()
        bundle.putInt(
            BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE,
            (numericValue as String).toInt()
        )
        NavigationUtils.navigateSafely(
            this,
            R.id.action_to_favoritesProbeTemperatureTumbler,
            bundle,
            NavOptions.Builder().setPopUpTo(R.id.favoritesProbeTemperatureNumberPadFragment,true).build()
        )
    }

    override fun manageLeftButton() {
        temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
        temperatureNumberPadViewHolderHelper?.getLeftConstraint()?.visibility = View.GONE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cook_time_text_button_right -> {
                NavigationUtils.navigateAndSetProbeTemperature(
                    this,
                    cookingViewModel,
                    numericValue.toString().toFloat()
                )
            }
            R.id.constraintNumberPadRight -> {
                NavigationUtils.navigateAndSetProbeTemperature(
                    this,
                    cookingViewModel,
                    numericValue.toString().toFloat()
                )
            }
        }
    }

    override fun onHMILeftKnobClick() {

    }

    override fun onHMILongLeftKnobPress() {

    }

    override fun onHMIRightKnobClick() {
        onClick(temperatureNumberPadViewHolderHelper?.getRightTextButton() as View)
    }

    override fun onHMILongRightKnobPress() {

    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {

    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
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
}
