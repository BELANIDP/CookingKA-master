package core.utils

import android.os.Handler
import android.os.Looper
import android.presenter.activity.KitchenAidLauncherActivity
import android.view.WindowManager
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.standby.SleepListener
import com.whirlpool.hmi.standby.SleepListenerInConnectedMode
import com.whirlpool.hmi.standby.SleepManager
import com.whirlpool.hmi.standby.SleepManagerInConnectedMode
import com.whirlpool.hmi.standby.WakeUpState
import com.whirlpool.hmi.standby.WakeUpState.AWAKE
import com.whirlpool.hmi.standby.WakeUpState.SLEEP
import com.whirlpool.hmi.standby.WakeUpState.SLEEP_FAILURE
import com.whirlpool.hmi.standby.WakeUpState.WAKING_UP
import com.whirlpool.hmi.standby.WakeUpState.WAKING_UP_FAILURE
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.standby.StandbyConnectedModeState
import com.whirlpool.hmi.utils.standby.StandbyConnectedModeState.REMOTE_AWAKE
import com.whirlpool.hmi.utils.standby.StandbyConnectedModeState.WAKEUP_FAILURE
import core.utils.AppConstants.SLEEP_MODE
import core.utils.SettingsManagerUtils.changeCurrentBrightness
import core.utils.SettingsManagerUtils.getCurrentBrightness
import core.utils.SettingsManagerUtils.isApplianceProvisioned
import core.utils.SettingsManagerUtils.isPreviouslyInSleepMode
import core.utils.SettingsManagerUtils.restoreBrightness
import core.utils.SettingsManagerUtils.restoreSleepState
import core.utils.SettingsManagerUtils.setBrightnessForConnectedSleepMode
import core.utils.SettingsManagerUtils.setPreviouslyInSleepMode
import core.utils.TimeUtils.SLEEP_BRIGHTNESS

/**
 * File       : core.utils.LowPowerModeUtils.
 * Brief      : Contains helper utility methods for sleep API's.
 * Author     : NIMMAM
 * Created On : 10.07.2024.
 * Details    : Sleep API's from View side.
 */
