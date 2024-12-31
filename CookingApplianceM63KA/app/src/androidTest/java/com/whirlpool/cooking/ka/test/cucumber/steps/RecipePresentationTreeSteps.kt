package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.appearance.RecipePresentationTreeAppearanceTest
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.HMIKeyUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.funWithGridViewScrollToTargetTextAndClick
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.convertStringToType
import com.whirlpool.hmi.uitesting.UiTestingUtils

import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.Then
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecipePresentationTreeSteps {
    private var recipePresentationTreeAppearanceTest: RecipePresentationTreeAppearanceTest? = null
    private var hmiKeyUtils: HMIKeyUtils? = null

    @Before
    fun setUp() {
        recipePresentationTreeAppearanceTest = RecipePresentationTreeAppearanceTest()
        hmiKeyUtils = HMIKeyUtils
    }

    @After
    fun tearDown() {
        recipePresentationTreeAppearanceTest = null
    }

    @Then("Check manual recipe names {string} {string}.")
    fun checkLinearRecycler(position: String, recipe: String) {
        recipePresentationTreeAppearanceTest?.checkLinearRecycler(position, recipe)
    }

    @Then("Check Convect menu recipes {string} {string}.")
    fun checkConvectRecipeNames(position: String, recipe: String) {
        recipePresentationTreeAppearanceTest?.checkLinearRecycler("4", "Convect")
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }

    @Then("Check Convect menu recipes for mw {string} {string}.")
    fun checkConvectRecipeNamesMW(position: String, recipe: String) {
        recipePresentationTreeAppearanceTest?.checkLinearRecycler("4", "Convect")
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }

    @Then("Check More Modes menu recipes for mw {string} {string}.")
    fun checkMoreModesRecipeNamesMW(position: String, recipe: String) {
        recipePresentationTreeAppearanceTest?.checkLinearRecycler("7", "More Modes")
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }

    @Then("Check More Modes menu recipes {string} {string}.")
    fun checkMoreModesRecipeNames(position: String, recipe: String) {
        recipePresentationTreeAppearanceTest?.checkLinearRecycler("7", "More Modes")
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }

    @Then("Check Probe menu recipes in More Modes menu {string} {string}.")
    fun checkProbeInMoreModesRecipeNames(position: String, recipe: String) {
        recipePresentationTreeAppearanceTest?.checkLinearRecycler("7", "More Modes")
        funWithGridViewScrollToTargetTextAndClick("Probe")
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }

    @Then("Check Assisted Mode menu names {string} {string}.")
    fun checkAssistedModeNames(position: String, recipe: String) {
        recipePresentationTreeAppearanceTest?.checkLinearRecycler("0", "Assisted Cooking")
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }

    @Then("Check Assisted Mode menu recipes when probe is connected {string} {string}.")
    fun checkAssistedModeNamesifProbeConnected(position: String, recipe: String) {
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }

    @Then("Check Auto cook menus when probe is connected {string} {string}.")
    fun checkAutoModeMenuNamesifProbeConnected(position: String, recipe: String) {
        funWithGridViewScrollToTargetTextAndClick("Auto Cook")
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }

    @Then("Check Auto cook menu recipes when probe connected {string} {string} {string}.")
    fun checkAutoModeRecipeNamesifProbeConnected(position: String, recipe: String, menu: String) {
        funWithGridViewScrollToTargetTextAndClick("Auto Cook")
        UiTestingUtils.performClickOnRecyclerViewIndex(
            R.id.recycler_view_grid_list,
            (convertStringToType(menu, Int::class.java))
        )
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }

    @Then("Check Assisted Mode menu recipes {string} {string} {string}.")
    fun checkAssistedModeRecipeNames(position: String, recipe: String, menu: String) {
        recipePresentationTreeAppearanceTest?.checkLinearRecycler("0", "Assisted Cooking")
        UiTestingUtils.performClickOnRecyclerViewIndex(
            R.id.recycler_view_grid_list,
            (convertStringToType(menu, Int::class.java))
        )
        recipePresentationTreeAppearanceTest?.checkGridRecycler(position, recipe)
    }
}