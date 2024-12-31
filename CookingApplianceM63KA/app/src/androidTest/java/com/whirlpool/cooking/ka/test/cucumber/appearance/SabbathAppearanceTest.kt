package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.InputDevice
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Tapper
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotClickable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.GestureDetector
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.dpToPx
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.matchesBackgroundColor
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.textProperties
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndex
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withLineHeight
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TextViewPropertiesMatcher
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkHeightOfText
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkWeightOfText
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import com.whirlpool.hmi.uitesting.components.view.GenericViewTest
import leakcanary.LeakAssertions

class SabbathAppearanceTest {
    val context: Context = ApplicationProvider.getApplicationContext()

    fun scrollAndClickOnSabbath(){
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        val index = 8// Sabbath
        onView(withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeSabbathScreen(){
        UiTestingUtils.sleep(300)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Sabbath")
        UiTestingUtils.isViewVisible(R.id.preferencesRecyclerList)
        UiTestingUtils.sleep(300)
    }

    fun iCheckTheSabbathHeaderTextView(text: String) {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.tvTitle),0)).check(matches(withText(text)))
        onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isNotClickable()))

    }

    fun iCheckTheSabbathHeaderTextViewAlignment() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
    }

    fun iCheckTheSabbathHeaderTextViewSize(){
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )
    }

    fun iCheckTheSabbathHeaderTextViewColor() {
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#FFFFFF")
    }

    fun iCheckTheBackgroundColorForSabbathModeSelectionScreen() {
        Espresso.onView(withIndex(withId(R.id.list_item_main_view), 0))
            .check(matches(matchesBackgroundColor(Color.parseColor("#000000"))))
//        TestingUtils.matchesBackgroundColor()
    }

    fun iCheckTheSabbathModeTextView(text: String) {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.list_item_title_text_view),0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.list_item_title_text_view),0)).check(matches(withText(text)))
        onView(withIndex(withId(R.id.list_item_title_text_view),0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.list_item_title_text_view),0)).check(matches(isNotClickable()))
    }

    fun iCheckTheSabbathModeTextViewSize() {
        val desiredTextSizeSp = 36f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withIndex( withId(R.id.list_item_title_text_view),0)), sizeInPixels.toFloat()
        )

//        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
//        val weight = "400"
//        onView(withIndex(withId(R.id.list_item_title_text_view),0))
//            .check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(withIndex(ViewMatchers.withId(R.id.list_item_title_text_view),0))
//            .check { view, _ -> checkWeightOfText(weight,view) }
    }

    fun iCheckTheSabbathModeTextViewColor() {
        GenericTextViewTest.checkMatchesTextColor(
        onView(TestingUtils.withIndex(withId(R.id.list_item_title_text_view), 0)),
        Color.parseColor("#ffffff"))
    }

    fun iCheckTheSabbathModeSubtitleTextView(title1: String) {
        UiTestingUtils.sleep(300)
        Espresso.onView(withIndex(withId(R.id.list_item_sub_text_view), 0)).check(matches(withText(title1)))
        onView(withIndex(withId(R.id.list_item_sub_text_view),0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.list_item_sub_text_view),0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.list_item_sub_text_view),0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.list_item_sub_text_view),0)).check(matches(isNotClickable()))
    }

    fun iCheckTheSabbathModeSubtitleTextViewSize() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withIndex( withId(R.id.list_item_sub_text_view),0)), sizeInPixels.toFloat()
        )

//        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
//        val weight = "300"
//        onView(withIndex( withId(R.id.list_item_sub_text_view),0))
//            .check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(
//            withIndex(ViewMatchers.withId(R.id.list_item_sub_text_view),0))
//            .check { view, _ -> checkWeightOfText(weight,view) }
    }

    fun iCheckTheSabbathModeSubtitleTextViewColor() {
        GenericTextViewTest.checkMatchesTextColor(
            onView(TestingUtils.withIndex(withId(R.id.list_item_sub_text_view), 0)),
            Color.parseColor("#ffffff"))
    }

    fun iCheckTheSabbathBakeTextView(text: String) {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.list_item_title_text_view),1)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.list_item_title_text_view),1)).check(matches(withText(text)))
        onView(withIndex(withId(R.id.list_item_title_text_view),1)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.list_item_title_text_view),1)).check(matches(isNotClickable()))
    }

    fun iCheckTheSabbathBakeTextViewSize() {
        val desiredTextSizeSp = 36f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withIndex( withId(R.id.list_item_title_text_view),1)), sizeInPixels.toFloat()
        )
//        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
//        val weight = "400"
//        onView(
//            withIndex(withId(R.id.list_item_title_text_view),0))
//            .check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(
//            withIndex(ViewMatchers.withId(R.id.list_item_title_text_view),0))
//            .check { view, _ -> checkWeightOfText(weight,view) }

    }

    fun iCheckTheSabbathBakeTextViewColor() {
        GenericTextViewTest.checkMatchesTextColor(
            onView(TestingUtils.withIndex(withId(R.id.list_item_title_text_view), 1)),
            Color.parseColor("#ffffff"))
    }

    fun iCheckTheSabbathBakeSubtitleTextView(title1: String) {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.list_item_sub_text_view),1)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.list_item_sub_text_view),1)).check(matches(withText(title1)))
        onView(withIndex(withId(R.id.list_item_sub_text_view),1)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.list_item_sub_text_view),1)).check(matches(isNotClickable()))
    }

    fun iCheckTheSabbathBakeSubtitleTextViewSize() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withIndex( withId(R.id.list_item_sub_text_view),1)), sizeInPixels.toFloat()
        )

