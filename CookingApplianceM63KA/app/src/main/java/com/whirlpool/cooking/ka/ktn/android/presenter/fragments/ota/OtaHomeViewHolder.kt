package android.presenter.fragments.ota

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.ErrorDiagnosticsPopupFragmentBinding
import com.whirlpool.cooking.ka.databinding.OtaHomeViewholderBinding
import com.whirlpool.hmi.ota.ui.viewproviders.AbstractOtaHomeViewProvider
import com.whirlpool.hmi.ota.ui.viewproviders.AbstractOtaPopupViewProvider
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.ota.viewmodel.OTAViewModel
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AppConstants
import core.utils.AppConstants.LAST_BUILD_DATE_FORMAT
import core.utils.AppConstants.OTA_LAST_UPDATE_DATE_FORMAT
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper.Logd
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils
import core.utils.KnobDirection
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import java.text.SimpleDateFormat
import java.util.Date


/**
 * File        : android.presenter.fragments.ota.OtaHomeViewHolder
 * Brief       : View Holder Class for OTA Home
 * Author      : Rajendra
 * Created On  : 15-July-2024
 * Details     : Provide View for Launching OTA screen, displaying target version, skip update and update now functionality
 */
class OtaHomeViewHolder : AbstractOtaHomeViewProvider(),HMIKnobInteractionListener{
    private var otaViewModel: OTAViewModel? = null

