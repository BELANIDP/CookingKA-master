package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.FavoritesAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavoritesSteps {
    private var favoritesAppearanceTest: FavoritesAppearanceTest? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        favoritesAppearanceTest = FavoritesAppearanceTest()
    }

    @After
    fun tearDown() {
        favoritesAppearanceTest = null
    }

    @And("I am on the Clock screen")
    fun iAmOnClockScreen(){
        favoritesAppearanceTest?.iAmOnClockScreen()
    }

    @And("I click and navigate to the cavity selection screen")
    fun iClickAndNavigateToTheCavitySelectionScreen() {
        favoritesAppearanceTest?.iClickAndNavigateToTheCavitySelectionScreen()
    }

    @And("I see the cavity selection screen")
    fun iSeeTheCavitySelectionScreen() {
        favoritesAppearanceTest?.iSeeTheCavitySelectionScreen()
    }

    @And("I select the required {string}")
    fun iSelectTheRequiredCavity(cavity: String) {
        favoritesAppearanceTest?.iSelectTheRequiredCavity(cavity)
    }

    @Then("I see the horizontal tumbler screen")
    fun iSeeTheHorizontalTumblerScreen() {
        favoritesAppearanceTest?.iSeeTheHorizontalTumblerScreen()
    }

    @Then("I click on Favorites")
    fun iClickOnFavorites() {
        favoritesAppearanceTest?.iClickOnFavorites()
    }

    @Then("I see Favorites list screen with no favorites added")
    fun iSeeFavoritesListScreenWithNoFavoritesAdded() {
        favoritesAppearanceTest?.iSeeFavoritesListScreenWithNoFavoritesAdded()
    }

    @Then("I click back button")
    fun iClickBackButton() {
        favoritesAppearanceTest?.iClickBackButton()
    }

    @Then("I click on plus icon to Add Favorites")
    fun iClickOnPlusIconToAddFavorites() {
        favoritesAppearanceTest?.iClickOnPlusIconToAddFavorites()
    }

    @Then("I see Favorites screen")
    fun iSeeFavoritesScreen() {
        favoritesAppearanceTest?.iSeeFavoritesScreen()
    }

    @Then("I click on Manual modes")
    fun iClickOnManualModes() {
        favoritesAppearanceTest?.iClickOnManualModes()
    }

    @Then("I see Create a favorite screen  with horizontal tumbler")
    fun iSeeCreateAFavoriteScreenWithHorizontalTumbler() {
        favoritesAppearanceTest?.iSeeCreateAFavoriteScreenWithHorizontalTumbler()
    }

    @Then("I scroll the horizontal tumbler to the {string} and click")
    fun iScrollTheHorizontalTumblerToIndexAndClick(index: String) {
        favoritesAppearanceTest?.iScrollTheHorizontalTumblerToIndexAndClick(index.toInt())
    }

    @Then("I see numpad view for setting temperature")
    fun iSeeNumpadViewForSettingTemperature() {
        favoritesAppearanceTest?.iSeeNumpadViewForSettingTemperature()
    }

    @Then("I click back button on numpad view")
    fun iClickBackButtonOnNumpadView() {
        favoritesAppearanceTest?.iClickBackButtonOnNumpadView()
    }

    @Then("I click on tumbler icon on numpad screen")
    fun iClickOnTumblerIconOnNumpadScreen() {
        favoritesAppearanceTest?.iClickOnTumblerIconOnNumpadScreen()
    }

    @Then("I see horizontal temperature tumbler")
    fun iSeeHorizontalTemperatureTumbler() {
        favoritesAppearanceTest?.iSeeHorizontalTemperatureTumbler()
    }

    @Then("I click on numpad icon")
    fun iClickOnNumpadIcon() {
        favoritesAppearanceTest?.iClickOnNumpadIcon()
    }

    @Then("I click next button on the temperature setting numpad view")
    fun iClickNextButtonOnTheTemperatureSettingNumpadView() {
        favoritesAppearanceTest?.iClickNextButtonOnTheTemperatureSettingNumpadView()
    }

    @Then("I see numpad view for setting cook time")
    fun iSeeNumpadViewForSettingCookTime() {
        favoritesAppearanceTest?.iSeeNumpadViewForSettingCookTime()
    }

    @Then("I set the temperature {string} on numpad view for setting temperature")
    fun iSetTheTemperatureOnNumpadViewForSettingTemperature(temp: String) {
        favoritesAppearanceTest?.iSetTheTemperatureOnNumpadViewForSettingTemperature(temp)
    }

    @Then("I see vertical tumbler for cooktime")
    fun iSeeVerticalTumblerForCooktime() {
        favoritesAppearanceTest?.iSeeVerticalTumblerForCooktime()
    }

    @Then("I set the CookTime to {string} on numpad view of setting cooktime")
    fun iSetTheCookTimeToOnNumpadViewOfSettingCooktime(cookTime: String) {
        val cookTimeSec = TestingUtils.convertTimeToHoursAndMinutes(
            cookTime.toInt().toLong() * 60)
        favoritesAppearanceTest?.iSetTheCookTimeToOnNumpadViewOfSettingCooktime(cookTimeSec)
    }

    @Then("I click next button on the cooktime setting numpad view")
    fun iClickNextButtonOnTheCooktimeSettingNumpadView() {
        favoritesAppearanceTest?.iClickNextButtonOnTheCooktimeSettingNumpadView()
    }

    @Then("I see details of the favorites recipe screen")
    fun iSeeDetailsOfTheFavoritesRecipeScreen() {
        favoritesAppearanceTest?.iSeeDetailsOfTheFavoritesRecipeScreen()
    }

    @Then("I click on Save button")
    fun iClickOnSaveButton() {
        favoritesAppearanceTest?.iClickOnSaveButton()
    }

    @Then("I see Favorites list screen and the recipe {string} is added to favorites")
    fun iSeeFavoritesListScreenAndTheRecipeIsAddedToFavorites(name: String) {
        favoritesAppearanceTest?.iSeeFavoritesListScreenAndTheRecipeIsAddedToFavorites(name)
    }

    @Then("I click on AutoCook")
    fun iClickOnAutoCook() {
        favoritesAppearanceTest?.iClickOnAutoCook()
    }

    @Then("I see Food Type screen of Favorites")
    fun iSeeFoodTypeScreenOfFavorites() {
        favoritesAppearanceTest?.iSeeFoodTypeScreenOfFavorites()
    }

    @Then("I scroll and click on meat")
    fun iScrollAndClickOnMeat() {
        favoritesAppearanceTest?.iScrollAndClickOnMeat()
    }

    @Then("I see all the Meat recipes")
    fun iSeeAllTheMeatRecipesScreen() {
        favoritesAppearanceTest?.iSeeAllTheMeatRecipesScreen()
    }

    @Then("I scroll and click on required {string}")
    fun iScrollAndClickOnIndex(index: String) {
        favoritesAppearanceTest?.iScrollAndClickOnIndex(index.toInt())
    }

    @Then("I see the Doneness tumbler screen")
    fun iSeeTheDonenessTumblerScreen() {
        favoritesAppearanceTest?.iSeeTheDonenessTumblerScreen()
    }

    @Then("I scroll Doneness level tumbler to {string}")
    fun iScrollDonenessLevelTumblerToLevel(level: String) {
        favoritesAppearanceTest?.iScrollDonenessLevelTumblerToLevel(level)
    }

    @Then("I click on Next button on the Doneness tumbler screen")
    fun iClickOnNextButtonOnTheDonenessTumblerScreen() {
        favoritesAppearanceTest?.iClickOnNextButtonOnTheDonenessTumblerScreen()
    }

    @Then("I click on Mode parameter")
    fun iClickOnModeParameter() {
        favoritesAppearanceTest?.iClickOnModeParameter()
    }

    @Then("I click on Oven temperature parameter")
    fun iClickOnOvenTemperatureParameter() {
        favoritesAppearanceTest?.iClickOnOvenTemperatureParameter()
    }

    @Then("I scroll and click on Time parameter {string}")
    fun iScrollAndClickOnTimeParameter(parameter: String) {
        favoritesAppearanceTest?.iScrollAndClickOnTimeParameter(parameter.toInt())
    }

    @Then("I click on the Save button")
    fun iClickOnTheSaveButton() {
        favoritesAppearanceTest?.iClickOnTheSaveButton()
    }

    @Then("I see Favorite already exists popup")
    fun iSeeFavoriteAlreadyExistsPopup() {
        favoritesAppearanceTest?.iSeeFavoriteAlreadyExistsPopup()
    }

    @Then("I click the OK button on the Favorite already exists popup")
    fun iClickTheOKButtonOnTheFavoriteAlreadyExistsPopup() {
        favoritesAppearanceTest?.iClickTheOKButtonOnTheFavoriteAlreadyExistsPopup()
    }

    @Then("I click on the recent added favorites cycle")
    fun iClickOnTheRecentAddedFavoritesCycle() {
        favoritesAppearanceTest?.iClickOnTheRecentAddedFavoritesCycle()
    }

    @Then("I click on Start button on details of the favorites recipe screen")
    fun iClickOnStartButtonOnDetailsOfTheFavoritesRecipeScreen() {
        favoritesAppearanceTest?.iClickOnStartButtonOnDetailsOfTheFavoritesRecipeScreen()
    }

    @Then("I see recipe status screen")
    fun iSeeRecipeStatusScreen() {
        favoritesAppearanceTest?.iSeeRecipeStatusScreen()
    }

    @Then("I click on Start Timer button")
    fun iClickOnStartTimerButton() {
        favoritesAppearanceTest?.iClickOnStartTimerButton()
    }

    @Then("I see cycle has started")
    fun iSeeCycleHasStarted() {
        favoritesAppearanceTest?.iSeeCycleHasStarted()
    }

    @Then("I click Turn OFF button")
    fun iClickTurnOFFButton() {
        favoritesAppearanceTest?.iClickTurnOFFButton()
    }

    @Then("I click on the existing Favorites")
    fun iClickOnTheExistingFavorites() {
        favoritesAppearanceTest?.iClickOnTheExistingFavorites()
    }

    @Then("I click on Choose Image button")
    fun iClickOnChooseImageButton() {
        favoritesAppearanceTest?.iClickOnChooseImageButton()
    }

    @Then("I scroll and click any image to {string}")
    fun iScrollAndClickAnyImageTo(image: String) {
        favoritesAppearanceTest?.iScrollAndClickAnyImageTo(image.toInt())
    }

    @Then("I see Image tumbler")
    fun iSeeImageTumbler() {
        favoritesAppearanceTest?.iSeeImageTumbler()
    }

    @Then("I see Leave Image Selection Popup")
    fun iSeeLeaveImageSelectionPopup() {
        favoritesAppearanceTest?.iSeeLeaveImageSelectionPopup()
    }

    @Then("I click Yes button on the Leave Image Selection Popup")
    fun iClickYesButtonOnTheLeaveImageSelectionPopup() {
        favoritesAppearanceTest?.iClickYesButtonOnTheLeaveImageSelectionPopup()
    }

    @Then("I click on Set button")
    fun iClickOnSetButton() {
        favoritesAppearanceTest?.iClickOnSetButton()
    }

    @Then("I see the image is set")
    fun iSeeTheImageIsSet() {
        favoritesAppearanceTest?.iSeeTheImageIsSet()
    }

    @Then("I see choose an image screen")
    fun iSeeChooseAnImageScreen() {
        favoritesAppearanceTest?.iSeeChooseAnImageScreen()
    }

    @Then("I see details of the favorites recipe screen with Image updated")
    fun iSeeDetailsOfTheFavoritesRecipeScreenWithImageUpdated() {
        favoritesAppearanceTest?.iSeeDetailsOfTheFavoritesRecipeScreenWithImageUpdated()
    }

    @Then("I click on Update Image button")
    fun iClickOnUpdateImageButton() {
        favoritesAppearanceTest?.iClickOnUpdateImageButton()
    }

    @Then("I see the horizontal tumbler screen to set Mode parameter")
    fun iSeeTumblerToSetModeParameter(){
        favoritesAppearanceTest?.iSeeTumblerToSetModeParameter()
    }

    @Then("I check the Header title text view of Favorites list screen with no favorites added")
    fun iCheckTheHeaderTitleTextViewOfFavoritesListScreenWithNoFavoritesAdded() {
        favoritesAppearanceTest?.iCheckTheHeaderTitleTextViewOfFavoritesListScreenWithNoFavoritesAdded()
    }

    @Then("I check the Favorites list screen with no favorites added header title text")
    fun iCheckTheFavoritesListScreenWithNoFavoritesAddedHeaderTitleText() {
        favoritesAppearanceTest?.iCheckTheFavoritesListScreenWithNoFavoritesAddedHeaderTitleText()
    }

    @Then("I check the description text view of Favorites list screen with no favorites added")
    fun iCheckTheDescriptionTextViewOfFavoritesListScreenWithNoFavoritesAdded() {
        favoritesAppearanceTest?.iCheckTheDescriptionTextViewOfFavoritesListScreenWithNoFavoritesAdded()
    }

    @Then("I check the Favorites list screen with no favorites added description text view")
    fun iCheckTheFavoritesListScreenWithNoFavoritesAddedDescriptionTextView() {
        favoritesAppearanceTest?.iCheckTheFavoritesListScreenWithNoFavoritesAddedDescriptionTextView()
    }

    @Then("I check the back button")
    fun iCheckTheBackButton() {
        favoritesAppearanceTest?.iCheckTheBackButton()
    }

    @Then("I check the plus icon")
    fun iCheckThePlusIcon() {
        favoritesAppearanceTest?.iCheckThePlusIcon()
    }

    @Then("I check the Header title text view of Favorites screen")
    fun iCheckTheHeaderTitleTextViewOfFavoritesScreen() {
        favoritesAppearanceTest?.iCheckTheHeaderTitleTextViewOfFavoritesScreen()
    }

    @Then("I check the Favorites screen Header title text")
    fun iCheckTheFavoritesScreenHeaderTitleText() {
        favoritesAppearanceTest?.iCheckTheFavoritesScreenHeaderTitleText()
    }

    @Then("I check the Manual modes title text view")
    fun iCheckTheManualModesTitleTextView() {
        favoritesAppearanceTest?.iCheckTheManualModesTitleTextView()
    }

    @Then("I check the History title text view")
    fun iCheckTheHistoryTitleTextView() {
        favoritesAppearanceTest?.iCheckTheHistoryTitleTextView()
    }

    @Then("I check the Auto Cook title text view")
    fun iCheckTheAutoCookTitleTextView() {
        favoritesAppearanceTest?.iCheckTheAutoCookTitleTextView()
    }

    @Then("I check the Manual modes title text")
    fun iCheckTheManualModesTitleText() {
        favoritesAppearanceTest?.iCheckTheManualModesTitleText()
    }

    @Then("I check the Auto Cook title text")
    fun iCheckTheAutoCookTitleText() {
        favoritesAppearanceTest?.iCheckTheAutoCookTitleText()
    }

    @Then("I check the History title text")
    fun iCheckTheHistoryTitleText() {
        favoritesAppearanceTest?.iCheckTheHistoryTitleText()
    }

    @Then("I click on Favorites on More Options popup")
    fun iClickOnSaveAsFavorites() {
        favoritesAppearanceTest?.iClickOnSaveAsFavorites()
    }

    @Then("I see recipe saved as favorites notification")
    fun iSeeRecipeSavedAsFavoritesNotification() {
        favoritesAppearanceTest?.iSeeRecipeSavedAsFavoritesNotification()
    }

    @Then("I check the notification text view")
    fun iCheckTheNotificationTextView() {
        favoritesAppearanceTest?.iCheckTheNotificationTextView()
    }

    @Then("I wait for ten seconds for the notification to go off")
    fun iWaitForSecondsForTheNotificationToGoOff() {
        favoritesAppearanceTest?.iWaitForSecondsForTheNotificationToGoOff()
    }

    @Then("I see recipe already as favorites notification")
    fun iSeeRecipeAlreadyAsFavoritesNotification() {
        favoritesAppearanceTest?.iSeeRecipeAlreadyAsFavoritesNotification()
    }

    @Then("I check the Header title text view of Create a Favorites screen")
    fun iCheckTheHeaderTitleTextViewOfCreateAFavoritesScreen() {
        favoritesAppearanceTest?.iCheckTheHeaderTitleTextViewOfCreateAFavoritesScreen()
    }

    @Then("I check the Create a Favorites screen Header title text")
    fun iCheckTheCreateAFavoritesScreenHeaderTitleText() {
        favoritesAppearanceTest?.iCheckTheCreateAFavoritesScreenHeaderTitleText()
    }

    @Then("I check the Header title text view of Favorites Preview screen")
    fun iCheckTheHeaderTitleTextViewOfFavoritesPreviewScreen() {
        favoritesAppearanceTest?.iCheckTheHeaderTitleTextViewOfFavoritesPreviewScreen()
    }

    @Then("I check the Favorites Preview screen Header title text")
    fun iCheckTheFavoritesPreviewScreenHeaderTitleText() {
        favoritesAppearanceTest?.iCheckTheFavoritesPreviewScreenHeaderTitleText()
    }

    @Then("I check the Edit icon")
    fun iCheckTheEditIcon() {
        favoritesAppearanceTest?.iCheckTheEditIcon()
    }

    @Then("I check the Choose Image button view")
    fun iCheckTheChooseImageButtonView() {
        favoritesAppearanceTest?.iCheckTheChooseImageButtonView()
    }

    @Then("I check the Choose Image button text")
    fun iCheckTheChooseImageButtonText() {
        favoritesAppearanceTest?.iCheckTheChooseImageButtonText()
    }

    @Then("I check the Save button view")
    fun iCheckTheSaveButtonView() {
        favoritesAppearanceTest?.iCheckTheSaveButtonView()
    }

    @Then("I check the Save button text")
    fun iCheckTheSaveButtonText() {
        favoritesAppearanceTest?.iCheckTheSaveButtonText()
    }

    @Then("I check the Mode parameter view")
    fun iCheckTheModeParameterView() {
        favoritesAppearanceTest?.iCheckTheModeParameterView()
    }

    @Then("I check the Mode parameter text view")
    fun iCheckTheModeParameterTextView() {
        favoritesAppearanceTest?.iCheckTheModeParameterTextView()
    }

    @Then("I check the Oven Temperature parameter view")
    fun iCheckTheOvenTemperatureParameterView() {
        favoritesAppearanceTest?.iCheckTheOvenTemperatureParameterView()
    }

    @Then("I check the Oven Temperature parameter text view")
    fun iCheckTheOvenTemperatureParameterTextView() {
        favoritesAppearanceTest?.iCheckTheOvenTemperatureParameterTextView()
    }

    @Then("I check the Time parameter view")
    fun iCheckTheTimeParameterView() {
        favoritesAppearanceTest?.iCheckTheTimeParameterView()
    }

    @Then("I check the Time parameter text view")
    fun iCheckTheTimeParameterTextView() {
        favoritesAppearanceTest?.iCheckTheTimeParameterTextView()
    }

    @Then("I check the Mode parameter subtitle text view")
    fun iCheckTheModeParameterSubtitleTextView() {
        favoritesAppearanceTest?.iCheckTheModeParameterSubtitleTextView()
    }

    @Then("I check the Oven Temperature parameter subtitle text view")
    fun iCheckTheOvenTemperatureParameterSubtitleTextView() {
        favoritesAppearanceTest?.iCheckTheOvenTemperatureParameterSubtitleTextView()
    }

    @Then("I check the Time parameter subtitle text view")
    fun iCheckTheTimeParameterSubtitleTextView() {
        favoritesAppearanceTest?.iCheckTheTimeParameterSubtitleTextView()
    }

    @Then("I check the header title view of Choose an Image screen")
    fun iCheckTheHeaderTitleViewOfChooseAnImageScreen() {
        favoritesAppearanceTest?.iCheckTheHeaderTitleViewOfChooseAnImageScreen()
    }

    @Then("I check the header title text view of Choose an Image screen")
    fun iCheckTheHeaderTitleTextViewOfChooseAnImageScreen() {
        favoritesAppearanceTest?.iCheckTheHeaderTitleTextViewOfChooseAnImageScreen()
    }

    @Then("I check the Image Tumbler of Choose an Image screen")
    fun iCheckTheImageTumblerOfChooseAnImageScreen() {
        favoritesAppearanceTest?.iCheckTheImageTumblerOfChooseAnImageScreen()
    }

    @Then("I check the header title of Image tumbler screen")
    fun iCheckTheHeaderTitleOfImageTumblerScreen() {
        favoritesAppearanceTest?.iCheckTheHeaderTitleOfImageTumblerScreen()
    }

    @Then("I check the header title text of Image tumbler screen")
    fun iCheckTheHeaderTitleTextOfImageTumblerScreen() {
        favoritesAppearanceTest?.iCheckTheHeaderTitleTextOfImageTumblerScreen()
    }

    @Then("I check the Set button text")
    fun iCheckTheSetButtonText() {
        favoritesAppearanceTest?.iCheckTheSetButtonText()
    }

    @Then("I check the Set button view")
    fun iCheckTheSetButtonView() {
        favoritesAppearanceTest?.iCheckTheSetButtonView()
    }

    @Then("I check the Newly Added Recipe view")
    fun iCheckTheNewlyAddedRecipeView() {
        favoritesAppearanceTest?.iCheckTheNewlyAddedRecipeView()
    }

    @Then("I check the Newly Added Recipe text")
    fun iCheckTheNewlyAddedRecipeText() {
        favoritesAppearanceTest?.iCheckTheNewlyAddedRecipeText()
    }

    @Then("I check the Newly Added Recipe oven icon")
    fun iCheckTheNewlyAddedRecipeOvenIcon() {
        favoritesAppearanceTest?.iCheckTheNewlyAddedRecipeOvenIcon()
    }

    @Then("I click on History")
    fun iClickOnHistory() {
        favoritesAppearanceTest?.iClickOnHistory()
    }

    @Then("I click on the recently run cycle for history list")
    fun iClickOnTheRecentlyRunCycle() {
        favoritesAppearanceTest?.iClickOnTheRecentlyRunCycle()
    }

    @Then("I see details of the favorites recipe screen for history")
    fun iSeeFavPreviewScreenForHistory() {
        favoritesAppearanceTest?.iSeeFavPreviewScreenForHistory()
    }

    @Then("I click on Probe")
    fun iClickOnProbe() {
        favoritesAppearanceTest?.iClickOnProbe()
    }

    @Then("I click on Bake")
    fun iClickOnBake() {
        favoritesAppearanceTest?.iClickOnBake()
    }

    @Then("I click next button on probe temp tumbler")
    fun iClickNextButtonOnProbeTempTumbler() {
        favoritesAppearanceTest?.iClickNextButtonOnProbeTempTumbler()
    }

    @Then("I see Favorites preview screen for probe")
    fun iSeeFavoritesPreviewScreenForProbe() {
        favoritesAppearanceTest?.iSeeFavoritesPreviewScreenForProbe()
    }

    @Then("I validate the Probe temperature parameter view")
    fun iValidateTheProbeTemperatureParameterView() {
        favoritesAppearanceTest?.iValidateTheProbeTemperatureParameterView()
    }

    @Then("I validate the Probe temperature parameter subtitle view")
    fun iValidateTheProbeTemperatureParameterSubtitleView() {
        favoritesAppearanceTest?.iValidateTheProbeTemperatureParameterSubtitleView()
    }

    @Then("I validate the Probe temperature parameter text")
    fun iValidateTheProbeTemperatureParameterText() {
        favoritesAppearanceTest?.iValidateTheProbeTemperatureParameterText()
    }

    @Then("I validate the Probe temperature parameter subtitle text")
    fun iValidateTheProbeTemperatureParameterSubtitleText() {
        favoritesAppearanceTest?.iValidateTheProbeTemperatureParameterSubtitleText()
    }

    @Then("I check oven temp parameter")
    fun iCheckOvenTempParameter() {
        favoritesAppearanceTest?.iCheckOvenTempParameter()
    }

    @Then("I see Favorites list screen and the recipe {string} with the probe icon")
    fun iSeeFavoritesListScreenAndTheRecipeWithTheProbeIcon(name: String) {
        favoritesAppearanceTest?.iSeeFavoritesListScreenAndTheRecipeWithTheProbeIcon(name)
    }

    @Then("I click on Save To Favorites")
    fun iClickOnSaveToFavorites() {
        favoritesAppearanceTest?.iClickOnSaveToFavorites()
    }

    @Then("I see history screen")
    fun iSeeHistoryScreen() {
        favoritesAppearanceTest?.iSeeHistoryScreen()
    }

    @Then("I check the History screen header title text")
    fun iCheckTheHistoryScreenHeaderTitleText() {
        favoritesAppearanceTest?.iCheckTheHistoryScreenHeaderTitleText()
    }

    @Then("I check the History screen header title view")
    fun iCheckTheHistoryScreenHeaderTitleView() {
        favoritesAppearanceTest?.iCheckTheHistoryScreenHeaderTitleView()
    }

    @Then("I check the recent recipe text")
    fun iCheckTheRecentRecipeText() {
        favoritesAppearanceTest?.iCheckTheRecentRecipeText()
    }

    @Then("I check the recent recipe view")
    fun iCheckTheRecentRecipeView() {
        favoritesAppearanceTest?.iCheckTheRecentRecipeView()
    }

    @Then("I check the recent recipe subtitle text and view")
    fun iCheckTheRecentRecipeSubtitleView() {
        favoritesAppearanceTest?.iCheckTheRecentRecipeSubtitleView()
    }

    @Then("I check the recent recipe Time view")
    fun iCheckTheRecentRecipeTimeView() {
        favoritesAppearanceTest?.iCheckTheRecentRecipeTimeView()
    }

    @Then("I check the recent recipe oven icon")
    fun iCheckTheRecentRecipeOvenIcon() {
        favoritesAppearanceTest?.iCheckTheRecentRecipeOvenIcon()
    }

    @Then("I set the temperature to 180 degrees")
    fun iSetTheTemperatureTo180Degrees() {
        favoritesAppearanceTest?.iSetTheTemperatureTo180Degrees()
    }

    @Then("I set the CookTime 5 min")
    fun iSetTheCookTime5Min() {
        favoritesAppearanceTest?.iSetTheCookTime5Min()
    }

    @Then("I set the temperature to 190 degrees")
    fun iSetTheTemperatureTo190Degrees() {
        favoritesAppearanceTest?.iSetTheTemperatureTo190Degrees()
    }

    @Then("I set the CookTime to 1 min")
    fun iSetTheCookTimeTo1Min() {
        favoritesAppearanceTest?.iSetTheCookTimeTo1Min()
    }

    @Then("I set the temperature to 200 degrees")
    fun iSetTheTemperatureTo200Degrees() {
        favoritesAppearanceTest?.iSetTheTemperatureTo200Degrees()
    }

    @Then("I set the CookTime to 7 min")
    fun iSetTheCookTimeTo7Min() {
        favoritesAppearanceTest?.iSetTheCookTimeTo7Min()
    }

    @Then("I set the temperature to 210 degrees")
    fun iSetTheTemperatureTo210Degrees() {
        favoritesAppearanceTest?.iSetTheTemperatureTo210Degrees()
    }

    @Then("I set the CookTime to 10 min")
    fun iSetTheCookTimeTo10Min() {
        favoritesAppearanceTest?.iSetTheCookTimeTo10Min()
    }

    @Then("I set the temperature to 235 degrees")
    fun iSetTheTemperatureTo235Degrees() {
        favoritesAppearanceTest?.iSetTheTemperatureTo235Degrees()
    }

    @Then("I see Max Limit reached popup")
    fun iSeeMaxLimitReachedPopup() {
        favoritesAppearanceTest?.iSeeMaxLimitReachedPopup()
    }

    @Then("I check Max Limit reached popup Title view")
    fun iCheckMaxLimitReachedPopupTitleView() {
        favoritesAppearanceTest?.iCheckMaxLimitReachedPopupTitleView()
    }

    @Then("I check Max Limit reached popup Title text")
    fun iCheckMaxLimitReachedPopupTitleText() {
        favoritesAppearanceTest?.iCheckMaxLimitReachedPopupTitleText()
    }

    @Then("I check Max Limit reached popup description view")
    fun iCheckMaxLimitReachedPopupDescriptionView() {
        favoritesAppearanceTest?.iCheckMaxLimitReachedPopupDescriptionView()
    }

    @Then("I check Max Limit reached popup description text")
    fun iCheckMaxLimitReachedPopupDescriptionText() {
        favoritesAppearanceTest?.iCheckMaxLimitReachedPopupDescriptionText()
    }

    @Then("I check Max Limit reached popup OKAY button")
    fun iCheckMaxLimitReachedPopupOKButton() {
        favoritesAppearanceTest?.iCheckMaxLimitReachedPopupOKButton()
    }

    @Then("I click on OK button")
    fun iClickOnOKButton() {
        favoritesAppearanceTest?.iClickOnOKButton()
    }

    @Then("I see multiple favorites added view")
    fun iSeeMultipleFavoritesAddedView() {
        favoritesAppearanceTest?.iSeeMultipleFavoritesAddedView()
    }

    @Then("I check the Leave Image Selection Popup title text")
    fun iCheckTheLeaveImageSelectionPopupTitleText() {
        favoritesAppearanceTest?.iCheckTheLeaveImageSelectionPopupTitleText()
    }

    @Then("I check the Leave Image Selection Popup title view")
    fun iCheckTheLeaveImageSelectionPopupTitleView() {
        favoritesAppearanceTest?.iCheckTheLeaveImageSelectionPopupTitleView()
    }

    @Then("I check the Leave Image Selection Popup description view")
    fun iCheckTheLeaveImageSelectionPopupDescriptionView() {
        favoritesAppearanceTest?.iCheckTheLeaveImageSelectionPopupDescriptionView()
    }

    @Then("I check the Leave Image Selection Popup description text")
    fun iCheckTheLeaveImageSelectionPopupDescriptionText() {
        favoritesAppearanceTest?.iCheckTheLeaveImageSelectionPopupDescriptionText()
    }

    @Then("I check the YES button view")
    fun iCheckTheYESButtonView() {
        favoritesAppearanceTest?.iCheckTheYESButtonView()
    }

    @Then("I check the NO button view")
    fun iCheckTheNOButtonView() {
        favoritesAppearanceTest?.iCheckTheNOButtonView()
    }

    @Then("I check the YES button text")
    fun iCheckTheYESButtonText() {
        favoritesAppearanceTest?.iCheckTheYESButtonText()
    }

    @Then("I check the NO button text")
    fun iCheckTheNOButtonText() {
        favoritesAppearanceTest?.iCheckTheNOButtonText()
    }

    @Then("I check the Favorite already exists Popup title text")
    fun iCheckTheFavoriteAlreadyExistsPopupTitleText() {
        favoritesAppearanceTest?.iCheckTheFavoriteAlreadyExistsPopupTitleText()
    }

    @Then("I check the Favorite already exists Popup title view")
    fun iCheckTheFavoriteAlreadyExistsPopupTitleView() {
        favoritesAppearanceTest?.iCheckTheFavoriteAlreadyExistsPopupTitleView()
    }

    @Then("I check the Favorite already exists Popup description text")
    fun iCheckTheFavoriteAlreadyExistsPopupDescriptionText() {
        favoritesAppearanceTest?.iCheckTheFavoriteAlreadyExistsPopupDescriptionText()
    }

    @Then("I check the Favorite already exists Popup description view")
    fun iCheckTheFavoriteAlreadyExistsPopupDescriptionView() {
        favoritesAppearanceTest?.iCheckTheFavoriteAlreadyExistsPopupDescriptionView()
    }

    @Then("I check the OK button view")
    fun iCheckTheOKButtonView() {
        favoritesAppearanceTest?.iCheckTheOKButtonView()
    }

    @Then("I check the OK button text")
    fun iCheckTheOKButtonText() {
        favoritesAppearanceTest?.iCheckTheOKButtonText()
    }

    @Then("I check the left holder arrow")
    fun iCheckTheLeftHolderArrow() {
        favoritesAppearanceTest?.iCheckTheLeftHolderArrow()
    }

    @Then("I check the Stepper bar")
    fun iCheckTheStepperBar() {
        favoritesAppearanceTest?.iCheckTheStepperBar()
    }

    @Then("I check the right holder arrow")
    fun iCheckTheRightHolderArrow() {
        favoritesAppearanceTest?.iCheckTheRightHolderArrow()
    }

    @Then("I check Delay button")
    fun iCheckDelayButton() {
        favoritesAppearanceTest?.iCheckDelayButton()
    }

    @Then("I check Start button")
    fun iCheckStartButton() {
        favoritesAppearanceTest?.iCheckStartButton()
    }

    @Then("I click on Delay button")
    fun iClickDelayButton() {
        favoritesAppearanceTest?.iClickDelayButton()
    }

    @Then("I long click for 3 seconds on the recent added favorites cycle")
    fun iLongClickFor3SecondsOnTheRecentAddedFavoritesCycle() {
        favoritesAppearanceTest?.iLongClickForSecondsOnTheRecentAddedFavoritesCycle(5000 )
    }

    @Then("I see delete and cancel icon")
    fun iSeeDeleteAndCancelIcon() {
        favoritesAppearanceTest?.iSeeDeleteAndCancelIcon()
    }

    @Then("I click on delete icon")
    fun iClickOnDeleteIcon() {
        favoritesAppearanceTest?.iClickOnDeleteIcon()
    }

    @Then("I see delete favorites popup")
    fun iSeeDeleteFavoritesPopup() {
        favoritesAppearanceTest?.iSeeDeleteFavoritesPopup()
    }

    @Then("I check the Delete Favorites Popup title view")
    fun iCheckTheDeleteFavoritesPopupTitleView() {
        favoritesAppearanceTest?.iCheckTheDeleteFavoritesPopupTitleView()
    }

    @Then("I check the Delete Favorites Popup title text")
    fun iCheckTheDeleteFavoritesPopupTitleText() {
        favoritesAppearanceTest?.iCheckTheDeleteFavoritesPopupTitleText()
    }

    @Then("I check the Delete Favorites Popup description text")
    fun iCheckTheDeleteFavoritesPopupDescriptionText() {
        favoritesAppearanceTest?.iCheckTheDeleteFavoritesPopupDescriptionText()
    }

    @Then("I check the Delete Favorites Popup description view")
    fun iCheckTheDeleteFavoritesPopupDescriptionView() {
        favoritesAppearanceTest?.iCheckTheDeleteFavoritesPopupDescriptionView()
    }

    @Then("I click on NO button")
    fun iClickOnNOButton() {
        favoritesAppearanceTest?.iClickOnNOButton()
    }
}