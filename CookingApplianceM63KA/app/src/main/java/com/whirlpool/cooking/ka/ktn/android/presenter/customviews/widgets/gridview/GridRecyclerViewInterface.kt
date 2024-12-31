/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.customviews.widgets.gridview

import android.presenter.customviews.widgets.gridview.viewholders.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.hmi.uicomponents.widgets.grid.GridViewInterface
import com.whirlpool.cooking.ka.databinding.*


/**
 * File        : android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
 * Brief       : interface to manage the grid list view in the grid list fragment
 * Author      : BHIMAR
 * Created On  : 01/19/2024
 */
class GridRecyclerViewInterface(
    private val gridListTileData: List<GridListItemModel>?,
    private val itemClickListener: GridItemClickListener? = null,
    private val itemMoreOptionsClickListener: GridItemMoreOptionsClickListener? = null
) : GridViewInterface {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            GridListItemModel.GRID_SETTINGS_TILE -> GridItemSettingsViewHolder(
                ItemGridlistSettingsTileLayoutBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                itemClickListener!!
            )
            GridListItemModel.GRID_SETTINGS_OPTIONS_TILE -> SettingsGridItemSettingsViewHolder(
                ItemGridlistSettingsTileLayoutBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                itemClickListener!!
            )

            GridListItemModel.GRID_TILE_WITHOUT_OPTIONS -> GridItemViewHolder(
                ItemGridListTileOptionsLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                itemClickListener!!
            )

            GridListItemModel.GRID_CONVECT_RECIPE_TILE -> GridItemConvectOptionViewHolder(
                ItemGridlistConvectTileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                itemClickListener!!
            )

            GridListItemModel.ASSISTED_RECIPE_TILE -> GridItemConvectOptionViewHolder(
                ItemGridlistConvectTileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                itemClickListener!!,
                isFromAssisted = true
            )

            GridListItemModel.GRID_TILE_WITH_IMAGE -> GridItemWithImageOptionViewHolder(
                ItemGridlistTileWithImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                ),
                itemClickListener!!
            )

            GridListItemModel.GRID_DRAWER_TILE -> DrawerViewHolder(
                ItemDrawerMenuBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                itemClickListener!!
            )

            GridListItemModel.GRID_MORE_OPTIONS_TILE -> GridMoreOptionsItemPopUpViewHolder(
                ItemMoreOptionsTileBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                itemMoreOptionsClickListener!!
            )

            else -> GridItemViewHolder(
                ItemGridListTileOptionsLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ), itemClickListener!!
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, any: Any) {
        val gridItemViewHolder = holder as BaseViewHolder
        val position = gridItemViewHolder.bindingAdapterPosition
        if (!gridListTileData.isNullOrEmpty()) {
            gridItemViewHolder.bind(gridListTileData[position])
        }
        if(holder.itemViewType == GridListItemModel.GRID_MORE_OPTIONS_TILE)
            gridListTileData?.get(position)
                .let {
                    if (it != null) {
                        gridItemViewHolder.handleMoreOptionsClickListeners(position, it.isMoreOptionDefaultLastTile)
                    }
                }
        else gridItemViewHolder.handleClickListeners(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (!gridListTileData.isNullOrEmpty()) {
            gridListTileData[position].tileType
        } else -1
    }

    override fun selectionUpdated(index: Int) {
        //Empty Override
    }

    override fun getSelectedObject(): String {
        return ""
    }

    /**
     * interface to provide callbacks of grid tile/item click events
     */
    interface GridItemClickListener{
        fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean = false)
        fun onListItemDeleteClick(view: View?, position: Int)
        fun onListItemImageClick(view: View?, position: Int)
    }

    /**
     * Interface to handle the More Options item click listener
     */
    interface GridItemMoreOptionsClickListener : GridItemClickListener {
        fun onListItemMoreOptionsClick(view: View?, position: Int, isFromKnob: Boolean = false, isMoreOptionDefaultTileClick: Boolean = false)
    }
}