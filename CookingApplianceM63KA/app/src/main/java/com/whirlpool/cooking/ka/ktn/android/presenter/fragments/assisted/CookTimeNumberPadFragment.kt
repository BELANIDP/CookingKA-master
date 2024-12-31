/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.assisted

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.AbstractCookTimeNumberPadFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.NavigationUtils

/**
 * File        : android.presenter.fragments.assisted.CookTimeNumberPadFragment.
 * Brief       : Instance of clock time numberPad for combo oven
 * Author      : Hiren <br>
 * Created On  : 18-03-2024 <br>
 */
class CookTimeNumberPadFragment : AbstractCookTimeNumberPadFragment(),
    AbstractCookTimeNumberPadFragment.ButtonClickListenerInterface {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recipeName = cookingViewModel?.recipeExecutionViewModel?.recipeName?.value
        toShowRemoveTimerInAssisted = (CookingAppUtils.isRecipeAssisted(
            recipeName, cookingViewModel?.cavityName?.value)
                && recipeName.equals(AppConstants.RECIPE_PIZZA_FROZEN_COOK,true)
                && arguments?.getString(BundleKeys.BUNDLE_IS_FROM_PREVIEW_SCREEN)
            ?.contentEquals(BundleKeys.BUNDLE_VALUE_POP_TO_PREVIEW) == true)
        initButton()
        setButtonInteractionListener(this)
    }

    /**
     * initialize UI components
     */
    private fun initButton() {
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.visibility = View.VISIBLE
        cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.visibility = View.VISIBLE
        if(toShowRemoveTimerInAssisted){
            cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.VISIBLE
            cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.setTextButtonText(R.string.text_button_remove_timer)
        }
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
        if(toShowRemoveTimerInAssisted){
            cookingViewModel?.recipeExecutionViewModel?.setCookTime(0)
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    NavigationUtils.getViewSafely(this) ?: requireView()
                )
            )
        }
    }

    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }

    override fun onLeftPowerButtonClick() {
        if (cookingViewModel?.isOfTypeMicrowaveOven == true) {
            setCookTimeAndNavigateToPowerLevel()
        }
    }
    override fun provideIntegerRange(): IntegerRange {
        return (recipeViewModel?.cookTimeOption?.value as IntegerRange?)!!
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}
