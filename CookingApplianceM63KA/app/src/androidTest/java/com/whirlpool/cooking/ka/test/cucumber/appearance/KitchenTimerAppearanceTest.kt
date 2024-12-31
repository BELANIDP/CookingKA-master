package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import android.presenter.customviews.topsheet.TopSheetBehavior
import android.view.View
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkText
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import org.hamcrest.Matcher

class KitchenTimerAppearanceTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    fun clickOnTimer(){
        UiTestingUtils.sleep(4000)
        performVisibilityExpandAndCheckState()
        UiTestingUtils.sleep(1000)
        val position = 1
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_grid_list, position)
    }
    private fun performVisibilityExpandAndCheckState() {
        val viewAction = object : ViewAction {
            override fun getDescription(): String {
                return ""
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                if (view != null) {
                    var topSheetBehavior: TopSheetBehavior<*>? = null
                    if (view is FrameLayout) {
                        topSheetBehavior = TopSheetBehavior.from(view)
                        topSheetBehavior.setState(TopSheetBehavior.STATE_EXPANDED)
                    }
                }
            }
        }
        Espresso.onView(ViewMatchers.withId(R.id.top_sheet)).perform(viewAction)
    }

    fun timerTumblerVisible(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBasedMins)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBasedHours)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBasedSeconds)
    }

    fun timerHoursTumblerValidation(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBasedHours)
        UiTestingUtils.isViewVisible(R.id.labelHourTumbler)
        UiTestingUtils.isViewVisible(R.id.hourView)
        UiTestingUtils.isTextMatchingAndFitting(R.id.labelHourTumbler,"H")
        TestingUtils.checkTextColorValidation(R.id.labelHourTumbler, "#ffffff")
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.labelHourTumbler)), sizeInPixels.toFloat()
        )
        ViewMatchers.withId(R.id.hourView).matches(TestingUtils.withViewHeightAndWidth(TestingUtils.dpToPx(context,81f),TestingUtils.dpToPx(context,1f)))
        ViewMatchers.withId(R.id.tumblerNumericBasedHours).matches(TestingUtils.withViewHeightAndWidth(TestingUtils.dpToPx(context,240f),TestingUtils.dpToPx(context,112f)))
    }
    fun timerMinutesTumblerValidation(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBasedMins)
        UiTestingUtils.isViewVisible(R.id.labelMinuteTumbler)
        UiTestingUtils.isViewVisible(R.id.minView)
        UiTestingUtils.isTextMatchingAndFitting(R.id.labelMinuteTumbler,"M")
        TestingUtils.checkTextColorValidation(R.id.labelMinuteTumbler, "#ffffff")
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.labelMinuteTumbler)), sizeInPixels.toFloat()
        )
        ViewMatchers.withId(R.id.minView).matches(TestingUtils.withViewHeightAndWidth(TestingUtils.dpToPx(context,81f),TestingUtils.dpToPx(context,1f)))
        ViewMatchers.withId(R.id.tumblerNumericBasedMins).matches(TestingUtils.withViewHeightAndWidth(TestingUtils.dpToPx(context,240f),TestingUtils.dpToPx(context,112f)))
    }
    fun timerSecondsTumblerValidation(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBasedSeconds)
        UiTestingUtils.isViewVisible(R.id.labelSecTumbler)
        UiTestingUtils.isTextMatchingAndFitting(R.id.labelSecTumbler,"S")
        TestingUtils.checkTextColorValidation(R.id.labelSecTumbler, "#ffffff")
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.labelSecTumbler)), sizeInPixels.toFloat()
        )
        ViewMatchers.withId(R.id.tumblerNumericBasedSeconds).matches(TestingUtils.withViewHeightAndWidth(TestingUtils.dpToPx(context,240f),TestingUtils.dpToPx(context,112f)))
    }

    fun setKtToMinAndSec(min:Int,sec:Int){
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerNumericBasedMins))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(min))
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerNumericBasedSeconds))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(sec))
        UiTestingUtils.sleep(1000)
    }
    fun setKtOnNumpad(min:String){
        UiTestingUtils.sleep(1500)
        TestingUtils.enterNumberStr(min)
    }

    fun performClickOnStart(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.startNowText)
    }
    fun performClickOnStartNumpad(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.cook_time_text_button_right)
    }
    fun KtWidgetVisible(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(TestingUtils.withIndex(ViewMatchers.withId(R.id.kitchen_timer_widget), 0))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        UiTestingUtils.isViewVisible(R.id.kitchen_timer_widget)
    }
    fun performClickOnPause(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.ivKitchenTimerPause)
    }
    fun performClickOnOneMin(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.tvKitchenTimerAddOneMin)
    }
    fun performClickOnCancelTimer(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.ivKitchenTimerCancel)
    }
    fun performClickOnCancelTimerOnSecondTimer(){
        UiTestingUtils.sleep(500)
        Espresso.onView(TestingUtils.withIndex(ViewMatchers.withId(R.id.ivKitchenTimerPause), 1))
            .perform(click())
        UiTestingUtils.sleep(1000)
        Espresso.onView(TestingUtils.withIndex(ViewMatchers.withId(R.id.ivKitchenTimerCancel), 1))
            .perform(click())
        UiTestingUtils.sleep(1000)

    }
    fun performClickOnOneMinOnSecondTimer(){
        UiTestingUtils.sleep(500)
        Espresso.onView(TestingUtils.withIndex(ViewMatchers.withId(R.id.ivKitchenTimerPause), 1))
            .perform(click())
        UiTestingUtils.sleep(1000)
        Espresso.onView(TestingUtils.withIndex(ViewMatchers.withId(R.id.tvKitchenTimerAddOneMin), 1))
            .perform(click())
    }
    fun performClickOnPauseOnSecondTimer(){
        UiTestingUtils.sleep(500)
        Espresso.onView(TestingUtils.withIndex(ViewMatchers.withId(R.id.ivKitchenTimerPause), 1))
            .perform(click())
    }
    fun cancelTimerPopupVisible(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
    }
    fun KtTumblerVisible(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.ktTumblers)
    }
    fun runningKtOnClockVisible(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.text_view_kitchen_timer_running_text)
    }
    fun cancelTimerPopupValidation(){
        UiTestingUtils.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.text_view_title))
            .check { view, _ -> checkText("Cancel Timer", view) }

//        Espresso.onView(ViewMatchers.withId(R.id.text_view_description))
//            .check { view, _ -> checkText("%s running that will be cancelled if you continue with your selection.", view) }
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
        val desiredDescriptionTextSizeSp = 30f
        val descriptionSizeInPixels = TestingUtils.spToPx(context, desiredDescriptionTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.text_view_description)), descriptionSizeInPixels.toFloat()
        )
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }
    fun completedTimerPopupVisible(){
        UiTestingUtils.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.text_view_title))
            .check { view, _ -> checkText("Timer 1 Complete", view) }

//        Espresso.onView(ViewMatchers.withId(R.id.text_view_description))
//            .check { view, _ -> checkText("%s running that will be cancelled if you continue with your selection.", view) }
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
        val desiredDescriptionTextSizeSp = 30f
        val descriptionSizeInPixels = TestingUtils.spToPx(context, desiredDescriptionTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.text_view_description)), descriptionSizeInPixels.toFloat()
        )
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }
    fun addTimerButtonClick(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }

    fun repeatButtonClick(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.text_button_left)
    }
    fun dismissButtonClick(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.text_button_right)
    }
}