package core.viewHolderHelpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import com.whirlpool.cooking.ka.databinding.FragmentMoreOptionsPopupBinding
import com.whirlpool.hmi.uicomponents.widgets.grid.GridView
import core.jbase.abstractViewHolders.AbstractMoreOptionsPopupViewHolder

/**
 * File:core.jbase.DialogPopupScrollViewHolderHelper
 *
 * Brief: Helper class for managing the views of a scrollable dialog pop-up.
 *
 * Author: PARMAM
 *
 * Created On: 09/02/2024
 */
class MoreOptionsPopupViewHolderHelper : AbstractMoreOptionsPopupViewHolder() {
    private var moreOptionsPopupViewBinding: FragmentMoreOptionsPopupBinding? = null

    /**
     * Creates the view hierarchy for the scrollable dialog pop-up.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root view of the dialog pop-up.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        moreOptionsPopupViewBinding =
            FragmentMoreOptionsPopupBinding.inflate(inflater, container, false)
        return moreOptionsPopupViewBinding?.root
    }

    /**
     * Called when the view hierarchy previously created by onCreateView(LayoutInflater, ViewGroup, Bundle) has been detached from the fragment.
     */
    override fun onDestroyView() {
        moreOptionsPopupViewBinding = null
    }

    override fun getLayoutViewBinding(): ViewDataBinding? {
        return moreOptionsPopupViewBinding
    }

    override val moreOptionsTitleTextView: AppCompatTextView?
        get() = moreOptionsPopupViewBinding?.moreOptionsTitle
    override val closeLeftIcon: ImageView?
        get() = moreOptionsPopupViewBinding?.closeIconLeft
    override val ovenCavityRightIcon: ImageView?
        get() = moreOptionsPopupViewBinding?.ovenIconRight
    override val gridCycleOptionsView: GridView?
        get() = moreOptionsPopupViewBinding?.moreCycleOptionsRecycler

    override val gridDefaultOptionsView: GridView?
        get() = moreOptionsPopupViewBinding?.moreDefaultOptionsRecycler

    val defaultOptionsSeparatorView: View?
        get() = moreOptionsPopupViewBinding?.moreOptionsSeparator

    val parentView: ConstraintLayout?
        get() = moreOptionsPopupViewBinding?.moreParentLayout

    val insideView: ConstraintLayout?
        get() = moreOptionsPopupViewBinding?.popupWithScroll
}