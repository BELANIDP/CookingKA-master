/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import com.whirlpool.cooking.ka.BuildConfig
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.settings.SettingsViewModel
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * File        : core.utils.TimeUtils
 * Brief       : TimeUtils class responsible for providing the time/date utilities methods
 * Author      : GHARDNS/Nikki
 * Created On  : 18-03-2024
 */


object TimeUtils {

    private const val HOUR_MINUTE_AMPM_FORMAT = "hhmm a"
    const val TEXT_TIME_FORMAT_HOUR = "h:mm aa"
    const val TEXT_TIME_FORMAT_24_HOUR = "HH:mm"
    private const val DAY_HOUR_FORMAT = "kkmm"
    private const val MINUTES_IN_AN_HOUR = 60
    private const val SECONDS_IN_A_MINUTE = 60
    const val HOURS_IN_A_DAY = 24
    const val TWELVE_HOURS = 12
    const val SLEEP_BRIGHTNESS: Int = 1
    const val SLEEP_TIMEOUT_MS: Int = 60000
    const val PERIODIC_SLEEP_TIME_MS: Int = 500
    const val DEFAULT_YEAR_PREFIX: String = "20"
    const val DEFAULT_TIME_TWELVE_HOURS: String = "12"
    const val TEXT_AM: String = "am"
    const val TEXT_PM: String = "pm"

    const val dayMonthFormat: String = "dd/MM/yy"
    const val monthDayFormat: String = "MM/dd/yy"

