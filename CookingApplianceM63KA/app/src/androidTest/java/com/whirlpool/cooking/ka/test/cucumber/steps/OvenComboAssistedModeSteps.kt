package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.MwoComboAssistedModeAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.When
import org.junit.runner.RunWith

/*
 * File : com.whirlpool.cooking.ka.test.cucumber.steps.MwoComboAssistedModeSteps
 * Author : DUNGAS
 * Created On : 5/28/24, 1:12 PM
 * Details :
 */

@RunWith(AndroidJUnit4::class)
class OvenComboAssistedModeSteps {
    private var mwoComboAssistedModeAppearanceTest: MwoComboAssistedModeAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @Before
    fun setUp() {
        mwoComboAssistedModeAppearanceTest = MwoComboAssistedModeAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }
    @After
    fun tearDown() {
        mwoComboAssistedModeAppearanceTest = null
    }

    @When("I see temp tumbler")
    fun tempTumblerVisible() {
        mwoComboAssistedModeAppearanceTest?.isNoOfServingsTumblerVisible()
    }

    @When("I validate Temperature tumbler title text")
    fun servingsTumblerTitleTextValidation() {
        mwoComboAssistedModeAppearanceTest?.tempTumblerTitleTextValidation()
    }
    @When("I validate Temperature tumbler subtitle text")
    fun servingsTumblerSubTitleTextValidation() {
        mwoComboAssistedModeAppearanceTest?.tempTumblerSubTitleTextValidation()
    }

    @When("I validate Temperature tumbler subtitle text size")
    fun servingsTumblerSubTitleTextSizeValidation() {
        mwoComboAssistedModeAppearanceTest?.servingsTumblerSubTitleTextSizeValidation()
    }

    @When("I validate Temperature tumbler subtitle text color")
    fun servingsTumblerSubTitleTextColorValidation() {
        mwoComboAssistedModeAppearanceTest?.tempTumblerSubTitleTextColorValidation()
    }

    @When("I validate Temperature tumbler oven icon visible")
    fun ovenIconVisibleOnServingsTumbler() {
        mwoComboAssistedModeAppearanceTest?.isOvenIconVisibleOnServingsTumbler()
    }

    @When("I validate Temperature tumbler numpad icon is clickable")
    fun validateNumpadIconOnServingsTumbler() {
        mwoComboAssistedModeAppearanceTest?.performClickOnNumpadButtonOnServingsTumbler()
    }

    @When("I validate Temperature tumbler back icon is clickable")
    fun validateBackIconOnServingsTumbler() {
        mwoComboAssistedModeAppearanceTest?.performClickOnBackButtonOnServingsTumbler()
    }

    @When("I validate Temperature tumbler next button is visible")
    fun nextButtonVisibleOnServingsTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonVisibleOnServingsTumbler()
    }
    @When("I validate Temperature tumbler next button is enabled")
    fun nextButtonEnabledOnServingsTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonEnabledOnServingsTumbler()
    }
    @When("I validate Temperature tumbler next button is clickable")
    fun nextButtonClickableOnServingsTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonClickableOnServingsTumbler()
    }
    @When("I click on Next button on temp tumbler")
    fun nextButtonClickOnTempTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonClickableOnServingsTumbler()
    }
    @When("I validate Temperature tumbler is scrolled to 7 servings")
    fun validateServingTumblerIsScrollable() {
        mwoComboAssistedModeAppearanceTest?.validateServingTumblerIsScrollable()
    }
    @When("I click on Temp section on preview screen")
    fun clickOnWeightOnPreviewScreen() {
        mwoComboAssistedModeAppearanceTest?.performClickOnAmountSectionOnPreviewScreen()
    }
    @When("I click on Time section on preview screen")
    fun clickOnTimeOnPreviewScreen() {
        mwoComboAssistedModeAppearanceTest?.performClickOnDonenessSectionOnPreviewScreen()
    }

}