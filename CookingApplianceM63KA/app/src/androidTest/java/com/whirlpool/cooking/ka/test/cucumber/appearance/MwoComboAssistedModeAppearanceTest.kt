package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndex
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest

class MwoComboAssistedModeAppearanceTest {
    val context: Context = ApplicationProvider.getApplicationContext()

    fun selectAssistedCookingOpt(){
        UiTestingUtils.sleep(1000)
        TestingUtils.withRecyclerViewScrollToTargetTextAndClick(R.id.tumblerString, "Assisted Cooking")
        UiTestingUtils.sleep(1000)
    }

    fun isRecipeGridVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.recycler_view_grid_list)
    }

    fun recipeSelectionGridTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Food Type")
    }

    fun recipeSelectionGridTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")
    }

    fun recipeSelectionGridTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )
    }

    fun performClickOnBackButtonOnRecipeGridView(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun scrollToTextAndClick(targetText: String) {
        UiTestingUtils.sleep(1500)
        TestingUtils.funWithGridViewScrollToTargetTextAndClick(targetText)
        UiTestingUtils.sleep(1500)
    }

    fun isOvenIconVisibleOnRecipeGridView(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
    }

    fun performClickOnRecipeAtIndex(index:Int){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_grid_list, index)
    }

    fun recipeNameTitleTextValidation(recipeName:String) {
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, recipeName)
    }

    fun performClickOnChickenNugget(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(withIndex(withId(R.id.recycler_view_grid_list), 0)).perform(click())
    }

    fun isNoOfServingsTumblerVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBased)
    }

    fun servingsTumblerTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Serving")
    }
    fun tempTumblerTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Biscuits")
    }

    fun servingsTumblerSubTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.degrees_type, " Pieces")
    }
    fun tempTumblerSubTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.degrees_type, "Celsius")
    }

    fun servingsTumblerSubTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.degrees_type, "#ffffff")
    }
    fun tempTumblerSubTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.degrees_type, "#AAA5A1")
    }

    fun servingsTumblerSubTitleTextSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.degrees_type)), sizeInPixels.toFloat()
        )
    }

    fun isOvenIconVisibleOnServingsTumbler(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
    }

    fun performClickOnNumpadButtonOnServingsTumbler(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }

    fun performClickOnBackButtonOnServingsTumbler(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun isNextButtonVisibleOnServingsTumbler(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
    }
    fun isNextButtonEnabledOnServingsTumbler(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewEnabled(R.id.btnPrimary)
        UiTestingUtils.sleep(1500)
    }
    fun isNextButtonClickableOnServingsTumbler(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.btnPrimary)
        UiTestingUtils.sleep(1500)
    }

    fun validateServingTumblerIsScrollable(){
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerNumericBased))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(2))
        UiTestingUtils.sleep(2500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerNumericBased, 2)
        UiTestingUtils.sleep(1500)
    }
    fun isDonenessTumblerVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.tumblerString)
        UiTestingUtils.sleep(1500)
    }
    fun donenessTumblerTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Doneness")
    }
    fun validateDonenessTumblerIsScrollable(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerString, 2)
        UiTestingUtils.sleep(1500)
    }
    fun isPreviewScreenVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.recycler_view_preview)
        UiTestingUtils.sleep(1500)
    }
    fun performClickOnAmountSectionOnPreviewScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_preview, 0)
    }
    fun performClickOnDonenessSectionOnPreviewScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_preview, 1)
    }
    fun performClickOnNextButtonOnPreviewScreen(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.btnPrimary)
        UiTestingUtils.sleep(1500)
    }
    fun isCookingGuideVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.scroll_view_popup_info_text)
    }
    fun isCookingGuideImageVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.ivCookingGuide)
    }
    fun isCookingGuideTitleVisible(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
    }

    fun cookingGuideTitleTextValidation(){
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Cooking Guide")
    }
    fun cookingGuideTitleTextSizeValidation(){
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )    }
    fun cookingGuideTitleTextColorValidation(){
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")
    }
    fun isCookingGuideCloseIconVisible(){
        UiTestingUtils.isViewVisible(R.id.ivRightIcon)
    }
    fun performClickOnNextButtonOnCookingGuide(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.btnPrimary)
        UiTestingUtils.sleep(1500)
    }
    fun performClickOnLowerOvenBtn(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.lower_oven_layout)
    }
    fun statusScreenVisibleForAssistedCombo(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.singleStatusWidget)
    }
}