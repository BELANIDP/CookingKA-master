package android.presenter.fragments.settings
/*
* ************************************************************************************************
* ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
* ************************************************************************************************
*/

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.fragments.digital_unboxing.AbstractDateTimeViewHolder
import android.presenter.fragments.digital_unboxing.SetupDateTimeViewHolder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.tools.util.KeyboardTextFormatUtils
import com.whirlpool.hmi.uicomponents.widgets.keyboard.Keyboard
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError
import core.jbase.AbstractDateTimeFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.SettingsManagerUtils.isUnboxing
import core.utils.SharedViewModel
import core.utils.TimeAndDateUtils
import core.utils.TimeUtils
import core.utils.visible
import java.util.Calendar
import java.util.Date
import java.util.Locale


/**
 * File       : android.presenter.fragments.settings.UnBoxingSetupLaterTime
 * Author     : Vijay Shinde
 * Created On : 14/10/2024
 * Details    : Sets the time for the operating system without connecting to the internet.
 */
class SettingsSetTimeNumpadFragment : AbstractDateTimeFragment(), HMIKnobInteractionListener,
        KeyboardInputManagerInterface {
    private var numberPadViewHolder: AbstractDateTimeViewHolder? = null
    private var keyboardViewModel: KeyboardViewModel? = null
    private var hour: String = AppConstants.EMPTY_STRING
    private var minute: String = AppConstants.EMPTY_STRING
    private var clockMode: String = AppConstants.EMPTY_STRING
    private var setDefaultUnboxingTimeValue = false
    private var isFormatChangeNotClicked = false
    private var sharedViewModel: SharedViewModel? = null
    private var knobRotationCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewHolder()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return numberPadViewHolder?.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dateTimeValue = AppConstants.EMPTY_STRING
        numberPadViewHolder?.onViewCreated(view, savedInstanceState)
        keyboardViewModel = getKeyboardViewModel(this)
        keyboardViewModel?.initKeyboard(this)
        keyboardViewModel?.keyboardAlpha =
                numberPadViewHolder?.provideKeyboardReference()?.let { Keyboard(requireContext(), it) }
        registerObservers()
        initializeView()
        checkForPreviousTimeValues()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
    }

    /**
     * init time view holder class
     * On header vertical tumbler callback
     */
    private fun initViewHolder() {
        numberPadViewHolder = SetupDateTimeViewHolder(this, onHeaderBarTumblerClick = {
            onNavigateToVerticalTumbler()
        }, onHeaderBarBackClick = {
            NavigationUtils.navigateSafely(
                    this,
                    R.id.action_settingsSetTimeNumpadFragment_to_settingsTimeAndDateFragment,
                    null,
                    null
            )
        })
    }

    /**
     * To set time format on click of Format selection listener
     *
     * @param is12HoursFormat true/false
     */
    private fun set12HourFormat(is12HoursFormat: Boolean) {
        when {
            is12HoursFormat -> {
                SettingsManagerUtils.setTimeFormat(SettingsManagerUtils.TimeFormatSettings.H_12)
                numberPadViewHolder?.provideDateTimeValueTextView()
                        ?.setClockFormat(KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR)
                clockFormat = KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR
            }

            else -> {
                SettingsManagerUtils.setTimeFormat(SettingsManagerUtils.TimeFormatSettings.H_24)
                numberPadViewHolder?.provideDateTimeValueTextView()
                        ?.setClockFormat(KeyboardTextFormatUtils.FORMAT_CLOCK_24_HOUR)
                clockFormat = KeyboardTextFormatUtils.FORMAT_CLOCK_24_HOUR
            }
        }
        updateDateTimeString()
        updateDateTimeValues(dateTimeValue)
        numberPadViewHolder?.provideDateTimeValueTextView()?.refreshView()
    }

    /**
     * set the hour, minute and clock Mode AM/PM in common Variable
     */
    private fun updateDateTimeString() {
        dateTimeValue = buildString {
            append(hour)
            append(AppConstants.EMPTY_STRING)
            append(minute)
            append(clockMode.uppercase(Locale.getDefault()))
        }
    }

    /**
     * Update the text view with hour, minute and clock mode
     * @param dateTimeValue Time Value in String 1200 AM/2400
     */
    private fun updateDateTimeValues(dateTimeValue: String) {
        if (dateTimeValue != AppConstants.EMPTY_STRING) {
            hour = dateTimeValue.substring(0, 2)
            minute = dateTimeValue.substring(2, 4)
            when {
                dateTimeValue.length > AppConstants.MAX_DATE_TIME_LENGTH -> {
                    clockMode = dateTimeValue.substring(dateTimeValue.length - 2)
                            .uppercase(Locale.getDefault())
                }

                clockFormat == KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR && hour.toInt() > AppConstants.DATE_TIME -> {
                    clockMode = TimeUtils.TEXT_PM.uppercase()
                }
            }

            if (clockFormat == KeyboardTextFormatUtils.FORMAT_CLOCK_24_HOUR
                    && numberPadViewHolder?.provideDateTimeValueTextView()
                            ?.amPmMode == KeyboardTextFormatUtils.CLOCK_MODE_PM
            ) {
                if (hour.toInt() < AppConstants.DATE_TIME) {
                    hour = buildString {
                        append((hour.toInt() + AppConstants.DATE_TIME))
                        append(AppConstants.EMPTY_STRING)
                    }
                }
            }

            if (clockFormat == KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR && hour.toInt() > AppConstants.DATE_TIME) {
                hour = buildString {
                    append((hour.toInt() - AppConstants.DATE_TIME))
                    append(AppConstants.EMPTY_STRING)
                }
            }

            hour = String.format(
                    Locale.getDefault(),
                    AppConstants.DEFAULT_DATE_VALUE_FORMAT,
                    hour.toInt()
            )

            if (!TextUtils.isEmpty(hour) && !TextUtils.isEmpty(minute)) {
                val dateTimeText = buildString {
                    append(hour)
                    append(AppConstants.EMPTY_STRING)
                    append(minute)
                }
                HMILogHelper.Logd("SettingsTime", "dateTimeText = $dateTimeText")
                numberPadViewHolder?.provideDateTimeValueTextView()?.value = dateTimeText
            }

            when (clockFormat) {
                KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR -> {
                    numberPadViewHolder?.provideDateTimeValueTextView()?.setUnderlinedSuffix(true)
                }

                else -> {
                    numberPadViewHolder?.provideDateTimeValueTextView()?.apply {
                        setUnderlinedSuffix(false)
                        setTextSuffix(AppConstants.EMPTY_STRING)
                    }
                }
            }

            when {
                CookingAppUtils.isTimeModeAM(clockMode) -> {
                    numberPadViewHolder?.provideDateTimeValueTextView()
                            ?.setClockModeAMPM(KeyboardTextFormatUtils.CLOCK_MODE_AM)
                }

                else -> {
                    numberPadViewHolder?.provideDateTimeValueTextView()
                            ?.setClockModeAMPM(KeyboardTextFormatUtils.CLOCK_MODE_PM)
                }
            }
            updateDateTimeString()
        }
    }

    /**
     * Check for previous time value
     */
    private fun checkForPreviousTimeValues() {
        if (arguments != null && arguments?.getString(BundleKeys.BUNDLE_PROVISIONING_TIME) != null) {
            if (arguments?.getBoolean(BundleKeys.BUNDLE_IS_FROM_TOOLS_MENU) == true || arguments?.getString(
                            BundleKeys.BUNDLE_PROVISIONING_TIME
                    ) != AppConstants.EMPTY_STRING
            ) {
                val timeString: String = TimeUtils.getTimeStringArguments(arguments).toString()
                updateDateTimeValues(timeString)
            } else {
                val cal = Calendar.getInstance()
                minute = cal[Calendar.MINUTE].toString()
                if (settingsViewModel?.is12HourTimeFormat?.value == true) {
                    // 12-hour format
                    hour = cal[Calendar.HOUR].toString()
                    clockMode = cal[Calendar.AM_PM].toString().uppercase(Locale.getDefault())
                } else {
                    // 24-hour format
                    hour = cal[Calendar.HOUR_OF_DAY].toString()
                }
            }
            if (!TextUtils.isEmpty(hour) && !TextUtils.isEmpty(minute)) {
                val dateTimeText = buildString {
                    append(hour)
                    append(AppConstants.EMPTY_STRING)
                    append(minute)
                }
                HMILogHelper.Logd("SettingsTime", "dateTimeText = $dateTimeText")
                numberPadViewHolder?.provideDateTimeValueTextView()?.value = dateTimeText
            }
            when (clockFormat) {
                KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR -> {
                    when {
                        CookingAppUtils.isTimeModeAM(clockMode) -> {
                            numberPadViewHolder?.provideDateTimeValueTextView()
                                    ?.setClockModeAMPM(KeyboardTextFormatUtils.CLOCK_MODE_AM)
                        }

                        else -> {
                            numberPadViewHolder?.provideDateTimeValueTextView()
                                    ?.setClockModeAMPM(KeyboardTextFormatUtils.CLOCK_MODE_PM)
                        }
                    }
                }

                else -> {
                    numberPadViewHolder?.provideDateTimeValueTextView()?.setClockModeAMPM(-1)
                }
            }
            updateDateTimeString()
        } else {
            val timeString = sharedViewModel?.getUnBoxingTime()
            HMILogHelper.Logd("SettingsTime", "Save timeString ----$timeString")
            if (timeString != null) {
                updateDateTimeValues(timeString)
            }
        }
    }

    private fun onNavigateToVerticalTumbler() {
        val timeValue = getTimeValue()
        val bundle = Bundle()
        bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, timeValue)
        HMILogHelper.Logd("SettingsTime", "Navigate vertical tumbler = $timeValue")
        NavigationUtils.navigateSafely(
                this,
                R.id.action_settingsSetTimeNumpadFragment_to_settingsSetTimeTumblerFragment,
                bundle,
                null
        )
    }

    private fun getTimeValue(): String {
        val dateTimeValue: String =
                if (SettingsManagerUtils.getTimeFormat() == SettingsManagerUtils.TimeFormatSettings.H_12) {
                    buildString {
                        append(hour)
                        append(AppConstants.EMPTY_STRING)
                        append(minute)
                        append(clockMode.uppercase(Locale.getDefault()))
                    }
                } else {
                    buildString {
                        append(hour)
                        append(AppConstants.EMPTY_STRING)
                        append(minute)
                    }
                }
        return dateTimeValue
    }

    private fun onSetButtonClick(): View.OnClickListener {
        return View.OnClickListener {
            if (keyboardViewModel?.isValidEntry == true) {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.start_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                HMILogHelper.Logd("SettingsTime", "Time valid entry = true")
                val navigationId = R.id.action_settingsSetTimeNumpadFragment_to_settingsTimeAndDateFragment
                if (updateTimeAndNavigateOut(this, dateTimeValue, navigationId)) {
                    sharedViewModel?.setUnBoxingTime(dateTimeValue)
                    CookingAppUtils.timeHasBeenSet(true)
                    if (CookingAppUtils.getDateHasBeenSet()) {
                        //remove notification
                        NotificationManagerUtils.removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME)
                        NotificationManagerUtils.removeNotificationFromNotificationCenter(NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME)
                    }
                    //clear the argument - Handling the scenario when user come from date tumbler so should populate selected previouse value
                    arguments?.clear()
                    HMILogHelper.Logv("SettingsTime", "Set Time successfully")
                } else {
                    HMILogHelper.Loge("SettingsTime", "Not able to set the Time")
                }
            } else {
                HMILogHelper.Logd("SettingsTime", "time valid entry = false")
                showInvalidMessage(
                        numberPadViewHolder?.provideErrorTextView(),
                        getErrorDetails(),
                        true
                )
                numberPadViewHolder?.provideErrorTextView()
                    ?.let { view -> TimeAndDateUtils.startFiveSecWarningTimer(view) }
            }
        }
    }

    /**
     * Update time values
     *
     * @param dateObject date object to process update time string
     */
    private fun updateDateTimeString(dateObject: Any?) {
        if (!isFormatChangeNotClicked) {
            dateTimeValue = AppConstants.EMPTY_STRING
            if (dateObject is Date) {
                val cal = Calendar.getInstance()
                cal.time = dateObject

                hour = cal[Calendar.HOUR_OF_DAY].toString()
                if (settingsViewModel?.is12HourTimeFormat?.value == true) {
                    hour = cal[Calendar.HOUR].toString()
                    if (AppConstants.DEFAULT_LEVEL.equals(
                                    hour,
                                    true
                            ) || AppConstants.DEFAULT_DOUBLE_ZERO.equals(
                                    hour, true
                            )
                    ) {
                        hour = TimeUtils.DEFAULT_TIME_TWELVE_HOURS
                    }
                }
                minute = cal[Calendar.MINUTE].toString()
                hour = String.format(
                        Locale.getDefault(),
                        AppConstants.DEFAULT_DATE_VALUE_FORMAT,
                        hour.toInt()
                )
                minute = String.format(
                        Locale.getDefault(),
                        AppConstants.DEFAULT_DATE_VALUE_FORMAT,
                        minute.toInt()
                )
                dateTimeValue = buildString {
                    append(hour)
                    append(AppConstants.EMPTY_STRING)
                    append(minute)
                }
                clockMode =
                        if ((cal[Calendar.AM_PM] == Calendar.AM)) TimeUtils.TEXT_AM else TimeUtils.TEXT_PM
                if (clockFormat == KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR) {
                    dateTimeValue =
                            if (numberPadViewHolder?.provideDateTimeValueTextView()!!.amPmMode == KeyboardTextFormatUtils.CLOCK_MODE_AM) {
                                buildString {
                                    append(dateTimeValue)
                                    append(AppConstants.EMPTY_SPACE)
                                    append(TimeUtils.TEXT_AM.uppercase())
                                }
                            } else {
                                buildString {
                                    append(dateTimeValue)
                                    append(AppConstants.EMPTY_SPACE)
                                    append(TimeUtils.TEXT_PM.uppercase())
                                }
                            }
                }
            }
        } else {
            isFormatChangeNotClicked = false
        }
    }

    /**
     * Initialize views
     */
    private fun initializeView() {
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        numberPadViewHolder?.setKeyboardViewModel(keyboardViewModel)
        numberPadViewHolder?.provideDateTimeValueTextView()?.keyboardViewModel = keyboardViewModel
        numberPadViewHolder?.provideDateTimeValueTextView()?.apply {
            maxCharacterLength = AppConstants.MAX_CHAR_LENGTH
            setClearEntryOnLimitReached(true)
            textViewType = KeyboardTextFormatUtils.TEXT_TYPE_CLOCK
            setClockFormat(clockFormat)
            setDateFormat(dateFormat)
            setDelayClockMode(false)
            setAutoCorrectEnabled(true)
            enableAutoCorrectionWithSections(false)
        }
        //Radio toggle visible
        numberPadViewHolder?.provideRadioGroupFormatSelection()?.visible()
        numberPadViewHolder?.provide24HTimeFormatSelection()?.visible()
        numberPadViewHolder?.provide12HTimeFormatSelection()?.visible()
        //Radio toggle click event
        numberPadViewHolder?.provide24HTimeFormatSelection()
                ?.setOnClickListener(onFormatSelectionClicked(false))
        numberPadViewHolder?.provide12HTimeFormatSelection()
                ?.setOnClickListener(onFormatSelectionClicked(true))

        if (numberPadViewHolder?.providePrimaryButton() != null) {
            numberPadViewHolder?.providePrimaryButton()?.visible()
            numberPadViewHolder?.providePrimaryButton()
                    ?.setTextButtonText(resources.getString(R.string.text_button_set))
            numberPadViewHolder?.providePrimaryButton()?.setOnClickListener(onSetButtonClick())
        }
        updateDateTimeString(if (isUnboxing) getStartOfDay(Date()) else Date())
        if (!TextUtils.isEmpty(hour) && !TextUtils.isEmpty(minute)) {
            val dateTimeText = buildString {
                append(hour)
                append(AppConstants.EMPTY_STRING)
                append(minute)
            }
            HMILogHelper.Logd("SettingsTime", "dateTimeText = $dateTimeText")
            numberPadViewHolder?.provideDateTimeValueTextView()?.value = dateTimeText
        }
        updateDateTimeString()
        setDefaultUnboxingTimeValue = true

        //Set AM PM as per preference
        if (settingsViewModel?.is12HourTimeFormat?.value == true) {
            // 12-hour format
            numberPadViewHolder?.provide24HTimeFormatSelection()?.isChecked = false
            numberPadViewHolder?.provide12HTimeFormatSelection()?.isChecked = true
            changeTimeFormatSelectionColors(
                    leftFormatSelectionView = numberPadViewHolder?.provide12HTimeFormatSelection(),
                    rightFormatSelectionView = numberPadViewHolder?.provide24HTimeFormatSelection(),
                    leftFormatTextColor = R.color.color_white,
                    rightFormatTextColor = R.color.under_line_color,
                    leftFormatTextStyle = R.style.RegionalToggleSwitchNormalStyle,
                    rightFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle
            )
        } else {
            // 24-hour format
            numberPadViewHolder?.provide24HTimeFormatSelection()?.isChecked = true
            numberPadViewHolder?.provide12HTimeFormatSelection()?.isChecked = false
            changeTimeFormatSelectionColors(
                    leftFormatSelectionView = numberPadViewHolder?.provide12HTimeFormatSelection(),
                    rightFormatSelectionView = numberPadViewHolder?.provide24HTimeFormatSelection(),
                    leftFormatTextColor = R.color.under_line_color,
                    rightFormatTextColor = R.color.color_white,
                    leftFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle,
                    rightFormatTextStyle = R.style.RegionalToggleSwitchNormalStyle
            )
        }
    }

    /**
     * for Default unboxing time needs to be 12:00 AM
     *
     * @param date current date to set the time as 12:00 AM
     * @return updated time
     */
    private fun getStartOfDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        return calendar.time
    }

    /**
     * Register observers on time entry
     */
    private fun registerObservers() {
        keyboardViewModel?.resultTextObserver?.observe(
                viewLifecycleOwner
        ) { value: Any? ->
            if (!setDefaultUnboxingTimeValue) {
                updateDateTimeString(value)
            } else {
                setDefaultUnboxingTimeValue = false
            }
        }

        keyboardViewModel?.errorCodeObserver?.observe(
                viewLifecycleOwner
        ) {
            if (isValidEntry()) {
                showInvalidMessage(
                        numberPadViewHolder?.provideErrorTextView(),
                        getErrorDetails(),
                        true
                )
            }
        }

        keyboardViewModel?.onBackSpace()?.observe(
                viewLifecycleOwner
        ) { charSequence: CharSequence ->
            showInvalidMessage(
                    numberPadViewHolder?.provideErrorTextView(),
                    getErrorDetails(),
                    false
            )
            if (charSequence == AppConstants.EMPTY_STRING) {
                updateDateTimeValues(dateTimeValue)
            }
        }
        keyboardViewModel?.onTextClear()?.observe(
                viewLifecycleOwner
        ) {
            showInvalidMessage(
                    numberPadViewHolder?.provideErrorTextView(),
                    getErrorDetails(),
                    false
            )
        }

    }

    override fun onStoredValueReceived(storedValue: String?) {
        // NA
    }

    /**
     * Get error details from SDK and update with custom message
     *
     * @return string values
     */
    private fun getErrorDetails(): String {
        val validEntry = keyboardViewModel?.errorCodeObserver?.value
        HMILogHelper.Logd("SettingsTime", "Keyboard validEntry = $validEntry")
        return if (validEntry != null) {
            CookingAppUtils.getTimeErrorMessage(validEntry, requireContext())
        } else {
            AppConstants.EMPTY_STRING
        }
    }

    /**
     * Helper method to determine if keypad entry is valid
     *
     * @return boolean
     */
    private fun isValidEntry(): Boolean {
        val validEntry = InputError.NOT_AN_ERROR
        return !TextUtils.isEmpty(keyboardViewModel?.enteredText?.value) && validEntry == keyboardViewModel?.errorCodeObserver?.value
    }

    override fun getKeyboardView(): KeyboardView? {
        return numberPadViewHolder?.provideKeyboardView()
    }

    private fun onFormatSelectionClicked(is12HoursFormat: Boolean): View.OnClickListener {
        return View.OnClickListener {
            if (is12HoursFormat && clockFormat != KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR) {
                set12HourFormat(true)
                changeTimeFormatSelectionColors(
                        leftFormatSelectionView = numberPadViewHolder?.provide12HTimeFormatSelection(),
                        rightFormatSelectionView = numberPadViewHolder?.provide24HTimeFormatSelection(),
                        leftFormatTextColor = R.color.color_white,
                        rightFormatTextColor = R.color.under_line_color,
                        leftFormatTextStyle = R.style.RegionalToggleSwitchNormalStyle,
                        rightFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle
                )
            } else if (!is12HoursFormat && clockFormat != KeyboardTextFormatUtils.FORMAT_CLOCK_24_HOUR) {
                set12HourFormat(false)
                changeTimeFormatSelectionColors(
                        leftFormatSelectionView = numberPadViewHolder?.provide12HTimeFormatSelection(),
                        rightFormatSelectionView = numberPadViewHolder?.provide24HTimeFormatSelection(),
                        leftFormatTextColor = R.color.under_line_color,
                        rightFormatTextColor = R.color.color_white,
                        leftFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle,
                        rightFormatTextStyle = R.style.RegionalToggleSwitchNormalStyle
                )
            } else {
                HMILogHelper.Loge(" Format Selection issue: is12HoursFormat $is12HoursFormat clockFormat $clockFormat")
            }
            isFormatChangeNotClicked = true
        }
    }

    override fun onHMILeftKnobClick() {
        // do nothing
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
        HMILogHelper.Logd("SettingsTime", "Unboxing onKnobRotateEvent")
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            KnobNavigationUtils.knobForwardTrace = true
            onNavigateToVerticalTumbler()
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        //Do nothing
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
        super.onDestroyView()
        MeatProbeUtils.removeMeatProbeListener()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }
}