    private const val TIME_12HR_FORMAT: String =  "HH:mm"
    private const val TIME_24HR_FORMAT: String =  "hh:mm a"
    /**
     * This method is to update the disable keys list based on the cook time input entered by the user
     * and also based on the Min and Max cook time allowed for that particular cycle selected
     * this method is used for both the keypad input value and the delete value from Cook time numPad
     *
     * @param index        Index
     * @param currentValue Current value of selection
     * @param maxCookTime  Maximum cook time range
     * @param isMWO     microwave or not
     */
    fun disableKeypadItemsForCookTime(
        index: Int, currentValue: String, @Suppress("UNUSED_PARAMETER") minCookTime: String?, maxCookTime: String?,
        isNumPadLoadingFirstTime: Boolean, isMWO: Boolean
    ): ArrayList<String> {
        val disabledKeysList = ArrayList<String>()
        var maxCookTimeAfterConversion =
            if (maxCookTime == AppConstants.EMPTY_STRING) 0 else parseWithIntegerDefault(
                maxCookTime!!, 0
            )
        var maxCookTimeMinutes =
            0 //to store the max minutes possible when max cook time exceeds 1 hour
        var maxCookTimeIsInHours = true
        var maxCookTimeIsInMin = false
        if (maxCookTimeAfterConversion <= 60) { //when max time is less than a minute
            maxCookTimeIsInHours = false
            maxCookTimeIsInMin = isMWO
        } // when max time is less than 60 minutes
        else if (maxCookTimeAfterConversion / 60 < 60) {
            //if in case of MWO, the hours and minutes logic has to be applied to minutes and seconds
            maxCookTimeIsInMin = !isMWO
            maxCookTimeIsInHours = isMWO
            maxCookTimeMinutes = if (isMWO) maxCookTimeAfterConversion % 60 else 0
            //here, maxCookTimeAfterConversion stores the max possible minutes
            maxCookTimeAfterConversion /= 60
        } else {
            maxCookTimeMinutes =
                if (isMWO) maxCookTimeAfterConversion / 60 else maxCookTimeAfterConversion % 3600 / 60
            //when max time is more than 1 hour, we are storing the possible max minutes separately
            val divFactor = if (isMWO) 60 else 3600
            maxCookTimeAfterConversion /= divFactor
        }

        //  to remove leading zeros on string with length 1
        var updatedValueOnFirstIndex = 0
        if (currentValue.length > 1) {
            val leadingZeroRemovedValues = removeLeadingZeroes(currentValue)
            if (leadingZeroRemovedValues.isNotEmpty()) updatedValueOnFirstIndex =
                parseWithIntegerDefault(leadingZeroRemovedValues.substring(0, 1), 0)
        }
        when (index) {
            0 -> {

                //in case of first time, 0 can't be the starting digit
                if (isNumPadLoadingFirstTime) {
                    disabledKeysList.add("0")
                }

                //eg. max cook time = 1h, then on value entry, max possible minutes is 60 or any single digit in the minutes place
                if (maxCookTimeIsInHours) {
                    if (!isNumPadLoadingFirstTime && maxCookTimeAfterConversion == 1 && maxCookTimeMinutes == 0 && updatedValueOnFirstIndex >= 5) {
                        var i = 0
                        while (i <= 9) {
                            if (i != 0 && updatedValueOnFirstIndex != 6) disabledKeysList.add(
                                i.toString()
                            )
                            i++
                        }
                    }
                    return disabledKeysList
                } else if (maxCookTimeIsInMin) {
                    // eg. default max value - 8 minutes, when loading the number pad first time 9 should be disabled
                    if (isNumPadLoadingFirstTime) {
                        var i = 9
                        while (i > maxCookTimeAfterConversion) {
                            disabledKeysList.add(i.toString())
                            i--
                        }
                    } else if (maxCookTimeAfterConversion.toString().length == 1) {
                        var i = 0
                        while (i <= 9) {
                            disabledKeysList.add(i.toString())
                            i++
                        }
                    } else {
                        if (updatedValueOnFirstIndex == maxCookTimeAfterConversion / 10) {
                            var i = 9
                            while (i > maxCookTimeAfterConversion % 10) {
                                disabledKeysList.add(i.toString())
                                i--
                            }
                        } else if (updatedValueOnFirstIndex > maxCookTimeAfterConversion / 10) {
                            var i = 0
                            while (i <= 9) {
                                disabledKeysList.add(i.toString())
                                i++
                            }
                        }
                    }
                    return disabledKeysList
                } else {
                    var i = 0
                    while (i <= 9) {
                        disabledKeysList.add(i.toString())
                        i++
                    }
                }
                return disabledKeysList
            }

            1 -> {
                var updatedValueOnOneTwoIndex = 0
                var valueOnZeroOneIndex = 0
                if (currentValue.length > 1) {
                    val leadingZeroRemovedValues = removeLeadingZeroes(currentValue)
                    if (leadingZeroRemovedValues.length > 1) {
                        updatedValueOnOneTwoIndex =
                            parseWithIntegerDefault(leadingZeroRemovedValues.substring(1, 2), 0)
                        valueOnZeroOneIndex =
                            parseWithIntegerDefault(leadingZeroRemovedValues.substring(0, 1), 0)
                    }
                }
                if (maxCookTimeIsInHours) {
                    if (maxCookTimeAfterConversion.toString().length == 1) {
                        if (maxCookTimeMinutes == 0) {
                            // if the maxCookTimeAfterConversion is 3 hours and user enters 30 as first 2 digits
                            if (valueOnZeroOneIndex == maxCookTimeAfterConversion &&
                                updatedValueOnOneTwoIndex == 0
                            ) {
                                var i = 1
                                while (i <= 9) {
                                    disabledKeysList.add(i.toString())
                                    i++
                                }
                            } // if the maxCookTimeAfterConversion is 3 hours and user enters 31 as first 2 digits
                            else if (valueOnZeroOneIndex == maxCookTimeAfterConversion &&
                                updatedValueOnOneTwoIndex > 0
                            ) {
                                var i = 0
                                while (i <= 9) {
                                    disabledKeysList.add(i.toString())
                                    i++
                                }
                            } // if the maxCookTimeAfterConversion is 3 hours and user enters 42 as first 2 digits
                            else if (valueOnZeroOneIndex > maxCookTimeAfterConversion) {
                                var i = 0
                                while (i <= 9) {
                                    disabledKeysList.add(i.toString())
                                    i++
                                }
                            }
                        } //if the max cook time is 1 hour and 23 minutes and user enters 14 as first 2 digits
                        else {
                            if (updatedValueOnOneTwoIndex > maxCookTimeMinutes / 10) {
                                var i = 0
                                while (i <= 9) {
                                    disabledKeysList.add(i.toString())
                                    i++
                                }
                            } //if the max cook time is 1 hour and 23 minutes and user enters 12 as first 2 digits
                            else if (updatedValueOnOneTwoIndex == maxCookTimeMinutes / 10) {
                                var i = 9
                                while (i > maxCookTimeMinutes % 10) {
                                    disabledKeysList.add(i.toString())
                                    i--
                                }
                            }
                        }
                    }
                    return disabledKeysList
                } else if (maxCookTimeIsInMin) {
                    var i = 0
                    while (i <= 9) {
                        disabledKeysList.add(i.toString())
                        i++
                    }
                }
                return disabledKeysList
            }

            2 -> {
                var updatedValueOnSecondIndex = 0
                var updatedValueOnTwoThreeIndex = 0
                if (currentValue.length > 1) {
                    val leadingZeroRemovedValues = removeLeadingZeroes(currentValue)
                    if (leadingZeroRemovedValues.length > 1) {
                        updatedValueOnSecondIndex = parseWithIntegerDefault(
                            removeLeadingZeroes(
                                leadingZeroRemovedValues
                            ).substring(0, 2), 0
                        )
                    }
                }
                if (currentValue.length > 2) {
                    val leadingZeroRemovedValues = removeLeadingZeroes(currentValue)
                    if (leadingZeroRemovedValues.length > 2) {
                        updatedValueOnTwoThreeIndex = parseWithIntegerDefault(
                            removeLeadingZeroes(
                                leadingZeroRemovedValues
                            ).substring(2, 3), 0
                        )
                    }
                }
                if (maxCookTimeIsInHours) {
                    if (isNumberValid(currentValue)) {
                        if (updatedValueOnSecondIndex > maxCookTimeAfterConversion || updatedValueOnSecondIndex == maxCookTimeAfterConversion && updatedValueOnTwoThreeIndex > maxCookTimeMinutes / 10) {
                            var i = 0
                            while (i <= 9) {
                                disabledKeysList.add(i.toString())
                                i++
                            }
                        } else if (updatedValueOnSecondIndex == maxCookTimeAfterConversion &&
                            updatedValueOnTwoThreeIndex == maxCookTimeMinutes / 10
                        ) {
                            var i = 9
                            while (i > maxCookTimeMinutes % 10) {
                                disabledKeysList.add(i.toString())
                                i--
                            }
                        }
                    }
                }
            }

            else -> {}
        }
        return disabledKeysList
    }

