/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */

package android.presenter.customviews.widgets.gridview.viewholders

import android.media.AudioManager
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.ItemGridlistConvectTileBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants.ASSISTED_IMAGE_WIDTH_240PX
import core.utils.AppConstants.ASSISTED_TITLE_START_MARGIN_16PX
import core.utils.AppConstants.ASSISTED_TITLE_TEXT_SIZE_36PX
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.widgets.gridview.viewholders.GridItemConvectOptionViewHolder
 * Brief       : ViewHolder class for Convect recipe options. This view holder contains an image and textview for convect option.
 * Author      : BHIMAR
 * Created On  : 18/03/2024
 */
class GridItemConvectOptionViewHolder(
    private val itemGridListConvectTileBinding: ItemGridlistConvectTileBinding,
    private val gridItemClickListener: GridRecyclerViewInterface.GridItemClickListener,
    private val isFromAssisted : Boolean = false
) : BaseViewHolder(
    itemGridListConvectTileBinding.root
) {

    /**
     * Method to bind the data to the recycler view items / view holders
     *
     * @param gridListItemModel GridListItemModel Instance
     */
    override fun bind(gridListItemModel: GridListItemModel) {
        itemGridListConvectTileBinding.textViewConvectOptionName.text = gridListItemModel.titleText
        val context = itemGridListConvectTileBinding.layoutGridItem.context

        itemGridListConvectTileBinding.imageViewConvectRecipe.isVisible =
            CookingAppUtils.isRecipeAssisted(
                gridListItemModel.gridDetails,
                CookingViewModelFactory.getInScopeViewModel().cavityName.value
            ) || CookingAppUtils.isRecipeAssistedOnRootNode(
                gridListItemModel.gridDetails,
                CookingViewModelFactory.getInScopeViewModel().cavityName.value
            )

        gridListItemModel.tileImageSrc.takeIf { it > 0 }
            ?.let { itemGridListConvectTileBinding.imageViewConvectRecipe.setImageResource(it) }

        if (gridListItemModel.isSelected == true)
            itemGridListConvectTileBinding.layoutGridItem.setBackgroundColor(
                context.getColor(
                    R.color.knob_selected_grey
                )
            )
        else
            itemGridListConvectTileBinding.layoutGridItem.setBackgroundColor(
                context.getColor(
                    R.color.very_dark_grey
                )
            )
        if (isFromAssisted){
            val paramsImageView = itemGridListConvectTileBinding.imageViewConvectRecipe.layoutParams
            val increasedWidthInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                ASSISTED_IMAGE_WIDTH_240PX.toFloat(),
                context.resources.displayMetrics
            ).toInt()
            paramsImageView.width = increasedWidthInPx
            itemGridListConvectTileBinding.imageViewConvectRecipe.layoutParams = paramsImageView

            val paramsParentLayout = itemGridListConvectTileBinding.layoutGridItem.layoutParams
            paramsParentLayout.width = LinearLayout.LayoutParams.MATCH_PARENT
            itemGridListConvectTileBinding.layoutGridItem.layoutParams = paramsParentLayout

            itemGridListConvectTileBinding.textViewConvectOptionName.textSize = ASSISTED_TITLE_TEXT_SIZE_36PX

            val paramsTextView = itemGridListConvectTileBinding.textViewConvectOptionName.layoutParams as ConstraintLayout.LayoutParams
            val increasedMarginInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                ASSISTED_TITLE_START_MARGIN_16PX.toFloat(),
                context.resources.displayMetrics
            ).toInt()
            paramsTextView.apply {
                marginStart = increasedMarginInPx
                width = ConstraintLayout.LayoutParams.WRAP_CONTENT
            }
            itemGridListConvectTileBinding.textViewConvectOptionName.layoutParams = paramsTextView
        }
    }

    /**
     * Method to handle clicks on the item of the recyclerview
     * @param position position of the item clicked
     */
    override fun handleClickListeners(position: Int) {
        itemGridListConvectTileBinding.root.setOnClickListener { v: View? ->
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