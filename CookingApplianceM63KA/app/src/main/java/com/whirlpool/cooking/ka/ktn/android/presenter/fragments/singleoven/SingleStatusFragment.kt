package android.presenter.fragments.singleoven

import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.basefragments.AbstractStatusFragment.Companion.KnobItem
import android.presenter.basefragments.abstract_view_helper.AbstractStatusViewHelper
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import core.utils.HMILogHelper
import core.utils.PopUpBuilderUtils
import core.viewHolderHelpers.SingleStatusViewHelper

/**
 * File        : android.presenter.fragments.singleoven.SingleStatusFragment
 * Brief       : Single only cavity status
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing cooking status widget along with its helpers.
 * Override AbstractStatusFragment protected method to have individual functionality related to this variant only
 */
class SingleStatusFragment : AbstractStatusFragment() {
    private lateinit var statusViewHelper: AbstractStatusViewHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        statusViewHelper = SingleStatusViewHelper()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun updateSteamCleanWidget() {
        if (isUpperSteamCleanRunning) {
            provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.apply {
                tvSetCookTime()?.visibility = View.GONE
                tvOvenStateAction()?.visibility = View.GONE
                getCavityMoreMenu()?.visibility = View.GONE
            }
        }
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(statusViewHelper.getUpperViewModel() != null && statusViewHelper.getUpperViewModel()?.cavityName?.value.contentEquals(cookingViewModel?.cavityName?.value)
            && statusViewHelper.getUpperViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == false)
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, statusViewHelper.getUpperViewModel())
    }

    override fun provideFarViewNavigationId(): Int {
        return R.id.action_singleNearView_to_singleFarView
    }

    override fun provideViewHolderHelper(): AbstractStatusViewHelper {
        return statusViewHelper
    }
    override fun provideKnobRotationItems(): ArrayList<KnobItem> {
        val arrayList :  ArrayList<KnobItem> = ArrayList()
        val upperWidgetHelper = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper
        if(upperWidgetHelper?.tvResumeCooking()?.isVisible == true) {
            HMILogHelper.Logd(tag,"KNOB: UPPER cavity tvResumeCooking is visible, LOWER Cavity is IDLE")
            arrayList.add(KnobItem(upperWidgetHelper.tvResumeCooking(), false, arrayList.size,provideViewHolderHelper().getUpperViewModel()))
            return arrayList
        }else{
            HMILogHelper.Logd(tag,"KNOB: UPPER cavity tvResumeCooking is NOT visible, LOWER Cavity is IDLE")
            if(upperWidgetHelper?.isCookTimeNotAllowed() == false) {
                if (upperWidgetHelper.tvSetCookTime()?.isVisible == true) {
                    arrayList.add(
                        KnobItem(
                            upperWidgetHelper.tvSetCookTime(),
                            false,
                            arrayList.size,
                            provideViewHolderHelper().getUpperViewModel()
                        )
                    )
                }
            }
            if (upperWidgetHelper?.tvOvenStateAction()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        upperWidgetHelper.tvOvenStateAction(),
                        false,
                        arrayList.size,
                        provideViewHolderHelper().getUpperViewModel()
                    )
                )
            }
            if (upperWidgetHelper?.getCavityMoreMenu()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        upperWidgetHelper.getCavityMoreMenu(),
                        false,
                        arrayList.size,
                        provideViewHolderHelper().getUpperViewModel()
                    )
                )
            }
            return arrayList
        }
    }


    override fun updateStatusWidget(statusWidget: CookingStatusWidget) {
        if (isUpperSteamCleanRunning) {
            statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                View.GONE
        } else {
            statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                View.VISIBLE
        }
    }
}