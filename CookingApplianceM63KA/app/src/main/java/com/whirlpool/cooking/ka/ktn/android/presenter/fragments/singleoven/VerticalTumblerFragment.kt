/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.singleoven

import android.os.Bundle
import android.presenter.basefragments.AbstractVerticalTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
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
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import java.util.Locale

/**
 * File       : android.presenter.fragments.singleoven
 * Brief      : Vertical tumbler .
 * Author     : GHARDNS.
 * Created On : 02-Apr-2024
 * Details    : This file will implement abstract vertical tumbler fragment and will override the method
 */
class VerticalTumblerFragment : AbstractVerticalTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface {

    /** ViewModel instances */
    private var recipeViewModel: RecipeExecutionViewModel? = null
    private var cookingViewModel: CookingViewModel? = null

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
        manageLeftButton()
    }

    override fun setCTARight() {
        tumblerViewHolderHelper?.providePrimaryButton()?.isVisible = true
        tumblerViewHolderHelper?.providePrimaryButton()?.setTextButtonText(
            NavigationUtils.getRightButtonTextForRecipeOption(
                context, cookingViewModel, RecipeOptions.COOK_TIME
            )
        )
    }

    override fun initTumbler() {
        setUpViewModels()
        initVerticalTumbler()
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
            this,
            R.id.action_verticalTumblerFragment_to_cookTimeFragment,
            bundle,
            null
        )
    }

}