    /**
     * Given string value to Integer parsing
     *
     * @param number     string to Integer value
     * @param defaultVal default value if number not valid
     * @return int value
     */
    private fun parseWithIntegerDefault(number: String, @Suppress("SameParameterValue") defaultVal: Int): Int {
        return try {
            number.toInt()
        } catch (e: NumberFormatException) {
            defaultVal
        }
    }

    /**
     * Method to remove leading zeroes
     *
     * @param inputString Input String
     * @return string after removing the leading zeroes
     */
    private fun removeLeadingZeroes(inputString: String): String {
        val sb = StringBuilder(inputString)
        while (sb.isNotEmpty() && sb[0] == '0') {
            sb.deleteCharAt(0)
        }
        return sb.toString()
    }

    /**
     * Method to check whether input String is valid
     *
     * @param inputString Input String
     * @return boolean
     */
    private fun isNumberValid(inputString: String): Boolean {
        val sb = java.lang.StringBuilder(inputString)
        while (sb.isNotEmpty() && sb[0] == '0') {
            sb.deleteCharAt(0)
        }
        return sb.isNotEmpty()
    }

    /**
     * function to get current time in seconds
     */
    fun getCurrentTimeInSeconds(): Long {
        // Get the current time
        val calendar = Calendar.getInstance()

        // Get the hour, minute, and AM/PM components
        val hour = calendar[Calendar.HOUR]
        val minute = calendar[Calendar.MINUTE]

        // Convert the time to seconds
        return ((hour * MINUTES_IN_AN_HOUR + minute) * SECONDS_IN_A_MINUTE).toLong()
    }

