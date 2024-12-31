/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.favorites

import android.os.Bundle
import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.HMILogHelper
import core.utils.NavigationUtils

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.fragments.favorites.FavoritesProbeTemperatureTumblerFragment.
 * Brief      : implementation fragment class to set probe temperature tumbler screen for probe recipes
 * Author     : VYASM
 * Created On : 23/10/2024
 * Details    : User can set probe temperature
 */
class FavoritesProbeTemperatureTumblerFragment : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface {
    override fun setCtaLeft() {
        getBinding()?.btnGhost?.visibility = View.GONE
        getBinding()?.constraintLeftButton?.visibility = View.GONE
    }

    override fun setCtaRight() {
        getBinding()?.btnPrimary?.visibility = View.VISIBLE
        getBinding()?.constraintRightButton?.visibility = View.VISIBLE
        getBinding()?.btnPrimary?.text =
            resources.getString(R.string.text_button_next)
    }

    override fun setHeaderLevel() {
        getBinding()?.headerBar?.setTitleText(R.string.text_header_probe_temp)
        getBinding()?.headerBar?.setCustomOnClickListener(this)
        getBinding()?.degreesType?.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.manual_mode_temp_type_text_color
            )
        )
        getBinding()?.headerBar?.setInfoIconVisibility(false)
        getBinding()?.headerBar?.setRightIconVisibility(true)
        getBinding()?.headerBar?.setRightIcon(R.drawable.numpad_icon)
        getBinding()?.headerBar?.setLeftIconVisibility(true)
        val productVariant = CookingViewModelFactory.getProductVariantEnum()
        if(productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO || productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN){
            getBinding()?.headerBar?.setOvenCavityIcon(if(getCookingViewModel()?.isPrimaryCavity == true) R.drawable.ic_oven_cavity_large else R.drawable.ic_lower_cavity_large)
        }else{
            getBinding()?.headerBar?.setOvenCavityIconVisibility(false)
        }
    }

    override fun initTumbler() {
        isShowSuffixDecoration = true
        initTemperatureTumblerForRange(tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.meatProbeTargetTemperatureOption)
        setTemperatureTypeSubText()
    }

    override fun updateSelectedTemperatureForCycleTemperature() {
        val bundle = arguments
        var bundleTemperature: Int = AppConstants.DEFAULT_SELECTED_TEMP
        if (bundle != null) {
            bundleTemperature =
                requireArguments().getInt(BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE, -1)
        }
        if (bundleTemperature == AppConstants.DEFAULT_SELECTED_TEMP) {
            /*Show current cycle temperature as the default selected temperature when changing
            temperature while cycle running*/
            val targetTemperature =
                getCookingViewModel()?.recipeExecutionViewModel?.meatProbeTargetTemperature?.value
            if (targetTemperature != null) {
                selectedTemp = targetTemperature
                HMILogHelper.Logi("updateSelectedTumbler: cycle temperature from view model$selectedTemp")
            }
        } else {
            /*Arguments to show the temperature entered in the Number pad while switching from
             number pad to tumbler . The value entered in the number pad input screen is an
             intermediate value of HMI which will not known to SDK until HMI sets it while updating
             the cycle.*/
            selectedTemp = bundleTemperature
            HMILogHelper.Logi("updateSelectedTumbler: selectedTemp from Arguments$selectedTemp")
        }
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return View.GONE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            getBinding()?.btnPrimary?.id -> {
                recyclerView?.selectedValue?.toFloat()?.let {
                    NavigationUtils.navigateAndSetProbeTemperature(
                        this, getCookingViewModel(),
                        it
                    )
                }
            }
            getBinding()?.constraintRightButton?.id -> {
                recyclerView?.selectedValue?.toFloat()?.let {
                    NavigationUtils.navigateAndSetProbeTemperature(
                        this, getCookingViewModel(),
                        it
                    )
                }
            }
            getBinding()?.btnGhost?.id -> {}
            getBinding()?.constraintLeftButton?.id -> {}
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onHMILeftKnobClick() {

    }

    override fun onHMILongLeftKnobPress() {

    }

    override fun onHMIRightKnobClick() {
        onHMIKnobRightOrLeftClick()
    }

    override fun onHMILongRightKnobPress() {

    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            manageKnobRotation(knobDirection)
        }
    }

    override fun leftIconOnClick() {
        super.leftIconOnClick()
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(this) ?: requireView()
            )
        )
    }

    override fun rightIconOnClick() {
        super.rightIconOnClick()
        val bundle = Bundle()
        getBinding()?.tumblerNumericBased?.selectedValue?.toInt()
            ?.let { bundle.putInt(BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE, it) }
        NavigationUtils.navigateSafely(
            this,
            R.id.action_to_favoritesProbeTemperatureNumPad,
            bundle,
            NavOptions.Builder().setPopUpTo(R.id.favoritesProbeTemperatureTumblerFragment,true).build()
        )
    }

    override fun setSuffixDecoration(): String {
        return AppConstants.DEGREE_SYMBOL
    }
}