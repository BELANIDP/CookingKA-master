/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.gridview.viewholders

import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.ItemGridlistSettingsTileLayoutBinding
import core.utils.changeTextColor

/**
 * File        : android.presenter.customviews.widgets.gridview.viewholders.GridItemSettingsViewHolder
 * Brief       : ViewHolder class for Settings widget layout. This view holder contains settings control with name and toggle switch.
 * Author      : BHIMAR
 * Created On  : 02/27/2024
 */
class GridItemSettingsViewHolder(
    private val itemGridListTileLayoutBinding: ItemGridlistSettingsTileLayoutBinding,
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
        itemGridListTileLayoutBinding.settingsItemTitleTextView.text = gridListItemModel.titleText
        when (gridListItemModel.id) {
            SETTINGS_CONTROL_LOCK_TILE_ID -> {
                itemGridListTileLayoutBinding.settingsItemImageView.setBackgroundResource(R.drawable.ic_40px_unlocked)
            }

            SETTINGS_MUTE_TILE_ID -> {
                if (gridListItemModel.isActive) {
                    itemGridListTileLayoutBinding.settingsItemImageView.setBackgroundResource(R.drawable.ic_40px_sound_mute)
                } else {
                    itemGridListTileLayoutBinding.settingsItemImageView.setBackgroundResource(R.drawable.ic_40px_sound_active)
                }
            }

            SETTINGS_HISTORY_TILE_ID -> {
                itemGridListTileLayoutBinding.settingsItemImageView.setBackgroundResource(R.drawable.ic_40px_time)
            }
        }

        if (gridListItemModel.isDemoActive && gridListItemModel.id == SETTINGS_CONTROL_LOCK_TILE_ID) {
            val titleTextView = itemGridListTileLayoutBinding.settingsItemTitleTextView
            titleTextView.apply {
                changeTextColor(itemGridListTileLayoutBinding.root.context, R.color.light_grey)
                isClickable = true
            }
        }
    }

    override fun handleClickListeners(position: Int) {
        itemGridListTileLayoutBinding.root.setOnClickListener { v: View? ->
            gridItemClickListener.onListItemClick(
                v,
                position
            )
            gridItemClickListener.onListItemImageClick(
                v,
                position
            )
        }
    }

    companion object {
        /*Settings widget*/
        const val SETTINGS_CONTROL_LOCK_TILE_ID = 1
        const val SETTINGS_MUTE_TILE_ID = 2
        const val SETTINGS_HISTORY_TILE_ID = 3
    }
}