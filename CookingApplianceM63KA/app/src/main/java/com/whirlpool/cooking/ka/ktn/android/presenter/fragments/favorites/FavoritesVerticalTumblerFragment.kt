package android.presenter.fragments.favorites

import android.os.Bundle
import android.presenter.basefragments.AbstractVerticalTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.AppConstants.DIGIT_ZERO
import core.utils.BundleKeys
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import java.util.Locale

/**
 * File       : android.presenter.fragments.favorites.FavoritesVerticalTumblerFragment
 * Brief      : Vertical tumbler .
 * Author     : VYASM
 * Created On : 23/10/2024
 * Details    : This file will implement abstract vertical tumbler fragment and will override the method
 */
class FavoritesVerticalTumblerFragment : AbstractVerticalTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface {


    /** ViewModel instances */
    private var recipeViewModel: RecipeExecutionViewModel? = null
    private var cookingViewModel: CookingViewModel? = null
    private  var firstUseFlag: Boolean = true

    /**
     * Method to setup the required View models
     */
    private fun setUpViewModels() {
        cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
        recipeViewModel = cookingViewModel?.recipeExecutionViewModel
    }

    override fun setHeaderLevel() {
        tumblerViewHolderHelper?.provideHeaderBar()?.setOvenCavityIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBar()?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBar()?.setRightIcon(R.drawable.numpad_icon)
        tumblerViewHolderHelper?.provideHeaderBar()?.setTitleText(getString(R.string.text_header_enter_time_tumbler))
        tumblerViewHolderHelper?.provideHeaderBar()?.setCustomOnClickListener(this)
    }

    override fun rightIconOnClick() {
        navigateToCookTimeNumPadScreen()
    }

    override fun leftIconOnClick() {
        super.leftIconOnClick()
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(this) ?: requireView()
            )
        )
    }

    override fun setCTALeft() {
        tumblerViewHolderHelper?.provideGhostButton()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideConstraintGhost()?.visibility = View.GONE
    }


    override fun manageLeftPowerButton() {
        tumblerViewHolderHelper?.provideLeftPowerButton()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideLeftPowerConstraint()?.visibility = View.GONE
    }

    override fun setCTARight() {
        tumblerViewHolderHelper?.providePrimaryButton()?.isVisible = true
        tumblerViewHolderHelper?.providePrimaryButton()?.setTextButtonText(R.string.text_button_next)
        tumblerViewHolderHelper?.providePrimaryConstraint()?.isVisible = true
    }

    override fun onClick(v: View) {
        when (v.id) {
            tumblerViewHolderHelper?.providePrimaryButton()?.id -> {
                if (isValidCookTime()) getSelectedCookTime()?.let {
                    if (recipeViewModel?.setCookTime(it.toLong())?.isError == true) return
                    NavigationUtils.navigateNextForFavoritesRecipe(
                        this@FavoritesVerticalTumblerFragment,
                        CookingViewModelFactory.getInScopeViewModel(),
                        RecipeOptions.COOK_TIME
                    )
                }
            }
            tumblerViewHolderHelper?.providePrimaryConstraint()?.id -> {
                if (isValidCookTime()) getSelectedCookTime()?.let {
                    if (recipeViewModel?.setCookTime(it.toLong())?.isError == true) return
                    NavigationUtils.navigateNextForFavoritesRecipe(
                        this@FavoritesVerticalTumblerFragment,
                        CookingViewModelFactory.getInScopeViewModel(),
                        RecipeOptions.COOK_TIME
                    )
                }
            }
        }
    }

    override fun initTumbler() {
        setUpViewModels()
        initVerticalTumbler()
    }

    override fun onResume() {
        super.onResume()
        firstUseFlag = true
        updateInabilityOfButtons()
    }

    override fun onPause() {
        super.onPause()
        firstUseFlag = false
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    /**
     * Method use for navigate to cook time numpad screen
     * send bundle for sending selected hour and min
     */
    private fun navigateToCookTimeNumPadScreen() {
        var selectedHour =  tumblerViewHolderHelper?.provideVerticalTumblerLeft()?.selectedValue
        var selectedMinute =  tumblerViewHolderHelper?.provideVerticalTumblerCenter()?.selectedValue
        selectedHour = String.format(
            Locale.getDefault(),
            AppConstants.DEFAULT_DATE_VALUE_FORMAT,
            selectedHour?.toInt()
        )
        selectedMinute = String.format(
            Locale.getDefault(),
            AppConstants.DEFAULT_DATE_VALUE_FORMAT,
            selectedMinute?.toInt()
        )
        HMILogHelper.Logd(" cook time numpad-->$selectedHour$selectedMinute")

        val bundle = Bundle()
        bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, "$selectedHour$selectedMinute")
        NavigationUtils.navigateSafely(
            this, R.id.action_to_favoritesCookTimeNumberPadFragment,
            bundle,
            NavOptions.Builder().setPopUpTo(R.id.favoritesCookTimeNumberPadFragment,true).build()
        )
    }

    override fun updateCTARightText() {
        // do nothing
    }

    override fun updateInabilityOfButtons() {

        tumblerViewHolderHelper?.provideGhostButton()?.isEnabled = false
        tumblerViewHolderHelper?.provideGhostButton()?.isClickable = false

        tumblerViewHolderHelper?.provideConstraintGhost()?.isEnabled = false
        tumblerViewHolderHelper?.provideConstraintGhost()?.isClickable = false

        tumblerViewHolderHelper?.provideLeftPowerButton()?.isEnabled = false
        tumblerViewHolderHelper?.provideLeftPowerButton()?.isClickable = false

        tumblerViewHolderHelper?.provideLeftPowerConstraint()?.isEnabled = false
        tumblerViewHolderHelper?.provideLeftPowerConstraint()?.isClickable = false

        val cookTime = recipeViewModel?.cookTime?.value
        HMILogHelper.Logd("Enabling buttons for CookTime : ${cookTime}, firstUseFlag $firstUseFlag")
        if (cookTime == DIGIT_ZERO.toLong() && firstUseFlag) {
            firstUseFlag = false
            tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = true
            tumblerViewHolderHelper?.providePrimaryButton()?.isClickable = true

            tumblerViewHolderHelper?.providePrimaryConstraint()?.isEnabled = true
            tumblerViewHolderHelper?.providePrimaryConstraint()?.isClickable = true
        } else if (!isValidCookTime()) {
            tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = false
            tumblerViewHolderHelper?.providePrimaryButton()?.isClickable = false

            tumblerViewHolderHelper?.providePrimaryConstraint()?.isEnabled = false
            tumblerViewHolderHelper?.providePrimaryConstraint()?.isClickable = false
        } else {
            tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = true
            tumblerViewHolderHelper?.providePrimaryButton()?.isClickable = true

            tumblerViewHolderHelper?.providePrimaryConstraint()?.isEnabled = true
            tumblerViewHolderHelper?.providePrimaryConstraint()?.isClickable = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        firstUseFlag = false
    }
}
