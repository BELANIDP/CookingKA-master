/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package com.whirlpool.cooking.ka.test.cucumber.steps

import com.whirlpool.cooking.ka.test.cucumber.appearance.CavityLightAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.hmi.uitesting.UiTestingUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import org.junit.runner.RunWith
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.runners.JUnit4
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import org.junit.Rule


/**
 * File        : com.whirlpool.cooking.ka.test.cucumber.steps.SettingsSteps
 * Brief       : Settings screen automation test cases
 * Author      : Amar Suresh Dugam
 * Created On  : 03/04/2024
 */
@RunWith(JUnit4::class)
class CavityLightSteps {
  
    private var cavityLightAppearanceTest: CavityLightAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        cavityLightAppearanceTest = CavityLightAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }

    @After
    fun tearDown() {
        cavityLightAppearanceTest = null
    }

    @Then("I check cavity light menu should clickable")
    fun iCheckCavityLightMenuClickable() {
        cavityLightAppearanceTest?.checkIfViewClickable()
    }
    @And("I click on cavity light")
    fun iClickOnCavityLight() {
        LeakAssertions.assertNoLeaks()
        cavityLightAppearanceTest?.clickOnCavityLight()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I check notification view visible")
    fun iCheckNotificationViewIsVisible(){
        cavityLightAppearanceTest?.cavityLightTitleTextValidation("Oven light turned on")
    }

    @Then("Settings screen has started for cavity light")
    fun settingsScreenHasStartedForCavityLight(){
        LeakAssertions.assertNoLeaks()
        cavityLightAppearanceTest?.performClickOnCavityLight()
        LeakAssertions.assertNoLeaks()
    }

    @And("I wait for 4 seconds")
    fun iWaitFor4Seconds(){
        cavityLightAppearanceTest?.waitFor4Seconds()
    }

    @Then("Settings screen has started for cavity light off")
    fun settingsScreenHasStartedForCavityLightOff(){
        LeakAssertions.assertNoLeaks()
        cavityLightAppearanceTest?.performClickOnCavityLight()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I check off notification view visible")
    fun iCheckOffNotificationViewIsVisible(){
        cavityLightAppearanceTest?.cavityLightOffTitleTextValidation("Oven light turned off")
    }

    @Then("I open the Lower Oven Door")
    fun iOpenLowerDoor() {
        hmiKeyUtils?.openAndCloseLowerOvenDoor(CookingKACucumberTests.mainActivity, true)
        UiTestingUtils.sleep(2000)
        hmiKeyUtils?.openAndCloseLowerOvenDoor(CookingKACucumberTests.mainActivity, true)
    }

    @Then("I close the Lower Oven Door")
    fun iCloseLowerDoor() {
        hmiKeyUtils?.openAndCloseLowerOvenDoor(CookingKACucumberTests.mainActivity, false)
        UiTestingUtils.sleep(2000)
        hmiKeyUtils?.openAndCloseLowerOvenDoor(CookingKACucumberTests.mainActivity, false)
    }
    @Then("I open the Upper Oven Door")
    fun iOpenUpperDoor() {
        hmiKeyUtils?.openAndCloseUpperOvenDoor(CookingKACucumberTests.mainActivity, true)
        UiTestingUtils.sleep(2000)
        hmiKeyUtils?.openAndCloseUpperOvenDoor(CookingKACucumberTests.mainActivity, true)
    }

    @Then("I close the Upper Oven Door")
    fun iCloseUpperDoor() {
        hmiKeyUtils?.openAndCloseUpperOvenDoor(CookingKACucumberTests.mainActivity, false)
        UiTestingUtils.sleep(2000)
        hmiKeyUtils?.openAndCloseUpperOvenDoor(CookingKACucumberTests.mainActivity, false)
    }
    
}