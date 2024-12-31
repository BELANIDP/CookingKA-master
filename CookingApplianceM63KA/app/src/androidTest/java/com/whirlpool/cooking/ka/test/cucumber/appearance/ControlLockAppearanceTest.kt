package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.appcompat.content.res.AppCompatResources
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.LayoutAssertions.noEllipsizedText
import androidx.test.espresso.assertion.LayoutAssertions.noOverlaps
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndex
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withLineHeight
import com.whirlpool.cooking.widgets.CustomControlLockSeekBar
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import com.whirlpool.hmi.uitesting.components.view.GenericViewTest
import org.hamcrest.Matcher

class ControlLockAppearanceTest {

    val context = ApplicationProvider.getApplicationContext<Context>()

    fun performClickOnControlLock() {
        UiTestingUtils.sleep(1000)
        onView(withIndex(withId(R.id.grid_parent_view), 0)).perform(ViewActions.click())
    }

    fun controlLockPopUpIsVisible(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
    }

    fun controlLockPopUpTitleValidation(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_title, "Control Lock" )

        val desiredLabelTextSizeSp = 40f
        val sizeInPixelsForLabel = TestingUtils.spToPx(context, desiredLabelTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_title)), sizeInPixelsForLabel.toFloat()
        )
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.text_view_title), 0)), typeface
        )

        onView(withId(R.id.text_view_title)).check(noOverlaps())

        onView(withId(R.id.text_view_title)).check(noEllipsizedText())
    }

    fun controlLockPopUpDescriptionValidation(){
        UiTestingUtils.sleep(1000)

        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_description, context.getString(R.string.text_description_controlLock))

        val desiredLabelTextSizeSp = 30f
        val sizeInPixelsForLabel = TestingUtils.spToPx(context, desiredLabelTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_description)), sizeInPixelsForLabel.toFloat()
        )
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.text_view_description), 0)), typeface
        )
    }

    fun controlLockLeftButtonValdiation(){
        UiTestingUtils.sleep(1000)

//        GenericTextViewTest.checkMatchesSize(
//            Espresso.onView(ViewMatchers.withId(R.id.text_button_left)),
//            WRAP_CONTENT,
//            WRAP_CONTENT
//        )

        UiTestingUtils.isViewClickable(R.id.text_button_left)
        UiTestingUtils.isViewEnabled(R.id.text_button_left)

//        UiTestingUtils.isTextMatchingAndFitting(R.id.text_button_left, "cancel")
    }

    fun controlLockRightButtonValdiation() {
        UiTestingUtils.sleep(1000)

//        GenericTextViewTest.checkMatchesSize(
//            Espresso.onView(ViewMatchers.withId(R.id.text_button_right)),
//            ActionBar.LayoutParams.WRAP_CONTENT,
//            ActionBar.LayoutParams.WRAP_CONTENT
//        )

        UiTestingUtils.isViewClickable(R.id.text_button_right)
        UiTestingUtils.isViewEnabled(R.id.text_button_right)

//        UiTestingUtils.isTextMatchingAndFitting(R.id.text_button_right, context.getString(R.string.text_button_continue))
    }

    fun performClickOnContinue() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_right)
    }

    fun validateControlLockIcon(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.ivStatusIcon1)

        val desiredLabelTextSizeSp = 32f
        val sizeInPixelsForLabel = TestingUtils.dpToPx(context, desiredLabelTextSizeSp)

        val desiredLabel1TextSizeSp = 32f
        val sizeInPixelsForLabel1 = TestingUtils.dpToPx(context, desiredLabel1TextSizeSp)

        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivStatusIcon1)),sizeInPixelsForLabel,sizeInPixelsForLabel1)

        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.icon_32px_lock)
        GenericViewTest.checkMatchesBackground(
            onView(withId(R.id.ivStatusIcon1)),
            leftIcon
        )
    }

    fun visibilityOfSlidingBar(){
        UiTestingUtils.isViewVisible(R.id.control_lock_popup)
    }

    fun validateSlidingArrow(){
        UiTestingUtils.isViewVisible(R.id.control_lock_custom_seek_bar)

        val desiredLabelTextSizeSp = 541f
        val sizeInPixelsForLabel = TestingUtils.dpToPx(context, desiredLabelTextSizeSp)

        val desiredLabel1TextSizeSp = 96f
        val sizeInPixelsForLabel1 = TestingUtils.dpToPx(context, desiredLabel1TextSizeSp)

        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.control_lock_custom_seek_bar)),sizeInPixelsForLabel,sizeInPixelsForLabel1)

