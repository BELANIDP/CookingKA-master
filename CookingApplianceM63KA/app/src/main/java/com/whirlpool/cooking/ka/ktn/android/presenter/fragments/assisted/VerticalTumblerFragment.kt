/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.assisted

import android.os.Bundle
import android.presenter.basefragments.AbstractVerticalTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import java.util.Locale

/**
 * File       : android.presenter.fragments.combooven
 * Brief      : Vertical tumbler .
 * Author     : GHARDNS.
 * Created On : 02-Apr-2024
 * Details    : This file will implement abstract vertical tumbler fragment and will override the method
 */
class VerticalTumblerFragment : AbstractVerticalTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    AbstractVerticalTumblerFragment.CTAButtonClickListenerInterface {


    /** ViewModel instances */
    private var recipeViewModel: RecipeExecutionViewModel? = null
    private var cookingViewModel: CookingViewModel? = null

    /**
     * Method to setup the required View models
     */
    private fun setUpViewModels() {
        cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
        recipeViewModel = cookingViewModel?.recipeExecutionViewModel
        val recipeName = cookingViewModel?.recipeExecutionViewModel?.recipeName?.value
        toShowRemoveTimerInAssisted = (CookingAppUtils.isRecipeAssisted(
            recipeName, cookingViewModel?.cavityName?.value)
                && recipeName.equals(AppConstants.RECIPE_PIZZA_FROZEN_COOK,true)
                && arguments?.getString(BundleKeys.BUNDLE_IS_FROM_PREVIEW_SCREEN)
            ?.contentEquals(BundleKeys.BUNDLE_VALUE_POP_TO_PREVIEW) == true)
        setButtonInteractionListener(this)
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
        manageLeftButton()
    }

    override fun setCTARight() {
        tumblerViewHolderHelper?.providePrimaryButton()?.isVisible = true
        tumblerViewHolderHelper?.providePrimaryButton()?.setTextButtonText(
            NavigationUtils.getRightButtonTextForRecipeOption(
                context, cookingViewModel, RecipeOptions.COOK_TIME
            )
        )
        if(toShowRemoveTimerInAssisted){
            tumblerViewHolderHelper?.fragmentVerticalTumblerBinding?.btnGhost?.visibility = View.VISIBLE
            tumblerViewHolderHelper?.fragmentVerticalTumblerBinding?.btnGhost?.setTextButtonText(R.string.text_button_remove_timer)
        }
    }

    override fun initTumbler() {
        setUpViewModels()
        initVerticalTumbler()
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    /**
     * Method use for navigate to cook time numpad screen
     * send bundle for sending selected hour and min
     */
    private fun navigateToCookTimeNumPadScreen() {
        var selectedHour = tumblerViewHolderHelper?.provideVerticalTumblerLeft()?.selectedValue
        var selectedMinute = tumblerViewHolderHelper?.provideVerticalTumblerCenter()?.selectedValue


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
            this, R.id.action_verticalTumblerFragment_to_cookTimeFragment,
            bundle, null
        )
    }

    override fun onRightButtonClick() {
//        NA
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
//        NA
    }

    override fun onLeftPowerButtonClick() {
//        NA
    }
}