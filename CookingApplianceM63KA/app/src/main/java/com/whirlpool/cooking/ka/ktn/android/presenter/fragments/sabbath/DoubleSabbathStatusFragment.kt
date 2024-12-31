@file:Suppress("KotlinConstantConditions")

package android.presenter.fragments.sabbath

import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.basefragments.abstract_view_helper.AbstractStatusViewHelper
import android.presenter.basefragments.abstract_view_helper.AbstractStatusWidgetHelper
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.MeatProbeUtils
import core.utils.faultcodesutils.FaultDetails
import java.util.concurrent.TimeUnit

/**
 * File        : android.presenter.fragments.sabbath.DoubleSabbathStatusFragment
 * Brief       : Double cavity status for Sabbath Recipes
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing cooking status widget along with its helpers.
 * Override AbstractStatusFragment protected method to have individual functionality related to this variant only
 */
class DoubleSabbathStatusFragment : AbstractStatusFragment(), HMIExpansionUtils.HMIErrorCodesListener {
    private lateinit var statusViewHelper: AbstractStatusViewHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        statusViewHelper = DoubleSabbathStatusViewHelper()
        return super.onCreateView(inflater, container, savedInstanceState)
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
            sabbathProbeConnectTimeout(statusViewHelper.getUpperViewModel())
        } else if (statusViewHelper.getLowerViewModel() != null && statusViewHelper.getLowerViewModel()?.cavityName?.value.contentEquals(
                cookingViewModel?.cavityName?.value
            )
        ) {
            HMILogHelper.Loge(
                tag,
                "Sabbath recipe, probe is inserted for cavity ${cookingViewModel?.cavityName?.value} initiating Probe Timeout"
            )
            sabbathProbeConnectTimeout(statusViewHelper.getLowerViewModel())
        }
    }

    override fun onStart() {
        super.onStart()
        manageSabbathLights()
        HMIExpansionUtils.setHMIErrorCodesListener(this)
        if (MeatProbeUtils.isMeatProbeConnected(statusViewHelper.getUpperViewModel())) sabbathProbeConnectTimeout(
            statusViewHelper.getUpperViewModel()
        )
        if (MeatProbeUtils.isMeatProbeConnected(statusViewHelper.getLowerViewModel())) sabbathProbeConnectTimeout(
            statusViewHelper.getLowerViewModel()
        )
    }
    override fun onStop() {
        super.onStop()
        HMIExpansionUtils.removeHMIErrorCodesListener(this)
    }

    override fun provideKnobRotationItems(): ArrayList<Companion.KnobItem>? {
        return null
    }


    override fun updateStatusWidget(statusWidget: CookingStatusWidget) {
        statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility = View.VISIBLE
    }

    override fun manageSabbathStatusWidgetOnExecutionState(
        statusWidgetHelper: AbstractStatusWidgetHelper,
        cookingVM: CookingViewModel?,
        recipeExecutionState: RecipeExecutionState,
    ) {
        //if both cavities have fault then cancel both cavities RecipeExecutionViewModel and let super method decide to navigate
        if (!CookingAppUtils.isCavityFaultNone(statusViewHelper.getLowerViewModel()) && !CookingAppUtils.isCavityFaultNone(statusViewHelper.getUpperViewModel())){
            HMILogHelper.Loge(tag,"Sabbath ExecutionState both Cavity have fault primaryCavity faultCode occurred : ${statusViewHelper.getUpperViewModel()?.faultCode?.value}, communicationFaultCode occurred : ${statusViewHelper.getUpperViewModel()?.communicationFaultCode?.value}")
            HMILogHelper.Loge(tag,"Sabbath ExecutionState both Cavity have fault secondaryCavity faultCode occurred : ${statusViewHelper.getLowerViewModel()?.faultCode?.value}, communicationFaultCode occurred : ${statusViewHelper.getLowerViewModel()?.communicationFaultCode?.value}")
            super.manageSabbathStatusWidgetOnExecutionState(statusWidgetHelper, statusViewHelper.getLowerViewModel(), recipeExecutionState)
            super.manageSabbathStatusWidgetOnExecutionState(statusWidgetHelper, statusViewHelper.getUpperViewModel(), recipeExecutionState)
            return
        }
        if (CookingAppUtils.isCavityFaultNone(cookingVM)) {
            //if any one of the cavity has no fault then let super method decide
            super.manageSabbathStatusWidgetOnExecutionState(statusWidgetHelper, cookingVM, recipeExecutionState)
        } else {
            //if cavity has fault then only update that particular cavity's UI
            HMILogHelper.Loge(tag,"Sabbath ExecutionState ${cookingVM?.cavityName?.value} faultCode occurred : ${cookingVM?.faultCode?.value}, communicationFaultCode occurred : ${cookingVM?.communicationFaultCode?.value}")
            statusWidgetHelper.tvCookTimeRemaining()?.visibility = View.GONE
            statusWidgetHelper.tvSabbathTemperatureUp()?.visibility = View.GONE
            statusWidgetHelper.tvSabbathTemperatureDown()?.visibility = View.GONE
            statusWidgetHelper.tvRecipeWithTemperature()?.text =
                getString(R.string.text_title_error)
            statusWidgetHelper.tvRecipeWithTemperature()
                ?.setTextAppearance(R.style.SabbathStyleCookingStatusWidgetErrorTextView)

        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        if (statusViewHelper.getUpperViewModel() != null && statusViewHelper.getUpperViewModel()?.cavityName?.value.contentEquals(
                cookingViewModel?.cavityName?.value
            )
        ) {
            HMILogHelper.Loge(
                tag,
                "Sabbath recipe, probe is removed for cavity ${cookingViewModel?.cavityName?.value} stopping Probe Timeout"
            )
            stopSabbathProbeConnectTimeout(statusViewHelper.getUpperViewModel())
        } else if (statusViewHelper.getLowerViewModel() != null && statusViewHelper.getLowerViewModel()?.cavityName?.value.contentEquals(
                cookingViewModel?.cavityName?.value
            )
        ) {
            HMILogHelper.Loge(
                tag,
                "Sabbath recipe, probe is removed for cavity ${cookingViewModel?.cavityName?.value} stopping Probe Timeout"
            )
            stopSabbathProbeConnectTimeout(statusViewHelper.getLowerViewModel())
        }
    }

    /**
     * Method to call to initiate screen timeout if any particular cavity has Meat Probe inserted
     */
    private fun sabbathProbeConnectTimeout(cookingViewModel: CookingViewModel?) {
        val handler = getCavityHandler(if (cookingViewModel?.isPrimaryCavity == true) 1 else 2)
        handler.postDelayed(
            {
                HMILogHelper.Logd("sabbathHandler elapsed: ${cookingViewModel?.cavityName?.value}")
                if (MeatProbeUtils.isMeatProbeConnected(cookingViewModel)) {
                    HMILogHelper.Loge(
                        tag,
                        "Cancelling Probe Connect Timeout ELAPSED for cavity ${cookingViewModel?.cavityName?.value}"
                    )
                    cookingViewModel?.recipeExecutionViewModel?.cancel()
                }
            },
            TimeUnit.MINUTES.toMillis(AppConstants.RECIPE_TIMEOUT_COOKING_COMPLETE_10_MINUTES.toLong())
        )
    }

    /**
     * remove the timeout if meat probe is removed
     */
    private fun stopSabbathProbeConnectTimeout(cookingViewModel: CookingViewModel?) {
        HMILogHelper.Loge(
            tag,
            "Stopping Probe Connect Timeout for cavity ${cookingViewModel?.cavityName?.value}"
        )
        val handler = getCavityHandler(if (cookingViewModel?.isPrimaryCavity == true) 1 else 2)
        handler.removeCallbacksAndMessages(null)
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
        HMILogHelper.Logd(tag, "SABBATH Fault DoubleStatus, cavityName ${cookingViewModel.cavityName.value}, faultId $faultId")
        if (faultId != 0) {
            val faultDetails = cookingViewModel.faultCode.value?.let { FaultDetails.getInstance(it) }
            if(CookingAppUtils.isFaultAorC(faultDetails)){
                HMILogHelper.Logd(tag, "SABBATH Fault DoubleStatus, cavityName ${cookingViewModel.cavityName.value}, faultDetails ${faultDetails?.getFaultCode()} is Type A 0r C so cancelling for both cavity")
                CookingAppUtils.cancelIfAnyRecipeIsRunning()
            }
        }
    }

    override fun onHMICommunicationFaultCode(
        cookingViewModel: CookingViewModel,
        communicationFaultCode: String?,
    ) {
        HMILogHelper.Logd(tag, "SABBATH Fault DoubleStatus, cavityName ${cookingViewModel.cavityName.value}, communicationFaultCode $communicationFaultCode")
    }

    override fun updateSteamCleanWidget() {
        //Do nothing
    }
    /***************************** KNOB Related *********************************/

}