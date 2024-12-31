package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.app.ActionBar
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests.Companion.context
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.checkTextColor
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.dpToPx
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.matchesBackgroundColor
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withDrawable
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withFontFamily
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withGravity
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndex
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndexHash
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withTextColor
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withTextSize
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkText
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.convertStringToType
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.textview.GenericTextViewTest
import com.whirlpool.hmi.uitesting.components.view.GenericViewTest
import org.hamcrest.Matcher

class ToolsAppearanceTest {

    private var toggleFragmentAppearanceTest: ToggleFragmentAppearanceTest?=null

    fun iCheckHeaderOfSettingsMenu(){
        //headerBar
        UiTestingUtils.isViewVisible(R.id.headerBarSettings)

        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.headerBarSettings)),
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT)


        Espresso.onView(withId(R.id.headerBarSettings))
            .check(matches(matchesBackgroundColor(R.color.common_solid_black)))

        //backArrow validation
        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.ic_back_arrow)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivLeftIcon)),
            leftIcon
        )

        //title text validation
        UiTestingUtils.isTextMatching(R.id.tvTitle, "Settings")

        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )

        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")

        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )
    }

    fun iCheckGridViewOfSettingsMenu(){
        val desiredHeightSp = 154f
        val desiredWidthSp = 854f
        val desiredHeightInpx = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredHeightSp)
        val desiredWidthInpx = TestingUtils.dpToPx(CookingKACucumberTests.context, desiredWidthSp)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.settingsRecyclerGridList)),
            desiredWidthInpx, desiredHeightInpx)

        Espresso.onView(withId(R.id.settingsRecyclerGridList))
            .check(matches(matchesBackgroundColor(R.color.common_solid_black)))

        onView(withIndex(withId(R.id.grid_parent_view), 0)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.grid_parent_view), 1)).check(matches(isDisplayed()))
        onView(withIndex(withId(R.id.grid_parent_view), 2)).check(matches(isDisplayed()))
    }

    fun iCheckListViewOfSettingsMenu(){
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.settingsRecyclerList)),
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT)

        Espresso.onView(withId(R.id.settingsRecyclerList))
            .check(matches(matchesBackgroundColor(R.color.settingMenuListBackground)))

        iCheckListViewItemsOfSettingsMenu()
    }

    fun iCheckListViewItemsOfSettingsMenu(){
        TestingUtils.checkPropertiesOfText(R.id.llListHeading,0,"Preferences", 40f,R.color.timeColor)
        TestingUtils.checkPropertiesOfText(R.id.llListHeading,4,"Network Settings", 40f, R.color.timeColor)
        TestingUtils.checkPropertiesOfText(R.id.llListHeading,7,"More Modes", 40f, R.color.timeColor)
        TestingUtils.checkPropertiesOfText(R.id.llListHeading,11,"Info",40f, R.color.timeColor)

        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Knob Settings",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Time and Date",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,3,"Show More",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,5,"Connect to Network",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,6,"Show More",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,8,"Sabbath",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,9,"Self Clean",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,10,"Steam Clean",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,12,"Demo Mode",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,13,"Service and Support",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,14,"Show More",36f,R.color.solid_white)

        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,1,40,40,R.drawable.ic_knob_settings)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,2,40,40,R.drawable.ic_timedate)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,3,40,40,R.drawable.ic_showmore)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,5,40,40,R.drawable.ic_network)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,6,40,40,R.drawable.ic_showmore)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,8,40,40,R.drawable.ic_sabbath)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,9,40,40,R.drawable.ic_40px_self_clean)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,10,40,40,R.drawable.ic_steam_clean)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,12,40,40,R.drawable.demomode)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,13,40,40,R.drawable.ic_servicesupporticon)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,14,40,40,R.drawable.ic_showmore)
    }

    fun iCheckListViewItemsOfPreferencesMenu(){
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Knob Settings",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Time and Date",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Sound Volume",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,3,"Display and Brightness",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,4,"Oven Light",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,5,"Regional Settings",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,6,"Temperature Calibration",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,7,"Restore Settings",36f,R.color.solid_white)

        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,0,40,40,R.drawable.ic_knob_settings)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,1,40,40,R.drawable.ic_timedate)
