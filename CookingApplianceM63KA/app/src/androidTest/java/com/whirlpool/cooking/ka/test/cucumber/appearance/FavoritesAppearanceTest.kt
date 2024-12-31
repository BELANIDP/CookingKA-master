package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.InputDevice
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.appcompat.content.res.AppCompatResources
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
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotClickable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.google.common.base.Preconditions
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.GestureDetector
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.RecyclerViewMatcher
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.dpToPx
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.textProperties
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndex
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withLineHeight
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withViewHeight
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TextViewPropertiesMatcher
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkLineHeightOfText
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkWeightOfText
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import com.whirlpool.hmi.uitesting.components.view.GenericViewTest
import io.cucumber.java.en.Then
import leakcanary.LeakAssertions

class FavoritesAppearanceTest {
    val context: Context = ApplicationProvider.getApplicationContext()

    fun iAmOnClockScreen() {
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.text_view_clock_digital_clock_time))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_clock_digital_clock_day))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun iClickAndNavigateToTheCavitySelectionScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(3500)
        UiTestingUtils.performClick(R.id.main_layout)
        UiTestingUtils.sleep(1000)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeTheCavitySelectionScreen() {
        Espresso.onView(ViewMatchers.withId(R.id.upper_oven_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.lower_oven_layout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.mainButtonLinearLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun iSelectTheRequiredCavity(cavity: String) {
        UiTestingUtils.sleep(1000)
        if (cavity.equals("upper", ignoreCase = true)) {
            LeakAssertions.assertNoLeaks()
            UiTestingUtils.performClick(R.id.upper_oven_layout)
            LeakAssertions.assertNoLeaks()
        }
        if (cavity.equals("lower", ignoreCase = true)) {
            LeakAssertions.assertNoLeaks()
            UiTestingUtils.performClick(R.id.lower_oven_layout)
            LeakAssertions.assertNoLeaks()
        }
    }

    fun iSeeTheHorizontalTumblerScreen() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerString)
        UiTestingUtils.isViewVisible(R.id.gradient)
        UiTestingUtils.isViewVisible(R.id.imgGhost)
        UiTestingUtils.isViewVisible(R.id.imgPrimary)
        UiTestingUtils.isViewVisible(R.id.tvOvenCavityName)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)

    }

    fun iSeeTumblerToSetModeParameter() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerString)
        UiTestingUtils.isViewVisible(R.id.gradient)
    }

    fun iClickOnFavorites() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnGhost)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeFavoritesListScreenWithNoFavoritesAdded() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.textCreateUpTo)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.ivRightIcon)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
    }

    fun iClickBackButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.ivLeftIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnPlusIconToAddFavorites() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(2000)
        UiTestingUtils.performClick(R.id.ivRightIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeFavoritesScreen() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.listFavoritesFrom)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
    }

    fun iClickOnManualModes() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.textTitle), 0)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeCreateAFavoriteScreenWithHorizontalTumbler() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.tumblerString)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
    }

    fun iScrollTheHorizontalTumblerToIndexAndClick(index: Int) {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerString))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(index))
        UiTestingUtils.sleep(3500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.tumblerString, index)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeNumpadViewForSettingTemperature() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.keyboardview)
        UiTestingUtils.isViewVisible(R.id.cook_time_text_button_right)
        UiTestingUtils.isViewVisible(R.id.ivTumblerIcon)
        UiTestingUtils.isViewVisible(R.id.ivCancelIcon)
        UiTestingUtils.isViewVisible(R.id.tvCookTime)
        UiTestingUtils.isViewVisible(R.id.ivBackIcon)
    }

    fun iClickBackButtonOnNumpadView() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.ivBackIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnTumblerIconOnNumpadScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.ivTumblerIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeHorizontalTemperatureTumbler() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBased)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewVisible(R.id.ivRightIcon)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.degrees_type)
    }

    fun iClickOnNumpadIcon() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.ivRightIcon)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickNextButtonOnTheTemperatureSettingNumpadView() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.cook_time_text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeNumpadViewForSettingCookTime() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.keyboardview)
        UiTestingUtils.isViewVisible(R.id.cook_time_text_button_right)
        UiTestingUtils.isViewVisible(R.id.tvCookTime)
        UiTestingUtils.isViewVisible(R.id.ivBackIcon)
        UiTestingUtils.isViewVisible(R.id.ivCancelIcon)
        UiTestingUtils.isViewVisible(R.id.ivTumblerIcon)
    }

    fun iSetTheTemperatureOnNumpadViewForSettingTemperature(temp: String) {
        TestingUtils.enterNumberStr(temp)
    }

    fun iSeeVerticalTumblerForCooktime() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tumblerLeft)
        UiTestingUtils.isViewVisible(R.id.tumblerCenter)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.ivRightIcon)
    }

    fun iSetTheCookTimeToOnNumpadViewOfSettingCooktime(cookTimeSec: String) {
        UiTestingUtils.sleep(1000)
        TestingUtils.enterNumberStr(cookTimeSec)
    }

    fun iClickNextButtonOnTheCooktimeSettingNumpadView() {
        UiTestingUtils.sleep(1000)
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.cook_time_text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeDetailsOfTheFavoritesRecipeScreen() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewEnabled(R.id.recycler_view_preview)
        UiTestingUtils.isViewVisible(R.id.btnLeft)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
        UiTestingUtils.isViewVisible(R.id.ivInfo)

    }

    fun iClickOnSaveButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnPrimary)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeFavoritesListScreenAndTheRecipeIsAddedToFavorites(name: String) {
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.listFavorites)
        Espresso.onView(withIndex(withId(R.id.textTitle), 0)).check(matches(withText(name)))
    }

    fun iClickOnAutoCook() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.textTitle), 1)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeFoodTypeScreenOfFavorites() {
        UiTestingUtils.isViewEnabled(R.id.recycler_view_grid_list)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        Espresso.onView(withIndex(withId(R.id.image_view_convect_recipe), 0))
            .check(matches(isDisplayed()))
        Espresso.onView(withIndex(withId(R.id.text_view_convect_option_name), 0))
            .check(matches(isDisplayed()))
    }

    fun iScrollAndClickOnMeat() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.text_view_convect_option_name), 2)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeAllTheMeatRecipesScreen() {
        UiTestingUtils.isViewEnabled(R.id.recycler_view_grid_list)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        Espresso.onView(withIndex(withId(R.id.image_view_convect_recipe), 0))
            .check(matches(isDisplayed()))
        Espresso.onView(withIndex(withId(R.id.text_view_convect_option_name), 0))
            .check(matches(isDisplayed()))
    }

    fun iScrollAndClickOnIndex(index: Int) {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.text_view_convect_option_name), index))
            .perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeTheDonenessTumblerScreen() {
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewVisible(R.id.tumblerString)
    }

    fun iScrollDonenessLevelTumblerToLevel(level: String) {
        TestingUtils.funWithGridViewScrollToTargetTextAndClick(level)
    }

    fun iClickOnNextButtonOnTheDonenessTumblerScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnPrimary)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnModeParameter() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.previewTileSecondaryTextView), 0)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnOvenTemperatureParameter() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.previewTileSecondaryTextView), 1)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iScrollAndClickOnTimeParameter(parameter: Int) {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(parameter))
        UiTestingUtils.sleep(2500)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.recycler_view_preview, parameter)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnTheSaveButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnPrimary)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeFavoriteAlreadyExistsPopup() {
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isViewVisible(R.id.scroll_view)
        UiTestingUtils.isViewVisible(R.id.text_view_description)
    }

    fun iClickTheOKButtonOnTheFavoriteAlreadyExistsPopup() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnTheRecentAddedFavoritesCycle() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.listFavorites), 0)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnStartButtonOnDetailsOfTheFavoritesRecipeScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnPrimary)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeRecipeStatusScreen() {
        UiTestingUtils.sleep(1500)
        Espresso.onView(withId(R.id.singleStatusWidget))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.clModeLayout))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.ivOvenCavity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.tvRecipeWithTemperature))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun iClickOnStartTimerButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.tvSetOvenCookTime)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeCycleHasStarted() {
        Espresso.onView(withIndex(withId(R.id.tvSetOvenCookTime), 0))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun iClickTurnOFFButton() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.tvOvenStateAction), 0)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnTheExistingFavorites() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1500)
        Espresso.onView(withIndex(withId(R.id.listFavorites), 0)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnChooseImageButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnLeft)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeChooseAnImageScreen() {
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.listImages)
    }

    fun iScrollAndClickAnyImageTo(image: Int) {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.imageView), image)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeImageTumbler() {
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.holderArrows)
        UiTestingUtils.isViewVisible(R.id.imageArrowLeft)
        UiTestingUtils.isViewVisible(R.id.imageArrowRight)
        UiTestingUtils.isViewVisible(R.id.btnLeft)
        TestingUtils.withRecyclerView(R.id.imageView, 0)
    }

    fun iSeeLeaveImageSelectionPopup() {
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isViewVisible(R.id.text_view_description)
        UiTestingUtils.isViewVisible(R.id.text_button_left)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun iClickYesButtonOnTheLeaveImageSelectionPopup() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.text_button_right)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnSetButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnLeft)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeDetailsOfTheFavoritesRecipeScreenWithImageUpdated() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewEnabled(R.id.recycler_view_preview)
        UiTestingUtils.isViewVisible(R.id.btnLeft)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
        UiTestingUtils.isViewVisible(R.id.ivInfo)

        Espresso.onView(withIndex(withId(R.id.btnLeft), 0))
            .check(matches((withText("UPDATE IMAGE"))))
    }

    fun iSeeTheImageIsSet() {
        val imageSet = AppCompatResources.getDrawable(context, R.drawable.favoritemeat1)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(
                withIndex(withId(R.id.imageFavoritedRecipe), 0)
            ),
            imageSet
        )
    }

    fun iClickOnUpdateImageButton() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnLeft)
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnSaveAsFavorites() {
        LeakAssertions.assertNoLeaks()
        onView(withIndex(withId(R.id.more_options_tile), 0)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeRecipeSavedAsFavoritesNotification() {
        UiTestingUtils.sleep(1000)
//        UiTestingUtils.isViewVisible(R.id.tvLightState)
        UiTestingUtils.isViewEnabled(R.id.tvLightState)
        UiTestingUtils.isViewNotClickable(R.id.tvLightState)
        UiTestingUtils.isTextMatchingAndFitting(R.id.tvLightState, "Bake 175° added to favorites.")
    }

    fun iCheckTheNotificationTextView() {
        Espresso.onView(withId(R.id.tvLightState))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvLightState)),
            Gravity.CENTER
        )
        //checks line height & weight
