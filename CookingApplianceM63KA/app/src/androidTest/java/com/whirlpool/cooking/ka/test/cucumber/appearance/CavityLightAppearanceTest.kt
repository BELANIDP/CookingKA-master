/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */

package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.presenter.customviews.topsheet.TopSheetBehavior
import android.view.View
import android.widget.FrameLayout
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uitesting.UiTestingUtils
import org.hamcrest.Matcher


/**
 * File        :com.whirlpool.cooking.ka.test.cucumber.appearance.SettingsFragmentAppearanceTest
 * Brief       : Settings screen automation test cases
 * Author      : Amar Suresh Dugam
 * Created On  : 03/04/2024
 */
class CavityLightAppearanceTest {

    fun performClickOnCavityLight() {
        UiTestingUtils.sleep(5000)
        performVisibilityExpandAndCheckState()
        UiTestingUtils.sleep(5000)
        val position = 0
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_grid_list, position)
    }

    private fun performVisibilityExpandAndCheckState() {
        val viewAction = object : ViewAction {
            override fun getDescription(): String {
                return ""
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                if (view != null) {
                    var topSheetBehavior: TopSheetBehavior<*>? = null
                    if (view is FrameLayout) {
                        topSheetBehavior = TopSheetBehavior.from(view)
                        topSheetBehavior.setState(TopSheetBehavior.STATE_EXPANDED)
                    }
                }
            }
        }
        onView(withId(R.id.top_sheet)).perform(viewAction)
    }

    fun clickOnCavityLight() {
        UiTestingUtils.sleep(4000)
        performVisibilityExpandAndCheckState()
        UiTestingUtils.sleep(1000)
        val position = 0
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_grid_list, position)
    }

    fun checkIfViewClickable() {
        UiTestingUtils.isViewClickable(R.id.recycler_view_grid_list)
    }

    fun cavityLightTitleTextValidation(text: String) {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isTextMatching(R.id.tvLightState, text)
    }
    fun cavityLightOffTitleTextValidation(text: String) {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isTextMatching(R.id.tvLightState, text)
    }

    fun waitFor4Seconds(){
        UiTestingUtils.sleep(4000)
    }
}