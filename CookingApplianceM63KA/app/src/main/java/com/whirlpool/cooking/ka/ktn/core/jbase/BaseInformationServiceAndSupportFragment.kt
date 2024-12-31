/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentInformationServiceAndSupportBinding
import com.whirlpool.hmi.cooking.utils.FaultSubCategory
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsRepository.DemoMode.DEMO_MODE_ENABLED
import com.whirlpool.hmi.settings.SettingsViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AppConstants.IMAGE_SIZE_200
import core.utils.AppConstants.SERVICE_SUPPORT_QR_CODE
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.DoorEventUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils
import core.utils.SharedViewModel
import core.utils.faultcodesutils.FaultDetails
import java.lang.Boolean.TRUE

/**
 * File       : com.whirlpool.cooking.base.AbstractInformationServiceAndSupportFragment
 * Brief      : Abstract class for information fragment with four different text view screen
 */
class BaseInformationServiceAndSupportFragment : SuperAbstractTimeoutEnableFragment(),
    DoorEventUtils.DoorEventListener, HMIExpansionUtils.HMICancelButtonInteractionListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface, MeatProbeUtils.MeatProbeListener,
    HMIKnobInteractionListener {
    private var fragmentInformationServiceAndSupportBinding: FragmentInformationServiceAndSupportBinding? =
        null
    private var faultCode: String = EMPTY_STRING
    private var faultCategory = FaultSubCategory.NOT_APPLICABLE.ordinal
    private var knobRotationCount = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentInformationServiceAndSupportBinding =
            FragmentInformationServiceAndSupportBinding.inflate(inflater, container, false)
        DoorEventUtils.setDoorEventListener(this)
        return fragmentInformationServiceAndSupportBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        initBundle()
        MeatProbeUtils.setMeatProbeListener(this)
        HMIExpansionUtils.setHMICancelButtonInteractionListener(this)
        manageFaultsCategoryChildViews()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            knobRotationCount = 1
            fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.background =
                resources.let {
                    ResourcesCompat.getDrawable(
                        it, R.drawable.selector_textview_walnut, null
                    )
                }
        }
    }

    /**
     * Method to set the view models
     */
    private fun setUpViewModels() {
        fragmentInformationServiceAndSupportBinding?.settingsViewModel =
            SettingsViewModel.getSettingsViewModel()
        fragmentInformationServiceAndSupportBinding?.infoFragment = this
    }

    /**
     * Init bundle
     * fault code - Get the fault code value from bundle
     * faultCategory - Get the faultCategory value from bundle
     *
     */
    private fun initBundle() {
        faultCode = arguments?.getString(BundleKeys.BUNDLE_FAULT_CODE) ?: EMPTY_STRING
        faultCategory = arguments?.getInt(BundleKeys.BUNDLE_FAULT_CATEGORY) ?: 0
    }

    /**
     * Method to set Faults Screen Views.
     */
    private fun manageFaultsCategoryChildViews() {
        val faultDetails = FaultDetails.getInstance(faultCode)
        if (faultCategory == FaultSubCategory.CATEGORY_C.ordinal) {
            HMILogHelper.Logd("HMI_KEY","Base Information Service and Support CATEGORY C Fault")
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_FAULT_BLOCKING)
            setTimeoutApplicable(false)
            fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.visibility = View.GONE
            fragmentInformationServiceAndSupportBinding?.headerBar?.setRightIconVisibility(false)
            fragmentInformationServiceAndSupportBinding?.textViewTitle?.text =
                faultDetails.getFaultName(this)
            fragmentInformationServiceAndSupportBinding?.textViewDescription?.setText(R.string.text_fault_description_service_support)
        } else {
            setTimeoutApplicable(true)
            fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.visibility =
                View.VISIBLE
            fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.setTextButtonText(R.string.text_button_enter_diagnostics)
            fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.setTextButtonTextSize(
                AppConstants.SERVICE_DIAGNOSTICS_TEXT_BUTTON_TEXT_SIZE
            )
            fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.setOnClickListener { navigateToDiagnosticsScreen() }
            fragmentInformationServiceAndSupportBinding?.headerBar?.setRightIcon(R.drawable.ic_close)
            fragmentInformationServiceAndSupportBinding?.headerBar?.setRightIconVisibility(true)
            fragmentInformationServiceAndSupportBinding?.headerBar?.setLeftIcon(R.drawable.ic_back_arrow)
            fragmentInformationServiceAndSupportBinding?.headerBar?.setLeftIconVisibility(true)
            fragmentInformationServiceAndSupportBinding?.headerBar?.setCustomOnClickListener(this)
            fragmentInformationServiceAndSupportBinding?.textViewDescription?.text = getString(
                R.string.text_layout_information_paragraph_qr_code_info,
                SettingsViewModel.getMacAddr(),
                AppConstants.DEMO_CODE
            )
            fragmentInformationServiceAndSupportBinding?.textViewTitle?.setText(R.string.text_layout_scan_qr_code_info_text)
            if(CookingAppUtils.isDemoModeEnabled()){
                fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.setButtonTextColor(R.color.text_button_disabled_grey)
            }
        }
        fragmentInformationServiceAndSupportBinding?.headerBar?.setOvenCavityIconVisibility(false)
        fragmentInformationServiceAndSupportBinding?.headerBar?.setInfoIconVisibility(false)
        fragmentInformationServiceAndSupportBinding?.headerBar?.setTitleText(R.string.service_support)
        fragmentInformationServiceAndSupportBinding?.qrCode?.apply {
            visibility = View.VISIBLE
            CookingAppUtils.getResIdFromResName(
                context,
                SERVICE_SUPPORT_QR_CODE + AppConstants.TEXT_SQUARE,
                AppConstants.RESOURCE_TYPE_DRAWABLE
            ).takeIf { it > 0 }?.let {
                setImageBitmap(context?.resources?.let { it1 ->
                    CookingAppUtils.resizeBitmapUsingMatrix(
                        it1, it, IMAGE_SIZE_200, IMAGE_SIZE_200
                    )
                })
            }
        }
    }

    override fun onDoorEvent(
        cookingViewModel: CookingViewModel?,
        isDoorOpen: Boolean,
        ovenType: Int
    ) {
        // do nothing
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

    override fun rightIconOnClick() {
        CookingAppUtils.setNavigatedFrom(EMPTY_STRING)
        if (CookingAppUtils.isSelfCleanFlow() && TRUE == CookingViewModelFactory.getInScopeViewModel().doorLockState.value) {
            navigateSafely(this, R.id.action_goToSelfCleanStatus, null, null)
        } else {
            CookingAppUtils.navigateToStatusOrClockScreen(this)
        }
    }

    override fun leftIconOnClick() {
        val navId : Int = if (CookingAppUtils.getNavigatedFrom() == AppConstants.SETTINGLANDING_FRAGMENT) {
            R.id.action_fragment_error_screen_to_settingsLandingFragment
        } else {
            R.id.action_fragment_error_screen_to_settingsInfoFragment
        }
        CookingAppUtils.setNavigatedFrom(EMPTY_STRING)
        navigateSafely(this, navId, null, null)
    }

    /**
     * update the fault code and fault category text
     */
    fun updateFaultValues(bundle: Bundle?) {
        if (bundle != null) {
            faultCode = bundle.getString(BundleKeys.BUNDLE_FAULT_CODE) ?: EMPTY_STRING
            faultCategory = bundle.getInt(BundleKeys.BUNDLE_FAULT_CATEGORY)
            manageFaultsCategoryChildViews()
        }

    }

    /**
     * Entry to diagnostics screen
     */
    private fun navigateToDiagnosticsScreen() {
        if (CookingAppUtils.isAnyCycleRunning() && !CookingAppUtils.isDemoModeEnabled()) {
            HMILogHelper.Logd("Cooking cycle is running")
            AudioManagerUtils.playOneShotSound(
                view?.context, R.raw.invalid_press,
                AudioManager.STREAM_SYSTEM, true, 0, 1
            )
            //TODO:showing this popup for now once GCD confirm the popup content
            PopUpBuilderUtils.showOtherFeatureRunningPopup(this@BaseInformationServiceAndSupportFragment)
        } else if (SettingsViewModel.getSettingsViewModel().demoMode.value == DEMO_MODE_ENABLED) {
            HMILogHelper.Logd("Demo Mode is Active  - Feature unavailable")
            AudioManagerUtils.playOneShotSound(
                view?.context, R.raw.invalid_press,
                AudioManager.STREAM_SYSTEM, true, 0, 1
            )
            //TODO:showing this popup for now once GCD confirm the popup content
            fragmentInformationServiceAndSupportBinding?.drwawerbar?.isVisible = true
            fragmentInformationServiceAndSupportBinding?.drwawerbar?.showNotification(getString(R.string.text_feature_unavailable_demo_mode), DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT,
                fragmentInformationServiceAndSupportBinding?.drwawerbar)
        } else {
            HMILogHelper.Logd("Navigate To Service And Diagnostic page")
            AudioManagerUtils.playOneShotSound(
                view?.context, R.raw.button_press,
                AudioManager.STREAM_SYSTEM, true, 0, 1
            )
            navigateSafely(this, R.id.action_go_to_diagnostics, null, null)
        }
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

    override fun onDestroyView() {
        MeatProbeUtils.removeMeatProbeListener()
        DoorEventUtils.removeDoorEventListener(this)
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        HMIExpansionUtils.removeHMICancelButtonInteractionListener(this)
        fragmentInformationServiceAndSupportBinding = null
        super.onDestroyView()
    }

    override fun onHMILeftKnobClick() {
        HMILogHelper.Logd("Unboxing", "Unboxing onHMILeftKnobClick")
        fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.background =
            resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
        fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.callOnClick()
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobClick() {
       // Do nothing
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
        HMILogHelper.Logd("Unboxing", "Unboxing onKnobRotateEvent")
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.MIN_KNOB_POSITION) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.background =
                        resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        fragmentInformationServiceAndSupportBinding?.enterDiagnosticsBtn?.background = null
        knobRotationCount = 0
    }
}