//        val desiredLineHeight = "43.2"
        val weight = "400"
        Espresso.onView(ViewMatchers.withId(R.id.tvLightState))
            .check { view, _ -> checkWeightOfText(weight, view) }
//            .check { view, _ -> checkLineHeightOfText(desiredLineHeight.toString(),view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvLightState)), typeface
        )
    }

    fun iWaitForSecondsForTheNotificationToGoOff() {
        UiTestingUtils.sleep(10000)
    }

    fun iSeeRecipeAlreadyAsFavoritesNotification() {
        UiTestingUtils.sleep(200)
//        UiTestingUtils.isViewVisible(R.id.tvLightState)
        UiTestingUtils.isViewEnabled(R.id.tvLightState)
        UiTestingUtils.isViewNotClickable(R.id.tvLightState)
        UiTestingUtils.isTextMatchingAndFitting(
            R.id.tvLightState,
            "Bake 175° already added to favorites."
        )
    }

    fun iCheckTheHeaderTitleTextViewOfFavoritesListScreenWithNoFavoritesAdded() {
        UiTestingUtils.sleep(300)
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvTitle)).check(matches(isEnabled()))
        onView(withId(R.id.tvTitle)).check(matches(isNotClickable()))
    }

    fun iCheckTheFavoritesListScreenWithNoFavoritesAddedHeaderTitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvTitle))
            .check(matches((withText("Favorites"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )

        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
//      checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvTitle))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)), typeface
        )
    }

    fun iCheckTheDescriptionTextViewOfFavoritesListScreenWithNoFavoritesAdded() {
        UiTestingUtils.sleep(300)
        onView(withId(R.id.textCreateUpTo)).check(matches(isDisplayed()))
        UiTestingUtils.isTextMatchingAndFitting(
            R.id.textCreateUpTo,
            "Create up to 10 favorites for quick access to your frequently used cooking recipes."
        )
        onView(withId(R.id.textCreateUpTo)).check(matches(isEnabled()))
        onView(withId(R.id.textCreateUpTo)).check(matches(isNotClickable()))
    }

    fun iCheckTheFavoritesListScreenWithNoFavoritesAddedDescriptionTextView() {
        UiTestingUtils.sleep(300)
        //text & text size
        Espresso.onView(withId(R.id.textCreateUpTo))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        30f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//      checks line height & weight
        val height = "36"
        val weight = "300"
        Espresso.onView(ViewMatchers.withId(R.id.textCreateUpTo))
            .check { view, _ -> checkWeightOfText(weight, view) }
        Espresso.onView(ViewMatchers.withId(R.id.textCreateUpTo))
            .check { view, _ -> checkLineHeightOfText(height, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.textCreateUpTo)), typeface
        )
    }

    fun iCheckTheBackButton() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewClickable(R.id.flLeftIcon)

        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivLeftIcon)), widthInPixels, heightInPixels
        )

        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.ic_back_arrow)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivLeftIcon)),
            leftIcon
        )
    }

    fun iCheckThePlusIcon() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.ivRightIcon)
        UiTestingUtils.isViewClickable(R.id.flRightIcon)
        UiTestingUtils.isViewVisible(R.id.ivRightIcon)
        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivRightIcon)), widthInPixels, heightInPixels
        )

        val rightIcon = AppCompatResources.getDrawable(context, R.drawable.ic_add_40)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivRightIcon)),
            rightIcon
        )
    }

    fun iCheckTheHeaderTitleTextViewOfFavoritesScreen() {
        UiTestingUtils.sleep(300)
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvTitle)).check(matches(isEnabled()))
        onView(withId(R.id.tvTitle)).check(matches(isNotClickable()))
    }

    fun iCheckTheFavoritesScreenHeaderTitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvTitle))
            .check(matches((withText("Favorites"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvTitle)).check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)), typeface
        )
    }

    fun iCheckTheManualModesTitleTextView() {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.textTitle), 0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.listFavoritesFrom), 0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.textTitle), 0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.listFavoritesFrom), 0)).check(matches(isEnabled()))
    }

    fun iCheckTheHistoryTitleTextView() {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.textTitle), 2)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.textTitle), 2)).check(matches(isEnabled()))
    }

    fun iCheckTheAutoCookTitleTextView() {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.textTitle), 1)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.textTitle), 1)).check(matches(isEnabled()))
    }

    fun iCheckTheManualModesTitleText() {
        UiTestingUtils.sleep(300)
        onView(RecyclerViewMatcher(R.id.listFavoritesFrom).atPositionOnView(0, R.id.textTitle))
            .check(matches((withText("Manual Modes"))))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
//        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
//        val weight = "400"
//        onView(withIndex(withId(R.id.textTitle),0)).check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(withIndex(ViewMatchers.withId(R.id.textTitle),0)).check { view, _ -> checkWeightOfText(weight,view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.textTitle), 0)), typeface
        )
    }

    fun iCheckTheAutoCookTitleText() {
        UiTestingUtils.sleep(300)
        onView(RecyclerViewMatcher(R.id.listFavoritesFrom).atPositionOnView(1, R.id.textTitle))
            .check(matches((withText("Auto Cook"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
//        val weight = "400"
//        onView(withIndex(withId(R.id.textTitle),1)).check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(withIndex(ViewMatchers.withId(R.id.textTitle),1)).check { view, _ -> checkWeightOfText(weight,view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.textTitle), 1)),
            typeface
        )
    }

    fun iCheckTheHistoryTitleText() {
        UiTestingUtils.sleep(300)
        onView(RecyclerViewMatcher(R.id.listFavoritesFrom).atPositionOnView(2, R.id.textTitle))
            .check(matches((withText("History"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
//        val weight = "400"
//        onView(withIndex(withId(R.id.textTitle),2)).check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(withIndex(ViewMatchers.withId(R.id.textTitle),2)).check { view, _ -> checkWeightOfText(weight,view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.textTitle), 2)),
            typeface
        )
    }

    fun iCheckTheHeaderTitleTextViewOfCreateAFavoritesScreen() {
        UiTestingUtils.sleep(300)
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvTitle)).check(matches(isEnabled()))
        onView(withId(R.id.tvTitle)).check(matches(isNotClickable()))
    }

    fun iCheckTheCreateAFavoritesScreenHeaderTitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvTitle))
            .check(matches((withText("Create a Favorite"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvTitle)).check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)), typeface
        )
    }

    fun iCheckTheHeaderTitleTextViewOfFavoritesPreviewScreen() {
        UiTestingUtils.sleep(300)
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvTitle)).check(matches(isEnabled()))
        onView(withId(R.id.tvTitle)).check(matches(isNotClickable()))
    }

    fun iCheckTheFavoritesPreviewScreenHeaderTitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvTitle))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvTitle)).check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)), typeface
        )
    }

    fun iCheckTheEditIcon() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.ivInfo)
        UiTestingUtils.isViewVisible(R.id.ivInfo)
        UiTestingUtils.isViewClickable(R.id.flLeftIcon)

        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivInfo)), widthInPixels, heightInPixels
        )

        val editIcon = AppCompatResources.getDrawable(context, R.drawable.ic_edit)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivInfo)),
            editIcon
        )
    }

    fun iCheckTheChooseImageButtonView() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.btnLeft)
        UiTestingUtils.isViewVisible(R.id.btnLeft)
        UiTestingUtils.isViewClickable(R.id.btnLeft)
    }

    fun iCheckTheChooseImageButtonText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.btnLeft))
            .check(matches((withText("CHOOSE IMAGE"))))
        Espresso.onView(withId(R.id.btnLeft))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
        Espresso.onView(withId(R.id.btnLeft))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//        //checks alignment
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.btnLeft)), Gravity.CENTER)
    }

    fun iCheckTheSaveButtonView() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.btnPrimary)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewClickable(R.id.btnPrimary)
    }

    fun iCheckTheSaveButtonText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.btnPrimary))
            .check(matches((withText("SAVE"))))

        Espresso.onView(withId(R.id.btnPrimary))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withId(R.id.btnPrimary))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )

