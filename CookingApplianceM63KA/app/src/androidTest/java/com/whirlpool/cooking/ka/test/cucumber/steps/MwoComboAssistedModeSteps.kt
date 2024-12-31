
package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.test.cucumber.appearance.MwoComboAssistedModeAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith


/*
 * File : com.whirlpool.cooking.ka.test.cucumber.steps.MwoComboAssistedModeSteps
 * Author : DUNGAS
 * Created On : 5/28/24, 1:12 PM
 * Details :
 */
@RunWith(AndroidJUnit4::class)
class MwoComboAssistedModeSteps {
    private var mwoComboAssistedModeAppearanceTest: MwoComboAssistedModeAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        mwoComboAssistedModeAppearanceTest = MwoComboAssistedModeAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }
    @After
    fun tearDown() {
        mwoComboAssistedModeAppearanceTest = null
    }
    @When("I scroll to Assisted Cooking Option")
    fun scrollToAssistedCookingOpt() {
        mwoComboAssistedModeAppearanceTest?.selectAssistedCookingOpt()
    }

    @When("I see recipe grid screen")
    fun recipeGridViewIsVisible() {
        mwoComboAssistedModeAppearanceTest?.isRecipeGridVisible()
    }

    @When("I validate title text of Food Type Selection grid")
    fun recipeGridViewTitleTextValidation() {
        mwoComboAssistedModeAppearanceTest?.recipeSelectionGridTitleTextValidation()
    }

    @When("I validate title text size of Food Type Selection grid")
    fun recipeGridViewTitleTextSizeValidation() {
        mwoComboAssistedModeAppearanceTest?.recipeSelectionGridTitleTextSizeValidation()
    }

    @When("I validate title text color of Food Type Selection grid")
    fun recipeGridViewTitleTextColorValidation() {
        mwoComboAssistedModeAppearanceTest?.recipeSelectionGridTitleTextColorValidation()
    }

    @When("I click on back button on Food Type Selection screen grid")
    fun clickOnBackButtonOnRecipeGridView() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnBackButtonOnRecipeGridView()
        LeakAssertions.assertNoLeaks()
    }

    @When("I click on {string} recipe name")
    fun clickOnRecipeName(index: String) {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnRecipeAtIndex(index.toInt())
        LeakAssertions.assertNoLeaks()
    }
    @When("I validate title text is same as {string}")
    fun validateRecipeName(recipeName: String) {
        mwoComboAssistedModeAppearanceTest?.recipeNameTitleTextValidation(recipeName)
    }

    @When("I click on  Frozen Chicken Nugget recipe")
    fun clickOnFrozenChickenNugget() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnChickenNugget()
        LeakAssertions.assertNoLeaks()
    }
    @When("I click on Biscuits recipe")
    fun clickOnBiscuits() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnChickenNugget()
        LeakAssertions.assertNoLeaks()
    }

    @When("I see No. of serving tumbler")
    fun noOfServingTumblerVisible() {
        mwoComboAssistedModeAppearanceTest?.isNoOfServingsTumblerVisible()
    }

    @When("I validate Serving tumbler title text")
    fun servingsTumblerTitleTextValidation() {
        mwoComboAssistedModeAppearanceTest?.servingsTumblerTitleTextValidation()
    }
    @When("I validate Serving tumbler subtitle text")
    fun servingsTumblerSubTitleTextValidation() {
        mwoComboAssistedModeAppearanceTest?.servingsTumblerSubTitleTextValidation()
    }

    @When("I validate Serving tumbler subtitle text size")
    fun servingsTumblerSubTitleTextSizeValidation() {
        mwoComboAssistedModeAppearanceTest?.servingsTumblerSubTitleTextSizeValidation()
    }

    @When("I validate Serving tumbler subtitle text color")
    fun servingsTumblerSubTitleTextColorValidation() {
        mwoComboAssistedModeAppearanceTest?.servingsTumblerSubTitleTextColorValidation()
    }

    @When("I validate Serving tumbler numpad icon is clickable")
    fun validateNumpadIconOnServingsTumbler() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnNumpadButtonOnServingsTumbler()
        LeakAssertions.assertNoLeaks()
    }

    @When("I validate Serving tumbler back icon is clickable")
    fun validateBackIconOnServingsTumbler() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnBackButtonOnServingsTumbler()
        LeakAssertions.assertNoLeaks()
    }

    @When("I validate Serving tumbler next button is visible")
    fun nextButtonVisibleOnServingsTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonVisibleOnServingsTumbler()
    }
    @When("I validate Serving tumbler next button is enabled")
    fun nextButtonEnabledOnServingsTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonEnabledOnServingsTumbler()
    }
    @When("I validate Serving tumbler next button is clickable")
    fun nextButtonClickableOnServingsTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonClickableOnServingsTumbler()
    }
    @When("I validate Serving tumbler is scrolled to 7 servings")
    fun validateServingTumblerIsScrollable() {
        mwoComboAssistedModeAppearanceTest?.validateServingTumblerIsScrollable()
    }
    @When("I see doneness tumbler")
    fun validateDonenessTumblerIsVisible() {
        mwoComboAssistedModeAppearanceTest?.isDonenessTumblerVisible()
    }

    @When("I validate title text of doneness tumbler")
    fun donenessTumblerTitleTextValidation() {
        mwoComboAssistedModeAppearanceTest?.donenessTumblerTitleTextValidation()
    }

    @When("I validate title text size of doneness tumbler")
    fun donenessTumblerTitleTextSizeValidation() {
        mwoComboAssistedModeAppearanceTest?.recipeSelectionGridTitleTextSizeValidation()
    }

    @When("I validate title text color of doneness tumbler")
    fun donenessTumblerTitleTextColorValidation() {
        mwoComboAssistedModeAppearanceTest?.recipeSelectionGridTitleTextColorValidation()
    }
    @When("I validate oven icon visible on doneness tumbler")
    fun ovenIconVisibleOnDonenessTumbler() {
        mwoComboAssistedModeAppearanceTest?.isOvenIconVisibleOnRecipeGridView()
    }
    @When("I click on back button on doneness tumbler")
    fun clickOnBackButtonOnDonenessTumbler() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnBackButtonOnRecipeGridView()
        LeakAssertions.assertNoLeaks()
    }

    @When("I validate Doneness tumbler next button is visible")
    fun nextButtonVisibleOnDonenessTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonVisibleOnServingsTumbler()
    }
    @When("I validate Doneness tumbler next button is enabled")
    fun nextButtonEnabledOnDonenessTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonEnabledOnServingsTumbler()
    }
    @When("I validate Doneness tumbler next button is clickable")
    fun nextButtonClickableOnDonenessTumbler() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonClickableOnServingsTumbler()
    }
    @When("I scroll doneness tumbler to Dark")
    fun performScrollOnDonenessTumbler() {
        mwoComboAssistedModeAppearanceTest?.validateDonenessTumblerIsScrollable()
    }
    @When("I see preview screen")
    fun previewScreenIsVisible() {
        mwoComboAssistedModeAppearanceTest?.isPreviewScreenVisible()
    }

    @When("I click on back button on preview screen")
    fun clickOnBackButtonOnPreviewScreen() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnBackButtonOnRecipeGridView()
        LeakAssertions.assertNoLeaks()
    }
    @When("I validate oven icon is visible on preview screen")
    fun ovenIconVisibleOnPreviewScreen() {
        mwoComboAssistedModeAppearanceTest?.isOvenIconVisibleOnRecipeGridView()
    }
    @When("I click on Amount section on preview screen")
    fun clickOnAmountOnPreviewScreen() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnAmountSectionOnPreviewScreen()
        LeakAssertions.assertNoLeaks()
    }
    @When("I click on doneness section on preview screen")
    fun clickOnDonenessOnPreviewScreen() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnDonenessSectionOnPreviewScreen()
        LeakAssertions.assertNoLeaks()
    }
    @When("I validate preview screen next button is visible")
    fun nextButtonVisibleOnPreviewScreen() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonVisibleOnServingsTumbler()
    }
    @When("I validate preview screen next button is enabled")
    fun nextButtonEnabledOnPreviewScreen() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonEnabledOnServingsTumbler()
    }
    @When("I validate preview screen next button is clickable")
    fun nextButtonClickableOnPreviewScreen() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonClickableOnServingsTumbler()
    }
    @When("I click on Next button on preview screen")
    fun clickOnNextButtonOnPreviewScreen() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnNextButtonOnPreviewScreen()
        LeakAssertions.assertNoLeaks()
    }
    @When("I validate cooking guide text is visible")
    fun isCookingGuideVisible() {
        mwoComboAssistedModeAppearanceTest?.isCookingGuideVisible()
    }
    @When("I validate cooking guide image is visible")
    fun isCookingGuideImageVisible() {
        mwoComboAssistedModeAppearanceTest?.isCookingGuideImageVisible()
    }
    @When("I validate cooking guide title text is visible")
    fun isCookingGuideTitleVisible() {
        mwoComboAssistedModeAppearanceTest?.isCookingGuideTitleVisible()
    }
    @When("I validate cooking guide title text is correct")
    fun isCookingGuideTitleTextValidation() {
        mwoComboAssistedModeAppearanceTest?.cookingGuideTitleTextValidation()
    }
    @When("I validate cooking guide title text size is correct")
    fun isCookingGuideTitleTextSizeValidation() {
        mwoComboAssistedModeAppearanceTest?.cookingGuideTitleTextSizeValidation()
    }
    @When("I validate cooking guide title text color is correct")
    fun isCookingGuideTitleTextColorValidation() {
        mwoComboAssistedModeAppearanceTest?.cookingGuideTitleTextColorValidation()
    }
    @When("I validate cooking guide close icon is visible")
    fun isCookingGuideCloseIconVisible() {
        mwoComboAssistedModeAppearanceTest?.isCookingGuideCloseIconVisible()
    }
    @When("I validate cooking guide next button is visible")
    fun nextButtonVisibleOnCookingGuide() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonVisibleOnServingsTumbler()
    }
    @When("I validate cooking guide next button is enabled")
    fun nextButtonEnabledOnCookingGuide() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonEnabledOnServingsTumbler()
    }
    @When("I validate cooking guide next button is clickable")
    fun nextButtonClickableOnCookingGuide() {
        mwoComboAssistedModeAppearanceTest?.isNextButtonClickableOnServingsTumbler()
    }
    @When("I click on Next button on cooking guide")
    fun nextButtonClickOnCookingGuide() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnNextButtonOnCookingGuide()
        LeakAssertions.assertNoLeaks()
    }
    @When("I click on Start button on cooking guide")
    fun startButtonClickOnCookingGuide() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnNextButtonOnCookingGuide()
        LeakAssertions.assertNoLeaks()
    }
    @When("I see status screen for assisted cooking")
    fun statusScreenVisibleForAssistedCombo() {
        mwoComboAssistedModeAppearanceTest?.statusScreenVisibleForAssistedCombo()
    }
    @When("I perform click on lower oven btn")
    fun clickOnLowerOvenBtn() {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.performClickOnLowerOvenBtn()
        LeakAssertions.assertNoLeaks()
    }

    @Then("I scroll to targetText {string} and click")
    fun tumbler_screen_scroll_to_targetText_and_click(targetText:String) {
        LeakAssertions.assertNoLeaks()
        mwoComboAssistedModeAppearanceTest?.scrollToTextAndClick(targetText)
        LeakAssertions.assertNoLeaks()
    }

}