    /**
     * Method to convert given seconds value to Hour and Minute
     *
     * @param seconds    : seconds value in int
     * @param returnType : Current fragment
     * @return String - given time Format will be returned.
     */
    fun getSecondsToHourMinute(seconds: Int, returnType: Int): String {
        var secondsToHourMinute = 0
        when (returnType) {
            TimeFormat.HOURS_FORMAT -> secondsToHourMinute = seconds / 3600
            TimeFormat.MINUTES_FORMAT -> secondsToHourMinute = seconds % 3600 / 60
            TimeFormat.SECONDS_FORMAT -> secondsToHourMinute = seconds % 60
            else -> {}
        }
        return twoDigitString(secondsToHourMinute)
    }

    /**
     * Method to convert given number to 2digit string
     *
     * @param number : seconds value in int
     */
    private fun twoDigitString(number: Int): String {
        if (number == 0) {
            return AppConstants.EMPTY_STRING
        }
        return if (number / 10 == 0) {
            AppConstants.EMPTY_STRING + number
        } else number.toString()
    }

    fun getTimeInHHMMSS(timeInSec: Long): String {
        val cookTimeValue: Time = convertTime(timeInSec)
        var textWithTime = ""
        if (0L != cookTimeValue.hours) {
            if (cookTimeValue.hours <= 9) {
                textWithTime += "0"
            }
            textWithTime += cookTimeValue.hours
            textWithTime += ":"
            if (cookTimeValue.minutes <= 9) {
                textWithTime += "0"
            }
            textWithTime += cookTimeValue.minutes
            textWithTime += ":"
            if (cookTimeValue.seconds <= 9) {
                textWithTime += "0"
            }
            textWithTime += cookTimeValue.seconds
        } else if (0L != cookTimeValue.minutes) {
            if (cookTimeValue.minutes <= 9) {
                textWithTime += "0"
            }
            textWithTime += cookTimeValue.minutes
            textWithTime += ":"
            if (cookTimeValue.seconds <= 9) {
                textWithTime += "0"
            }
            textWithTime += cookTimeValue.seconds
        } else if (0L != cookTimeValue.seconds) {
            textWithTime += "00"
            textWithTime += ":"
            if (cookTimeValue.seconds <= 9) {
                textWithTime += "0"
            }
            textWithTime += cookTimeValue.seconds
        } else {
            //Time is up
            textWithTime = "00:00"
        }
        return textWithTime
    }

    @Suppress("RedundantNullableReturnType")
    fun getDelayUntilTime(context: Context, delayTime: Long): String? {
        val delayCalendarPlaceHolder = Calendar.getInstance()
        delayCalendarPlaceHolder.add(Calendar.SECOND, delayTime.toInt())
        var isAmPm = ""
        val delayedUntil: String
        var hours: Int
        val timeFormat: SettingsManagerUtils.TimeFormatSettings = SettingsManagerUtils.getTimeFormat()
        if (timeFormat === SettingsManagerUtils.TimeFormatSettings.H_12) {
            isAmPm =
                if (delayCalendarPlaceHolder[Calendar.AM_PM] == Calendar.AM) context.resources.getString(
                    R.string.text_label_am
                ) else context.resources.getString(R.string.text_label_pm)
            hours = delayCalendarPlaceHolder[Calendar.HOUR]
            hours = if (hours == 0) 12 else hours
        } else {
//            hours = delayCalendarPlaceHolder.getTime().getHours();
            hours = delayCalendarPlaceHolder[Calendar.HOUR_OF_DAY]
        }
        val time = String.format(
            Locale.getDefault(), "%d:%02d %s", hours,
            delayCalendarPlaceHolder[Calendar.MINUTE], isAmPm
        )
        delayedUntil = context.resources.getString(R.string.text_delayed_until_self_clean, time)
        return delayedUntil
    }

