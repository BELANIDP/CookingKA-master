package android.presenter.fragments.settings

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.model.KnobEntity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.navOptions
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentKnobSwapBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.LogHelper
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.BundleKeys.Companion.BUNDLE_CUSTOMIZE_KNOB_STEPPER_TYPE
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.SharedPreferenceManager

/**
 * File       : android.presenter.fragments.settings.SettingsKnobSwapFragment
 * Brief      : Settings Knob Swap Fragment
 * Author     : Rajendra
 * Created On : 10-10-2024
 */
class SettingsKnobSwapFragment : SuperAbstractTimeoutEnableFragment(), HMIKnobInteractionListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface, MeatProbeUtils.MeatProbeListener {
    private var viewBinding: FragmentKnobSwapBinding? = null

    //Knob Implementation
    private var knobRotationCount = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = inflater.let {
            FragmentKnobSwapBinding.inflate(
                it
            )
        }
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            viewBinding?.btnPrimarySwap?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
        }
    }

    /**
     * Method to manage Views of the Fragment
     */
    private fun manageChildViews() {
        manageHeaderBar()
        manageNavigationButtons()
        updateLeftAndRightKnobBody()
    }


    /**
     * Method to manage header bar View of the Fragment
     */
    private fun manageHeaderBar() {
        viewBinding?.headerBar?.apply {
            setTitleText(R.string.swap_knob_function)
            setLeftIconVisibility(true)
            viewBinding?.headerBar?.setLeftIcon(R.drawable.ic_back_arrow)
            setInfoIconVisibility(false)
            setRightIconVisibility(false)
            setOvenCavityIconVisibility(false)
            setCustomOnClickListener(this@SettingsKnobSwapFragment)
        }
    }

    /**
     * Method to manage swapping the knob functionality
     */
    private fun manageNavigationButtons() {
        viewBinding?.btnPrimarySwap?.setOnClickListener {
            HMILogHelper.Logd(TAG, "swapping the knob functionality ")
            swapLeftAndRightKnobFunctionality()
        }
    }

    private fun swapLeftAndRightKnobFunctionality() {
        HMILogHelper.Logd("Before swap knob value: ${AppConstants.LEFT_KNOB_ID} and ${AppConstants.RIGHT_KNOB_ID}")
        val knobEntity = KnobEntity()
        knobEntity.leftKnob = SharedPreferenceManager.getLeftAndRightKnobPositionIntoPreference().rightKnob
        knobEntity.rightKnob = SharedPreferenceManager.getLeftAndRightKnobPositionIntoPreference().leftKnob
        SharedPreferenceManager.setLeftAndRightKnobPositionIntoPreference(knobEntity)
        HMILogHelper.Logd("after swap knob value: ${AppConstants.LEFT_KNOB_ID} and ${AppConstants.RIGHT_KNOB_ID}")
        updateLeftAndRightKnobBody()
    }

    override fun leftIconOnClick() {
        if (SettingsManagerUtils.isUnboxing) {
            val bundle = Bundle()
            bundle.putInt(
                BUNDLE_CUSTOMIZE_KNOB_STEPPER_TYPE,
                arguments?.getInt(BUNDLE_CUSTOMIZE_KNOB_STEPPER_TYPE) ?: 0
            )
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                NavigationUtils.navigateSafely(it,
                    R.id.action_settingsKnobSwapFragment_to_unboxingApplianceFeaturesInfoFragment,
                    bundle,
                    navOptions {
                        popUpTo(R.id.unboxingApplianceFeaturesInfoFragment) {
                            inclusive = true
                        }
                    })
            }
        } else {
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                NavigationUtils.navigateSafely(
                    it, R.id.action_settingsKnobSwapFragment_to_settingsKnobFragment, null, null
                )
            }
        }
    }

    /**
     * Method to manage left and right knob functionality
     */
    private fun updateLeftAndRightKnobBody() {
        if (AppConstants.LEFT_KNOB_ID == 0) {
            viewBinding?.leftKnobDescription?.text =
                resources.getString(R.string.text_left_knob_description_label)
            viewBinding?.icon40pxSettings?.setBackgroundResource(R.drawable.icon_40px_settings)
            viewBinding?.rightKnobDescription?.text =
                resources.getString(R.string.text_right_knob_description_label)
            viewBinding?.icon40pxAssisted?.setBackgroundResource(R.drawable.icon_40px_assisted)
        } else {
            viewBinding?.leftKnobDescription?.text =
                resources.getString(R.string.text_right_knob_description_label)
            viewBinding?.icon40pxSettings?.setBackgroundResource(R.drawable.icon_40px_assisted)
            viewBinding?.rightKnobDescription?.text =
                resources.getString(R.string.text_left_knob_description_label)
            viewBinding?.icon40pxAssisted?.setBackgroundResource(R.drawable.icon_40px_settings)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        MeatProbeUtils.removeMeatProbeListener()
        viewBinding = null
    }

    override fun onHMILeftKnobClick() {
        handleKnobClick()
    }

    private fun handleKnobClick() {
        viewBinding?.btnPrimarySwap?.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.selector_textview_walnut, null
            )
        }
        viewBinding?.btnPrimarySwap?.callOnClick()
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobClick() {
        if (SettingsManagerUtils.isUnboxing) {
            handleKnobClick()
        }
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (SettingsManagerUtils.isUnboxing) {
            if (knobId == AppConstants.LEFT_KNOB_ID || knobId == AppConstants.RIGHT_KNOB_ID) {
                handleKnobEvent(knobDirection)
            }
        } else {
            if (knobId == AppConstants.LEFT_KNOB_ID) {
                handleKnobEvent(knobDirection)
            }
        }
    }

    private fun handleKnobEvent(knobDirection: String) {
        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.MIN_KNOB_POSITION) knobRotationCount++
        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
        when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> {
                viewBinding?.btnPrimarySwap?.background = resources.let {
                    ResourcesCompat.getDrawable(
                        it, R.drawable.selector_textview_walnut, null
                    )
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        viewBinding?.btnPrimarySwap?.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
    }

    companion object {
        private val TAG: String = SettingsKnobSwapFragment::class.java.simpleName
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
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
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }
}