    private var viewBinding: OtaHomeViewholderBinding? = null
    private var fromConnectivityListMenu = false
    //Knob Implementation
    private var knobRotationCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = OtaHomeViewholderBinding.inflate(inflater, container, false)
        Logd(TAG, "Showing OTA Home View Holder")
        otaViewModel = OTAVMFactory.getOTAViewModel()
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        updateNavigatedFromFragmentResultListener()
        managePreferencesCollectionHeaderBar()
        updateVersionInfo()
    }

    private fun updateVersionInfo() {
        if (otaViewModel?.otaState != null && (otaViewModel?.otaState?.value == OTAStatus.AWAITING_USER_ACK ||
                    otaViewModel?.otaState?.value == OTAStatus.DELAYED)) {
            viewBinding?.softwareUpdateScheduled?.visibility = View.VISIBLE
            viewBinding?.softwareUpdateScheduled?.text = provideResources().getString(
                R.string.text_software_update_scheduled,
                otaViewModel?.targetSystemVersion?.value
            )
            viewBinding?.thisMayTakeMinutes?.visibility = View.VISIBLE
            viewBinding?.thisMayTakeMinutes?.text =
                provideResources().getString(R.string.text_software_update_scheduled_time)
        } else {
            viewBinding?.softwareUpdateScheduled?.visibility = View.GONE
            viewBinding?.thisMayTakeMinutes?.visibility = View.GONE
        }
    }

    private fun managePreferencesCollectionHeaderBar() {
        viewBinding?.titleBar?.setLeftIconVisibility(true)
        viewBinding?.titleBar?.setRightIconVisibility(true)
        viewBinding?.titleBar?.setOvenCavityIconVisibility(false)
        viewBinding?.titleBar?.setInfoIconVisibility(false)
        viewBinding?.titleBar?.setRightIcon(R.drawable.ic_close)
        viewBinding?.titleBar?.getHeaderTitle()?.visibility = View.VISIBLE
    }

    private fun setGraphAndNavigateToClock(view: View) {
        Logd(TAG,"OTA : Navigating to Clock Screen")
        CookingAppUtils.setCookFlowGraphBasedOnVariant(view)
        Navigation.findNavController(view).navigate(R.id.global_action_to_clockScreen)
    }

    override fun onDestroyView() {
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResult(AppConstants.CONNECTIVITYLIST_FRAGMENT)
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResultListener(AppConstants.CONNECTIVITYLIST_FRAGMENT)
        viewBinding = null
    }

    override fun provideResources(): Resources {
        return viewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return viewBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.text_subMenu_network_softwareUpdate)
    }

    override fun provideSubTitleTextView(): TextView? {
        return null
    }

    override fun provideSubTitleText(): CharSequence? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return viewBinding?.titleBar?.getRightImageView()
    }

    override fun provideCloseButtonView(): View {
        return viewBinding?.titleBar?.getLeftImageView() as View
    }

    override fun provideRightNavigationView(): View? {
        return null
    }

    private fun updateSoftwareUpdateInfo() {
        if (otaViewModel != null && otaViewModel?.otaState != null && (otaViewModel?.otaState?.value == OTAStatus.IDLE
                    || otaViewModel?.otaState?.value == OTAStatus.ERROR)
        ) {
            viewBinding?.softwareVersionHeader?.visibility = View.INVISIBLE
            viewBinding?.softwareVersionNumber?.visibility = View.INVISIBLE
        }
    }

    /**
     * Method to check if the appliance is busy.
     * It the return value is true, ota will not be performed.
     *
     * @return boolean busy state
     */
    override fun provideApplianceBusyState(): Boolean {
        val otaBusyState = CookingAppUtils.setApplianceOtaState()
        Logd(TAG, "OTA: Home view Holder appliance busy state: $otaBusyState")
        return otaBusyState
    }

    /**
     * Method to provide the popup view for the case where ota can not be proceed.
     * This will be occurred when appliance is busy with performing any features
     *
     * @return [AbstractOtaPopupViewProvider]
     */
    override fun provideUnableToUpdateErrorPopupView(): AbstractOtaPopupViewProvider {
        return object : AbstractOtaPopupViewProvider() {
            private var popupBinding: ErrorDiagnosticsPopupFragmentBinding? = null

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                popupBinding = ErrorDiagnosticsPopupFragmentBinding.inflate(inflater, container, false)
                return popupBinding?.root
            }

            override fun onDestroyView() {
                popupBinding = null
            }

            override fun provideResources(): Resources {
                return popupBinding?.root?.resources as Resources
            }

            override fun providePrimaryButton(): NavigationButton? {
                return popupBinding?.textButtonCenter
            }

            override fun provideSecondaryButton(): NavigationButton? {
                return null
            }

            override fun provideTitleTextView(): TextView? {
                return popupBinding?.textViewTitle
            }

            override fun provideSubTitleTextView(): TextView? {
                return null
            }

            override fun provideDescriptionTextView(): TextView? {
                return popupBinding?.textViewDescription
            }

            override fun provideUnableToUpdateErrorPopupDescription(): CharSequence {
                return provideResources().getString(R.string.text_description_appliance_busy)
            }

            override fun provideUnableToUpdateErrorPopupTitle(): CharSequence {
                return provideResources().getString(R.string.text_header_appliance_is_busy)
            }

            override fun providePrimaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_got_it)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onUpToDate(version: CharSequence?, lastOtaUpdateDate: CharSequence?) {
        var lastUpdated = lastOtaUpdateDate.toString()
        viewBinding?.ivSWUpToDateIcon?.visibility = View.VISIBLE
        viewBinding?.tvWhatIsNewTitle?.visibility = View.GONE
        viewBinding?.ivSWUpToDateIcon?.setImageResource(R.drawable.icon_check_mark)
        viewBinding?.tvSWUpToDate?.visibility = View.VISIBLE
        viewBinding?.tvSWUpToDate?.text = provideUpToDateDescription()
        if (lastUpdated.contentEquals("")) {
            viewBinding?.tvSWUpToDateTime?.visibility = View.GONE
        } else {
            viewBinding?.tvSWUpToDateTime?.visibility = View.VISIBLE
            updateSoftwareUpdateInfo()
            val formatter = SimpleDateFormat(LAST_BUILD_DATE_FORMAT)
            val formatterOut = SimpleDateFormat(OTA_LAST_UPDATE_DATE_FORMAT)
            val date: Date? = formatter.parse(lastUpdated)
            if (date != null) {
                lastUpdated =
                    provideResources().getString(R.string.text_last_updated) + " " + formatterOut.format(
                        date
                    )
                viewBinding?.tvSWUpToDateTime?.text = lastUpdated
            }
        }
    }

    override fun onUpdateAvailable(
        versionInfo: CharSequence,
        releaseInfoMap: HashMap<String, String>
    ) {
        super.onUpdateAvailable(versionInfo, releaseInfoMap)
        val spannableStringBuilder = getSpannableStringBuilder(releaseInfoMap)
        if (spannableStringBuilder.isNotEmpty()) {
            //Visible release info textview if release info text is available.
            viewBinding?.tvSWUpToDate?.visibility = View.INVISIBLE
            viewBinding?.tvWhatIsNewReleaseInfo?.visibility = View.VISIBLE
            viewBinding?.tvWhatIsNewReleaseInfo?.movementMethod = ScrollingMovementMethod()
            viewBinding?.tvWhatIsNewReleaseInfo?.text = spannableStringBuilder
        } else {
            viewBinding?.tvWhatIsNewReleaseInfo?.visibility = View.INVISIBLE
            viewBinding?.tvSWUpToDate?.visibility = View.VISIBLE
        }
    }

    private fun getSpannableStringBuilder(releaseInfoMap: HashMap<String, String>): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        val headingSizeSpan = AbsoluteSizeSpan(22)
        AbsoluteSizeSpan(18)
        val boldStyleSpan = StyleSpan(Typeface.BOLD)
        for (header in releaseInfoMap.keys) {
            val heading = SpannableString(header)
            heading.setSpan(headingSizeSpan, 0, header.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            heading.setSpan(boldStyleSpan, 0, heading.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val description = CookingAppUtils.extractSecondaryTextDescriptionFileContent(otaViewModel?.descriptionFileContent?.getValue())
            spannableStringBuilder.append(description)
        }
        return spannableStringBuilder
    }

    override fun providePositiveButton(): NavigationButton? {
        return viewBinding?.btnUpdateNow
    }

    override fun provideNegativeButton(): NavigationButton? {
        return null
    }

    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun providePositiveButtonText(): CharSequence {
        return provideResources().getString(R.string.text_button_update_now)
    }

    override fun provideNegativeButtonText(): CharSequence {
        return provideResources().getString(R.string.text_button_SKIP)
    }

    override fun onUpdateCompleted(
        version: CharSequence?,
        releaseDetailsMap: MutableMap<String, String>?
    ) {
        CookingAppUtils.setOTACompleteStatus(true)
        //Trigger notification: OTA completed. SW Updated.
        NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_SW_UPDATED_SUCCESSFULLY)
        viewBinding?.titleBar?.setRightIconVisibility(false)
        viewBinding?.titleBar?.setLeftIconVisibility(false)
        viewBinding?.tvWhatIsNewTitle?.visibility = View.GONE
        viewBinding?.softwareUpdateScheduled?.visibility = View.GONE
        viewBinding?.thisMayTakeMinutes?.visibility = View.GONE
        viewBinding?.softwareUpdateCompleteVersionTitle?.visibility = View.VISIBLE
        viewBinding?.softwareUpdateCompleteVersionNumber?.visibility = View.VISIBLE
        viewBinding?.softwareUpdateCompleteVersionNumber?.text = version
        viewBinding?.titleBar?.setTitleText("")
        viewBinding?.titleBar?.getHeaderTitle()?.visibility = View.VISIBLE
        viewBinding?.titleBar?.getHeaderTitle()?.text = provideResources().getString(R.string.text_header_software_update_successful)
        viewBinding?.btnUpdateNow?.text = provideResources().getString(R.string.text_button_dismiss)
        val spannableStringBuilderOTAComplete = getSpannableStringBuilder(releaseDetailsMap as HashMap<String, String>)
        if (spannableStringBuilderOTAComplete.isNotEmpty()) {
            //Visible release info textview if release info text is available.
            viewBinding?.informationScrollablePrimaryTextView?.visibility = View.VISIBLE
            viewBinding?.informationScrollablePrimaryTextView?.text = provideResources().getString(R.string.text_sub_heading_new_feature)
            viewBinding?.informationScrollableSecondaryTextView?.visibility = View.VISIBLE
            viewBinding?.informationScrollableSecondaryTextView?.movementMethod = ScrollingMovementMethod()
            viewBinding?.informationScrollableSecondaryTextView?.text = spannableStringBuilderOTAComplete
        } else {
            viewBinding?.informationScrollableSecondaryTextView?.visibility = View.GONE
            viewBinding?.informationScrollablePrimaryTextView?.visibility = View.VISIBLE
            viewBinding?.informationScrollablePrimaryTextView?.text = provideResources().getString(R.string.weMissedThat)
        }
    }

    override fun handleOtaExitNavigationExternally(view: View?, otaStatus: OTAStatus?): Boolean {
        Log.d(TAG, "OTA: handleOtaExitNavigationExternally: $otaStatus")
        if (view != null) {
            if (SettingsManagerUtils.isUnboxing) {
                Logd("Unboxing", "Unboxing: Settings the digital_unboxing_nav_graph and return")
                Navigation.findNavController(view).setGraph(R.navigation.digital_unboxing_nav_graph)
            } else if (fromConnectivityListMenu) {
                Logd("Tools", "Navigate back to the connectivity list screen")
                Navigation.findNavController(view)
                    .navigate(R.id.global_action_to_connectivity_list_fragment)
            } else {
                setGraphAndNavigateToClock(view)
            }
        }
        return true
    }

    override fun provideUpToDateDescription(): CharSequence {
        return if (otaViewModel != null && otaViewModel?.otaManager != null) provideResources().getString(
            R.string.text_software_upToDate,
            otaViewModel?.otaManager?.currentSystemVersion
        )
        else provideResources().getString(R.string.text_helper_upToDate)
    }

    override fun provideSkipUpdatePopupView(): AbstractOtaPopupViewProvider {
        return object : AbstractOtaPopupViewProvider() {
            private var popupBinding: ErrorDiagnosticsPopupFragmentBinding? = null

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                popupBinding = ErrorDiagnosticsPopupFragmentBinding.inflate(inflater, container, false)
                return popupBinding?.root
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
            }

            //Knob Implementation
            val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
                onHMIRightKnobClick = {
                    Logd("OTA", "OTA Popup onHMIRightKnobClick")
                    popupBinding?.textButtonRight?.callOnClick()
                },
                onHMILeftKnobClick = {
                    Logd("OTA", "OTA Popup onHMILeftKnobClick")
                },
                onKnobSelectionTimeout = {}
            )
            override fun onDestroyView() {
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                popupBinding = null
            }

            override fun provideResources(): Resources {
                return popupBinding?.root?.resources as Resources
            }

            override fun providePrimaryButton(): NavigationButton? {
                return popupBinding?.textButtonRight
            }

            override fun provideSecondaryButton(): NavigationButton? {
                return null
            }

            override fun provideTitleTextView(): TextView? {
                return popupBinding?.textViewTitle
            }

            override fun provideSubTitleTextView(): TextView? {
                return null
            }

            override fun provideDescriptionTextView(): TextView? {
                return popupBinding?.textViewDescription
            }

            override fun provideUpdateSkippedPopupTitle(): CharSequence {
                return provideResources().getString(R.string.text_header_software_update_skipped)
            }

            override fun provideUpdateSkippedPopupDescription(): CharSequence {
                return provideResources().getString(R.string.text_widget_description_update_skipped)
            }

            override fun providePrimaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_got_it)
            }
        }
    }

    companion object {
        private val TAG: String = OtaHomeViewHolder::class.java.simpleName
    }

    override fun onHMILeftKnobClick() {
        Logd("OTA", "OTA onHMILeftKnobClick")
        if (viewBinding?.btnUpdateNow?.isVisible == true) {
            viewBinding?.btnUpdateNow?.background =
                viewBinding?.btnUpdateNow?.context.let {
                    ContextCompat.getDrawable(
                        it!!, R.drawable.selector_textview_walnut
                    )
                }
            viewBinding?.btnUpdateNow?.callOnClick()
        }
    }

    override fun onHMILongLeftKnobPress() {
        Logd("OTA", "OTA onHMILongLeftKnobPress")
    }

    override fun provideScreenTimeOutInSeconds(currentOtaStatus: OTAStatus?): Int {
        Logd("OTA", "Timeout started for OTA Home or completed screen")
        return provideResources().getInteger(R.integer.integer_ota_timeout_10mins)
    }

    override fun onHMIRightKnobClick() {
        Logd("OTA", "OTA onHMIRightKnobClick")
    }

    override fun onHMILongRightKnobPress() {
        Logd("OTA", "OTA onHMILongRightKnobPress")
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        Logd("OTA", "OTA onHMIRightKnobTickHoldEvent")
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        Logd("OTA", "OTA onKnobRotateEvent")
        if (knobId == AppConstants.LEFT_KNOB_ID && viewBinding?.btnUpdateNow?.isVisible == true) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.MIN_KNOB_POSITION) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    viewBinding?.btnSkipUpdate?.background =
                        null
                    viewBinding?.btnUpdateNow?.background =
                        viewBinding?.btnUpdateNow?.context.let {
                            ContextCompat.getDrawable(
                                it!!, R.drawable.selector_textview_walnut
                            )
                        }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        viewBinding?.btnUpdateNow?.background = null
    }

    /**
     * This function will help if we navigated from the connectivity list menu or not
     */
    private fun updateNavigatedFromFragmentResultListener() {
        NavigationUtils.getVisibleFragment().let {
            it?.parentFragmentManager?.setFragmentResultListener(
                AppConstants.CONNECTIVITYLIST_FRAGMENT,
                it.viewLifecycleOwner
            ) { _, bundle ->
                val result = bundle.getBoolean(AppConstants.CONNECTIVITYLIST_FRAGMENT)
                if (result) {
                    Logd("Tools", "received bundle the connectivity list screen")
                    fromConnectivityListMenu = true
                }
            }
        }
    }
}