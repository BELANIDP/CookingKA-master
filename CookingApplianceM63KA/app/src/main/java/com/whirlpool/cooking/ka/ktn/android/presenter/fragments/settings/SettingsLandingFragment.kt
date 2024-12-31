package android.presenter.fragments.settings

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.SettingsLandingListViewHolderInterface
import android.presenter.customviews.widgets.gridview.GridListItemModel
import android.presenter.customviews.widgets.gridview.GridRecyclerViewInterface
import android.presenter.customviews.widgets.gridview.SettingsControlToggleListener
import android.presenter.customviews.widgets.gridview.viewholders.GridItemSettingsViewHolder
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.whirlpool.cooking.ka.BuildConfig
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentSettingsLandingBinding
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.settings.SettingsViewModel.getSettingsViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.CONNECT_TO_NETWORK_TILE_INDEX
import core.utils.AppConstants.DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT
import core.utils.AppConstants.FEATURE_IS_NOT_AVAILABLE
import core.utils.AppConstants.FEATURE_IS_UNDER_DEVELOPMENT
import core.utils.AppConstants.KEY_CONFIGURATION_SETTING_LANDING
import core.utils.AppConstants.NAVIGATION_ID_DEMO_INSTRUCTION
import core.utils.AppConstants.NAVIGATION_ID_DEMO_NOT_AVAILABLE
import core.utils.AppConstants.NAVIGATION_ID_DEMO_NOT_AVAILABLE_IN_TECHNICIAN_MODE
import core.utils.AppConstants.SETTINGLANDING_FRAGMENT
import core.utils.AppConstants.SOURCE_FRAGMENT
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.FavoriteDataHolder
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils
import core.utils.PopUpBuilderUtils.Companion.provisioningUnavailablePopup
import core.utils.PopUpBuilderUtils.Companion.removeProbeToStartSelfClean
import core.utils.SabbathUtils
import core.utils.SettingsManagerUtils.isApplianceProvisioned
import core.utils.SettingsManagerUtils.isBleProvisionSuccess
import core.utils.SharedPreferenceManager
import core.utils.SharedViewModel
import core.utils.ToastUtils
import core.utils.ToolsMenuJsonKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * File       : android.presenter.fragments.settings.SettingsLandingFragment
 * Brief      : This class provides the Settings screen
 * Author     : Vishal
 * Created On : 28-02-2024
 */

