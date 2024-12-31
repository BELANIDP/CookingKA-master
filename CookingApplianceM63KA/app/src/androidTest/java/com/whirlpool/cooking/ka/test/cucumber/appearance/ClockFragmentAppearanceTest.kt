package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.isNotDisplayed
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withLineHeight
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withLineSpacingExtra
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TextViewPropertiesMatcher
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import junit.framework.TestCase
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matcher

class ClockFragmentAppearanceTest {

    fun checkAllViewsVisibility() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.text_view_clock_digital_clock_time)
    }

    fun tvClockTimeViewSizeValidation() {
        val desiredTextSizeSp = 208f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_clock_digital_clock_time)), sizeInPixels.toFloat()
        )
    }

    fun tvClockTimeViewSizeValidationNotMatched() {
        val desiredTextSizeSp = 1000f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_clock_digital_clock_time)),
            sizeInPixels.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_SIZE_NOT_MATCHED
        )
    }

    fun tvClockTimeViewColorValidation() {
        val customColorValue = Color.parseColor("#ffffff")
        onView(withId(R.id.text_view_clock_digital_clock_time)).check { view, _ ->
            TestCase.assertEquals(customColorValue, (view as TextView).currentTextColor)
        }
    }

    fun tvClockTimeViewColorValidationNotMatched() {
        val customColorValue = Color.parseColor("#FF0000")
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_clock_digital_clock_time)),
            customColorValue.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_COLOR_NOT_MATCHED
        )
    }

    fun clockTextViewAlignmentValidation(isMatched: Boolean) {
        var textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT
        var textStyle = TextView.TEXT_ALIGNMENT_GRAVITY.toFloat()
        if (!isMatched) {
            textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT_NOT_MATCHED
            textStyle = TextView.TEXT_ALIGNMENT_TEXT_END.toFloat()
        }
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_clock_digital_clock_time)),
            textStyle,
            textProperties
        )
    }

    fun tvClockTimeViewVisibilityValidation() {
        onView(withId(R.id.text_view_clock_digital_clock_time)).check(matches(isDisplayed()))
    }

    fun tvDayTextViewSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_clock_digital_clock_day)), sizeInPixels.toFloat()
        )
    }

    fun tvDayTextViewSizeValidationNotMatched() {
        val desiredTextSizeSp = 100f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_clock_digital_clock_day)),
            sizeInPixels.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_SIZE_NOT_MATCHED
        )
    }

    fun tvDayTextViewColorValidation() {
        val customColorValue = Color.parseColor("#ffffff")
        onView(withId(R.id.text_view_clock_digital_clock_day)).check { view, _ ->
            TestCase.assertEquals(customColorValue, (view as TextView).currentTextColor)
        }
    }

    fun tvDayTextViewColorValidationNotMatched() {
        val customColorValue = Color.parseColor("#FF0000")
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_clock_digital_clock_day)),
            customColorValue.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_COLOR_NOT_MATCHED
        )
    }

    fun tvDayTextAlignmentValidation(isMatched: Boolean) {
        var textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT
        var textStyle = TextView.TEXT_ALIGNMENT_GRAVITY.toFloat()
        if (!isMatched) {
            textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT_NOT_MATCHED
            textStyle = TextView.TEXT_ALIGNMENT_TEXT_END.toFloat()
        }
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_clock_digital_clock_day)),
            textStyle,
            textProperties
        )
    }

    fun tvDayTextViewVisibilityValidation() {
        onView(withId(R.id.text_view_clock_digital_clock_day)).check(matches(isDisplayed()))
    }

    fun tvKitchenTimerRunningTextViewSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_kitchen_timer_running_text)), sizeInPixels.toFloat()
        )
    }

    fun tvKitchenTimerRunningTextViewSizeValidationNotMatched() {
        val desiredTextSizeSp = 100f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_kitchen_timer_running_text)),
            sizeInPixels.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_SIZE_NOT_MATCHED
        )
    }

    fun tvKitchenTimerRunningTextViewColorValidation() {
        val customColorValue = Color.parseColor("#ffffff")
        onView(withId(R.id.text_view_kitchen_timer_running_text)).check { view, _ ->
            TestCase.assertEquals(customColorValue, (view as TextView).currentTextColor)
        }
    }

    fun tvKitchenTimerRunningTextViewColorValidationNotMatched() {
        val customColorValue = Color.parseColor("#FF0000")
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_kitchen_timer_running_text)),
            customColorValue.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_COLOR_NOT_MATCHED
        )
    }

    fun tvKitchenTimerRunningTextAlignmentValidation(isMatched: Boolean) {
        var textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT
        var textStyle = TextView.TEXT_ALIGNMENT_GRAVITY.toFloat()
        if (!isMatched) {
            textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT_NOT_MATCHED
            textStyle = TextView.TEXT_ALIGNMENT_TEXT_END.toFloat()
        }
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_kitchen_timer_running_text)),
            textStyle,
            textProperties
        )
    }

    fun tvKitchenTimerRunningTextViewVisibilityValidation() {
        onView(withId(R.id.text_view_kitchen_timer_running_text)).check(matches(isNotDisplayed()))
    }

    fun tvSabbathModeTextViewSizeValidation() {
        val desiredTextSizeSp = 80f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_sabbath_mode)), sizeInPixels.toFloat()
        )
    }

    fun tvSabbathModeTextViewSizeValidationNotMatched() {
        val desiredTextSizeSp = 100f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_sabbath_mode)),
            sizeInPixels.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_SIZE_NOT_MATCHED
        )
    }

    fun tvSabbathModeTextViewColorValidation() {
        val customColorValue = Color.parseColor("#ffffff")
        onView(withId(R.id.text_view_sabbath_mode)).check { view, _ ->
            TestCase.assertEquals(customColorValue, (view as TextView).currentTextColor)
        }
    }

    fun tvSabbathModeTextViewColorValidationNotMatched() {
        val customColorValue = Color.parseColor("#FF0000")
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_sabbath_mode)),
            customColorValue.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_COLOR_NOT_MATCHED
        )
    }

    fun tvSabbathModeTextAlignmentValidation(isMatched: Boolean) {
        var textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT
        var textStyle = TextView.TEXT_ALIGNMENT_GRAVITY.toFloat()
        if (!isMatched) {
            textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT_NOT_MATCHED
            textStyle = TextView.TEXT_ALIGNMENT_TEXT_END.toFloat()
        }
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_sabbath_mode)),
            textStyle,
            textProperties
        )
    }

    fun tvSabbathModeTextViewVisibilityValidation() {
        onView(withId(R.id.text_view_sabbath_mode)).check(matches(isNotDisplayed()))
    }

    fun tvDescriptionTextViewSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )
    }

    fun tvDescriptionTextViewSizeValidationNotMatched() {
        val desiredTextSizeSp = 100f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_description)),
            sizeInPixels.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_SIZE_NOT_MATCHED
        )
    }

    fun tvDescriptionTextViewColorValidation() {
        val customColorValue = Color.parseColor("#ffffff")
        onView(withId(R.id.text_view_description)).check { view, _ ->
            TestCase.assertEquals(customColorValue, (view as TextView).currentTextColor)
        }
    }

    fun tvDescriptionTextViewColorValidationNotMatched() {
        val customColorValue = Color.parseColor("#FF0000")
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_description)),
            customColorValue.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_COLOR_NOT_MATCHED
        )
    }

    fun tvDescriptionTextAlignmentValidation(isMatched: Boolean) {
        var textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT
        var textStyle = TextView.TEXT_ALIGNMENT_GRAVITY.toFloat()
        if (!isMatched) {
            textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT_NOT_MATCHED
            textStyle = TextView.TEXT_ALIGNMENT_TEXT_END.toFloat()
        }
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_description)),
            textStyle,
            textProperties
        )
    }

    fun tvDescriptionTextViewVisibilityValidation() {
        onView(withId(R.id.text_view_description)).check(matches(isNotDisplayed()))
    }

    fun tvNotificationTextViewSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_notification)), sizeInPixels.toFloat()
        )
    }

    fun tvNotificationTextViewSizeValidationNotMatched() {
        val desiredTextSizeSp = 100f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_notification)),
            sizeInPixels.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_SIZE_NOT_MATCHED
        )
    }

    fun tvNotificationTextViewColorValidation() {
        val customColorValue = Color.parseColor("#ffffff")
        onView(withId(R.id.text_view_notification)).check { view, _ ->
            TestCase.assertEquals(customColorValue, (view as TextView).currentTextColor)
        }
    }

    fun tvNotificationTextViewColorValidationNotMatched() {
        val customColorValue = Color.parseColor("#FF0000")
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_notification)),
            customColorValue.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_COLOR_NOT_MATCHED
        )
    }

    fun tvNotificationTextAlignmentValidation(isMatched: Boolean) {
        var textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT
        var textStyle = TextView.TEXT_ALIGNMENT_GRAVITY.toFloat()
        if (!isMatched) {
            textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT_NOT_MATCHED
            textStyle = TextView.TEXT_ALIGNMENT_TEXT_END.toFloat()
        }
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_notification)),
            textStyle,
            textProperties
        )
    }

    fun tvNotificationTextViewVisibilityValidation() {
        onView(withId(R.id.text_view_notification)).check(matches(isNotDisplayed()))
    }

    fun tvTipsAndTricksTextViewSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_tips_and_tricks)), sizeInPixels.toFloat()
        )
    }

    fun tvTipsAndTricksTextViewSizeValidationNotMatched() {
        val desiredTextSizeSp = 100f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_tips_and_tricks)),
            sizeInPixels.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_SIZE_NOT_MATCHED
        )
    }

    fun tvTipsAndTricksTextViewColorValidation() {
        val customColorValue = Color.parseColor("#AAA5A1")
        onView(withId(R.id.text_view_tips_and_tricks)).check { view, _ ->
            TestCase.assertEquals(customColorValue, (view as TextView).currentTextColor)
        }
    }

    fun tvTipsAndTricksTextViewColorValidationNotMatched() {
        val customColorValue = Color.parseColor("#FF0000")
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_tips_and_tricks)),
            customColorValue.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_COLOR_NOT_MATCHED
        )
    }

    fun tvTipsAndTricksTextAlignmentValidation(isMatched: Boolean) {
        var textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT
        var textStyle = TextView.TEXT_ALIGNMENT_GRAVITY.toFloat()
        if (!isMatched) {
            textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT_NOT_MATCHED
            textStyle = TextView.TEXT_ALIGNMENT_TEXT_END.toFloat()
        }
        TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_tips_and_tricks)),
            textStyle,
            textProperties
        )
    }

    fun tvTipsAndTricksTextViewVisibilityValidation() {
        onView(withId(R.id.text_view_tips_and_tricks)).check(matches(isNotDisplayed()))
    }

    fun checkClockTextViewLineHeight() {
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 208f)
        onView(withId(R.id.text_view_clock_digital_clock_time))
            .check(matches(withLineHeight(desiredLineHeight)))
    }

    fun checkClockTextViewLineHeightNotMatched() {
        val undesiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_clock_digital_clock_time))
            .check(matches(not(withLineHeight(undesiredLineHeight))))
    }

    fun checkDayTextViewLineHeight() {
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 40f)
        onView(withId(R.id.text_view_clock_digital_clock_day))
            .check(matches(withLineHeight(desiredLineHeight)))
    }

    fun checkDayTextViewLineHeightNotMatched() {
        val undesiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_clock_digital_clock_day))
            .check(matches(not(withLineHeight(undesiredLineHeight))))
    }

    fun checkKitchenTimerRunningTextViewLineHeight() {
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 40f)
        onView(withId(R.id.text_view_kitchen_timer_running_text))
            .check(matches(withLineHeight(desiredLineHeight)))
    }

    fun checkKitchenTimerRunningTextViewLineHeightNotMatched() {
        val undesiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_kitchen_timer_running_text))
            .check(matches(not(withLineHeight(undesiredLineHeight))))
    }

    fun checkSabbathModeTextViewLineHeight() {
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 80f)
        onView(withId(R.id.text_view_sabbath_mode))
            .check(matches(withLineHeight(desiredLineHeight)))
    }

    fun checkSabbathModeTextViewLineHeightNotMatched() {
        val undesiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_sabbath_mode))
            .check(matches(not(withLineHeight(undesiredLineHeight))))
    }

    fun checkDescriptionTextViewLineHeight() {
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
        onView(withId(R.id.text_view_description))
            .check(matches(withLineHeight(desiredLineHeight)))
    }

    fun checkDescriptionTextViewLineHeightNotMatched() {
        val undesiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_description))
            .check(matches(not(withLineHeight(undesiredLineHeight))))
    }

    fun checkNotificationTextViewLineHeight() {
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
        onView(withId(R.id.text_view_notification))
            .check(matches(withLineHeight(desiredLineHeight)))
    }

    fun checkNotificationTextViewLineHeightNotMatched() {
        val undesiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_notification))
            .check(matches(not(withLineHeight(undesiredLineHeight))))
    }

    fun checkTipsAndTricksTextViewLineHeight() {
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
        onView(withId(R.id.text_view_tips_and_tricks))
            .check(matches(withLineHeight(desiredLineHeight)))
    }

    fun checkTipsAndTricksTextViewLineHeightNotMatched() {
        val undesiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_tips_and_tricks))
            .check(matches(not(withLineHeight(undesiredLineHeight))))
    }

    fun checkClockTextViewLineSpacingExtra() {
        val desiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, -36f)
        GenericTextViewTest.checkMatchesLineSpacing(
            onView(withId(R.id.text_view_clock_digital_clock_time)),
            desiredLineSpacingExtra.toFloat()
        )
    }

    fun checkClockTextViewLineSpacingExtraNotMatched() {
        val undesiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_clock_digital_clock_time))
            .check(matches(not(withLineSpacingExtra(undesiredLineSpacingExtra))))
    }

    fun checkDayTextViewLineSpacingExtra() {
        val desiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, -7f)
        GenericTextViewTest.checkMatchesLineSpacing(
            onView(withId(R.id.text_view_clock_digital_clock_day)),
            desiredLineSpacingExtra.toFloat()
        )
    }

    fun checkDayTextViewLineSpacingExtraNotMatched() {
        val undesiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_clock_digital_clock_day))
            .check(matches(not(withLineSpacingExtra(undesiredLineSpacingExtra))))
    }

    fun checkKitchenTimerRunningTextViewLineSpacingExtra() {
        val desiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, -7f)
        GenericTextViewTest.checkMatchesLineSpacing(
            onView(withId(R.id.text_view_kitchen_timer_running_text)),
            desiredLineSpacingExtra.toFloat()
        )
    }

    fun checkKitchenTimerRunningTextViewLineSpacingExtraNotMatched() {
        val undesiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_kitchen_timer_running_text))
            .check(matches(not(withLineSpacingExtra(undesiredLineSpacingExtra))))
    }

    fun checkSabbathModeTextViewLineSpacingExtra() {
        val desiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, -14f)
        GenericTextViewTest.checkMatchesLineSpacing(
            onView(withId(R.id.text_view_sabbath_mode)),
            desiredLineSpacingExtra.toFloat()
        )
    }

    fun checkSabbathModeTextViewLineSpacingExtraNotMatched() {
        val undesiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_sabbath_mode))
            .check(matches(not(withLineSpacingExtra(undesiredLineSpacingExtra))))
    }

    fun checkDescriptionTextViewLineSpacingExtra() {
        val desiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 1f)
        GenericTextViewTest.checkMatchesLineSpacing(
            onView(withId(R.id.text_view_description)),
            desiredLineSpacingExtra.toFloat()
        )
    }

    fun checkDescriptionTextViewLineSpacingExtraNotMatched() {
        val undesiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_description))
            .check(matches(not(withLineSpacingExtra(undesiredLineSpacingExtra))))
    }

    fun checkNotificationTextViewLineSpacingExtra() {
        val desiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 1f)
        GenericTextViewTest.checkMatchesLineSpacing(
            onView(withId(R.id.text_view_notification)),
            desiredLineSpacingExtra.toFloat()
        )
    }

    fun checkNotificationTextViewLineSpacingExtraNotMatched() {
        val undesiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_notification))
            .check(matches(not(withLineSpacingExtra(undesiredLineSpacingExtra))))
    }

    fun checkTipsAndTricksTextViewLineSpacingExtra() {
        val desiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 1f)
        GenericTextViewTest.checkMatchesLineSpacing(
            onView(withId(R.id.text_view_tips_and_tricks)),
            desiredLineSpacingExtra.toFloat()
        )
    }

    fun checkTipsAndTricksTextViewLineSpacingExtraNotMatched() {
        val undesiredLineSpacingExtra = TestingUtils.spToPx(CookingKACucumberTests.context, 100f)
        onView(withId(R.id.text_view_tips_and_tricks))
            .check(matches(not(withLineSpacingExtra(undesiredLineSpacingExtra))))
    }

    fun setVisibilityAndCheckHeightWidth(
        viewId: Int,
        height: Float,
        width: Float,
        isMatchCase: Boolean
    ) {
        val desiredHeight = TestingUtils.dpToPx(CookingKACucumberTests.context, height)
        val desiredWidth = TestingUtils.dpToPx(CookingKACucumberTests.context, width)

        val viewAction = object : ViewAction {
            override fun getDescription(): String {
                return "Set view visibility to VISIBLE"
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.visibility = View.VISIBLE
                if (isMatchCase) {
                    matches(TestingUtils.withViewHeightAndWidth(desiredHeight, desiredWidth))
                } else {
                    matches(not(TestingUtils.withViewHeightAndWidth(desiredHeight, desiredWidth)))
                }
            }
        }
        onView(withId(viewId)).perform(viewAction)
    }

    fun setVisibilityAndCheckHeight(viewId: Int, height: Float, isMatchCase: Boolean) {
        val desiredHeight = TestingUtils.dpToPx(CookingKACucumberTests.context, height)

        val viewAction = object : ViewAction {
            override fun getDescription(): String {
                return "Set view visibility to VISIBLE"
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.visibility = View.VISIBLE
                if (isMatchCase) {
                    matches(TestingUtils.withViewHeight(desiredHeight))
                } else {
                    matches(not(TestingUtils.withViewHeight(desiredHeight)))
                }
            }
        }
        onView(withId(viewId)).perform(viewAction)
    }

}