//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
//        onView(withId(R.id.list_item_sub_text_view))
//            .check(matches(withLineHeight(desiredLineHeight)))
//        val weight = "300"
//        Espresso.onView(ViewMatchers.withId(R.id.list_item_sub_text_view))
//            .check { view, _ -> checkWeightOfText(weight,view) }
    }

    fun iCheckTheSabbathBakeSubtitleTextViewColor() {
        GenericTextViewTest.checkMatchesTextColor(
            onView(TestingUtils.withIndex(withId(R.id.list_item_sub_text_view), 1)),
            Color.parseColor("#ffffff"))
    }

    fun iCheckTheBackButton() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.flLeftIcon)
        UiTestingUtils.isViewClickable(R.id.flLeftIcon)
        //onView(withId(R.id.flLeftIcon)).check(matches(isClickable()))
    }

    fun iCheckTheBackButtonIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.flLeftIcon)
        //onView(withId(R.id.flLeftIcon)).check(matches(isEnabled()))
    }

    fun iCheckTheBackgroundOfTheBackButton() {
        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.ic_back_arrow)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivLeftIcon)),
            leftIcon
        )

        val desiredHeightInSp1 = 40f
        val desiredWidthInSp1 = 40f
        val heightInPixels1 = dpToPx(CookingKACucumberTests.context, desiredHeightInSp1)
        val widthInPixels1 = dpToPx(CookingKACucumberTests.context, desiredWidthInSp1)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivLeftIcon)), widthInPixels1, heightInPixels1)


    }

    fun iClickTheBackButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.ivLeftIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeSettingsScreen() {
        UiTestingUtils.sleep(1500)
//        Espresso.onView(ViewMatchers.withId(R.id.llListHeading))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(withIndex(withId(R.id.list_item_main_view),0)
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed())))
        Espresso.onView(withIndex(withId(R.id.llListHeading),0)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        Espresso.onView(withIndex(withId(R.id.list_item_main_view),0)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun iCheckTheCancelButtonIsNotClickable() {
        UiTestingUtils.sleep(1000)
        //onView(withId(R.id.flRightIcon)).check(matches(isClickable()))
        UiTestingUtils.isViewNotVisible(R.id.ivRightIcon)
        UiTestingUtils.isViewNotClickable(R.id.ivRightIcon)

    }

    fun iCheckTheBackgroundOfTheCancelButton() {
//        val rightIcon = AppCompatResources.getDrawable(context, R.drawable.ic_close)
//        GenericViewTest.checkMatchesBackground(
//            Espresso.onView(withId(R.id.ivRightIcon)),
//            rightIcon
//        )
        val desiredHeightInSp = 64f
        val desiredWidthInSp = 64f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.flRightIcon)), widthInPixels, heightInPixels)
    }

    fun iClickOnSabbathMode() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.list_item_main_view),0)).perform(click())
        LeakAssertions.assertNoLeaks()
        //UiTestingUtils.performClick(R.id.list_item_main_view)
    }

    fun iSeeInstructionScreenForSabbathMode() {
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.description_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.text_button_left)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.text_button_right)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.scroll_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    fun iCheckTheHeaderTextOfSabbathModeScreen(title: String) {
        UiTestingUtils.sleep(2000)
        Espresso.onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)).
        check(ViewAssertions.matches(ViewMatchers.withText(title)))
        Espresso.onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isNotClickable()))
    }

    fun iCheckTheSabbathModeScreenTextViewAlignment() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
    }
    fun iCheckTheSabbathModeScreenTextViewSize() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )
    }

    fun iCheckTheSabbathModeScreenHeaderTextViewColor() {
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")
    }

    fun iCheckTheSabbathModeScreenHeaderTextViewFont() {
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)), typeface
        )
    }

    fun iValidateTheDescriptionOfSabbathModeFeature() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isTextMatchingAndFitting(R.id.description_text, "The oven will stay in a Sabbath compliant state. All lights\nand displays will be disabled. No cooking will take place in\nthe oven while in this mode.")
    }

    fun iValidateTheDescriptionTextAlignmentOfSabbathModeFeature(){
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.description_text)),
            Gravity.CENTER
        )
    }

    fun iValidateTheDescriptionTextSizeOfSabbathModeFeature() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.description_text)), sizeInPixels.toFloat()
        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
        onView(withId(R.id.description_text))
            .check(matches(withLineHeight(desiredLineHeight)))
        val weight = "300"
        Espresso.onView(ViewMatchers.withId(R.id.description_text))
            .check { view, _ -> checkWeightOfText(weight,view) }

    }

    fun iValidateTheDescriptionTextColorOfSabbathModeFeature(){
        UiTestingUtils.sleep(300)
        TestingUtils.checkTextColorValidation(R.id.description_text, "#ffffff")
    }

    fun iValidateTheDescriptionTextFontOfSabbathModeFeature() {
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(TestingUtils.withIndex(withId(R.id.description_text), 0)), typeface
        )
    }

    fun iCheckTheDonTShowAgainCheckBoxIsVisible() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.text_button_left)
    }

    fun iCheckTheDonTShowAgainCheckBoxIsEnable() {
        UiTestingUtils.isViewEnabled(R.id.text_button_left)
    }

    fun iCheckTheDonTShowAgainCheckBoxIsClickable() {
        onView(withId(R.id.text_button_left)).check(matches(isClickable()))
    }

    fun iCheckTheTextViewOfDonTShowAgainCheckBox() {
        UiTestingUtils.sleep(300)
        //DON'T SHOW AGAIN button text validation
        //UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_text_button, "DON\'T SHOW AGAIN")
        //TestingUtils.checkTextColorValidation(R.id.text_button_left, "#ffffff")
        Espresso.onView(withIndex(withId(R.id.text_view_text_button),1))
            .check(matches((withText("DON'T SHOW AGAIN"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),1))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),1))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))
