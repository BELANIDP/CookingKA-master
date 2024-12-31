/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.gridview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.whirlpool.cooking.ka.R

/**
 * File        : android.presenter.customviews.widgets.gridview.GridListTileDecorator
 * Brief       : Custom decorator for adding margin to list tiles. Used by list cvt recycler view
 * Author      : BHIMAR
 * Created On  : 02/02/2024
 */
class GridListTileDecorator(gridListTileData: List<GridListItemModel>?) : ItemDecoration() {
    private var gridListTileData: List<GridListItemModel>? = null

    init {
        this.gridListTileData = gridListTileData
    }
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val margin =
            if (gridListTileData?.getOrNull(0)?.tileType == GridListItemModel.GRID_CONVECT_RECIPE_TILE
                || gridListTileData?.getOrNull(0)?.tileType == GridListItemModel.ASSISTED_RECIPE_TILE
            )
                parent.resources.getDimension(R.dimen.grid_list_tile_gap_margin_convect).toInt()
            else parent.resources.getDimension(R.dimen.grid_list_tile_gap_margin).toInt()
        outRect[0, 0, margin] = margin
    }
}