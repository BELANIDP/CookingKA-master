/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.basefragments

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridListTileDecorator
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentGridListBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils.Companion.knobForwardTrace
import core.utils.NavigationUtils

/**
 * File        : android.presenter.basefragments.AbstractGridListFragment
 * Brief       : Abstract fragment to extend for the use case implementations
 * Author      : BHIMAR
 * Created On  : 02/05/2024
 * Details     : For creating a instance of [com.whirlpool.cooking.ka.R.layout.fragment_grid_list], extend this class and
 * implement the abstract methods. This class does the actions that are common to all the
 * variants and provides interfaces that the extended classes shall implement
 */
abstract class AbstractGridListFragment : SuperAbstractTimeoutEnableFragment(),
    GridRecyclerViewInterface.GridItemClickListener, HMIKnobInteractionListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface {
    private var fragmentListBinding: FragmentGridListBinding? = null
    private var currentPosition = -1
    private var lastItemSelectedPos = -1
    private lateinit var gridListItems: List<GridListItemModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentListBinding = FragmentGridListBinding.inflate(inflater)
        fragmentListBinding?.lifecycleOwner = this
        fragmentListBinding?.gridListKtnFragment = this
        fragmentListBinding?.recyclerViewGridList?.layoutAnimation =
            android.view.animation.AnimationUtils.loadLayoutAnimation(
                requireContext(),
                R.anim.layout_animation_slide_from_bottom
            )
        return fragmentListBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModels()
        manageChildViews()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        if (knobForwardTrace) {
            knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            gridListItems[lastItemSelectedPos].isSelected = true
            notifyDataSetChanged()
        }
    }

    override fun onDestroyView() {
        clearMemory()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        super.onDestroyView()
    }

    private fun clearMemory() {
        fragmentListBinding = null
    }

    /**
     * Method for handling the child views.
     */
    protected fun manageChildViews() {
        manageHeaderBar()
    }

    /**
     * to provide the recyclerview object to subclass.
     */
    protected val recyclerView: RecyclerView
        get() = fragmentListBinding?.recyclerViewGridList as RecyclerView

    /**
     * Method to setup Recycler View
     */
    protected fun manageListRecyclerView() {
        fragmentListBinding?.recyclerViewGridList?.isVerticalScrollBarEnabled = false
        fragmentListBinding?.recyclerViewGridList?.isHorizontalScrollBarEnabled = true
        gridListItems = provideListRecyclerViewTilesData()
        val listItems = ArrayList<Any>(gridListItems)
        fragmentListBinding?.recyclerViewGridList?.addItemDecoration(GridListTileDecorator(gridListItems))
        val gridRecyclerViewInterface = GridRecyclerViewInterface(gridListItems, this)
        fragmentListBinding?.recyclerViewGridList?.setupGridWithObjects(
            listItems,
            gridRecyclerViewInterface
        )
        fragmentListBinding?.recyclerViewGridList?.visibility
            ?: if (provideListRecyclerViewTilesData().isEmpty()) View.INVISIBLE else View.VISIBLE
        setScrollBarVisibility()
        fragmentListBinding?.recyclerViewGridList?.isVerticalScrollBarEnabled = true
    }

    /**
     * This function will set the Visibility of the Scroll Bar based on the list item count
     */
    private fun setScrollBarVisibility() {
        fragmentListBinding?.recyclerViewGridList?.isVerticalScrollBarEnabled =
            provideListRecyclerViewSize() >= resources.getInteger(R.integer.min_scrolling_list_size_grid_list)
    }

    protected open fun manageKnobRotation(knobId: Int, knobDirectionEvent: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            currentPosition = CookingAppUtils.getKnobPositionIndex(
                knobDirectionEvent,
                currentPosition,
                provideListRecyclerViewSize()
            )
        }
        if (currentPosition >= 0) {
            gridListItems.forEachIndexed { index, gridListItemModel ->
                if (index == currentPosition) {
                    gridListItemModel.isSelected = true
                    fragmentListBinding?.recyclerViewGridList?.scrollToPosition(index)
                } else {
                    gridListItemModel.isSelected = false
                }
            }
            notifyDataSetChanged()
        } else {
            HMILogHelper.Logd("Invalid knob rotation, Current position is 0 ")
            currentPosition = 0
        }
    }

    /**
     * Abstract Method to get the array of tile data of the list recycler view , it varies based on
     * screen
     */
    protected abstract fun provideListRecyclerViewTilesData(): ArrayList<GridListItemModel>

    /**
     * Abstract Method to get the size of recycler view tile data array, it varies based on screen
     */
    protected abstract fun provideListRecyclerViewSize(): Int

    /**
     * Method to register to the live data and observe
     */
    protected abstract fun observeViewModels()

    /**
     * Method to notify the list recycler view adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    protected fun notifyDataSetChanged() {
        fragmentListBinding?.recyclerViewGridList?.adapter?.notifyDataSetChanged()
    }

    /**
     * Method to set the number of columns of the GridView.
     *
     * @param numberOfColumns number of spans(column if orientation is vertical, row if orientation is horizontal)
     */
    @Suppress("SameParameterValue")
    protected open fun setNumberOfColumns(numberOfColumns: Int) {
        val gridLayoutManager =
            fragmentListBinding?.recyclerViewGridList?.layoutManager as GridLayoutManager?
        if (gridLayoutManager != null) gridLayoutManager.spanCount = numberOfColumns
    }

    private fun manageHeaderBar() {
        fragmentListBinding?.headerBarGridList?.setRightIconVisibility(false)
        fragmentListBinding?.headerBarGridList?.setLeftIcon(R.drawable.ic_back_arrow)
        fragmentListBinding?.headerBarGridList?.setTitleText(provideHeaderBarTitle())
        fragmentListBinding?.headerBarGridList?.setOvenCavityIconVisibility(false)
        if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO || CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
            if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) {
                fragmentListBinding?.headerBarGridList?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
            } else {
                fragmentListBinding?.headerBarGridList
                    ?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
            }
        }
        fragmentListBinding?.headerBarGridList?.setInfoIconVisibility(false)
        fragmentListBinding?.headerBarGridList?.setCustomOnClickListener(this)
    }

    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(
                    this
                ) ?: requireView()
            )
        )
    }

    /**
     * method to provide the title
     * @return title of a child fragment
     */
    abstract fun provideHeaderBarTitle(): String

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    /************************************ Knob related Methods Only **************************************/
    override fun onHMILeftKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMIRightKnobClick() {
        if (currentPosition >= 0) {
            knobForwardTrace = true
            onListItemClick(
                fragmentListBinding?.recyclerViewGridList as View,
                currentPosition,
                true
            )
        }
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            manageKnobRotation(knobId, knobDirection)
        }
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            currentPosition = -1
            gridListItems.forEach {
                it.isSelected = false
            }
            notifyDataSetChanged()
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}