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
class NotificationsTileData {
    var headingText = ""
    var titleText = ""
    var subText = ""
    var rightText = "0"
    var historyID = 0
    var rightIconID = 0
    var headingVisibility = View.GONE
    var listItemDividerViewVisibility = View.VISIBLE
    var titleTextVisibility = View.VISIBLE
    var subTextVisibility = View.VISIBLE
    var rightTextVisibility = View.VISIBLE
    var seperatorVisibility = View.GONE
}
