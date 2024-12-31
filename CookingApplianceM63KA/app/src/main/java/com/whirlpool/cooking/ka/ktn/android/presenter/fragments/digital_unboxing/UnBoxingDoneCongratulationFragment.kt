package android.presenter.fragments.digital_unboxing

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.OtaHomeViewholderBinding
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper.Logd
import core.utils.KnobDirection
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils.addNotificationToQueue
import core.utils.KnobNavigationUtils
import core.utils.NotificationManagerUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.SharedPreferenceManager
import core.utils.SharedPreferenceManager.setTechnicianTestDoneStatusIntoPreference


/**
 * File        : android.presenter.fragments.digital_unboxing.UnboxingDoneCongratulationFragment
 * Brief       : View Holder Class for UnboxingDoneCongratulationFragment
 * Author      : Rajendra
 * Created On  : 10-SEP-2024
 * Details     : Provide View for UnboxingDoneCongratulationFragment screen
 */
class UnBoxingDoneCongratulationFragment : SuperAbstractTimeoutEnableFragment(),
    HMIKnobInteractionListener,View.OnClickListener {
    private var viewBinding: OtaHomeViewholderBinding? = null
    //Knob Implementation
    private var knobRotationCount = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = OtaHomeViewholderBinding.inflate(inflater, container, false)
        Logd(TAG, "Unboxing - Showing congratulation screen")
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //set OTA state to IDLE if OTA is already completed in digital unboxing as we don't want to
        // show any completed popup  after digital unboxing
        if (OTAVMFactory.getOTAViewModel().otaState.value == OTAStatus.COMPLETED) {
            Logd("OTA", "OTA is completed in digital unboxing -> Settings the OTA state to IDLE")
            OTAVMFactory.getOTAViewModel().setOTAToIDLEState()
        } else {
            Logd("OTA", "OTA is completed in digital unboxing -> OTA state is Not IDLE")
        }
        initView()
        SettingsManagerUtils.isUnboxing = false
        setTechnicianTestDoneStatusIntoPreference(AppConstants.FALSE_CONSTANT)
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            knobRotationCount = 1
            viewBinding?.btnUpdateNow?.background =
                resources.let {
                    ResourcesCompat.getDrawable(
                        it, R.drawable.selector_textview_walnut, null
                    )
                }
        }
    }

    private fun initView() {
        viewBinding?.titleBar?.setRightIconVisibility(false)
        viewBinding?.titleBar?.setLeftIconVisibility(false)
        viewBinding?.titleBar?.setInfoIconVisibility(false)
        viewBinding?.titleBar?.setOvenCavityIconVisibility(false)
        viewBinding?.tvWhatIsNewTitle?.visibility = View.GONE
        viewBinding?.titleBar?.setTitleText("")
        viewBinding?.unboxingCelebrationMark?.visibility = View.VISIBLE
        viewBinding?.informationScrollablePrimaryTextView?.visibility = View.VISIBLE
        viewBinding?.informationScrollablePrimaryTextView?.text =
            resources.getString(R.string.text_header_congratulations)
        viewBinding?.informationScrollableSecondaryTextView?.visibility = View.VISIBLE
        viewBinding?.informationScrollableSecondaryTextView?.text = resources.getString(
            R.string.text_description_congratulations,
            resources.getString(R.string.text_description_congratulations)
        )
        viewBinding?.btnUpdateNow?.visibility = View.VISIBLE
        viewBinding?.btnUpdateNow?.text = resources.getString(R.string.text_button_finish)
        viewBinding?.btnUpdateNow?.setOnClickListener(this)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        setGraphAndNavigateToClock(true)
    }

    override fun onDestroyView() {
        //Trigger notification: Tap to begin
        addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_TAP_TO_BEGIN)

        //Trigger notification: Connect to network. User has selected connect later while unboxing
        if (SettingsViewModel.getSettingsViewModel().awsConnectionStatus.value == SettingsViewModel.CloudConnectionState.IDLE){
            addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW)
        }

        //Trigger Get to know notification after connect to nw. User has skipped Explore product while unboxing
        if (SharedPreferenceManager.getSkipExploreFeatureFlag() == AppConstants.TRUE_CONSTANT) {
            //Trigger notification: Explore product skipped.
            addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_GET_TO_KNOW_YOUR_APPLIANCE)
        }

        NotificationManagerUtils.dayCounter.start()
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        viewBinding = null
    }


    override fun onClick(view: View?) {
        when(view?.id){
            viewBinding?.btnUpdateNow?.id ->{
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.start_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                CookingAppUtils.setNavGraphAndNavigateToClock(this)

            }
        }
    }


    companion object {
        private val TAG: String = UnBoxingDoneCongratulationFragment::class.java.simpleName
    }

    override fun onHMILeftKnobClick() {
        Logd("Unboxing", "Unboxing onHMILeftKnobClick")
        viewBinding?.btnSkipUpdate?.background = null
        viewBinding?.btnUpdateNow?.background =
            resources.let {
                ResourcesCompat.getDrawable(
                    it, R.drawable.selector_textview_walnut, null
                )
            }
        viewBinding?.btnUpdateNow?.callOnClick()
    }

    override fun onHMILongLeftKnobPress() {
        Logd("Unboxing", "Unboxing onHMILongLeftKnobPress")
    }

    override fun onHMIRightKnobClick() {
        Logd("Unboxing", "Unboxing onHMIRightKnobClick")
        PopUpBuilderUtils.userLeftKnobWarningPopup(this)
    }

    override fun onHMILongRightKnobPress() {
        Logd("Unboxing", "Unboxing onHMILongRightKnobPress")
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        Logd("Unboxing", "Unboxing onHMIRightKnobTickHoldEvent")
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        Logd("Unboxing", "Unboxing onKnobRotateEvent")
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.MIN_KNOB_POSITION) knobRotationCount++
            else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    viewBinding?.btnSkipUpdate?.background = null
                    viewBinding?.btnUpdateNow?.background =
                        resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                }
            }
        } else if (knobId == AppConstants.RIGHT_KNOB_ID) {
            PopUpBuilderUtils.userLeftKnobWarningPopup(this)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if(knobId == AppConstants.LEFT_KNOB_ID) {
            knobRotationCount = 0
            viewBinding?.btnUpdateNow?.background =
                resources.let {
                    ResourcesCompat.getDrawable(
                        it, R.drawable.text_view_ripple_effect, null
                    )
                }
        }
    }
}