    /**
     * get time value in string to show until or ready at 1:20 AM, 4:45 PM
     *
     * @param setValueInSeconds set value in seconds
     * @return string to show until or ready time
     */
    @SuppressLint("SimpleDateFormat")
    fun getUntilReadyTime(setValueInSeconds: Long): String{
        val oldDate = Calendar.getInstance()
        oldDate.add(Calendar.SECOND, setValueInSeconds.toInt())
        val newDate = oldDate.time
        val timeFormat = if(SettingsManagerUtils.getTimeFormat() ==  SettingsManagerUtils.TimeFormatSettings.H_12) TEXT_TIME_FORMAT_HOUR else TEXT_TIME_FORMAT_24_HOUR
        val dateFormat: DateFormat = SimpleDateFormat(timeFormat)
        return dateFormat.format(newDate)
    }

    /**
     * get Time from bundle values
     * @param arguments bundle values from fragments
     * @return time string from given bundle
     */
    fun getTimeStringArguments(arguments: Bundle?): String? {
        @Suppress("DEPRECATION")
        return if (arguments != null && arguments.containsKey(BundleKeys.BUNDLE_PROVISIONING_TIME) && arguments[BundleKeys.BUNDLE_PROVISIONING_TIME] != null) {
            arguments.getString(BundleKeys.BUNDLE_PROVISIONING_TIME)
        } else {
            SimpleDateFormat(
                getTimeFormatString(),
                Locale.getDefault()
            ).format(
                Calendar.getInstance().time
            )
        }
    }

    /**
     * 12/24 hr time format
     * @return time formatted string
     */
    fun getTimeFormatString(): String {
        return if (java.lang.Boolean.TRUE == SettingsViewModel.getSettingsViewModel()
                .is12HourTimeFormat().value
        ) {
            HOUR_MINUTE_AMPM_FORMAT
        } else {
            DAY_HOUR_FORMAT
        }
    }

    /**
     * Coverts time from seconds into hours, minutes and seconds
     *
     * @param timeInSeconds time in seconds
     * @return time object containing converted time in hours, minutes and seconds
     */
    fun convertTime(timeInSeconds: Long): Time {
        val hours = TimeUnit.SECONDS.toHours(timeInSeconds)
        val minutes = TimeUnit.SECONDS.toMinutes(timeInSeconds) - TimeUnit.HOURS.toMinutes(hours)
        val sec = TimeUnit.MINUTES.toSeconds(minutes)
        val seconds = timeInSeconds - TimeUnit.HOURS.toSeconds(hours) - sec
        return Time(hours, minutes, seconds)
    }

    /**
     * Coverts time from seconds into Hours and Minutes
     *
     * @param timeInSeconds time in seconds
     * @return time object containing converted time in Hours and Minutes
     */
    fun convertTimeToHoursAndMinutes(timeInSeconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(timeInSeconds).toInt()
        val minutes =
            (TimeUnit.SECONDS.toMinutes(timeInSeconds) - TimeUnit.HOURS.toMinutes(hours.toLong())).toInt()
        val seconds = 0
        return String.format(
            Locale.getDefault(), "%02d%02d%02d",
            hours, minutes, seconds
        )
    }

