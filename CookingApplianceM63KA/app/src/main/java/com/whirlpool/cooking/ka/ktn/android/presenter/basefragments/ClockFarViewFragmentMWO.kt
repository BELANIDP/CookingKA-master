package android.presenter.basefragments

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentClockFarViewBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import core.utils.AppConstants
import core.utils.CavityLightUtils
import core.utils.CookingAppUtils
import core.utils.DoorEventUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.LowPowerModeUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.gone
import core.utils.invisible
import core.utils.visible
import java.time.LocalDate
import java.util.Locale

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.basefragments.ClockFarViewFragmentMWO
 * Description : This fragment represents the Clock Far View, which is a low-power mode display for the clock.
 *               It handles user interactions, low-power state transitions, and updates the clock and cavity light states.
 * Brief       : Clock Far View Implemention
 * Author      : ANISH WAGH
 * Created On  : 12/11/2024
 */

class ClockFarViewFragmentMWO : Fragment(),
    HMIExpansionUtils.UserInteractionListener, HMIExpansionUtils.HMICancelButtonInteractionListener, HMIKnobInteractionListener , HMIExpansionUtils.HMIHomeButtonInteractionListener{
    private var fragmentClockFarViewBinding: FragmentClockFarViewBinding? = null
    private var initialPrimaryCavityLightState: Boolean? = null
    private val timeoutViewModel: TimeoutViewModel by viewModels()
    private var handler: Handler = Handler(Looper.getMainLooper())
    private var primaryCookingViewModel: CookingViewModel? = null
    private var secondaryCookingViewModel: CookingViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentClockFarViewBinding = FragmentClockFarViewBinding.inflate(inflater)
        return fragmentClockFarViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMILogHelper.Logd(AppConstants.SLEEP_MODE,"MWO_FAR_VIEW::onViewCreated")
        initCavityLight()
        observeOnKitchenTimerViewModel()
        initClockTime()
        updateDate()
        registerClickEvent()
        updateLiveOTABusyState()
        manageChildViews()
        // Initialize the ViewModel and timer setup
        if (!CookingAppUtils.isDemoModeEnabled()) initEnterSleepModeTimeout()
    }
     fun manageChildViews() {
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
    }
    /**
     * Init cavity light state
     */
    private fun initCavityLight() {
        //Update initial state of cavity lights
        initialPrimaryCavityLightState = CavityLightUtils.getPrimaryCavityLightState()
        CavityLightUtils.setPrimaryCavityLightState(false)
    }

    /**
     * Init clock settings
     */
    private fun initClockTime() {
        val timeFormat: SettingsManagerUtils.TimeFormatSettings =
            SettingsManagerUtils.getTimeFormat()
        fragmentClockFarViewBinding?.textFarViewClockDigitalClockTime?.set12HourTimeFormat(
            timeFormat == SettingsManagerUtils.TimeFormatSettings.H_12
        )
    }
    /**
     * Init Date
     */
    private fun updateDate() {
        val timeFormat: SettingsManagerUtils.TimeFormatSettings = SettingsManagerUtils.getTimeFormat()
        fragmentClockFarViewBinding?.textFarViewClockDigitalClockTime?.set12HourTimeFormat(timeFormat == SettingsManagerUtils.TimeFormatSettings.H_12)
        val today = LocalDate.now()
        val dateString = getString(
            R.string.text_clock_day_date, resources.getString(
                CookingAppUtils.getResIdFromResName(
                    requireContext(), today.dayOfWeek.toString().lowercase(
                        Locale.getDefault()
                    ), AppConstants.RESOURCE_TYPE_STRING
                )
            ), resources.getString(
                CookingAppUtils.getResIdFromResName(
                    requireContext(), today.month.toString().lowercase(
                        Locale.getDefault()
                    ), AppConstants.RESOURCE_TYPE_STRING
                )
            ), today.dayOfMonth
        )
        fragmentClockFarViewBinding?.textFarViewClockDigitalClockDay?.text = dateString
    }
    /**
     * Function to come out of sleep mode on touch selection of Clock screen
     **/
    private fun manageClockTouchButtonPress() {
        if (SettingsManagerUtils.isApplianceProvisioned() && LowPowerModeUtils.isApplianceInNotGoingToSleepConnected()) {
            HMILogHelper.Logd(AppConstants.SLEEP_MODE, "Sleep Mode::Appliance provisioned.")
            if (SettingsManagerUtils.isPreviouslyInSleepMode()) {
                LowPowerModeUtils.wakeUpConnectedMode()
            } else {
                HMILogHelper.Logi(AppConstants.SLEEP_MODE, "Sleep Mode::Appliance was already Waked Up.")
                CookingAppUtils.openCavitySelectionScreen(this)
            }
        } else if (!SettingsManagerUtils.isApplianceProvisioned() && LowPowerModeUtils.isApplianceAwakeInNonConnected()) {
            HMILogHelper.Logd(AppConstants.SLEEP_MODE, "came inside non connected")
            SettingsManagerUtils.setPreviouslyInSleepMode(false)
            CookingAppUtils.openCavitySelectionScreen(this)
        }
    }

    /**
     * Timer to check appliance state before entering in sleep mode
     **/
    private fun initEnterSleepModeTimeout() {
        timeoutViewModel.timeoutCallback?.observe(
            viewLifecycleOwner
        ) { timeoutStatesEnum: TimeoutViewModel.TimeoutStatesEnum ->
            HMILogHelper.Logd("TimeoutCallback: " + timeoutStatesEnum.name)
            if (timeoutStatesEnum == TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                if (LowPowerModeUtils.isSafeToEnterLowPower()) {
                    LowPowerModeUtils.enterSleepMode()
                } else {
                    HMILogHelper.Logd(
                        AppConstants.SLEEP_MODE,
                        "Sleep Mode::onFinish nearViewTimeout detected->> RestartingTimer"
                    )
                    timeoutViewModel.restart()
                    return@observe
                }
            }
        }
        timeoutViewModel.setTimeout(
            resources.getInteger(R.integer.duration_min)
        )
    }

    /**
     * Register click event
     */
    private fun registerClickEvent() {
        fragmentClockFarViewBinding?.root?.setOnClickListener {
            //check if in low power
            manageClockTouchButtonPress()
            onUserInteraction()
        }
    }

    override fun onResume() {
        //Enabled and Disable HMI key after recipe selected
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN)
        //Register observer
        registerObserver()

        HMILogHelper.Logd(AppConstants.SLEEP_MODE, "Sleep Mode::On Resume Called")
        if (!SettingsManagerUtils.isApplianceProvisioned()) {
            if (SettingsManagerUtils.isPreviouslyInSleepMode()) {
                LowPowerModeUtils.wakeUpNonConnectedMode()
            } else {
                HMILogHelper.Logi(AppConstants.SLEEP_MODE, "Sleep Mode::Appliance was already Waked Up.")
            }
        }
        super.onResume()
        // Restart the timer if it was stopped
        timeoutViewModel.restart()
    }

    override fun onPause() {
        super.onPause()
        removeObserver()
    }

    override fun onStart() {
        super.onStart()
        // Ensure timer is set when fragment becomes visible
        if (!CookingAppUtils.isDemoModeEnabled())
            timeoutViewModel.setTimeout(resources.getInteger(R.integer.duration_min))
    }
    override fun onStop() {
        HMILogHelper.Logd(AppConstants.SLEEP_MODE, "Sleep Mode::On Stop Called")
        super.onStop()
        HMILogHelper.Logd("Clock Far OTA","On stop")
        removeApplianceBusyObservers()
        updateOTABusyStateTrue()
        timeoutViewModel.stop()
    }

    /**
     * side keys and handling in clock far view
     */
    private fun buttonClickHandling() {
        HMILogHelper.Logd(AppConstants.SLEEP_MODE, "onHmi buttonClickHandling")
        if (SettingsManagerUtils.isPreviouslyInSleepMode()) {
            if (SettingsManagerUtils.isApplianceProvisioned() && LowPowerModeUtils.isApplianceAwakeInConnected()) {
                SettingsManagerUtils.setPreviouslyInSleepMode(false)
            } else if (!SettingsManagerUtils.isApplianceProvisioned() && LowPowerModeUtils.isApplianceAwakeInNonConnected()) {
                SettingsManagerUtils.setPreviouslyInSleepMode(false)
            }
        }
    }
    override fun onUserInteraction() {
        //check if in low power
        timeoutViewModel.restart()
        if(!ScrollDialogPopupBuilder.isAnyPopupShowing()) {
            NavigationUtils.navigateSafely(
                this, R.id.global_action_to_clockScreen, null, null
            )
        }
    }

    override fun onHMICancelButtonInteraction() {
        //check if in low power
        onUserInteraction()
        buttonClickHandling()
    }

    override fun onHMIHomeButtonInteraction() {
        buttonClickHandling()
    }


    override fun onHMILeftKnobClick() {
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        if (timeInterval == AppConstants.TIME_INTERVAL_JET_START) {
            HMILogHelper.Logd(tag, "Initiating JET start from ClockFarViewFragmentMWO")
            PopUpBuilderUtils.jetStartMWOBakeRecipe(
                this
            )
        }
    }
    override fun onDestroyView() {
        removeObserver()
        PopUpBuilderUtils.dismissPopupByTag(activity?.supportFragmentManager, AppConstants.POPUP_TAG_JET_START)
        super.onDestroyView()
    }
    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMIRightKnobClick() {
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
    }
    override fun onKnobSelectionTimeout(knobId: Int) {
    }

     fun observeDoorInteraction(cookingViewModel: CookingViewModel) {
        cookingViewModel.doorState.observe(this) { doorState ->
            HMILogHelper.Logd(
                tag,
                "current door: $doorState last door status = primary: ${DoorEventUtils.doorLastOpenStateForCavity[0]}}"
            )
            if (SettingsManagerUtils.isApplianceProvisioned()) {
                HMILogHelper.Logd(
                    AppConstants.SLEEP_MODE, "Sleep Mode::observeDoorInteraction"
                )
                if (!LowPowerModeUtils.isApplianceAwakeInConnected()) LowPowerModeUtils.wakeUpFromSleep()
                manageDoorInteraction(doorState, cookingViewModel)
            } else if (LowPowerModeUtils.isApplianceAwakeInNonConnected()) {
                HMILogHelper.Logd(
                    AppConstants.SLEEP_MODE,
                    "Sleep Mode::observeDoorInteraction isApplianceAwakeInNonConnected()"
                )
                manageDoorInteraction(doorState, cookingViewModel)
            }
        }
    }
     private fun manageDoorInteraction(doorState: Boolean, cookingViewModel: CookingViewModel){
        val cavity = if (cookingViewModel.isPrimaryCavity) 0 else 1
        if (!CookingAppUtils.isSabbathMode() && doorState && doorState != DoorEventUtils.doorLastOpenStateForCavity[cavity]) {
            HMILogHelper.Logd(
                tag,
                "door opened for ${cookingViewModel.cavityName.value}, moving to recipe selection fragment"
            )
            if (cookingViewModel.isPrimaryCavity) {
                NavigationUtils.navigateToUpperRecipeSelection(this)
            } else {
                NavigationUtils.navigateToLowerRecipeSelection(this)
            }
            CookingAppUtils.manageHMIPanelLights(
                homeLight = true,
                cancelLight = true,
                cleanLight = false
            )
        }
        //only update after the fragment is visible
        if (isVisible) {
            HMILogHelper.Logd(
                tag,
                "current door: $doorState updating last door state of ${cookingViewModel.cavityName.value}"
            )
            DoorEventUtils.doorLastOpenStateForCavity[cavity] = doorState
        }
    }
    /**
     * Register Knob,Meat Probe, User Interaction etc Listener
     */
    private fun registerObserver() {
        HMIExpansionUtils.setHMIHomeButtonInteractionListener(this)
        HMIExpansionUtils.setHMICancelButtonInteractionListener(this)
        HMIExpansionUtils.setFragmentUserInteractionListener(this)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
    }

    /**
     * updating Clock view with kitchen time remaining value and replacing clock text
     * if any KitchenTimer is running and if multiple timer is running then show shortest
     */
    private fun observeOnKitchenTimerViewModel() {
        if (KitchenTimerVMFactory.isAnyKitchenTimerRunning()) {
            HMILogHelper.Logd(tag, "Kitchen Timer is running, replacing clockView to kitchenTimerView")
            val runningKitchenTimer = KitchenTimerVMFactory.getKitchenTimerWithLeastRemainingTime()
            runningKitchenTimer?.timerStatus?.observe(viewLifecycleOwner) { timerStatus ->
                if(timerStatus == KitchenTimerViewModel.TimerStatus.RUNNING){
                    //if kt is running update the view and do not got forward
                    viewUpdateOnKitchenTimer(true)
                    return@observe
                }
                if(timerStatus == KitchenTimerViewModel.TimerStatus.COMPLETED){
                    //show kitchen timer completed popup
                    PopUpBuilderUtils.kitchenTimerCompletedPopup(this, runningKitchenTimer)
                }
                //to check on any other kitchen timer is running and update the view accordingly
                if (KitchenTimerVMFactory.isAnyKitchenTimerRunning()){
                    observeOnKitchenTimerViewModel()
                }
                else viewUpdateOnKitchenTimer(false)
            }
        }
    }

    /**
     * show/hide clock textView vs Kitchen timer textView
     *
     * @param isRunning true if kitchen timer is running false otherwise
     */
    private fun viewUpdateOnKitchenTimer(isRunning: Boolean) {
        HMILogHelper.Logd(tag, "Clock Text Kitchen Timer isRunning =$isRunning")
        handler.removeCallbacksAndMessages(null)
        when {
            isRunning && isAdded -> {
                fragmentClockFarViewBinding?.apply {
                    textFarViewClockDigitalClockTime.gone()
                    textFarViewClockDigitalClockDay.gone()
                    textViewKitchenTimerRunningTextFarViewClock.visible()
                    iconKitchenTimerFarViewClock.visible()
                }
                handler.post(kitchenTimerRunnable())
            }
            else -> {
                fragmentClockFarViewBinding?.apply {
                    textFarViewClockDigitalClockTime.visible()
                    textViewKitchenTimerRunningTextFarViewClock.invisible()
                    iconKitchenTimerFarViewClock.invisible()
                    textFarViewClockDigitalClockDay.visible()
                }
            }
        }
    }
    private fun kitchenTimerRunnable(): Runnable {
        return Runnable {
            if(isAdded) {
                HMILogHelper.Logi(tag, "KitchenTimer: runnable active")
                val runningKitchenTimer =
                    KitchenTimerVMFactory.getKitchenTimerWithLeastRemainingTime()
                val ktText = KitchenTimerUtils.convertTimeRemainingToShortString(
                    runningKitchenTimer?.remainingTime?.value?.toLong() ?: 0
                )
                HMILogHelper.Logd(tag, "Clock Text Kitchen Timer text =$ktText")
                fragmentClockFarViewBinding?.textViewKitchenTimerRunningTextFarViewClock?.text =
                    ktText
                handler.postDelayed(
                    kitchenTimerRunnable(),
                    resources.getInteger(R.integer.ms_1000).toLong()
                )
            }
        }
    }

    /**
     * Removed Knob,Meat Probe, User Interaction etc Listener
     */
    private fun removeObserver() {
        HMIExpansionUtils.removeHMIHomeButtonInteractionListener(this)
        HMIExpansionUtils.removeHMICancelButtonInteractionListener(this)
        HMIExpansionUtils.removeFragmentUserInteractionListener(this)
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }

    /**
     * This function will start observing prerequisites for OTA
     */
    private fun updateLiveOTABusyState() {
        HMILogHelper.Logd("Clock Far OTA", "OTA : started observing prerequisites for OTA")
        //Update OTA busy state live for Sabbath mode
        SettingsViewModel.getSettingsViewModel().sabbathMode.observe(viewLifecycleOwner) {
            HMILogHelper.Logd("Clock Far OTA", "OTA : Sabbath state : $it")
            CookingAppUtils.setApplianceOtaState()
        }
        //Update OTA busy state live for kitchen timer
        KitchenTimerVMFactory.getKitchenTimerViewModels()
            ?.forEach { kitchenTimerViewModel: KitchenTimerViewModel? ->
                kitchenTimerViewModel?.timerStatus?.observe(viewLifecycleOwner) {
                    HMILogHelper.Logd("Clock Far OTA", "OTA : Kitchen Timer state : $it")
                    if(it.equals(KitchenTimerViewModel.TimerStatus.IDLE)) {
                        CookingAppUtils.setApplianceOtaState()
                    }
                }
            }
        when (CookingViewModelFactory.getProductVariantEnum()) {
            //Update OTA busy state live for primary Cooling fan and Oven temperature
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN, CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                CookingViewModelFactory.getPrimaryCavityViewModel().coolingFanState.observe(viewLifecycleOwner) {
                    HMILogHelper.Logd("Clock Far OTA", "OTA : primary Cavity Fan state : $it")
                    CookingAppUtils.setApplianceOtaState()
                }
                CookingViewModelFactory.getPrimaryCavityViewModel().ovenTemperatureInCelsius
                    .observe(viewLifecycleOwner) {
                        HMILogHelper.Logd("Clock Far OTA", "OTA : primary ovenTemperatureInCelsius : $it")
                        CookingAppUtils.setApplianceOtaState()
                    }
            }

            //Update OTA busy state live for primary and secondary Cooling fan and Oven temperature
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN, CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                CookingViewModelFactory.getPrimaryCavityViewModel().coolingFanState.observe(viewLifecycleOwner) {
                    HMILogHelper.Logd("Clock Far OTA", "OTA : primary Cavity Fan state : $it")
                    CookingAppUtils.setApplianceOtaState()
                }
                CookingViewModelFactory.getSecondaryCavityViewModel().coolingFanState.observe(viewLifecycleOwner) {
                    HMILogHelper.Logd("Clock Far OTA", "OTA : Secondary Cavity Fan state : $it")
                    CookingAppUtils.setApplianceOtaState()
                }
                CookingViewModelFactory.getPrimaryCavityViewModel().ovenTemperatureInCelsius
                    .observe(viewLifecycleOwner) {
                        HMILogHelper.Logd("Clock Far OTA", "OTA : primary ovenTemperatureInCelsius : $it")
                        CookingAppUtils.setApplianceOtaState()
                    }
                CookingViewModelFactory.getSecondaryCavityViewModel().ovenTemperatureInCelsius
                    .observe(viewLifecycleOwner) {
                        HMILogHelper.Logd("Clock Far OTA", "OTA : Secondary ovenTemperatureInCelsius : $it")
                        CookingAppUtils.setApplianceOtaState()
                    }
            }

            else -> {}
        }

    }

    /**
     * Remove all OTA observer
     */
    private fun removeApplianceBusyObservers() {
        HMILogHelper.Logd("Clock Far OTA", "OTA : removed ota Observer")
        primaryCookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
        secondaryCookingViewModel = CookingAppUtils.getSecondaryCookingViewModel()
        if (primaryCookingViewModel != null) {
            primaryCookingViewModel?.ovenTemperatureInCelsius?.removeObservers(viewLifecycleOwner)
            primaryCookingViewModel?.coolingFanState?.removeObservers(viewLifecycleOwner)
        }
        if (secondaryCookingViewModel != null) {
            secondaryCookingViewModel?.ovenTemperatureInCelsius?.removeObservers(viewLifecycleOwner)
            secondaryCookingViewModel?.coolingFanState?.removeObservers(viewLifecycleOwner)
        }
        if (SettingsViewModel.getSettingsViewModel().sabbathMode != null) SettingsViewModel.getSettingsViewModel().sabbathMode.removeObservers(
            viewLifecycleOwner
        )
        if (KitchenTimerVMFactory.getKitchenTimerViewModels() != null) {
            KitchenTimerVMFactory.getKitchenTimerViewModels()
                ?.forEach { kitchenTimerViewModel: KitchenTimerViewModel? ->
                    kitchenTimerViewModel?.timerStatus?.removeObservers(viewLifecycleOwner)
                }
        }
    }

    private fun updateOTABusyStateTrue() {
        //In case of OTA is Running do not set appliance is busy
        if (!OTAVMFactory.getOTAViewModel().isOTARunning) {
            HMILogHelper.Logd("Clock Far OTA","OTA : on user interaction and OTA is not running, setting appliance -> busy")
            OTAVMFactory.getOTAViewModel().setApplianceBusyState(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HMILogHelper.Logd("HMI_KEY", "Clock --> Disable and enable buttons")
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN)
        HMILogHelper.Logd(AppConstants.SLEEP_MODE, "Sleep Mode::On Destroy Called")
        //Restore initial state of cavity lights
        CavityLightUtils.setPrimaryCavityLightState(initialPrimaryCavityLightState ?: false)
        //Remove observer
        removeObserver()
    }
}

