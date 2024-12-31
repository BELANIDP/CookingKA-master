/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.gridview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.ItemGridListTileOptionsLayoutBinding

/**
 * File        : android.presenter.customviews.widgets.gridview.GridListTile
 * Brief       : Custom widget for displaying list cvt tiles.
 * Author      : BHIMAR
 * Created On  : 02/02/2024
 */
class GridListTile : ConstraintLayout {
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        val inflater = LayoutInflater.from(context)
        val itemGridListTileLayoutBinding =
            ItemGridListTileOptionsLayoutBinding.inflate(inflater, this, true)
        itemGridListTileLayoutBinding.root.foreground = null
    }
}