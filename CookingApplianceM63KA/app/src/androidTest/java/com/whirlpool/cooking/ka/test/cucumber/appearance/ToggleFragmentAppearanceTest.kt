/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */

package com.whirlpool.cooking.ka.test.cucumber.appearance

import androidx.appcompat.widget.SwitchCompat
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndexHash
import com.whirlpool.hmi.uitesting.components.view.GenericViewTest


class ToggleFragmentAppearanceTest {

    fun checkAllViewsVisibility() {
        Espresso.onView(withIndexHash(withId(R.id.toggle_switch), 0)).check(matches(isDisplayed()))
    }

    fun toggleSwitchSizeValidationMatched() {
        val desiredHeight = 56f
        val desiredWidth = 112f
        val heightInPixel = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredHeight)
        val widthInPixel = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredWidth)
        withId(R.id.toggle_switch).matches(
            GenericViewTest.ViewSizeMatcher(
                widthInPixel,
                heightInPixel
            )
        )
    }

    fun toggleSwitchSizeValidationNotMatched() {
        val desiredHeight = 58f
        val desiredWidth = 110f
        val heightInPixel = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredHeight)
        val widthInPixel = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredWidth)
        !withId(R.id.toggle_switch).matches(
            GenericViewTest.ViewSizeMatcher(
                widthInPixel,
                heightInPixel
            )
        )
    }

    fun isToggleButtonIsChecked(viewId: Int, checked: Boolean): Boolean {
        var isChecked = false
        Espresso.onView(withIndexHash(withId(viewId), 0)).check { view, _ ->
            (view as SwitchCompat?)?.isChecked = checked
            isChecked = (view)?.isChecked!!
        }
        return isChecked
    }

    fun isToggleButtonIsEnabled(viewId: Int, enabled: Boolean): Boolean {
        var isEnable = false
        Espresso.onView(withIndexHash(withId(viewId), 0)).check { view, _ ->
            (view as SwitchCompat?)?.isEnabled = enabled
            isEnable = (view)?.isEnabled()!!
        }
        return isEnable
    }


}