//        //checks alignment
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.btnPrimary)), Gravity.CENTER)

    }

    fun iCheckTheModeParameterView() {
        UiTestingUtils.sleep(500)
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.recycler_view_preview), 0))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.recycler_view_preview), 0))
            .check(ViewAssertions.matches(ViewMatchers.isClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.recycler_view_preview), 0))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 0))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 0))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 0))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 0))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 0))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 0))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    fun iCheckTheModeParameterTextView() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withIndex(withId(R.id.previewTilePrimaryTextView), 0))
            .check(matches((withText("Mode"))))
        Espresso.onView(withIndex(withId(R.id.previewTilePrimaryTextView), 0))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
        Espresso.onView(withIndex(withId(R.id.previewTilePrimaryTextView), 0))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(
                withIndex(
                    withId(R.id.previewTilePrimaryTextView),
                    0
                )
            ), typeface
        )
    }

    fun iCheckTheOvenTemperatureParameterView() {
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(1))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    fun iCheckTheOvenTemperatureParameterTextView() {
        UiTestingUtils.sleep(300)
        onView(
            RecyclerViewMatcher(R.id.recycler_view_preview).atPositionOnView(
                1,
                R.id.previewTilePrimaryTextView
            )
        )
            .check(matches((withText("Oven Temperature"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(
                withIndex(
                    withId(R.id.previewTilePrimaryTextView),
                    0
                )
            ), typeface
        )
    }

    fun iCheckTheTimeParameterView() {
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(2))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 2))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 2))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 2))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))

        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 2))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 2))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 2))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    fun iCheckTheTimeParameterTextView() {
        UiTestingUtils.sleep(300)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(2))
        onView(
            RecyclerViewMatcher(R.id.recycler_view_preview).atPositionOnView(
                2,
                R.id.previewTilePrimaryTextView
            )
        )
            .check(matches((withText("Time"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(
                withIndex(
                    withId(R.id.previewTilePrimaryTextView),
                    2
                )
            ), typeface
        )
    }

    fun iCheckTheModeParameterSubtitleTextView() {
        UiTestingUtils.sleep(300)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(0))
        onView(
            RecyclerViewMatcher(R.id.recycler_view_preview).atPositionOnView(
                0,
                R.id.previewTileSecondaryTextView
            )
        )
            .check(matches((withText("Bake"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#AAA5A1"))))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(
                withIndex(
                    withId(R.id.previewTileSecondaryTextView),
                    0
                )
            ), typeface
        )
    }

    fun iCheckTheOvenTemperatureParameterSubtitleTextView() {
        UiTestingUtils.sleep(300)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(1))
        onView(
            RecyclerViewMatcher(R.id.recycler_view_preview).atPositionOnView(
                1,
                R.id.previewTileSecondaryTextView
            )
        )
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#AAA5A1"))))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(
                withIndex(
                    withId(R.id.previewTileSecondaryTextView),
                    1
                )
            ), typeface
        )
    }

    fun iCheckTheTimeParameterSubtitleTextView() {
        UiTestingUtils.sleep(300)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(2))
        onView(
            RecyclerViewMatcher(R.id.recycler_view_preview).atPositionOnView(
                2,
                R.id.previewTileSecondaryTextView
            )
        )
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#AAA5A1"))))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(
                withIndex(
                    withId(R.id.previewTileSecondaryTextView),
                    2
                )
            ), typeface
        )
    }

    fun iCheckTheHeaderTitleViewOfChooseAnImageScreen() {
        UiTestingUtils.sleep(300)
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvTitle)).check(matches(isEnabled()))
        onView(withId(R.id.tvTitle)).check(matches(isNotClickable()))
    }

    fun iCheckTheHeaderTitleTextViewOfChooseAnImageScreen() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvTitle))
            .check(matches((withText("Choose an image"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvTitle)).check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)), typeface
        )
    }

    fun iCheckTheImageTumblerOfChooseAnImageScreen() {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.imageView), 0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.imageView), 0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.imageView), 0)).check(matches(isNotClickable()))

        onView(withId(R.id.listImages)).check(matches(isDisplayed()))
        onView(withId(R.id.listImages)).check(matches(isEnabled()))
        onView(withId(R.id.listImages)).check(matches(isNotClickable()))

        val desiredHeightInSp = 250f
        val desiredWidthInSp = 250f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withIndex(withId(R.id.imageView), 0)), widthInPixels, heightInPixels
        )

        val heightInSp = 250f
        val height = dpToPx(CookingKACucumberTests.context, heightInSp)
        onView(withId(R.id.listImages))
            .check(matches(withViewHeight(height)))
    }

    fun iCheckTheHeaderTitleOfImageTumblerScreen() {
        UiTestingUtils.sleep(300)
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvTitle)).check(matches(isEnabled()))
        onView(withId(R.id.tvTitle)).check(matches(isNotClickable()))
    }

    fun iCheckTheHeaderTitleTextOfImageTumblerScreen() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvTitle))
            .check(matches((withText("Choose an image"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvTitle)).check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)), typeface
        )
    }

    fun iCheckTheSetButtonText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.btnLeft))
            .check(matches((withText("SET"))))

        Espresso.onView(withId(R.id.btnLeft))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withId(R.id.btnLeft))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )

//        //checks alignment
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.btnLeft)), Gravity.CENTER)
    }

    fun iCheckTheSetButtonView() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.btnLeft)
        UiTestingUtils.isViewVisible(R.id.btnLeft)
        UiTestingUtils.isViewClickable(R.id.btnLeft)
    }

    fun iCheckTheNewlyAddedRecipeView() {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.textTitle), 0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.textTitle), 0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.textTitle), 0)).check(matches(isNotClickable()))
        UiTestingUtils.isViewVisible(R.id.listFavorites)
    }

    fun iCheckTheNewlyAddedRecipeText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withIndex(withId(R.id.textTitle), 0))
            .check(matches((withText("Favorite"))))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
        val weight = "400"
        onView(
            withIndex(
                withId(R.id.textTitle),
                0
            )
        ).check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.textTitle), 0))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.textTitle), 1)), typeface
        )
    }

    fun iCheckTheNewlyAddedRecipeOvenIcon() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.imageCavityIcon)
        UiTestingUtils.isViewVisible(R.id.imageCavityIcon)
        UiTestingUtils.isViewNotClickable(R.id.imageCavityIcon)
    }

    fun iClickOnHistory() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.textTitle), 2)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnTheRecentlyRunCycle() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.textTitle), 0)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeFavPreviewScreenForHistory() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewEnabled(R.id.recycler_view_preview)
        UiTestingUtils.isViewVisible(R.id.btnLeft)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
    }

    fun iClickOnProbe() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(2000)
        Espresso.onView(withIndex(withId(R.id.text_view_convect_option_name), 2)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iClickOnBake() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(2000)
        Espresso.onView(withIndex(withId(R.id.text_view_convect_option_name), 0)).perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iClickNextButtonOnProbeTempTumbler() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnPrimary)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeFavoritesPreviewScreenForProbe() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewEnabled(R.id.recycler_view_preview)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
        UiTestingUtils.isViewVisible(R.id.ivInfo)
    }

    fun iValidateTheProbeTemperatureParameterView() {
        UiTestingUtils.sleep(1500)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(1))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    fun iValidateTheProbeTemperatureParameterSubtitleView() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(1))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView), 1))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    fun iValidateTheProbeTemperatureParameterText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withIndex(withId(R.id.previewTilePrimaryTextView), 1))
            .check(matches((withText("Probe Temperature"))))
        Espresso.onView(withIndex(withId(R.id.previewTilePrimaryTextView), 1))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
        Espresso.onView(withIndex(withId(R.id.previewTilePrimaryTextView), 1))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//        checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
