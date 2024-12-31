/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.sabbath

import android.media.AudioManager
import android.presenter.basefragments.abstract_self_clean.AbstractCavitySelectionFragment
import android.view.View
import android.view.View.VISIBLE
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SabbathUtils

/**
 * File       : android.presenter.fragments.sabbath.SabbathCavitySelectionFragment.
 * Brief      : implementation fragment class for Cavity selection screen for Sabbath Bake
 * Author     : Hiren
 * Created On : 08/20/2024
 * Details    : Selecting cavity for Sabbath Bake recipe
 */
class SabbathCavitySelectionFragment : AbstractCavitySelectionFragment() {
    override fun headerBarSetUp() {
        binding.singleLineHeaderBar.setOvenCavityIconVisibility(false)
        binding.singleLineHeaderBar.setInfoIconVisibility(false)
        binding.singleLineHeaderBar.setRightIconVisibility(false)
        binding.singleLineHeaderBar.setOvenCavityTitleTextVisibility(false)
        binding.singleLineHeaderBar.setLeftIconVisibility(true)
        binding.backArrowArea.setOnClickListener(this)
    }

    override fun providesBackPressNav() {
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
    override fun checkUpperCavityAvailability() {
        // do not delete this empty method as logic is executing when self clean unavailable after 30 mins
    }

    override fun checkLowerCavityAvailability() {
        // do not delete this empty method as logic is executing when self clean unavailable after 30 mins
    }

    override fun rightIconOnClick() {
        navigateUpperCavitySelection()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.upper_oven_layout, R.id.upper_oven_area -> {
                navigateUpperCavitySelection()
            }

            R.id.lower_oven_layout, R.id.lower_oven_area -> {
                CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
                NavigationUtils.navigateAfterSabbathRecipeSelection(this, CookingViewModelFactory.getSecondaryCavityViewModel())
            }
            R.id.back_arrow_area->{
                providesBackPressNav()
            }
        }
    }

    private fun navigateUpperCavitySelection() {
        CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        NavigationUtils.navigateAfterSabbathRecipeSelection(
            this,
            CookingViewModelFactory.getPrimaryCavityViewModel()
        )
    }

    override fun initViewsDoubleOven() {
        binding.uppperCavityLbl.visibility = VISIBLE
        binding.uppperCavityLbl.text = resources.getString(R.string.cavity_selection_upper_oven_all_caps)
        binding.lowerCavityLbl.visibility = VISIBLE
        binding.lowerCavityLbl.text = resources.getString(R.string.cavity_selection_lower_oven_all_cap)
        CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.cancel()
        CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.cancel()
        binding.holder.setBackgroundColor(resources.getColor(R.color.black, null))
        HMILogHelper.Logd(
            "SabbathCavitySelection fragment cancel",
            "cancelled recipe for both cavities"
        )
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        SabbathUtils.probeDetectedBeforeSabbathProgramming(this, cookingViewModel, {
            SabbathUtils.navigateToSabbathSettingSelection(this)
        }, {})
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            manageKnobRotation(knobDirection)
        }
    }

    override fun onHMIRightKnobClick() {
        // Do nothing
    }

    override fun onHMILeftKnobClick() {
        KnobNavigationUtils.knobForwardTrace = true
        manageKnobClickListener()
    }


    override fun updateTechnicianTextModeText() {
        // Do nothing
    }
}