package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.RightKnobAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import core.utils.KnobDirection
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RightKnobSteps {

    private var rightKnobAppearanceTest: RightKnobAppearanceTest? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        rightKnobAppearanceTest = RightKnobAppearanceTest()
    }

    @After
    fun tearDown() {
        rightKnobAppearanceTest = null
    }

    @And("I navigate to clock screen for verify knob events")
    fun iNavigateToClockScreen() {
        rightKnobAppearanceTest?.checkClockVisibility()
        UiTestingUtils.sleep(5000)
    }

    @Then("I rotate right knob clockwise")
    fun iRotateRightKnobClockWise() {
       rightKnobAppearanceTest?.rotateKnobEvent(
           activity = CookingKACucumberTests.mainActivity,
           knobID = HMIKeyUtils.KNOB_ID_RIGHT,
           knobDirectionEvent = KnobDirection.CLOCK_WISE_DIRECTION
       )
    }
    @Then("I rotate right knob counter clockwise")
    fun iRotateRightKnobCounterClockWise() {
        rightKnobAppearanceTest?.rotateKnobEvent(
            activity = CookingKACucumberTests.mainActivity,
            knobID = HMIKeyUtils.KNOB_ID_RIGHT,
            knobDirectionEvent = KnobDirection.COUNTER_CLOCK_WISE_DIRECTION
        )
    }

    @And("I navigate to cavity selection screen")
    fun iNavigateToCavitySelectionScreen() {
        UiTestingUtils.sleep(2000)
        rightKnobAppearanceTest?.checkCavityScreenVisibility()
    }
    @And("I expect upper cavity should hover")
    fun iExpectUpperCavityShouldHover() {
        rightKnobAppearanceTest?.checkUpperCavityHoverVisibility()
    }
    @And("I expect lower cavity should hover")
    fun iExpectLowerCavityShouldHover() {
        rightKnobAppearanceTest?.checkLowerCavityHoverVisibility()
    }

    @And("I click right knob")
    fun iClickRightKnob() {
        rightKnobAppearanceTest?.clickRightKnobEvent(
            activity = CookingKACucumberTests.mainActivity,
            buttonIndex = HMIKeyUtils.HMI_KNOB_BUTTON_INDEX_RIGHT
        )
    }
    @And("I navigate to duration tumbler screen")
    fun iNavigateToDurationTumblerScreen() {
        rightKnobAppearanceTest?.checkDurationTumblerScreenVisibility()
    }
    @And("I navigate to instruction screen")
    fun iNavigateToInstructionTumblerScreen() {
        rightKnobAppearanceTest?.checkInstructionScreenVisibility()
    }

    @And("I navigate to temperature screen")
    fun iNavigateToTemperatureTumblerScreen() {
        rightKnobAppearanceTest?.checkTemperatureScreenVisibility()
    }

    @And("I navigate to manual mode status screen")
    fun iNavigateToManualModeStatusScreen() {
        rightKnobAppearanceTest?.checkManualModeStatusScreenVisibility()
    }
    @And("I navigate to manual mode more options screen")
    fun iNavigateToManualModeMoreOptionsScreen() {
        rightKnobAppearanceTest?.checkManualModeMoreOptionsScreenVisibility()
    }
    @And("I navigate to manual mode vertical cook time screen")
    fun iNavigateToManualModeCookTimeScreen() {
        rightKnobAppearanceTest?.checkManualModeCookTimeScreenVisibility()
    }
    @Then("I click turn off cycle")
    fun iClickOnTurnOffCycle() {
        rightKnobAppearanceTest?.clickOnTurnOffCycle()
    }

}