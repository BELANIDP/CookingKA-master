package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkText
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import org.hamcrest.Matchers

class ProbeAppearanceTest {
    val context = ApplicationProvider.getApplicationContext<Context>()

    fun probeRecipeGridViewVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.recycler_view_grid_list)
    }
    fun probeRecipeGridViewValidation(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Probe")
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )
    }

    fun performClickOnBackButtonOnProbeRecipeGridView(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }
    fun performClickOnBackButtonOnProbeTempKeyboard(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivBackIcon)
    }
    fun probeStillDetectedPopupVisible(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.popup_with_scroll))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun probeStillDetectedPopupValidation(){
        Espresso.onView(withId(R.id.text_view_title))
            .check { view, _ -> checkText("Probe Detected", view) }

        Espresso.onView(withId(R.id.text_view_description))
            .check { view, _ -> checkText("Remove the probe to use standard oven modes.", view) }
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
        val desiredDescriptionTextSizeSp = 30f
        val descriptionSizeInPixels = TestingUtils.spToPx(context, desiredDescriptionTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), descriptionSizeInPixels.toFloat()
        )
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }
    fun returnToProbeModesButtonValidation(){
        UiTestingUtils.sleep(500)
        Espresso.onView(TestingUtils.withIndex(withId(R.id.text_view_text_button), 2))
            .check(ViewAssertions.matches(ViewMatchers.withText("RETURN TO PROBE MODES")))
        Espresso.onView(TestingUtils.withIndex(withId(R.id.text_view_text_button), 2)).check(
            ViewAssertions.matches(
                TestingUtils.withTextColor(Color.parseColor("#ffffff"))
            )
        )
        val desiredTextSizeSp = 32f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(TestingUtils.withIndex(withId(R.id.text_view_text_button), 2)),
            sizeInPixels.toFloat()
        )
    }
    fun performClickOnReturnToProbeModesButton(){
        UiTestingUtils.sleep(500)
        Espresso.onView(TestingUtils.withIndex(withId(R.id.text_view_text_button), 2)).perform(click())
    }
    fun scrollToTextAndClick(targetText: String) {
        UiTestingUtils.sleep(1500)
        TestingUtils.funWithGridViewScrollToTargetTextAndClick(targetText)
        UiTestingUtils.sleep(1500)
    }

    fun isProbeTempTumblerVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBased)
    }
    fun probeTempTumblerTextValidation(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Probe Temperature")
    }
    fun clickOnNumpadIconOnTempTumbler(){
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }
    fun validateChangeTempNumpadVisible(){
        UiTestingUtils.isViewVisible(R.id.keyboardview)
    }
    fun setProbeTempViaTumbler(index:Int){
        Espresso.onView(ViewMatchers.withId(R.id.tumblerNumericBased))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(index))
    }
    fun setOvenTempViaTumbler(index:Int){
        Espresso.onView(ViewMatchers.withId(R.id.tumblerNumericBased))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(index))
    }
    fun isProbePreviewScreenVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.recycler_view_preview)
    }
    fun performClickOnProbeTempOnPreviewScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_preview, 0)
    }
    fun performClickOnOvenTempOnPreviewScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_preview, 1)
    }
    fun performClickOnNextButtonOnProbePreviewScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.btnPrimary)
    }
    fun statusScreenVisibleForProbe(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.singleStatusWidget)
    }

    fun statusScreenValidateForProbe(){
        UiTestingUtils.sleep(500)
        validateOvenIconVisibleNearRecipeName()
        validateRecipeNameVisible()
        validateRemainingTimeVisible()
        validateRecipeNameTextColor()
        validateRecipeNameTextSize()
        validateSetLowerOvenButtonVisible()
        performClickOnThreeDotsIconButton()
        validateMoreOptionsPopupVisible()
        moreOptionsPopupTitleTextValidation()
        moreOptionsPopupTitleTextColorValidation()
        moreOptionsPopupTitleTextSizeValidation()
        performClickOnUpperLeftButtonMoreOptionsPopup()
        validateChangeTempNumpadVisible()
        performClickOnBackButtonOnProbeTempKeyboard()
        performClickOnThreeDotsIconButton()
        performClickOnUpperRightButtonMoreOptionsPopup()
        validateChangeTempNumpadVisible()
        performClickOnBackButtonOnProbeTempKeyboard()
        performClickOnThreeDotsIconButton()
        performClickOnLowerLeftButtonMoreOptionsPopup()
        performClickOnThreeDotsIconButton()
        performClickOnLowerRightButtonMoreOptionsPopup()
        validateInstructionPopupVisible()
        performClickOkButtonMoreOptionPopup()
        performClickTurnOffButton()
    }
    fun statusScreenValidateForProbeOnCombo(){
        UiTestingUtils.sleep(500)
        validateOvenIconVisibleNearRecipeName()
        validateRecipeNameVisible()
        validateRemainingTimeVisible()
        validateRecipeNameTextColor()
        validateRecipeNameTextSize()
        validateSetMwoOvenButtonVisible()
        performClickOnThreeDotsIconButton()
        validateMoreOptionsPopupVisible()
        moreOptionsPopupTitleTextValidation()
        moreOptionsPopupTitleTextColorValidation()
        moreOptionsPopupTitleTextSizeValidation()
        performClickOnUpperLeftButtonMoreOptionsPopup()
        validateChangeTempNumpadVisible()
        performClickOnBackButtonOnProbeTempKeyboard()
        performClickOnThreeDotsIconButton()
        performClickOnUpperRightButtonMoreOptionsPopup()
        validateChangeTempNumpadVisible()
        performClickOnBackButtonOnProbeTempKeyboard()
        performClickOnThreeDotsIconButton()
        performClickOnLowerLeftButtonMoreOptionsPopup()
        performClickOnThreeDotsIconButton()
        performClickOnLowerRightButtonMoreOptionsPopup()
        validateInstructionPopupVisible()
        performClickOkButtonMoreOptionPopup()
        performClickTurnOffButton()
    }
    fun statusScreenValidateForProbeOnSingle(){
        UiTestingUtils.sleep(500)
        validateRecipeNameVisible()
        validateRemainingTimeVisible()
        validateRecipeNameTextColor()
        validateRecipeNameTextSize()
        performClickOnThreeDotsIconButton()
        validateMoreOptionsPopupVisible()
        moreOptionsPopupTitleTextValidation()
        moreOptionsPopupTitleTextColorValidation()
        moreOptionsPopupTitleTextSizeValidation()
        performClickOnUpperLeftButtonMoreOptionsPopup()
        validateChangeTempNumpadVisible()
        performClickOnBackButtonOnProbeTempKeyboard()
        performClickOnThreeDotsIconButton()
        performClickOnUpperRightButtonMoreOptionsPopup()
        validateChangeTempNumpadVisible()
        performClickOnBackButtonOnProbeTempKeyboard()
        performClickOnThreeDotsIconButton()
        performClickOnLowerRightButtonMoreOptionsPopup()
        validateMoreOptionsInstructionPopupVisible()
        performClickOkButtonMoreOptionPopup()
        performClickOnThreeDotsIconButton()
        performClickOnLowerLeftButtonMoreOptionsPopup()
        performClickTurnOffButton()
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
        UiTestingUtils.sleep(5000)
    }
    fun performClickOnUpperRightButtonMoreOptionsPopup(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.moreCycleOptionsRecycler,1)
    }
    fun performClickOnLowerRightButtonMoreOptionsPopup(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.moreDefaultOptionsRecycler,1)
    }

    fun validateInstructionPopupVisible(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(withId(R.id.popup_with_scroll))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun performClickOkButtonMoreOptionPopup(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withText(R.string.text_button_ok))
            .check(ViewAssertions.matches(Matchers.notNullValue()));
        Espresso.onView(ViewMatchers.withText(R.string.text_button_ok))
            .perform(ViewActions.click())
        UiTestingUtils.sleep(1000)
    }

    fun insertProbePopupVisible(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
    }
    fun probeDetectedInUpperCavityPopupVisible(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
    }

    fun insertProbePopupValidation(){
        Espresso.onView(withId(R.id.text_view_title))
            .check { view, _ -> checkText("Insert Probe", view) }

        Espresso.onView(withId(R.id.text_view_description))
            .check { view, _ -> checkText("Insert probe as shown. Preheating is not necessary.", view) }
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
        val desiredDescriptionTextSizeSp = 30f
        val descriptionSizeInPixels = TestingUtils.spToPx(context, desiredDescriptionTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), descriptionSizeInPixels.toFloat()
        )
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
        UiTestingUtils.isViewVisible(R.id.image_view_text_center)
    }
    fun probeDetectedInUpperCavityPopupValidation(){
        Espresso.onView(withId(R.id.text_view_title))
            .check { view, _ -> checkText("Probe Detected", view) }

        Espresso.onView(withId(R.id.text_view_description))
            .check { view, _ -> checkText("Set up Probe Modes? If not please remove the probe to resume standard oven mode.", view) }
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
        val desiredDescriptionTextSizeSp = 30f
        val descriptionSizeInPixels = TestingUtils.spToPx(context, desiredDescriptionTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), descriptionSizeInPixels.toFloat()
        )
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
//        UiTestingUtils.isViewVisible(R.id.image_view_header_center)
    }
    fun probeDetectedInLowerCavityPopupValidation(){
        Espresso.onView(withId(R.id.text_view_title))
            .check { view, _ -> checkText("Probe Detected", view) }

        Espresso.onView(withId(R.id.text_view_description))
            .check { view, _ -> checkText("Set up Probe Modes? If not please remove the probe to resume standard oven mode.", view) }
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
        val desiredDescriptionTextSizeSp = 30f
        val descriptionSizeInPixels = TestingUtils.spToPx(context, desiredDescriptionTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), descriptionSizeInPixels.toFloat()
        )
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }
    fun probeRemovedPopupValidation(){
        Espresso.onView(withId(R.id.text_view_title))
            .check { view, _ -> checkText("Probe Removed", view) }

        Espresso.onView(withId(R.id.text_view_description))
            .check { view, _ -> checkText("Insert probe to resume cooking.", view) }
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
        val desiredDescriptionTextSizeSp = 30f
        val descriptionSizeInPixels = TestingUtils.spToPx(context, desiredDescriptionTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), descriptionSizeInPixels.toFloat()
        )
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }

    fun probeDetectedPopupValidation(){
        Espresso.onView(withId(R.id.text_view_description))
            .check { view, _ -> checkText("Set up Probe Modes? If not please remove the probe to resume standard oven mode.", view) }
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
        val desiredDescriptionTextSizeSp = 30f
        val descriptionSizeInPixels = TestingUtils.spToPx(context, desiredDescriptionTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), descriptionSizeInPixels.toFloat()
        )
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }
    fun performClickOnYesButton(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.text_button_right)
    }
    fun performClickOnNoButton(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.text_button_left)
    }
    fun validateMoreOptionsInstructionPopupVisible(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(withId(R.id.scroll_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}