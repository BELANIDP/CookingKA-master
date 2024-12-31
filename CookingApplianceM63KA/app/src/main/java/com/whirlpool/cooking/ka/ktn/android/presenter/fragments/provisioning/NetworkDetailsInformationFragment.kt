package android.presenter.fragments.provisioning

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.presenter.customviews.listView.AbstractListFragment
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.ListViewHolderInterface
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.view.Gravity
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.BuildInfo
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AppConstants.CONNECTIVITYLIST_FRAGMENT
import core.utils.AppConstants.SOURCE_FRAGMENT
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.updatePopUpLeftTextButtonBackground
import core.utils.CookingAppUtils.Companion.updatePopUpRightTextButtonBackground
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils

/**
 * File       : com.whirlpool.cooking.provisioning.NetworkDetailsInformationFragment
 * Brief      : Handles show network details
 * Author     : Vishal
 * Created On : 15-05-2024
 */
class NetworkDetailsInformationFragment : AbstractListFragment(), ListViewHolderInterface.ListItemClickListener {

    override fun setUpViews() {
    }

    /**
     * Brief      : Handles Forget Network popup
     */
    private fun forgetNetworkPopupBuilder() {
        val dialogPopupBuilder =
            ScrollDialogPopupBuilder.Builder(R.layout.layout_ota_popup_fragment)
                .setDescriptionMessage(R.string.text_description_forget_network)
                .setHeaderTitle(R.string.text_header_forget_network)
                .setIsLeftButtonEnable(true)
                .setIsRightButtonEnable(true)
                .setLeftButton(R.string.text_button_cancel) {
                    true
                }
                .setTopMarginForTitleText(AppConstants.POPUP_TITLE_TOP_SMALL_MARGIN)
                .setHeaderViewCenterIcon(AppConstants.HEADER_VIEW_CENTER_ICON_GONE, false)
                .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                .setRightButton(R.string.text_button_proceed) {
                    SettingsViewModel.getSettingsViewModel().resetWifiAndCloud()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // Android 12 (SDK 31) or higher
                        if (ContextCompat.checkSelfPermission(
                                ContextProvider.getApplication(),
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            HMILogHelper.Logw("we do not reset BLE for Android SDK " + Build.VERSION.SDK_INT + " if we don't have BLUETOOTH_CONNECT permission granted")
                        } else {
                            HMILogHelper.Logi("calling resetBLE for Android SDK " + Build.VERSION.SDK_INT + " when we have BLUETOOTH_CONNECT permission granted")
                            SettingsViewModel.getSettingsViewModel().resetBLE()
                        }
                    } else {
                        SettingsViewModel.getSettingsViewModel().resetBLE()
                    }
                    SettingsViewModel.getSettingsViewModel().setRemoteStartEnable(false)
                    CookingAppUtils.startGattServer(this)
                    if (arguments?.getString(SOURCE_FRAGMENT) == CONNECTIVITYLIST_FRAGMENT){
                        navigateSafely(this, R.id.action_networkDetailsInformationFragment_to_connectivityListFragment, null, null)
                    }else{
                        navigateSafely(this, R.id.action_networkDetailsInformationFragment_to_settingsLandingFragment, null, null)
                    }
                    true
                }
                .build()
        dialogPopupBuilder.setTimeoutCallback({ dialogPopupBuilder.dismiss() }, resources.getInteger(R.integer.modal_popup_timeout))
        var knobRotationCount = 0
        val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
            onKnobRotateEvent = { knobId, knobDirection ->
                HMILogHelper.Logd("forgetNetworkPopupBuilder: onKnobRotateEvent: knobId:$knobId, knobDirection:$knobDirection")
                if (knobId == AppConstants.LEFT_KNOB_ID) {
                    if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                    else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                    HMILogHelper.Logd("forgetNetworkPopupBuilder : onKnobRotateEvent: knobRotationCount:$knobRotationCount")
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            updatePopUpRightTextButtonBackground(
                                this@NetworkDetailsInformationFragment,
                                dialogPopupBuilder,
                                R.drawable.text_view_ripple_effect
                            )
                            updatePopUpLeftTextButtonBackground(
                                this@NetworkDetailsInformationFragment,
                                dialogPopupBuilder,
                                R.drawable.selector_textview_walnut
                            )
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            updatePopUpLeftTextButtonBackground(
                                this@NetworkDetailsInformationFragment,
                                dialogPopupBuilder,
                                R.drawable.text_view_ripple_effect
                            )
                            updatePopUpRightTextButtonBackground(
                                this@NetworkDetailsInformationFragment,
                                dialogPopupBuilder,
                                R.drawable.selector_textview_walnut
                            )
                        }
                    }
                }
            },
            onHMILeftKnobClick = {
                KnobNavigationUtils.knobForwardTrace = true
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        dialogPopupBuilder.onHMILeftKnobClick()
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        dialogPopupBuilder.onHMIRightKnobClick()
                    }
                }
            }, onKnobSelectionTimeout = {
                knobRotationCount = 0
                dialogPopupBuilder.provideViewHolderHelper()?.apply {
                    CookingAppUtils.setLeftAndRightButtonBackgroundNull(
                        this.leftTextButton,
                        this.rightTextButton
                    )
                }
            })
        dialogPopupBuilder.setOnDialogCreatedListener(object :
            ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                if(KnobNavigationUtils.knobForwardTrace){
                    KnobNavigationUtils.knobForwardTrace = false
                    knobRotationCount = AppConstants.KNOB_COUNTER_ONE
                    updatePopUpRightTextButtonBackground(
                        this@NetworkDetailsInformationFragment,
                        dialogPopupBuilder,
                        R.drawable.selector_textview_walnut
                    )
                }
            }

            override fun onDialogDestroy() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                CookingAppUtils.setHmiKnobListenerAfterDismissDialog(this@NetworkDetailsInformationFragment)
                dialogPopupBuilder.dismiss()
            }
        })
        dialogPopupBuilder.show(parentFragmentManager, "networkDetailsInformationPopup")
    }

    /**
     * Method to add the Network Details to a List
     */
    private fun prepareAndDisplayNetworkDetails(): ArrayList<ListTileData> {
        val settingsViewModel = SettingsViewModel.getSettingsViewModel()
        val networkDetails = ArrayList<ListTileData>()
        val radioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        radioButtonData.visibility = View.GONE
        // WiFi SSID
        var info = ListTileData()
        if (!BuildInfo.isRunningOnEmulator() && settingsViewModel.provisionedWifiSsid != null) {
            info.titleText = settingsViewModel.provisionedWifiSsid
        } else {
            info.titleText = SettingsViewModel.getWifiSsid() ?: getString(R.string.weMissedThat)
        }
        val finalInfo = info

        SettingsViewModel.getSettingsViewModel().wifiConnectState.observe(
            viewLifecycleOwner
        ) { state: Int ->
            when (state) {
                SettingsViewModel.WifiConnectState.CONNECTED -> {
                    SettingsViewModel.getSettingsViewModel().awsConnectionStatus.observe(
                        viewLifecycleOwner
                    ) { cloudConnectionState: Int ->

                        when (cloudConnectionState) {
                            SettingsViewModel.CloudConnectionState.CONNECTED -> {
                                settingsViewModel.wifiStrength.observe(viewLifecycleOwner) { value: Int? ->
                                    val signalStrengthText = when (value) {
                                        0 -> getString(R.string.text_tiles_list_molecule_signal_strength_zero)
                                        1 -> getString(R.string.text_tiles_list_molecule_signal_strength_one)
                                        2 -> getString(R.string.text_tiles_list_molecule_signal_strength_two)
                                        3 -> getString(R.string.text_tiles_list_molecule_signal_strength_three)
                                        4 -> getString(R.string.text_tiles_list_molecule_signal_strength_four)
                                        else -> getString(R.string.text_tiles_list_molecule_signal_strength_zero)
                                    }
                                    finalInfo.rightText = signalStrengthText
                                    notifyItemChanged(0)
                                }
                            }
                            else -> finalInfo.rightText = getString(R.string.text_tiles_list_molecule_signal_strength_zero)
                        }
                        notifyItemChanged(0)
                    }
                }
                else -> finalInfo.rightText = getString(R.string.text_tiles_list_molecule_signal_strength_zero)
            }
            notifyItemChanged(0)
        }
        finalInfo.itemIconVisibility = View.GONE
        finalInfo.subTextVisibility = View.GONE
        finalInfo.rightIconVisibility = View.GONE
        finalInfo.radioButtonData = radioButtonData
        networkDetails.add(finalInfo)

        // RSSI
        info = ListTileData()
        info.titleText = getString(R.string.text_tiles_list_molecule_rssi)
        info.rightText = getString(R.string.text_tiles_list_molecule_rssi_value, settingsViewModel.rssi)
        info.itemIconVisibility = View.GONE
        info.subTextVisibility = View.GONE
        info.rightIconVisibility = View.GONE
        finalInfo.radioButtonData = radioButtonData
        networkDetails.add(info)

        // DNS
        info = ListTileData()
        info.titleText = getString(R.string.text_tiles_list_molecule_dns)
        info.rightText = settingsViewModel.dns?.getOrNull(0) ?: getString(R.string.weMissedThat)
        info.itemIconVisibility = View.GONE
        info.subTextVisibility = View.GONE
        info.rightIconVisibility = View.GONE
        finalInfo.radioButtonData = radioButtonData
        networkDetails.add(info)

        // Subnet Mask
        info = ListTileData()
        info.titleText = getString(R.string.text_tiles_list_molecule_subnet_mask)
        info.rightText = settingsViewModel.subnetMask.takeIf { !it.isNullOrEmpty() } ?: getString(R.string.weMissedThat)
        info.itemIconVisibility = View.GONE
        info.subTextVisibility = View.GONE
        info.rightIconVisibility = View.GONE
        finalInfo.radioButtonData = radioButtonData
        networkDetails.add(info)

        // Router IP
        info = ListTileData()
        info.titleText = getString(R.string.text_tiles_list_molecule_router_ip)
        info.rightText = settingsViewModel.routerIp.takeIf { !it.isNullOrEmpty() } ?: getString(R.string.weMissedThat)
        info.itemIconVisibility = View.GONE
        info.subTextVisibility = View.GONE
        info.rightIconVisibility = View.GONE
        finalInfo.radioButtonData = radioButtonData

        networkDetails.add(info)

        // IP Address
        info = ListTileData()
        info.titleText = getString(R.string.system_info_ip_address)
        info.rightText = settingsViewModel.ipAddress ?: getString(R.string.weMissedThat)
        info.itemIconVisibility = View.GONE
        info.subTextVisibility = View.GONE
        info.rightIconVisibility = View.GONE
        info.listItemDividerViewVisibility = View.GONE
        info.radioButtonData
        networkDetails.add(info)

        return networkDetails
    }

    override fun onDestroyView() {
        super.onDestroyView()
        NavigationViewModel.getNavigationViewModel().disableInteractionTimeout()
        SettingsViewModel.getSettingsViewModel().wifiStrength.removeObservers(viewLifecycleOwner)
        SettingsViewModel.getSettingsViewModel().wifiConnectState.removeObservers(viewLifecycleOwner)
        SettingsViewModel.getSettingsViewModel().awsConnectionStatus.removeObservers(viewLifecycleOwner)
    }

    override fun provideHeaderBarRightIconVisibility(): Boolean {
        return false
    }

    override fun provideHeaderBarLeftIconVisibility(): Boolean {
        return true
    }

    override fun provideHeaderBarInfoIconVisibility(): Boolean? {
        return false
    }

    override fun provideHeaderBarTitleText(): String {
        return getString(R.string.connect_to_network)
    }

    override fun setRightButton(): String {
        return getString(R.string.text_button_forget_network)
    }

    override fun provideListRecyclerViewTilesData(): ArrayList<ListTileData> {
        return prepareAndDisplayNetworkDetails()
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        // Handle meat probe insertion if necessary
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        // Handle meat probe removal if necessary
    }

    override fun onListViewItemClick(view: View?, position: Int) {
        // Handle list view item click if necessary
    }

    override fun observeViewModels() {
        // Implement view model observers if necessary
    }

    override fun headerBarOnClick(view: View?, buttonType: Int) {
        when (buttonType) {
            ICON_TYPE_LEFT -> {
                // Handle left button click
                NavigationViewModel.popBackStack(
                    Navigation.findNavController(
                        NavigationUtils.getViewSafely(
                            this
                        ) ?: requireView()
                    )
                )
            }
            ICON_TYPE_RIGHT -> {
                // Handle right button click
            }
            ICON_TYPE_INFO -> {
                // Handle info button click if necessary
            }
        }
    }

    override fun onRightNavigationButtonClick(view: View?, buttonType: Int) {
        forgetNetworkPopupBuilder()
    }

    override fun onLeftNavigationButtonClick(view: View?, buttonType: Int) {
    }

    override fun manageKnobRotation(knobDirection: String) {
        currentPosition = CookingAppUtils.getKnobPositionIndex(
            knobDirection,
            currentPosition,
            allItemSize + 1
        )
        HMILogHelper.Logd(
            "Knob",
            "LEFT_KNOB: rotate left current knob index = $currentPosition"
        )
        when (currentPosition) {
            in 0 until allItemSize -> {
                fragmentBinding?.buttonRight?.background = ResourcesCompat.getDrawable(resources, R.drawable.text_view_ripple_effect, null)
                fragmentBinding?.recyclerViewList?.smoothScrollToPosition(
                    currentPosition
                )
                highLightSelectedTiles()
            }

            in allItemSize until allItemSize + 1 -> {
                updateButtonBackgroundsForSpecialPositions(allItemSize)
                lastItemSelectedPos = currentPosition
            }

            else -> {
                currentPosition = 0
                lastItemSelectedPos = 0
            }
        }
    }

    private fun updateButtonBackgroundsForSpecialPositions(listItemsSize: Int) {
        val viewHolder = fragmentBinding?.recyclerViewList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
        viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
        when (currentPosition) {
            listItemsSize -> {
                fragmentBinding?.buttonRight?.background = ResourcesCompat.getDrawable(resources, R.drawable.selector_textview_walnut, null)
            }
        }
    }

    override fun onKnobTimeOut() {
        super.onKnobTimeOut()
        fragmentBinding?.buttonRight?.background =
            ResourcesCompat.getDrawable(resources, R.drawable.text_view_ripple_effect, null)
    }

    override fun onHMILeftKnobClick() {
        if (lastItemSelectedPos != -1) {
            when (currentPosition) {
                allItemSize -> {
                    KnobNavigationUtils.knobForwardTrace = true
                    fragmentBinding?.buttonRight?.callOnClick()
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
}
