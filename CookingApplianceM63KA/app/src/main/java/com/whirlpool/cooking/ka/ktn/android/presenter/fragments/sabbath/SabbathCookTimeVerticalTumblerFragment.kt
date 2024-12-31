/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.sabbath

import android.os.Bundle
import android.presenter.basefragments.AbstractVerticalTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.SabbathUtils
import java.util.Locale

/**
 * File       : android.presenter.fragments.combooven
 * Brief      : Vertical tumbler for Sabbath Cook Timer Fragment
 * Author     : Hiren
 * Created On : 08/27/2024
 * Details    : This file will implement abstract vertical tumbler fragment and will override the method
 */
class SabbathCookTimeVerticalTumblerFragment : AbstractVerticalTumblerFragment(),
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
        tumblerViewHolderHelper?.provideHeaderBar()?.setRightIcon(R.drawable.ic_numpad)
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
        tumblerViewHolderHelper?.provideGhostButton()?.setOnClickListener(this)
        tumblerViewHolderHelper?.provideGhostButton()?.setTextButtonText(
            getString(R.string.text_button_set_untimed)
        )
        tumblerViewHolderHelper?.provideGhostButton()?.isVisible = true
        tumblerViewHolderHelper?.provideGhostButton()?.isEnabled = true
    }

    override fun setCTARight() {
        tumblerViewHolderHelper?.providePrimaryButton()?.isVisible = true
    }

    override fun updateCTARightText() {
        tumblerViewHolderHelper?.providePrimaryButton()?.setTextButtonText(
            getString(R.string.text_button_set_timed)
        )
    }

    override fun initTumbler() {
        setUpViewModels()
        initVerticalTumbler()
    }

    override fun updateInabilityOfButtons() {
        if (getSelectedCookTime() == 0 || !isValidCookTime()) {
            tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = false
            tumblerViewHolderHelper?.providePrimaryButton()?.isClickable = false

        } else {
            tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = true
            tumblerViewHolderHelper?.providePrimaryButton()?.isClickable = true
        }
    }

    /**
     * Method use for navigate to cook time numpad screen
     * send bundle for sending selected hour and min
     */
    private fun navigateToCookTimeNumPadScreen() {
        var selectedHour = tumblerViewHolderHelper?.provideVerticalTumblerLeft()?.selectedValue
        var selectedMinute = tumblerViewHolderHelper?.provideVerticalTumblerCenter()?.selectedValue
        selectedHour = String.format(
            Locale.getDefault(), AppConstants.DEFAULT_DATE_VALUE_FORMAT, selectedHour?.toInt()
        )
        selectedMinute = String.format(
            Locale.getDefault(), AppConstants.DEFAULT_DATE_VALUE_FORMAT, selectedMinute?.toInt()
        )
        HMILogHelper.Logd(" cook time numpad-->$selectedHour$selectedMinute")

        val bundle = Bundle()
        bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, "$selectedHour$selectedMinute")
        NavigationUtils.navigateSafely(
            this, R.id.action_verticalTumblerFragment_to_cookTimeFragment, bundle, null
        )
    }

    override fun onClick(v: View) {
        when (v.id) {
            tumblerViewHolderHelper?.provideGhostButton()?.id -> {
                HMILogHelper.Logd(tag, "Sabbath vertical cook time fragment setting untimed cycle")
                SabbathUtils.handleSabbathCookTimeRightTextButtonClick(cookingViewModel, this, 0)
            }

            tumblerViewHolderHelper?.providePrimaryButton()?.id -> {
                if (isValidCookTime()) getSelectedCookTime()?.let {
                    HMILogHelper.Logd(tag, "Sabbath vertical cook time fragment setting timed cycle value=$it")
                    SabbathUtils.handleSabbathCookTimeRightTextButtonClick(
                        cookingViewModel, this, it
                    )
                }
            }
        }
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        SabbathUtils.probeDetectedBeforeSabbathProgramming(this, cookingViewModel, {
            SabbathUtils.navigateToSabbathSettingSelection(this)
        }, {})
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            manageKnobRotationOnVerticalTumbler(knobDirection)
        }
    }

    override fun onHMIRightKnobClick() {
       // do nothing
    }

    override fun onHMILeftKnobClick() {
        super.onHMILeftKnobClick()
        manageLeftOrRightKnobClick()
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID)
            resetKnobParameters()
    }
    override fun onResume() {
        super.onResume()
        HMILogHelper.Logd("HMI_KEY","Sabbath Cook time vertical Tumbler \n----------------")
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
    }
}