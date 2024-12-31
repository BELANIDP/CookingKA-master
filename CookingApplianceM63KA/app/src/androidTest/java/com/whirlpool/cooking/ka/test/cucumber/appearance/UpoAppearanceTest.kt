package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.app.ActionBar
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkText
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import core.utils.HMILogHelper


class UpoAppearanceTest {

    val context = ApplicationProvider.getApplicationContext<Context>()

    fun preferencesrecyclerlistIsVisible() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.preferencesRecyclerList)
    }

    fun performClickShowMorePreferences() {
        UiTestingUtils.sleep(1000)
        val index = 3
        Espresso.onView(ViewMatchers.withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun preferencesrecyclerlistValidateView() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewClickable(R.id.preferencesRecyclerList)
        UiTestingUtils.isViewEnabled(R.id.preferencesRecyclerList)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)),
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
    }

    fun performClickOnTempCalibrationOpt(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(6))
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.preferencesRecyclerList, 6)
    }
    fun scrollOnTempCalibrationOpt(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(5))
        UiTestingUtils.sleep(1000)
    }

    fun isCavitySelectionScreenVisible(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.upper_oven_layout)
    }

    fun defaultSubtextValidation(){
        UiTestingUtils.sleep(1000)
        onView(ViewMatchers.withId(R.id.uppper_cavity_lbl)).check { view, _ -> checkText("UPPER OVEN", view) }
        onView(ViewMatchers.withId(R.id.upper_cavity_subtext)).check { view, _ -> checkText("Default", view) }
        onView(ViewMatchers.withId(R.id.lower_cavity_lbl)).check { view, _ -> checkText("LOWER OVEN", view) }
        onView(ViewMatchers.withId(R.id.lower_cavity_subtext)).check { view, _ -> checkText("Default", view) }
        val desiredLabelTextSizeSp = 32f
        val sizeInPixelsForLabel = TestingUtils.spToPx(context, desiredLabelTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.uppper_cavity_lbl)), sizeInPixelsForLabel.toFloat()
        )
        val desiredSubTextSizeSp = 30f
        val sizeInPixelsForSubtext = TestingUtils.spToPx(context, desiredSubTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.upper_cavity_subtext)), sizeInPixelsForSubtext.toFloat()
        )
        TestingUtils.checkTextColorValidation(R.id.uppper_cavity_lbl, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.lower_cavity_lbl, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.upper_cavity_subtext, "#ffffff")
        TestingUtils.checkTextColorValidation(R.id.lower_cavity_subtext, "#ffffff")
    }

    fun leftButtonClickValidation(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }
    fun setButtonClickValidation(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.btnPrimary)
    }

    fun cavitySelectionButtonValidation(){
        UiTestingUtils.sleep(1000)
        val desiredHeightInSp = 88f
        val heightInPixels = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 784f
        val widthInPixels = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.upper_oven_layout)
            ), widthInPixels, heightInPixels
        )
    }

    fun tempCalibrationTumblerVisible(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBased)
    }

    fun tempCalibrationTumblerTitleTextValidation(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Temperature Calibration")
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )
    }
    fun tempCalibrationTumblerSubTextValidation(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isTextMatchingAndFitting(R.id.ifTooCool, "If too cool")
        UiTestingUtils.isTextMatchingAndFitting(R.id.ifTooHot, "If too hot")
        TestingUtils.checkTextColorValidation(R.id.ifTooCool, "#AAA5A1")
        TestingUtils.checkTextColorValidation(R.id.ifTooHot, "#AAA5A1")
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.ifTooCool)), sizeInPixels.toFloat()
        )
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.ifTooHot)), sizeInPixels.toFloat()
        )
        UiTestingUtils.isViewVisible(R.id.divider1)
    }

    fun tempCalibrationTumblerScrollValidation(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerNumericBased))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(7))
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerNumericBased, 7)
        UiTestingUtils.sleep(2000)
    }
    fun defaultTempCalibrationTumblerScrollValidation(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerNumericBased))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(5))
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerNumericBased, 5)
        UiTestingUtils.sleep(2000)
    }

    fun upperCavitySelectionButtonSubtextModified(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isTextMatchingAndFitting(R.id.upper_cavity_subtext, "+6°")
    }
    fun lowerCavitySelectionButtonSubtextModified(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isTextMatchingAndFitting(R.id.lower_cavity_subtext, "+6°")
    }
    fun targetTempValidate(): UpoAppearanceTest? {
        val cookingVM = CookingViewModelFactory.getPrimaryCavityViewModel()
        val targetTemp =cookingVM.recipeExecutionViewModel.targetTemperature.value?.toInt()
        HMILogHelper.Logi("Target Temp is-->{$targetTemp}")
        return if (targetTemp == 181){
            this
        } else{
            null
        }
    }

    fun validateSubtextOnTempCalibrationOpt(){
        UiTestingUtils.sleep(1000)
        onView(UiTestingUtils.matchRecyclerViewItem(R.id.preferencesRecyclerList, 5, R.id.list_item_right_text_view))
            .check { view, _ -> checkText("Default", view) }
    }
    fun validateSubtextOnTempCalibrationOptModified(){
        UiTestingUtils.sleep(2000)
        onView(UiTestingUtils.matchRecyclerViewItem(R.id.preferencesRecyclerList, 5, R.id.list_item_right_text_view))
            .check { view, _ -> checkText("+6°", view) }
    }
}