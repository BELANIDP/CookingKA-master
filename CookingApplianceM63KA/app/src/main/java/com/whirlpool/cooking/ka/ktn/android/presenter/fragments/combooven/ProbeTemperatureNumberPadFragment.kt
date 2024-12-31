/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.combooven

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.utils.ContextProvider
import core.jbase.AbstractTemperatureNumberPadFragment
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.NavigationUtils

/**
 * File        : android.presenter.fragments.combooven.ProbeTemperatureNumberPadFragment.
 * Brief       : Temperature number-pad screen for probe
 * Author      : Hiren
 * Created On  : 05/29/2024
 */
class ProbeTemperatureNumberPadFragment : AbstractTemperatureNumberPadFragment(),
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
            R.id.action_probeTemperatureNumPadFragment_to_probeTumbler,
            bundle,
            null
        )
    }

    override fun manageLeftButton() {
        temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cook_time_text_button_right -> {
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                NavigationUtils.navigateAndSetProbeTemperature(
                    this,
                    cookingViewModel,
                    numericValue.toString().toFloat()
                )
            }
            R.id.constraintNumberPadRight -> {
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                NavigationUtils.navigateAndSetProbeTemperature(
                    this,
                    cookingViewModel,
                    numericValue.toString().toFloat()
                )
            }
        }
    }

    override fun onHMIRightKnobClick() {
        onClick(temperatureNumberPadViewHolderHelper?.getRightTextButton() as View)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onRightButtonClick() {
        AudioManagerUtils.playOneShotSound(
            ContextProvider.getContext(),
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        //Bottom right button click event
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }

    override fun onLeftButtonClick() {
        //Bottom left button click event
        AudioManagerUtils.playOneShotSound(
            ContextProvider.getContext(),
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
    }

    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }
}
