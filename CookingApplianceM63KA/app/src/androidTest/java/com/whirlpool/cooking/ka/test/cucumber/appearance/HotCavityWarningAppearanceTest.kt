package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkColorOfText
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkHeightOfImage
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkLineHeightOfText
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkText
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkTextSize
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkWeightOfText
import com.whirlpool.hmi.uitesting.UiTestingUtils

class HotCavityWarningAppearanceTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    fun performClickOnCavityButton(indexId: Int) {
        UiTestingUtils.performClick(indexId)
    }

    fun isHotCavityPopupVisible() {
        Espresso.onView(ViewMatchers.withId(R.id.popup_with_scroll))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun verifyIconOfHotCavityPopup(iconId: Int, height: String, width: String) {
        Espresso.onView(ViewMatchers.withId(R.id.image_view_header_center))
            .check(ViewAssertions.matches(TestingUtils.withDrawable(iconId)))
            .check { view, _ -> checkHeightOfImage(height, view) }
            .check { view, _ -> checkHeightOfImage(width, view) }
    }

    fun verifyTitleOfHotCavityPopup(title: String, font: String, weight: String, textSize: String, lineHeight: String, alignment: String, color: String) {
        Espresso.onView(ViewMatchers.withId(R.id.text_view_title))
            .check { view, _ -> checkText(title, view) }
            .check { view, _ -> checkWeightOfText(weight,view) }
            .check { view, _ -> checkTextSize(textSize, view) }
            .check { view, _ -> checkColorOfText(color, view) }
    }

    fun verifyLeftButtonOfHotCavityPopup() {
        UiTestingUtils.isViewVisible(R.id.bodyTextWithHotCavityTemp)
    }

    fun verifyLeftButtonOfHotCavityPopup1() {
        UiTestingUtils.isViewVisible(R.id.text_button_left)
    }

    fun verifyDescriptionOfHotCavityPopup(description: String, font: String, weight: String, textSize: String, lineHeight: String, alignment: String, color: String) {
        Espresso.onView(ViewMatchers.withId(R.id.text_view_description))
            .check {view, _ -> checkText(description, view)}
            .check { view, _ -> checkTextSize(textSize, view) }
            .check { view, _ -> checkLineHeightOfText(lineHeight, view) }
            .check { view, _ -> checkColorOfText(color, view) }
    }

    fun verifyRightButtonOfHotCavityPopup(rightButton: String) {
        Espresso.onView(ViewMatchers.withId(R.id.text_button_right))
            .check(TestingUtils.textButtonTextAssertion(rightButton))
    }

    fun iClickOnRightButton() {
        UiTestingUtils.performClick(R.id.text_button_right)
    }

    fun verifyStatusScreen(cavity: String) {
        when (cavity) {
            "Upper", "Lower", "Single", "Microwave", "ComboOven" -> {
                Espresso.onView(ViewMatchers.withId(R.id.singleStatusWidget))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
            }
            else -> {
                listOf(R.id.doubleStatusWidgetUpper, R.id.doubleStatusWidgetLower).forEach { id ->
                    Espresso.onView(ViewMatchers.withId(id))
                        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
                }
            }
        }
    }

    fun iSeeClockScreen(){
        UiTestingUtils.isViewVisible(R.id.text_view_clock_digital_clock_time)
    }

    fun iSeeDoorOpenPopup(){
        Espresso.onView(ViewMatchers.withId(R.id.popup_with_scroll))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.text_view_title))
            .check { view, _ -> checkText("Open Door", view) }

        Espresso.onView(ViewMatchers.withId(R.id.text_view_description))
            .check { view, _ -> checkText("Please close the door to continue cooking", view) }
    }

    fun iScrollToMoreModesAndClickOnIt(text: String){
        UiTestingUtils.sleep(1000)
        TestingUtils.withRecyclerViewScrollToTargetTextAndClick(R.id.tumblerString, text)
        UiTestingUtils.sleep(1000)
    }

    fun iClickOnIndex(index: String) {
        TestingUtils.clickViewWithText(index)
    }
}

