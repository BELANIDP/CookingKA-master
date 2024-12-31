package com.whirlpool.cooking.ka.test.cucumber.steps

import android.graphics.Color
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import android.presenter.fragments.mwo.ClockFragment
import com.whirlpool.cooking.ka.test.cucumber.appearance.HorizontalTumblerAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.navigation.setFragmentScenario
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HorizontalTumblerSteps {
    private var horizontalTumblerAppearanceTest: HorizontalTumblerAppearanceTest? = null

    @Before
    fun setUp() {
        horizontalTumblerAppearanceTest = HorizontalTumblerAppearanceTest()
    }

    @After
    fun tearDown() {
        horizontalTumblerAppearanceTest = null
    }

    @And("I navigate to horizontal tumbler test screen")
    fun navigationTohorizontalTumbler() {
        horizontalTumblerAppearanceTest?.navigateToTumblerTestScreen();
    }

    @Then("Horizontal tumbler test Screen will be visible")
    fun horizontalTumblerTestIsVisible() {
        horizontalTumblerAppearanceTest?.horizontalTumblerTestIsVisible();
    }

    @Then("Click on tumbler test button")
    fun clickOnTumblerTestButton() {
        horizontalTumblerAppearanceTest?.navigateToTumblerScreen()
    }

    @Then("Horizontal tumbler Screen will be visible")
    fun horizontalTumblerViewVisible() {
        horizontalTumblerAppearanceTest?.horizontalTumblerIsVisible();
    }

    @Then("I verify tumbler height is proper")
    fun verifyTumblerHeight() {
        horizontalTumblerAppearanceTest?.checkTumblerHeight()
    }

    @Then("I verify tumbler width is proper")
    fun verifyTumblerWidth() {
        horizontalTumblerAppearanceTest?.checkTumblerWidth();
    }

    @Then("I verify that the padding of tumbler container is proper")
    fun checkIsContainerPaddingProper() {
        horizontalTumblerAppearanceTest?.checkIsContainerPaddingProper()
    }

    @Then("I verify that the padding of tumbler container is not proper")
    fun checkIsContainerPaddingProperNotProper() {
        horizontalTumblerAppearanceTest?.checkIsContainerPaddingProperNotProper()
    }

    @Then("I verify that text size of tumbler container is proper")
    fun checkFontSizeOfTumblerText(textView: Int) {
        horizontalTumblerAppearanceTest?.validateTextViewSize(
            R.id.tumblerNumericBased,
            com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            3,
            40
        )
    }

    @Then("I verify that the tumbler item has visible title text")
    fun checkIfItemHasVisibleTitleText() {
        val isVisible = horizontalTumblerAppearanceTest?.isTextViewVisibleInItem(
            R.id.tumblerNumericBased,
            com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            false,
            0
        )
        assert(isVisible == true) { "The item does not have visible title text" }
    }

    @Then("I verify that the tumbler title text of the item is hidden")
    fun checkIfTitleTextOfItemIsHidden() {
        val isVisible = horizontalTumblerAppearanceTest?.isTextViewVisibleInItem(
            R.id.tumblerNumericBased,
            com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            true,
            0
        )
        assert(isVisible == true) { "The item does not have hidden title text" }
    }

    @Then("I verify that the tumbler item title text size is proper")
    fun checkIsTitleTextSize() {
        val desiredTextSizeSp = 36f
        val expectedTextSize =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        val isValid = horizontalTumblerAppearanceTest?.validateTextViewSize(
            viewId = R.id.tumblerNumericBased,
            childId = com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            position = 0,
            expectedTextSize = expectedTextSize
        )
        assert(isValid == true) { "The title text size is as expected" }
    }

    @Then("I verify that the tumbler item title text size is not proper")
    fun checkIsTextSizeNotProper() {
        val expectedTextSize = 1000
        val isValid = horizontalTumblerAppearanceTest?.validateTextViewSize(
            viewId = R.id.tumblerNumericBased,
            childId = com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            position = 0,
            expectedTextSize = expectedTextSize
        ) ?: false
        assert(!isValid == true) { "The title text size is as expected" }
    }

    @Then("I verify that the tumbler item title text color proper")
    fun checkIsTitleTextColor() {
        val expectedColor = Color.parseColor("#6d6d6d")
        val isValid = horizontalTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerNumericBased,
            childId = com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            position = 0,
            expectedColor = expectedColor
        )
        assert(isValid == true) { "The title text color is as expected" }
    }

    @Then("I verify that the tumbler item title text color is not proper")
    fun checkIsTitleTextColorNotProper() {
        val expectedColor = Color.parseColor("#6d6d6d")
        val isValid = horizontalTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerNumericBased,
            childId = com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            position = 0,
            expectedColor = expectedColor
        ) ?: false
        assert(!isValid) { "The title text color is not as expected" }
    }

    @Then("I verify that the tumbler scroll left")
    fun scrollLeft() {
        horizontalTumblerAppearanceTest?.horizontalScrollLeft()
    }

    @Then("I verify that the tumbler scroll right")
    fun scrollRight() {
        horizontalTumblerAppearanceTest?.horizontalScrollRight()
    }

    @Then("I verify that tumbler first item is exist")
    fun checkIfFirstItemIsExist() {
        horizontalTumblerAppearanceTest?.checkIfFirstItemIsExist(R.id.tumblerNumericBased)
    }

    @Then("I verify that the tumbler selected item text color is proper")
    fun checkTextColorIsNotGrey() {
        val expectedColor = Color.parseColor("#ffffff")
        val isValid = horizontalTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerNumericBased,
            childId = com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            position = 0,
            expectedColor = expectedColor
        ) ?: false
        assert(!isValid) { "The title text color is not as expected" }
    }

    @Then("I navigate to clock screen for horizontal tumbler screen")
    fun navigateToClockScreenForListViewScreen() {
        testNavigationToClockViewScreenForScrollViewScreen()
    }

    @Test
    @UiThreadTest
    fun testNavigationToClockViewScreenForScrollViewScreen() {
        setFragmentScenario(ClockFragment::class.java)
    }

    @Test
    @UiThreadTest
    fun testNavigationToClockScreen() {
//        setFragmentScenario(TumblerTestFragment::class.java)
    }
}