/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */

package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import android.presenter.fragments.mwo.ClockFragment
import com.whirlpool.cooking.ka.test.cucumber.appearance.ToggleFragmentAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.navigation.setFragmentScenario
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToggleSteps {

    private var toggleFragmentAppearanceTest: ToggleFragmentAppearanceTest? = null

    @Before
    fun setUp() {
        toggleFragmentAppearanceTest = ToggleFragmentAppearanceTest()
    }

    @After
    fun tearDown() {
        toggleFragmentAppearanceTest = null
    }

    @Then("Toggle widget screen will be visible")
    fun checkToggleWidgetVisibility() {
        toggleFragmentAppearanceTest?.checkAllViewsVisibility()
    }

    @Then("I navigate to Toggle widget screen")
    fun navigateToToggleWidgetScreen() {
        testNavigationToToggleWidgetScreen()
    }

    @Then("I navigate to clock screen for Toggle widget screen")
    fun navigateToClockScreenForToggleWidgetScreen() {
        testNavigationToClockViewScreenForToggleWidgetScreen()
    }

    @Test
    @UiThreadTest
    fun testNavigationToToggleWidgetScreen() {
//        setFragmentScenario(ToggleSwitchWidgetTestFragment::class.java)
    }

    @Test
    @UiThreadTest
    fun testNavigationToClockViewScreenForToggleWidgetScreen() {
        setFragmentScenario(ClockFragment::class.java)
    }

    @Then("Verify the toggle button size matched")
    @Throws(Exception::class)
    fun checkToggleButtonSize() {
        toggleFragmentAppearanceTest?.toggleSwitchSizeValidationMatched()
    }

    @Then("Verify the toggle button size not matched")
    @Throws(Exception::class)
    fun checkToggleButtonSizeNotMatched() {
        toggleFragmentAppearanceTest?.toggleSwitchSizeValidationNotMatched()
    }

    @Then("Verify in toggle widget screen that the toggle button is checked")
    fun checkIfToggleButtonIsChecked() {
        val isChecked: Boolean = toggleFragmentAppearanceTest?.isToggleButtonIsChecked(
            R.id.toggle_switch,
            true
        ) ?: false
        assert(isChecked) { "Checked Successfully" }
    }

    @Then("Verify in toggle widget screen that the toggle button is unchecked")
    fun checkIfToggleButtonIsUnChecked() {
        val isChecked: Boolean = toggleFragmentAppearanceTest?.isToggleButtonIsChecked(
            R.id.toggle_switch,
            false
        ) ?: false
        assert(!isChecked) { "Unchecked Successfully" }
    }

    @Then("Verify that the toggle button is enable")
    fun checkIfToggleButtonIsEnable() {
        val isEnable: Boolean = toggleFragmentAppearanceTest?.isToggleButtonIsEnabled(
            R.id.toggle_switch,
            true
        ) ?: false
        assert(isEnable) { "Enable Successfully" }
    }

    @Then("Verify that the toggle button is disable")
    fun checkIfToggleButtonIsDisable() {
        val isEnable: Boolean = toggleFragmentAppearanceTest?.isToggleButtonIsEnabled(
            R.id.toggle_switch,
            false
        ) ?: false
        assert(!isEnable) { "Disable Successfully" }
    }
}