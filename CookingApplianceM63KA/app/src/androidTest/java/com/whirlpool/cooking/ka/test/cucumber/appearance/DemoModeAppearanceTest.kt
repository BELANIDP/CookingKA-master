/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */

package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.app.ActionBar
import android.presenter.customviews.topsheet.TopSheetBehavior
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.matchesBackgroundColor
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndex
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndexHash
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import io.cucumber.java.en.And
import org.hamcrest.Matcher


/**
 * File        :com.whirlpool.cooking.ka.test.cucumber.appearance.SettingsFragmentAppearanceTest
 * Brief       : Settings screen automation test cases
 * Author      : Amar Suresh Dugam
 * Created On  : 03/04/2024
 */
class DemoModeAppearanceTest {

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

    fun performClickOnSettings() {
        UiTestingUtils.sleep(4000)
        performVisibilityExpandAndCheckState()
        UiTestingUtils.sleep(1000)
        val position = 2
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_grid_list, position)
    }

    fun performClickOnSettingsListItem() {
        UiTestingUtils.sleep(1000)
        val index = 12// Demo Mode
        onView(withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
    }

    fun isNestedScrollViewVisible() {
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.nested_scroll_view_collection)
    }

    fun instructionScreenVisibilityValidation() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
    }

    fun instructionScreenTitleTextValidation(titleText: String) {
        UiTestingUtils.sleep(2000)
        Espresso.onView(
            TestingUtils.withIndex(
                withId(R.id.text_view_title),
                0
            )
        ).check(ViewAssertions.matches(ViewMatchers.withText(titleText)))
    }

    fun demoModeInstructionScreenDescriptionTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_description, "This action will place the product in ‘Demo mode’.\nDuring Demo Mode heating elements will not work. A\ncode is needed in order to access this feature.")
    }


    fun demoModeExitInstructionScreenDescriptionTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_description, "This action will place the product out of ‘Demo Mode’\nenabling heating elements. A code is needed in order\nto exit this feature.")
    }
    fun performClickContinueButtonDemoInstruction() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.text_button_right)
    }

    fun demoModeCodeScreenVisibilityValidation()  {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isViewVisible(R.id.keyboardview)
    }

    fun setDemoCodeOnNumpad(demoCode:String){
        UiTestingUtils.sleep(1500)
        TestingUtils.enterNumberStr(demoCode)
    }

    fun iClickOnRightButton(){
        UiTestingUtils.performClick(R.id.demo_code_text_button_right)
    }

    fun isDemoModeLandingScreenVisible(){
        UiTestingUtils.sleep(300)
        UiTestingUtils.isViewVisible(R.id.exit_demo)
    }

    fun iClickExploreProductButton(){
        UiTestingUtils.sleep(300)
        UiTestingUtils.performClick(R.id.explore_product)
    }
    fun iOpenSettingsMenu(){
        UiTestingUtils.sleep(4000)
        performVisibilityExpandAndCheckState()
        UiTestingUtils.sleep(1000)
        val position = 2
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_grid_list, position)
    }
    fun iClickControlLock(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(withIndex(withId(R.id.grid_parent_view), 0)).perform(ViewActions.click())
    }

    fun featureNotAvailableTitleTextValidation(text: String) {
        UiTestingUtils.sleep(4000)
        UiTestingUtils.isTextMatching(R.id.tvLightState, text)
    }

    fun iClickRemoteEnable(){
        UiTestingUtils.sleep(1000)
//        Espresso.onView(withIndex(withId(R.id.settings_item_toggle_switch), 2)).perform(ViewActions.click())
        val index = 6// Demo Mode
        onView(withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )

        Espresso.onView(withIndex(withId(R.id.settings_item_toggle_switch), 2)).perform(ViewActions.click())
    }

    fun iClickSelfClean(){
        UiTestingUtils.sleep(4000)
        val index = 9// Self clean
        onView(withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
    }
    fun iClickConnectToNetwork(){
        UiTestingUtils.sleep(4000)
        val index = 5// Connect to Network
        onView(withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
    }

    fun performClickShowMorePreferences() {
        UiTestingUtils.sleep(1000)
        val index = 3// Self clean
        Espresso.onView(ViewMatchers.withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun preferencesrecyclerlistValidateView() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewClickable(R.id.preferencesRecyclerList)
        UiTestingUtils.isViewEnabled(R.id.preferencesRecyclerList)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)),
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        Espresso.onView(withId(R.id.preferencesRecyclerList))
            .check(matches(matchesBackgroundColor(R.color.color_black)))
    }

    fun preferencesrecyclerlistIsVisible() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.preferencesRecyclerList)
    }

    fun performClickOnTempCalibrationOpt(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(6))
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.preferencesRecyclerList, 6)
    }

    fun performClickShowMoreNetworkSettings() {
        UiTestingUtils.sleep(1000)
        val index = 6
        Espresso.onView(ViewMatchers.withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun networkSettingsRecyclerlistIsVisible() {
        UiTestingUtils.sleep(1000)
        Espresso.onView(withIndexHash(withId(R.id.list_item_main_view), 0)).check(matches(isDisplayed()))
//        UiTestingUtils.isViewVisible(R.id.connectivityRecyclerList)
    }

    fun networkSettingsRecyclerlistValidateView() {
        UiTestingUtils.sleep(1000)
        Espresso.onView(withIndexHash(withId(R.id.list_item_main_view), 0)).check(matches(
            isClickable()
        ))

        Espresso.onView(withIndexHash(withId(R.id.list_item_main_view), 0)).check(matches(
            isEnabled()
        ))

        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.connectivityRecyclerList)),
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )

        Espresso.onView(withId(R.id.connectivityRecyclerList))
            .check(matches(matchesBackgroundColor(R.color.common_solid_black)))
    }

    fun performClickOnNetworkSettingsOptions(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.connectivityRecyclerList))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(1))
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.connectivityRecyclerList, 1)
    }

    fun performClickOnSettingsListItemService() {
        UiTestingUtils.sleep(1000)
        val index = 13// Service Diagnostic
        onView(withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
    }

    fun isServiceInstructionScreenVisible(){
        UiTestingUtils.isViewVisible(R.id.fragment_service_and_support_info)
    }

    fun iClickOnEnterService(){
        UiTestingUtils.sleep(300)
        UiTestingUtils.performClick(R.id.enter_diagnostics_btn)
    }

    fun iSeeClockScreenOnDemoExit(){
        UiTestingUtils.isViewVisible(R.id.text_view_clock_digital_clock_time)
    }

    fun verifyDemoModeIcon(){
        Espresso.onView(TestingUtils.withIndex(ViewMatchers.withId(R.id.demo_icon), 0))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun iWaitfor10secpnds(){
        UiTestingUtils.sleep(12000)
    }

    fun iSeeClockScreenOnDemo(){
        UiTestingUtils.isViewVisible(R.id.text_view_clock_digital_clock_time)
    }
}