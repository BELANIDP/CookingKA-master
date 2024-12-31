package android.presenter.customviews.widgets.gridview.viewholders

import android.media.AudioManager
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.ItemGridlistTileWithImageBinding
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AudioManagerUtils

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.widgets.gridview.viewholders.GridItemWithImageOptionViewHolder
 * Brief       : ViewHolder class for Item with image for example(SeeVideos in demo mode). This view holder contains an image and textview for convect option.
 * Author      : Karthikeyan D s
 * Created On  : 05/12/2024
 */
class GridItemWithImageOptionViewHolder(
    private val itemGridListWithImageBinding: ItemGridlistTileWithImageBinding,
    private val gridItemClickListener: GridRecyclerViewInterface.GridItemClickListener
) : BaseViewHolder(
    itemGridListWithImageBinding.root
) {

    /**
     * Method to bind the data to the recycler view items / view holders
     *
     * @param gridListItemModel GridListItemModel Instance
     */
    override fun bind(gridListItemModel: GridListItemModel) {
        itemGridListWithImageBinding.textViewItemName.text = gridListItemModel.titleText
        val context = itemGridListWithImageBinding.layoutGridItem.context

        itemGridListWithImageBinding.imageViewItemTile.visibility = when {
            gridListItemModel.showTileImage == true && gridListItemModel.tileImageSrc > 0 -> {
                itemGridListWithImageBinding.imageViewItemTile.setImageResource(gridListItemModel.tileImageSrc)
                View.VISIBLE
            }
            else -> View.GONE
        }

        if (gridListItemModel.isSelected == true)
            itemGridListWithImageBinding.layoutGridItem.setBackgroundColor(
                context.getColor(
                    R.color.knob_selected_grey
                )
            )
        else
            itemGridListWithImageBinding.layoutGridItem.setBackgroundColor(
                context.getColor(
                    R.color.very_dark_grey
                )
            )
    }

    /**
     * Method to handle clicks on the item of the recyclerview
     * @param position position of the item clicked
     */
    override fun handleClickListeners(position: Int) {
        itemGridListWithImageBinding.root.setOnClickListener { v: View? ->
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            gridItemClickListener.onListItemClick(
                v,
                position
            )
        }
    }
}