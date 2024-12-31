package core.utils

import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants.IS_FROM_BLACKOUT
import core.utils.AppConstants.POWERLOSS_TIME_DATE_UPDATE_POPUP
import core.utils.CookingAppUtils.Companion.updatePopUpLeftTextButtonBackground
import core.utils.CookingAppUtils.Companion.updatePopUpRightTextButtonBackground
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils.Companion.observeHmiKnobListener

class BlackoutUtils {
    companion object {
        private var showNotificationOnClock : Boolean = false
        private val TAG: String = BlackoutUtils::class.java.simpleName
        var dialogPopupBuilder: ScrollDialogPopupBuilder? = null
        /*
        * This method takes care of showing various popups to user based on connectivity state after blackout has occurred
        * args navGraph & navController are passed to have next navigation after the Blackout related activities are done by user.
        * */
        fun handleBlackOutRecoveryPopUps(navGraph: NavGraph, navController: NavController, activity: FragmentActivity) {
            if(CookingAppUtils.isSabbathMode()) {
                HMILogHelper.Logd("$TAG : handleBlackOutRecoveryPopUps, SABBATH_COMPLIANT not showing popups")
                return
            }
            /* Handle Blackout recovery */
            SettingsViewModel.getSettingsViewModel().awsConnectionStatus.observe(activity) {
                HMILogHelper.Logd("$TAG : handleBlackOutRecoveryPopUps, CloudConnectionState:$it")
            }
            if (SettingsViewModel.getSettingsViewModel().awsConnectionStatus.value != SettingsViewModel.CloudConnectionState.IDLE
                && SettingsViewModel.getSettingsViewModel().isWifiEnabled
            ) {
                SettingsViewModel.getSettingsViewModel().setTimeModeAuto()
                showPowerLossWifiConnectedPopUpBuilder(navGraph, navController, activity)
            } else {

                //Trigger notification: Update date and time
                NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME)

                //Trigger notification: Connect to Network
                //Trigger after Update date and time as Connect to nw notification has higher priority
                NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW)
                showPowerLossWithoutWiFiPopUpBuilder(navGraph, navController, activity)
            }
        }

        fun getShowNotificationOnClock(): Boolean {
            return showNotificationOnClock
        }

        private fun handleBlackOutRecovery(navGraph: NavGraph, navController: NavController, activity: FragmentActivity) {
            //  Handle sabbath mode
            //  Turn table

            if (CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.isPyroliticClean
                || (CookingAppUtils.getSecondaryCookingViewModel() != null && CookingAppUtils.getSecondaryCookingViewModel()!!
                    .recipeExecutionViewModel.isPyroliticClean)
            ) {
                HMILogHelper.Logd(TAG,"handleBlackOutRecovery: Pyrolitic Clean")
                navController.graph = navGraph
                if (CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.isPyroliticClean) {
                    CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
                } else {
                    CookingViewModelFactory.setInScopeViewModel(CookingAppUtils.getSecondaryCookingViewModel())
                }
                CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                    var bundle = Bundle()
                    bundle.putBoolean(IS_FROM_BLACKOUT, true)
                    navigateSafely(
                        it,
                        R.id.global_action_to_self_clean_status,
                        bundle,
                        null
                    )
                }
            } else if(CookingAppUtils.isDemoModeEnabled()){
                navController.graph = navGraph
                CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                    navigateSafely(
                        it,
                        R.id.global_action_to_demoModeLandingFragment,
                        null,
                        null
                    )
                }
            } else {
                navController.graph = navGraph
                CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                    navigateSafely(
                        it,
                        R.id.global_action_to_clockScreen,
                        null,
                        null
                    )
                }
            }
        }

        private fun showPowerLossWifiConnectedPopUpBuilder(navGraph: NavGraph, navController: NavController, activity: FragmentActivity) {
            HMILogHelper.Logd("$TAG : showPowerLossWifiConnectedPopUpBuilder")
            if (dialogPopupBuilder == null) {
                dialogPopupBuilder =
                    ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                        .setHeaderTitle(R.string.text_power_loss_popup_title)
                        .setDescriptionMessage(R.string.text_description_power_loss_occured2)
                        .setRightButton(R.string.text_button_dismiss) {
                            HMILogHelper.Logd("$TAG : showPowerLossWifiConnectedPopUpBuilder Pressing Dismiss")
                            AudioManagerUtils.playOneShotSound(
                                ContextProvider.getContext(),
                                R.raw.button_press,
                                AudioManager.STREAM_SYSTEM,
                                true,
                                0,
                                1
                            )
                            handleBlackOutRecovery(navGraph, navController, activity)
                            false
                        }
                        .setCancellableOutSideTouch(false)
                        .build()
                dialogPopupBuilder?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)
                dialogPopupBuilder?.show(
                    activity.supportFragmentManager,
                    "showPowerLossWifiConnected"
                )
                var isKnobSelected = false
                val hmiCancelButtonInteractionListener =
                    HMIExpansionUtils.HMICancelButtonInteractionListener {
                        HMILogHelper.Logd(TAG, "Blackout popup CANCEL button pressed, handleBlackOutRecovery")
                        handleBlackOutRecovery(navGraph, navController, activity)
                        dialogPopupBuilder?.dismiss()
                    }
                val hmiKnobListener = observeHmiKnobListener(
                    onKnobRotateEvent = { knobId, _ ->
                        if (knobId == AppConstants.RIGHT_KNOB_ID) {
                            isKnobSelected = true
                            dialogPopupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                                activity.applicationContext.let {
                                    ContextCompat.getDrawable(
                                        it, R.drawable.selector_textview_walnut
                                    )
                                }
                        }
                    },
                    onHMIRightKnobClick = {
                        if (isKnobSelected)
                            dialogPopupBuilder?.onHMIRightKnobClick()
                    }, onKnobSelectionTimeout = {
                        isKnobSelected = false
                        dialogPopupBuilder?.provideViewHolderHelper()?.apply {
                            rightTextButton?.background = rightTextButton?.context.let {
                                it?.let { it1 ->
                                    ContextCompat.getDrawable(
                                        it1, R.drawable.text_view_ripple_effect
                                    )
                                }
                            }
                        }
                    }
                )

                dialogPopupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                        HMIExpansionUtils.setHMICancelButtonInteractionListener(hmiCancelButtonInteractionListener)
                        SettingsViewModel.getSettingsViewModel().wifiConnectState.observe(
                            activity
                        ) { state: Int ->
                            if (state == SettingsViewModel.WifiConnectState.CONNECTED) {
                                dialogPopupBuilder?.provideViewHolderHelper()?.descriptionTextView?.setText(
                                    R.string.text_description_power_loss_occured3
                                )
                            }
                        }
                    }

                    override fun onDialogDestroy() {
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        HMIExpansionUtils.removeHMICancelButtonInteractionListener(hmiCancelButtonInteractionListener)
                        if (dialogPopupBuilder != null) {
                            dialogPopupBuilder?.dismiss()
                            dialogPopupBuilder = null
                        }
                    }
                })
            }
        }

        private fun showPowerLossWithoutWiFiPopUpBuilder(navGraph: NavGraph, navController: NavController, activity: FragmentActivity) {
            HMILogHelper.Logd("$TAG : showPowerLossWithoutWiFiPopUpBuilder")
            if (dialogPopupBuilder == null) {
                dialogPopupBuilder =
                    ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                        .setHeaderTitle(R.string.text_power_loss_popup_title)
                        .setDescriptionMessage(R.string.text_description_power_loss_occured1)
                        .setRightButton(R.string.text_button_update) {
                            AudioManagerUtils.playOneShotSound(
                                ContextProvider.getContext(),
                                R.raw.button_press,
                                AudioManager.STREAM_SYSTEM,
                                true,
                                0,
                                1
                            )
                            updateDateAndTimePopUpBuilder(navGraph, navController, activity, true)
                            false
                        }
                        .setLeftButton(R.string.text_button_SKIP) {
                            AudioManagerUtils.playOneShotSound(
                                ContextProvider.getContext(),
                                R.raw.button_press,
                                AudioManager.STREAM_SYSTEM,
                                true,
                                0,
                                1
                            )
                            handleBlackOutRecovery(navGraph, navController, activity)
                            false
                        }
                        .setTopMarginForTitleText(AppConstants.POPUP_BLACKOUT_TITLE_TOP_SMALL_MARGIN)
                        .setTopMarginForDescriptionText(AppConstants.POPUP_BLACKOUT_DESCRIPTION_TOP_SMALL_MARGIN)
                        .setHeaderViewCenterIcon(AppConstants.HEADER_VIEW_CENTER_ICON_GONE, false)
                        .setWidthForDescriptionText(AppConstants.COMMON_POPUP_DESCRIPTION_WIDTH)
                        .setCancellableOutSideTouch(false)
                        .build()
                dialogPopupBuilder?.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)
                val hmiCancelButtonInteractionListener =
                    HMIExpansionUtils.HMICancelButtonInteractionListener {
                        HMILogHelper.Logd(TAG, "Blackout popup CANCEL button pressed, handleBlackOutRecovery")
                        handleBlackOutRecovery(navGraph, navController, activity)
                        dialogPopupBuilder?.dismiss()
                    }

                //Knob Interaction on popup
                var knobRotationCount = 0
                val hmiKnobListener = observeHmiKnobListener(
                    onKnobRotateEvent = { knobId, knobDirection ->
                        HMILogHelper.Logd("$TAG : onKnobRotateEvent: knobId:$knobId, knobDirection:$knobDirection")
                        if (knobId == AppConstants.LEFT_KNOB_ID) {
                            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                            HMILogHelper.Logd("$TAG : onKnobRotateEvent: knobRotationCount:$knobRotationCount")
                            when (knobRotationCount) {
                                AppConstants.KNOB_COUNTER_ONE -> {
                                    dialogPopupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                                        null
                                    dialogPopupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                        ContextProvider.getContext().let {
                                            ContextCompat.getDrawable(
                                                it, R.drawable.selector_textview_walnut
                                            )
                                        }
                                }

                                AppConstants.KNOB_COUNTER_TWO -> {
                                    dialogPopupBuilder?.provideViewHolderHelper()?.leftTextButton?.background =
                                        null
                                    dialogPopupBuilder?.provideViewHolderHelper()?.rightTextButton?.background =
                                        ContextProvider.getContext().let {
                                            ContextCompat.getDrawable(
                                                it, R.drawable.selector_textview_walnut
                                            )
                                        }
                                }
                            }
                        }
                    },
                    onHMILeftKnobClick = {
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                KnobNavigationUtils.knobBackTrace = true
                                dialogPopupBuilder?.onHMILeftKnobClick()
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                KnobNavigationUtils.knobForwardTrace = true
                                dialogPopupBuilder?.onHMIRightKnobClick()
                            }
                        }
                    }, onKnobSelectionTimeout = {
                        knobRotationCount = 0
                        dialogPopupBuilder?.provideViewHolderHelper()?.apply {
                            CookingAppUtils.setLeftAndRightButtonBackgroundNull(
                                this.leftTextButton,
                                this.rightTextButton
                            )
                        }
                    })

                dialogPopupBuilder?.setOnDialogCreatedListener(object :
                    ScrollDialogPopupBuilder.OnDialogCreatedListener {
                    override fun onDialogCreated() {
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                        HMIExpansionUtils.setHMICancelButtonInteractionListener(hmiCancelButtonInteractionListener)
                    }

                    override fun onDialogDestroy() {
                        HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                        HMIExpansionUtils.removeHMICancelButtonInteractionListener(hmiCancelButtonInteractionListener)
                        if (dialogPopupBuilder != null) {
                            dialogPopupBuilder?.dismiss()
                            dialogPopupBuilder = null
                        }
                    }
                })
                dialogPopupBuilder?.show(activity.supportFragmentManager, "showPowerLossWithoutWiFi")
            }
        }

        @Suppress("SameParameterValue")
        private fun updateDateAndTimePopUpBuilder(navGraph: NavGraph, navController: NavController, activity: FragmentActivity, isBlackoutRecovery: Boolean) {
            HMILogHelper.Logd(TAG,"updateDateAndTimePopUpBuilder")
            val dateAndTimeDialogPopupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                .setHeaderTitle(R.string.text_header_update_date_time)
                .setDescriptionMessage(R.string.text_description_update_date_time)
                .setLeftButton(R.string.text_button_manual_setup) {
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.button_press,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                    if (SettingsViewModel.getSettingsViewModel().controlLock.value == true){
                        handleBlackOutRecovery(navGraph, navController, activity)
                    } else {
                        if (isBlackoutRecovery) {
                            navController.graph = navGraph
                            CookingAppUtils.setNavigatedFrom(POWERLOSS_TIME_DATE_UPDATE_POPUP)
                            CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                                navigateSafely(
                                    it,
                                    R.id.global_action_to_settingsTimeAndDateFragment,
                                    null,
                                    null
                                )
                            }
                        }
                    }
                    false
                }
                .setRightButton(R.string.text_button_wifi_setup) {
                    if (isBlackoutRecovery) {
                        AudioManagerUtils.playOneShotSound(
                            ContextProvider.getContext(),
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        //Navigate to WiFi Connection management(provisioning flow beginning)
                        navController.graph = navGraph
                        val bundle = Bundle()
                        bundle.putBoolean(BundleKeys.BUNDLE_IS_FROM_BLACKOUT_CONNECT_WIFI, true)
                        CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                            navigateSafely(
                                it,
                                R.id.action_clockFragment_to_settingsLandingFragment,
                                bundle,
                                null
                            )
                        }
                    }
                    false
                }
                .setTopMarginForTitleText(AppConstants.POPUP_BLACKOUT_UPDATE_DATE_AND_TIME_TOP_SMALL_MARGIN)
                .setTopMarginForDescriptionText(AppConstants.POPUP_BLACKOUT_DESCRIPTION_TOP_SMALL_MARGIN)
                .setHeaderViewCenterIcon(AppConstants.HEADER_VIEW_CENTER_ICON_GONE, false)
                .setCancellableOutSideTouch(false)
                .setWidthForDescriptionText(AppConstants.COMMON_POPUP_DESCRIPTION_WIDTH)
                .build()
            dateAndTimeDialogPopupBuilder.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)
            dateAndTimeDialogPopupBuilder.show(activity.supportFragmentManager, "updateDateAndTime")

            //Knob Interaction on popup
            var knobRotationCount = 0
            val hmiKnobListener = observeHmiKnobListener(
                onKnobRotateEvent = { knobId, knobDirection ->
                    HMILogHelper.Logd("$TAG : onKnobRotateEvent: knobId:$knobId, knobDirection:$knobDirection")
                    if (knobId == AppConstants.LEFT_KNOB_ID) {
                        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                        HMILogHelper.Logd("$TAG : onKnobRotateEvent: knobRotationCount:$knobRotationCount")
                        when (knobRotationCount) {
                            AppConstants.KNOB_COUNTER_ONE -> {
                                CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                                    updatePopUpRightTextButtonBackground(
                                        it,
                                        dateAndTimeDialogPopupBuilder,
                                        R.drawable.text_view_ripple_effect
                                    )
                                }
                                CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                                    updatePopUpLeftTextButtonBackground(
                                        it,
                                        dateAndTimeDialogPopupBuilder,
                                        R.drawable.selector_textview_walnut
                                    )
                                }
                            }

                            AppConstants.KNOB_COUNTER_TWO -> {
                                CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                                    updatePopUpLeftTextButtonBackground(
                                        it,
                                        dateAndTimeDialogPopupBuilder,
                                        R.drawable.text_view_ripple_effect
                                    )
                                }
                                CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                                    updatePopUpRightTextButtonBackground(
                                        it,
                                        dateAndTimeDialogPopupBuilder,
                                        R.drawable.selector_textview_walnut
                                    )
                                }
                            }
                        }
                    }
                },
                onHMILeftKnobClick = {
                    KnobNavigationUtils.knobForwardTrace = true
                    when (knobRotationCount) {
                        AppConstants.KNOB_COUNTER_ONE -> {
                            dateAndTimeDialogPopupBuilder.onHMIRightKnobClick()
                        }

                        AppConstants.KNOB_COUNTER_TWO -> {
                            dateAndTimeDialogPopupBuilder.onHMILeftKnobClick()
                        }
                    }
                }, onKnobSelectionTimeout = {
                    knobRotationCount = 0
                    dateAndTimeDialogPopupBuilder.provideViewHolderHelper()?.apply {
                        CookingAppUtils.setLeftAndRightButtonBackgroundNull(
                            this.leftTextButton,
                            this.rightTextButton
                        )
                    }
                })

            dateAndTimeDialogPopupBuilder.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    if(KnobNavigationUtils.knobForwardTrace){
                        KnobNavigationUtils.knobForwardTrace = false
                        knobRotationCount = AppConstants.KNOB_COUNTER_ONE
                        CookingAppUtils.getVisibleFragment(activity.supportFragmentManager)?.let {
                            updatePopUpRightTextButtonBackground(
                                it,
                                dateAndTimeDialogPopupBuilder,
                                R.drawable.selector_textview_walnut
                            )
                        }
                    }
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                }
            })
        }
    }
}
