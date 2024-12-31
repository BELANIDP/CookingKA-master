/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.utils

import androidx.annotation.IntDef
import core.utils.NumPadHelperTextColor.Companion.ERROR_TEXT_COLOR
import core.utils.NumPadHelperTextColor.Companion.NORMAL_TEXT_COLOR

/**
 * File       : com.whirlpool.cooking.utils
 * Brief      : Annotated interface class for managing helper text color
 * Author     : GHARDNS/Nikki
 * Created On : 18-03-2024
 */
@Retention(AnnotationRetention.SOURCE)
@IntDef(NORMAL_TEXT_COLOR, ERROR_TEXT_COLOR)
annotation class NumPadHelperTextColor {
    companion object {
        const val NORMAL_TEXT_COLOR = 0
        const val ERROR_TEXT_COLOR = 1
    }
}