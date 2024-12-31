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
import core.viewHolderHelpers.DoubleStatusViewHelper

/**
 * File        : android.presenter.fragments.combooven.DoubleStatusFragment
 * Brief       : Double upper status and lower status combine
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing cooking status widget along with its helpers.
 * Override AbstractStatusFragment protected method to have individual functionality related to this variant only
 */
@Suppress("KotlinConstantConditions")
class DoubleStatusFragment : AbstractStatusFragment() {
    private lateinit var statusViewHelper: AbstractStatusViewHelper
    override fun provideFarViewNavigationId(): Int {
        return R.id.action_doubleNearView_to_doubleFarView
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        statusViewHelper = DoubleStatusViewHelper()
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

    override fun updateSteamCleanWidget() {
        if (isLowerSteamCleanRunning) {
            HMILogHelper.Logd(tag, "Steam clean: Lower steam clean is running")
            provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.apply {
                tvSetCookTime()?.visibility = View.GONE
                tvOvenStateAction()?.visibility = View.GONE
                getCavityMoreMenu()?.visibility = View.GONE
            }
        } else {
            HMILogHelper.Logd(tag, "recipe is running")
        }
    }


    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        val isNeedToShowPopup =
            if (statusViewHelper.getLowerViewModel() != null && statusViewHelper.getLowerViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == false) {
                true
            } else {
                CookingAppUtils.isProbeBasedRecipeAndTemperatureReached(statusViewHelper.getLowerViewModel()?.recipeExecutionViewModel)
            }
        if (isNeedToShowPopup) {
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(
                this,
                statusViewHelper.getLowerViewModel()
            )
        }
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
                            HMILogHelper.Logd("Door","DO: Showing MagnetronUsed based popup")
                            NavigationUtils.getVisibleFragment()?.let {
                                PopUpBuilderUtils.mwoDoorOpenPopup(it, cookingVM, onDoorCloseEventAction = {
                                    onResume()
                                })
                            }
                        }
                        else -> {
                            HMILogHelper.Logd("Door","DO: Showing Non - MagnetronUsed based popup")
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

    override fun provideKnobRotationItems(): ArrayList<KnobItem>? {
        val upperWidgetHelper = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper
        val lowerWidgetHelper = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper
        if(upperWidgetHelper?.tvResumeCooking()?.isVisible == false && lowerWidgetHelper?.tvResumeCooking()?.isVisible == false){
            HMILogHelper.Logd(tag, "KNOB: both cavity tvResumeCooking is not visible")
            val arrayList :  ArrayList<KnobItem> = ArrayList()
            if(upperWidgetHelper.isCookTimeNotAllowed() == false) {
                if(upperWidgetHelper.tvSetCookTime()?.isVisible == true) {
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
            if(lowerWidgetHelper.isCookTimeNotAllowed() == false) {
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

    override fun updateStatusWidget(statusWidget: CookingStatusWidget) {
        if (isLowerSteamCleanRunning && !isUpperSteamCleanRunning) {
            if (statusWidget.ovenType == resources.getString(R.string.cavity_selection_lower_oven)) {
                statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                    View.GONE
            } else {
                statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                    View.VISIBLE
            }
        } else {
            statusWidget.statusWidgetHelper.clParentWidgetAction()?.visibility =
                View.VISIBLE
        }
    }

    private fun provideKnobRotationItemsUpperResume(): ArrayList<KnobItem> {
        val arrayList :  ArrayList<KnobItem> = ArrayList()
        val lowerWidgetHelper = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper
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
    private fun provideKnobRotationItemsLowerResume(): ArrayList<KnobItem> {
        val arrayList :  ArrayList<KnobItem> = ArrayList()
        val upperWidgetHelper = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper
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
        if (upperWidgetHelper?.getCavityMoreMenu()?.isVisible ==true) {
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
}