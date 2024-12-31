package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.app.Activity
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils

class LeftKnobAppearanceTest {

    val context: Context = ApplicationProvider.getApplicationContext()

    /* ------------- Common Method for Knob ----------------*/

    fun rotateKnobEvent(
        activity: Activity?,
        knobID: String,
        knobDirectionEvent: String
    ) {
        UiTestingUtils.sleep(2000)
        HMIKeyUtils.onKnobRotateEvent(activity, knobID, knobDirectionEvent)
    }

    fun clickLeftKnobEvent(
        activity: Activity?,
        buttonIndex: String
    ) {
        UiTestingUtils.sleep(2000)
        HMIKeyUtils.onHMILeftKnobClick(activity, buttonIndex)
    }

    /* --------------------------------------------------------------------*/
    fun checkClockVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.text_view_clock_digital_clock_time)
    }


   }
