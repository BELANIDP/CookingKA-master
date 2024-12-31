/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.digital_unboxing

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.SettingsListViewHolderInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentUserRoleBinding
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper.Logd
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SettingsManagerUtils
import core.utils.SharedPreferenceManager.getCurrentUserRoleIntoPreference
import core.utils.SharedPreferenceManager.setCurrentUserRoleIntoPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * File        : android.presenter.fragments.digital_unboxing.UnboxingUserRoleListFragment
 * Brief       : user role either technician or product owner.
 * Author      : Nikki Gharde
 * Created On  : 3.Sep.2024
 * Details     : Instance of super abstract fragment to represent the user role List Screen.
 */
class UnBoxingUserRoleListFragment : SuperAbstractTimeoutEnableFragment(),
    SettingsListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener {

    private var isUnboxing: Boolean = false
    private var userRoleItemList: ArrayList<ListTileData>? = null
    private var userRoleList: Array<String>? = null
    private var fragmentUserRoleBinding: FragmentUserRoleBinding? = null

    private var lastItemSelectedPos = -1
    private var currentPosition = -1
    private var allItemSize = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserRoleBinding = FragmentUserRoleBinding.inflate(inflater)
        fragmentUserRoleBinding?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentUserRoleBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        init()
        setTimeoutApplicable(!isUnboxing)
        setMeatProbeApplicable(!isUnboxing)
        manageHeaderBar()
        manageRoleListRecyclerView()
        if(KnobNavigationUtils.knobForwardTrace){
            KnobNavigationUtils.knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highLightSelectedTiles()
        }
    }


    /**
     * header bar UI populate as per requirement for unboxing
     */
    private fun manageHeaderBar() {
        fragmentUserRoleBinding?.headerBar?.setTitleText(getString(R.string.text_header_welcome))
        fragmentUserRoleBinding?.headerBar?.setOvenCavityIconVisibility(false)
        fragmentUserRoleBinding?.headerBar?.setLeftIconVisibility(true)
        fragmentUserRoleBinding?.headerBar?.setRightIconVisibility(false)
        fragmentUserRoleBinding?.headerBar?.setInfoIconVisibility(false)
        fragmentUserRoleBinding?.headerBar?.setCustomOnClickListener(this)
    }

    /**
     * Init settings
     */
    private fun init() {
        isUnboxing = SettingsManagerUtils.isUnboxing
        userRoleList = resources.getStringArray(R.array.user_role)
    }

    /**
     * populate the user role list options and bind into the recycler view
     */
    private fun manageRoleListRecyclerView() {
        val listTileData: ArrayList<ListTileData> = provideListRecyclerViewTilesData()
        listTileData.let {
            allItemSize = listTileData.size
            fragmentUserRoleBinding?.userRecyclerList?.visibility = View.VISIBLE
            val listItems: ArrayList<Any> = ArrayList(listTileData)
            val toolsListViewInterface =
                SettingsListViewHolderInterface(
                    listTileData, this
                )
            fragmentUserRoleBinding?.userRecyclerList?.setupListWithObjects(
                listItems,
                toolsListViewInterface
            )
        }
    }

    /**
     * provide recycler view data to list view
     *
     * @return list of user role list
     */
    fun provideListRecyclerViewTilesData(): ArrayList<ListTileData> {
        val listViewItems = userRoleList?.size
        if (userRoleItemList != null && userRoleItemList?.isNotEmpty() == true) {
            userRoleItemList?.clear()
        } else {
            userRoleItemList = ArrayList()
        }
        return provideUserListOptions(listViewItems)
    }


    /**
     * provide user rile list with radio button
     *
     * @param listItemSize available user role list size
     * @return user list items with radio button support
     */
    @SuppressLint("DiscouragedApi")
    private fun provideUserListOptions(listItemSize: Int?): ArrayList<ListTileData> {
        if (listItemSize != null) {
            for (i in 0 until listItemSize) {
                val builder = ListTileData()
                builder.titleText = getString(
                    resources.getIdentifier(
                        AppConstants.TEXT_TILE_LIST + userRoleList!![i],
                        AppConstants.RESOURCE_TYPE_STRING, requireContext().packageName
                    )
                )
                val radioData = ListTileData.RadioButtonData()
                radioData.visibility = View.VISIBLE
                radioData.isEnabled = true
                if (i == 0 && getCurrentUserRoleIntoPreference() == AppConstants.TRUE_CONSTANT) {
                    radioData.isChecked = true
                } else if (i == 1 && getCurrentUserRoleIntoPreference() == AppConstants.FALSE_CONSTANT) {
                    radioData.isChecked = true
                } else {
                    radioData.isChecked = false
                }
                builder.radioButtonData = radioData
                builder.itemIconVisibility = View.INVISIBLE
                builder.subTextVisibility = View.GONE
                builder.rightTextVisibility = View.GONE
                builder.titleTextVisibility = View.VISIBLE
                builder.rightIconID = R.drawable.ic_rightarrowicon
                builder.rightIconVisibility = View.VISIBLE
                builder.itemViewVisibility = View.VISIBLE
                builder.isPaddingView = false
                if (i == listItemSize.minus(1)) builder.listItemDividerViewVisibility =
                    View.GONE
                userRoleItemList?.add(builder)
            }
            return userRoleItemList ?: arrayListOf()
        }
        return arrayListOf()
    }


    /**
     * based on user selection the navigate to next screen
     *
     * @param position clicked user role position
     */
    private fun handleListItemClick(position: Int, isNeedToNavigate:Boolean) {
        updateListItemOnClick(position,isNeedToNavigate)
    }

    /**
     * @param view - view
     * @param position - adapter position
     */
    override fun onListViewItemClick(view: View?, position: Int) {
        //on item list click listener
        handleListItemClick(position,isNeedToNavigate = true)
    }

    override fun onToggleSwitchClick(position: Int, isChecked: Boolean) {
        //on Radio button click listener
        handleListItemClick(position, isNeedToNavigate = false)
    }
    /**
     * Called when there is a Left Knob click event
     */
    override fun onHMILeftKnobClick() {
        knobLeftAndRightClickEvent()
    }

    /**
     * Called when there is a Long Left Knob click event
     */
    override fun onHMILongLeftKnobPress() {
    }

    /**
     * Called when there is a Right Knob click event
     */
    override fun onHMIRightKnobClick() {
        knobLeftAndRightClickEvent()
    }

    /**
     * Called when there is a Long Right Knob click event
     */
    override fun onHMILongRightKnobPress() {
    }

    /**
     * Called when there is a Long Right Knob click event
     */
    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    /**
     * Called when there is a knob rotate event on a Knobs
     * @param knobId  knob ID
     * @param knobDirection knob movement direction
     */
    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentPosition = CookingAppUtils.getKnobPositionIndex(
                    knobDirection,
                    currentPosition,
                    allItemSize
                )
                if (currentPosition >= 0) {
                    Logd(
                        "Unboxing",
                        "LEFT_KNOB: rotate right current knob index = $currentPosition"
                    )
                    fragmentUserRoleBinding?.userRecyclerList?.smoothScrollToPosition(
                        currentPosition
                    )
                    highLightSelectedTiles()

                } else {
                    Logd(
                        "Unboxing",
                        "LEFT_KNOB: rotate left current knob index = $currentPosition"
                    )
                    currentPosition = 0
                    highLightSelectedTiles()
                }
            }
        }
    }

    /**
     * Called after 10 sec when there is no interaction on knob
     */
    override fun onKnobSelectionTimeout(knobId: Int) {
        if (lastItemSelectedPos != -1) {
            val viewHolder =
                fragmentUserRoleBinding?.userRecyclerList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )
            viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
        }
        lastItemSelectedPos = -1
    }

    /**
     * highlight the item row during knob interaction
     */
    private fun highLightSelectedTiles() {
        fragmentUserRoleBinding?.userRecyclerList?.postDelayed({
            if (lastItemSelectedPos != -1) {
                val viewHolder =
                    fragmentUserRoleBinding?.userRecyclerList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = currentPosition
            val viewHolderOld =
                fragmentUserRoleBinding?.userRecyclerList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }, 50) // Adjust delay as needed
    }

    /**
     * common function for knob left and right click event
     */
    private fun knobLeftAndRightClickEvent() {
        if (lastItemSelectedPos != -1) {
            lifecycleScope.launch(Dispatchers.Main) {
                KnobNavigationUtils.knobForwardTrace = true
                fragmentUserRoleBinding?.userRecyclerList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )?.itemView?.findViewById<ConstraintLayout>(R.id.list_item_main_view)
                    ?.callOnClick()

            }
        }
    }

    /**
     * Method to update the user checked choices to reflect in the radio button in menu list
     *
     * @param position position of the tile in the list
     */
    private fun updateListItemOnClick(position: Int,isNeedToNavigate:Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            userRoleItemList?.let {
                val selectedItem: ListTileData = it[position]
                for (i in 0 until it.size) {
                    val listItemModel: ListTileData = it[i]
                    val radioData = listItemModel.radioButtonData
                    radioData.visibility = View.VISIBLE
                    radioData.isEnabled = true
                    radioData.isChecked = selectedItem == listItemModel
                }
            }
            withContext(Dispatchers.Main){
                notifyDataSetChanged()
                    when (position) {
                        0 -> {
                            setCurrentUserRoleIntoPreference(AppConstants.TRUE_CONSTANT)
                            if (isNeedToNavigate) {
                                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)
                                    ?.let {
                                        NavigationUtils.navigateSafely(
                                            it,
                                            R.id.action_unboxingUserRoleListFragment_to_unboxingTechnicianExitModeFragment,
                                            null,
                                            null
                                        )
                                    }
                            }
                        }
                        else -> {
                            setCurrentUserRoleIntoPreference(AppConstants.FALSE_CONSTANT)
                            if (isNeedToNavigate) {
                                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)
                                    ?.let {
                                        NavigationUtils.navigateSafely(
                                            it,
                                            R.id.action_unboxingUserRoleListFragment_to_unboxingApplianceFeaturesInfoFragment,
                                            null,
                                            null
                                        )
                                    }
                            }
                    }
                }
            }
        }


    }

    /**
     * Method to notify the item changed for recycler view adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun notifyDataSetChanged() {
        if (fragmentUserRoleBinding?.userRecyclerList?.adapter != null) {
            fragmentUserRoleBinding?.userRecyclerList?.adapter?.notifyDataSetChanged()
        }
    }

    override fun leftIconOnClick() {
        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)
            ?.let {
                NavigationUtils.navigateSafely(
                    it,
                    R.id.action_unboxingUserRoleListFragment_to_fragment_language,
                    null,
                    null
                )
            }
    }
    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        fragmentUserRoleBinding = null
        allItemSize = 0
        super.onDestroyView()
    }
}