/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package core.jbase

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.presenter.customviews.topsheet.TopSheetBehavior
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.transition.Fade
import androidx.transition.Visibility
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentClockBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel.TimerStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.ota.viewmodel.OTAViewModel
import com.whirlpool.hmi.settings.SettingsRepository.DemoMode.DEMO_MODE_DISABLED
import com.whirlpool.hmi.settings.SettingsRepository.DemoMode.DEMO_MODE_ENABLED
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.settings.SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT
import com.whirlpool.hmi.settings.SettingsViewModel.SabbathMode.SABBATH_COMPLIANT
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AppConstants.KEY_CONFIGURATION_CLOCK_SCREEN
import core.utils.AppConstants.KEY_CONFIGURATION_CONTROL_LOCK
import core.utils.AppConstants.KEY_CONFIGURATION_DEMO_MODE_HOME
import core.utils.AppConstants.KEY_CONFIGURATION_SABBATH_MODE
import core.utils.AppConstants.NAVIGATION_FROM_NOTIFICATION
import core.utils.AppConstants.RESOURCE_TYPE_STRING
import core.utils.CommonAnimationUtils
import core.utils.CommonAnimationUtils.animateView
import core.utils.CommonAnimationUtils.dissolveViews
import core.utils.CommonAnimationUtils.slideUpViews
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.isSabbathMode
import core.utils.CookingAppUtils.Companion.isTechnicianModeEnabled
import core.utils.CookingAppUtils.Companion.manageHMIPanelLights
import core.utils.CookingAppUtils.Companion.openCavitySelectionScreen
import core.utils.DoorEventUtils
import core.utils.DoorEventUtils.Companion.DOOR_STATE
import core.utils.FavoriteDataHolder
import core.utils.HMIExpansionUtils
import core.utils.HMIExpansionUtils.Companion.setBothKnobLightOffDirectly
import core.utils.HMILogHelper
import core.utils.HMILogHelper.Logd
import core.utils.KitchenTimerUtils
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils
import core.utils.NotificationManagerUtils.activateDeactivateTipsAndTricks
import core.utils.NotificationManagerUtils.addNotificationToQueue
import core.utils.NotificationManagerUtils.getActiveNotification
import core.utils.NotificationManagerUtils.isNoUserInteractionTimerActive
import core.utils.NotificationManagerUtils.isNoUserInteractionTimerPaused
import core.utils.NotificationManagerUtils.pauseNoUserInteractionTimer
import core.utils.NotificationManagerUtils.removeNotificationFromQueue
import core.utils.NotificationManagerUtils.resumeNoUserInteractionTimer
import core.utils.NotificationManagerUtils.stopNoUserInteractionTimer
import core.utils.PopUpBuilderUtils
import core.utils.PopUpBuilderUtils.Companion.updateDateAndTimeNotificationInstructionPopUp
import core.utils.SettingsManagerUtils
import core.utils.SettingsManagerUtils.isUnboxing
import core.utils.SharedPreferenceManager.getNoOfUses
import core.utils.SharedPreferenceManager.getNoOfUsesOfSwipeDown
import core.utils.SharedPreferenceManager.setNoOfUses
import core.utils.SharedPreferenceManager.setNoOfUsesOfSwipeDown
import core.utils.SharedViewModel
import core.utils.gone
import core.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

/**
 * File       : core.jbase.AbstractClockFragment.
 * Brief      : Abstract class for clock fragment
 * Author     : PATELJ7/Amar Suresh Dugam
 * Created On : 01-02-2024
 */
