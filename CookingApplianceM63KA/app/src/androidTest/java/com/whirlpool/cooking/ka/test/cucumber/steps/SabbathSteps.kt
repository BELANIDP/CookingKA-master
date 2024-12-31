package com.whirlpool.cooking.ka.test.cucumber.steps


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.SabbathAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import leakcanary.DetectLeaksAfterTestSuccess
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SabbathSteps {
    private var sabbathAppearanceTest:SabbathAppearanceTest?=null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        sabbathAppearanceTest = SabbathAppearanceTest()
    }
    @After
    fun tearDown() {
        sabbathAppearanceTest = null
    }

    @And("I click on Sabbath/KCF")
    fun scrollAndClickOnSabbath(){
        sabbathAppearanceTest?.scrollAndClickOnSabbath()
    }

    @Then("I see Sabbath mode selection screen")
    fun iSeeSabbathScreen(){
        sabbathAppearanceTest?.iSeeSabbathScreen()
    }

//-------------------------Appearance-----------------------------

    @Then("I check the Sabbath header {string} text view")
    fun iCheckTheSabbathHeaderTextView(title : String) {
        sabbathAppearanceTest?.iCheckTheSabbathHeaderTextView(title)
    }

    @Then("I check the Sabbath header text view alignment")
    fun iCheckTheSabbathHeaderTextViewAlignment() {
        sabbathAppearanceTest?.iCheckTheSabbathHeaderTextViewAlignment()
    }

    @Then("I check the Sabbath header text view size")
    fun iCheckTheSabbathHeaderTextViewSize() {
        sabbathAppearanceTest?.iCheckTheSabbathHeaderTextViewSize()
    }

    @Then("I check the Sabbath header text view color")
    fun iCheckTheSabbathHeaderTextViewColor() {
        sabbathAppearanceTest?.iCheckTheSabbathHeaderTextViewColor()
    }

    @Then("I check the background color for Sabbath mode selection screen")
    fun iCheckTheBackgroundColorForSabbathModeSelectionScreen() {
        sabbathAppearanceTest?.iCheckTheBackgroundColorForSabbathModeSelectionScreen()
    }

    @Then("I check the Sabbath mode {string} text view")
    fun iCheckTheSabbathModeTextView(title : String) {
        sabbathAppearanceTest?.iCheckTheSabbathModeTextView(title)
    }

    @Then("I check the Sabbath mode text view size")
    fun iCheckTheSabbathModeTextViewSize() {
        sabbathAppearanceTest?.iCheckTheSabbathModeTextViewSize()
    }

    @Then("I check the Sabbath mode text view color")
    fun iCheckTheSabbathModeTextViewColor() {
        sabbathAppearanceTest?.iCheckTheSabbathModeTextViewColor()
    }

    @Then("I check the Sabbath mode subtitle {string} text view")
    fun iCheckTheSabbathModeSubtitleTextView(title1: String) {
        sabbathAppearanceTest?.iCheckTheSabbathModeSubtitleTextView(title1)
    }

    @Then("I check the Sabbath mode subtitle text view size")
    fun iCheckTheSabbathModeSubtitleTextViewSize() {
        sabbathAppearanceTest?.iCheckTheSabbathModeSubtitleTextViewSize()
    }

    @Then("I check the Sabbath mode subtitle text view color")
    fun iCheckTheSabbathModeSubtitleTextViewColor() {
        sabbathAppearanceTest?.iCheckTheSabbathModeSubtitleTextViewColor()
    }

    @Then("I check the Sabbath bake {string} text view")
    fun iCheckTheSabbathBakeTextView(title : String) {
        sabbathAppearanceTest?.iCheckTheSabbathBakeTextView(title)
    }

    @Then("I check the Sabbath bake text view size")
    fun iCheckTheSabbathBakeTextViewSize() {
        sabbathAppearanceTest?.iCheckTheSabbathBakeTextViewSize()
    }

    @Then("I check the Sabbath bake text view color")
    fun iCheckTheSabbathBakeTextViewColor() {
        sabbathAppearanceTest?.iCheckTheSabbathBakeTextViewColor()
    }

    @Then("I check the Sabbath bake subtitle {string} text view")
    fun iCheckTheSabbathBakeSubtitleTextView(title1: String) {
        sabbathAppearanceTest?.iCheckTheSabbathBakeSubtitleTextView(title1)
    }

    @Then("I check the Sabbath bake subtitle text view size")
    fun iCheckTheSabbathBakeSubtitleTextViewSize() {
        sabbathAppearanceTest?.iCheckTheSabbathBakeSubtitleTextViewSize()
    }

    @Then("I check the Sabbath bake subtitle text view color")
    fun iCheckTheSabbathBakeSubtitleTextViewColor() {
        sabbathAppearanceTest?.iCheckTheSabbathBakeSubtitleTextViewColor()
    }

    @Then("I check the Back button is clickable")
    fun iCheckTheBackButton() {
        sabbathAppearanceTest?.iCheckTheBackButton()
    }

    @Then("I check the Back button is enabled")
    fun iCheckTheBackButtonIsEnabled() {
        sabbathAppearanceTest?.iCheckTheBackButtonIsEnabled()
    }

    @Then("I check the background of the back button")
    fun iCheckTheBackgroundOfTheBackButton() {
        sabbathAppearanceTest?.iCheckTheBackgroundOfTheBackButton()
    }

    @Then("I click the back button")
    fun iClickTheBackButton() {
        sabbathAppearanceTest?.iClickTheBackButton()
    }

    @Then("I see settings screen")
    fun iSeeSettingsScreen() {
        sabbathAppearanceTest?.iSeeSettingsScreen()
    }

    @Then("I check the Cancel button is not clickable")
    fun iCheckTheCancelButtonIsNotClickable() {
        sabbathAppearanceTest?.iCheckTheCancelButtonIsNotClickable()
    }

    @Then("I check the background of the cancel button")
    fun iCheckTheBackgroundOfTheCancelButton() {
        sabbathAppearanceTest?.iCheckTheBackgroundOfTheCancelButton()
    }

    @Then("I click on Sabbath Mode")
    fun iClickOnSabbathMode() {
        sabbathAppearanceTest?.iClickOnSabbathMode()
    }

    @Then("I see Instruction screen for Sabbath Mode")
    fun iSeeInstructionScreenForSabbathMode() {
        sabbathAppearanceTest?.iSeeInstructionScreenForSabbathMode()
    }

    @Then("I check the header text {string} of Sabbath Mode screen")
    fun iCheckTheHeaderTextOfSabbathModeScreen(title: String) {
        sabbathAppearanceTest?.iCheckTheHeaderTextOfSabbathModeScreen(title)
    }

    @Then("I check the Sabbath Mode Screen header text view alignment")
    fun iCheckTheSabbathModeScreenTextViewAlignment() {
        sabbathAppearanceTest?.iCheckTheSabbathModeScreenTextViewAlignment()
    }

    @Then("I check the Sabbath Mode Screen header text view size")
    fun iCheckTheSabbathModeScreenTextViewSize() {
        sabbathAppearanceTest?.iCheckTheSabbathModeScreenTextViewSize()
    }

    @Then("I check the Sabbath Mode Screen header text view color")
    fun iCheckTheSabbathModeScreenHeaderTextViewColor(){
        sabbathAppearanceTest?.iCheckTheSabbathModeScreenHeaderTextViewColor()
    }

    @Then("I check the Sabbath Mode Screen header text view font")
    fun iCheckTheSabbathModeScreenHeaderTextViewFont() {
        sabbathAppearanceTest?.iCheckTheSabbathModeScreenHeaderTextViewFont()
    }

    @Then("I validate the description of Sabbath Mode feature")
    fun iValidateTheDescriptionOfSabbathModeFeature() {
        sabbathAppearanceTest?.iValidateTheDescriptionOfSabbathModeFeature()
    }

    @Then("I validate the description text alignment of Sabbath Mode feature")
    fun iValidateTheDescriptionTextAlignmentOfSabbathModeFeature(){
        sabbathAppearanceTest?.iValidateTheDescriptionTextAlignmentOfSabbathModeFeature()
    }

    @Then("I validate the description text size of Sabbath Mode feature")
    fun iValidateTheDescriptionTextSizeOfSabbathModeFeature() {
        sabbathAppearanceTest?.iValidateTheDescriptionTextSizeOfSabbathModeFeature()
    }

    @Then("I validate the description text color of Sabbath Mode feature")
    fun iValidateTheDescriptionTextColorOfSabbathModeFeature(){
        sabbathAppearanceTest?.iValidateTheDescriptionTextColorOfSabbathModeFeature()
    }

    @Then("I validate the description text font of Sabbath Mode feature")
    fun iValidateTheDescriptionTextFontOfSabbathModeFeature() {
        sabbathAppearanceTest?.iValidateTheDescriptionTextFontOfSabbathModeFeature()
    }

    @Then("I check the don't show again check box is visible")
    fun iCheckTheDonTShowAgainCheckBoxIsVisible() {
        sabbathAppearanceTest?.iCheckTheDonTShowAgainCheckBoxIsVisible()
    }

    @Then("I check the don't show again check box is enabled")
    fun iCheckTheDonTShowAgainCheckBoxIsEnable() {
        sabbathAppearanceTest?.iCheckTheDonTShowAgainCheckBoxIsEnable()
    }

    @Then("I check the don't show again check box is clickable")
    fun iCheckTheDonTShowAgainCheckBoxIsClickable() {
        sabbathAppearanceTest?.iCheckTheDonTShowAgainCheckBoxIsClickable()
    }

    @Then("I check the text view of don't show again check box")
    fun iCheckTheTextViewOfDonTShowAgainCheckBox() {
        sabbathAppearanceTest?.iCheckTheTextViewOfDonTShowAgainCheckBox()
    }

    @Then("I check the layout size of don't show again check box")
    fun iCheckTheLayoutSizeOfDonTShowAgainCheckBox() {
        sabbathAppearanceTest?.iCheckTheLayoutSizeOfDonTShowAgainCheckBox()
    }

    @Then("I check the Start button is visible")
    fun iCheckTheStartButtonIsVisible() {
        sabbathAppearanceTest?.iCheckTheStartButtonIsVisible()
    }

    @Then("I check the Start button is clickable")
    fun iCheckTheStartButtonIsClickable() {
        sabbathAppearanceTest?.iCheckTheStartButtonIsClickable()
    }

    @Then("I check the Start button is enabled")
    fun iCheckTheStartButtonIsEnabled() {
        sabbathAppearanceTest?.iCheckTheStartButtonIsEnabled()
    }

    @Then("I check the Start button text")
    fun iCheckTheStartButtonText(){
        sabbathAppearanceTest?.iCheckTheStartButtonText()
    }

    @Then("I click on Start Button for Sabbath Mode")
    fun iClickOnStartButtonForSabbathMode() {
        sabbathAppearanceTest?.iClickOnStartButtonForSabbathMode()
    }

    @Then("I see Sabbath Mode is ON")
    fun iSeeSabbathModeIsON() {
        sabbathAppearanceTest?.iSeeSabbathModeIsON()
    }

    @Then("I check the Sabbath {string} text view")
    fun iCheckTheSabbathTextView(title: String) {
        sabbathAppearanceTest?.iCheckTheSabbathTextView(title)
    }

    @Then("I check the Sabbath text view size")
    fun iCheckTheSabbathTextViewSize() {
        sabbathAppearanceTest?.iCheckTheSabbathTextViewSize()
    }

    @Then("I check the Sabbath text view color")
    fun iCheckTheSabbathTextViewColor() {
        sabbathAppearanceTest?.iCheckTheSabbathTextViewColor()
    }

    @Then("I check the Sabbath text view font")
    fun iCheckTheSabbathTextViewFont() {
        sabbathAppearanceTest?.iCheckTheSabbathTextViewFont()
    }

    @Then("I check the Sabbath text view alignment")
    fun iCheckTheSabbathTextViewAlignment() {
        sabbathAppearanceTest?.iCheckTheSabbathTextViewAlignment()
    }

    @Then("I check the Sabbath text view line height and weight")
    fun iCheckTheSabbathTextViewLineHeightAndWeight() {
        sabbathAppearanceTest?.iCheckTheSabbathTextViewLineHeightAndWeight()
    }

    @Then("I check the Sabbath icon visibility")
    fun iCheckTheSabbathIconVisibility() {
        sabbathAppearanceTest?.iCheckTheSabbathIconVisibility()
    }

    @Then("I check the Sabbath icon size")
    fun iCheckTheSabbathIconSize() {
        sabbathAppearanceTest?.iCheckTheSabbathIconSize()
    }

    @Then("I check the Sabbath press and hold text view")
    fun iCheckTheSabbathPressAndHoldTextView() {
        sabbathAppearanceTest?.iCheckTheSabbathPressAndHoldTextView()
    }

    @Then("I check the Sabbath press and hold text view size")
    fun iCheckTheSabbathPressAndHoldTextViewSize() {
        sabbathAppearanceTest?.iCheckTheSabbathPressAndHoldTextViewSize()

    }

    @Then("I check the Sabbath press and hold text view color")
    fun iCheckTheSabbathPressAndHoldTextViewColor() {
        sabbathAppearanceTest?.iCheckTheSabbathPressAndHoldTextViewColor()
    }

    @Then("I check the Sabbath press and hold text view font")
    fun iCheckTheSabbathPressAndHoldTextViewFont() {
        sabbathAppearanceTest?.iCheckTheSabbathPressAndHoldTextViewFont()
    }

    @Then("I check the Sabbath press and hold text view alignment")
    fun iCheckTheSabbathPressAndHoldTextViewAlignment() {
        sabbathAppearanceTest?.iCheckTheSabbathPressAndHoldTextViewAlignment()
    }

    @Then("I check the Sabbath press and hold text view line height and weight")
    fun iCheckTheSabbathPressAndHoldTextViewLineHeightAndWeight() {
        sabbathAppearanceTest?.iCheckTheSabbathPressAndHoldTextViewLineHeightAndWeight()
    }

    @Then("I press and hold on the screen for three seconds")
    fun iPressAndHoldOnTheScreenThreeForSeconds() {
        sabbathAppearanceTest?.iPressAndHoldOnTheScreenForThreeSeconds()
    }

    @Then("I see clock screen")
    fun iSeeClockScreen() {
        sabbathAppearanceTest?.iSeeClockScreen()
    }

    @Then("I click on Sabbath Bake")
    fun iClickOnSabbathBake() {
        sabbathAppearanceTest?.iClickOnSabbathBake()
    }

    @Then("I see Instruction screen for Sabbath Bake")
    fun iSeeInstructionScreenForSabbathBake() {
        sabbathAppearanceTest?.iSeeInstructionScreenForSabbathBake()
    }

    @Then("I validate the header text {string} of Sabbath Bake screen")
    fun iValidateTheHeaderTextOfSabbathBakeScreen(title:String) {
        sabbathAppearanceTest?.iValidateTheHeaderTextOfSabbathBakeScreen(title)
    }

    @Then("I check the Sabbath Bake mode header text view alignment")
    fun iCheckTheSabbathBakeModeTextViewAlignment() {
        sabbathAppearanceTest?.iCheckTheSabbathBakeModeTextViewAlignment()
    }

    @Then("I check the Sabbath Bake mode header text view size")
    fun iCheckTheSabbathBakeModeTextViewSize() {
        sabbathAppearanceTest?.iCheckTheSabbathBakeModeTextViewSize()
    }

    @Then("I check the Sabbath Bake mode header text view color")
    fun iCheckTheSabbathBakeModeTextViewColor() {
        sabbathAppearanceTest?.iCheckTheSabbathBakeModeTextViewColor()
    }

    @Then("I check the Sabbath Bake mode header text view font")
    fun iCheckTheSabbathBakeModeTextViewFont() {
        sabbathAppearanceTest?.iCheckTheSabbathBakeModeTextViewFont()
    }

    @Then("I check the Next button is visible")
    fun iCheckTheNextButtonIsVisible() {
        sabbathAppearanceTest?.iCheckTheNextButtonIsVisible()
    }

    @Then("I check the Next button text")
    fun iCheckTheNextButtonText() {
        sabbathAppearanceTest?.iCheckTheNextButtonText()
    }

    @Then("I check the Next button is clickable")
    fun iCheckTheNextButtonIsClickable() {
        sabbathAppearanceTest?.iCheckTheNextButtonIsClickable()
    }

    @Then("I check the Next button is enabled")
    fun iCheckTheNextButtonIsEnabled() {
        sabbathAppearanceTest?.iCheckTheNextButtonIsEnabled()
    }

    @Then("I validate the description of Sabbath Bake feature")
    fun iValidateTheDescriptionOfSabbathBakeFeature() {
        sabbathAppearanceTest?.iValidateTheDescriptionOfSabbathBakeFeature()
    }

    @Then("I validate the description text alignment of Sabbath Bake feature")
    fun iValidateTheDescriptionTextAlignmentOfSabbathBakeFeature() {
        sabbathAppearanceTest?.iValidateTheDescriptionTextAlignmentOfSabbathBakeFeature()
    }

    @Then("I validate the description text size of Sabbath Bake feature")
    fun iValidateTheDescriptionTextSizeOfSabbathBakeFeature() {
        sabbathAppearanceTest?.iValidateTheDescriptionTextSizeOfSabbathBakeFeature()
    }

    @Then("I validate the description text color of Sabbath Bake feature")
    fun iValidateTheDescriptionTextColorOfSabbathBakeFeature() {
        sabbathAppearanceTest?.iValidateTheDescriptionTextColorOfSabbathBakeFeature()
    }

    @Then("I validate the description text font of Sabbath Bake feature")
    fun iValidateTheDescriptionTextFontOfSabbathBakeFeature() {
        sabbathAppearanceTest?.iValidateTheDescriptionTextFontOfSabbathBakeFeature()
    }

    @Then("I click on Next Button for Sabbath Bake")
    fun iClickOnNextButtonForSabbathBake() {
        sabbathAppearanceTest?.iClickOnNextButtonForSabbathBake()
    }

    @Then("I see the cavity selection screen for Sabbath")
    fun iSeeTheCavitySelectionScreenForSabbath() {
        sabbathAppearanceTest?.iSeeTheCavitySelectionScreenForSabbath()
    }

    @Then("I select the {string} for Sabbath")
    fun iSelectTheForSabbath(cavity: String) {
        sabbathAppearanceTest?.iSelectTheForSabbath(cavity)
    }

    @Then("I see the horizontal tumbler screen for Sabbath Bake")
    fun iSeeTheHorizontalTumblerScreenForSabbathBake() {
        sabbathAppearanceTest?.iSeeTheHorizontalTumblerScreenForSabbathBake()
    }

    @Then("I scroll to select the temperature for Sabbath bake")
    fun iScrollSelectTheTemperatureForSabbathBake() {
        sabbathAppearanceTest?.iScrollSelectTheTemperatureForSabbathBake()
    }

    @Then("I see cooktime numpad for Sabbath")
    fun iSeeCooktimeNumpadForSabbath() {
        sabbathAppearanceTest?.iSeeCooktimeNumpadForSabbath()
    }

    @Then("I click the back button on cooktime numpad")
    fun iClickBackButtonOnCooktimeNumpad() {
        sabbathAppearanceTest?.iClickBackButtonOnCooktimeNumpad()
    }

    @Then("I set the CookTime to {string} on numpad")
    fun iSetTheCookTimeToOnNumpad(cookTime: String) {
        val cookTimeSec = TestingUtils.convertTimeToHoursAndMinutes(
            cookTime.toInt().toLong() * 60)
        sabbathAppearanceTest?.iSetTheCookTimeToOnNumpad(cookTimeSec)

    }


    @Then("I click on Cancel button")
    fun iClickCancelButton(){
        sabbathAppearanceTest?.iClickCancelButton()
    }

    @Then("I click on Set Untimed button")
    fun iClickOnSetUntimedButton() {
        sabbathAppearanceTest?.iClickOnSetUntimedButton()
    }

    @Then("I see Set Timed Button is enabled and clickable")
    fun iSeeSetTimedButtonIsEnabledAndClickable() {
        sabbathAppearanceTest?.iSeeSetTimedButtonIsVisible()
    }

    @Then("I click on Set Timed Button")
    fun iClickOnSetTimedButton() {
        sabbathAppearanceTest?.iClickOnSetTimedButton()
    }

    @Then("I see both cavity status screen for Sabbath Bake with time if set timed")
    fun iSeeBothCavityStatusScreenForSabbathBake() {

        sabbathAppearanceTest?.iSeeBothCavityStatusScreenForSabbathBake()
    }

    @Then("I click on Start Button for Sabbath Bake")
    fun iClickOnStartButtonForSabbathBake() {
        sabbathAppearanceTest?.iClickOnStartButtonForSabbathBake()
    }

    @Then("I see status screen running with recipe name and selected temperature")
    fun iSeeStatusScreeRunningWithRecipeNameAndSelectedTemperature() {
        sabbathAppearanceTest?.iSeeStatusScreeRunningWithRecipeNameAndSelectedTemperature()
    }

    @Then("I click on numpad icon button")
    fun iClickOnNumpadIconButton() {
        sabbathAppearanceTest?.iClickOnNumpadIconButton()
    }

    @Then("I see temperature numpad")
    fun iSeeTemperatureNumpad() {
        sabbathAppearanceTest?.iSeeTemperatureNumpad()
    }

    @Then("I set the temperature {string}")
    fun iSetTheTemperature(temp: String) {
        sabbathAppearanceTest?.iSetTheTemperature(temp)
    }

    @Then("I click on Next Button on the numpad screen")
    fun iClickOnNextBtnOnNumpadScreen(){
        sabbathAppearanceTest?.iClickOnNextBtnOnNumpadScreen()
    }

    @Then("I click on the tumbler icon")
    fun iClickOnTheTumblerIcon() {
        sabbathAppearanceTest?.iClickOnTheTumblerIcon()
    }


    @Then("I see Set Timed Button is not clickable")
    fun iSeeSetTimedButtonIsNotClickable() {
        sabbathAppearanceTest?.iSeeSetTimedButtonIsNotClickable()
    }

    @Then("I scroll the vertical tumbler to required duration")
    fun iScrollTheVerticalTumblerToRequiredDuration() {
        sabbathAppearanceTest?.iScrollTheVerticalTumblerToRequiredDuration()
    }


    @Then("I click the Next Button on tumbler screen")
    fun iClickTheNextButtonOnTumblerScreen() {
        sabbathAppearanceTest?.iClickTheNextButtonOnTumblerScreen()
    }

    @Then("I see vertical tumbler for Sabbath")
    fun iSeeVerticalTumblerForSabbath(){
        sabbathAppearanceTest?.iSeeVerticalTumblerForSabbath()
    }

    @Then("I see Set Timed Button is not clickable on Vertical tumbler screen")
    fun iSeeSetTimedButtonIsNotClickableOnVerticalTumblerScreen() {
        sabbathAppearanceTest?.iSeeSetTimedButtonIsNotClickableOnVerticalTumblerScreen()
    }

    @Then("I see Set Timed Button is enabled and clickable on Vertical tumbler screen")
    fun iSeeSetTimedButtonIsEnabledAndClickableOnVerticalTumblerScreen() {
        sabbathAppearanceTest?.iSeeSetTimedButtonIsEnabledAndClickableOnVerticalTumblerScreen()
    }

    @Then("I click on Set Untimed button on Vertical tumbler screen")
    fun iClickOnSetUntimedButtonOnVerticalTumblerScreen() {
        sabbathAppearanceTest?.iClickOnSetUntimedButtonOnVerticalTumblerScreen()
    }

    @Then("I click on Set Timed Button on Vertical tumbler screen")
    fun iClickOnSetTimedButtonOnVerticalTumblerScreen() {
        sabbathAppearanceTest?.iClickOnSetTimedButtonOnVerticalTumblerScreen()
    }

    @Then("I validate the header text of Sabbath Bake screen")
    fun iValidateTheHeaderTextOfSabbathBakeScreen() {
        sabbathAppearanceTest?.iValidateTheHeaderTextOfSabbathBakeScreen()
    }

    @Then("I check the Temperature tumbler subtitle text")
    fun iCheckTheTemperatureTumblerSubtitleText() {
        sabbathAppearanceTest?.iCheckTheTemperatureTumblerSubtitleText()
    }

    @Then("I check Temperature tumbler is scrolled to all the temperature")
    fun iCheckTemperatureTumblerIsScrolledToServings() {
        sabbathAppearanceTest?.iCheckTemperatureTumblerIsScrolledToServings()
    }

    @Then("I check the numpad icon")
    fun iCheckTheNumpadIcon() {
        sabbathAppearanceTest?.iCheckTheNumpadIcon()
    }

    @Then("I check the Next button of the tumbler screen")
    fun iCheckTheNextButtonOfTheTumblerScreen() {
        sabbathAppearanceTest?.iCheckTheNextButtonOfTheTumblerScreen()
    }

    @Then("I check the Next button text of the tumbler screen")
    fun iCheckTheNextButtonTextOfTheTumblerScreen() {
        sabbathAppearanceTest?.iCheckTheNextButtonTextOfTheTumblerScreen()
    }

    @Then("I check the Upper Oven text view")
    fun iCheckTheUpperOvenTextView() {
        sabbathAppearanceTest?.iCheckTheUpperOvenTextView()
    }

    @Then("I check the Upper Oven icon visibility")
    fun iCheckTheUpperOvenIconVisibility() {
        sabbathAppearanceTest?.iCheckTheUpperOvenIconVisibility()
    }

    @Then("I check the Upper Oven text view layout")
    fun iCheckTheUpperOvenTextViewLayout() {
        sabbathAppearanceTest?.iCheckTheUpperOvenTextViewLayout()
    }

    @Then("I check the cavity selection screen background")
    fun iCheckTheCavitySelectionScreenBackground() {
        sabbathAppearanceTest?.iCheckTheCavitySelectionScreenBackground()
    }

    @Then("I check the Lower Oven text view")
    fun iCheckTheLowerOvenTextView() {
        sabbathAppearanceTest?.iCheckTheLowerOvenTextView()
    }

    @Then("I check the Lower Oven icon visibility")
    fun iCheckTheLowerOvenIconVisibility() {
        sabbathAppearanceTest?.iCheckTheLowerOvenIconVisibility()
    }

    @Then("I check the Lower Oven text view layout")
    fun iCheckTheLowerOvenTextViewLayout() {
        sabbathAppearanceTest?.iCheckTheLowerOvenTextViewLayout()
    }

    @Then("I check the keyboard view header")
    fun iCheckTheKeyboardViewHeader() {
        sabbathAppearanceTest?.iCheckTheKeyboardViewHeader()
    }

    @Then("I check the Back button on Cooktime numpad screen")
    fun iCheckTheBackButtonOnCooktimeNumpadScreen() {
        sabbathAppearanceTest?.iCheckTheBackButtonOnCooktimeNumpadScreen()
    }

    @Then("I check the tumbler icon")
    fun iCheckTheTumblerIcon() {
        sabbathAppearanceTest?.iCheckTheTumblerIcon()
    }

    @Then("I check the cancel button")
    fun iCheckTheCancelButton() {
        sabbathAppearanceTest?.iCheckTheCancelButton()
    }

    @Then("I check the Set timed button")
    fun iCheckTheSetTimedButton() {
        sabbathAppearanceTest?.iCheckTheSetTimedButton()
    }

    @Then("I check the Set Untimed button")
    fun iCheckTheSetUntimedButton() {
        sabbathAppearanceTest?.iCheckTheSetUntimedButton()
    }

    @Then("I check the header text of Cavity status screen for Set Untimed")
    fun iCheckTheHeaderTextOfCavityStatusScreenForSetUntimed() {
        sabbathAppearanceTest?.iCheckTheHeaderTextOfCavityStatusScreenForSetUntimed()
    }

    @Then("I check the Back button for Cavity status screen for Set Untimed")
    fun iCheckTheBackButtonForCavityStatusScreenForSetUntimed() {
        sabbathAppearanceTest?.iCheckTheBackButtonForCavityStatusScreenForSetUntimed()
    }

    @Then("I check the Start button text for Cavity status screen for Set Untimed")
    fun iCheckTheStartButtonTextForCavityStatusScreenForSetUntimed() {
        sabbathAppearanceTest?.iCheckTheStartButtonTextForCavityStatusScreenForSetUntimed()
    }

    @Then("I check the Start button icon for Cavity status screen for Set Untimed")
    fun iCheckTheStartButtonIconForCavityStatusScreenForSetUntimed() {
        sabbathAppearanceTest?.iCheckTheStartButtonIconForCavityStatusScreenForSetUntimed()
    }

    @Then("I check Upper Oven status text")
    fun iCheckUpperOvenStatusText() {
        sabbathAppearanceTest?.iCheckUpperOvenStatusText()
    }

    @Then("I check the Upper Oven icon on Cavity status screen")
    fun iCheckTheUpperOvenIconOnCavityStatusScreen() {
        sabbathAppearanceTest?.iCheckTheUpperOvenIconOnCavityStatusScreen()
    }

    @Then("I check Lower Oven status text")
    fun iCheckLowerOvenStatusText() {
        sabbathAppearanceTest?.iCheckLowerOvenStatusText()
    }

    @Then("I check the Lower Oven icon on Cavity status screen")
    fun iCheckTheLowerOvenIconOnCavityStatusScreen() {
        sabbathAppearanceTest?.iCheckTheLowerOvenIconOnCavityStatusScreen()
    }

    @Then("I check the Upper Oven status layout")
    fun iCheckTheUpperOvenStatusLayout() {
        sabbathAppearanceTest?.iCheckTheUpperOvenStatusLayout()
    }

    @Then("I check the Lower Oven status layout")
    fun iCheckTheLowerOvenStatusLayout() {
        sabbathAppearanceTest?.iCheckTheLowerOvenStatusLayout()
    }

    @Then("I check status bar")
    fun iCheckStatusBar() {
        sabbathAppearanceTest?.iCheckStatusBar()
    }

    @Then("I check the recipe name text view")
    fun iCheckTheRecipeNameTextView() {
        sabbathAppearanceTest?.iCheckTheRecipeNameTextView()
    }

    @Then("I check the remaining cook time is displayed if set timed")
    fun iCheckTheRemainingCookTimeIsDisplayedIfSetTimed() {
        sabbathAppearanceTest?.iCheckTheRemainingCookTimeIsDisplayedIfSetTimed()
    }

    @Then("I check temperature Up button")
    fun iCheckTemperatureUpButton() {
        sabbathAppearanceTest?.iCheckTemperatureUpButton()
    }

    @Then("I check temperature down button")
    fun iCheckTemperatureDownButton() {
        sabbathAppearanceTest?.iCheckTemperatureDownButton()
    }

    @Then("I check temperature Up button text")
    fun iCheckTemperatureUpButtonText() {
        sabbathAppearanceTest?.iCheckTemperatureUpButtonText()
    }

    @Then("I check temperature down button text")
    fun iCheckTemperatureDownButtonText() {
        sabbathAppearanceTest?.iCheckTemperatureDownButtonText()
    }

    @Then("I click on Upper Oven")
    fun iClickOnUpperOven() {
        sabbathAppearanceTest?.iClickOnUpperOven()
    }

    @Then("I click on Lower Oven to set it")
    fun iClickOnLowerOvenToSetIt() {
        sabbathAppearanceTest?.iClickOnLowerOvenToSetIt()
    }

    @Then("I see status screen running with recipe name and selected temperature for both cavity")
    fun iSeeStatusScreenRunningWithRecipeNameAndSelectedTemperatureForBothCavity() {
        sabbathAppearanceTest?.iSeeStatusScreenRunningWithRecipeNameAndSelectedTemperatureForBothCavity()
    }

    @Then("I check the text view of both cavity")
    fun iCheckTextViewOfBothCavity() {
        sabbathAppearanceTest?.iCheckTextViewOfBothCavity()
    }
}