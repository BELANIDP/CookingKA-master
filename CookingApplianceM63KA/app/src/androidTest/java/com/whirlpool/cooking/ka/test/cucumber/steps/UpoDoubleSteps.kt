package com.whirlpool.cooking.ka.test.cucumber.steps

import com.whirlpool.cooking.ka.test.cucumber.appearance.UpoAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UpoDoubleSteps {
    private var upoAppearanceTest: UpoAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        upoAppearanceTest  = UpoAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }

    @After
    fun tearDown() {
        upoAppearanceTest = null
    }


    @And("I click on Show More in Preference option")
    fun performClickOnShowMorePreferences(){
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        upoAppearanceTest?.performClickShowMorePreferences()
        UiTestingUtils.sleep(1000)
        LeakAssertions.assertNoLeaks()
    }

    @And("I check the preference options")
    fun validatePreferenceScreen(){
        UiTestingUtils.sleep(1000)
        upoAppearanceTest?.preferencesrecyclerlistValidateView()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on Temp calibration option")
    fun performClickOnTempCalibration(){
        LeakAssertions.assertNoLeaks()
        upoAppearanceTest?.performClickOnTempCalibrationOpt()
        LeakAssertions.assertNoLeaks()
    }
    @And("I see Temp calibration option")
    fun scrollOnTempCalibration(){
        upoAppearanceTest?.scrollOnTempCalibrationOpt()
    }
    @And("I see cavity selection screen")
    fun cavitySelectionScreenVisible(){
        upoAppearanceTest?.isCavitySelectionScreenVisible()
    }

    @And("I validate the cavity name and subtext as default")
    fun defaultSubtextValidation(){
        upoAppearanceTest?.defaultSubtextValidation()
    }
    @And("I click on back button on cavity selection screen")
    fun leftButtonClickValidation(){
        LeakAssertions.assertNoLeaks()
        upoAppearanceTest?.leftButtonClickValidation()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on back button on settings screen")
    fun leftButtonClickValidationOnSettingsScreen(){
        LeakAssertions.assertNoLeaks()
        upoAppearanceTest?.leftButtonClickValidation()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on back button on temp calibration screen")
    fun leftButtonClicksValidation(){
        LeakAssertions.assertNoLeaks()
        upoAppearanceTest?.leftButtonClickValidation()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on Set button on temp calibration screen")
    fun setButtonClickValidation(){
        LeakAssertions.assertNoLeaks()
        upoAppearanceTest?.setButtonClickValidation()
        LeakAssertions.assertNoLeaks()
    }
    @And("I validate the height and width of cavity selection buttons")
    fun cavitySelectionButtonValidation(){
        upoAppearanceTest?.cavitySelectionButtonValidation()
    }
    @Then("I see temp calibration tumbler")
    fun tempCalibrationTumblerVisible(){
        upoAppearanceTest?.tempCalibrationTumblerVisible()
    }
    @Then("I validate the title text of tumbler")
    fun tempCalibrationTumblerTitleTextValidation(){
        upoAppearanceTest?.tempCalibrationTumblerTitleTextValidation()
    }
    @Then("I validate the sub text of tumbler")
    fun tempCalibrationTumblerSubTextValidation(){
        upoAppearanceTest?.tempCalibrationTumblerSubTextValidation()
    }
    @Then("I scroll the temp calibration tumbler to required calibration")
    fun tempCalibrationTumblerScrollValidation(){
        upoAppearanceTest?.tempCalibrationTumblerScrollValidation()
    }
    @Then("I scroll the temp calibration tumbler to default temp")
    fun defaultTempCalibrationTumblerScrollValidation(){
        upoAppearanceTest?.defaultTempCalibrationTumblerScrollValidation()
    }
    @Then("I validate that subtext on upper oven button is modified")
    fun upperCavitySelectionButtonSubtextModified(){
        upoAppearanceTest?.upperCavitySelectionButtonSubtextModified()
    }
    @Then("I validate that subtext on temp calibration option is modified")
    fun validateSubtextOnTempCalibrationOptModified(){
        upoAppearanceTest?.validateSubtextOnTempCalibrationOptModified()
    }
    @Then("I validate that subtext on lower oven button is modified")
    fun lowerCavitySelectionButtonSubtextModified(){
        upoAppearanceTest?.lowerCavitySelectionButtonSubtextModified()
    }
    @Then("I check target temp is adjusted acc to offset")
    fun targetTempValidate(){
        upoAppearanceTest?.targetTempValidate()
    }
    @Then("I validate subtext on temp calibration option")
    fun validateSubtextOnTempCalibrationOpt(){
        upoAppearanceTest?.validateSubtextOnTempCalibrationOpt()
    }
}