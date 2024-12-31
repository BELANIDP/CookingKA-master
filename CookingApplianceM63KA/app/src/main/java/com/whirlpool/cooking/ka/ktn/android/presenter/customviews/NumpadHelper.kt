package android.presenter.customviews


/*
*
*  * ************************************************************************************************
*  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
*  * ************************************************************************************************
*
*/
import android.content.Context
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uicomponents.widgets.keyboard.Keyboard
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel

/**
 * File       : com.whirlpool.cooking.ka.ktn.presenter.testwidgets.customviews.NumpadHelper
 * Brief      : this class provides common functionality for numpad
 * Author     : PETEG
 * Created On : 04-01-2024
 * Details    : this class provides common functionality for numpad
 */
class NumpadHelper {
    /* to get keyboard view model*/
    var keyboardCommonVM: KeyboardViewModel? = null
        private set

    /* initialize the numpad keyboard */
    fun initNumpad(
        keyboardInputManagerInterface: KeyboardInputManagerInterface?,
        context: Context?,
    ) {
        keyboardCommonVM = KeyboardViewModel.getKeyboardViewModel()
        keyboardCommonVM?.initKeyboard(keyboardInputManagerInterface)
        keyboardCommonVM?.keyboardAlpha = Keyboard(context, R.xml.keyboard_numpad)
    }

    /* to get clear keyboard view model*/
    fun clearKeyboardViewModel() {
        keyboardCommonVM = null
    }

