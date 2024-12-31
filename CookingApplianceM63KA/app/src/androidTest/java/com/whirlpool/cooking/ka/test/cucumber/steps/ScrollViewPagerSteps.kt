/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */

package com.whirlpool.cooking.ka.test.cucumber.steps
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import android.presenter.fragments.mwo.ClockFragment
import com.whirlpool.cooking.ka.test.cucumber.appearance.ScrollViewPagerAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.navigation.setFragmentScenario
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScrollViewPagerSteps {

    private var scrollViewPagerAppearanceTest: ScrollViewPagerAppearanceTest? = null

    @Before
    fun setUp() {
        scrollViewPagerAppearanceTest = ScrollViewPagerAppearanceTest()
    }

    @After
    fun tearDown() {
        scrollViewPagerAppearanceTest = null
    }

    @Then("Scroll view screen will be visible")
    fun checkScrollViewVisibility() {
        scrollViewPagerAppearanceTest?.checkAllViewsVisibility()
    }

    @Then("I checked that vertical scroll is not enabled")
    fun checkScrollViewVerticalScrollIsNotEnabled() {
        scrollViewPagerAppearanceTest?.checkScrollViewVerticalScrollIsNotEnabled()
    }

    @Then("I checked that vertical scroll is enabled")
    fun enableVerticalScrollBarOnScroll() {
        scrollViewPagerAppearanceTest?.enableVerticalScrollBarOnScroll()
    }

    @Then("I verify that the content is at the bottom")
    fun checkIfScrollingDownwardsWorks() {
//        scrollViewPagerAppearanceTest?.isScrollingDown(R.id.scroll_view_test)
    }

    @Then("I verify that the content is at the top")
    fun checkIfScrollingUpwardsWorks() {
//        scrollViewPagerAppearanceTest?.isScrollingUp(R.id.scroll_view_test)
    }

    @Then("I checked the description text view alignment")
    fun checkClockTextViewAlignment() {
        scrollViewPagerAppearanceTest?.descriptionTextViewAlignmentValidation(true)
    }

    @Then("I checked the description text view alignment not matched")
    fun checkClockTextViewAlignmentNotMatched() {
        scrollViewPagerAppearanceTest?.descriptionTextViewAlignmentValidation(false)
    }

    @Then("I checked the description text view size")
    fun checkClockTextViewSize() {
        scrollViewPagerAppearanceTest?.descriptionTextViewSizeValidation()
    }

    @Then("I checked the description text view size not matched")
    fun checkClockTextViewSizeNotMatched() {
        scrollViewPagerAppearanceTest?.descriptionTextViewSizeValidationNotMatched()
    }

    @Then("I checked the description text view color")
    fun checkClockTextViewColor() {
        scrollViewPagerAppearanceTest?.descriptionTextViewColorValidation()
    }

    @Then("I checked the description text view color not matched")
    fun checkClockTextViewColorNotMatched() {
        scrollViewPagerAppearanceTest?.descriptionTextViewColorValidationNotMatched()
    }

    @Then("I scroll to the bottom")
    fun scrollToBottom() {
        scrollViewPagerAppearanceTest?.scrollTo()
    }

    @Then("I scroll to the top")
    fun scrollToTop() {
        scrollViewPagerAppearanceTest?.scrollTo()
    }

    @Then("I navigate to scroll view screen")
    fun navigateToScrollViewScreen() {
        testNavigationToScrollViewScreen()
    }

    @Then("I navigate to clock screen for scroll view screen")
    fun navigateToClockScreenForScrollViewScreen() {
        testNavigationToClockViewScreenForScrollViewScreen()
    }

    @Test
    @UiThreadTest
    fun testNavigationToScrollViewScreen() {
//        setFragmentScenario(ScrollViewTestFragment::class.java)
    }

    @Test
    @UiThreadTest
    fun testNavigationToClockViewScreenForScrollViewScreen() {
        setFragmentScenario(ClockFragment::class.java)
    }
}