//        //checks alignment
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withIndex(withId(R.id.text_view_text_button),1)), Gravity.CENTER)
    }

    fun iCheckTheLayoutSizeOfDonTShowAgainCheckBox() {
        val desiredHeightInSp = 64f
        val desiredWidthInSp = 400f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.text_button_left)), widthInPixels, heightInPixels)
    }

    fun iCheckTheStartButtonIsVisible() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun iCheckTheStartButtonIsClickable() {
        onView(withId(R.id.text_button_right)).check(matches(isClickable()))
    }

    fun iCheckTheStartButtonIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_button_right)
    }

    fun iCheckTheStartButtonText(){
        UiTestingUtils.sleep(300)
        //Start button validation
        //TestingUtils.checkTextColorValidation(R.id.text_view_text_button, "#FFFFFF")
        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches((withText("START"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        //checks alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button),0)), Gravity.CENTER)

//        //check line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
//        val weight = "500"
//        onView(withId(R.id.text_button_right))
//            .check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(ViewMatchers.withId(R.id.text_button_right))
//            .check { view, _ -> checkWeightOfText(weight,view) }
//

//        //checks layout size(height & width)
//        val desiredHeightInSp = 38f
//        val desiredWidthInSp = 104f
//        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
//        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
//        GenericTextViewTest.checkMatchesTextSize(onView(withId(R.id.text_button_right)), widthInPixels.toFloat())
//        GenericTextViewTest.checkMatchesTextSize(onView(withId(R.id.text_button_right)), heightInPixels.toFloat())
    }

    fun iClickOnStartButtonForSabbathMode() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeSabbathModeIsON() {
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.main_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_sabbath_mode))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.icon_sabbath_mode)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_description)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

    }

    fun iCheckTheSabbathTextView(title: String) {
        UiTestingUtils.sleep(300)
        onView(withId(R.id.text_view_sabbath_mode)).check(matches(isDisplayed()))
        onView(withId(R.id.text_view_sabbath_mode)).check(matches(withText(title)))
        onView(withId(R.id.text_view_sabbath_mode)).check(matches(isEnabled()))
        onView(withId(R.id.text_view_sabbath_mode)).check(matches(isNotClickable()))
    }

    fun iCheckTheSabbathTextViewSize() {
        val desiredTextSizeSp = 80f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_sabbath_mode)), sizeInPixels.toFloat()
        )
    }

    fun iCheckTheSabbathTextViewColor() {
        GenericTextViewTest.checkMatchesTextColor(
            onView(withId(R.id.text_view_sabbath_mode)),Color.parseColor("#ffffff"))
    }

    fun iCheckTheSabbathTextViewFont() {
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_sabbath_mode)),typeface)
    }

    fun iCheckTheSabbathTextViewAlignment() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.text_view_sabbath_mode)),
            Gravity.CENTER
        )
    }

    fun iCheckTheSabbathTextViewLineHeightAndWeight() {
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 96f)
        val weight = "300"
        onView(withId(R.id.text_view_sabbath_mode))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_sabbath_mode))
            .check { view, _ -> checkWeightOfText(weight,view) }
    }

    fun iCheckTheSabbathIconVisibility() {
        Espresso.onView(ViewMatchers.withId(R.id.icon_sabbath_mode))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        UiTestingUtils.isViewVisible(R.id.icon_sabbath_mode)
        UiTestingUtils.isViewEnabled(R.id.icon_sabbath_mode)
        UiTestingUtils.isViewNotClickable(R.id.icon_sabbath_mode)
    }


    fun iCheckTheSabbathIconSize() {
        val desiredHeightInSp = 80f
        val desiredWidthInSp = 90.57f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.icon_sabbath_mode)), widthInPixels, heightInPixels)
    }

    fun iCheckTheSabbathPressAndHoldTextView() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_description, "Press and hold anywhere on the screen for 3 seconds\nto disable Sabbath Mode")
    }

    fun iCheckTheSabbathPressAndHoldTextViewSize() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )
    }

    fun iCheckTheSabbathPressAndHoldTextViewColor() {
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#AAA5A1")
//        GenericTextViewTest.checkMatchesTextColor(
//            onView(withId(R.id.text_view_description)),Color.parseColor("#AAA5A1"))
    }

    fun iCheckTheSabbathPressAndHoldTextViewFont() {
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_description)),typeface)
    }

    fun iCheckTheSabbathPressAndHoldTextViewAlignment() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.text_view_description)),
            Gravity.CENTER
        )
    }

    fun iCheckTheSabbathPressAndHoldTextViewLineHeightAndWeight() {
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
        onView(withId(R.id.text_view_description))
            .check(matches(withLineHeight(desiredLineHeight)))
        val weight = "400"
        Espresso.onView(ViewMatchers.withId(R.id.text_view_description))
            .check { view, _ -> checkWeightOfText(weight,view) }

    }

    fun iPressAndHoldOnTheScreenForThreeSeconds() {
        onView(withId((R.id.main_layout))).perform(
            ViewActions.actionWithAssertions(
                GeneralClickAction(
                    GestureDetector.LONG,
                    GeneralLocation.CENTER,
                    Press.FINGER,
                    InputDevice.SOURCE_UNKNOWN,
                    MotionEvent.BUTTON_PRIMARY
                )
            )
        )
        UiTestingUtils.sleep(5000)
    }

    fun iSeeClockScreen() {
        UiTestingUtils.sleep(3500)
        Espresso.onView(ViewMatchers.withId(R.id.text_view_clock_digital_clock_time))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_clock_digital_clock_day))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(ViewMatchers.withId(R.id.textviewOvenCoolingText))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    fun iClickOnSabbathBake() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.list_item_main_view),1)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeInstructionScreenForSabbathBake() {
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.description_text))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.text_button_left)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.text_button_right)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.scroll_view)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun iValidateTheHeaderTextOfSabbathBakeScreen(title:String) {
        UiTestingUtils.sleep(2000)
        Espresso.onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)).
        check(ViewAssertions.matches(ViewMatchers.withText(title)))
        Espresso.onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isNotClickable()))

    }

    fun iCheckTheSabbathBakeModeTextViewAlignment() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
    }

    fun iCheckTheSabbathBakeModeTextViewSize() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )
    }

    fun iCheckTheSabbathBakeModeTextViewColor() {
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")
    }

    fun iCheckTheSabbathBakeModeTextViewFont() {
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)),typeface)
    }

    fun iCheckTheNextButtonIsVisible() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun iCheckTheNextButtonText() {
        UiTestingUtils.sleep(300)
        //NEXT button validation
        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches((withText("NEXT"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        //checks alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button),0)), Gravity.CENTER)

//        //check line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
//        val weight = "500"
//        onView(withId(R.id.text_button_right))
//            .check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(ViewMatchers.withId(R.id.text_button_right))
//            .check { view, _ -> checkWeightOfText(weight,view) }

//        //checks layout text size(height & width)
//        val desiredHeightInSp = 38f
//        val desiredWidthInSp = 86f
//        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
//        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
//        GenericTextViewTest.checkMatchesTextSize(onView(withId(R.id.text_button_right)), widthInPixels.toFloat())
//        GenericTextViewTest.checkMatchesTextSize(onView(withId(R.id.text_button_right)), heightInPixels.toFloat())
    }

    fun iCheckTheNextButtonIsClickable() {
//        onView(withId(R.id.text_button_right)).check(matches(isClickable()))
        UiTestingUtils.isViewClickable(R.id.text_button_right)
    }

    fun iCheckTheNextButtonIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_button_right)
    }

    fun iValidateTheDescriptionOfSabbathBakeFeature() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isTextMatchingAndFitting(R.id.description_text, "Sabbath bake disables the twelve-hour shut off. All lights\nand displays will be disabled. Set temperature will be\ndisplayed and not the actual oven temperature.")
    }

    fun iValidateTheDescriptionTextAlignmentOfSabbathBakeFeature() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.description_text)),
            Gravity.CENTER
        )
    }

    fun iValidateTheDescriptionTextSizeOfSabbathBakeFeature() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.description_text)), sizeInPixels.toFloat()
        )
    }

    fun iValidateTheDescriptionTextColorOfSabbathBakeFeature() {
        UiTestingUtils.sleep(300)
        TestingUtils.checkTextColorValidation(R.id.description_text, "#ffffff")
    }

    fun iValidateTheDescriptionTextFontOfSabbathBakeFeature() {
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.description_text), 0)), typeface
        )
    }

    fun iClickOnNextButtonForSabbathBake() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(3000)
        UiTestingUtils.performClick(R.id.text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickTheNextButtonOnTumblerScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnPrimary)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeTheCavitySelectionScreenForSabbath() {
        Espresso.onView(ViewMatchers.withId(R.id.upper_oven_layout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.lower_oven_layout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.mainButtonLinearLayout)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun iSelectTheForSabbath(cavity: String) {
        UiTestingUtils.sleep(1000)
        if (cavity.equals("upper", ignoreCase = true)) {
            LeakAssertions.assertNoLeaks()
            UiTestingUtils.performClick(R.id.upper_oven_layout)
            LeakAssertions.assertNoLeaks()
        }
        if (cavity.equals("lower", ignoreCase = true)){
            LeakAssertions.assertNoLeaks()
            UiTestingUtils.performClick(R.id.lower_oven_layout)
            LeakAssertions.assertNoLeaks()
        }
    }

    fun iSeeTheHorizontalTumblerScreenForSabbathBake() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBased)
    }

    fun iScrollSelectTheTemperatureForSabbathBake() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerNumericBased))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(3))
