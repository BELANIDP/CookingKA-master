package core.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import com.whirlpool.hmi.utils.LogHelper


/**
 * File       : core/utils/TimeAndDateUtils.kt
 * Brief      : Contains helper utility methods which provides APIs for Time and date ONLY
 * Author     : Rajendra
 * Created On : 09/09/24
 * Details    : This Util class is provides APIs for Time and date ONLY and provide data
 */
class TimeAndDateUtils {

    companion object {

        /**
         * method to get the list of Hours
         *
         * @return
         */
        fun getHourList(): MutableList<String> {
            val tumblerElements: MutableList<String> = java.util.ArrayList()
            var i = AppConstants.DIGIT_ONE
            while (i <= AppConstants.DIGIT_TWELVE) {
                if (i < AppConstants.DIGIT_TEN) {
                    tumblerElements.add("0$i")
                } else {
                    tumblerElements.add(i.toString())
                }
                i += AppConstants.DIGIT_ONE
            }
            return tumblerElements
        }

        /**
         * method to get the list of Hours
         *
         * @return
         */
        fun get24HourList(): MutableList<String> {
            val tumblerElements: MutableList<String> = java.util.ArrayList()
            var i = 0
            while (i <= AppConstants.DIGIT_TWENTY_THREE) {
                if (i < AppConstants.DIGIT_TEN) {
                    tumblerElements.add("0$i")
                } else {
                    tumblerElements.add(i.toString())
                }
                i += 1
            }
            return tumblerElements
        }
        /**
         * method to get the list of minutes
         *
         * @return
         */
        fun getMinuteList(): MutableList<String> {
            return mutableListOf<String>().apply {
                repeat(AppConstants.DIGIT_SIXTY) { add(it.toString().padStart(AppConstants.DIGIT_TWO, '0')) }
            }
        }

        /**
         * method to get the list of day
         *
         * @return
         */
        fun getDayList(): MutableList<String> {
            val tumblerElements: MutableList<String> = java.util.ArrayList()
            var i = AppConstants.DIGIT_ONE
            while (i <= AppConstants.DIGIT_THIRTY_ONE) {
                if (i < AppConstants.DIGIT_TEN) {
                    tumblerElements.add("0$i")
                } else {
                    tumblerElements.add(i.toString())
                }
                i += AppConstants.DIGIT_ONE
            }
            return tumblerElements
        }

        /**
         * method to get the list of Month
         *
         * @return
         */
        fun getMonthList(): MutableList<String> {
            val tumblerElements: MutableList<String> = java.util.ArrayList()
            var i = AppConstants.DIGIT_ONE
            while (i <= AppConstants.DIGIT_TWELVE) {
                if (i < AppConstants.DIGIT_TEN) {
                    tumblerElements.add("0$i")
                } else {
                    tumblerElements.add(i.toString())
                }
                i += AppConstants.DIGIT_ONE
            }
            return tumblerElements
        }

        /**
         * method to get the list of Month
         *
         * @return
         */
        fun getYearList(): MutableList<String> {
            return (AppConstants.DIGIT_TWENTY_FOUR..AppConstants.DIGIT_FOURTY).map { it.toString() }.toMutableList()
        }

        /*
     * Coverts date from DDMMYYYY into DDMMYY
     *
     * @param date date in integer (DD)
     * @param month month in integer (MM)
     * @param year year in integer (YYYY)
     * @return date in DDMMYY format
     */
        fun convertDateToDDMMYY(date: Int, month: Int, year: Int): String {
            val dates = if ((date >= AppConstants.DIGIT_TEN)) date.toString() else "0$date"
            val months = if ((month >= AppConstants.DIGIT_TEN)) month.toString() else "0$month"
            val subYear = year.toString()
            val years = subYear.substring(subYear.length - AppConstants.DIGIT_TWO)
            return dates + months + years
        }

        fun startFiveSecWarningTimer(textView: TextView) {
            val warningHandler = Handler(Looper.getMainLooper())
            // Start a 5-second timer
            warningHandler.postDelayed({
                // Code to execute after 5 seconds
                textView.visibility = View.GONE
                warningHandler.removeCallbacksAndMessages(this)
            }, AppConstants.TIME_DATE_WARNING_TIMEOUT) // 5000 milliseconds = 5 seconds
        }

    }
}