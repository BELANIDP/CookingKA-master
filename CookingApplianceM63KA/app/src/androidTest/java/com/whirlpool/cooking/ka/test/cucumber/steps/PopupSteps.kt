package com.whirlpool.cooking.ka.test.cucumber.steps

import android.view.View
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.presenter.fragments.mwo.ClockFragment
import com.whirlpool.cooking.ka.test.cucumber.appearance.PopupFragmentFragmentAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.navigation.PopupNavigationHelper
import com.whirlpool.cooking.ka.test.cucumber.navigation.setFragmentScenario
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * File       : com.whirlpool.cooking.ka.test.cucumber.steps
 * Brief      : PopupSteps class for popup test setup
 * Author     : GOYALM5
 * Created On : 25/02/2024
 * Details    : This class is having function related to popup fragment
 * ui test setups
 */

@RunWith(AndroidJUnit4::class)
class PopupSteps {

    private var popUpFragmentAppearanceTest: PopupFragmentFragmentAppearanceTest? = null
    private var popupNavigationHelper: PopupNavigationHelper? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        popUpFragmentAppearanceTest = PopupFragmentFragmentAppearanceTest()
        popupNavigationHelper = PopupNavigationHelper()
    }

    @After
    fun tearDown() {
        popUpFragmentAppearanceTest = null
        popupNavigationHelper = null
    }

    @Then("I expect that popup should be visible with Image")
    fun checkCenterImageVisibility() {
        popUpFragmentAppearanceTest?.imageViewHeaderCenterIsVisible()
    }

    @Then("I expect that popup should be visible with Title")
    fun checkTitleVisibility() {
        popUpFragmentAppearanceTest?.textViewTitleIsVisible()
    }

    @Then("I expect that popup should be visible with Notification")
    fun checkNotificationVisibility() {
        popUpFragmentAppearanceTest?.textViewNotificationIsVisible()
    }

    @Then("I expect that popup should be visible with Description")
    fun checkDescriptionVisibility() {
        popUpFragmentAppearanceTest?.textViewDescriptionIsVisible()
    }

    @Then("I expect that popup should be visible with ProgressBar")
    fun checkProgressBarVisibility() {
        popUpFragmentAppearanceTest?.popupProgressbarIsVisible()
    }

    @Then("I expect that popup should be visible with Left Side Button")
    fun checkLeftButtonVisibility() {
        popUpFragmentAppearanceTest?.textButtonLeftIsVisible()
    }

    @Then("I expect that popup should be dismiss on Left Side Button click")
    fun checkLeftButtonIsClickable() {
        LeakAssertions.assertNoLeaks()
        popupNavigationHelper?.performLeftButtonClick()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I click on Left Side button")
    fun performLeftButtonClick() {
        popUpFragmentAppearanceTest?.textButtonLeftIsViewClickable()
    }

    @Then("I expect that popup should be visible with Right Side Button")
    fun checkRightButtonVisibility() {
        popUpFragmentAppearanceTest?.textButtonRightIsVisible()
    }

    @Then("I expect that popup should be dismiss on Right Side Button click")
    fun checkRightButtonIsClickable() {
        popUpFragmentAppearanceTest?.textButtonRightIsViewClickable()
    }

    @Then("I click on Right Side button")
    fun performRightButtonClick() {
        popUpFragmentAppearanceTest?.textButtonRightIsViewClickable()
    }

    @Then("error should be log")
    fun checkAllViewNotVisible() {
        popUpFragmentAppearanceTest?.popupWithScrollIsNotVisible()
    }

    @Then("Image error should be log")
    fun checkCenterImageNotVisible() {
        popUpFragmentAppearanceTest?.imageViewHeaderCenterIsNotVisible()
    }

    @Then("Title error should be log")
    fun checkTitleNotVisible() {
        popUpFragmentAppearanceTest?.textViewTitleIsNotVisible()
    }

    @Then("Description error should be log")
    fun checkDescriptionNotVisible() {
        popUpFragmentAppearanceTest?.scrollViewIsNotVisible()
    }

    @Then("I expect that popup should be visible with scroll")
    fun checkScrollViewIsVisible() {
        popUpFragmentAppearanceTest?.scrollViewIsVisible()
    }

    @Then("Progress error should be log")
    fun checkProgressBarNotVisible() {
        popUpFragmentAppearanceTest?.popupProgressbarIsNotVisible()
    }

    @Then("Left Side Button error should be log")
    fun checkLeftButtonNotVisible() {
        popUpFragmentAppearanceTest?.textButtonLeftIsNotVisible()
    }

    @Then("Right Side Button error should be log")
    fun checkRightButtonNotVisible() {
        popUpFragmentAppearanceTest?.textButtonRightIsNotVisible()
    }

    @Then("Left Side Button click error should be log")
    fun checkLeftButtonIsEnabled() {
        popUpFragmentAppearanceTest?.textButtonLeftIsViewEnabled()
    }

    @Then("Right Side Button click error should be log")
    fun checkRightButtonIsEnabled() {
        popUpFragmentAppearanceTest?.textButtonRightIsViewEnabled()
    }

    @Then("I checked the Title text size")
    fun checkTitleTextViewSize() {
        popUpFragmentAppearanceTest?.tvTitleSizeValidation()
    }

    @Then("I checked the Notification text size")
    fun checkNotificationTextViewSize() {
        popUpFragmentAppearanceTest?.tvNotificationTextSizeValidation()
    }

    @Then("I checked the Description text size")
    fun checkDescriptionTextViewSize() {
        popUpFragmentAppearanceTest?.tvDescriptionSizeValidation()
    }

    @Test
    @UiThreadTest
    fun testOpenPopup() {
//        setFragmentScenario(PopupDialogTestFragment::class.java)
    }

    @Then("I navigate popup testing button screen")
    fun navigateToPopUpTestButton() {
//        setFragmentScenario(PopupDialogTestFragment::class.java)
    }

    @Then("I navigate to clock screen for popup screen")
    fun navigateToClockScreenForListViewScreen() {
        LeakAssertions.assertNoLeaks()
        testNavigationToClockScreen()
        LeakAssertions.assertNoLeaks()
    }

    @Test
    @UiThreadTest
    fun testNavigationToClockScreen() {
        setFragmentScenario(ClockFragment::class.java)
    }

    @Then("I click on Popup button")
    fun clickOnPopupFragmentButton() {
//        onView(withId(R.id.button1))
//            .check(matches(isDisplayed()))
//            .perform(click())
    }

    @Then("I click on first button")
    fun firstButtonClick() {
//        onView(withId(R.id.button1)).perform(forceClick())
    }

    private fun forceClick(): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Set view visibility to VISIBLE"
            }

            override fun getConstraints(): Matcher<View> {
                return allOf(isClickable(), isEnabled())
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.performClick()
                uiController?.loopMainThreadUntilIdle()
            }
        }
    }

    @Then("I click on second button")
    fun secondButtonClick() {
        LeakAssertions.assertNoLeaks()
        popupNavigationHelper?.performSecondButtonClick()
        LeakAssertions.assertNoLeaks()
    }


    @Then("I click on third button")
    fun thirdButtonClick() {
        LeakAssertions.assertNoLeaks()
        popupNavigationHelper?.performThirdButtonClick()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I click on fourth button")
    fun fourthButtonClick() {
        LeakAssertions.assertNoLeaks()
        popupNavigationHelper?.performFourthButtonClick()
        LeakAssertions.assertNoLeaks()
    }
}