package com.whirlpool.cooking.ka.test.cucumber.steps

import android.graphics.Color
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import android.presenter.fragments.mwo.ClockFragment
import com.whirlpool.cooking.ka.test.cucumber.appearance.VerticalTumblerAppearanceTest
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
class VerticalTumblerSteps {
    private var verticalTumblerAppearanceTest: VerticalTumblerAppearanceTest? = null

    @Before
    fun setUp() {
        verticalTumblerAppearanceTest = VerticalTumblerAppearanceTest()
    }
    @After
    fun tearDown() {
        verticalTumblerAppearanceTest = null
    }

    @And("I navigate to test widget screen for vertical tumbler")
    fun navigationToTestWidgetScreen() {
        verticalTumblerAppearanceTest?.navigateToTumblerTestScreen();
    }
    @Then("I navigate to vertical tumbler screen")
    fun  horizontalTumblerTestIsVisible() {
        verticalTumblerAppearanceTest?.performVerticalTumblerClick();
    }

    @Then("Vertical tumbler tumbler Screen will be visible")
    fun stringTumblerViewVisible() {
        verticalTumblerAppearanceTest?.verticalTumblerIsVisible();
    }


    @Then("I verify left tumbler height is proper")
    fun verifyTumblerHeight()
    {
        verticalTumblerAppearanceTest?.checkTumblerHeight(R.id.tumblerLeft)
    }

    @Then("I verify left tumbler width is proper")
    fun verifyTumblerWidth() {
        verticalTumblerAppearanceTest?.checkTumblerWidth(R.id.tumblerLeft);
    }

    @Then("I verify that the padding of left tumbler container is proper")
    fun checkIsContainerPaddingProper() {
        verticalTumblerAppearanceTest?.checkIsContainerPaddingProper(R.id.tumblerLeft)
    }
    @Then("I verify that the padding of left tumbler container is not proper")
    fun checkIsContainerPaddingProperNotProper() {
        verticalTumblerAppearanceTest?.checkIsContainerPaddingProperNotProper(R.id.tumblerLeft)
    }

    @Then(" I verify that the left tumbler scroll top")
    fun scrollTop()
    {
        verticalTumblerAppearanceTest?.verticalScrollTop(R.id.tumblerLeft)
    }

    @Then("I verify that the left tumbler scroll bottom")
    fun scrollBottom()
    {
        verticalTumblerAppearanceTest?.verticalScrollBottom(R.id.tumblerLeft)
    }

    @Then("I verify that text size of left tumbler container is proper")
    fun checkFontSizeOfTumblerText() {
        verticalTumblerAppearanceTest?.validateTextViewSize(R.id.tumblerLeft, R.id.vertical_tumbler_item,0,80)
    }
    @Then("I verify that the left tumbler item has visible title text")
    fun checkIfItemHasVisibleTitleText() {
        val isVisible = verticalTumblerAppearanceTest?.isTextViewVisibleInItem(
            R.id.tumblerLeft, R.id.vertical_tumbler_item,
            false,
            0
        )
        assert(isVisible == true) { "The item does not have visible title text" }
    }

    @Then("I verify that the left tumbler title text of the item is hidden")
    fun checkIfTitleTextOfItemIsHidden() {
        val isVisible = verticalTumblerAppearanceTest?.isTextViewVisibleInItem(
            R.id.tumblerLeft,
            R.id.vertical_tumbler_item,
            true,
            0
        )
        assert(isVisible == true) { "The item does not have hidden title text" }
    }

    @Then("I verify that the left tumbler item title text size is proper")
    fun checkIsTitleTextSizeProper() {
        val desiredTextSizeSp = 36f
        val expectedTextSize =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        val isValid = verticalTumblerAppearanceTest?.validateTextViewSize(
            viewId = R.id.tumblerLeft,
            childId = R.id.vertical_tumbler_item,
            position = 0,
            expectedTextSize = expectedTextSize
        )
        assert(isValid == true) { "The title text size is as expected" }
    }


    @Then("I verify that the left tumbler item title text size is not proper")
    fun checkIsTextSizeNotProper() {
        val expectedTextSize = 1000
        val isValid = verticalTumblerAppearanceTest?.validateTextViewSize(
            viewId = R.id.tumblerLeft,
            childId = R.id.vertical_tumbler_item,
            position = 0,
            expectedTextSize = expectedTextSize
        ) ?: false
        assert(!isValid == true) { "The title text size is as expected" }
    }

    @Then("I verify that left tumbler first item is exist")
    fun checkIfFirstItemIsExist() {
        verticalTumblerAppearanceTest?.checkIfFirstItemIsExist(R.id.tumblerLeft)
    }

    @Then("I verify that the left tumbler item text color proper")
    fun checkIsTitleTextColor() {
        val expectedColor = Color.parseColor("#6d6d6d")
        val isValid = verticalTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerLeft,
            childId = R.id.vertical_tumbler_item,
            position = 0,
            expectedColor =expectedColor
        )
        assert(isValid == true) { "The title text color is as expected" }
    }

    @Then("I verify that the left tumbler item text color is not proper")
    fun checkIsTitleTextColorNotProper() {
        val expectedColor = Color.parseColor("#6d6d6d")
        val isValid = verticalTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerLeft,
            childId = R.id.vertical_tumbler_item,
            position = 0,
            expectedColor = expectedColor
        ) ?: false
        assert(!isValid) { "The title text color is not as expected" }
    }


    @Then("I verify that the left tumbler selected item text color is proper")
    fun checkTextColorIsNotGrey() {
        val expectedColor = Color.parseColor("#ffffff")
        val isValid = verticalTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerLeft,
            childId = R.id.vertical_tumbler_item,
            position = 0,
            expectedColor = expectedColor
        ) ?: false
        assert(!isValid) { "The title text color is not as expected" }
    }
    @Then("I navigate to clock screen for vertical tumbler screen")
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
//        setFragmentScenario(TestWidgetFragment::class.java)
    }

}