package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.appearance.RadioButtonAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.navigation.setFragmentScenario
//import com.whirlpool.cooking.ka.testwidgets.RadioButtonTestFragment
import core.jbase.AbstractClockFragment
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RadioButtonSteps {

    private var radioButtonAppearanceTest: RadioButtonAppearanceTest? = null
    @Before
    fun setUp() {
        radioButtonAppearanceTest = RadioButtonAppearanceTest()
    }

    @After
    fun tearDown() {
        radioButtonAppearanceTest = null
    }

    @And("I navigate to Radio Button")
    fun navigateToListView() {
        testNavigationToClockScreen()
    }

    @Then("Radio Button Screen will be visible")
    fun checkClockViewVisibility() {
        radioButtonAppearanceTest?.checkAllViewsVisibility()
    }

    @Then("I navigate to clock screen for radio button Screen")
    fun navigateToClockScreenForListViewScreen() {
        testNavigationToClockViewScreenForScrollViewScreen()
    }

    @Test
    @UiThreadTest
    fun testNavigationToClockViewScreenForScrollViewScreen() {
        setFragmentScenario(AbstractClockFragment::class.java)
    }

    @Test
    @UiThreadTest
    fun testNavigationToClockScreen() {
//        setFragmentScenario(RadioButtonTestFragment::class.java)
    }

    @Then("I verify in radio button widget that the radio button is checked")
    fun checkIfRadioButtonIsChecked() {
        /*val isChecked: Boolean = radioButtonAppearanceTest?.isRadioButtonIsChecked(
            R.id.Radio1,
            true
        ) ?: false
        assert(isChecked == true) {"Checked Successfully"}*/
    }

    @Then("I verify in radio button widget that the radio button is unchecked")
    fun checkIfRadioButtonIsUnChecked() {
       /* val isChecked: Boolean = radioButtonAppearanceTest?.isRadioButtonIsChecked(
            R.id.Radio1,
            false
        ) ?: false
        assert(isChecked == false) {"Unchecked Successfully"}*/
    }

    @Then("I verify that the radio button is enable")
    fun checkIfRadioButtonIsEnable() {
    /*    val isEnable: Boolean = radioButtonAppearanceTest?.isRadioButtonIsEnabled(
            R.id.Radio1,
            true
        ) ?: false
        assert(isEnable == true) {"Enable Successfully"}*/
    }

    @Then("I verify that the radio button is disable")
    fun checkIfRadioButtonIsDisable() {
     /*   val isEnable: Boolean = radioButtonAppearanceTest?.isRadioButtonIsEnabled(
            R.id.Radio1,
            false
        ) ?: false
        assert(isEnable == false) {"Disable Successfully"}*/
    }
}