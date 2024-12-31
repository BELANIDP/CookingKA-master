/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.gridview.viewholders

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import android.presenter.customviews.widgets.gridview.GridListItemModel

/**
 * File        : android.presenter.customviews.widgets.gridview.viewholders.BaseViewHolder
 * Brief       : Base view holder for other concrete implementations of GridView widget
 * Author      : BHIMAR
 * Created On  : 02/27/2024
 */
abstract class BaseViewHolder protected constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    open fun bind(gridListItemModel: GridListItemModel) {
        val overlay = itemView.overlay
        if (gridListItemModel.isActive) {
            overlay.clear()
        } else {
            overlay.clear() //To avoid issue like multiple overlay while scrolling
            val layoutParams = itemView.layoutParams
            val dimDrawable: Drawable =
                ColorDrawable(itemView.context.getColor(R.color.culinary_center_tile_overlay))
            dimDrawable.setBounds(0, 0, layoutParams.width, layoutParams.height)
            overlay.add(dimDrawable)
        }
    }

    open fun handleClickListeners(position: Int){
        //Defined in the respective derived classes
    }

    open fun handleMoreOptionsClickListeners(position: Int, isMoreOptionsClicked: Boolean = false) {
        //Defined in the respective derived classes
    }
}