//        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,2,40,40,R.drawable.ic_40px_sound_active)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,3,40,40,R.drawable.ic_displaysettings)
//        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,4,40,40,R.drawable.ic_light_oven_off)
//        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,5,40,40,R.drawable.ic_regionalsettings)
//        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,6,40,40,R.drawable.ic_tempcalibration)
//        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,7,40,40,R.drawable.ic_restoresettings)
    }

    fun iCheckListViewItemsOfNetworkSettingsMenu(){
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Wi-Fi",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Connect to Network",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Remote Enable",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,3,"SAID Code",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,4,"MAC Address",36f,R.color.solid_white)

//        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,0,40,40,R.drawable.icon_connect_network)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,1,40,40,R.drawable.ic_network)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,2,40,40,R.drawable.ic_remote_enable_settings)

        Espresso.onView(withIndex(withId(R.id.toggle_switch), 0)).check(matches(isDisplayed()))
        Espresso.onView(withIndex(withId(R.id.toggle_switch), 2)).check(matches(isDisplayed()))
    }

    fun performClickOnBack(){
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun performClickShowMoreInfo() {
        UiTestingUtils.sleep(1000)
        val index = 14
        Espresso.onView(ViewMatchers.withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun RecyclerViewHeaderValidateView(title: String){
        //title text validation
        UiTestingUtils.isTextMatching(R.id.tvTitle, title)

        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvTitle)), sizeInPixels.toFloat()
        )

        TestingUtils.checkTextColorValidation(R.id.tvTitle, "#ffffff")

        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.tvTitle)),
            Gravity.CENTER
        )

        //backArrow validation
        val leftIcon = AppCompatResources.getDrawable(context, R.drawable.ic_back_arrow)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivLeftIcon)),
            leftIcon
        )

        //closeIcon validation
        val rightIcon = AppCompatResources.getDrawable(context, R.drawable.ic_close)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivRightIcon)),
            rightIcon
        )
    }

    fun preferenceRecyclerViewItemsValidateView(){
        iCheckListViewItemsOfPreferencesMenu()
    }

    fun infoRecyclerlistIsVisible() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.infoRecyclerList)
    }

    fun infoRecyclerlistValidateView() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewClickable(R.id.infoRecyclerList)
        UiTestingUtils.isViewEnabled(R.id.infoRecyclerList)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.infoRecyclerList)),
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        Espresso.onView(withId(R.id.infoRecyclerList))
            .check(matches(matchesBackgroundColor(R.color.color_black)))
    }

    fun iCheckListViewItemsOfInfoMenu(){
//        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Demo Mode",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Service and Support",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Software Terms and Conditions",36f,R.color.solid_white)

        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,1,40,40,R.drawable.ic_servicesupporticon)
        TestingUtils.checkPropertiesOfImage(R.id.list_item_icon_image_view,2,40,40,R.drawable.ic_software_term_and_condition)
    }

    fun performClickOnKnobSettings(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnSwapKnobFunctions(){
        UiTestingUtils.sleep(1000)
        val index = 1
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnSwap(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.btnPrimarySwap)
    }

    fun performClickOnBackButtonOfSwap(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun performClickOnKnobFunctionsInfo(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performScrollAndClick() {
        val viewAction = object : ViewAction {
            override fun getDescription(): String {
                return ""
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(View::class.java)
            }

            override fun perform(uiController: UiController?, view: View?) {
                if (view != null) {
                    val scrollview  =  view as ScrollView
                    scrollview.fullScroll(ScrollView.FOCUS_DOWN)
                    UiTestingUtils.sleep(1000)
                }
            }
        }
        onView(withId(R.id.scroll_view_popup_info_text)).perform(viewAction)
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.btnPrimary)
    }

    fun performClickOnKnobLightToggleButton(){
        UiTestingUtils.sleep(1000)
        val index = 3
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.toggle_switch)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnAssignFavorite(){
        UiTestingUtils.sleep(1000)
        val index = 2
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnInfoIconOfAssignFavorite(){
        UiTestingUtils.performClick(R.id.ivInfo)
    }

    fun performClickOnOkayInAssignFavoriteInfoPopup(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_right)
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnStartAFavoriteCycleOption(){
        UiTestingUtils.sleep(1000)
        val index = 1
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnBackArrowOfFavoritesScreen(){
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun performClickOnAssignFavoriteCloseIcon(){
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }

    fun performClickOnBake(){
        TestingUtils.withRecyclerViewScrollToTargetTextAndClick(R.id.tumblerString, "Bake")
    }

    fun performClickOnNextButtonOfBake(){
        UiTestingUtils.performClick(R.id.text_button_right)
    }

    fun performClickOnStartButtonOfBake(){
        UiTestingUtils.performClick(R.id.btnPrimary)
    }

    fun performClickOnThreeDotsOfBake(){
        UiTestingUtils.performClick(R.id.ivOvenCavityMoreMenu)
    }

    fun performClickOnSaveAsFavorite(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClickOnRecyclerViewIndex(R.id.moreCycleOptionsRecycler,0)
    }

    fun performClickOnTurnOffButtonOfBake(){
        UiTestingUtils.sleep(3000)
        UiTestingUtils.performClick(R.id.tvOvenStateAction)
        UiTestingUtils.sleep(1500)
        UiTestingUtils.performClick(R.id.tvOvenStateAction)
        UiTestingUtils.sleep(1500)
    }

    fun performClickOnBakeInFavorites(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.languageRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnBackArrowOfAssignFavorite(){
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun performClickOnBackArrowOfKnobSettingsScreen(){
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun performClickOnCloseIconOfKnobSettingsScreen(){
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }

    fun performClickOnMute() {
        UiTestingUtils.sleep(1000)
        onView(withIndex(withId(R.id.grid_parent_view), 1)).perform(ViewActions.click())
    }

    fun performClickOnTimeAndDate(){
        UiTestingUtils.sleep(1000)
        val index = 1
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnSetTime(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.recyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnSetOfVerticalTumbler(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.startNowText)
    }

    fun performClickOnNumpadIconOfVerticalTumbler(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }

    fun performClickOn24H(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.right_format_selection)
    }

    fun performClickOn12H(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.left_format_selection)
    }

    fun performClickOnBackIcon(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivCancelIcon)
    }

    fun performClickOnToggleIcon(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivTumblerIcon)
    }

    fun performClickOnSetButtonOfNumpad(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_right)
    }

    fun performClickOnBackArrowOfTimeAndDateMenuScreen(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun performClickOnNumpadIconOf24HVerticalTumbler(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }

    fun performClickOnSoundVolume(){
        UiTestingUtils.sleep(1000)
        val index = 2
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnAlertsAndTimers(){
        UiTestingUtils.sleep(1000)
        val index = 1
        Espresso.onView(ViewMatchers.withId(R.id.recyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnBackArrowOfAlertsAndTimers(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun performClickOnBackArrowOfLanguage(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivLeftIcon)
    }

    fun performClickOnButtonsAndEffects(){
        UiTestingUtils.sleep(1000)
        val index = 2
        Espresso.onView(ViewMatchers.withId(R.id.recyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnCloseIconOfSoundVolume(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivRightIcon)
    }

    fun performClickOnMuteToggleButton(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.recyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.toggle_switch)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnRegionalSettings(){
        UiTestingUtils.sleep(1000)
        val index = 5
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnLanguage(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.regionalSettingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnSpanish(){
        UiTestingUtils.sleep(1000)
        val index = 2
        Espresso.onView(ViewMatchers.withId(R.id.languageRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnEnglish(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.languageRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnDegreeCelsius(){
        UiTestingUtils.sleep(1000)
        onView(withIndex(withId(R.id.toggle_switch_off), 1)).perform(ViewActions.click())
    }

    fun performClickOnGrams(){
        UiTestingUtils.sleep(1000)
        onView(withIndex(withId(R.id.toggle_switch_off), 2)).perform(ViewActions.click())
    }

    fun performClickOn24Hrs(){
        UiTestingUtils.sleep(1000)
        onView(withIndex(withId(R.id.toggle_switch_off), 2)).perform(ViewActions.click())
    }

    fun performClickOnDDMM(){
        UiTestingUtils.sleep(1000)
        onView(withIndex(withId(R.id.toggle_switch_off), 3)).perform(ViewActions.click())
    }

    fun performClickOnDisplayAndBrightness(){
        UiTestingUtils.sleep(1000)
        val index = 3
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnDisplayBrightness(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.recyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnTemperatureCalibration(){
        UiTestingUtils.sleep(1000)
        val index = 6
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnSetButtonOfTempCalibration(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.btnPrimary)
    }

    fun performClickOnRestoreSettings(){
        UiTestingUtils.sleep(1000)
        val index = 7
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnRestoreFactoryDefaults(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.recyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }
     fun performClickOnCancelOfRestoreFactoryDefaults(){
         UiTestingUtils.sleep(1000)
         UiTestingUtils.performClick(R.id.primary_reset_button)
     }

    fun performClickOnProceedOfRestoreFactoryDefaults(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.secondary_reset_button)
    }

    fun performClickOnCancelOfRestoreFactoryDefaultsPopup(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_left)
    }

    fun performClickOnProceedOfRestoreFactoryDefaultsPopup(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_right)
    }

    fun performClickOnCavityLight(){
        UiTestingUtils.sleep(1000)
        val index = 4
        Espresso.onView(ViewMatchers.withId(R.id.preferencesRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnManuallyControlLights(){
        UiTestingUtils.sleep(1000)
        val index = 1
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickShowMoreNetwork() {
        UiTestingUtils.sleep(1000)
        val index = 6
        Espresso.onView(ViewMatchers.withId(R.id.settingsRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnWifiToggleIcon(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.connectivityRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.toggle_switch)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnConnectToNetwork() {
        UiTestingUtils.sleep(1000)
        val index = 1
        Espresso.onView(ViewMatchers.withId(R.id.connectivityRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(10000)
    }

    fun performClickOnRemoteEnableOption(){
        UiTestingUtils.sleep(1000)
        val index = 2
        Espresso.onView(ViewMatchers.withId(R.id.connectivityRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.toggle_switch)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnConnectLater(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_left)
    }

    fun performClickOnSoftwareTermsAndConditions(){
        UiTestingUtils.sleep(1000)
        val index = 2
        Espresso.onView(ViewMatchers.withId(R.id.infoRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnServiceAndSupport(){
        UiTestingUtils.sleep(1000)
        val index = 1
        Espresso.onView(ViewMatchers.withId(R.id.infoRecyclerList)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnEnterDiagnostics(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.enter_diagnostics_btn)
    }

    fun performClickOnNextButtonOfEnterCode(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.next_button)
    }

    fun performClickOnBackspaceOfEnterCode(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.ivCancelIcon)
    }

    fun performClickOnDismissOfEnterServiceDiagnostic(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_left)
    }

    fun performClickOnEnterButtonOfEnterServiceDiagnostic(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_right)
    }

    fun performClickOnDismissButton(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_left)
    }

    fun performClickOnExitButton(){
        UiTestingUtils.sleep(1000)
        UiTestingUtils.performClick(R.id.text_button_right)
    }

    fun performClickOnErrorCodesHistory(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnAutoDiagnostics(){
        UiTestingUtils.sleep(1000)
        val index = 1
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnComponentActivation(){
        UiTestingUtils.sleep(1000)
        val index = 2
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnHmiVerification(){
        UiTestingUtils.sleep(1000)
        val index = 3
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnSystemInfo(){
        UiTestingUtils.sleep(1000)
        val index = 4
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnProduct(){
        UiTestingUtils.sleep(1000)
        val index = 0
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnWifi(){
        UiTestingUtils.sleep(1000)
        val index = 1
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnSoftware(){
        UiTestingUtils.sleep(1000)
        val index = 2
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun performClickOnRestoreFactoryDefaultsInService(){
        UiTestingUtils.sleep(1000)
        val index = 5
        Espresso.onView(ViewMatchers.withId(R.id.list)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                index, TestingUtils.clickChildViewWithId(R.id.list_item_main_view)
            )
        )
        UiTestingUtils.sleep(1000)
    }

    fun checkAllViewsVisibilityOfInfoMenu(){
        UiTestingUtils.isViewVisible(R.id.infoRecyclerList)
        UiTestingUtils.isViewVisible(R.id.headerBarInfo)
    }

    fun enterCode123(){
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(90F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(360F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(90F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(360F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(90F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(360F, 40F));
    }

    fun enterCode12(){
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(90F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(90F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(90F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(90F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboard)).perform(TestingUtils.touchDownAndUp(90F, 40F));
    }

    fun checkAllViewsVisibilityOfNetworkMenu(){
        UiTestingUtils.isViewVisible(R.id.connectivityRecyclerList)
        UiTestingUtils.isViewVisible(R.id.headerBarPreferences)
    }

    fun iCheckNetworkMenuWithWifiOff(){
        UiTestingUtils.sleep(1000)
        Espresso.onView(withIndexHash(withId(R.id.toggle_switch), 0)).check(matches(isDisplayed()))

        val isEnable: Boolean = toggleFragmentAppearanceTest?.isToggleButtonIsEnabled(
            R.id.toggle_switch,
            false
        ) ?: false
        assert(!isEnable) { "Disable Successfully" }

        toggleFragmentAppearanceTest?.toggleSwitchSizeValidationMatched()

//        UiTestingUtils.isTextMatchingAndFitting(R.id.btnPrimary, "NEXT")
    }

    fun knobSettingsRecyclerListIsVisible() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewVisible(R.id.recycler_view_list)
    }

    fun knobSettingsRecyclerListValidateView() {
        UiTestingUtils.sleep(1000)
        UiTestingUtils.isViewClickable(R.id.recycler_view_list)
        UiTestingUtils.isViewEnabled(R.id.recycler_view_list)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.recycler_view_list)),
            ActionBar.LayoutParams.MATCH_PARENT,
            0
        )
        Espresso.onView(withId(R.id.recycler_view_list))
            .check(matches(matchesBackgroundColor(R.color.common_solid_black)))
    }

    fun swapKnobFunctionsItemsValidateView(){
        UiTestingUtils.isViewVisible(R.id.widget_swap)
        UiTestingUtils.isViewVisible(R.id.parameter_1)
        UiTestingUtils.isViewVisible(R.id.swap_arrow_layout)
        UiTestingUtils.isViewVisible(R.id.parameter_2)
        //Left Knob
        UiTestingUtils.isTextMatching(R.id.left_knob,"LEFT KNOB")
//        val desiredTextSizeDp = 38.400001525878906f
//        val sizeInPixels = TestingUtils.dpToPx(context, desiredTextSizeDp)
//        GenericTextViewTest.checkMatchesSize(
//            Espresso.onView(ViewMatchers.withId(R.id.left_knob)),
//            ActionBar.LayoutParams.WRAP_CONTENT,
//            sizeInPixels
//        )
//        Espresso.onView(withId(R.id.left_knob))
//            .check(matches(matchesBackgroundColor(R.color.colorWhite)))
        val desiredTextSizeSp = 32f
        val sizeInPixels1 = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.left_knob)), sizeInPixels1.toFloat()
        )
        //Right Knob
        UiTestingUtils.isTextMatching(R.id.right_knob,"RIGHT KNOB")
//        val desiredTextSizeDp1 = 38.400001525878906f
//        val sizeInPixels2 = TestingUtils.dpToPx(context, desiredTextSizeDp1)
//        GenericTextViewTest.checkMatchesSize(
//            Espresso.onView(ViewMatchers.withId(R.id.right_knob)),
//            ActionBar.LayoutParams.WRAP_CONTENT,
//            sizeInPixels2
//        )
//        Espresso.onView(withId(R.id.right_knob))
//            .check(matches(matchesBackgroundColor(R.color.colorWhite)))
        val desiredTextSizeSp2 = 32f
        val sizeInPixels3 = TestingUtils.spToPx(context, desiredTextSizeSp2)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.right_knob)), sizeInPixels3.toFloat()
        )
        //Icons Validation
        val settingsIcon = AppCompatResources.getDrawable(context, R.drawable.icon_40px_settings)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.icon_40px_settings)),
            settingsIcon
        )
        val desiredHeight = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeight)
        val desiredWidth = 40f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidth)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.icon_40px_settings),
                    0
                )
            ), widthInPixels, heightInPixels
        )

        val assistedIcon = AppCompatResources.getDrawable(context, R.drawable.icon_40px_assisted)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.icon_40px_assisted)),
            assistedIcon
        )
        val desiredHeight1 = 40f
        val heightInPixels1 = dpToPx(CookingKACucumberTests.context, desiredHeight1)
        val desiredWidth1 = 40f
        val widthInPixels1 = dpToPx(CookingKACucumberTests.context, desiredWidth1)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.icon_40px_assisted),
                    0
                )
            ), widthInPixels1, heightInPixels1
        )

        //Description Validate view
        UiTestingUtils.isViewVisible(R.id.left_knob_description)
//        Espresso.onView(withId(R.id.left_knob_description))
//            .check(matches(matchesBackgroundColor(R.color.colorWhite)))
        UiTestingUtils.isTextMatching(R.id.left_knob_description,"For all features\nrelated to settings")
//        val desiredText1 = 288f
//        val sizeInPixels4 = TestingUtils.dpToPx(context, desiredText1)
//        GenericTextViewTest.checkMatchesSize(
//            Espresso.onView(ViewMatchers.withId(R.id.left_knob_description)),
//            sizeInPixels4,
//            ActionBar.LayoutParams.WRAP_CONTENT
//        )

        UiTestingUtils.isViewVisible(R.id.right_knob_description)
//        Espresso.onView(withId(R.id.right_knob_description))
//            .check(matches(matchesBackgroundColor(R.color.colorWhite)))
        UiTestingUtils.isTextMatching(R.id.right_knob_description,"For all features\nrelated to cooking")
//        val desiredText2 = 288f
//        val sizeInPixels5 = TestingUtils.dpToPx(context, desiredText2)
//        GenericTextViewTest.checkMatchesSize(
//            Espresso.onView(ViewMatchers.withId(R.id.right_knob_description)),
//            sizeInPixels5,
//            ActionBar.LayoutParams.WRAP_CONTENT
//        )

        //swap arrow
        val swapArrowIcon = AppCompatResources.getDrawable(context, R.drawable.swap_arrow)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.swap_arrow)),
            swapArrowIcon
        )
        val desiredHeight2 = 38f
        val heightInPixels2 = dpToPx(CookingKACucumberTests.context, desiredHeight2)
        val desiredWidth2= 51f
        val widthInPixels2 = dpToPx(CookingKACucumberTests.context, desiredWidth2)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.swap_arrow),
                    0
                )
            ), widthInPixels2, heightInPixels2
        )

        //Button Validate view
        UiTestingUtils.isViewVisible(R.id.btnPrimarySwap)
        UiTestingUtils.isTextMatching(R.id.btnPrimarySwap,"SWAP")
        val desiredTextSizeSp3 = 32f
        val sizeInPixels6 = TestingUtils.spToPx(context, desiredTextSizeSp3)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.btnPrimarySwap)), sizeInPixels6.toFloat()
        )
    }

    fun swapKnobFunctionsHeaderValidateView(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Swap Knob Functions")
    }

    fun swapKnobFunctionsItemsValidateViewAfterSwapping(){
        //Left Knob
        UiTestingUtils.isTextMatching(R.id.left_knob,"LEFT KNOB")

        val desiredTextSizeSp = 32f
        val sizeInPixels1 = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.left_knob)), sizeInPixels1.toFloat()
        )

        //Right Knob
        UiTestingUtils.isTextMatching(R.id.right_knob,"RIGHT KNOB")

        val desiredTextSizeSp2 = 32f
        val sizeInPixels3 = TestingUtils.spToPx(context, desiredTextSizeSp2)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.right_knob)), sizeInPixels3.toFloat()
        )

        //Icons Validation
        val settingsIcon = AppCompatResources.getDrawable(context, R.drawable.icon_40px_assisted)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.icon_40px_settings)),
            settingsIcon
        )
        val desiredHeight = 40f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeight)
        val desiredWidth = 40f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidth)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.icon_40px_settings),
                    0
                )
            ), widthInPixels, heightInPixels
        )

        val assistedIcon = AppCompatResources.getDrawable(context, R.drawable.icon_40px_settings)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.icon_40px_assisted)),
            assistedIcon
        )
        val desiredHeight1 = 40f
        val heightInPixels1 = dpToPx(CookingKACucumberTests.context, desiredHeight1)
        val desiredWidth1 = 40f
        val widthInPixels1 = dpToPx(CookingKACucumberTests.context, desiredWidth1)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.icon_40px_assisted),
                    0
                )
            ), widthInPixels1, heightInPixels1
        )

        //Description Validate view
        UiTestingUtils.isViewVisible(R.id.left_knob_description)

        UiTestingUtils.isTextMatching(R.id.left_knob_description,"For all features\nrelated to cooking")

        UiTestingUtils.isViewVisible(R.id.right_knob_description)

        UiTestingUtils.isTextMatching(R.id.right_knob_description,"For all features\nrelated to settings")

        //swap arrow
        val swapArrowIcon = AppCompatResources.getDrawable(context, R.drawable.swap_arrow)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.swap_arrow)),
            swapArrowIcon
        )
        val desiredHeight2 = 38f
        val heightInPixels2 = dpToPx(CookingKACucumberTests.context, desiredHeight2)
        val desiredWidth2= 51f
        val widthInPixels2 = dpToPx(CookingKACucumberTests.context, desiredWidth2)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.swap_arrow),
                    0
                )
            ), widthInPixels2, heightInPixels2
        )

        //Button Validate view
        UiTestingUtils.isViewVisible(R.id.btnPrimarySwap)
        UiTestingUtils.isTextMatching(R.id.btnPrimarySwap,"SWAP")
        val desiredTextSizeSp3 = 32f
        val sizeInPixels6 = TestingUtils.spToPx(context, desiredTextSizeSp3)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.btnPrimarySwap)), sizeInPixels6.toFloat()
        )
    }

    fun checkAllViewsVisibilityOfKnobSettingsScreen(){
        UiTestingUtils.isViewVisible(R.id.recycler_view_list)
        UiTestingUtils.isViewVisible(R.id.headerBarPreferences)
    }

    fun knobFunctionsInfoHeaderValidateView(){
        UiTestingUtils.isTextMatching(R.id.tvTitleApplianceFeatureGuide,"Understanding Knob Functions")
    }

    fun knobFunctionsLeftKnobInfoValidateView(){
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isTextMatching(R.id.text_view_title,"USE THE LEFT KNOB\nFOR COOKING")

        val desiredTextSize6 = 30f
        val sizeInPixels6 = TestingUtils.spToPx(context, desiredTextSize6)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels6.toFloat()
        )

        //Description text validate view
        val desiredTextSize7 = 30f
        val sizeInPixels7 = TestingUtils.spToPx(context, desiredTextSize7)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_popup_info)), sizeInPixels7.toFloat()
        )
    }

    fun knobFunctionsRightKnobInfoValidateView(){
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isTextMatching(R.id.text_view_title,"USE THE RIGHT KNOB\nFOR SETTINGS")

        val desiredTextSize6 = 30f
        val sizeInPixels6 = TestingUtils.spToPx(context, desiredTextSize6)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels6.toFloat()
        )

        //Description text validate view
        val desiredTextSize7 = 30f
        val sizeInPixels7 = TestingUtils.spToPx(context, desiredTextSize7)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_popup_info)), sizeInPixels7.toFloat()
        )
    }

    fun knobFunctionsNextButtonValidateView(){
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isTextMatching(R.id.btnPrimary,"NEXT")

        val desiredTextSize7 = 32f
        val sizeInPixels7 = TestingUtils.spToPx(context, desiredTextSize7)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.btnPrimary)), sizeInPixels7.toFloat()
        )
    }

    fun knobFunctionsDoneButtonValidateView(){
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isTextMatching(R.id.btnPrimary,"DONE")

        val desiredTextSize7 = 32f
        val sizeInPixels7 = TestingUtils.spToPx(context, desiredTextSize7)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.btnPrimary)), sizeInPixels7.toFloat()
        )
    }

    fun assignFavoriteHeaderValidateView(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Assign Favorite")
        UiTestingUtils.isViewVisible(R.id.ivInfo)

        //info icon
        val infoIcon = AppCompatResources.getDrawable(context, R.drawable.ic_info)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivInfo)),
            infoIcon
        )
    }

    fun assignFavoriteItemsValidateView(){
        UiTestingUtils.isViewVisible(R.id.recycler_view_list)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Quick bake 350",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Start a Favorite mode",36f,R.color.solid_white)

        Espresso.onView(withIndex(withId(R.id.list_item_radio_button),0)).check(matches(isDisplayed()))
        Espresso.onView(withIndex(withId(R.id.list_item_radio_button),1)).check(matches(isDisplayed()))
    }

    fun assignFavoriteInfoPopupValidateView(){
        UiTestingUtils.isViewVisible(R.id.text_view_title)
        UiTestingUtils.isTextMatching(R.id.text_view_title,"Assign Favorite")
        val desiredTextSize7 = 40f
        val sizeInPixels7 = TestingUtils.spToPx(context, desiredTextSize7)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels7.toFloat()
        )
        TestingUtils.checkTextColorValidation(R.id.text_view_title, "#ffffff")

        //Description Validation
        UiTestingUtils.isViewVisible(R.id.text_view_description)

        val desiredTextSize8 = 30f
        val sizeInPixels8 = TestingUtils.spToPx(context, desiredTextSize8)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels8.toFloat()
        )
        TestingUtils.checkTextColorValidation(R.id.text_view_description, "#ffffff")

        //Button Validation
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun checkAllViewsVisibilityOfAssignFavorite(){
        UiTestingUtils.isViewVisible(R.id.recycler_view_list)
        UiTestingUtils.isViewVisible(R.id.headerBarPreferences)
    }

    fun FavoritesHeaderValidateView(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Favorites")
    }

    fun FavoritesEmptyScreenValidateView(){
        UiTestingUtils.isViewVisible(R.id.favEmpty)
        UiTestingUtils.isTextMatching(R.id.favEmpty,"There is no history available")
        TestingUtils.checkTextColorValidation(R.id.favEmpty, "#ffffff")

        val desiredTextSize8 = 30f
        val sizeInPixels8 = TestingUtils.spToPx(context, desiredTextSize8)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.favEmpty)), sizeInPixels8.toFloat()
        )

        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.favEmpty)),
            Gravity.CENTER
        )
    }

    fun FavoritesItemsValidateView(){
        UiTestingUtils.isViewVisible(R.id.languageRecyclerList)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Bake 175° ",36f,R.color.solid_white)
        Espresso.onView(withIndex(withId(R.id.list_item_radio_button),0)).check(matches(isDisplayed()))
    }

    fun assignFavoriteItemsWithFavoriteSelected(){
        UiTestingUtils.isViewVisible(R.id.recycler_view_list)
    }

    fun knobSettingsValidateViewWithBakeRecipe(){
        UiTestingUtils.isViewVisible(R.id.recycler_view_list)
    }

    fun checkTimeAndDateMenuScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Time and Date")
        UiTestingUtils.isViewVisible(R.id.recyclerList)

        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Set Time",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Set Date",36f,R.color.solid_white)
    }

    fun checkSetTimeVerticalTumblerScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Set Time")
        UiTestingUtils.isViewVisible(R.id.tumblerNumeric)
        UiTestingUtils.isViewVisible(R.id.startNowText)

        UiTestingUtils.isViewVisible(R.id.left_format_selection)
        UiTestingUtils.isTextMatching(R.id.left_format_selection,"12H")
        val desiredTextSize8 = 36f
        val sizeInPixels8 = TestingUtils.spToPx(context, desiredTextSize8)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.left_format_selection)), sizeInPixels8.toFloat()
        )

        UiTestingUtils.isViewVisible(R.id.right_format_selection)
        UiTestingUtils.isTextMatching(R.id.right_format_selection,"24H")
        val desiredTextSize = 36f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSize)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.right_format_selection)), sizeInPixels.toFloat()
        )

        checkVericalTumblerHeight()
    }

    fun checkVericalTumblerHeight(): Boolean {
        val expectedHeight = 240
        var isPaddingAsExpected = false

        onView(withId(R.id.tumblerNumeric)).check { view, _ ->
            if (view is BaseTumbler) {
                val baseTumbler = view as BaseTumbler
                val itemView = baseTumbler.findViewHolderForAdapterPosition(0)?.itemView

                itemView?.let {
                    // Retrieve padding values
                    val containerHeight = itemView.height

                    // Check if all padding values match the expected padding
                    isPaddingAsExpected = containerHeight == expectedHeight
                }
            }
        }
        return isPaddingAsExpected
    }

    fun setTimeOnTumbler(){
         UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerNumericBasedHours,0)
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerNumericBasedMins,0)
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerNumericBasedSeconds,0)
    }

    fun checkTimeInTimeAndDateMenuScreen(){
        UiTestingUtils.isViewVisible(R.id.recyclerList)
    }

    fun checkSetTimeNumpadScreen(){
        TestingUtils.checkTextColorValidation(R.id.tvCookTime, "#FFFFFF")

        val desiredTextSize8 = 56f
        val sizeInPixels8 = TestingUtils.spToPx(context, desiredTextSize8)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.tvCookTime)), sizeInPixels8.toFloat()
        )

        //backspace
        val backspace = AppCompatResources.getDrawable(context, R.drawable.ic_cancel)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivCancelIcon)),
            backspace
        )

        //tumbler icon
        val tumblerIcon = AppCompatResources.getDrawable(context, R.drawable.ic_tumbler)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withId(R.id.ivTumblerIcon)),
            tumblerIcon
        )

        //keyboard
        UiTestingUtils.isViewVisible(R.id.keyboardview)
