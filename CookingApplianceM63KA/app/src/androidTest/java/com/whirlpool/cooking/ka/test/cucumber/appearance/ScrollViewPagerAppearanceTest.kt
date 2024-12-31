/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.graphics.Color
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.spToPx
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TextViewPropertiesMatcher
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matcher

class ScrollViewPagerAppearanceTest {

    fun checkAllViewsVisibility() {
//        UiTestingUtils.isViewVisible(R.id.scroll_view_test)
    }

    fun checkScrollViewVerticalScrollIsNotEnabled() {
//        isVerticalScrollBarEnabled(R.id.scroll_view_test)
    }

    fun enableVerticalScrollBarOnScroll() {
//        enableVerticalScrollBarOnScroll(R.id.scroll_view_test)
    }

    private fun isVerticalScrollBarEnabled(viewId: Int): Boolean {
        var isScrollBarEnabled = false
        onView(withId(viewId)).perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Check if vertical scroll bar is enabled"
                }

                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(ScrollView::class.java)
                }

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        isScrollBarEnabled = view.isVerticalScrollBarEnabled
                    }
                }
            }
        )
        return isScrollBarEnabled
    }

    private fun enableVerticalScrollBarOnScroll(viewId: Int) {
        onView(withId(viewId)).perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Enable vertical scroll bar on scroll"
                }

                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(ScrollView::class.java)
                }

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        view.viewTreeObserver.addOnScrollChangedListener {
                            view.isVerticalScrollBarEnabled = true
                        }
                    }
                }
            }
        )
    }

    fun isScrollingDown(viewId: Int): Boolean {
        var isScrollingDownwards = false
        onView(withId(viewId)).perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Check if scrolling downwards works"
                }

                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(ScrollView::class.java)
                }

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        val initialScrollY = view.scrollY
                        view.post {
                            view.fullScroll(View.FOCUS_DOWN)
                        }
                        val finalScrollY = view.scrollY
                        isScrollingDownwards = finalScrollY > initialScrollY
                    }
                }
            }
        )
        return isScrollingDownwards
    }

    fun isScrollingUp(viewId: Int): Boolean {
        var isScrollingUpwards = false
        onView(withId(viewId)).perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Check if scrolling upwards works"
                }

                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(ScrollView::class.java)
                }

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        val initialScrollY = view.scrollY
                        view.post {
                            view.fullScroll(View.FOCUS_UP)
                        }
                        val finalScrollY = view.scrollY
                        isScrollingUpwards = finalScrollY < initialScrollY
                    }
                }
            }
        )
        return isScrollingUpwards
    }

    fun descriptionTextViewAlignmentValidation(isMatched: Boolean) {
        var textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT
        var textStyle = TextView.TEXT_ALIGNMENT_GRAVITY.toFloat()
        if (!isMatched) {
            textProperties = TextViewPropertiesMatcher.TextProperties.TEXT_ALIGNMENT_NOT_MATCHED
            textStyle = TextView.TEXT_ALIGNMENT_TEXT_END.toFloat()
        }
      /*  TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_description_test)),
            textStyle,
            textProperties
        )*/
    }

    fun descriptionTextViewSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
//        GenericTextViewTest.checkMatchesTextSize(
//            onView(withId(R.id.text_view_description_test)), sizeInPixels.toFloat()
//        )
    }

    fun descriptionTextViewSizeValidationNotMatched() {
        val desiredTextSizeSp = 32f
        val sizeInPixels = spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
     /*   TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_description_test)),
            sizeInPixels.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_SIZE_NOT_MATCHED
        )*/
    }

    fun descriptionTextViewColorValidation() {
        val customColorValue = Color.parseColor("#ffffff")
        /*onView(withId(R.id.text_view_description_test)).check { view, _ ->
            assertEquals(customColorValue, (view as TextView).currentTextColor)
        }*/
    }

    fun descriptionTextViewColorValidationNotMatched() {
        val customColorValue = Color.parseColor("#FF0000")
    /*    TestingUtils.checkMatchesTextProperties(
            onView(withId(R.id.text_view_description_test)),
            customColorValue.toFloat(),
            TextViewPropertiesMatcher.TextProperties.TEXT_COLOR_NOT_MATCHED
        )*/
    }

    fun scrollTo() {
    /*    onView(withId(R.id.scroll_view_test)).perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Enable vertical scroll bar on scroll"
                }

                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(ScrollView::class.java)
                }

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        view.viewTreeObserver.addOnScrollChangedListener {
                            if (!view.isVerticalScrollBarEnabled) {
                                view.isVerticalScrollBarEnabled = true
                            }
                            ViewActions.scrollTo()
                        }
                    }
                }
            }
        )*/
    }
}