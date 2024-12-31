/***Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL***/
package core.utils

import android.content.Context
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.settings.SettingsViewModel.TemperatureUnit

/*
 * File : core.utils.TemperatureUtils
 * Author : SINGHA80.
 * Created On : 3/27/24
 * Details : Provides helper methods
 */
object TemperatureUtils {
    fun getTemperatureFormat(context: Context): String {
        val tempFormat = SettingsViewModel.getSettingsViewModel()
            .temperatureUnit.value
        return if (tempFormat == TemperatureUnit.FAHRENHEIT) {
            context.resources.getString(R.string.text_tiles_list_fahrenheit_value)
        } else {
            context.resources.getString(R.string.text_tiles_list_celsius_value)
        }
    }
}