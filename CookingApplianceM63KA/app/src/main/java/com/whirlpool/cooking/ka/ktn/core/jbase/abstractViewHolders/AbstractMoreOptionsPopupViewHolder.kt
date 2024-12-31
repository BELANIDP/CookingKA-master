/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase.abstractViewHolders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ViewDataBinding
import com.whirlpool.hmi.uicomponents.widgets.grid.GridView

/**
 * File : core.jbase.AbstractDialogPopupViewHolder
 *
 * Brief :  Abstract class for managing the views of a dialog pop-up.
 *
 * Author : PARMAM
 *
 * Created On : 09/02/2024
 */
abstract class AbstractMoreOptionsPopupViewHolder {

    /**
     * Creates the view hierarchy for the dialog pop-up.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root view of the dialog pop-up.
     */
    abstract fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View?

    /**
     * Called when the view hierarchy previously created by onCreateView(LayoutInflater, ViewGroup, Bundle) has been detached from the fragment.
     */
    abstract fun onDestroyView()
    abstract fun getLayoutViewBinding(): ViewDataBinding?
    abstract val moreOptionsTitleTextView: AppCompatTextView?
    abstract val ovenCavityRightIcon: ImageView?
    abstract val closeLeftIcon: ImageView?
    abstract val gridCycleOptionsView: GridView?
    abstract val gridDefaultOptionsView: GridView?
}