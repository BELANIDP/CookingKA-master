package core.utils


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.presenter.basefragments.AbstractStatusFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.utils.Constants
import com.whirlpool.hmi.cooking.utils.PowerInterruptState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.HMILogHelper.Logd
import core.utils.SettingsManagerUtils.restoreSleepState
import core.utils.faultcodesutils.FaultDetails
import java.util.Date

/**
 * This class handles all the data across fragments in the view which is not handled by the SDK
 */
class SharedViewModel : ViewModel() {
    private val tag: String = SharedViewModel::class.java.simpleName
    private var currentRecipeBeingProgrammed = AppConstants.EMPTY_STRING
    private lateinit var runnableBrownOut: Runnable
    private lateinit var runnableBlackOut: Runnable
    private lateinit var timeoutHandler: Handler
    private var runnableFaultPopUp: Runnable? = null
    private var isBrownoutCycle = false
    private var isBootTimeEventSingleOven = true
    private var isBootTimeEventDoubleOven = true
    private val isBlackOutRecoveryComplete = MutableLiveData<Boolean>()
    private val isNavigatedFromKnobClick = MutableLiveData<Boolean>()

    /**
     * Flag to know if appliance is in A Or C fault.
     */
    private var isApplianceInAOrCCategoryFault = false

    /**
     * Flag to check if fault popUp is open currently.
     */
    private var isFaultPopUpOpen = false

    /**
     * Flag to know if other cavity fault is shown
     */
    private var showOtherCavityFaultPopUp = false

    /**
     * Flag to know if current fault displayed is in Primary Cavity
     */
    private var isCurrentDisplayedFaultInPrimaryCavity = false

    /**
     * Flag to check if tools menu is open currently.
     **/
    private var isToolsMenuVisible = false

    /**
     * Time variable during unboxing
     */
    private var unBoxingTime = ""

    /**
     * Flag to know if cycle is in paused state for cancelled and 15 seconds grace period
     */
    private var isCycleInPausedStateGracePeriod = false

    fun instantiateBrownOutHandler(navController: NavController, navGraph: NavGraph) {
        timeoutHandler = Handler(Looper.getMainLooper())
        runnableBrownOut = Runnable {
            //If appliance is in the Brick state error code 500 do not navigate to the clock screen
            // because one of the node in our system is stopped working
            if (OTAVMFactory.getOTAViewModel().errorCode.value == AppConstants.OTA_ERROR_CODE_BRICK_STATE) {
                Logd("OTA : Runnable: Appliance is in bricked sate: OTA Error Code 500")
                timeoutHandler.removeCallbacks(runnableBrownOut)
                return@Runnable
            }
            if (!isBrownoutCycle) {
                //Moving to clock screen after waiting for brownout events
                isBrownoutCycle = true
                timeoutHandler.removeCallbacks(runnableBrownOut)
                restoreSleepState()
                navController.graph = navGraph
                if (CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel
                        .recipeExecutionState.value == RecipeExecutionState.DELAYED
                ) {
                    CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel
                        .cancel()
                }
                if (CookingAppUtils.getSecondaryCookingViewModel()
                        ?.recipeExecutionViewModel?.recipeExecutionState
                        ?.value === RecipeExecutionState.DELAYED
                ) {
                    CookingAppUtils.getSecondaryCookingViewModel()?.recipeExecutionViewModel
                        ?.cancel()
                }
            }
        }
    }

