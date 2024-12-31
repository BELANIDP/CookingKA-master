package com.whirlpool.cooking.ka.test.cucumber.steps

import android.graphics.Color
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import android.presenter.fragments.mwo.ClockFragment
import com.whirlpool.cooking.ka.test.cucumber.appearance.ListItemAppearanceTest
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
class ListItemSteps {

    private var listItemAppearanceTest: ListItemAppearanceTest? = null

    @Before
    fun setUp() {
        listItemAppearanceTest = ListItemAppearanceTest()
    }

    @After
    fun tearDown() {
        listItemAppearanceTest = null
    }


    @Then("List View Screen will be visible")
    fun listviewVisible() {
        listItemAppearanceTest?.recyclerViewListIsVisible();
    }


    @And("I navigate to List View")
    fun navigateToListView() {
//        setFragmentScenario(ListViewTestFragment::class.java)
    }


    @Then("I verify that the content of first item is exist")
    fun checkIfFirstItemIsExist() {
        listItemAppearanceTest?.checkIfFirstItemIsExist(R.id.recycler_view_list)
    }


    @Then("I verify that the item has visible title text")
    fun checkIfItemHasVisibleTitleText() {
        val isVisible = listItemAppearanceTest?.isTextViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_title_text_view,
            false,
            0
        )
        assert(isVisible == true) { "The item does not have visible title text" }
    }

    @Then("I verify that the title text of the item is hidden")
    fun checkIfTitleTextOfItemIsHidden() {
        val isVisible = listItemAppearanceTest?.isTextViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_title_text_view,
            true,
            0
        )
        assert(isVisible == true) { "The item does not have hidden title text" }
    }

    @Then("I verify that the item has visible sub text")
    fun checkIfItemHasVisibleSubText() {
        val isVisible = listItemAppearanceTest?.isTextViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_sub_text_view,
            false,
            0
        )
        assert(isVisible == false) { "The item does not have visible sub text" }
    }

    @Then("I verify that the title sub of the item is hidden")
    fun checkIfSubTextOfItemIsHidden() {
        val isVisible = listItemAppearanceTest?.isTextViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_sub_text_view,
            true,
            0
        )
        assert(isVisible == false) { "The item does not have hidden sub text" }
    }

    @Then("I verify that the item has visible right text")
    fun checkIfItemHasVisibleRightText() {
        val isVisible = listItemAppearanceTest?.isTextViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_right_text_view,
            false,
            0
        )
        assert(isVisible == false) { "The item does not have visible right text" }
    }

    @Then("I verify that the title right of the item is hidden")
    fun checkIfRightTextOfItemIsHidden() {
        val isVisible = listItemAppearanceTest?.isTextViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_right_text_view,
            true,
            0
        )
        assert(isVisible == false) { "The item does not have hidden right text" }
    }

    @Then("I verify that the item has visible Radio button")
    fun checkIfItemHasVisibleRadioButton() {
        val isVisible = listItemAppearanceTest?.isRadioButtonVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_radio_button,
            false,
            0
        )
        assert(isVisible == false) { "The item does not have visible Radio button" }
    }

    @Then("I verify that the Radio button of the item is hidden")
    fun checkIfRadioButtonOfItemIsHidden() {
        val isVisible = listItemAppearanceTest?.isRadioButtonVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_radio_button,
            true,
            0
        )
        assert(isVisible == false) { "The item does not have hidden Radio button" }
    }

    @Then("I verify that the item has visible icon image view")
    fun checkIfItemHasVisibleImageView() {
        val isVisible = listItemAppearanceTest?.isImageViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_icon_image_view,
            false,
            0
        )
        assert(isVisible == false) { "The item does not have visible Image view" }
    }

    @Then("I verify that the icon image view of the item is hidden")
    fun checkIfImageVIewOfItemIsHidden() {
        val isVisible = listItemAppearanceTest?.isImageViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_icon_image_view,
            true,
            0
        )
        assert(isVisible == false) { "The item does not have hidden Image view" }
    }

    @Then("I verify that the item has visible right icon image view")
    fun checkIfItemHasVisibleRightImageView() {
        val isVisible = listItemAppearanceTest?.isImageViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_image_view_right_icon,
            false,
            0
        )
        assert(isVisible == true) { "The item does not have visible Right image view" }
    }

    @Then("I verify that the right icon image view of the item is hidden")
    fun checkIfRightImageVIewOfItemIsHidden() {
        val isVisible = listItemAppearanceTest?.isImageViewVisibleInItem(
            R.id.recycler_view_list,
            R.id.list_item_image_view_right_icon,
            true,
            0
        )
        assert(isVisible == true) { "The item does not have hidden Right image view" }
    }

    @Then("I verify that the radio button is enabled")
    fun checkIfRadioButtonOfItemIsEnabled() {
        val isEnabled = listItemAppearanceTest?.isRadioButtonIsEnabledInItem(
            R.id.recycler_view_list,
            R.id.list_item_radio_button,
            0
        )
        assert(isEnabled == true) { "The item does not have enabled radio button" }
    }

    @Then("I verify that the radio button is disabled")
    fun checkIfRadioButtonOfItemIsDisabled() {
        val isEnabled: Boolean = listItemAppearanceTest?.isRadioButtonIsEnabledInItem(
            R.id.recycler_view_list,
            R.id.list_item_radio_button,
            0
        ) ?: false
        assert(!isEnabled == false) { "The item does not have disabled radio button" }
    }


    @Then("I verify that the radio button is checked in list item")
    fun checkIfRadioButtonOfItemIsChecked() {
        val isChecked: Boolean = listItemAppearanceTest?.isRadioButtonIsCheckedInItem(
            R.id.recycler_view_list,
            R.id.list_item_radio_button,
            0
        ) ?: false
        assert(isChecked == false) { "The item does not have checked radio button" }
    }

    @Then("I verify that the radio button is unchecked in list item")
    fun checkIfRadioButtonOfItemIsNotChecked() {
        val isChecked = listItemAppearanceTest?.isRadioButtonIsCheckedInItem(
            R.id.recycler_view_list,
            R.id.list_item_radio_button,
            0
        )!!
        assert(!isChecked == true) { "The item does not have unchecked radio button" }
    }

    @Then("I verify that the raw is disabled")
    fun checkIfRawIsDisabled() {
        val isDisabled = listItemAppearanceTest?.isItemDisabled(
            R.id.recycler_view_list,
            1
        )!!
        assert(isDisabled == true) { "The recycler view does not have any disabled item" }
    }


    @Then("I verify that the raw is disabled and title text color is grey")
    fun checkIfRawIsDisabledAndTextColorIsGrey() {
        val isDisabled = listItemAppearanceTest?.isItemDisabled(R.id.recycler_view_list, 1)
        val isColorGrey = listItemAppearanceTest?.isTitleTextColorIsGrey(
            R.id.recycler_view_list,
            R.id.list_item_title_text_view,
            1
        )
        assert(isDisabled == true && isColorGrey == true) { "The recycler view does not have any disabled item with grey color" }
    }

    @Then("I verify that the text size is proper")
    fun checkIsTitleTextSize() {
        val desiredTextSizeSp = 36f
        val expectedTextSize =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        val isValid = listItemAppearanceTest?.validateTextViewSize(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_title_text_view,
            position = 0,
            expectedTextSize = expectedTextSize
        )
        assert(isValid == true) { "The title text size is as expected" }
    }

    @Then("I verify that the text size is not proper")
    fun checkIsTextSizeNotProper() {
        val expectedTextSize = 1000
        val isValid = listItemAppearanceTest?.validateTextViewSize(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_title_text_view,
            position = 0,
            expectedTextSize = expectedTextSize
        ) ?: false
        assert(!isValid == true) { "The title text size is as expected" }
    }

    @Then("I verify that the sub text size is proper")
    fun checkIsSubTextSize() {
        val desiredTextSizeSp = 30f
        val expectedTextSize =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        val isValid = listItemAppearanceTest?.validateTextViewSize(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_sub_text_view,
            position = 0,
            expectedTextSize = expectedTextSize
        )
        assert(isValid == true) { "The sub text size is as expected" }
    }

    @Then("I verify that the sub text size is not proper")
    fun checkIsSubTextSizeNotProper() {
        val expectedTextSize = 1000
        val isValid = listItemAppearanceTest?.validateTextViewSize(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_sub_text_view,
            position = 0,
            expectedTextSize = expectedTextSize
        ) ?: false
        assert(!isValid == true) { "The sub text size is as expected" }
    }


    @Then("I verify that the right text size is proper")
    fun checkIsRightTextSize() {
        val desiredTextSizeSp = 30f
        val expectedTextSize =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        val isValid = listItemAppearanceTest?.validateTextViewSize(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_right_text_view,
            position = 0,
            expectedTextSize = expectedTextSize
        )
        assert(isValid == true) { "The right text size is as expected" }
    }

    @Then("I verify that the right text size is not proper")
    fun checkIsRightTextSizeNotProper() {
        val expectedTextSize = 1000
        val isValid = listItemAppearanceTest?.validateTextViewSize(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_right_text_view,
            position = 0,
            expectedTextSize = expectedTextSize
        ) ?: false
        assert(!isValid == true) { "The right text size is as expected" }
    }


    @Then("I verify that the title text color proper")
    fun checkIsTitleTextColor() {
        val expectedColor = Color.parseColor("#ffffff")
        val isValid = listItemAppearanceTest?.validateTextViewColor(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_title_text_view,
            position = 0,
            expectedColor = expectedColor
        )
        assert(isValid == true) { "The title text color is as expected" }
    }

    @Then("I verify that the title text color is not proper")
    fun checkIsTitleTextColorNotProper() {
        val expectedColor = Color.parseColor("#000000")
        val isValid = listItemAppearanceTest?.validateTextViewColor(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_title_text_view,
            position = 0,
            expectedColor = expectedColor
        ) ?: false
        assert(!isValid == true) { "The title text color is not as expected" }
    }


    @Then("I verify that the sub text color proper")
    fun checkIsSubTextColor() {
        val expectedColor = Color.parseColor("#ffffff")
        val isValid = listItemAppearanceTest?.validateTextViewColor(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_sub_text_view,
            position = 0,
            expectedColor = expectedColor
        )
        assert(isValid == true) { "The sub text color is as expected" }
    }

    @Then("I verify that the sub text color is not proper")
    fun checkIsSubTextColorNotProper() {
        val expectedColor = Color.parseColor("#000000")
        val isValid = listItemAppearanceTest?.validateTextViewColor(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_sub_text_view,
            position = 0,
            expectedColor = expectedColor
        ) ?: false
        assert(!isValid == true) { "The sub text color is not as expected" }
    }

    @Then("I verify that the right text color proper")
    fun checkIsRightTextColor() {
        val expectedColor = Color.parseColor("#ffffff")
        val isValid = listItemAppearanceTest?.validateTextViewColor(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_right_text_view,
            position = 0,
            expectedColor = expectedColor
        )
        assert(isValid == true) { "The right text color is as expected" }
    }

    @Then("I verify that the right text color is not proper")
    fun checkIsRightTextColorNotProper() {
        val expectedColor = Color.parseColor("#000000")
        val isValid = listItemAppearanceTest?.validateTextViewColor(
            viewId = R.id.recycler_view_list,
            childId = R.id.list_item_right_text_view,
            position = 0,
            expectedColor = expectedColor
        ) ?: false
        assert(!isValid == true) { "The right text color is not as expected" }
    }


    @Then("I verify that the padding of main container is proper")
    fun checkIsContainerPaddingProper() {
        val expectedPaddingTop = 0
        val expectedPaddingBottom = 0
        //val expectedPaddingLeft = CookingKACucumberTests.context.resources.getDimension(R.dimen.text_button_background_height).toInt()
        //val expectedPaddingRight = CookingKACucumberTests.context.resources.getDimension(R.dimen.text_button_background_height).toInt()
        val desiredTextSizeSpLeft = 64f
        val desiredTextSizeSpRight = 64f
        val expectedPaddingLeft =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSpLeft)
        val expectedPaddingRight =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSpRight)
        val isValid = listItemAppearanceTest?.validateContainerPadding(
            viewId = R.id.recycler_view_list,
            position = 0,
            expectedPaddingTop = expectedPaddingTop,
            expectedPaddingBottom = expectedPaddingBottom,
            expectedPaddingLeft = expectedPaddingLeft,
            expectedPaddingRight = expectedPaddingRight,
        )
        assert(isValid == true) { "The right text color is as expected" }
    }

    @Then("I verify that the padding of main container is not proper")
    fun checkIsContainerPaddingProperNotProper() {
        val expectedPaddingTop = 0
        val expectedPaddingBottom = 0
        val expectedPaddingLeft = 0
        val expectedPaddingRight = 0
        val isValid = listItemAppearanceTest?.validateContainerPadding(
            viewId = R.id.recycler_view_list,
            position = 0,
            expectedPaddingTop = expectedPaddingTop,
            expectedPaddingBottom = expectedPaddingBottom,
            expectedPaddingLeft = expectedPaddingLeft,
            expectedPaddingRight = expectedPaddingRight,
        ) ?: false
        assert(!isValid == true) { "The right text color is not as expected" }
    }


    @Then("I navigate to clock screen for list view screen")
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
//        setFragmentScenario(ListViewTestFragment::class.java)

    }

}