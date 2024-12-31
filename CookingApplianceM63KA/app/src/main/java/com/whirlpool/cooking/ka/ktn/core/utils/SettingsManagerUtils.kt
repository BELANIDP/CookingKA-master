package core.utils

import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.settings.SettingsViewModel.getSettingsViewModel
import core.utils.AppConstants.SLEEP_MODE
import core.utils.TimeUtils.SLEEP_BRIGHTNESS


object SettingsManagerUtils {

    //Date and time settings variables
    private const val KEY_AUTO_MODE: String = "AutoMode"
    private const val KEY_TEMPERATURE_FORMAT: String = "TemperatureFormat"
    private const val KEY_WEIGHT_FORMAT: String = "WeightFormat"
    
    
    var isUnboxing: Boolean
        get() = getSettingsViewModel().isUnboxing.getValue() == true
        set(isUnboxing) {
            getSettingsViewModel().setUnboxing(isUnboxing)
        }
    val isBleProvisionSuccess: Boolean
        /**
         * This should check the Wifi provisioned state as connected and also for the internet access
         *
         * @return true for successful provisioning state
         */
        get() {
            if (getSettingsViewModel().wifiConnectState.value == SettingsViewModel.WifiConnectState.CONNECTED && getSettingsViewModel().provisionedWifiSsid != null && getSettingsViewModel().awsConnectionStatus.value != SettingsViewModel.CloudConnectionState.IDLE) {
                return true
            } else {
                HMILogHelper.Loge("WifiConnectState is not CONNECTED")
            }
            return false
        }


    fun isApplianceProvisioned(): Boolean {
        val provisionedWifiSsid = getSettingsViewModel().provisionedWifiSsid
        return provisionedWifiSsid != null && provisionedWifiSsid != AppConstants.EMPTY_STRING
    }

    fun restoreSleepState() {
        if (CookingViewModelFactory.getProductVariantEnum() ==
            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN
        ) {
            restoreBrightness()
            setPreviouslyInSleepMode(false)
        }
    }

    /**
     * restore brightness in case of sleep failures.
     */
    fun restoreBrightness() {
        val currentBrightness = getCurrentBrightness()
        val brightnessOfConnected: Int = getBrightnessForConnectedSleepMode()
        if (currentBrightness == SLEEP_BRIGHTNESS && brightnessOfConnected > SLEEP_BRIGHTNESS
            && isPreviouslyInSleepMode() && isApplianceProvisioned()
        ) {
            HMILogHelper.Logd(SLEEP_MODE, " came inside changing brightness")
            changeCurrentBrightness(brightnessOfConnected)
            setBrightnessForConnectedSleepMode(SLEEP_BRIGHTNESS)
        }
    }

    /**
     * Change Brightness.
     */
    fun changeCurrentBrightness(value: Int) {
        getSettingsViewModel().setBrightness(value)
    }

    /**
     * Check for previously in sleep mode
     */
    fun isPreviouslyInSleepMode(): Boolean {
        HMILogHelper.Logd(SLEEP_MODE, " isPreviouslyInSleepMode called")
        return getSettingsViewModel()
            .getUserDataBooleanValue(AppConstants.IS_PREVIOUSLY_IN_SLEEP_MODE, true)
    }

    /**
     * set previously in sleep mode
     */
    fun setPreviouslyInSleepMode(value: Boolean) {
        HMILogHelper.Logd(SLEEP_MODE, " setPreviouslyInSleepMode called $value")
        getSettingsViewModel()
            .setUserDataBooleanValue(AppConstants.IS_PREVIOUSLY_IN_SLEEP_MODE, value, true)
    }

    /**
     * Get Current Brightness from system settings.
     */
    fun getCurrentBrightness(): Int {
        return getSettingsViewModel().brightness.value ?: SLEEP_BRIGHTNESS
    }

    /**
     * set last Brightness for Connected sleep mode
     */
    fun setBrightnessForConnectedSleepMode(value: Int) {
        HMILogHelper.Logd(SLEEP_MODE, " setBrightnessForConnectedSleepMode $value")
        getSettingsViewModel()
            .setUserDataIntValue(AppConstants.LAST_SAVED_BRIGHTNESS_IN_CONNECTED_MODE, value, false)
    }

