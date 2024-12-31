package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R

import com.whirlpool.cooking.ka.test.cucumber.appearance.ClockFragmentAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.appearance.SplashFragmentAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.navigation.checkCurrentFragmentDestinationMatched
import com.whirlpool.cooking.ka.test.cucumber.navigation.checkCurrentFragmentDestinationNotMatched
import com.whirlpool.cooking.ka.test.cucumber.navigation.setCurrentDestinationToNavHostController
import core.jbase.AbstractClockFragment
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashSteps {
    private var splashFragmentAppearanceTest: SplashFragmentAppearanceTest? = null
    private var clockFragmentAppearanceTest: ClockFragmentAppearanceTest? = null

    @Before
    fun setUp() {
        splashFragmentAppearanceTest = SplashFragmentAppearanceTest()
        clockFragmentAppearanceTest = ClockFragmentAppearanceTest()
    }

    @After
    fun tearDown() {
        if (splashFragmentAppearanceTest != null) {
            splashFragmentAppearanceTest = null
        }
        if (clockFragmentAppearanceTest != null) {
            clockFragmentAppearanceTest = null
        }
    }

    @Then("Splash screen will be visible")
    @Throws(Exception::class)
    fun checkAllViewsVisibility() {
        splashFragmentAppearanceTest?.checkAllViewsVisibility()
    }

    @Test
    @Then("Splash screen video will be played")
    @Throws(Exception::class)
    fun videoPlaying() {
        setCurrentDestinationToNavHostController(
            AbstractClockFragment::class.java,
            R.navigation.start_up_nav_graph
        )
    }

    @Then("I navigate to clock screen")
    fun checkClockViewVisibility() {
        clockFragmentAppearanceTest?.checkAllViewsVisibility()
    }

    @Test
    @Then("Nav destination will be clock screen")
    fun checkFragmentDestinationSameViewId() {
        checkCurrentFragmentDestinationMatched(
            AbstractClockFragment::class.java,
            currentDesId = R.id.clockFragment,
            expectedDesId = R.id.clockFragment,
            R.navigation.manual_cooking_single_oven
        )
    }

    @Test
    @Then("Nav destination will not be clock screen")
    fun checkFragmentDestinationNotSameViewId() {
        checkCurrentFragmentDestinationNotMatched(
            AbstractClockFragment::class.java,
            currentDesId = R.id.clockFragment,
            expectedDesId = R.id.splashFragment,
            R.navigation.manual_cooking_single_oven
        )
    }
}