package android.presenter.fragments.settings

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.SettingsListViewHolderInterface
import android.presenter.customviews.radiobutton.RadioButton
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentLanguageBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.utils.cookbook.records.FavoriteRecord
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SharedPreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * File        : android.presenter.fragments.settings.AssignKnobFavoritesListFragment
 * Brief       : select the fav.
 * Author      : Rajendra
 * Created On  : 14/OCT/2025
 * Details     : Instance of Abstract List fragment to represent the FAV. List Screen.
 */
class AssignKnobFavoritesListFragment : SuperAbstractTimeoutEnableFragment(),
    SettingsListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener {
    private var savedFavoritesList: ArrayList<ListTileData>? = null
    private var favList: List<FavoriteRecord>? = null
    private var fragmentFavoritesListBinding: FragmentLanguageBinding? = null
    private var lastItemSelectedPos = -1
    private var currentPosition = -1
    private var allItemSize = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFavoritesListBinding = FragmentLanguageBinding.inflate(inflater)
        fragmentFavoritesListBinding?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentFavoritesListBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        init()
        setTimeoutApplicable(true)
        manageHeaderBar()
        manageFavoritesListRecyclerView()
        if(KnobNavigationUtils.knobForwardTrace){
            KnobNavigationUtils.knobForwardTrace = false
            updateRadioButtonBackgroundOnLaunch()
        }
    }


    /**
     * Method to apply selected background color on Knob Navigation on initial launch only
     */
    private fun updateRadioButtonBackgroundOnLaunch() {
        fragmentFavoritesListBinding?.languageRecyclerList?.postDelayed({
            val itemCount = fragmentFavoritesListBinding?.languageRecyclerList?.adapter?.itemCount ?: 0
            var anyChecked = false
            // Loop through all items and update `RadioButton` states
            HMILogHelper.Logd("Item count: $itemCount")
            for (position in 0 until itemCount) {
                // Update the RadioButton background based on whether it's checked
                HMILogHelper.Logd("Updating RadioButton at position: $position")
                val viewHolder =
                    fragmentFavoritesListBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(position)
                val radioButton =
                    viewHolder?.itemView?.findViewById<RadioButton>(R.id.list_item_radio_button)
                if (radioButton?.isVisible == true) {
                    radioButton.let {
                        val radioButtonColor = if (it.isChecked()) {
                            HMILogHelper.Logd("RadioButton at position $position is selected")
                            anyChecked = true
                            R.color.cavity_selected_button_background
                        } else {
                            HMILogHelper.Logd("RadioButton at position $position is unselected")
                            R.color.color_black
                        }
                        if (radioButtonColor == R.color.cavity_selected_button_background) {
                            lastItemSelectedPos = position
                            currentPosition = position
                        }
                        updateTileBackground(position, radioButtonColor)
                    }
                }
            }
            // If no radio button was selected, highlight the first item
            if (!anyChecked && itemCount > 0) {
                val firstPosition = 0
                HMILogHelper.Logd("No RadioButton was selected, defaulting to the first item at position: $firstPosition")
                lastItemSelectedPos = firstPosition
                currentPosition = firstPosition
                updateTileBackground(firstPosition, R.color.cavity_selected_button_background)
            }
        }, 50)
    }

    private fun updateTileBackground(
        position: Int,
        backgroundColor: Int
    ) {
        val viewHolder =
            fragmentFavoritesListBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
                position
            )
        viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(backgroundColor))
    }

    /**
     * header bar UI populate as per requirement for unboxing
     */
    private fun manageHeaderBar() {
        fragmentFavoritesListBinding?.headerBar?.apply {
            setTitleText(getString(R.string.text_see_video_favorites))
            setOvenCavityIconVisibility(false)
            setLeftIconVisibility(true)
            setLeftIcon(R.drawable.ic_back_arrow)
            setRightIconVisibility(true)
            setRightIconVisibility(true)
            setInfoIconVisibility(false)
        }
        fragmentFavoritesListBinding?.headerBar?.setCustomOnClickListener(this)
    }

    override fun leftIconOnClick() {
        KnobNavigationUtils.setBackPress()
        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
            NavigationUtils.navigateSafely(
                it,
                R.id.action_assignKnobFavoritesListFragment_to_settingsAssignFavoriteToKnobFragment,
                null,
                null
            )
        }
    }

    override fun rightIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
    }

    /**
     * Init fav list
     */
    private fun init() {
        favList =
            CookBookViewModel.getInstance().allFavoriteRecords.value?.filterNot { favoriteRecord ->
                    CookingAppUtils.isProbeRequiredForRecipe(
                        favoriteRecord?.recipeName?: "", favoriteRecord?.cavity ?: ""
                    )
                } ?: emptyList() // Handle null case with emptyList()
    }

    /**
     * populate the language list options and bind into the recycler view
     */
    private fun manageFavoritesListRecyclerView() {
        val listTileData: ArrayList<ListTileData> = provideListRecyclerViewTilesData()
        listTileData.let {
            allItemSize = listTileData.size
            fragmentFavoritesListBinding?.languageRecyclerList?.visibility = View.VISIBLE
            val listItems: ArrayList<Any> = ArrayList(listTileData)
            val toolsListViewInterface =
                SettingsListViewHolderInterface(
                    listTileData, this
                )
            fragmentFavoritesListBinding?.languageRecyclerList?.setupListWithObjects(
                listItems,
                toolsListViewInterface
            )
        }
    }

    /**
     * provide recycler view data to list view
     *
     * @return fav. list
     */
    fun provideListRecyclerViewTilesData(): ArrayList<ListTileData> {
        val listViewItems = favList?.size?:0
        if (listViewItems == resources.getInteger(R.integer.integer_range_0)) {
            fragmentFavoritesListBinding?.favEmpty?.visibility = View.VISIBLE
        } else {
            fragmentFavoritesListBinding?.favEmpty?.visibility = View.GONE
        }
        if (savedFavoritesList != null && savedFavoritesList?.isNotEmpty() == true) {
            savedFavoritesList?.clear()
        } else {
            savedFavoritesList = ArrayList()
        }
        return provideFavoritesListOptions(listViewItems)
    }


    /**
     * provide favorites list
     *
     * @param favoritesListItemSize available favorites list size
     * @return favorites list items with radio button support
     */
    private fun provideFavoritesListOptions(favoritesListItemSize: Int?): ArrayList<ListTileData> {
        if (favoritesListItemSize != null) {
            for (i in 0 until favoritesListItemSize) {
                val favoritesListBuilder = ListTileData()
                favoritesListBuilder.titleText = favList?.get(i)?.favoriteName.toString()
                val radioData = ListTileData.RadioButtonData()
                radioData.apply {
                        visibility = View.VISIBLE
                        isEnabled = true
                        isChecked =
                            favList?.get(i)?.favoriteName.toString() ==
                                    SharedPreferenceManager.getKnobAssignFavoritesCycleNameIntoPreference().toString()
                }
                favoritesListBuilder.apply {
                    radioButtonData = radioData
                    itemIconVisibility = View.VISIBLE
                    subTextVisibility = View.GONE
                    rightTextVisibility = View.GONE
                    titleTextVisibility = View.VISIBLE
                    rightIconVisibility = View.GONE
                    itemViewVisibility = View.VISIBLE
                    isPaddingView = false
                    if (i == favoritesListItemSize.minus(1)) listItemDividerViewVisibility =
                        View.GONE
                }
                savedFavoritesList?.add(favoritesListBuilder)
            }
            return savedFavoritesList ?: arrayListOf()
        }
        return arrayListOf()
    }

    /**
     * favorites selection
     *
     * @param position clicked favorites position
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun handleFavoritesItemClick(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            savedFavoritesList?.let {
                val selectedItem: ListTileData = it[position]
                for (i in 0 until it.size) {
                    val listItemModel: ListTileData = it[i]
                    val radioData = listItemModel.radioButtonData
                    radioData.visibility = View.VISIBLE
                    radioData.isEnabled = true
                    radioData.isChecked = selectedItem == listItemModel
                }
            }
            withContext(Dispatchers.Main) {
                if (fragmentFavoritesListBinding?.languageRecyclerList?.adapter != null) {
                    fragmentFavoritesListBinding?.languageRecyclerList?.adapter?.notifyDataSetChanged()
                }
                if (favList != null && (favList?.size ?: 0) > 0) {
                    HMILogHelper.Logd("Assign favorites", "Navigate to the assign fav screen")
                    SharedPreferenceManager.setKnobAssignFavoritesCycleStatusIntoPreference(
                        AppConstants.TRUE_CONSTANT
                    )
                    savedFavoritesList?.get(position)?.titleText?.let {
                        SharedPreferenceManager.setKnobAssignFavoritesCycleNameIntoPreference(
                            it
                        )
                    }
                    CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                        NavigationUtils.navigateSafely(
                            it,
                            R.id.action_assignKnobFavoritesListFragment_to_settingsAssignFavoriteToKnobFragment,
                            null,
                            null
                        )
                    }
                } else {
                    HMILogHelper.Loge("favorites list not available in app")
                }
            }
        }
    }

    /**
     * @param view - view
     * @param position - adapter position
     */
    override fun onListViewItemClick(view: View?, position: Int) {
        handleFavoritesItemClick(position)
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
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    currentPosition = CookingAppUtils.getKnobPositionIndex(
                        knobDirection,
                        currentPosition,
                        allItemSize
                    )
                    if (currentPosition >= 0) {
                        HMILogHelper.Logd(
                            "Unboxing",
                            "LEFT_KNOB: rotate right current knob index = $currentPosition"
                        )
                        fragmentFavoritesListBinding?.languageRecyclerList?.smoothScrollToPosition(
                            currentPosition
                        )
                        highLightSelectedTiles()

                    } else {
                        HMILogHelper.Logd(
                            "Unboxing",
                            "LEFT_KNOB: rotate left current knob index = $currentPosition"
                        )
                        currentPosition = 0
                        highLightSelectedTiles()
                    }
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
                fragmentFavoritesListBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
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
        fragmentFavoritesListBinding?.languageRecyclerList?.postDelayed({
            if (lastItemSelectedPos != -1)
                updateTileBackground(lastItemSelectedPos, R.color.color_black)
            lastItemSelectedPos = currentPosition
            updateTileBackground(
                lastItemSelectedPos,
                R.color.cavity_selected_button_background
            )
        }, 50) // Adjust delay as needed
    }

    /**
     * common function for knob left and right click event
     */
    private fun knobLeftAndRightClickEvent() {
        if (lastItemSelectedPos != -1) {
            KnobNavigationUtils.knobBackTrace = true
            lifecycleScope.launch(Dispatchers.Main) {
                fragmentFavoritesListBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )?.itemView?.findViewById<ConstraintLayout>(R.id.list_item_main_view)
                    ?.callOnClick()
            }
        }
    }

    override fun onToggleSwitchClick(position: Int, isChecked: Boolean) {
        handleFavoritesItemClick(position)
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        savedFavoritesList = null
        favList = null
        fragmentFavoritesListBinding = null
        allItemSize = 0
        super.onDestroyView()
    }
}