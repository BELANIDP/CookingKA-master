/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.doubleoven

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import core.jbase.AbstractCookTimeNumberPadFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils

/**
 * File        : android.presenter.fragments.doubleoven.CookTimeNumberPadFragment.
 * Brief       : Cook time number-pad screen for double oven variant
 * Author      : GHARDNS/Nikki
 * Created On  : 18-03-2024
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
    }

    override fun onRightButtonClick() {
        var buttonText = cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.getTextButtonText()
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
            if (validateCookTime())
                handleCommonCookTimeRightTextButtonClick(
                    cookingViewModel,
                    this, CookingAppUtils.getCookTimerStringAsSeconds(
                        cookTimeText, !isOfTypeMicrowaveOven || !isMagnetronBasedRecipe()
                    )
                )
        }
    }

    override fun onLeftButtonClick() {
        //Bottom left button click event
    }

    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }

    override fun onLeftPowerButtonClick() {
        //TODO("Not yet implemented")
    }

    override fun provideIntegerRange(): IntegerRange {
        return (recipeViewModel?.cookTimeOption?.value as IntegerRange?)!!
    }

}
