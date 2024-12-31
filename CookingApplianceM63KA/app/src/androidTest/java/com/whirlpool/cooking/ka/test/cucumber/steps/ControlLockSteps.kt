package com.whirlpool.cooking.ka.test.cucumber.steps

import com.whirlpool.cooking.ka.test.cucumber.appearance.ClockFragmentAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.appearance.ControlLockAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.appearance.DoubleManualModeAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule

class ControlLockSteps {
    private var controlLockAppearanceTest:ControlLockAppearanceTest?=null
    private var hmiKeyUtils:HMIKeyUtils?=null
    private var clockFragmentAppearanceTest:ClockFragmentAppearanceTest? = null
    private var doubleManualModeAppearanceTest:DoubleManualModeAppearanceTest? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()


    @Before
    fun setUp() {
        controlLockAppearanceTest  = ControlLockAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
        clockFragmentAppearanceTest = ClockFragmentAppearanceTest()
        doubleManualModeAppearanceTest = DoubleManualModeAppearanceTest()
    }

    @After
    fun tearDown() {
        controlLockAppearanceTest = null
        clockFragmentAppearanceTest = null
        doubleManualModeAppearanceTest = null
    }

    @And("I click on control lock")
    fun performClickOnControlLock() {
        LeakAssertions.assertNoLeaks()
        controlLockAppearanceTest?.performClickOnControlLock()
        controlLockAppearanceTest?.controlLockPopUpIsVisible()
        LeakAssertions.assertNoLeaks()
    }

    @And("I validate the control lock popup")
    fun controlLockPopupValidation(){
        controlLockAppearanceTest?.controlLockPopUpTitleValidation()
        controlLockAppearanceTest?.controlLockPopUpDescriptionValidation()
        controlLockAppearanceTest?.controlLockLeftButtonValdiation()
        controlLockAppearanceTest?.controlLockRightButtonValdiation()
    }

    @And("I click on continue")
    fun performClickOnContinue() {
        LeakAssertions.assertNoLeaks()
        controlLockAppearanceTest?.performClickOnContinue()
        LeakAssertions.assertNoLeaks()
    }

    @And("The clock screen is visible with control lock")
    fun controlLockClockScreenVisibility(){
        clockFragmentAppearanceTest?.checkAllViewsVisibility()
        controlLockAppearanceTest?.validateControlLockIcon()
    }

    @And("The sliding bar to unlock is visible")
    fun VisibilityOfSlidingBar(){
        controlLockAppearanceTest?.visibilityOfSlidingBar()
        controlLockAppearanceTest?.validateSlidingArrow()
        controlLockAppearanceTest?.validateArrow1()
        controlLockAppearanceTest?.validateArrow2()
        controlLockAppearanceTest?.validateArrow3()
        controlLockAppearanceTest?.lockicon()
        controlLockAppearanceTest?.slidingBarUnlockTextValidation()
    }

    @And("I slide all the way right to unlock")
    fun slideAllTheWayRight(){
        LeakAssertions.assertNoLeaks()
        controlLockAppearanceTest?.performSliding()
        LeakAssertions.assertNoLeaks()
    }

    @And("the clock screen is visible")
    fun clockScreenVisibility(){
        controlLockAppearanceTest?.clockScreen()
        UiTestingUtils.sleep(3000)
    }

    @Then("I see control lock status screen with both cavity running")
    fun controlLockStatus(){
        doubleManualModeAppearanceTest?.isStatusScreenBothCavityRunning()
        controlLockAppearanceTest?.validateControlLockIcon()
    }

    @And("I click on set cook time")
    fun performClickOnSetCookTime() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        controlLockAppearanceTest?.clickOnSetCookTime()
        LeakAssertions.assertNoLeaks()
    }

    @And("I click on cancel")
    fun performClickOnCancel() {
        LeakAssertions.assertNoLeaks()
        controlLockAppearanceTest?.performClickOnCancel()
        LeakAssertions.assertNoLeaks()
    }

    @And("The preference screen is visible")
    fun preferenceScreenVisibility(){
        controlLockAppearanceTest?.preferenceScreenVisibility()
    }

}