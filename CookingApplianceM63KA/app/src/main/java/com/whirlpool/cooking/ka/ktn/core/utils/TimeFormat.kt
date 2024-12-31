/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.utils

import androidx.annotation.IntDef
import core.utils.TimeFormat.Companion.HOURS_FORMAT
import core.utils.TimeFormat.Companion.MINUTES_FORMAT
import core.utils.TimeFormat.Companion.SECONDS_FORMAT

@Retention(AnnotationRetention.SOURCE)
@IntDef(HOURS_FORMAT, MINUTES_FORMAT, SECONDS_FORMAT)
annotation class TimeFormat {
    companion object {
        const val HOURS_FORMAT = 1
        const val MINUTES_FORMAT = 2
        const val SECONDS_FORMAT = 3
    }
}
