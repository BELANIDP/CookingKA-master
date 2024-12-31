/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.sabbath

import android.os.Bundle
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import core.jbase.AbstractCookTimeNumberPadFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.SabbathUtils

/**
 * File        : android.presenter.fragments.assisted.CookTimeNumberPadFragment.
 * Brief       : Instance of clock time numberPad for combo oven
 * Author      : Hiren <br>
 * Created On  : 18-03-2024 <br>
 */
class SabbathCookTimeNumberPadFragment : AbstractCookTimeNumberPadFragment(),
    AbstractCookTimeNumberPadFragment.ButtonClickListenerInterface {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
        setButtonInteractionListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cook_time_text_button_left -> {
               SabbathUtils.handleSabbathCookTimeRightTextButtonClick(cookingViewModel, this,0)
            }
            else -> {
                super.onClick(view)
            }
        }
    }

    /**
     * initialize UI components
     */
    private fun initButton() {
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.visibility = View.VISIBLE
    }

    override fun updateRightButtonText(): String {
        return getString(R.string.text_button_set_timed)
    }

    override fun onRightButtonClick() {
        if (validateCookTime()) SabbathUtils.handleSabbathCookTimeRightTextButtonClick(
            cookingViewModel, this, CookingAppUtils.getCookTimerStringAsSeconds(
                cookTimeText, !isOfTypeMicrowaveOven || !isMagnetronBasedRecipe()
            )
        )
    }

    override fun manageLeftButton() {
        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.setOnClickListener(this)
        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.VISIBLE
        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.setTextButtonText(getString(R.string.text_button_set_untimed))
        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled = true
    }

    override fun enableTextButtons() {
        if (cookTimeText != null && cookTimeText != AppConstants.DEFAULT_COOK_TIME) {
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = true
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = true
        }
        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled = true
        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isClickable = true
    }

    override fun observePreheatingLiveData() {
        //do nothing
    }

    override fun observeCookTimerLiveData() {
        //do nothing
    }
    override fun disableTextButtons() {
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = false
        cookTimeNumberPadViewHolderHelper?.getMiddleTextButton()?.isEnabled = false
        cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isEnabled = false

        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = false
        cookTimeNumberPadViewHolderHelper?.getMiddleTextButton()?.isClickable = false
        cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isClickable = false
    }

    override fun onLeftButtonClick() {
        //Bottom left button click event
        HMILogHelper.Logd(tag, "Sabbath left untimed button clicked")
    }

    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }

    override fun onLeftPowerButtonClick() {
        //do nothing
    }
    override fun provideIntegerRange(): IntegerRange {
        return (recipeViewModel?.cookTimeOption?.value as IntegerRange?)!!
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        SabbathUtils.probeDetectedBeforeSabbathProgramming(this, cookingViewModel, {
            SabbathUtils.navigateToSabbathSettingSelection(this)
        }, {})
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if(knobId == AppConstants.LEFT_KNOB_ID) manageKnobRotation()
    }
    override fun onResume() {
        super.onResume()
        HMILogHelper.Logd("HMI_KEY","Sabbath Cook time numpad Tumbler \n----------------")
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
    }
}
