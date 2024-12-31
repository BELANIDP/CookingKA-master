package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.LeftKnobAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import core.utils.KnobDirection
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LeftKnobSteps {

    private var leftKnobAppearanceTest: LeftKnobAppearanceTest? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        leftKnobAppearanceTest = LeftKnobAppearanceTest()
    }

    @After
    fun tearDown() {
        leftKnobAppearanceTest = null
    }

    @And("I navigate to clock screen for verify left knob events")
    fun iNavigateToClockScreen() {
        leftKnobAppearanceTest?.checkClockVisibility()
    }

    @Then("I rotate left knob clockwise")
    fun iRotateLeftKnobClockWise() {
        LeakAssertions.assertNoLeaks()
        leftKnobAppearanceTest?.rotateKnobEvent(
           activity = CookingKACucumberTests.mainActivity,
           knobID = HMIKeyUtils.KNOB_ID_LEFT,
           knobDirectionEvent = KnobDirection.CLOCK_WISE_DIRECTION
       )
        LeakAssertions.assertNoLeaks()
    }
    @Then("I rotate Left knob counter clockwise")
    fun iRotateLeftKnobCounterClockWise() {
        LeakAssertions.assertNoLeaks()
        leftKnobAppearanceTest?.rotateKnobEvent(
            activity = CookingKACucumberTests.mainActivity,
            knobID = HMIKeyUtils.KNOB_ID_LEFT,
            knobDirectionEvent = KnobDirection.COUNTER_CLOCK_WISE_DIRECTION
        )
        LeakAssertions.assertNoLeaks()
    }

    @And("I click left knob")
    fun iClickLeftKnob() {
        LeakAssertions.assertNoLeaks()
        leftKnobAppearanceTest?.clickLeftKnobEvent(
            activity = CookingKACucumberTests.mainActivity,
            buttonIndex = HMIKeyUtils.HMI_KNOB_BUTTON_INDEX_LEFT
        )
        LeakAssertions.assertNoLeaks()
    }

}