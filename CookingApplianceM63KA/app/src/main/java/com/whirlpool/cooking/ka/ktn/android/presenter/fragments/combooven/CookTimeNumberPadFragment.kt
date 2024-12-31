/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.combooven

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.utils.timers.Timer
import core.jbase.AbstractCookTimeNumberPadFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.DoorEventUtils

/**
 * File        : android.presenter.fragments.combooven.CookTimeNumberPadFragment.
 * Brief       : Instance of clock time numberPad for combo oven
 * Author      : GHARDNS <br>
 * Created On  : 18-03-2024 <br>
 */
class CookTimeNumberPadFragment : AbstractCookTimeNumberPadFragment(),
    AbstractCookTimeNumberPadFragment.ButtonClickListenerInterface {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
        setButtonInteractionListener(this)
    }

    /**
     * initialize UI components
     */
    private fun initButton() {
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.visibility = View.VISIBLE
        cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.visibility = View.VISIBLE
    }

    override fun onRightButtonClick() {
        val buttonText = cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.getTextButtonText()
        AudioManagerUtils.playOneShotSound(
            view?.context,
            if(buttonText.equals(getString(R.string.text_button_next))){
                R.raw.button_press
            }else{
                R.raw.start_press
            },
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        if (cookTimeText != AppConstants.DEFAULT_COOK_TIME_MICROWAVE) {
            if (validateCookTime()) {
                val cookTimeSec = CookingAppUtils.getCookTimerStringAsSeconds(
                    cookTimeText, !isOfTypeMicrowaveOven || !isMagnetronBasedRecipe()
                )
                val requiredRecipeOptions =
                    cookingViewModel?.recipeExecutionViewModel?.requiredOptions?.value
                val powerAndTimeOptions =
                    requiredRecipeOptions?.contains(RecipeOptions.COOK_TIME) == true && requiredRecipeOptions.contains(
                        RecipeOptions.MWO_POWER_LEVEL
                    )
                if (cookingViewModel?.isOfTypeMicrowaveOven == true && powerAndTimeOptions && cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.visibility == View.VISIBLE && cookingViewModel?.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.IDLE) {
                    if (cookingViewModel?.recipeExecutionViewModel?.setCookTime(cookTimeSec.toLong())?.isError == true) return
                    DoorEventUtils.startMicrowaveRecipeOrShowPopup(this, cookingViewModel)
                    return
                }
                handleCommonCookTimeRightTextButtonClick(
                    cookingViewModel, this, cookTimeSec
                )
            }
        }
    }

    override fun onLeftButtonClick() {
        //Bottom left button click event
    }

    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }

    override fun onLeftPowerButtonClick() {
        if (cookingViewModel?.isOfTypeMicrowaveOven == true) {
            setCookTimeAndNavigateToPowerLevel()
        }
    }

    override fun updateRightButtonText(): String {
        if (cookingViewModel?.isOfTypeMicrowaveOven == true && cookingViewModel?.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.IDLE) {
            val requiredRecipeOptions =
                cookingViewModel?.recipeExecutionViewModel?.requiredOptions?.value
            if (requiredRecipeOptions?.contains(RecipeOptions.COOK_TIME) == true && requiredRecipeOptions.contains(
                    RecipeOptions.MWO_POWER_LEVEL
                )
            ) return getString(R.string.text_button_start)
        }
        return super.updateRightButtonText()
    }

    override fun provideIntegerRange(): IntegerRange {
        return (recipeViewModel?.cookTimeOption?.value as IntegerRange?)!!
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}
