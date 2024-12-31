package android.presenter.fragments.steamclean

import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.abstract_self_clean.AbstractCavitySelectionFragment
import android.view.View
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.cookbook.records.RecipeRecord
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.HMILogHelper.Logd
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils


/**
 * File       : com.whirlpool.cooking.steamclean.SteamCleanCavitySelectionFragment
 * Brief      : Handles Steam Clean Cavity Selection
 * Author     : Rajendra
 * Created On : 11-NOV-2024
 */

class SteamCleanCavitySelectionFragment : AbstractCavitySelectionFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productVariant = CookingViewModelFactory.getProductVariantEnum()
        if (productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO) {
            isEnableUpperOven(false)
        }
    }

    override fun headerBarSetUp() {
        binding.singleLineHeaderBar.apply {
            visibility = View.VISIBLE
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setRightIconVisibility(false)
            setLeftIconVisibility(true)
            getBinding()?.clOvenCavity?.visibility = View.GONE
        }
    }

    override fun navigateToUpperOven() {
        if (MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getPrimaryCavityViewModel())) {
            PopUpBuilderUtils.probeIncompatiblePopup(this, onMeatProbeConditionMet = {
                navigateTOSteamInstructionScreen()
            })
        } else {
            navigateTOSteamInstructionScreen()
        }
    }


    override fun navigateToLowerOven() {
        if (MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getSecondaryCavityViewModel())) {
            PopUpBuilderUtils.probeIncompatiblePopup(this, onMeatProbeConditionMet = {
                navigateTOSteamInstructionScreen()
            })
        } else {
            navigateTOSteamInstructionScreen()
        }
    }

    private fun navigateTOSteamInstructionScreen() {
        NavigationUtils.navigateSafely(
            this,
            R.id.action_steamCleanCavitySelectionFragment_to_steamCleanInstructionsFragment,
            null,
            null
        )
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

    override fun onHMILeftKnobClick() {
        manageKnobClickListener()
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO)
                isUpperOvenSelected = true
            manageKnobRotation(knobDirection)
        }
    }


    override fun updateTechnicianTextModeText() {
        // Do nothing
    }
}