    /**
     * Method to handle brownout recovery based on recipe execution state live data
     */
    fun handleBrownOutRecovery(
        activity: FragmentActivity, navController: NavController,
        navGraph: NavGraph, timeout: Int
    ) =
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN ->
                CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel
                    .recipeExecutionState.observe(
                        activity
                    ) { event: RecipeExecutionState ->
                        if (event == RecipeExecutionState.RUNNING_EXT || event == RecipeExecutionState.DELAYED || (CookingAppUtils.isDemoModeEnabled())) {
                            Logd("$tag: Cycle Operations is RUNNING_EXT for Upper oven")
                            CookingViewModelFactory.setInScopeViewModel(
                                CookingViewModelFactory.getPrimaryCavityViewModel()
                            )
                            isBrownoutCycle = true
                            BrownoutUtils.handleBrownoutNavigation(
                                navGraph,
                                navController,
                                activity
                            )
                        } else if (isBootTimeEventSingleOven) {
                            Logd("$tag: Unhandled Cooking Cycle Operations API Event:: $event")
                            isBootTimeEventSingleOven = false
                            timeoutHandler.removeCallbacks(runnableBrownOut)
                            timeoutHandler.postDelayed(runnableBrownOut, timeout.toLong())
                        }
                    }

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel
                    .recipeExecutionState.observe(
                        activity
                    ) { event: RecipeExecutionState ->
                        if (event == RecipeExecutionState.RUNNING_EXT || event == RecipeExecutionState.DELAYED || CookingAppUtils.isDemoModeEnabled()) {
                            Logd("$tag: Cycle Operations is RUNNING_EXT for Upper oven")
                            CookingViewModelFactory.setInScopeViewModel(
                                CookingViewModelFactory.getPrimaryCavityViewModel()
                            )
                            isBrownoutCycle = true
                            BrownoutUtils.handleBrownoutNavigation(
                                navGraph,
                                navController,
                                activity
                            )
                        } else if (isBootTimeEventSingleOven) {
                            Logd("$tag: Unhandled Cooking Cycle Operations API Event:: $event")
                            isBootTimeEventSingleOven = false
                            timeoutHandler.removeCallbacks(runnableBrownOut)
                            timeoutHandler.postDelayed(
                                runnableBrownOut,
                                timeout.toLong()
                            )
                        }
                    }
                CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel
                    .recipeExecutionState.observe(
                        activity
                    ) { event: RecipeExecutionState ->
                        if (event == RecipeExecutionState.RUNNING_EXT || event == RecipeExecutionState.DELAYED || CookingAppUtils.isDemoModeEnabled()) {
                            Logd("$tag: Cycle Operations is RUNNING_EXT for Lower oven")
                            CookingViewModelFactory.setInScopeViewModel(
                                CookingViewModelFactory.getSecondaryCavityViewModel()
                            )
                            isBrownoutCycle = true
                            BrownoutUtils.handleBrownoutNavigation(
                                navGraph,
                                navController,
                                activity
                            )
                        } else if (isBootTimeEventDoubleOven) {
                            Logd("$tag: Unhandled Cooking Cycle Operations API for Lower Oven Event:: $event")
                            isBootTimeEventDoubleOven = false
                            timeoutHandler.removeCallbacks(runnableBrownOut)
                            timeoutHandler.postDelayed(
                                runnableBrownOut,
                                timeout.toLong()
                            )
                        }
                    }
            }

            else -> {
                HMILogHelper.Loge("$tag: product variant is null")
            }
        }

    fun instantiateBlackOutHandler(
        activity: FragmentActivity?,
        navController: NavController,
        navGraph: NavGraph
    ) {
        timeoutHandler = Handler(Looper.getMainLooper())
        runnableBlackOut = Runnable {
            timeoutHandler.removeCallbacks(runnableBlackOut)
            restoreSleepState()
            CookingAppUtils.updateDefaultTimeOnBlackOut(
                Date(),
                SettingsViewModel.getSettingsViewModel()
            )
            Logd(
                tag,
                "instantiateBlackOutHandler : ota state is " + OTAVMFactory.getOTAViewModel().otaState.value
            )
            //Moving to show popups after waiting for OTA state
            if (CookingAppUtils.isSabbathMode()) {
                Logd(
                    tag,
                    "Sabbath Mode Blackout recovery go to Sabbath Idle Screen, setBlackOutRecoveryComplete to false "
                )
                this.setBlackOutRecoveryComplete(false)
                navController.graph = navGraph
                return@Runnable
            }
            if (CookingViewModelFactory.getPowerInterruptState() == PowerInterruptState.BLACKOUT && SettingsViewModel.getSettingsViewModel()
                    .isUnboxing.value != true
                && isBlackOutPopupAllowedInOTAState()
            ) {
                this.setBlackOutRecoveryComplete(false)
                if (activity != null) {
                    BlackoutUtils.handleBlackOutRecoveryPopUps(navGraph, navController, activity)
                }
            } else {
                this.setBlackOutRecoveryComplete(true)
                if (SettingsViewModel.getSettingsViewModel().awsConnectionStatus
                        .value != SettingsViewModel.CloudConnectionState.IDLE
                    && SettingsViewModel.getSettingsViewModel().isWifiEnabled
                ) {
                    SettingsViewModel.getSettingsViewModel().setTimeModeAuto()
                } else {
                    //do nothing
                }
                if (isBlackOutPopupAllowedInOTAState()) {
                    navController.graph = navGraph
                }
            }
        }
    }

    private fun isBlackOutPopupAllowedInOTAState(): Boolean {
        return OTAVMFactory.getOTAViewModel().otaState.value != OTAStatus.COMPLETED &&
                OTAVMFactory.getOTAViewModel().otaState.value != OTAStatus.INSTALLING &&
                OTAVMFactory.getOTAViewModel().otaState.value != OTAStatus.DOWNLOADING && !CookingAppUtils.isFetalError()
    }

    /*Method to manage Blackout recovery state*/
    fun setBlackOutRecoveryComplete(blackOutRecoveryComplete: Boolean) {
        isBlackOutRecoveryComplete.value = blackOutRecoveryComplete
    }

    /*
    *
    * @ return isBlackOutRecoveryComplete
    */
    fun isBlackOutRecoveryComplete(): LiveData<Boolean> {
        return isBlackOutRecoveryComplete
    }

    fun handleBlackOutRecovery(timeout: Int) {
        Logd("$tag: handleBlackOutRecovery : timeout is $timeout")
        timeoutHandler.postDelayed(runnableBlackOut, timeout.toLong())
    }

    /*Method to manage is knob click navigation */
    fun setIsNavigatedFromKnobClick(navigatedFromKnobClick: Boolean) {
        isNavigatedFromKnobClick.value = navigatedFromKnobClick
    }

    /* isNavigatedFromKnobClick */
    fun isNavigatedFromKnobClick(): LiveData<Boolean> {
        return isNavigatedFromKnobClick
    }

    /**Method to know tools menu is open
     * @return isToolsMenuVisible */
    fun isToolsMenuVisible(): Boolean {
        return isToolsMenuVisible
    }

    /**Method to set tools menu visible
     * @param toolsMenuVisible update if tools menu been shown */
    @Suppress("unused")
    fun setToolsMenuVisible(toolsMenuVisible: Boolean) {
        isToolsMenuVisible = toolsMenuVisible
    }

    /**
     * Method to know fault popup is open
     *
     * @return isFaultPopUpOpen
     */
    fun isFaultPopUpOpen(): Boolean {
        return isFaultPopUpOpen
    }

    /**
     * Method to set fault open popup
     *
     * @param faultPopUpOpen update if fault is been shown
     */
    fun setFaultPopUpOpen(faultPopUpOpen: Boolean) {
        isFaultPopUpOpen = faultPopUpOpen
    }

    /**
     * Method to know fault popup is open
     *
     * @return isFaultPopUpOpen
     */
    fun isCycleInPausedStateGracePeriod(): Boolean {
        return isCycleInPausedStateGracePeriod
    }

    /**
     * Method to set fault open popup
     *
     * @param isCycleInPausedStateGracePeriod update if fault is been shown
     */
    fun setCycleInPausedStateGracePeriod(isCycleInPausedStateGracePeriod: Boolean) {
        this@SharedViewModel.isCycleInPausedStateGracePeriod = isCycleInPausedStateGracePeriod
    }

    /**
     * Method to check if fault A OR C exist
     *
     * @return true/false
     */
    fun isApplianceInAOrCCategoryFault(): Boolean {
        return isApplianceInAOrCCategoryFault
    }

    fun setApplianceInAOrCCategoryFault(applianceInAOrCCategoryFault: Boolean) {
        isApplianceInAOrCCategoryFault = applianceInAOrCCategoryFault
    }

    fun isCurrentDisplayedFaultInPrimaryCavity(): Boolean {
        return isCurrentDisplayedFaultInPrimaryCavity
    }

    fun setCurrentDisplayedFaultInPrimaryCavity(currentDisplayedFaultInPrimaryCavity: Boolean) {
        isCurrentDisplayedFaultInPrimaryCavity = currentDisplayedFaultInPrimaryCavity
    }

    fun isShowOtherCavityFaultPopUp(): Boolean {
        return showOtherCavityFaultPopUp
    }

    fun setShowOtherCavityFaultPopUp(showOtherCavityFaultPopUp: Boolean) {
        this.showOtherCavityFaultPopUp = showOtherCavityFaultPopUp
    }

    fun setCurrentRecipeBeingProgrammed(currentProgrammedRecipe: String) {
        this.currentRecipeBeingProgrammed = currentProgrammedRecipe
    }

    fun getCurrentRecipeBeingProgrammed(): String {
        return currentRecipeBeingProgrammed
    }

    fun instantiateFaultPopUpForOtherCavity(cavityName: String?) {
        timeoutHandler = Handler(Looper.getMainLooper())
        runnableFaultPopUp = Runnable {
            runnableFaultPopUp?.let { timeoutHandler.removeCallbacks(it) }
            if (!isFaultPopUpOpen()) {
                val faultCode: String
                setShowOtherCavityFaultPopUp(false)
                val primaryCookingViewModel =
                    CookingViewModelFactory.getPrimaryCavityViewModel()
                val secondaryCookingViewModel =
                    CookingAppUtils.getSecondaryCookingViewModel()
                if (Constants.PRIMARY_CAVITY_KEY == cavityName &&
                    secondaryCookingViewModel?.faultId?.value != 0
                ) {
                    faultCode = secondaryCookingViewModel?.faultCode?.value.toString()
                    val faultDetails = FaultDetails.getInstance(faultCode)
                    faultDetails.handleSecondaryFaultNavigation(
                        faultDetails,
                        primaryCookingViewModel,
                        secondaryCookingViewModel,
                        ContextProvider.getFragmentActivity()
                    )
                } else if (Constants.SECONDARY_CAVITY_KEY == cavityName &&
                    primaryCookingViewModel?.faultId?.value != 0
                ) {
                    faultCode = primaryCookingViewModel.faultCode.value.toString()
                    val faultDetails = FaultDetails.getInstance(faultCode)
                    faultDetails.handlePrimaryFaultNavigation(
                        faultDetails,
                        primaryCookingViewModel,
                        secondaryCookingViewModel,
                        ContextProvider.getFragmentActivity()
                    )
                }
            }
        }
    }

    fun handleFaultPopUpForOtherCavity(timeout: Int) {
        runnableFaultPopUp?.let { timeoutHandler.postDelayed(it, timeout.toLong()) }
    }

    /************************ For cancelling Recipe Events***************************/
    //handler for upper oven cavity
    private val cancelUpperCavityHandler = Handler(Looper.getMainLooper())

    //handler for lower oven cavity
    private val cancelLowerCavityHandler = Handler(Looper.getMainLooper())

    /**
     * Global cancel event for status screen,
     * because to keep track of 15 sec resume interval if another fragment is opened ex in dual cavity if lower oven is in cancelling state and user clicked on set upper cavity
     * in this case the 15 sec window should not exceeds
     *
     * @param cavityPosition
     * @param isCancelPressed
     */
    fun cancelRecipeEventHandler(context: Context, cavityPosition: Int, isCancelPressed: Boolean) {
        val handler =
            if (cavityPosition == 2) cancelLowerCavityHandler else cancelUpperCavityHandler
        handler.removeCallbacksAndMessages(null)
        Logd(
            "AbstractStatusFragment",
            "CANCEL: Cancelling Handler cavityPosition $cavityPosition isCancelPressed $isCancelPressed"
        )
        if (isCancelPressed) {
            handler.postDelayed(
                {
                    //resetting cancelled time variable for a particular cavity
                    AbstractStatusFragment.cancelledTime[cavityPosition - 1] = 0
                    //canceling recipe execution if it is in PAUSED
                    val cookingViewModel: CookingViewModel =
                        if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel()
                        else CookingViewModelFactory.getPrimaryCavityViewModel()
                    if (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED
                        || cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED_EXT
                    ) {
                        Logd(
                            "AbstractStatusFragment",
                            "CANCEL: Cancelling RecipeExecutionViewModel as 15 Seconds reached for cavityPosition $cavityPosition"
                        )
                        cookingViewModel.recipeExecutionViewModel.cancel()
                    }
                    //15 sec delay to execute this runnable
                },
                context.resources.getInteger(R.integer.duration_status_mode_text_cancelled_recipe)
                    .toLong()
            )
        }
    }

    /************************ For cancelling Recipe Events***************************/
    fun setUnBoxingTime(time: String) {
        this.unBoxingTime = time
    }

    fun getUnBoxingTime(): String {
        return unBoxingTime
    }

    companion object {
        fun getSharedViewModel(activity: ViewModelStoreOwner): SharedViewModel {
            return ViewModelProvider(activity)[SharedViewModel::class.java]
        }
    }
}