package android.presenter.fragments.ota

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.OtaHomeViewholderBinding
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.ota.viewmodel.OTAViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper.Logd
import core.utils.KnobDirection
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils


/**
 * File        : android.presenter.fragments.ota.OtaHomeViewHolder
 * Brief       : View Holder Class for OTA Home
 * Author      : Rajendra
 * Created On  : 4-SEP-2024
 * Details     : Provide View for Launching OTA screen, displaying target version, skip update and update now functionality
 */
class OtaBusyUpdateAvailableViewHolder : SuperAbstractTimeoutEnableFragment(), HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener,View.OnClickListener {
    private var otaViewModel: OTAViewModel? = null

    private var viewBinding: OtaHomeViewholderBinding? = null
    //Knob Implementation
    private var knobRotationCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = OtaHomeViewholderBinding.inflate(inflater, container, false)
        Logd(TAG, "Showing update available View Holder")
        otaViewModel = OTAVMFactory.getOTAViewModel()
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        managePreferencesCollectionHeaderBar()
        updateVersionInfo()
        onBusyOTAUpdateAvailable()
        viewBinding?.btnUpdateNow?.setOnClickListener(this)
        viewBinding?.titleBar?.setCustomOnClickListener(this)
        viewBinding?.btnUpdateNow?.visibility = View.VISIBLE
        viewBinding?.btnUpdateNow?.text = resources.getString(R.string.text_button_update_now)
        viewBinding?.informationPrimaryScrollView?.visibility = View.GONE
        viewBinding?.versionDetails?.visibility = View.GONE
        viewBinding?.frameTvSWUpToDate?.visibility = View.GONE
    }

    private fun updateVersionInfo() {
        if (otaViewModel?.otaState != null && (otaViewModel?.otaState?.value == OTAStatus.BUSY
                    || otaViewModel?.otaState?.value == OTAStatus.ERROR)) {
            viewBinding?.softwareUpdateScheduled?.visibility = View.VISIBLE
            viewBinding?.softwareUpdateScheduled?.text = resources.getString(
                R.string.text_software_update_scheduled, otaViewModel?.targetSystemVersion?.value
            )
            viewBinding?.thisMayTakeMinutes?.visibility = View.VISIBLE
            viewBinding?.thisMayTakeMinutes?.text =
                resources.getString(R.string.text_software_update_scheduled_time)
        } else {
            viewBinding?.softwareUpdateScheduled?.visibility = View.GONE
            viewBinding?.thisMayTakeMinutes?.visibility = View.GONE
        }
    }

    private fun managePreferencesCollectionHeaderBar() {
        Logd("Unboxing", "isUnboxing =${SettingsManagerUtils.isUnboxing}")
        if (SettingsManagerUtils.isUnboxing) {
            viewBinding?.titleBar?.setLeftIconVisibility(false)
            viewBinding?.titleBar?.setRightIconVisibility(false)
            viewBinding?.titleBar?.setOvenCavityIconVisibility(false)
            viewBinding?.titleBar?.setInfoIconVisibility(false)
            viewBinding?.btnSkipUpdate?.visibility = View.VISIBLE
            viewBinding?.btnSkipUpdate?.text = resources.getString(R.string.text_button_update_later)
            viewBinding?.btnSkipUpdate?.setOnClickListener(this)
        } else {
            viewBinding?.titleBar?.setLeftIconVisibility(true)
            viewBinding?.titleBar?.setRightIconVisibility(true)
            viewBinding?.titleBar?.setOvenCavityIconVisibility(false)
            viewBinding?.titleBar?.setInfoIconVisibility(false)
            viewBinding?.titleBar?.setRightIcon(R.drawable.ic_close)
        }
        viewBinding?.titleBar?.setTitleText(R.string.text_subMenu_network_softwareUpdate)
        viewBinding?.titleBar?.getHeaderTitle()?.visibility = View.VISIBLE

    }

    override fun leftIconOnClick() {
        if (!SettingsManagerUtils.isUnboxing) {
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    NavigationUtils.getViewSafely(
                        this
                    ) ?: requireView()
                )
            )
        }
    }

    override fun rightIconOnClick() {
        Logd(TAG,"OTA : Navigating to Clock Screen")
        CookingAppUtils.navigateToStatusOrClockScreen(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }

    private fun onBusyOTAUpdateAvailable() {
        val spannableStringBuilder = CookingAppUtils.extractSecondaryTextDescriptionFileContent(otaViewModel?.descriptionFileContent?.value)
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

    override fun onClick(view: View?) {
        if (view?.id == R.id.btnUpdateNow){
            if ((OTAVMFactory.getOTAViewModel().otaState.value == OTAStatus.BUSY ||
                        OTAVMFactory.getOTAViewModel().otaState.value == OTAStatus.ERROR)
                && !CookingAppUtils.checkApplianceBusyStateForOTA()) {
                OTAVMFactory.getOTAViewModel().setApplianceBusyState(false)
                otaViewModel?.otaManager?.updateNow()
                PopUpBuilderUtils.initAndStartOtaFlow(this, true)
            }
        }else if (view?.id == R.id.btnSkipUpdate){
            Logd("Unboxing", "Unboxing: Navigate to user role list flow")
            //For smooth transition between fragment we have added navOption with anim parameter
            val navOptions = NavOptions
                .Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.fade_out)
                .build()
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                NavigationUtils.navigateSafely(
                    it,
                    R.id.global_action_to_unboxingRegionalSettingsFragment,
                    null,
                    navOptions
                )
            }
        }
    }


    companion object {
        private val TAG: String = OtaBusyUpdateAvailableViewHolder::class.java.simpleName
    }

    override fun onHMILeftKnobClick() {
        Logd("OTA", "OTA onHMILeftKnobClick")
        if (viewBinding?.btnUpdateNow?.isVisible == true && viewBinding?.btnSkipUpdate?.isVisible == true) {
            when (knobRotationCount) {
                AppConstants.KNOB_COUNTER_ONE -> {
                    viewBinding?.btnSkipUpdate?.callOnClick()
                }

                AppConstants.KNOB_COUNTER_TWO -> {
                    viewBinding?.btnUpdateNow?.callOnClick()
                }
            }
        } else if (viewBinding?.btnUpdateNow?.isVisible == true) {
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
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (viewBinding?.btnUpdateNow?.isVisible == true && viewBinding?.btnSkipUpdate?.isVisible == true) {
                if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        viewBinding?.btnUpdateNow?.background =
                            null
                        viewBinding?.btnSkipUpdate?.background =
                            viewBinding?.btnSkipUpdate?.context.let {
                                ContextCompat.getDrawable(
                                    it!!, R.drawable.selector_textview_walnut
                                )
                            }
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
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
            } else if (viewBinding?.btnUpdateNow?.isVisible == true) {
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
            } else {
                //Do nothing
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        viewBinding?.btnUpdateNow?.background = null
        viewBinding?.btnSkipUpdate?.background = null
        knobRotationCount = 0
    }
}