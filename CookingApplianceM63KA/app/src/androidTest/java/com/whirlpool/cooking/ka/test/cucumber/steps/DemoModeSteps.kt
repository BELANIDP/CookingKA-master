/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package com.whirlpool.cooking.ka.test.cucumber.steps

import com.whirlpool.cooking.ka.test.cucumber.appearance.DemoModeAppearanceTest
import com.whirlpool.hmi.uitesting.UiTestingUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule


/**
 * File        : com.whirlpool.cooking.ka.test.cucumber.steps.SettingsSteps
 * Brief       : Settings screen automation test cases
 * Author      : Amar Suresh Dugam
 * Created On  : 03/04/2024
 */
@RunWith(JUnit4::class)
class DemoModeSteps {

    private var demoModeAppearanceTest: DemoModeAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        demoModeAppearanceTest = DemoModeAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }

    @After
    fun tearDown() {
        demoModeAppearanceTest = null
    }

    @And("Demo mode settings screen has started")
    fun appHasStartedOnTheDemoModeSettingsFragment() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.performClickOnSettings()
        LeakAssertions.assertNoLeaks()
    }

    @And("I navigate to demo mode settings screen")
    fun iNavigateToDemoModeSettingsScreen() {
        demoModeAppearanceTest?.isNestedScrollViewVisible()
    }

    @And("I click on demo mode")
    fun iClickOnDemoMode() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.performClickOnSettingsListItem()
        LeakAssertions.assertNoLeaks()
    }

    @And("I navigate to demo mode instructions screen")
    fun iNavigateToDemoInstructionScreen() {
        //Nav graph will handle the navigation. We have to check Instruction screen component visibility
        demoModeAppearanceTest?.instructionScreenVisibilityValidation()
    }

    @Then("I check demo instructions screen header {string} text")
    fun iCheckDemoInstructionScreenHeaderText(titleText: String) {
        demoModeAppearanceTest?.instructionScreenTitleTextValidation(titleText)
    }

    @Then("I validate description text on demo mode instructions screen")
    fun validateDemoModeEntryDescriptionPopup() {
        demoModeAppearanceTest?.demoModeInstructionScreenDescriptionTextValidation()
    }

    @And("I click on continue button on demo mode instruction screen")
    fun iClickOnContinueButtonOnSoilLevelSelection() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.performClickContinueButtonDemoInstruction()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I validate description text on demo mode Exit instructions screen")
    fun validateDemoModeExitDescriptionPopup() {
        demoModeAppearanceTest?.demoModeExitInstructionScreenDescriptionTextValidation()
    }

    @Then("I expect it should navigate to demo mode code screen")
    fun iExpectItShouldNavigateToDemoModeCodeScreen() {
        UiTestingUtils.sleep(2000)
        demoModeAppearanceTest?.demoModeCodeScreenVisibilityValidation()
    }

    @Then("I enter demo mode code {string} on numpad screen")
    fun setKtToMinAndSec(demoCode: String) {
        demoModeAppearanceTest?.setDemoCodeOnNumpad(demoCode)
    }

    @Then("I click on next button of demo mode code screen")
    fun clickOnNextButton() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.iClickOnRightButton()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see demo mode landing screen")
    fun isDemoModeLandingScreenVisible() {
        demoModeAppearanceTest?.isDemoModeLandingScreenVisible()
    }

    @Then("I click explore product button on demo landing screen")
    fun iClickExploreProductButton() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.iClickExploreProductButton()
        LeakAssertions.assertNoLeaks()
    }

    @And("I open settings from top drawer")
    fun iOpenSettingsMenu() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.iOpenSettingsMenu()
        LeakAssertions.assertNoLeaks()
    }

    @And("I click on Control lock option")
    fun iClickControlLock() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.iClickControlLock()
        LeakAssertions.assertNoLeaks()
    }

    @Then("Setting menu is visible")
    fun isSettingViewVisible() {
        demoModeAppearanceTest?.isNestedScrollViewVisible()
    }

    @Then("I see Control Lock not available notification")
    fun iCheckNotificationViewIsVisible() {
        demoModeAppearanceTest?.featureNotAvailableTitleTextValidation("Feature unavailable in demo mode")
    }

    @And("I click on Remote Enable option")
    fun iClickRemoteEnable() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.iClickRemoteEnable()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see Remote Enable not available notification")
    fun iCheckRemoteEnableNotificationViewIsVisible() {
        demoModeAppearanceTest?.featureNotAvailableTitleTextValidation("Feature unavailable in demo mode")
    }

    @And("I click on Self Clean option")
    fun iClickSelfClean() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.iClickSelfClean()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see Self Clean not available notification")
    fun iCheckSelfCleanNotificationViewIsVisible() {
        demoModeAppearanceTest?.featureNotAvailableTitleTextValidation("Feature unavailable in demo mode")
    }

    @And("I click on Connect to Network option")
    fun iClickConnectToNetwork() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.iClickConnectToNetwork()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see Connect to Network not available notification")
    fun iCheckConnectToNetworkNotificationViewIsVisible() {
        demoModeAppearanceTest?.featureNotAvailableTitleTextValidation("Feature unavailable in demo mode")
    }

    @And("I click on Show More in Preference option when demo is enabled")
    fun performClickOnShowMorePreferences() {
        UiTestingUtils.sleep(1000)
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.performClickShowMorePreferences()
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
    }

    @And("I check the preference options when demo is enabled")
    fun validatePreferenceScreen() {
        UiTestingUtils.sleep(1000)
        demoModeAppearanceTest?.preferencesrecyclerlistIsVisible()
        UiTestingUtils.sleep(1000)
        demoModeAppearanceTest?.preferencesrecyclerlistValidateView()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on Temp calibration option when demo is enabled")
    fun performClickOnTempCalibration() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.performClickOnTempCalibrationOpt()
        LeakAssertions.assertNoLeaks()

    }

    @Then("I see Temp calibration option not available notification")
    fun iCheckTempCalibrationNotificationViewIsVisible() {
        demoModeAppearanceTest?.featureNotAvailableTitleTextValidation("Feature unavailable in demo mode")
    }

    @And("I click on Show More in Network Settings option when demo is enabled")
    fun performClickOnShowMoreNetworkSettings() {
        UiTestingUtils.sleep(1000)
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.performClickShowMoreNetworkSettings()
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
    }

    @And("I check the Network Settings options when demo is enabled")
    fun validateNetworkSettingsScreen() {
        UiTestingUtils.sleep(1000)
        demoModeAppearanceTest?.networkSettingsRecyclerlistIsVisible()
        UiTestingUtils.sleep(1000)
        demoModeAppearanceTest?.networkSettingsRecyclerlistValidateView()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on Network Settings option when demo is enabled")
    fun performClickOnNetworkSettingsOptions() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.performClickOnNetworkSettingsOptions()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see Network Settings option not available notification")
    fun iCheckNetworkSettingsNotificationViewIsVisible() {
        demoModeAppearanceTest?.featureNotAvailableTitleTextValidation("Feature unavailable in demo mode")
    }

    @And("I click on Service Diagnostic option when demo is enabled")
    fun iClickOnService() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.performClickOnSettingsListItemService()
        LeakAssertions.assertNoLeaks()
    }

    @And("I check the Service Diagnostic Instruction screen when demo is enabled")
    fun isServiceInstructionScreenVisible() {
        demoModeAppearanceTest?.isServiceInstructionScreenVisible()
    }

    @And("I click on Enter Diagnostic button when demo is enabled")
    fun iClickOnEnterService() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.iClickOnEnterService()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see Enter Diagnostic option not available notification")
    fun iCheckServiceUnavailableNotificationViewIsVisible() {
        demoModeAppearanceTest?.featureNotAvailableTitleTextValidation("Feature unavailable in demo mode")
    }

    @And("I click on Demo Mode option when demo is enabled")
    fun iClickOnDemoModeExit() {
        LeakAssertions.assertNoLeaks()
        demoModeAppearanceTest?.performClickOnSettingsListItem()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see the clock screen with demo mode disabled")
    fun iSeeClockScreenOnDemoExit(){
        demoModeAppearanceTest?.iSeeClockScreenOnDemoExit()
    }

    @And("I check Clock view with Demo mode icon")
    fun verifyDemoModeIcon() {
        demoModeAppearanceTest?.verifyDemoModeIcon()
    }

    @And("I wait for 10 seconds to see if screen timeouts to demo landing screen")
    fun iWaitfor10secpnds(){
        demoModeAppearanceTest?.iWaitfor10secpnds()
    }

    @Then("I see the clock screen with demo mode")
    fun iSeeClockScreenOnDemo(){
        demoModeAppearanceTest?.iSeeClockScreenOnDemo()
    }
}