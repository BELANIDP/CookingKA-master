/*
* ************************************************************************************************
* ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
* ************************************************************************************************
*/
package core.jbase


import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.tools.util.KeyboardTextFormatUtils
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel
import com.whirlpool.hmi.utils.BuildInfo
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.SettingsManagerUtils
import core.utils.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * An abstract class for presenting and managing entry of a keypad.
 * Brief       : Base class for Date and Time Fragment CVT <br></br>
 * Author      : Nikki Gharde <br></br>
 * Created On  : 05.sep.2024 <br></br>
 * Details     : AbstractDateTimeFragment for unboxing
 */
abstract class AbstractDateTimeFragment : SuperAbstractTimeoutEnableFragment() {
    protected var settingsViewModel: SettingsViewModel? = null

    var dateTimeValue: String = AppConstants.EMPTY_STRING
    var clockFormat: Int = KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR
    var dateFormat: Int = KeyboardTextFormatUtils.FORMAT_DATE_MM_DD_YY
    private var isUnboxing: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViewModel()
        init()
    }

    private fun setViewModel() {
        settingsViewModel = SettingsViewModel.getSettingsViewModel()
        isUnboxing = SettingsManagerUtils.isUnboxing
    }

    /**
     * init setting for clock format and date format
     */
    private fun init() {
        if (settingsViewModel?.is12HourTimeFormat()?.value != null) {
            clockFormat = if (settingsViewModel?.is12HourTimeFormat()?.value == true) {
                KeyboardTextFormatUtils.FORMAT_CLOCK_12_HOUR
            } else {
                KeyboardTextFormatUtils.FORMAT_CLOCK_24_HOUR
            }
        }
        if (settingsViewModel?.isDateFormatDayMonth() != null) {
            dateFormat = if (settingsViewModel?.isDateFormatDayMonth() == true) {
                KeyboardTextFormatUtils.FORMAT_DATE_DD_MM_YY
            } else {
                KeyboardTextFormatUtils.FORMAT_DATE_MM_DD_YY
            }
        }
    }


    /**
     * To show invalid message on Date/Time Num pad entry
     *
     * @param textView       textview to show the error
     * @param error          error message
     * @param show           whether to show/hide the error message
     */
    protected fun showInvalidMessage(textView: TextView?, error: CharSequence?, show: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            if (textView != null) {
                if (show) {
                    textView.text = error
                    textView.visibility = View.VISIBLE
                    textView.setTextColor(resources.getColor(R.color.notification_red, null))
                } else {
                    textView.visibility = View.GONE
                }
            }
        }

    }

    /**
     * Get Date values from Bundle Arguments
     * @param arguments bundle argument
     * @return string[] date values
     */
    private fun getDateFromArguments(arguments: Bundle?): Array<String> {
        val dateArray: Array<String>
        if (arguments != null && arguments.containsKey(BundleKeys.BUNDLE_PROVISIONING_DATE)) {
            dateArray =
                arguments.getString(BundleKeys.BUNDLE_PROVISIONING_DATE)
                    ?.split(AppConstants.SLASH.toRegex())?.dropLastWhile { it.isEmpty() }
                    ?.toTypedArray()?: emptyArray()
        } else {
            val currentTime = Calendar.getInstance().time
            val formattedDate = SimpleDateFormat(
                SettingsViewModel.getSettingsViewModel().dateFormatString.value,
                Locale.getDefault()
            ).format(currentTime)
            dateArray =
                formattedDate.split(AppConstants.SLASH.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
        }
        return dateArray
    }

    /**
     * get time Values from Bundle arguments
     * @param arguments bundle arguments
     * @return string time
     */
    protected fun getTimeFromArguments(arguments: Bundle?): String? {
        return if (arguments != null && arguments.containsKey(BundleKeys.BUNDLE_PROVISIONING_TIME)) {
            arguments.getString(BundleKeys.BUNDLE_PROVISIONING_TIME)
        } else {
            SimpleDateFormat(
                TimeUtils.getTimeFormatString(),
                Locale.getDefault()
            ).format(Calendar.getInstance().time)
        }
    }

    /**
     * Method provides instance of keyboard view model.
     * @param owner owner fragment
     * @return KeyboardViewModel view model of keyboard manager
     */
    protected fun getKeyboardViewModel(owner: ViewModelStoreOwner): KeyboardViewModel {
        return ViewModelProvider(owner)[KeyboardViewModel::class.java]
    }

    /**
     * Check and get date values from bundle values and assign to respective variables
     */
    protected fun checkForPreviousDateValue() {
        // Check if the value is received from previous
        if (arguments != null && arguments?.containsKey(BundleKeys.BUNDLE_PROVISIONING_DATE) == true
            && !TextUtils.isEmpty(requireArguments().getString(BundleKeys.BUNDLE_PROVISIONING_DATE))
        ) {
            val dateArray = getDateFromArguments(arguments)
            var date: String = TimeUtils.getDayFromDateArray(dateArray)
            var month: String = TimeUtils.getMonthFromDateArray(dateArray)
            var year = dateArray[2]
            if (year.length == 4) {
                year = year.substring(2)
            }
            val parsedValue: String
            date = java.lang.String.format(
                Locale.getDefault(),
                AppConstants.DEFAULT_DATE_VALUE_FORMAT,
                date.toInt()
            )
            month = java.lang.String.format(
                Locale.getDefault(),
                AppConstants.DEFAULT_DATE_VALUE_FORMAT,
                month.toInt()
            )
            year = java.lang.String.format(
                Locale.getDefault(),
                AppConstants.DEFAULT_DATE_VALUE_FORMAT,
                year.toInt()
            )
            if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(month) && !TextUtils.isEmpty(year)) {
                parsedValue = if (SettingsViewModel.getSettingsViewModel().isDateFormatDayMonth) {
                    ((date + AppConstants.EMPTY_STRING) + month + AppConstants.EMPTY_STRING) + year
                } else {
                    ((month + AppConstants.EMPTY_STRING) + date + AppConstants.EMPTY_STRING) + year
                }
                onStoredValueReceived(parsedValue)
            }
        }
    }

    /**
     * Navigate to next screen based on entered values
     * @param bundle bundles values
     * @param fragment fragment reference
     * @param navigationId navigation id to move to the next screen
     * @return true/ false
     */
    protected fun updateDateAndNavigateOut(
        bundle: Bundle?,
        fragment: Fragment?,
        navigationId: Int
    ): Boolean? {
        val calendar = Calendar.getInstance()
        val dateArray: Array<String> = TimeUtils.getDateArrayArguments(bundle)
        val day: String = TimeUtils.getDayFromDateArray(dateArray)
        val month: String = TimeUtils.getMonthFromDateArray(dateArray)
        var year = dateArray[2]
        if (year.length == 2) {
            year = TimeUtils.DEFAULT_YEAR_PREFIX + year
        }
        calendar[year.toInt(), month.toInt() - 1] = day.toInt()

        var success = settingsViewModel?.setTimeModeManual(calendar.time)
        if (BuildInfo.isRunningOnEmulator()) {
            // emulator does not have permission to set date or time
            success = true
        }
        if (SettingsViewModel.getSettingsViewModel().isDateFormatDayMonth) {
            settingsViewModel?.setUserDataStringValue(
                BundleKeys.KEY_DATE,
                (buildString {
                    append(calendar[Calendar.DATE])
                    append(AppConstants.SLASH)
                    append((calendar[Calendar.MONTH] + 1))
                    append(AppConstants.SLASH)
                    append(calendar[Calendar.YEAR])
                }),
                false
            )
        } else {
            settingsViewModel?.setUserDataStringValue(
                BundleKeys.KEY_DATE,
                buildString {
                    append((calendar[Calendar.MONTH] + 1))
                    append(AppConstants.SLASH)
                    append(calendar[Calendar.DATE])
                    append(AppConstants.SLASH)
                    append(calendar[Calendar.YEAR])
                },
                false
            )
        }
        if (success == true) {
            if (fragment != null) {
                navigateSafely(fragment, navigationId, null, null)
            }
        }
        return success
    }

    /**
     * Navigate to next screen based on entered values
     * @param fragment view to navigate to next screen
     * @param navigationId navigation id to move to the next screen
     * @return true/ false
     */
    protected fun updateTimeAndNavigateOut(
            fragment: Fragment?,
            timeValue: String?,
            navigationId: Int,
            bundle: Bundle? = null
    ): Boolean {
        HMILogHelper.Logd("Unboxing","updateTimeAndNavigateOut")
        var success: Boolean
        val hour: Int? = timeValue?.let { CookingAppUtils.getHour(it) }
        val minute: Int? = timeValue?.let { CookingAppUtils.getMinute(it) }
        val calendar = Calendar.getInstance()
        if (settingsViewModel?.is12HourTimeFormat()?.value == true) {
            if (hour != null) {
                calendar[Calendar.HOUR] = hour % 12
            }
            if (minute != null) {
                calendar[Calendar.MINUTE] = minute
            }
            val timeMode =
                if (timeValue?.let { CookingAppUtils.isTimeModeAM(it) } == true) KeyboardTextFormatUtils.CLOCK_MODE_AM else KeyboardTextFormatUtils.CLOCK_MODE_PM
            calendar[Calendar.AM_PM] = timeMode
        } else {
            if (hour != null) {
                calendar[Calendar.HOUR_OF_DAY] = hour
            }
            if (minute != null) {
                calendar[Calendar.MINUTE] = minute
            }
        }
        success = settingsViewModel?.setTimeModeManual(calendar.time) == true

        if (BuildInfo.isRunningOnEmulator()) {
            // emulator does not have permission to set date or time
            success = true
        }
        if (success) {
            if (fragment != null) {
                navigateSafely(fragment, navigationId, bundle, null)
            }
        }
        return success
    }

    /**
     * get Date and Time Bundle values
     * @return bundle values
     */
    fun getDateTimeBundleValues(): Bundle {
        val bundle = Bundle()
        bundle.putString(BundleKeys.BUNDLE_PROVISIONING_DATE, dateTimeValue)
        bundle.putString(
            BundleKeys.BUNDLE_PROVISIONING_TIME,
            settingsViewModel?.getUserDataStringValue(BundleKeys.KEY_TIME, false)
        )
        if (arguments != null) arguments?.getBoolean(BundleKeys.BUNDLE_IS_NAVIGATE_TOOLS_DATE_FRAGMENT)
            ?.let {
                bundle.putBoolean(
                    BundleKeys.BUNDLE_IS_NAVIGATE_TOOLS_DATE_FRAGMENT,
                    it
                )
            }
        return bundle
    }

    /**
     * To change the non selected item of date and time formats
     */
    protected fun changeTimeFormatSelectionColors(
        leftFormatSelectionView: RadioButton?,
        rightFormatSelectionView: RadioButton?,
        leftFormatTextColor: Int,
        rightFormatTextColor: Int,
        leftFormatTextStyle: Int,
        rightFormatTextStyle: Int,
    ) {
        if (leftFormatSelectionView != null) {
            leftFormatSelectionView.setTextColor(resources.getColor(leftFormatTextColor, null))
            TextViewCompat.setTextAppearance(leftFormatSelectionView, leftFormatTextStyle)
        }
        if (rightFormatSelectionView != null) {
            rightFormatSelectionView.setTextColor(resources.getColor(rightFormatTextColor, null))
            TextViewCompat.setTextAppearance(rightFormatSelectionView, rightFormatTextStyle)
        }
    }

    protected abstract fun onStoredValueReceived(storedValue: String?)
}