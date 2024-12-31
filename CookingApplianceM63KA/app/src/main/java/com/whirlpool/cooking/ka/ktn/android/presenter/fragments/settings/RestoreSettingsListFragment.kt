package android.presenter.fragments.settings

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.SettingsListViewHolderInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.toggleswitch.ToggleSwitch
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentToolsListMenuBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * File        : android.presenter.fragments.settings.RestoreSettingsListFragment
 * Brief       : Instance of Abstract List fragment to represent the Restore Settings Menu List Screen
 * Author      : Rajendra
 * Created On  : 10-October-2024
 * Details     : Instance of Abstract List fragment to represent the Restore Settings Menu List Screen.
 */
open class RestoreSettingsListFragment : SuperAbstractTimeoutEnableFragment(),
        SettingsListViewHolderInterface.ListItemClickListener,
        HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener {
    private var restoreSettingsList: ArrayList<ListTileData>? = null
    private var fragmentToolsRestoreListMenuBinding: FragmentToolsListMenuBinding? = null
    private var lastItemSelectedPos = -1
    private var currentPosition = -1
    private var allItemSize = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        fragmentToolsRestoreListMenuBinding = FragmentToolsListMenuBinding.inflate(inflater)
        fragmentToolsRestoreListMenuBinding?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentToolsRestoreListMenuBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        initHeaderBar()
        manageListView()
        when {
            KnobNavigationUtils.knobBackTrace -> {
                KnobNavigationUtils.knobBackTrace = false
                currentPosition = KnobNavigationUtils.lastTimeSelectedData()
                lastItemSelectedPos = KnobNavigationUtils.lastTimeSelectedData()
                highlightSelectedItem(lastItemSelectedPos)
            }
            KnobNavigationUtils.knobForwardTrace -> {
                KnobNavigationUtils.knobForwardTrace = false
                currentPosition = 0
                lastItemSelectedPos = 0
                highlightSelectedItem(lastItemSelectedPos)
            }
        }
    }

    private fun highlightSelectedItem(position: Int){
        fragmentToolsRestoreListMenuBinding?.recyclerList?.post {
            val viewHolderOld = fragmentToolsRestoreListMenuBinding?.recyclerList?.findViewHolderForAdapterPosition(position)
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }
    }

    private fun initHeaderBar() {
        fragmentToolsRestoreListMenuBinding?.headerBarPreferences?.apply {
            setLeftIcon(R.drawable.ic_back_arrow)
            setTitleText(getString(R.string.restore_setting))
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setRightIconVisibility(true)
            setCustomOnClickListener(this@RestoreSettingsListFragment)
        }
    }

    private fun manageListView() {
        fragmentToolsRestoreListMenuBinding?.recyclerList?.visibility = View.VISIBLE
        val listTileData = provideListRecyclerViewTilesData()
        allItemSize = listTileData?.size ?: 0
        listTileData.let {
            val listItems: ArrayList<Any> = ArrayList(it!!)
            val toolsListViewInterface =
                    listTileData?.let { it1 ->
                        SettingsListViewHolderInterface(
                                it1, this
                        )
                    }
            fragmentToolsRestoreListMenuBinding?.recyclerList?.setupListWithObjects(
                    listItems,
                    toolsListViewInterface
            )
        }
    }

    /**
     * Method to initialise the list view
     */
    fun provideListRecyclerViewTilesData(): ArrayList<ListTileData>? {
        restoreSettingsList = ArrayList()
        fragmentToolsRestoreListMenuBinding?.recyclerList?.visibility = View.VISIBLE
        addRestoreFactoryDefaults()
        addResetLearnMore()
        return restoreSettingsList
    }

    /**
     * Method to initialise the Display Brightness tile
     */
    private fun addRestoreFactoryDefaults() {
        val builder = ListTileData()
        builder.apply {
            titleText = getString(R.string.restore_fatory_defaults)
            titleTextVisibility = View.VISIBLE
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            rightTextVisibility = View.GONE
            itemViewVisibility = View.VISIBLE
            rightIconID = R.drawable.icon_list_item_right_arrow
            rightIconVisibility = View.VISIBLE
            isItemEnabled = true
        }
        restoreSettingsList?.add(builder)
    }

    /**
     * Method to initialise the Display Themes tile
     */
    private fun addResetLearnMore() {
        val builder = ListTileData()
        builder.apply {
            titleText = getString(R.string.settings_reset_learn_more)
            titleTextVisibility = View.VISIBLE
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            rightTextVisibility = View.GONE
            itemViewVisibility = View.VISIBLE
            rightIconVisibility = View.VISIBLE
            rightIconID = R.drawable.icon_list_item_right_arrow
            listItemDividerViewVisibility = View.GONE
            isItemEnabled = true
        }
        restoreSettingsList?.add(builder)
    }

    override fun onListViewItemClick(view: View?, position: Int) {
        val bundle = Bundle()
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        if (position == resources.getInteger(R.integer.integer_range_0)) {
            bundle.putBoolean(BundleKeys.BUNDLE_RESTORE_FACTORY, true)
        } else if (position == resources.getInteger(R.integer.integer_range_1)) {
            bundle.putBoolean(BundleKeys.BUNDLE_RESTORE_FACTORY, false)
        }
        NavigationUtils.navigateSafely(
                this,
                R.id.action_restoreSettingsListFragment_to_settingsResetRestoreConfirmationFragment,
                bundle,
                null
        )
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

    override fun rightIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return resources.getInteger(R.integer.session_long_timeout)
    }

    override fun onHMILeftKnobClick() {
        if (lastItemSelectedPos != -1) {
            lifecycleScope.launch(Dispatchers.Main) {
                KnobNavigationUtils.knobForwardTrace = true
                KnobNavigationUtils.addTraversingData(lastItemSelectedPos, false)
                val toggle =
                        fragmentToolsRestoreListMenuBinding?.recyclerList?.findViewHolderForAdapterPosition(
                                lastItemSelectedPos
                        )?.itemView?.findViewById<ToggleSwitch>(R.id.settings_item_toggle_switch)

                if (toggle?.visibility == View.VISIBLE) {
                    val toggleSwitch = toggle.findViewById<SwitchCompat>(R.id.toggle_switch)
                    toggleSwitch.isChecked = !toggleSwitch.isChecked
                } else {
                    fragmentToolsRestoreListMenuBinding?.recyclerList?.findViewHolderForAdapterPosition(
                            lastItemSelectedPos
                    )?.itemView?.findViewById<ConstraintLayout>(R.id.list_item_main_view)
                            ?.callOnClick()
                }

            }
        }
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (knobId == AppConstants.LEFT_KNOB_ID) {
                    currentPosition = CookingAppUtils.getKnobPositionIndex(
                            knobDirection,
                            currentPosition,
                            allItemSize
                    )
                    if (currentPosition >= 0) {
                        HMILogHelper.Logd(
                                "Knob",
                                "LEFT_KNOB: rotate right current knob index = $currentPosition"
                        )
                        fragmentToolsRestoreListMenuBinding?.recyclerList?.smoothScrollToPosition(
                                currentPosition
                        )
                        highLightSelectedTiles()
                    } else {
                        HMILogHelper.Logd("Knob", "LEFT_KNOB: rotate left current knob index = $currentPosition")
                        currentPosition = 0
                        highLightSelectedTiles()
                    }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (lastItemSelectedPos != -1) {
                val viewHolder = fragmentToolsRestoreListMenuBinding?.recyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = -1
        }
    }

    private fun highLightSelectedTiles() {
        fragmentToolsRestoreListMenuBinding?.recyclerList?.postDelayed({
            if (lastItemSelectedPos != -1) {
                val viewHolder = fragmentToolsRestoreListMenuBinding?.recyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = currentPosition
            val viewHolderOld = fragmentToolsRestoreListMenuBinding?.recyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }, 50) // Adjust delay as needed
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if (CookingViewModelFactory.getInScopeViewModel() == null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, cookingViewModel)
            return
        }
        if (cookingViewModel != null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            launchProbeDetectedPopupAsPerVariant(cookingViewModel)
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        allItemSize = 0
        super.onDestroyView()
    }
}