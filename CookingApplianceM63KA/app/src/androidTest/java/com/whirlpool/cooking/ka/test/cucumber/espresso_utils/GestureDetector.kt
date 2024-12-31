package com.whirlpool.cooking.ka.test.cucumber.espresso_utils

import android.view.ViewConfiguration
import androidx.test.espresso.UiController
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Tapper

enum class GestureDetector : Tapper {
    LONG {
        override fun sendTap(
            uiController: UiController?,
            coordinates: FloatArray?,
            precision: FloatArray?,
            inputDevice: Int,
            buttonState: Int
        ): Tapper.Status {
            var downEvent =
                MotionEvents.sendDown(
                    uiController,
                    coordinates,
                    precision,
                    inputDevice,
                    buttonState
                )
                    .down
            try {
                // Duration before a press turns into a long press.
                // Factor 9 is needed, otherwise a long press is not safely detected.
                // See android.test.TouchUtils longClickView
                val longPressTimeout = (ViewConfiguration.getLongPressTimeout() * 9f).toLong()
                uiController!!.loopMainThreadForAtLeast(longPressTimeout)

                if (!MotionEvents.sendUp(uiController, downEvent)) {
                    MotionEvents.sendCancel(uiController, downEvent)
                    return Tapper.Status.FAILURE
                }
            } finally {
                downEvent!!.recycle()
                downEvent = null
            }
            return Tapper.Status.SUCCESS
        }

        @Deprecated("Deprecated in Java")
        override fun sendTap(
            uiController: UiController?,
            coordinates: FloatArray?,
            precision: FloatArray?
        ): Tapper.Status {
            return sendTap(uiController, coordinates, precision, 0, 0)
        }

    }
}