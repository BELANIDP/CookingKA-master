
package com.whirlpool.cooking.appearance

import android.content.Context
import com.whirlpool.cooking.ka.R
import androidx.test.core.app.ApplicationProvider
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils.*

class QuickToggleSettingGridListAppearanceTest {

    val context = ApplicationProvider.getApplicationContext<Context>()

    fun recyclerViewGridListIsVisible() {
        isViewVisible(R.id.recycler_view_grid_list)
    }

    fun recyclerViewGridListIsViewEnabled () {
        isViewEnabled(R.id.recycler_view_grid_list)
    }

    fun scrollQuickToggleGridListToSpecificElement(position : Int){
        scrollToRecyclerViewIndex(R.id.recycler_view_grid_list,position)
    }

    fun verifyTextAndProperties(position: String, visible: String, text: String, width: String, height: String, fontFamily: String, weight: String, size: String, lineHeight: String, gravity: String, color:String){
        TestingUtils.checkAllPropertiesOfTextOfRecyclerViewItem(R.id.recycler_view_grid_list, position, R.id.settings_item_title_text_view, visible, text, width, height, fontFamily, weight, size, lineHeight , gravity, color)
    }

    fun verifyToggleButtonsProperties(position: String, visible: String, width: String, height: String, on_off: String, enable_disable: String){
        TestingUtils.checkAllPropertiesOfToggleOfRecyclerViewItem(R.id.recycler_view_grid_list, position, R.id.toggle_switch, visible, width, height, on_off, enable_disable)
    }
}
