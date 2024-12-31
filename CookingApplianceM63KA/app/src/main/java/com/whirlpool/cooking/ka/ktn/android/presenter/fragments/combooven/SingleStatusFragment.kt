package android.presenter.fragments.combooven

import android.content.Context
import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.basefragments.AbstractStatusFragment.Companion.KnobItem
import android.presenter.basefragments.abstract_view_helper.AbstractStatusViewHelper
import android.presenter.basefragments.abstract_view_helper.AbstractStatusWidgetHelper
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import core.utils.AppConstants
import core.utils.AppConstants.CONTROL_UNLOCK_FROM_POPUP
import core.utils.CookingAppUtils
import core.utils.DoorEventUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedPreferenceManager
import core.viewHolderHelpers.SingleStatusViewHelper

/**
 * File        : android.presenter.fragments.combooven.SingleStatusFragment
 * Brief       : Single upper status
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Navigation.findNavController(view).currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(CONTROL_UNLOCK_FROM_POPUP)?.observe(
            viewLifecycleOwner
        ) {
            if (it) {
                HMILogHelper.Logd("value of bundle $it")
                Navigation.findNavController(view).currentBackStackEntry?.savedStateHandle
                    ?.set(CONTROL_UNLOCK_FROM_POPUP, false)
            } else {
                HMILogHelper.Logd("value of bundle $it")
            }
            Navigation.findNavController(view).currentBackStackEntry?.savedStateHandle
                ?.getLiveData<Boolean>(CONTROL_UNLOCK_FROM_POPUP)?.removeObservers(viewLifecycleOwner)
        }
        showDoorOpenClosePopupBasedOnDoor()
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, cookingViewModel)
    }
    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        //show dialog here that probe is removed, GCD screen 10
        if(statusViewHelper.getUpperViewModel() != null && statusViewHelper.getUpperViewModel()?.cavityName?.value.contentEquals(cookingViewModel?.cavityName?.value)
            && statusViewHelper.getUpperViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == true)
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, statusViewHelper.getUpperViewModel())
    }
    override fun provideViewHolderHelper(): AbstractStatusViewHelper {
        return statusViewHelper
    }

    override fun provideUpperSetCookTimeText(): CharSequence {
        return getString(R.string.text_button_start_timer)
    }

    override fun provideLowerSetCookTimeText(): CharSequence {
        return getString(R.string.text_button_set_cook_time)
    }

    override fun provideFarViewNavigationId(): Int {
        return R.id.action_singleNearView_to_singleFarView
    }

    override fun updateSteamCleanWidget() {
        // Do nothing
    }

    override fun updateRecipeNameWIthRecipeState(
        context: Context?,
        cookingVM: CookingViewModel,
        recipeCookingState: RecipeCookingState?,
        statusWidgetHelper: AbstractStatusWidgetHelper?,
        rawRecipeName: String?,
        isDoorOpened: Boolean,
        recipeExecutionState: RecipeExecutionState
    ) {
        if (cookingVM.isOfTypeMicrowaveOven && ((isDoorOpened && cookingVM.recipeExecutionViewModel.isRunning) ||
                    (SharedPreferenceManager.getPauseForCancelRecovery(cookingVM.isPrimaryCavity)?.contentEquals(AppConstants.TRUE_CONSTANT) == false &&
                            cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED_EXT))) {
            if(!CookingAppUtils.isUserInstructionRequired(cookingVM)) {
                activity?.supportFragmentManager?.let { CookingAppUtils.dismissAllDialogs(it) }
                if(cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED ||
                    cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED_EXT){
                    when (cookingVM.recipeExecutionViewModel?.isMagnetronUsed) {
                        true -> {
                            HMILogHelper.Logd("Door","CO: Showing MagnetronUsed based popup")
                            NavigationUtils.getVisibleFragment()?.let {
                                PopUpBuilderUtils.mwoDoorOpenPopup(it, cookingVM, onDoorCloseEventAction = {
                                    onResume()
                                })
                            }
                        }
                        else -> {
                            HMILogHelper.Logd("Door","CO: Showing Non - MagnetronUsed based popup")
                            NavigationUtils.getVisibleFragment()?.let {
                                DoorEventUtils.upperCloseDoorToContinueAction(it, cookingVM, onDoorCloseEventAction = {
                                    onResume()
                                })
                            }
                        }
                    }
                }
            }
            return
        }
        super.updateRecipeNameWIthRecipeState(
            context,
            cookingVM,
            recipeCookingState,
            statusWidgetHelper,
            rawRecipeName,
            isDoorOpened,
            recipeExecutionState
        )
    }
    override fun provideKnobRotationItems(): ArrayList<KnobItem> {
        val arrayList :  ArrayList<KnobItem> = ArrayList()
        val upperWidgetHelper = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper
        if(upperWidgetHelper?.tvResumeCooking()?.isVisible == true) {
            HMILogHelper.Logd(tag,"KNOB: UPPER cavity tvResumeCooking is visible, LOWER Cavity is IDLE")
            if (upperWidgetHelper.tvResumeCooking()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        upperWidgetHelper.tvResumeCooking(),
                        false,
                        arrayList.size,
                        provideViewHolderHelper().getUpperViewModel()
                    )
                )
            }
            if (provideViewHolderHelper().provideLowerCavitySelectionLayout()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        provideViewHolderHelper().provideLowerCavitySelectionLayout(),
                        false,
                        arrayList.size,
                        null
                    )
                )
            }
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
            if (provideViewHolderHelper().provideLowerCavitySelectionLayout()?.isVisible == true) {
                arrayList.add(
                    KnobItem(
                        provideViewHolderHelper().provideLowerCavitySelectionLayout(),
                        false,
                        arrayList.size,
                        null
                    )
                )
            }
            return arrayList
        }
    }


    override fun updateStatusWidget(statusWidget: CookingStatusWidget) {
        statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
            View.VISIBLE
    }
}