//        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerNumericBased, 3)
        UiTestingUtils.sleep(2000)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeCooktimeNumpadForSabbath() {
        UiTestingUtils.isViewVisible(R.id.keyboardview)
//        UiTestingUtils.isViewNotClickable(R.id.text_button_right)
    }

    fun iClickBackButtonOnCooktimeNumpad() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.ivBackIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iSetTheCookTimeToOnNumpad(cookTimeSec: String) {
        UiTestingUtils.sleep(1000)
        TestingUtils.enterNumberStr(cookTimeSec)
    }

    fun iClickCancelButton(){
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.ivCancelIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnSetUntimedButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.cook_time_text_button_left)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeSetTimedButtonIsVisible() {
        UiTestingUtils.isViewVisible(R.id.cook_time_text_button_right)
//        UiTestingUtils.isViewClickable(R.id.cook_time_text_button_right)
//        UiTestingUtils.isViewEnabled(R.id.cook_time_text_button_right)
    }

    fun iClickOnSetTimedButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.cook_time_text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeBothCavityStatusScreenForSabbathBake() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.clSabbathUpperCavityStartSelection)
        UiTestingUtils.isViewVisible(R.id.clSabbathLowerCavityStartSelection)
        UiTestingUtils.isViewVisible(R.id.header_bar)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun iClickOnStartButtonForSabbathBake() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeStatusScreeRunningWithRecipeNameAndSelectedTemperature() {
        UiTestingUtils.isViewVisible(R.id.clModeLayout)
        UiTestingUtils.isViewVisible(R.id.progressBarRecipeCookTime)
        UiTestingUtils.isViewVisible(R.id.tvSabbathTemperatureDown)
        UiTestingUtils.isViewVisible(R.id.tvSabbathTemperatureUp)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
    }

    fun iClickOnNumpadIconButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.ivRightIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iSetTheTemperature(temp: String) {
        TestingUtils.enterNumberStr(temp)
    }

    fun iClickOnNextBtnOnNumpadScreen(){
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(2000)
        UiTestingUtils.performClick(R.id.cook_time_text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeTemperatureNumpad() {
        UiTestingUtils.isViewVisible(R.id.keyboardview)
    }

    fun iClickOnTheTumblerIcon() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.ivTumblerIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeSetTimedButtonIsNotClickable() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewNotClickable(R.id.cook_time_text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iScrollTheVerticalTumblerToRequiredDuration() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(ViewMatchers.withId(R.id.tumblerCenter))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(2))
        Espresso.onView(ViewMatchers.withId(R.id.tumblerLeft))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(1))
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeVerticalTumblerForSabbath(){
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.tumblerLeft)
        UiTestingUtils.isViewVisible(R.id.tumblerCenter)
    }

    fun iSeeSetTimedButtonIsNotClickableOnVerticalTumblerScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewNotClickable(R.id.btnPrimary)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeSetTimedButtonIsEnabledAndClickableOnVerticalTumblerScreen() {
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewClickable(R.id.btnPrimary)
        UiTestingUtils.isViewEnabled(R.id.btnPrimary)
    }

    fun iClickOnSetUntimedButtonOnVerticalTumblerScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.btnGhost)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnSetTimedButtonOnVerticalTumblerScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.btnPrimary)
        LeakAssertions.assertNoLeaks()
    }

    fun iValidateTheHeaderTextOfSabbathBakeScreen() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTitle, "Sabbath Bake")
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)),typeface)

        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvTitle))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
            .check { view, _ -> checkWeightOfText(weight,view) }
    }

    fun iCheckTheTemperatureTumblerSubtitleText() {
        TestingUtils.checkTextColorValidation(R.id.degrees_type, "#AAA5A1")
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.degrees_type)), sizeInPixels.toFloat()
        )
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.degrees_type)),
            Gravity.CENTER
        )
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.degrees_type)),typeface)
    }

    fun iCheckTemperatureTumblerIsScrolledToServings() {
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerNumericBased, 10)
        UiTestingUtils.sleep(1500)
    }

    fun iCheckTheNumpadIcon() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewEnabled(R.id.ivRightIcon)
        UiTestingUtils.isViewVisible(R.id.ivRightIcon)
        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivRightIcon)), widthInPixels, heightInPixels)

        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.numpad_icon)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivRightIcon)),
            leftIcon
        )
    }

    fun iCheckTheNextButtonOfTheTumblerScreen() {
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewClickable(R.id.btnPrimary)
        UiTestingUtils.isViewEnabled(R.id.btnPrimary)
    }

    fun iCheckTheNextButtonTextOfTheTumblerScreen() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.btnPrimary))
            .check(matches((withText("NEXT"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.btnPrimary)),
            Gravity.CENTER
        )

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.btnPrimary)),typeface)

