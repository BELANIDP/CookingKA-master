package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotClickable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.whirlpool.cooking.ka.R
import android.presenter.fragments.self_clean.SelfCleanStatusFragment
import androidx.test.espresso.Espresso.onView
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.textProperties
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndex
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TextViewPropertiesMatcher
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import com.whirlpool.hmi.uitesting.components.view.GenericViewTest
import com.whirlpool.hmi.utils.CookingSimConst
import core.utils.HMILogHelper
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
import org.mockito.Mockito.mock

/**
 * File        : com.whirlpool.cooking.ka.test.cucumber.steps.SelfCleanSteps
 * Brief       : Self Clean screen Appearance automation test cases
 * Author      : GHARDNS/Nikki
 * Created On  : 27/02/2024
 */
class SelfCleanFragmentAppearanceTest {

    val context: Context = ApplicationProvider.getApplicationContext()

    /* ---------- Common Methods START ---------------------- */

    fun soilLevelSelectedAs(soilLevel: String) {
        val index: Int = when (soilLevel) {
            "low" -> {
                0
            }

            "medium" -> {
                1
            }

            "high" -> {
                2
            }

            else -> {
                0
            }
        }
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerString, index)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerString, index)
    }

    fun tapOnBlackAreaOfTheScreen() {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).click(0, 100)
    }

    fun wait30Seconds() {
        UiTestingUtils.sleep(30000)
    }

    fun wait10Seconds() {
        UiTestingUtils.sleep(10000)
    }

    fun waitGivenSeconds(time: Long) {
        UiTestingUtils.sleep(time)
    }
    /* ---------- Common Methods END ---------------------- */


    fun headerTitleTextValidation(text: String) {
        UiTestingUtils.isTextMatching(R.id.tvTitle, text)
    }

    fun headerTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )
    }

    fun headerTitleTextAlignmentValidation() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
    }

    fun headerTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")
    }

    fun headerTitleTextViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.tvTitle)
    }

    fun headerTitleTextViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.tvTitle)
    }

    fun headerBackArrowViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.flLeftIcon)
    }

    fun headerBackArrowViewIsClickable() {
        UiTestingUtils.isViewClickable(R.id.flLeftIcon)

    }

    fun headerBackArrowSettingsScreenNavigation() {
        //Settings Screen is view visible
        UiTestingUtils.isViewVisible(R.id.nested_scroll_view_collection)
    }

    fun headerBackArrowCavitySelectionScreenNavigation() {
        //Settings Screen is view visible
        UiTestingUtils.isViewVisible(R.id.upper_oven_layout)
    }

    fun headerBackArrowBackgroundValidation() {
        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.ic_back_arrow)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivLeftIcon)),
            leftIcon
        )
    }

    fun headerCavityIconViewIsNotVisible() {
        UiTestingUtils.isViewNotVisible(R.id.flOvenCavityIcon)
    }

    fun headerCavityIconViewIsVisible() {
        UiTestingUtils.isViewVisible(R.id.flOvenCavityIcon)
    }

    fun soilLevelSelectedTypeTextValidation() {
        UiTestingUtils.sleep(1000)
        val isVisible = TestingUtils.isTextViewVisibleInItem(
            R.id.tumblerString,
            R.id.title,
            false,
            1
        )
        assert(isVisible)
    }

    fun soilLevelNextButtonTextValidation() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isTextMatchingAndFitting(R.id.btnPrimary, "NEXT")
    }

    fun soilLevelNextButtonTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.btnPrimary, "#ffffff")
    }

    fun soilLevelNextButtonTextSizeValidation() {
        val desiredTextSizeSp = 32f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.btnPrimary)), sizeInPixels.toFloat()
        )
    }

    fun soilLevelNextButtonViewIsClickable() {
        UiTestingUtils.isViewClickable(R.id.btnPrimary)
    }

    fun soilLevelNextButtonViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.btnPrimary)
    }

    fun soilLevelScreenVisibilityValidation() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerString)
    }

    fun instructionScreenVisibilityValidation() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isViewVisible(R.id.instruction_header)
    }

    fun instructionScreenTitleTextValidation(text: String) {
        UiTestingUtils.sleep(2000)
        Espresso.onView(withIndex(withId(R.id.tvTitle), 0)).check(matches(withText(text)))
    }

    fun instructionScreenTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )
    }

    fun instructionScreenTitleTextAlignmentValidation() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
    }

    fun instructionScreenTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")
    }

    fun instructionScreenTitleTextViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.tvTitle)
    }

    fun instructionScreenTitleTextViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.tvTitle)
    }

    fun instructionScreenBackArrowViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.flLeftIcon)
    }

    fun instructionScreenBackArrowViewIsClickable() {
        UiTestingUtils.isViewClickable(R.id.flLeftIcon)
    }

    fun instructionScreenBackArrowBackgroundValidation() {
        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.ic_back_arrow)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivLeftIcon)),
            leftIcon
        )
    }

    fun instructionScreenHeaderCavityIconVisibility() {
        UiTestingUtils.isViewNotVisible(R.id.flOvenCavityIcon)
    }

    fun instructionScreenVerticalScrollEnabled() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isViewEnabled(R.id.scroll_view)
    }

    fun instructionScreenVerticalScrollDownwardWorks() {
        UiTestingUtils.sleep(300)
        TestingUtils.isScrollingDown(R.id.scroll_view)
    }

    fun instructionScreenVerticalScrollUpwardWorks() {
        UiTestingUtils.sleep(300)
        TestingUtils.isScrollingUp(R.id.scroll_view)
    }

    fun instructionScreenDescriptionTextValidation() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_description, "1. Ensure your oven is empty. Remove the oven racks,\nbroiler pan, cookware and bakeware, all cooking utensils\nand aluminum foil and, on some models, the temperature\nprobe from the oven. \n\n 2. Use a damp cloth to clean inside door edge and the 1½\"\n(38 mm) area around the inside oven cavity frame, being\ncertain not to move or bend the gasket. \n\n 3. Wipe out any loose soil to reduce smoke and avoid\ndamage. At high temperatures, foods react with porcelain.\nStaining, etching, pitting, or faint white spots can result.\nThis will not affect cooking performance.\n\n\n")
    }

    fun instructionScreenDescriptionTextSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )
    }

    fun instructionScreenDescriptionTextAlignmentValidation() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.text_view_description)),
            Gravity.CENTER_HORIZONTAL
        )
    }

    fun instructionScreenDescriptionTextColorValidation() {
        UiTestingUtils.sleep(300)
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
    }


    fun instructionScreenNextButtonTextValidation() {
        UiTestingUtils.sleep(300)
        Espresso.onView(
            withIndex(withId(R.id.text_view_text_button),1))
            .check(matches((withText("NEXT"))))
    }

    fun instructionScreenNextButtonTextColorValidation() {
        UiTestingUtils.sleep(300)
        Espresso.onView(
            withIndex(withId(R.id.text_view_text_button),1))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
    }

    fun instructionScreenNextButtonTextSizeValidation() {
        val desiredTextSizeSp = 32f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(
                withIndex(withId(R.id.text_view_text_button),1)), sizeInPixels.toFloat()
        )
    }

    fun instructionScreenNextButtonTextGravityValidation() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button),1)),
            Gravity.CENTER
        )
    }

    fun instructionScreenNextButtonViewIsClickable() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isViewClickable(R.id.instruction_button_next)
    }

    fun instructionScreenNextButtonViewIsEnabled() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isViewEnabled(R.id.instruction_button_next)
    }


    /* ------------- Prepare Oven Screen --------------- */

    fun prepareOvenScreenVisibilityValidation() {
        UiTestingUtils.sleep(300)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
    }


    fun prepareOvenScreenTitleTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_title, "Prepare oven")
    }

    fun prepareOvenScreenTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
    }

    fun prepareOvenScreenTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }

    fun prepareOvenScreenTitleViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.text_view_title)
    }

    fun prepareOvenScreenTitleViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_view_title)
    }

    fun prepareOvenScreenDescriptionTextValidation() {
        UiTestingUtils.isTextMatchingAndFitting(
            R.id.text_view_description,
            "Press the flashing CLEAN button to begin."
        )
    }

    fun prepareOvenScreenDescriptionTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
    }

    fun prepareOvenScreenDescriptionTextSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )
    }

    fun prepareOvenScreenDescriptionViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.text_view_description)
    }

    fun prepareOvenScreenDescriptionViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_view_description)
    }

    fun prepareOvenScreenDescriptionTextAlignmentValidation() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.text_view_description)),
            Gravity.CENTER_HORIZONTAL
        )
    }


    fun cleanButtonTimeoutAndNavigationToSettingsScreen() {
        //Settings Screen is view visible
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.nested_scroll_view_collection)
        UiTestingUtils.sleep(1000)
    }

    fun prepareOvenScreenNavigateToDoorHasntOpenedClosed() {
        UiTestingUtils.isViewVisible(R.id.text_view_title)
    }

    fun prepareOvenScreenNavigateToDoorHasOpenedClosed() {
        UiTestingUtils.isViewVisible(R.id.text_view_title)
    }

    fun doorHasOpenedClosedScreenTitleTextValidation(text: String) {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_title, text)
    }


    fun doorHasOpenedClosedScreenTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
    }

    fun doorHasOpenedClosedScreenTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }

    fun doorHasOpenedClosedScreenTitleViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.text_view_title)
    }

    fun doorHasOpenedClosedScreenTitleViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_view_title)
    }


    fun doorHasOpenedClosedScreenDescriptionTextValidation(description: String) {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_description, description)
    }

    fun doorHasOpenedClosedScreenDescriptionTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
    }

    fun doorHasOpenedClosedScreenDescriptionTextSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )
    }

    fun doorHasOpenedClosedScreenDescriptionViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.text_view_description)
    }

    fun doorHasOpenedClosedScreenDescriptionViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_view_description)
    }

    fun doorHasOpenedClosedScreenDescriptionTextAlignmentValidation() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.text_view_description)),
            Gravity.CENTER_HORIZONTAL
        )
    }

    /* Door has opened closed screen Start button */

    fun doorHasOpenedClosedScreenStartButtonTextValidation(buttonText: String) {
        UiTestingUtils.sleep(2000)
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches(withText(buttonText)))
    }

    fun doorHasOpenedClosedScreenStartButtonTextColor() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2)).check(
            matches(
                TestingUtils.withTextColor(Color.parseColor("#ffffff"))
            )
        )
    }

    fun doorHasOpenedClosedScreenStartButtonTextSize() {
        val desiredTextSizeSp = 32f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2)),
            sizeInPixels.toFloat()
        )
    }

    fun doorHasOpenedClosedScreenStartButtonViewIsClickable() {
        UiTestingUtils.sleep(2000)
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches(isNotClickable()))
    }

    fun doorHasOpenedClosedScreenStartButtonViewIsEnabled() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches(isEnabled()))
    }

    fun lockingOvenDoorVisibility() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.title_text)
    }

    /* Door has opened closed screen Delay button */

    fun doorHasOpenedClosedScreenDelayButtonTextValidation(buttonText: String) {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0))
            .check(matches(withText(buttonText)))
    }

    fun doorHasOpenedClosedScreenDelayButtonTextColor() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0)).check(
            matches(
                TestingUtils.withTextColor(Color.parseColor("#ffffff"))
            )
        )
    }

    fun doorHasOpenedClosedScreenDelayButtonTextSize() {
        val desiredTextSizeSp = 32f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)

        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0)),
            sizeInPixels.toFloat()
        )
    }

    fun doorHasOpenedClosedScreenDelayButtonViewIsClickable() {
        UiTestingUtils.isViewClickable(R.id.text_button_left)
    }

    fun doorHasOpenedClosedScreenDelayButtonViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_button_left)
    }


    fun doorHasOpenedClosedScreenVisibility() {
        UiTestingUtils.isViewVisible(R.id.text_view_title)
    }

    fun delayScreenContentValidation() {
        //primary button validation
        Espresso.onView(withId(R.id.btnPrimary))
            .check(matches((withText("START DELAY"))))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
            .check(
                matches(
                    (textProperties(
                        0.05f,
                        TextViewPropertiesMatcher.TextProperties.LETTERSPACING
                    ))
                )
            )
            .check(
                matches(
                    (textProperties(
                        1f,
                        TextViewPropertiesMatcher.TextProperties.LINESPACINGEXTRA
                    ))
                )
            )
            .check(matches(isDisplayed()))
            .check(matches(isClickable()))

        //time text validation
        Espresso.onView(withId(R.id.degrees_type))
            .check(
                matches(
                    TestingUtils.withTextColor(Color.parseColor("#ffffff"))
                )
            )
            .check(
                matches(
                    (textProperties(
                        30f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
            .check(
                matches(
                    (textProperties(
                        1f,
                        TextViewPropertiesMatcher.TextProperties.LINESPACINGEXTRA
                    ))
                )
            )
            .check(matches(isDisplayed()))
            .check(matches(isNotClickable()))

        val typeface1 = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.degrees_type), 0)), typeface1
        )

        UiTestingUtils.sleep(1000)

        //title text validation
        Espresso.onView(withId(R.id.tvTitle))
            .check(
                matches(
                    TestingUtils.withTextColor(Color.parseColor("#ffffff"))
                )
            )
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
            .check(
                matches(
                    (textProperties(
                        1f,
                        TextViewPropertiesMatcher.TextProperties.LINESPACINGEXTRA
                    ))
                )
            )
            .check(matches(isDisplayed()))
            .check(matches(isNotClickable()))

        val typeface2 = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.tvTitle), 0)), typeface2
        )

        UiTestingUtils.sleep(1000)
    }


    /*----------------------------------------------------------------------- */
    /* ----------- Door hasn't Opened/Closed Screen Test Cases  ------------- */
    /*----------------------------------------------------------------------- */

    fun doorHasntOpenedClosedScreenVisibility() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.text_view_title)
    }

    fun doorHasntOpenedClosedScreenTitleTextValidation(text: String) {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_title, text)
    }


    fun doorHasntOpenedClosedScreenTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
    }

    fun doorHasntOpenedClosedScreenTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }

    fun doorHasntOpenedClosedScreenTitleViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.text_view_title)
    }

    fun doorHasntOpenedClosedScreenTitleViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_view_title)
    }

    fun doorHasntOpenedClosedScreenTitleTextAlignmentValidation() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.text_view_title)),
            Gravity.CENTER_HORIZONTAL
        )
    }


    fun doorHasntOpenedClosedScreenDescriptionTextValidation(description: String) {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_description, description)
    }

    fun doorHasntOpenedClosedScreenDescriptionTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
    }

    fun doorHasntOpenedClosedScreenDescriptionTextSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )
    }

    fun doorHasntOpenedClosedScreenDescriptionViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.text_view_description)
    }

    fun doorHasntOpenedClosedScreenDescriptionViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.text_view_description)
    }

    fun doorHasntOpenedClosedScreenDescriptionTextAlignmentValidation() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.text_view_description)),
            Gravity.CENTER_HORIZONTAL
        )
    }

    /* Locking Door Oven Title Text */

    fun lockingOvenDoorScreenTitleTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.title_text, "#ffffff")
    }

    fun lockingOvenDoorScreenTitleTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.title_text)), sizeInPixels.toFloat()
        )
    }

    fun lockingOvenDoorScreenTitleViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.title_text)
    }

    fun lockingOvenDoorScreenTitleViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.title_text)
    }


    /* Locking Door Oven Notification Text */

    fun lockingOvenDoorScreenVisibilityValidation() {
        UiTestingUtils.isViewVisible(R.id.title_text)
    }

    fun lockingOvenDoorScreenNotificationTextValidation(text: String) {
        UiTestingUtils.isTextMatchingAndFitting(R.id.details_text_view, text)
    }

    fun lockingOvenDoorScreenNotificationTextColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.details_text_view, "#ffffff")
    }

    fun lockingOvenDoorScreenNotificationTextSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.details_text_view)), sizeInPixels.toFloat()
        )
    }

    fun lockingOvenDoorScreenNotificationViewIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.details_text_view)
    }

    fun lockingOvenDoorScreenNotificationViewIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.details_text_view)
    }

    fun definedTimeShouldNavigateToSelfCleanStatusScreen() {
        try {
            UiTestingUtils.sleep(2000)
            val mockNavController = mock(NavController::class.java)
            val scenario = launchFragmentInContainer<SelfCleanStatusFragment>(
                themeResId = R.style.Theme_CookingApplianceM63KA,
                initialState = Lifecycle.State.STARTED
            )
            scenario.onFragment { fragment: Fragment ->
                Navigation.setViewNavController(fragment.requireView(), mockNavController)
            }
            UiTestingUtils.sleep(3000)
        } catch (e: Exception) {
            //catch exception and do test other cases
            UiTestingUtils.sleep(1000)
            HMILogHelper.Logd("TEST_", "exception:-->${e.message}")
        }
    }


    /*----------------------------------------------------------------------- */
    /* ----------- Self Clean Status Test Cases  ---------------------------- */
    /*----------------------------------------------------------------------- */

    fun selfCleanCycleCancel() {
        UiTestingUtils.sleep(1000)
        CookingSimConst.simulateCancelButtonPressEvent(CookingKACucumberTests.mainActivity, true)
        //Espresso.pressBackUnconditionally()
    }


    /* --------------- Self Clean Status: soilLevel Text Appearance  ------------ */

    fun selfCleanStatusSoilLevelTextValidation(text: String) {
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvTemperature, text)
    }

    fun selfCleanStatusSoilLevelTextSizeValidation() {
        val desiredTextSizeSp = 56f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvTemperature)), sizeInPixels.toFloat()
        )
    }

    fun selfCleanStatusSoilLevelColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.tvTemperature, "#ffffff")
    }

    fun selfCleanStatusSoilLevelAlignmentValidation() {
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTemperature)),
            Gravity.START
        )
    }

    fun selfCleanStatusSoilLevelIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.tvTemperature)
    }

    fun selfCleanStatusSoilLevelIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.tvTemperature)
    }

    /* --------------- Self Clean Status: Stat Now Text Appearance  ------------ */
    fun selfCleanStatusStatNowTextValidation() {
        UiTestingUtils.sleep(300)
        //Start Now button validation
        Espresso.onView(withId(R.id.tvStartNow))
            .check(matches((withText("START NOW"))))
            .check(
                matches(
                    TestingUtils.withTextColor(Color.parseColor("#ffffff"))
                )
            )
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
            .check(matches(isDisplayed()))
            .check(matches(isEnabled()))
        UiTestingUtils.sleep(1000)
    }

    /* --------------- Self Clean Status: Timer Text Appearance  ------------ */
    fun selfCleanStatusTimerTextValidation(text: String) {
        UiTestingUtils.sleep(5000)
        Espresso.onView(withId(R.id.tvCookTime))
            .check(matches(withText(not(Matchers.equalTo(text)))))
    }

    fun selfCleanStatusTimerTextSizeValidation() {
        val desiredTextSizeSp = 56f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvCookTime)), sizeInPixels.toFloat()
        )


    }

    fun selfCleanStatusTimerColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.tvCookTime, "#ffffff")
    }

    /* ---- Self Clean Status: Cycle Completed Cleaning Completed Text Appearance  ---------- */

    fun selfCleanStatusCycleCompletedVisibility() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.text_view_title)
    }

    fun selfCleanStatusCycleCompletedTextValidation(text: String) {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_title, text)
    }

    fun selfCleanStatusCycleCompletedTextSizeValidation() {
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )

    }

    fun selfCleanStatusCycleCompletedColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")
    }

    /* ---- Self Clean Status: Cycle Completed Cleaning Completed Text Appearance  ---------- */

    fun selfCleanStatusCycleCompletedDescriptionTextValidation(text: String) {
        UiTestingUtils.isTextMatchingAndFitting(R.id.text_view_description, text)
    }

    fun selfCleanStatusCycleCompletedDescriptionTextSizeValidation() {
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )

    }

    fun selfCleanStatusCycleCompletedDescriptionColorValidation() {
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")
    }

    /* ---- Self Clean Completed: Cavity Icon Appearance  ---------- */

    fun selfCleanStatusCycleCompletedCavityIconVisibility() {
        UiTestingUtils.isViewVisible(R.id.image_view_header_center)
    }

    fun selfCleanStatusCycleCompletedCavityIconNotVisibility() {
        UiTestingUtils.isViewNotVisible(R.id.image_view_header_center)
    }

    fun selfCleanStatusCycleCompletedCavityIconIsEnabled() {
        UiTestingUtils.isViewEnabled(R.id.image_view_header_center)
    }

    fun selfCleanStatusCycleCompletedCavityIconIsNotClickable() {
        UiTestingUtils.isViewNotClickable(R.id.image_view_header_center)
    }


    /* ---- Self Clean Completed: Ok Button Appearance  ---------- */
    fun selfCleanStatusCycleCompletedOKButtonVisibility() {
        Espresso.onView(withIndex(withId(R.id.text_button_right), 0)).check(matches(isDisplayed()))
    }

    fun selfCleanStatusCycleCompletedOKButtonIsEnabled() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0))
            .check(matches(isEnabled()))
    }

    fun selfCleanStatusCycleCompletedOKButtonIsClickable() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0))
            .check(matches(not(isClickable())))
    }


    fun selfCleanStatusCycleCompletedOKButtonTextValidation(text: String) {
        Espresso.onView(withIndex(withId(R.id.text_button_right), 0)).check(matches(withText(text)))
    }

    fun selfCleanStatusCycleCompletedOKButtonTextSizeValidation() {
        val desiredTextSizeSp = 32f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)

        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0)),
            sizeInPixels.toFloat()
        )

    }

    fun selfCleanStatusCycleCompletedOKButtonColorValidation() {
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0)).check(
            matches(
                TestingUtils.withTextColor(Color.parseColor("#ffffff"))
            )
        )
    }


    fun selfCleanStatusCycleCompletedClockVisibility() {
        UiTestingUtils.isViewVisible(R.id.text_view_clock_digital_clock_time)
    }

    fun startAfterDelayScreenVisibility() {
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBased)
        UiTestingUtils.isViewVisible(R.id.headerBar)
        UiTestingUtils.isViewVisible(R.id.degrees_type)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
    }

}