//        val weight = "300"
//        onView(withIndex(withId(R.id.previewTilePrimaryTextView),1)).check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView),1)).check { view, _ -> checkWeightOfText(weight,view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(
                withIndex(
                    withId(R.id.previewTilePrimaryTextView),
                    0
                )
            ), typeface
        )
    }

    fun iValidateTheProbeTemperatureParameterSubtitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(1))
        Espresso.onView(withIndex(withId(R.id.previewTileSecondaryTextView), 1))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#AAA5A1"))))
        Espresso.onView(withIndex(withId(R.id.previewTileSecondaryTextView), 1))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
//        val weight = "500"
//        onView(withIndex(withId(R.id.previewTileSecondaryTextView),1)).check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTileSecondaryTextView),1)).check { view, _ -> checkWeightOfText(weight,view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(
                withIndex(
                    withId(R.id.previewTileSecondaryTextView),
                    1
                )
            ), typeface
        )
    }

    fun iCheckOvenTempParameter() {
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_preview))
            .perform(TestingUtils.withRecyclerViewScrollToPosition(2))
        Espresso.onView(withIndex(withId(R.id.previewTilePrimaryTextView), 2))
            .check(matches((withText("Oven Temperature"))))

        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 2))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 2))
            .check(ViewAssertions.matches(ViewMatchers.isNotClickable()))
        Espresso.onView(withIndex(ViewMatchers.withId(R.id.previewTilePrimaryTextView), 2))
            .check(ViewAssertions.matches(ViewMatchers.isEnabled()))
    }

    fun iSeeFavoritesListScreenAndTheRecipeWithTheProbeIcon(name: String) {
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewVisible(R.id.listFavorites)
        UiTestingUtils.isViewVisible(R.id.imageProbeIcon)
        UiTestingUtils.isViewVisible(R.id.imageCavityIcon)
        Espresso.onView(withIndex(withId(R.id.textTitle), 0)).check(matches(withText(name)))
    }

    fun iClickOnSaveToFavorites() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.performClick(R.id.btnPrimary)
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeHistoryScreen() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewEnabled(R.id.listHistory)
        UiTestingUtils.isViewVisible(R.id.listHistory)
        UiTestingUtils.isViewVisible(R.id.tvTitle)
        UiTestingUtils.isViewVisible(R.id.ivLeftIcon)
    }

    fun iCheckTheHistoryScreenHeaderTitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.tvTitle))
            .check(matches((withText("History"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )

        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
//      checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.tvTitle))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.tvTitle))
            .check { view, _ -> checkWeightOfText(weight, view) }

        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)), typeface
        )
    }

    fun iCheckTheHistoryScreenHeaderTitleView() {
        UiTestingUtils.sleep(300)
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.tvTitle)).check(matches(isEnabled()))
        onView(withId(R.id.tvTitle)).check(matches(isNotClickable()))
    }

    fun iCheckTheRecentRecipeText() {
        UiTestingUtils.sleep(300)
        onView(
            RecyclerViewMatcher(R.id.listHistory).atPositionOnView(0, R.id.textTitle)
        )
            .check(matches((withText("Bake"))))
            .check(
                matches(
                    (textProperties(
                        36f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
//        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 43.2f)
//        val weight = "400"
//        onView(
//            withIndex(
//                withId(R.id.textTitle),
//                0
//            )
//        ).check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(withIndex(ViewMatchers.withId(R.id.textTitle), 0))
//            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.textTitle), 0)), typeface
        )
    }

    fun iCheckTheRecentRecipeView() {
        UiTestingUtils.sleep(300)
        onView(withIndex(withId(R.id.listHistory), 0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.listHistory), 0)).check(matches(isEnabled()))
        onView(withIndex(withId(R.id.listHistory), 0)).check(matches(isNotClickable()))
        onView(withIndex(withId(R.id.textTitle), 0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.textTitle), 0)).check(matches(isEnabled()))
    }

    fun iCheckTheRecentRecipeSubtitleView() {
        onView(withIndex(withId(R.id.textTempAndDuration), 0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.textTempAndDuration), 0)).check(matches(isEnabled()))

        UiTestingUtils.sleep(300)
        onView(
            RecyclerViewMatcher(R.id.listHistory).atPositionOnView(0, R.id.textTempAndDuration)
        )
            .check(
                matches(
                    (textProperties(
                        30f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
//        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
//        val weight = "300"
//        onView(withIndex(withId(R.id.textTempAndDuration), 0)).check(
//            matches(
//                withLineHeight(
//                    desiredLineHeight
//                )
//            )
//        )
//        Espresso.onView(withIndex(ViewMatchers.withId(R.id.textTempAndDuration), 0))
//            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.textTempAndDuration), 0)), typeface
        )
    }

    fun iCheckTheRecentRecipeTimeView() {
//        onView(withIndex(withId(R.id.textTime), 0)).check(matches(isDisplayed()))
//        onView(withIndex(withId(R.id.textTime), 0)).check(matches(isEnabled()))
//
//        UiTestingUtils.sleep(300)
//        onView(
//            RecyclerViewMatcher(R.id.listHistory).atPositionOnView(0, R.id.textTime)
//        )
//        Espresso.onView(withIndex(withId(R.id.textTime), 0))
//            .check(
//                matches(
//                    (textProperties(
//                        30f,
//                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
//                    ))
//                )
//            )
//            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
//        //alignment
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.textTime)),
//            Gravity.RIGHT
//        )
//        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
//        val weight = "300"
//        onView(
//            withIndex(
//                withId(R.id.textTime),
//                0
//            )
//        ).check(matches(withLineHeight(desiredLineHeight)))
//        Espresso.onView(withIndex(ViewMatchers.withId(R.id.textTime), 0))
//            .check { view, _ -> checkWeightOfText(weight, view) }
//        //checks font
//        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
//        GenericTextViewTest.checkMatchesTypeface(
//            onView(withIndex(withId(R.id.textTime), 0)), typeface
//        )
    }

    fun iCheckTheRecentRecipeOvenIcon() {
        UiTestingUtils.isViewVisible(R.id.imageCavityIcon)
        UiTestingUtils.isViewEnabled(R.id.imageCavityIcon)
        UiTestingUtils.isViewNotClickable(R.id.imageCavityIcon)
    }

    fun iSetTheTemperatureTo180Degrees() {
        TestingUtils.enterNumberStr("180")
    }

    fun iSetTheCookTime5Min() {
        val cookTime = 5
        val cookTimeSec = TestingUtils.convertTimeToHoursAndMinutes(
            cookTime.toInt().toLong() * 60
        )
        TestingUtils.enterNumberStr(cookTimeSec)
    }

    fun iSetTheTemperatureTo190Degrees() {
        TestingUtils.enterNumberStr("190")
    }

    fun iSetTheCookTimeTo1Min() {
        val cookTime = 1
        val cookTimeSec = TestingUtils.convertTimeToHoursAndMinutes(
            cookTime.toInt().toLong() * 60
        )
        TestingUtils.enterNumberStr(cookTimeSec)
    }

    fun iSetTheTemperatureTo200Degrees() {
        TestingUtils.enterNumberStr("200")
    }

    fun iSetTheCookTimeTo7Min() {
        val cookTime = 7
        val cookTimeSec = TestingUtils.convertTimeToHoursAndMinutes(
            cookTime.toInt().toLong() * 60
        )
        TestingUtils.enterNumberStr(cookTimeSec)
    }

    fun iSetTheTemperatureTo210Degrees() {
        TestingUtils.enterNumberStr("210")
    }

    fun iSetTheCookTimeTo10Min() {
        TestingUtils.enterNumberStr("10")
    }

    fun iSetTheTemperatureTo235Degrees() {
        TestingUtils.enterNumberStr("235")
    }

    fun iSeeMaxLimitReachedPopup() {
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isViewVisible(R.id.text_view_description)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun iCheckMaxLimitReachedPopupTitleView() {
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isViewEnabled(R.id.text_view_title)
        UiTestingUtils.isViewNotClickable(R.id.text_view_title)
    }

    fun iCheckMaxLimitReachedPopupTitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.text_view_title))
            .check(matches((withText("You have reached the max limit [10]"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.text_view_title)),
//            Gravity.CENTER
//        )
        //checks line height & weight
//        val desiredLineHeight = "48"
        val weight = "400"
        Espresso.onView(ViewMatchers.withId(R.id.text_view_title))
            .check { view, _ -> checkWeightOfText(weight, view) }
//        Espresso.onView(ViewMatchers.withId(R.id.text_view_title)).check { view, _ -> checkLineHeightOfText(desiredLineHeight,view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_title)), typeface
        )
    }

    fun iCheckMaxLimitReachedPopupDescriptionView() {
        UiTestingUtils.isViewVisible(R.id.text_view_description)
        UiTestingUtils.isViewEnabled(R.id.text_view_description)
        UiTestingUtils.isViewNotClickable(R.id.text_view_description)
    }

    fun iCheckMaxLimitReachedPopupDescriptionText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.text_view_description))
            .check(matches((withText("To add any new favorites, you will have to delete any of the existing favorite cycles."))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        30f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.text_view_title)),
