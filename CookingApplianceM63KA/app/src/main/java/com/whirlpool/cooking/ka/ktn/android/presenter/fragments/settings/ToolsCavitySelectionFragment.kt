package android.presenter.fragments.settings

import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.abstract_self_clean.AbstractCavitySelectionFragment
import android.view.View
import android.view.View.VISIBLE
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils.Companion.appendPlusSignToString
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils

class ToolsCavitySelectionFragment : AbstractCavitySelectionFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.holder.background = ResourcesCompat.getDrawable(
            resources,
            R.color.common_solid_black,
            requireContext().theme
        )
    }

    override fun navigateToUpperOven() {
        NavigationUtils.navigateSafely(
            this,
            R.id.action_toolsCavitySelection_to_temperatureCalibrationTumbler,
            null,
            null
        )
    }

    override fun navigateToLowerOven() {
        NavigationUtils.navigateSafely(
            this,
            R.id.action_toolsCavitySelection_to_temperatureCalibrationTumbler,
            null,
            null
        )
    }

    override fun checkUpperCavityAvailability() {
        binding.upperCavitySubtext.visibility = VISIBLE
        if (SettingsViewModel.getSettingsViewModel()?.upoPrimaryCavity?.value == resources.getInteger(
                R.integer.temperature_default_value
            )
        ) {
            binding.upperCavitySubtext.text =
                resources.getString(R.string.text_helper_text_default)
        } else {
            val upperText =  appendPlusSignToString(SettingsViewModel.getSettingsViewModel().upoPrimaryCavity.value.toString())+AppConstants.DEGREE_SYMBOL
            binding.upperCavitySubtext.text = upperText

        }
    }

    override fun checkLowerCavityAvailability() {
        binding.lowerCavitySubtext.visibility = VISIBLE
        if (SettingsViewModel.getSettingsViewModel()?.upoSecondaryCavity?.value == resources.getInteger(
                R.integer.temperature_default_value
            )
        ) {
            binding.lowerCavitySubtext.text =
                resources.getString(R.string.text_helper_text_default)
        } else {
            val lowerText = appendPlusSignToString(SettingsViewModel.getSettingsViewModel().upoSecondaryCavity.value.toString())+AppConstants.DEGREE_SYMBOL
            binding.lowerCavitySubtext.text = lowerText

        }
    }

    override fun headerBarSetUp() {
        //set Header Bar
        binding.singleLineHeaderBar.visibility = VISIBLE
        binding.singleLineHeaderBar.setLeftIconVisibility(true)
        binding.singleLineHeaderBar.setLeftIcon(R.drawable.ic_back_arrow)
        binding.singleLineHeaderBar.setOvenCavityIconVisibility(false)
        binding.singleLineHeaderBar.setInfoIconVisibility(false)
        binding.singleLineHeaderBar.setRightIconVisibility(false)
    }

    override fun initViewsDoubleOven() {
        binding.uppperCavityLbl.visibility = VISIBLE
        binding.uppperCavityLbl.text = getString(R.string.cavity_selection_upper_oven_all_caps)
        binding.lowerCavityLbl.visibility = VISIBLE
        binding.lowerCavityLbl.text = getString(R.string.cavity_selection_lower_oven_all_cap)
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
    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        //Meat probe removed dialog shown here and updating the screen time out. Once probe inserted then reset the screen timeout as expected.
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            manageKnobRotation(knobDirection)
        }
    }

    override fun onHMILeftKnobClick() {
        manageKnobClickListener()
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMIRightKnobClick() {
        //Do nothing
    }


    override fun updateTechnicianTextModeText() {
        // Do nothing
    }
}