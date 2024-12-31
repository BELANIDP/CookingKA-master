/*
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.gridview.viewholders

import android.media.AudioManager
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.ItemMoreOptionsTileBinding
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils.Companion.capitalizeAllChars

/**
 * Specific View holder class for the MoreOptionsPopUp.
 */
class GridMoreOptionsItemPopUpViewHolder(
    private val itemMoreOptionsTileBinding: ItemMoreOptionsTileBinding,
    private val gridItemClickListener: GridRecyclerViewInterface.GridItemMoreOptionsClickListener
) : BaseViewHolder(itemMoreOptionsTileBinding.root) {

    /**
     * Method to bind the data to the recycler view items / view holders
     *
     * @param gridListItemModel GridListItemModel Instance
     */
    override fun bind(gridListItemModel: GridListItemModel) {
        itemMoreOptionsTileBinding.modesTextView.setTextButtonText(gridListItemModel.titleText?.capitalizeAllChars())
        itemMoreOptionsTileBinding.modesTextView.setTextButtonTextSize(AppConstants.MORE_OPTIONS_POPUP_TEXT_BUTTON_TEXT_SIZE)
        itemMoreOptionsTileBinding.imageViewItemTile.setImageResource(
            gridListItemModel.tileImageSrc.takeIf{ it > 0 }
            ?:  R.drawable.more_options_default_icon)
        itemMoreOptionsTileBinding.moreOptionsTile.setBackgroundResource(
            if (gridListItemModel.isSelected == true) R.drawable.background_option_selected else
                R.color.common_transparent
        )
        if(!gridListItemModel.isEnable) {
            itemMoreOptionsTileBinding.root.isEnabled = false
            itemMoreOptionsTileBinding.root.isClickable = false
        }
    }

    override fun handleMoreOptionsClickListeners(position: Int, isMoreOptionsClicked: Boolean) {
        itemMoreOptionsTileBinding.root.setOnClickListener { v: View? ->
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
             gridItemClickListener.onListItemMoreOptionsClick(
                v,
                position,
                isMoreOptionDefaultTileClick = isMoreOptionsClicked
            )
        }
    }
}