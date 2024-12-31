package android.presenter.fragments.digital_unboxing

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uicomponents.tools.util.KeyboardTextFormatUtils
import com.whirlpool.hmi.uicomponents.widgets.keyboard.Keyboard
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError
import core.jbase.AbstractDateTimeFragment
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.SettingsManagerUtils.isUnboxing
import core.utils.TimeAndDateUtils
import core.utils.TimeUtils
import core.utils.visible
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


/**
 * File       : android.presenter.fragments.digital_unboxing.UnBoxingSetupLaterTime
 * Author     : Nikki Gharde
 * Created On : 11/sep/2024
 * Details    : Sets the Operating System date without connecting the HMI to the internet.
 */
class UnBoxingSetupDateNumpadFragment : AbstractDateTimeFragment(), HMIKnobInteractionListener,
    KeyboardInputManagerInterface {
    private var numberPadViewHolder: AbstractDateTimeViewHolder? = null
    private var isNavigateToolsFragment = false
    private var keyboardViewModel: KeyboardViewModel? = null
    private var defaultDateValue: String = AppConstants.EMPTY_STRING
    private var knobRotationCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewHolder()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (numberPadViewHolder == null) initViewHolder()
        if (arguments != null) {
            isNavigateToolsFragment =
                arguments?.getBoolean(BundleKeys.BUNDLE_IS_NAVIGATE_TOOLS_DATE_FRAGMENT) == true
        }
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
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        initializeView()
        registerObservers()
        setTimeoutApplicable(!isUnboxing)
    }

    /**
     * init date view holder class
     * On header vertical tumbler callback
     */
    private fun initViewHolder() {
        numberPadViewHolder =
            SetupDateTimeViewHolder(this, isSetupForDate = true, onHeaderBarTumblerClick = {
                onNavigateToVerticalTumbler()
            })
    }

    private fun onFormatSelectionClicked(isMMDDFormat: Boolean): View.OnClickListener {
        return View.OnClickListener {
            if (isMMDDFormat && dateFormat != KeyboardTextFormatUtils.FORMAT_DATE_MM_DD_YY) {
                formatSelectionUpdate(true)
                changeTimeFormatSelectionColors(
                    leftFormatSelectionView = numberPadViewHolder?.provideMMDDDateFormatSelection(),
                    rightFormatSelectionView = numberPadViewHolder?.provideDDMMDateFormatSelection(),
                    leftFormatTextColor = R.color.color_white,
                    rightFormatTextColor = R.color.under_line_color,
                    leftFormatTextStyle = R.style.RegionalToggleSwitchNormalStyle,
                    rightFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle
                )
            } else if (!isMMDDFormat && dateFormat != KeyboardTextFormatUtils.FORMAT_DATE_DD_MM_YY) {
                formatSelectionUpdate(false)
                changeTimeFormatSelectionColors(
                    leftFormatSelectionView = numberPadViewHolder?.provideMMDDDateFormatSelection(),
                    rightFormatSelectionView = numberPadViewHolder?.provideDDMMDateFormatSelection(),
                    leftFormatTextColor = R.color.under_line_color,
                    rightFormatTextColor = R.color.color_white,
                    leftFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle,
                    rightFormatTextStyle = R.style.RegionalToggleSwitchNormalStyle
                )
            } else {
                HMILogHelper.Loge(" Format Selection issue: isMMDDFormat $isMMDDFormat clockFormat $dateFormat")
            }
        }
    }

    /**
     * To set Date format on click of Format selection listener
     *
     * @param isMMDDFormat true/false
     */
    private fun formatSelectionUpdate(isMMDDFormat: Boolean) {
        if (isMMDDFormat) {
            dateFormat = KeyboardTextFormatUtils.FORMAT_DATE_MM_DD_YY
            SettingsManagerUtils.setDateFormat(SettingsManagerUtils.DateFormatSettings.MMDDYY)
        } else {
            dateFormat = KeyboardTextFormatUtils.FORMAT_DATE_DD_MM_YY
            SettingsManagerUtils.setDateFormat(SettingsManagerUtils.DateFormatSettings.DDMMYY)
        }
        checkForPreviousValues(true, dateTimeValue)
    }


    /**
     * Check for previous values from bundles or initial time
     * @param isFromFormatSelection true/false
     */
    private fun checkForPreviousValues(
        isFromFormatSelection: Boolean,
        dateTimePreviousValue: String
    ) {
        var dateTimeValue = dateTimePreviousValue
        var dateArray: Array<String>
        if (isNavigateToolsFragment) {
            dateArray =
                dateTimeValue.split(AppConstants.SLASH.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
            if (isFromFormatSelection) {
                dateTimeValue = buildString {
                    append(dateArray[1])
                    append(AppConstants.SLASH)
                    append(dateArray[0])
                    append(AppConstants.SLASH)
                    append(dateArray[2])
                }
                dateArray =
                    dateTimeValue.split(AppConstants.SLASH.toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()

            }
        } else if (arguments != null && (arguments?.containsKey(BundleKeys.BUNDLE_PROVISIONING_DATE) == true &&
                    !TextUtils.isEmpty(requireArguments().getString(BundleKeys.BUNDLE_PROVISIONING_DATE)))
        ) {
            dateArray = TimeUtils.getDateArrayArguments(arguments)
        } else {
            if (isFromFormatSelection) {
                dateArray =
                    dateTimeValue.split(AppConstants.SLASH.toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                dateTimeValue = buildString {
                    append(dateArray[1])
                    append(AppConstants.SLASH)
                    append(dateArray[0])
                    append(AppConstants.SLASH)
                    append(dateArray[2])
                }
                dateArray =
                    dateTimeValue.split(AppConstants.SLASH.toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()

            } else {
                dateArray = TimeUtils.getFormattedDateArray()
            }
        }

        var date: String = TimeUtils.getDayFromDateArray(dateArray)
        var month: String = TimeUtils.getMonthFromDateArray(dateArray)
        var year = dateArray[2]
        if (year.length == 4) {
            year = year.substring(2)
        }
        date = String.format(
            Locale.getDefault(),
            AppConstants.DEFAULT_DATE_VALUE_FORMAT,
            date.toInt()
        )
        month = String.format(
            Locale.getDefault(),
            AppConstants.DEFAULT_DATE_VALUE_FORMAT,
            month.toInt()
        )
        year = String.format(
            Locale.getDefault(),
            AppConstants.DEFAULT_DATE_VALUE_FORMAT,
            year.toInt()
        )
        if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(month) && !TextUtils.isEmpty(year)) {
            dateTimeValue = if (SettingsManagerUtils.getDateFormat() == SettingsManagerUtils.DateFormatSettings.DDMMYY) {
                buildString {
                    append(date)
                    append(AppConstants.SLASH)
                    append(month)
                    append(AppConstants.SLASH)
                    append(year)
                }
            } else {
                buildString {
                    append(month)
                    append(AppConstants.SLASH)
                    append(date)
                    append(AppConstants.SLASH)
                    append(year)
                }
            }
            numberPadViewHolder?.provideDateTimeValueTextView()
                ?.setValue(dateTimeValue.replace(AppConstants.SLASH, AppConstants.EMPTY_STRING))
        }
    }

    private fun onNextButtonClick(): View.OnClickListener {
        return View.OnClickListener {
            if (keyboardViewModel?.isValidEntry == true) {
                HMILogHelper.Logd("Unboxing", "Date valid entry = true")
                val bundle = Bundle()
                bundle.putString(BundleKeys.BUNDLE_PROVISIONING_DATE, dateTimeValue)
                val navigationId: Int =
                    R.id.action_unboxingSetupDateFragment_to_unboxingDoneCongratulationFragment
                if (updateDateAndNavigateOut(bundle, this, navigationId) == true) {
                    HMILogHelper.Logv("Set Date successfully")
                } else {
                    HMILogHelper.Loge("Not able to set the Date")
                }
            } else {
                HMILogHelper.Logd("Unboxing", "Date valid entry = false")
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

    private fun updateDateTimeString(objectAny: Any?) {
        if (objectAny is Date) {
            this.dateTimeValue = SimpleDateFormat(
                settingsViewModel?.dateFormatString?.getValue(),
                Locale.getDefault()
            ).format(objectAny)
        }
    }

    /**
     * Initialize views
     */
    private fun initializeView() {
        if (numberPadViewHolder?.provideDateTimeValueTextView() != null) {
            numberPadViewHolder?.setKeyboardViewModel(keyboardViewModel)
            numberPadViewHolder?.provideDateTimeValueTextView()?.keyboardViewModel =
                keyboardViewModel
            numberPadViewHolder?.provideDateTimeValueTextView()?.apply {
                textViewType = KeyboardTextFormatUtils.TEXT_TYPE_DATE
                maxCharacterLength = AppConstants.MAX_DATE_CHAR_LENGTH
                setClearEntryOnLimitReached(true)
                setDateFormat(dateFormat)
                setAutoCorrectEnabled(true)
                enableAutoCorrectionWithSections(false)
            }
        }

        if (numberPadViewHolder?.providePrimaryButton() != null) {
            numberPadViewHolder?.providePrimaryButton()?.visible()
            numberPadViewHolder?.providePrimaryButton()?.setOnClickListener(onNextButtonClick())
        }

        if (arguments != null && (arguments?.containsKey(BundleKeys.BUNDLE_PROVISIONING_DATE) == true
                    && !TextUtils.isEmpty(requireArguments().getString(BundleKeys.BUNDLE_PROVISIONING_DATE)))
        ) {
            dateTimeValue = arguments?.getString(BundleKeys.BUNDLE_PROVISIONING_DATE).toString()
        } else {
            updateDateTimeString(Date())
        }
        defaultDateValue = dateTimeValue


        numberPadViewHolder?.provideMMDDDateFormatSelection()?.setText(R.string.text_tiles_list_mm_dd_yy_value)
        numberPadViewHolder?.provideMMDDDateFormatSelection()
            ?.setOnClickListener(onFormatSelectionClicked(true))

        numberPadViewHolder?.provideDDMMDateFormatSelection()?.setText(R.string.text_tiles_list_dd_mm_yy_value)
        numberPadViewHolder?.provideDDMMDateFormatSelection()
            ?.setOnClickListener(onFormatSelectionClicked(false))


        numberPadViewHolder?.provideRadioGroupFormatSelection()?.visible()
        numberPadViewHolder?.provideDDMMDateFormatSelection()?.visible()
        numberPadViewHolder?.provideMMDDDateFormatSelection()?.visible()

        HMILogHelper.Logd(
            "Unboxing",
            "isDateFormatDayMonth = ${settingsViewModel?.isDateFormatDayMonth}"
        )
        if (SettingsManagerUtils.getDateFormat() == SettingsManagerUtils.DateFormatSettings.DDMMYY) {
            // dd/mm-date format
            numberPadViewHolder?.provideDDMMDateFormatSelection()?.isChecked = true
            numberPadViewHolder?.provideMMDDDateFormatSelection()?.isChecked = false
            changeTimeFormatSelectionColors(
                leftFormatSelectionView = numberPadViewHolder?.provideMMDDDateFormatSelection(),
                rightFormatSelectionView = numberPadViewHolder?.provideDDMMDateFormatSelection(),
                leftFormatTextColor = R.color.under_line_color,
                rightFormatTextColor = R.color.color_white,
                leftFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle,
                rightFormatTextStyle = R.style.RegionalToggleSwitchNormalStyle
            )
        } else {
            // mm/dd date format
            numberPadViewHolder?.provideDDMMDateFormatSelection()?.isChecked = false
            numberPadViewHolder?.provideMMDDDateFormatSelection()?.isChecked = true
            changeTimeFormatSelectionColors(
                leftFormatSelectionView = numberPadViewHolder?.provideMMDDDateFormatSelection(),
                rightFormatSelectionView = numberPadViewHolder?.provideDDMMDateFormatSelection(),
                leftFormatTextColor = R.color.color_white,
                rightFormatTextColor = R.color.under_line_color,
                leftFormatTextStyle = R.style.RegionalToggleSwitchNormalStyle,
                rightFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle
            )
        }

        checkForPreviousValues(false, dateTimeValue)
    }

    /**
     * Register observers on text entry
     */
    private fun registerObservers() {
        keyboardViewModel?.displayTextObserver?.observe(
            getViewLifecycleOwner()
        ) { displayText: String ->
            dateTimeValue = displayText.replace(AppConstants.COMMA, AppConstants.SLASH)
        }

        keyboardViewModel?.errorCodeObserver?.observe(
            getViewLifecycleOwner()
        ) {
            if (isValidEntry()) {
                showInvalidMessage(
                    numberPadViewHolder?.provideErrorTextView(),
                    getErrorDetails(),
                    false
                )
            }
        }

        keyboardViewModel?.onBackSpace()?.observe(
            getViewLifecycleOwner()
        ) { charSequence: CharSequence ->
            showInvalidMessage(
                numberPadViewHolder?.provideErrorTextView(),
                getErrorDetails(),
                false
            )
            if (charSequence == AppConstants.EMPTY_STRING) {
                checkForPreviousValues(false, defaultDateValue)
            }
        }
        keyboardViewModel?.onTextClear()?.observe(
            getViewLifecycleOwner()
        ) {
            showInvalidMessage(
                numberPadViewHolder?.provideErrorTextView(),
                getErrorDetails(),
                false
            )
        }
    }


    /**
     * Get error details from SDK and update with custom message
     *
     * @return string values
     */
    private fun getErrorDetails(): String {
        val validEntry = keyboardViewModel?.errorCodeObserver?.getValue()
        HMILogHelper.Logd("Unboxing", "Date validEntry = $validEntry")
        return if (validEntry != null) {
            CookingAppUtils.getTimeDateErrorMessage(validEntry, requireContext())
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
        return !TextUtils.isEmpty(keyboardViewModel?.enteredText?.getValue()) && validEntry == keyboardViewModel?.errorCodeObserver?.getValue()
    }

    private fun onNavigateToVerticalTumbler() {
        HMILogHelper.Logd("Unboxing", "Date: on Navigate To Vertical Tumbler")
        val bundle = Bundle()
        if (isValidEntry()) {
            bundle.putString(BundleKeys.BUNDLE_PROVISIONING_DATE, dateTimeValue)
        }
        HMILogHelper.Logd("Unboxing", "Numpad dateTimeValue------$dateTimeValue")
        NavigationUtils.navigateSafely(
            this,
            R.id.action_unboxingSetupDateFragment_to_dateTumblerFragment,
            bundle,
            null
        )
    }

    override fun onStoredValueReceived(storedValue: String?) {
        if (numberPadViewHolder?.provideDateTimeValueTextView() != null) {
            numberPadViewHolder?.provideDateTimeValueTextView()?.value = storedValue
        }
    }

    override fun getKeyboardView(): KeyboardView? {
        return numberPadViewHolder?.provideKeyboardView()
    }

    override fun onHMILeftKnobClick() {
        // do nothing
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobClick() {
        HMILogHelper.Logd("Unboxing", "Unboxing onHMIRightKnobClick")
        PopUpBuilderUtils.userLeftKnobWarningPopup(this)
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
            KnobNavigationUtils.knobForwardTrace = true
            onNavigateToVerticalTumbler()
        } else if (knobId == AppConstants.RIGHT_KNOB_ID) {
            PopUpBuilderUtils.userLeftKnobWarningPopup(this)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        //Do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }
}
