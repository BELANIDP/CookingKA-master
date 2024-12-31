/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
@file:Suppress("unused")

package android.presenter.customviews.widgets.gridview

import android.view.View
import androidx.annotation.IntDef
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButtonOnClickListener


/**
 * File        : android.presenter.customviews.widgets.gridview.GridListItemModel
 * Brief       : Data model for storing grid tile information.
 * Author      : BHIMAR
 * Created On  : 01/31/2024
 */
class GridListItemModel(
    /**
     * string id of the title text
     */
    var titleText: String?,
    gridMoreOptionsTile: Int,
) {
    /**
     * id of the tile item
     */
    var id = 0

    var isSelected: Boolean? = null

    /**
     * sub title text string id
     */
    var subTitleText: String? = null

    /**
     * date and time text string id
     */
    private var listDateTime: String? = null

    /**
     * if tile has image options, we can leverage this to handle it's visibility
     */
    var showTileImage: Boolean? = null
    var tileImageSrc: Int = 0

    @JvmField
    @get:TileType
    var tileType = gridMoreOptionsTile

    /**
     * Get/Set the event
     */
    var event: String? = null

    /**
     * string of the delete text of the tile
     */
    private var tileDeleteText: String? = null

    /**
     *  status of the grid tile
     *  True if Active / False if Inactive
     */
    var isActive = false

    /**
     *  status of the demo mode
     *  True if enabled / False if disabled
     */
    var isDemoActive = false

    /**
     *  status of the grid tile
     *  True if enable / False if disable
     */
    var isEnable = true

    /**
     *  size of the grid tile
     *  True if big / False if regular
     */
    var isMoreOptionPopUpLargeTile = false

    /**
     *  To know default more option grid tile eg: save as favorite, view instructions etc...
     *  True if default options / False if not
     */
    var isMoreOptionDefaultLastTile = false

    /**
     * Tile data associated with a particular tile.
     * Use this when data needs to pass along with grid tile but not necessarily needs to show to user in that tile.
     * Brief data that is associated with grid tile
     */
    var gridDetails: String? = null

    var settingsControlToggleListener: SettingsControlToggleListener? = null

    /**
     * This listener will listen the click event on tile
     */
    var transparentTileLayoutListener: NavigationButtonOnClickListener? = null

    val toggleSwitchData: ToggleSwitchData? = null

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
        GRID_TILE_WITHOUT_OPTIONS,
        GRID_SETTINGS_TILE,
        GRID_CONVECT_RECIPE_TILE,
        GRID_DRAWER_TILE,
        GRID_MORE_OPTIONS_TILE
    )
    annotation class TileType

    /**
     * Tile sub Category for example which Category
     * For ex-> Temp, Time, Favorite, Instructions, Power level
     */
    var tileSubCategory = ""

    /**
     * availability of the tile item delete mode
     *
     */
    private var isDeleteAvailable = false

    override fun toString(): String {
        return "GridListItemModel{" +
                "id=" + id +
                ", titleText='" + titleText + '\'' +
                ", subTitleText='" + subTitleText + '\'' +
                ", dateTime='" + listDateTime + '\'' +
                ", tileImageSrc=" + tileImageSrc +
                ", event='" + event + '\'' +
                ", tileDeleteText='" + tileDeleteText + '\'' +
                ", isActive=" + isActive +
                ", isLargeItem=" + isMoreOptionPopUpLargeTile +
                ", isLargeItem=" + isMoreOptionDefaultLastTile +
                ", isDeleteAvailable=" + isDeleteAvailable +
                '}'
    }

    /**
     * Class to hold the data related to list Item Radio Button view
     */
    class ToggleSwitchData {
        val visibility = View.VISIBLE
        val isChecked = false
        val isEnabled = true
    }

    companion object {
        const val GRID_TILE_WITHOUT_OPTIONS = 0 // title with title and subtitle texts
        const val GRID_SETTINGS_TILE = 1 // Tile with a title and Toggle switch for changing settings
        const val GRID_CONVECT_RECIPE_TILE = 2 // convect recipe grid tile with image and title text
        const val GRID_DRAWER_TILE = 3 //Drawerbar Tile
        const val GRID_MORE_OPTIONS_TILE = 4 // Tile with title
        const val GRID_SETTINGS_OPTIONS_TILE = 5 // Tile with title
        const val ASSISTED_RECIPE_TILE = 6 // assisted recipe grid tile with image and title text
        const val GRID_TILE_WITH_IMAGE = 7 // grid tile with image and title text
    }
}