package android.presenter.fragments.provisioning

import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentProvisioningErrorPopupBinding
import com.whirlpool.hmi.provisioning.ProvisioningViewModel
import com.whirlpool.hmi.provisioning.manager.ProvisioningManager
import com.whirlpool.hmi.provisioning.viewholder.base.AbstractErrorViewHolder
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import core.utils.AppConstants
import core.utils.HMIExpansionUtils
import core.utils.KnobDirection
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils

/**
 * File       : com.whirlpool.cooking.provisioning.ErrorViewHolder
 * Brief      : Child View Holder Class of
 * Author     : Vishal
 * Created On : 15-05-2024
 */
class ErrorViewHolder : AbstractErrorViewHolder(),HMIKnobInteractionListener {
    private var provisioningErrorBinding: FragmentProvisioningErrorPopupBinding? = null
    private var knobRotationCount = 0
    var resources: Resources? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        provisioningErrorBinding = FragmentProvisioningErrorPopupBinding.inflate(inflater)
        resources = provisioningErrorBinding?.root?.resources
        return provisioningErrorBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
        //Trigger notification: Connect to NW as provisioning has failed
        NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW)
    }

    private fun manageChildViews() {
        providePrimaryButton()?.text = providePrimaryButtonText().toString()
        provideSecondaryButton()?.setText(R.string.text_button_dismiss)
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        provisioningErrorBinding = null
        resources = null
    }

    override fun provideTitleTextView(): TextView? {
        return provisioningErrorBinding?.textViewTitle
    }

    override fun provideDescriptionTextView(): TextView? {
        return provisioningErrorBinding?.textViewTitleDescription
    }

    override fun provideEnableWifiTitleText(): CharSequence? {
        return resources?.getString(R.string.text_header_failed_to_connect)
    }

    override fun provideEnableWifiDescriptionText(): CharSequence? {
        return resources?.getText(R.string.text_description_failed_to_connect)
    }

    override fun provideWifiGeneralConnectionErrorTitleText(): CharSequence? {
        return resources?.getString(R.string.text_header_failed_to_connect)
    }

    override fun provideWifiGeneralConnectionErrorDescriptionText(): CharSequence? {
        return resources?.getText(R.string.text_description_failed_to_connect)
    }

    override fun provideWifiNotFoundErrorTitleText(): CharSequence? {
        return resources?.getString(R.string.text_header_failed_to_connect)
    }

    override fun provideWifiNotFoundErrorDescriptionText(): CharSequence? {
        return resources?.getString(R.string.text_description_failed_to_connect)
    }

    override fun provideConnectionLostTitleText(): CharSequence? {
        return resources?.getString(R.string.text_header_failed_to_connect)
    }

    override fun provideConnectionLostDescriptionText(): CharSequence? {
        return resources?.getText(R.string.text_description_failed_to_connect)
    }

    override fun onInflatingPopup(popupType: Int) {
        // handle visiblity operation based on popupType
        if (popupType == ProvisioningManager.getInstance().errorViewHolder.POPUP_TYPE_ERROR_WIFI_AUTH_FAILED) {
            provideSecondaryButton()?.visibility = View.GONE
        }
    }

    override fun provideConnectionResetTitleText(): CharSequence? {
        return if (ProvisioningViewModel.getProvisioningViewModel().bondState.getValue() == ProvisioningViewModel.BondState.NONE) {
            resources?.getString(R.string.text_header_pairing_failed)
        } else {
            resources?.getString(R.string.text_header_failed_to_connect)
        }
    }

    override fun provideConnectionResetDescriptionText(): CharSequence? {
        return if (ProvisioningViewModel.getProvisioningViewModel().bondState.getValue() == ProvisioningViewModel.BondState.NONE) {
            resources?.getText(R.string.text_pairing_failed_body)
        } else {
            resources?.getText(R.string.text_description_failed_to_connect)
        }
    }

    override fun provideRegistrationTimeoutErrorTitleText(): CharSequence? {
        return resources?.getString(R.string.text_header_failed_to_connect)
    }

    override fun provideRegistrationTimeoutErrorDescriptionText(): CharSequence? {
        return resources?.getText(R.string.text_description_failed_to_connect)
    }

    override fun provideInternetTimeoutErrorTitleText(): CharSequence? {
        return resources?.getString(R.string.text_header_failed_to_connect)
    }

    override fun provideInternetTimeoutErrorDescriptionText(): CharSequence? {
        return resources?.getText(R.string.text_description_failed_to_connect)
    }

    override fun provideCloudRegistrationErrorTitleText(): CharSequence? {
        return resources?.getString(R.string.text_header_failed_to_connect)
    }

    override fun provideCloudRegistrationErrorDescriptionText(): CharSequence? {
        return resources?.getText(R.string.text_description_failed_to_connect)
    }

    override fun provideWifiPasswordErrorTitleText(): CharSequence? {
        return resources?.getString(R.string.text_header_incorrect_password)
    }

    override fun provideWifiPasswordErrorDescriptionText(): CharSequence? {
        return resources?.getText(R.string.text_description_incorrect_password)
    }

    override fun provideWifiPasswordErrorPrimaryButtonText(): CharSequence? {
        return resources?.getString(R.string.text_button_cancel)
    }

    override fun providePrimaryButton(): NavigationButton? {
        return provisioningErrorBinding?.textButtonRight
    }

    override fun providePrimaryButtonText(): CharSequence? {
        return resources?.getString(R.string.text_button_try_again)
    }

    override fun provideSecondaryButton(): NavigationButton? {
        return provisioningErrorBinding?.textButtonLeft
    }

    override fun provideResources(): Resources? {
        return resources
    }

    override fun onHMILeftKnobClick() {
        if (provisioningErrorBinding?.textButtonLeft?.visibility != View.VISIBLE){
            provisioningErrorBinding?.textButtonRight?.callOnClick()
        }
        else{
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    provisioningErrorBinding?.textButtonLeft?.callOnClick()
                }

                AppConstants.KNOB_COUNTER_TWO -> {
                    provisioningErrorBinding?.textButtonRight?.callOnClick()
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
        if (provisioningErrorBinding?.textButtonLeft?.visibility == View.VISIBLE) {
            if (knobId == AppConstants.LEFT_KNOB_ID) {
                if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        provisioningErrorBinding?.textButtonRight?.background = null
                        provisioningErrorBinding?.textButtonLeft?.background =
                            resources?.let {
                                ResourcesCompat.getDrawable(
                                    it,
                                    R.drawable.selector_textview_walnut,
                                    null
                                )
                            }
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        provisioningErrorBinding?.textButtonLeft?.background = null
                        provisioningErrorBinding?.textButtonRight?.background =
                            resources?.let {
                                ResourcesCompat.getDrawable(
                                    it,
                                    R.drawable.selector_textview_walnut,
                                    null
                                )
                            }
                    }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID){
            provisioningErrorBinding?.textButtonLeft?.background = null
            provisioningErrorBinding?.textButtonRight?.background = null
        }
    }
}