//        val desiredHeight = 192f
//        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeight)
//        val desiredWidth = 854f
//        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidth)
//        GenericTextViewTest.checkMatchesSize(
//            Espresso.onView(ViewMatchers.withId(R.id.keyboardview)),
//            widthInPixels,heightInPixels)

        UiTestingUtils.isViewVisible(R.id.left_format_selection)
        UiTestingUtils.isViewVisible(R.id.right_format_selection)
        val desiredTextSize = 36f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSize)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.left_format_selection)), sizeInPixels.toFloat()
        )
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.right_format_selection)), sizeInPixels.toFloat()
        )

        //Button Validation
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun check24HSetTimeNumpadScreen(){
        UiTestingUtils.sleep(1000)
        TestingUtils.checkTextColorValidation(R.id.right_format_selection, "#FFFFFF")
        TestingUtils.checkTextColorValidation(R.id.left_format_selection, "#AAA5A1")
        UiTestingUtils.sleep(1000)
    }

    fun check12HSetTimeNumpadScreen(){
        UiTestingUtils.sleep(1000)
        TestingUtils.checkTextColorValidation(R.id.left_format_selection, "#FFFFFF")
        TestingUtils.checkTextColorValidation(R.id.right_format_selection, "#AAA5A1")
        UiTestingUtils.sleep(1000)
    }

    fun setTime1212OnSetTimeNumpad(){
        UiTestingUtils.sleep(1000)
        onView(withId(R.id.keyboardview)).perform(TestingUtils.touchDownAndUp(90F, 40F));
        onView(withId(R.id.keyboardview)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboardview)).perform(TestingUtils.touchDownAndUp(90F, 40F));
        onView(withId(R.id.keyboardview)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        UiTestingUtils.sleep(1000)
    }

    fun setTime2222OnSetTimeNumpad(){
        UiTestingUtils.sleep(1000)
        onView(withId(R.id.keyboardview)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboardview)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboardview)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        onView(withId(R.id.keyboardview)).perform(TestingUtils.touchDownAndUp(180F, 40F));
        UiTestingUtils.sleep(1000)
    }

    fun checkAllViewsVisibilityOfSetTimeVerticalTumbler(){
        UiTestingUtils.isViewVisible(R.id.tumblerNumeric)
        UiTestingUtils.isViewVisible(R.id.startNowText)
    }

    fun checkAllViewsVisibilityOfSetTimeNumpad(){
        UiTestingUtils.isViewVisible(R.id.keyboardview)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun checkSetTimeNumpadScreenWithErrorMessage() {
        UiTestingUtils.isViewVisible(R.id.text_view_helper_text)
        UiTestingUtils.isTextMatching(R.id.text_view_helper_text, "Time must be in 12 hour format")

        TestingUtils.checkTextColorValidation(R.id.text_view_helper_text, "#F87262")

        val desiredTextSize = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSize)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_helper_text)), sizeInPixels.toFloat()
        )
    }

    fun check24HSetTimeVerticalTumblerScreen(){
        UiTestingUtils.isViewNotVisible(R.id.tumblerNumericBasedSeconds)
    }

    fun checkAllViewsVisibilityOfTimeAndDateMenuScreen(){
        UiTestingUtils.isViewVisible(R.id.recyclerList)
    }

    fun checkSoundVolumeMenuScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Sound Volume")

        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Mute",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Alert and Timers",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Buttons and Effects",36f,R.color.solid_white)

        UiTestingUtils.sleep(1000)
        Espresso.onView(withIndexHash(withId(R.id.toggle_switch), 0)).check(matches(isDisplayed()))

        val isEnable: Boolean = toggleFragmentAppearanceTest?.isToggleButtonIsEnabled(
            R.id.toggle_switch,
            true
        ) ?: false
        assert(!isEnable) { "Disable Successfully" }

        toggleFragmentAppearanceTest?.toggleSwitchSizeValidationMatched()
    }

    fun checkAlertsAndTimersTumblerScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Alert and Timers")
        UiTestingUtils.isViewVisible(R.id.tumblerString)

        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.tumblerString)),
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT)
    }

    fun checkSoundVolumeMenuScreenWhenMute(){
        UiTestingUtils.sleep(1000)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Mute",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Alert and Timers",36f,R.color.light_grey)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Buttons and Effects",36f,R.color.light_grey)

        Espresso.onView(withIndexHash(withId(R.id.toggle_switch), 0)).check(matches(isDisplayed()))

        val isEnable: Boolean = toggleFragmentAppearanceTest?.isToggleButtonIsEnabled(
            R.id.toggle_switch,
            false
        ) ?: false
        assert(!isEnable) { "Disable Successfully" }
    }

    fun checkRegionalSettingsMenuScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Regional Settings")
        UiTestingUtils.isViewVisible(R.id.regionalSettingsRecyclerList)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Language",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Temperature Unit",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Weight Unit",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,3,"Time Format",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,4,"Date Format",36f,R.color.solid_white)

        TestingUtils.checkPropertiesOfText(R.id.list_item_right_text_view,0,"English",30f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_on,1,"°F",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_on,2,"lbs",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_on,3,"12 hrs",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_on,4,"MM/DD",30f,R.color.solid_white)

        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_off,1,"°C",36f,R.color.light_grey)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_off,2,"gms",36f,R.color.light_grey)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_off,3,"24 hrs",36f,R.color.light_grey)
