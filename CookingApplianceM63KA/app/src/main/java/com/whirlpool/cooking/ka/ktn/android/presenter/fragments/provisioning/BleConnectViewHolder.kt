package android.presenter.fragments.provisioning

import android.content.Context
import android.content.res.Resources
import android.framework.services.HMIKnobInteractionListener
import android.graphics.Color
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentDevicePairInformationBinding
import com.whirlpool.hmi.provisioning.ProvisioningViewModel
import com.whirlpool.hmi.provisioning.viewholder.base.AbstractBLEConnectViewHolder
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.utils.AppConstants
import core.utils.AppConstants.IMAGE_SIZE_200
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.handleTextScrollOnKnobRotateEvent
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.SettingsManagerUtils.isUnboxing
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * File       : com.whirlpool.cooking.provisioning.BleConnectViewHolder
 * Brief      : View Holder Class for the fragment to connect to Bluetooth
 * Author     : Vishal
 * Created On : 10-05-2024
 */
class BleConnectViewHolder : AbstractBLEConnectViewHolder(), HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener {
    private var fragmentDevicePairInformationBinding: FragmentDevicePairInformationBinding? = null
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDevicePairInformationBinding = inflater?.let {
            FragmentDevicePairInformationBinding.inflate(
                it
            )
        }
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_CONNECT_TO_NETWORK)
        return fragmentDevicePairInformationBinding?.root
    }

    /*
     * If Entry and Exit animations required later, need to remove this override methods
     */
    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
        fragmentDevicePairInformationBinding?.textViewPin?.visibility = View.GONE
        if(isUnboxing){
            NavigationViewModel.getNavigationViewModel().disableInteractionTimeout()
            HMILogHelper.Logd("Unboxing", "TimeOut Disabled")
        }else{
            HMILogHelper.Logd("Provisioning", "TimeOut Started")
        }
    }

    /**
     * Method to manage Views of the Fragment
     */
    private fun manageChildViews() {
        manageHeaderBar()
        manageNavigationButtons()
        manageQrCode()
    }

    /**
     * Method to manage header bar View of the Fragment
     */
    private fun manageHeaderBar() {
        fragmentDevicePairInformationBinding?.headerBar?.setTitleText(R.string.connect_to_network)
        fragmentDevicePairInformationBinding?.headerBar?.setLeftIconVisibility(true)
        fragmentDevicePairInformationBinding?.headerBar?.setLeftIcon(R.drawable.ic_back_arrow)
        fragmentDevicePairInformationBinding?.headerBar?.setInfoIconVisibility(false)
        fragmentDevicePairInformationBinding?.headerBar?.setRightIconVisibility(false)
        fragmentDevicePairInformationBinding?.headerBar?.setCustomOnClickListener(this)
        fragmentDevicePairInformationBinding?.headerBar?.setOvenCavityIconVisibility(false)
    }

    /**
     * Method to manage NAVIGATION Buttons of the Fragment
     */
    private fun manageNavigationButtons() {
        fragmentDevicePairInformationBinding?.navigationButtonRight?.visibility = View.GONE
    }

    /**
     * Method to manage QR code Image of the Fragment
     */
    private fun manageQrCode() {
        fragmentDevicePairInformationBinding?.pinInfo?.visibility = View.GONE
        fragmentDevicePairInformationBinding?.qrCode?.apply {
            visibility = View.VISIBLE
            CookingAppUtils.getResIdFromResName(
                context,
                AppConstants.CONNECTED_APP_QR_CODE + AppConstants.TEXT_SQUARE,
                AppConstants.RESOURCE_TYPE_DRAWABLE
            ).takeIf { it > 0 }?.let {
                setImageBitmap(context?.resources?.let { it1 ->
                    CookingAppUtils.resizeBitmapUsingMatrix(
                        it1, it, IMAGE_SIZE_200, IMAGE_SIZE_200
                    )
                })
            }
        }

    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        fragmentDevicePairInformationBinding = null
    }

    override fun provideResources(): Resources? {
        return fragmentDevicePairInformationBinding?.root?.resources
    }

    override fun provideDescriptionTextView(): TextView? {
        return fragmentDevicePairInformationBinding?.textViewDescription
    }

    override fun provideDescription(): CharSequence? {
        return provideResources()?.getString(R.string.text_connect_device_info)
    }

    override fun provideSaidValueTextView(): TextView? {
        return fragmentDevicePairInformationBinding?.textViewSAID
    }

    override fun handleSaid(saidValue: String) {
        val text = SpannableString(provideResources()?.getString(R.string.text_said, saidValue))
        if (saidValue != AppConstants.EMPTY_STRING) {
            text.setSpan(
                ForegroundColorSpan(Color.WHITE), text.length - saidValue.length,
                text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        provideSaidValueTextView()?.setText(text, TextView.BufferType.SPANNABLE)
    }

    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(fragmentDevicePairInformationBinding?.root?.let { Navigation.findNavController(it) })
        stopBluetoothServer()
        KnobNavigationUtils.setBackPress()
    }

    private fun stopBluetoothServer() {
        if (ProvisioningViewModel.getProvisioningViewModel().isServerStarted) {
            ProvisioningViewModel.getProvisioningViewModel().stopServer()
            HMILogHelper.Logd("Bluetooth Server stopped successfully!!")
        }
    }

    override fun provideScreenTimeoutInSeconds(): Int {
        return provideResources()?.getInteger(R.integer.integer_timeout_10mins)?:0
    }

    override fun onHMILeftKnobClick() {
        
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
            // Use the custom coroutine scope to launch the coroutine
            coroutineScope.launch {
                val scrollView = fragmentDevicePairInformationBinding?.informationPrimaryScrollView
                val scrollViewContent = fragmentDevicePairInformationBinding?.llInformation
                val lineHeight =
                    scrollViewContent?.measuredHeight?.let { it / scrollViewContent.childCount }
                        ?: 0
                if (scrollView != null && scrollViewContent != null) {
                    handleTextScrollOnKnobRotateEvent(scrollView, lineHeight, knobDirection)
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
    }
}