    /**
     * get last Brightness for Connected sleep mode
     */
    private fun getBrightnessForConnectedSleepMode(): Int {
        return getSettingsViewModel()
            .getUserDataIntValue(AppConstants.LAST_SAVED_BRIGHTNESS_IN_CONNECTED_MODE, false)
    }

    internal fun getWeightUnitFormat(): WeightFormatSettings {
        val settingsViewModel = getSettingsViewModel()
        val userData = settingsViewModel.getUserDataStringValue(
            KEY_WEIGHT_FORMAT,
            false
        )
        return getWeightUnitFormat(userData)
    }

    private fun getWeightUnitFormat(userData: String?): WeightFormatSettings {
        var value = WeightFormatSettings.IMPERIAL
        try {
            if (userData != null) {
                value = WeightFormatSettings.valueOf(
                    userData
                )
            }
        } catch (exception: java.lang.IllegalArgumentException) {
            //just keep the default value, data is either corrupted or null;
        }
        return value
    }
    fun setWeightUnitFormat(setting: WeightFormatSettings): Boolean {
        val settingsViewModel = getSettingsViewModel()
        settingsViewModel.setUserDataStringValue(
            KEY_WEIGHT_FORMAT,
            setting.toString(),
            false
        )
        return when (setting) {
            WeightFormatSettings.METRIC -> settingsViewModel.setWeightUnit(
                SettingsViewModel.WeightUnit.METRIC
            )

            WeightFormatSettings.IMPERIAL -> settingsViewModel.setWeightUnit(
                SettingsViewModel.WeightUnit.IMPERIAL
            )

        }
    }

    enum class WeightFormatSettings {
        METRIC,
        IMPERIAL {
            override fun next(): WeightFormatSettings {
                return METRIC // see below for options for this line
            }
        };

        open fun next(): WeightFormatSettings {
            // No bounds checking required here, because the last instance overrides
            return WeightFormatSettings.values()[ordinal + 1]
        }
    }

    enum class TimeFormatSettings {
        H_24, H_12 {
            override operator fun next(): TimeFormatSettings {
                return H_24 // see below for options for this line
            }
        };

        open operator fun next(): TimeFormatSettings {
            // No bounds checking required here, because the last instance overrides
            return values()[ordinal + 1]
        }
    }


    enum class DateFormatSettings {
        MMDDYY,
        DDMMYY {
            override fun next(): DateFormatSettings {
                return MMDDYY // see below for options for this line
            }
        };

        open fun next(): DateFormatSettings {
            // No bounds checking required here, because the last instance overrides
            return DateFormatSettings.values()[ordinal + 1]
        }
    }

    enum class TemperatureFormatSettings {
        FAHRENHEIT,
        CELSIUS {
            override fun next(): TemperatureFormatSettings {
                return FAHRENHEIT // see below for options for this line
            }
        };

        open fun next(): TemperatureFormatSettings {
            // No bounds checking required here, because the last instance overrides
            return TemperatureFormatSettings.values()[ordinal + 1]
        }
    }

    enum class TimeAutoModeSettings {
        ON,
        OFF {
            override fun next(): TimeAutoModeSettings {
                return ON
            }
        };

        open fun next(): TimeAutoModeSettings {
            // No bounds checking required here, because the last instance overrides
            return TimeAutoModeSettings.values()[ordinal + 1]
        }
    }


    fun getTimeAutoModeFormat(): TimeAutoModeSettings {
        val settingsViewModel = getSettingsViewModel()
        HMILogHelper.Logd("SettingsViewModel.getSettingsViewModel$settingsViewModel")
        val userData = settingsViewModel.getUserDataStringValue(
            KEY_AUTO_MODE,
            false
        )
        var value = TimeAutoModeSettings.OFF
        try {
            if (userData != null) {
                value =
                    TimeAutoModeSettings.valueOf(userData)
            }
        } catch (exception: java.lang.IllegalArgumentException) {
            //just keep the default value, data is either corrupted or null;
        }
        return value
    }

    private fun setTimeAutoMode(setting: TimeAutoModeSettings): Boolean {
        val settingsViewModel = getSettingsViewModel()
        return settingsViewModel.setUserDataStringValue(
            KEY_AUTO_MODE,
            setting.toString(),
            false
        )
    }