//        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_off,4,"DD/MM",30f,R.color.light_grey)
    }

    fun checkRegionalSettingsMenuScreenAfterChanging(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Regional Settings")
        UiTestingUtils.isViewVisible(R.id.regionalSettingsRecyclerList)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Language",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Temperature Unit",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Weight Unit",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,3,"Time Format",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,4,"Date Format",36f,R.color.solid_white)

        TestingUtils.checkPropertiesOfText(R.id.list_item_right_text_view,0,"English",30f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_on,1,"°F",36f,R.color.light_grey)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_on,2,"lbs",36f,R.color.light_grey)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_on,3,"12 hrs",36f,R.color.light_grey)
//        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_on,4,"MM/DD",30f,R.color.light_grey)

        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_off,1,"°C",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_off,2,"gms",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_off,3,"24 hrs",36f,R.color.solid_white)
//        TestingUtils.checkPropertiesOfText(R.id.toggle_switch_off,4,"DD/MM",30f,R.color.solid_white)
    }

    fun checkLanguageMenuScreen(){
        UiTestingUtils.isViewVisible(R.id.languageRecyclerList)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"English",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Canadian French",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Spanish",36f,R.color.solid_white)
    }

    fun checkDisplayAndBrightnessMenuScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Display and Brightness")
        UiTestingUtils.isViewVisible(R.id.recyclerList)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Display and Brightness",36f,R.color.solid_white)
    }

    fun checkTemperatureCalibrationScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Temperature Calibration")
        UiTestingUtils.isViewVisible(R.id.tumblerNumericBased)

        val desiredTextSizeDp = 96f
        val sizeInPixels = TestingUtils.dpToPx(context, desiredTextSizeDp)
        GenericTextViewTest.checkMatchesSize(
            Espresso.onView(ViewMatchers.withId(R.id.left_knob)),
            ActionBar.LayoutParams.MATCH_PARENT,
            sizeInPixels
        )

        UiTestingUtils.isTextMatching(R.id.ifTooHot,"If too hot")
        UiTestingUtils.isTextMatching(R.id.ifTooCool,"If too cool")

        //Button Validation
        UiTestingUtils.isViewVisible(R.id.btnPrimary)
        UiTestingUtils.isTextMatching(R.id.btnPrimary,"SET")
    }

    fun setTemperatureOnTemperatureCalibration(){
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerNumericBased,1)
    }

    fun checkRestoreSettingsMenuScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Restore Settings")
        UiTestingUtils.isViewVisible(R.id.recyclerList)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Restore Factory Defaults",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Reset Learn More",36f,R.color.solid_white)
    }

    fun checkResetFactoryDefaultsScreen(){
        UiTestingUtils.isViewVisible(R.id.icon_112px_alert)

        val desiredHeight = 112f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeight)
        val desiredWidth = 112f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidth)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.icon_112px_alert),
                    0
                )
            ), widthInPixels, heightInPixels
        )
        //alert icon
