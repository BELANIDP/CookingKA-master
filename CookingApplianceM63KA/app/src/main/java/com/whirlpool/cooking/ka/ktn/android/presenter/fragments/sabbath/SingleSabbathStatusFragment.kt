@file:Suppress("KotlinConstantConditions")

package android.presenter.fragments.sabbath

import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.basefragments.AbstractStatusFragment.Companion.KnobItem
import android.presenter.basefragments.abstract_view_helper.AbstractStatusViewHelper
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.faultcodesutils.FaultDetails

/**
 * File        : android.presenter.fragments.sabbath.SingleSabbathStatusFragment
 * Brief       : Single upper cavity status
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing cooking status widget along with its helpers.
 * Override AbstractStatusFragment protected method to have individual functionality related to this variant only
 */
class SingleSabbathStatusFragment : AbstractStatusFragment(), HMIExpansionUtils.HMIErrorCodesListener {
    private lateinit var statusViewHelper: AbstractStatusViewHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        statusViewHelper = SingleSabbathStatusViewHelper()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        manageSabbathLights()
        HMIExpansionUtils.setHMIErrorCodesListener(this)
        if(MeatProbeUtils.isMeatProbeConnected(statusViewHelper.getUpperViewModel()))
            sabbathProbeConnectTimeout()
    }
    override fun onStop() {
        super.onStop()
        HMIExpansionUtils.removeHMIErrorCodesListener(this)
    }
    override fun onHMICancelButtonInteraction() {
        sabbathOnHMICancelButtonInteraction()
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if (statusViewHelper.getUpperViewModel() != null && statusViewHelper.getUpperViewModel()?.cavityName?.value.contentEquals(
                cookingViewModel?.cavityName?.value
            )
        ) {
            HMILogHelper.Loge(
                tag,
                "Sabbath recipe, probe is inserted for cavity ${cookingViewModel?.cavityName?.value} initiating Probe Timeout"
            )
            sabbathProbeConnectTimeout()
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        //show dialog here that probe is removed, GCD screen 10
        if (statusViewHelper.getUpperViewModel() != null && statusViewHelper.getUpperViewModel()?.cavityName?.value.contentEquals(
                cookingViewModel?.cavityName?.value
            )
        ) {
            HMILogHelper.Loge(
                tag,
                "Sabbath recipe, probe is removed for cavity ${cookingViewModel?.cavityName?.value} stopping Probe Timeout"
            )
            stopSabbathProbeConnectTimeout()
        }
    }

    override fun statusWidgetOnClick(
        view: View?,
        statusWidget: CookingStatusWidget?,
        viewModel: CookingViewModel?,
    ) {
        sabbathStatusWidgetOnClick(view, statusWidget, viewModel)
    }

    override fun isTimeoutApplicable(): Boolean {
        return false
    }

    override fun provideFarViewNavigationId(): Int {
        return 0
    }

    override fun provideViewHolderHelper(): AbstractStatusViewHelper {
        return statusViewHelper
    }

    /***************************** KNOB Related *********************************/
    override fun onHMIRightKnobClick() {
        //do nothing
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        //do nothing
    }

    override fun onHMILeftKnobClick() {
        //do nothing
    }

    override fun onHMILongLeftKnobPress() {
        //do nothing
    }

    override fun onHMILongRightKnobPress() {
        // do nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        // do nothing
    }

    override fun onHMIFaultId(cookingViewModel: CookingViewModel, faultId: Int?) {
        HMILogHelper.Logd(tag, "SABBATH Fault SingleStatus, cavityName ${cookingViewModel.cavityName.value}, faultId $faultId")
        if (faultId != 0) {
            val faultDetails = cookingViewModel.faultCode.value?.let { FaultDetails.getInstance(it) }
            if(CookingAppUtils.isFaultAorC(faultDetails)){
                HMILogHelper.Logd(tag, "SABBATH Fault SingleStatus, cavityName ${cookingViewModel.cavityName.value}, faultDetails ${faultDetails?.getFaultCode()} is Type A 0r C so cancelling for both cavity")
                CookingAppUtils.cancelIfAnyRecipeIsRunning()
            }
        }
    }

    override fun onHMICommunicationFaultCode(
        cookingViewModel: CookingViewModel,
        communicationFaultCode: String?,
    ) {
        HMILogHelper.Logd(tag, "SABBATH Fault SingleStatus, cavityName ${cookingViewModel.cavityName.value}, communicationFaultCode $communicationFaultCode")
    }
    override fun provideKnobRotationItems(): ArrayList<KnobItem>? {
        return null
    }


    override fun updateStatusWidget(statusWidget: CookingStatusWidget) {
        statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility = View.VISIBLE
    }

    override fun updateSteamCleanWidget() {
        //Do nothing
    }
    /***************************** KNOB Related *********************************/
}