/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.utils

import androidx.annotation.StringDef
import core.utils.KnobDirection.Companion.CLOCK_WISE_DIRECTION
import core.utils.KnobDirection.Companion.COUNTER_CLOCK_WISE_DIRECTION
import core.utils.KnobDirection.Companion.UNKNOWN_DIRECTION

/**
 * File       : com.whirlpool.cooking.utils.SettingsKey
 * Brief      : Annotated interface class for managing Knob direction
 * Author     : Maruthi Nimmani
 * Created On : 03-12-2024
 * Details    : This class handles knob direction names.
 */
@Retention(AnnotationRetention.SOURCE)
@StringDef(CLOCK_WISE_DIRECTION, COUNTER_CLOCK_WISE_DIRECTION, UNKNOWN_DIRECTION)
annotation class KnobDirection {
    companion object {
        const val CLOCK_WISE_DIRECTION = "CW"
        const val COUNTER_CLOCK_WISE_DIRECTION = "CCW"
        const val UNKNOWN_DIRECTION = "UNKNOWN"
    }
}