//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
//        val weight = "500"
//        Espresso.onView(ViewMatchers.withId(R.id.btnPrimary))
//            .check { view, _ -> checkWeightOfText(weight,view) }
//        Espresso.onView(ViewMatchers.withId(R.id.btnPrimary))
//            .check { view, _ -> checkHeightOfText(desiredLineHeight.toString(),view) }
    }

    fun iCheckTheUpperOvenTextView() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.uppper_cavity_lbl))
            .check(matches((withText("UPPER OVEN"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))
            //.check(matches((withLineHeight(32))))

        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
        val weight = "500"
        onView(withId(R.id.uppper_cavity_lbl))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.uppper_cavity_lbl))
            .check { view, _ -> checkWeightOfText(weight,view) }

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.uppper_cavity_lbl)),typeface)
    }

    fun iCheckTheUpperOvenIconVisibility() {
        UiTestingUtils.isViewVisible(R.id.upper_cavity_icon)

        val icon = AppCompatResources.getDrawable(context, R.drawable.cavity_upper)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.upper_cavity_icon)),
            icon
        )
        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.upper_cavity_icon)), widthInPixels, heightInPixels)
    }

    fun iCheckTheUpperOvenTextViewLayout() {
        val desiredHeightInSp = 88f
        val desiredWidthInSp = 784f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.upper_oven_layout)), widthInPixels, heightInPixels)
        Espresso.onView(withId(R.id.upper_oven_layout))
            .check(matches(matchesBackgroundColor(Color.parseColor("#232323"))))
    }

    fun iCheckTheCavitySelectionScreenBackground() {
//        val bg = AppCompatResources.getDrawable(context, R.drawable.background)
        Espresso.onView(withId(R.id.mainButtonLinearLayout))
            .check(matches(matchesBackgroundColor(Color.parseColor("#000000"))))
//        GenericViewTest.checkMatchesBackground(
//            Espresso.onView(withId(R.id.holder)), bg)

    }

    fun iCheckTheLowerOvenTextView() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.lower_cavity_lbl))
            .check(matches((withText("LOWER OVEN"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
        val weight = "500"
        onView(withId(R.id.lower_cavity_lbl))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.lower_cavity_lbl))
            .check { view, _ -> checkWeightOfText(weight,view) }

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.lower_cavity_lbl)),typeface)
    }

    fun iCheckTheLowerOvenIconVisibility() {
        UiTestingUtils.isViewVisible(R.id.lower_cavity_icon)

        val icon = AppCompatResources.getDrawable(context, R.drawable.cavity_lower)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.lower_cavity_icon)),
            icon
        )
        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.lower_cavity_icon)), widthInPixels, heightInPixels)
    }

    fun iCheckTheLowerOvenTextViewLayout() {
        val desiredHeightInSp = 88f
        val desiredWidthInSp = 784f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.lower_oven_layout)), widthInPixels, heightInPixels)
        //checks color
        Espresso.onView(withId(R.id.lower_oven_layout))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#232323"))))
    }

    fun iCheckTheKeyboardViewHeader() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvCookTime))
            .check(matches((withText("00h00m"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 56f)
//        val weight = "400"
//        Espresso.onView(ViewMatchers.withId(R.id.tvCookTime)).check { view, _ -> checkWeightOfText(weight,view) }
//        Espresso.onView(ViewMatchers.withId(R.id.tvCookTime)).check { view, _ -> checkHeightOfText(
//            desiredLineHeight.toString(),view) }
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvCookTime)),typeface)
    }

    fun iCheckTheBackButtonOnCooktimeNumpadScreen() {
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.ivBackIcon)
        UiTestingUtils.isViewClickable(R.id.ivBackIcon)
        UiTestingUtils.isViewEnabled(R.id.ivBackIcon)
        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivBackIcon)), widthInPixels, heightInPixels)
    }

    fun iCheckTheTumblerIcon() {
        UiTestingUtils.isViewVisible(R.id.ivTumblerIcon)
        UiTestingUtils.isViewClickable(R.id.ivTumblerIcon)
        UiTestingUtils.isViewEnabled(R.id.ivTumblerIcon)

        val icon = AppCompatResources.getDrawable(context, R.drawable.ic_tumbler)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivTumblerIcon)),
            icon
        )
        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivTumblerIcon)), widthInPixels, heightInPixels)
    }

    fun iCheckTheCancelButton() {
        UiTestingUtils.isViewVisible(R.id.ivCancelIcon)
        UiTestingUtils.isViewClickable(R.id.ivCancelIcon)
        UiTestingUtils.isViewEnabled(R.id.ivCancelIcon)

        val icon = AppCompatResources.getDrawable(context, R.drawable.ic_cancel)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivCancelIcon)),
            icon
        )
        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivCancelIcon)), widthInPixels, heightInPixels)
    }

    fun iCheckTheSetTimedButton() {
        UiTestingUtils.sleep(2000)
        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches((withText("SET TIMED"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        //checks alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button),0)), Gravity.CENTER)

        UiTestingUtils.isViewVisible(R.id.cook_time_text_button_right)
        UiTestingUtils.isViewClickable(R.id.cook_time_text_button_right)
        UiTestingUtils.isViewEnabled(R.id.cook_time_text_button_right)

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView((withIndex( withId(R.id.text_view_text_button),0))),typeface)


//        Espresso.onView(withId(R.id.text_view_text_button))
//            .check(matches((withText("SET TIMED"))))
//            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
//            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))
//
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
//        val weight = "500"
//        Espresso.onView(ViewMatchers.withId(R.id.cook_time_text_button_right))
//            .check { view, _ -> checkWeightOfText(weight,view) }
//        Espresso.onView(ViewMatchers.withId(R.id.cook_time_text_button_right))
//            .check { view, _ -> checkWeightOfText(desiredLineHeight.toString(),view) }
//
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.cook_time_text_button_right)),
//            Gravity.CENTER
//        )



//        val desiredHeightInSp = 64f
//        val desiredWidthInSp = 205f
//        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
//        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
//        GenericTextViewTest.checkMatchesSize(
//            onView(withId(R.id.cook_time_text_button_right)), widthInPixels, heightInPixels)
    }


    fun iCheckTheSetUntimedButton() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withIndex(withId(R.id.text_view_text_button),2))
            .check(matches((withText("SET UNTIMED"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),2))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),2))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        //checks alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button),2)), Gravity.CENTER)

        UiTestingUtils.isViewVisible(R.id.cook_time_text_button_left)
        UiTestingUtils.isViewClickable(R.id.cook_time_text_button_left)
        UiTestingUtils.isViewEnabled(R.id.cook_time_text_button_left)

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView((withIndex(withId(R.id.text_view_text_button),2))),typeface)