    /* get the list of disable number list*/
    @Suppress("VARIABLE_WITH_REDUNDANT_INITIALIZER")
    fun disableKeypadItemsForTemp(
        index: Int, currentValue: String, currentIndexValue: Int, minTemperature: String,
        maxTemperature: String,
    ): ArrayList<String> {
        val disabledKeysList = ArrayList<String>()
        val isMinTempOnlyTwoDigit = minTemperature.toInt() < 100 && maxTemperature.toInt() >= 100
        val isMaxTempOnlyTwoDigit = minTemperature.toInt() >= 0 && maxTemperature.toInt() < 100
        var minNumericValue = 0
        var maxNumericValueDigit1 = 0
        var maxNumericValueDigit0 = 0
        if (isMinTempOnlyTwoDigit && index >= 2) {
            minNumericValue = Character.getNumericValue(minTemperature[1])
            maxNumericValueDigit1 = Character.getNumericValue(maxTemperature[index])
        } else if (isMaxTempOnlyTwoDigit) {
            minNumericValue = Character.getNumericValue(minTemperature[if (index == 0) 0 else 1])
            maxNumericValueDigit0 = Character.getNumericValue(maxTemperature[0])
            maxNumericValueDigit1 = Character.getNumericValue(maxTemperature[1])
        } else {
            minNumericValue = Character.getNumericValue(minTemperature[index])
            maxNumericValueDigit1 = Character.getNumericValue(maxTemperature[index])
        }
        when (index) {
            0 -> if (isMaxTempOnlyTwoDigit) {
                run {
                    var i = 0
                    while (i <= 9) {
                        if (i < minNumericValue) disabledKeysList.add(i.toString())
                        i++
                    }
                }
                var i = maxNumericValueDigit0 + 1
                while (i <= 9) {
                    disabledKeysList.add(i.toString())
                    i++
                }
            } else if (!isMinTempOnlyTwoDigit) {
                run {
                    var i = minNumericValue - 1
                    while (i >= 0) {
                        disabledKeysList.add(i.toString())
                        i--
                    }
                }
                var i = maxNumericValueDigit1 + 1
                while (i <= 9) {
                    disabledKeysList.add(i.toString())
                    i++
                }
            } else {
                var i = 0
                while (i <= 9) {
                    if (i in (maxNumericValueDigit1 + 1) until minNumericValue) disabledKeysList.add(
                        i.toString()
                    )
                    i++
                }
            }

            1 -> {
                if (isMaxTempOnlyTwoDigit) {
                    val currentTemperatureValue1 = currentValue.substring(2, 3).toInt()
                    if (currentTemperatureValue1 == maxNumericValueDigit1) {
                        var i = 0
                        while (i <= maxNumericValueDigit1) {
                            disabledKeysList.add(i.toString())
                            i++
                        }
                    }
                } else if (isMinTempOnlyTwoDigit && currentValue.substring(0, 1)
                        .toInt() > maxTemperature.substring(1, 2).toInt()
                ) {
                    var i = 0
                    while (i <= 9) {
                        disabledKeysList.add(i.toString())
                        i++
                    }

                }
                // check if the number entered in the range of min and max then we are allowed to enable all the keys
                val currentTemperatureValue1 = currentValue.substring(0, 1).toInt()
                if (currentTemperatureValue1 > minTemperature.substring(0, index)
                        .toInt() && currentTemperatureValue1 < maxTemperature.substring(0, index)
                        .toInt()
                ) {
                    return disabledKeysList
                }
                // If the current index is valid and  the value before entered was equal to the value at the index in the
                // Minimum temperature then disable all the keys lesser than the value at that index
                if (isValidCurrentIndexValue(index, currentIndexValue, minTemperature)) {
                    var i = minNumericValue - 1
                    while (i >= 0) {
                        disabledKeysList.add(i.toString())
                        i--
                    }
                }
                // If the current index is valid and the value entered before was equal to the value at the index in
                // the Maximum temperature then disable the values that are greater than the second index as we cannot
                // allow user to enter value higher than that
                if (isValidCurrentIndexValue(index, currentIndexValue, maxTemperature)) {
                    var i = maxNumericValueDigit1 + 1
                    while (i <= 9) {
                        disabledKeysList.add(i.toString())
                        i++
                    }
                }
            }

            2 -> {
                if (isMaxTempOnlyTwoDigit) {
                    var i = 9
                    while (i >= 0) {
                        disabledKeysList.add(i.toString())
                        i--
                    }
                } else if (isMinTempOnlyTwoDigit && currentValue.substring(1, 2)
                        .toInt() > maxTemperature.substring(0, 1).toInt()
                ) {
                    var i = 0
                    while (i <= 9) {
                        disabledKeysList.add(i.toString())
                        i++
                    }

                }
                // check if the number entered in the range of min and max then we are allowed to enable all the keys
                val currentTemperatureValue = currentValue.substring(1, 3).toInt()
                if (currentTemperatureValue > minTemperature.substring(0, index)
                        .toInt() && currentTemperatureValue < maxTemperature.substring(0, index)
                        .toInt()
                ) {
                    return disabledKeysList
                }
                // If the current index is valid and  the value before entered was equal to the value at the index in the
                // Minimum temperature then disable all the keys lesser than the value at that index
                if (isValidCurrentIndexValue(index, currentIndexValue, minTemperature)) {
                    var i = minNumericValue - 1
                    while (i >= 0) {
                        disabledKeysList.add(i.toString())
                        i--
                    }
                }
                // If the current index is valid and the value entered before was equal to the value at the index in
                // the Maximum temperature then disable the values that are greater than the second index as we cannot
                // allow user to enter value higher than that
                if (isValidCurrentIndexValue(index, currentIndexValue, maxTemperature)) {
                    var i = maxNumericValueDigit1 + 1
                    while (i <= 9) {
                        disabledKeysList.add(i.toString())
                        i++
                    }
                }
            }

            else -> {}
        }
        return disabledKeysList
    }

    /* verify the current index value*/
    private fun isValidCurrentIndexValue(
        index: Int,
        currentIndexValue: Int,
        rangeValue: String,
    ): Boolean {
        return currentIndexValue != -1 &&
                currentIndexValue == Character.getNumericValue(rangeValue[index - 1])
    }

    /*
    * for clear the text
    * */
    @Suppress("unused")
    fun clearText() {
        keyboardCommonVM!!.onKey(Keyboard.KEYCODE_CLEAR, null)
    }

    companion object {
        /*is used to validate the time*/
        fun isValidTime(hour: Int, min: Int, isTwelveHrFormat: Boolean): Boolean {
            val timeVal = hour * 60 + min
            val isValid: Boolean = if (isTwelveHrFormat) {
                // Values in min.
                val maxTimeVal = 779 // 12 * 60 + 59
                hour in 1..12 && 59 >= min && timeVal <= maxTimeVal
            } else {
                // Values in min.
                val maxTimeVal = 1439 // 23 * 60 + 59
                23 >= hour && 59 >= min && timeVal <= maxTimeVal
            }
            return isValid
        }
    }
}