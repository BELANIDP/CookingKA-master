package com.whirlpool.cooking.ka.test.cucumber.steps

import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.whirlpool.cooking.appearance.QuickToggleSettingGridListAppearanceTest
import com.whirlpool.cooking.ka.R
import android.presenter.fragments.mwo.ClockFragment
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.convertStringToType
import com.whirlpool.hmi.uitesting.UiTestingUtils
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import io.cucumber.java.en.Then
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuickToggleSettingGridListSteps {

    private var quickToggleSettingGridListAppearanceTest: QuickToggleSettingGridListAppearanceTest? = null
//    private var quickToggleSettingGridListFragmentScenario: FragmentScenario<GridViewWidgetTestFragment>? =
//        null
    private var clockFragmentFragmentScenario: FragmentScenario<ClockFragment>? = null

    @Before
    fun setUp() {
        quickToggleSettingGridListAppearanceTest = QuickToggleSettingGridListAppearanceTest()
    }

    @After
    fun tearDown() {
        quickToggleSettingGridListAppearanceTest = null
    }


    @Then("Quick Toggle Setting Grid List View Screen will be visible")
    fun quickToggleSettingGridListVisible() {
        quickToggleSettingGridListAppearanceTest?.recyclerViewGridListIsVisible()
    }

    @And("I navigate to clock screen for quick toggle settings list view screen")
    fun navigateToClockforQuickToggleSettingListView(){
        clockFragmentFragmentScenario =
            FragmentScenario.launchInContainer(
                ClockFragment::class.java,
                themeResId = R.style.Theme_CookingApplianceM63KA
            )
    }

    @And("I navigate to Quick Toggle Setting Grid List View")
    fun navigateToQuickToggleSettingListView() {
//        quickToggleSettingGridListFragmentScenario = FragmentScenario.launchInContainer(
//            GridViewWidgetTestFragment::class.java,
//            themeResId = R.style.Theme_CookingApplianceM63KA
//        )
    }

    @Then("Check Quick Toggle Setting Grid List View Screen is enable")
    fun checkquickToggleSettingGridListEnable() {
        quickToggleSettingGridListAppearanceTest?.recyclerViewGridListIsViewEnabled()
    }

    @Then("I verify Scroll Quick Toggle Setting Grid list to specific elements {string}")
    fun scrollQuickToggleGridListToSpecificElement(position : String) {
        quickToggleSettingGridListAppearanceTest?.scrollQuickToggleGridListToSpecificElement(convertStringToType(position, Int::class.java))
    }

    @Then("Verify click on toggle button {string}")
    fun checkIfToggleButtonIsClickable(position: String) {
    (Espresso.onView(UiTestingUtils.matchRecyclerViewItem(R.id.recycler_view_grid_list, (convertStringToType(position, Int::class.java)), R.id.settings_item_toggle_switch))).perform(click())
    }

    @Then("Check Title text and All properties of Title of Quick Toggle Setting Grid {string} , {string} , {string} , {string} , {string} , {string} , {string} , {string} , {string} , {string} , {string}")
    fun verifyTextAndProperties(position: String, visible: String, text: String,  width: String, height: String, fontFamily: String, weight: String, size: String, lineHeight: String, gravity: String, color:String) {
        quickToggleSettingGridListAppearanceTest?.verifyTextAndProperties(
            position,
            visible,
            text,
            width,
            height,
            fontFamily,
            weight,
            size,
            lineHeight,
            gravity,
            color
        )
    }

    @Then("Check toggle Button's width, height ,ON_OFF and enabled_disabled {string} {string} {string} {string} {string} {string}")
    fun verifyToggleButtonsProperties(position: String, visible: String, width: String, height: String, on_off: String, enable_disable: String){
        quickToggleSettingGridListAppearanceTest?.verifyToggleButtonsProperties(
            position,
            visible,
            width,
            height,
            on_off,
            enable_disable
        )
    }
}