//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
//        val weight = "500"
//        Espresso.onView(ViewMatchers.withId(R.id.cook_time_text_button_left))
//            .check { view, _ -> checkWeightOfText(weight,view) }
//        Espresso.onView(ViewMatchers.withId(R.id.cook_time_text_button_left))
//            .check { view, _ -> checkWeightOfText(desiredLineHeight.toString(),view) }

//        val desiredHeightInSp = 64f
//        val desiredWidthInSp = 252f
//        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
//        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
//        GenericTextViewTest.checkMatchesSize(
//            onView(withId(R.id.cook_time_text_button_left)), widthInPixels, heightInPixels)

    }

    fun iCheckTheHeaderTextOfCavityStatusScreenForSetUntimed() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvTitle))
            .check(matches((withText("Sabbath Bake"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(matches((textProperties(40f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle)).check { view, _ -> checkWeightOfText(weight,view) }
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle)).check { view, _ -> checkHeightOfText(
            desiredLineHeight.toString(),view) }
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)),typeface)
    }

    fun iCheckTheBackButtonForCavityStatusScreenForSetUntimed() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewEnabled(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivLeftIcon)), widthInPixels, heightInPixels)

        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.ic_back_arrow)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivLeftIcon)),
            leftIcon
        )
    }

    fun iCheckTheStartButtonTextForCavityStatusScreenForSetUntimed() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches((withText("START"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button),0))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))