    internal fun getTimeFormat(): TimeFormatSettings {
        val settingsViewModel = getSettingsViewModel()
        HMILogHelper.Logd("SettingsViewModel.getSettingsViewModel$settingsViewModel")
        val userData = settingsViewModel.getUserDataStringValue(
            BundleKeys.KEY_TIME_FORMAT,
            false
        )
        var value: TimeFormatSettings = TimeFormatSettings.H_12
        try {
            if (userData != null) {
                value = TimeFormatSettings.valueOf(userData)
            }
        } catch (exception: java.lang.IllegalArgumentException) {
            //just keep the default value, data is either corrupted or null;
        }
        return value
    }

    fun setTimeFormat(setting: TimeFormatSettings): Boolean {
        val settingsViewModel = getSettingsViewModel()
        if (setting === TimeFormatSettings.H_12) {
            settingsViewModel.set12HourTimeFormat(true)
        } else {
            settingsViewModel.set12HourTimeFormat(false)
        }
        return settingsViewModel.setUserDataStringValue(
            BundleKeys.KEY_TIME_FORMAT,
            setting.toString(),
            false
        )
    }

    internal fun getDateFormat(): DateFormatSettings {
        val settingsViewModel = getSettingsViewModel()
        val userData = settingsViewModel.getUserDataStringValue(
            BundleKeys.KEY_DATE,
            false
        )
        var value = DateFormatSettings.MMDDYY
        try {
            if (userData != null) {
                value = DateFormatSettings.valueOf(userData)
            }
        } catch (exception: java.lang.IllegalArgumentException) {
            //just keep the default value, data is either corrupted or null;
        }
        return value
    }
    /**
     * get selected Date Format based on option
     * @param setting MMDDYY/DDMMYY
     */
    fun setDateFormat(setting: DateFormatSettings): Boolean {
        val settingsViewModel = getSettingsViewModel()
        if (setting == DateFormatSettings.DDMMYY) {
            settingsViewModel.setDateFormat(TimeUtils.dayMonthFormat)
        } else {
            settingsViewModel.setDateFormat(TimeUtils.monthDayFormat)
        }
        return settingsViewModel.setUserDataStringValue(
            BundleKeys.KEY_DATE,
            setting.toString(),
            false
        )
    }

    private fun getTemperatureUnits(setting: TemperatureFormatSettings?): Int {
        return when (setting) {
            TemperatureFormatSettings.CELSIUS -> SettingsViewModel.TemperatureUnit.CELSIUS
            TemperatureFormatSettings.FAHRENHEIT -> SettingsViewModel.TemperatureUnit.FAHRENHEIT
            else -> -1
        }
    }

    internal fun getTemperatureFormat(): TemperatureFormatSettings {
        val settingsViewModel = getSettingsViewModel()
        val userData = settingsViewModel.getUserDataStringValue(
            KEY_TEMPERATURE_FORMAT,
            false
        )
        return getTemperatureFormat(userData)
    }

    private fun getTemperatureFormat(userData: String?): TemperatureFormatSettings {
        var value = TemperatureFormatSettings.FAHRENHEIT
        try {
            if (userData != null) {
                value = TemperatureFormatSettings.valueOf(
                    userData
                )
            }
        } catch (exception: java.lang.IllegalArgumentException) {
            //just keep the default value, data is either corrupted or null;
        }
        return value
    }


    fun setTemperatureFormat(setting: TemperatureFormatSettings): Boolean {
        val settingsViewModel = getSettingsViewModel()
        settingsViewModel.setUserDataStringValue(
            KEY_TEMPERATURE_FORMAT,
            setting.toString(),
            false
        )
        return when (setting) {
            TemperatureFormatSettings.CELSIUS -> settingsViewModel.setTemperatureUnit(
                SettingsViewModel.TemperatureUnit.CELSIUS
            )

            TemperatureFormatSettings.FAHRENHEIT -> settingsViewModel.setTemperatureUnit(
                SettingsViewModel.TemperatureUnit.FAHRENHEIT
            )

        }
    }

    fun isFavoritesKnobAssignCycleAvailableInFavoritesRecords(): Boolean {
        val favoriteRecord = CookBookViewModel.getInstance().getFavoriteRecordByFavoriteName(
            SharedPreferenceManager.getKnobAssignFavoritesCycleNameIntoPreference().toString()
        )
        return favoriteRecord != null
    }

    class Time internal constructor(val hours: Int, val minutes: Int, val seconds: Int)

}