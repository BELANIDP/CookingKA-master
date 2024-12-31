/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.assisted

import android.os.Bundle
import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateToShowInstructionFragment

/**
 * File       : android.presenter.fragments.assisted.TemperatureTumblerFragment.
 * Brief      : implementation fragment class for temperature tumbler screen for manual modes.
 * Author     : BHIMAR.
 * Created On : 15/03/2024
 * Details    :
 */
class TemperatureTumblerFragment : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface {
    override fun setCtaLeft() {
        getBinding()?.btnGhost?.visibility = View.GONE
        getBinding()?.constraintLeftButton?.visibility = View.GONE
    }

    override fun setCtaRight() {
        getBinding()?.btnPrimary?.visibility = View.VISIBLE

    }

    override fun updateCtaRightButton() {
        getBinding()?.btnPrimary?.text = when (arguments?.getString(BundleKeys.BUNDLE_IS_FROM_PREVIEW_SCREEN)) {
            BundleKeys.BUNDLE_VALUE_POP_TO_PREVIEW -> resources.getString(R.string.text_button_update)
            else -> resources.getString(R.string.text_button_next)
        }
    }

    override fun setHeaderLevel() {
        CookingAppUtils.setHeaderTitleAsRecipeName(
            getBinding()?.headerBar, CookingViewModelFactory.getInScopeViewModel()
        )
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
        if (productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO || productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
            getBinding()?.headerBar?.setOvenCavityIcon(if (getCookingViewModel()?.isPrimaryCavity == true) R.drawable.ic_oven_cavity_large else R.drawable.ic_lower_cavity_large)
        } else {
            getBinding()?.headerBar?.setOvenCavityIconVisibility(false)
        }
    }

    override fun initTumbler() {
        isShowSuffixDecoration = true
        @Suppress("UNCHECKED_CAST")
        initTemperatureTumblerForRange(tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.targetTemperatureOptions as LiveData<IntegerRange>)
        setTemperatureTypeSubText()
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return View.GONE
    }

    override fun onClick(v: View?) {
        handleGenericTemperatureClick(v)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onHMIRightKnobClick() {
       onHMIKnobRightOrLeftClick()
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
        val bundle = if (arguments == null) Bundle() else arguments
        getBinding()?.tumblerNumericBased?.selectedValue?.toInt()
            ?.let {
                bundle?.putInt(BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE, it)
            }
        NavigationUtils.navigateSafely(
            this,
            R.id.action_manualModeTemperatureTumblerFragment_to_temperature_numpad,
            bundle,
            null
        )
    }

    override fun setSuffixDecoration(): String {
        return AppConstants.DEGREE_SYMBOL
    }

    override fun infoIconOnClick() {
        super.infoIconOnClick()
        navigateToShowInstructionFragment(activity)
    }
}