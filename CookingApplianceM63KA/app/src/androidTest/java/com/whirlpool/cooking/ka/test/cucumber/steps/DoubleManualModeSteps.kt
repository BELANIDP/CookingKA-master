package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.DoubleManualModeAppearanceTest

import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DoubleManualModeSteps {
    private var doubleManualModeAppearanceTest: DoubleManualModeAppearanceTest? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        doubleManualModeAppearanceTest = DoubleManualModeAppearanceTest()
    }
    @After
    fun tearDown() {
        doubleManualModeAppearanceTest = null
    }

    @And("I perform click on upper cavity btn")
    fun perform_click_upper_cavity_navigate_to_tumbler() {
        LeakAssertions.assertNoLeaks()
        doubleManualModeAppearanceTest?.performClickOnUpperCavityButton()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I scroll tumbler to the {string} and click")
    fun tumbler_screen_scroll_and_click(index:String) {
        LeakAssertions.assertNoLeaks()
        doubleManualModeAppearanceTest?.scrollToGivenIndexAndClick(index.toInt())
        LeakAssertions.assertNoLeaks()
    }
    @Then("I see horizontal tumbler")
    fun validateTumbler(){
        doubleManualModeAppearanceTest?.horizontalTumblerIsVisible()
    }

    @Then("I see horizontal temp tumbler")
    fun validateTempTumbler(){
        doubleManualModeAppearanceTest?.horizontalTempTumblerIsVisible()
    }

    @Then("I click on back button on horizontal tumbler screen")
    fun clickOnBackButtonHorizontalTumbler(){
        LeakAssertions.assertNoLeaks()
        doubleManualModeAppearanceTest?.performClickOnBackButtonOnHorizontalTumblerScreen()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I click on start button")
    fun clickStart(){
        LeakAssertions.assertNoLeaks()
        doubleManualModeAppearanceTest?.performClickOnStartButtonOnHorizontalTumblerScreen()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I scroll numberic tumbler to {string}")
    fun scrollNumbericTumbler(High: String){
        LeakAssertions.assertNoLeaks()
        doubleManualModeAppearanceTest?.scrollNumbericTumblerAndClick(High.toInt())
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see status screen with upper running")
    fun PrimaryCavityStatusScreenVisibility(){
        doubleManualModeAppearanceTest?.isPrimaryCavityStatusScreenVisible()
    }

    @Then("I click on Set Cook Time button")
    fun clickSetCookTimefromStatus(){
        LeakAssertions.assertNoLeaks()
        doubleManualModeAppearanceTest?.clickSetCookTimeStatusScreen()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see status screen with both cavity running")
    fun validateStatusScreenForBothCavityRunning(){
        doubleManualModeAppearanceTest?.isStatusScreenBothCavityRunning()
    }
}