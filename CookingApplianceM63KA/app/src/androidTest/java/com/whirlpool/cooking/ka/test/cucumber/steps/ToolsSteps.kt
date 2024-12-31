package com.whirlpool.cooking.ka.test.cucumber.steps

import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.appearance.CavityLightAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.appearance.ControlLockAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.appearance.DemoModeAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.appearance.ToolsAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ToolsSteps {
    private var toolsAppearanceTest: ToolsAppearanceTest?=null
    private var demoModeAppearanceTest: DemoModeAppearanceTest?=null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        toolsAppearanceTest = ToolsAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }

    @After
    fun tearDown() {
        toolsAppearanceTest = null
    }

    @And("I validate the header of settings menu")
    fun iValidateTheHeaderOfSettingsMenu() {
        toolsAppearanceTest?.iCheckHeaderOfSettingsMenu()
    }

    @And("I validate the grid view of settings menu")
    fun iValidateTheGridViewOfSettingsMenu() {
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.iCheckGridViewOfSettingsMenu()
    }

    @And("I validate the list view of settings menu")
    fun iValidateTheListViewOfSettingsMenu() {
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.iCheckListViewOfSettingsMenu()
    }

    @And("I validate the preference screen")
    fun iValidateThePreferenceScreen(){
        demoModeAppearanceTest?.preferencesrecyclerlistIsVisible()
        demoModeAppearanceTest?.preferencesrecyclerlistValidateView()
        toolsAppearanceTest?.RecyclerViewHeaderValidateView("Preferences")
        toolsAppearanceTest?.preferenceRecyclerViewItemsValidateView()
    }

    @And("I perform click on back button")
    fun iClickOnBackButton(){
        toolsAppearanceTest?.performClickOnBack()
    }

    @And("I click on Show More in Network Settings option")
    fun iClickOnShowMoreInNetworkSettings(){
        UiTestingUtils.sleep(1000)
        demoModeAppearanceTest?.performClickShowMoreNetworkSettings()
        UiTestingUtils.sleep(1000)
    }

    @And("I validate the Network Settings screen")
    fun iValidateNetworkSettingsScreen() {
        UiTestingUtils.sleep(1000)
        demoModeAppearanceTest?.networkSettingsRecyclerlistIsVisible()
        demoModeAppearanceTest?.networkSettingsRecyclerlistValidateView()
        toolsAppearanceTest?.RecyclerViewHeaderValidateView("Network Settings")
        toolsAppearanceTest?.iCheckListViewItemsOfNetworkSettingsMenu()
    }

    @And("I click on Show More in Info option")
    fun iClickOnShowMoreInInfo(){
        toolsAppearanceTest?.performClickShowMoreInfo()
    }

    @And("I validate the Info screen")
    fun iValidateInfoScreen() {
//        toolsAppearanceTest?.infoRecyclerlistIsVisible()
        toolsAppearanceTest?.infoRecyclerlistValidateView()
        toolsAppearanceTest?.iCheckListViewItemsOfInfoMenu()
    }

    @And("I click on Knob Settings")
    fun iClickOnKnobSettings(){
        toolsAppearanceTest?.performClickOnKnobSettings()
    }

    @And("I click on Swap Knob Functions")
    fun iClickOnSwapKnobFunctions(){
        toolsAppearanceTest?.performClickOnSwapKnobFunctions()
    }

    @And("I click on Swap")
    fun iClickOnSwap(){
        toolsAppearanceTest?.performClickOnSwap()
    }

    @And("I click on Knob Functions Info")
    fun iClickOnKnobFunctionsInfo(){
        toolsAppearanceTest?.performClickOnKnobFunctionsInfo()
    }

    @And("I click on back button of Swap Knob Functions")
    fun iClickOnBackButtonOfSwap(){
        toolsAppearanceTest?.performClickOnBackButtonOfSwap()
    }

    @And("I click on Next button on Knob Functions info")
    fun iClickOnNextButtonOfKnobFunctionInfo(){
        toolsAppearanceTest?.performScrollAndClick()
    }

    @And("I click on Done button on Knob Functions info")
    fun iClickOnDoneButtonOfKnobFunctionInfo(){
        toolsAppearanceTest?.performScrollAndClick()
    }

    @And("I click on Knob Light toggle button")
    fun iClickOnKnobLightToggleButton(){
        toolsAppearanceTest?.performClickOnKnobLightToggleButton()
    }

    @And("I click on Assign Favorite")
    fun iClickOnAssignFavorite(){
        toolsAppearanceTest?.performClickOnAssignFavorite()
    }

    @And("I click on info icon of Assign Favorite")
    fun iClickOnInfoIconOfAssignFavorite(){
        toolsAppearanceTest?.performClickOnInfoIconOfAssignFavorite()
    }

    @And("I click on Okay in assign favorite info popup")
    fun iClickOnOkayInAssignFavoriteInfoPopup(){
        toolsAppearanceTest?.performClickOnOkayInAssignFavoriteInfoPopup()
    }

    @And("I click on Start a Favorite cycle option")
    fun iClickOnStartAFavoriteCycleOption(){
        toolsAppearanceTest?.performClickOnStartAFavoriteCycleOption()
    }

    @And("I click on backarrow of Favorites screen")
    fun iClickOnBackArrowOfFavoritesScreen(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOnBackArrowOfFavoritesScreen()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on Assign Favorite close icon")
    fun iClickOnAssignFavoriteCloseIcon(){
        toolsAppearanceTest?.performClickOnAssignFavoriteCloseIcon()
    }

    @And("I click on Bake recipe")
    fun iClickOnBake(){
        toolsAppearanceTest?.performClickOnBake()
    }

    @And("I click on Next button of Bake recipe")
    fun iClickOnNextButtonOfBake(){
        toolsAppearanceTest?.performClickOnNextButtonOfBake()
    }

    @And("I click on Start button of Bake recipe")
    fun iClickOnStartButtonOfBake(){
        toolsAppearanceTest?.performClickOnStartButtonOfBake()
    }

    @And("I click on three dots in Bake recipe running cycle")
    fun iClickOnThreeDotsOfBake(){
        toolsAppearanceTest?.performClickOnThreeDotsOfBake()
    }

    @And("I click on Save as Favorite")
    fun iClickOnSaveAsFavorite(){
        toolsAppearanceTest?.performClickOnSaveAsFavorite()
    }

    @And("I click on Turn Off button of Bake recipe")
    fun iClickOnTurnOffButtonOfBake(){
        toolsAppearanceTest?.performClickOnTurnOffButtonOfBake()
    }

    @And("I click on Bake in Favorites")
    fun iClickOnBakeInFavorites(){
        toolsAppearanceTest?.performClickOnBakeInFavorites()
    }

    @And("I click on backarrow of Assign Favorite")
    fun iClickOnBackArrowOfAssignFavorite(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOnBackArrowOfAssignFavorite()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on backarrow of Knob Settings screen")
    fun iClickOnBackArrowOfKnobSettingsScreen(){
        toolsAppearanceTest?.performClickOnBackArrowOfKnobSettingsScreen()
    }

    @And("I click on close icon of Knob Settings screen")
    fun iClickOnCloseIconOfKnobSettingsScreen(){
        toolsAppearanceTest?.performClickOnCloseIconOfKnobSettingsScreen()
    }

    @And("I click on close icon of Time & date menu screen")
    fun iClickOnCloseIconOfTimeAndDateMenuScreen(){
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }

    @And("I perform click on mute")
    fun iClickOnMute(){
        toolsAppearanceTest?.performClickOnMute()
    }

    @And("I click on Time & Date")
    fun iClickOnTimeAndDate(){
        toolsAppearanceTest?.performClickOnTimeAndDate()
    }

    @And("I click on Set Time")
    fun iClickOnSetTime(){
        toolsAppearanceTest?.performClickOnSetTime()
    }

    @And("I click on Set button on vertical tumbler")
    fun iClickOnSetOfVerticalTumbler(){
        toolsAppearanceTest?.performClickOnSetOfVerticalTumbler()
    }

    @And("I click on numpad icon on Set time vertical tumbler")
    fun iClickOnNumpadIconOfVerticalTumbler(){
        toolsAppearanceTest?.performClickOnNumpadIconOfVerticalTumbler()
    }

    @And("I click on 24H")
    fun iClickOn24H(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOn24H()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on 12H")
    fun iClickOn12H(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOn12H()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on backspace on Set Time numpad")
    fun iClickOnBackOnSetTimeNumpad(){
        toolsAppearanceTest?.performClickOnBackIcon()
    }
    
    @And("I click on toggle icon on Set Time numpad")
    fun iClickOnToggleIconOfSetTimeNumpad(){
        toolsAppearanceTest?.performClickOnToggleIcon()
    }

    @And("I click on Set button on numpad")
    fun iClickOnSetButtonOfNumpad(){
        toolsAppearanceTest?.performClickOnSetButtonOfNumpad()
    }
    
    @And("I click on backarrow of Time & date menu screen")
    fun iClickOnBackArrowOfTimeAndDateMenuScreen(){
        toolsAppearanceTest?.performClickOnBackArrowOfTimeAndDateMenuScreen()
    }

    @And("I click on numpad icon on Set time 24H vertical tumbler")
    fun iClickOnNumpadIconOf24HVerticalTumbler(){
        toolsAppearanceTest?.performClickOnNumpadIconOf24HVerticalTumbler()
    }

    @And("I click on Sound Volume")
    fun iClickOnSoundVolume(){
        toolsAppearanceTest?.performClickOnSoundVolume()
    }

    @And("I click on Alerts & Timers")
    fun iClickOnAlertsAndTimers(){
        toolsAppearanceTest?.performClickOnAlertsAndTimers()
    }

    @And("I click on backarrow of Alerts & Timers tumbler screen")
    fun iClickOnBackArrowOfAlertsAndTimers(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOnBackArrowOfAlertsAndTimers()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on Buttons And Effects")
    fun iClickOnButtonsAndEffects(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOnButtonsAndEffects()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on close icon of Sound Volume menu screen")
    fun iClickOnCloseIconOfSoundVolume(){
        toolsAppearanceTest?.performClickOnCloseIconOfSoundVolume()
    }

    @And("I click on Mute toggle button")
    fun iClickOnMuteToggleButton(){
        toolsAppearanceTest?.performClickOnMuteToggleButton()
    }

    @And("I click on Regional Settings")
    fun iClickOnRegionalSettings(){
        toolsAppearanceTest?.performClickOnRegionalSettings()
    }

    @And("I click on Language")
    fun iClickOnLanguage(){
        toolsAppearanceTest?.performClickOnLanguage()
    }

    @And("I click on Spanish")
    fun iClickOnSpanish(){
        toolsAppearanceTest?.performClickOnSpanish()
    }

    @And("I click on backarrow of Language menu screen")
    fun iClickOnBackArrowOfLanguage(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on English")
    fun iClickOnEnglish(){
        toolsAppearanceTest?.performClickOnEnglish()
    }

    @And("I change Temperature Unit to degree Celsius")
    fun iClickOnDegreeCelsius(){
        toolsAppearanceTest?.performClickOnDegreeCelsius()
    }

    @And("I change Weight Unit to grams")
    fun iClickOnWeightUnitToGrams(){
        toolsAppearanceTest?.performClickOnGrams()
    }

    @And("I change Time Format to degree 24hrs")
    fun iClickOnTimeFormatTo24Hrs() {
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.regionalSettingsRecyclerList,4)
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOn24Hrs()
    }

    @And("I change Date Format to DDMM")
    fun iClickOnDateFormatToDDMM() {
        toolsAppearanceTest?.performClickOnDDMM()
    }

    @And("I click on backarrow of Regional Settings menu screen")
    fun iClickOnBackArrowOfRegionalSettings(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I validate Regional Settings menu screen")
    fun iValidateRegionalSettingsMenuScreen(){
        toolsAppearanceTest?.checkRegionalSettingsMenuScreen()
    }

    @And("I validate Regional Settings menu screen after changing")
    fun iValidateRegionalSettingsMenuScreenAgain(){
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.regionalSettingsRecyclerList,0)
        toolsAppearanceTest?.checkRegionalSettingsMenuScreenAfterChanging()
    }

    @And("I click on close icon of Regional Settings")
    fun iClickOnCloseIconOfRegionalSettings(){
        toolsAppearanceTest?.performClickOnCloseIconOfSoundVolume()
    }

    @And("I click on Display & Brightness")
    fun iClickOnDisplayAndBrightness(){
        toolsAppearanceTest?.performClickOnDisplayAndBrightness()
    }

    @And("I click on Display Brightness")
    fun iClickOnDisplayBrightness(){
        toolsAppearanceTest?.performClickOnDisplayBrightness()
    }

    @And("I click on backarrow of Display Brightness")
    fun iClickOnBackArrowOfDisplayBrightness(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on close icon of Display & Brightness")
    fun iClickOnCloseIconOfDisplayAndBrightness(){
        toolsAppearanceTest?.performClickOnCloseIconOfSoundVolume()
    }

    @And("I click on Temperature Calibration")
    fun iClickOnTemperatureCalibration(){
        toolsAppearanceTest?.performClickOnTemperatureCalibration()
    }

    @And("I click on Set button of temperature Calibration")
    fun iClickOnSetButtonOfTemperatureCalibration(){
        toolsAppearanceTest?.performClickOnSetButtonOfTempCalibration()
    }

    @And("I click on backarrow of Temperature Calibration screen")
    fun iClickOnBackArrowOfTempeCalibration(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on Restore Settings")
    fun iClickOnRestoreSettings(){
        toolsAppearanceTest?.performClickOnRestoreSettings()
    }

    @And("I click on Restore Factory Defaults")
    fun iClickOnRestoreFactoryDefaults(){
        toolsAppearanceTest?.performClickOnRestoreFactoryDefaults()
    }

    @And("I click on cancel of Restore Factory Defaults")
    fun iClickOnCancelOfRestoreFactoryDefaults(){
        toolsAppearanceTest?.performClickOnCancelOfRestoreFactoryDefaults()
    }

    @And("I click on proceed of Restore Factory Defaults")
    fun iClickOnProceedOfRestoreFactoryDefaults(){
        toolsAppearanceTest?.performClickOnProceedOfRestoreFactoryDefaults()
    }

    @And("I click on cancel of Restore Factory Defaults popup")
    fun iClickOnCancelOfRestoreFactoryDefaultsPopup(){
        toolsAppearanceTest?.performClickOnCancelOfRestoreFactoryDefaultsPopup()
    }

    @And("I click on proceed of Restore Factory Defaults popup")
    fun iClickOnProceedOfRestoreFactoryDefaultsPopup(){
        toolsAppearanceTest?.performClickOnProceedOfRestoreFactoryDefaultsPopup()
    }

    @And("I click on Cavity Light")
    fun iClickOnCavityLight(){
        toolsAppearanceTest?.performClickOnCavityLight()
    }

    @And("I click on Manually control lights")
    fun iClickOnManuallyControlLights(){
        toolsAppearanceTest?.performClickOnManuallyControlLights()
    }

    @And("I click on backarrow of Cavity Light menu screen")
    fun iClickOnBackArrowOfCavityLight(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on close icon of Cavity Light menu screen")
    fun iClickOnCloseIconOfCavityLight(){
        toolsAppearanceTest?.performClickOnCloseIconOfSoundVolume()
    }

    @And("I click on backarrow of Sound Volume menu screen")
    fun iClickOnBackArrowOfSoundVolume(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on Show more in Network Settings")
    fun iClickOnShowMoreInNetwork(){
        toolsAppearanceTest?.performClickShowMoreNetwork()
    }

    @And("I click on Wifi toggle icon")
    fun iClickOnWifiToggleIcon(){
        toolsAppearanceTest?.performClickOnWifiToggleIcon()
    }

    @And("I click on Connect to Network")
    fun iClickOnConnectToNetwork(){
        toolsAppearanceTest?.performClickOnConnectToNetwork()
    }

    @And("I click on backarrow of Connect to Network")
    fun iClickOnBackArrowOfConnectToNetwork(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on Remote Enable")
    fun iClickOnRemoteEnableOption(){
        toolsAppearanceTest?.performClickOnRemoteEnableOption()
    }

    @And("I click on connect later")
    fun iClickOnConnectLater(){
        toolsAppearanceTest?.performClickOnConnectLater()
    }

    @And("I click on backarrow of Network Settings menu screen")
    fun iClickOnBackArrowOfNetworkSettings(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on close icon of Network Settings menu screen")
    fun iClickOnCloseIconOfNetworkSettings(){
        toolsAppearanceTest?.performClickOnCloseIconOfSoundVolume()
    }

    @And("I click on Software Terms and Conditions")
    fun iClickOnSoftwareTermsAndConditions(){
        toolsAppearanceTest?.performClickOnSoftwareTermsAndConditions()
    }

    @And("I click on backarrow of Software Terms and Conditions")
    fun iClickOnBackArrowOfSoftwareTerms(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on Service and Support")
    fun iClickOnServiceAndSupport(){
        toolsAppearanceTest?.performClickOnServiceAndSupport()
    }

    @And("I click on Enter Diagnostics")
    fun iClickOnEnterDiagnostics(){
        toolsAppearanceTest?.performClickOnEnterDiagnostics()
    }

    @And("I click on Next button of Enter Code screen")
    fun iClickOnNextButtonOfEnterCode(){
        toolsAppearanceTest?.performClickOnNextButtonOfEnterCode()
    }

    @And("I click on backspace of Enter code screen")
    fun iClickOnBackspaceOfEnterCode(){
        toolsAppearanceTest?.performClickOnBackspaceOfEnterCode()
    }

    @And("I click on Dismiss of Enter Service Diagnostic popup")
    fun iClickOnDismissOfEnterServiceDiagnostic(){
        toolsAppearanceTest?.performClickOnDismissOfEnterServiceDiagnostic()
    }

    @And("I click on Enter button of Enter Service Diagnostic popup")
    fun iClickOnEnterButtonOfEnterServiceDiagnostic(){
        toolsAppearanceTest?.performClickOnEnterButtonOfEnterServiceDiagnostic()
    }

    @And("I click on close icon of Service Diagnostics menu screen")
    fun iClickOnCloseIconOfServiceDiagnostics(){
        toolsAppearanceTest?.performClickOnCloseIconOfSoundVolume()
    }

    @And("I click on Dismiss button of End Service Diagnostics popup")
    fun iClickOnDismissButtonOfEndServiceDiagnostics(){
        toolsAppearanceTest?.performClickOnDismissButton()
    }

    @And("I click on Exit button of End Service Diagnostics popup")
    fun iClickOnExitButtonOfEndServiceDiagnostics(){
        toolsAppearanceTest?.performClickOnExitButton()
    }

    @And("I click on Error codes history")
    fun iClickOnErrorCodesHistory(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOnErrorCodesHistory()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on backarrow of Error codes history")
    fun iClickOnBackArrowOfErrorCodesHistory(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on Auto diagnostics")
    fun iClickOnAutoDiagnostics(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOnAutoDiagnostics()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on Dismiss button of Auto Diagnostics popup")
    fun iClickOnDismissButtonOfAutoDiagnostics(){
        toolsAppearanceTest?.performClickOnDismissButton()
    }

    @And("I click on Component Activation")
    fun iClickOnComponentActivation(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.performClickOnComponentActivation()
        UiTestingUtils.sleep(1000)
    }

    @And("I click on backarrow of Component Activation")
    fun iClickOnBackArrowOfComponentActivation(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on HMI verification")
    fun iClickOnHmiVerification(){
        toolsAppearanceTest?.performClickOnHmiVerification()
    }

    @And("I click on close icon of HMI verification")
    fun iClickOnCloseIconOfHmiVerification(){
        toolsAppearanceTest?.performClickOnCloseIconOfSoundVolume()
    }

    @And("I click on System Info")
    fun iClickOnSystemInfo(){
        toolsAppearanceTest?.performClickOnSystemInfo()
    }

    @And("I click on product")
    fun iClickOnProduct(){
        toolsAppearanceTest?.performClickOnProduct()
    }

    @And("I click on backarrow of product")
    fun iClickOnBackArrowOfProduct(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on Wifi")
    fun iClickOnWifi(){
        toolsAppearanceTest?.performClickOnWifi()
    }

    @And("I click on backarrow of Wifi")
    fun iClickOnBackArrowOfWifi(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on Software")
    fun iClickOnSoftware(){
        toolsAppearanceTest?.performClickOnSoftware()
    }

    @And("I click on backarrow of Software")
    fun iClickOnBackArrowOfSoftware(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("I click on Restore factory Defaults in Service Diagnostics")
    fun iClickOnRestoreFactoryDefaultsInService(){
        toolsAppearanceTest?.performClickOnRestoreFactoryDefaultsInService()
    }

    @And("I click on backarrow of Reboot and Restart")
    fun iClickOnBackArrowOfRebootAndRestart(){
        toolsAppearanceTest?.performClickOnBackArrowOfLanguage()
    }

    @And("the Info menu screen is visible")
    fun checkAllViewsVisibilityOfInfoMenu(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfInfoMenu()
    }

    @And("I Enter code 123123123")
    fun iEnterCode(){
        toolsAppearanceTest?.enterCode123()
    }

    @And("I Enter code 121212121")
    fun iEnterCode121212121(){
        toolsAppearanceTest?.enterCode12()
    }

    @And("the Network Settings menu screen is visible")
    fun checkAllViewsVisibilityOfNetworkMenu(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfNetworkMenu()
    }

    @And("I validate the Network Settings screen with Wifi Off")
    fun iValidateNetworkSettingsScreenWithWifiOff(){
        toolsAppearanceTest?.iCheckNetworkMenuWithWifiOff()
    }

    @And("I validate the Knob Settings screen")
    fun iValidateTheKnobSettingsScreen(){
            toolsAppearanceTest?.knobSettingsRecyclerListIsVisible()
            toolsAppearanceTest?.knobSettingsRecyclerListValidateView()
            toolsAppearanceTest?.RecyclerViewHeaderValidateView("Knob Settings")
            toolsAppearanceTest?.knobSettingsRecyclerViewItemsValidateView()
    }

    @And("I validate the Knob Settings screen with Knob Light off")
    fun iValidateTheKnobSettingsScreenWithKnobLightOff(){
            toolsAppearanceTest?.knobSettingsRecyclerViewItemsValidateViewWithKnobLightOff()
    }

    @And("I validate Swap Knob Functions screen")
    fun iValidateSwapKnobFunctionsScreen(){
      toolsAppearanceTest?.swapKnobFunctionsHeaderValidateView()
        toolsAppearanceTest?.swapKnobFunctionsItemsValidateView()
    }

    @And("I validate Swap Knob Functions screen after swapping")
    fun iValidateSwapKnobFunctionsScreenAfterSwapping(){
        toolsAppearanceTest?.swapKnobFunctionsItemsValidateViewAfterSwapping()
    }

    @And("The Knob Settings screen is visible")
    fun checkAllViewsVisibilityOfKnobSettingsScreen(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfKnobSettingsScreen()
    }

    @And("I validate the Knob Functions Info screen for left knob")
    fun iValidateKnobFunctionsInfoScreenForLeft(){
        toolsAppearanceTest?.knobFunctionsInfoHeaderValidateView()
        toolsAppearanceTest?.knobFunctionsLeftKnobInfoValidateView()
        toolsAppearanceTest?.knobFunctionsNextButtonValidateView()
    }

    @And("I validate the Knob Functions Info screen for right knob")
    fun iValidateKnobFunctionsInfoScreenForRight(){
        toolsAppearanceTest?.knobFunctionsInfoHeaderValidateView()
        toolsAppearanceTest?.knobFunctionsRightKnobInfoValidateView()
        toolsAppearanceTest?.knobFunctionsDoneButtonValidateView()
    }

    @And("I validate Assign Favorite screen")
    fun iValidateAssignFavoriteScreen(){
        toolsAppearanceTest?.assignFavoriteHeaderValidateView()
        toolsAppearanceTest?.assignFavoriteItemsValidateView()
    }

    @And("I validate the Assign Favorite info popup")
    fun iValidateTheAssignFavoritePopup(){
        toolsAppearanceTest?.assignFavoriteInfoPopupValidateView()
    }

    @And("The Assign favorite screen is visible")
    fun checkAllViewsVisibilityOfAssignFavorite(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfAssignFavorite()
    }

    @And("I validate the Favorites screen with no favorites")
    fun iValidateTheFavoritesScreenWithNoFavorites(){
        toolsAppearanceTest?.FavoritesHeaderValidateView()
        toolsAppearanceTest?.FavoritesEmptyScreenValidateView()
    }

    @And("I validate the Favorites screen with Bake recipe in it")
    fun iValidateTheFavoritesScreenWithBakeRecipe(){
        toolsAppearanceTest?.FavoritesItemsValidateView()
    }

    @And("I validate Assign Favorite screen with favorite cycle selected")
    fun iValidateAssignFavoriteWithFavoriteSelected(){
        toolsAppearanceTest?.assignFavoriteItemsWithFavoriteSelected()
    }

    @And("I validate the Knob Settings screen with Bake recipe in assign favorite")
    fun iValidateKnobSettingsWithBakeRecipeInAssignFavorite(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.knobSettingsRecyclerViewItemsValidateViewWithBakeFavorite()
        UiTestingUtils.sleep(1000)
    }

    @And("I validate the Time & Date menu screen")
    fun iValidateTimeAndDateMenuScreen(){
        toolsAppearanceTest?.checkTimeAndDateMenuScreen()
    }

    @And("I validate the Set Time vertical tumbler screen")
    fun iValidateSetTimeVerticalTumblerScreen(){
        toolsAppearanceTest?.checkSetTimeVerticalTumblerScreen()
    }

    @And("I set the time to {string} on tumbler")
    fun iSetTimeOnTumbler(time: String){
        toolsAppearanceTest?.setTimeOnTumbler()
    }

    @And("I validate the {string} in Time & Date menu screen")
    fun iValidateTimeInTimeAndDateMenuScreen(time: String){
        toolsAppearanceTest?.checkTimeInTimeAndDateMenuScreen()
    }

    @And("I validate the Set time numpad screen")
    fun iValidateSetTimeNumpadScreen(){
        toolsAppearanceTest?.checkSetTimeNumpadScreen()
    }

    @And("the Set time 24H numpad screen is visible")
    fun checkVisibilityOf24HSetTimeNumpadScreen(){
        toolsAppearanceTest?.check24HSetTimeNumpadScreen()
    }

    @And("the Set time 12H numpad screen is visible")
    fun checkVisibilityOf12HSetTimeNumpadScreen(){
        toolsAppearanceTest?.check12HSetTimeNumpadScreen()
    }

    @And("I set the time to 12:12 on Set Time numpad")
    fun iSetTimeOnSetTimeNumpad(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.setTime1212OnSetTimeNumpad()
        UiTestingUtils.sleep(1000)
    }

    @And("I set the time to 22:22 on Set Time numpad")
    fun iSetTimeOnSetTimeNumpad1(){
        UiTestingUtils.sleep(1000)
        toolsAppearanceTest?.setTime2222OnSetTimeNumpad()
        UiTestingUtils.sleep(1000)
    }

    @And("I validate the Set time numpad screen with {string}")
    fun iValidateSetTimeNumpadScreenWithTime(time: String){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isTextMatching(R.id.keypadTextViewForDateTime, time)
    }

    @And("the Set Time vertical tumbler screen is visible")
    fun checkAllViewsVisibilityOfSetTimeVerticalTumblerScreen(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfSetTimeVerticalTumbler()
    }

    @And("the Set Time numpad screen is visible")
    fun checkAllViewsVisibilityOfSetTimeNumpadScreen(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfSetTimeNumpad()
    }

    @And("I validate the Set time numpad screen with error message")
    fun iValidateErrorMessage(){
        toolsAppearanceTest?.checkSetTimeNumpadScreenWithErrorMessage()
    }

    @And("I validate the Set Time 24H vertical tumbler screen")
    fun iValidate24HSetTimeVerticalTumblerScreen(){
        toolsAppearanceTest?.check24HSetTimeVerticalTumblerScreen()
    }

    @And("the Time & date menu screen is visible")
    fun checkAllViewsVisibilityOfTimeAndDateMenuScreen(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfTimeAndDateMenuScreen()
    }

    @And("I validate the Sound Volume menu screen")
    fun iValidateSoundVolumeMenuScreen(){
        toolsAppearanceTest?.checkSoundVolumeMenuScreen()
    }

    @And("I validate Alerts & Timers tumbler screen")
    fun iValidateAlertsAndTimersTumblerScreen(){
        toolsAppearanceTest?.checkAlertsAndTimersTumblerScreen()
    }

    @And("I validate the Sound Volume menu screen when mute")
    fun iValidateSoundVolumeMenuScreenWhenMute(){
        toolsAppearanceTest?.checkSoundVolumeMenuScreenWhenMute()
    }

    @And("I validate Language menu screen")
    fun iValidateLanguageScreen(){
        toolsAppearanceTest?.checkLanguageMenuScreen()
    }

    @And("I validate Display & Brightness menu screen")
    fun iValidateDisplayAndBrightness(){
        toolsAppearanceTest?.checkDisplayAndBrightnessMenuScreen()
    }

    @And("I validate the Temperature Calibration tumbler screen")
    fun iValidateTemperatureCalibration() {
        toolsAppearanceTest?.checkTemperatureCalibrationScreen()
    }

    @And("I set the temperature on temperature calibration tumbler")
    fun iSetTemperatureOnTemperatureCalibration(){
        toolsAppearanceTest?.setTemperatureOnTemperatureCalibration()
    }

    @And("I validate Restore Settings menu screen")
    fun iValidateRestoreSettingsMenuScreen(){
        toolsAppearanceTest?.checkRestoreSettingsMenuScreen()
    }

    @And("I validate Restore Factory Defaults screen")
    fun iValidateResetFactoryDefaultsScreen(){
        toolsAppearanceTest?.checkResetFactoryDefaultsScreen()
    }

    @And("the Restore Settings menu screen is visible")
    fun checkAllViewsVisibilityOfRestoreMenuScreen(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfRestoreMenuScreen()
    }

    @And("I validate the popup of Restore factory defaults")
    fun iValidateRestoreFactoryDefaultsPopup(){
        toolsAppearanceTest?.checkRestoreFactoryDefaultsPopup()
    }

    @And("I validate the Cavity Light menu screen")
    fun iValidateCavityLightMenuScreen(){
        toolsAppearanceTest?.checkCavityLightMenuScreen()
    }

    @And("I validate the do not unplug")
    fun iValidateDoNotUnplug(){
        toolsAppearanceTest?.checkDoNotUnplug()
    }

    @And("I validate Connect to Network screen")
    fun iValidateConnectToNetworkScreen(){
        toolsAppearanceTest?.checkConnectToNetworkScreen()
    }

    @And("I validate Set up Wifi popup")
    fun iValidateSetUpWifiPopup(){
        toolsAppearanceTest?.checkSetUpWifiPopup()
    }

    @And("I validate Software Terms and Conditions screen")
    fun iValidateSoftwareTermsAndConditionsScreen(){
        toolsAppearanceTest?.checkSoftwareTermsAndConditionsScreen()
    }

    @And("I validate Service and Support screen")
    fun iValidateServiceAndSupportScreen(){
        toolsAppearanceTest?.checkServiceAndSupportScreen()
    }

    @And("I validate Enter code screen")
    fun iValidateEnterCodeScreen(){
        toolsAppearanceTest?.checkEnterCodeScreen()
    }

    @And("I validate Enter code screen with error message")
    fun iValidateEnterCodeScreenWithError(){
        toolsAppearanceTest?.checkEnterCodeScreenWithError()
    }

    @And("I validate Enter Service Diagnostics popup")
    fun iValidateEnterServiceDiagnosticsPopup(){
        toolsAppearanceTest?.checkEnterServiceDiagnosticsPopup()
    }

    @And("the Enter code screen is visible")
    fun checkAllViewsVisibilityOfEnterCodeScreen(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfEnterCodeScreen()
    }

    @And("I validate Service Diagnostics menu screen")
    fun iValidateServiceDiagnosticsMenuScreen(){
        toolsAppearanceTest?.checkServiceDiagnosticsMenuScreen()
    }

    @And("I validate End Service Diagnostics popup")
    fun iValidateEndServiceDiagnosticsPopup(){
        toolsAppearanceTest?.checkEndServiceDiagnosticsPopup()
    }

    @And("the Service Diagnostics menu screen is visible")
    fun checkAllViewsVisibilityOfDiagnosticsMenuScreen(){
        toolsAppearanceTest?.checkAllViewsVisibilityOfDiagnosticsMenuScreen()
    }

    @And("I validate Error Code History screen")
    fun iValidateErrorCodeHistoryScreen(){
        toolsAppearanceTest?.checkErrorCodeHistoryScreen()
    }

    @And("I validate Start Auto Diagnostics popup")
    fun iValidateStartAutoDiagnosticsPopup(){
        toolsAppearanceTest?.checkStartAutoDiagnosticsPopup()
    }

    @And("I validate Component Activation menu screen")
    fun iValidateComponentActivationMenuScreen(){
        toolsAppearanceTest?.checkComponentActivationMenuScreen()
    }

    @And("I validate the HMI verification screen")
    fun iValidateHmiVerificationScreen(){
        UiTestingUtils.isViewVisible(R.id.parentLayout)
    }

    @And("I validate System info screen")
    fun iValidateSystemInfoScreen(){
        toolsAppearanceTest?.checkSystemInfoScreen()
    }

    @And("I validate product screen")
    fun iValidateProductScreen(){
        toolsAppearanceTest?.checkProductScreen()
    }

    @And("the System info menu screen is visible")
    fun checkAllViewsVisibilityOfSystemInfoMenuScreen(){
        toolsAppearanceTest?.checkAllViewsOfSystemInfoMenuScreen()
    }

    @And("I validate Wifi screen")
    fun checkAllViewsVisibilityOfWifiScreen(){
        toolsAppearanceTest?.checkAllViewsOfWifiScreen()
    }

    @And("I validate Software screen")
    fun iValidateSoftwareScreen(){
        toolsAppearanceTest?.checkAllViewsOfSoftwareScreen()
    }

    @And("I validate Reboot and Reset screen")
    fun iValidateResetAndRebootScreen(){
        toolsAppearanceTest?.checkResetAndRebootScreen()
    }

    @And("the Restore Factory Defaults screen is visible")
    fun checkAllViewsVisibilityOfRestoreDefaultScreen(){
       toolsAppearanceTest?.checkAllViewsVisibilityOfRestoreDefaultScreen()
    }
}