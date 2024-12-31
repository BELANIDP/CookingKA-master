package android.presenter.fragments.provisioning

import android.content.res.Resources
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentProvisioningStatusInformationBinding
import com.whirlpool.hmi.provisioning.viewholder.base.AbstractWifiConnectViewHolder
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import core.utils.HMILogHelper

/**
 * File       : com.whirlpool.cooking.provisioning.WifiConnectViewHolder
 * Brief      : View Holder Class for the fragment
 * Author     : DUNGAS
 * Created On : 17-05-2024
 */
class WifiConnectViewHolder : AbstractWifiConnectViewHolder(),
    HeaderBarWidgetInterface.CustomClickListenerInterface {
    private var fragmentProvisioningStatusInformationBinding: FragmentProvisioningStatusInformationBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        HMILogHelper.Logi("inside wifi oncreate")

        fragmentProvisioningStatusInformationBinding =
            FragmentProvisioningStatusInformationBinding.inflate(inflater)
        return fragmentProvisioningStatusInformationBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMILogHelper.Logi("inside wifi oncreated")

        manageChildViews()
    }

    /**
     * Method to manage Views of the Fragment
     */
    private fun manageChildViews() {
        manageHeaderBar()
        manageStepProgressBar()
        manageNavigationButtons()
    }

    /**
     * Method to manage header bar of the Fragment
     */
    private fun manageHeaderBar() {
        HMILogHelper.Logi("inside manage headerbar")
        fragmentProvisioningStatusInformationBinding?.headerBar?.setTitleText(R.string.connect_to_network)
        fragmentProvisioningStatusInformationBinding?.headerBar?.setLeftIconVisibility(false)
        fragmentProvisioningStatusInformationBinding?.headerBar?.setInfoIconVisibility(false)
        fragmentProvisioningStatusInformationBinding?.headerBar?.setRightIconVisibility(false)
        fragmentProvisioningStatusInformationBinding?.headerBar?.setOvenCavityIconVisibility(false)
        fragmentProvisioningStatusInformationBinding?.headerBar?.setCustomOnClickListener(this)    }

    /**
     * Method to manage step progress bar of the Fragment
     */
    private fun manageStepProgressBar() {
        HMILogHelper.Logi("inside manage progressbar")
        fragmentProvisioningStatusInformationBinding?.stepProgressBarInformation?.setCurrentStep(0)
    }

    override fun onWifiConnectStateChange(state: Int) {
        HMILogHelper.Logi("State-->$state")
        when (state) {
            WIFI_CONNECTING -> { fragmentProvisioningStatusInformationBinding?.stepProgressBarInformation?.setCurrentStep(1)}

            INTERNET_CONNECTED, CLOUD_CONNECTED -> {
                fragmentProvisioningStatusInformationBinding?.stepProgressBarInformation?.setCurrentStep(2)}
        }
    }

    /**
     * Method to manage NAVIGATION buttons of the Fragment
     */
    private fun manageNavigationButtons() {
        fragmentProvisioningStatusInformationBinding?.navigationButtonRight?.visibility = View.GONE
        fragmentProvisioningStatusInformationBinding?.remoteEnableToggleSwitch?.visibility = View.GONE
    }

    override fun onDestroyView() {
        fragmentProvisioningStatusInformationBinding = null
    }

    override fun provideTitleTextView(): TextView? {
        return null
    }

    override fun provideSubTitleTextView(): TextView? {
        return fragmentProvisioningStatusInformationBinding?.textViewPairingInProgress
    }

    override fun provideDescriptionTextView(): AppCompatTextView? {
        return fragmentProvisioningStatusInformationBinding?.textViewPairingInProgressDescription
    }

    //Stage (1) Bluetooth Pairing
    override fun provideWifiCredentialsFromMobilePhoneTitleText(): CharSequence? {
        return provideResources()?.getString(R.string.connect_to_network)
    }

    override fun provideWifiCredentialsFromMobilePhoneSubTitleText(): CharSequence? {
        return provideResources()?.getString(R.string.text_title_pairing_in_process)
    }

    override fun provideWifiCredentialsFromMobilePhoneDescriptionText(): CharSequence? {
        return provideResources()?.getText(R.string.text_description_pairing_in_process)
    }

    //Stage (2) WIFI_CONNECTION
    override fun provideWifiConnectingTitleText(): CharSequence? {
        return provideResources()?.getText(R.string.connect_to_network)
    }

    override fun provideWifiConnectingSubTitleText(): CharSequence? {
        return provideResources()?.getString(R.string.text_title_connecting_to_WiFi)
    }

    override fun provideWifiConnectingDescriptionText(): CharSequence? {
        return provideResources()?.getString(R.string.text_description_connecting_to_WiFi)
    }

    //Stage (3) INTERNET_CONNECTION
    override fun provideInternetConnectingTitleText(): CharSequence? {
        return provideResources()?.getString(R.string.connect_to_network)
    }

    override fun provideInternetConnectingSubTitleText(): CharSequence? {
        return provideResources()?.getString(R.string.text_title_connecting_to_server)
    }

    override fun provideInternetConnectingDescriptionText(): CharSequence? {
        return provideResources()?.getString(R.string.text_description_may_take_up_to_2min)
    }

    //Stage (4) CLOUD_REGISTER_CONNECTION
    override fun provideCloudRegisteringTitleText(): CharSequence? {
        return provideResources()?.getText(R.string.connect_to_network)
    }

    override fun provideCloudRegisteringWaitTitleText(): CharSequence? {
        return provideResources()?.getText(R.string.connect_to_network)
    }

    override fun provideCloudRegisteringWaitSubTitleText(): CharSequence? {
        return provideResources()?.getText(R.string.text_title_registering_oven)
    }

    override fun provideCloudRegisteringSubTitleText(): CharSequence? {
        return provideResources()?.getText(R.string.text_title_registering_oven)
    }

    override fun provideCloudRegisteringDescriptionText(): CharSequence ? {
        return provideResources()?.getString(R.string.text_description_may_take_up_to_2min)
    }

    override fun provideCloudRegisteringWaitDescriptionText(): CharSequence? {
        return provideResources()?.getText(R.string.text_descirption_longer_time)
    }

    override fun provideResources(): Resources? {
        return fragmentProvisioningStatusInformationBinding?.root?.resources
    }

    override fun provideStepperBar(): Stepper? {
        return fragmentProvisioningStatusInformationBinding?.stepper
    }

    override fun provideScreenTimeoutInSeconds(): Int {
        return provideResources()?.getInteger(R.integer.integer_timeout_5mins)?:0
    }

}
