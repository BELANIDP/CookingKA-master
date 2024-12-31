/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package com.whirlpool.cooking.ka.test.cucumber.steps

import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.appearance.SelfCleanFragmentAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.cooking.ka.test.cucumber.navigation.SelfCleanNavigationHelper
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory.setInScopeViewModel
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.utils.CookingSimConst
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * File        : com.whirlpool.cooking.ka.test.cucumber.steps.SelfCleanSteps
 * Brief       : Self Clean screen step definition automation test cases
 * Author      : GHARDNS/Nikki
 * Created On  : 27/02/2024
 */
@RunWith(JUnit4::class)
class SelfCleanSteps {

    private var selfCleanFragmentAppearanceTest: SelfCleanFragmentAppearanceTest? = null

    private var hmiKeyUtils: HMIKeyUtils? = null

    private var selfCleanNavigationHelper: SelfCleanNavigationHelper? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        selfCleanNavigationHelper = SelfCleanNavigationHelper()
        selfCleanFragmentAppearanceTest = SelfCleanFragmentAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }

    @After
    fun tearDown() {
        selfCleanNavigationHelper = null
        selfCleanFragmentAppearanceTest = null
    }

    /** ================================================================ #
    # =========================  Self Clean Single Oven =============== #
    # ================================================================= */

    /**
     * ---------- Common Methods START ---------------------- *
     */


    @And("I navigate to soil level screen from single oven mode")
    fun iNavigateToSoilScreenFromSingleOvenMode() {
        selfCleanFragmentAppearanceTest?.soilLevelScreenVisibilityValidation()
    }

    @And("I select the {string}")
    fun iSelectTheCavity(cavity: String) {
        setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
    }

    @And("I select the {string} as given variant")
    fun iSelectTheCavityForDoubleOven(cavity: String) {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        if (cavity.equals("upper", ignoreCase = true)) {
            UiTestingUtils.performClick(R.id.upper_oven_layout)
        } else {
            UiTestingUtils.performClick(R.id.lower_oven_layout)
        }
        LeakAssertions.assertNoLeaks()

    }

    @And("I select the soil level as {string}")
    fun iSelectTheSoilLevelAs(soilLevel: String) {

        selfCleanFragmentAppearanceTest?.soilLevelSelectedAs(soilLevel)
    }

    @And("I click on next button on soil level selection screen")
    fun iClickOnNextButtonOnSoilLevelSelection() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.performClickNextButtonSoilLevel()
        LeakAssertions.assertNoLeaks()
    }

    @And("I navigate to instructions screen")
    fun iNavigateToInstructionScreen() {
        //Nav graph will handle the navigation. We have to check Instruction screen component visibility
        selfCleanFragmentAppearanceTest?.instructionScreenVisibilityValidation()
    }

    @And("I click on next button on instructions selection screen")
    fun iClickOnNextButtonOnInstructionsScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.performClickInstructionScreenNextButton()
        LeakAssertions.assertNoLeaks()
    }


    @And("I navigate to prepare oven screen")
    fun iNavigateToPrepareOvenScreen() {
        //Nav graph will handle the navigation. We have to check Prepare Oven screen component visibility
        selfCleanFragmentAppearanceTest?.prepareOvenScreenVisibilityValidation()
    }

    @And("I tap on black area of the screen")
    fun iTapOnBlackAreaOfTheScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.tapOnBlackAreaOfTheScreen()
        LeakAssertions.assertNoLeaks()
    }

    @And("I did not perform any action till 30 second")
    fun iDidNotPerformAnyActionTill30Seconds() {
        selfCleanFragmentAppearanceTest?.wait30Seconds()
    }

    @And("I click on HMI key clean button on variant {string}")
    fun iClickOnHMIKeyCleanButton(cavityType: String) {
        LeakAssertions.assertNoLeaks()
        if (cavityType.equals("upper", ignoreCase = true)) {
            hmiKeyUtils?.pressUpperOvenCleanButton(CookingKACucumberTests.mainActivity)
        } else {
            hmiKeyUtils?.pressLowerOvenCleanButton(CookingKACucumberTests.mainActivity)
        }
        LeakAssertions.assertNoLeaks()
    }

    @And("I click on HMI key clean button on variant")
    fun iClickOnHMIKeyCleanButton() {
        LeakAssertions.assertNoLeaks()
        hmiKeyUtils?.pressUpperOvenCleanButton(CookingKACucumberTests.mainActivity)
        LeakAssertions.assertNoLeaks()
    }

    @And("I open and close the door of {string}")
    fun iOpenCloseDoor(cavityType: String) {
        if (cavityType.equals("upper", ignoreCase = true)) {
            hmiKeyUtils?.openAndCloseUpperOvenDoor(CookingKACucumberTests.mainActivity)
        } else {
            hmiKeyUtils?.openAndCloseLowerOvenDoor(CookingKACucumberTests.mainActivity)
        }
        UiTestingUtils.sleep(1000)
        if (cavityType.equals("upper", ignoreCase = true)) {
            hmiKeyUtils?.openAndCloseUpperOvenDoor(CookingKACucumberTests.mainActivity)
        } else {
            hmiKeyUtils?.openAndCloseLowerOvenDoor(CookingKACucumberTests.mainActivity)
        }
    }

    @And("I click on start button on door has opened closed screen")
    fun iClickOnStartButtonOnDoorHasOpenedClosedScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.performClickDoorHasOpenedClosedScreenNextButton()
        LeakAssertions.assertNoLeaks()
    }

    @And("I did not perform any action")
    fun iDidNotPerformAnyAction() {
        selfCleanFragmentAppearanceTest?.wait10Seconds()
    }

    @And("I navigate to locking oven door screen")
    fun iNavigateToLockingOvenDoorScreen() {
        //Nav graph will handle the navigation. We have to check Locking Oven Door screen component visibility
        UiTestingUtils.sleep(2000)
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenVisibilityValidation()
    }


    @And("After cycle completed of {string}")
    fun iAfterDefinedTime(soilLevel: String) {
        val time: Long = when (soilLevel) {
            "low" -> {
                2000
            }

            "medium" -> {
                3000
            }

            "high" -> {
                4000
            }

            else -> {
                0
            }
        }
        selfCleanFragmentAppearanceTest?.waitGivenSeconds(time)
    }

    @And("I locked the {string} door")
    fun iLockedDoor(ovenVariant: String) {
        UiTestingUtils.sleep(3000)
        val isPrimary: Boolean = ovenVariant.equals("upper", ignoreCase = true)
        CookingSimConst.simulateDoorLatchLockedEvent(
            CookingKACucumberTests.mainActivity,
            isPrimary
        )
        UiTestingUtils.sleep(1000)
    }

    @And("I unlocked the {string} door")
    fun iUnLockedDoor(ovenVariant: String) {
        UiTestingUtils.sleep(1000)
        val isPrimary: Boolean = ovenVariant.equals("upper", ignoreCase = true)
        CookingSimConst.simulateDoorLatchUnlockedEvent(
            CookingKACucumberTests.mainActivity,
            isPrimary
        )
        UiTestingUtils.sleep(1000)
    }


    /**
     * ---------- Common Methods END ---------------------- *
     */


    @Then("I check soil level screen header {string} text")
    fun iCheckSoilLevelScreenHeaderTitle(title: String) {
        selfCleanFragmentAppearanceTest?.headerTitleTextValidation(title)
    }

    @Then("I check soil level screen header title text size")
    fun iCheckSoilLevelScreenHeaderTitleTextSize() {
        selfCleanFragmentAppearanceTest?.headerTitleTextSizeValidation()
    }

    @Then("I check soil level screen header title text alignment")
    fun iCheckSoilLevelScreenHeaderTitleTextAlignment() {
        selfCleanFragmentAppearanceTest?.headerTitleTextAlignmentValidation()
    }

    @Then("I check soil level screen header title text color")
    fun iCheckSoilLevelScreenHeaderTitleTextColor() {
        selfCleanFragmentAppearanceTest?.headerTitleTextColorValidation()
    }

    @Then("I check soil level screen header title view should not clickable")
    fun iCheckSoilLevelScreenHeaderTitleViewNotClickable() {
        selfCleanFragmentAppearanceTest?.headerTitleTextViewIsNotClickable()
    }

    @Then("I check soil level screen header title view is enabled")
    fun iCheckSoilLevelScreenHeaderTitleViewEnabled() {
        selfCleanFragmentAppearanceTest?.headerTitleTextViewIsEnabled()
    }

    @Then("I check soil level screen header back arrow view is enabled")
    fun iCheckSoilLevelScreenHeaderBackArrowViewEnabled() {
        selfCleanFragmentAppearanceTest?.headerBackArrowViewIsEnabled()
    }

    @Then("I check soil level screen header back arrow view is clickable")
    fun iCheckSoilLevelScreenHeaderBackArrowViewClickable() {
        selfCleanFragmentAppearanceTest?.headerBackArrowViewIsClickable()
    }

    @And("I click on soil level screen header back arrow")
    fun iClickOnSoilLevelScreenHeaderBackArrow() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(2000)
        selfCleanNavigationHelper?.performClickOnSoilLevelHeaderBackArrow()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I expect it should navigate to previous screen")
    fun iExpectItShouldNavigateToSettingsScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        selfCleanFragmentAppearanceTest?.headerBackArrowSettingsScreenNavigation()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I expect it should navigate to cavity selection screen")
    fun iExpectItShouldNavigateToCavitySelectionScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        selfCleanFragmentAppearanceTest?.headerBackArrowCavitySelectionScreenNavigation()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I check soil level screen header back arrow background")
    fun iCheckSoilLevelScreenHeaderBackArrowBackground() {
        selfCleanFragmentAppearanceTest?.headerBackArrowBackgroundValidation()
    }

    @Then("I check soil level screen header cavity icon is not visible")
    fun iCheckSoilLevelScreenHeaderCavityIconIsNotVisible() {
        selfCleanFragmentAppearanceTest?.headerCavityIconViewIsNotVisible()
    }

    @Then("I check soil level screen header cavity icon is visible")
    fun iCheckSoilLevelScreenHeaderCavityIconIsVisible() {
        selfCleanFragmentAppearanceTest?.headerCavityIconViewIsVisible()
    }

    @Then("I expect it should display selected soil level clean type")
    fun iExpectItShouldDisplaySoilLevelSelectedType() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.soilLevelSelectedTypeTextValidation()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I check soil level screen next button text")
    fun iCheckSoilLevelNextButtonText() {
        selfCleanFragmentAppearanceTest?.soilLevelNextButtonTextValidation()
    }

    @Then("I check soil level screen next button color")
    fun iCheckSoilLevelNextButtonTextColor() {
        UiTestingUtils.sleep(300)
        selfCleanFragmentAppearanceTest?.soilLevelNextButtonTextColorValidation()
    }

    @Then("I check soil level screen next button size")
    fun iCheckSoilLevelNextButtonTextSize() {
        UiTestingUtils.sleep(300)
        selfCleanFragmentAppearanceTest?.soilLevelNextButtonTextSizeValidation()
    }

    @Then("I check soil level screen next button text alignment")
    fun iCheckSoilLevelNextButtonTextAlignment() {
        //Next button has no gravity
    }

    @Then("I check soil level screen next button is clickable")
    fun iCheckSoilLevelNextButtonViewIsClickable() {
        UiTestingUtils.sleep(300)
        selfCleanFragmentAppearanceTest?.soilLevelNextButtonViewIsClickable()
    }

    @Then("I check soil level screen next button is enabled")
    fun iCheckSoilLevelNextButtonViewIsEnabled() {
        UiTestingUtils.sleep(300)
        selfCleanFragmentAppearanceTest?.soilLevelNextButtonViewIsEnabled()
    }

    @Then("I expect it should navigate to instructions screen")
    fun iExpectItShouldNavigateToInstructionScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(2000)
        selfCleanFragmentAppearanceTest?.instructionScreenVisibilityValidation()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I check instructions screen header {string} text")
    fun iCheckInstructionScreenHeaderText(title: String) {
        selfCleanFragmentAppearanceTest?.instructionScreenTitleTextValidation(title)
    }

    @Then("I check instructions screen header title text size")
    fun iCheckInstructionScreenHeaderTextSize() {
        selfCleanFragmentAppearanceTest?.instructionScreenTitleTextSizeValidation()
    }

    @Then("I check instructions screen header title text color")
    fun iCheckInstructionScreenHeaderTextColor() {
        selfCleanFragmentAppearanceTest?.instructionScreenTitleTextColorValidation()
    }

    @Then("I check instructions screen header title text alignment")
    fun iCheckInstructionScreenHeaderTextAlignment() {
        selfCleanFragmentAppearanceTest?.instructionScreenTitleTextAlignmentValidation()
    }

    @Then("I check instructions screen header title view should not clickable")
    fun iCheckInstructionScreenHeaderTitleViewIsNotClickable() {
        selfCleanFragmentAppearanceTest?.instructionScreenTitleTextViewIsNotClickable()
    }

    @Then("I check instructions screen header title view is enabled")
    fun iCheckInstructionScreenHeaderTitleViewIsEnabled() {
        selfCleanFragmentAppearanceTest?.instructionScreenTitleTextViewIsEnabled()
    }

    @Then("I check instructions screen header back arrow view is enabled")
    fun iCheckInstructionScreenHeaderBackViewIsEnabled() {
        selfCleanFragmentAppearanceTest?.instructionScreenBackArrowViewIsEnabled()
    }

    @Then("I check instructions screen header back arrow view is clickable")
    fun iCheckInstructionScreenHeaderBackViewIsClickable() {
        selfCleanFragmentAppearanceTest?.instructionScreenBackArrowViewIsClickable()
    }

    @And("I click on instructions screen header back arrow")
    fun iClickOnInstructionScreenHeaderBackArrow() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.performClickOnHeaderBackArrow()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I expect it should navigate to soil level screen")
    fun iExpectItShouldNavigateToSoilLevelScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.soilLevelScreenVisibilityValidation()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I check instructions screen header back arrow background")
    fun iCheckInstructionScreenHeaderBackArrowBackground() {
        selfCleanFragmentAppearanceTest?.instructionScreenBackArrowBackgroundValidation()
    }

    @Then("I check instructions screen header cavity icon is not visible")
    fun iCheckInstructionScreenHeaderCavityIconVisibility() {
        selfCleanFragmentAppearanceTest?.instructionScreenHeaderCavityIconVisibility()
    }

    @Then("I check instructions screen header cavity icon is visible")
    fun iCheckInstructionScreenHeaderCavityIconIsVisibility() {
        selfCleanFragmentAppearanceTest?.headerCavityIconViewIsVisible()
    }

    @Then("I check instructions screen vertical scroll is enabled")
    fun iCheckInstructionScreenVerticalScrollEnabled() {
        selfCleanFragmentAppearanceTest?.instructionScreenVerticalScrollEnabled()
    }

    @Then("I expect instructions screen vertical scroll downwards should working")
    fun iExpectInstructionScreenVerticalScrollDownwardWorks() {
        selfCleanFragmentAppearanceTest?.instructionScreenVerticalScrollDownwardWorks()
    }

    @Then("I expect instructions screen vertical scroll upwards should working")
    fun iExpectInstructionScreenVerticalScrollUpwardsWorks() {
        selfCleanFragmentAppearanceTest?.instructionScreenVerticalScrollUpwardWorks()
    }

    @Then("I check instructions screen description text")
    fun iCheckInstructionScreenDescriptionText() {
        selfCleanFragmentAppearanceTest?.instructionScreenDescriptionTextValidation()
    }


    @Then("I check instructions screen description size")
    fun iCheckInstructionScreenHeaderDescriptionTextSize() {
        selfCleanFragmentAppearanceTest?.instructionScreenDescriptionTextSizeValidation()
    }

    @Then("I check instructions screen description color")
    fun iCheckInstructionScreenHeaderDescriptionTextColor() {
        selfCleanFragmentAppearanceTest?.instructionScreenDescriptionTextColorValidation()
    }

    @Then("I check instructions screen description alignment")
    fun iCheckInstructionScreenHeaderDescriptionTextAlignment() {
        selfCleanFragmentAppearanceTest?.instructionScreenDescriptionTextAlignmentValidation()
    }

    @Then("I check instructions next button text")
    fun iCheckInstructionScreenNextButtonText() {
        selfCleanFragmentAppearanceTest?.instructionScreenNextButtonTextValidation()
    }

    @Then("I check instructions next button color")
    fun iCheckInstructionScreenNextButtonTextColor() {
        selfCleanFragmentAppearanceTest?.instructionScreenNextButtonTextColorValidation()
    }

    @Then("I check instructions next button size")
    fun iCheckInstructionScreenNextButtonTextSize() {
        selfCleanFragmentAppearanceTest?.instructionScreenNextButtonTextSizeValidation()
    }

    @Then("I check instructions next button text alignment")
    fun iCheckInstructionScreenNextButtonTextAlignment() {
        selfCleanFragmentAppearanceTest?.instructionScreenNextButtonTextGravityValidation()
    }

    @Then("I check instructions screen next button is clickable")
    fun iCheckInstructionScreenNextButtonViewIsClickable() {
        selfCleanFragmentAppearanceTest?.instructionScreenNextButtonViewIsClickable()
    }

    @Then("I check instructions screen next button is enabled")
    fun iCheckInstructionScreenNextButtonViewIsEnabled() {
        selfCleanFragmentAppearanceTest?.instructionScreenNextButtonViewIsEnabled()
    }

    @Then("I expect it should navigate to prepare oven screen")
    fun iExpectItShouldNavigatePrepareOvenScreen() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenVisibilityValidation()
    }

    @Then("I expect prepare oven screen should visible")
    fun iExpectPrepareOvenScreenShouldVisible() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenVisibilityValidation()
    }

    @Then("I check prepare oven title text")
    fun iCheckPrepareOvenScreenTitleText() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenTitleTextValidation()
    }

    @Then("I check prepare oven title color")
    fun iCheckPrepareOvenScreenTitleTextColor() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenTitleTextColorValidation()
    }

    @Then("I check prepare oven title size")
    fun iCheckPrepareOvenScreenTitleTextSizeColor() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenTitleTextSizeValidation()
    }


    @Then("I check prepare oven title view should not clickable")
    fun iCheckPrepareOvenTitleViewIsNotClickable() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenTitleViewIsNotClickable()
    }

    @Then("I check prepare oven title view is enabled")
    fun iCheckPrepareOvenTitleViewIsEnabled() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenTitleViewIsEnabled()
    }

    @Then("I check prepare oven description text")
    fun iCheckPrepareOvenScreenDescriptionText() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenDescriptionTextValidation()
    }

    @Then("I check prepare oven description color")
    fun iCheckPrepareOvenScreenDescriptionTextColor() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenDescriptionTextColorValidation()
    }

    @Then("I check prepare oven description size")
    fun iCheckPrepareOvenScreenDescriptionTextSizeColor() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenDescriptionTextSizeValidation()
    }

    @Then("I check prepare oven description alignment")
    fun iCheckPrepareOvenScreenDescriptionTextAlignment() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenDescriptionTextAlignmentValidation()
    }

    @Then("I check prepare oven description view should not clickable")
    fun iCheckPrepareOvenScreenDescriptionViewIsNotClickable() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenDescriptionViewIsNotClickable()
    }

    @Then("I check prepare oven description view is enabled")
    fun iCheckPrepareOvenScreenDescriptionViewIsEnabled() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenDescriptionViewIsEnabled()
    }

    @Then("I check it should not take user back to the previous screen from prepare oven screen")
    fun iCheckPrepareOvenTapOnBlackAreaNavigation() {
        selfCleanFragmentAppearanceTest?.prepareOvenScreenVisibilityValidation()
    }

    /** ------------------ Prepare Oven Clean Button Behaviour ----------------    */
    @Then("I check clean button timeout and stop flashing")
    fun iCheckCleanButtonTimeoutAndStopFlashing() {
        //On HMI we have to check flashing and stop cycle
    }

    @Then("I check clean button timeout and should navigate to settings screen")
    fun iCheckCleanButtonTimeoutAndNavigationToSettingsScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.cleanButtonTimeoutAndNavigationToSettingsScreen()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I expect it should navigate to door hasn't opened closed screen")
    fun iExpectItShouldNavigateToDoorHasNotOpenedClosedScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.prepareOvenScreenNavigateToDoorHasntOpenedClosed()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I expect the door has opened and closed screen should visible")
    fun iExpectItShouldNavigateToDoorHasOpenedClosedScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.prepareOvenScreenNavigateToDoorHasOpenedClosed()
        LeakAssertions.assertNoLeaks()
    }


    /* Door has opened closed screen Title Text */

    @Then("I check door has opened closed screen title {string} text")
    fun iCheckDoorHasOpenedClosedScreenTitleText(title: String) {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenTitleTextValidation(title)
    }

    @Then("I check door has opened closed screen title color")
    fun iCheckDoorHasOpenedClosedScreenTitleTextColor() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenTitleTextColorValidation()
    }

    @Then("I check door has opened closed screen title size")
    fun iCheckDoorHasOpenedClosedScreenTitleTextSizeColor() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenTitleTextSizeValidation()
    }


    @Then("I check door has opened closed screen title view should not clickable")
    fun iCheckDoorHasOpenedClosedScreenTitleViewIsNotClickable() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenTitleViewIsNotClickable()
    }

    @Then("I check door has opened closed screen title view is enabled")
    fun iCheckDoorHasOpenedClosedScreenTitleViewIsEnabled() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenTitleViewIsEnabled()
    }

    /* Door has opened closed screen Description Text */

    @Then("I check door has opened closed screen description {string} text")
    fun iCheckDoorHasOpenedClosedScreenDescriptionText(description: String) {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDescriptionTextValidation(
            description
        )
    }


    @Then("I check door has opened closed screen description color")
    fun iCheckDoorHasOpenedClosedScreenDescriptionTextColor() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDescriptionTextColorValidation()
    }

    @Then("I check door has opened closed screen description size")
    fun iCheckDoorHasOpenedClosedScreenDescriptionTextSizeColor() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDescriptionTextSizeValidation()
    }

    @Then("I check door has opened closed screen description alignment")
    fun iCheckDoorHasOpenedClosedScreenDescriptionTextAlignment() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDescriptionTextAlignmentValidation()
    }

    @Then("I check door has opened closed screen description view is not clickable")
    fun iCheckDoorHasOpenedClosedScreenDescriptionViewIsNotClickable() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDescriptionViewIsNotClickable()
    }

    @Then("I check door has opened closed screen description view is enabled")
    fun iCheckDoorHasOpenedClosedScreenDescriptionViewIsEnabled() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDescriptionViewIsEnabled()
    }

    /* Door has opened closed screen Start button */

    @Then("I check door has opened closed screen start {string} text")
    fun iCheckDoorHasOpenedClosedScreenButtonText(buttonText: String) {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenStartButtonTextValidation(
            buttonText
        )
    }


    @Then("I check door has opened closed screen start button text color")
    fun iCheckDoorHasOpenedClosedScreenStartButtonTextColor() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenStartButtonTextColor()
    }

    @Then("I check door has opened closed screen start button text size")
    fun iCheckDoorHasOpenedClosedScreenStartButtonTextSize() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenStartButtonTextSize()
    }

    @Then("I check door has opened closed screen start button view is clickable")
    fun iCheckDoorHasOpenedClosedScreenStartButtonViewIsClickable() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenStartButtonViewIsClickable()
    }

    @Then("I check door has opened closed screen start button view is enabled")
    fun iCheckDoorHasOpenedClosedScreenStartButtonViewIsEnable() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenStartButtonViewIsEnabled()
    }

    @Then("I expect it should navigate to locking oven door screen")
    fun iExpectItShouldNavigateToLockingOvenDoorScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.lockingOvenDoorVisibility()
        LeakAssertions.assertNoLeaks()
    }

    /* Door has opened closed screen Delay button */

    @Then("I check door has opened closed screen delay {string} text")
    fun iCheckDoorHasOpenedClosedScreenDelayButtonText(buttonText: String) {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDelayButtonTextValidation(
            buttonText
        )
    }

    @Then("I check door has opened closed screen delay button text color")
    fun iCheckDoorHasOpenedClosedScreenDelayButtonTextColor() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDelayButtonTextColor()
    }

    @Then("I check door has opened closed screen delay button text size")
    fun iCheckDoorHasOpenedClosedScreenDelayButtonTextSize() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDelayButtonTextSize()
    }

    @Then("I check door has opened closed screen delay button view is clickable")
    fun iCheckDoorHasOpenedClosedScreenDelayButtonViewIsClickable() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDelayButtonViewIsClickable()
    }

    @Then("I click on delay button on door has opened closed screen")
    fun iClickOnDelayButtonOnDoorHasOpenedClosedScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.performClickDoorHasOpenedClosedScreenDelayButton()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I check door has opened closed screen delay button view is enabled")
    fun iCheckDoorHasOpenedClosedScreenDelayButtonViewIsEnable() {
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenDelayButtonViewIsEnabled()
    }

    @Then("I expect it should navigate to start after delay screen")
    fun iExpectItShouldNavigateToDelayScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.startAfterDelayScreenVisibility()
        LeakAssertions.assertNoLeaks()

    }

    @Then("I click on start now button")
    fun iClickOnStartNowButtonScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.performStartNowButton()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I click on start now button from combo")
    fun iClickOnStartNowButtonScreenFromCombo() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.performStartDelayButtonFromCombo()
        LeakAssertions.assertNoLeaks()
    }


    @Then("I click on cancel now button")
    fun iClickOnCancelDelayButtonScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.performCancelDelayButton()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I check delay screen navigation")
    fun iClickOnStartNowButtonNavigationScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(3000)
        LeakAssertions.assertNoLeaks()
    }

    @Then("I check delay screen cancel navigation")
    fun iClickOnStartNowButtonCancelNavigationScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.nested_scroll_view_collection)
        LeakAssertions.assertNoLeaks()
    }


    /* ----------- Door has Opened/Closed Screen Behaviour ------------- */

    @And("I navigate to door has opened and closed screen")
    fun iNavigateToDoorHasOpenedClosedScreen() {
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(2000)
        //Nav Graph will handle the navigation. We have to check door has opened/closed screen visibility
        selfCleanFragmentAppearanceTest?.doorHasOpenedClosedScreenVisibility()
        LeakAssertions.assertNoLeaks()
    }

    @And("I check global sleep timeout")
    fun iCheckGlobalSleepTimeout() {
        //Check global sleep timeout on Real HMI
    }

    /*-------------------------------------------------------------------------------------------------- */

    /* ----------- Door hasn't Opened/Closed Screen Test Cases  ------------- */

    @Then("I expect the door hasn't opened and closed screen should visible")
    fun iExpectDoorHasntOpenedClosedScreenShouldVisible() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenVisibility()
    }

    /* Door hasn't opened closed screen Title Text */

    @Then("I check door hasn't opened closed screen title {string} text")
    fun iCheckDoorHasntOpenedClosedScreenTitleText(title: String) {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenTitleTextValidation(title)
    }

    @Then("I check door hasn't opened closed screen title color")
    fun iCheckDoorHasntOpenedClosedScreenTitleTextColor() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenTitleTextColorValidation()
    }

    @Then("I check door hasn't opened closed screen title size")
    fun iCheckDoorHasntOpenedClosedScreenTitleTextSizeColor() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenTitleTextSizeValidation()
    }

    @Then("I check door hasn't opened closed screen title alignment")
    fun iCheckDoorHasntOpenedClosedScreenTitleTextAlignment() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenTitleTextAlignmentValidation()
    }

    @Then("I check door hasn't opened closed screen title view should not clickable")
    fun iCheckDoorHasntOpenedClosedScreenTitleViewIsNotClickable() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenTitleViewIsNotClickable()
    }

    @Then("I check door hasn't opened closed screen title view is enabled")
    fun iCheckDoorHasntOpenedClosedScreenTitleViewIsEnabled() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenTitleViewIsEnabled()
    }

    /*-------------------------------------------------------------------------------------------------- */

    /* Door hasn't opened closed screen Description Text */

    @Then("I check door hasn't opened closed screen description {string} text")
    fun iCheckDoorHasntOpenedClosedScreenDescriptionText(description: String) {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenDescriptionTextValidation(
            description
        )
    }


    @Then("I check door hasn't opened closed screen description color")
    fun iCheckDoorHasntOpenedClosedScreenDescriptionTextColor() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenDescriptionTextColorValidation()
    }

    @Then("I check door hasn't opened closed screen description size")
    fun iCheckDoorHasntOpenedClosedScreenDescriptionTextSizeColor() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenDescriptionTextSizeValidation()
    }

    @Then("I check door hasn't opened closed screen description alignment")
    fun iCheckDoorHasntOpenedClosedScreenDescriptionTextAlignment() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenDescriptionTextAlignmentValidation()
    }

    @Then("I check door hasn't opened closed screen description view is not clickable")
    fun iCheckDoorHasntOpenedClosedScreenDescriptionViewIsNotClickable() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenDescriptionViewIsNotClickable()
    }

    @Then("I check door hasn't opened closed screen description view is enabled")
    fun iCheckDoorHasntOpenedClosedScreenDescriptionViewIsEnabled() {
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenDescriptionViewIsEnabled()
    }

    @And("I navigate to door hasn't opened and closed screen")
    fun iNavigateToDoorHasntOpenedClosedScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.doorHasntOpenedClosedScreenVisibility()
        LeakAssertions.assertNoLeaks()
    }


    /*-------------------------------------------------------------------------------------------------- */


    /* Locking Door Oven Title Text */

    @Then("I check locking oven door screen title {string} text")
    fun iCheckLockingOvenDoorScreenTitleText(buttonText: String) {
        /*  selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenTitleTextValidation(
              buttonText
          )*/
    }

    @Then("I check locking oven door screen title text color")
    fun iCheckLockingOvenDoorScreenTitleTextColor() {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenTitleTextColorValidation()
    }

    @Then("I check locking oven door screen title text size")
    fun iCheckLockingOvenDoorScreenTitleTextSize() {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenTitleTextSizeValidation()
    }

    @Then("I check locking oven door screen title text view is not clickable")
    fun iCheckLockingOvenDoorScreenTitleViewIsClickable() {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenTitleViewIsNotClickable()
    }

    @Then("I check locking oven door screen title text view is enabled")
    fun iCheckLockingOvenDoorScreenTitleViewIsEnable() {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenTitleViewIsEnabled()
    }

    /*-------------------------------------------------------------------------------------------------- */

    /* Locking Door Oven notification Text */

    @Then("I check locking oven door screen notification {string} text")
    fun iCheckLockingOvenDoorScreenNotificationText(buttonText: String) {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenNotificationTextValidation(
            buttonText
        )
    }

    @Then("I check locking oven door screen notification text color")
    fun iCheckLockingOvenDoorScreenNotificationTextColor() {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenNotificationTextColorValidation()
    }

    @Then("I check locking oven door screen notification text size")
    fun iCheckLockingOvenDoorScreenNotificationTextSize() {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenNotificationTextSizeValidation()
    }

    @Then("I check locking oven door screen notification text view is not clickable")
    fun iCheckLockingOvenDoorScreenNotificationViewIsClickable() {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenNotificationViewIsNotClickable()
    }

    @Then("I check locking oven door screen notification text view is enabled")
    fun iCheckLockingOvenDoorScreenNotificationViewIsEnable() {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenNotificationViewIsEnabled()
    }

    @Then("I check it should not take user back to the previous screen from locking oven screen")
    fun iCheckLockingOvenDoorTapOnBlackAreaNavigation() {
        selfCleanFragmentAppearanceTest?.lockingOvenDoorScreenTitleViewIsEnabled()
    }

    @Then("I check after defined time it should navigate to self clean status screen")
    fun iCheckAfterDefinedTimeItNavigateToSelfCleanStatusScreen() {
        selfCleanFragmentAppearanceTest?.definedTimeShouldNavigateToSelfCleanStatusScreen()
    }

    @And("After defined time")
    fun iAfterDefinedTime() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.navigateToSelfCleanStatusScreen()
        LeakAssertions.assertNoLeaks()
    }

    @And("After defined time from combo variant")
    fun iAfterDefinedTimeFromCombo() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.navigateToSelfCleanStatusScreenFromComboVariant()
        LeakAssertions.assertNoLeaks()
    }

    /*-------------------------------------------------------------------------------------------------- */


    /*----------------------------------------------------------------------- */
    /* ----------- Self Clean Status Test Cases  ---------------------------- */
    /*----------------------------------------------------------------------- */

    /* --------------- Self Clean Status: soilLevel Text Appearance  ------------ */

    @Then("I check self clean status screen soilLevel {string} text matched")
    fun iCheckSelfCleanStatusSoilLevelText(text: String) {
        selfCleanFragmentAppearanceTest?.selfCleanStatusSoilLevelTextValidation(
            text
        )
    }

    @Then("I check self clean status screen soilLevel text size")
    fun iCheckSelfCleanStatusSoilLevelTextSize() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusSoilLevelTextSizeValidation()
    }

    @Then("I check self clean status screen soilLevel text color")
    fun iCheckSelfCleanStatusSoilLevelTextColor() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusSoilLevelColorValidation()
    }

    @Then("I check self clean status screen soilLevel text alignment")
    fun iCheckSelfCleanStatusSoilLevelTextAlignment() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusSoilLevelAlignmentValidation()
    }

    @Then("I check self clean status screen soilLevel text view is not clickable")
    fun iCheckSelfCleanStatusSoilLevelTextIsNotClickable() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusSoilLevelIsNotClickable()
    }

    @Then("I check self clean status screen soilLevel text is enabled")
    fun iCheckSelfCleanStatusSoilLevelTextIsEnabled() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusSoilLevelIsEnabled()
    }

    /* --------------- Self Clean Status: Timer Text Appearance  ------------ */

    @Then("I check self clean status screen timer {string} text matched")
    fun iCheckSelfCleanTimerText(text: String) {
        selfCleanFragmentAppearanceTest?.selfCleanStatusTimerTextValidation(
            text
        )
    }

    @Then("I check self clean status screen timer text size")
    fun iCheckSelfCleanStatusTimerTextSize() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusTimerTextSizeValidation()
    }

    @Then("I check self clean status screen timer text color")
    fun iCheckSelfCleanStatusTimerTextColor() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusTimerColorValidation()
    }

    /* --------------- Self Clean Status: Start Now Button Content  ------------ */
    @Then("I check self clean status screen start now button content")
    fun iCheckSelfCleanStartNowButtonText() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusStatNowTextValidation()
    }
    /* --------------- Self Clean Status: Oven Locked Icon Appearance ------------ */
    @Then("I cancel the self clean cycle")
    fun iCancelSelfCleanCycle() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.selfCleanCycleCancel()
        LeakAssertions.assertNoLeaks()
    }


    /* ---- Self Clean Status: Cycle Completed Cleaning Completed Title Text Appearance  ---------- */


    @Then("I expect self clean completed screen should visible")
    fun iExpectSelfCleanCompleteScreenVisible() {
        LeakAssertions.assertNoLeaks()
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedVisibility()
        LeakAssertions.assertNoLeaks()
    }


    @Then("I check self clean status screen cycle completed {string} matched")
    fun iCheckSelfCleanStatusCycleCompletedTitleText(text: String) {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedTextValidation(
            text
        )
    }

    @Then("I check self clean status screen cycle completed text size")
    fun iCheckSelfCleanStatusCycleCompletedTitleTextSize() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedTextSizeValidation()
    }

    @Then("I check self clean status screen cycle completed text color")
    fun iCheckSelfCleanStatusCycleCompletedTitleTextColor() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedColorValidation()
    }

    /* ---- Self Clean Status: Cycle Completed Cleaning Completed Description Text Appearance  ---------- */

    @Then("I check self clean status screen cycle completed description {string} matched")
    fun iCheckSelfCleanStatusCycleCompletedDescriptionText(text: String) {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedDescriptionTextValidation(
            text
        )
    }

    @Then("I check self clean status screen cycle completed description text size")
    fun iCheckSelfCleanStatusCycleCompletedDescriptionTextSize() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedDescriptionTextSizeValidation()
    }

    @Then("I check self clean status screen cycle completed description text color")
    fun iCheckSelfCleanStatusCycleCompletedDescriptionTextColor() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedDescriptionColorValidation()
    }


    /* ---- Self Clean Completed: Cavity Icon Appearance  ---------- */

    @Then("I check self clean complete cavity icon is visible")
    fun iCheckSelfCleanStatusCycleCompletedCavityIcon() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedCavityIconVisibility()
    }

    @Then("I check self clean complete cavity icon is not visible")
    fun iCheckSelfCleanStatusCycleCompletedCavityIconNotVisible() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedCavityIconNotVisibility()
    }

    @Then("I check self clean complete icon is enabled")
    fun iCheckSelfCleanStatusCycleCompletedCavityIconIsEnabled() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedCavityIconIsEnabled()
    }

    @Then("I check self clean complete icon is not clickable")
    fun iCheckSelfCleanStatusCycleCompletedCavityIconIsNotClickable() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedCavityIconIsNotClickable()
    }


    /* --------------- Self Clean Completed: Ok Button Appearance ------------ */


    @Then("I check self clean complete screen OK button is visible")
    fun iCheckSelfCleanStatusCycleCompletedOKButtonVisibility() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedOKButtonVisibility()
    }

    @Then("I check self clean complete screen OK {string} text matched")
    fun iCheckSelfCleanStatusCycleCompletedOKButtonText(text: String) {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedOKButtonTextValidation(
            text
        )
    }

    @Then("I check self clean complete screen OK button size")
    fun iCheckSelfCleanStatusCycleCompletedOKButtonTextSize() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedOKButtonTextSizeValidation()
    }

    @Then("I check self clean complete screen OK button text color")
    fun iCheckSelfCleanStatusCycleCompletedOKButtonTextColor() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedOKButtonColorValidation()
    }

    @Then("I check self clean complete screen OK button is enabled")
    fun iCheckSelfCleanStatusCycleCompletedOKButtonIsEnabled() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedOKButtonIsEnabled()
    }

    @Then("I check self clean complete screen OK button is clickable")
    fun iCheckSelfCleanStatusCycleCompletedOKButtonIsClickable() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedOKButtonIsClickable()
    }

    @Then("I click on OK button on self clean complete screen")
    fun iClickOnOKButtonOnSelfCleanCompleteScreen() {
        LeakAssertions.assertNoLeaks()
        selfCleanNavigationHelper?.performClickSelfCleanCompleteOKButton()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I expect clock screen should visible")
    fun iExpectClockScreenShouldVisible() {
        selfCleanFragmentAppearanceTest?.selfCleanStatusCycleCompletedClockVisibility()
    }

    /* ---------------Delay Tumbler screen content ------------ */
    @Then("I check delay tumbler screen content")
    fun iCheckDelayTumblerScreenContent() {
        selfCleanFragmentAppearanceTest?.delayScreenContentValidation()
    }
}