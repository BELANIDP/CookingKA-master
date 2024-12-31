package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import androidx.appcompat.content.res.AppCompatResources
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isClickable
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotClickable
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.dpToPx
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.isNotDisplayed
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.textProperties
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TextViewPropertiesMatcher
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import com.whirlpool.hmi.uitesting.components.view.GenericViewTest
import com.whirlpool.hmi.utils.CookingSimConst

class DelayAppearanceTest {
    val context: Context = ApplicationProvider.getApplicationContext()
    fun delayScreenValidation(){
        //primary button validation
        Espresso.onView(withId(R.id.btnPrimary))
            .check(matches((withText("START DELAY"))))
            .check(matches((textProperties(32f, TextViewPropertiesMatcher.TextProperties.TEXTSIZE))))
            .check(matches((textProperties(0.05f, TextViewPropertiesMatcher.TextProperties.LETTERSPACING))))
            .check(matches((textProperties(1f, TextViewPropertiesMatcher.TextProperties.LINESPACINGEXTRA))))
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

        val typeface = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.degrees_type)),typeface)
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
        val typeface1 = CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
        GenericTextViewTest.checkMatchesTypeface(
            onView(withId(R.id.tvTitle)),typeface1)
        UiTestingUtils.sleep(1000)

        // back icon validation
        val desiredHeightInSp = 40f
        val desiredWidthInSp = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeightInSp)
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidthInSp)
        GenericTextViewTest.checkMatchesSize(
            onView(withId(R.id.ivLeftIcon)), widthInPixels, heightInPixels)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivLeftIcon)),
            AppCompatResources.getDrawable(context, R.drawable.ic_back_arrow)
        )
        UiTestingUtils.sleep(1000)
    }

    fun delayScreenOvenIconValidation(cavity:String){
        if (cavity=="upper") {
            GenericViewTest.checkMatchesBackground(
                Espresso.onView(withId(R.id.ivOvenCavity)),
                AppCompatResources.getDrawable(context, R.drawable.ic_oven_cavity_large)
            )
        }
        else if(cavity=="lower"){
            GenericViewTest.checkMatchesBackground(
                Espresso.onView(withId(R.id.ivOvenCavity)),
                AppCompatResources.getDrawable(context, R.drawable.ic_lower_cavity_large)
            )
        }
    }

    fun performClickOnStartDelay(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.btnPrimary)
        UiTestingUtils.sleep(3000)
        navigateToSelfCleanStatusScreen()
    }

    fun navigateToSelfCleanStatusScreen() {
        UiTestingUtils.sleep(1000)
        CookingSimConst.simulateDoorLatchLockedEvent(
            CookingKACucumberTests.mainActivity,
            true
        )

        UiTestingUtils.sleep(1000)
        CookingSimConst.simulateDoorLatchUnlockedEvent(
            CookingKACucumberTests.mainActivity,
            true
        )
        UiTestingUtils.sleep(1000)
    }

    fun selfCleanDelayedRunningScreenVisibility(){
        UiTestingUtils.sleep(2000)
        UiTestingUtils.isViewVisible(R.id.tvTemperature)
        UiTestingUtils.isViewVisible(R.id.ivOvenCavity)
        UiTestingUtils.isViewVisible(R.id.tvStartNow)
        UiTestingUtils.isViewEnabled(R.id.progressBarCookTime)
        UiTestingUtils.isViewVisible(R.id.tvCookTime)
    }

    fun selfCleanDelayedRunningScreenValidation(){
        // recipe name validation
        Espresso.onView(withId(R.id.tvTemperature))
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

        // cook time validation
        Espresso.onView(withId(R.id.tvCookTime))
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

        // start now button validation
        Espresso.onView(withId(R.id.tvStartNow))
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

        // lock icon validation
        val desiredHeightWidthLockIconInSp = 32f
        val desiredHeightWidthLockIconInPx = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredHeightWidthLockIconInSp)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(
                withId(R.id.ivStatusIcon1)
            ), desiredHeightWidthLockIconInPx, desiredHeightWidthLockIconInPx
        )
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivStatusIcon1)),
            AppCompatResources.getDrawable(context, R.drawable.icon_32px_lock)
        )

        // oven cavity icon validation
        val desiredHeightWidthOvenIconInSp = 56f
        val desiredHeightWidthOvenIconInSp1 = 40f
        val desiredHeightWidthOvenIconInpx = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredHeightWidthOvenIconInSp)
        val desiredHeightWidthOvenIconInpx1 = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredHeightWidthOvenIconInSp1)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(
                withId(R.id.ivOvenCavity)
            ), desiredHeightWidthOvenIconInpx, desiredHeightWidthOvenIconInpx1
        )

    }

    fun manualDelayedRunningScreenValidation(){
        // recipe name validation
        Espresso.onView(withId(R.id.tvRecipeWithTemperature))
            .check(
                matches(
                    (textProperties(
                        1f,
                        TextViewPropertiesMatcher.TextProperties.LINESPACINGEXTRA
                    ))
                )
            )
            .check(matches(isDisplayed()))

        // cook time validation
        Espresso.onView(withId(R.id.tvCookTimeRemaining))
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
            .check(matches(isNotDisplayed()))

        // start now button validation
        Espresso.onView(withId(R.id.tvResumeCooking))
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

        // oven cavity icon validation
        val desiredHeightWidthOvenIconInSp = 50f
        val desiredHeightWidthOvenIconInSp1 = 33f
        val desiredHeightWidthOvenIconInpx = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredHeightWidthOvenIconInSp)
        val desiredHeightWidthOvenIconInpx1 = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredHeightWidthOvenIconInSp1)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(
                withId(R.id.ivOvenCavity)
            ), desiredHeightWidthOvenIconInpx, desiredHeightWidthOvenIconInpx1
        )
    }

    fun performClickOnDelayOnHorizontalTumbler(){
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClick(R.id.btnGhost)
    }

}