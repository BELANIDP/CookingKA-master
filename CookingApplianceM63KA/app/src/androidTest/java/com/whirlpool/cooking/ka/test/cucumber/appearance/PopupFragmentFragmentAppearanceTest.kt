package com.whirlpool.cooking.ka.test.cucumber.appearance

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest

/**
 * File       : com.whirlpool.cooking.ka.test.cucumber.appearance
 * Brief      : AppearanceTest for popup fragment
 * Author     : GOYALM5
 * Created On : 25/02/2024
 * Details    : This class PopupFragmentFragmentAppearanceTest having function
 * related to popup fragments ui test cases e.g. visibility, size, color, clickable
 *
 */

class PopupFragmentFragmentAppearanceTest {

    fun imageViewHeaderCenterIsVisible() {

//        onView(
//            TestingUtils.withIndex(
//                ViewMatchers.withId(R.id.image_view_header_center),
//                0
//            )
//        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun textViewTitleIsVisible() {
        onView(
            TestingUtils.withIndex(
                ViewMatchers.withId(R.id.text_view_title),
                0
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
     }

    fun popupProgressbarIsVisible() {
        onView(ViewMatchers.withId(R.id.popup_progressbar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun textViewNotificationIsVisible() {
        onView(ViewMatchers.withId(R.id.text_view_notification))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun textViewDescriptionIsVisible() {
        onView(
            TestingUtils.withIndex(
                ViewMatchers.withId(R.id.text_view_description),
                0
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun scrollViewIsVisible() {
        onView(ViewMatchers.withId(R.id.scroll_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun textButtonLeftIsVisible() {
        onView(ViewMatchers.withId(R.id.text_button_left))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun textButtonRightIsVisible() {
        onView(ViewMatchers.withId(R.id.text_button_right))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }


    fun imageViewHeaderCenterIsNotVisible() {
//        UiTestingUtils.isViewNotVisible(R.id.image_view_header_center)
    }

    fun textViewTitleIsNotVisible() {
        UiTestingUtils.isViewNotVisible(R.id.text_view_title)
    }

    fun popupProgressbarIsNotVisible() {
        UiTestingUtils.isViewNotVisible(R.id.popup_progressbar)
    }

    fun scrollViewIsNotVisible() {
        UiTestingUtils.isViewNotVisible(R.id.scroll_view)
    }

    fun textButtonLeftIsNotVisible() {
        UiTestingUtils.isViewNotVisible(R.id.text_button_left)
    }

    fun textButtonRightIsNotVisible() {
        UiTestingUtils.isViewNotVisible(R.id.text_button_right)
    }

    fun popupWithScrollIsNotVisible() {
        UiTestingUtils.isViewNotVisible(R.id.popup_with_scroll)
    }

    fun textButtonLeftIsViewClickable() {
        UiTestingUtils.isViewClickable(R.id.text_button_left)
    }

    fun textButtonRightIsViewClickable() {
        UiTestingUtils.isViewClickable(R.id.text_button_right)
    }

    fun textButtonLeftIsViewEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_button_left)
    }

    fun textButtonRightIsViewEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_button_right)
    }

    fun tvTitleSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(ViewMatchers.withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }

    fun tvDescriptionSizeValidation() {
        val size =
            CookingKACucumberTests.context.resources.getDimension(R.dimen.popup_description_text_size).toInt()
        GenericTextViewTest.checkMatchesTextSize(
            onView(ViewMatchers.withId(R.id.text_view_description)), size.toFloat()
        )
    }

    fun tvNotificationTextSizeValidation() {
        val size =
            CookingKACucumberTests.context.resources.getDimension(R.dimen.popup_notification_text_size).toInt()
        GenericTextViewTest.checkMatchesTextSize(
            onView(ViewMatchers.withId(R.id.text_view_notification)), size.toFloat()
        )
    }
}