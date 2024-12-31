package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.ClockFragmentAppearanceTest
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uitesting.UiTestingUtils
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClockSteps {

    private var clockFragmentAppearanceTest: ClockFragmentAppearanceTest? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        clockFragmentAppearanceTest = ClockFragmentAppearanceTest()
    }

    @After
    fun tearDown() {
        clockFragmentAppearanceTest = null
    }

    @Then("I navigate to clock screen from splash screen")
    fun checkClockViewVisibility() {
            UiTestingUtils.sleep(4000)
            clockFragmentAppearanceTest?.checkAllViewsVisibility()
    }

    @Then("I checked the clock text view size")
    fun checkClockTextViewSize() {
            clockFragmentAppearanceTest?.tvClockTimeViewSizeValidation()
    }

    @Then("I checked the clock text view size not matched")
    fun checkClockTextViewSizeNotMatched() {
            clockFragmentAppearanceTest?.tvClockTimeViewSizeValidationNotMatched()
    }

    @Then("I checked the clock text view color")
    fun checkClockTextViewColor() {
        clockFragmentAppearanceTest?.tvClockTimeViewColorValidation()
    }

    @Then("I checked the clock text view color not matched")
    fun checkClockTextViewColorNotMatched() {
            clockFragmentAppearanceTest?.tvClockTimeViewColorValidationNotMatched()
    }

    @Then("I checked the clock text view alignment")
    fun checkClockTextViewAlignment() {
        clockFragmentAppearanceTest?.clockTextViewAlignmentValidation(true)
    }

    @Then("I checked the clock text view alignment not matched")
    fun checkClockTextViewAlignmentNotMatched() {
        clockFragmentAppearanceTest?.clockTextViewAlignmentValidation(false)
    }

    @Then("I checked the clock text view is visible by default")
    fun checkClockTextViewIsVisibleByDefault() {
        clockFragmentAppearanceTest?.tvClockTimeViewVisibilityValidation()
    }

    @Then("I checked the day text view size")
    fun checkDayTextViewSize() {
        clockFragmentAppearanceTest?.tvDayTextViewSizeValidation()
    }

    @Then("I checked the day text view size not matched")
    fun checkDayTextViewSizeNotMatched() {
        clockFragmentAppearanceTest?.tvDayTextViewSizeValidationNotMatched()
    }

    @Then("I checked the day text view color")
    fun checkDayTextViewColor() {
        clockFragmentAppearanceTest?.tvDayTextViewColorValidation()
    }

    @Then("I checked the day text view color not matched")
    fun checkDayTextViewColorNotMatched() {
        clockFragmentAppearanceTest?.tvDayTextViewColorValidationNotMatched()
    }

    @Then("I checked the day text view alignment")
    fun checkDayTextViewAlignment() {
        clockFragmentAppearanceTest?.tvDayTextAlignmentValidation(true)
    }

    @Then("I checked the day text view alignment not matched")
    fun checkDayTextViewAlignmentNotMatched() {
        clockFragmentAppearanceTest?.tvDayTextAlignmentValidation(false)
    }

    @Then("I checked the day text view is visible by default")
    fun checkDayTextViewIsVisibleByDefault() {
        clockFragmentAppearanceTest?.tvDayTextViewVisibilityValidation()
    }

    @Then("I checked the kitchen timer running text view size")
    fun checkKitchenTimerRunningTextViewSize() {
        clockFragmentAppearanceTest?.tvKitchenTimerRunningTextViewSizeValidation()
    }

    @Then("I checked the kitchen timer running text view size not matched")
    fun checkKitchenTimerRunningTextViewSizeNotMatched() {
        clockFragmentAppearanceTest?.tvKitchenTimerRunningTextViewSizeValidationNotMatched()
    }

    @Then("I checked the kitchen timer running text view color")
    fun checkKitchenTimerRunningTextViewColor() {
        clockFragmentAppearanceTest?.tvKitchenTimerRunningTextViewColorValidation()
    }

    @Then("I checked the kitchen timer running text view color not matched")
    fun checkKitchenTimerRunningTextViewColorNotMatched() {
        clockFragmentAppearanceTest?.tvKitchenTimerRunningTextViewColorValidationNotMatched()
    }

    @Then("I checked the kitchen timer running text view alignment")
    fun checkKitchenTimerRunningTextViewAlignment() {
        clockFragmentAppearanceTest?.tvKitchenTimerRunningTextAlignmentValidation(true)
    }

    @Then("I checked the kitchen timer running text view alignment not matched")
    fun checkKitchenTimerRunningTextViewAlignmentNotMatched() {
        clockFragmentAppearanceTest?.tvKitchenTimerRunningTextAlignmentValidation(false)
    }

    @Then("I checked the kitchen timer running text view is not visible by default")
    fun checkKitchenTimerRunningTextViewIsVisibleByDefault() {
        clockFragmentAppearanceTest?.tvKitchenTimerRunningTextViewVisibilityValidation()
    }

    @Then("I checked the sabbath mode text view size")
    fun checkSabbathModeTextViewSize() {
        clockFragmentAppearanceTest?.tvSabbathModeTextViewSizeValidation()
    }

    @Then("I checked the sabbath mode text view size not matched")
    fun checkSabbathModeTextViewSizeNotMatched() {
        clockFragmentAppearanceTest?.tvSabbathModeTextViewSizeValidationNotMatched()
    }

    @Then("I checked the sabbath mode text view color")
    fun checkSabbathModeTextViewColor() {
        clockFragmentAppearanceTest?.tvSabbathModeTextViewColorValidation()
    }

    @Then("I checked the sabbath mode text view color not matched")
    fun checkSabbathModeTextViewColorNotMatched() {
        clockFragmentAppearanceTest?.tvSabbathModeTextViewColorValidationNotMatched()
    }

    @Then("I checked the sabbath mode text view alignment")
    fun checkSabbathModeTextViewAlignment() {
        clockFragmentAppearanceTest?.tvSabbathModeTextAlignmentValidation(true)
    }

    @Then("I checked the sabbath mode text view alignment not matched")
    fun checkSabbathModeTextViewAlignmentNotMatched() {
        clockFragmentAppearanceTest?.tvSabbathModeTextAlignmentValidation(false)
    }

    @Then("I checked the sabbath mode text view is not visible by default")
    fun checkSabbathModeTextViewIsVisibleByDefault() {
        clockFragmentAppearanceTest?.tvSabbathModeTextViewVisibilityValidation()
    }

    @Then("I checked the description text view size for clock")
    fun checkDescriptionTextViewSize() {
        clockFragmentAppearanceTest?.tvDescriptionTextViewSizeValidation()
    }

    @Then("I checked the description text view size not matched for clock")
    fun checkDescriptionTextViewSizeNotMatched() {
        clockFragmentAppearanceTest?.tvDescriptionTextViewSizeValidationNotMatched()
    }

    @Then("I checked the description text view color for clock")
    fun checkDescriptionTextViewColor() {
        clockFragmentAppearanceTest?.tvDescriptionTextViewColorValidation()
    }

    @Then("I checked the description text view color not matched for clock")
    fun checkDescriptionTextViewColorNotMatched() {
        clockFragmentAppearanceTest?.tvDescriptionTextViewColorValidationNotMatched()
    }

    @Then("I checked the description text view alignment for clock")
    fun checkDescriptionTextViewAlignment() {
        clockFragmentAppearanceTest?.tvDescriptionTextAlignmentValidation(true)
    }

    @Then("I checked the description text view alignment not matched for clock")
    fun checkDescriptionTextViewAlignmentNotMatched() {
        clockFragmentAppearanceTest?.tvDescriptionTextAlignmentValidation(false)
    }

    @Then("I checked the description text view is not visible by default for clock")
    fun checkDescriptionTextViewIsVisibleByDefault() {
        clockFragmentAppearanceTest?.tvDescriptionTextViewVisibilityValidation()
    }

    @Then("I checked the notification text view size")
    fun checkNotificationTextViewSize() {
        clockFragmentAppearanceTest?.tvNotificationTextViewSizeValidation()
    }

    @Then("I checked the notification text view size not matched")
    fun checkNotificationTextViewSizeNotMatched() {
        clockFragmentAppearanceTest?.tvNotificationTextViewSizeValidationNotMatched()
    }

    @Then("I checked the notification text view color")
    fun checkNotificationTextViewColor() {
        clockFragmentAppearanceTest?.tvNotificationTextViewColorValidation()
    }

    @Then("I checked the notification text view color not matched")
    fun checkNotificationTextViewColorNotMatched() {
        clockFragmentAppearanceTest?.tvNotificationTextViewColorValidationNotMatched()
    }

    @Then("I checked the notification text view alignment")
    fun checkNotificationTextViewAlignment() {
        clockFragmentAppearanceTest?.tvNotificationTextAlignmentValidation(true)
    }

    @Then("I checked the notification text view alignment not matched")
    fun checkNotificationTextViewAlignmentNotMatched() {
        clockFragmentAppearanceTest?.tvNotificationTextAlignmentValidation(false)
    }

    @Then("I checked the notification text view is not visible by default")
    fun checkNotificationTextViewIsVisibleByDefault() {
        clockFragmentAppearanceTest?.tvNotificationTextViewVisibilityValidation()
    }

    @Then("I checked the tips and tricks text view size")
    fun checkTipsAndTricksTextViewSize() {
        clockFragmentAppearanceTest?.tvTipsAndTricksTextViewSizeValidation()
    }

    @Then("I checked the tips and tricks text view size not matched")
    fun checkTipsAndTricksTextViewSizeNotMatched() {
        clockFragmentAppearanceTest?.tvTipsAndTricksTextViewSizeValidationNotMatched()
    }

    @Then("I checked the tips and tricks text view color")
    fun checkTipsAndTricksTextViewColor() {
        clockFragmentAppearanceTest?.tvTipsAndTricksTextViewColorValidation()
    }

    @Then("I checked the tips and tricks text view color not matched")
    fun checkTipsAndTricksTextViewColorNotMatched() {
        clockFragmentAppearanceTest?.tvTipsAndTricksTextViewColorValidationNotMatched()
    }

    @Then("I checked the tips and tricks text view alignment")
    fun checkTipsAndTricksTextViewAlignment() {
        clockFragmentAppearanceTest?.tvTipsAndTricksTextAlignmentValidation(true)
    }

    @Then("I checked the tips and tricks text view alignment not matched")
    fun checkTipsAndTricksTextViewAlignmentNotMatched() {
        clockFragmentAppearanceTest?.tvTipsAndTricksTextAlignmentValidation(false)
    }

    @Then("I checked the tips and tricks text view is not visible by default")
    fun checkTipsAndTricksTextViewIsVisibleByDefault() {
        clockFragmentAppearanceTest?.tvTipsAndTricksTextViewVisibilityValidation()
    }

    @Then("I checked clock text view line height is matched")
    fun checkClockTextViewLineHeight() {
        clockFragmentAppearanceTest?.checkClockTextViewLineHeight()
    }

    @Then("I checked clock text view line height is not matched")
    fun checkClockTextViewLineHeightNotMatched() {
        clockFragmentAppearanceTest?.checkClockTextViewLineHeightNotMatched()
    }

    @Then("I checked day text view line height is matched")
    fun checkDayTextViewLineHeight() {
        clockFragmentAppearanceTest?.checkDayTextViewLineHeight()
    }

    @Then("I checked day text view line height is not matched")
    fun checkDayTextViewLineHeightNotMatched() {
        clockFragmentAppearanceTest?.checkDayTextViewLineHeightNotMatched()
    }

    @Then("I checked kitchen timer running text view line height is matched")
    fun checkKitchenTimerRunningTextViewLineHeight() {
        clockFragmentAppearanceTest?.checkKitchenTimerRunningTextViewLineHeight()
    }

    @Then("I checked kitchen timer running text view line height is not matched")
    fun checkKitchenTimerRunningTextViewLineHeightNotMatched() {
        clockFragmentAppearanceTest?.checkKitchenTimerRunningTextViewLineHeightNotMatched()
    }

    @Then("I checked sabbath mode text view line height is matched")
    fun checkSabbathModeTextViewLineHeight() {
        clockFragmentAppearanceTest?.checkSabbathModeTextViewLineHeight()
    }

    @Then("I checked sabbath mode text view line height is not matched")
    fun checkSabbathModeTextViewLineHeightNotMatched() {
        clockFragmentAppearanceTest?.checkSabbathModeTextViewLineHeightNotMatched()
    }

    @Then("I checked description text view line height is matched")
    fun checkDescriptionTextViewLineHeight() {
        clockFragmentAppearanceTest?.checkDescriptionTextViewLineHeight()
    }

    @Then("I checked description text view line height is not matched")
    fun checkDescriptionTextViewLineHeightNotMatched() {
        clockFragmentAppearanceTest?.checkDescriptionTextViewLineHeightNotMatched()
    }

    @Then("I checked notification text view line height is matched")
    fun checkNotificationTextViewLineHeight() {
        clockFragmentAppearanceTest?.checkNotificationTextViewLineHeight()
    }

    @Then("I checked notification text view line height is not matched")
    fun checkNotificationTextViewLineHeightNotMatched() {
        clockFragmentAppearanceTest?.checkNotificationTextViewLineHeightNotMatched()
    }

    @Then("I checked tips and tricks text view line height is matched")
    fun checkTipsAndTricksTextViewLineHeight() {
        clockFragmentAppearanceTest?.checkTipsAndTricksTextViewLineHeight()
    }

    @Then("I checked tips and tricks text view line height is not matched")
    fun checkTipsAndTricksTextViewLineHeightNotMatched() {
        clockFragmentAppearanceTest?.checkTipsAndTricksTextViewLineHeightNotMatched()
    }

    @Then("I checked clock text view line spacing extra is matched")
    fun checkClockTextViewLineSpacingExtra() {
        clockFragmentAppearanceTest?.checkClockTextViewLineSpacingExtra()
    }

    @Then("I checked clock text view line spacing extra is not matched")
    fun checkClockTextViewLineSpacingExtraNotMatched() {
        clockFragmentAppearanceTest?.checkClockTextViewLineSpacingExtraNotMatched()
    }

    @Then("I checked day text view line spacing extra is matched")
    fun checkDayTextViewLineSpacingExtra() {
        clockFragmentAppearanceTest?.checkDayTextViewLineSpacingExtra()
    }

    @Then("I checked day text view line spacing extra is not matched")
    fun checkDayTextViewLineSpacingExtraNotMatched() {
        clockFragmentAppearanceTest?.checkDayTextViewLineSpacingExtraNotMatched()
    }

    @Then("I checked kitchen timer running text view line spacing extra is matched")
    fun checkKitchenTimerRunningTextViewLineSpacingExtra() {
        clockFragmentAppearanceTest?.checkKitchenTimerRunningTextViewLineSpacingExtra()
    }

    @Then("I checked kitchen timer running text view line spacing extra is not matched")
    fun checkKitchenTimerRunningTextViewLineSpacingExtraNotMatched() {
        clockFragmentAppearanceTest?.checkKitchenTimerRunningTextViewLineSpacingExtraNotMatched()
    }

    @Then("I checked sabbath mode text view line spacing extra is matched")
    fun checkSabbathModeTextViewLineSpacingExtra() {
        clockFragmentAppearanceTest?.checkSabbathModeTextViewLineSpacingExtra()
    }

    @Then("I checked sabbath mode text view line spacing extra is not matched")
    fun checkSabbathModeTextViewLineSpacingExtraNotMatched() {
        clockFragmentAppearanceTest?.checkSabbathModeTextViewLineSpacingExtraNotMatched()
    }

    @Then("I checked description text view line spacing extra is matched")
    fun checkDescriptionTextViewLineSpacingExtra() {
        clockFragmentAppearanceTest?.checkDescriptionTextViewLineSpacingExtra()
    }

    @Then("I checked description text view line spacing extra is not matched")
    fun checkDescriptionTextViewLineSpacingExtraNotMatched() {
        clockFragmentAppearanceTest?.checkDescriptionTextViewLineSpacingExtraNotMatched()
    }

    @Then("I checked notification text view line spacing extra is matched")
    fun checkNotificationTextViewLineSpacingExtra() {
        clockFragmentAppearanceTest?.checkNotificationTextViewLineSpacingExtra()
    }

    @Then("I checked notification text view line spacing extra is not matched")
    fun checkNotificationTextViewLineSpacingExtraNotMatched() {
        clockFragmentAppearanceTest?.checkNotificationTextViewLineSpacingExtraNotMatched()
    }

    @Then("I checked tips and tricks text view line spacing extra is matched")
    fun checkTipsAndTricksTextViewLineSpacingExtra() {
        clockFragmentAppearanceTest?.checkTipsAndTricksTextViewLineSpacingExtra()
    }

    @Then("I checked tips and tricks text view line spacing extra is not matched")
    fun checkTipsAndTricksTextViewLineSpacingExtraNotMatched() {
        clockFragmentAppearanceTest?.checkTipsAndTricksTextViewLineSpacingExtraNotMatched()
    }

    @Then("I checked kitchen timer icon height and width are matched")
    fun checkedKitchenTimerIconHeightAndWidth() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.icon_kitchen_timer, height = 102f,
            width = 102f,
            isMatchCase = true
        )
    }

    @Then("I checked kitchen timer icon height and width are not matched")
    fun checkedKitchenTimerIconHeightAndWidthNotMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.icon_kitchen_timer,
            height = 100f,
            width = 100f,
            isMatchCase = false
        )
    }

    @Then("I checked sabbath mode icon height and width are matched")
    fun checkedSabbathModeIconHeightAndWidth() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.icon_sabbath_mode, height = 102f,
            width = 102f,
            isMatchCase = true
        )
    }

    @Then("I checked sabbath mode icon height and width are not matched")
    fun checkedSabbathModeIconHeightAndWidthNotMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.icon_sabbath_mode,
            height = 100f,
            width = 100f,
            isMatchCase = false
        )
    }

    @Then("I checked tips and tricks icon height and width are matched")
    fun checkedTipsAndTricksIconHeightAndWidth() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.icon_sabbath_mode, height = 40f,
            width = 40f,
            isMatchCase = true
        )
    }

    @Then("I checked tips and tricks icon height and width are not matched")
    fun checkedTipsAndTricksIconHeightAndWidthNotMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.icon_sabbath_mode,
            height = 100f,
            width = 100f,
            isMatchCase = false
        )
    }

    @Then("I checked clock text view height is matched")
    fun checkedClockTextViewHeightIsMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeight(
            viewId = R.id.text_view_clock_digital_clock_time,
            height = 224f,
            isMatchCase = true
        )
    }

    @Then("I checked clock text view height is not matched")
    fun checkedClockTextViewHeightIsNotMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeight(
            viewId = R.id.text_view_clock_digital_clock_time,
            height = 100f,
            isMatchCase = false
        )
    }

    @Then("I checked day text view height and width are matched")
    fun checkedDayTextViewHeightAndWidthAreMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.text_view_clock_digital_clock_day,
            height = 44f,
            width = 790f,
            isMatchCase = true
        )
    }

    @Then("I checked day text view height and width are not matched")
    fun checkedDayTextViewHeightAndWidthIsNotMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.text_view_clock_digital_clock_day,
            height = 100f,
            width = 100f,
            isMatchCase = false
        )
    }

    @Then("I checked kitchen running time text view height and width are matched")
    fun checkedKitchenRunningTimeTextViewHeightAndWidthAreMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.text_view_clock_digital_clock_day,
            height = 44f,
            width = 790f,
            isMatchCase = true
        )
    }

    @Then("I checked kitchen running time text view height and width are not matched")
    fun checkedKitchenRunningTimeTextViewHeightAndWidthAreNotMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.text_view_clock_digital_clock_day,
            height = 100f,
            width = 100f,
            isMatchCase = false
        )
    }

    @Then("I checked description text view height and width are matched")
    fun checkedDescriptionTextViewHeightAndWidthAreMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.text_view_description,
            height = 88f,
            width = 790f,
            isMatchCase = true
        )
    }

    @Then("I checked description text view height and width are not matched")
    fun checkedDescriptionTextViewHeightAndWidthAreNotMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeightWidth(
            viewId = R.id.text_view_description,
            height = 100f,
            width = 100f,
            isMatchCase = false
        )
    }

    @Then("I checked sabbath mode text view height is matched")
    fun checkedSabbathModeTextViewHeightIsMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeight(
            viewId = R.id.text_view_sabbath_mode,
            height = 224f,
            isMatchCase = true
        )
    }

    @Then("I checked sabbath mode text view height is not matched")
    fun checkedSabbathModeTextViewHeightIsNotMatched() {
        clockFragmentAppearanceTest?.setVisibilityAndCheckHeight(
            viewId = R.id.text_view_sabbath_mode,
            height = 100f,
            isMatchCase = false
        )
    }
}