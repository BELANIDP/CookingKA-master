/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.sabbath

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.jbase.AbstractTemperatureNumberPadFragment
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.SabbathUtils

/**
 * File        : android.presenter.fragments.sabbath.SabbathTemperatureNumberPadFragment
 * Brief       : Temperature number-pad screen For Sabbath Related recipe
 * Author      : Hiren
 * Created On  : 08/27/2024
 */
class SabbathTemperatureNumberPadFragment : AbstractTemperatureNumberPadFragment(),
    AbstractTemperatureNumberPadFragment.ButtonClickListenerInterface {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonInteractionListener(this)
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        SabbathUtils.probeDetectedBeforeSabbathProgramming(this, cookingViewModel, {
            SabbathUtils.navigateToSabbathSettingSelection(this)
        }, {})
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

    override fun provideRightButtonText(): String {
        return getString(R.string.text_button_next)
    }

    override fun provideCurrentTargetTemperature(): Int? {
        return cookingViewModel.recipeExecutionViewModel?.targetTemperature?.value
    }

    override fun onHMIRightKnobClick() {
        onClick(temperatureNumberPadViewHolderHelper?.getRightTextButton() as View)
    }

    override fun onRightButtonClick() {
        //Bottom right button click event
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cook_time_text_button_right -> {
                SabbathUtils.sabbathSetTemperature(
                    this,
                    CookingViewModelFactory.getInScopeViewModel(),
                    numericValue.toString().toFloat(),
                    false
                )
            }
        }
    }

    override fun onLeftButtonClick() {
       //do nothing
    }

    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }

    override fun switchToTumblerScreen() {
        val bundle = Bundle()
        bundle.putInt(
            BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE,
            (numericValue as String).toInt()
        )
        NavigationUtils.navigateSafely(
            this,
            R.id.action_sabbathTemperatureNumPadFragment_to_sabbathTemperatureTumblerFragment,
            bundle,
            null
        )
    }

    override fun onResume() {
        super.onResume()
        HMILogHelper.Logd("HMI_KEY","Sabbath Temperature Numpad \n----------------")
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
    }
}
