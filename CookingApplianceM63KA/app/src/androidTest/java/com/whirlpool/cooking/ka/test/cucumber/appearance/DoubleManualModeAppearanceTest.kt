package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import org.hamcrest.CoreMatchers.notNullValue
import java.util.regex.Pattern.matches

class DoubleManualModeAppearanceTest {
    val context: Context = ApplicationProvider.getApplicationContext()

    fun performClickOnUpperCavityButton(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.upper_oven_layout)
        UiTestingUtils.sleep(1500)
    }
    fun scrollToGivenIndexAndClick(index:Int){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerString))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(index))
        UiTestingUtils.sleep(2500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerString, index)
    }
    fun horizontalTumblerIsVisible() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerString)
    }

    fun horizontalTempTumblerIsVisible() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBased)
    }

    fun performClickOnBackButtonOnHorizontalTumblerScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }
    fun performClickOnStartButtonOnHorizontalTumblerScreen(){
        UiTestingUtils.sleep(1000)
        onView(withText(R.string.text_button_start)).perform(click())
        UiTestingUtils.sleep(2000)
    }
    fun scrollNumbericTumblerAndClick(index: Int){
        UiTestingUtils.sleep(1500)
//        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerString, index)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerString))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(index))

    }
    fun isPrimaryCavityStatusScreenVisible(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.singleStatusWidget))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        UiTestingUtils.sleep(1500)
    }

    fun clickSetCookTimeStatusScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.tvSetOvenCookTime)
        UiTestingUtils.sleep(1500)
    }

    fun isStatusScreenBothCavityRunning(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.doubleStatusWidgetUpper))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.doubleStatusWidgetLower))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}