//        val alertIcon = AppCompatResources.getDrawable(context, R.drawable.icon_alert)
//        GenericViewTest.checkMatchesBackground(
//            Espresso.onView(withId(R.id.icon_112px_alert)),
//            alertIcon
//        )

        UiTestingUtils.isTextMatching(R.id.header_text_title,"Restore Factory Defaults")
        UiTestingUtils.isTextMatching(R.id.description_text,"This action will restore the product to default settings.")
        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.header_text_title)), sizeInPixels.toFloat()
        )
        val desiredTextSizeSp1 = 30f
        val sizeInPixels1 = TestingUtils.spToPx(context, desiredTextSizeSp1)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.description_text)), sizeInPixels1.toFloat()
        )

        UiTestingUtils.isViewVisible(R.id.primary_reset_button)
        UiTestingUtils.isTextMatching(R.id.primary_reset_button,"CANCEL")

        UiTestingUtils.isViewVisible(R.id.secondary_reset_button)
        UiTestingUtils.isTextMatching(R.id.secondary_reset_button,"PROCEED")
    }

    fun checkAllViewsVisibilityOfRestoreMenuScreen(){
        UiTestingUtils.isViewVisible(R.id.recyclerList)
        UiTestingUtils.isViewVisible(R.id.headerBarPreferences)
    }

    fun checkRestoreFactoryDefaultsPopup(){
        UiTestingUtils.isTextMatching(R.id.text_view_title,"Restore Factory Defaults")
        UiTestingUtils.isTextMatching(R.id.text_view_description,"You will lose all your personal data, saved recipes\nand history. After the system reboots you will also\nhave to set up & reconnect your appliance to the wifi.\nDo you wish to proceed?")

        val desiredTextSizeSp = 40f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
        val desiredTextSizeSp1 = 30f
        val sizeInPixels1 = TestingUtils.spToPx(context, desiredTextSizeSp1)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels1.toFloat()
        )
        UiTestingUtils.isViewVisible(R.id.text_button_left)
        UiTestingUtils.isViewVisible(R.id.text_button_right)
    }

    fun checkCavityLightMenuScreen(){
        UiTestingUtils.isViewVisible(R.id.headerBarPreferences)
        UiTestingUtils.isViewVisible(R.id.recycler_view_list)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Keep light on while cooking",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Control light manually",36f,R.color.solid_white)
    }

    fun checkDoNotUnplug(){
        UiTestingUtils.isTextMatching(R.id.do_not_unplug_appliance,"DO NOT UNPLUG APPLIANCE")

        val desiredTextSizeSp = 36f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.do_not_unplug_appliance)), sizeInPixels.toFloat()
        )

        GenericTextViewTest.checkMatchesGravity(
            Espresso.onView(withId(R.id.do_not_unplug_appliance)),
            Gravity.CENTER
        )
    }

    fun checkConnectToNetworkScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Connect to Network")
        UiTestingUtils.isViewVisible(R.id.pin_info)

        val desiredHeight = 200f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeight)
        val desiredWidth = 200f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidth)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.pin_info),
                    0
                )
            ), widthInPixels, heightInPixels
        )

        UiTestingUtils.isViewVisible(R.id.text_view_SAID)
        UiTestingUtils.isViewVisible(R.id.text_view_description)
    }

    fun checkSetUpWifiPopup(){
        UiTestingUtils.isTextMatching(R.id.text_view_title,"Set up Wifi to use this feature")
        UiTestingUtils.isTextMatching(R.id.text_view_description,"To turn on Remote Enable you will first need to connect this appliance to a Wifi network. Tap Connect now to begin Wifi setup.")

        UiTestingUtils.isViewVisible(R.id.text_button_left)
//        UiTestingUtils.isTextMatching(R.id.text_button_left,"CONNECT LATER")

        UiTestingUtils.isViewVisible(R.id.text_button_right)
//        UiTestingUtils.isTextMatching(R.id.text_button_right,"CONNECT NOW")
    }

    fun checkSoftwareTermsAndConditionsScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Software Terms and Conditions")
        UiTestingUtils.isViewVisible(R.id.qr_code)

        val desiredHeight = 200f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeight)
        val desiredWidth = 200f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidth)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.qr_code),
                    0
                )
            ), widthInPixels, heightInPixels
        )
        UiTestingUtils.isTextMatching(R.id.text_view_description,"By scanning the QR code to the right you'll be taken to the store for downloading the app.")
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_description)), sizeInPixels.toFloat()
        )
    }

    fun checkServiceAndSupportScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Service & Support")
        UiTestingUtils.isViewVisible(R.id.qr_code)

        val desiredHeight = 200f
        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeight)
        val desiredWidth = 200f
        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidth)
        GenericTextViewTest.checkMatchesSize(
            onView(
                TestingUtils.withIndex(
                    withId(R.id.qr_code),
                    0
                )
            ), widthInPixels, heightInPixels
        )

        UiTestingUtils.isTextMatching(R.id.text_view_title,"Scan QR Code for Customer Support Info")
        val desiredTextSizeSp = 36f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_title)), sizeInPixels.toFloat()
        )
    }

    fun checkEnterCodeScreen(){
        UiTestingUtils.isTextMatching(R.id.keypadTextView,"000000000")
        UiTestingUtils.isTextMatching(R.id.text_view_system_edit_text,"Enter code")
        val desiredTextSizeSp = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSizeSp)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_system_edit_text)), sizeInPixels.toFloat()
        )

        GenericTextViewTest.checkMatchesTextColor(
            onView(TestingUtils.withIndex(withId(R.id.text_view_system_edit_text), 0)),
            parseColor("#AAA5A1")
        )
        UiTestingUtils.isViewVisible(R.id.keyboard)

