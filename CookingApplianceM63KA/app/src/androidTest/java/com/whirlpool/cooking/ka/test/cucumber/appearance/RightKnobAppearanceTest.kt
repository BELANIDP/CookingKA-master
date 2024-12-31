package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.app.Activity
import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.view.GenericViewTest

class RightKnobAppearanceTest {

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

    fun clickRightKnobEvent(
        activity: Activity?,
        buttonIndex: String
    ) {
        UiTestingUtils.sleep(2000)
        HMIKeyUtils.onHMIRightKnobClick(activity, buttonIndex)
    }

    /* --------------------------------------------------------------------*/


    fun checkClockVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.text_view_clock_digital_clock_time)
    }


    fun checkCavityScreenVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.upper_oven_layout)
        UiTestingUtils.isViewVisible(R.id.lower_oven_layout)
    }

    fun checkUpperCavityHoverVisibility() {
        UiTestingUtils.sleep(1000)
        val background = AppCompatResources.getDrawable(context, R.drawable.button_selected_ripple_effect)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.upper_oven_layout)),
            background
        )
    }

    fun checkLowerCavityHoverVisibility() {
        UiTestingUtils.sleep(1000)
        val background = AppCompatResources.getDrawable(context, R.drawable.button_selected_ripple_effect)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.lower_oven_layout)),
            background
        )
    }

    fun checkDurationTumblerScreenVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerString)
    }

    fun checkInstructionScreenVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.description_text)
    }

    fun checkTemperatureScreenVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBased)
    }

    fun checkManualModeStatusScreenVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.clModeLayout)
        UiTestingUtils.sleep(10000)
    }

    fun checkManualModeMoreOptionsScreenVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
    }

    fun checkManualModeCookTimeScreenVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerLeft)
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerCenter)
    }

    fun clickOnTurnOffCycle() {
        UiTestingUtils.sleep(65000)
        Espresso.onView(
            TestingUtils.withIndex(
                withId(R.id.tvOvenStateAction),
                0
            )
        ).perform(ViewActions.click())
        UiTestingUtils.sleep(2000)
        Espresso.onView(
            TestingUtils.withIndex(
                withId(R.id.tvOvenStateAction),
                0
            )
        ).perform(ViewActions.click())
    }

}