//            Gravity.CENTER
//        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
        val weight = "300"
        onView(withId(R.id.text_view_description))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_description))
            .check { view, _ -> checkWeightOfText(weight, view) }

        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_description)), typeface
        )
    }

    fun iCheckMaxLimitReachedPopupOKButton() {
        UiTestingUtils.isViewClickable(R.id.text_button_right)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
        UiTestingUtils.isViewEnabled(R.id.text_button_right)

        UiTestingUtils.sleep(300)
        //OK button validation
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches((withText("OKAY"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )

        //checks alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2)), Gravity.CENTER
        )

        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
        val weight = "500"
//        onView(withIndex(withId(R.id.text_view_text_button),2))
//            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(
            withIndex(ViewMatchers.withId(R.id.text_view_text_button), 2)
        )
            .check { view, _ -> checkWeightOfText(weight, view) }

        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.text_view_text_button), 2)), typeface
        )
    }

    fun iClickOnOKButton() {
        LeakAssertions.assertNoLeaks()
        Espresso.onView(withIndex(withId(R.id.text_button_right), 0))
            .perform(click())
        LeakAssertions.assertNoLeaks()
    }

    fun iSeeMultipleFavoritesAddedView() {
        UiTestingUtils.sleep(1500)
        UiTestingUtils.isViewEnabled(R.id.listFavorites)
        onView(withIndex(withId(R.id.imageCavityIcon), 0)).check(matches(isDisplayed()))
    }

    fun iCheckTheLeaveImageSelectionPopupTitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.text_view_title))
            .check(matches((withText("Leave Image Selection"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.text_view_title)),
//            Gravity.CENTER
//        )
        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
//        onView(withId(R.id.text_view_title)).check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_title))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_title)), typeface
        )
    }

    fun iCheckTheLeaveImageSelectionPopupTitleView() {
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isViewEnabled(R.id.text_view_title)
        UiTestingUtils.isViewNotClickable(R.id.text_view_title)
    }

    fun iCheckTheLeaveImageSelectionPopupDescriptionView() {
        UiTestingUtils.isViewVisible(R.id.text_view_description)
        UiTestingUtils.isViewEnabled(R.id.text_view_description)
        UiTestingUtils.isViewNotClickable(R.id.text_view_description)
    }

    fun iCheckTheLeaveImageSelectionPopupDescriptionText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.text_view_description))
            .check(matches((withText("The image you chose will not be saved. Are you sure you want to go back?"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        30f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.text_view_title)),
//            Gravity.CENTER
//        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
        val weight = "300"
        onView(withId(R.id.text_view_description))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_description))
            .check { view, _ -> checkWeightOfText(weight, view) }

        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_description)), typeface
        )
    }

    fun iCheckTheYESButtonView() {
        UiTestingUtils.isViewEnabled(R.id.text_button_right)
        UiTestingUtils.isViewClickable(R.id.text_button_right)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun iCheckTheNOButtonView() {
        UiTestingUtils.isViewEnabled(R.id.text_button_left)
        UiTestingUtils.isViewClickable(R.id.text_button_left)
        UiTestingUtils.isViewVisible(R.id.text_button_left)
    }

    fun iCheckTheYESButtonText() {
        UiTestingUtils.sleep(300)
        //YES button validation
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches((withText("YES"))))
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2)), Gravity.CENTER
        )
        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
        val weight = "500"