    fun getBuildDate(): Date {
        val lastUpdateTime: Date = Date(BuildConfig.BUILD_DATE)
        return lastUpdateTime
    }
    /**
 * Coverts time from seconds into Hours and Minutes
 *
 * @param timeInSeconds time in seconds
 * @return time object containing converted time in Hours and Minutes
 */
    fun convertTimeToMinutesAndSeconds(timeInSeconds: Long): String {
        val hours = 0
        val min = TimeUnit.SECONDS.toMinutes(timeInSeconds)
        val minutes = TimeUnit.SECONDS.toMinutes(timeInSeconds).toInt()
        val seconds = (timeInSeconds - TimeUnit.MINUTES.toSeconds(min)).toInt()

        return String.format(
            Locale.getDefault(), "%02d%02d%02d",
            hours, minutes, seconds
        )

    }

    /**
     * Coverts time from seconds into Hours and Minutes
     *
     * @param timeInSeconds time in seconds
     * @return time object containing converted time in Hours and Minutes
     */
    fun convertTimeToMinutes(timeInSeconds: Long): String {
        val minutes = TimeUnit.SECONDS.toMinutes(timeInSeconds)
        return minutes.toString()
    }

    class Time(val hours: Long, val minutes: Long, val seconds: Long)


    /**
     * This method is to update the disable keys list based on the temperature input entered by the user
     * and also based on the Min and Max temperature allowed for that particular cycle selected
     * this method is used for both the keypad input value and the delete value from Temperature numPad
     *
     * @param index             Index
     * @param currentValue      Current value of selection
     * @param currentIndexValue Current Index value
     * @param minTemperature    Minimum temperature range
     * @param maxTemperature    Maximum temperature range
     */
    fun disableKeypadItemsForTemp(
        index: Int,
        currentValue: String,
        currentIndexValue: Int,
        minTemperature: String,
        maxTemperature: String
    ): ArrayList<String> {
        val disabledKeysList = ArrayList<String>()
        if (minTemperature.toInt() > 0 && maxTemperature.toInt() > 0) {
            val isMinTempOnlyTwoDigit =
                minTemperature.toInt() < 100 && maxTemperature.toInt() >= 100
            val isMaxTempOnlyTwoDigit = minTemperature.toInt() >= 0 && maxTemperature.toInt() < 100

            var minNumericValue = 0
            var maxNumericValueDigit1 = 0
            var maxNumericValueDigit0 = 0

            if (isMinTempOnlyTwoDigit && index >= 2) {
                minNumericValue = minTemperature[1].digitToInt()
                maxNumericValueDigit1 = maxTemperature[index].digitToInt()
            } else if (isMaxTempOnlyTwoDigit) {
                minNumericValue = minTemperature[if (index == 0) 0 else 1].digitToInt()
                maxNumericValueDigit0 = maxTemperature[0].digitToInt()
                maxNumericValueDigit1 = maxTemperature[1].digitToInt()
            } else {
                minNumericValue = minTemperature[index].digitToInt()
                maxNumericValueDigit1 = maxTemperature[index].digitToInt()
            }


            when (index) {
                0 -> {
                    if (isMaxTempOnlyTwoDigit) {
                        for (i in 0..9) {
                            if (i < minNumericValue) {
                                disabledKeysList.add(i.toString())
                            }
                        }
                        for (i in (maxNumericValueDigit0 + 1)..9) {
                            disabledKeysList.add(i.toString())
                        }
                    } else if (!isMinTempOnlyTwoDigit) {
                        for (i in (minNumericValue - 1) downTo 0) {
                            disabledKeysList.add(i.toString())
                        }
                        for (i in (maxNumericValueDigit1 + 1)..9) {
                            disabledKeysList.add(i.toString())
                        }
                    } else {
                        for (i in 0..9) {
                            if (i < minNumericValue && i > maxNumericValueDigit1) {
                                disabledKeysList.add(i.toString())
                            }
                        }
                    }
                }

                1 -> {
                    if (isMaxTempOnlyTwoDigit) {
                        val currentTemperatureValue1 = currentValue.substring(2, 3).toInt()
                        if (currentTemperatureValue1 == maxNumericValueDigit1) {
                            for (i in 0 until maxNumericValueDigit1) {
                                disabledKeysList.add(i.toString())
                            }
                        }
                    } else if (isMinTempOnlyTwoDigit && currentValue[0].toString()
                            .toInt() > maxTemperature[1].toString().toInt()
                    ) {
                        for (i in 0..9) {
                            disabledKeysList.add(i.toString())
                        }
                    }
                    val currentTemperatureValue1 = currentValue[0].toString().toInt()
                    if (currentTemperatureValue1 > minTemperature.substring(0, index).toInt() &&
                        currentTemperatureValue1 < maxTemperature.substring(0, index).toInt()
                    ) {
                        return disabledKeysList
                    }

                    if (isValidCurrentIndexValue(index, currentIndexValue, minTemperature)) {
                        for (i in (minNumericValue - 1) downTo 0) {
                            disabledKeysList.add(i.toString())
                        }
                    }
                    if (isValidCurrentIndexValue(index, currentIndexValue, maxTemperature)) {
                        for (i in (maxNumericValueDigit1 + 1)..9) {
                            disabledKeysList.add(i.toString())
                        }
                    }
                }

                2 -> {
                    if (isMaxTempOnlyTwoDigit) {
                        for (i in 9 downTo 0) {
                            disabledKeysList.add(i.toString())
                        }
                    } else if (isMinTempOnlyTwoDigit && currentValue[1].toString()
                            .toInt() > maxTemperature[0].toString().toInt()
                    ) {
                        for (i in 0..9) {
                            disabledKeysList.add(i.toString())
                        }
                    }
                    val currentTemperatureValue = currentValue.substring(1, 3).toInt()
                    if (currentTemperatureValue > minTemperature.substring(0, index).toInt() &&
                        currentTemperatureValue < maxTemperature.substring(0, index).toInt()
                    ) {
                        return disabledKeysList
                    }

                    if (isValidCurrentIndexValue(index, currentIndexValue, minTemperature)) {
                        for (i in (minNumericValue - 1) downTo 0) {
                            disabledKeysList.add(i.toString())
                        }
                    }
                    if (isValidCurrentIndexValue(index, currentIndexValue, maxTemperature)) {
                        if (currentValue[1].toString().toInt() >= maxTemperature[0].toString()
                                .toInt()
                        ) {
                            for (i in (maxNumericValueDigit1 + 1)..9) {
                                disabledKeysList.add(i.toString())
                            }
                        }
                    }
                }
            }
        }
        return disabledKeysList
    }

