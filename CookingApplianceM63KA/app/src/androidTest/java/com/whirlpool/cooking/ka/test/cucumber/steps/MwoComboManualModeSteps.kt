/***Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL***/

package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.appearance.MwoManualModeComboAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import core.utils.HMILogHelper
import core.utils.TimeUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith


/*
 * File : com.whirlpool.cooking.ka.test.cucumber.steps.MwoManualModeSteps
 * Author : DUNGAS
 * Created On : 4/17/24, 1:12 PM
 * Details :
 */
@RunWith(AndroidJUnit4::class)
class MwoComboManualModeSteps {
    private var mwoManualModeComboAppearanceTest: MwoManualModeComboAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        mwoManualModeComboAppearanceTest = MwoManualModeComboAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }
    @After
    fun tearDown() {
        mwoManualModeComboAppearanceTest = null
    }
    @When("I click and navigate to cavity selection screen")
    fun navigate_to_cavity_selection_screen_returns_success() {
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnClockScreen()
        LeakAssertions.assertNoLeaks()
    }

    @And("I verify {string} button text name, {string} button text color, {string} button text size, {string} button image resource for combo")
    fun verify_cavity_selection_screen_properties_returns_success(
        cavityName: String,
        cavityTextColor: String,
        cavityTextSize: String,
        cavityImageResource: String,
    ) {
        mwoManualModeComboAppearanceTest?.checkUpperAndLowerCavityButtonProperties(cavityName,cavityTextColor,cavityTextSize,cavityImageResource)
    }


    @And("I perform click on microwave btn")
    fun perform_click_upper_cavity_navigate_to_tumbler() {
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnMicrowaveButton()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see tumbler screen")
    fun tumbler_screen_visible_success() {
        mwoManualModeComboAppearanceTest?.isRecipeTumblerVisible()
    }

    @Then("I scroll tumbler to {string}")
    fun tumbler_screen_scroll(index:String) {
        mwoManualModeComboAppearanceTest?.scrollToIndex(index.toInt())
        }

    @Then("I scroll tumbler to {string} and click")
    fun tumbler_screen_scroll_and_click(index:String) {
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.scrollToIndexAndClick(index.toInt())
        LeakAssertions.assertNoLeaks()
    }

    @Then("I scroll tumbler to targetText {string} and click")
    fun tumbler_screen_scroll_to_targetText_and_click(targetText:String) {
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.scrollToTargetTextAndClick(targetText)
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see Instruction screen")
    fun validateInstructionScreen(){
        mwoManualModeComboAppearanceTest?.instructionScreenIsVisible()
    }

    @Then("I see recipe screen")
    fun validateRecipeScreen(){
        mwoManualModeComboAppearanceTest?.recipeScreenIsVisible()
    }

    @Then("I click on back button on instruction screen")
    fun clickOnBackButtonInstruction(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnBackButtonOnInstructionScreen()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on back button on numpad screen")
    fun clickOnBackButtonNumpad(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnBackButtonOnNumpadScreen()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on back button on vertical tumbler screen")
    fun clickOnBackButtonVerticalTumbler(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnBackButtonOnVerticalTumblerScreen()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on numpad button on vertical tumbler screen")
    fun clickOnNumpadButtonVerticalTumbler(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnNumpadButtonOnVerticalTumblerScreen()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on delete button on numpad screen")
    fun clickOnDeleteButtonNumpad(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.isDeleteButtonClickable()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I validate the text size for the title of the Instruction screen")
    fun validateTitleTextSizeInstructionScreen(){
        mwoManualModeComboAppearanceTest?.titleTextSizeInstructionScreenValidation()
    }

    @Then("I validate the text size for the description of the Instruction screen")
    fun validateDescriptionTextSizeInstructionScreen(){
        mwoManualModeComboAppearanceTest?.descriptionTextSizeInstructionScreenValidation()
    }

    @Then("I click on next button on recipe instructions selection screen")
    fun navigateFromInstructionScreen(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnNextButtonOnInstructionScreen()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see numpad")
    fun validateNumpad(){
        mwoManualModeComboAppearanceTest?.isNumpadVisible()
    }
    @Then("I see vertical tumbler")
    fun validateTumbler(){
        mwoManualModeComboAppearanceTest?.isVerticalTumblerVisible()
    }

    @Then("I set the CookTime to {string} via Numpad")
    fun setCooktimeForRecipeViaNumpad(cooktime: String){
        LeakAssertions.assertNoLeaks()
        val cookTimeSec = TestingUtils.convertTimeToHoursAndMinutes(
            cooktime.toInt()
                .toLong() * 60)
        mwoManualModeComboAppearanceTest?.setRecipeCooktimeViaNumpad(cookTimeSec)
        LeakAssertions.assertNoLeaks()
    }

    @Then("I click on tumbler icon")
    fun clickOnTumblerIcon(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnTumblerIcon()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I set the CookTime to {string}")
    fun programTargetCookTimeOption(CookTime: String) {
        LeakAssertions.assertNoLeaks()
        programCookOption(CookTime)
        LeakAssertions.assertNoLeaks()
    }

    fun programCookOption(value: String) {
        selectCookTimeOption(
            TimeUtils.convertTimeToHoursAndMinutes(
                value.toInt()
                    .toLong() * 60
            )
        )
    }
    fun selectCookTimeOption(CookTimeInSeconds: String) {
        val hours = CookTimeInSeconds.substring(0, 2).toInt()
        val minutes = CookTimeInSeconds.substring(2, 4).toInt()
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerLeft, hours)
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerCenter, minutes)
        UiTestingUtils.sleep(1500)
    }

    @Then("I click the Next Button")
    fun clickOnNextButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnNextButtonOnVerticalTumblerScreen()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I click the Next Button on numpad")
    fun clickOnNextButtonOnNumpad(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnNextButtonOnNumpadScreen()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see door open close popup")
    fun doorOpenCloseVisibility(){
        mwoManualModeComboAppearanceTest?.isDoorOpenClosePopupVisible()
    }
    @Then("I validate size of title text on door open and close popup")
    fun validateTitleTextSizeDoorOpenClosePopup(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenTitleTextSizeValidation()
    }

    @Then("I validate title text on door open and close popup")
    fun validateTitleTextDoorOpenClosePopup(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenTitleTextValidation()
    }
    @Then("I validate color of title text on door open and close popup")
    fun validateTitleColorTextDoorOpenClosePopup(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenTitleTextColorValidation()
    }
    @Then("I validate description text on door open and close popup")
    fun validateDescriptionTextDoorOpenClosePopup(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenDescriptionTextValidation()
    }
    @Then("I validate color of description text on door open and close popup")
    fun validateDescriptionTextColorDoorOpenClosePopup(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenDescriptionTextColorValidation()
    }

    @Then("I validate size of description text on door open and close popup")
    fun validateDescriptionTextSizeDoorOpenClosePopup(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenDescriptionTextSizeValidation()
    }

    @Then("I validate size of title text on prepare Mwo popup")
    fun validateTitleTextSizeMwoPopup(){
        mwoManualModeComboAppearanceTest?.prepareMwoScreenTitleTextSizeValidation()
    }

    @Then("I validate title text on prepare Mwo popup")
    fun validateTitleTextMwoPopup(){
        mwoManualModeComboAppearanceTest?.prepareMwoScreenTitleTextValidation()
    }
    @Then("I validate title text color on prepare Mwo popup")
    fun validateTitleColorTextMwoPopup(){
        mwoManualModeComboAppearanceTest?.prepareMwoScreenTitleTextColorValidation()
    }
    @Then("I validate description text on prepare Mwo popup")
    fun validateDescriptionTextMwoPopup(){
        mwoManualModeComboAppearanceTest?.prepareOvenScreenDescriptionTextValidation()
    }
    @Then("I validate description text color on prepare Mwo popup")
    fun validateDescriptionTextColorMwoPopup(){
        mwoManualModeComboAppearanceTest?.prepareMwoScreenDescriptionTextColorValidation()
    }

    @Then("I validate size of description text on prepare Mwo popup")
    fun validateDescriptionTextSizeMwoPopup(){
        mwoManualModeComboAppearanceTest?.prepareMwoScreenDescriptionTextSizeValidation()
    }

    @And("I open and close the door of {string} for mwo manual mode")
    fun iOpenCloseDoorForMwoManual(cavityType: String) {
        HMILogHelper.Logi("TEST_","Cavity type->>>>$cavityType")
        if (cavityType.equals("upper", ignoreCase = true)) {
            hmiKeyUtils?.openAndCloseUpperOvenDoor(CookingKACucumberTests.mainActivity, true)
        } else {
            hmiKeyUtils?.openAndCloseLowerOvenDoor(CookingKACucumberTests.mainActivity, true)
        }
        UiTestingUtils.sleep(3000)
        if (cavityType.equals("upper", ignoreCase = true)) {
            hmiKeyUtils?.openAndCloseUpperOvenDoor(CookingKACucumberTests.mainActivity, false)
        } else {
            hmiKeyUtils?.openAndCloseLowerOvenDoor(CookingKACucumberTests.mainActivity, false)
        }
        UiTestingUtils.sleep(1000)
    }

    @And("I open the door of {string} for mwo manual mode")
    fun iOpenDoorForMwoManual(cavityType: String) {
        HMILogHelper.Logi("TEST_","Cavity type->>>>$cavityType")
        if (cavityType.equals("upper", ignoreCase = true)) {
            hmiKeyUtils?.openAndCloseUpperOvenDoor(CookingKACucumberTests.mainActivity, true)
        } else {
            hmiKeyUtils?.openAndCloseLowerOvenDoor(CookingKACucumberTests.mainActivity, true)
        }
        UiTestingUtils.sleep(1000)
    }

    @Then("I click on Start button on the door open close popup")
    fun performClickOnStartButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnStartButtonOnDoorOpenClosePopup()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see status screen")
    fun statusScreenVisibility(){
        mwoManualModeComboAppearanceTest?.isStatusScreenVisible()
    }

    @Then("I see that Start button is disabled")
    fun validateStartButtonDisabled(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenStartButtonDisabled()
    }
    @Then("I see that Start button is enabled")
    fun validateStartButtonEnabled(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenStartButtonViewIsEnabled()
    }
    @Then("I validate text of Start button")
    fun validateStartButtonText(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenStartButtonTextValidation()
    }
    @Then("I validate text size of Start button")
    fun validateStartButtonTextSize(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenStartButtonTextSize()
    }
    @Then("I validate text color of Start button")
    fun validateStartButtonTextColor(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenStartButtonTextColor()
    }
    @Then("I validate Start button is clickable")
    fun validateStartButtonClickable(){
        mwoManualModeComboAppearanceTest?.doorHasOpenedClosedMwoScreenStartButtonViewIsClickable()
    }

    @Then("I click on three dots icon")
    fun clickOnMoreOptionsButton(){
        mwoManualModeComboAppearanceTest?.performClickOnThreeDotsIconButton()
    }
    @Then("I see more options popup")
    fun isMoreOptionsPopupVisible(){
        mwoManualModeComboAppearanceTest?.validateMoreOptionsPopupVisible()
    }
    @Then("I see the oven icon besides recipe name")
    fun isOvenIconVisibleNearRecipeName(){
        mwoManualModeComboAppearanceTest?.validateOvenIconVisibleNearRecipeName()
    }
    @Then("I see the recipe name on status screen")
    fun isRecipeNameVisible(){
        mwoManualModeComboAppearanceTest?.validateRecipeNameVisible()
    }
    @Then("I validate the recipe name text size on status screen")
    fun validateRecipeNameTextSize(){
        mwoManualModeComboAppearanceTest?.validateRecipeNameTextSize()
    }
    @Then("I validate the recipe name text color on status screen")
    fun validateRecipeNameTextColor(){
        mwoManualModeComboAppearanceTest?.validateRecipeNameTextColor()
    }
    @Then("I validate the remaining time visible on status screen")
    fun validateRemainingTimeVisible(){
        mwoManualModeComboAppearanceTest?.validateRemainingTimeVisible()
    }
    @Then("I expect clock screen should be visible")
    fun validateClockScreenVisible(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.validateClockScreenVisible()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I see Set lower oven button")
    fun validateSetLowerOvenButtonVisible(){
        mwoManualModeComboAppearanceTest?.validateSetLowerOvenButtonVisible()
    }
    @Then("I see Set Microwave oven button")
    fun validateSetMwoOvenButtonVisible(){
        mwoManualModeComboAppearanceTest?.validateSetMwoOvenButtonVisible()
    }
    @Then("I click on +5 min button")
    fun validatePlusFiveButtonClickable(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.plusFiveButtonIsClickable()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I validate title text for more options popup")
    fun validateTitleTextMoreOptionsPopup(){
        mwoManualModeComboAppearanceTest?.moreOptionsPopupTitleTextValidation()
    }
    @Then("I validate title text size for more options popup")
    fun validateTitleTextSizeMoreOptionsPopup(){
        mwoManualModeComboAppearanceTest?.moreOptionsPopupTitleTextSizeValidation()
    }
    @Then("I validate title text color for more options popup")
    fun validateTitleTextColorMoreOptionsPopup(){
        mwoManualModeComboAppearanceTest?.moreOptionsPopupTitleTextColorValidation()
    }
    @Then("I click on Change Temperature button")
    fun clickOnUpperLeftButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnUpperLeftButtonMoreOptionsPopup()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on Change Cooktime button")
    fun clickOnUpperRightButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnUpperRightButtonMoreOptionsPopup()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on Change Cooktime button in MWO")
    fun clickOnUpperLeftButtonMwo(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnUpperLeftButtonMoreOptionsPopup()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on Set as fav button")
    fun clickOnLowerLeftButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnLowerLeftButtonMoreOptionsPopup()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on View Instructions button")
    fun clickOnLowerRightButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnLowerRightButtonMoreOptionsPopup()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on View Instructions button in MWO")
    fun clickOnLowerLeftButtonMwo(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnLowerLeftButtonMoreOptionsPopup()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on Turn Microwave Off button")
    fun clickOnLowerButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnLowerButtonMoreOptionsPopup()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on Turn Microwave Off button in MWO")
    fun clickOnLowerRightButtonMwo(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOnLowerRightButtonMoreOptionsPopup()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I click on Turn OFF button")
    fun clickOnTurnOffButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickTurnOffButton()
        LeakAssertions.assertNoLeaks()

    }
    @Then("I click on Set lower oven button")
    fun clickOnSetLowerButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickSetLowerButtonButton()
        LeakAssertions.assertNoLeaks()
    }
    @Then("I click on Set Mwo button")
    fun clickOnSetMwoButton(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickSetMwoButton()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I see temp numpad")
    fun tempNumpadVisible(){
        mwoManualModeComboAppearanceTest?.validateChangeTempNumpadVisible()
    }
    @Then("I see cooktime numpad")
    fun cooktimeNumpadVisible(){
        mwoManualModeComboAppearanceTest?.validateChangeCookTimeNumpadVisible()
    }
    @Then("I see instruction popup")
    fun instructionPopupVisible(){
        mwoManualModeComboAppearanceTest?.validateInstructionPopupVisible()
    }
    @Then("I click on Ok button on instruction popup")
    fun clickOnOkButtonOnInstructionPopup(){
        LeakAssertions.assertNoLeaks()
        mwoManualModeComboAppearanceTest?.performClickOkButtonMoreOptionPopup()
        LeakAssertions.assertNoLeaks()
    }

}