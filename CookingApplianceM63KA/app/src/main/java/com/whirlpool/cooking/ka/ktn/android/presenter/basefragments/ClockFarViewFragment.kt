package android.presenter.basefragments

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import core.utils.CookingAppUtils.Companion.setNavigatedFrom
import core.utils.DoorEventUtils
import core.utils.DoorEventUtils.Companion.DOOR_STATE
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.gone
import core.utils.invisible
import core.utils.visible
import java.time.LocalDate
import java.util.Locale

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.basefragments.ClockFarViewFragment
 * Brief       : Clock Far View Implemention
 * Author      : ANISH WAGH
 * Created On  : 12/11/2024
 */

class ClockFarViewFragment : Fragment(),
    HMIExpansionUtils.UserInteractionListener, HMIExpansionUtils.HMICancelButtonInteractionListener,
    MeatProbeUtils.MeatProbeListener, HMIKnobInteractionListener{
    private var _binding: FragmentClockFarViewBinding? = null
    private var primaryCookingViewModel: CookingViewModel? = null
    private var secondaryCookingViewModel: CookingViewModel? = null
    private val fragmentClockFarViewBinding get() = _binding!!
    private var initialPrimaryCavityLightState: Boolean? = null
    private var initialSecondaryCavityLightState: Boolean? = null
    private var handler: Handler = Handler(Looper.getMainLooper())
    //this array variable keeps track of door event based on door event
    private val doorStateToAction = arrayOf(DOOR_STATE.INITIAL, DOOR_STATE.INITIAL)
    private val timeoutViewModel: TimeoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentClockFarViewBinding.inflate(inflater)
        return fragmentClockFarViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateLiveOTABusyState()
        initCavityLight()
        observeOnKitchenTimerViewModel()
        initClockTime()
        updateDate()
        registerClickEvent()
        initEnterSleepModeTimeout()
    }

    /**
     * Init cavity light state
     */
    private fun initCavityLight() {
        //Update initial state of cavity lights
        initialPrimaryCavityLightState = CavityLightUtils.getPrimaryCavityLightState()
        CavityLightUtils.setPrimaryCavityLightState(false)
        if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO ||
            CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
        ){
            initialSecondaryCavityLightState = CavityLightUtils.getSecondaryCavityLightState()
            CavityLightUtils.setSecondaryCavityLightState(false)
        }
    }

    /**
     * Init clock settings
     */
    private fun initClockTime() {
        val timeFormat: SettingsManagerUtils.TimeFormatSettings =
            SettingsManagerUtils.getTimeFormat()
        fragmentClockFarViewBinding.textFarViewClockDigitalClockTime.set12HourTimeFormat(
            timeFormat == SettingsManagerUtils.TimeFormatSettings.H_12
        )
    }
    /**
     * Init Date
     */
    private fun updateDate() {
        val timeFormat: SettingsManagerUtils.TimeFormatSettings =
            SettingsManagerUtils.getTimeFormat()
        fragmentClockFarViewBinding.textFarViewClockDigitalClockTime.set12HourTimeFormat(timeFormat == SettingsManagerUtils.TimeFormatSettings.H_12)
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
        fragmentClockFarViewBinding.textFarViewClockDigitalClockDay.text = dateString
    }
    /**
     * Register click event
     */
    private fun registerClickEvent() {
        fragmentClockFarViewBinding.root.setOnClickListener {
            onUserInteraction()
        }
    }

    override fun onResume() {
        super.onResume()
        //Enabled and Disable HMI key after recipe selected
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN)
        //Register observer
        registerObserver()
    }


    override fun onUserInteraction() {
        //do not navigate if any popup is visible ex JET start flow will not work if door is open or probe is connected
        if(!ScrollDialogPopupBuilder.isAnyPopupShowing()) {
            NavigationUtils.navigateSafely(
                this, R.id.global_action_to_clockScreen, null, null
            )
        }
    }

    override fun onHMICancelButtonInteraction() {
        onUserInteraction()
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        onUserInteraction()
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
    }

    override fun onHMILeftKnobClick() {
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        if (timeInterval == AppConstants.TIME_INTERVAL_JET_START) {
            HMILogHelper.Logd(tag, "Initiating JET start from ClockFarView")
            PopUpBuilderUtils.jetStartMWOBakeRecipe(
                this
            )
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMIRightKnobClick() {
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
    }
    override fun onKnobSelectionTimeout(knobId: Int) {
    }
    /**
     * Register Knob,Meat Probe, User Interaction etc Listener
     */
    private fun registerObserver() {
        HMIExpansionUtils.setHMICancelButtonInteractionListener(this)
        HMIExpansionUtils.setFragmentUserInteractionListener(this)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        MeatProbeUtils.setMeatProbeListener(this)
    }

    /**
     * Removed Knob,Meat Probe, User Interaction etc Listener
     */
    private fun removeObserver() {
        HMIExpansionUtils.removeHMICancelButtonInteractionListener(this)
        HMIExpansionUtils.removeFragmentUserInteractionListener(this)
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        MeatProbeUtils.removeMeatProbeListener()
    }

    override fun onDestroyView() {
        PopUpBuilderUtils.dismissPopupByTag(activity?.supportFragmentManager, AppConstants.POPUP_TAG_JET_START)
        removeObserver()
        _binding = null
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        removeObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        //Restore initial state of cavity lights
        CavityLightUtils.setPrimaryCavityLightState(initialPrimaryCavityLightState ?: false)
        if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO ||
            CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
        ) {
            CavityLightUtils.setSecondaryCavityLightState(false)
        }
        //Remove observer
        removeObserver()
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
                fragmentClockFarViewBinding.apply {
                    textFarViewClockDigitalClockTime.gone()
                    textFarViewClockDigitalClockDay.gone()
                    textViewKitchenTimerRunningTextFarViewClock.visible()
                    iconKitchenTimerFarViewClock.visible()
                }
                handler.post(kitchenTimerRunnable())
            }
            else -> {
                fragmentClockFarViewBinding.apply {
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
                fragmentClockFarViewBinding.textViewKitchenTimerRunningTextFarViewClock.text =
                    ktText
                handler.postDelayed(
                    kitchenTimerRunnable(),
                    resources.getInteger(R.integer.ms_1000).toLong()
                )
            }
        }
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

    override fun onStop() {
        super.onStop()
        HMILogHelper.Logd("Clock Far OTA","On stop")
        removeApplianceBusyObservers()
        updateOTABusyStateTrue()
    }

    override fun onStart() {
        super.onStart()
        if(CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN || CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO){
            observeDoorInteraction(CookingViewModelFactory.getSecondaryCavityViewModel())
        }
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
    }
    private fun observeDoorInteraction(cookingViewModel: CookingViewModel) {
        //always update door action to initial before observing, because upon registering observer it would give the value of current door which we want to ignore
        //as door have to open and close, the initial values are ignored as event triggered
        doorStateToAction[if(cookingViewModel.isPrimaryCavity) 0 else 1] =  DOOR_STATE.INITIAL
        cookingViewModel.doorState.observe(this){ doorState ->
            if (SettingsViewModel.getSettingsViewModel().controlLock.value == true ){
                if (doorState) {
                    NavigationUtils.navigateSafely(
                        this,
                        R.id.action_to_controlUnlockFragment,
                        null,
                        null
                    )
                }
            } else {
                setNavigatedFrom(AppConstants.CLOCK_FAR_OR_VIDEO_VIEW_FRAGMENT)
                doorStateToAction[if(cookingViewModel.isPrimaryCavity) 0 else 1] = DoorEventUtils.manageDoorInteractionWithRecipeSelection(this, doorState, cookingViewModel, doorStateToAction)
            }
        }
    }

    private fun initEnterSleepModeTimeout() {
        timeoutViewModel.timeoutCallback?.observe(
            viewLifecycleOwner
        ) { timeoutStatesEnum: TimeoutViewModel.TimeoutStatesEnum ->
            HMILogHelper.Logd("TimeoutCallback: " + timeoutStatesEnum.name)
            if (timeoutStatesEnum == TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_ELAPSED
                && !KitchenTimerVMFactory.isAnyKitchenTimerRunning()
                && !CookingAppUtils.isSabbathMode()) {
                val clockAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_out)

                // Set up a listener to navigate after the animation ends
                val animationListener = object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        HMILogHelper.Logd("Animation---->", "onAnimationEnd")
                        NavigationUtils.navigateSafely(
                            requireParentFragment(),
                            R.id.action_clockFragment_to_showInactivityVideoFragment,
                            null,
                            null)
                        }

                    override fun onAnimationRepeat(animation: Animation?) {
                    }
                }
                clockAnimation.setAnimationListener(animationListener)
                view?.startAnimation(clockAnimation)
            }
        }
        timeoutViewModel.setTimeout(
            resources.getInteger(R.integer.duration_thirty_min)
                .minus(AppConstants.CLOCK_FAR_VIEW_TIME_OUT / 1000)
        )
    }
}

