package core.utils

import android.animation.TimeInterpolator
import android.view.animation.Interpolator
import kotlin.math.pow

/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */ /**
 * File       : com.whirlpool.cooking.ka.ktn.core.utils.EaseInOutExpoInterpolatorKtn
 * Brief      : we can handel the animation
 * Author     : Gaurav Pete
 * Created On : 04-01-2024
 * Details    :  we can handel the animation
 */
class EaseInOutExpoInterpolator : TimeInterpolator, Interpolator {
    override fun getInterpolation(input: Float): Float {
        return easeInOut(input, 0f, 1f, 1f)
    }

    /*
    * for inOut animation*/
    @Suppress("SameParameterValue")
    private fun easeInOut(t1: Float, b: Float, c: Float, d: Float): Float {
        var t = t1
        if (t == 0f) return b
        if (t == d) return b + c
        return if (d / 2.let { t /= it; t } < 1) c / 2 * 2.0.pow((10 * (t - 1)).toDouble())
            .toFloat() + b else c / 2 * (-2.0.pow((-10 * --t).toDouble()).toFloat() + 2) + b
    }
}