//        onView(
//            withIndex(withId(R.id.text_view_text_button),2))
//            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(
            withIndex(ViewMatchers.withId(R.id.text_view_text_button), 2)
        )
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.text_view_text_button), 2)), typeface
        )
    }

    fun iCheckTheNOButtonText() {
        UiTestingUtils.sleep(300)
        //NO button validation
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0))
            .check(matches((withText("NO"))))
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button), 0)), Gravity.CENTER
        )
        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
        val weight = "500"
//        onView(withIndex(withId(R.id.text_view_text_button),0))
//            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(
            withIndex(ViewMatchers.withId(R.id.text_view_text_button), 0)
        )
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.text_view_text_button), 0)), typeface
        )
    }

    fun iCheckTheFavoriteAlreadyExistsPopupTitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.text_view_title))
            .check(matches((withText("Favorite already exist"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.text_view_title)),
//            Gravity.CENTER
//        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 48f)
        val weight = "400"
        onView(withId(R.id.text_view_title))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_title))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_title)), typeface
        )
    }

    fun iCheckTheFavoriteAlreadyExistsPopupTitleView() {
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isViewEnabled(R.id.text_view_title)
        UiTestingUtils.isViewNotClickable(R.id.text_view_title)
    }

    fun iCheckTheFavoriteAlreadyExistsPopupDescriptionText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.text_view_description))
            .check(matches((withText("There already exist a favorite with the same settings."))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        30f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.text_view_title)),
//            Gravity.CENTER
//        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
        val weight = "300"
        onView(withId(R.id.text_view_description))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_description))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_description)), typeface
        )
    }

    fun iCheckTheFavoriteAlreadyExistsPopupDescriptionView() {
        UiTestingUtils.isViewVisible(R.id.text_view_description)
        UiTestingUtils.isViewEnabled(R.id.text_view_description)
        UiTestingUtils.isViewNotClickable(R.id.text_view_description)
    }

    fun iCheckTheOKButtonView() {
        UiTestingUtils.isViewClickable(R.id.text_button_right)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
        UiTestingUtils.isViewEnabled(R.id.text_button_right)
    }

    fun iCheckTheOKButtonText() {
        UiTestingUtils.sleep(300)
        //YES button validation
        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches((withText("OK"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))

        Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
        //checks alignment
        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withIndex(withId(R.id.text_view_text_button), 2)), Gravity.CENTER
        )
        //checks line height & weight
