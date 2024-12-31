package com.whirlpool.cooking.ka.test.cucumber.navigation

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.utils.CookingSimConst
import org.hamcrest.Matchers.notNullValue
import org.mockito.Mockito


/**
 * File       : com.whirlpool.cooking.ka.test.cucumber.navigation
 * Brief      : PopupNavigationHelper helper class
 * Author     : GOYALM5
 * Created On : 25/02/2024
 * Details    : This class is having helper function related to popup fragment
 */

class SelfCleanNavigationHelper {
    fun performClickOnHeaderBackArrow() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.flLeftIcon)
    }

    fun performClickOnSoilLevelHeaderBackArrow() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.flLeftIcon)
    }

    fun performClickNextButtonSoilLevel() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.btnPrimary)
    }

    fun performClickInstructionScreenNextButton() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.performClick(R.id.instruction_button_next)
    }

    fun performClickDoorHasOpenedClosedScreenNextButton() {
        UiTestingUtils.sleep(1000)
        onView(withText(R.string.text_button_start)).check(matches(notNullValue()));
        onView(withText(R.string.text_button_start)).perform(click())
        UiTestingUtils.sleep(2000)
    }

    fun performClickDoorHasOpenedClosedScreenDelayButton() {
        UiTestingUtils.sleep(1000)
        onView(withText(R.string.text_button_delay)).check(matches(notNullValue()));
        onView(withText(R.string.text_button_delay)).perform(click())
        UiTestingUtils.sleep(1000)
    }

    fun performClickSelfCleanCompleteOKButton() {
        UiTestingUtils.sleep(1000)
        onView(withText(R.string.text_button_ok)).check(matches(notNullValue()));
        onView(withText(R.string.text_button_ok)).perform(click())
        UiTestingUtils.sleep(1000)
    }

    fun performStartNowButton() {
        UiTestingUtils.sleep(1000)
        CookingSimConst.simulateDoorLatchUnlockedEvent(
            CookingKACucumberTests.mainActivity,
            true
        )
        UiTestingUtils.sleep(1000)
        onView(withText(R.string.text_button_start_delay)).check(matches(notNullValue()));
        onView(withText(R.string.text_button_start_delay)).perform(click())
        UiTestingUtils.sleep(1000)

    }

    fun performStartDelayButtonFromCombo() {
        UiTestingUtils.sleep(1000)
        CookingSimConst.simulateDoorLatchUnlockedEvent(
            CookingKACucumberTests.mainActivity,
            false
        )
        UiTestingUtils.sleep(1000)
        onView(withText(R.string.text_button_start_delay)).check(matches(notNullValue()));
        onView(withText(R.string.text_button_start_delay)).perform(click())
        UiTestingUtils.sleep(1000)

    }

    fun performCancelDelayButton() {
        UiTestingUtils.sleep(1000)
        onView(withId(R.id.btnGhost)).perform(click())
        UiTestingUtils.sleep(1000)
    }

    fun navigateToSelfCleanStatusScreen() {
        UiTestingUtils.sleep(1000)
        CookingSimConst.simulateDoorLatchLockedEvent(
            CookingKACucumberTests.mainActivity,
            true
        )

        UiTestingUtils.sleep(1000)
        CookingSimConst.simulateDoorLatchUnlockedEvent(
            CookingKACucumberTests.mainActivity,
            true
        )
        UiTestingUtils.sleep(1000)
    }

    fun navigateToSelfCleanStatusScreenFromComboVariant() {
        UiTestingUtils.sleep(1000)
        CookingSimConst.simulateDoorLatchLockedEvent(
            CookingKACucumberTests.mainActivity,
            false
        )

        UiTestingUtils.sleep(1000)
        CookingSimConst.simulateDoorLatchUnlockedEvent(
            CookingKACucumberTests.mainActivity,
            false
        )
        UiTestingUtils.sleep(1000)
    }

}