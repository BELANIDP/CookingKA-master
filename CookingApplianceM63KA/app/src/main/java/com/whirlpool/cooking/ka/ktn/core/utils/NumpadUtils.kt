package core.utils

import android.annotation.SuppressLint
import android.content.Context
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError.INVALID_CLOCK_RANGE_0H_23H
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError.INVALID_CLOCK_RANGE_0M_59M
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError.INVALID_CLOCK_RANGE_1H_12H
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError.INVALID_DAY_RANGE_1D_28D
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError.INVALID_DAY_RANGE_1D_29D
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError.INVALID_DAY_RANGE_1D_30D
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel.InputError.INVALID_DAY_RANGE_1D_31D
import java.text.ParseException
import java.util.Date

/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */ /**
 * File       : com.whirlpool.cooking.ka.ktn.core.utils.NumpadUtilsKtn
 * Brief      : common features for numpad
 * Author     : Gaurav Pete
 * Created On : 12-02-2024
 * Details    : common features for numpad
 */

object NumpadUtils {
    /* To convert the Fahrenheit configuration change*/
    fun isFAHRENHEITUnitConfigured(): Boolean {
        val localTemperatureUnit = SettingsViewModel.getSettingsViewModel().temperatureUnit.value
        return localTemperatureUnit == SettingsViewModel.TemperatureUnit.FAHRENHEIT
    }

    /* TO convert the 12 Hr to 24Hr */
    fun convertTo24H(twelveHrFormatString: String?): String {
        var date: Date? = null
        try {
            date = twelveHrFormatString?.let { AppConstants.twelveHrFormat.parse(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return if (date != null) {
            AppConstants.twentyFourHrFormat.format(date)
        } else {
            HMILogHelper.Logi("invalid parsing of time")
            AppConstants.EMPTY_STRING
        }
    }

    /* To convert the 24 Hr to 12 hr*/
    fun convertTo12(twentyFourHrFormatString: String?): String {
        var date: Date? = null
        try {
            date = twentyFourHrFormatString?.let { AppConstants.twentyFourHrFormat.parse(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return if (date != null) {
            AppConstants.twelveHrFormat.format(date)
        } else {
            HMILogHelper.Logi("invalid parsing of time")
            AppConstants.EMPTY_STRING
        }
    }

    /*get the error according to Input error code*/
    @SuppressLint("StringFormatInvalid")
    fun getTimeDateErrorMessage(validEntry: InputError, context: Context): String {
        var errorMessage = -1
        val startingValue = 1
        var endingValue = -1
        var isValueRangeModified = true
        when (validEntry) {
            INVALID_DAY_RANGE_1D_28D -> {
                errorMessage = R.string.text_dateError_message
                endingValue = 28
            }
            INVALID_DAY_RANGE_1D_29D -> {
                errorMessage = R.string.text_dateError_message
                endingValue = 29
            }
            INVALID_DAY_RANGE_1D_30D -> {
                errorMessage = R.string.text_dateError_message
                endingValue = 30
            }
            INVALID_DAY_RANGE_1D_31D -> {
                errorMessage = R.string.text_dateError_message
                endingValue = 31
            }
            INVALID_CLOCK_RANGE_1H_12H -> {
                errorMessage = R.string.text_error_time_format
                endingValue = 12
            }
            INVALID_CLOCK_RANGE_0H_23H -> {
                errorMessage = R.string.text_error_time_format
                endingValue = 23
            }
            INVALID_CLOCK_RANGE_0M_59M -> {
                errorMessage = R.string.text_error_time_format
                endingValue = 59
            }
            else -> isValueRangeModified = false
        }
        return if (isValueRangeModified) context.resources.getString(
            errorMessage,
            startingValue,
            endingValue
        ) else validEntry.name
    }
}