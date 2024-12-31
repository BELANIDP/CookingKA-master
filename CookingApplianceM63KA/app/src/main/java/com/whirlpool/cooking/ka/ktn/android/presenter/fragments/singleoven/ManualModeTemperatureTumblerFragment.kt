/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.singleoven

import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.text.TextUtils
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateToShowInstructionFragment

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.fragments.single oven.ManualModeTemperatureTumblerFragment.
 * Brief      : implementation fragment class for temperature tumbler screen for manual modes.
 * Author     : BHIMAR.
 * Created On : 15/03/2024
 * Details    :
 */
class ManualModeTemperatureTumblerFragment : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface {
    override fun setCtaLeft() {
            if (getCookingViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == false &&
                (getCookingViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.DELAYED ||
                        getCookingViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.IDLE) &&
                getCookingViewModel()?.let {
                    CookingAppUtils.isRecipeOptionAvailable(
                        it.recipeExecutionViewModel,
                        RecipeOptions.DELAY_TIME
                    )
                } == true) {
                getBinding()?.btnGhost?.visibility = View.VISIBLE
                getBinding()?.btnGhost?.text = resources.getString(R.string.text_button_delay)
            } else {
                getBinding()?.btnGhost?.visibility = View.GONE
            }
    }

    override fun setCtaRight() {
        getBinding()?.btnPrimary?.visibility = View.VISIBLE
        getBinding()?.btnPrimary?.text =
            resources.getString(R.string.text_button_start)
    }

    override fun setHeaderLevel() {
        val isProbeRecipe =
            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.isProbeBasedRecipe
        val checkIfInstructionAvailable = CookingAppUtils.checkIfInstructionAvailable(this,CookingViewModelFactory.getInScopeViewModel())
        getBinding()?.headerBar?.setInfoIconVisibility(!isProbeRecipe&&checkIfInstructionAvailable)
        getBinding()?.headerBar?.setRightIconVisibility(true)
        getBinding()?.headerBar?.setRightIcon(R.drawable.numpad_icon)
        if (isProbeRecipe) {
            getBinding()?.headerBar?.setTitleText(R.string.text_header_oven_temp)
        } else {
            CookingAppUtils.setHeaderTitleAsRecipeName(
                getBinding()?.headerBar, CookingViewModelFactory.getInScopeViewModel()
            )
        }
        getBinding()?.headerBar?.setCustomOnClickListener(this)
        getBinding()?.degreesType?.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.manual_mode_temp_type_text_color
            )
        )
        getBinding()?.headerBar?.setOvenCavityIconVisibility(false)
    }

    @Suppress("UNCHECKED_CAST")
    override fun initTumbler() {
        isShowSuffixDecoration = true
        initTemperatureTumblerForRange(tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.targetTemperatureOptions as LiveData<IntegerRange>)
        setTemperatureTypeSubText()
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return View.GONE
    }

    override fun onClick(v: View?) {
        handleGenericTemperatureClick(v)
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
        AudioManagerUtils.playOneShotSound(
            ContextProvider.getContext(),
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
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
        AudioManagerUtils.playOneShotSound(
            ContextProvider.getContext(),
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        NavigationUtils.navigateSafely(
            this, R.id.action_manualModeTemperatureTumblerFragment_to_temperature_numpad,
            bundle, null
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