//        //checks alignment
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withIndex(withId(R.id.text_view_text_button),0)), Gravity.CENTER)
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView((withIndex( withId(R.id.text_view_text_button),0))),typeface)
    }

    fun iCheckTheStartButtonIconForCavityStatusScreenForSetUntimed() {
        UiTestingUtils.isViewVisible(R.id.text_button_right)
        UiTestingUtils.isViewClickable(R.id.text_button_right)
        UiTestingUtils.isViewEnabled(R.id.text_button_right)

        val desiredHeightInSp = 64f
        val desiredWidthInSp = 136f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.text_button_right)), widthInPixels, heightInPixels)
    }

    fun iCheckUpperOvenStatusText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvUpperOvenCavity))
            .check(matches((withText("Upper Oven"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(matches((textProperties(36f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvUpperOvenCavity)),typeface)


        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
        val weight = "400"

        Espresso.onView(ViewMatchers.withId(R.id.tvUpperOvenCavity))
            .check { view, _ -> checkWeightOfText(weight,view) }
        Espresso.onView(ViewMatchers.withId(R.id.tvUpperOvenCavity))
            .check { view, _ -> checkHeightOfText(desiredLineHeight.toString(),view) }
    }

    fun iCheckTheUpperOvenIconOnCavityStatusScreen() {
        UiTestingUtils.isViewVisible(R.id.ivUpperOvenCavity)
        UiTestingUtils.isViewEnabled(R.id.ivUpperOvenCavity)

        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivUpperOvenCavity)), widthInPixels, heightInPixels)

        val icon = AppCompatResources.getDrawable(context, R.drawable.cavity_upper)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivUpperOvenCavity)),
            icon
        )

    }

    fun iCheckLowerOvenStatusText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvLowerOvenCavity))
            .check(matches((withText("Upper Oven"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(matches((textProperties(36f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvLowerOvenCavity)),typeface)

        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
        val weight = "400"
        Espresso.onView(ViewMatchers.withId(R.id.tvLowerOvenCavity))
            .check { view, _ -> checkWeightOfText(weight,view) }
        Espresso.onView(ViewMatchers.withId(R.id.tvLowerOvenCavity))
            .check { view, _ -> checkHeightOfText(desiredLineHeight.toString(),view) }
    }

    fun iCheckTheLowerOvenIconOnCavityStatusScreen() {
        UiTestingUtils.isViewVisible(R.id.ivLowerOvenCavity)
        UiTestingUtils.isViewEnabled(R.id.ivLowerOvenCavity)

        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivLowerOvenCavity)), widthInPixels, heightInPixels)

        val icon = AppCompatResources.getDrawable(context, R.drawable.cavity_lower)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivLowerOvenCavity)),
            icon
        )
    }

    fun iCheckTheUpperOvenStatusLayout() {
        UiTestingUtils.isViewVisible(R.id.clSabbathUpperCavityStartSelection)
        UiTestingUtils.isViewEnabled(R.id.clSabbathUpperCavityStartSelection)

        val desiredHeightInSp = 96f
        val desiredWidthInSp = 854f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.clSabbathUpperCavityStartSelection)), widthInPixels, heightInPixels)

    }

    fun iCheckTheLowerOvenStatusLayout() {
        UiTestingUtils.isViewVisible(R.id.clSabbathLowerCavityStartSelection)
        UiTestingUtils.isViewEnabled(R.id.clSabbathLowerCavityStartSelection)

        val desiredHeightInSp = 96f
        val desiredWidthInSp = 854f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.clSabbathLowerCavityStartSelection)), widthInPixels, heightInPixels)
    }

    fun iCheckStatusBar() {
        UiTestingUtils.isViewVisible(R.id.progressBarRecipeCookTime)
        val desiredHeightInSp = 16f
        val desiredWidthInSp = 784f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.progressBarRecipeCookTime)), widthInPixels, heightInPixels)
    }

    fun iCheckTheRecipeNameTextView() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.tvRecipeWithTemperature)
        TestingUtils.checkTextColorValidation(R.id.tvRecipeWithTemperature, "#ffffff")

        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.tvRecipeWithTemperature)), sizeInPixels.toFloat()
        )

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvRecipeWithTemperature)),typeface)

        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvRecipeWithTemperature))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.tvRecipeWithTemperature))
            .check { view, _ -> checkWeightOfText(weight,view) }
    }

    fun iCheckTheRemainingCookTimeIsDisplayedIfSetTimed() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.tvCookTimeRemaining)
        TestingUtils.checkTextColorValidation(R.id.tvCookTimeRemaining, "#ffffff")

        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(ViewMatchers.withId(R.id.tvCookTimeRemaining)), sizeInPixels.toFloat()
        )

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvCookTimeRemaining)),typeface)

        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvCookTimeRemaining))
            .check(matches(withLineHeight(desiredLineHeight)))
            .check { view, _ -> checkWeightOfText(weight,view) }

        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvCookTimeRemaining)),
            Gravity.RIGHT
        )
    }

    fun iCheckTemperatureDownButton() {
        UiTestingUtils.isViewVisible(R.id.tvSabbathTemperatureDown)
        UiTestingUtils.isViewEnabled(R.id.tvSabbathTemperatureDown)
        UiTestingUtils.isViewClickable(R.id.tvSabbathTemperatureDown)
    }

    fun iCheckTemperatureUpButton() {
        UiTestingUtils.isViewVisible(R.id.tvSabbathTemperatureUp)
        UiTestingUtils.isViewEnabled(R.id.tvSabbathTemperatureUp)
        UiTestingUtils.isViewClickable(R.id.tvSabbathTemperatureUp)
    }

    fun iCheckTemperatureUpButtonText() {
        UiTestingUtils.sleep(1000)
        Espresso.onView(withId(R.id.tvSabbathTemperatureUp))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvSabbathTemperatureUp)),
            Gravity.CENTER
        )
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvSabbathTemperatureUp)),typeface)
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
        val weight = "500"
        onView(withId(R.id.tvSabbathTemperatureUp))
            .check(matches(withLineHeight(desiredLineHeight)))
            .check { view, _ -> checkWeightOfText(weight,view) }
    }

    fun iCheckTemperatureDownButtonText() {
        UiTestingUtils.sleep(1000)
        Espresso.onView(withId(R.id.tvSabbathTemperatureDown))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))

        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvSabbathTemperatureDown)),
            Gravity.CENTER
        )

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvSabbathTemperatureDown)),typeface)

        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
        val weight = "500"
        onView(withId(R.id.tvSabbathTemperatureDown))
            .check(matches(withLineHeight(desiredLineHeight)))
            .check { view, _ -> checkWeightOfText(weight,view) }
    }

    fun iClickOnUpperOven() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.upper_oven_layout)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnLowerOvenToSetIt() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.clSabbathLowerCavityStartSelection)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeStatusScreenRunningWithRecipeNameAndSelectedTemperatureForBothCavity() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewVisible(R.id.singleUpperStatusWidget)
        UiTestingUtils.isViewVisible(R.id.singleLowerStatusWidget)