//        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 38.4f)
        val weight = "500"
//        onView(
//            withIndex(withId(R.id.text_view_text_button),2))
//            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(
            withIndex(ViewMatchers.withId(R.id.text_view_text_button), 2)
        )
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withIndex(withId(R.id.text_view_text_button), 2)), typeface
        )
    }

    fun iCheckTheLeftHolderArrow() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.imageArrowLeft)
        UiTestingUtils.isViewVisible(R.id.imageArrowLeft)
        UiTestingUtils.isViewClickable(R.id.imageArrowLeft)

        val desiredHeightInSp = 64f
        val desiredWidthInSp = 64f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.imageArrowLeft)), widthInPixels, heightInPixels
        )

        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.ic_arrow_left)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.imageArrowLeft)),
            leftIcon
        )
    }

    fun iCheckTheStepperBar() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.stepperImages)
        UiTestingUtils.isViewVisible(R.id.stepperImages)
        UiTestingUtils.isViewNotClickable(R.id.stepperImages)
    }

    fun iCheckTheRightHolderArrow() {
        UiTestingUtils.sleep(500)
        UiTestingUtils.isViewEnabled(R.id.imageArrowRight)
        UiTestingUtils.isViewVisible(R.id.imageArrowRight)
        UiTestingUtils.isViewClickable(R.id.imageArrowRight)

        val desiredHeightInSp = 64f
        val desiredWidthInSp = 64f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.imageArrowRight)), widthInPixels, heightInPixels
        )

        val rightIcon = AppCompatResources.getDrawable(context, R.drawable.ic_arrow_right)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.imageArrowRight)),
            rightIcon
        )
    }

    fun iCheckDelayButton() {
        UiTestingUtils.isViewClickable(R.id.btnLeft)
        UiTestingUtils.isViewVisible(R.id.btnLeft)
        UiTestingUtils.isViewEnabled(R.id.btnLeft)

        UiTestingUtils.sleep(300)
        //DELAY button validation
        Espresso.onView(withId(R.id.btnLeft))
            .check(matches((withText("DELAY"))))
        Espresso.onView(withId(R.id.btnLeft))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
        Espresso.onView(withId(R.id.btnLeft))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
    }

    fun iCheckStartButton() {
        UiTestingUtils.isViewClickable(R.id.btnPrimary)
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isViewEnabled(R.id.btnPrimary)

        UiTestingUtils.sleep(300)
        //START button validation
        Espresso.onView(withId(R.id.btnPrimary))
            .check(matches((withText("START"))))
        Espresso.onView(withId(R.id.btnPrimary))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
        Espresso.onView(withId(R.id.btnPrimary))
            .check(
                matches(
                    (textProperties(
                        32f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
    }

    fun iClickDelayButton() {
        UiTestingUtils.performClick(R.id.btnLeft)
    }

    fun iLongClickForSecondsOnTheRecentAddedFavoritesCycle(millis: Long) {
        onView(withIndex(withId((R.id.listFavorites)), 0)).perform(
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

    fun iSeeDeleteAndCancelIcon() {
        onView(RecyclerViewMatcher(R.id.listFavorites).atPositionOnView(0, R.id.imageDeleteFav)).check(
            matches(isDisplayed())
        )
        onView(RecyclerViewMatcher(R.id.listFavorites).atPositionOnView(0, R.id.imageCancel)).check(
            matches(isDisplayed())
        )
    }

    fun iClickOnDeleteIcon() {
        onView(RecyclerViewMatcher(R.id.listFavorites).atPositionOnView(0, R.id.imageDeleteFav)).perform(click())
    }

    fun iSeeDeleteFavoritesPopup() {
        UiTestingUtils.isViewVisible(R.id.popup_with_scroll)
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isViewVisible(R.id.scroll_view)
        UiTestingUtils.isViewVisible(R.id.text_view_description)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
        UiTestingUtils.isViewVisible(R.id.text_button_left)
    }

    fun iCheckTheDeleteFavoritesPopupTitleView() {
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isViewEnabled(R.id.text_view_title)
        UiTestingUtils.isViewNotClickable(R.id.text_view_title)
    }

    fun iCheckTheDeleteFavoritesPopupTitleText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.text_view_title))
            .check(matches((withText("Delete Favorites"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        40f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )

//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.text_view_title)),
//            Gravity.CENTER
//        )
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_title)), typeface
        )
    }

    fun iCheckTheDeleteFavoritesPopupDescriptionText() {
        UiTestingUtils.sleep(300)
        Espresso.onView(withId(R.id.text_view_description))
            .check(matches((withText("Are you sure you want to delete Favorite?"))))
            .check(matches(TestingUtils.withTextColor(Color.parseColor("#ffffff"))))
            .check(
                matches(
                    (textProperties(
                        30f,
                        TextViewPropertiesMatcher.TextProperties.TEXTSIZE
                    ))
                )
            )
//        GenericTextViewTest.checkMatchesGravity(
//            Espresso.onView(withId(R.id.text_view_title)),
//            Gravity.CENTER
//        )
        //checks line height & weight
        val desiredLineHeight = TestingUtils.spToPx(CookingKACucumberTests.context, 36f)
        val weight = "300"
        onView(withId(R.id.text_view_description))
            .check(matches(withLineHeight(desiredLineHeight)))
        Espresso.onView(ViewMatchers.withId(R.id.text_view_description))
            .check { view, _ -> checkWeightOfText(weight, view) }
        //checks font
        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.text_view_description)), typeface
        )
    }

    fun iCheckTheDeleteFavoritesPopupDescriptionView() {
        UiTestingUtils.isViewVisible(R.id.text_view_description)
        UiTestingUtils.isViewEnabled(R.id.text_view_description)
        UiTestingUtils.isViewNotClickable(R.id.text_view_description)
    }

    fun iClickOnNOButton() {
        UiTestingUtils.performClick(R.id.text_button_left)
        UiTestingUtils.sleep(3000)
    }
}





