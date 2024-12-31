/*
* ************************************************************************************************
* ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
* ************************************************************************************************
*/
package android.presenter.fragments.settings


import android.content.Context
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.SettingsListViewHolderInterface
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.toggleswitch.ToggleSwitch
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentConnectivityFragmentBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.ota.viewmodel.OTAViewModel
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.BuildInfo
import com.whirlpool.hmi.utils.ContextProvider
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getKitchenTimerCancelPopupDescription
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.SettingsManagerUtils.isBleProvisionSuccess
import core.utils.SharedPreferenceManager
import core.utils.ToolsMenuJsonKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * File        : android.presenter.fragments.settings.ConnectivityListFragment
 * Brief       : Instance of Abstract List fragment to represent the Connectivity Menu List Screen
 * Author      : Nikki
 * Created On  : 15-July-2022
 * Details     : Instance of Abstract List fragment to represent the Connectivity Menu List Screen.
 */
open class ConnectivityListFragment : SuperAbstractTimeoutEnableFragment(),
    SettingsListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener {
    private var connectivitySettingsList: ArrayList<ListTileData>? = null
    private var canScrollVertically = true
    private var wifiConnectionErrorPopup: ScrollDialogPopupBuilder? = null
    private var fragmentConnectivityListFragment: FragmentConnectivityFragmentBinding? = null
    private var preferencesListItems: ArrayList<String>? = null
    private var lastItemSelectedPos = -1
    private var currentPosition = -1
    private var allItemSize = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        fragmentConnectivityListFragment = FragmentConnectivityFragmentBinding.inflate(inflater)
        fragmentConnectivityListFragment?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentConnectivityListFragment?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        initHeaderBar()
        manageListView()
        observeOTAStateAndUpdateSoftwareMenu()
        observeWifiConnection()
        HMILogHelper.Logd("OTA","OTA : OTA is not running, setting appliance -> busy")
        OTAVMFactory.getOTAViewModel().setApplianceBusyState(true)
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highLightSelectedTiles()
        }
    }

    override fun onResume() {
        super.onResume()
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SETTING_LANDING)
    }

    private fun initHeaderBar() {
        fragmentConnectivityListFragment?.headerBarPreferences?.setLeftIcon(R.drawable.ic_back_arrow)
        fragmentConnectivityListFragment?.headerBarPreferences?.setTitleText(getString(R.string.network_settings))
        fragmentConnectivityListFragment?.headerBarPreferences?.setOvenCavityIconVisibility(false)
        fragmentConnectivityListFragment?.headerBarPreferences?.setInfoIconVisibility(false)
        fragmentConnectivityListFragment?.headerBarPreferences?.setRightIconVisibility(true)
        fragmentConnectivityListFragment?.headerBarPreferences?.setCustomOnClickListener(this)
    }

    private fun manageListView() {
        if (!SettingsViewModel.getSettingsViewModel().isWifiEnabled) {
            fragmentConnectivityListFragment?.connectivityRecyclerList?.isVerticalScrollBarEnabled =
                false
            canScrollVertically = false
        }
        fragmentConnectivityListFragment?.connectivityRecyclerList?.setLayoutManager(
            LinearLayoutManagerForScrolling(
                context
            )
        )
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SHOW_ALL_CONNECTIVITY)
            ?.let {
                preferencesListItems = it
            }
        preferencesListItems?.let {
            fragmentConnectivityListFragment?.connectivityRecyclerList?.visibility = View.VISIBLE
            val listTileData= provideListRecyclerViewTilesData()
            allItemSize = listTileData?.size?:0
            listTileData.let {
                val listItems: ArrayList<Any> = ArrayList(it!!)
                val toolsListViewInterface =
                    listTileData?.let { it1 ->
                        SettingsListViewHolderInterface(
                            it1, this
                        )
                    }
                fragmentConnectivityListFragment?.connectivityRecyclerList?.setupListWithObjects(
                    listItems,
                    toolsListViewInterface
                )
            }
        }

    }

    private fun getDrawableForName(name: String): Int {
        return when (name) {
            ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_WIFI -> R.drawable.ic_wifi_settings
            ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_CONNECT_TO_NETWORK -> R.drawable.ic_network
            ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SOFTWARE_UPDATE -> R.drawable.ic_software_update
            ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_REMOTE_ENABLE -> R.drawable.ic_remote_enable_settings
            else -> R.drawable.ic_placeholder
        }
    }


    /**
     * Method to initialise the list view
     */
    fun provideListRecyclerViewTilesData(): ArrayList<ListTileData>? {
        connectivitySettingsList = ArrayList()
        fragmentConnectivityListFragment?.connectivityRecyclerList?.visibility = View.VISIBLE
        updateWifiMenu()
        updateWifiNameMenu()
        updateSoftwareMenu()
        updateRemoteMenu()
        updateSaidMenu()
        updateMacAddressMenu()
        return connectivitySettingsList
    }


    /**
     * Method to initialise the wifi Name menu based on connectivity
     */
    private fun updateWifiNameMenu() {
        connectivitySettingsList?.add(getAndUpdateWifiNameMenu())
    }

    /**
     * Method to initialise the software update menu based on connectivity
     */
    private fun observeOTAStateAndUpdateSoftwareMenu() {
        if (isBleProvisionSuccess && SettingsViewModel.getSettingsViewModel().isWifiEnabled) {
            val otaViewModel = ViewModelProvider(requireActivity())[OTAViewModel::class.java]
            otaViewModel.otaState.observe(
                getViewLifecycleOwner()
            ) { otaStatus: OTAStatus ->
                HMILogHelper.Logd("++++++++++++++++++++++++++OTA Status : $otaStatus")
                when (otaStatus) {
                    OTAStatus.AWAITING_USER_ACK, OTAStatus.ERROR,OTAStatus.BUSY -> {
                        connectivitySettingsList?.set(
                            2,
                            getAndUpdateSoftwareMenu(getString(R.string.text_helper_updateAvailable))
                        )
                        notifyItemChanged(2)
                    }

                    OTAStatus.DELAYED, OTAStatus.INSTALLING, OTAStatus.REBOOTING, OTAStatus.DOWNLOADING ->
                        //In case of OTA error code in range of 1-5 then ignore and display "UP TO DATE"
                        if (otaViewModel.errorCode
                                .value != null && (otaViewModel.errorCode
                                .value ?: 0) <= AppConstants.ERROR_CODE_IGNORE_RANGE
                        ) {
                            connectivitySettingsList?.set(
                                2,
                                getAndUpdateSoftwareMenu(getString(R.string.text_helper_upToDate))
                            )
                            notifyItemChanged(2)
                        }

                    OTAStatus.IDLE, OTAStatus.COMPLETED -> {
                        connectivitySettingsList?.set(
                            2,
                            getAndUpdateSoftwareMenu(getString(R.string.text_helper_upToDate))
                        )
                        notifyItemChanged(2)
                    }

                    else -> {
                        connectivitySettingsList?.set(
                            2,
                            getAndUpdateSoftwareMenu(getString(R.string.text_helper_upToDate))
                        )
                        notifyItemChanged(2)
                    }
                }
            }
        }
    }

    private fun getAndUpdateWifiNameMenu(): ListTileData {
        val builder = ListTileData()
        val settingsViewModel = SettingsViewModel.getSettingsViewModel()
        val provisionedWifiSsid = settingsViewModel.provisionedWifiSsid
        if (provisionedWifiSsid != null && provisionedWifiSsid != AppConstants.EMPTY_STRING) {
            builder.titleText = provisionedWifiSsid
        } else {
            if (BuildInfo.isRunningOnEmulator()) {
                if (SettingsViewModel.getWifiSsid()!=null) {
                    builder.titleText = SettingsViewModel.getWifiSsid()
                } else {
                    HMILogHelper.Loge("SettingsViewModel.getSettingsViewModel().getWifiSsid() is NULL")
                    builder.titleText = getString(R.string.connect_to_network)
                }

            } else {
                builder.titleText = getString(R.string.connect_to_network)
                builder.isClickable = CookingAppUtils.isApplianceIdleForProvisioning()
            }
        }
        builder.titleTextVisibility = View.VISIBLE
        builder.subTextVisibility = View.GONE
        builder.itemIconID = getDrawableForName(preferencesListItems?.get(1) ?: "")
        builder.itemIconVisibility = View.VISIBLE
        builder.isAllCaps = false
        builder.rightIconID = R.drawable.ic_rightarrowicon
        builder.rightIconVisibility = View.VISIBLE
        builder.itemViewVisibility = if (settingsViewModel.isWifiEnabled) View.VISIBLE else View.GONE
        val radioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        radioButtonData.visibility = View.INVISIBLE
        builder.radioButtonData = radioButtonData
        if (CookingAppUtils.isDemoModeEnabled() || CookingAppUtils.isAnyCycleRunning()
            || (SharedPreferenceManager.getTechnicianTestDoneStatusIntoPreference() == AppConstants.TRUE_CONSTANT)) {
            builder.isItemEnabled = false
        }
        observeConnectivityStatus(builder)
        return builder
    }

    private fun getAndUpdateSoftwareMenu(updateStatus: String): ListTileData {
        val builder = ListTileData()
        builder.titleText = getString(R.string.text_subMenu_network_softwareUpdate)
        builder.rightText = updateStatus
        builder.rightTextVisibility = View.VISIBLE
        builder.itemIconID = getDrawableForName(preferencesListItems?.get(2) ?: "")
        builder.itemIconVisibility = View.VISIBLE
        builder.subTextVisibility = View.GONE
        builder.titleTextVisibility = View.VISIBLE
        builder.rightIconID = R.drawable.ic_rightarrowicon
        builder.rightIconVisibility = View.VISIBLE
        builder.itemViewVisibility = if (SettingsViewModel.getSettingsViewModel().isWifiEnabled) View.VISIBLE else View.GONE
        val radioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        radioButtonData.visibility = View.INVISIBLE
        builder.radioButtonData = radioButtonData
        if(CookingAppUtils.isDemoModeEnabled()){
            builder.isItemEnabled = false
        }
        return builder
    }

    /**
     * Method to initialise the said menu
     */
    private fun updateSaidMenu() {
        val builder = ListTileData()
        builder.titleText = getString(R.string.text_subMenu_network_saidCode)
        builder.titleTextVisibility = View.VISIBLE
        builder.itemIconID = getDrawableForName(preferencesListItems?.get(4) ?: "")
        builder.itemIconVisibility = View.GONE
        builder.subTextVisibility = View.GONE
        builder.rightText = SettingsViewModel.getSettingsViewModel().said.value.toString()
        builder.rightTextVisibility = View.VISIBLE
        builder.rightIconVisibility = View.GONE
        builder.itemViewVisibility =  if (SettingsViewModel.getSettingsViewModel().isWifiEnabled) View.VISIBLE else View.GONE
        if(CookingAppUtils.isDemoModeEnabled()){
            builder.isItemEnabled = false
        }
        val radioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        radioButtonData.visibility = View.GONE
        builder.radioButtonData = radioButtonData
        connectivitySettingsList?.add(builder)
    }

    /**
     * Method to initialise the mac address
     */
    private fun updateMacAddressMenu() {
        val builder = ListTileData()
        builder.titleText = getString(R.string.text_subMenu_network_macAddress)
        builder.titleTextVisibility = View.VISIBLE
        builder.itemIconID = getDrawableForName(preferencesListItems?.get(5) ?: "")
        builder.itemIconVisibility = View.GONE
        builder.subTextVisibility = View.GONE
        builder.rightText = SettingsViewModel.getMacAddr()
        builder.rightTextVisibility = View.VISIBLE
        builder.itemViewVisibility = if (SettingsViewModel.getSettingsViewModel().isWifiEnabled) View.VISIBLE else View.GONE
        builder.rightIconVisibility = View.GONE
        if(CookingAppUtils.isDemoModeEnabled()){
            builder.isItemEnabled = false
        }
        val radioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        radioButtonData.visibility = View.GONE
        builder.listItemDividerViewVisibility = View.GONE
        builder.radioButtonData = radioButtonData
        connectivitySettingsList?.add(builder)
    }

    /**
     * Method to initialise the wifi menu
     */
    private fun updateWifiMenu() {
        connectivitySettingsList?.add(getWifiMenuListBuilder(SettingsViewModel.getSettingsViewModel().isWifiEnabled))
    }
    /**
     * Method to initialise the remote enable menu
     */
    private fun updateRemoteMenu() {
        //TODO: Add enable method and removed isWifiEnabled check
        SettingsViewModel.getSettingsViewModel().remoteStartEnable.value?.let {
            getRemoteEnableMenuListBuilder(
                it
            )
        }?.let { connectivitySettingsList?.add(it) }
    }

    /**
     * Method to initialise the wifi menu
     */
    private fun updateSoftwareMenu() {
        if (isBleProvisionSuccess) connectivitySettingsList?.add(
            getAndUpdateSoftwareMenu(AppConstants.EMPTY_STRING)
        )
    }


    /**
     * Method to build the wifi status menu based on its current state
     *
     * @param isWifiEnabled true if wifi is enabled
     * @return list item build
     */
    private fun getWifiMenuListBuilder(isWifiEnabled: Boolean): ListTileData {
        val builder = ListTileData()
        val switchData: ListTileData.ToggleSwitchData = ListTileData.ToggleSwitchData()
        switchData.visibility = View.VISIBLE
        switchData.isEnabled = true
        switchData.isChecked = isWifiEnabled
        builder.titleText = getString(R.string.diagnostics_label_wifi)
        builder.itemIconID = getDrawableForName(preferencesListItems?.get(0) ?: "")
        builder.itemIconVisibility = View.VISIBLE
        builder.titleTextVisibility = View.VISIBLE
        builder.subTextVisibility = View.GONE
        builder.rightTextVisibility = View.VISIBLE
        builder.listItemDividerViewVisibility = if (isWifiEnabled) View.VISIBLE else View.GONE
        builder.toggleSwitchData = switchData
        builder.isItemEnabled = true
        val radioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        radioButtonData.visibility = View.INVISIBLE
        builder.radioButtonData = radioButtonData
        if(CookingAppUtils.isDemoModeEnabled()){
            builder.isItemEnabled = false
        }
        return builder
    }

    /**
     * Method to build the wifi status menu based on its current state
     *
     * @param isWifiEnabled true if wifi is enabled
     * @return list item build
     */
    private fun getRemoteEnableMenuListBuilder(isWifiEnabled: Boolean): ListTileData {
        val builder = ListTileData()
        val switchData: ListTileData.ToggleSwitchData = ListTileData.ToggleSwitchData()
        switchData.visibility = View.VISIBLE
        switchData.isEnabled = true
        switchData.isChecked = isWifiEnabled
        builder.titleText = getString(R.string.remote_enable)
        builder.itemIconID = getDrawableForName(preferencesListItems?.get(3) ?: "")
        builder.itemIconVisibility = View.VISIBLE
        builder.titleTextVisibility = View.VISIBLE
        builder.subTextVisibility = View.GONE
        builder.rightTextVisibility = View.VISIBLE
        builder.toggleSwitchData = switchData
        builder.isItemEnabled = true
        builder.isAllCaps = false
        builder.itemViewVisibility = if (SettingsViewModel.getSettingsViewModel().isWifiEnabled) View.VISIBLE else View.GONE
        val radioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        radioButtonData.visibility = View.INVISIBLE
        builder.radioButtonData = radioButtonData
        if(CookingAppUtils.isDemoModeEnabled()){
            builder.isItemEnabled = false
        }
        return builder
    }


    override fun onToggleSwitchClick(position: Int, isChecked: Boolean) {
        val toggle =  fragmentConnectivityListFragment?.connectivityRecyclerList?.
        findViewHolderForAdapterPosition(position)?.itemView?.findViewById<ToggleSwitch>(R.id.settings_item_toggle_switch)
        val toggleSwitch =
            toggle?.findViewById<SwitchCompat>(R.id.toggle_switch)

        if (toggleSwitch != null) {
            if(toggleSwitch.isChecked){
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.toggle_off,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
            }else{
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.toggle_on,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
            }
        }

        if(CookingAppUtils.isDemoModeEnabled()){
            toggleSwitch?.isChecked = !isChecked
            fragmentConnectivityListFragment?.drwawerbar?.isVisible = true
            fragmentConnectivityListFragment?.drwawerbar?.showNotification(getString(R.string.text_feature_unavailable_demo_mode), DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT,
                fragmentConnectivityListFragment?.drwawerbar)
            return
        }

        if (position == resources.getInteger(R.integer.integer_range_0)) {
            SettingsViewModel.getSettingsViewModel().isWifiEnabled = isChecked
            if (isChecked) {
                val provisionedWifiSsid =
                    SettingsViewModel.getSettingsViewModel().provisionedWifiSsid
                if (provisionedWifiSsid != null && provisionedWifiSsid != AppConstants.EMPTY_STRING) {
                    SettingsViewModel.getSettingsViewModel().setTimeModeAuto()
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        CookingAppUtils.startGattServer(this)
                    }, this.resources.getInteger(R.integer.ms_500).toLong())
                }
                fragmentConnectivityListFragment?.connectivityRecyclerList?.isVerticalScrollBarEnabled =
                    true
                canScrollVertically = true
            } else {
                CookingAppUtils.stopGattServer()
                fragmentConnectivityListFragment?.connectivityRecyclerList?.isVerticalScrollBarEnabled =
                    false
                canScrollVertically = false
            }

            if (connectivitySettingsList != null) {
                for (i in 0 until (connectivitySettingsList?.size ?: 0)) {
                    val listTileData: ListTileData? = connectivitySettingsList?.get(i)
                    if (i == 0) {
                        listTileData?.itemViewVisibility = View.VISIBLE
                        listTileData?.toggleSwitchData?.isChecked = isChecked
                        listTileData?.listItemDividerViewVisibility =
                            (if (isChecked) View.VISIBLE else View.GONE)
                    } else {
                        listTileData?.itemViewVisibility =
                            (if (isChecked) View.VISIBLE else View.GONE)
                    }
                    if (listTileData?.titleText.equals(
                            getString(R.string.connect_to_network),
                            true
                        )
                    ) {
                        listTileData?.isClickable =
                            isChecked && CookingAppUtils.isApplianceIdleForProvisioning()
                    }
                }
                fragmentConnectivityListFragment?.connectivityRecyclerList?.post {
                    fragmentConnectivityListFragment?.connectivityRecyclerList?.adapter?.notifyDataSetChanged()
                }
            }
        }else if (position == resources.getInteger(R.integer.integer_range_2) || position == resources.getInteger(R.integer.integer_range_3)){
            val isRemoteStartEnabled = SettingsViewModel.getSettingsViewModel().getRemoteStartEnable().getValue() == true
            if (isRemoteStartEnabled || (SettingsViewModel.getSettingsViewModel().getAWSConnectionStatus()
                    .getValue() == SettingsViewModel.CloudConnectionState.CONNECTED && isBleProvisionSuccess)
            ) {
                SettingsViewModel.getSettingsViewModel().setRemoteStartEnable(isChecked)
            } else{
                PopUpBuilderUtils.wifiSetUpPopupBuilder(this)
                toggleSwitch?.isChecked = !isChecked
            }
        }
    }


    override fun onListViewItemClick(view: View?, position: Int) {
        if(CookingAppUtils.isDemoModeEnabled()){
            fragmentConnectivityListFragment?.drwawerbar?.isVisible = true
            fragmentConnectivityListFragment?.drwawerbar?.showNotification(getString(R.string.text_feature_unavailable_demo_mode), DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT,
                fragmentConnectivityListFragment?.drwawerbar)
            return
        }
        if (position == 1) {
            if (SharedPreferenceManager.getTechnicianTestDoneStatusIntoPreference() == AppConstants.TRUE_CONSTANT) {
                showNotificationDemoNotAvailableInTechnicianMode()
                return
            }
            //To verify the WIFI is connected or not, check SSID value instead of the AWS connection status. SSID value is present only when it is connected to WIFI. Once User
            // request "Forget Network" then SSID value cleared. SDK approach to use getProvisionedWifiSsid() to make the decision for navigating the provisioning flow.
            if (isBleProvisionSuccess || SettingsManagerUtils.isApplianceProvisioned()) {
                val bundle = Bundle().apply { putString(
                    AppConstants.SOURCE_FRAGMENT,
                    AppConstants.CONNECTIVITYLIST_FRAGMENT
                ) }
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_connectivityListFragment_to_networkDetailsInformationFragment,
                    bundle,
                    null
                )
            } else if (!CookingAppUtils.isApplianceIdleForProvisioning()) {
                PopUpBuilderUtils.provisioningUnavailablePopup(
                    this,
                    R.string.text_header_appliance_is_busy,
                    getString(R.string.text_description_try_again_later)
                )
            } else if (KitchenTimerUtils.isKitchenTimersRunning() != 0) {
                PopUpBuilderUtils.provisioningUnavailablePopup(
                    this,
                    R.string.text_header_cancel_timer,
                    getKitchenTimerCancelPopupDescription(this)
                )
            } else {
                CookingAppUtils.startProvisioning(NavigationUtils.getVisibleFragment()?.let {
                    NavigationUtils.getViewSafely(
                        it
                    )
                }, false, isFromConnectivityScreen = true, false)
            }
        }

        if ((connectivitySettingsList?.get(position)?.titleText
                ?: "") == getString(R.string.text_subMenu_network_softwareUpdate)
        ) {
            HMILogHelper.Logd(
                "OTA",
                "OTA :AWS connection status = ${SettingsViewModel.getSettingsViewModel().awsConnectionStatus.value}"
            )
            if (SettingsViewModel.getSettingsViewModel().awsConnectionStatus.value ==
                SettingsViewModel.CloudConnectionState.CONNECTED
            ) {
                if ((OTAVMFactory.getOTAViewModel().otaState.value == OTAStatus.BUSY ||
                            OTAVMFactory.getOTAViewModel().otaState.value == OTAStatus.ERROR)
                    && !CookingAppUtils.checkApplianceBusyStateForOTA()) {
                    NavigationUtils.navigateSafely(
                        this,
                        R.id.action_connectivityListFragment_to_otaBusyUpdateAvailableViewHolder,
                        null,
                        null
                    )
                } else {
                    requireActivity().supportFragmentManager.setFragmentResult(
                        AppConstants.CONNECTIVITYLIST_FRAGMENT, bundleOf(
                            AppConstants.CONNECTIVITYLIST_FRAGMENT to true)
                    )
                    PopUpBuilderUtils.initAndStartOtaFlow(this, true)
                }
            } else {
                try {
                    showConfirmationPopup()
                } catch (exp: Exception) {
                    HMILogHelper.Logd("Software update : onClick$exp")
                }

            }
        }
    }

    private fun showConfirmationPopup() {
        if (wifiConnectionErrorPopup == null) {
            HMILogHelper.Logd("showing wifi connection error popup")
            wifiConnectionErrorPopup =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_ota_popup_fragment)
                    .setHeaderTitle(R.string.text_header_wifi_error)
                    .setDescriptionMessage(R.string.text_description_wifi_error)
                    .setRightButton(R.string.text_button_ok) {
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        true
                    }
                    .setTopMarginForTitleText(AppConstants.POPUP_OTA_CD_TITLE_TOP_SMALL_MARGIN)
                    .setTopMarginForDescriptionText(AppConstants.POPUP_OTA_CD_DESCRIPTION_TOP_SMALL_MARGIN)
                    .setHeaderViewCenterIcon(AppConstants.HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                    .build()
        }

        //Knob Implementation
        val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
            onHMIRightKnobClick = {
                wifiConnectionErrorPopup?.onHMIRightKnobClick()
            }, onKnobSelectionTimeout = {}
        )
        wifiConnectionErrorPopup?.setOnDialogCreatedListener(object :
            ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                //TODO:Audio asset need to update once GCD finalize the audio assets
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.audio_alert,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
            }

            override fun onDialogDestroy() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                CookingAppUtils.setHmiKnobListenerAfterDismissDialog(this@ConnectivityListFragment)
                if (wifiConnectionErrorPopup != null) {
                    wifiConnectionErrorPopup?.dismiss()
                    wifiConnectionErrorPopup = null
                }
            }
        })
        wifiConnectionErrorPopup?.show(parentFragmentManager, "WIFI_ERROR_CONFIRMATION_POPUP")
    }

    private fun observeWifiConnection() {
        SettingsViewModel.getSettingsViewModel().wifiConnectState.observe(
            getViewLifecycleOwner()
        ) {
            connectivitySettingsList?.set(1, getAndUpdateWifiNameMenu())
            notifyItemChanged(1)
        }
    }

    private inner class LinearLayoutManagerForScrolling(context: Context?) :
        LinearLayoutManager(context) {
        override fun canScrollVertically(): Boolean {
            return canScrollVertically && super.canScrollVertically()
        }
    }

    override fun leftIconOnClick() {
        lifecycleScope.launch(Dispatchers.Main) {
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                NavigationUtils.navigateSafely(
                    it,
                    R.id.settingsLandingFragment,
                    null,
                    null
                )
            }
        }
    }
    override fun rightIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
    }
    override fun provideScreenTimeoutValueInSeconds(): Int {
        return resources.getInteger(R.integer.session_long_timeout)
    }
    /**
     * Method to notify the item changed for recycler view adapter
     */
    private fun notifyItemChanged(position: Int) {
        if (fragmentConnectivityListFragment?.connectivityRecyclerList?.adapter != null) {
            fragmentConnectivityListFragment?.connectivityRecyclerList?.adapter?.notifyItemChanged(position)
        }
    }
    /**
     * Method to notify the set of list changed for recycler view adapter
     */
    private fun notifyItemRangeSetChanged(positionStart: Int = 1, positionEnd: Int) {
        if (fragmentConnectivityListFragment?.connectivityRecyclerList?.adapter != null) {
            fragmentConnectivityListFragment?.connectivityRecyclerList?.adapter?.notifyItemRangeChanged(
                positionStart,
                positionEnd
            )
        }
    }

    override fun onHMILeftKnobClick() {
        if (lastItemSelectedPos != -1) {
            lifecycleScope.launch(Dispatchers.Main) {
                KnobNavigationUtils.knobForwardTrace = true
                KnobNavigationUtils.addTraversingData(lastItemSelectedPos, false)
                val toggle =
                    fragmentConnectivityListFragment?.connectivityRecyclerList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )?.itemView?.findViewById<ToggleSwitch>(R.id.settings_item_toggle_switch)

                if (toggle?.visibility == View.VISIBLE) {
                    val toggleSwitch = toggle.findViewById<SwitchCompat>(R.id.toggle_switch)
                    toggleSwitch.isChecked = !toggleSwitch.isChecked
                } else {
                    fragmentConnectivityListFragment?.connectivityRecyclerList?.findViewHolderForAdapterPosition(
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
                    if (currentPosition >= 0 && SettingsViewModel.getSettingsViewModel()?.isWifiEnabled == true) {
                        HMILogHelper.Logd(
                            "Knob",
                            "LEFT_KNOB: rotate right current knob index = $currentPosition"
                        )
                        fragmentConnectivityListFragment?.connectivityRecyclerList?.smoothScrollToPosition(
                            currentPosition
                        )
                        highLightSelectedTiles()

                    }else{
                        HMILogHelper.Logd("Knob","LEFT_KNOB: rotate left current knob index = $currentPosition")
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
                val viewHolder = fragmentConnectivityListFragment?.connectivityRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = -1
        }
    }

    private fun highLightSelectedTiles() {
        fragmentConnectivityListFragment?.connectivityRecyclerList?.postDelayed({
            if (lastItemSelectedPos != -1) {
                val viewHolder = fragmentConnectivityListFragment?.connectivityRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = currentPosition
            val viewHolderOld = fragmentConnectivityListFragment?.connectivityRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }, 50) // Adjust delay as needed
    }

    override fun onDestroyView() {
        SettingsViewModel.getSettingsViewModel().wifiConnectState.removeObservers(this)
        SettingsViewModel.getSettingsViewModel().awsConnectionStatus.removeObservers(this)
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        super.onDestroyView()
    }

    private fun observeConnectivityStatus(builder: ListTileData ) {
        if (SettingsViewModel.getSettingsViewModel().isWifiEnabled) {
            SettingsViewModel.getSettingsViewModel().wifiConnectState.observe(
                viewLifecycleOwner
            ) { state: Int ->
                updateWifiSsid(builder)
                HMILogHelper.Logd("## Connectivity - WifiConnectState:", state.toString())
                when (state) {
                    SettingsViewModel.WifiConnectState.CONNECTED -> updateCloudStatusInformation(builder)
                    SettingsViewModel.WifiConnectState.CONNECTING -> {
//                      Icon update
                    }

                    SettingsViewModel.WifiConnectState.DISCONNECTED, SettingsViewModel.WifiConnectState.AUTH_FAILED,SettingsViewModel.WifiConnectState.UNKNOWN, SettingsViewModel.WifiConnectState.MISSING_INFO, SettingsViewModel.WifiConnectState.AP_NOT_FOUND -> {
//                       Icon Update
                        updateWifiSsid(builder)
                    }

                    else -> {}
                }
                notifyItemChanged(1)
            }
        }
    }
    private fun updateCloudStatusInformation(builder: ListTileData) {
        SettingsViewModel.getSettingsViewModel().awsConnectionStatus.observe(
            viewLifecycleOwner
        ) { cloudConnectionState: Int ->
            HMILogHelper.Logd(
                "## Connectivity - CloudConnectionState:",
                cloudConnectionState.toString()
            )
            when (cloudConnectionState) {
                SettingsViewModel.CloudConnectionState.CONNECTING, SettingsViewModel.CloudConnectionState.RECONNECTING -> if (isBleProvisionSuccess) {
//                   Icon Update
                }

                SettingsViewModel.CloudConnectionState.IDLE, SettingsViewModel.CloudConnectionState.FAILED_TO_CONNECT -> {
//                   Icon Update
                }

                SettingsViewModel.CloudConnectionState.CONNECTED -> {
//                    Icon Update
                }

                else -> {}
            }
            notifyItemChanged(1)
        }
    }

    private fun updateWifiSsid(builder: ListTileData) {
        when {
            SettingsViewModel.getWifiSsid() != null -> {
                builder.titleText = SettingsViewModel.getWifiSsid()
            }
            SettingsManagerUtils.isApplianceProvisioned() -> {
                builder.titleText = SettingsViewModel.getSettingsViewModel().provisionedWifiSsid
            }
            else -> {
                builder.titleText = getString(R.string.connect_to_network)
                builder.navigationID = R.id.action_connectivityListFragment_to_networkDetailsInformationFragment
            }
        }
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(CookingViewModelFactory.getInScopeViewModel() == null) {
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
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true  && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }
    private fun showNotificationDemoNotAvailableInTechnicianMode(){
        fragmentConnectivityListFragment?.drwawerbar?.isVisible = true
        fragmentConnectivityListFragment?.drwawerbar?.showNotification(getString(R.string.text_feature_unavailable_technician_mode), DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT,
            fragmentConnectivityListFragment?.drwawerbar)
    }
}