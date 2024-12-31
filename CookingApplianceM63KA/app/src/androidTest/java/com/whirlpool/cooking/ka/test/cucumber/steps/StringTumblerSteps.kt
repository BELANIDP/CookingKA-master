package com.whirlpool.cooking.ka.test.cucumber.steps

import android.graphics.Color
import android.presenter.fragments.combooven.ClockFragment
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.appearance.StringTumblerAppearanceTest
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
class StringTumblerSteps {
    private var stringTumblerAppearanceTest: StringTumblerAppearanceTest? = null

    @Before
    fun setUp() {
        stringTumblerAppearanceTest = StringTumblerAppearanceTest()
    }
    @After
    fun tearDown() {
        stringTumblerAppearanceTest = null
    }

    @And("I navigate to test widget screen")
    fun navigationToStringTumbler() {
        stringTumblerAppearanceTest?.navigateToTumblerTestScreen();
    }
    @Then("I navigate to String tumbler screen")
    fun  horizontalTumblerTestIsVisible() {
        stringTumblerAppearanceTest?.navigateToTumblerScreen();
    }


    @Then("String tumbler tumbler Screen will be visible")
    fun stringTumblerViewVisible() {
        stringTumblerAppearanceTest?.stringTumblerIsVisible();
    }
    @Then("I verify String tumbler height is proper")
    fun verifyTumblerHeight()
    {
        stringTumblerAppearanceTest?.checkTumblerHeight()
    }

    @Then("I verify String tumbler width is proper")
    fun verifyTumblerWidth() {
        stringTumblerAppearanceTest?.checkTumblerWidth();
    }
    @Then("I verify that the padding of String tumbler container is proper")
    fun checkIsContainerPaddingProper() {
        stringTumblerAppearanceTest?.checkIsContainerPaddingProper()
    }
    @Then("I verify that the padding of String tumbler container is not proper")
    fun checkIsContainerPaddingProperNotProper() {
        stringTumblerAppearanceTest?.checkIsContainerPaddingProperNotProper()
    }



    @Then("I verify that the string tumbler scroll left")
    fun scrollLeft()
    {
        stringTumblerAppearanceTest?.horizontalScrollLeft()
    }

    @Then("I verify that the string tumbler scroll right")
    fun scrollRight()
    {
        stringTumblerAppearanceTest?.horizontalScrollRight()
    }


    @Then("I verify that text size of String tumbler container is proper")
    fun checkFontSizeOfTumblerText( textView: Int) {
        stringTumblerAppearanceTest?.validateTextViewSize(R.id.tumblerString, R.id.title,0,40)
    }
    @Then("I verify that the String tumbler item has visible title text")
    fun checkIfItemHasVisibleTitleText() {
        val isVisible = stringTumblerAppearanceTest?.isTextViewVisibleInItem(
            R.id.tumblerString, R.id.title,
            false,
            0
        )
        assert(isVisible == true) { "The item does not have visible title text" }
    }


    @Then(" I verify that the String tumbler item has visible sub text")
    fun checkIfItemHasVisibleSubText() {
        val isVisible = stringTumblerAppearanceTest?.isTextViewVisibleInItem(
            R.id.tumblerString, R.id.sub_title,
            false,
            0
        )
        assert(isVisible == true) { "The item does not have visible title text" }
    }

    @Then("I verify that the String tumbler title text of the item is hidden")
    fun checkIfTitleTextOfItemIsHidden() {
        val isVisible = stringTumblerAppearanceTest?.isTextViewVisibleInItem(
            R.id.tumblerString,
            R.id.title,
            true,
            0
        )
        assert(isVisible == true) { "The item does not have hidden title text" }
    }

    @Then("I verify that the string tumbler item title text size is proper")
    fun checkIsTitleTextSizeProper() {
        val desiredTextSizeSp = 80f
        val expectedTextSize =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        val isValid = stringTumblerAppearanceTest?.validateTextViewSize(
            viewId = R.id.tumblerString,
            childId = R.id.title,
            position = 0,
            expectedTextSize = expectedTextSize
        )
        assert(isValid == true) { "The title text size is as expected" }
    }


    @Then("I verify that the string tumbler item sub text size is proper")
    fun checkIsSubTextSizeProper() {
        val desiredTextSizeSp = 36f
        val expectedTextSize =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        val isValid = stringTumblerAppearanceTest?.validateTextViewSize(
            viewId = R.id.tumblerString,
            childId = R.id.sub_title,
            position = 0,
            expectedTextSize = expectedTextSize
        )
        assert(isValid == true) { "The title text size is as expected" }
    }



    @Then("I verify that the string tumbler item title text size is not proper")
    fun checkIsTextSizeNotProper() {
        val expectedTextSize = 1000
        val isValid = stringTumblerAppearanceTest?.validateTextViewSize(
            viewId = R.id.tumblerNumericBased,
            childId = com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            position = 0,
            expectedTextSize = expectedTextSize
        ) ?: false
        assert(!isValid == true) { "The title text size is as expected" }
    }

    @Then("I verify that the String tumbler item text color proper")
    fun checkIsTitleTextColor() {
        val expectedColor = Color.parseColor("#6d6d6d")
        val isValid = stringTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerString,
            childId = R.id.title,
            position = 0,
            expectedColor =expectedColor
        )
        assert(isValid == true) { "The title text color is as expected" }
    }

    @Then(" I verify that the String tumbler item sub text color proper")
    fun checkIsSubTextColor() {
        val expectedColor = Color.parseColor("#6d6d6d")
        val isValid = stringTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerString,
            childId = R.id.sub_title,
            position = 0,
            expectedColor =expectedColor
        )
        assert(isValid == true) { "The title text color is as expected" }
    }




    @Then("I verify that the String tumbler item text color is not proper")
    fun checkIsTitleTextColorNotProper() {
        val expectedColor = Color.parseColor("#6d6d6d")
        val isValid = stringTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerNumericBased,
            childId = com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            position = 0,
            expectedColor = expectedColor
        ) ?: false
        assert(!isValid) { "The title text color is not as expected" }
    }

    @Then("I verify that String tumbler first item is exist")
    fun checkIfFirstItemIsExist() {
        stringTumblerAppearanceTest?.checkIfFirstItemIsExist(R.id.tumblerNumericBased)
    }
    @Then("I verify that the String tumbler selected item text color is proper")
    fun checkTextColorIsNotGrey() {
        val expectedColor = Color.parseColor("#ffffff")
        val isValid = stringTumblerAppearanceTest?.validateTextViewColor(
            viewId = R.id.tumblerNumericBased,
            childId = com.whirlpool.hmi.cooking.R.id.base_tumbler_item_text_view,
            position = 0,
            expectedColor = expectedColor
        ) ?: false
        assert(!isValid) { "The title text color is not as expected" }
    }

    @Then("I navigate to clock screen for string tumbler screen")
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