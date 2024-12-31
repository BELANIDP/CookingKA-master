package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.DelayAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DelaySteps {
    private var delayAppearanceTest:DelayAppearanceTest?=null
    private var hmiKeyUtils: HMIKeyUtils?=null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()


    @Before
    fun setUp() {
        delayAppearanceTest = DelayAppearanceTest()
        hmiKeyUtils = HMIKeyUtils

    }

    @After
    fun tearDown() {
        delayAppearanceTest = null
    }

    @Then("I check delay tumbler screen content for self clean")
    fun delayScreenValidation(){
        delayAppearanceTest?.delayScreenValidation()
    }
    @Then("I check oven icon matches the selected {string}")
    fun delayScreenOvenIconValidation(cavity:String){
        delayAppearanceTest?.delayScreenOvenIconValidation(cavity)
    }
    @Then("I click on Start delay button")
    fun performClickOnStartDelay(){
        LeakAssertions.assertNoLeaks()
        delayAppearanceTest?.performClickOnStartDelay()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see delayed until screen for self clean")
    fun selfCleanDelayedRunningScreenVisibility(){
        delayAppearanceTest?.selfCleanDelayedRunningScreenVisibility()
    }
    @Then("I validate delay running screen")
    fun selfCleanDelayedRunningScreenValidation(){
        delayAppearanceTest?.selfCleanDelayedRunningScreenValidation()
    }
    @Then("I validate delay running screen for manual modes")
    fun manualDelayedRunningScreenValidation(){
        delayAppearanceTest?.manualDelayedRunningScreenValidation()
    }
    @Then("I click on delay button on horizontal tumbler")
    fun performClickOnDelayOnHorizontalTumbler(){
        LeakAssertions.assertNoLeaks()
        delayAppearanceTest?.performClickOnDelayOnHorizontalTumbler()
        LeakAssertions.assertNoLeaks()
    }
}