@SuppressLint("ClickableViewAccessibility")
abstract class AbstractClockFragment : Fragment(), HMIKnobInteractionListener,
    MeatProbeUtils.MeatProbeListener, HMIExpansionUtils.UserInteractionListener {
    /**
     * To binding Fragment variables
     */
    private var fragmentClockBinding: FragmentClockBinding? = null

    private var knobCounter = -1
    private var noOfUses = 0
    private var noOfUsesOfSwipeDown = 0

    /**
     * To hold the instance of Settings View Model
     */
    protected var settingsViewModel: SettingsViewModel? = null

    private var isKnobRotated = false

    private var handler: Handler = Handler(Looper.getMainLooper())
    private var onUserInteraction: Boolean = false
    private val demoTimeoutViewModel: TimeoutViewModel by viewModels()
    private val technicianTimerFinished = 2
    private val clockFarViewTimerFinished = 3
    private var timerHandler: Handler ?= null
    //this array variable keeps track of door event based on door event
    private val doorStateToAction = arrayOf(DOOR_STATE.INITIAL, DOOR_STATE.INITIAL)
    private var otaViewModel: OTAViewModel? = null

    val productVariantEnum: CookingViewModelFactory.ProductVariantEnum =
        CookingViewModelFactory.getProductVariantEnum()

    private val sabbathTimer = Runnable {
        Logd(tag, "Sabbath: Disable and making topSheet view visible with drag")
        fragmentClockBinding?.homeHeader?.manageTopSheetVisibilityWithDrag(
            isTopSheetVisible = true,
            isDrag = true
        )
        SettingsViewModel.getSettingsViewModel()
            .setSabbathMode(NOT_SABBATH_COMPLIANT)
    }

    /**
     * kitchen timer runnable to update the clock text based on kitchen timer remaining time value
     * useful because if multiple KT are set with close value then overlap the text value
     * @return runnable object
     */
    private fun kitchenTimerRunnable(): Runnable {
        return Runnable {
            HMILogHelper.Logi(tag, "KitchenTimer: runnable active")
            val runningKitchenTimer = KitchenTimerVMFactory.getKitchenTimerWithLeastRemainingTime()
            val ktText = KitchenTimerUtils.convertTimeRemainingToShortString(
                runningKitchenTimer?.remainingTime?.value?.toLong() ?: 0
            )
            Logd(tag, "Clock Text Kitchen Timer text =$ktText")
            fragmentClockBinding?.textViewKitchenTimerRunningText?.text = ktText
            handler.postDelayed(kitchenTimerRunnable(), resources.getInteger(R.integer.ms_1000).toLong())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        fragmentClockBinding = FragmentClockBinding.inflate(inflater)
        fragmentClockBinding?.lifecycleOwner = this
        setHoldAndDisableSabbathMode()
        val sharedViewModel: SharedViewModel =
            this.requireActivity().let {
                ViewModelProvider(it)[SharedViewModel::class.java]
            }
        // Exiting from service diagnostics mode should clear this flag.
        if (sharedViewModel.isApplianceInAOrCCategoryFault()) {
            sharedViewModel.setApplianceInAOrCCategoryFault(false)
        }
//      To handle Door Events for MWO recipes when cycle is running and navigating to another screen
        sharedViewModel.setCycleInPausedStateGracePeriod(false)
        enterTransition = Fade().apply {
            duration = resources.getInteger(R.integer.ms_250).toLong()
            mode = Visibility.MODE_IN
        }
        exitTransition = Fade().apply {
            duration = resources.getInteger(R.integer.ms_300).toLong()
            mode = Visibility.MODE_OUT
        }

        //To ensure selfCleanFlow flag is reset in case of Faults or unhandled use cases.
        CookingAppUtils.setIsSelfCleanFlow(false)

        KnobNavigationUtils.clearState()
        FavoriteDataHolder.isNotificationFlow = false
        return fragmentClockBinding?.root
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    private fun setHoldAndDisableSabbathMode() {
        //Touch 3 second on screen to disable Sabbath mode
        if (SABBATH_COMPLIANT == SettingsViewModel.getSettingsViewModel()?.sabbathMode?.value) {
            fragmentClockBinding?.root?.setOnTouchListener { _, motionEvent ->
                if (SettingsViewModel.getSettingsViewModel().sabbathMode.value == SABBATH_COMPLIANT
                ) {
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {
                            handler?.postDelayed(
                                sabbathTimer,
                                resources.getInteger(R.integer.sabbath_mode_touch_timer).toLong()
                            )
                            HMILogHelper.Logi("Sabbath: ACTION_DOWN")
                        }

                        MotionEvent.ACTION_UP -> {
                            HMILogHelper.Logi("Sabbath: ACTION_UP")
                            handler?.removeCallbacks(sabbathTimer)
                        }

                        else -> {}
                    }
                    return@setOnTouchListener true
                }
                false
            }
        }
    }

    protected open fun manageLightButtonClick() {
        fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.topSheetBehavior?.setState(TopSheetBehavior.STATE_EXPANDED)
        fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.performOvenLightOperation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewModels()
        initTimerHandler()
        manageChildViews()
        setTimeFormat()
        if (!CookingAppUtils.isErrorPresentOnHMIScreen()) setBothKnobLightOffDirectly()
        manageHMIPanelLights(homeLight = false, cancelLight = false, cleanLight = false)
        observeSabbathLiveData()
        noOfUses = getNoOfUses()?.toInt()!!
        noOfUsesOfSwipeDown = getNoOfUsesOfSwipeDown()?.toInt()!!
        observeNotificationLiveData()
        startClockFarViewTimer()
        if (isTechnicianModeEnabled()) {
            Logd("Clock technician mode enabled", "isUnboxing =$isUnboxing")
            fragmentClockBinding?.homeHeader?.getBinding()?.demoIcon?.visibility = View.VISIBLE
            fragmentClockBinding?.homeHeader?.getBinding()?.demoIcon?.text = resources.getString(R.string.text_test_status)
            startTechnicianTimerExitMode()
        } else {
            Logd("product Owner mode enabled", "isUnboxing =$isUnboxing")
            observeDemoModeLiveData()
            updateOTABusyStateTrue()
        }
        AbstractStatusFragment.updateStaticVariables()
        SharedViewModel.getSharedViewModel(this).cancelRecipeEventHandler(requireContext() ,1, false)
        SharedViewModel.getSharedViewModel(this).cancelRecipeEventHandler(requireContext(),2, false)
        SharedViewModel.getSharedViewModel(this.requireActivity())
            .setCurrentRecipeBeingProgrammed(AppConstants.EMPTY_STRING)
        fragmentClockBinding?.homeHeader?.getBinding()?.clockTextView?.visibility = View.GONE
        handleHeaderBarIcons()
        handleNotificationsArrowIcon()
        HMIExpansionUtils.setFragmentUserInteractionListener(this)
        if(!isSabbathMode()) {
            updateNotification()
        }
        // Initialize the ViewModel and timer setup
        if (CookingAppUtils.isDemoModeEnabled()) initDemoLandingTimeout()
        //Handling the button congiguration. Adding the false settings flow
        CookingAppUtils.setSettingsFlow(false)

        if(noOfUses != -1) {
            if (noOfUses == 2) {
                removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_TAP_TO_BEGIN)
                activateDeactivateTipsAndTricks(true)
                CookingAppUtils.handleTipsAndTricks()
                noOfUses = -1
                setNoOfUses((-1).toString())
            }
            else{
                if(getActiveNotification() == null){
                    //Trigger notification: Bring back tap to begin if it has not been displayed 2 times
                    addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_TAP_TO_BEGIN)
                }
            }
        }
        if(noOfUsesOfSwipeDown != -1) {
            if (noOfUsesOfSwipeDown == 2) {
                removeSwipeDownForSettingsNotification()
                noOfUsesOfSwipeDown = -1
                setNoOfUsesOfSwipeDown((-1).toString())
            } else {
                setSwipeDownForSettingsNotification()
            }
        }

        if(isNoUserInteractionTimerPaused()){
            resumeNoUserInteractionTimer()
        }
        otaViewModel = OTAVMFactory.getOTAViewModel()
    }

    private fun setTimeFormat() {
        fragmentClockBinding?.textViewClockDigitalClockTime?.set12HourTimeFormat(
            SettingsManagerUtils.getTimeFormat() == SettingsManagerUtils.TimeFormatSettings.H_12
        )
    }

    private fun setSwipeDownForSettingsNotification(){
        // animation variables
        val translationYStart = -10f  // Start position (above the view)
        val alphaStart = 0f           // Fully transparent
        val alphaEnd = 1f             // Fully visible
        val fadeInDuration = 500L     // Duration for fade-in (in milliseconds)

        if (fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.topSheetBehavior?.getState() == TopSheetBehavior.STATE_COLLAPSED) {
            val currentFragment =
                CookingAppUtils.getVisibleFragment(ContextProvider.getFragmentActivity()?.supportFragmentManager)
            if (currentFragment is AbstractClockFragment) {
                fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.drawerWidgetBinding?.clockHelperText?.apply {
                    visibility = View.VISIBLE
                    translationY = translationYStart // Start above the view's position
                    alpha = alphaStart // Start fully transparent

                    animate()
                        .translationY(0f) // Move back to the original position
                        .alpha(alphaEnd) // Fade in to fully visible
                        .setDuration(fadeInDuration)
                        .setListener(null)
                }
            }
        }
    }

    private fun removeSwipeDownForSettingsNotification(){
        // animation variables
        val translationYStart = -10f  // Start position (above the view)
        val translationYEnd = 10f     // End position (below the view)
        val alphaStart = 0f           // Fully transparent
        val fadeOutDuration = 500L    // Duration for fade-out (in milliseconds)

        if (fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.topSheetBehavior?.getState() == TopSheetBehavior.STATE_COLLAPSED) {
            val currentFragment =
                CookingAppUtils.getVisibleFragment(ContextProvider.getFragmentActivity()?.supportFragmentManager)
            if (currentFragment is AbstractClockFragment) {
                fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.drawerWidgetBinding?.clockHelperText?.apply {
                    translationY = translationYStart // Start above the view's position
                    alpha = alphaStart // Start fully transparent
                    animate()
                        .translationY(translationYEnd) // Move down to simulate fading out to the bottom
                        .alpha(alphaStart) // Fade out to fully transparent
                        .setDuration(fadeOutDuration)
                        .withEndAction {
                            visibility = View.GONE // Hide the view after fading out
                        }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MeatProbeUtils.setMeatProbeListener(this)
        if(!isSabbathMode())
            HMIExpansionUtils.setHMIKnobInteractionListener(this)
        // Restart the timer if it was stopped
        if (CookingAppUtils.isDemoModeEnabled()) demoTimeoutViewModel.restart()
    }

    override fun onPause() {
        super.onPause()
        MeatProbeUtils.removeMeatProbeListener()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }

    /**
     * door interaction observer, if the door is open and the value is not same as when Fragment was created then move to recipe selection for that cavity
     * @param cookingViewModel
     */
    protected open fun observeDoorInteraction(cookingViewModel: CookingViewModel) {
        //always update door action to initial before observing, because upon registering observer it would give the value of current door which we want to ignore
        //as door have to open and close, the initial values are ignored as event triggered
        doorStateToAction[if(cookingViewModel.isPrimaryCavity) 0 else 1] =  DOOR_STATE.INITIAL
        cookingViewModel.doorState.observe(this){ doorState ->
            if (SettingsViewModel.getSettingsViewModel().controlLock.value == true ){
                if (doorState) {
                    NavigationUtils.navigateSafely(
                        this,
                        R.id.action_to_controlUnlockFragment,
                        null,
                        null
                    )
                }
            } else {
                doorStateToAction[if(cookingViewModel.isPrimaryCavity) 0 else 1] = DoorEventUtils.manageDoorInteractionWithRecipeSelection(this, doorState, cookingViewModel, doorStateToAction)
            }
        }
    }

    /**
     * get door state action variable based on cooking View Model
     */
    private fun getDoorState(cookingViewModel: CookingViewModel): DOOR_STATE {
        return doorStateToAction[if(cookingViewModel.isPrimaryCavity) 0 else 1]
    }

    /**
     * update doorStateToAction variable based on cavity
     *
     * @param cookingViewModel for a particular cavity that has triggered door event
     * @param doorState enum that represents opening/close event
     */
    private fun updateDoorState(cookingViewModel: CookingViewModel, doorState: DOOR_STATE) {
        Logd(tag, "DoorState: ${cookingViewModel.cavityName.value} updateDoorState to $doorState")
        doorStateToAction[if(cookingViewModel.isPrimaryCavity) 0 else 1] = doorState
    }


    /**
     * manage door interaction for ClockFragment
     * the opening and closing event has been triggered if in clock screen door was open before then closing door event would not triggered recipe selection
     * @param doorState true if door was opened, false otherwise
     * @param cookingViewModel for a particular cavity
     */
    private fun manageDoorInteraction(doorState: Boolean, cookingViewModel: CookingViewModel){
        //if initial then door observer was registered and ready to receive next events
        if (getDoorState(cookingViewModel) == DOOR_STATE.INITIAL) {
            //if door is already open then we don't want to update its state, as closing the door would again got to recipe selection
            if(doorState) return
            updateDoorState(cookingViewModel, DOOR_STATE.SHOW_OPEN_CLOSE_DOOR_POPUP)
            return
        }

        //if door is open and passed initial check then mark as ready
        if (doorState && getDoorState(cookingViewModel) == DOOR_STATE.SHOW_OPEN_CLOSE_DOOR_POPUP) {
            updateDoorState(cookingViewModel, DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP)
            return
        }

        //If sabbath mode is activated then do not navigate to the recipe selection screen
        if (SettingsViewModel.getSettingsViewModel().sabbathMode.value == SABBATH_COMPLIANT) {
            Logd("Door closed : Sabbath mode is activated , do not navigate to the recipe selection screen")
            return
        }

        if(ScrollDialogPopupBuilder.isAnyPopupShowing()){
            Logd(tag, "Door open /close condition satisfy for ${cookingViewModel.cavityName.value}, but a DialogFragment is Visible so not moving to recipeSelectionFragment")
            return
        }

        //if door is closed and passed initial checks then move on to recipe selection
        if (!doorState && getDoorState(cookingViewModel) == DOOR_STATE.DO_NOT_SHOW_OPEN_CLOSE_DOOR_POPUP) {
            Logd(
                tag,
                "door opened and closed satisfied for ${cookingViewModel.cavityName.value}, moving to recipe selection fragment"
            )
            if (cookingViewModel.isOfTypeOven) SharedViewModel.getSharedViewModel(this.requireActivity())
                .setCurrentRecipeBeingProgrammed(AppConstants.QUICK_START)
            if (cookingViewModel.isPrimaryCavity) {
                NavigationUtils.navigateToUpperRecipeSelection(this)
            } else {
                NavigationUtils.navigateToLowerRecipeSelection(this)
            }
            manageHMIPanelLights(homeLight = true, cancelLight = true, cleanLight = false)
        }
    }

    private fun registerUsageForNotificationDismiss(){
        if(noOfUses != -1 && noOfUses < 2) {
            noOfUses += 1
            setNoOfUses((noOfUses).toString())
            Logd(
                tag,
                "Number of usage: $noOfUses"
            )
        }
    }

    override fun onDestroyView() {
        CookingAppUtils.setOTACompleteStatus(false)
        SettingsViewModel.getSettingsViewModel().controlLock.removeObservers(viewLifecycleOwner)
        HMIExpansionUtils.removeFragmentUserInteractionListener(this)
        PopUpBuilderUtils.dismissPopupByTag(activity?.supportFragmentManager, AppConstants.POPUP_TAG_JET_START)
        fragmentClockBinding = null
        super.onDestroyView()
    }
    /**
     * Method for creating the view model instances.
     */
    protected fun setUpViewModels() {
        settingsViewModel = SettingsViewModel.getSettingsViewModel()
        fragmentClockBinding?.settingViewModel = settingsViewModel
        fragmentClockBinding?.textViewNotification?.setOnClickListener{
            evaluateNotificationRemovalCondition()
        }
    }

    private fun evaluateNotificationRemovalCondition() {
        when (fragmentClockBinding?.notification?.text) {

            // User clicked Get to know your appliance. Navigate to Explore product flow.
            getString(R.string.text_notification_get_to_know_appliance) -> {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_clockFragment_to_unboxingExploreFeaturesInfoFragment,
                    null, null
                )
                stopNoUserInteractionTimer()
                removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_GET_TO_KNOW_YOUR_APPLIANCE)
            }

            // User clicked save last recipe to favorite. Navigate to history list.
            getString(R.string.text_notification_save_to_favorite) ->{
                val notificationsListItems = NotificationManagerUtils.getNotificationCenterListItems()
                val historyId = notificationsListItems?.get(0)?.historyID
                val historyRecord = historyId?.let {
                    CookBookViewModel.getInstance().getHistoryRecordByHistoryId(
                        it
                    )
                }

                if (historyRecord?.cavity == AppConstants.PRIMARY_CAVITY_KEY){
                    CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
                } else {
                    CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
                }
                if (historyRecord != null) {
                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.load(
                        historyRecord
                    )
                }
                if (historyRecord != null) {
                    CookingAppUtils.updateParametersInViewModel(
                        historyRecord,
                        CookingAppUtils.getRecipeOptions(),
                        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                    )
                }
                CookingAppUtils.setNavigatedFrom(AppConstants.NAVIGATION_FROM_CREATE_FAV)
                FavoriteDataHolder.isNotificationFlow = true
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_clockFragment_to_fav_preview_fragment,
                    null, null
                )
                stopNoUserInteractionTimer()
                removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_SAVE_LAST_RECIPE_FAVORITE)
            }

            // User clicked Software updated successfully. Navigate to Software update successful view.
            getString(R.string.text_notification_update_successful, otaViewModel?.otaManager?.currentSystemVersion) -> {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_clockFragment_to_otaCompleteNotificationView,
                    null, null
                )
                stopNoUserInteractionTimer()
                removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_SW_UPDATED_SUCCESSFULLY)
            }

            // User clicked Connect to network. Navigate to BLE connect view.
            getString(R.string.connect_to_network) ->{
                CookingAppUtils.startProvisioning(
                    NavigationUtils.getViewSafely(this),
                    false,
                    isFromConnectivityScreen = true,
                    isAoBProvisioning = false
                )
                stopNoUserInteractionTimer()
                removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW)
            }

            // User clicked Update date and time. Navigate to a popup asking user to Update Manually or using Wifi
            getString(R.string.text_notification_update_date_time) ->{
                CookingAppUtils.setNavigatedFrom(NAVIGATION_FROM_NOTIFICATION)
                updateDateAndTimeNotificationInstructionPopUp(this)
                stopNoUserInteractionTimer()
                removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME)
            }

            // User clicked Software update available. Navigate to Software update successful view
            getString(R.string.text_notification_update_available) ->{
                NavigationUtils.navigateSafely(
                    this,
                    R.id.global_action_to_otaBusyUpdateAvailableViewHolder,
                    null, null
                )
                stopNoUserInteractionTimer()
                removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_SW_UPDATE_AVAILABLE)
            }

            else -> {}
        }
    }
    override fun onStart() {
        super.onStart()
        observeOnKitchenTimerViewModel()
        // Ensure timer is set when fragment becomes visible
        if (CookingAppUtils.isDemoModeEnabled())
            demoTimeoutViewModel.setTimeout(resources.getInteger(R.integer.duration_sec))
    }

    /**
     * Method for setting the view properties and data.
     */
    protected open fun manageChildViews() {
        fragmentClockBinding?.root?.setOnClickListener {
            if (SettingsViewModel.getSettingsViewModel().controlLock.value == true) {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_to_controlUnlockFragment,
                    null,
                    null
                )
            } else {
                manageClockTouchButtonPress()
            }
        }
        fragmentClockBinding?.textViewKitchenTimerRunningText?.setOnClickListener {
            if (SettingsViewModel.getSettingsViewModel().controlLock.value == true) {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_to_controlUnlockFragment,
                    null,
                    null
                )
            } else {
                if(noOfUses != -1){
                    if(isNoUserInteractionTimerActive()){
                        pauseNoUserInteractionTimer()
                    }
                    registerUsageForNotificationDismiss()
                }
                openCavitySelectionScreen(this)
            }
        }

        fragmentClockBinding?.clScrollUpArrow?.setOnClickListener {
            if (SettingsViewModel.getSettingsViewModel().controlLock.value == true) {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_to_controlUnlockFragment,
                    null,
                    null
                )
            } else {
                openNotificationCenterScreen()
            }
        }
    }

    private fun openNotificationCenterScreen(){
        if(NotificationManagerUtils.getNotificationCenterListItems()?.size == 0){
            NavigationUtils.navigateSafely(
                this,
                R.id.action_global_navigate_to_notificationEmpty,
                null, null
            )
        }
        else {
            NavigationUtils.navigateSafely(
                this,
                R.id.action_global_navigate_to_notificationCenter,
                null, null
            )
        }
    }

    protected open fun manageClockTouchButtonPress() {
        if(isNoUserInteractionTimerActive()){
            pauseNoUserInteractionTimer()
        }
        if(noOfUses != -1){
            registerUsageForNotificationDismiss()
        }
        openCavitySelectionScreen(this)
    }

    private fun handleHeaderBarIcons(){
        //control lock icon
        SettingsViewModel.getSettingsViewModel().controlLock.observe(viewLifecycleOwner){
            if (it == true) {
                fragmentClockBinding?.homeHeader?.getBinding()?.ivStatusIcon1?.visibility = View.VISIBLE
                fragmentClockBinding?.homeHeader?.getBinding()?.ivStatusIcon1?.setImageResource(R.drawable.icon_32px_lock)
                CookingAppUtils.stopGattServer()
            } else {
                fragmentClockBinding?.homeHeader?.getBinding()?.ivStatusIcon1?.visibility = View.GONE
                if (!CookingAppUtils.isErrorPresentOnHMIScreen()) CookingAppUtils.startGattServer(this)
            }
        }
        //wifi not connected icon
        if(!SettingsManagerUtils.isApplianceProvisioned() && !CookingAppUtils.isDemoModeEnabled() && !isTechnicianModeEnabled() && !isSabbathMode()){
            fragmentClockBinding?.homeHeader?.getBinding()?.ivStatusIcon2?.visibility = View.VISIBLE
            fragmentClockBinding?.homeHeader?.getBinding()?.ivStatusIcon2?.setImageResource(R.drawable.ic_wifioff)
        }
        else{
            fragmentClockBinding?.homeHeader?.getBinding()?.ivStatusIcon2?.visibility = View.GONE
        }
    }

    private fun handleNotificationsArrowIcon(){
        if(!isSabbathMode()) {
            fragmentClockBinding?.clScrollUpArrow?.visibility = View.VISIBLE
        }
        else{
            fragmentClockBinding?.clScrollUpArrow?.visibility = View.GONE
        }
    }

    private fun observeSabbathLiveData() {
        SettingsViewModel.getSettingsViewModel().sabbathMode.observe(
            viewLifecycleOwner
        ) { sabbathMode: Int? ->
            when (sabbathMode) {
                SABBATH_COMPLIANT -> {
                    CookingAppUtils.stopGattServer()
                    HMIExpansionUtils.disableFeatureHMIKeys(KEY_CONFIGURATION_SABBATH_MODE)
                    HMIExpansionUtils.setBothKnobLightOffSabbath()
                    fragmentClockBinding?.iconSabbathMode?.visibility = View.VISIBLE
                    fragmentClockBinding?.textViewSabbathMode?.visibility = View.VISIBLE
                    fragmentClockBinding?.textViewDescription?.visibility = View.VISIBLE
                    fragmentClockBinding?.textViewClockDigitalClockTime?.visibility = View.GONE
                    fragmentClockBinding?.textViewClockDigitalClockDay?.visibility = View.GONE
                    fragmentClockBinding?.mainLayout?.setBackgroundColor(requireContext().getColor(R.color.colorBlack))
                    fragmentClockBinding?.tvSabbathError?.visibility = provideVisibilityOfSabbathErrorTextView()
                    fragmentClockBinding?.clScrollUpArrow?.visibility = View.GONE
                    fragmentClockBinding?.homeHeader?.getBinding()?.ivStatusIcon2?.visibility = View.GONE
                    val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
                    @Suppress("CanBeVal") var isBlackOutRecoveryComplete = sharedViewModel.isBlackOutRecoveryComplete().value
                    Logd(tag, "Sabbath Idle isBlackOutRecoveryComplete $isBlackOutRecoveryComplete")
                    if ((isBlackOutRecoveryComplete == false)) {
                        fragmentClockBinding?.tvSabbathError?.visibility = View.VISIBLE
                        fragmentClockBinding?.tvSabbathError?.text =
                            getString(R.string.text_title_error_reboot_all_caps)
                        sharedViewModel.setBlackOutRecoveryComplete(true)
                    } else if (CookingAppUtils.isAnyCavityHasFault()) {
                        HMILogHelper.Loge(tag, "Sabbath Fault, any cavity has fault so displaying ERROR")
                        fragmentClockBinding?.tvSabbathError?.visibility = View.VISIBLE
                        fragmentClockBinding?.tvSabbathError?.text =
                            getString(R.string.text_title_error_all_caps)
                        sharedViewModel.setBlackOutRecoveryComplete(true)
                    }
                }

                NOT_SABBATH_COMPLIANT -> {
                    fragmentClockBinding?.iconSabbathMode?.visibility = View.GONE
                    fragmentClockBinding?.textViewSabbathMode?.visibility = View.GONE
                    fragmentClockBinding?.textViewDescription?.visibility = View.GONE
                    fragmentClockBinding?.clScrollUpArrow?.visibility = View.VISIBLE
                    fragmentClockBinding?.textViewClockDigitalClockTime?.visibility = View.VISIBLE
                    fragmentClockBinding?.mainLayout?.background = AppCompatResources.getDrawable(requireContext(), R.drawable.background)
                    fragmentClockBinding?.tvSabbathError?.visibility = View.GONE
                    updateNotification()
                    //If OTA is completed then update the clock view text
                    when {
                        SettingsViewModel.getSettingsViewModel()?.controlLock?.value == true -> {
                            Logd("HMI_KEY", "controlLock true --> Disable and enable buttons")
                            HMIExpansionUtils.disableFeatureHMIKeys(KEY_CONFIGURATION_CONTROL_LOCK)
                            HMIExpansionUtils.enableFeatureHMIKeys(KEY_CONFIGURATION_CONTROL_LOCK)
                        }
                        CookingAppUtils.isDemoModeEnabled() -> {
                            Logd("HMI_KEY", "Clock Demo Mode On--> Disable and enable buttons")
                            HMIExpansionUtils.disableFeatureHMIKeys(KEY_CONFIGURATION_DEMO_MODE_HOME)
                            HMIExpansionUtils.enableFeatureHMIKeys(KEY_CONFIGURATION_DEMO_MODE_HOME)
                        }
                        else -> {
                            Logd("HMI_KEY", "Clock --> Disable and enable buttons")
                            HMIExpansionUtils.disableFeatureHMIKeys(KEY_CONFIGURATION_CLOCK_SCREEN)
                            HMIExpansionUtils.enableFeatureHMIKeys(KEY_CONFIGURATION_CLOCK_SCREEN)
                        }
                    }

                    if (CookingAppUtils.getOTACompleteComplete().value == false) {
                        // Save to favorite has higher priority than Oven cooling.
                        if(getActiveNotification() != NotificationJsonKeys.NOTIFICATION_SAVE_LAST_RECIPE_FAVORITE) {
                            updateCavityCoolingView()
                        }
                    }else {
                        CookingAppUtils.setOTACompleteStatus(false)
                    }
                    HMIExpansionUtils.setHMIKnobInteractionListener(this)
                    if (!CookingAppUtils.isErrorPresentOnHMIScreen()) CookingAppUtils.startGattServer(this)
                }

                else -> {}
            }
        }
    }



    private fun observeNotificationLiveData() {
        CookingAppUtils.getActiveNotificationChanged().observe(
            viewLifecycleOwner
        ) { hasChanged: Boolean? ->
            if (hasChanged == true && lifecycle.currentState == Lifecycle.State.RESUMED) {
                if(!isSabbathMode()) {
                    updateNotification()
                }
                CookingAppUtils.setActiveNotificationChanged(false)
            }
        }
    }


    private fun updateNotification() {
        lifecycleScope.launch(Dispatchers.Main) {
            val animDuration = 300L
            val delayDuration = 800L
            val notificationString = getActiveNotification()?.let {
                CookingAppUtils.getNotificationStringId(it,requireContext())
            }

            if (getActiveNotification() == null) {
                if(noOfUses != -1 && noOfUses < 2) {
                    //Trigger notification: Bring back tap to begin if it has not been displayed 2 times
                    addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_TAP_TO_BEGIN)
                }
                else {
                    // Reset views when there's no notification
                    resetViews()
                }
                return@launch
            }

            // Actionable notification
            if (NotificationManagerUtils.isActiveNotificationActionable()) {
                slideUpViews(
                    listOf(
                        fragmentClockBinding?.iconTipsAndTricks,
                        fragmentClockBinding?.textViewTipsAndTricks,
                        fragmentClockBinding?.textviewOvenCoolingText,
                        fragmentClockBinding?.textViewClockDigitalClockDay
                    ),
                    animDuration
                )
                fragmentClockBinding?.textViewNotification?.let { notificationView ->
                    animateView(notificationView, 200f, 0f, animDuration)
                    notificationView.visibility = View.VISIBLE
                }
                notificationString?.let {
                    fragmentClockBinding?.notification?.text = notificationString
                }
                return@launch
            }

            // Tip notification
            if (NotificationManagerUtils.isActiveNotificationATip()) {
                slideUpViews(
                    listOf(
                        fragmentClockBinding?.textviewOvenCoolingText,
                        fragmentClockBinding?.textViewClockDigitalClockDay,
                        fragmentClockBinding?.textViewNotification
                    ),
                    animDuration
                )
                fragmentClockBinding?.iconTipsAndTricks?.visibility = View.GONE
                fragmentClockBinding?.textViewTipsAndTricks?.apply {
                    visibility = View.VISIBLE
                    text = notificationString
                }
                fragmentClockBinding?.widgetClockSecondaryDate?.let { secondaryDate ->
                    animateView(secondaryDate, 200f, 0f, animDuration)
                    secondaryDate.visibility = View.VISIBLE
                }
                return@launch
            }

            // Default dissolve action for other cases
            dissolveViews(
                listOf(
                    fragmentClockBinding?.textViewNotification,
                    fragmentClockBinding?.iconTipsAndTricks,
                    fragmentClockBinding?.textViewTipsAndTricks,
                    fragmentClockBinding?.textViewClockDigitalClockDay
                ),
                animDuration,
                delayDuration
            ) {
                notificationString?.let {
                    if(isAdded){
                        updateCoolingText(it)
                    }
                }
            }
        }
    }

    private fun resetViews() {
        hideNotificationView()
        hideTipsAndTricksView()
        hideDayAndDate()
        hideOvenCoolingTextView()
        updateDate()
    }

    /**
     * Sabbath Error textView based on cavity
     * @return View.VISIBLE or View.INVISIBLE if SabbathMode is active
     */
    abstract fun provideVisibilityOfSabbathErrorTextView(): Int

    private fun observeDemoModeLiveData() {
        SettingsViewModel.getSettingsViewModel().demoMode.observe(
            viewLifecycleOwner
        ) { demoMode: Int? ->
            when (demoMode) {
                DEMO_MODE_ENABLED -> {
                    Logd("DEMO MODE IS ACTIVE")
                    fragmentClockBinding?.homeHeader?.getBinding()?.demoIcon?.visibility = View.VISIBLE
                    CookingAppUtils.stopGattServer()
                }

                DEMO_MODE_DISABLED -> {
                    Logd("DEMO MODE IS NOT ACTIVE")
                    fragmentClockBinding?.homeHeader?.getBinding()?.demoIcon?.visibility = View.INVISIBLE
                    if (!CookingAppUtils.isErrorPresentOnHMIScreen()) CookingAppUtils.startGattServer(this)
                }

                else -> {
                    Logd("DEMO MODE IS NOT ACTIVE")
                    fragmentClockBinding?.homeHeader?.getBinding()?.demoIcon?.visibility = View.INVISIBLE
                    if (!CookingAppUtils.isErrorPresentOnHMIScreen()) CookingAppUtils.startGattServer(this)
                }
            }
        }
    }
    /**
     * updating Clock view with kitchen time remaining value and replacing clock text
     * if any KitchenTimer is running and if multiple timer is running then show shortest
     */
    private fun observeOnKitchenTimerViewModel() {
        if (KitchenTimerVMFactory.isAnyKitchenTimerRunning()) {
            Logd(tag, "Kitchen Timer is running, replacing clockView to kitchenTimerView")
            val runningKitchenTimer = KitchenTimerVMFactory.getKitchenTimerWithLeastRemainingTime()
            runningKitchenTimer?.timerStatus?.removeObservers(viewLifecycleOwner)
            runningKitchenTimer?.timerStatus?.observe(viewLifecycleOwner) { timerStatus ->
                if(timerStatus == TimerStatus.RUNNING){
                    //if kt is running update the view and do not got forward
                    viewUpdateOnKitchenTimer(true)
                    return@observe
                }
                if(timerStatus == TimerStatus.COMPLETED){
                    //show kitchen timer completed popup
                    PopUpBuilderUtils.kitchenTimerCompletedPopup(this, runningKitchenTimer)
                }
                //to check on any other kitchen timer is running and update the view accordingly
                if (KitchenTimerVMFactory.isAnyKitchenTimerRunning()){
                    observeOnKitchenTimerViewModel()
                }
                else viewUpdateOnKitchenTimer(false)
            }
        }
    }

    /**
     * show/hide clock textView vs Kitchen timer textView
     *
     * @param isRunning true if kitchen timer is running false otherwise
     */
    private fun viewUpdateOnKitchenTimer(isRunning: Boolean) {
        Logd(tag, "Clock Text Kitchen Timer isRunning =$isRunning")
        handler.removeCallbacksAndMessages(null)
        if (isRunning) {
            fragmentClockBinding?.textViewClockDigitalClockDay?.visibility = View.GONE
            fragmentClockBinding?.textViewClockDigitalClockTime?.visibility = View.GONE
            fragmentClockBinding?.textViewKitchenTimerRunningText?.visibility = View.VISIBLE
            fragmentClockBinding?.iconKitchenTimer?.visibility = View.VISIBLE
            handler.post(kitchenTimerRunnable())
        } else {
            //fragmentClockBinding?.textViewClockDigitalClockDay?.visibility = View.VISIBLE
            fragmentClockBinding?.textViewClockDigitalClockTime?.visibility = View.VISIBLE
            fragmentClockBinding?.textViewKitchenTimerRunningText?.visibility = View.INVISIBLE
            fragmentClockBinding?.iconKitchenTimer?.visibility = View.INVISIBLE
            if(!isSabbathMode()) {
                updateNotification()
            }
        }
    }

    private var primaryCavityCoolingFanState = false
    private var secondaryCavityCoolingFanState = false

    private fun updateCavityCoolingView() {
        var conditionValue = AppConstants.OVEN_SAFE_TEMPERATURE_FAHRENHEIT_VALUE // FAHRENHEIT
        if (settingsViewModel?.temperatureUnit?.value == SettingsViewModel.TemperatureUnit.CELSIUS) {
            conditionValue = AppConstants.OVEN_SAFE_TEMPERATURE_CELSIUS_VALUE.toInt()
        }
        CookingViewModelFactory.getPrimaryCavityViewModel().ovenTemperature.observe(
            viewLifecycleOwner
        ) { ovenTemperature ->
            if (ovenTemperature > conditionValue){
                primaryCavityCoolingFanState = true
                if(getActiveNotification() != NotificationJsonKeys.NOTIFICATION_OVEN_COOLING) {
                    //Trigger notification: Oven cooling
                    addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_OVEN_COOLING)
                }
            }else{
                primaryCavityCoolingFanState = false
                if(getActiveNotification() == NotificationJsonKeys.NOTIFICATION_OVEN_COOLING) {
                    removeOvenCoolingNotification()
                }
            }
        }

        if (productVariantEnum == CookingViewModelFactory.ProductVariantEnum.COMBO || productVariantEnum == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
            CookingViewModelFactory.getSecondaryCavityViewModel().ovenTemperature.observe(
                viewLifecycleOwner
            ) { ovenTemperature ->
                if (ovenTemperature > conditionValue){
                    secondaryCavityCoolingFanState = true
                    if(getActiveNotification() != NotificationJsonKeys.NOTIFICATION_OVEN_COOLING) {
                        //Trigger notification: Oven cooling
                        addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_OVEN_COOLING)
                    }
                }else{
                    secondaryCavityCoolingFanState = false
                    if(getActiveNotification() == NotificationJsonKeys.NOTIFICATION_OVEN_COOLING) {
                        removeOvenCoolingNotification()
                    }
                }
            }
        }
    }

    private fun removeOvenCoolingNotification(){
        if(!primaryCavityCoolingFanState && !secondaryCavityCoolingFanState) {
            //Remove Notification: Oven temp is below safe temperature
            removeNotificationFromQueue(NotificationJsonKeys.NOTIFICATION_OVEN_COOLING)
            if(noOfUses != -1 && noOfUses < 2 && getActiveNotification() == null) {
                //Trigger notification: Bring back tap to begin if it has not been displayed 2 times
                addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_TAP_TO_BEGIN)
            }
        }
    }

    private fun updateCoolingText(notificationText: String) {
        val animDuration = 150L
        if(notificationText == getString(R.string.text_notification_oven_cooling)){
            val textView = fragmentClockBinding?.textviewOvenCoolingText ?: return

            val coolingText = when (productVariantEnum) {
                CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                    when {
                        primaryCavityCoolingFanState && secondaryCavityCoolingFanState -> getString(
                            R.string.text_MWO_and_Oven_cooling
                        )

                        primaryCavityCoolingFanState -> getString(
                            R.string.clock_text_oven_cooling,
                            getString(R.string.microwave)
                        )

                        secondaryCavityCoolingFanState -> getString(
                            R.string.text_oven_cooling,
                            getString(R.string.cavity_selection_lower)
                        )

                        else -> null
                    }
                }

                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                    when {
                        primaryCavityCoolingFanState && secondaryCavityCoolingFanState -> getString(
                            R.string.text_upper_and_lower_ovens_cooling,
                            getString(R.string.cavity_selection_upper),
                            getString(R.string.cavity_selection_lower)
                        )

                        primaryCavityCoolingFanState -> getString(
                            R.string.text_oven_cooling,
                            getString(R.string.cavity_selection_upper)
                        )

                        secondaryCavityCoolingFanState -> getString(
                            R.string.text_oven_cooling,
                            getString(R.string.cavity_selection_lower)
                        )

                        else -> null
                    }
                }

                CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                    if (primaryCavityCoolingFanState) getString(
                        R.string.clock_text_oven_cooling,
                        getString(R.string.cavity_selection_oven)
                    ) else null
                }

                CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                    if (primaryCavityCoolingFanState) getString(
                        R.string.clock_text_oven_cooling,
                        getString(R.string.microwave)
                    ) else null
                }

                else -> null
            }

            if (coolingText != null) {
                textView.apply {
                    visibility = View.VISIBLE
                    alpha = 0f
                    text = coolingText
                    animate()
                        .alpha(1f)
                        .setDuration(animDuration)
                }
            } else {
                if (!KitchenTimerVMFactory.isAnyKitchenTimerRunning()) {
                    textView.visibility = View.GONE
                }
            }
        }
        else{
            val textView = fragmentClockBinding?.textviewOvenCoolingText ?: return
            val textToSet = (notificationText)
            textView.apply {
                visibility = View.VISIBLE
                alpha = 0f
                text = textToSet
                animate().alpha(1f).setDuration(animDuration)
            }
        }
    }

    private fun updateDate() {
        fragmentClockBinding?.textViewClockDigitalClockDay?.apply {
            visible()
            alpha = 1f
        }
        val today = LocalDate.now()
        val dateString = getString(
            R.string.text_clock_day_date, resources.getString(
                CookingAppUtils.getResIdFromResName(
                    requireContext(), today.dayOfWeek.toString().lowercase(
                        Locale.getDefault()
                    ), RESOURCE_TYPE_STRING
                )
            ), resources.getString(
                CookingAppUtils.getResIdFromResName(
                    requireContext(), today.month.toString().lowercase(
                        Locale.getDefault()
                    ), RESOURCE_TYPE_STRING
                )
            ), today.dayOfMonth
        )
        fragmentClockBinding?.textViewClockDigitalClockDay?.text = dateString
    }

    private fun hideNotificationView() {
        fragmentClockBinding?.textViewNotification?.apply {
            alpha = 1f
            gone()
        }
    }
    private fun hideTipsAndTricksView() {
        fragmentClockBinding?.iconTipsAndTricks?.apply {
            alpha = 1f
            gone()
        }
        fragmentClockBinding?.textViewTipsAndTricks?.apply {
            alpha = 1f
            gone()
        }
    }

    private fun hideOvenCoolingTextView(){
        fragmentClockBinding?.textviewOvenCoolingText?.apply {
            alpha = 1f
            gone()
        }
    }

    private fun hideDayAndDate(){
        fragmentClockBinding?.textViewClockDigitalClockDay?.apply {
            alpha = 1f
            gone()
        }

    }

    private fun startTechnicianTimerExitMode() {
        timerHandler?.removeMessages(technicianTimerFinished)
        Logd("Technician mode", "Technician exit 30 sec timer started")
        timerHandler?.sendEmptyMessageDelayed(
            technicianTimerFinished,
            AppConstants.CLOCK_SCREEN_TECHNICIAN_EXIT_TIME_OUT.toLong()
        )

    }

    private fun startClockFarViewTimer(){
        timerHandler?.removeMessages(clockFarViewTimerFinished)
        Logd("Clock_Far_View", "1 min timer for Clock Far View started")
        timerHandler?.sendEmptyMessageDelayed(
            clockFarViewTimerFinished,
            AppConstants.CLOCK_FAR_VIEW_TIME_OUT.toLong()
        )
    }

    override fun onStop() {
        isKnobRotated = false
        super.onStop()
        handler.removeCallbacksAndMessages(null)
        if (CookingAppUtils.isDemoModeEnabled()) demoTimeoutViewModel.stop()
        if (timerHandler != null) {
            timerHandler?.removeMessages(technicianTimerFinished)
            timerHandler?.removeCallbacksAndMessages(null)
            timerHandler = null
        }
    }

    protected open fun manageKnobRotation(knobId: Int, knobDirection: String) {
        if ((knobId == AppConstants.LEFT_KNOB_ID) &&
            (fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.drawerWidgetBinding?.tvLightState?.isVisible == false)) {
            if (fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.topSheetBehavior?.getState() != (TopSheetBehavior.STATE_EXPANDED)) {
                knobCounter = -1
                fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.topSheetBehavior?.setState(TopSheetBehavior.STATE_EXPANDED)
            }
            val itemsSize = fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.drawerWidgetBinding?.recyclerViewGridList?.size
            when (knobDirection) {
                KnobDirection.CLOCK_WISE_DIRECTION -> {
                    itemsSize?.let { size ->
                        knobCounter = if (knobCounter < size - 1) knobCounter + 1 else itemsSize - 1
                    }
                }

                KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                    itemsSize?.let { _ ->
                        knobCounter = if (knobCounter <= 0) 0 else knobCounter - 1
                    }
                }
            }
            fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.manageKnobRotation(counter = knobCounter)
        }
    }

    protected open fun manageLeftKnobClick() {
        if (fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.topSheetBehavior?.getState() != (TopSheetBehavior.STATE_EXPANDED)
            || (fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.drawerWidgetBinding?.tvLightState?.isVisible == true)
        ) {
            knobCounter = -1
            manageLightButtonClick()
        } else {
            fragmentClockBinding?.homeHeader?.getBinding()?.topSheetView?.manageLeftKnobClick(counter = knobCounter)
        }
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(isSabbathMode()){
            HMILogHelper.Loge(tag, "Sabbath mode is enabled, ignoring probe Insertion for cavity ${cookingViewModel?.cavityName?.value}")
            return
        }
        if (SettingsViewModel.getSettingsViewModel().controlLock.value == true ){

            NavigationUtils.navigateSafely(
                this,
                R.id.action_to_controlUnlockFragment,
                null,
                null
            )
        }
        else if (cookingViewModel != null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            NavigationUtils.navigateSafely(
                this@AbstractClockFragment,
                R.id.action_to_probeCyclesSelectionFragment,
                null,
                null
            )
            MeatProbeUtils.removeMeatProbeListener()
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
    }

    override fun onHMILeftKnobClick() {
        Logd(
            "Knob Event",
            "Knob id = $id knobEvent = LEFT CLICK"
        )
        manageLeftKnobClick()
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMIRightKnobClick() {
        KnobNavigationUtils.knobForwardTrace = true
        if(isNoUserInteractionTimerActive()){
            pauseNoUserInteractionTimer()
        }
        openCavitySelectionScreen(this)
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID && !isKnobRotated) {
            isKnobRotated = true
            KnobNavigationUtils.knobForwardTrace = true
            if(isNoUserInteractionTimerActive()){
                pauseNoUserInteractionTimer()
            }
            openCavitySelectionScreen(this)
        } else if (knobId == AppConstants.LEFT_KNOB_ID) {
            (Logd(
        "Knob Event",
        "Knob id = $knobId knobDirection = $knobDirection"
    ))
            manageKnobRotation(knobId, knobDirection)
        }
    }

    private fun initDemoLandingTimeout() {
        demoTimeoutViewModel.timeoutCallback?.observe(
            viewLifecycleOwner
        ) { timeoutStatesEnum: TimeoutViewModel.TimeoutStatesEnum ->
            Logd("TimeoutCallback: " + timeoutStatesEnum.name)
            if (timeoutStatesEnum == TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.demoModeLandingFragment,
                    null,
                    null
                )
            }
        }
        demoTimeoutViewModel.setTimeout(
            resources.getInteger(R.integer.duration_sec)
        )
    }

    override fun onKnobSelectionTimeout(knobId: Int) {

    }

    override fun onUserInteraction() {
        onUserInteraction = true
        Logd("Abstract User Interaction")
        startClockFarViewTimer()
        if (CookingAppUtils.isDemoModeEnabled()) demoTimeoutViewModel.restart()
        if (isTechnicianModeEnabled()){
            Logd("Technician mode:", "Technician mode: Restart Technician mode exit timer")
            startTechnicianTimerExitMode()
        }
    }

    /**
     * Init handler
     * msg.what - for execute the task as per providing task id
     */
    private fun initTimerHandler() {
        timerHandler = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    technicianTimerFinished -> {
                        Logd("Technician mode:", "30 sec timer finished , " +
                                "navigate back to the technician exit mode")
                        timerHandler?.removeMessages(technicianTimerFinished)
                        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)
                            ?.let {
                                NavigationUtils.navigateSafely(
                                    it,
                                    R.id.action_clockFragment_to_unboxingTechnicianExitModeFragment,
                                    null,
                                    null
                                )
                            }
                    }
                    clockFarViewTimerFinished ->{
                        Logd("Clock_Far_View", "60 sec timer for Far View finished")
                        timerHandler?.removeMessages(clockFarViewTimerFinished)
                        //If sabbath mode is activated then do not navigate to the recipe selection screen
                        if (SettingsViewModel.getSettingsViewModel().sabbathMode.value == SABBATH_COMPLIANT) {
                            Logd("Clock_Far_View", "Sabbath mode is activated , do not navigate to the recipe selection screen")
                            startClockFarViewTimer()
                            return
                        }
                        if (CookingAppUtils.isAnyPopupShowing()) {
                            HMILogHelper.Loge("Clock_Far_View","Popup is still showing, not moving to Clock farStatusScreen")
                            startClockFarViewTimer()
                            return
                        }
                        var navigationId =  R.id.action_ClockNearViewFragment_to_ClockFarViewFragment
                        if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN){
                            Logd("Clock_Far_View","Product variant is MICROWAVEOVEN")
                            navigationId = R.id.action_ClockFarViewFragment_to_ClockFarViewFragmentMWO
                        }
                        NavigationUtils.getVisibleFragment()
                            ?.let {
                                NavigationUtils.navigateSafely(
                                    it,
                                    navigationId,
                                    null,
                                    null
                                )
                            }
                    }
                    else -> {
                        // Handle unknown message types (optional)
                    }
                }
            }
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        if (timeInterval == AppConstants.TIME_INTERVAL_JET_START) {
            PopUpBuilderUtils.jetStartMWOBakeRecipe(
                this
            )
        }
    }

    private fun updateOTABusyStateTrue() {
        //In case of OTA is Running do not set appliance is busy
        if (!OTAVMFactory.getOTAViewModel().isOTARunning) {
            Logd("Clock Far OTA","OTA : on user interaction and OTA is not running, setting appliance -> busy")
            OTAVMFactory.getOTAViewModel().setApplianceBusyState(true)
        }
    }
}