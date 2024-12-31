/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.favorites

import android.media.AudioManager
import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.jbase.AbstractCookTimeNumberPadFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateNextForFavoritesRecipe

/**
 * File        : android.presenter.fragments.favorites.FavoritesCookTimeNumberPadFragment.
 * Brief       : Instance of clock time numberPad for Favorites
 * Author      : VYASM
 * Created On  : 23/10/2024
 */
class FavoritesCookTimeNumberPadFragment : AbstractCookTimeNumberPadFragment(),
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

    override fun updateRightButtonText(): String {
        return getString(R.string.text_button_next)
    }

    override fun manageLeftButton() {
        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
        cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.visibility = View.GONE
        cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.visibility = View.GONE
        cookTimeNumberPadViewHolderHelper?.getLeftPowerConstraint()?.visibility = View.GONE
    }

    override fun observePreheatingLiveData() {
        //do nothing
    }

    override fun observeCookTimerLiveData() {
        //do nothing
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
                if (recipeViewModel?.setCookTime(cookTimeSec.toLong())?.isError == true) return
                navigateNextForFavoritesRecipe(
                    this@FavoritesCookTimeNumberPadFragment,
                    CookingViewModelFactory.getInScopeViewModel(),
                    RecipeOptions.COOK_TIME
                )
            }
        }
    }

    override fun navigateToVerticalTimeTumblerScreen() {
        if (cookTimeText != AppConstants.EMPTY_STRING) {
            val cookTimeSec = CookingAppUtils.getCookTimerStringAsSeconds(
                cookTimeText,
                !isOfTypeMicrowaveOven || !isMagnetronBasedRecipe()
            )
            val bundle = Bundle()
            bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, "$cookTimeSec")
            NavigationUtils.navigateSafely(
                this,
                R.id.action_to_favoritesCookTimeTumbler,
                bundle,
                NavOptions.Builder().setPopUpTo(R.id.favoritesVerticalTumblerFragment,true).build()
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
        //Do nothing
    }

    override fun provideIntegerRange(): IntegerRange {
        return (recipeViewModel?.cookTimeOption?.value as IntegerRange?)!!
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun enableTextButtons() {
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = true
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = true
        cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isEnabled = true
        cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isClickable = true
    }
}
