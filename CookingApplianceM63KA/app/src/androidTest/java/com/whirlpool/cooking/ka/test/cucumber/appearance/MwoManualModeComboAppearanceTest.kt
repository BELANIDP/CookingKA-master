package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndex
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkColorOfText
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkText
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkTextSize
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import org.hamcrest.Matchers


/**
 * File        : com.whirlpool.cooking.ka.test.cucumber.steps.MwoManualModeSteps
 * Brief       : Mwo Manual mode Appearance automation test cases
 * Author      : DUNGAS
 * Created On  : 24/04/2024
 */

class MwoManualModeComboAppearanceTest {
    val context: Context = ApplicationProvider.getApplicationContext()

    fun performClickOnClockScreen(){
        UiTestingUtils.sleep(3500)
        UiTestingUtils.performClick(R.id.main_layout)
        UiTestingUtils.sleep(1500)
    }

    fun checkUpperAndLowerCavityButtonProperties(
        cavityName: String,
        cavityTextColor: String,
        cavityTextSize: String,
        cavityImageResource: String,
        )
    {
        var cavityId = -1
        var imageId = -1
        var resId = -1

        if (cavityName == "Set Microwave") {
            cavityId = R.id.uppper_cavity_lbl
            imageId = R.id.upper_cavity_icon
            resId = R.drawable.ic_oven_cavity
        } else if (cavityName == "Set Lower Oven") {
            cavityId = R.id.lower_cavity_lbl
            imageId = R.id.lower_cavity_icon
            resId = R.drawable.ic_lower_cavity

        }

        Espresso.onView(ViewMatchers.withId(cavityId))
            .check { view, _ -> checkText(cavityName, view) }
            .check { view, _ -> checkColorOfText(cavityTextColor, view) }
            .check { view, _ -> checkTextSize(cavityTextSize, view) }

        Espresso.onView(ViewMatchers.withId(imageId))
            .check(ViewAssertions.matches(TestingUtils.withDrawable(resId)))
    }

