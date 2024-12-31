@file:Suppress("KotlinConstantConditions")

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
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.PopUpBuilderUtils
import core.viewHolderHelpers.DoubleStatusViewHelper

/**
 * File        : android.presenter.fragments.doubleoven.DoubleStatusFragment
 * Brief       : Double cavity status lower and upper
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing cooking status widget along with its helpers.
 * Override AbstractStatusFragment protected method to have individual functionality related to this variant only
 */
class DoubleStatusFragment : AbstractStatusFragment() {
    private lateinit var statusViewHelper: AbstractStatusViewHelper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        statusViewHelper = DoubleStatusViewHelper()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun updateSteamCleanWidget() {
        if (isUpperSteamCleanRunning && !isLowerSteamCleanRunning) {
            HMILogHelper.Logd(tag, "Steam clean: Upper steam clean is running")
            updateUpperSteamCleanWidget()
        } else if (isLowerSteamCleanRunning && !isUpperSteamCleanRunning) {
            HMILogHelper.Logd(tag, "Steam clean: Lower steam clean is running")
            updateLowerSteamCleanWidget()
        } else if (isLowerSteamCleanRunning && isUpperSteamCleanRunning) {
            HMILogHelper.Logd(tag, "Steam clean: Upper and Lower steam clean is running")
            updateUpperSteamCleanWidget()
            updateLowerSteamCleanWidget()
        } else {
            HMILogHelper.Logd(tag, "recipe is running")
        }
    }

    private fun updateLowerSteamCleanWidget() {
        provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.apply {
            tvSetCookTime()?.visibility = View.GONE
            tvOvenStateAction()?.visibility = View.GONE
            getCavityMoreMenu()?.visibility = View.GONE
        }
    }

    private fun updateUpperSteamCleanWidget() {
        provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.apply {
            tvSetCookTime()?.visibility = View.GONE
            tvOvenStateAction()?.visibility = View.GONE
            getCavityMoreMenu()?.visibility = View.GONE
        }
    }

    override fun provideFarViewNavigationId(): Int {
        return R.id.action_doubleNearView_to_doubleFarView
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if (statusViewHelper.getLowerViewModel() != null && statusViewHelper.getLowerViewModel()?.cavityName?.value.contentEquals(
                cookingViewModel?.cavityName?.value
            )
            && statusViewHelper.getLowerViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == false
        )
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(
                this,
                statusViewHelper.getLowerViewModel()
            )
        else if (statusViewHelper.getUpperViewModel() != null && statusViewHelper.getUpperViewModel()?.cavityName?.value.contentEquals(
                cookingViewModel?.cavityName?.value
            )
            && statusViewHelper.getUpperViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == false
        )
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(
                this,
                statusViewHelper.getUpperViewModel()
            )
        else  {
            if (CookingAppUtils.isProbeBasedRecipeAndTemperatureReached(cookingViewModel?.recipeExecutionViewModel)) {
                val existingCookingViewModel = if (statusViewHelper.getLowerViewModel() != null && statusViewHelper.getLowerViewModel()?.cavityName?.value.contentEquals(
                            cookingViewModel?.cavityName?.value
                        )
                    ) {
                        statusViewHelper.getLowerViewModel()
                    } else {
                        statusViewHelper.getUpperViewModel()
                    }
                PopUpBuilderUtils.probeDetectedInOtherCavityMidWayRecipeRunning(
                    this,
                    cookingViewModel, existingCookingViewModel
                )
            }
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        val isProbeExtendedCycle = CookingAppUtils.isProbeBasedRecipeAndTemperatureReached(cookingViewModel?.recipeExecutionViewModel)
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && !isProbeExtendedCycle) {
            //show dialog here that probe is removed, GCD screen 10
            if (statusViewHelper.getLowerViewModel() != null && statusViewHelper.getLowerViewModel()?.cavityName?.value.contentEquals(
                    cookingViewModel.cavityName?.value
                )
                && statusViewHelper.getLowerViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == true
            )
                PopUpBuilderUtils.probeRemovedDuringRecipeRunning(
                    this,
                    statusViewHelper.getLowerViewModel()
                )
            else if (statusViewHelper.getUpperViewModel() != null && statusViewHelper.getUpperViewModel()?.cavityName?.value.contentEquals(
                    cookingViewModel.cavityName?.value
                )
                && statusViewHelper.getUpperViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == true
            )
                PopUpBuilderUtils.probeRemovedDuringRecipeRunning(
                    this,
                    statusViewHelper.getUpperViewModel()
                )
        }
    }

