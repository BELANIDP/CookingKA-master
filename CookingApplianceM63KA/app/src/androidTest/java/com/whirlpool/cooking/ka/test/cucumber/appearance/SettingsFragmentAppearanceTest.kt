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
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import org.hamcrest.Matcher


/**
 * File        :com.whirlpool.cooking.ka.test.cucumber.appearance.SettingsFragmentAppearanceTest
 * Brief       : Settings screen automation test cases
 * Author      : GHARDNS/Nikki
 * Created On  : 27/02/2024
 */
class SettingsFragmentAppearanceTest {

    fun performClickOnSettings() {
        UiTestingUtils.sleep(2000)
        performVisibilityExpandAndCheckState()
        UiTestingUtils.sleep(1000)
        val position = 2
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_grid_list, position)
    }
    fun performClickOnSettingsForCombo() {
        UiTestingUtils.sleep(4000)
        performVisibilityExpandAndCheckState()
        UiTestingUtils.sleep(1000)
        val position = 2
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_grid_list, position)
    }
    fun isNestedScrollViewVisible() {
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.nested_scroll_view_collection)
    }

    fun performClickOnSettingsListItem() {
        UiTestingUtils.sleep(1000)
        val index = 9// Self clean
        onView(withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
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
}