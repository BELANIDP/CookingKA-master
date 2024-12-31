/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.customviews.listView

import android.view.View

/**
 * File        : android.presenter.customviews.listView.ListTileData
 * Brief       : Item model class used by List Fragment
 * Author      : PATELJ7
 * Created On  : 06-Feb-2024
 * Details     : Used by List Fragment to create instances of list items
 */
class ListTileData {
    var headingText = ""
    var titleText = ""
    var subText = ""
    var rightText = ""
    var itemIconID = 0
    var rightIconID = 0
    var navigationID = 0
    var seperatorVisibility = View.GONE
    var headingVisibility = View.GONE
    var listItemMainViewVisibility = View.GONE
    var listItemDividerViewVisibility = View.VISIBLE
    var titleTextVisibility = View.VISIBLE
    var subTextVisibility = View.VISIBLE
    var itemIconVisibility = View.GONE
    var rightIconVisibility = View.VISIBLE
    var rightTextVisibility = View.VISIBLE
    var rightClockTextVisibility = View.GONE
    var itemViewVisibility = View.VISIBLE
    var isItemEnabled = true
    var isPaddingView = false
    var radioButtonData = RadioButtonData()
    var underLineButton = UnderLineButtonData()

    //Network Settings Variables
    var isClickable = true
    var isAllCaps = true

    var toggleSwitchData = ToggleSwitchData()

    /**
     * Class to hold the data related to list Item Radio Button view
     */
    class RadioButtonData {
        var visibility = View.GONE
        var isChecked = false
        var isEnabled = true
    }
    class ToggleSwitchData {
        var visibility = View.GONE
        var isEnabled = true
        var isChecked = false
    }
    /**
     * Class to hold the data related to list Item Underline Button view
     */
    class UnderLineButtonData {
        var visibility = View.GONE
        var isChecked = false
        var isEnabled = true
        var leftButtonText = ""
        var rightButtonText = ""
    }
}
