/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.demo

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentDemoModeLandingBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils
import core.utils.SharedViewModel
import core.utils.ToastUtils
import java.lang.Boolean.TRUE

/**
 * File       : com.whirlpool.cooking.base.DemoModeLandingFragment
 * Brief      : Abstract class for information fragment with four different text view screen
 */
class DemoModeLandingFragment : Fragment(), HMIKnobInteractionListener,
HMIExpansionUtils.HMICancelButtonInteractionListener {
    private var viewBinding: FragmentDemoModeLandingBinding? =
        null
    //Knob Implementation
    private var knobRotationCount = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding =
            FragmentDemoModeLandingBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMILogHelper.Logd("HMI_KEY","Demo Mode Landing Fragment")
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_DEMO_MODE_LANDING)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_DEMO_MODE_LANDING)
        setUpViewModels()
        HMIExpansionUtils.setHMICancelButtonInteractionListener(this)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        observeKnobBackTrace()
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            knobRotationCount = 1
            viewBinding?.seeVideos?.background = resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
        }
    }

    private fun observeKnobBackTrace() {
        SharedViewModel.getSharedViewModel(this.requireActivity()).isNavigatedFromKnobClick()
            .observe(viewLifecycleOwner) { value ->
                value?.let { navigated ->
                    if (navigated && KnobNavigationUtils.knobBackTrace) {
                        // Log the last action if available
                        HMILogHelper.Logd("last saved action: ${KnobNavigationUtils.lastTimeSelectedData()}")
                        KnobNavigationUtils.knobBackTrace = false
                        knobRotationCount = 1
                        viewBinding?.seeVideos?.background = resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                        KnobNavigationUtils.removeLastAction()
                    } else {
                        HMILogHelper.Logd("livedata observer: not navigated form Knob")
                    }
                }
            }
    }

    /**
     * Method to set the view models
     */
    private fun setUpViewModels() {
        viewBinding?.exitDemo?.setOnClickListener {
            exitDemo()
        }
        viewBinding?.exitDemo1?.setOnClickListener {
            exitDemo()
        }
        viewBinding?.seeVideos?.setOnClickListener {
            seeVideoDemo()
        }
        viewBinding?.exploreProduct?.setOnClickListener {
            exploreProductDemo()
        }
    }

    private fun seeVideoDemo() {
        navigateSafely(this, R.id.action_demoModeLandingScreen_to_demoModeSeeVideoListFragment, null, null)
    }

    private fun exploreProductDemo() {
        navigateSafely(this, R.id.global_action_to_clockScreen, null, null)
    }

    private fun exitDemo() {
        CookingAppUtils.setNavigatedFrom(AppConstants.DEMOLANDING_FRAGMENT)
        PopUpBuilderUtils.demoModeExitInstructionPopUp(this)
    }


    override fun onHMICancelButtonInteraction() {
        val sharedViewModel: SharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        if (!sharedViewModel.isApplianceInAOrCCategoryFault()) {
            if (CookingAppUtils.isSelfCleanFlow() && TRUE == CookingViewModelFactory.getInScopeViewModel().doorLockState.value) {
                navigateSafely(this, R.id.action_goToSelfCleanStatus, null, null)
            } else {
                CookingAppUtils.navigateToStatusOrClockScreen(this)
            }
        }
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMICancelButtonInteractionListener(this)
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        viewBinding = null
        super.onDestroyView()
    }

    override fun onHMILeftKnobClick() {
        HMILogHelper.Logd("onHMILeftKnobClick")
        when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> {
                viewBinding?.seeVideos?.callOnClick()
            }

            AppConstants.KNOB_COUNTER_TWO -> {
                viewBinding?.exploreProduct?.callOnClick()
            }
        }
    }

    override fun onHMILongLeftKnobPress() {
        //Do Nothing
    }

    override fun onHMIRightKnobClick() {
        //Do Nothing
    }

    override fun onHMILongRightKnobPress() {
        //Do Nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do Nothing
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        HMILogHelper.Logd("onKnobRotateEvent")
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    viewBinding?.exploreProduct?.background = null
                    viewBinding?.seeVideos?.background = resources.let {
                        ResourcesCompat.getDrawable(
                            it, R.drawable.selector_textview_walnut, null
                        )
                    }
                }

                AppConstants.KNOB_COUNTER_TWO -> {
                    viewBinding?.seeVideos?.background = null
                    viewBinding?.exploreProduct?.background = resources.let {
                        ResourcesCompat.getDrawable(
                            it, R.drawable.selector_textview_walnut, null
                        )
                    }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        viewBinding?.exploreProduct?.background = null
        viewBinding?.seeVideos?.background = null
    }
}

