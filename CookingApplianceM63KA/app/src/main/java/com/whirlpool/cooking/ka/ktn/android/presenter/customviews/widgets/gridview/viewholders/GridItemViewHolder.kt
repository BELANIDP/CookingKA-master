/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.gridview.viewholders

import android.view.View
import com.whirlpool.cooking.ka.databinding.ItemGridListTileOptionsLayoutBinding
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface

/**
 * File        : android.presenter.customviews.widgets.gridview.viewholders.GridItemViewHolder
 * Brief       : ViewHolder class for default gridview item layout. This view holder contains Title and subtitle textviews.
 * Author      : BHIMAR
 * Created On  : 02/27/2024
 */
class GridItemViewHolder(
    private val itemGridListTileLayoutBinding: ItemGridListTileOptionsLayoutBinding,
    private val gridItemClickListener: GridRecyclerViewInterface.GridItemClickListener
) : BaseViewHolder(
    itemGridListTileLayoutBinding.root
) {
    /**
     * Method to bind the data to the recycler view items / view holders
     *
     * @param gridListItemModel GridListItemModel Instance
     */
    override fun bind(gridListItemModel: GridListItemModel) {
        itemGridListTileLayoutBinding.listTileMainText.text = gridListItemModel.titleText
        itemGridListTileLayoutBinding.listTileSubText.text = gridListItemModel.subTitleText
    }

    override fun handleClickListeners(position: Int) {
        itemGridListTileLayoutBinding.root.setOnClickListener { v: View? ->
            gridItemClickListener.onListItemClick(
                v,
                position
            )
        }
    }
}