package android.presenter.fragments.doubleoven

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
import core.viewHolderHelpers.SingleStatusLowerViewHelper

/**
 * File        : android.presenter.fragments.doubleoven.SingleLowerStatusFragment
 * Brief       : Single lower cavity status
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing cooking status widget along with its helpers.
 * Override AbstractStatusFragment protected method to have individual functionality related to this variant only
 */
class SingleLowerStatusFragment : AbstractStatusFragment() {
    private lateinit var statusViewHelper: AbstractStatusViewHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        statusViewHelper = SingleStatusLowerViewHelper()
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun updateSteamCleanWidget() {
        if (isLowerSteamCleanRunning) {
            provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.apply{
                tvSetCookTime()?.visibility = View.GONE
                tvOvenStateAction()?.visibility = View.GONE
                getCavityMoreMenu()?.visibility = View.GONE
            }
        }
    }

    override fun provideFarViewNavigationId(): Int {
        return R.id.action_singleNearView_to_singleFarView
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if (statusViewHelper.getLowerViewModel() != null && statusViewHelper.getLowerViewModel()?.cavityName?.value.contentEquals(
                cookingViewModel?.cavityName?.value
            )
            && statusViewHelper.getLowerViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == false
        ) {
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(
                this,
                statusViewHelper.getLowerViewModel()
            )
            return
        }
        PopUpBuilderUtils.probeDetectedInOtherCavityMidWayRecipeRunning(
            this,
            cookingViewModel,
            statusViewHelper.getLowerViewModel()
        )
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        //show dialog here that probe is removed, GCD screen 10
        if (statusViewHelper.getLowerViewModel() != null && statusViewHelper.getLowerViewModel()?.cavityName?.value.contentEquals(
                cookingViewModel?.cavityName?.value
            )
            && statusViewHelper.getLowerViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == true
        )
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(
                this,
                statusViewHelper.getLowerViewModel()
            )
    }
    override fun provideKnobRotationItems(): ArrayList<KnobItem> {
        val lowerWidgetHelper = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper
        val arrayList :  ArrayList<KnobItem> = ArrayList()
        if(lowerWidgetHelper?.tvResumeCooking()?.isVisible == true){
            HMILogHelper.Logd(tag, "KNOB: LOWER cavity tvResumeCooking is visible, Upper Cavity is IDLE")
            if (provideViewHolderHelper().provideUpperCavitySelectionLayout()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        provideViewHolderHelper().provideUpperCavitySelectionLayout(),
                        false,
                        arrayList.size,
                        null
                    )
                )
            }
            if (lowerWidgetHelper.tvResumeCooking()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        lowerWidgetHelper.tvResumeCooking(),
                        false,
                        arrayList.size,
                        provideViewHolderHelper().getLowerViewModel()
                    )
                )
            }
            return arrayList
        }else{
            HMILogHelper.Logd(tag, "KNOB: LOWER cavity tvResumeCooking is NOT visible, Upper Cavity is IDLE")
            if (provideViewHolderHelper().provideUpperCavitySelectionLayout()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        provideViewHolderHelper().provideUpperCavitySelectionLayout(),
                        false,
                        arrayList.size,
                        null
                    )
                )
            }
            if(lowerWidgetHelper?.isCookTimeNotAllowed() == false) {
                if (lowerWidgetHelper.tvSetCookTime()?.isVisible == true) {
                    arrayList.add(
                        KnobItem(
                            lowerWidgetHelper.tvSetCookTime(),
                            false,
                            arrayList.size,
                            provideViewHolderHelper().getLowerViewModel()
                        )
                    )
                }
            }
            if (lowerWidgetHelper?.tvOvenStateAction()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        lowerWidgetHelper.tvOvenStateAction(),
                        false,
                        arrayList.size,
                        provideViewHolderHelper().getLowerViewModel()
                    )
                )
            }
            if (lowerWidgetHelper?.getCavityMoreMenu()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        lowerWidgetHelper.getCavityMoreMenu(),
                        false,
                        arrayList.size,
                        provideViewHolderHelper().getLowerViewModel()
                    )
                )
            }
            return arrayList
        }
    }

    override fun updateStatusWidget(statusWidget: CookingStatusWidget) {
        if (isLowerSteamCleanRunning) {
            statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                View.GONE
        } else {
            statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                View.VISIBLE
        }
    }

    override fun provideViewHolderHelper(): AbstractStatusViewHelper {
        return statusViewHelper
    }

}