package com.whirlpool.cooking.ka.test.cucumber.steps

import com.whirlpool.cooking.ka.test.cucumber.appearance.ProbeAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProbeDoubleSteps {
    private var probeAppearanceTest: ProbeAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        probeAppearanceTest = ProbeAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }
    @After
    fun tearDown() {
        probeAppearanceTest = null
        hmiKeyUtils = null
    }

    @And("I insert a probe in upper cavity")
    fun insertProbeInUpperCavity(){
        UiTestingUtils.sleep(1000)
        hmiKeyUtils?.simulateMeatProbeConnected(CookingKACucumberTests.mainActivity,true)
        UiTestingUtils.sleep(2000)
    }
    @And("I insert a probe in lower cavity")
    fun insertProbeInLowerCavity(){
        UiTestingUtils.sleep(1000)
        hmiKeyUtils?.simulateMeatProbeConnected(CookingKACucumberTests.mainActivity,false)
    }
    @And("I remove a probe in upper cavity")
    fun removeProbeInUpperCavity(){
        UiTestingUtils.sleep(1000)
        hmiKeyUtils?.simulateMeatProbeDisconnected(CookingKACucumberTests.mainActivity,true)
    }
    @And("I remove a probe in lower cavity")
    fun removeProbeInLowerCavity(){
        UiTestingUtils.sleep(1000)
        hmiKeyUtils?.simulateMeatProbeDisconnected(CookingKACucumberTests.mainActivity,false)
    }
    @And("I see probe recipe gridview")
    fun probeRecipeGridViewVisible(){
        UiTestingUtils.sleep(1000)
        probeAppearanceTest?.probeRecipeGridViewVisible()
    }
    @And("I check probe recipe selection gridview title text")
    fun probeRecipeGridViewValidation(){
        UiTestingUtils.sleep(1000)
        probeAppearanceTest?.probeRecipeGridViewValidation()
    }

    @And("I click on back button on Probe recipe Selection screen grid")
    fun performClickOnBackButtonOnProbeRecipeGridView(){
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        probeAppearanceTest?.performClickOnBackButtonOnProbeRecipeGridView()
        LeakAssertions.assertNoLeaks()
    }
    @And("I see probe still detected popup")
    fun probeStillDetectedPopupVisible(){
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        probeAppearanceTest?.probeStillDetectedPopupVisible()
        LeakAssertions.assertNoLeaks()
    }
    @And("I validate the title text and description of probe still detected popup")
    fun probeStillDetectedPopupValidation(){
        UiTestingUtils.sleep(1000)
        probeAppearanceTest?.probeStillDetectedPopupValidation()
    }
    @And("I validate the return to probe modes button")
    fun returnToProbeModesButtonValidation(){
        UiTestingUtils.sleep(1000)
        probeAppearanceTest?.returnToProbeModesButtonValidation()
    }
    @And("I click on return to probe modes button")
    fun returnToProbeModesButtonClick(){
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(1000)
        probeAppearanceTest?.performClickOnReturnToProbeModesButton()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on {string}")
    fun scrollToTargetRecipeName(recipeName:String){
        LeakAssertions.assertNoLeaks()
        UiTestingUtils.sleep(500)
        probeAppearanceTest?.scrollToTextAndClick(recipeName)
        LeakAssertions.assertNoLeaks()
    }
    @And("I see probe temp tumbler")
    fun isProbeTempTumblerVisible(){
        UiTestingUtils.sleep(500)
        probeAppearanceTest?.isProbeTempTumblerVisible()
    }
    @And("I see oven temp tumbler")
    fun isOvenTempTumblerVisible(){
        UiTestingUtils.sleep(500)
        probeAppearanceTest?.isProbeTempTumblerVisible()
    }
    @And("I validate title text on probe temp tumbler")
    fun isProbeTempTumblerTextCorrect(){
        UiTestingUtils.sleep(500)
        probeAppearanceTest?.probeTempTumblerTextValidation()
    }
    @And("T click on numpad icon")
    fun clickOnNumpadIconOnTempTumbler(){
        UiTestingUtils.sleep(500)
        LeakAssertions.assertNoLeaks()
        probeAppearanceTest?.clickOnNumpadIconOnTempTumbler()
        LeakAssertions.assertNoLeaks()
    }
    @And("I see temp numpad for probe")
    fun validateChangeTempNumpadVisible(){
        UiTestingUtils.sleep(500)
        probeAppearanceTest?.validateChangeTempNumpadVisible()
    }
    @And("I set probe temp at {string}")
    fun setProbeTempViaTumbler(index:String){
        UiTestingUtils.sleep(500)
        probeAppearanceTest?.setProbeTempViaTumbler(index.toInt())
    }
    @And("I set oven temp at {string}")
    fun setOvenTempViaTumbler(index:String){
        UiTestingUtils.sleep(500)
        probeAppearanceTest?.setOvenTempViaTumbler(index.toInt())
    }
    @And("I click on Probe temp section")
    fun performClickOnProbeTempOnPreviewScreen(){
        LeakAssertions.assertNoLeaks()
        probeAppearanceTest?.performClickOnProbeTempOnPreviewScreen()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on oven temp section")
    fun performClickOnOvenTempOnPreviewScreen(){
        LeakAssertions.assertNoLeaks()
        probeAppearanceTest?.performClickOnOvenTempOnPreviewScreen()
        LeakAssertions.assertNoLeaks()
    }
    @And("I see probe preview screen")
    fun isProbePreviewScreenVisible(){
        probeAppearanceTest?.isProbePreviewScreenVisible()
    }
    @And("I click on Start button on probe preview screen")
    fun performClickOnNextButtonOnProbePreviewScreen(){
        LeakAssertions.assertNoLeaks()
        probeAppearanceTest?.performClickOnNextButtonOnProbePreviewScreen()
        LeakAssertions.assertNoLeaks()
    }
    @And("I see status screen for probe")
    fun statusScreenVisibleForProbe(){
        probeAppearanceTest?.statusScreenVisibleForProbe()
    }
    @And("I validate the probe status screen")
    fun statusScreenValidateForProbe(){
        probeAppearanceTest?.statusScreenValidateForProbe()
    }
    @And("I validate the probe status screen for double {string}")
    fun statusScreenValidateForProbeForDO(cavity: String) {
        if (cavity == "Upper")
            probeAppearanceTest?.statusScreenValidateForProbe()
        else
            probeAppearanceTest?.statusScreenValidateForProbeOnCombo()
    }
    @And("I validate the probe status screen on combo")
    fun statusScreenValidateForProbeOnCombo(){
        probeAppearanceTest?.statusScreenValidateForProbeOnCombo()
    }
    @And("I validate the probe status screen on single")
    fun statusScreenValidateForProbeOnSingle(){
        probeAppearanceTest?.statusScreenValidateForProbeOnSingle()
    }
    @And("I see insert temp probe popup")
    fun insertProbePopupVisible(){
        probeAppearanceTest?.insertProbePopupVisible()
    }
    @And("I validate the insert temp probe popup")
    fun insertProbePopupValidation(){
        probeAppearanceTest?.insertProbePopupValidation()
    }
    @And("I see probe detected in upper cavity popup")
    fun probeDetectedInUpperCavityPopupVisible(){
        probeAppearanceTest?.probeDetectedInUpperCavityPopupVisible()
    }
    @And("I see probe detected in lower cavity popup")
    fun probeDetectedInLowerCavityPopupVisible(){
        probeAppearanceTest?.probeDetectedInUpperCavityPopupVisible()
    }
    @And("I see probe removed popup")
    fun probeRemovedPopupVisible(){
        probeAppearanceTest?.probeDetectedInUpperCavityPopupVisible()
    }
    @And("I see probe detected popup")
    fun probeDetectedPopupVisible(){
        probeAppearanceTest?.probeDetectedInUpperCavityPopupVisible()
    }
    @And("I validate probe detected in upper cavity popup")
    fun probeDetectedInUpperCavityPopupValidation(){
        probeAppearanceTest?.probeDetectedInUpperCavityPopupValidation()
    }
    @And("I validate probe detected in lower cavity popup")
    fun probeDetectedInLowerCavityPopupValidation(){
        probeAppearanceTest?.probeDetectedInLowerCavityPopupValidation()
    }
    @And("I validate probe removed popup")
    fun probeRemovedPopupValidation(){
        probeAppearanceTest?.probeRemovedPopupValidation()
    }
    @And("I validate probe detected popup")
    fun probeDetectedPopupValidation(){
        probeAppearanceTest?.probeDetectedPopupValidation()
    }
    @And("I click on yes button")
    fun performClickOnYesButton(){
        LeakAssertions.assertNoLeaks()
        probeAppearanceTest?.performClickOnYesButton()
        LeakAssertions.assertNoLeaks()
    }
    @And("I click on no button")
    fun performClickOnNoButton(){
        LeakAssertions.assertNoLeaks()
        probeAppearanceTest?.performClickOnNoButton()
        LeakAssertions.assertNoLeaks()
    }

}