    private fun isValidCurrentIndexValue(
        index: Int,
        currentIndexValue: Int,
        rangeValue: String
    ): Boolean {
        return currentIndexValue != -1 &&
                currentIndexValue == Character.getNumericValue(rangeValue[index - 1])
    }

    /**
     * get day from given date array
     * @param dateArray array of date values
     * @return formatted day with user preferred
     */
    fun getDayFromDateArray(dateArray: Array<String>): String {
        return dateArray[0]
    }

    /**
     * get month from given date array
     * @param dateArray array of date values
     * @return formatted month with user preferred
     */
    fun getMonthFromDateArray(dateArray: Array<String>): String {
        return CookingAppUtils.convertStringToInt(dateArray[1]).toString()
    }

    /**
     * get Date from Arguments
     * @param arguments bundle values
     * @return string array of dates
     */
    fun getDateArrayArguments(arguments: Bundle?): Array<String> {
        val dateArray: Array<String>
        if (arguments != null && arguments.containsKey(BundleKeys.BUNDLE_PROVISIONING_DATE)) {
            dateArray =
                arguments.getString(BundleKeys.BUNDLE_PROVISIONING_DATE)
                    ?.split(AppConstants.SLASH.toRegex())
                    ?.dropLastWhile { it.isEmpty() }
                    ?.toTypedArray()?: emptyArray()
        } else {
            return getFormattedDateArray()
        }
        return dateArray
    }

