package android.presenter.fragments.provisioning

import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentDevicePairInformationBinding
import com.whirlpool.hmi.provisioning.ProvisioningViewModel
import com.whirlpool.hmi.provisioning.viewholder.base.AbstractBLEPairViewHolder
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import core.utils.AppConstants
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper

/**
 * File       : com.whirlpool.cooking.provisioning.BlePairViewHolder
 * Brief      : View Holder Class for the fragment to pair with BLE device
 * Author     : Vishal
 * Created On : 14-05-2024
 */
class BlePairViewHolder : AbstractBLEPairViewHolder(), HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener{
    private var fragmentDevicePairInformationBinding: FragmentDevicePairInformationBinding? = null
    private var isKnobSelected:Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDevicePairInformationBinding =
            FragmentDevicePairInformationBinding.inflate(inflater)
        return fragmentDevicePairInformationBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        HMILogHelper.Logi("inside pair info view created")
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
    }

    private fun manageChildViews() {
        HMILogHelper.Logi("inside manage child view")
        manageHeaderBar()
        manageBlePinView()
        manageDeviceNameTextView()

    }

    private fun manageHeaderBar() {
        fragmentDevicePairInformationBinding?.headerBar?.setTitleText(R.string.connect_to_network)
        fragmentDevicePairInformationBinding?.headerBar?.setLeftIconVisibility(true)
        fragmentDevicePairInformationBinding?.headerBar?.setLeftIcon(R.drawable.ic_back_arrow)
        fragmentDevicePairInformationBinding?.headerBar?.setInfoIconVisibility(false)
        fragmentDevicePairInformationBinding?.headerBar?.setRightIconVisibility(false)
        fragmentDevicePairInformationBinding?.headerBar?.setCustomOnClickListener(this)
        fragmentDevicePairInformationBinding?.headerBar?.setOvenCavityIconVisibility(false)
    }

    private fun manageDeviceNameTextView() {
        provideTitleTextView()?.text = provideResources()?.getString(R.string.text_title_connect_to_your_device)
    }

    private fun manageBlePinView() {
        HMILogHelper.Logi("inside manage ble pin view")
        fragmentDevicePairInformationBinding?.pinInfo?.visibility = View.VISIBLE
        fragmentDevicePairInformationBinding?.textViewPin?.visibility = View.VISIBLE
        fragmentDevicePairInformationBinding?.textViewPinNumber?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        fragmentDevicePairInformationBinding = null
    }

    override fun provideTitleTextView(): TextView? {
        fragmentDevicePairInformationBinding?.textViewSAID?.visibility = View.VISIBLE
        return fragmentDevicePairInformationBinding?.textViewSAID
    }

    override fun provideDescriptionTextView(): TextView? {
        fragmentDevicePairInformationBinding?.textViewDescription?.visibility = View.VISIBLE
        return fragmentDevicePairInformationBinding?.textViewDescription
    }

    override fun providePinValueTextView(): TextView? {
        return fragmentDevicePairInformationBinding?.textViewPinNumber
    }

    override fun providePrimaryButton(): NavigationButton? {
        return fragmentDevicePairInformationBinding?.navigationButtonRight
    }

    override fun providePrimaryButtonText(): CharSequence? {
        return provideResources()?.getString(R.string.text_button_pair)
    }

    override fun provideResources(): Resources? {
        return fragmentDevicePairInformationBinding?.root?.resources
    }


    override fun leftIconOnClick() {
        ProvisioningViewModel.getProvisioningViewModel().cancelPairing()
        NavigationViewModel.popBackStack(fragmentDevicePairInformationBinding?.root?.let { Navigation.findNavController(it) })
    }

    override fun provideDescriptionText(): CharSequence? {
        return provideResources()?.getText(R.string.text_description_pair_info)
    }

    override fun provideScreenTimeoutInSeconds(): Int {
        return provideResources()?.getInteger(R.integer.integer_timeout_2mins)?:0
    }

    override fun onHMILeftKnobClick() {
        if (isKnobSelected) {
            HMILogHelper.Logd("PAIR", "Pair button Click")
            fragmentDevicePairInformationBinding?.navigationButtonRight?.callOnClick()
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
            isKnobSelected = true
            fragmentDevicePairInformationBinding?.navigationButtonRight?.background =
                provideResources().let {
                    it?.let { it1 ->
                        ResourcesCompat.getDrawable(
                            it1, R.drawable.selector_textview_walnut, null
                        )
                    }
                }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            isKnobSelected = false
            fragmentDevicePairInformationBinding?.navigationButtonRight?.background =
                provideResources().let {
                    it?.let { it1 ->
                        ResourcesCompat.getDrawable(
                            it1, R.drawable.text_view_ripple_effect, null
                        )
                    }
                }
        }
    }
}