//        UiTestingUtils.isViewVisible(R.id.clModeLayout)
        Espresso.onView(withIndex(withId(R.id.progressBarRecipeCookTime),0))
        Espresso.onView(withIndex(withId(R.id.progressBarRecipeCookTime),1))
        Espresso.onView(withIndex(withId(R.id.tvSabbathTemperatureDown),0))
        Espresso.onView(withIndex(withId(R.id.tvSabbathTemperatureDown),1))
        Espresso.onView(withIndex(withId(R.id.tvSabbathTemperatureUp),0))
        Espresso.onView(withIndex(withId(R.id.tvSabbathTemperatureUp),1))
        Espresso.onView(withIndex(withId(R.id.tvRecipeWithTemperature),0))
        Espresso.onView(withIndex(withId(R.id.tvRecipeWithTemperature),1))
        UiTestingUtils.sleep(2500)
    }

    fun iCheckTextViewOfBothCavity() {
        val desiredUpperHeightInSp = 160f
        val desiredUpperWidthInSp = 854f
        val upperheightInPixels = dpToPx(CookingKACucumberTests.context, desiredUpperHeightInSp)
        val upperwidthInPixels = dpToPx(CookingKACucumberTests.context, desiredUpperWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.singleUpperStatusWidget)), upperwidthInPixels, upperheightInPixels)

        val desiredLowerHeightInSp = 160f
        val desiredLowerWidthInSp = 854f
        val lowerHeightInPixels = dpToPx(CookingKACucumberTests.context, desiredLowerHeightInSp)
        val lowerWidthInPixels = dpToPx(CookingKACucumberTests.context, desiredLowerWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.singleLowerStatusWidget)), lowerWidthInPixels, lowerHeightInPixels)
    }
}