    /**
     * formatted date array by user preferred
     * @return string array of dates
     */
    fun getFormattedDateArray(): Array<String> {
        HMILogHelper.Logd("Unboxing","Date format = ${SettingsViewModel.getSettingsViewModel().dateFormatString.value}")
        val currentTime = Calendar.getInstance().time
        val formattedDate = SimpleDateFormat(
            SettingsViewModel.getSettingsViewModel().dateFormatString.value,
            Locale.getDefault()
        ).format(currentTime)
        return formattedDate.split(AppConstants.SLASH.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     *convert 12 hr format to 24 hr format
     * @param time12 - 12 hr time
     * @return Pair value - hour, min
     */
    fun convert12HrTo24Hr(time12: String): Pair<String,String> {
        try {
            val formatter12Hr = DateTimeFormatter.ofPattern(TIME_24HR_FORMAT)
            val formatter24Hr = DateTimeFormatter.ofPattern(TIME_12HR_FORMAT)
            val localTime = LocalTime.parse(time12, formatter12Hr)
            val (hour24,minute) = localTime.format(formatter24Hr).split(AppConstants.COLON)
            return Pair(hour24,minute)
        } catch (e: DateTimeParseException) {
            HMILogHelper.Logd("Unboxing", "convert12To24 Error: ${e.message}")
        }
        return Pair(AppConstants.TIME_TWELVE_HR,AppConstants.TIME_ZERO_HR)
    }

    /**
     *convert 24 hr format to 12 hr format
     * @param time24 - 24 hr time
     * @return Pair value - hour, min
     */
    fun convert24HrTo12Hr(time24: String,timeAMPM:MutableList<String>): Triple<String, String, String> {
        try {
            val formatter = DateTimeFormatter.ofPattern(TIME_12HR_FORMAT)
            val localTime = LocalTime.parse(time24, formatter)

            val hour12 = if (localTime.hour > AppConstants.DIGIT_TWELVE) localTime.hour - AppConstants.DIGIT_TWELVE else localTime.hour
            val amPm = if (localTime.hour < AppConstants.DIGIT_TWELVE) timeAMPM[AppConstants.DIGIT_ZERO] else timeAMPM[AppConstants.DIGIT_ONE]
            return Triple(convertToTwoDigitString(hour12), convertToTwoDigitString(localTime.minute), amPm)
        } catch (e: DateTimeParseException) {
            HMILogHelper.Logd("Unboxing", "convert24HrTo12Hr Error: ${e.message}")
        }
        return Triple(
            AppConstants.TIME_TWELVE_HR,
            AppConstants.TIME_ZERO_HR,
            timeAMPM[AppConstants.DIGIT_ZERO]
        )
    }

    /**
     *
     */
    private fun convertToTwoDigitString(number: Int): String {
        return String.format(Locale.getDefault(),AppConstants.DEFAULT_DATE_VALUE_FORMAT, number)
    }

    /**
     * get relative time from given date string
     */
    fun getRelativeTime(dateString: String, context: Context, is24HourFormat: Boolean): String {
        // Use correct input format to parse the date string
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())

        try {
            // Parse the input date string
            val date: Date = inputFormat.parse(dateString) ?: return ""

            // Create the appropriate format for output
            val outputFormat = if (is24HourFormat) {
                SimpleDateFormat("HH:mm", Locale.getDefault()) // 24-hour format
            } else {
                SimpleDateFormat("hh:mm a", Locale.getDefault()) // 12-hour format
            }

            // Get the relative time string for the date (e.g., "5 minutes ago")
            val relativeTime = DateUtils.getRelativeTimeSpanString(
                date.time,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE
            )

            // Append the formatted time to the relative time string
            return "$relativeTime, ${outputFormat.format(date)}"

        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }
    }

    fun getTime(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // AM/PM format

        try {
            val date: Date = inputFormat.parse(dateString)!!
            val formattedTime = outputFormat.format(date)
            return formattedTime
        } catch (e: Exception) {
            e.printStackTrace()
            return " "
        }
    }
}