package android.presenter.fragments.self_clean

import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.abstract_self_clean.AbstractCavitySelectionFragment
import android.view.View
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils


class SelfCleanCavitySelectionFragment : AbstractCavitySelectionFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val productVariant = CookingViewModelFactory.getProductVariantEnum()
        if (productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO) {
            isEnableUpperOven(false)
        }
    }

    override fun headerBarSetUp() {
        binding.singleLineHeaderBar.visibility = View.VISIBLE
        binding.singleLineHeaderBar.setOvenCavityIconVisibility(false)
        binding.singleLineHeaderBar.setInfoIconVisibility(false)
        binding.singleLineHeaderBar.setRightIconVisibility(false)
        binding.singleLineHeaderBar.setLeftIconVisibility(true)
        binding.singleLineHeaderBar.getBinding()?.clOvenCavity?.visibility = View.GONE
    }

    override fun navigateToUpperOven() {
        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = true)
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.BUNDLE_NAVIGATED_FROM_SELF_CLEAN, true)
        NavigationUtils.navigateSafely(
            this,
            R.id.action_selfCleanCavitySelectionFragment_to_durationSelectionFragment,
            bundle,
            null
        )
    }


    override fun navigateToLowerOven() {
        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = true)
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.BUNDLE_NAVIGATED_FROM_SELF_CLEAN, true)
        NavigationUtils.navigateSafely(
            this,
            R.id.action_selfCleanCavitySelectionFragment_to_durationSelectionFragment,
            bundle,
            null
        )
    }

    override fun providesBackPressNav() {
        //Handling Self clean flow user navigate back need to enabled and disabled HMI keys
        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = false)
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
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        //Handling Self clean flow cavity selection screen user insert probe need to enabled and disabled HMI keys
        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = false)
        if (CookingViewModelFactory.getInScopeViewModel() == null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, cookingViewModel)
            return
        }
        if (cookingViewModel != null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            launchProbeDetectedPopupAsPerVariant(cookingViewModel)
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        //Handling Self clean flow cavity selection screen user removed probe need to enabled and disabled HMI keys
        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = true)
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }


    override fun updateTechnicianTextModeText() {
        // Do nothing
    }
}