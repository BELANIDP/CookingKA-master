package android.presenter.customviews.widgets.stringtumbler

import android.presenter.basefragments.AbstractStringTumblerFragment

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.widgets.stringtumbler
 * Brief       : This interface provide click listner for Tumbler Item
 * Author      : PATELJ7
 * Created On  : 21/03/2024
 */

interface TextStringItemClickInterface {
    fun onItemClick(index: Int, isKnobClick: Boolean = false)

    fun onItemClickVision(
        index: Int,
        recyclerViewType: AbstractStringTumblerFragment.RecyclerViewType,
        isKnobClick: Boolean = false
    ) {
        // Handle click logic based on tumblerType
    }
}