class SettingsLandingFragment : SuperAbstractTimeoutEnableFragment(),
    SettingsControlToggleListener, GridRecyclerViewInterface.GridItemClickListener,
    SettingsLandingListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener {
    private var fragmentSettingsLandingBinding: FragmentSettingsLandingBinding? = null
    private var settingsListItems: ArrayList<String>? = null
    private var settingsGridItems: ArrayList<String>? = null
    private var currentPosition = -1
    private var lastItemSelectedPos = -1
    private var isBlackOutConnectWiFi = false
    private var settingsViewModel: SettingsViewModel? = null
    var bundle: Bundle? = null
    private var settingsListTileData: ArrayList<ListTileData> = ArrayList()
    private var settingsListTileMoreData: ArrayList<ListTileData> = ArrayList()
    private var settingsGridListTileData = ArrayList<GridListItemModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isBlackOutConnectWiFi = if (bundle == null) {
            arguments?.getBoolean(BundleKeys.BUNDLE_IS_FROM_BLACKOUT_CONNECT_WIFI) ?: false
        } else {
            bundle?.getBoolean(BundleKeys.BUNDLE_IS_FROM_BLACKOUT_CONNECT_WIFI) ?: false
        }
        HMILogHelper.Logd("on create of setting page")
        fragmentSettingsLandingBinding = FragmentSettingsLandingBinding.inflate(inflater)
        fragmentSettingsLandingBinding?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentSettingsLandingBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Set Settings flow true for handling Button configuration between all settings screen
        CookingAppUtils.setSettingsFlow(true)
        HMIExpansionUtils.disableFeatureHMIKeys(KEY_CONFIGURATION_SETTING_LANDING)
        HMIExpansionUtils.enableFeatureHMIKeys(KEY_CONFIGURATION_SETTING_LANDING)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        setUpViewModels()
        manageChildViews()
        observeKnobBackTrace()
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highlightNewSelection(lastItemSelectedPos, true)
        }
        if (KnobNavigationUtils.isBackPress()) {
            KnobNavigationUtils.removeLastAction()
        }
    }

    private fun observeKnobBackTrace() {
        SharedViewModel.getSharedViewModel(this.requireActivity()).isNavigatedFromKnobClick()
            .observe(viewLifecycleOwner) { value ->
                value?.let { navigated ->
                    if (navigated && KnobNavigationUtils.knobBackTrace) {
                        // Log the last action if available
                        HMILogHelper.Logd("last saved action: ${KnobNavigationUtils.lastTimeSelectedData()}")
                        KnobNavigationUtils.knobBackTrace = false
                        currentPosition = KnobNavigationUtils.lastTimeSelectedData()
                        lastItemSelectedPos = KnobNavigationUtils.lastTimeSelectedData()
                        highlightNewSelection(lastItemSelectedPos, true)
                        KnobNavigationUtils.removeLastAction()
                    } else {
                        HMILogHelper.Logd("livedata observer: not navigated form Knob")
                    }
                }
            }
    }

    private fun setUpViewModels() {
        settingsViewModel = getSettingsViewModel()
        if (isBlackOutConnectWiFi) {
            isBlackOutConnectWiFi = false
            bundle = Bundle()
            bundle?.putBoolean(BundleKeys.BUNDLE_IS_FROM_BLACKOUT_CONNECT_WIFI, false)
            navigateForConnectToNetwork()
        }
    }

    private fun manageChildViews() {
        manageSettingsCollectionHeaderBar()
        manageSettingsGridRecyclerView()
        manageSettingsListRecyclerView()
        fragmentSettingsLandingBinding?.nestedScrollViewCollection?.isVerticalScrollBarEnabled =
            true
    }

    private fun manageSettingsCollectionHeaderBar() {
        fragmentSettingsLandingBinding?.headerBarSettings?.apply {
            setLeftIcon(R.drawable.ic_back_arrow)
            setRightIconVisibility(false)
            setTitleText(getString(R.string.settings))
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setCustomOnClickListener(this@SettingsLandingFragment)
        }
    }

    private fun manageSettingsGridRecyclerView() {
        settingsGridItems =
            CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_GRID)
        if (settingsGridItems != null) {
            fragmentSettingsLandingBinding?.settingsRecyclerGridList?.visibility = View.VISIBLE
            settingsGridListTileData = provideSettingsGridListRecyclerViewTilesData()
            if (settingsGridListTileData.isNotEmpty()) {
                fragmentSettingsLandingBinding?.settingsRecyclerGridList?.isVerticalScrollBarEnabled =
                    false
                val listItems = ArrayList<Any>(settingsGridListTileData)
                val gridRecyclerViewInterface =
                    GridRecyclerViewInterface(settingsGridListTileData, this)
                fragmentSettingsLandingBinding?.settingsRecyclerGridList?.setupGridWithObjects(
                    listItems,
                    gridRecyclerViewInterface
                )
                val gridLayoutManager =
                    fragmentSettingsLandingBinding?.settingsRecyclerGridList?.layoutManager as GridLayoutManager?
                if (gridLayoutManager != null) gridLayoutManager.spanCount = 3
            } else {
                HMILogHelper.Loge("Grid List Tile data not available!...")
            }
        } else {
            HMILogHelper.Loge("Grid List Tile data not available!...")
        }
    }

    private fun manageSettingsListRecyclerView() {
        settingsListItems =
            CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU)
        if (settingsListItems != null) {
            fragmentSettingsLandingBinding?.settingsRecyclerList?.visibility = View.VISIBLE
            settingsListTileMoreData = provideSettingsListRecyclerViewTilesData()
            if (settingsListTileMoreData.isNotEmpty()) {
                val listItems: ArrayList<Any> = ArrayList(settingsListTileMoreData)
                val toolsListViewInterface =
                    SettingsLandingListViewHolderInterface(
                        settingsListTileMoreData, this
                    )
                fragmentSettingsLandingBinding?.settingsRecyclerList?.setupListWithObjects(
                    listItems,
                    toolsListViewInterface
                )
            } else {
                HMILogHelper.Loge("List Tile data not available!...")
            }
        } else {
            HMILogHelper.Loge("List Tile data not available!...")
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun provideSettingsGridListRecyclerViewTilesData(): ArrayList<GridListItemModel> {
        val settingsGridListTileData = ArrayList<GridListItemModel>()
        return try {
            settingsGridItems?.indices?.let { indices ->
                for (i in indices) {
                    lateinit var gridListItemModel: GridListItemModel
                    when (settingsGridItems?.get(i)) {
                        ToolsMenuJsonKeys.JSON_KEY_TOOLS_CONTROL_LOCK -> {
                            gridListItemModel = GridListItemModel(
                                getString(
                                    resources.getIdentifier(
                                        settingsGridItems?.get(i),
                                        ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                        requireContext().packageName
                                    )
                                ), GridListItemModel.GRID_SETTINGS_OPTIONS_TILE
                            )

                            gridListItemModel.settingsControlToggleListener = this
                            gridListItemModel.id =
                                GridItemSettingsViewHolder.SETTINGS_CONTROL_LOCK_TILE_ID
                            gridListItemModel.isDemoActive = CookingAppUtils.isDemoModeEnabled()
                            gridListItemModel.isActive = false
                        }

                        ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_MUTE -> {
                            gridListItemModel = GridListItemModel(
                                getString(
                                    resources.getIdentifier(
                                        settingsGridItems?.get(i),
                                        ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                        requireContext().packageName
                                    )
                                ), GridListItemModel.GRID_SETTINGS_OPTIONS_TILE
                            )
                            gridListItemModel.settingsControlToggleListener = this
                            gridListItemModel.id =
                                GridItemSettingsViewHolder.SETTINGS_MUTE_TILE_ID

                            gridListItemModel.isActive = getSettingsViewModel().mute.value == true
                        }

                        ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_HISTORY -> {
                            gridListItemModel = GridListItemModel(
                                getString(
                                    resources.getIdentifier(
                                        settingsGridItems?.get(i),
                                        ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                        requireContext().packageName
                                    )
                                ), GridListItemModel.GRID_SETTINGS_OPTIONS_TILE
                            )
                            gridListItemModel.settingsControlToggleListener = this
                            gridListItemModel.id =
                                GridItemSettingsViewHolder.SETTINGS_HISTORY_TILE_ID
                            gridListItemModel.isDemoActive = CookingAppUtils.isDemoModeEnabled()
                        }

                        else -> {
                            gridListItemModel = GridListItemModel(
                                AppConstants.EMPTY_STRING,
                                GridListItemModel.GRID_SETTINGS_OPTIONS_TILE
                            )
                        }
                    }
                    settingsGridListTileData.add(gridListItemModel)
                }
            }
            settingsGridListTileData
        } catch (e: Exception) {
            e.printStackTrace()
            settingsGridListTileData
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun provideSettingsListRecyclerViewTilesData(): ArrayList<ListTileData> {
        return try {
            var i = 0
            if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN) {
                settingsListItems?.apply {
                    remove(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_MORE_MODES)
                    remove(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SABBATH_KCF)
                    remove(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SELF_CLEAN)
                    remove(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_STEAM_CLEAN)
                }
            }
            if(!BuildConfig.IS_VISION_BUILD){
                settingsListItems?.apply {
                    remove(ToolsMenuJsonKeys.JSON_KEY_TOOLS_VISION)
                    remove(ToolsMenuJsonKeys.JSON_KEY_TOOLS_LIVE_LOOK_PREVIEW)
                    remove(ToolsMenuJsonKeys.JSON_KEY_TOOLS_DIRTY_LENS_DETECTOR)
                }
            }

            while (i < (settingsListItems?.size ?: 0)) {
                val listTileData = ListTileData()
                listTileData.apply {
                    headingVisibility = View.GONE
                    titleTextVisibility = View.GONE
                    subTextVisibility = View.GONE
                    rightTextVisibility = View.GONE
                    radioButtonData = ListTileData.RadioButtonData().apply {
                        visibility = View.INVISIBLE
                    }
                    isPaddingView = true
                    itemIconVisibility = View.VISIBLE
                    rightIconVisibility = View.VISIBLE
                    listItemDividerViewVisibility = View.GONE
                    seperatorVisibility = View.GONE
                    listItemMainViewVisibility = View.GONE
                }
                when (settingsListItems?.get(i)) {
                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_MORE_MODES -> {
                        // Here we have More Modes Heading plus Self Clean List Title
                        listTileData.apply {
                            headingText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            headingVisibility = View.VISIBLE
                            seperatorVisibility = View.VISIBLE
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SELF_CLEAN -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.ic_40px_self_clean
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.VISIBLE
                        }

                        when (CookingViewModelFactory.getProductVariantEnum()) {
                            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> if ((CookingAppUtils.isPyroAllowed(
                                    CookingViewModelFactory.getPrimaryCavityViewModel()
                                ) && !CookingAppUtils.isDemoModeEnabled())
                            ) {
                                listTileData.apply {
                                    navigationID =
                                        R.id.action_settingsLandingFragment_to_durationSelectionFragmentSOktn
                                }
                            } else if (CookingAppUtils.isDemoModeEnabled()) {
                                listTileData.apply {
                                    isItemEnabled = false
                                    subTextVisibility = View.GONE
                                    navigationID = NAVIGATION_ID_DEMO_NOT_AVAILABLE
                                }
                            } else {
                                listTileData.apply {
                                    isItemEnabled = false
                                    subTextVisibility = View.VISIBLE
                                }
                                if (activity != null) listTileData.subText =
                                    CookingAppUtils.getPyroNotAllowedMessage(requireContext(),
                                        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.pyroCleanNotAllowedUntilTimeRemaining.value)
                            }
                            // same id
                            CookingViewModelFactory.ProductVariantEnum.COMBO -> if (CookingAppUtils.isDemoModeEnabled()) {
                                listTileData.apply {
                                    isItemEnabled = false
                                    subTextVisibility = View.GONE
                                    navigationID = NAVIGATION_ID_DEMO_NOT_AVAILABLE
                                }
                            } else {
                                listTileData.apply {
                                    rightText = getString(R.string.text_supportingText)
                                    rightTextVisibility = View.VISIBLE
                                }
                                listTileData.navigationID =
                                    R.id.action_settingsLandingFragment_to_durationSelectionFragmentCombo
                            }

                            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> if (CookingAppUtils.isDemoModeEnabled()) {
                                listTileData.apply {
                                    isItemEnabled = false
                                    subTextVisibility = View.GONE
                                    navigationID = NAVIGATION_ID_DEMO_NOT_AVAILABLE
                                }
                            } else {
                                listTileData.apply {
                                    navigationID =
                                        R.id.action_settingsLandingFragment_to_selfCleanCavitySelectionFragmentDOktn
                                }

                            }

                            else -> HMILogHelper.Loge("Variant not able to perform Self-Clean Mode")
                        }

                        if (CookingAppUtils.isAnyCycleRunning()) {
                            listTileData.apply {
                                isItemEnabled = false
                                subTextVisibility = View.GONE
                            }
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SABBATH_KCF -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.ic_sabbath
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.VISIBLE
                        }
                        if (CookingAppUtils.isAnyCycleRunning()) {
                            listTileData.apply {
                                isItemEnabled = false
                                subTextVisibility = View.GONE
                                navigationID = FEATURE_IS_NOT_AVAILABLE
                            }
                        } else {
                            listTileData.navigationID =
                                R.id.action_settingsLandingFragment_to_sabbathSelectionListFragment
                        }
                        when (CookingViewModelFactory.getProductVariantEnum()) {
                            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                                listTileData.apply {
                                    rightText = getString(R.string.text_supportingText)
                                    rightTextVisibility = View.VISIBLE
                                }
                            }

                            else -> {
                                //Do nothing
                            }
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_STEAM_CLEAN -> {
                        //Clear recipe data if the cavity is IDLE for steam clean
                        CookingAppUtils.clearRecipeData()
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.ic_steam_clean
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.GONE
                        }
                        if (CookingAppUtils.isAnyCycleRunning()) {
                            updateCleanSteamTileForCurrentVariant(listTileData)
                        } else {
                            when (CookingViewModelFactory.getProductVariantEnum()) {
                                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> if (CookingAppUtils.isDemoModeEnabled()) {
                                    listTileData.apply {
                                        isItemEnabled = false
                                        subTextVisibility = View.GONE
                                        navigationID = NAVIGATION_ID_DEMO_NOT_AVAILABLE
                                    }
                                } else {
                                    listTileData.apply {
                                        navigationID =
                                            R.id.global_action_to_steamInstructionFragment
                                    }
                                }
                                // same id
                                CookingViewModelFactory.ProductVariantEnum.COMBO -> if (CookingAppUtils.isDemoModeEnabled()) {
                                    listTileData.apply {
                                        isItemEnabled = false
                                        subTextVisibility = View.GONE
                                        navigationID = NAVIGATION_ID_DEMO_NOT_AVAILABLE
                                    }
                                } else {
                                    listTileData.apply {
                                        rightText = getString(R.string.text_supportingText)
                                        rightTextVisibility = View.VISIBLE
                                    }
                                    listTileData.navigationID =
                                        R.id.global_action_to_steamInstructionFragment
                                }

                                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> if (CookingAppUtils.isDemoModeEnabled()) {
                                    listTileData.apply {
                                        isItemEnabled = false
                                        subTextVisibility = View.GONE
                                        navigationID = NAVIGATION_ID_DEMO_NOT_AVAILABLE
                                    }
                                } else {
                                    listTileData.apply {
                                        navigationID =
                                            R.id.action_settingsLandingFragment_to_steamCleanCavitySelectionFragment
                                    }

                                }

                                else -> HMILogHelper.Loge("Variant not able to perform steam-Clean Mode")
                            }
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_VISION -> {
                        // Here we have Preferences Heading plus Time & Date List Title
                        listTileData.apply {
                            itemIconVisibility = View.GONE
                            rightIconVisibility = View.GONE
                            headingText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            headingVisibility = View.VISIBLE
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_LIVE_LOOK_PREVIEW -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            rightTextVisibility = View.GONE
                            itemIconID = R.drawable.cam_shrink
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.VISIBLE
                            navigationID =
                                R.id.action_to_live_preview_fragment
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_DIRTY_LENS_DETECTOR -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.ic_placeholder
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.GONE
                            navigationID =
                                R.id.action_dummy_dirty_lens_action
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_PREFERENCES -> {
                        // Here we have Preferences Heading plus Time & Date List Title
                        listTileData.apply {
                            itemIconVisibility = View.GONE
                            rightIconVisibility = View.GONE
                            headingText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            headingVisibility = View.VISIBLE
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_TIME_AND_DATE -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            rightTextVisibility = View.GONE
                            itemIconID = R.drawable.ic_timedate
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.VISIBLE
                            navigationID =
                                R.id.action_settingsLandingFragment_to_settingsTimeAndDateFragment
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_KNOB_SETTING -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.ic_knob_settings
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.VISIBLE
                            navigationID =
                                R.id.action_settingsLandingFragment_to_settingsKnobFragment
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SHOW_ALL_PREFERENCES -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.ic_showmore
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.GONE
                            navigationID =
                                R.id.action_settingsLandingFragment_to_preferencesFragment
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_NETWORK_SETTINGS -> {
                        listTileData.apply {
                            headingText =
                                getString(
                                    resources.getIdentifier(
                                        settingsListItems?.get(i),
                                        ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                        requireContext().packageName
                                    )
                                )
                            seperatorVisibility = View.VISIBLE
                            headingVisibility = View.VISIBLE
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_CONNECT_TO_NETWORK -> {
                        updateNetworkInformation(listTileData)
                        listTileData.apply {
                            titleTextVisibility = View.VISIBLE
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.VISIBLE
                        }
                        if (CookingAppUtils.isDemoModeEnabled() || CookingAppUtils.isAnyCycleRunning() ||
                            (SharedPreferenceManager.getTechnicianTestDoneStatusIntoPreference() == AppConstants.TRUE_CONSTANT)
                        ) {
                            listTileData.apply {
                                isItemEnabled = false
                                subTextVisibility = View.GONE
                            }
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SHOW_ALL_CONNECTIVITY -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.ic_showmore
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.GONE
                            navigationID =
                                R.id.action_settingsLandingFragment_to_toolsConnectivityList
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_INFO -> {
                        listTileData.apply {
                            headingText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )

                            headingVisibility = View.VISIBLE
                            seperatorVisibility = View.VISIBLE
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_DEMO -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.demomode
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.VISIBLE
                        }
                        if (SharedPreferenceManager.getTechnicianTestDoneStatusIntoPreference() == AppConstants.TRUE_CONSTANT) {
                            listTileData.apply {
                                isItemEnabled = false
                                subTextVisibility = View.GONE
                                navigationID = NAVIGATION_ID_DEMO_NOT_AVAILABLE_IN_TECHNICIAN_MODE
                            }
                        } else {
                            if (CookingAppUtils.isAnyCycleRunning()) {
                                listTileData.apply {
                                    isItemEnabled = false
                                    subTextVisibility = View.GONE
                                    navigationID = FEATURE_IS_NOT_AVAILABLE
                                }
                            } else {
                                listTileData.navigationID = NAVIGATION_ID_DEMO_INSTRUCTION
                            }
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SERVICE_SUPPORT -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.ic_servicesupporticon
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.VISIBLE
                            if (CookingAppUtils.isAnyCycleRunning()) {
                                isItemEnabled = false
                                subTextVisibility = View.GONE
                                navigationID = FEATURE_IS_NOT_AVAILABLE

                            } else {
                                navigationID = R.id.global_action_go_to_error_screen
                            }
                        }
                    }

                    ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SHOW_ALL_INFO -> {
                        listTileData.apply {
                            titleText = getString(
                                resources.getIdentifier(
                                    settingsListItems?.get(i),
                                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING,
                                    requireContext().packageName
                                )
                            )
                            titleTextVisibility = View.VISIBLE
                            itemIconID = R.drawable.ic_showmore
                            rightIconID = R.drawable.ic_rightarrowicon
                            listItemDividerViewVisibility = View.GONE
                            navigationID = R.id.action_settingsLandingFragment_to_infoFragment
                        }
                    }

                    else -> {}
                }
                settingsListTileData.add(listTileData)
                i++
            }
            settingsListTileData
        } catch (e: Exception) {
            e.printStackTrace()
            settingsListTileData
        }
    }

    private fun updateCleanSteamTileForCurrentVariant(listTileData: ListTileData) {
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                val primaryModel = CookingViewModelFactory.getPrimaryCavityViewModel()
                val secondaryModel = CookingViewModelFactory.getSecondaryCavityViewModel()
                if (CookingAppUtils.isUpperSteamCleanRunning() && !CookingAppUtils.isLowerSteamCleanRunning()) {
                    updateSteamCleanTileForLowerOven(secondaryModel, listTileData)
                } else if (CookingAppUtils.isLowerSteamCleanRunning() && !CookingAppUtils.isUpperSteamCleanRunning()) {
                    updateSteamCleanTileForUpperOven(primaryModel, listTileData)
                } else if (!CookingAppUtils.isLowerSteamCleanRunning() && !CookingAppUtils.isUpperSteamCleanRunning()) {
                    updateSteamCleanTileForUpperAndLowerOven(primaryModel, secondaryModel, listTileData)
                } else {
                    listTileData.apply {
                        isItemEnabled = false
                        subTextVisibility = View.GONE
                        navigationID = FEATURE_IS_NOT_AVAILABLE
                    }
                }
            }

            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                listTileData.apply {
                    isItemEnabled = false
                    subTextVisibility = View.GONE
                    navigationID = FEATURE_IS_NOT_AVAILABLE
                }
            }

            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                if (CookingAppUtils.isLowerSteamCleanRunning()) {
                    listTileData.apply {
                        isItemEnabled = false
                        subTextVisibility = View.GONE
                        rightText = getString(R.string.text_supportingText)
                        rightTextVisibility = View.VISIBLE
                        navigationID = FEATURE_IS_NOT_AVAILABLE
                    }
                } else if (!CookingAppUtils.isLowerSteamCleanRunning() && CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value ==
                    RecipeExecutionState.IDLE
                ) {
                    listTileData.apply {
                        isItemEnabled = true
                        rightText = getString(R.string.text_supportingText)
                        subTextVisibility = View.GONE
                        rightTextVisibility = View.VISIBLE
                        navigationID = R.id.global_action_to_steamInstructionFragment
                    }
                } else {
                    listTileData.apply {
                        isItemEnabled = false
                        subTextVisibility = View.GONE
                        rightText = getString(R.string.text_supportingText)
                        rightTextVisibility = View.VISIBLE
                        navigationID = FEATURE_IS_NOT_AVAILABLE
                    }
                }
            }

            else -> {
                // Do nothing
            }
        }
    }

    private fun updateSteamCleanTileForUpperAndLowerOven(
        primaryModel: CookingViewModel,
        secondaryModel: CookingViewModel,
        listTileData: ListTileData
    ) {
        if (isBothCavityRunningDelayCycle(primaryModel, secondaryModel)) {
            listTileData.apply {
                isItemEnabled = false
                subTextVisibility = View.GONE
                navigationID = FEATURE_IS_NOT_AVAILABLE
            }
            return
        }
        if ((primaryModel.recipeExecutionViewModel.recipeCookingState.value !=
                    RecipeCookingState.CLEANING) && (secondaryModel.recipeExecutionViewModel.recipeCookingState.value ==
                    RecipeCookingState.IDLE && secondaryModel.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.DELAYED)
        ) {
            disableTile(secondaryModel, listTileData)
        } else if ((secondaryModel.recipeExecutionViewModel.recipeCookingState.value !=
                    RecipeCookingState.CLEANING) && (primaryModel.recipeExecutionViewModel.recipeCookingState.value ==
                    RecipeCookingState.IDLE && primaryModel.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.DELAYED)
        ) {
            disableTile(primaryModel, listTileData)
        } else {
            listTileData.apply {
                isItemEnabled = false
                subTextVisibility = View.GONE
                navigationID = FEATURE_IS_NOT_AVAILABLE
            }
        }
    }

    private fun disableTile(
        cookingViewModel: CookingViewModel,
        listTileData: ListTileData
    ) {
        if (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED) {
            listTileData.apply {
                isItemEnabled = false
                subTextVisibility = View.GONE
                navigationID = FEATURE_IS_NOT_AVAILABLE
            }
        } else {
            listTileData.apply {
                isItemEnabled = true
                rightTextVisibility = View.GONE
                navigationID =
                    R.id.global_action_to_steamInstructionFragment
            }
        }
    }

    private fun isBothCavityRunningDelayCycle(
        primaryModel: CookingViewModel,
        secondaryModel: CookingViewModel
    ): Boolean {
        return secondaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED &&
                primaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED
    }

    private fun updateSteamCleanTileForUpperOven(
        primaryModel: CookingViewModel,
        listTileData: ListTileData
    ) {
        if (primaryModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.IDLE
        ) {
            if (primaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED) {
                listTileData.apply {
                    isItemEnabled = false
                    subTextVisibility = View.GONE
                    navigationID = FEATURE_IS_NOT_AVAILABLE
                }
            } else {
                listTileData.apply {
                    isItemEnabled = true
                    rightTextVisibility = View.GONE
                    navigationID =
                        R.id.global_action_to_steamInstructionFragment
                }
            }
        } else {
            listTileData.apply {
                isItemEnabled = false
                subTextVisibility = View.GONE
                navigationID = FEATURE_IS_NOT_AVAILABLE
            }
        }
    }

    private fun updateSteamCleanTileForLowerOven(
        secondaryModel: CookingViewModel,
        listTileData: ListTileData
    ) {
        if (secondaryModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.IDLE
        ) {
            if (secondaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED) {
                listTileData.apply {
                    isItemEnabled = false
                    subTextVisibility = View.GONE
                    navigationID = FEATURE_IS_NOT_AVAILABLE
                }
            } else {
                listTileData.apply {
                    isItemEnabled = true
                    rightTextVisibility = View.GONE
                    navigationID =
                        R.id.global_action_to_steamInstructionFragment
                }
            }
        } else {
            listTileData.apply {
                isItemEnabled = false
                subTextVisibility = View.GONE
                navigationID = FEATURE_IS_NOT_AVAILABLE
            }
        }
    }

    private fun navigateForSelfCleanDurationSelectionSO(navigationId: Int) {
        navigateSafely(this, navigationId, null, null)
    }


    private fun navigateForConnectToNetwork() {
        if (isBleProvisionSuccess || isApplianceProvisioned()) {
            val bundle = Bundle().apply { putString(SOURCE_FRAGMENT, SETTINGLANDING_FRAGMENT) }
            navigateSafely(
                this,
                R.id.action_settingsLandingFragment_to_networkDetailsInformationFragment,
                bundle,
                null
            )
        } else if (!CookingAppUtils.isApplianceIdleForProvisioning()) {
            provisioningUnavailablePopup(
                this,
                R.string.text_header_appliance_is_busy,
                getString(R.string.text_description_try_again_later)
            )
        } else if (KitchenTimerUtils.isKitchenTimersRunning() != 0) {
            provisioningUnavailablePopup(
                this,
                R.string.text_header_cancel_timer,
                CookingAppUtils.getKitchenTimerCancelPopupDescription(this)
            )
        } else {
            CookingAppUtils.startProvisioning(
                NavigationUtils.getVisibleFragment()?.let { getViewSafely(it) },
                isFromRemoteEnable = false,
                isFromConnectivityScreen = false,
                false
            )
        }
    }

    override fun onListItemClick(view: View?, position: Int, isFromKnob: Boolean) {}

    /*
        * Listener method which is called on Grid tile click
        * @param position index/position for the tile clicked
        */
    private fun onGridItemClickEvent(position: Int) {
        val gridListModel: GridListItemModel = settingsGridListTileData[position]
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        when (gridListModel.id) {
            GridItemSettingsViewHolder.SETTINGS_CONTROL_LOCK_TILE_ID -> {
                // activate key lock
                if (gridListModel.isDemoActive) {
                    showNotificationLockNotAvailable()
                } else {
                    onControlLockToggled(true, gridListModel)
                }
            }

            GridItemSettingsViewHolder.SETTINGS_MUTE_TILE_ID -> {
                // toggle mute icon & activate / deactivate sound the  system
                HMILogHelper.Logd("Is muted = ${getSettingsViewModel().mute.value}")
                if (getSettingsViewModel().mute.value == true) {
                    gridListModel.isActive = false
                    getSettingsViewModel().setMute(false)
                } else {
                    gridListModel.isActive = true
                    getSettingsViewModel().setMute(true)
                }
                notifyGridItemChanged(position)
            }

            GridItemSettingsViewHolder.SETTINGS_HISTORY_TILE_ID -> {
                FavoriteDataHolder.isSettingsFlow = true
                navigateSafely(
                    this,
                    R.id.action_settingsLandingFragment_to_historyFragment,
                    null,
                    null
                )
            }
        }
    }

    override fun onListViewItemClick(view: View?, position: Int) {
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        onListItemClickEvent(position)
    }

    /*
     * Listener method which is called on List tile click
     *
     * @param view     the tile view which is clicked
     * @param position index/position for the tile clicked
     */
    private fun onListItemClickEvent(position: Int) {
        when (val navigationId: Int = settingsListTileMoreData[position].navigationID) {

            NAVIGATION_ID_DEMO_NOT_AVAILABLE -> {
                showNotificationLockNotAvailable()
            }

            NAVIGATION_ID_DEMO_NOT_AVAILABLE_IN_TECHNICIAN_MODE -> {
                showNotificationDemoNotAvailableInTechnicianMode()
            }

            FEATURE_IS_NOT_AVAILABLE -> {
                PopUpBuilderUtils.featureNotAvailablePopUp(this)
            }

            FEATURE_IS_UNDER_DEVELOPMENT -> {
                ToastUtils.showToast(this.requireContext(), "Under development")
            }

            R.id.global_action_to_steamInstructionFragment,
            R.id.action_settingsLandingFragment_to_steamCleanCavitySelectionFragment -> {
                val primaryModel = CookingViewModelFactory.getPrimaryCavityViewModel()
                when (CookingViewModelFactory.getProductVariantEnum()) {
                    CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                        checkIfProbeDetectedAndNavigate(primaryModel, navigationId)
                    }

                    CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                        val secondaryModel = CookingViewModelFactory.getSecondaryCavityViewModel()
                        CookingViewModelFactory.setInScopeViewModel(secondaryModel)
                        checkIfProbeDetectedAndNavigate(secondaryModel, navigationId)
                    }

                    CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                        val secondaryModel = CookingViewModelFactory.getSecondaryCavityViewModel()
                        if (CookingAppUtils.isUpperSteamCleanRunning() && !CookingAppUtils.isLowerSteamCleanRunning()) {
                            if (secondaryModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.IDLE
                            ) {
                                HMILogHelper.Logd("Steam clean: isUpperSteamCleanRunning")
                                CookingViewModelFactory.setInScopeViewModel(secondaryModel)
                                checkIfProbeDetectedAndNavigate(secondaryModel, navigationId)
                            }
                        } else if (CookingAppUtils.isLowerSteamCleanRunning() && !CookingAppUtils.isUpperSteamCleanRunning()) {
                            if (primaryModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.IDLE
                            ) {
                                HMILogHelper.Logd("Steam clean: isLowerSteamCleanRunning")
                                CookingViewModelFactory.setInScopeViewModel(primaryModel)
                                checkIfProbeDetectedAndNavigate(primaryModel, navigationId)
                            }
                        } else if (!CookingAppUtils.isLowerSteamCleanRunning() && !CookingAppUtils.isUpperSteamCleanRunning()) {
                            handleBothCavityNavigationInIdleState(
                                primaryModel,
                                secondaryModel,
                                navigationId
                            )
                        } else {
                            navigateSafely(this, navigationId, null, null)
                            HMILogHelper.Logd("Steam clean: navigate to the steam instruction screen")
                        }
                    }
                    else -> {

                    }
                }
            }

            R.id.action_settingsLandingFragment_to_durationSelectionFragmentSOktn,
            R.id.action_settingsLandingFragment_to_selfCleanCavitySelectionFragmentDOktn,
            R.id.action_settingsLandingFragment_to_durationSelectionFragmentCombo -> {
                if (KitchenTimerVMFactory.isAnyKitchenTimerRunning()) {
                    PopUpBuilderUtils.allKitchenTimerCancelPopup(
                        this,
                        onCancellingAllKitchenTimers = { onListItemClickEvent(position) }, null
                    )
                    return
                }
                isSelfCleanAllowed(onMeatProbeConditionMet = {
                    //If the product variant is combo navigate directly to the pyro level
                    if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO) {
                        HMILogHelper.Logd("Self clean: set secondary in scope model and navigate to the pyro level selection screen")
                        CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
                    }
                    //Self clean flow started method which is help to handle Button configuration
                    CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = true)
                    HMIExpansionUtils.setLightForCancelButton(true)
                    navigateForSelfCleanDurationSelectionSO(navigationId)
                })
            }

            R.id.action_settingsLandingFragment_to_sabbathSelectionListFragment -> {
                if (KitchenTimerVMFactory.isAnyKitchenTimerRunning()) {
                    PopUpBuilderUtils.allKitchenTimerCancelPopup(
                        this,
                        onCancellingAllKitchenTimers = { onListItemClickEvent(position) }, null
                    )
                    return
                }
                val (isProbeConnected, connectedCavityViewModel) = MeatProbeUtils.isAnyCavityHasMeatProbeConnected()
                if (isProbeConnected) {
                    SabbathUtils.probeAlreadyPresentBeforeSabbathMenu(
                        this,
                        connectedCavityViewModel
                    ) {
                        onListItemClickEvent(position)
                    }
                    return
                }
                // Handle Sabbath selection click
                navigateSafely(this, navigationId, null, null)
            }

            R.id.action_settingsLandingFragment_to_provisioningBleConnect -> {
                if (CookingAppUtils.isDemoModeEnabled()) {
                    showNotificationLockNotAvailable()
                } else if (SharedPreferenceManager.getTechnicianTestDoneStatusIntoPreference() == AppConstants.TRUE_CONSTANT) {
                    showNotificationDemoNotAvailableInTechnicianMode()
                } else {
                    navigateForConnectToNetwork()
                }
            }

            NAVIGATION_ID_DEMO_INSTRUCTION -> {
                if (CookingAppUtils.isDemoModeEnabled()) {
                    if (CookingAppUtils.isAnyCycleRunning() || KitchenTimerUtils.isAnyKitchenTimerRunningOrPaused()) {
                        PopUpBuilderUtils.demoModeCycleRunningExitInstructionPopUp(this)
                    } else {
                        CookingAppUtils.setNavigatedFrom(AppConstants.DEMOSETTINGSLANDING_FRAGMENT)
                        PopUpBuilderUtils.demoModeExitInstructionPopUp(this)
                    }
                } else {
                    if (CookingAppUtils.isAnyCycleRunning() && KitchenTimerUtils.isAnyKitchenTimerRunningOrPaused()) {
                        PopUpBuilderUtils.featureNotAvailablePopUp(this)
                    } else if (CookingAppUtils.isAnyCycleRunning()) {
                        PopUpBuilderUtils.featureNotAvailablePopUp(this)
                    } else if (KitchenTimerUtils.isAnyKitchenTimerRunningOrPaused()) {
                        PopUpBuilderUtils.demoModeKTRunningEntryInstructionPopUp(this)
                    } else
                        PopUpBuilderUtils.demoModeEntryInstructionPopUp(this)
                }
            }

            R.id.action_settingsLandingFragment_to_preferencesFragment,
            R.id.action_settingsLandingFragment_to_toolsConnectivityList,
            R.id.action_settingsLandingFragment_to_infoFragment -> {
                navigateSafely(this, navigationId, null, null)
            }

            R.id.action_settingsLandingFragment_to_settingsTimeAndDateFragment -> {
                CookingAppUtils.setNavigatedFrom(SETTINGLANDING_FRAGMENT)
                navigateSafely(this, navigationId, null, null)
            }

            R.id.global_action_go_to_error_screen,
            R.id.action_settingsLandingFragment_to_settingsKnobFragment -> {
                CookingAppUtils.setNavigatedFrom(SETTINGLANDING_FRAGMENT)
                navigateSafely(this, navigationId, null, null)
            }
            R.id.action_to_live_preview_fragment -> {
                navigateSafely(this, navigationId, null, null)
            }
            R.id.action_dummy_dirty_lens_action -> {
                showNotification(R.string.dirty_lens_detector)
            }

        }
    }

    private fun handleBothCavityNavigationInIdleState(
        primaryModel: CookingViewModel,
        secondaryModel: CookingViewModel,
        navigationId: Int
    ) {
        if ((primaryModel.recipeExecutionViewModel.recipeCookingState.value !=
                    RecipeCookingState.CLEANING && primaryModel.recipeExecutionViewModel.recipeCookingState.value !=
                    RecipeCookingState.IDLE) && secondaryModel.recipeExecutionViewModel.recipeCookingState.value ==
            RecipeCookingState.IDLE
        ) {
            HMILogHelper.Logd("Steam clean: Upper is running and lower is IDLE")
            CookingViewModelFactory.setInScopeViewModel(secondaryModel)
            checkIfProbeDetectedAndNavigate(secondaryModel, navigationId)

        } else if ((secondaryModel.recipeExecutionViewModel.recipeCookingState.value !=
                    RecipeCookingState.CLEANING && secondaryModel.recipeExecutionViewModel.recipeCookingState.value !=
                    RecipeCookingState.IDLE) && primaryModel.recipeExecutionViewModel.recipeCookingState.value ==
            RecipeCookingState.IDLE
        ) {
            HMILogHelper.Logd("Steam clean: Lower is running and Upper is IDLE")
            CookingViewModelFactory.setInScopeViewModel(primaryModel)
            checkIfProbeDetectedAndNavigate(primaryModel, navigationId)
        } else {
            if ((primaryModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.IDLE
                        && primaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED)
                && secondaryModel.recipeExecutionViewModel.recipeCookingState.value ==
                RecipeCookingState.IDLE
            ) {
                HMILogHelper.Logd("Steam clean: Upper Oven delay is running and lower is IDLE")
                CookingViewModelFactory.setInScopeViewModel(secondaryModel)
                checkIfProbeDetectedAndNavigate(secondaryModel, navigationId)
            } else if ((secondaryModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.IDLE
                        && secondaryModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED)
                && primaryModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.IDLE
            ) {
                HMILogHelper.Logd("Steam clean: Lower Oven delay is running and Upper is IDLE")
                CookingViewModelFactory.setInScopeViewModel(primaryModel)
                checkIfProbeDetectedAndNavigate(secondaryModel, navigationId)

            } else {
                navigateSafely(this, navigationId, null, null)
                HMILogHelper.Logd("Steam clean: Both cavity is IDLE : navigate to the steam cavity selection screen")
            }
        }
    }

    private fun checkIfProbeDetectedAndNavigate(
        cookingViewModel: CookingViewModel?,
        navigationId: Int
    ) {
        if (MeatProbeUtils.isMeatProbeConnected(cookingViewModel)) {
            PopUpBuilderUtils.probeIncompatiblePopup(this, onMeatProbeConditionMet = {
                navigateSafely(this, navigationId, null, null)
            })
            return
        }
        navigateSafely(this, navigationId, null, null)
    }

    override fun onListItemDeleteClick(view: View?, position: Int) {
    }

    override fun onListItemImageClick(view: View?, position: Int) {
        onGridItemClickEvent(position)
    }

    override fun onControlLockToggled(isChecked: Boolean, gridListItemModel: GridListItemModel) {
        HMILogHelper.Logd("Control Lock: $isChecked")
        if (isChecked) {
            PopUpBuilderUtils.showControlLockPopup(this, controlLockToggle = {
                gridListItemModel.apply {
                    isSelected = false
                    isActive = true
                }
                fragmentSettingsLandingBinding?.settingsRecyclerGridList?.adapter?.notifyItemChanged(
                    0
                )
            })
        }
    }

    private fun showNotification(messageResId: Int) {
        fragmentSettingsLandingBinding?.drwawerbar?.apply {
            isVisible = true
            showNotification(
                getString(messageResId),
                DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT,
                this
            )
        }
    }

    override fun showNotificationLockNotAvailable() {
        showNotification(R.string.text_feature_unavailable_demo_mode)
    }

    override fun showNotificationDemoNotAvailableInTechnicianMode() {
        showNotification(R.string.text_feature_unavailable_technician_mode)
    }

    override fun onMuteToggled(isChecked: Boolean) {

    }

    override fun onRemoteEnableToggled(isChecked: Boolean) {
        ToastUtils.showToast(requireContext(), "Remote Enable: $isChecked")
    }

    override fun leftIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }

    override fun rightIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }

    override fun onHMILeftKnobClick() {
        if (lastItemSelectedPos != -1) {
            val gridItemCount = settingsGridListTileData.size
            val isGridView = lastItemSelectedPos < gridItemCount
            KnobNavigationUtils.knobForwardTrace = true
            KnobNavigationUtils.addTraversingData(lastItemSelectedPos, false)
            if (isGridView) {
                onGridItemClickEvent(lastItemSelectedPos)
            } else {
                onListItemClickEvent(lastItemSelectedPos - gridItemCount)
            }
        }
    }


    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (knobId == AppConstants.LEFT_KNOB_ID) {
                    val gridItemCount = settingsGridListTileData.size
                    val listItemCount = settingsListTileMoreData.size
                    val totalItemCount = gridItemCount + listItemCount

                    HMILogHelper.Logd("Current position before = $currentPosition, Total Item Count = $totalItemCount")

                    // Calculate the new position based on knob direction
                    val newPosition = CookingAppUtils.getKnobPositionIndex(
                        knobDirection,
                        currentPosition,
                        totalItemCount
                    )

                    // Validate newPosition
                    if (newPosition in 0 until totalItemCount) {
                        currentPosition = newPosition

                        // Adjust position if it points to the list and headingText is empty
                        if (currentPosition >= gridItemCount) {
                            val listPosition = currentPosition - gridItemCount
                            if (settingsListTileMoreData[listPosition].headingText.isNotEmpty()) {
                                currentPosition = adjustPosition(knobDirection, totalItemCount)
                            }
                        }

                        currentPosition = currentPosition.coerceIn(0, totalItemCount - 1)
                        HMILogHelper.Logd("Current position after = $currentPosition")

                        // Determine view type and update selection
                        val isGridView = currentPosition < gridItemCount
                        updateSelectedItem(currentPosition, isGridView)
                    } else {
                        resetPosition()
                    }
                } else {
                    resetPosition()
                }
            }
        }
    }

    private fun adjustPosition(knobDirection: String, totalItemCount: Int): Int {
        when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION -> {
                currentPosition++
            }

            KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                currentPosition--
                if (currentPosition < 0) {
                    currentPosition = totalItemCount - 1
                }
            }
        }
        return currentPosition
    }

    private fun resetPosition() {
        HMILogHelper.Logd("Current position out of bounds: resetting to 0")
        currentPosition = 0
    }

    private fun updateSelectedItem(position: Int, isGridView: Boolean) {
        // Clear last selection
        if (lastItemSelectedPos != -1) {
            clearLastSelection(lastItemSelectedPos)
        }
        lastItemSelectedPos = position
        highlightNewSelection(position, isGridView)
        scrollToItem(position, isGridView)
    }

    private fun clearLastSelection(position: Int) {
        if (position in 0 until settingsGridListTileData.size) {
            settingsGridListTileData[position].isSelected = false
            notifyGridItemChanged(position)
        } else {
            val listPosition = position - settingsGridListTileData.size
            if (listPosition in 0 until settingsListTileMoreData.size) {
                fragmentSettingsLandingBinding?.settingsRecyclerList?.get(listPosition)
                    ?.setBackgroundColor(requireContext().getColor(R.color.cavity_selection_button_background))
            }
        }
    }

    private fun highlightNewSelection(position: Int, isGridView: Boolean) {
        if (isGridView && position in 0 until settingsGridListTileData.size) {
            settingsGridListTileData[position].isSelected = true
            notifyGridItemChanged(position)
        } else if (!isGridView) {
            val listPosition = position - settingsGridListTileData.size
            if (listPosition in 0 until settingsListTileMoreData.size) {
                fragmentSettingsLandingBinding?.settingsRecyclerList?.get(listPosition)
                    ?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
            }
        }
    }

    private fun scrollToItem(position: Int, isGridView: Boolean) {
        val itemTopY = if (isGridView) {
            fragmentSettingsLandingBinding?.settingsRecyclerGridList?.top
        } else {
            fragmentSettingsLandingBinding?.settingsRecyclerList?.get(position - settingsGridListTileData.size)?.top
        }
        itemTopY?.let {
            fragmentSettingsLandingBinding?.nestedScrollViewCollection?.smoothScrollTo(0, it)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (lastItemSelectedPos != -1) {
                clearLastSelection(lastItemSelectedPos)
            }
            lastItemSelectedPos = -1
        }
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    private fun isSelfCleanAllowed(onMeatProbeConditionMet: () -> Unit): Boolean {
        return if ((CookingViewModelFactory.getOutOfScopeCookingViewModel() != null
                    && CookingViewModelFactory.getOutOfScopeCookingViewModel().recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.IDLE) ||
            (CookingViewModelFactory.getInScopeViewModel() != null
                    && CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.IDLE)
        ) {
            PopUpBuilderUtils.featureNotAvailablePopUp(this)
            false
        } else if (MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getInScopeViewModel()) || MeatProbeUtils.isMeatProbeConnected(
                CookingViewModelFactory.getOutOfScopeCookingViewModel()
            )
        ) {
            removeProbeToStartSelfClean(this, onMeatProbeConditionMet = {
                onMeatProbeConditionMet()
            })
            false
        } else {
            onMeatProbeConditionMet()
            true
        }
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    private fun updateNetworkInformation(listTileData: ListTileData) {
        getSettingsViewModel().wifiConnectState.observe(
            viewLifecycleOwner
        ) { state: Int ->
            if (getSettingsViewModel().isWifiEnabled) {
                listTileData.apply {
                    rightIconVisibility = View.VISIBLE
                    itemIconID = R.drawable.ic_network
                    navigationID = R.id.action_settingsLandingFragment_to_provisioningBleConnect
                }
                updateWifiSsid(listTileData)
                HMILogHelper.Logd("## Connectivity - WifiConnectState:", state.toString())
                when (state) {
                    SettingsViewModel.WifiConnectState.CONNECTED -> {
                        updateCloudStatusInformation(listTileData)
                    }

                    SettingsViewModel.WifiConnectState.CONNECTING -> {
//                        Icon update
                    }

                    SettingsViewModel.WifiConnectState.DISCONNECTED, SettingsViewModel.WifiConnectState.AUTH_FAILED, SettingsViewModel.WifiConnectState.UNKNOWN, SettingsViewModel.WifiConnectState.MISSING_INFO, SettingsViewModel.WifiConnectState.AP_NOT_FOUND -> {
//                        Icon update
                        updateWifiSsid(listTileData)
                    }

                    else -> {}

                }
            } else {
                updateWifiNotAvailableLayout(listTileData)
            }
            notifyItemChanged(CONNECT_TO_NETWORK_TILE_INDEX)
        }
    }

    private fun updateWifiNotAvailableLayout(listTileData: ListTileData): ListTileData {
        val switchData: ListTileData.ToggleSwitchData = ListTileData.ToggleSwitchData()
        if (isApplianceProvisioned()) {
            listTileData.apply {
                titleText = (getString(R.string.text_subMenu_network_wifi))
                rightTextVisibility = View.GONE
                rightIconVisibility = View.GONE
                itemIconID = R.drawable.ic_40px_connected
            }
            switchData.apply {
                visibility = View.VISIBLE
                isEnabled = true
                isChecked = getSettingsViewModel().isWifiEnabled
            }
        } else {
            listTileData.apply {
                titleText = (getString(R.string.connect_to_network))
                itemIconID = R.drawable.ic_network
            }
            switchData.visibility = View.GONE
        }
        listTileData.apply {
            navigationID = R.id.action_settingsLandingFragment_to_provisioningBleConnect
            toggleSwitchData = switchData
        }
        return listTileData
    }

    override fun onToggleSwitchClick(position: Int, isChecked: Boolean) {
        if (position == CONNECT_TO_NETWORK_TILE_INDEX) {
            val listTileData = settingsListTileData[position]
            val switchData = listTileData.toggleSwitchData
            getSettingsViewModel().isWifiEnabled = isChecked
            if (isChecked) {
                switchData.visibility = View.GONE
                val provisionedWifiSsid =
                    getSettingsViewModel().provisionedWifiSsid
                if (provisionedWifiSsid != null && provisionedWifiSsid != AppConstants.EMPTY_STRING) {
                    getSettingsViewModel().setTimeModeAuto()
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        CookingAppUtils.startGattServer(this)
                    }, this.resources.getInteger(R.integer.ms_500).toLong())
                }
            } else {
                switchData.visibility = View.VISIBLE
            }
            updateNetworkInformation(listTileData)
            notifyItemChanged(CONNECT_TO_NETWORK_TILE_INDEX)
        }
    }

    private fun updateCloudStatusInformation(listTileData: ListTileData) {
        getSettingsViewModel().awsConnectionStatus.observe(
            viewLifecycleOwner
        ) { cloudConnectionState: Int ->
            HMILogHelper.Logd(
                "## Connectivity - CloudConnectionState:",
                cloudConnectionState.toString()
            )
            when (cloudConnectionState) {
                SettingsViewModel.CloudConnectionState.CONNECTING, SettingsViewModel.CloudConnectionState.RECONNECTING ->
                    if (isBleProvisionSuccess) {
//                      Icon update
                    }

                SettingsViewModel.CloudConnectionState.IDLE, SettingsViewModel.CloudConnectionState.FAILED_TO_CONNECT -> {
//                      Icon update
                }

                SettingsViewModel.CloudConnectionState.CONNECTED -> {
//                      Icon update
                }

                else -> {}
            }
            notifyItemChanged(CONNECT_TO_NETWORK_TILE_INDEX)
        }
    }

    private fun updateWifiSsid(listTileData: ListTileData) {
        if (SettingsViewModel.getWifiSsid() != null) {
            listTileData.titleText = (SettingsViewModel.getWifiSsid())
        } else if (isApplianceProvisioned()) {
            listTileData.titleText = (getSettingsViewModel().provisionedWifiSsid)
        } else {
            listTileData.apply {
                titleText = (getString(R.string.connect_to_network))
                navigationID = R.id.action_settingsLandingFragment_to_provisioningBleConnect
            }
        }
    }

    /**
     * Method to notify the item changed for recycler view adapter
     */
    private fun notifyItemChanged(position: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            fragmentSettingsLandingBinding?.settingsRecyclerList?.adapter?.notifyItemChanged(
                position
            )
        }
    }

    /**
     * Method to notify the item changed for recycler view adapter
     */
    private fun notifyGridItemChanged(position: Int) {
        fragmentSettingsLandingBinding?.settingsRecyclerGridList?.adapter?.notifyItemChanged(
            position
        )
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
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        MeatProbeUtils.removeMeatProbeListener()
        settingsListItems?.clear()
        settingsListItems = null
        settingsGridItems?.clear()
        settingsGridItems = null
        settingsListTileData.clear()
        settingsListTileMoreData.clear()
        settingsGridListTileData.clear()
        getSettingsViewModel().wifiConnectState.removeObservers(this)
        getSettingsViewModel().awsConnectionStatus.removeObservers(this)
        getSettingsViewModel().awsProvisioningStatus.removeObservers(this)
    }
}