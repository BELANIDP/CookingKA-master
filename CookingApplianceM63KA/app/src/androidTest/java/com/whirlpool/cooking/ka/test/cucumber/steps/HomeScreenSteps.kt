package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.HomeScreenAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils

import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenSteps {
    private var homeScreenAppearanceTest: HomeScreenAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        homeScreenAppearanceTest = HomeScreenAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }

    @After
    fun tearDown() {
        homeScreenAppearanceTest = null
    }

    @Then("Verify cavity selection screen {string} {string}")
    fun verifyCavitySelectionScreen(cavity1: String, cavity2: String) {
        homeScreenAppearanceTest?.verifyCavitySelectionScreen(cavity1, cavity2)
    }

    @And("I perform click on clock screen")
    fun performClickOnClockScreen() {
        LeakAssertions.assertNoLeaks()
        homeScreenAppearanceTest?.performClickOnClockScreen()
        LeakAssertions.assertNoLeaks()
    }

    @And("I perform click on home button")
    fun performClickOnHomeButton() {
        hmiKeyUtils?.onButtonClick(CookingKACucumberTests.mainActivity, "1")
    }
}