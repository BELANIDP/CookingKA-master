package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.appearance.HotCavityWarningAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils.closeLowerOvenDoor
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils.closeUpperOvenDoor
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils.openLowerOvenDoor
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils.openUpperOvenDoor
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils.pressLowerOvenCancelButton
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils.pressUpperOvenCancelButton
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.hasWhrPreStartConfiguration
import com.whirlpool.hmi.uitesting.UiTestingUtils
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HotCavityWarningSteps {
    private var hotCavityWarningAppearanceTest: HotCavityWarningAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()
    @Before
    fun setUp() {
        hotCavityWarningAppearanceTest = HotCavityWarningAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }

    @After
    fun tearDown() {
        hotCavityWarningAppearanceTest = null
    }

    @And("I perform click on {string} cavity btn")
    fun performClickOnCavityButton(cavity: String) {
        LeakAssertions.assertNoLeaks()
        val indexId = when (cavity) {
            "Upper", "Microwave" -> R.id.upper_oven_layout
            else -> R.id.lower_oven_layout
        }
        hotCavityWarningAppearanceTest?.performClickOnCavityButton(indexId)
        LeakAssertions.assertNoLeaks()
    }

    @And("I increase the oven temperature {string}.")
    fun increaseCavitiesTemperature(cavity : String) {
        when (cavity) {
            "Single" -> {
                CookingKACucumberTests.mainActivity?.let {
                    hmiKeyUtils?.simulateOvenTemperatureChangeEvent(it, false, "300")
                }
            }
            "ComboOven" -> {
                CookingKACucumberTests.mainActivity?.let {
                    hmiKeyUtils?.simulateOvenTemperatureChangeEvent(it, false, "300")
                }
            }
            else -> {
                CookingKACucumberTests.mainActivity?.let {
                    hmiKeyUtils?.simulateOvenTemperatureChangeEvent(it, true, "300")
                    hmiKeyUtils?.simulateOvenTemperatureChangeEvent(it, false, "300")
                }
            }
        }
    }
    @And("I increase the oven temperature.")
    fun increaseOvenTemperature() {
        CookingKACucumberTests.mainActivity?.let {
            hmiKeyUtils?.simulateOvenTemperatureChangeEvent(it, false, "300")
        }
    }

    @And("I wait for cavity cool down {string}.")
    fun waitForCavityCoolDown(cavity: String) {
        val temperatures = listOf("210", "110", "30")
        val isUpper = (cavity == "Upper") || (cavity == "Single")

        CookingKACucumberTests.mainActivity?.let {
            temperatures.forEach { temp ->
                hmiKeyUtils?.simulateOvenTemperatureChangeEvent(it, isUpper, temp)
            }
        }
    }

    @And("I see hot cavity popup.")
    fun checkHotCavityPopupVisible() {
        hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
    }

    @And("I verify icon of popup {string} {string} {string}.")
    fun verifyIconOfHotCavityPopup(icon: String, height: String, width: String) {
        val iconId = when (icon) {
            "Upper" -> R.drawable.ic_large_upper_cavity
            else -> R.drawable.ic_large_lower_cavity
        }
        hotCavityWarningAppearanceTest?.verifyIconOfHotCavityPopup(iconId,height,width)
    }

    @And("I verify title of popup {string} {string} {string} {string} {string} {string} {string}.")
    fun verifyTitleOfHotCavityPopup(title: String, font: String, weight: String, textSize: String, lineHeight: String, alignment: String, color: String) {
        hotCavityWarningAppearanceTest?.verifyTitleOfHotCavityPopup(title, font ,weight, textSize, lineHeight, alignment, color)
    }
    
    @And("Verify Hot cavity popup details {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string}.")
    fun verifyHotCavityPopups(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,                  //name of recipe in capability
        title: String,
        font: String,
        weight: String,
        textSize: String,
        lineHeight: String,
        alignment: String,
        color: String,
        description: String,
        font1: String,
        weight1: String,
        textSize1: String,
        lineHeight1: String,
        alignment1: String,
        color1: String,
        rightButton: String
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
        if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.apply {
                isHotCavityPopupVisible()
                verifyTitleOfHotCavityPopup(title, font, weight, textSize, lineHeight, alignment, color)
                verifyDescriptionOfHotCavityPopup(description, font1, weight1, textSize1, lineHeight1, alignment1, color1)
                verifyLeftButtonOfHotCavityPopup()
                verifyRightButtonOfHotCavityPopup(rightButton)
            }
        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
    }

    @And("For Double Oven Verify Hot cavity popup details {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string}.")
    fun verifyHotCavityPopupsForDO(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,         //name of recipe in capability
        icon: String,
        height: String,
        width: String,
        title: String,
        font: String,
        weight: String,
        textSize: String,
        lineHeight: String,
        alignment: String,
        color: String,
        description: String,
        font1: String,
        weight1: String,
        textSize1: String,
        lineHeight1: String,
        alignment1: String,
        color1: String,
        rightButton: String
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
        if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.apply {
                isHotCavityPopupVisible()
                verifyIconOfHotCavityPopup(icon,height,width)
                verifyTitleOfHotCavityPopup(title, font, weight, textSize, lineHeight, alignment, color)
                verifyDescriptionOfHotCavityPopup(description, font1, weight1, textSize1, lineHeight1, alignment1, color1)
                verifyLeftButtonOfHotCavityPopup()
                verifyRightButtonOfHotCavityPopup(rightButton)
            }
        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
    }

    @And("Verify Oven ready popup details {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string}.")
    fun verifyOvenReadyPopups(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,                  //name of recipe in capability
        title: String,
        font: String,
        weight: String,
        textSize: String,
        lineHeight: String,
        alignment: String,
        color: String,
        description: String,
        font1: String,
        weight1: String,
        textSize1: String,
        lineHeight1: String,
        alignment1: String,
        color1: String,
        rightButton: String
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
        if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            waitForCavityCoolDown(cavity)
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            hotCavityWarningAppearanceTest?.verifyTitleOfHotCavityPopup(title, font, weight, textSize, lineHeight, alignment, color)
            hotCavityWarningAppearanceTest?.verifyDescriptionOfHotCavityPopup(description, font1, weight1, textSize1, lineHeight1, alignment1, color1)
            hotCavityWarningAppearanceTest?.verifyLeftButtonOfHotCavityPopup1()
            hotCavityWarningAppearanceTest?.verifyRightButtonOfHotCavityPopup(rightButton)
            
        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
    }

    @And("For Double Oven Verify Oven ready popup details {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string} {string}.")
    fun verifyOvenReadyPopupsForDO(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,         //name of recipe in capability
        icon: String,
        height: String,
        width: String,
        title: String,
        font: String,
        weight: String,
        textSize: String,
        lineHeight: String,
        alignment: String,
        color: String,
        description: String,
        font1: String,
        weight1: String,
        textSize1: String,
        lineHeight1: String,
        alignment1: String,
        color1: String,
        rightButton: String
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
        if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            verifyIconOfHotCavityPopup(icon,height,width)
            waitForCavityCoolDown(cavity)
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            hotCavityWarningAppearanceTest?.verifyTitleOfHotCavityPopup(title, font, weight, textSize, lineHeight, alignment, color)
            hotCavityWarningAppearanceTest?.verifyDescriptionOfHotCavityPopup(description, font1, weight1, textSize1, lineHeight1, alignment1, color1)
            hotCavityWarningAppearanceTest?.verifyLeftButtonOfHotCavityPopup1()
            hotCavityWarningAppearanceTest?.verifyRightButtonOfHotCavityPopup(rightButton)

        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
    }

    @And("Verify happy flow for hot cavity warning for not recommended recipe, user wait for cavity to cool down {string} {string} {string} {string}.")
    fun verifyHappyFlowForHotCavityWarningWaitForCoolDown(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,                  //name of recipe in capability
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
        LeakAssertions.assertNoLeaks()
        if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            waitForCavityCoolDown(cavity)
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            hotCavityWarningAppearanceTest?.iClickOnRightButton()
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
        LeakAssertions.assertNoLeaks()
    }
       @And("Verify happy flow for hot cavity warning for not recommended recipe, user not wait for cavity to cool down {string} {string} {string} {string}.")
    fun verifyHappyFlowForHotCavityWarningNotWaitForCoolDown(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,                  //name of recipe in capability
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
           LeakAssertions.assertNoLeaks()
           if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            waitForCavityCoolDown(cavity)
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            hotCavityWarningAppearanceTest?.iClickOnRightButton()
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
           LeakAssertions.assertNoLeaks()

       }

   @And("Verify Cancel press on Oven ready popup {string} {string} {string} {string}.")
    fun verifyCancelPressOnOvenReadyPopup(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,                  //name of recipe in capability
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
        if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            waitForCavityCoolDown(cavity)
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            pressCancelButtonOnHmi(cavity)
            hotCavityWarningAppearanceTest?.iSeeClockScreen()
        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
    }
    @And("Verify Cancel press on hot cavity warning popup {string} {string} {string} {string}.")
    fun verifyCancelPressOnHotCavityPopup(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,                  //name of recipe in capability
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
        if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            pressCancelButtonOnHmi(cavity)
            hotCavityWarningAppearanceTest?.iSeeClockScreen()
        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
    }

    @And("Verify Door open on Oven ready popup {string} {string} {string} {string}.")
    fun verifyDoorOpenOnOvenReadyPopup(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,                  //name of recipe in capability
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
        LeakAssertions.assertNoLeaks()
        if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            waitForCavityCoolDown(cavity)
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            openTheDoor(cavity)
            hotCavityWarningAppearanceTest?.iClickOnRightButton()
            hotCavityWarningAppearanceTest?.iSeeDoorOpenPopup()
        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
        LeakAssertions.assertNoLeaks()
    }

    @And("Verify Door open on hot cavity popup {string} {string} {string} {string}.")
    fun verifyDoorOpenOnHotCavityPopup(
        product: String,
        cavity: String,
        recipe: String,
        recipeName: String,                  //name of recipe in capability
    ) {
        val mappedProduct = when (product) {
            "Single" -> "singleovenlowend"
            "Combo" -> "combolowend"
            "Double" -> "doubleovenlowend"
            "Microwave" -> "microwaveoven"
            else -> product
        }

        val mappedCavity = when (cavity) {
            "Lower", "ComboOven" -> "secondaryCavity"
            else -> "primaryCavity"
        }

        val activity = CookingKACucumberTests.mainActivity
        UiTestingUtils.sleep(1000)
        val hasConfiguration = activity?.let { hasWhrPreStartConfiguration(it, mappedProduct, mappedCavity, recipeName) }
        UiTestingUtils.sleep(1000)
        LeakAssertions.assertNoLeaks()
        if (hasConfiguration == true) {
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
            openTheDoor(cavity)
            hotCavityWarningAppearanceTest?.iClickOnRightButton()
            hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
        } else {
            hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
        }
        LeakAssertions.assertNoLeaks()
    }

    @And("I verify description of popup {string} {string} {string} {string} {string} {string} {string}.")
    fun verifyDescriptionOfHotCavityPopup(description: String, font: String, weight: String, textSize: String, lineHeight: String, alignment: String, color: String) {
        hotCavityWarningAppearanceTest?.verifyDescriptionOfHotCavityPopup(description, font ,weight, textSize, lineHeight, alignment, color)
    }

    @And("I verify left button of popup.")
    fun verifyLeftButtonOfHotCavityPopup() {
        hotCavityWarningAppearanceTest?.verifyLeftButtonOfHotCavityPopup()
    }

    @And("I verify right button of popup {string}.")
    fun verifyRightButtonOfHotCavityPopup(rightButton: String) {
        hotCavityWarningAppearanceTest?.verifyRightButtonOfHotCavityPopup(rightButton)
    }

    @And("I see oven ready popup.")
    fun checkOvenReadyPopupVisible() {
        hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
    }

    @And("I click on next button of popup.")
    fun clickOnNextButton() {
        LeakAssertions.assertNoLeaks()
        hotCavityWarningAppearanceTest?.iClickOnRightButton()
        LeakAssertions.assertNoLeaks()
    }

    @And("I click on start button of popup.")
    fun clickOnStartButton() {
        LeakAssertions.assertNoLeaks()
        hotCavityWarningAppearanceTest?.iClickOnRightButton()
        LeakAssertions.assertNoLeaks()
    }

    @And("I see cycle is running {string}.")
    fun verifyStatusScreen(cavity: String) {
        hotCavityWarningAppearanceTest?.verifyStatusScreen(cavity)
    }

    @And("I press cancel button on HMI {string}.")
    fun pressCancelButtonOnHmi(cavity: String) {
        when (cavity) {
            "Upper", "Microwave", "Single", "ComboOven", "Lower" -> pressUpperOvenCancelButton(CookingKACucumberTests.mainActivity)
            else -> CookingKACucumberTests.mainActivity?.let { pressLowerOvenCancelButton(it) }
        }
    }

    @And("I opened the door {string}.")
    fun openTheDoor(cavity: String) {
        when (cavity) {
            "Upper", "Microwave", "Single" -> openUpperOvenDoor(CookingKACucumberTests.mainActivity)
            else -> openLowerOvenDoor(CookingKACucumberTests.mainActivity)
        }
    }

    @And("I closed the door {string}.")
    fun closeTheDoor(cavity: String) {
        when (cavity) {
            "Upper", "Microwave", "Single" -> closeUpperOvenDoor(CookingKACucumberTests.mainActivity)
            else -> closeLowerOvenDoor(CookingKACucumberTests.mainActivity)
        }
    }

    @Then("App should remain on hot cavity popup screen.")
    fun appNotNavigateToStatusScreenRemainOnHotCavityPopup(){
        hotCavityWarningAppearanceTest?.isHotCavityPopupVisible()
    }

    @Then("I see the clock screen.")
    fun iSeeClockScreen(){
        hotCavityWarningAppearanceTest?.iSeeClockScreen()
    }

    @Then("I see Door Open popup.")
    fun iSeeDoorOpenPopup(){
        hotCavityWarningAppearanceTest?.iSeeDoorOpenPopup()
    }

    @And("I scroll to {string} and click on it.")
    fun iScrollToMoreModesAndClickOnIt(text: String){
        LeakAssertions.assertNoLeaks()
        hotCavityWarningAppearanceTest?.iScrollToMoreModesAndClickOnIt(text)
        LeakAssertions.assertNoLeaks()
    }

    @And("I click on more modes {string}")
    fun iClickOnIndex(index: String) {
        hotCavityWarningAppearanceTest?.iClickOnIndex(index)
    }
}