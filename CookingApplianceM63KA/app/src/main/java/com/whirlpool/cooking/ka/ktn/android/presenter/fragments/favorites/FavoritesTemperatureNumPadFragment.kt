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
import core.utils.HMILogHelper
import core.utils.NavigationUtils

/**
 * File        : android.presenter.fragments.favorites.FavoritesTemperatureNumPadFragment.
 * Brief       : Temperature number-pad screen for double oven variant
 * Author      : VYASM
 * Created On  : 23/10/2024
 */
class FavoritesTemperatureNumPadFragment : AbstractTemperatureNumberPadFragment(),
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
        temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
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
        //do nothing, delay button is not applicable for favorites temperature selection.
    }

    override fun switchToTumblerScreen() {
        val bundle = if (arguments == null) Bundle() else arguments
        bundle?.putInt(
            BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE,
            (numericValue as String).toInt()
        )
        NavigationUtils.navigateSafely(
            this,
            R.id.action_to_favoritesTemperatureTumblerFragment,
            bundle,
            NavOptions.Builder().setPopUpTo(R.id.favoritesTemperatureNumPadFragment,true).build()
        )
    }
    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }
}
