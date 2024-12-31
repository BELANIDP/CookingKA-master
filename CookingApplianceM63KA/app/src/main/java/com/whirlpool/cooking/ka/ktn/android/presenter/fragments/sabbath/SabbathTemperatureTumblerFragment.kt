/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.sabbath

import android.os.Bundle
import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateToShowInstructionFragment
import core.utils.SabbathUtils

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.fragments.combo oven.ProbeTemperatureTumblerFragment.
 * Brief      : implementation fragment class to set probe temperature tumbler screen for probe recipes
 * Author     : Hiren
 * Created On : 05/29/2024
 * Details    : User can set Sabbath Bake temperature through Tumbler
 */
class SabbathTemperatureTumblerFragment : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface {
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        SabbathUtils.probeDetectedBeforeSabbathProgramming(this, cookingViewModel, {
            SabbathUtils.navigateToSabbathSettingSelection(this)
        }, {})
    }
    override fun setCtaLeft() {
        getBinding()?.btnGhost?.visibility = View.GONE
    }

    override fun setCtaRight() {
        getBinding()?.btnPrimary?.visibility = View.VISIBLE
    }

    override fun updateCtaRightButton() {
        getBinding()?.btnPrimary?.text =
            resources.getString(R.string.text_button_next)
    }

    override fun setHeaderLevel() {
        getBinding()?.headerBar?.setInfoIconVisibility(false)
        getBinding()?.headerBar?.setRightIconVisibility(true)
        getBinding()?.headerBar?.setRightIcon(R.drawable.ic_numpad)
        getBinding()?.headerBar?.setOvenCavityTitleTextVisibility(true)
        getBinding()?.headerBar?.setTitleText(R.string.sabbathBake)
        getBinding()?.headerBar?.setCustomOnClickListener(this)
        getBinding()?.degreesType?.setTextColor(
            ContextCompat.getColor(
                requireContext(), R.color.manual_mode_temp_type_text_color
            )
        )
        if(CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
            getBinding()?.headerBar?.setOvenCavityIconVisibility(true)
            if (getCookingViewModel()?.isPrimaryCavity == true) {
                getBinding()?.headerBar?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
            } else {
                getBinding()?.headerBar?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
            }
        }else{
            getBinding()?.headerBar?.setOvenCavityIconVisibility(false)
        }
    }

    override fun initTumbler() {
        isShowSuffixDecoration = true
        @Suppress("UNCHECKED_CAST") initTemperatureTumblerForRange(tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.targetTemperatureOptions  as LiveData<IntegerRange>)
        setTemperatureTypeSubText()
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return View.GONE
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            getBinding()?.btnPrimary?.id -> {
                recyclerView?.selectedValue?.toFloat()?.let {
                    SabbathUtils.sabbathSetTemperature(
                        this,
                        getCookingViewModel(),
                        it,
                        KnobNavigationUtils.knobForwardTrace
                    )
                }
            }
            getBinding()?.btnGhost?.id -> {}
        }
    }

    override fun onHMILeftKnobClick() {
       onHMIKnobRightOrLeftClick()
    }

    override fun onHMILongLeftKnobPress() {

    }

    override fun onHMIRightKnobClick() {

    }

    override fun onHMILongRightKnobPress() {

    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
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
            R.id.action_sabbathTemperatureTumblerFragment_to_sabbathTemperatureNumPadFragment,
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
    override fun onResume() {
        super.onResume()
        HMILogHelper.Logd("HMI_KEY","Sabbath Temperature Tumbler \n----------------")
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
    }
}