package android.presenter.fragments.provisioning

import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentProvisioningStatusInformationBinding
import com.whirlpool.hmi.provisioning.viewholder.base.AbstractProvisioningCompletedViewHolder
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.utils.LogHelper
import core.utils.AppConstants
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils.removeNotificationFromNotificationCenter
import core.utils.NotificationManagerUtils.removeNotificationFromQueue
import core.utils.SettingsManagerUtils.isUnboxing


/**
 * File       : com.whirlpool.cooking.provisioning.ProvisioningCompletedViewHolder
 * Brief      : View Holder Class for the fragment when provision completes
 * Author     : DUNGAS
 * Created On : 15-05-2024
 */
class CompletedViewHolder : AbstractProvisioningCompletedViewHolder(),
    HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener {
    private var provisioningCompletedBinding: FragmentProvisioningStatusInformationBinding? = null
    private var knobRotationCount = 0
    var resources: Resources? = null
    override fun provideToggleButtonView(): CompoundButton? {
        return provisioningCompletedBinding?.remoteEnableToggleSwitch!!
    }

    override fun provideToggleButtonText(): CharSequence? {
        return null
    }

    override fun provideToggleButtonDefaultState(): Boolean {
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup,
        savedInstanceState: Bundle?
    ): View? {
        provisioningCompletedBinding =
            FragmentProvisioningStatusInformationBinding.inflate(inflater)
        resources = provisioningCompletedBinding?.root?.resources
        if (!isUnboxing) HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CONNECT_TO_NETWORK)
        return provisioningCompletedBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
        NavigationViewModel.getNavigationViewModel().disableInteractionTimeout()
        //  Remove notification: Connect to network
        removeNotificationFromNotificationCenter(NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW)
        removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW)

        //  Remove notification: Update date and time
        removeNotificationFromNotificationCenter(NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME)
        removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME)
    }

    /**
     * Method to manage Views of the Fragment
     */
    private fun manageChildViews() {
        manageHeaderBar()
        manageStepProgressBar()
        manageNavigationButtons()
        provideToggleButtonView()?.visibility = View.VISIBLE
    }

    /**
     * Method to manage header bar of the Fragment
     */
    private fun manageHeaderBar() {
        provisioningCompletedBinding?.headerBar?.setTitleText(R.string.text_header_successfully_connected1)
        provisioningCompletedBinding?.headerBar?.setLeftIconVisibility(false)
        provisioningCompletedBinding?.headerBar?.setInfoIconVisibility(false)
        provisioningCompletedBinding?.headerBar?.setRightIconVisibility(false)
        provisioningCompletedBinding?.headerBar?.setCustomOnClickListener(this)
        provisioningCompletedBinding?.headerBar?.setOvenCavityIconVisibility(false)
    }

    /**
     * Method to manage step progress bar of the Fragment
     */
    private fun manageStepProgressBar() {
        provisioningCompletedBinding?.stepProgressBarInformation?.visibility=View.GONE
    }

    /**
     * Method to manage NAVIGATION buttons of the Fragment
     */
    private fun manageNavigationButtons() {
        provideRemoteEnableButton()?.visibility = View.VISIBLE
        provisioningCompletedBinding?.navigationButtonRight?.visibility = View.VISIBLE
        provisioningCompletedBinding?.remoteEnableToggleSwitch?.visibility= View.VISIBLE
        provisioningCompletedBinding?.remoteEnableTextView?.visibility= View.VISIBLE
        provisioningCompletedBinding?.textViewPairingInProgressDescription?.visibility = View.GONE
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        provisioningCompletedBinding = null
        resources = null
    }

    override fun provideRemoteEnableTurnOnButtonText(): CharSequence? {
        return resources?.getString(R.string.text_button_finish)
    }

    override fun provideRemoteEnableTurnOffButtonText(): CharSequence? {
        return resources?.getString(R.string.text_button_finish)
    }

    override fun provideRemoteEnableButton(): NavigationButton? {
        return provisioningCompletedBinding?.navigationButtonRight
    }

    override fun provideTurnOffRemoteEnableButton(): NavigationButton? {
        return null
    }

    override fun provideResources(): Resources? {
        return provisioningCompletedBinding?.root?.resources
    }

    override fun provideTitleTextView(): TextView? {
        return provisioningCompletedBinding?.textViewPairingInProgress
    }

    override fun provideTitleText(): CharSequence? {
        return null
    }

    override fun provideRemoteEnabledSubtitleTextView(): TextView? {
        return provisioningCompletedBinding?.textViewPairingInProgress
    }

    override fun provideRemoteEnabledDescriptionTextView(): TextView? {
        return provisioningCompletedBinding?.textViewRemoteEnableDescription
    }

    override fun provideRemoteEnableSubtitleText(): CharSequence? {
        return resources?.getString(R.string.text_title_remote_enable_turned_on)
    }

    override fun provideRemoteEnableOffSubtitleText(): CharSequence? {
        return resources?.getString(R.string.text_title_remote_enable_turned_off)
    }

    override fun provideRemoteEnableOffDescriptionText(): CharSequence? {
        return resources?.getString(R.string.text_description_remote_enable_turned_off)
    }

    override fun provideRemoteEnableDescriptionText(): CharSequence? {
        return resources?.getString(R.string.text_description_remote_enable_turned_on)
    }

    override fun provideUpdateAvailableButtonText(): CharSequence? {
        provisioningCompletedBinding?.navigationButtonRight?.width = resources?.getDimension(R.dimen.button_ota_update_width)?.toInt()?:0
        return resources?.getString(R.string.text_button_update_available)
    }

    override fun onHMILeftKnobClick() {
        when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> {
                provisioningCompletedBinding?.remoteEnableToggleSwitch?.let { toggleSwitch ->
                    // Simulate toggle instead of calling click
                    toggleSwitch.isChecked = !toggleSwitch.isChecked
                }
            }

            AppConstants.KNOB_COUNTER_TWO -> {
                provisioningCompletedBinding?.navigationButtonRight?.callOnClick()
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
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    provisioningCompletedBinding?.navigationButtonRight?.background = null
                    provisioningCompletedBinding?.remoteEnableLayout?.background =
                        resources?.let {
                            ResourcesCompat.getDrawable(
                                it,
                                R.drawable.selector_textview_and_toggle_button,
                                null
                            )
                        }
                }

                AppConstants.KNOB_COUNTER_TWO -> {
                    provisioningCompletedBinding?.remoteEnableLayout?.background = null
                    provisioningCompletedBinding?.navigationButtonRight?.background =
                        resources?.let {
                            ResourcesCompat.getDrawable(
                                it,
                                R.drawable.selector_textview_and_toggle_button,
                                null
                            )
                        }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            provisioningCompletedBinding?.navigationButtonRight?.background = null
            provisioningCompletedBinding?.remoteEnableLayout?.background = null
        }
    }

    override fun handleMandatoryOtaExternally(
        targetVersion: String?,
        releaseInfoMap: java.util.HashMap<String, String>?
    ): Boolean {
        HMILogHelper.Logd("onMandatoryUpdate", "Target version: $targetVersion\n")
        HMILogHelper.Logd("onMandatoryUpdate", "releaseInfoMap: $releaseInfoMap\n")
        HMILogHelper.Logd("Unboxing", "Unboxing: Navigate to update OTA flow")
        provisioningCompletedBinding?.root?.let {
            Navigation.findNavController(it)
                .navigate(R.id.global_action_to_otaBusyUpdateAvailableViewHolder)
        }
        return true
    }

}