class LowPowerModeUtils {
    companion object {
        /**
         * Sleep Listener for Connected Mode Sleep API's
         */
        private var sleepListenerInConnectedMode: SleepListenerInConnectedMode =
            SleepListenerInConnectedMode { state ->
                if (isApplianceProvisioned()) {
                    HMILogHelper.Logi(
                        SLEEP_MODE,
                        "Sleep Mode::StandbyConnectedModeState = $state"
                    )
                    when (state) {
                        StandbyConnectedModeState.AWAKE, REMOTE_AWAKE ->
                            if (isPreviouslyInSleepMode()) {
                                HMILogHelper.Logd(SLEEP_MODE, "entered the awake mode")
                                restoreBrightness()
                                NavigationUtils.getVisibleFragment()
                                    ?.let { CookingAppUtils.navigateToStatusOrClockScreen(it) }
                                setPreviouslyInSleepMode(false)
                            }

                        StandbyConnectedModeState.SLEEP_FAILURE -> restoreSleepState()
                        WAKEUP_FAILURE ->
                            /*If we receive Waking up failure, we are trying to wakeup again */
                            if (isPreviouslyInSleepMode()) {
                                HMILogHelper.Logd(
                                    SLEEP_MODE,
                                    "entered the awake mode in WAKEUP_FAILURE"
                                )
                                SleepManagerInConnectedMode.getInstance().wakeup()
                            }

                        else -> {
                            HMILogHelper.Logi(
                                SLEEP_MODE,
                                "StandbyConnectedModeState unused states"
                            )
                        }
                    }
                }
            }

        /**
         * Sleep Listener for Non Connected Mode Sleep API's
         */
        private var sleepListenerNonConnectedMode: SleepListener = object : SleepListener {
            @Deprecated("Deprecated in Java")
            override fun onGoToSleepStatus(status: Boolean) {
                // Deprecated
            }

            override fun publishStatus(wakeUpState: WakeUpState) {
                HMILogHelper.Logi(SLEEP_MODE, "Sleep Mode::publish status $wakeUpState")

                when (wakeUpState) {
                    WAKING_UP_FAILURE -> {
                        /*If we receive Waking up failure, we are trying to wakeup again */
                        if (isPreviouslyInSleepMode()) {
                            SleepManager.getInstance().wakeupFromApplicationOnResume()
                        }
                    }

                    AWAKE -> {
                        if (isPreviouslyInSleepMode()) {
                            CookingAppUtils.getVisibleFragment(
                                ContextProvider.getFragmentActivity()
                                    ?.supportFragmentManager
                            )
                                ?.let { CookingAppUtils.navigateToStatusOrClockScreen(it) }
                            setPreviouslyInSleepMode(false)
                        }
                        CookingAppUtils.startGattServer(NavigationUtils.getVisibleFragment())
                    }

                    SLEEP -> {
                        if (isPreviouslyInSleepMode()) {
                            CookingAppUtils.stopGattServer()
                        }
                    }

                    else -> {
                        HMILogHelper.Logi(SLEEP_MODE, "Sleep Mode:: remaining states")
                    }
                }
            }
        }

        /**
         * Low Power mode initialization
         */
        fun initLowPowerMode() {
            if (CookingViewModelFactory.getProductVariantEnum() ==
                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN
            ) {
                SleepManager.getInstance()
                SleepManagerInConnectedMode.getInstance()
                SleepManager.getInstance().registerSleepListener(sleepListenerNonConnectedMode)
                registerSleepListeners()
                HMILogHelper.Logd(SLEEP_MODE, "Sleep Mode:: initLowPowerMode ")
            }
        }

        /**
         * Register Sleep Listeners for Low power mode
         */
        private fun registerSleepListeners() {
            SleepManagerInConnectedMode.getInstance()
                .addSleepListenerInConnectedMode(sleepListenerInConnectedMode)
            SleepManager.getInstance().registerSleepListener(sleepListenerNonConnectedMode)
        }

        /**
         * Check's if it safe to enter in Sleep mode
         */
        fun isSafeToEnterLowPower(): Boolean {
            var isSafeToEnter = true
            val otaViewModel = OTAVMFactory.getOTAViewModel()
            HMILogHelper.Logd(
                SLEEP_MODE,
                "Sleep Mode:: isSafeToEnterLowPower :: OTAState " + otaViewModel.otaState.value
            )
            val cookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
            // ToDo: add conditions for ota error
            if (CookingAppUtils.isDemoModeEnabled()
                || CookingAppUtils.checkApplianceBusyState()
                || cookingViewModel.doorState.value == true
                || cookingViewModel.lightState.value == true
                || otaViewModel.otaState.value == OTAStatus.BUSY
            ) {
                isSafeToEnter = false
            }
            return isSafeToEnter
        }

        /**
         * Entering sleep mode for connected and non connected mode.
         */
        fun enterSleepMode() {
            HMILogHelper.Logd(SLEEP_MODE, "Sleep Mode::enterSleepMode")
            if (isApplianceProvisioned()) {
                enterSleepForConnectedMode()
            } else {
                enterSleepForNonConnectedMode()
            }
        }

        /**
         * Entering sleep mode for connected mode.
         */
        private fun enterSleepForConnectedMode() {
            if (isApplianceAwakeInConnected()) {
                val brightness: Int = getCurrentBrightness()
                HMILogHelper.Logd(
                    SLEEP_MODE,
                    "Sleep Mode::enterSleepForConnectedMode -> $brightness"
                )
                SleepManagerInConnectedMode.getInstance().goToSleep()
                setPreviouslyInSleepMode(true)
                changeCurrentBrightness(SLEEP_BRIGHTNESS)
                setBrightnessForConnectedSleepMode(brightness)
            } else {
                HMILogHelper.Logd(
                    SLEEP_MODE,
                    "Sleep Mode::enterSleepForConnectedMode already in awake mode"
                )
            }
        }

        /**
         * Entering sleep mode for Non Connected mode.
         */
        private fun enterSleepForNonConnectedMode() {
            if (isApplianceAwakeInNonConnected()) {
                HMILogHelper.Logi(SLEEP_MODE, "Sleep Mode::enterSleepForNonConnectedMode")
                SleepManager.getInstance().goToSleep(
                    SLEEP_BRIGHTNESS,
                    TimeUtils.SLEEP_TIMEOUT_MS,
                    TimeUtils.PERIODIC_SLEEP_TIME_MS
                )
                setPreviouslyInSleepMode(true)
            }
        }

        /**
         * Check appliance state in non connected mode
         */
        fun isApplianceAwakeInNonConnected(): Boolean {
            var applianceAwake = false
            val wakeupState: WakeUpState = SleepManager.getInstance().currentStatus
            HMILogHelper.Logd(SLEEP_MODE, "wakeup state is $wakeupState")
            if (wakeupState == AWAKE || wakeupState == SLEEP_FAILURE) {
                applianceAwake = true
            }
            return applianceAwake
        }

        /**
         * Check appliance state in non connected mode
         */
        private fun isApplianceWakingUpInNonConnected(): Boolean {
            var applianceAwake = false
            val wakeupState: WakeUpState = SleepManager.getInstance().currentStatus
            if (wakeupState == AWAKE || wakeupState == WAKING_UP) {
                applianceAwake = true
            }
            return applianceAwake
        }

        /**
         * Check appliance state in non connected mode
         */
        private fun isApplianceInSleepNonConnected(): Boolean {
            var applianceInSleep = false
            val wakeupState: WakeUpState = SleepManager.getInstance().currentStatus
            if (wakeupState == SLEEP) {
                applianceInSleep = true
            }
            return applianceInSleep
        }

        /**
         * Check appliance state in connected mode
         */
        fun isApplianceInNotGoingToSleepConnected(): Boolean {
            var applianceInSleep = true
            val wakeupState: StandbyConnectedModeState =
                SleepManagerInConnectedMode.getInstance().status
            if (wakeupState == StandbyConnectedModeState.GOING_TO_SLEEP) {
                applianceInSleep = false
            }
            return applianceInSleep
        }

        /**
         * Check Appliance state in Connected mode
         */
        fun isApplianceAwakeInConnected(): Boolean {
            var applianceAwake = false
            val wakeupState: StandbyConnectedModeState =
                SleepManagerInConnectedMode.getInstance().status
            HMILogHelper.Logd(SLEEP_MODE, "Sleep Mode::Appliance state::$wakeupState")
            if (wakeupState == StandbyConnectedModeState.AWAKE || wakeupState == REMOTE_AWAKE
                || wakeupState == StandbyConnectedModeState.SLEEP_FAILURE
            ) {
                applianceAwake = true
            }
            return applianceAwake
        }

        /**
         * To wake the appliance from sleep mode
         */
        fun wakeUpFromSleep() {
            if (isPreviouslyInSleepMode()) {
                if (isApplianceProvisioned()) {
                    wakeUpConnectedMode()
                } else {
                    wakeUpNonConnectedMode()
                }
            } else {
                HMILogHelper.Logi(SLEEP_MODE, "Sleep Mode::Appliance was already Waked Up.")
            }
        }

        /**
         * To wake the appliance from sleep mode in Connected Mode
         */
        fun wakeUpConnectedMode() {
            SleepManagerInConnectedMode.getInstance().wakeup()
            restoreBrightness()
        }

        /**
         * To wake the appliance from sleep mode in Non Connected Mode
         */
        fun wakeUpNonConnectedMode() {
            if (isApplianceInSleepNonConnected()) {
                SleepManager.getInstance().wakeupFromApplicationOnResume()
            }
        }

        /**
         * Restoring MainActivity flags after resuming from Sleep
         */
        fun postResumeSleep(kitchenAidLauncherActivity: KitchenAidLauncherActivity) {
            if (CookingViewModelFactory.getProductVariantEnum() ==
                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN &&
                !isApplianceProvisioned() && isApplianceWakingUpInNonConnected()
            ) {
                HMILogHelper.Logi(
                    SLEEP_MODE,
                    "Sleep Mode::onPostResume-> changing flags for keep screen on"
                )
                kitchenAidLauncherActivity.window.clearFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                )
                Handler(Looper.getMainLooper()).postDelayed({
                    kitchenAidLauncherActivity.window
                        .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }, 500)
                Handler(Looper.getMainLooper()).postDelayed({
                    HMILogHelper.Logi(
                        SLEEP_MODE,
                        "Set Clock Button Configurations"
                    )
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN)
                }, 500)

            }
        }
    }
}