//        val slidingBarIcon = AppCompatResources.getDrawable(context, R.drawable.control_lock_slider)
//        GenericViewTest.checkMatchesBackground(
//            Espresso.onView(withId(R.id.control_lock_custom_seek_bar)),
//            slidingBarIcon
//        )
    }

    fun validateArrow1(){
        UiTestingUtils.isViewVisible(R.id.icon_40px_arrow_2)

        val desiredLabelTextSizeSp = 40f
        val sizeInPixelsForLabel = TestingUtils.dpToPx(context, desiredLabelTextSizeSp)

        val desiredLabel1TextSizeSp = 40f
        val sizeInPixelsForLabel1 = TestingUtils.dpToPx(context, desiredLabel1TextSizeSp)

        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.icon_40px_arrow_2)),sizeInPixelsForLabel,sizeInPixelsForLabel1)

        val arrow1 = AppCompatResources.getDrawable(context, R.drawable.icon_list_item_right_arrow)
        GenericViewTest.checkMatchesBackground(
            onView(withId(R.id.icon_40px_arrow_2)),
            arrow1
        )
    }

    fun validateArrow2(){
        UiTestingUtils.isViewVisible(R.id.icon_40px_arrow_1)

        val desiredLabelTextSizeSp = 40f
        val sizeInPixelsForLabel = TestingUtils.dpToPx(context, desiredLabelTextSizeSp)

        val desiredLabel1TextSizeSp = 40f
        val sizeInPixelsForLabel1 = TestingUtils.dpToPx(context, desiredLabel1TextSizeSp)

        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.icon_40px_arrow_1)),sizeInPixelsForLabel,sizeInPixelsForLabel1)

        val arrow2 = AppCompatResources.getDrawable(context, R.drawable.icon_list_item_right_arrow)
        GenericViewTest.checkMatchesBackground(
            onView(withId(R.id.icon_40px_arrow_1)),
            arrow2
        )
    }

    fun validateArrow3(){
        UiTestingUtils.isViewVisible(R.id.icon_40px_arrow_3)

        val desiredLabelTextSizeSp = 40f
        val sizeInPixelsForLabel = TestingUtils.dpToPx(context, desiredLabelTextSizeSp)

        val desiredLabel1TextSizeSp = 40f
        val sizeInPixelsForLabel1 = TestingUtils.dpToPx(context, desiredLabel1TextSizeSp)

        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.icon_40px_arrow_3)),sizeInPixelsForLabel,sizeInPixelsForLabel1)

        val arrow3 = AppCompatResources.getDrawable(context, R.drawable.icon_list_item_right_arrow)
        GenericViewTest.checkMatchesBackground(
            onView(withId(R.id.icon_40px_arrow_3)),
            arrow3
        )
    }

    fun lockicon(){
        UiTestingUtils.isViewVisible(R.id.control_lock)

        val desiredLabelTextSizeSp = 96f
        val sizeInPixelsForLabel = TestingUtils.dpToPx(context, desiredLabelTextSizeSp)

        val desiredLabel1TextSizeSp = 96f
        val sizeInPixelsForLabel1 = TestingUtils.dpToPx(context, desiredLabel1TextSizeSp)

        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.control_lock)),sizeInPixelsForLabel,sizeInPixelsForLabel1)

        val arrow3 = AppCompatResources.getDrawable(context, R.drawable.icon_control_lock)
        GenericViewTest.checkMatchesBackground(
            onView(withId(R.id.control_lock)),
            arrow3
        )
    }

    fun slidingBarUnlockTextValidation(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_popup_control_lock_message, context.getString(R.string.text_description_unlock) )

        val desiredLabelTextSizeSp = 790f
        val sizeInPixelsForLabel = TestingUtils.dpToPx(context, desiredLabelTextSizeSp)

        val desiredLabel1TextSizeSp = 36f
        val sizeInPixelsForLabel1 = TestingUtils.dpToPx(context, desiredLabel1TextSizeSp)

        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.text_view_popup_control_lock_message)),sizeInPixelsForLabel,sizeInPixelsForLabel1)

        val desiredLabel2TextSizeSp = 30f
        val sizeInPixelsForLabel2 = TestingUtils.spToPx(context, desiredLabel2TextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(withId(R.id.text_view_popup_control_lock_message)), sizeInPixelsForLabel2.toFloat()
        )
        TestingUtils.checkTextColorValidation(R.id.text_view_popup_control_lock_message, "#AAA5A1")

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.text_view_popup_control_lock_message), 0)), typeface
        )
    }

    fun clockScreen(){
        UiTestingUtils.sleep(4000)
        UiTestingUtils.isViewVisible(R.id.main_layout)

    }

    fun performSliding() {
        val viewAction = object : ViewAction {
            override fun getDescription(): String {
                return ""
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                if (view != null) {
                    val customControlLockSeekBar  =  view as CustomControlLockSeekBar
                    customControlLockSeekBar.progress = 100
                    customControlLockSeekBar.forwardValueAnimator?.start()
                    customControlLockSeekBar.handleForwardAnimation()
                    UiTestingUtils.sleep(3000)

                }
            }
        }
        onView(withId(R.id.control_lock_custom_seek_bar)).perform(viewAction)

        UiTestingUtils.sleep(7000)
    }

    fun clickOnSetCookTime(){
        onView(withIndex(withId(R.id.tvSetOvenCookTime), 0)).perform(ViewActions.click())
    }

    fun performClickOnCancel(){
        UiTestingUtils.performClick(R.id.text_button_left)
    }

    fun preferenceScreenVisibility(){
        UiTestingUtils.isViewVisible(R.id.headerBarSettings)
    }
}