//        val desiredHeight = 192f
//        val heightInPixels = dpToPx(CookingKACucumberTests.context, desiredHeight)
//        val desiredWidth = 854f
//        val widthInPixels = dpToPx(CookingKACucumberTests.context, desiredWidth)
//        GenericTextViewTest.checkMatchesSize(
//            onView(
//                TestingUtils.withIndex(
//                    withId(R.id.keyboard),
//                    0
//                )
//            ), widthInPixels, heightInPixels
//        )
        UiTestingUtils.isViewVisible(R.id.next_button)
    }

    fun checkEnterCodeScreenWithError(){
        UiTestingUtils.isViewVisible(R.id.text_view_helper_red_warning_text)
        UiTestingUtils.isTextMatching(R.id.text_view_helper_red_warning_text,"Enter correct pin")

        TestingUtils.checkTextColorValidation(R.id.text_view_helper_red_warning_text, "#F87262")

        val desiredTextSize = 30f
        val sizeInPixels = TestingUtils.spToPx(context, desiredTextSize)
        GenericTextViewTest.checkMatchesTextSize(
            Espresso.onView(withId(R.id.text_view_helper_red_warning_text)), sizeInPixels.toFloat()
        )
    }

    fun checkEnterServiceDiagnosticsPopup(){
        UiTestingUtils.sleep((1000))
        UiTestingUtils.isTextMatching(R.id.text_view_title,"Enter Service Diagnostics")
        UiTestingUtils.isTextMatching(R.id.text_view_description,"The area you are about to enter is only for service technicians. Press DISMISS to exit or ENTER to navigate through the diagnostic settings.")

        UiTestingUtils.isViewVisible(R.id.text_button_left)
        UiTestingUtils.isTextMatching(R.id.text_button_left,"DISMISS")

        UiTestingUtils.isViewVisible(R.id.text_button_right)
        UiTestingUtils.isTextMatching(R.id.text_button_right,"ENTER")
    }

    fun checkAllViewsVisibilityOfEnterCodeScreen(){
        UiTestingUtils.isViewVisible(R.id.title_bar)
        UiTestingUtils.isViewVisible(R.id.keyboard)
        UiTestingUtils.isViewVisible(R.id.next_button)
    }

    fun checkServiceDiagnosticsMenuScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Service Diagnostics")
        UiTestingUtils.isViewVisible(R.id.list)
    }

    fun checkEndServiceDiagnosticsPopup(){
        UiTestingUtils.isTextMatching(R.id.text_view_title,"End Service Diagnostics")
        UiTestingUtils.isTextMatching(R.id.text_view_description,"Pressing EXIT will return you to the home screen.")

        UiTestingUtils.isViewVisible(R.id.text_button_left)
        UiTestingUtils.isTextMatching(R.id.text_button_left,"DISMISS")

        UiTestingUtils.isViewVisible(R.id.text_button_right)
        UiTestingUtils.isTextMatching(R.id.text_button_right,"ENTER")
    }

    fun checkAllViewsVisibilityOfDiagnosticsMenuScreen(){
        UiTestingUtils.isViewVisible(R.id.title_bar)
        UiTestingUtils.isViewVisible(R.id.list)
    }

    fun checkErrorCodeHistoryScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitle,"Error Code History")
        UiTestingUtils.isViewVisible(R.id.list)
        UiTestingUtils.isViewVisible(R.id.button_primary)
        UiTestingUtils.isTextMatching(R.id.button_primary,"CLEAR ALL")
    }

    fun checkStartAutoDiagnosticsPopup(){
        UiTestingUtils.isTextMatching(R.id.text_view_title,"Start Auto Diagnostics")
        UiTestingUtils.isTextMatching(R.id.text_view_description,"Once the Auto Diagnostic begins all past stored error codes will be cleared. Do you wish to continue?")

        UiTestingUtils.isViewVisible(R.id.text_button_left)
        UiTestingUtils.isTextMatching(R.id.text_button_left,"DISMISS")

        UiTestingUtils.isViewVisible(R.id.text_button_right)
        UiTestingUtils.isTextMatching(R.id.text_button_right,"CONTINUE")
    }

    fun checkComponentActivationMenuScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitleResource,"Component Activation")
        UiTestingUtils.isTextMatching(R.id.text_sub_text,"Please refer to technician guide.")

        UiTestingUtils.isViewVisible(R.id.list)
    }

    fun checkSystemInfoScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitleResource,"System Info")
        UiTestingUtils.isViewVisible(R.id.list)
    }

    fun checkProductScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitleResource,"Product")
        UiTestingUtils.isViewVisible(R.id.list)
    }

    fun checkAllViewsOfSystemInfoMenuScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitleResource,"System Info")
        UiTestingUtils.isViewVisible(R.id.list)
    }

    fun checkAllViewsOfWifiScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitleResource,"Wifi")
        UiTestingUtils.isViewVisible(R.id.list)
    }

    fun checkAllViewsOfSoftwareScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitleResource,"Software")
        UiTestingUtils.isViewVisible(R.id.list)
        UiTestingUtils.isTextMatching(R.id.title,"GUI")
    }

    fun checkResetAndRebootScreen(){
        UiTestingUtils.isTextMatching(R.id.tvTitleResource,"Reboot & Reset")
        UiTestingUtils.isViewVisible(R.id.list)
    }

    fun knobSettingsRecyclerViewItemsValidateView(){
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Understanding Knob Functions",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Swap Knob Functions",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Assign Favorite",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,3,"Knob Light",36f,R.color.solid_white)

        TestingUtils.checkPropertiesOfText(R.id.list_item_right_text_view,2,"Quick bake 350",30f,R.color.solid_white)
        Espresso.onView(withIndex(withId(R.id.toggle_switch), 3)).check(matches(isDisplayed()))
    }

    fun knobSettingsRecyclerViewItemsValidateViewWithKnobLightOff(){
        Espresso.onView(withIndex(withId(R.id.toggle_switch), 3)).check(matches(isDisplayed()))
    }

    fun knobSettingsRecyclerViewItemsValidateViewWithBakeFavorite(){
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,0,"Knob Functions Info",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,1,"Swap Knob Functions",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,2,"Assign Favorite",36f,R.color.solid_white)
        TestingUtils.checkPropertiesOfText(R.id.list_item_title_text_view,3,"Knob Light",36f,R.color.solid_white)

        TestingUtils.checkPropertiesOfText(R.id.list_item_right_text_view,2,"Bake 175° ",30f,R.color.solid_white)

        Espresso.onView(withIndex(withId(R.id.toggle_switch), 3)).check(matches(isDisplayed()))
    }

    fun checkAllViewsVisibilityOfRestoreDefaultScreen(){
        UiTestingUtils.isViewVisible(R.id.content)
    }
}