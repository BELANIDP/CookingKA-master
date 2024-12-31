package com.whirlpool.cooking.ka.test.cucumber.steps

import android.graphics.Color
import androidx.test.annotation.UiThreadTest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.appearance.HeaderFragmentAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.dpToPx
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.spToPx
import com.whirlpool.cooking.ka.test.cucumber.navigation.setFragmentScenario
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HeaderbarSteps {
    private var headerFragmentAppearanceTest: HeaderFragmentAppearanceTest? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        headerFragmentAppearanceTest = HeaderFragmentAppearanceTest()
    }

    @After
    fun tearDown() {
        headerFragmentAppearanceTest = null
    }

    @Then("I see test widget button")
    fun seeTestWidget() {
//        UiTestingUtils.isViewVisible(R.id.btn_test)

    }

    @Then("I click test widget button")
    @UiThreadTest
    fun clickOnTestWidget() {
//        onView(withId(R.id.btn_test)).perform(click())
    }

    @Then("I click on Headerbar button")
    fun clickOnHeaderbar() {
//        UiTestingUtils.performClick(R.id.btnHeaderBar)
    }

    @Then("I see Test Buttons Screen")
    fun iSeeTestBtnScreen() {
    }

    @Then("I see Test Fragment")
    fun testFragmentVisible() {
//        UiTestingUtils.isViewVisible(R.id.testFragment)
    }

    @Then("I see Headerbar views")
    @Throws(Exception::class)
    fun checkAllViewsVisibility() {
        headerFragmentAppearanceTest?.checkAllViewsVisibility()
    }

    @Then("I click on Left Icon Single Header")
    fun checkLeftIconSingleHeaderClickable() = run {
        UiTestingUtils.sleep(1500)
        LeakAssertions.assertNoLeaks()
        headerFragmentAppearanceTest?.ivLeftIconSingleHeaderIsViewClickable()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I click on Left Icon Double Header")
    fun checkLeftIconDoubleHeaderClickable() {
        headerFragmentAppearanceTest?.ivLeftIconDoubleHeaderIsViewClickable()
    }

    @Then("I click on Right Icon Double Header")
    fun checkRightIconDoubleHeaderClickable() {
        headerFragmentAppearanceTest?.ivRightIconDoubleHeaderIsViewClickable()
    }

    @Then("I see Left Icon Single Header")
    fun checkLeftIconSingleHeaderVisible() {
        headerFragmentAppearanceTest?.ivLeftIconSingleHeaderIsVisible()

    }

    @Then("I see Left Icon Double Header")
    fun checkLeftIconDoubleHeaderVisible() {
        headerFragmentAppearanceTest?.ivLeftIconDoubleHeaderIsVisible()

    }

    @Then("I see Oven Icon")
    fun checkOvenCavityVisible() {
        headerFragmentAppearanceTest?.ivOvenCavityIsVisible()
    }

    @Then("I click on Oven Icon")
    fun checkOvenIconClickable() {
        headerFragmentAppearanceTest?.ivOvenCavityIsViewClickable()

    }

    @Then("I click on Info Icon")
    fun checkInfoIconClickable() {
        headerFragmentAppearanceTest?.ivInfoIsViewClickable()
    }

    @Then("I see Info Icon")
    fun checkInfoIconVisible() {
        headerFragmentAppearanceTest?.ivInfoIsVisible()
    }

    @Then("I see Status Icon 1")
    fun checkStatusIcon1Visible() {
        headerFragmentAppearanceTest?.ivStatusIcon1IsVisible()
    }

    @Then("I see Status Icon 2")
    fun checkStatusIcon2Visible() {
        headerFragmentAppearanceTest?.ivStatusIcon2IsVisible()
    }

    @Then("I see Status Icon 3")
    fun checkStatusIcon3Visible() {
        headerFragmentAppearanceTest?.ivStatusIcon3IsVisible()
    }

    @Then("I see Status Icon 4")
    fun checkStatusIcon4Visible() {
        headerFragmentAppearanceTest?.ivStatusIcon4IsVisible()
    }

    @Then("I see horizontal line")
    fun checkHorizontalLineVisible() {
        headerFragmentAppearanceTest?.ivHorizontalLineIsVisible()
    }

    @Then("I see Time")
    fun checkTimeVisible() {
        headerFragmentAppearanceTest?.clockTextViewIsVisible()
    }

    @Then("I click on Right Icon Single Header")
    fun checkRightIconClickable() {
        headerFragmentAppearanceTest?.ivRightIconSingleHeaderIsViewClickable()
    }

    @Then("I see Right Icon Single Header")
    fun checkRightIconSingleHeaderVisible() {
        headerFragmentAppearanceTest?.ivRightIconSingleHeaderIsVisible()
    }

    @Then("I see Right Icon Double Header")
    fun checkRightIconDoubleHeaderVisible() {
        headerFragmentAppearanceTest?.ivRightIconDoubleHeaderIsVisible()
    }

    @Then("I see Title text Single Header")
    fun titleTextSingleHeaderVisible() {
        headerFragmentAppearanceTest?.tvTitleSingleHeaderIsVisible()
    }

    @Then("I see Title text Double Header")
    fun titleTextDoubleHeaderVisible() {
        headerFragmentAppearanceTest?.tvTitleDoubleHeaderIsVisible()
    }

    @Then("I validate Title size of Single Header")
    fun checkFontSizeOfTitleSingleHeader() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)), sizeInPixels.toFloat()
        )
    }

    @Then("I validate Title size of Double Header")
    fun checkFontSizeOfTitleDoubleHeader() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)), sizeInPixels.toFloat()
        )
    }

    @Then("I validate Title color of Double Header")
    fun tvTitleDoubleHeaderColorMatch() {
        GenericTextViewTest.checkMatchesTextColor(
            onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)),
            Color.parseColor("#ffffff")
        )
    }

    @Then("I validate Title color of Single Header")
    fun tvTitleSingleHeaderColorMatch() {
        GenericTextViewTest.checkMatchesTextColor(
            onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)),
            Color.parseColor("#ffffff")
        )
    }

    @Then("I validate Title Font Family of Single Header")
    fun tvTitleSingleHeaderFontFamilyMatch() {
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)), typeface
        )
    }

    @Then("I validate Title Font Family of Double Header")
    fun tvTitleDoubleHeaderFontFamilyMatch() {
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)), typeface
        )
    }

    @Then("I validate Size of Left Icon of Single Header")
    fun ivLeftIconSingleHeaderSizeValidate() {
        val desiredHeightInSp = 24f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 40f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.ivLeftIcon),
                    0
                )
            ), widthInPixels, heightInPixels
        )

    }

    @Then("I validate Size of Right Icon of Single Header")
    fun ivRightIconSingleHeaderSizeValidate() {
        val desiredHeightInSp = 30f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 30f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.ivRightIcon),
                    0
                )
            ), widthInPixels, heightInPixels
        )

    }

    @Then("I validate Size of Right Icon of Double Header")
    fun ivRightIconDoubleHeaderSizeValidate() {
        val desiredHeightInSp = 30f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 30f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.ivRightIcon),
                    1
                )
            ), widthInPixels, heightInPixels
        )

    }

    @Then("I validate Size of Left Icon of Double Header")
    fun ivLeftIconDoubleHeaderSizeValidate() {
        val desiredHeightInSp = 24f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 40f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.ivLeftIcon),
                    1
                )
            ), widthInPixels, heightInPixels
        )
    }

    @Then("I validate Size of Oven Icon")
    fun ivOvenIconSizeValidate() {
        val desiredHeightInSp = 33f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 34f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.ivOvenCavity),
                    1
                )
            ), widthInPixels, heightInPixels
        )
    }

    @Then("I validate Size of Info Icon")
    fun ivInfoIconSizeValidate() {
        val desiredHeightInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 40f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(TestingUtils.withIndex(withId(R.id.ivInfo), 1)),
            widthInPixels,
            heightInPixels
        )
    }

    @Then("I validate Size of Status Icon")
    fun ivStatusIconSizeValidate() {
        val desiredHeightInSp = 32f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 32f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivStatusIcon1)),
            widthInPixels,
            heightInPixels
        )
    }

    @Then("I validate Size of Horizontal Line")
    fun ivHorizontalLineSizeValidate() {
        val desiredHeightInSp = 4f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 210f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivHorizontalLine)),
            widthInPixels,
            heightInPixels
        )
    }

    @Then("I validate Size of clock view")
    fun tvClockViewSizeValidate() {
        val desiredHeightInSp = 32f
        val heightInPixels = spToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val desiredWidthInSp = 128f
        val widthInPixels = spToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.clockTextView)),
            widthInPixels,
            heightInPixels
        )
    }


    @Then("I navigate to header screen")
    fun navigateToHeaderScreen() {
        /*setFragmentScenario(
            HeaderBarTestFragment::class.java
        )*/
    }

    @Given("App has started for header screen")
    fun headerScreenLaunch() {

    }
}