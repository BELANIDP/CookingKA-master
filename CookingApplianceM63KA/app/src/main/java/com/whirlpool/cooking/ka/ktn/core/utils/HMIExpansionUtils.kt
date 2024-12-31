package core.utils

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.presenter.fragments.digital_unboxing.UnBoxingDoneCongratulationFragment
import android.presenter.fragments.kitchentimer.KitchenTumblerListTimerFragment
import android.presenter.fragments.self_clean.DoorLockingIntermediateLoader
import android.presenter.fragments.settings.DemoModeCodeFragment
import android.presenter.fragments.settings.SettingsLandingFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.diagnostics.models.DiagnosticsManager
import com.whirlpool.hmi.expansion.viewmodel.HmiExpParameters.KnobEventData
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModel
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModel.FUNCTION_CANCEL
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModel.FUNCTION_CLEAN
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModel.INSTANCE_CAVITY_0
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModel.LightAnimation
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.timers.Timer
import core.jbase.AbstractClockFragment
import core.jbase.AbstractTemperatureNumberPadFragment
import core.utils.AppConstants.BUTTON_LIGHT_ON
import core.utils.AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN
import core.utils.AppConstants.KEY_CONFIGURATION_CONNECT_TO_NETWORK
import core.utils.AppConstants.KEY_CONFIGURATION_CONTROL_LOCK
import core.utils.AppConstants.KEY_CONFIGURATION_DEMO_MODE
import core.utils.AppConstants.KEY_CONFIGURATION_DEMO_MODE_CLOCK
import core.utils.AppConstants.KEY_CONFIGURATION_DEMO_MODE_HOME
import core.utils.AppConstants.KEY_CONFIGURATION_DEMO_MODE_LANDING
import core.utils.AppConstants.KEY_CONFIGURATION_DIGITAL_UNBOXING
import core.utils.AppConstants.KEY_CONFIGURATION_DURING_DOOR_LOCK
import core.utils.AppConstants.KEY_CONFIGURATION_FAULT_A_C
import core.utils.AppConstants.KEY_CONFIGURATION_FAULT_B2
import core.utils.AppConstants.KEY_CONFIGURATION_FAULT_BLOCKING
import core.utils.AppConstants.KEY_CONFIGURATION_HOME
import core.utils.AppConstants.KEY_CONFIGURATION_KITCHEN_TIMER
import core.utils.AppConstants.KEY_CONFIGURATION_POPUPS
import core.utils.AppConstants.KEY_CONFIGURATION_POPUPS_IDLE
import core.utils.AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY
import core.utils.AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION
import core.utils.AppConstants.KEY_CONFIGURATION_RUNNING
import core.utils.AppConstants.KEY_CONFIGURATION_SABBATH_MODE
import core.utils.AppConstants.KEY_CONFIGURATION_SELF_CLEAN
import core.utils.AppConstants.KEY_CONFIGURATION_SELF_CLEAN_RUNNING
import core.utils.AppConstants.KEY_CONFIGURATION_SERVICE
import core.utils.AppConstants.KEY_CONFIGURATION_SETTING_LANDING
import core.utils.AppConstants.KNOB_SELECTION_TIME_OUT
import core.utils.AppConstants.KNOB_SELECTION_TIME_OUT_TEN_MIN
import core.utils.AppConstants.LEFT_KNOB_ID
import core.utils.AppConstants.RIGHT_KNOB_ID
import core.utils.AppConstants.SLEEP_MODE
import core.utils.CookingAppUtils.Companion.dismissAllDialogs
import core.utils.CookingAppUtils.Companion.getVisibleFragment
import core.utils.CookingAppUtils.Companion.isAnyCycleRunning
import core.utils.CookingAppUtils.Companion.isDemoModeEnabled
import core.utils.CookingAppUtils.Companion.openCavitySelectionScreen
import core.utils.HMILogHelper.Logd
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NavigationUtils.Companion.navigateSafely
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HMIExpansionUtils {
    companion object {
        private var hmiExpansionViewModel: HmiExpansionViewModel? = null
        private lateinit var activity: FragmentActivity

        private const val FUNCTION_LEFT_KNOB = "knobLeft"
        private const val FUNCTION_RIGHT_KNOB = "knobRight"
        private const val FUNCTION_RIGHT_KNOB_HOLD = "knobRightHold"
        private const val FUNCTION_LEFT_KNOB_HOLD = "knobLeftHold"
        private const val FUNC_KNOB_RIGHT_ROTATE = "rightKnobRotation"
        private const val FUNC_KNOB_LEFT_ROTATE = "leftKnobRotation"
        private const val FUNC_KNOB_LED_SET_RIGHT = "knobLedSetRight"
        private const val FUNC_KNOB_LED_SET_LEFT = "knobLedSetLeft"
        private const val FUNC_HOME = "home"

        //Listener variable
        private var userInteractionListener: UserInteractionListener? = null
        private var hmiCleanButtonInteractionListener: HMICleanButtonInteractionListener? = null
        private var hmiCancelButtonInteractionListener: HMICancelButtonInteractionListener? = null
        private var hmiHomeButtonInteractionListener: HMIHomeButtonInteractionListener? = null
        private var hmiKnobInteractionListener: HMIKnobInteractionListener? = null
        private var hmiErrorCodesListener: HMIErrorCodesListener? = null
        private var leftKnobhandler = Handler(Looper.getMainLooper())
        private var rightKnobhandler = Handler(Looper.getMainLooper())
        private var knobEventFlag: Boolean = true
        private var knobSlowBlinkingTimeoutHandler: Handler? = null
        private var knobSlowBlinkingTimeoutRunnable: Runnable? = null
        private var isKnobSlowBlinkingTimeoutRunning: Boolean = false
        private var knobFastBlinkingTimeoutHandler: Handler? = null
        private var knobFastBlinkingTimeoutRunnable: Runnable? = null
        private var isKnobFastBlinkingTimeoutRunning: Boolean = false

        /**
         * Method to get the OffsetId of appliance
         *
         * @return Offset ID
         */
        private fun getOffsetId(): Int {
            return INSTANCE_CAVITY_0
        }

        /**
         * Method to intimate the UserInteractionListener when there is an interaction through HMI
         * Expansion Buttons such as Cancel , Clean or Set Button
         */
        fun onUserInteraction() {
            userInteractionListener?.onUserInteraction()
            if (isSlowBlinkingKnobTimeoutActive()) userInteractWithinSlowBlinkingTimeoutElapsed()
        }

        /**
         * Method to initialize HMIExpansionViewModel and to start observe for the HMI Expansion
         * button events
         *
         * @param fragmentActivity The fragment activity
         */
        fun observeLiveData(fragmentActivity: FragmentActivity) {
            hmiExpansionViewModel = HmiExpansionViewModelFactory.getHMIExpansion()
            activity = fragmentActivity
            handleHomeButton(activity)
            handleCancelButtonEvent(activity)
            handleLeftKnobButtonEvent(activity)
            handleRightKnobButtonEvent(activity)
            handleRightKnobHoldButtonEvent(activity)
            handleRightKnobTickHoldButtonEvent(activity)
            handleLeftKnobTickHoldButtonEvent(activity)
            handleLeftKnobHoldButtonEvent(activity)
            handleLeftKnobEvent(activity)
            handleRightKnobEvent(activity)
            if (CookingViewModelFactory.getProductVariantEnum() !=
                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN
            )
                handleCleanButtonEvent(activity, true)
            else
                HMILogHelper.Loge("Unhandled Technical model")
        }

        /**
         * Method remove the observers
         * @param lifecycleOwner The LifecycleOwner which controls observer for product variants.
         */
        fun removeObservers(lifecycleOwner: LifecycleOwner) {
            hmiExpansionViewModel?.getButtonStateEventFor(
                FUNCTION_CANCEL,
                getOffsetId()
            )?.removeObservers(lifecycleOwner)
            hmiExpansionViewModel?.getButtonStateEventFor(
                FUNC_HOME,
                getOffsetId()
            )?.removeObservers(lifecycleOwner)
            hmiExpansionViewModel?.getButtonStateEventFor(
                FUNCTION_RIGHT_KNOB_HOLD,
                getOffsetId()
            )?.removeObservers(lifecycleOwner)
            hmiExpansionViewModel?.getButtonStateEventFor(
                FUNCTION_LEFT_KNOB_HOLD,
                getOffsetId()
            )?.removeObservers(lifecycleOwner)
            hmiExpansionViewModel?.getButtonStateEventFor(
                FUNCTION_RIGHT_KNOB,
                getOffsetId()
            )?.removeObservers(lifecycleOwner)
            hmiExpansionViewModel?.getButtonStateEventFor(
                FUNCTION_LEFT_KNOB,
                getOffsetId()
            )?.removeObservers(lifecycleOwner)
            hmiExpansionViewModel?.getKnobEventFor(
                FUNC_KNOB_RIGHT_ROTATE,
                getOffsetId()
            )?.removeObservers(lifecycleOwner)
            hmiExpansionViewModel?.getKnobEventFor(
                FUNC_KNOB_LEFT_ROTATE,
                getOffsetId()
            )?.removeObservers(lifecycleOwner)
            if (CookingViewModelFactory.getProductVariantEnum() !=
                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN
            )
                handleCleanButtonEvent(lifecycleOwner, false)
        }

        /**
         * Method to register for Right knob rotation events
         *
         * @param lifecycleOwner LifecycleOwner
         */
        private fun handleRightKnobEvent(lifecycleOwner: LifecycleOwner) {
            hmiExpansionViewModel?.getKnobEventFor(
                FUNC_KNOB_RIGHT_ROTATE,
                getOffsetId()
            )?.observe(lifecycleOwner) { knobEventData: KnobEventData ->
                parseKnobRotateEvent(knobEventData)
                resetKnobSelectionHandler(RIGHT_KNOB_ID)
                setKnobLedAnimationOffAndLightState(true, FUNC_KNOB_RIGHT_ROTATE)
            }
        }

        /**
         * Method to register for Left knob rotation events
         *
         * @param lifecycleOwner LifecycleOwner
         */
        private fun handleLeftKnobEvent(lifecycleOwner: LifecycleOwner) {
            hmiExpansionViewModel?.getKnobEventFor(
                FUNC_KNOB_LEFT_ROTATE,
                getOffsetId()
            )?.observe(lifecycleOwner) { knobEventData: KnobEventData ->
                parseKnobRotateEvent(knobEventData)
                resetKnobSelectionHandler(LEFT_KNOB_ID)
                setKnobLedAnimationOffAndLightState(true, FUNC_KNOB_LEFT_ROTATE)
            }
        }

        /**
         * Method to register for left knob button events
         *
         * @param lifecycleOwner LifecycleOwner
         */
        private fun handleLeftKnobButtonEvent(lifecycleOwner: LifecycleOwner) {
            try {
                hmiExpansionViewModel?.getButtonStateEventFor(
                    FUNCTION_LEFT_KNOB,
                    getOffsetId()
                )?.observe(lifecycleOwner) { clickState: Boolean ->
                    parseLeftKnobButtonClick(
                        clickState
                    )
                    resetKnobSelectionHandler(LEFT_KNOB_ID)
                    setKnobLedAnimationOffAndLightState(true, FUNCTION_LEFT_KNOB)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Method to register for right knob button events
         *
         * @param lifecycleOwner LifecycleOwner
         */
        private fun handleRightKnobButtonEvent(lifecycleOwner: LifecycleOwner) {
            try {
                hmiExpansionViewModel?.getButtonStateEventFor(
                    FUNCTION_RIGHT_KNOB,
                    getOffsetId()
                )?.observe(lifecycleOwner) { clickState: Boolean ->
                    parseRightKnobButtonClick(
                        clickState
                    )
                    resetKnobSelectionHandler(RIGHT_KNOB_ID)
                    setKnobLedAnimationOffAndLightState(true, FUNCTION_RIGHT_KNOB)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Method to register for right knob button events
         *
         * @param lifecycleOwner LifecycleOwner
         */
        private fun handleRightKnobHoldButtonEvent(lifecycleOwner: LifecycleOwner) {
            try {
                hmiExpansionViewModel?.getButtonStateEventFor(
                    FUNCTION_RIGHT_KNOB_HOLD,
                    getOffsetId()
                )?.observe(lifecycleOwner) { clickState: Boolean ->
                    parseRightKnobButtonLongPress(
                        clickState
                    )
                    resetKnobSelectionHandler(RIGHT_KNOB_ID)
                    setKnobLedAnimationOffAndLightState(true, FUNCTION_RIGHT_KNOB_HOLD)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Method to register for right knob tick interval events
         *
         * @param lifecycleOwner LifecycleOwner
         */
        private fun handleRightKnobTickHoldButtonEvent(lifecycleOwner: LifecycleOwner) {
            try {
                hmiExpansionViewModel?.getHoldButtonTickEventFor(
                    FUNCTION_RIGHT_KNOB_HOLD,
                    getOffsetId()
                )?.observe(lifecycleOwner) { timeInterval: Int ->
                    parseRightKnobButtonTickHoldEvent(
                        timeInterval
                    )
                    resetKnobSelectionHandler(RIGHT_KNOB_ID)
                    if(timeInterval != 0){
                        setKnobLedAnimationOffAndLightState(true, FUNCTION_RIGHT_KNOB_HOLD)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Method to register for left knob tick interval events
         *
         * @param lifecycleOwner LifecycleOwner
         */
        private fun handleLeftKnobTickHoldButtonEvent(lifecycleOwner: LifecycleOwner) {
            try {
                hmiExpansionViewModel?.getHoldButtonTickEventFor(
                    FUNCTION_LEFT_KNOB_HOLD,
                    getOffsetId()
                )?.observe(lifecycleOwner) { timeInterval: Int ->
                    parseLeftKnobButtonTickHoldEvent(
                        timeInterval
                    )
                    resetKnobSelectionHandler(LEFT_KNOB_ID)
                    if(timeInterval != 0){
                        setKnobLedAnimationOffAndLightState(true, FUNCTION_LEFT_KNOB_HOLD)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Method to register for Left knob button events
         *
         * @param lifecycleOwner LifecycleOwner
         */
        private fun handleLeftKnobHoldButtonEvent(lifecycleOwner: LifecycleOwner) {
            try {
                hmiExpansionViewModel?.getButtonStateEventFor(
                    FUNCTION_LEFT_KNOB_HOLD,
                    getOffsetId()
                )?.observe(lifecycleOwner) { clickState: Boolean ->
                    parseLeftKnobButtonLongPress(
                        clickState
                    )
                    resetKnobSelectionHandler(LEFT_KNOB_ID)
                    setKnobLedAnimationOffAndLightState(true, FUNCTION_LEFT_KNOB_HOLD)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Method to register for Home button events of Single Cavity Upper Oven
         *
         * @param lifecycleOwner LifecycleOwner
         */
        private fun handleHomeButton(lifecycleOwner: LifecycleOwner) {
            try {
                hmiExpansionViewModel?.getButtonStateEventFor(
                    FUNC_HOME,
                    getOffsetId()
                )?.observe(lifecycleOwner) { clickState: Boolean ->
                    parseButtonEventHome(clickState)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Function to be called when Home button click happens.
         *
         * @param clickState to detect Home button click.
         */
        private fun parseButtonEventHome(clickState: Boolean) {
            if (!isFastBlinkingKnobTimeoutActive() && !isSlowBlinkingKnobTimeoutActive() && !isAnyCycleRunning()) setBothKnobLightOff()
            // Navigate to clock screen
            val sharedViewModel: SharedViewModel =
                ViewModelProvider(activity)[SharedViewModel::class.java]
            val currentFragment = getVisibleFragment(activity.supportFragmentManager)
            val cookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
            Logd("HMI_KEY","RecipeExecutionState Running--> ${CookingAppUtils.isAnyCavityRunningRecipe()}")
            if (SettingsViewModel.getSettingsViewModel()?.controlLock?.value == false){
                if(CookingViewModelFactory.getPrimaryCavityViewModel().isOfTypeMicrowaveOven &&
                    CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.
                    recipeExecutionState.value == RecipeExecutionState.PAUSED) {
                    HMILogHelper.Loge("TAG: HOME BUTTON", "Door open and close, recipe is in pause state")
                    return
                }
                if (clickState) {
                    onUserInteraction()
                    if (currentFragment is AbstractClockFragment &&
                        !sharedViewModel.isApplianceInAOrCCategoryFault()
                    ) {
                        if (moveToDemoHome()) return
                        Logd(SLEEP_MODE, " on home click button")
                        hmiHomeButtonInteractionListener?.onHMIHomeButtonInteraction()
                            ?: openCavitySelectionScreen(currentFragment)
                    } else if (!CookingAppUtils.isAnyCavityRunningRecipeOrDelayedStateOrPausedState())  {
                        if (moveToDemoHome()) return
                        Logd("HMI_KEY","RecipeExecutionState.IDLE && RecipeCookingState.IDLE && Timer.State.IDLE --> Navigate to Cavity or Home screen")
                        hmiHomeButtonInteractionListener?.onHMIHomeButtonInteraction()
                            ?: currentFragment?.let { openCavitySelectionScreen(it,isFromHmiButton = true) }
                    }
                    else if (!sharedViewModel.isApplianceInAOrCCategoryFault())
                        getVisibleFragment(activity.supportFragmentManager)?.let {
                            dismissAllDialogs(activity.supportFragmentManager)
                            CookingAppUtils.navigateToStatusOrClockScreen(
                                it
                            )
                        }
                }
            }
        }

        private fun moveToDemoHome(): Boolean {
            if (isDemoModeEnabled()) {
                NavigationUtils.getVisibleFragment()?.let {
                    navigateSafely(
                        it,
                        R.id.demoModeLandingFragment,
                        null,
                        null
                    )
                }
                return true
            }
            return false
        }

        /**
         * Function to be called when Left knob button click happens.
         *
         * @param clickState to detect knob button click.
         */
        private fun parseLeftKnobButtonClick(clickState: Boolean) {
            if (SettingsViewModel.getSettingsViewModel()?.controlLock?.value == false) {
                if (clickState) {
                    onUserInteraction()
                    if (AppConstants.LEFT_KNOB_ID != AppConstants.DEFAULT_LEFT_KNOB_ID) {
                        hmiKnobInteractionListener?.onHMIRightKnobClick()
                            ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
                    } else {
                        hmiKnobInteractionListener?.onHMILeftKnobClick()
                            ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
                    }
                }
            } else {
                navigateToControlUnlock(activity.supportFragmentManager)
            }
        }

        /**
         * Function to be called when Left knob button pressed for longer duration.
         *
         * @param clickState to detect knob button press.
         */
        private fun parseLeftKnobButtonLongPress(clickState: Boolean) {
            if (clickState) {
                onUserInteraction()
                if (AppConstants.LEFT_KNOB_ID != AppConstants.DEFAULT_LEFT_KNOB_ID) {
                    hmiKnobInteractionListener?.onHMILongRightKnobPress()
                        ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
                } else {
                    hmiKnobInteractionListener?.onHMILongLeftKnobPress()
                        ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
                }
            }
        }

        /**
         * Function to be called when Right knob button click happens.
         *
         * @param clickState to detect knob button click.
         */
        private fun parseRightKnobButtonClick(clickState: Boolean) {
            if (SettingsViewModel.getSettingsViewModel()?.controlLock?.value == false) {
                if (clickState) {
                    onUserInteraction()
                    if (AppConstants.RIGHT_KNOB_ID != AppConstants.DEFAULT_RIGHT_KNOB_ID) {
                        hmiKnobInteractionListener?.onHMILeftKnobClick()
                            ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
                    } else {
                        hmiKnobInteractionListener?.onHMIRightKnobClick()
                            ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
                    }
                }
            } else {
                navigateToControlUnlock(activity.supportFragmentManager)
            }
        }

        /**
         * Function to be called when Right knob button pressed for longer duration.
         *
         * @param clickState to detect knob button press.
         */
        private fun parseRightKnobButtonLongPress(clickState: Boolean) {
            if (clickState) {
                onUserInteraction()
                if (AppConstants.RIGHT_KNOB_ID != AppConstants.DEFAULT_RIGHT_KNOB_ID) {
                    hmiKnobInteractionListener?.onHMILongLeftKnobPress()
                        ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
                } else {
                    hmiKnobInteractionListener?.onHMILongRightKnobPress()
                        ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
                }
            }
        }

        /**
         * Function to be called when Right knob tick events for longer duration.
         *
         * @param timeInterval to detect knob hold tick events.
         */
        private fun parseRightKnobButtonTickHoldEvent(timeInterval: Int) {
            if (AppConstants.RIGHT_KNOB_ID != AppConstants.DEFAULT_RIGHT_KNOB_ID) {
                hmiKnobInteractionListener?.onHMILeftKnobTickHoldEvent(timeInterval)
                    ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
            } else {
                hmiKnobInteractionListener?.onHMIRightKnobTickHoldEvent(timeInterval)
                    ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
            }
        }

        /**
         * Function to be called when left knob tick events for longer duration.
         *
         * @param timeInterval to detect knob hold tick events.
         */
        private fun parseLeftKnobButtonTickHoldEvent(timeInterval: Int) {
            if (AppConstants.LEFT_KNOB_ID != AppConstants.DEFAULT_LEFT_KNOB_ID) {
                hmiKnobInteractionListener?.onHMIRightKnobTickHoldEvent(timeInterval)
                    ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
            } else {
                hmiKnobInteractionListener?.onHMILeftKnobTickHoldEvent(timeInterval)
                    ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
            }
        }

        /**
         * Function to be called when knob rotate happens.
         *
         * @param knobEventData to get knob event info(knobId, knobDirection).
         */
        private fun parseKnobRotateEvent(knobEventData: KnobEventData) {
            if (SettingsViewModel.getSettingsViewModel()?.controlLock?.value == false) {
                onUserInteraction()
                hmiKnobInteractionListener?.onKnobRotateEvent(
                    knobEventData.knobID,
                    knobEventData.direction
                ) ?: HMILogHelper.Loge("hmiKnobInteractionListener is null")
            } else {
                if (knobEventFlag) {
                    setKnobEventFlag(false)
                    navigateToControlUnlock(activity.supportFragmentManager)
                }
            }
        }

        fun setKnobEventFlag(flag: Boolean) {
            knobEventFlag = flag
        }
        /* ************************************************************************************** */
        /* ***************************  Handling HMI Expansion Clean Button ********************* */
        /* ************************************************************************************** */
        /**
         * Method to register for clean button events
         *
         * @param lifecycleOwner LifecycleOwner
         * @param observeFlag true to observe for button event and false to remove observer
         */
        private fun handleCleanButtonEvent(
            lifecycleOwner: LifecycleOwner,
            observeFlag: Boolean
        ) {
            if (observeFlag) {
                hmiExpansionViewModel?.getButtonStateEventFor(
                    FUNCTION_CLEAN, getOffsetId()
                )?.observe(lifecycleOwner) { isPressed: Boolean ->
                    parseButtonEventForClean(isPressed)
                }
            } else {
                hmiExpansionViewModel?.getButtonStateEventFor(
                    FUNCTION_CLEAN, getOffsetId()
                )?.removeObservers(lifecycleOwner)
            }
        }

        private fun parseButtonEventForClean(isPressed: Boolean) {
            Logd("parseButtonEventForClean+ with pressed state as $isPressed")
            if (!isFastBlinkingKnobTimeoutActive() && !isSlowBlinkingKnobTimeoutActive() && !isAnyCycleRunning()) setBothKnobLightOff()
            if (isPressed) {
                // Button clicks are considered for timeouts. They are also user interactions.
                onUserInteraction()
                //Audio for button press
                AudioManagerUtils.playOneShotSound(
                    activity.applicationContext,
                    R.raw.audio_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                //Handled Listener in Safety Instruction Fragment (which is the only screen)
                // applicable for Clean Button
                hmiCleanButtonInteractionListener?.onHMICleanButtonInteraction()
                    ?: HMILogHelper.Loge("hmiCleanButtonInteractionListener is null")
            }
        }

        /**
         * Method to handle cancel button click on HMI
         *
         * @param lifecycleOwner the LifecycleOwner which controls the observer
         */
        private fun handleCancelButtonEvent(lifecycleOwner: LifecycleOwner) {
            try {
                hmiExpansionViewModel?.getButtonStateEventFor(
                    FUNCTION_CANCEL, getOffsetId()
                )?.observe(lifecycleOwner) { isPressed: Boolean ->
                    parseButtonEventForCancel(isPressed)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Handle Cancel Button Event for Double Oven
         *
         * @param isPressed       true if cancel pressed
         */
        private fun parseButtonEventForCancel(isPressed: Boolean) {
            HMILogHelper.Logi("parseButtonEventForCancel button isPressed state = $isPressed")
            if (isFastBlinkingKnobTimeoutActive()) userInteractWithinFastBlinkingTimeoutElapsed()
            if (!isFastBlinkingKnobTimeoutActive() && !isSlowBlinkingKnobTimeoutActive() && !isAnyCycleRunning()) setBothKnobLightOff()
            val sharedViewModel: SharedViewModel =
                ViewModelProvider(activity)[SharedViewModel::class.java]
            if (isPressed && !sharedViewModel.isApplianceInAOrCCategoryFault()) {
                // Button clicks are considered for timeouts. They are also user interactions.
                if (CookingViewModelFactory.getInScopeViewModel() != null) {
                    hmiCancelButtonInteractionListener?.onHMICancelButtonInteraction()
                        ?: run {
                            // ToDo: need to check for Sabbath mode
                            HMILogHelper.Loge(
                                "TAG: CANCEL BUTTON",
                                " hmiCancelButtonInteractionListener = null " +
                                        "canceling currentCookingViewModel"
                            )
                            if (SettingsViewModel.getSettingsViewModel()?.controlLock?.value == true){
                                // Play Audio for Controls Locked & Navigate to Control Unlock fragment
                                if(CookingAppUtils.isAnyCavityRunningRecipe()){
                                    Logd("HMI_KEY", "controlLock true --> Cancel pressed cancel running cycle")
                                    getVisibleFragment(
                                        activity.supportFragmentManager)?.let {
                                        cancelButtonPressEventFromAnyScreen(
                                            it
                                        )
                                    }
                                }else {
                                    Logd("HMI_KEY", "controlLock true --> Cancel pressed navigate To Control Unlock")
                                    navigateToControlUnlock(activity.supportFragmentManager)
                                }

                            } else {
                                Logd("HMI_KEY", "controlLock false --> Cancel pressed cancel Cycle And Navigate To Clock")
                                if (!sharedViewModel.isToolsMenuVisible()) {
                                    getVisibleFragment(
                                        activity.supportFragmentManager)?.let {
                                        cancelButtonPressEventFromAnyScreen(
                                            it
                                        )
                                    }
                                } else {

                                }
                            }
                        }
                } else {
                    HMILogHelper.Logi("TAG: getInScopeViewModel is Null")
                }
                onUserInteraction()
            } else {
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.start_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                HMILogHelper.Logi("SDK: Cancel Button Pressed for Upper Oven VM is false")
            }
        }

        /**
         * Set the interface to receive callbacks in the fragment onUserInteraction.
         */
        fun setFragmentUserInteractionListener(
            fragmentUserInteractionListener: UserInteractionListener?
        ) {
            userInteractionListener = fragmentUserInteractionListener
        }

        /**
         * Remove the set interface which was used to receive onUserInteraction callbacks.
         *
         * @param fragmentUserInteractionListener previously set listener interface.
         */
        fun removeFragmentUserInteractionListener(
            fragmentUserInteractionListener: UserInteractionListener
        ) {
            if (userInteractionListener === fragmentUserInteractionListener) {   // only the
                // owning fragment can make the listener null.
                userInteractionListener = null
            }
        }

        /**
         * Set the interface to receive callbacks in the fragment onCleanButtonInteraction.
         */
        fun setHMICleanButtonInteractionListener(
            buttonInteractionListener: HMICleanButtonInteractionListener?
        ) {
            hmiCleanButtonInteractionListener = buttonInteractionListener
        }

        /**
         * Remove the set interface which was used to receive onCleanButtonInteraction callbacks.
         *
         * @param buttonInteractionListener previously set listener interface.
         */
        fun removeHMICleanButtonInteractionListener(
            buttonInteractionListener: HMICleanButtonInteractionListener
        ) {
            if (hmiCleanButtonInteractionListener === buttonInteractionListener) {   // only the
                // owning fragment can make the listener null.
                hmiCleanButtonInteractionListener = null
            }
        }

        /**
         * Set the interface to receive callbacks in the fragment onCancelButtonInteraction.
         */
        fun setHMICancelButtonInteractionListener(
            buttonInteractionListener: HMICancelButtonInteractionListener?
        ) {
            Logd("TAG: CANCEL BUTTON", "Set cancel button interaction listener")
            hmiCancelButtonInteractionListener = buttonInteractionListener
        }

        /**
         * Remove the set interface which was used to receive onCancelButtonInteraction callbacks.
         *
         * @param buttonInteractionListener previously set listener interface.
         */
        fun removeHMICancelButtonInteractionListener(
            buttonInteractionListener: HMICancelButtonInteractionListener
        ) {
            if (hmiCancelButtonInteractionListener === buttonInteractionListener) {   // only the
                // owning fragment can make the listener null.
                Logd("TAG: CANCEL BUTTON", "Remove cancel button interaction listener")
                hmiCancelButtonInteractionListener = null
            }
        }

        /**
        * Set the interface to receive callbacks in the fragment onCancelButtonInteraction.
        */
        fun setHMIHomeButtonInteractionListener(
            buttonInteractionListener: HMIHomeButtonInteractionListener?
        ) {
            Logd("TAG: CANCEL BUTTON", "Set cancel button interaction listener")
            hmiHomeButtonInteractionListener = buttonInteractionListener
        }

        /**
         * Remove the set interface which was used to receive onCancelButtonInteraction callbacks.
         *
         * @param buttonInteractionListener previously set listener interface.
         */
        fun removeHMIHomeButtonInteractionListener(
            buttonInteractionListener: HMIHomeButtonInteractionListener
        ) {
            if (hmiHomeButtonInteractionListener === buttonInteractionListener) {   // only the
                // owning fragment can make the listener null.
                Logd("TAG: CANCEL BUTTON", "Remove cancel button interaction listener")
                hmiHomeButtonInteractionListener = null
            }
        }

        /**
         * Set the interface to receive callbacks in the fragment hmiKnobInteractionListener.
         */
        fun setHMIKnobInteractionListener(knobInteractionListener: HMIKnobInteractionListener) {
            hmiKnobInteractionListener = knobInteractionListener
        }

        /**
         * Remove the set interface which was used to receive hmiKnobInteractionListener callbacks.
         *
         * @param knobInteractionListener previously set listener interface.
         */
        fun removeHMIKnobInteractionListener(knobInteractionListener: HMIKnobInteractionListener) {
            if (hmiKnobInteractionListener === knobInteractionListener) {   // only the
                // owning fragment can make the listener null.
                hmiKnobInteractionListener = null
            }
        }

        /**
         * Set the interface to receive callbacks in the fragment ErrorCodes.
         */
        @Suppress("unused")
        fun setHMIErrorCodesListener(errorCodesListener: HMIErrorCodesListener?) {
            Logd("TAG: Error Codes", "Set Error codes listener")
            hmiErrorCodesListener = errorCodesListener
        }

        /**
         * Remove the set interface which was used to receive ErrorCodes callbacks.
         *
         * @param errorCodesListener previously set listener interface.
         */
        @Suppress("unused")
        fun removeHMIErrorCodesListener(errorCodesListener: HMIErrorCodesListener) {
            if (hmiErrorCodesListener === errorCodesListener) {   // only the
                // owning fragment can make the listener null.
                Logd("TAG: Error Codes", "Remove Error codes listener")
                hmiErrorCodesListener = null
            }
        }

        fun getHMIErrorCodesListener(): HMIErrorCodesListener? {
            return hmiErrorCodesListener
        }

        /* *************************************************************************************** */
        /* ******************************* Button Light APIs ************************************ */
        /* *************************************************************************************** */
        /**
         * Method to set the cancel button light flashing
         *
         * @param lightForCancel   true - set the cancel button light on
         */
        fun setLightForCancelButton(
            lightForCancel: Boolean
        ) {
            val intensity = if (lightForCancel) 100 else 0
            if (hmiExpansionViewModel?.setLightForCancel(
                    getOffsetId(),
                    lightForCancel,
                    intensity
                ) == true
            ) {
                Logd("Success in setLightForButton: Cancel Button state: $lightForCancel")
            } else {
                Logd("Failed in setLightForButton: Cancel Button state: $lightForCancel")
            }
        }

        /**
         * Method to set the clean button light flashing for self clean cycle
         * @param lightState true - set the clean state on ;false - set the clean state off
         */
        fun setLightForCleanButton(lightState: Boolean) {
            if (hmiExpansionViewModel?.setLightForClean(
                    getOffsetId(),
                    lightState,
                    100
                ) == true
            ) {
                Logd("Success in setLightForButton: Clean Button state: $lightState")
            } else {
                Logd("Failed in setLightForButton: Clean Button state: $lightState")
            }
        }

        /**
         * Method to set the illumination for HOME button for single cavity
         *
         * @param lightState: true - set the light on; false - set the light off
         */
        fun setLightForHomeButton(lightState: Boolean) {
            //For single cavity, oven type is UPPER oven
            if (hmiExpansionViewModel?.setLightFor(
                    FUNC_HOME,
                    getOffsetId(),
                    lightState,
                    BUTTON_LIGHT_ON
                ) == true
            ) {
                Logd("Success in setLightForButton: Home Button state: $lightState")
            } else {
                Logd("Failed in setLightForButton: Home Button state: $lightState")
            }
        }

        /**
         * Turn off the cavity light and button Light
         *
         * @param cookingViewModel view model to which cancel key is pressed
         */
        private fun setLightOffForButtonAndCavity(cookingViewModel: CookingViewModel?) {
            setLightForCancelButton(false)
            setLightForCleanButton(false)
            setKnobLedAnimationOffAndLightState(false, FUNC_KNOB_LEFT_ROTATE)
            setKnobLedAnimationOffAndLightState(false, FUNC_KNOB_RIGHT_ROTATE)
        }

        /**
         * Turn ON the Both knob Lights
         *
         */
        fun setBothKnobLightOn() {
            setKnobLedAnimationOffAndLightState(true, FUNC_KNOB_LEFT_ROTATE)
            setKnobLedAnimationOffAndLightState(true, FUNC_KNOB_RIGHT_ROTATE)
        }

        /**
         * Turn OFF the Both knob Lights
         *
         */
        fun setBothKnobLightOff() {
            setKnobLedAnimationOffAndLightState(false, FUNC_KNOB_LEFT_ROTATE)
            setKnobLedAnimationOffAndLightState(false, FUNC_KNOB_RIGHT_ROTATE)
        }

        /**
         * Function to be called to turn on/off the animation and turn on/off LED's around knobs
         */
        private fun setKnobLedAnimationOffAndLightState(
            ledState: Boolean, knobId: String
        ) {
            if (SharedPreferenceManager.getKnobLightStatusIntoPreference().toBoolean() && SettingsViewModel.getSettingsViewModel().sabbathMode.value == SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT) {
                if (isFastBlinkingKnobTimeoutActive() || isAnyCycleRunning())
                    return
                when (knobId) {
                    FUNC_KNOB_LEFT_ROTATE, FUNCTION_LEFT_KNOB_HOLD, FUNCTION_LEFT_KNOB, LEFT_KNOB_ID.toString() -> {
                        hmiExpansionViewModel?.stopLightAnimationFor(
                            FUNC_KNOB_LED_SET_LEFT,
                            getOffsetId()
                        )
                        hmiExpansionViewModel?.setLightFor(
                            FUNC_KNOB_LED_SET_LEFT, getOffsetId(), ledState, BUTTON_LIGHT_ON
                        )
                    }

                    FUNC_KNOB_RIGHT_ROTATE, FUNCTION_RIGHT_KNOB_HOLD, FUNCTION_RIGHT_KNOB, RIGHT_KNOB_ID.toString() -> {
                        hmiExpansionViewModel?.stopLightAnimationFor(
                            FUNC_KNOB_LED_SET_RIGHT,
                            getOffsetId()
                        )
                        hmiExpansionViewModel?.setLightFor(
                            FUNC_KNOB_LED_SET_RIGHT, getOffsetId(), ledState, BUTTON_LIGHT_ON
                        )
                    }
                }
            }
        }

        /**
         * Method to start ot stop the animation/flashing of the clean LED button
         *
         * @param animationState true if light flashes
         */
        fun startOrStopCleanButtonLightBlinkAnimation(animationState: Boolean) {
            startOrStopButtonLedFastBlinkAnimation(animationState, FUNCTION_CLEAN)
        }

        /**
         * Method to start ot stop the fast blink animation/flashing of the Knob LEDs
         *
         * @param animationState true if light flashes
         */
        @Suppress("unused")
        fun startOrStopKnobLEDFastBlinkAnimation(animationState: Boolean) {
            if (SharedPreferenceManager.getKnobLightStatusIntoPreference().toBoolean() && SettingsViewModel.getSettingsViewModel().sabbathMode.value == SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT) {
                startOrStopButtonLedFastBlinkAnimation(animationState, FUNC_KNOB_LED_SET_LEFT)
                startOrStopButtonLedFastBlinkAnimation(animationState, FUNC_KNOB_LED_SET_RIGHT)
            }
        }

        /**
         * Method to start ot stop the slow blink animation/flashing of the Knob LEDs
         *
         * @param animationState true if light flashes
         */
        fun startOrStopKnobLEDSlowBlinkAnimation(animationState: Boolean) {
            if (SharedPreferenceManager.getKnobLightStatusIntoPreference().toBoolean() && SettingsViewModel.getSettingsViewModel().sabbathMode.value == SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT) {
                startOrStopButtonLedSlowBlinkAnimation(animationState, FUNC_KNOB_LED_SET_RIGHT)
                startOrStopButtonLedSlowBlinkAnimation(animationState, FUNC_KNOB_LED_SET_LEFT)
            }
        }

        /**
         * Method to start ot stop the Fade in light animation/flashing of the Knob LEDs
         *
         * @param animationState true if light flashes
         */
        fun startOrStopKnobLEDFadeInLightAnimation(animationState: Boolean) {
            if (SharedPreferenceManager.getKnobLightStatusIntoPreference().toBoolean() && SettingsViewModel.getSettingsViewModel().sabbathMode.value == SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT) {
                startOrStopKnobButtonFadeInLightAnimation(animationState, FUNC_KNOB_LED_SET_RIGHT)
                startOrStopKnobButtonFadeInLightAnimation(animationState, FUNC_KNOB_LED_SET_LEFT)
            }
        }

        /**
         * Method to start ot stop the LED FAST animation
         *
         * @param animationState true if light flashes
         * @param functionId set of Led lights
         */
        private fun startOrStopButtonLedFastBlinkAnimation(
            animationState: Boolean,
            functionId: String
        ) {
            if (animationState) {
                if (hmiExpansionViewModel?.startLightAnimationFor(
                        functionId,
                        getOffsetId(),
                        LightAnimation.BLINK_FAST
                    ) == true
                ) {
                    Logd("Successfully started Animation for LED function : $functionId")
                } else {
                    Logd("Unsuccessful in starting Animation for : $functionId")
                }
            } else {
                if (hmiExpansionViewModel?.stopLightAnimationFor(
                        functionId,
                        getOffsetId()
                    ) == true
                ) {
                    Logd("Successfully Stopped Animation for $functionId button")
                } else {
                    Logd("failed to stop animation for $functionId")
                }
            }
        }

        /**
         * Method to start ot stop the LED SLOW animation
         *
         * @param animationState true if light flashes
         * @param functionId set of Led lights
         */
        @Suppress("SameParameterValue")
        private fun startOrStopButtonLedSlowBlinkAnimation(
            animationState: Boolean,
            functionId: String
        ) {
            if (animationState) {
                if (hmiExpansionViewModel?.startLightAnimationFor(
                        functionId,
                        getOffsetId(),
                        LightAnimation.SLOW_FADE
                    ) == true
                ) {
                    Logd("Successfully started Animation for LED function : $functionId")
                } else {
                    Logd("Unsuccessful in starting Animation for : $functionId")
                }
            } else {
                if (hmiExpansionViewModel?.stopLightAnimationFor(
                        functionId,
                        getOffsetId()
                    ) == true
                ) {
                    Logd("Successfully Stopped Animation for $functionId button")
                } else {
                    Logd("failed to stop animation for $functionId")
                }
            }
        }

        /**
         * Method to start ot stop the Knob Led Fade In animation
         *
         * @param animationState true if light flashes
         * @param functionId set of Led lights
         */
        private fun startOrStopKnobButtonFadeInLightAnimation(
            animationState: Boolean,
            functionId: String
        ) {
            if (animationState) {
                if (hmiExpansionViewModel?.startLightAnimationFor(
                        functionId,
                        getOffsetId(),
                        LightAnimation.FADE_IN_FULL_BRIGHTNESS
                    ) == true
                ) {
                    Logd("Successfully started Animation for LED function : $functionId")
                } else {
                    Logd("Unsuccessful in starting Animation for : $functionId")
                }
            } else {
                if (hmiExpansionViewModel?.stopLightAnimationFor(
                        functionId,
                        getOffsetId()
                    ) == true
                ) {
                    Logd("Successfully Stopped Animation for $functionId button")
                } else {
                    Logd("failed to stop animation for $functionId")
                }
            }

            CoroutineScope(Dispatchers.Main).launch {
                delay(5000)
                setBothKnobLightOnDirectly()
            }
        }

        /**
         * Handle Method to cancel cooking cycle and navigate to clock screen
         */
        fun cancelCycleAndNavigateToClock() {
            cancelCookingBasedOnRecipeExecutionState(
                CookingViewModelFactory.getInScopeViewModel()
            )
            cancelCookingBasedOnRecipeExecutionState(
                CookingViewModelFactory.getOutOfScopeCookingViewModel()
            )
            dismissAllDialogs(activity.supportFragmentManager)
            navigateToClock(activity.supportFragmentManager)
        }

        /**
         * Handle Method to cancel cooking cycle based on cavity if any cavity is running then setFragmentResult FUNCTION_CANCEL to Status screen and all the logic of showing
         * resume recipe will be handle there
         */
        fun cancelButtonPressEventFromAnyScreen(fragment: Fragment) {
            dismissAllDialogs(activity.supportFragmentManager)
            if(CookingAppUtils.isAnyCavityRunningRecipe()){
                HMILogHelper.Loge("HMIExpansionUtils", "CANCEL button has been pressed while Cavity is RUNNING, setting setFragmentResult as FUNCTION_CANCEL and navigating to STATUS Screen")
                activity.supportFragmentManager.setFragmentResult(FUNCTION_CANCEL, bundleOf(FUNCTION_CANCEL to true))
            }else{
                Logd("HMIExpansionUtils", "CANCEL button has been pressed NONE Cavity is running, navigating to CLOCK Screen")
                when(CookingViewModelFactory.getProductVariantEnum()){
                    CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                    CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                        Logd("HMIExpansionUtils", "Cancelling Secondary recipeExecutionViewModel")
                        CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.cancel()
                    }
                    else -> {}
                }
                Logd("HMIExpansionUtils", "Cancelling Primary recipeExecutionViewModel")
                CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.cancel()
            }
            CookingAppUtils.navigateToStatusOrClockScreen(fragment)
        }


        /**
         * Cancel Cooking based on Cooking State and turning off cavity lights
         *
         * @param cookingViewModel view model to cancel cycle if not IDLE
         */
        private fun cancelCookingBasedOnRecipeExecutionState(cookingViewModel: CookingViewModel?) {
            if (cookingViewModel?.recipeExecutionViewModel?.recipeExecutionState?.value
                != RecipeExecutionState.IDLE
            ) {
                // ToDO: Need to call stopWhenDoneAudio() before CancelCooking as
                // it checks the current state is when done to stop the audio
                cookingViewModel?.recipeExecutionViewModel?.cancel()
                    ?: HMILogHelper.Loge("$cookingViewModel is null ")
            } else {
                HMILogHelper.Loge("recipeExecutionState is IDLE")
            }
            cookingViewModel?.let {
                setLightOffForButtonAndCavity(cookingViewModel)
            }
        }

        /*
         * Method to navigate to clock screen
         */
        private fun navigateToClock(fragmentManager: FragmentManager) {
            //To avoid clock to clock screen navigation
            if (!CookingAppUtils.isClockScreen(fragmentManager)) {
                val fragment = getVisibleFragment(fragmentManager)
                if (fragment != null) {
                    when (CookingViewModelFactory.getProductVariantEnum()) {
                        CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN ->
                            (fragmentManager.primaryNavigationFragment?.let { getViewSafely(it) }
                                ?: fragmentManager.primaryNavigationFragment?.requireView())?.let {
                                Navigation.findNavController(
                                    it
                                ).setGraph(R.navigation.manual_cooking_single_oven)
                            }

                        CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN ->
                            (fragmentManager.primaryNavigationFragment?.let { getViewSafely(it) }
                                ?: fragmentManager.primaryNavigationFragment?.requireView())?.let {
                                Navigation.findNavController(
                                    it
                                ).setGraph(R.navigation.manual_cooking_mwo_oven)
                            }

                        CookingViewModelFactory.ProductVariantEnum.COMBO ->
                            (fragmentManager.primaryNavigationFragment?.let { getViewSafely(it) }
                                ?: fragmentManager.primaryNavigationFragment?.requireView())?.let {
                                Navigation.findNavController(
                                    it
                                ).setGraph(R.navigation.manual_cooking_combo)
                            }

                        CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN ->
                            (fragmentManager.primaryNavigationFragment?.let { getViewSafely(it) }
                                ?: fragmentManager.primaryNavigationFragment?.requireView())?.let {
                                Navigation.findNavController(
                                    it
                                ).setGraph(R.navigation.manual_cooking_double_oven)
                            }

                        else -> HMILogHelper.Loge("Null Variant", "Variant not handled")
                    }
                    NavigationUtils.navigateSafely(
                        fragment,
                        R.id.global_action_to_clockScreen,
                        null,
                        null
                    )
                }
            }
        }

        private fun navigateToControlUnlock(fragmentManager: FragmentManager){
            if (!CookingAppUtils.isControlUnlockScreen(fragmentManager)) {
                val fragment = getVisibleFragment(fragmentManager)
                if (fragment != null) {
                    NavigationUtils.navigateSafely(
                        fragment,
                        R.id.controlUnlockFragment,
                        null,
                        null
                    )
                }
            }
        }

        /**
         * Enable the HMI Keys for the feature specific use case
         */
        fun enableFeatureHMIKeys(feature: Int) {
            val variant = CookingViewModelFactory.getProductVariantEnum()
            when (variant) {
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                    Logd("HMI_KEY", "------- Enable Key for Single oven or MWO --------")
                    makeButtonValid(feature)
                }

                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    Logd("HMI_KEY", "------- Enable Key for Double oven or Combo --------")
                    makeButtonValid(feature)
                }

                else -> {}
            }

        }

        /**
         * Disable the HMI Keys for the feature specific use case
         */
        fun disableFeatureHMIKeys(feature: Int) {
            val variant = CookingViewModelFactory.getProductVariantEnum()
            when (variant) {
                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                    Logd("HMI_KEY", "------- Disable Key for Single oven or MWO --------")
                    makeButtonInvalid(feature)
                }

                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    Logd("HMI_KEY", "------- Disable Key for Double oven or Combo --------")
                    makeButtonInvalid(feature)
                }

                else -> {}
            }
        }

        /**
         * Make Clean button valid
         */
        fun makeCleanButtonValid(){
            Logd("HMI_KEY", "HMI Keys enable for Self Clean --> Clean Button\n----------------")
            hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
        }

        /**
         * Make Clean button Invalid
         */
        fun makeCleanButtonInValid(){
            Logd("HMI_KEY", "HMI Keys disable for Self Clean --> Clean Button\n----------------")
            hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
        }
        private fun makeButtonValid(feature: Int) {
            when (feature) {
                KEY_CONFIGURATION_CLOCK_SCREEN -> {
                    Logd("HMI_KEY", "HMI Keys enable for clock --> Home Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = true, cancelLight = false, cleanLight = false)
                }

                KEY_CONFIGURATION_HOME -> {
                    Logd("HMI_KEY", "HMI Keys enable for home --> Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }

                KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION -> {
                    Logd("HMI_KEY","HMI Keys enable for Programming Mode Selection --> Home and Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = true, cancelLight = true, cleanLight = false)
                }

                KEY_CONFIGURATION_RUNNING -> {
                    Logd("HMI_KEY","HMI Keys enable for Running --> Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }

                KEY_CONFIGURATION_SETTING_LANDING -> {
                    Logd("HMI_KEY","HMI Keys enable for Settings --> Cancel Button\n---------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }

                KEY_CONFIGURATION_KITCHEN_TIMER -> {
                    Logd("HMI_KEY","HMI Keys enable for Kitchen Timer --> Cancel Button\n---------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }

                KEY_CONFIGURATION_CONTROL_LOCK -> {
                    Logd("HMI_KEY","HMI Keys enable for Control Lock --> Cancel Button\n---------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }
                KEY_CONFIGURATION_DEMO_MODE -> {
                    Logd("HMI_KEY","HMI Keys enable for Demo Mode --> Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }
                KEY_CONFIGURATION_DEMO_MODE_CLOCK -> {
                    Logd("HMI_KEY","HMI Keys enable for Demo Mode Clock --> Home Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = true, cancelLight = false, cleanLight = false)
                }
                KEY_CONFIGURATION_DEMO_MODE_HOME -> {
                    Logd("HMI_KEY","HMI Keys enable for Demo Mode Home --> Home and Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = true, cancelLight = false, cleanLight = false)
                }
                KEY_CONFIGURATION_DEMO_MODE_LANDING -> {
                    Logd("HMI_KEY","HMI Keys enable for Demo Mode Landing --> Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }

                KEY_CONFIGURATION_SELF_CLEAN -> {
                    Logd("HMI_KEY","HMI Keys enable for Self Clean --> Clean and Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }
                KEY_CONFIGURATION_SELF_CLEAN_RUNNING -> {
                    Logd("HMI_KEY","HMI Keys enable for Self Clean Running --> Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }
                KEY_CONFIGURATION_FAULT_B2 -> {
                    Logd("HMI_KEY","HMI Keys enable for Fault B2 --> Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }
                KEY_CONFIGURATION_POPUPS -> {
                    Logd("HMI_KEY","HMI Keys enable for Popup --> Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }
                KEY_CONFIGURATION_POPUPS_PRIORITY -> {
                    Logd("HMI_KEY","HMI Keys enable for Popups Priority --> Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }
                else -> Logd("HMI_KEY","This particular feature has not handled for enabling the HMI Key logic")
            }
        }

        private fun makeButtonInvalid(feature: Int) {
            when (feature) {
                KEY_CONFIGURATION_DIGITAL_UNBOXING -> {
                    Logd("HMI_KEY","HMI Keys disable for Unboxing --> All Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = false, cleanLight = false)
                }

                KEY_CONFIGURATION_CLOCK_SCREEN -> {
                    Logd("HMI_KEY","HMI Keys disable for clock --> Cancel and Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }

                KEY_CONFIGURATION_HOME -> {
                    Logd("HMI_KEY","HMI Keys disable for home --> Home and Clean Button\n---------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }

                KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION -> {
                    Logd("HMI_KEY","HMI Keys disable for Programming Mode Selection --> Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }

                KEY_CONFIGURATION_RUNNING -> {
                    Logd("HMI_KEY","HMI Keys disable for Running --> Home and Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
                }

                KEY_CONFIGURATION_SETTING_LANDING -> {
                    Logd("HMI_KEY","HMI Keys disable for Settings --> Home and Clean Button\n---------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }

                KEY_CONFIGURATION_KITCHEN_TIMER -> {
                    Logd("HMI_KEY","HMI Keys disable for Kitchen Timer --> Home and Clean Button\n---------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }

                KEY_CONFIGURATION_CONTROL_LOCK -> {
                    Logd("HMI_KEY","HMI Keys disable for Control Lock --> Home and Clean Button\n---------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }

                KEY_CONFIGURATION_SABBATH_MODE -> {
                    Logd("HMI_KEY","HMI Keys disable for Sabbath Mode --> All Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = false, cleanLight = false)
                }

                KEY_CONFIGURATION_DEMO_MODE -> {
                    Logd("HMI_KEY","HMI Keys disable for Demo Mode --> Home and Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }
                KEY_CONFIGURATION_DEMO_MODE_CLOCK -> {
                    Logd("HMI_KEY","HMI Keys disable for Demo Mode Clock --> Cancel and Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                }
                KEY_CONFIGURATION_DEMO_MODE_HOME-> {
                    Logd("HMI_KEY","HMI Keys disable for Demo Mode home --> Clean and Cancel Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = true, cancelLight = false, cleanLight = false)
                }
                KEY_CONFIGURATION_DEMO_MODE_LANDING-> {
                    Logd("HMI_KEY","HMI Keys disable for Demo Mode landing --> Home and Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                }
                KEY_CONFIGURATION_CONNECT_TO_NETWORK -> {
                    Logd("HMI_KEY","HMI Keys disable for Connect to Network --> All Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = false, cleanLight = false)
                }

                KEY_CONFIGURATION_SELF_CLEAN -> {
                    Logd("HMI_KEY","HMI Keys disable for Self Clean --> Home Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                }
                KEY_CONFIGURATION_SELF_CLEAN_RUNNING -> {
                    Logd("HMI_KEY","HMI Keys disable for Self Clean Running --> Home and Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }

                KEY_CONFIGURATION_DURING_DOOR_LOCK -> {
                    Logd("HMI_KEY","HMI Keys disable for During door lock --> All Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = false, cleanLight = false)
                }
                KEY_CONFIGURATION_SERVICE -> {
                    Logd("HMI_KEY","HMI Keys disable for Service Diagnostics --> All Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = false, cleanLight = false)
                }
                KEY_CONFIGURATION_FAULT_BLOCKING -> {
                    Logd("HMI_KEY","HMI Keys disable for Fault Blocking --> All Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = false, cleanLight = false)
                }
                KEY_CONFIGURATION_FAULT_A_C -> {
                    Logd("HMI_KEY","HMI Keys disable for Fault A-C --> All Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                    CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = false, cleanLight = false)
                }
                KEY_CONFIGURATION_FAULT_B2 -> {
                    Logd("HMI_KEY","HMI Keys disable for Fault B2 --> Home and Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }
                KEY_CONFIGURATION_POPUPS -> {
                    Logd("HMI_KEY","HMI Keys disable for Popups --> Home and Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }
                KEY_CONFIGURATION_POPUPS_PRIORITY -> {
                    Logd("HMI_KEY","HMI Keys disable for Popups Priority --> Home and Clean Button\n----------------")
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                    hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                }
                KEY_CONFIGURATION_POPUPS_IDLE -> {
                    if(CookingAppUtils.isSelfCleanFlow()){
                        Logd("HMI_KEY","HMI Keys disable for Popups Self Clean--> Home Disable and Clean and Cancel Enable Button\n----------------")
                        hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL, INSTANCE_CAVITY_0)
                        hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CLEAN, INSTANCE_CAVITY_0)
                        hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME, INSTANCE_CAVITY_0)
                        CookingAppUtils.manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = true)
                    } else {
                        val (executionState, isTimeCompleted, activeStates) = getActiveRunningState()
                        if (executionState in activeStates || isTimeCompleted) {
                            Logd("HMI_KEY","HMI Keys disable for Popups Running/Complete state --> Cancel Enable Button \n----------------")
                            hmiExpansionViewModel?.makeButtonValidFor(FUNCTION_CANCEL,INSTANCE_CAVITY_0)
                            CookingAppUtils.manageHMIPanelLights(homeLight = false,cancelLight = true,cleanLight = false)
                        } else {
                            Logd("HMI_KEY","HMI Keys disable for Popups Idle state--> All Button\n----------------")
                            hmiExpansionViewModel?.makeButtonInvalidFor(FUNC_HOME,INSTANCE_CAVITY_0)
                            hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CANCEL,INSTANCE_CAVITY_0)
                            hmiExpansionViewModel?.makeButtonInvalidFor(FUNCTION_CLEAN,INSTANCE_CAVITY_0)
                            CookingAppUtils.manageHMIPanelLights(homeLight = false,cancelLight = false,cleanLight = false)
                        }
                    }
                }

                else -> Logd("This particular feature has not handled for disabling the HMI Key logic")
            }
        }

        /**
         * get active running state
         */
        private fun getActiveRunningState(): Triple<RecipeExecutionState?, Boolean, Set<RecipeExecutionState>> {
            var cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
            if (cookingViewModel == null) cookingViewModel =
                CookingViewModelFactory.getOutOfScopeCookingViewModel()
            val executionState =
                cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value
            val isTimeCompleted =
                cookingViewModel.recipeExecutionViewModel.cookTimerState.value == Timer.State.COMPLETED
            val activeStates = setOf(
                RecipeExecutionState.RUNNING,
                RecipeExecutionState.RUNNING_EXT,
                RecipeExecutionState.DELAYED,
                RecipeExecutionState.PAUSED,
                RecipeExecutionState.PAUSED_EXT
            )
            return Triple(executionState, isTimeCompleted, activeStates)
        }

        /**
         * Method is responsible for handling the HMI key configuration accroding to recipe state
         * IDLE - Disabled Home,Clean and Cancel buttons
         * RUNNING - Disabled Home and Clean buttons
         * RUNING - Enabled Cancel button
         */
        fun hmiKeyEnableDisablePopupsConfiguration() {
            var cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
            if (cookingViewModel == null) cookingViewModel = CookingViewModelFactory.getOutOfScopeCookingViewModel()

            cookingViewModel?.let {
                when {
                    cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE
                    || cookingViewModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.IDLE
                    || cookingViewModel.recipeExecutionViewModel.cookTimerState.value == Timer.State.IDLE -> {

                        Logd("HMI_KEY","RecipeExecutionState.IDLE --> on Popup disable key\n----------------")
                        disableFeatureHMIKeys(KEY_CONFIGURATION_POPUPS_IDLE)
                    }
                    else -> {
                        Logd("HMI_KEY", "RecipeExecutionState.RUNNING --> on Popup enable key\n----------------")
                        disableFeatureHMIKeys(KEY_CONFIGURATION_POPUPS)
                        enableFeatureHMIKeys(KEY_CONFIGURATION_POPUPS)
                    }
                }
            }
        }
        /**
         * Method is responsible for handling the HMI key configuration accroding to recipe state
         * Demo mode - Enabled Home and Cancel button
         * Control Lock - Enabled Cancel button
         * RUNING - Enabled Cancel button
         */
        fun hmiKeyEnableDisableAfterPopupDestroyed() {
            Logd("HMI_KEY","Dismiss Popup Unboxing State --> Disable All buttons\n----------------")
            if(SettingsManagerUtils.isUnboxing){
                return
            }
            var cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
            if(cookingViewModel == null) cookingViewModel =  CookingViewModelFactory.getOutOfScopeCookingViewModel()

            cookingViewModel?.let {
                val (executionState, isTimeCompleted, activeStates) = getActiveRunningState()
                when {
                    (executionState in activeStates || isTimeCompleted) -> {
                        val currentFragment = getVisibleFragment(activity.supportFragmentManager)
                        when (currentFragment) {
                            is DoorLockingIntermediateLoader -> {
                                Logd("HMI_KEY","Dismiss Popup State Door Locking --> Disable and enable buttons\n----------------")
                                disableFeatureHMIKeys(KEY_CONFIGURATION_DURING_DOOR_LOCK)
                                enableFeatureHMIKeys(KEY_CONFIGURATION_DURING_DOOR_LOCK)
                            }
                            is AbstractTemperatureNumberPadFragment -> {
                                Logd("HMI_KEY","Dismiss Popup State Temperature Numpad --> Disable and enable buttons\n----------------")
                                if(CookingAppUtils.isSelfCleanFlow()) {
                                    disableFeatureHMIKeys(KEY_CONFIGURATION_SELF_CLEAN)
                                    enableFeatureHMIKeys(KEY_CONFIGURATION_SELF_CLEAN)
                                }else{
                                    disableFeatureHMIKeys(KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
                                    enableFeatureHMIKeys(KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
                                }
                            }
                            else -> {
                                Logd("HMI_KEY","Dismiss Popup State RUNNING/COMPLETED/PAUSED/DELAYED --> Disable and enable buttons\n----------------")
                                disableFeatureHMIKeys(KEY_CONFIGURATION_RUNNING)
                                enableFeatureHMIKeys(KEY_CONFIGURATION_RUNNING)
                            }
                        }

                    }
                    SettingsViewModel.getSettingsViewModel()?.controlLock?.value == true -> {
                        Logd("HMI_KEY", "Dismiss Popup controlLock true --> Disable and enable buttons\n----------------")
                        disableFeatureHMIKeys(KEY_CONFIGURATION_CONTROL_LOCK)
                        enableFeatureHMIKeys(KEY_CONFIGURATION_CONTROL_LOCK)
                    }
                    CookingAppUtils.isDemoModeEnabled() -> {
                        val currentFragment = getVisibleFragment(activity.supportFragmentManager)
                        when (currentFragment) {
                            is DemoModeCodeFragment -> {
                                Logd("HMI_KEY", "Dismiss Popup Demo Mode Numpad--> Disable and enable buttons\n----------------")
                                disableFeatureHMIKeys(KEY_CONFIGURATION_SETTING_LANDING)
                                enableFeatureHMIKeys(KEY_CONFIGURATION_SETTING_LANDING)
                            }
                            else -> {
                                Logd("HMI_KEY", "Dismiss Popup Demo Mode On--> Disable and enable buttons\n----------------")
                                disableFeatureHMIKeys(KEY_CONFIGURATION_DEMO_MODE_CLOCK)
                                enableFeatureHMIKeys(KEY_CONFIGURATION_DEMO_MODE_CLOCK)
                            }
                        }


                    }
                    else -> {
                        val currentFragment = getVisibleFragment(activity.supportFragmentManager)
                        when (currentFragment) {
                            is AbstractClockFragment -> {
                                Logd("HMI_KEY", "Dismiss Popup Clock--> Disable and enable buttons\n----------------")
                                disableFeatureHMIKeys(KEY_CONFIGURATION_CLOCK_SCREEN)
                                enableFeatureHMIKeys(KEY_CONFIGURATION_CLOCK_SCREEN)
                            }

                            is SettingsLandingFragment -> {
                                Logd("HMI_KEY", "Dismiss Popup Settings --> Disable and enable buttons\n----------------")
                                disableFeatureHMIKeys(KEY_CONFIGURATION_SETTING_LANDING)
                                enableFeatureHMIKeys(KEY_CONFIGURATION_SETTING_LANDING)
                            }
                            is UnBoxingDoneCongratulationFragment -> {
                                Logd("HMI_KEY", "Dismiss Popup Unboxing --> Disable all buttons\n----------------")
                                disableFeatureHMIKeys(KEY_CONFIGURATION_DIGITAL_UNBOXING)
                            }
                            is KitchenTumblerListTimerFragment -> {
                                Logd("HMI_KEY", "Dismiss Popup Kitchen Time --> Disable and Unable buttons\n----------------")
                                disableFeatureHMIKeys(KEY_CONFIGURATION_KITCHEN_TIMER)
                                enableFeatureHMIKeys(KEY_CONFIGURATION_KITCHEN_TIMER)
                            }
                            else -> {
                                if (CookingAppUtils.isSettingsFlow()) {
                                    if (CookingAppUtils.getFactoryRestoreStarted() || DiagnosticsManager.getInstance().isDiagnosticsModeActive) {
                                        Logd("HMI_KEY","Dismiss Popup restore factory Flow or in service mode popup--> Disable and enable buttons\n----------------")
                                        disableFeatureHMIKeys(KEY_CONFIGURATION_SERVICE)
                                        CookingAppUtils.setFactoryRestoreStarted(false)
                                    } else {
                                        Logd("HMI_KEY","Dismiss Popup Settings Flow--> Disable and enable buttons\n----------------")
                                        disableFeatureHMIKeys(KEY_CONFIGURATION_SETTING_LANDING)
                                        enableFeatureHMIKeys(KEY_CONFIGURATION_SETTING_LANDING)
                                    }
                                } else if (CookingAppUtils.isSelfCleanFlow()) {
                                    disableFeatureHMIKeys(KEY_CONFIGURATION_SELF_CLEAN_RUNNING)
                                    enableFeatureHMIKeys(KEY_CONFIGURATION_SELF_CLEAN_RUNNING)
                                    Logd("HMI_KEY", "Dismiss Popup Self Clean Flow--> Disable and enable buttons\n----------------")
                                }else {
                                    Logd("HMI_KEY", "Dismiss Popup Programming Mode--> ${cookingViewModel.doorState.value}\n----------------")
                                    if(cookingViewModel.doorState.value == true){
                                        disableFeatureHMIKeys(KEY_CONFIGURATION_POPUPS_PRIORITY)
                                        enableFeatureHMIKeys(KEY_CONFIGURATION_POPUPS_PRIORITY)
                                    } else{
                                        disableFeatureHMIKeys(KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
                                        enableFeatureHMIKeys(KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
                                    }
                                }

                            }
                        }

                    }
                }
            }

        }

        /*
        *  Reset knob selection handler
        * */
        private fun resetKnobSelectionHandler(knobID: Int){
            when(knobID) {
                LEFT_KNOB_ID -> {
                    leftKnobhandler.removeCallbacksAndMessages(null)
                    leftKnobhandler.postDelayed({
                        Logd(
                            "Left knob handler",
                            "knob time out: after 10 sec"
                        )
                        leftKnobhandler.removeCallbacksAndMessages(null)
                        if (LEFT_KNOB_ID != AppConstants.DEFAULT_LEFT_KNOB_ID) {
                            hmiKnobInteractionListener?.onKnobSelectionTimeout(RIGHT_KNOB_ID)
                        } else {
                            hmiKnobInteractionListener?.onKnobSelectionTimeout(LEFT_KNOB_ID)
                        }
                        setKnobLedAnimationOffAndLightState(false, FUNC_KNOB_LEFT_ROTATE)
                    }, KNOB_SELECTION_TIME_OUT.toLong())
                }
                RIGHT_KNOB_ID -> {
                    rightKnobhandler.removeCallbacksAndMessages(null)
                    rightKnobhandler.postDelayed({
                        Logd(
                            "Right knob handler",
                            "knob time out: after 10 sec"
                        )
                        rightKnobhandler.removeCallbacksAndMessages(null)
                        if (LEFT_KNOB_ID != AppConstants.DEFAULT_LEFT_KNOB_ID) {
                            hmiKnobInteractionListener?.onKnobSelectionTimeout(LEFT_KNOB_ID)
                        } else {
                            hmiKnobInteractionListener?.onKnobSelectionTimeout(RIGHT_KNOB_ID)
                        }
                        setKnobLedAnimationOffAndLightState(false, FUNC_KNOB_RIGHT_ROTATE)
                    }, KNOB_SELECTION_TIME_OUT.toLong())
                }
            }
        }

        // Starts a slow blinking timeout for the knob LEDs, checking the cycle state after 10 minutes.
        fun startKnobSlowBlinkingTimeout() {
            knobSlowBlinkingTimeoutHandler = Handler(Looper.getMainLooper())
            knobSlowBlinkingTimeoutRunnable = Runnable {
                if (isAnyCycleRunning()) {
                    setBothKnobLightOn()
                } else {
                    setBothKnobLightOff()
                }
                isKnobSlowBlinkingTimeoutRunning = false
                knobSlowBlinkingTimeoutRunnable?.let {
                    knobSlowBlinkingTimeoutHandler?.removeCallbacks(
                        it
                    )
                }
            }
            knobSlowBlinkingTimeoutRunnable?.let {
                knobSlowBlinkingTimeoutHandler?.postDelayed(
                    it,
                    KNOB_SELECTION_TIME_OUT_TEN_MIN.toLong()
                )
                isKnobSlowBlinkingTimeoutRunning = true
            }
        }

        // Cancels the slow blinking timeout if user interaction is detected during the timeout period.
        fun userInteractWithinSlowBlinkingTimeoutElapsed() {
            if (isAnyCycleRunning()) {
                setBothKnobLightOnDirectly()
            } else {
                setBothKnobLightOffDirectly()
            }

            knobSlowBlinkingTimeoutRunnable?.let {
                knobSlowBlinkingTimeoutHandler?.removeCallbacks(it)
                isKnobSlowBlinkingTimeoutRunning = false
            }
            knobSlowBlinkingTimeoutHandler = null
            knobSlowBlinkingTimeoutRunnable = null
        }

        // Checks if the slow blinking timeout is active.
        fun isSlowBlinkingKnobTimeoutActive(): Boolean {
            return isKnobSlowBlinkingTimeoutRunning
        }

        // Starts a fast blinking timeout for the knob LEDs, checking the cycle state after 10 minutes.
        fun startKnobFastBlinkingTimeout() {
            knobFastBlinkingTimeoutHandler = Handler(Looper.getMainLooper())
            knobFastBlinkingTimeoutRunnable = Runnable {
                if (isAnyCycleRunning()) {
                    setBothKnobLightOn()
                } else {
                    setBothKnobLightOff()
                }
                isKnobFastBlinkingTimeoutRunning = false
                knobFastBlinkingTimeoutRunnable?.let {
                    knobFastBlinkingTimeoutHandler?.removeCallbacks(
                        it
                    )
                }
            }
            knobFastBlinkingTimeoutRunnable?.let {
                knobFastBlinkingTimeoutHandler?.postDelayed(
                    it,
                    KNOB_SELECTION_TIME_OUT_TEN_MIN.toLong()
                )
                isKnobFastBlinkingTimeoutRunning = true
            }
        }

        // Cancels the fast blinking timeout if user interaction is detected during the timeout period.
        fun userInteractWithinFastBlinkingTimeoutElapsed() {
            if (isAnyCycleRunning()) {
                setBothKnobLightOnDirectly()
            } else {
                setBothKnobLightOffDirectly()
            }

            knobFastBlinkingTimeoutRunnable?.let {
                knobFastBlinkingTimeoutHandler?.removeCallbacks(it)
                isKnobFastBlinkingTimeoutRunning = false
            }
            knobFastBlinkingTimeoutHandler = null
            knobFastBlinkingTimeoutRunnable = null
        }

        // Checks if the fast blinking timeout is active.
        fun isFastBlinkingKnobTimeoutActive(): Boolean {
            return isKnobFastBlinkingTimeoutRunning
        }

        // Special handling for Fade in transition, Pause state, Slow blinking
        fun setBothKnobLightOffDirectly(){
            if (SharedPreferenceManager.getKnobLightStatusIntoPreference().toBoolean() && SettingsViewModel.getSettingsViewModel().sabbathMode.value == SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT) {
                hmiExpansionViewModel?.stopLightAnimationFor(
                    FUNC_KNOB_LED_SET_LEFT,
                    getOffsetId()
                )
                hmiExpansionViewModel?.setLightFor(
                    FUNC_KNOB_LED_SET_LEFT, getOffsetId(), false, BUTTON_LIGHT_ON
                )
                hmiExpansionViewModel?.stopLightAnimationFor(
                    FUNC_KNOB_LED_SET_RIGHT,
                    getOffsetId()
                )
                hmiExpansionViewModel?.setLightFor(
                    FUNC_KNOB_LED_SET_RIGHT, getOffsetId(), false, BUTTON_LIGHT_ON
                )
            }
        }

        // Special handling for Sabbath
        fun setBothKnobLightOffSabbath(){
                hmiExpansionViewModel?.stopLightAnimationFor(
                    FUNC_KNOB_LED_SET_LEFT,
                    getOffsetId()
                )
                hmiExpansionViewModel?.setLightFor(
                    FUNC_KNOB_LED_SET_LEFT, getOffsetId(), false, BUTTON_LIGHT_ON
                )
                hmiExpansionViewModel?.stopLightAnimationFor(
                    FUNC_KNOB_LED_SET_RIGHT,
                    getOffsetId()
                )
                hmiExpansionViewModel?.setLightFor(
                    FUNC_KNOB_LED_SET_RIGHT, getOffsetId(), false, BUTTON_LIGHT_ON
                )
        }

        // Special handling for Fade in transition, Pause state, Slow blinking
        fun setBothKnobLightOnDirectly(){
            if (SharedPreferenceManager.getKnobLightStatusIntoPreference().toBoolean() && SettingsViewModel.getSettingsViewModel().sabbathMode.value == SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT) {
                hmiExpansionViewModel?.stopLightAnimationFor(
                    FUNC_KNOB_LED_SET_LEFT,
                    getOffsetId()
                )
                hmiExpansionViewModel?.setLightFor(
                    FUNC_KNOB_LED_SET_LEFT, getOffsetId(), true, BUTTON_LIGHT_ON
                )
                hmiExpansionViewModel?.stopLightAnimationFor(
                    FUNC_KNOB_LED_SET_RIGHT,
                    getOffsetId()
                )
                hmiExpansionViewModel?.setLightFor(
                    FUNC_KNOB_LED_SET_RIGHT, getOffsetId(), true, BUTTON_LIGHT_ON
                )
            }
        }
    }

    /* **********  HMI Error Codes Interaction Listener Interfaces ********** */
    interface HMIErrorCodesListener {
        fun onHMIFaultId(cookingViewModel: CookingViewModel, faultId: Int?)
        fun onHMICommunicationFaultCode(cookingViewModel: CookingViewModel, communicationFaultCode: String?)
    }

    /* **********  User Interaction Listener Interfaces ********** */
    /**
     * Use this to receive [.onUserInteraction] callbacks in fragment.
     */
    fun interface UserInteractionListener {
        fun onUserInteraction()
    }

    /* **********  HMI Clean Button Interaction Listener Interfaces ********** */
    fun interface HMICleanButtonInteractionListener {
        fun onHMICleanButtonInteraction()
    }

    /* **********  HMI Cancel Button Interaction Listener Interfaces ********** */
    fun interface HMICancelButtonInteractionListener {
        fun onHMICancelButtonInteraction()
    }

    /* **********  HMI Home Button Interaction Listener Interfaces ********** */
    fun interface HMIHomeButtonInteractionListener {
        fun onHMIHomeButtonInteraction()
    }
}