    fun performClickOnMicrowaveButton(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.upper_oven_layout)
        UiTestingUtils.sleep(1500)
    }

    fun checkRecipeSelectionVisibility(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerString))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun scrollTumblerToRight(index:Int){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerString, index)
    }
    fun isRecipeTumblerVisible(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerString))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun scrollToIndex(index:Int){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerString))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(index))
    }

    fun scrollToIndexAndClick(index:Int){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerString))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(index))
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerString, index)
    }

    fun scrollToTargetTextAndClick(targetText: String) {
        UiTestingUtils.sleep(1500)
        TestingUtils.withRecyclerViewScrollToTargetTextAndClick(R.id.tumblerString, targetText)
        UiTestingUtils.sleep(1500)
    }

    fun instructionScreenIsVisible(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.main_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun recipeScreenIsVisible(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerString))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun titleTextSizeInstructionScreenValidation(){
        UiTestingUtils.sleep(1500)
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)), sizeInPixels.toFloat()
        )
    }

    fun descriptionTextSizeInstructionScreenValidation(){
        UiTestingUtils.sleep(1500)
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(TestingUtils.withIndex(withId(R.id.description_text), 0)), sizeInPixels.toFloat()
        )
    }

    fun performClickOnNextButtonOnInstructionScreen(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_right)
    }
    fun performClickOnNextButtonOnVerticalTumblerScreen(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.btnPrimary)
    }
    fun performClickOnNextButtonOnNumpadScreen(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.cook_time_text_button_right)
    }

    fun isNumpadVisible(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(withId(R.id.keyboardview))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    fun isVerticalTumblerVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.tumblerLeft)
    }
    fun isDoorOpenClosePopupVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
    }

    fun prepareMwoScreenTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_title, "Prepare Microwave")
    }

    fun prepareMwoScreenTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
    }

    fun prepareMwoScreenTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }


    fun prepareOvenScreenDescriptionTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(
            R.id.text_view_description,
            "Before starting, please open and close the\nmicrowave door."
        )
    }

    fun prepareMwoScreenDescriptionTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
    }

    fun prepareMwoScreenDescriptionTextSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )
    }


    fun doorHasOpenedClosedMwoScreenTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_title, "Press Start")
    }


    fun doorHasOpenedClosedMwoScreenTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
    }

    fun doorHasOpenedClosedMwoScreenTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }


    fun doorHasOpenedClosedMwoScreenDescriptionTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_description, "Press START to begin cooking.")
    }

    fun doorHasOpenedClosedMwoScreenDescriptionTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
    }

    fun doorHasOpenedClosedMwoScreenDescriptionTextSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )
    }

    /* Door has opened closed screen Start button */

    fun doorHasOpenedClosedMwoScreenStartButtonTextValidation() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches(withText("START")))
    }

    fun doorHasOpenedClosedMwoScreenStartButtonTextColor() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2)).check(
            matches(
                TestingUtils.withTextColor(Color.parseColor("#ffffff"))
            )
        )
    }

    fun doorHasOpenedClosedMwoScreenStartButtonDisabled() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2)).check(
            matches(
                TestingUtils.withTextColor(Color.parseColor("#595959"))
            )
        )
    }

    fun doorHasOpenedClosedMwoScreenStartButtonTextSize() {
        val desiredTextSizeSp = 32f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2)),
            sizeInPixels.toFloat()
        )
    }

    fun doorHasOpenedClosedMwoScreenStartButtonViewIsClickable() {
        UiTestingUtils.sleep(2000)
        onView(withIndex(withId(R.id.text_view_text_button),2)).perform(click())
    }

    fun doorHasOpenedClosedMwoScreenStartButtonViewIsEnabled() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches(isEnabled()))
    }
    fun setRecipeCooktimeViaNumpad(cookTimeSec: String) {
        UiTestingUtils.sleep(1500)
        TestingUtils.enterNumberStr(cookTimeSec)

    }

    fun performClickOnTumblerIcon(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.ivTumblerIcon)
    }

    fun performClickOnStartButtonOnDoorOpenClosePopup(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withText(R.string.text_button_start))
            .check(ViewAssertions.matches(Matchers.notNullValue()));
        Espresso.onView(ViewMatchers.withText(R.string.text_button_start))
            .perform(ViewActions.click())
        UiTestingUtils.sleep(1000)
    }


    fun isStatusScreenVisible(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(withId(R.id.singleStatusWidget))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun performClickOnBackButtonOnInstructionScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }
    fun performClickOnBackButtonOnNumpadScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.ivBackIcon)
    }
    fun performClickOnBackButtonOnVerticalTumblerScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }
    fun performClickOnNumpadButtonOnVerticalTumblerScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }

    fun isDeleteButtonClickable(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewClickable(R.id.ivCancelIcon)
    }

    fun performClickOnThreeDotsIconButton(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.ivOvenCavityMoreMenu)
    }

    fun validateMoreOptionsPopupVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
    }
    fun validateOvenIconVisibleNearRecipeName(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
    }
    fun validateRecipeNameVisible(){
        UiTestingUtils.isViewVisible(R.id.tvRecipeWithTemperature)
    }
    fun validateRecipeNameTextSize(){
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvRecipeWithTemperature)), sizeInPixels.toFloat()
        )
    }
    fun validateRecipeNameTextColor(){
        TestingUtils.checkTextColorValidation(R.id.tvRecipeWithTemperature, "#ffffff")
    }
    fun validateRemainingTimeVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.tvCookTimeRemaining)
    }

    fun plusFiveButtonIsClickable() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewClickable(R.id.tvSetOvenCookTime)
    }

    fun validateClockScreenVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.text_view_clock_digital_clock_time)
        UiTestingUtils.sleep(1500)
    }
    fun validateSetLowerOvenButtonVisible(){
        UiTestingUtils.isViewVisible(R.id.clLowerCavitySelectionLayout)
    }
    fun validateSetMwoOvenButtonVisible(){
        UiTestingUtils.isViewVisible(R.id.clUpperCavitySelectionLayout)
    }

    fun performClickTurnOffButton(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.tvOvenStateAction)
        UiTestingUtils.sleep(1500)
    }
    fun performClickSetLowerButtonButton(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.clLowerCavitySelectionLayout)
    }
    fun performClickSetMwoButton(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.clUpperCavitySelectionLayout)
    }

    fun moreOptionsPopupTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.more_options_title, "More Options")
    }

    fun moreOptionsPopupTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.more_options_title, "#ffffff")
    }

    fun moreOptionsPopupTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.more_options_title)), sizeInPixels.toFloat()
        )
    }

    fun performClickOnUpperLeftButtonMoreOptionsPopup(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.moreCycleOptionsRecycler,0)
    }
    fun performClickOnLowerLeftButtonMoreOptionsPopup(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.moreDefaultOptionsRecycler,0)
    }
    fun performClickOnUpperRightButtonMoreOptionsPopup(){
        UiTestingUtils.sleep(1500)
    UiTestingUtils.performClickOnRecyclerViewIndex(R.id.moreCycleOptionsRecycler,1)
    }
    fun performClickOnLowerRightButtonMoreOptionsPopup(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.moreDefaultOptionsRecycler,1)
    }
    fun performClickOnLowerButtonMoreOptionsPopup(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.moreCycleOptionsRecycler,4)
        UiTestingUtils.sleep(2500)

    }
    fun validateChangeTempNumpadVisible(){
        UiTestingUtils.isViewVisible(R.id.keyboardview)
    }
    fun validateChangeCookTimeNumpadVisible(){
        UiTestingUtils.isViewVisible(R.id.keyboardview)
    }
    fun validateInstructionPopupVisible(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(withId(R.id.scroll_view))
            .check(matches(ViewMatchers.isDisplayed()))
        UiTestingUtils.isViewVisible(R.id.text_button_right_ok)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
    }

    fun performClickOkButtonMoreOptionPopup(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withText(R.string.text_button_ok))
            .check(ViewAssertions.matches(Matchers.notNullValue()));
        Espresso.onView(ViewMatchers.withText(R.string.text_button_ok))
            .perform(ViewActions.click())
        UiTestingUtils.sleep(1000)
    }

}