    override fun provideViewHolderHelper(): AbstractStatusViewHelper {
        return statusViewHelper
    }
    override fun provideKnobRotationItems(): ArrayList<KnobItem>? {
        val upperWidgetHelper = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper
        val lowerWidgetHelper = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper
        if(upperWidgetHelper?.tvResumeCooking()?.isVisible == false && lowerWidgetHelper?.tvResumeCooking()?.isVisible == false){
            HMILogHelper.Logd(tag, "KNOB: both cavity tvResumeCooking is not visible")
            val arrayList :  ArrayList<KnobItem> = ArrayList()
            if (upperWidgetHelper.isCookTimeNotAllowed() == false) {
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
            if (upperWidgetHelper.tvOvenStateAction()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        upperWidgetHelper.tvOvenStateAction(),
                        false,
                        arrayList.size,
                        provideViewHolderHelper().getUpperViewModel()
                    )
                )
            }
            if (upperWidgetHelper.getCavityMoreMenu()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        upperWidgetHelper.getCavityMoreMenu(),
                        false,
                        arrayList.size,
                        provideViewHolderHelper().getUpperViewModel()
                    )
                )
            }
            if (lowerWidgetHelper.isCookTimeNotAllowed() == false) {
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
            if (lowerWidgetHelper.tvOvenStateAction()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        lowerWidgetHelper.tvOvenStateAction(),
                        false,
                        arrayList.size,
                        provideViewHolderHelper().getLowerViewModel()
                    )
                )
            }
            if (lowerWidgetHelper.getCavityMoreMenu()?.isVisible == true) {
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
        if(upperWidgetHelper?.tvResumeCooking()?.isVisible == true && lowerWidgetHelper?.tvResumeCooking()?.isVisible == false){
            HMILogHelper.Logd(tag, "KNOB: UPPER cavity tvResumeCooking is visible, but NOT on LOWER cavity")
            return provideKnobRotationItemsUpperResume()
        }
        if(upperWidgetHelper?.tvResumeCooking()?.isVisible == false && lowerWidgetHelper?.tvResumeCooking()?.isVisible == true){
            HMILogHelper.Logd(tag, "KNOB: UPPER cavity tvResumeCooking is NOT visible, but IS VISIBLE on LOWER cavity")
            return provideKnobRotationItemsLowerResume()
        }
        if(upperWidgetHelper?.tvResumeCooking()?.isVisible == true && provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking()?.isVisible == true){
            HMILogHelper.Logd(tag, "KNOB: both cavity tvResumeCooking ARE visible")
            return provideKnobRotationItemsBothResume()
        }
        HMILogHelper.Logd(tag, "KNOB: Rotation items are not defined")
        return null
    }
    private fun provideKnobRotationItemsUpperResume(): ArrayList<KnobItem> {
        val arrayList :  ArrayList<KnobItem> = ArrayList()
        val lowerWidgetHelper = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper
        if (provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking()?.isVisible ==true) {
            arrayList.add(
                KnobItem(
                    provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking(),
                    false,
                    arrayList.size,
                    provideViewHolderHelper().getUpperViewModel()
                )
            )
        }
        if (lowerWidgetHelper?.isCookTimeNotAllowed() == false) {
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
    private fun provideKnobRotationItemsLowerResume(): ArrayList<KnobItem> {
        val arrayList :  ArrayList<KnobItem> = ArrayList()
        val upperWidgetHelper = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper
        if(upperWidgetHelper?.isCookTimeNotAllowed() == false) {
            if (upperWidgetHelper.tvSetCookTime()?.isVisible ==true) {
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
        if (provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking()?.isVisible == true) {
            arrayList.add(
                KnobItem(
                    provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking(),
                    false,
                    arrayList.size,
                    provideViewHolderHelper().getLowerViewModel()
                )
            )
        }
        return arrayList
    }
    private fun provideKnobRotationItemsBothResume(): ArrayList<KnobItem> {
        val arrayList :  ArrayList<KnobItem> = ArrayList()
        if (provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking()?.isVisible == true) {
            arrayList.add(
                KnobItem(
                    provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking(),
                    false,
                    arrayList.size,
                    provideViewHolderHelper().getUpperViewModel()
                )
            )
        }
        if (provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking()?.isVisible == true) {
            arrayList.add(
                KnobItem(
                    provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking(),
                    false,
                    arrayList.size,
                    provideViewHolderHelper().getLowerViewModel()
                )
            )
        }
        return arrayList
    }

    override fun updateStatusWidget(statusWidget: CookingStatusWidget) {
        if (isUpperSteamCleanRunning && !isLowerSteamCleanRunning) {
            if (statusWidget.ovenType == resources.getString(R.string.cavity_selection_upper_oven)) {
                statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                    View.GONE
            } else {
                statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                    View.VISIBLE
            }
        } else if (isLowerSteamCleanRunning && !isUpperSteamCleanRunning) {
            if (statusWidget.ovenType == resources.getString(R.string.cavity_selection_lower_oven)) {
                statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                    View.GONE
            } else {
                statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                    View.VISIBLE
            }
        } else if (isLowerSteamCleanRunning && isUpperSteamCleanRunning) {
            statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                View.GONE
        } else {
            statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                View.VISIBLE
        }
    }
}