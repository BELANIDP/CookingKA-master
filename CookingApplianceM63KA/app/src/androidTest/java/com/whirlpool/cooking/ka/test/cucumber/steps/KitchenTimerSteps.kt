package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.appearance.KitchenTimerAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.appearance.MwoManualModeComboAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import core.utils.HMILogHelper
import core.utils.TimeUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith


/*
 * File : com.whirlpool.cooking.ka.test.cucumber.steps.KitchenTimerSteps
 * Author : DUNGAS
 * Created On : 7/19/24, 1:12 PM
 * Details :
 */
@Suppress("NAME_SHADOWING")
@RunWith(AndroidJUnit4::class)

class KitchenTimerSteps {
    private var kitchenTimerAppearanceTest : KitchenTimerAppearanceTest? = null
    private var hmiKeyUtils : HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        kitchenTimerAppearanceTest = KitchenTimerAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }
    @After
    fun tearDown() {
        kitchenTimerAppearanceTest = null
    }
    @And("I click on kitchen timer")
    fun clickOnTimer(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.clickOnTimer()
        LeakAssertions.assertNoLeaks()
    }
    @And("I see kitchen timer tumbler view")
    fun timerTumblerVisible(){
        kitchenTimerAppearanceTest?.timerTumblerVisible()
    }
    @And("I validate the hours tumbler view")
    fun timerHoursTumblerValidation(){
        kitchenTimerAppearanceTest?.timerHoursTumblerValidation()
    }
    @And("I validate the minutes tumbler view")
    fun timerMinutesTumblerValidation(){
        kitchenTimerAppearanceTest?.timerMinutesTumblerValidation()
    }
    @And("I validate the seconds tumbler view")
    fun timerSecondsTumblerValidation(){
        kitchenTimerAppearanceTest?.timerSecondsTumblerValidation()
    }

    @And("I set KT to {string} and {string}")
    fun setKtToMinAndSec(min:String,sec:String){
        kitchenTimerAppearanceTest?.setKtToMinAndSec(min.toInt(),sec.toInt())
    }
    @And("I set {string} on numpad")
    fun setKtToMinAndSec(min:String){
        val ktSec = TestingUtils.convertTimeToHoursAndMinutes(
            min.toInt()
                .toLong() * 60)
        kitchenTimerAppearanceTest?.setKtOnNumpad(ktSec)
    }
    @And("I click on Start")
    fun performClickOnStart(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.performClickOnStart()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on pause button")
    fun performClickOnPause(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.performClickOnPause()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on pause button on second timer")
    fun performClickOnPauseOnSecondTimer(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.performClickOnPauseOnSecondTimer()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on 1 min button")
    fun performClickOnOneMin(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.performClickOnOneMin()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on 1 min button on second timer")
    fun performClickOnOneMinOnSecondTimer(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.performClickOnOneMinOnSecondTimer()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click cancel timer button")
    fun performClickOnCancelTimer(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.performClickOnCancelTimer()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click cancel timer button on second timer")
    fun performClickOnCancelTimerOnSecondTimer(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.performClickOnCancelTimerOnSecondTimer()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on Start on numpad")
    fun performClickOnStartNumpad(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.performClickOnStartNumpad()
        LeakAssertions.assertNoLeaks()
    }
    @And("I see KT widget")
    fun KtWidgetVisible(){
        kitchenTimerAppearanceTest?.KtWidgetVisible()
    }
    @And("I see cancel timer popup")
    fun cancelTimerPopupVisible(){
        kitchenTimerAppearanceTest?.cancelTimerPopupVisible()
    }
    @And("I validate cancel timer popup")
    fun cancelTimerPopupValidation(){
        kitchenTimerAppearanceTest?.cancelTimerPopupValidation()
    }
    @And("I see the clock screen with running KT")
    fun runningKtOnClockVisible(){
        kitchenTimerAppearanceTest?.runningKtOnClockVisible()
    }
    @And("I click on add timer button on KT widget")
    fun addKtButtonClickValidation(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.addTimerButtonClick()
        LeakAssertions.assertNoLeaks()
    }
    @And("I see KT tumbler")
    fun KtTumblerVisible(){
        kitchenTimerAppearanceTest?.KtTumblerVisible()
    }
    @And("I wait for {string} till timer completes")
    fun waitTillTimerCompletes(waitTime:String){
        val waitTime = waitTime.toInt()*1000
        UiTestingUtils.sleep(waitTime.toLong())
    }
    @And("I see the timer is completed popup")
    fun completedTimerPopupVisible(){
        kitchenTimerAppearanceTest?.cancelTimerPopupVisible()
    }
    @And("I validate timer is completed popup")
    fun completedTimerPopupValidation(){
        kitchenTimerAppearanceTest?.completedTimerPopupVisible()
    }
    @And("I click on repeat button")
    fun repeatButtonClick(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.repeatButtonClick()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on dismiss button")
    fun dismissButtonClick(){
        LeakAssertions.assertNoLeaks()
        kitchenTimerAppearanceTest?.dismissButtonClick()
        LeakAssertions.assertNoLeaks()
    }
}