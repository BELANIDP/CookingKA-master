package android.presenter.basefragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.basefragments.abstract_view_helper.AbstractStatusViewHelper
import android.presenter.basefragments.abstract_view_helper.AbstractStatusWidgetHelper
import android.presenter.customviews.topsheet.TopSheetBehavior
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.presenter.dialogs.MoreOptionsPopupBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.size
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.model.capability.recipe.step.Notification
import com.whirlpool.hmi.cooking.model.capability.recipe.step.UserInstruction
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.utils.RecipeProgressBasis
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModel
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel
import com.whirlpool.hmi.settings.SettingsRepository
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.utils.timers.Timer
import core.utils.AppConstants
import core.utils.AppConstants.EMPTY_SPACE
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CommonAnimationUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getRecipeModeWithTemperatureAsString
import core.utils.CookingAppUtils.Companion.getRecipeModeWithTemperatureAsStringForHotTemperature
import core.utils.CookingAppUtils.Companion.getRecipeNameText
import core.utils.CookingAppUtils.Companion.isCavityFaultNone
import core.utils.CookingAppUtils.Companion.isCavityHot
import core.utils.CookingAppUtils.Companion.manageHMIPanelLights
import core.utils.CookingAppUtils.Companion.navigateToStatusOrClockScreen
import core.utils.CookingAppUtils.Companion.setKnobLightWhenCycleRunning
import core.utils.DoorEventUtils
import core.utils.DoorEventUtils.Companion.dismissLowerCavityDoorPopup
import core.utils.DoorEventUtils.Companion.dismissUpperCavityDoorPopup
import core.utils.FavoriteDataHolder
import core.utils.HMIExpansionUtils
import core.utils.HMIExpansionUtils.Companion.isSlowBlinkingKnobTimeoutActive
import core.utils.HMIExpansionUtils.Companion.setBothKnobLightOffDirectly
import core.utils.HMIExpansionUtils.Companion.setBothKnobLightOffSabbath
import core.utils.HMIExpansionUtils.Companion.userInteractWithinSlowBlinkingTimeoutElapsed
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.NavigationUtils.Companion.navigateToDelayScreen
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedPreferenceManager
import core.utils.SharedViewModel
import core.utils.TimeUtils
import core.utils.gone
import core.utils.transition.CustomSlideBottom
import core.utils.transition.CustomSlideTop
import core.utils.transition.TransitionListener
import core.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit


/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.basefragments.AbstractStatusFragment
 * Brief       : Abstract fragment to extend for the use case implementations
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : Use this class as Base to extend the functionality of all variants e single, double mwo,etc
 */
abstract class AbstractStatusFragment : Fragment(),
    CookingStatusWidget.OnStatusWidgetClickListener,
    HMIExpansionUtils.HMICancelButtonInteractionListener,
    HMIKnobInteractionListener,
    HMIExpansionUtils.UserInteractionListener,
    MeatProbeUtils.MeatProbeListener,
    View.OnClickListener {

    private var leftKnobCounter = -1
    protected abstract fun provideViewHolderHelper(): AbstractStatusViewHelper
    protected var sharedViewModel: SharedViewModel? = null
    private lateinit var timeoutViewModel: TimeoutViewModel
    private var firstTimeKeepDoorClosed = true
    private var isFromKnob: Boolean = false

    protected var isUpperSteamCleanRunning = false
    protected var isLowerSteamCleanRunning = false

    private var shouldApplySharedElementTransition = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        HMILogHelper.Logd(" AbstractStatusFragment -  onCreateView")
        provideViewHolderHelper().onCreateView(inflater, container, savedInstanceState)
        provideViewHolderHelper().getLayoutViewBinding()?.lifecycleOwner = this
        provideViewHolderHelper().setupBindingData(this)
        provideViewHolderHelper().getLayoutViewBinding()?.root?.setOnClickListener(this)
        updateSteamCleanRunningCavity()
        return provideViewHolderHelper().getLayoutViewBinding()?.root
    }

    /**
     * GET status widget helper based on cavity view model
     *
     * @param cookingVM cooking view model of a given cavity
     * @return AbstractStatusWidgetHelper to access CookingStatusWidget component
     */
    fun getStatusWidgetHelper(cookingVM: CookingViewModel): AbstractStatusWidgetHelper?{
        return if(cookingVM.isPrimaryCavity) provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper
        else provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper
    }

    /**
     * Method to call when user interacts with screen and restart timeout
     */
    override fun onUserInteraction() {
        HMILogHelper.Logd("onUserInteraction")
        timeoutViewModel.restart()
    }

    override fun onStart() {
        super.onStart()
        initTimeout()
        observeOnKitchenTimerViewModel()
        observeMarkFavoriteStatus()
    }

    private fun observeMarkFavoriteStatus() {
        FavoriteDataHolder.markFavorite.observe(viewLifecycleOwner) { markFav ->
            markFav?.let {
                val favoriteRecipeAdded = StringBuilder() // Initialize as StringBuilder
                CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.apply {
                    var favoriteName = getRecipeNameText(
                            requireContext(),
                            recipeName.value.toString()
                        )
                    if (it) {
                        favoriteName = CookingAppUtils.updateRecordName(favoriteName)
                        // Append the string to the StringBuilder
                        favoriteRecipeAdded.append(
                            resources.getString(R.string.text_favorite_added_during_running, favoriteName)
                        )
                    } else {
                        // Append the string to the StringBuilder
                        favoriteRecipeAdded.append(
                            resources.getString(R.string.text_already_added_to_fav, favoriteName)
                        )
                    }
                }
                // Convert StringBuilder to String before passing it
                postMarkFavoriteStatus(favoriteRecipeAdded)
            }
        }
    }

    /**
     * override if fragment doesn't require timeout functionality, default is true
     * @return true if Fragment needs timeout functionality, false otherwise
     */
    protected open fun isTimeoutApplicable(): Boolean {
        return true
    }


    override fun onResume() {
        super.onResume()
        //Enabled and Disable HMI key after recipe selected
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_RUNNING)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_RUNNING)
        MeatProbeUtils.setMeatProbeListener(this)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
    }

    override fun onPause() {
        super.onPause()
        MeatProbeUtils.removeMeatProbeListener()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }

    /**
     * Method to call to initiate screen timeout
     */
    private fun initTimeout() {
        if (isTimeoutApplicable()) {
            timeoutViewModel.timeoutCallback?.observe(
                viewLifecycleOwner
            ) { timeoutStatesEnum: TimeoutViewModel.TimeoutStatesEnum ->
                HMILogHelper.Logd("TimeoutCallback: " + timeoutStatesEnum.name)
                if (timeoutStatesEnum == TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                    if (provideViewHolderHelper().getLowerViewModel() != null
                        && provideViewHolderHelper().getLowerViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == true
                        && provideViewHolderHelper().getLowerViewModel()?.recipeExecutionViewModel?.targetMeatProbeTemperatureReached?.value == false
                        && !MeatProbeUtils.isMeatProbeConnected(
                            provideViewHolderHelper().getLowerViewModel()
                        )
                    ) {
                        timeoutViewModel.restart()
                        return@observe
                    }
                    if (provideViewHolderHelper().getUpperViewModel() != null
                        && provideViewHolderHelper().getUpperViewModel()?.isOfTypeOven == true
                        && provideViewHolderHelper().getUpperViewModel()?.recipeExecutionViewModel?.isProbeBasedRecipe == true
                        && provideViewHolderHelper().getLowerViewModel()?.recipeExecutionViewModel?.targetMeatProbeTemperatureReached?.value == false
                        && !MeatProbeUtils.isMeatProbeConnected(
                            provideViewHolderHelper().getUpperViewModel()
                        )
                    ) {
                        timeoutViewModel.restart()
                        return@observe
                    }
                    if (PopUpBuilderUtils.isPopupShowing()) {
                        HMILogHelper.Loge(
                            tag,
                            "Popup is still showing, not moving to farStatusScreen"
                        )
                        timeoutViewModel.restart()
                        return@observe
                    }
                    if (PopUpBuilderUtils.isSteamCleanPopupShowing()) {
                        HMILogHelper.Loge(
                            tag,
                            "steam clean Popup is still showing, not moving to farStatusScreen"
                        )
                        timeoutViewModel.restart()
                        return@observe
                    }
                    if (MoreOptionsPopupBuilder.isAnyPopupShowing()) {
                        HMILogHelper.Loge(
                            tag,
                            "More option Popup is still showing, not moving to farStatusScreen"
                        )
                        timeoutViewModel.restart()
                        return@observe
                    }
                    if (isRecipeCompleteBeforeKitchenTimer()) {
                        HMILogHelper.Logd(
                            tag,
                            "kitchenTimer running, recipe is completed moving to clock screen, cancelling recipeExecution for both cavity"
                        )
                        provideViewHolderHelper().getUpperViewModel()?.recipeExecutionViewModel?.cancel()
                        provideViewHolderHelper().getLowerViewModel()?.recipeExecutionViewModel?.cancel()
                        return@observe
                    } else {
                        navigateToFarViewWithTransitionAnimation()
                    }
                }
            }
            timeoutViewModel.setTimeout(
                resources.getInteger(R.integer.timeout_near_to_far_view_status_running_in_sec)
            )
        }
    }

    /**
     * Method to manage LED and Knob lights when Sabbath cycle is Running
     */
    protected fun manageSabbathLights() {
        setBothKnobLightOffSabbath()
        manageHMIPanelLights(homeLight = false, cancelLight = true, cleanLight = false)
    }

    /**
     * Method to call to initiate screen timeout when Sabbath recipe is running, useful when Timeout needs to be run if Meat Probe is connected Midway
     */
    protected fun sabbathProbeConnectTimeout() {
        timeoutViewModel.timeoutCallback?.observe(
            viewLifecycleOwner
        ) { timeoutStatesEnum: TimeoutViewModel.TimeoutStatesEnum ->
            HMILogHelper.Logd("TimeoutCallback: " + timeoutStatesEnum.name)
            if (timeoutStatesEnum == TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_ELAPSED) {
                if (MeatProbeUtils.isMeatProbeConnected(
                        provideViewHolderHelper().getLowerViewModel()
                    )
                ) {
                    HMILogHelper.Logd(
                        tag,
                        "SABBATH lowerCavity has meat probe connection timeout, so cancelling recipeExecution"
                    )
                    provideViewHolderHelper().getLowerViewModel()?.recipeExecutionViewModel?.cancel()
                    return@observe
                }
                if (MeatProbeUtils.isMeatProbeConnected(
                        provideViewHolderHelper().getUpperViewModel()
                    )
                ) {
                    HMILogHelper.Logd(
                        tag,
                        "SABBATH upperCavity has meat probe connection timeout, so cancelling recipeExecution"
                    )
                    provideViewHolderHelper().getUpperViewModel()?.recipeExecutionViewModel?.cancel()
                    return@observe
                }
            }
        }
        timeoutViewModel.setTimeout(
            resources.getInteger(R.integer.duration_status_mode_start_cook_timer_oven_ready_10_min)
        )
    }

    /**
     * Stop the timeout if meat probe removed and do nothing for Sabbath Recipe
     *
     */
    protected fun stopSabbathProbeConnectTimeout() {
        HMILogHelper.Loge(tag, "Stopping Probe Connect Timeout")
        timeoutViewModel.stop()
    }

    /**
     * provide navigation d of far view, each child far view fragment has to provide
     *
     * @return navigation id of far view
     */
    abstract fun provideFarViewNavigationId(): Int

    /**
     * This function will update th steam widget layout
     *
     * @return nothing
     */
    abstract fun updateSteamCleanWidget()

    /**
     * Common cancel button event for all cavity
     * update UI with cancelled runnable
     * This method will post the cook timer complete message on the cavity handler and also the timeout to cancel
     * and move to clock or status screen for that particular recipe
     *
     */
        override fun onHMICancelButtonInteraction() {
        val primaryCookingVM = provideViewHolderHelper().getUpperViewModel()
        var recipeErrorResponse: RecipeErrorResponse? = null
        activity?.supportFragmentManager?.let { CookingAppUtils.dismissAllDialogs(it) }
        if (primaryCookingVM != null) {
            //MAF-2036: Cycle is not going into Resume cooking state from preheat state if cancel button is pressed
            if ((primaryCookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING ||
                        primaryCookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING_EXT) &&
                (primaryCookingVM.recipeExecutionViewModel.cookTimerState.value != Timer.State.COMPLETED)
            ) {
                HMILogHelper.Logd(
                    TAG,
                    "primaryCavity recipeExecutionState is running, trying to pause recipeExecutionViewModel"
                )
                recipeErrorResponse = primaryCookingVM.recipeExecutionViewModel.pauseForCancel()
                HMILogHelper.Logd(
                    TAG,
                    " name=${recipeErrorResponse.name} description= ${recipeErrorResponse.description}"
                )
                recipeErrorResponse = primaryCookingVM.recipeExecutionViewModel.pauseCookTimer()
                HMILogHelper.Logd(
                    TAG,
                    " name=${recipeErrorResponse.name} description= ${recipeErrorResponse.description}"
                )
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.attention,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                if (provideViewHolderHelper().getDefaultCookingStatusWidget() != null) {
                    SharedViewModel.getSharedViewModel(this)
                        .cancelRecipeEventHandler(requireContext(), 1, true)
                    cancelledTime[0] = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()).toInt()
                    updateUIOnCancelEvent(
                        primaryCookingVM, provideViewHolderHelper().getDefaultCookingStatusWidget()
                    )
                }
            } else {
                HMILogHelper.Logd(
                    tag,
                    "primaryCavity recipeExecutionState is ${primaryCookingVM.recipeExecutionViewModel.recipeExecutionState.value} not running, cancelling recipeExecutionViewModel"
                )
                primaryCookingVM.recipeExecutionViewModel.cancel()
            }
        }
        val secondaryCookingVM = provideViewHolderHelper().getLowerViewModel()
        if (secondaryCookingVM != null) {
            //MAF-2036: Cycle is not going into Resume cooking state from preheat state if cancel button is pressed
            if ((secondaryCookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING ||
                        secondaryCookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING_EXT) &&
                (secondaryCookingVM.recipeExecutionViewModel.cookTimerState.value != Timer.State.COMPLETED)
            ) {
                HMILogHelper.Logd(
                    tag,
                    "secondaryCavity recipeExecutionState is running, trying to pause recipeExecutionViewModel"
                )
                recipeErrorResponse = secondaryCookingVM.recipeExecutionViewModel.pauseForCancel()
                HMILogHelper.Logd(
                    TAG,
                    " name=${recipeErrorResponse.name} description= ${recipeErrorResponse.description}"
                )
                recipeErrorResponse = secondaryCookingVM.recipeExecutionViewModel.pauseCookTimer()
                HMILogHelper.Logd(
                    TAG,
                    " name=${recipeErrorResponse.name} description= ${recipeErrorResponse.description}"
                )
                if (provideViewHolderHelper().getLowerCookingStatusWidget() != null) {
                    SharedViewModel.getSharedViewModel(this)
                        .cancelRecipeEventHandler(requireContext(), 2, true)
                    cancelledTime[1] = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()).toInt()
                    updateUIOnCancelEvent(
                        secondaryCookingVM, provideViewHolderHelper().getLowerCookingStatusWidget()
                    )
                }
            } else {
                HMILogHelper.Logd(
                    tag,
                    "secondaryCavity recipeExecutionState is ${secondaryCookingVM.recipeExecutionViewModel.recipeExecutionState.value} not running, cancelling recipeExecutionViewModel"
                )
                secondaryCookingVM.recipeExecutionViewModel.cancel()
            }
        }
        if (recipeErrorResponse?.isError == true) PopUpBuilderUtils.runningFailPopupBuilder(this)
    }

    /**
     * Common cancel button event for all cavity
     * Calls for Sabbath Related Fragments only, no need to update UI as Sabbath Mode is enabled
     */
    protected fun sabbathOnHMICancelButtonInteraction() {
        val primaryCookingVM = provideViewHolderHelper().getUpperViewModel()
        if (primaryCookingVM != null) {
            HMILogHelper.Logd(
                tag,
                "primaryCavity, cancelling recipeExecutionViewModel"
            )
            primaryCookingVM.recipeExecutionViewModel.cancel()
        }
        val secondaryCookingVM = provideViewHolderHelper().getLowerViewModel()
        if (secondaryCookingVM != null) {
            HMILogHelper.Logd(
                tag,
                "secondaryCavity, cancelling recipeExecutionViewModel"
            )
            secondaryCookingVM.recipeExecutionViewModel.cancel()
        }
    }


    /**
     * Updating UI and executing runnable to switch the text
     *
     * @param cookingVM cooking view model
     * @param statusWidget to access status widget components
     */
    private fun updateUIOnCancelEvent(
        cookingVM: CookingViewModel,
        statusWidget: CookingStatusWidget?
    ) {
        HMILogHelper.Loge(
            TAG,
            " executing Cancelling runnable ${cookingVM.cavityName.value} due to HMI Cancel event"
        )
        sharedViewModel?.setCycleInPausedStateGracePeriod(true)
        statusWidget?.statusWidgetHelper?.tvCookTimeRemaining()?.background = null
        statusWidget?.statusWidgetHelper?.tvSetCookTime()?.background = null
        statusWidget?.statusWidgetHelper?.getCavityMoreMenu()?.background = null
        statusWidget?.statusWidgetHelper?.tvRecipeWithTemperature()?.background = null
        statusWidget?.statusWidgetHelper?.tvSetCookTime()?.let { CommonAnimationUtils.animateToResumeCookingView(statusWidget,this) }
        makeIndefiniteProgressBarVisible(
            statusWidget?.statusWidgetHelper?.getStatusProgressBar(),
            statusWidget?.statusWidgetHelper?.getProgressbarInfinite(),
            true
        )
        statusWidget?.statusWidgetHelper?.tvRecipeWithTemperature()?.text = getString(
            R.string.text_cancelled_cycle_running,
            getRecipeNameText(
                requireContext(),
                cookingVM.recipeExecutionViewModel.recipeName.value.toString()
            )
        )

        val handler = getCavityHandler(if (cookingVM.isPrimaryCavity) 1 else 2)
        handler.removeCallbacksAndMessages(null)
        if (isRunningSteam(cookingVM)) {
            statusWidget?.statusWidgetHelper?.tvResumeCooking()?.visibility = View.GONE
            handler.postDelayed({
                navigateToStatusOrClockScreen(this)
            }, resources.getInteger(R.integer.ms_15000).toLong())
        } else {
            statusWidget?.statusWidgetHelper?.tvResumeCooking()?.text =
                getString(R.string.text_resume_cooking_status_button)
            handler.post(
                runnableResumeCancelledCycle(
                    statusWidget?.statusWidgetHelper?.tvRecipeWithTemperature(),
                    if (cookingVM.isPrimaryCavity) 1 else 2
                )
            )
            knobRotationItems = initKnobItems()
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        farViewTransitionFragmentResultListener()
        animateSharedElementView()
        provideViewHolderHelper().getDefaultCookingStatusWidget()
            ?.setStatusWidgetClickListener(this)
        provideViewHolderHelper().getLowerCookingStatusWidget()?.setStatusWidgetClickListener(this)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        if (CookingAppUtils.isTechnicianModeEnabled()) {
            provideViewHolderHelper().provideHeaderBarWidget()
                ?.getBinding()?.demoIcon?.visibility = View.VISIBLE
            provideViewHolderHelper().provideHeaderBarWidget()
                ?.getBinding()?.demoIcon?.text = resources.getString(R.string.text_test_status)
        } else {
            observeDemoModeLiveData()
        }
        observeHistoryLiveData()
        updateSteamCleanWidget()
        setChildViewData()
        navigateUpperRecipeSelectionListener()
        navigateLowerRecipeSelectionListener()
        HMIExpansionUtils.setHMICancelButtonInteractionListener(this)
        HMIExpansionUtils.setFragmentUserInteractionListener(this)
        timeoutViewModel = ViewModelProvider(this)[TimeoutViewModel::class.java]
        farViewCancelFragmentResultListener()
        SettingsViewModel.getSettingsViewModel().controlLock.observe(viewLifecycleOwner) {
            if (it == true) {
                provideViewHolderHelper().provideHeaderBarWidget()
                    ?.getBinding()?.ivStatusIcon1?.visibility = View.VISIBLE
                provideViewHolderHelper().provideHeaderBarWidget()
                    ?.getBinding()?.ivStatusIcon1?.setImageResource(R.drawable.icon_32px_lock)
            } else {
                provideViewHolderHelper().provideHeaderBarWidget()
                    ?.getBinding()?.ivStatusIcon1?.visibility = View.GONE
            }
        }
        //Handling the button congiguration. Set false settings flow and configuration Running/Complete etc state flow
        CookingAppUtils.setSettingsFlow(false)
        if(KnobNavigationUtils.knobForwardTrace){
            isFromKnob = true
            KnobNavigationUtils.knobForwardTrace = false
        }
        knobRotationItems = initKnobItems()
        CookingAppUtils.stopGattServer()
    }

    /**
     * cancel event listener when click event detected from Far view and perform operation on Near view
     */
    private fun farViewCancelFragmentResultListener() {
        // listener for far view fragment cancel key event
        requireActivity().let {
            it.supportFragmentManager.setFragmentResultListener(
                HmiExpansionViewModel.FUNCTION_CANCEL,
                this
            ) { _, bundle ->
                val result = bundle.getBoolean(HmiExpansionViewModel.FUNCTION_CANCEL)
                if (result) {
                    HMILogHelper.Logd(
                        tag,
                        "setFragmentResultListener for cancel button from Far status View"
                    )
                    onHMICancelButtonInteraction()
                }
            }
        }
    }

    private fun setChildViewData() {
        if (provideViewHolderHelper().getDefaultCookingStatusWidget() != null) provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.tvSetCookTime()?.text =
            provideUpperSetCookTimeText()
        if (provideViewHolderHelper().getLowerCookingStatusWidget() != null) provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.tvSetCookTime()?.text =
            provideLowerSetCookTimeText()
    }

    protected open fun provideUpperSetCookTimeText(): CharSequence? {
        return getString(R.string.text_button_set_cook_time)
    }

    protected open fun provideLowerSetCookTimeText(): CharSequence? {
        return getString(R.string.text_button_set_cook_time)
    }

    /**
     * after preheat completes start oven ready runnable
     */
    private fun managePreheatOvenReadyState(
        statusWidget: CookingStatusWidget,
        cookingVM: CookingViewModel,
        preheatCompleteNotificationText: Notification?,
        readySincePreHeatTimerState: Timer.State?,
        isDoorOpened: Boolean
    ) {
        if (cookingVM.recipeExecutionViewModel.isNotRunning || cancelledTime[if(cookingVM.isPrimaryCavity) 0 else 1] > 0) return
        if (context == null || preheatCompleteNotificationText == null || preheatCompleteNotificationText.text == null) return
        if (statusWidget.statusWidgetHelper.tvResumeCooking()?.isVisible == true && cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED) {
            HMILogHelper.Logd(
                tag,
                "${cookingVM.cavityName.value} has tvResumeCooking shown, so skipping preheatCompleteNotificationText ${preheatCompleteNotificationText.text}"
            )
            return
        }
        if(CookingAppUtils.isUserInstructionRequired(cookingVM)) {
            HMILogHelper.Logd(tag, "${cookingVM.cavityName.value} user instruction is required, skipping isDoorOpenClosedInPreHeat")
            return
        }
        if (preheatCompleteNotificationText.text?.contentEquals("preheatComplete") == true ||
            readySincePreHeatTimerState?.equals(Timer.State.RUNNING) == true
        ) {
            if (isDoorOpened) {
                isDoorOpenClosedInPreHeat[if (cookingVM.isPrimaryCavity) 0 else 1] = false
            } else {
                if (isDoorOpenClosedInPreHeat.any { !it }) {
                    HMILogHelper.Logd(tag, "${cookingVM.cavityName.value} starting cookTimer based on isDoorOpenClosedInPreHeat $isDoorOpenClosedInPreHeat")
                    cookingVM.recipeExecutionViewModel.startCookTimer()
                    executeReadyAtTimeRunnable(
                        cookingVM,
                        statusWidget.statusWidgetHelper
                    )
                    isDoorOpenClosedInPreHeat[if (cookingVM.isPrimaryCavity) 0 else 1] = true
                }
            }
            //after 2 minutes delay oven ready text will be replace by Recipe name and temperature value
            if (isCavityHot(cookingVM)) {
                executeOvenCoolingForHotCavityRunnable(cookingVM, statusWidget.statusWidgetHelper)
            } else if ((cookingVM.recipeExecutionViewModel.readySincePreheatTime.value
                    ?: 0) < resources.getInteger(R.integer.duration_status_mode_text_oven_ready_2_min)
            ) {
                HMILogHelper.Logd(
                    tag,
                    "${cookingVM.cavityName.value} executeOvenReadyRunnable, preheatCompleteNotificationText ${preheatCompleteNotificationText.text} AND readySincePreheatTime is less than 2 min"
                )
                executeOvenReadyRunnable(
                    cookingVM,
                    statusWidget.statusWidgetHelper.tvRecipeWithTemperature(),
                    statusWidget.statusWidgetHelper
                )
                statusWidget.statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.GONE
            }
        }
    }

    /**
     * post oven ready message on Recipe text view and send delayed message to remove the runnbale
     */
    private fun executeOvenReadyRunnable(
        cookingVM: CookingViewModel,
        tvRecipeWithTemperature: ResourceTextView?,
        statusWidgetHelper: AbstractStatusWidgetHelper,
    ) {
        val cavityPosition = if (cookingVM.isPrimaryCavity) 1 else 2
        if (statusWidgetHelper.tvResumeCooking()?.isVisible == true) {
            HMILogHelper.Loge(
                tag,
                "${cookingVM.cavityName.value} has tvResumeCooking shown, so skipping executeOvenReadyRunnable"
            )
            return
        }
        HMILogHelper.Logd(tag, "HANDLER: ${cookingVM.cavityName.value} executeOvenReadyRunnable oven ready state, coming from preheat Complete notification or readySincePreheat timer is running")
        if(cookingVM.recipeExecutionViewModel.isProbeBasedRecipe && cookingVM.recipeExecutionViewModel.targetMeatProbeTemperatureReached.value == true && cookingVM.recipeExecutionViewModel.cookTimerState.value == Timer.State.IDLE){
            HMILogHelper.Logd(tag, "HANDLER: ${cookingVM.cavityName.value} executeOvenReadyRunnable isProbeBasedRecipe and targetMeatProbeTemperatureReached, so skipping oven ready runnable")
            return
        }
        val handler = getCavityHandler(cavityPosition)
        //to catch the message on handler for which cavity later can be added for data object as well if needed we can pass the object along with bundle value
//       for example val bundle = Bundle() bundle.putInt(MSG_KEY_CAVITY_POSITION, cavityPosition) msg.data = bundle msg.obj = statusWidgetHelper.tvRecipeWithTemperature()
        handler.removeCallbacksAndMessages(null)
        handler.post(
            runnableOvenReady(
                tvRecipeWithTemperature, cavityPosition, statusWidgetHelper
            )
        )
    }

    /**
     * post oven ready message on Recipe text view and send delayed message to remove the runnbale
     */
    private fun executeOvenCoolingForHotCavityRunnable(
        cookingVM: CookingViewModel,
        statusWidgetHelper: AbstractStatusWidgetHelper?
    ) {
        if (statusWidgetHelper?.tvResumeCooking()?.isVisible == true) {
            HMILogHelper.Loge(
                tag,
                "${cookingVM.cavityName.value} has tvResumeCooking shown, so skipping executeOvenCoolingForHotCavityRunnable"
            )
            return
        }
        HMILogHelper.Logd(tag, "HANDLER: ${cookingVM.cavityName.value} executeOvenCoolingForHotCavityRunnable")
        val cavityPosition = if (cookingVM.isPrimaryCavity) 1 else 2
        HMILogHelper.Logd(tag, "executing runnable for oven hot cavityPosition = $cavityPosition")
        val handler = getCavityHandler(cavityPosition)
        handler.removeCallbacksAndMessages(null)
        handler.post(
            runnableOvenCoolingInHotCavity(
                statusWidgetHelper, cavityPosition
            )
        )
    }

    /**
     * post runnable object on handler when cookTimer is running
     */
    private fun executeReadyAtTimeRunnable(
        cookingVM: CookingViewModel,
        statusWidgetHelper: AbstractStatusWidgetHelper,
    ) {
        //MAF_2754: Defect fixed for the system is not transitioning between the "Ready at XX:XX" screen and the "Melt Cycle Running" screen as expected.
        if (statusWidgetHelper.tvResumeCooking()?.isVisible == true && (cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED ||
                        cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED_EXT)) {
            HMILogHelper.Loge(
                tag,
                "${cookingVM.cavityName.value} has tvResumeCooking shown, so skipping executeReadyAtTimeRunnable"
            )
            return
        }
        HMILogHelper.Logd(tag, "HANDLER: ${cookingVM.cavityName.value} executeReadyAtTimeRunnable")
        val cavityPosition = if (cookingVM.isPrimaryCavity) 1 else 2
        val handler = getCavityHandler(cavityPosition)
        //to catch the message on handler for which cavity later can be added for data object as well if needed we can pass the object along with bundle value
//       for example val bundle = Bundle() bundle.putInt(MSG_KEY_CAVITY_POSITION, cavityPosition) msg.data = bundle msg.obj = statusWidgetHelper.tvRecipeWithTemperature()
        handler.removeCallbacksAndMessages(null)
        handler.post(
            if (cookingVM.recipeExecutionViewModel.isVirtualchefBasedRecipe)
                runnableReadyAtTimeVision(
                    statusWidgetHelper, cavityPosition, true
                )
            else
                runnableReadyAtTime(
                    statusWidgetHelper, cavityPosition, true
                )
        )
    }

    /**
     * post runnable object on handler when sensing recipe is running
     */
    private fun executeKeepDoorClosedRunnable(
        cookingVM: CookingViewModel,
        tvRecipeWithTemperature: ResourceTextView?,
        tvCookTimeRemaining: ResourceTextView?,
    ) {
        HMILogHelper.Logd(tag, "HANDLER: ${cookingVM.cavityName.value} executeKeepDoorClosedRunnable")
        val cavityPosition = if (cookingVM.isPrimaryCavity) 1 else 2
        val handler = getCavityHandler(cavityPosition)
        //to catch the message on handler for which cavity later can be added for data object as well if needed we can pass the object along with bundle value
//       for example val bundle = Bundle() bundle.putInt(MSG_KEY_CAVITY_POSITION, cavityPosition) msg.data = bundle msg.obj = statusWidgetHelper.tvRecipeWithTemperature()
        handler.removeCallbacksAndMessages(null)
        handler.post(
            runnableKeepDoorClosed(
                tvRecipeWithTemperature, cavityPosition, tvCookTimeRemaining
            )
        )
    }

    /**
     * post runnable object on handler when cookTimer is running
     */
    private fun executeDelayRecipeRunnable(
        cookingVM: CookingViewModel,
        statusWidgetHelper: AbstractStatusWidgetHelper,
    ) {
        HMILogHelper.Logd(tag, "HANDLER: ${cookingVM.cavityName.value} executeDelayRecipeRunnable")
        val cavityPosition = if (cookingVM.isPrimaryCavity) 1 else 2
        val handler = getCavityHandler(cavityPosition)
        //to catch the message on handler for which cavity later can be added for data object as well if needed we can pass the object along with bundle value
//       for example val bundle = Bundle() bundle.putInt(MSG_KEY_CAVITY_POSITION, cavityPosition) msg.data = bundle msg.obj = statusWidgetHelper.tvRecipeWithTemperature()
        handler.removeCallbacksAndMessages(null)
        handler.post(
            runnableDelayRecipeTime(
                statusWidgetHelper, cavityPosition, true
            )
        )
    }

    /**
     * Runnable to change the textview back and forth between Ready at (time) and recipe name with temperature
     */
    private fun runnableReadyAtTime(
        statusWidgetHelper: AbstractStatusWidgetHelper?,
        cavityPosition: Int,
        isFirstTime: Boolean
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            val tvRecipeWithTemperature = statusWidgetHelper?.tvRecipeWithTemperature() ?: return@Runnable
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            HMILogHelper.Logd(
                TAG,
                "runnableReadyAtTime cavity= $cavityPosition text=" + tvRecipeWithTemperature.text
            )
            var modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
            val handler = getCavityHandler(cavityPosition)
            val currentTimerState = cookingVM.recipeExecutionViewModel.cookTimerState.value
            if (currentTimerState != Timer.State.RUNNING) {
                if (cookingVM.recipeExecutionViewModel.currentStep?.name?.text.equals(AppConstants.BROWNING_CONSTANT) ||
                    cookingVM.recipeExecutionViewModel.currentStep?.name?.text.equals(AppConstants.ADD_BROWNING_CONSTANT)
                ) {
                    return@Runnable
                }
                HMILogHelper.Logd(
                    "$TAG runnable",
                    "existing runnableReadyAtTime RUNNABLE and setting text as $modeText due to $currentTimerState is NOT RUNNING"
                )
                handler.removeCallbacksAndMessages(null)
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                return@Runnable
            }

            if (cookingVM.recipeExecutionViewModel.isSensingRecipe && (!cookingVM.recipeExecutionViewModel.isProbeBasedRecipe)) {
                if (tvRecipeWithTemperature.text.startsWith(
                        tvRecipeWithTemperature.context.getString(
                            R.string.text_running_ready_at
                        )
                    )
                ) {
                    if (!firstTimeKeepDoorClosed) {
                        modeText =
                            tvRecipeWithTemperature.context.getString(R.string.text_running_keep_door_closed)
                        updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, false)
                    }
                } else if (tvRecipeWithTemperature.text.startsWith(
                        tvRecipeWithTemperature.context.getString(
                            R.string.text_running_keep_door_closed
                        )
                    )
                ) {
                    if (!firstTimeKeepDoorClosed)
                        updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                } else if (tvRecipeWithTemperature.text.equals(context?.getString(R.string.text_cancelled_cycle_running, modeText))) {
                    //Defect fixed where we don't need to show cancelled text once click on Resume button
                    updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                } else
                 {
                    val remainingTime =
                        cookingVM.recipeExecutionViewModel.remainingCookTime.value ?: 0
                    if (!firstTimeKeepDoorClosed) {
                        modeText =
                            tvRecipeWithTemperature.context.getString(R.string.text_running_ready_at) + EMPTY_SPACE + TimeUtils.getUntilReadyTime(
                                remainingTime
                            )
                        updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, false)
                    }
                }
            } else {
                if (isFirstTime || tvRecipeWithTemperature.text.startsWith(
                                tvRecipeWithTemperature.context.getString(
                                        R.string.text_running_ready_at
                                )
                        )
                ) {
                    updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                } else {
                    val remainingTime = cookingVM.recipeExecutionViewModel.remainingCookTime.value
                        ?: 0
                    modeText =
                        tvRecipeWithTemperature.context.getString(R.string.text_running_ready_at) + EMPTY_SPACE + TimeUtils.getUntilReadyTime(
                            remainingTime
                        )
                    updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, false)
                }
            }
            firstTimeKeepDoorClosed = false
            handler.postDelayed(
                runnableReadyAtTime(
                    statusWidgetHelper,
                    cavityPosition,
                    false
                ),
                tvRecipeWithTemperature.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * Runnable to change the textview back and forth between Ready at (time) and recipe name with temperature
     */
    private fun runnableReadyAtTimeVision(
        statusWidgetHelper: AbstractStatusWidgetHelper?,
        cavityPosition: Int,
        isFirstTime: Boolean
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            val tvRecipeWithTemperature = statusWidgetHelper?.tvRecipeWithTemperature() ?: return@Runnable
            HMILogHelper.Logd(
                TAG,
                "runnableReadyAtTimeVision cavity= $cavityPosition text=" + tvRecipeWithTemperature.text
            )
            var modeText: String = CookingAppUtils.getRecipeNameWithParametersVision(requireContext(), cookingVM)
            val handler = getCavityHandler(cavityPosition)

            if (isFirstTime || tvRecipeWithTemperature.text.startsWith(
                    tvRecipeWithTemperature.context.getString(
                        R.string.text_running_ready_in
                    )
                ) || tvRecipeWithTemperature.text.startsWith(
                    tvRecipeWithTemperature.context.getString(
                        R.string.text_running_sensing
                    )
                )
            ) {
                HMILogHelper.Logd(
                    TAG,
                    "runnableReadyAtTimeVision cavity= $cavityPosition modeText=" + modeText
                )
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
            } else {
                val remainingDonenessTime = cookingVM.recipeExecutionViewModel.remainingDonenessTimeEstimate.value
                    ?: 0L

                HMILogHelper.Logd("doneness remaining time remainingDonenessTime $remainingDonenessTime")
                modeText = if (remainingDonenessTime <= 0L)
                    getString(R.string.text_running_sensing)
                else
                    tvRecipeWithTemperature.context.getString(R.string.text_running_ready_in) + EMPTY_SPACE + TimeUtils.convertTimeToMinutes(
                        remainingDonenessTime
                    ) + EMPTY_SPACE + tvRecipeWithTemperature.context.getString(R.string.text_label_min)
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, false)
            }
            firstTimeKeepDoorClosed = false
            handler.postDelayed(
                runnableReadyAtTimeVision(
                    statusWidgetHelper,
                    cavityPosition,
                    false
                ),
                tvRecipeWithTemperature.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * Runnable to change the textview back and forth between keep door closed and recipe name with temperature
     */
    private fun runnableKeepDoorClosed(
        tvRecipeWithTemperature: ResourceTextView?,
        cavityPosition: Int,
        tvCookTimeRemaining: ResourceTextView?
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            if (tvRecipeWithTemperature == null) return@Runnable
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            HMILogHelper.Logd(
                TAG,
                "runnableKeepDoorClosed cavity= $cavityPosition text=" + tvRecipeWithTemperature.text
            )
            var modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
            val handler = getCavityHandler(cavityPosition)
            val currentTimerState = cookingVM.recipeExecutionViewModel.cookTimerState.value
            if (currentTimerState != Timer.State.IDLE && currentTimerState != Timer.State.PAUSED) {
                HMILogHelper.Logd(
                    "$TAG runnable",
                    "existing runnableKeepDoorClosed RUNNABLE and setting text as $modeText due to $currentTimerState is NOT RUNNING"
                )
                handler.removeCallbacksAndMessages(null)
                //updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                return@Runnable
            }

            if (tvRecipeWithTemperature.text.startsWith(
                    tvRecipeWithTemperature.context.getString(
                        R.string.text_running_keep_door_closed
                    )
                )
            ) {
                if (!firstTimeKeepDoorClosed) {
                    updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                    updateRecipeModeTextStyle(
                        tvCookTimeRemaining,
                        tvCookTimeRemaining?.context?.getString(R.string.text_running_sensing),
                        true
                    )
                }
            } else {
                // modeText = tvRecipeWithTemperature.context.getString(R.string.text_running_keep_door_closed)
                if (!firstTimeKeepDoorClosed) {
                    modeText =
                        tvRecipeWithTemperature.context.getString(R.string.text_running_keep_door_closed)
                    updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, false)
                    updateRecipeModeTextStyle(
                        tvCookTimeRemaining,
                        tvCookTimeRemaining?.context?.getString(R.string.text_running_sensing),
                        false
                    )
                }
            }
            firstTimeKeepDoorClosed = false
            handler.postDelayed(
                runnableKeepDoorClosed(
                    tvRecipeWithTemperature,
                    cavityPosition, tvCookTimeRemaining
                ),
                tvRecipeWithTemperature.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * Runnable to change the textview back and forth when recipe execution is in delay state
     */
    private fun runnableDelayRecipeTime(
        statusWidgetHelper: AbstractStatusWidgetHelper,
        cavityPosition: Int,
        isFirstTime: Boolean
    ): Runnable {
        return Runnable {
            val tvRecipeWithTemperature =
                statusWidgetHelper.tvRecipeWithTemperature() ?: return@Runnable
            //Do something after 3 secs...
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            val delayedTime = resources.getString(
                R.string.text_delayed_until_self_clean, TimeUtils.getUntilReadyTime(
                    cookingVM.recipeExecutionViewModel.remainingDelayTime.value ?: 0
                )
            )
            HMILogHelper.Logd(
                TAG,
                "cavity= $cavityPosition delayedTime $delayedTime delayState ${cookingVM.recipeExecutionViewModel.delayTimerState.value} remainingDelay ${cookingVM.recipeExecutionViewModel.remainingDelayTime.value} setDelayTime ${cookingVM.recipeExecutionViewModel.delayTime.value}"
            )
            val modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)

            val handler = getCavityHandler(cavityPosition)
            if (cookingVM.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.DELAYED) {
                HMILogHelper.Logd(
                    "$TAG runnable",
                    "existing runnableDelayRecipeTime RUNNABLE and setting text as $modeText due to recipeExecutionState is NOT DELAYED"
                )
                handler.removeCallbacksAndMessages(null)
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                return@Runnable
            }
            if(isFirstTime) makeDelayUIUpdates(cookingVM, statusWidgetHelper)
            if (isFirstTime || tvRecipeWithTemperature.text.startsWith(
                    tvRecipeWithTemperature.context.getString(
                        R.string.text_delayed_until_self_clean
                    ).dropLast(5)
                )
            ) updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
            else updateRecipeModeTextStyle(
                tvRecipeWithTemperature, delayedTime, false
            )
            handler.postDelayed(
                runnableDelayRecipeTime(
                    statusWidgetHelper, cavityPosition, false
                ),
                tvRecipeWithTemperature.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * make updates about delay UI components like hiding all action buttons except starts
     *
     * @param cookingVM cooking view model of a particular cavity
     * @param statusWidgetHelper status widget resources
     */
    private fun makeDelayUIUpdates(
        cookingVM: CookingViewModel,
        statusWidgetHelper: AbstractStatusWidgetHelper
    ) {
        if (cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED) {
            statusWidgetHelper.getStatusProgressBar()?.max = 0
            statusWidgetHelper.getStatusProgressBar()?.progress = 0
            makeIndefiniteProgressBarVisible(
                statusWidgetHelper.getStatusProgressBar(),
                statusWidgetHelper.getProgressbarInfinite(),
                false
            )
            statusWidgetHelper.tvResumeCooking()?.visibility = View.VISIBLE
            statusWidgetHelper.clParentWidgetAction()?.visibility = View.GONE
            statusWidgetHelper.tvResumeCooking()?.text = getString(R.string.text_start_now_button)
        } else {
            statusWidgetHelper.tvResumeCooking()?.visibility = View.GONE
            statusWidgetHelper.clParentWidgetAction()?.visibility = View.VISIBLE
        }
        knobRotationItems = initKnobItems()
    }

    private fun initKnobItems(): List<KnobItem>?{
        knobRotationItems = provideKnobRotationItems()
        if(isFromKnob) {
            HMILogHelper.Logd(tag, "KNOB: highlighting first item as coming from knobForwardTrace=true")
            if (knobRotationItems?.isNotEmpty() == true) {
                removeKnobItemHighlightExcept(knobRotationItems?.get(0))
            }
        }
        return knobRotationItems
    }


    /**
     * post oven ready message on Recipe text view and send delayed message to remove the runnable
     */
    private fun executeOvenStartTimerRunnable(
        cookingVM: CookingViewModel,
        tvRecipeWithTemperature: ResourceTextView?,
        statusWidgetHelper: AbstractStatusWidgetHelper
    ) {
        if (statusWidgetHelper.tvResumeCooking()?.isVisible == true) {
            HMILogHelper.Loge(
                tag,
                "${cookingVM.cavityName.value} has tvResumeCooking shown, so skipping executeOvenStartTimerRunnable"
            )
            return
        }
        HMILogHelper.Logd(tag, "HANDLER: ${cookingVM.cavityName.value} executeOvenStartTimerRunnable")
        val cavityPosition = if (cookingVM.isPrimaryCavity) 1 else 2
        val handler = getCavityHandler(cavityPosition)
        val ovenReadyTime = cookingVM.recipeExecutionViewModel.readySincePreheatTime.value ?: 0
        //to catch the message on handler for which cavity later can be added for data object as well if needed we can pass the object along with bundle value
//       for example val bundle = Bundle() bundle.putInt(MSG_KEY_CAVITY_POSITION, cavityPosition) msg.data = bundle msg.obj = statusWidgetHelper.tvRecipeWithTemperature()
        var startTimerExecutionDelay = 0L
        if(ovenReadyTime > resources.getInteger(R.integer.duration_status_mode_text_oven_ready_2_min)) {
            handler.removeCallbacksAndMessages(null)
        }else {
            startTimerExecutionDelay = resources.getInteger(R.integer.duration_status_mode_text_oven_ready_2_min) + ovenReadyTime
        }
        HMILogHelper.Logd(tag, "Executing oven start timer runnable is timer is paused, ovenReadyTime $ovenReadyTime, startTimerExecutionDelay $startTimerExecutionDelay")
        handler.postDelayed(
            runnableOvenStartTimer(
                tvRecipeWithTemperature, cavityPosition
            ), TimeUnit.SECONDS.toMillis(startTimerExecutionDelay)
        )
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        HMILogHelper.Logd("RecipeExecution isRunning =${cookingViewModel?.recipeExecutionViewModel?.isRunning}")
        if (cookingViewModel?.recipeExecutionViewModel?.isProbeBasedRecipe == true) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    private fun checkMeatProbeConnectionPostBlackout(cookingViewModel: CookingViewModel?) {
        if ((cookingViewModel?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.RUNNING_EXT ||
                    cookingViewModel?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.PAUSED_EXT)
            && cookingViewModel.recipeExecutionViewModel?.isProbeBasedRecipe == true
            && !MeatProbeUtils.isMeatProbeConnected(cookingViewModel)) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }
    companion object {
        private const val TAG = "AbstractStatusFragment"

        /**
         * object that holds the data of knob related item
         * @property view subject to change its appearance like applying highlight when knob rotates or click event happens
         * @property isSelected to indicate if the current view is selected or not
         * @property index indicate the position in the knobRotation
         * @property cookingVM cooking view model associated with the knob item
         */
        data class KnobItem(var view: View?, var isSelected: Boolean, var index: Int, val cookingVM: CookingViewModel?){
            init {
                if (view is ConstraintLayout) view?.context?.getColor(R.color.cavity_selection_button_background)
                    ?.let {
                        (view as ConstraintLayout).setBackgroundColor(
                            it
                        )
                    }
                else  view?.background = null
            }
        }

        /**
         * Global variable to track the time when cancel event was triggered,useful to show in view for text "Resume in x Sec"
         */
        var cancelledTime = IntArray(2)

        /**
         * Global variable to track door interaction during pre-heat
         */
        var isDoorOpenClosedInPreHeat = BooleanArray(2) { true }

        /**
         * Global variable extend cooking for any recipe which has non editable cook time, no API from SDK so had to track it here
         */
        var isRecipeCompleteForNonEditableCookTime = BooleanArray(2) { false }

        /**
         * Method to check whether recipe has entered into extended cooking phase or not, applicable for only recipe which has non editable cook time and not a assisted recipes
         */
        fun isExtendedCookingForNonEditableCookTimeRecipe(cookingVM: CookingViewModel?): Boolean {
            val isCookTimeNonEditable = cookingVM?.recipeExecutionViewModel?.nonEditableOptions?.value?.containsKey(RecipeOptions.COOK_TIME)
            if(CookingAppUtils.isRecipeAssisted(cookingVM?.recipeExecutionViewModel?.recipeName?.value, cookingVM?.cavityName?.value)) return false
            if (isCookTimeNonEditable == true && !CookingAppUtils.isCookTimeOptionAvailable(cookingVM) && isRecipeCompleteForNonEditableCookTime[if(cookingVM.isPrimaryCavity) 0 else 1]){
                return true
            }else if(cookingVM?.recipeExecutionViewModel?.isProbeBasedRecipe == true && cookingVM.recipeExecutionViewModel?.targetMeatProbeTemperatureReached?.value == true && cookingVM.recipeExecutionViewModel?.cookTimerState?.value != Timer.State.IDLE){
                return true
            }
            return false
        }

        /**
         * to handle delay state when door is open
         * show door open when delay timer is running, after closing door do nothing
         * override delay if door is closed and delay state is completed
         */
        @BindingAdapter(value = ["status_door_fragment", "status_door_cook_view_model", "status_door_state"])
        @JvmStatic
        fun bindDoorState(
            statusWidget: CookingStatusWidget,
            abstractStatusFragment: AbstractStatusFragment,
            cookingVM: CookingViewModel,
            isDoorOpened: Boolean
        ) {
            HMILogHelper.Logi(
                "$TAG ${cookingVM.cavityName.value}",
                "bindDoorState isDoorOpened $isDoorOpened statusWidget ${statusWidget.ovenType}"
            )
            if(cookingVM.isOfTypeOven) abstractStatusFragment.manageDoorState(cookingVM, isDoorOpened)
        }


        /**
         * Binding adapter method for updating live data with recipe execution
         */
        @BindingAdapter(value = ["status_user_instruction_fragment", "status_user_instruction_cook_view_model", "status_user_instruction"])
        @JvmStatic
        fun bindUserInstructionHelper(
            statusWidget: CookingStatusWidget,
            abstractStatusFragment: AbstractStatusFragment,
            cookingVM: CookingViewModel,
            userInstruction: UserInstruction
        ) {
            HMILogHelper.Logi(
                "$TAG ${cookingVM.cavityName.value}",
                "userInstruction ${userInstruction.text}"
            )
            abstractStatusFragment.manageUserInstructionHelper(
                statusWidget,
                cookingVM,
                userInstruction
            )
        }

        /**
         * Binding adapter method for updating live data with recipe execution
         */
        @BindingAdapter(value = ["status_execution_fragment", "status_execution_view_model", "status_execution_state"])
        @JvmStatic
        fun bindRecipeExecutionState(
            statusWidget: CookingStatusWidget,
            abstractStatusFragment: AbstractStatusFragment,
            cookingVM: CookingViewModel,
            recipeExecutionState: RecipeExecutionState,
        ) {
            HMILogHelper.Logi(
                "$TAG ${cookingVM.cavityName.value}",
                "recipeExecutionState ${recipeExecutionState.name}"
            )
            abstractStatusFragment.manageStatusWidgetOnExecutionState(
                statusWidget, cookingVM, recipeExecutionState
            )
        }

        /**
         * Binding adapter method for updating live data with recipe mode and temperature
         */
        @BindingAdapter(value = ["status_recipe_text_cooking_state", "status_recipe_text_recipe_name", "status_recipe_cook_view_model", "status_recipe_text_door_state", "status_recipe_fragment", "status_recipe_recipe_execution_state"])
        @JvmStatic
        fun bindRecipeUpdateText(
            statusWidget: CookingStatusWidget,
            recipeCookingState: RecipeCookingState?,
            recipeName: String?,
            cookingVM: CookingViewModel,
            isDoorOpened: Boolean,
            abstractStatusFragment: AbstractStatusFragment,
            recipeExecutionState: RecipeExecutionState,
        ) {
            HMILogHelper.Logi(
                "$TAG ${cookingVM.cavityName.value}",
                "recipeCookingState $recipeCookingState  recipeName $recipeName recipeExecutionState $recipeExecutionState"
            )
            abstractStatusFragment.updateRecipeNameWIthRecipeState(
                abstractStatusFragment.context,
                cookingVM,
                recipeCookingState,
                statusWidget.statusWidgetHelper,
                recipeName,
                isDoorOpened,
                recipeExecutionState
            )
        }

        /**
         * Binding adapter method for updating live data with recipe mode and temperature
         */
        @BindingAdapter(value = ["status_temperature_text_cooking_state", "status_temperature_text_recipe_name", "status_temperature_text_display_temperature", "status_temperature_text_target_temperature", "status_temperature_cook_view_model", "status_temperature_fragment", "status_temperature_recipe_execution_state"])
        @JvmStatic
        fun bindRecipeUpdateTextWithTemperature(
            statusWidget: CookingStatusWidget,
            recipeCookingState: RecipeCookingState?,
            recipeName: String?,
            ovenDisplayTemperature: Int?,
            targetTemperature: Int?,
            cookingVM: CookingViewModel,
            abstractStatusFragment: AbstractStatusFragment,
            recipeExecutionState: RecipeExecutionState,
        ) {
            HMILogHelper.Logi(
                "$TAG  ${cookingVM.cavityName.value}",
                "ovenDisplayTemperature $ovenDisplayTemperature targetTemperature $targetTemperature recipeCookingState $recipeCookingState  recipeName $recipeName"
            )
            if(cookingVM.recipeExecutionViewModel.isVirtualchefBasedRecipe){
                HMILogHelper.Logd("is Virtual Chef based recipe")
                return
            }
            abstractStatusFragment.updateRecipeNameWIthTemperature(
                abstractStatusFragment.context,
                cookingVM,
                recipeCookingState,
                statusWidget.statusWidgetHelper,
                recipeName,
                ovenDisplayTemperature,
                targetTemperature,
                recipeExecutionState
            )
        }

        /**
         * Binding adapter method for updating live data with recipe mode and temperature
         */
        @BindingAdapter(value = ["status_preheat_fragment", "status_preheat_door_state", "status_preheat_notification", "status_preheat_cook_view_model", "status_preheat_timer_state", "status_preheat_recipe_execution_state"])
        @JvmStatic
        fun bindPreheatOvenReadyState(
            statusWidget: CookingStatusWidget,
            abstractStatusFragment: AbstractStatusFragment,
            isDoorOpened: Boolean,
            preheatCompleteNotificationText: Notification?,
            cookingVM: CookingViewModel,
            readySincePreHeatTimerState: Timer.State?,
            recipeExecutionState: RecipeExecutionState
        ) {
            HMILogHelper.Logi(
                "$TAG ${cookingVM.cavityName.value}",
                "preheatCompleteNotificationText=${preheatCompleteNotificationText?.text}, recipeExecutionState $recipeExecutionState readySincePreHeatTimerState $readySincePreHeatTimerState isDoorOpened $isDoorOpened"
            )
            if (cookingVM.recipeExecutionViewModel.cookTimerState.value != Timer.State.COMPLETED || CookingAppUtils.isTimePreheatJustCompleted(cookingVM)) //escape condition for time based preheat recipes
                abstractStatusFragment.managePreheatOvenReadyState(
                statusWidget,
                cookingVM,
                preheatCompleteNotificationText,
                readySincePreHeatTimerState, isDoorOpened
            )
        }

        /**
         * Binding Adapter to bind the Progress bar
         * The progressbar progress can happen based on temperature progress in preheating phase when
         * the cook timer is not started, once the cook timer is started it will follow the cook timer
         * progress
         *
         * @param statusWidget      Status widget
         * @param percentageVal     The progressbar value received from the SDK
         * @param cookProgressBasis SDK will send the basis of the cook progress (time or temperature
         *                          based)
         */
        @BindingAdapter(value = ["status_progress_bar_cook_view_model", "status_progress_bar_fragment", "status_progress_bar_progress_percentage", "status_progress_bar_basis", "status_progress_bar_cook_timer_state", "status_progress_bar_recipe_execution_state"])
        @JvmStatic
        fun bindCookingProgressBar(
            statusWidget: CookingStatusWidget,
            cookingVM: CookingViewModel,
            abstractStatusFragment: AbstractStatusFragment,
            percentageVal: Int?,
            cookProgressBasis: RecipeProgressBasis?,
            cookTimerState: Timer.State?,
            recipeExecutionState: RecipeExecutionState?
        ) {
            val statusProgressBar: ProgressBar? =
                statusWidget.statusWidgetHelper.getStatusProgressBar()
            if (null == cookProgressBasis || null == percentageVal || null == cookTimerState || null == statusProgressBar) {
                HMILogHelper.Loge("ProgressBar: SDK: cookProgressBasis or progressPercentage is NULL")
                statusProgressBar?.visibility = View.INVISIBLE
                return
            }
            HMILogHelper.Logi(
                "$TAG ${cookingVM.cavityName.value}",
                "cookProgressBasis $cookProgressBasis  ProgressPercentage $percentageVal cookTimerState $cookTimerState recipeExecutionState $recipeExecutionState"
            )
            abstractStatusFragment.manageCookingProgressBar(
                cookingVM,
                statusWidget.statusWidgetHelper,
                percentageVal,
                cookProgressBasis,
                cookTimerState
            )
        }


        /**
         * Binding Adapter to bind the cook remaining timer text
         * The progressbar progress can happen based on temperature progress in preheating phase when
         * the cook timer is not started, once the cook timer is started it will follow the cook timer
         * progress
         *
         * @param statusWidget      Status widget
         *
         */
        @BindingAdapter(
            value = ["status_etr_cook_view_model", "status_etr_cook_timer_fragment", "status_etr_cook_timer_remaining_cook_time",
                "status_etr_cook_timer_recipe_timer_state", "status_etr_cook_time_recipe_cooking_state",
                "status_etr_cook_time_probe_current_temperature", "status_etr_cook_time_probe_target_temperature", "status_etr_cook_time_probe_connection_state",
            "status_etr_cook_time_probe_target_temperature_reached"]
        )
        @JvmStatic
        fun bindCookRemainingTimeText(
            statusWidget: CookingStatusWidget,
            cookingViewModel: CookingViewModel?,
            abstractStatusFragment: AbstractStatusFragment?,
            remainingTime: Long?,
            cookTimerState: Timer.State?,
            recipeCookingState: RecipeCookingState?,
            probeCurrentTemperature: Int?,
            probeTargetTemperature: Int?,
            probeConnectionState: Boolean?,
            probeTargetTemperatureReached: Boolean?
        ) {
            if (null == remainingTime || null == cookTimerState || null == abstractStatusFragment || null == recipeCookingState || cookingViewModel == null) {
                HMILogHelper.Loge(
                    "CookTimerTextView: SDK: remainingTime  or cookTimerState or recipeExecutionState is null"
                )
                statusWidget.statusWidgetHelper.tvSetCookTime()?.visibility = View.INVISIBLE
                return
            }
            HMILogHelper.Logd(
                "TAG ${cookingViewModel.cavityName.value}",
                "remainingCookTime=$remainingTime || cookTimerState=$cookTimerState || recipeCookingState=$recipeCookingState"
            )
            if (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE) return
            if (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED && cookingViewModel.doorState.value == true) {
                HMILogHelper.Loge("Door was opened and recipeExecutionState is PAUSED so not updating bindCookRemainingTimeText")
                return
            }
            if (cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
                //Defect fixes:- Probe temperature not able show during probe assisted cycle and probe cycle based on doneness level
                if ((CookingAppUtils.isRequiredTargetAvailable(
                        cookingViewModel,
                        RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE) && probeTargetTemperatureReached == false)
                    || (cookingViewModel.recipeExecutionViewModel?.nonEditableOptions?.value?.containsKey(RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE) == true
                            && probeTargetTemperatureReached == false)
                    || (CookingAppUtils.isRequiredTargetAvailable(
                        cookingViewModel,
                        RecipeOptions.DONENESS) && cookingViewModel.recipeExecutionViewModel.meatProbeTargetTemperature.value != 0 && probeTargetTemperatureReached == false)
                ) abstractStatusFragment.manageProbeTemperature(
                    statusWidget,
                    cookingViewModel,
                    probeCurrentTemperature,
                    probeTargetTemperature,
                    probeConnectionState
                )
                if(probeTargetTemperatureReached == true)
                    abstractStatusFragment.manageProbeExtendedCookTimeText(
                        statusWidget.statusWidgetHelper,
                        cookingViewModel
                    )
                return
            }
            if (cookingViewModel.recipeExecutionViewModel.isVirtualchefBasedRecipe) {
                statusWidget.statusWidgetHelper.tvCookTimeRemaining()?.visibility = View.GONE
            } else {
                abstractStatusFragment.manageCookTimeRemainingText(
                    statusWidget,
                    cookingViewModel,
                    remainingTime,
                    cookTimerState,
                    recipeCookingState
                )
            }
        }

        /**
         * Binding Adapter to bind the cook timer state
         * once cook timer is started then port runnable to switch text
         * progress
         *8
         * @param statusWidget      Status widget
         * @param cookingViewModel  The progressbar value received from the SDK
         * @param cookTimerState  state of the cook timer ex running, paused, idle, completed
         */
        @BindingAdapter(value = ["status_cookTimer_cook_view_model", "status_cookTimer_fragment", "status_cookTimer_state","status_cookTimer_probe_temperature_reached", "status_cookTimer_recipe_execution_state"])
        @JvmStatic
        fun bindCookTimerState(
            statusWidget: CookingStatusWidget,
            cookingViewModel: CookingViewModel?,
            abstractStatusFragment: AbstractStatusFragment?,
            cookTimerState: Timer.State?,
            probeTargetTemperatureReached: Boolean?,
            recipeExecutionState: RecipeExecutionState
        ) {
            if (null == cookTimerState || null == abstractStatusFragment || cookingViewModel == null) {
                HMILogHelper.Loge(
                    "CookTimerTextView: SDK: remainingTime  or cookTimerState or " + "delayTimerState or recipeExecutionState  or is null"
                )
                return
            }
            HMILogHelper.Logd(
                "$TAG ${cookingViewModel.cavityName.value}", "cookTimerState=$cookTimerState recipeExecutionState =$recipeExecutionState"
            )
            if (recipeExecutionState == RecipeExecutionState.IDLE) return
            if (cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe && probeTargetTemperatureReached == true && cookTimerState == Timer.State.IDLE){
                HMILogHelper.Logd("$TAG ${cookingViewModel.cavityName.value}", "probeTargetTemperatureReached is TRUE")
                abstractStatusFragment.executeProbeCompleteRunnable(if (cookingViewModel.isPrimaryCavity) 1 else 2, statusWidget.statusWidgetHelper.tvRecipeWithTemperature(), statusWidget.statusWidgetHelper)
                return
            }
            abstractStatusFragment.manageCookTimeStateRunnable(
                statusWidget,
                cookingViewModel,
                cookTimerState
            )
        }

        /**
         * Binding adapter method for updating live data with recipe execution
         */
        @BindingAdapter(value = ["sabbath_status_execution_fragment", "sabbath_status_execution_view_model", "sabbath_status_execution_state"])
        @JvmStatic
        fun bindSabbathRecipeExecutionState(
            statusWidget: CookingStatusWidget,
            abstractStatusFragment: AbstractStatusFragment,
            cookingVM: CookingViewModel,
            recipeExecutionState: RecipeExecutionState,
        ) {
            HMILogHelper.Logi(
                "$TAG ${cookingVM.cavityName.value}",
                "recipeExecutionState ${recipeExecutionState.name} statusWidget ${statusWidget.ovenType}"
            )
            abstractStatusFragment.manageSabbathStatusWidgetOnExecutionState(
                statusWidget.statusWidgetHelper,
                cookingVM,
                recipeExecutionState
            )
        }

        /**
         * Binding adapter method for updating live data with recipe mode and temperature
         */
        @BindingAdapter(value = ["sabbath_status_recipe_text_recipe_name", "sabbath_status_recipe_cook_view_model", "sabbath_status_recipe_fragment"])
        @JvmStatic
        fun bindSabbathRecipeWithTemperature(
            statusWidget: CookingStatusWidget,
            recipeName: String,
            cookingVM: CookingViewModel,
            abstractStatusFragment: AbstractStatusFragment
        ) {
            HMILogHelper.Logi(
                "$TAG  ${cookingVM.cavityName.value}",
                "recipeName $recipeName"
            )
            abstractStatusFragment.manageSabbathRecipeWithTemperature(
                cookingVM,
                statusWidget.statusWidgetHelper,
                recipeName
            )
        }


        /**
         * Binding adapter method for updating live data with recipe mode and temperature
         */
        @BindingAdapter(value = ["sabbath_status_etr_cook_timer_recipe_timer_state", "sabbath_status_etr_cook_timer_remaining_cook_time", "sabbath_status_etr_cook_view_model", "sabbath_status_etr_cook_timer_fragment"])
        @JvmStatic
        fun bindSabbathCookRemainingTimeText(
            statusWidget: CookingStatusWidget,
            cookTimerState: Timer.State?,
            remainingTime: Long?,
            cookingVM: CookingViewModel,
            abstractStatusFragment: AbstractStatusFragment
        ) {
            HMILogHelper.Logi(
                "$TAG  ${cookingVM.cavityName.value}",
                "cookTimerState $cookTimerState, remainingTime $remainingTime"
            )
            abstractStatusFragment.manageSabbathCookRemainingTimeText(
                statusWidget.statusWidgetHelper,
                cookingVM,
                cookTimerState,
                remainingTime
            )
        }

        /**
         * update static variables like door, preheat message based on cavity, useful when switching from single, to double, or lower status fragment
         * also in resume state
         */
        fun updateStaticVariables() {
            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.COMBO,
                CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
                -> {
                    if (CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE) {
                        cancelledTime[1] = 0
                        isDoorOpenClosedInPreHeat.forEachIndexed { index, _ -> isDoorOpenClosedInPreHeat[index] = true }
                        SharedPreferenceManager.setPauseForCancelRecovery(false, AppConstants.FALSE_CONSTANT)
                        isRecipeCompleteForNonEditableCookTime[1] = false
                    }
                }

                else -> {}
            }
            if (CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE) {
                cancelledTime[0] = 0
                isDoorOpenClosedInPreHeat.forEachIndexed { index, _ -> isDoorOpenClosedInPreHeat[index] = true }
                SharedPreferenceManager.setPauseForCancelRecovery(true, AppConstants.FALSE_CONSTANT)
                isRecipeCompleteForNonEditableCookTime[0] = false
            }
        }
    }

    /**
     * responsible to update cook time remaining text when Sabbath Bake is running
     */
    private fun manageSabbathCookRemainingTimeText(
        statusWidgetHelper: AbstractStatusWidgetHelper,
        cookingVM: CookingViewModel,
        cookTimerState: Timer.State?,
        remainingTime: Long?
    ) {

        when (cookTimerState) {
            Timer.State.IDLE -> statusWidgetHelper.tvCookTimeRemaining()?.visibility = View.GONE
            Timer.State.COMPLETED -> {
                HMILogHelper.Logd(
                    tag,
                    "Sabbath cookTimeRemaining cookTimerState $cookTimerState, so cancelling view model for ${cookingVM.cavityName.value}"
                )
                cookingVM.recipeExecutionViewModel.cancel()
            }


            else -> {
                val etrToDisplay = CookingAppUtils.spannableETRRunning(
                    context, remainingTime?.toInt() ?: 0
                )
                statusWidgetHelper.tvCookTimeRemaining()?.text = etrToDisplay
                HMILogHelper.Logd(
                    tag,
                    "Sabbath cookTimeRemaining cookTimerState $cookTimerState, etrToDisplay $etrToDisplay"
                )
            }
        }
    }


    /**
     * responsible to update recipe name along with temperature for Sabbath Bake
     */
    private fun manageSabbathRecipeWithTemperature(
        cookingVM: CookingViewModel,
        statusWidgetHelper: AbstractStatusWidgetHelper,
        recipeName: String?
    ) {
        if (recipeName?.isNotEmpty() == true) {
            val recipeNameWithTemperature =
                CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
            HMILogHelper.Logd(
                tag,
                "Sabbath ${cookingVM.cavityName.value} recipeUpdate text $recipeName"
            )
            if (isCavityFaultNone(cookingVM)) statusWidgetHelper.tvRecipeWithTemperature()?.text =
                recipeNameWithTemperature
        }
    }

    /**
     * responsible to manage RecipeExecutionState and cancel the view model in case recipe failed
     */
    protected open fun manageSabbathStatusWidgetOnExecutionState(
        statusWidgetHelper: AbstractStatusWidgetHelper,
        cookingVM: CookingViewModel?,
        recipeExecutionState: RecipeExecutionState
    ) {
        when (recipeExecutionState) {
            RecipeExecutionState.IDLE -> {
                HMILogHelper.Loge(
                    TAG,
                    "Moving to Clock or Status screen for ${cookingVM?.cavityName?.value} due to RecipeExecutionState.IDLE"
                )
                CookingAppUtils.navigateToSabbathStatusOrClockScreen(this)
            }


            RecipeExecutionState.CANCELLED_EXT,
            RecipeExecutionState.RUNNING_FAILED,
            RecipeExecutionState.PAUSED_FAILED,
            -> {//due to fault either from ACU or other external events, in this state cancel current cavity cycle
                HMILogHelper.Loge(
                    TAG,
                    "Cancelling recipeExecutionViewModel ${cookingVM?.cavityName?.value} due to recipeExecutionState $recipeExecutionState"
                )
                cookingVM?.recipeExecutionViewModel?.cancel()
            }

            else -> {}
        }
    }

    /**
     * to handle delay state when door is open
     * show door open when delay timer is running, after closing door do nothing
     * override delay if door is closed and delay state is completed
     * @param cookingVM cooking view model of a particular cavity
     * @param doorOpened true if door is open false otherwise
     */
    private fun manageDoorState(
        cookingVM: CookingViewModel,
        doorOpened: Boolean
    ) {
        if(!doorOpened && MeatProbeUtils.isMeatProbeConnected(cookingVM) && !cookingVM.recipeExecutionViewModel.isProbeBasedRecipe && cancelledTime[if(cookingVM.isPrimaryCavity) 0 else 1] == 0){
            HMILogHelper.Logd(TAG, "${cookingVM.cavityName.value} Meat probe is connected and recipe is not a probe recipe, probeDetectedFromSameCavityPopupBuilder")
            PopUpBuilderUtils.removeProbeToContinueCooking(this, cookingVM, getRecipeNameText(requireContext(), cookingVM.recipeExecutionViewModel.recipeName.value?:"")){
                HMILogHelper.Logd(TAG, "${cookingVM.cavityName.value} Meat probe is removed, probeDetectedFromSameCavityPopupBuilder")
            }
            return
        }
        //door open condition
        if (doorOpened && cookingVM.recipeExecutionViewModel.cookTimerState.value != Timer.State.COMPLETED && cookingVM.recipeExecutionViewModel.isRunning) {
            HMILogHelper.Logd(
                "$TAG ${cookingVM.cavityName.value}",
                "manageDoorState doorOpened TRUE showing to close the door popup"
            )
            if (isUpperSteamCleanRunning && cookingVM.isPrimaryCavity) {
                NavigationUtils.getVisibleFragment()?.let {
                    DoorEventUtils.upperSteamCloseDoorToContinueAction(
                        it, cookingVM, onDoorCloseEventAction = {
                            onResume()
                        }
                    )
                }
            } else if (isLowerSteamCleanRunning && cookingVM.isSecondaryCavity) {
                NavigationUtils.getVisibleFragment()?.let {
                    DoorEventUtils.lowerSteamCloseDoorToContinueAction(
                        it, cookingVM, onDoorCloseEventAction = {
                            onResume()
                        }
                    )
                }
            } else {
                if (cookingVM.isPrimaryCavity) {
                    NavigationUtils.getVisibleFragment()?.let {
                        DoorEventUtils.upperCloseDoorToContinueAction(
                            it, cookingVM, onDoorCloseEventAction = {
                                onResume()
                            }
                        )
                    }
                }else if(cookingVM.isSecondaryCavity){
                    NavigationUtils.getVisibleFragment()?.let {
                        DoorEventUtils.lowerCloseDoorToContinueAction(
                            it, cookingVM,onDoorCloseEventAction = {
                                onResume()
                            }
                        )
                    }
                }
            }
        }
    }

    /**
     * manage user instruction popup when required to continue running the recipe
     *
     * @param statusWidget      Status widget
     * @param cookingVM  cookingViewModel for a particular cavity
     * @param userInstruction recipe cooking instruction comes from capability file generated in hestia
     */
    @Suppress("UNUSED_PARAMETER")
    fun manageUserInstructionHelper(
        statusWidget: CookingStatusWidget,
        cookingVM: CookingViewModel,
        userInstruction: UserInstruction
    ) {
        if (cookingVM.recipeExecutionViewModel.isProbeBasedRecipe && !MeatProbeUtils.isMeatProbeConnected(
                cookingVM
            )
        ) return
        if (CookingAppUtils.isUserInstructionRequired(cookingVM)) PopUpBuilderUtils.displayCookingInstructionPopUp(
            this,
            cookingVM,
            userInstruction.text
        )
    }

    /**
     * show switch text back and forth until new state comes in
     * @param statusWidget      Status widget
     * @param cookingViewModel  cookingViewModel for a particular cavity
     * @param probeCurrentTemperature current int value of meat probe (either C or F) not changeable comes for hardware
     * @param probeTargetTemperature set target int value of meat probe when recipe being programmed(either C or F) changeable
     * @param probeConnectionState true if meat probe is connected false otherwise
     */
    private fun manageProbeTemperature(
        statusWidget: CookingStatusWidget,
        cookingViewModel: CookingViewModel,
        probeCurrentTemperature: Int?,
        probeTargetTemperature: Int?,
        probeConnectionState: Boolean?
    ) {
        HMILogHelper.Logd(
            tag,
            "cavityName: ${cookingViewModel.cavityName.value}, probeCurrentTemperature=$probeCurrentTemperature, probeTargetTemperature=$probeTargetTemperature,  probeConnectionState=$probeConnectionState"
        )
        if (probeConnectionState == true) {
            val probeComplete =
                cookingViewModel.recipeExecutionViewModel.targetMeatProbeTemperatureReached.value == true || ((probeCurrentTemperature
                    ?: 0) >= (probeTargetTemperature ?: 0))
            val tvCookingEtr = statusWidget.statusWidgetHelper.tvCookTimeRemaining()
            val displayProbeTemperature = StringBuilder()
            displayProbeTemperature.append(if (probeComplete) probeTargetTemperature else probeCurrentTemperature)
                .append(AppConstants.DEGREE_SYMBOL).append(AppConstants.SYMBOL_FORWARD_SLASH)
            displayProbeTemperature.append(probeTargetTemperature)
                .append(AppConstants.DEGREE_SYMBOL)
            tvCookingEtr?.text = displayProbeTemperature.toString()
            HMILogHelper.Logd(
                tag,
                "cavityName: ${cookingViewModel.cavityName.value} displayProbeTemperature=$displayProbeTemperature"
            )
        }else{
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    /**
     * show switch text back and forth until new state comes in
     * @param statusWidget      Status widget
     * @param cookingViewModel  The progressbar value received from the SDK
     * @param cookTimerState  state of the cook timer ex running, paused, idle, completed
     */
    private fun manageCookTimeStateRunnable(
        statusWidget: CookingStatusWidget,
        cookingViewModel: CookingViewModel,
        cookTimerState: Timer.State,
    ) {
        when (cookTimerState) {
            Timer.State.COMPLETED -> {
                statusWidget.statusWidgetHelper.getStatusProgressBar()?.progress = 100
                //MAF_2406:  The system should not displays the +30 sec option to extend cook time at the end of the popcorn cycle
                if(!(cookingViewModel.recipeExecutionViewModel.isSensingRecipe && (!cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) &&
                        (!CookingAppUtils.isRecipeOptionAvailable(cookingViewModel.recipeExecutionViewModel, RecipeOptions.COOK_TIME)))) {
                    updateCookTimeWidgetDynamically(statusWidget.statusWidgetHelper, false)
                }
                if (CookingAppUtils.isTimePreheatJustCompleted(cookingViewModel)) {
                    HMILogHelper.Logd(
                        tag,
                        "${cookingViewModel.cavityName.value} has time based preheat recipe and preheat just completed with escape condition of cookTimeState COMPLETED, so skipping executeTimerCompleteRunnable"
                    )
                    return
                }
                HMIExpansionUtils.startOrStopKnobLEDSlowBlinkAnimation(true)
                executeTimerCompleteRunnable(
                    cookingViewModel,
                    statusWidget.statusWidgetHelper.tvRecipeWithTemperature(),
                    statusWidget.statusWidgetHelper
                )
            }

            Timer.State.RUNNING -> {
                setKnobLightWhenCycleRunning()
                if (isCavityHot(cookingViewModel)) {
                    executeOvenCoolingForHotCavityRunnable(
                        cookingViewModel,
                        statusWidget.statusWidgetHelper
                    )
                } else {
                    if (cookingViewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                            AppConstants.BROWNING_CONSTANT
                        ) ||
                        cookingViewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                            AppConstants.ADD_BROWNING_CONSTANT
                        )
                    ) {
                        return
                    }
                    firstTimeKeepDoorClosed = true
                    if (cookingViewModel.recipeExecutionViewModel.recipeCookingState.value?.equals(RecipeCookingState.COOKING) == true) executeReadyAtTimeRunnable(
                        cookingViewModel,
                        statusWidget.statusWidgetHelper
                    )
                }
            }

            Timer.State.PAUSED -> {
                if (cookingViewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                        AppConstants.BROWNING_CONSTANT
                    ) ||
                    cookingViewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                        AppConstants.ADD_BROWNING_CONSTANT
                    )
                ) {
                    return
                }
                if (cookingViewModel.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.COOKING) {
                    makeIndefiniteProgressBarVisible(
                        statusWidget.statusWidgetHelper.getStatusProgressBar(),
                        statusWidget.statusWidgetHelper.getProgressbarInfinite(),
                        true
                    )
                }
            }

            Timer.State.IDLE -> {
            }

            else -> {}
        }
    }

    /**
     * responsible for update status widget for ex single, double or combo status of move to clock screen
     *
     * @param recipeExecutionState current state of recipe execution
     */
    fun manageStatusWidgetOnExecutionState(
        statusWidget: CookingStatusWidget,
        cookingVM: CookingViewModel,
        recipeExecutionState: RecipeExecutionState,
    ) {
        if(recipeExecutionState == RecipeExecutionState.PAUSED || recipeExecutionState == RecipeExecutionState.PAUSED_EXT){
            setBothKnobLightOffDirectly()
            HMILogHelper.Loge(
                TAG,
                "Turning Off Knob light due to Cycle is paused"
            )
        }
        when (recipeExecutionState) {
            RecipeExecutionState.IDLE -> {
                HMILogHelper.Loge(
                    TAG,
                    "Moving to Clock or Status screen for ${cookingVM.cavityName.value} due to RecipeExecutionState.IDLE"
                )
                SharedPreferenceManager.setPauseForCancelRecovery(
                    cookingVM.isPrimaryCavity,
                    "false"
                )
                navigateToStatusOrClockScreen(this)
            }

            RecipeExecutionState.CANCELLED_EXT -> {
                //due to fault either from ACU or other external events, in this state cancel current cavity cycle
                HMILogHelper.Loge(
                    TAG,
                    "Cancelling recipeExecutionViewModel ${cookingVM.cavityName.value} due to RecipeExecutionState.CANCELLED_EXT"
                )
                cookingVM.recipeExecutionViewModel.cancel()
            }

            RecipeExecutionState.RUNNING_FAILED,
            RecipeExecutionState.PAUSED_FAILED,
            -> {//due to fault either from ACU or other external events, in this state cancel current cavity cycle
                HMILogHelper.Loge(
                    TAG,
                    "Cancelling recipeExecutionViewModel ${cookingVM.cavityName.value} due to RecipeExecutionState.PAUSED_FAILED"
                )
                cookingVM.recipeExecutionViewModel.cancel()
            }

            RecipeExecutionState.RUNNING_EXT,
            RecipeExecutionState.RUNNING,
            -> {
                checkMeatProbeConnectionPostBlackout(CookingViewModelFactory.getInScopeViewModel())
                setKnobLightWhenCycleRunning()
                statusWidget.statusWidgetHelper.tvResumeCooking()?.visibility = View.GONE
                updateStatusWidget(statusWidget)
                SharedPreferenceManager.setPauseForCancelRecovery(
                    cookingVM.isPrimaryCavity,
                    "false"
                )
                knobRotationItems = initKnobItems()
                //Trigger Notification if user has run a cycle in PM and has not connected to NW for 15 days
                if((CookingAppUtils.isItAmOrPm()) && CookingAppUtils.getConnectToNwNotificationTriggerCheck()){
                    NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW)
                    CookingAppUtils.setConnectToNwNotificationTriggerCheck(false)
                }
            }

            RecipeExecutionState.DELAYED -> {
                HMILogHelper.Logd(
                    TAG,
                    "delayed Cavity ${cookingVM.cavityName.value} RecipeExecutionState = $recipeExecutionState"
                )
                executeDelayRecipeRunnable(cookingVM, statusWidget.statusWidgetHelper)
                SharedPreferenceManager.setPauseForCancelRecovery(
                    cookingVM.isPrimaryCavity,
                    "false"
                )
            }

            RecipeExecutionState.PAUSED_EXT -> {
                checkMeatProbeConnectionPostBlackout(CookingViewModelFactory.getInScopeViewModel())
                HMILogHelper.Logd(
                    TAG,
                    "PAUSED_EXT Cavity ${cookingVM.cavityName.value} RecipeExecutionState = $recipeExecutionState"
                )
                if (SharedPreferenceManager.getPauseForCancelRecovery(cookingVM.isPrimaryCavity)
                        ?.contentEquals(AppConstants.TRUE_CONSTANT) == true
                ) {
                    //not recovering any brownout/blackout if pause for cancel was pressed
                    HMILogHelper.Logd(
                        TAG,
                        "PAUSED_EXT Cavity ${cookingVM.cavityName.value} getPauseForCancelRecovery is TRUE, cancelling recipeExecutionViewModel"
                    )
                    cookingVM.recipeExecutionViewModel.cancel()
                }
            }


            else -> {
                val cancelled =
                    if (cookingVM.isPrimaryCavity) cancelledTime[0] else cancelledTime[1]
                if (cancelled > 0) {
                    HMILogHelper.Loge(
                        TAG,
                        " cavity ${cookingVM.cavityName.value} RecipeExecutionState = $recipeExecutionState with cancelledTime=$cancelled"
                    )
                    updateUIOnCancelEvent(cookingVM, statusWidget)
                    return
                } else if (recipeExecutionState != RecipeExecutionState.PAUSED && (cookingVM.recipeExecutionViewModel.cookTimerState.value != Timer.State.COMPLETED || cookingVM.recipeExecutionViewModel.cookTimerState.value != Timer.State.PAUSED)) {
                    HMILogHelper.Loge(
                        TAG,
                        " cavity ${cookingVM.cavityName.value} removeCallbacksAndMessages RecipeExecutionState=$recipeExecutionState with cookTimerState ${cookingVM.recipeExecutionViewModel.cookTimerState.value}"
                    )
                    getCavityHandler(if (cookingVM.isPrimaryCavity) 1 else 2).removeCallbacksAndMessages(
                        null
                    )
                    val recipeName =
                        CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
                    updateRecipeModeTextStyle(
                        statusWidget.statusWidgetHelper.tvRecipeWithTemperature(),
                        recipeName,
                        true
                    )
                }
            }
        }
        if (cookingVM.isPrimaryCavity) cancelledTime[0] = 0 else cancelledTime[1] = 0
    }

    //handler for upper oven cavity
    private val handlerUpperCavity = Handler(Looper.getMainLooper())

    //handler for lower oven cavity
    private val handlerLowerCavity = Handler(Looper.getMainLooper())

    /**
     * gives the static instance of handler based on cavity selection
     */
    protected fun getCavityHandler(whichCavity: Int): Handler {
        return if (whichCavity == 2) handlerLowerCavity
        else handlerUpperCavity
    }

    /**
     * update recipe name mode text view with style
     */
    private fun updateRecipeModeTextStyle(
        tvRecipe: ResourceTextView?,
        text: String?,
        isWhite: Boolean,
    ) {
        CommonAnimationUtils.setFadeOutViewAnimation(tvRecipe as View, 200)
        tvRecipe.text = text
        CommonAnimationUtils.setFadeInViewAnimation(tvRecipe as View, 200)
        if (isWhite) tvRecipe.setTextAppearance(R.style.StyleCookingStatusWidgetTextView)
        else tvRecipe.setTextAppearance(R.style.StylePreheatReadyStatusWidgetTextView)
    }

    /**
     * Runnable to change the textview back and forth between oven ready and recipe name with temperature
     */
    private fun runnableOvenReady(
        tvRecipeWithTemperature: ResourceTextView?,
        cavityPosition: Int,
        statusWidgetHelper: AbstractStatusWidgetHelper,
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            if (tvRecipeWithTemperature == null) return@Runnable
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            HMILogHelper.Logd(
                TAG,
                "tvRecipeWithTemperature cavity= $cavityPosition text=" + tvRecipeWithTemperature.text
            )
            var modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
            val handler = getCavityHandler(cavityPosition)
            val currentTimerState = cookingVM.recipeExecutionViewModel.cookTimerState.value
            val ovenReadyTime = cookingVM.recipeExecutionViewModel.readySincePreheatTime.value ?: 0
            if(currentTimerState == Timer.State.PAUSED) {
                HMILogHelper.Logd(
                    "$TAG runnable",
                    "${cookingVM.cavityName.value} currentTimerState=PAUSED AND setting START TIMER text ovenReadyTime $ovenReadyTime"
                )
                statusWidgetHelper.tvSetCookTime()?.text = if((cookingVM.recipeExecutionViewModel.cookTime.value
                        ?: 0) > 0
                )getString(R.string.text_button_start_timer) else getString(R.string.text_button_set_cook_time)

            }
            if (ovenReadyTime > resources.getInteger(R.integer.duration_status_mode_text_oven_ready_2_min) || (currentTimerState == Timer.State.RUNNING || currentTimerState == Timer.State.COMPLETED)) {
                if (cookingVM.isOfTypeOven && currentTimerState == Timer.State.PAUSED && cookingVM.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.COOKING){
                    HMILogHelper.Logd(tag, "executeOvenStartTimerRunnable from runnableOvenReady  as currentTimerState PAUSED and recipeCookingState COOKING")
                    handler.removeCallbacksAndMessages(null)
                    executeOvenStartTimerRunnable(cookingVM, tvRecipeWithTemperature, statusWidgetHelper)
                    return@Runnable
                }
                if (currentTimerState == Timer.State.RUNNING){
                    HMILogHelper.Logd(tag, "executeReadyAtTimeRunnable from runnableOvenReady as currentTimerState RUNNING")
                    executeReadyAtTimeRunnable(cookingVM, statusWidgetHelper)
                    return@Runnable
                }
                //escape condition for time based preheat recipes, preventing to kill oven ready state when cookTimerState flips to COMPLETED wait for it to be PAUSED
                if(!CookingAppUtils.isTimePreheatJustCompleted(cookingVM)){
                    HMILogHelper.Logd("$TAG runnable","existing oven ready RUNNABLE and setting text as $modeText due to $ovenReadyTime or Door activity")
                    handler.removeCallbacksAndMessages(null)
                    updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                    return@Runnable
                }
            }

            if (tvRecipeWithTemperature.text.contentEquals(
                    tvRecipeWithTemperature.context.getString(
                        R.string.text_header_oven_ready,
                        tvRecipeWithTemperature.context.getString(
                            if (cookingVM.isOfTypeOven) R.string.cavity_selection_oven else R.string.microwave
                        )
                    )
                )
            ) {
                modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
            } else {
                modeText = getString(
                    R.string.text_header_oven_ready, getString(
                        if (cookingVM.isOfTypeOven) R.string.cavity_selection_oven else R.string.microwave
                    )
                )
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, false)
            }
            statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.GONE
            handler.postDelayed(
                runnableOvenReady(
                    tvRecipeWithTemperature,
                    cavityPosition,
                    statusWidgetHelper//every 3 seconds there will be another callback to switch text from Oven Ready to temperature value
                ),
                tvRecipeWithTemperature.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * Runnable to change the textview back and forth between oven cooling and recipe name with oven display temperature / oven target temperature
     */
    private fun runnableOvenCoolingInHotCavity(
        statusWidgetHelper: AbstractStatusWidgetHelper?,
        cavityPosition: Int
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            val tvRecipeWithTemperature =
                statusWidgetHelper?.tvRecipeWithTemperature() ?: return@Runnable
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            HMILogHelper.Logd(
                TAG,
                "tvRecipeWithTemperature cavity= $cavityPosition text=" + tvRecipeWithTemperature.text
            )
            val modeText: String
            val handler = getCavityHandler(cavityPosition)
            val currentTimerState = cookingVM.recipeExecutionViewModel.cookTimerState.value
            if (CookingAppUtils.isCavityCooled(cookingVM) || currentTimerState == Timer.State.COMPLETED) {
                statusWidgetHelper.updateRecipeAllowedInHotCavity(false)
                modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
                HMILogHelper.Logd(
                    "$TAG runnable",
                    "existing oven cooling RUNNABLE and setting text as $modeText due to $currentTimerState"
                )
                handler.removeCallbacksAndMessages(null)
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.GONE
                val cookTimerState = cookingVM.recipeExecutionViewModel.cookTimerState.value
                val readySincePreheatTimerState =
                    cookingVM.recipeExecutionViewModel.readySincePreheatTimerState.value

                if (cookTimerState == Timer.State.RUNNING && cookingVM.recipeExecutionViewModel.recipeCookingState.value?.equals(
                        RecipeCookingState.COOKING
                    ) == true
                ) {
                    if (cookingVM.recipeExecutionViewModel.currentStep?.name?.text.equals(
                            AppConstants.BROWNING_CONSTANT
                        ) ||
                        cookingVM.recipeExecutionViewModel.currentStep?.name?.text.equals(
                            AppConstants.ADD_BROWNING_CONSTANT
                        )
                    ) {
                        return@Runnable
                    }
                    executeReadyAtTimeRunnable(
                        cookingVM, statusWidgetHelper
                    )
                } else if (readySincePreheatTimerState == Timer.State.RUNNING) {
                    cookingVM.doorState.value?.let {
                        executeOvenReadyRunnable(
                            cookingVM,
                            statusWidgetHelper.tvRecipeWithTemperature(),
                            statusWidgetHelper
                        )
                    }
                }
                return@Runnable
            }

            if (tvRecipeWithTemperature.text.contentEquals(
                    tvRecipeWithTemperature.context.getString(
                        R.string.clock_text_oven_cooling,
                        tvRecipeWithTemperature.context.getString(
                            if (cookingVM.isOfTypeOven) R.string.cavity_selection_oven else R.string.microwave
                        )
                    )
                )
            ) {
                val newModeText = buildString {
                    append(
                        getRecipeModeWithTemperatureAsStringForHotTemperature(
                            tvRecipeWithTemperature.context,
                            getRecipeNameText(
                                tvRecipeWithTemperature.context,
                                cookingVM.recipeExecutionViewModel.recipeName.value?.toString()
                                    ?: EMPTY_STRING
                            ),
                            cookingVM.ovenTemperature.value,
                            cookingVM
                        )
                    )

                    append(
                        getRecipeModeWithTemperatureAsString(
                            tvRecipeWithTemperature.context,
                            EMPTY_STRING,
                            cookingVM.recipeExecutionViewModel.targetTemperature.value,
                            cookingVM
                        )
                    )
                }
                updateRecipeModeTextStyle(tvRecipeWithTemperature, newModeText, true)
                statusWidgetHelper.getTemperatureRampIcon()?.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(), R.drawable.tc_status_temperature_ramp_down
                    )
                )
                statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.VISIBLE
            } else {
                modeText = tvRecipeWithTemperature.context.getString(
                    R.string.clock_text_oven_cooling,
                    getString(if (cookingVM.isOfTypeOven) R.string.cavity_selection_oven else R.string.microwave )
                )
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, false)
                statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.GONE
            }
            handler.postDelayed(
                runnableOvenCoolingInHotCavity(
                    statusWidgetHelper,
                    cavityPosition//every 3 seconds there will be another callback to switch text from Oven Ready to temperature value
                ),
                tvRecipeWithTemperature.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * Runnable to change the textview back and forth between from Preheating to target recipe temperature to current oven cavity temperature
     */
    private fun runnableOvenPreheat(
        tvPreHeat: ResourceTextView?,
        cavityPosition: Int,
        statusWidgetHelper: AbstractStatusWidgetHelper,
        isFirstTime: Boolean
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            if (tvPreHeat == null) return@Runnable
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            HMILogHelper.Logd("$TAG preHeat", "tvPreHeat=" + tvPreHeat.text)
            var modeText: String
            if(isFirstTime){
                modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
                updateRecipeModeTextStyle(tvPreHeat, modeText, true)
            }else {
                if(CookingAppUtils.getResIdFromResName(
                        this.requireContext(),
                        AppConstants.TEXT_PREHEATING_TYPE + cookingVM.recipeExecutionViewModel.recipeName.value.toString(),
                        AppConstants.RESOURCE_TYPE_STRING
                    ) != R.string.weMissedThat){
                    //special case for time based preheat recipe, as need to show Preparing Oven with  timeRemaining as preheat timer
                    val preparingText = tvPreHeat.context.getString(R.string.text_preheat_type_freshPizza).split(AppConstants.VERTICAL_BAR)[0]
                    HMILogHelper.Logd(tag, "preparingText $preparingText")
                    if (tvPreHeat.text?.startsWith(preparingText) == true || (cookingVM.recipeExecutionViewModel.remainingCookTime.value?: 0) <= 60L) {
                        modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
                        if(tvPreHeat.text?.contentEquals(modeText) == false) updateRecipeModeTextStyle(tvPreHeat, modeText, true)
                    } else {
                        modeText = tvPreHeat.context.getString(R.string.text_preheat_type_freshPizza, CookingAppUtils.displayCookTimeToUser(requireContext(), cookingVM.recipeExecutionViewModel.remainingCookTime.value, false, arrayOf(R.string.text_tile_preview_display_hour_lower, R.string.text_tile_preview_display_min_lower, R.string.text_tile_preview_display_sec_lower)))
                        updateRecipeModeTextStyle(tvPreHeat, modeText, false)
                    }
                }else {
                    if (tvPreHeat.text?.startsWith(tvPreHeat.context.getString(R.string.text_prehating_running)) == true) {
                        modeText = getRecipeModeWithTemperatureAsString(
                            tvPreHeat.context,
                            EMPTY_STRING,
                            cookingVM.ovenDisplayTemperature.value,
                            cookingVM
                        ) ?: EMPTY_STRING
                        updateRecipeModeTextStyle(tvPreHeat, modeText, true)
                    } else {
                        modeText = getRecipeModeWithTemperatureAsString(
                            tvPreHeat.context,
                            tvPreHeat.context.getString(R.string.text_prehating_running),
                            cookingVM.recipeExecutionViewModel.targetTemperature.value,
                            cookingVM
                        ) ?: EMPTY_STRING
                        updateRecipeModeTextStyle(tvPreHeat, modeText, false)
                    }
                }
            }
            HMILogHelper.Logd("$TAG preHeat", "switching tvPreHeat=" + tvPreHeat.text)
            val handler = getCavityHandler(cavityPosition)
            if (cookingVM.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.COOKING && cookingVM.recipeExecutionViewModel.cookTimerState.value == Timer.State.RUNNING) {
                HMILogHelper.Logd(tag, "${cookingVM.cavityName.value} has recipeCookingState COOKING and cookTimerState=RUNNING, so executing executeReadyAtTimeRunnable")
                executeReadyAtTimeRunnable(cookingVM, statusWidgetHelper)
                return@Runnable
            }
            if (cookingVM.recipeExecutionViewModel.recipeCookingState.value != RecipeCookingState.PREHEATING) {
                modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
                handler.removeCallbacksAndMessages(null)
                updateRecipeModeTextStyle(tvPreHeat, modeText, true)
                return@Runnable
            }
            handler.postDelayed(
                runnableOvenPreheat(
                    tvPreHeat,
                    cavityPosition,
                    statusWidgetHelper,//every 3 seconds there will be another callback to switch text from Oven Ready to temperature value
                    false
                ),
                tvPreHeat.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * update progressbar based on probeTemperature current value and the value set to reach target
     *
     * @param cookingVM
     * @param statusWidgetHelper
     */
    private fun updateProbeProgressBar(
        cookingVM: CookingViewModel,
        statusWidgetHelper: AbstractStatusWidgetHelper,
    ) {
        if (cookingVM.recipeExecutionViewModel.targetMeatProbeTemperatureReached.value == true) {
            statusWidgetHelper.getStatusProgressBar()?.progress = AppConstants.MAX_PROGRESS_VALUE
            return
        }
        val meatProbeTargetValue =
            cookingVM.recipeExecutionViewModel?.meatProbeTargetTemperature?.value
        val meatProbeCurrentValue = cookingVM.meatProbeTemperature?.value
        if (meatProbeTargetValue != null && meatProbeTargetValue > 0 && meatProbeCurrentValue != null && meatProbeCurrentValue > 0) {
            val meatProbeProgress = meatProbeCurrentValue.times(AppConstants.MAX_PROGRESS_VALUE)
                .div(meatProbeTargetValue)
            statusWidgetHelper.getStatusProgressBar()?.progress = meatProbeProgress
        }
    }


    /**
     * This method will post the probe complete message on the cavity handler and also the timeout to cancel and move to clock or status screen for that particular recipe
     */
    fun executeProbeCompleteRunnable(
        cavityPosition: Int,
        tvRecipe: ResourceTextView?,
        statusWidgetHelper: AbstractStatusWidgetHelper,
    ) {
        if (statusWidgetHelper.tvResumeCooking()?.isVisible == true) {
            HMILogHelper.Loge(
                tag,
                "cavityPosition $cavityPosition has tvResumeCooking shown, so skipping executeProbeCompleteRunnable"
            )
            return
        }
        HMILogHelper.Logd(tag, "HANDLER: cavityPosition $cavityPosition executeProbeCompleteRunnable")
        val handler = getCavityHandler(cavityPosition)
        handler.removeCallbacksAndMessages(null)
        makeIndefiniteProgressBarVisible(statusWidgetHelper.getStatusProgressBar(),statusWidgetHelper.getProgressbarInfinite(),true)
        manageProbeExtendedCookTimeText(
            statusWidgetHelper,
            if(cavityPosition == 1) CookingViewModelFactory.getPrimaryCavityViewModel() else CookingViewModelFactory.getSecondaryCavityViewModel()
        )
        handler.post(
            runnableProbeTimeComplete(
                tvRecipe, cavityPosition
            )
        )
    }


    /**
     * Runnable to change the textview back and forth between Cool timer complete and Ready since time
     * use this only when probe state is Turning_Off
     */
    private fun runnableProbeTimeComplete(
        tvComplete: ResourceTextView?,
        cavityPosition: Int,
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            if (tvComplete == null) return@Runnable
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            val handler = getCavityHandler(cavityPosition)
            if (cookingVM.recipeExecutionViewModel.recipeCookingState.value != RecipeCookingState.TURNING_OFF) {
                handler.removeCallbacksAndMessages(null)
                return@Runnable
            }
            val readySinceMin = cookingVM.recipeExecutionViewModel.readySinceTime.value?.let {
                TimeUnit.SECONDS.toMinutes(it)
            }?.toInt()
            if (readySinceMin != null && readySinceMin >= AppConstants.RECIPE_TIMEOUT_COOKING_COMPLETE_10_MINUTES) {
                handler.removeCallbacksAndMessages(null)
                HMILogHelper.Loge(
                    "$TAG probeComplete",
                    "${cookingVM.cavityName.value} Cancelling recipeExecutionViewModel as timeout completed for $readySinceMin minutes"
                )
                cookingVM.recipeExecutionViewModel.cancel()
                return@Runnable
            }
            HMILogHelper.Logd(
                "$TAG probeTimeComplete",
                "tvComplete=" + tvComplete.text + "|| readySinceMin=$readySinceMin minutes"
            )

            val recipeName = getRecipeNameText(
                tvComplete.context,
                cookingVM.recipeExecutionViewModel.recipeName.value.toString()
            )

            var modeText =
                tvComplete.context.getString(R.string.text_recipe_completed_status, recipeName)
            if (tvComplete.text.contentEquals(modeText)) {
                modeText = tvComplete.context.getString(
                    R.string.text_complete_since_1_min,
                        if (readySinceMin == 0) cookingVM.recipeExecutionViewModel.readySinceTime.value?.let {
                            TimeUnit.SECONDS.toSeconds(it)
                        }?.toInt() else readySinceMin,
                        if (readySinceMin == 0) getString(R.string.text_label_sec) else getString(R.string.text_label_min)
                )
                updateRecipeModeTextStyle(tvComplete, modeText, false)
            } else {
                updateRecipeModeTextStyle(tvComplete, modeText, true)
            }
            HMILogHelper.Logd(
                "$TAG probeTimeComplete",
                "switching to tvComplete=" + tvComplete.text
            )
            handler.postDelayed(
                runnableProbeTimeComplete(
                    tvComplete,
                    cavityPosition//every 3 seconds there will be another callback to switch text from Oven Ready to temperature value
                ),
                tvComplete.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }


    /**
     * Runnable to change the textview back and forth between Cool timer complete and Ready since time
     * use this only when timer state is Completed
     */
    private fun runnableOvenCookTimeComplete(
        tvComplete: ResourceTextView?,
        cavityPosition: Int,
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            if (tvComplete == null) return@Runnable
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            val handler = getCavityHandler(cavityPosition)
            if (cookingVM.recipeExecutionViewModel.cookTimerState.value != Timer.State.COMPLETED) {
                if (cookingVM.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.RUNNING_EXT) {
                    handler.removeCallbacksAndMessages(null)
                    return@Runnable
                }
            }
            val readySinceMin = cookingVM.recipeExecutionViewModel.readySinceTime.value?.let {
                TimeUnit.SECONDS.toMinutes(it)
            }?.toInt()
            if (readySinceMin != null && readySinceMin >= AppConstants.RECIPE_TIMEOUT_COOKING_COMPLETE_10_MINUTES) {
                handler.removeCallbacksAndMessages(null)
                HMILogHelper.Loge(
                    "$TAG cookTimeComplete",
                    "${cookingVM.cavityName.value} Cancelling recipeExecutionViewModel as timeout completed for $readySinceMin minutes"
                )
                cookingVM.recipeExecutionViewModel.cancel()
                return@Runnable
            }
            HMILogHelper.Logd(
                "$TAG cookTimeComplete",
                "tvComplete=" + tvComplete.text + "|| readySinceMin=$readySinceMin minutes"
            )
            val recipeName = getRecipeNameText(
                tvComplete.context, cookingVM.recipeExecutionViewModel.recipeName.value.toString()
            )

            var modeText =
                tvComplete.context.getString(R.string.text_recipe_completed_status, recipeName)
            if (tvComplete.text.contentEquals(modeText)) {
                modeText = tvComplete.context.getString(
                    R.string.text_complete_since_1_min,
                    if (readySinceMin == 0) cookingVM.recipeExecutionViewModel.readySinceTime.value?.let {
                        TimeUnit.SECONDS.toSeconds(it)
                    }?.toInt() else readySinceMin,
                    if (readySinceMin == 0) getString(R.string.text_label_sec) else getString(R.string.text_label_min)
                )
                updateRecipeModeTextStyle(tvComplete, modeText, false)
            } else {
                updateRecipeModeTextStyle(tvComplete, modeText, true)
            }
            HMILogHelper.Logd("$TAG cookTimeComplete", "switching to tvComplete=" + tvComplete.text)
            handler.postDelayed(
                runnableOvenCookTimeComplete(
                    tvComplete,
                    cavityPosition//every 3 seconds there will be another callback to switch text from Oven Ready to temperature value
                ),
                tvComplete.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * Runnable to change the textview back and forth between from Press ‘Start Timer’ to begin to recipe name
     * use this only when timer state is paused (wait for preheat condition)
     */
    private fun runnableOvenStartTimer(
        tvRecipeWithTemperature: ResourceTextView?,
        cavityPosition: Int,
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            if (tvRecipeWithTemperature == null) return@Runnable
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            HMILogHelper.Logd(TAG, "tvRecipeWithTemperature=" + tvRecipeWithTemperature.text)
            var modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
            val handler = getCavityHandler(cavityPosition)
            val ovenStartTime = cookingVM.recipeExecutionViewModel.readySincePreheatTime.value ?: 0
            HMILogHelper.Logd("$TAG ovenStartTimer", "ovenStartTime=$ovenStartTime")
            //as user action is required to start the timer so do not go to Far status screen
            if (cookingVM.recipeExecutionViewModel.cookTimerState.value != Timer.State.PAUSED) {
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
                HMILogHelper.Logd(tag, "exiting ovenStartTimer as cookTimerState is not PAUSED but ${cookingVM.recipeExecutionViewModel.cookTimerState.value}")
                handler.removeCallbacksAndMessages(null)
                return@Runnable
            }
            if (ovenStartTime > resources.getInteger(R.integer.duration_status_mode_start_cook_timer_oven_ready_10_min) && cookingVM.recipeExecutionViewModel.cookTimerState.value == Timer.State.PAUSED) {
                HMILogHelper.Logd(tag, "Cancelling ${cookingVM.cavityName.value} as 10 minutes timeout ovenStartTime =${ovenStartTime} and cookTimerState is still PAUSED")
                cookingVM.recipeExecutionViewModel.cancel()
                return@Runnable
            }
            if (tvRecipeWithTemperature.text.contentEquals(
                    tvRecipeWithTemperature.context.getString(
                        R.string.text_start_timer_to_begin_status
                    )
                )
            ) {
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, true)
            } else {
                modeText =
                    tvRecipeWithTemperature.context.getString(R.string.text_start_timer_to_begin_status)
                updateRecipeModeTextStyle(tvRecipeWithTemperature, modeText, false)
            }
            handler.postDelayed(
                runnableOvenStartTimer(
                    tvRecipeWithTemperature,
                    cavityPosition//every 3 seconds there will be another callback to switch text from Oven Ready to temperature value
                ),
                tvRecipeWithTemperature.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * Runnable to change the textview back and forth between Recipe Cancelled and Resume in x Sec
     * use this only when cancel press key is detected
     */
    private fun runnableResumeCancelledCycle(
        tvCancelled: ResourceTextView?,
        cavityPosition: Int,
    ): Runnable {
        return Runnable {
            //Do something after 3 secs...
            if (tvCancelled == null) return@Runnable
            val cookingVM =
                if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
            val handler = getCavityHandler(cavityPosition)
            var recipeName = getRecipeNameText(
                tvCancelled.context,
                cookingVM.recipeExecutionViewModel.recipeName.value.toString()
            )

            if (cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING
                || cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.RUNNING_EXT
                || cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED
            ) {
                handler.removeCallbacksAndMessages(null)
                recipeName =
                    CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
                updateRecipeModeTextStyle(tvCancelled, recipeName, true)
                return@Runnable
            }
            val cancelRemainTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime())
                .toInt() - cancelledTime[cavityPosition - 1]
            HMILogHelper.Logd("$TAG cancel", "cancelledTime=$cancelRemainTime")
            if (cancelRemainTime >= AppConstants.RECIPE_CANCEL_DURATION_15_SEC) {
                HMILogHelper.Logd(
                    "$TAG cancel",
                    "cancelled recipe for ${cookingVM.cavityName.value} as cancelRemainTime > 15 sec"
                )
                cookingVM.recipeExecutionViewModel.cancel()
                handler.removeCallbacksAndMessages(null)
                return@Runnable
            }
            SharedPreferenceManager.setPauseForCancelRecovery(cookingVM.isPrimaryCavity, "true")
            val modeText = tvCancelled.context.getString(R.string.text_cancelled_cycle_running, recipeName)
            if(!tvCancelled.text.contentEquals(modeText))
                updateRecipeModeTextStyle(tvCancelled, modeText, true)
            HMILogHelper.Logd("$TAG cancel", "switching to tvCancelled=" + tvCancelled.text)
            handler.postDelayed(
                runnableResumeCancelledCycle(
                    tvCancelled,
                    cavityPosition//every 3 seconds there will be another callback to switch text from Oven Ready to temperature value
                ),
                tvCancelled.context.resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                    .toLong()
            )
        }
    }

    /**
     * Utility method to show/hide or swap indefinite image view wth progressbar
     * @param isVisible true if indefinite progressbar image needs to show false if progressbar need to show
     */
    private fun makeIndefiniteProgressBarVisible(
        progressBar: ProgressBar?,
        imageView: LottieAnimationView?,
        isVisible: Boolean,
    ) {
        if (isVisible) {
            progressBar?.visibility = View.GONE
            imageView?.visibility = View.VISIBLE
            setAnimationForIndefiniteProgressBar(imageView)
        } else {
            progressBar?.visibility = View.VISIBLE
            imageView?.visibility = View.GONE
        }
    }

    /**
     * Method to set the animation for indefinite progress bar
     */
    private fun setAnimationForIndefiniteProgressBar(locKAnimationView: LottieAnimationView?) {
        CommonAnimationUtils.playLottieAnimation(
            locKAnimationView,
            R.raw.loop_fill_progress_bar_amber_white
        )
    }

    /**
     * responsible to update cook time text in status widget
     */
    protected fun manageCookTimeRemainingText(
        statusWidget: CookingStatusWidget,
        cookingViewModel: CookingViewModel,
        remainingTime: Long,
        cookTimerState: Timer.State,
        recipeCookingState: RecipeCookingState,
    ) {
        val tvSetCookTime = statusWidget.statusWidgetHelper.tvSetCookTime()
        val tvCookingEtr = statusWidget.statusWidgetHelper.tvCookTimeRemaining()
        when (cookTimerState) {
            Timer.State.IDLE -> {
                if ((recipeCookingState == RecipeCookingState.SENSING)) {
                    tvCookingEtr?.visibility = View.VISIBLE
                    tvCookingEtr?.text = getString(R.string.text_running_sensing)
                    return
                }
                tvCookingEtr?.visibility = View.INVISIBLE
                tvSetCookTime?.text = getString(R.string.text_button_set_cook_time)
                tvSetCookTime?.setTextAppearance(R.style.StatusWidgetTitleTextView)
            }

            else -> {
                if ((recipeCookingState == RecipeCookingState.SENSING)) {
                    tvCookingEtr?.visibility = View.VISIBLE
                    tvCookingEtr?.text = getString(R.string.text_running_sensing)
                    updateCookTimeWidgetDynamically(statusWidget.statusWidgetHelper, true)
                    return
                }
                tvCookingEtr?.visibility = View.VISIBLE
                tvCookingEtr?.setTextAppearance(R.style.StyleCookingStatusWidgetTextView)
                if(CookingAppUtils.isTimePreheatRunning(cookingViewModel)){
                    val setCookTimed = cookingViewModel.recipeExecutionViewModel.cookTime.value
                    HMILogHelper.Logd(
                        tag,
                        "TIME PREHEAT, ${cookingViewModel.cavityName.value} has cookTimerState RUNNING, setCookTimed=$setCookTimed recipeCookingState PREHEATING"
                    )
                    if(setCookTimed == 0L) {
                        tvSetCookTime?.text = getString(R.string.text_button_set_cook_time)
                        return
                    }
                    tvCookingEtr?.text = CookingAppUtils.spannableETRRunning(
                        context, setCookTimed?.toInt()?:0
                    )
                }else {
                    tvCookingEtr?.text = CookingAppUtils.spannableETRRunning(
                        context, remainingTime.toInt()
                    )
                }
                if (cookingViewModel.isOfTypeOven && cookTimerState == Timer.State.PAUSED && (recipeCookingState == RecipeCookingState.PREHEATING || recipeCookingState == RecipeCookingState.COOKING)){
                    tvSetCookTime?.text = getString(R.string.text_button_start_timer)
                    HMILogHelper.Logd(
                        tag,
                        "${cookingViewModel.cavityName.value} has cookTimerState PAUSED, recipeCookingState $recipeCookingState, changing tvSetCookTime to text_button_start_timer"
                    )
                    if (recipeCookingState == RecipeCookingState.COOKING && cookingViewModel.recipeExecutionViewModel.readySincePreheatTimerState.value == Timer.State.RUNNING && cookingViewModel.recipeExecutionViewModel.cookTime.value != 0L) {
                        HMILogHelper.Logd(
                            tag,
                            "${cookingViewModel.cavityName.value} has cookTimerState PAUSED, recipeCookingState $recipeCookingState, readySincePreheatTimerState is RUNNING so executeOvenStartTimerRunnable"
                        )
                        executeOvenStartTimerRunnable(
                            cookingViewModel,
                            statusWidget.statusWidgetHelper.tvRecipeWithTemperature(),
                            statusWidget.statusWidgetHelper
                        )
                    }
                } else if (cookingViewModel.isOfTypeMicrowaveOven && !cookingViewModel.recipeExecutionViewModel.isMagnetronUsed && cookTimerState == Timer.State.PAUSED && (recipeCookingState == RecipeCookingState.PREHEATING || recipeCookingState == RecipeCookingState.COOKING)) {
                    tvSetCookTime?.text = getString(R.string.text_button_start_timer)
                    HMILogHelper.Logd(
                        tag,
                        "${cookingViewModel.cavityName.value} has cookTimerState PAUSED, isMagnetronUsed=false recipeCookingState PREHEATING, channing changing to text_button_start_timer"
                    )
                }
                //MAF_2406:  The system should not displays the +30 sec option to extend cook time at the end of the popcorn cycle
                else if (cookingViewModel.recipeExecutionViewModel.isSensingRecipe && (!cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) && cookTimerState == Timer.State.COMPLETED && (!CookingAppUtils.isRecipeOptionAvailable(
                        cookingViewModel.recipeExecutionViewModel,
                        RecipeOptions.COOK_TIME
                    ))
                ) {
                    updateCookTimeWidgetDynamically(statusWidget.statusWidgetHelper, true)
                } else {
                    val setCookedTime = if(CookingAppUtils.isTimePreheatRunning(cookingViewModel))cookingViewModel.recipeExecutionViewModel.cookTime.value else cookingViewModel.recipeExecutionViewModel.remainingCookTime.value
                    HMILogHelper.Logd(tag, "${cookingViewModel.cavityName.value} has cookTimerState $cookTimerState setCookedTime $setCookedTime" )
                    tvSetCookTime?.text =
                        if ((setCookedTime?: 0) > AppConstants.ADD_COOK_TIME_TEN_MINUTE
                        ) getString(
                            R.string.text_5_min_button,
                            resources.getInteger(R.integer.five_minute_cook_time),
                            getString(R.string.text_label_MIN)
                        ) else provideVisualCookMinTime(
                            cookingViewModel
                        )
                }
                val addition = provideMinCookTimeIncrement(cookingViewModel)
                val setCookTime = cookingViewModel.recipeExecutionViewModel.cookTime.value
                if (setCookTime != null) {
                    //MAF_2750: Defect fixed where System is not showing the +30 sec extended cook time option after sensing phase completes.
                        if (isValidCookTime(cookingViewModel, addition)) {
                            if (cookingViewModel.recipeExecutionViewModel.isSensingRecipe && (!cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe)) {
                                updateCookTimeWidgetDynamically(statusWidget.statusWidgetHelper, false) }
                            tvSetCookTime?.setTextAppearance(R.style.StatusWidgetTitleTextView)
                        } else {
                            //MAF_2752: Defect fixed where Start Timer option is disabled when user set the MAX cook time (12Hr). Actually we need to make this disappearance only for +30SEC, +5MIN
                            if(tvSetCookTime?.text?.equals(getString(R.string.text_button_start_timer)) == false)
                            tvSetCookTime.setTextAppearance(R.style.StatusWidgetTitleDisableTextView)
                        }
                }
                if (((cookingViewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                        AppConstants.BROWNING_CONSTANT
                    )) || (cookingViewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                        AppConstants.ADD_BROWNING_CONSTANT
                    )))
                ) {
                    tvSetCookTime?.setTextAppearance(R.style.StatusWidgetTitleDisableTextView)
                }
            }
        }
    }

    /**
     * update Cook Time View dynamically based on whether current recipe running is allowing to add cook time or not
     * mostly called when Cook Time is not editable ex. probe recipe but when recipe completes user can prolong the recipe by simply adding cook time
     *
     * @param statusWidgetHelper widget provider for a particular cavity
     * @param hideCookTime true to hide and false to make it visible
     */
    private fun updateCookTimeWidgetDynamically(statusWidgetHelper: AbstractStatusWidgetHelper, hideCookTime: Boolean) {
        if(statusWidgetHelper.isCookTimeNotAllowed() != hideCookTime) {
            statusWidgetHelper.shouldShowCookTimeForDividerView(hideCookTime)
            //knob rotation also have to adjust because now there will be an additional view to rotate to
            knobRotationItems = initKnobItems()
            HMILogHelper.Logd(
                tag,
                "KNOB: cook time view adjustment knobRotationItems size= ${knobRotationItems?.size} hideCookTime $hideCookTime"
            )
        }else{
            HMILogHelper.Loge(tag, "updateCookTimeWidgetDynamically should not called multiple times, area of improvement because updating view should only be doing once")
        }
    }

    /**
     * Method to validate whether the cook timer is in range
     *
     * @return true if it is a valid cook time
     */
    private fun isValidCookTime(cookingVM: CookingViewModel, timeAddition: Long): Boolean {
        val cookTimeOptionRange =
            cookingVM.recipeExecutionViewModel?.cookTimeOption?.value as IntegerRange?
        val setCookedTime = if(CookingAppUtils.isTimePreheatRunning(cookingVM))cookingVM.recipeExecutionViewModel.cookTime.value else cookingVM.recipeExecutionViewModel.remainingCookTime.value
        val totalCookTime = setCookedTime?.plus(timeAddition)?:0
        return if (cookTimeOptionRange != null) {
            if (cookTimeOptionRange.max == 0) {
                val defaultMaxTime = if (cookingVM.recipeExecutionViewModel.isMagnetronUsed)
                    AppConstants.DEFAULT_MAX_COOK_TIME_IN_MAGNETRON
                else
                    AppConstants.DEFAULT_MAX_COOK_TIME
                cookTimeOptionRange.setMax(defaultMaxTime.toDouble())
            }
            totalCookTime >= cookTimeOptionRange.min && totalCookTime <= cookTimeOptionRange.max
        } else {
            false
        }
    }

    /**
     * responsible to recipe name with oven display temperature
     */
    protected open fun updateRecipeNameWIthRecipeState(
        context: Context?,
        cookingVM: CookingViewModel,
        recipeCookingState: RecipeCookingState?,
        statusWidgetHelper: AbstractStatusWidgetHelper?,
        rawRecipeName: String?,
        isDoorOpened: Boolean,
        recipeExecutionState: RecipeExecutionState,
    ) {
        if (statusWidgetHelper == null) return
        if (context == null) return
        if (rawRecipeName == null) return
        val modeText = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
        if (cookingVM.recipeExecutionViewModel.isNotRunning || cancelledTime[if(cookingVM.isPrimaryCavity) 0 else 1] > 0) return
        if (statusWidgetHelper.tvResumeCooking()?.isVisible == true && cookingVM.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED) {
            HMILogHelper.Logd(
                tag,
                "${cookingVM.cavityName.value} has tvResumeCooking VISIBLE and RecipeExecutionState=PAUSED so Skipping updateRecipeNameWIthRecipeState"
            )
            return
        }
        updateRecipeModeTextStyle(statusWidgetHelper.tvRecipeWithTemperature(), modeText, true)
        when(recipeCookingState){
            RecipeCookingState.SENSING -> {
                executeKeepDoorClosedRunnable(
                    cookingVM,
                    statusWidgetHelper.tvRecipeWithTemperature(), statusWidgetHelper.tvCookTimeRemaining()
                )
                return
            }
            RecipeCookingState.PREHEATING -> {
                if (cookingVM.recipeExecutionViewModel.isVirtualchefBasedRecipe) {
                    executeReadyAtTimeRunnable(
                        cookingVM,
                        statusWidgetHelper
                    )
                } else {
                    val cookTimerState = cookingVM.recipeExecutionViewModel.cookTimerState.value
                    if (!CookingAppUtils.isTimeBasedPreheatRecipe(cookingVM) && CookingAppUtils.isRecipeAssisted(
                            cookingVM.recipeExecutionViewModel.recipeName.value,
                            cookingVM.cavityName.value
                        ) && cookTimerState == Timer.State.PAUSED && (cookingVM.recipeExecutionViewModel.cookTime.value
                            ?: 0) > 0L
                    ) {
                        HMILogHelper.Logd(
                            tag,
                            "door assisted, $rawRecipeName is not time based preheat recipe and RecipeCookingState PREHEATING AND cookTimerState=$cookTimerState"
                        )
                        if (isDoorOpened) {
                            isDoorOpenClosedInPreHeat[if (cookingVM.isPrimaryCavity) 0 else 1] =
                                false
                        } else {
                            if (isDoorOpenClosedInPreHeat.any { !it }) {
                                cookingVM.recipeExecutionViewModel.startCookTimer()
                            }
                            isDoorOpenClosedInPreHeat[if (cookingVM.isPrimaryCavity) 0 else 1] =
                                true
                        }
                    }
                    executeOvenPreheatRunnable(
                        if (cookingVM.isPrimaryCavity) 1 else 2,
                        statusWidgetHelper.tvRecipeWithTemperature(),
                        statusWidgetHelper
                    )
                    return
                }
            }

            RecipeCookingState.COOKING -> {
                if (cookingVM.recipeExecutionViewModel.isVirtualchefBasedRecipe) {
                    executeReadyAtTimeRunnable(
                        cookingVM,
                        statusWidgetHelper
                    )
                } else if (statusWidgetHelper.isRecipeAllowedInHotCavity()) {
                    HMILogHelper.Logd(
                        tag, "Hot cavity is allowed, displaying oven cooling runnable"
                    )
                    if (isCavityHot(cookingVM)) {
                        executeOvenCoolingForHotCavityRunnable(cookingVM, statusWidgetHelper)
                    } else {
                        statusWidgetHelper.updateRecipeAllowedInHotCavity(false)
                        HMILogHelper.Logd(
                            tag, "Cavity is not hot, not showing oven cooling runnable"
                        )
                        statusWidgetHelper.tvRecipeWithTemperature()?.text = modeText
                    }
                }
                return
            }
            else -> {
                HMILogHelper.Logd(tag, "${cookingVM.cavityName.value} recipeCookingState $recipeCookingState")
                statusWidgetHelper.tvRecipeWithTemperature()?.text = modeText
            }
        }
        statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.GONE
    }

    /**
     * responsible to recipe name with oven display temperature
     */
    @Suppress("UNUSED_PARAMETER")
    protected fun updateRecipeNameWIthTemperature(
        context: Context?,
        cookingVM: CookingViewModel,
        recipeCookingState: RecipeCookingState?,
        statusWidgetHelper: AbstractStatusWidgetHelper?,
        rawRecipeName: String?,
        ovenDisplayTemperature: Int?,
        targetTemperature: Int?,
        recipeExecutionState: RecipeExecutionState,
    ) {
        if (statusWidgetHelper == null) return
        if (context == null) return
        if (rawRecipeName == null) return
        if (ovenDisplayTemperature == null) return
        if (targetTemperature == null) return
        val recipeName = CookingAppUtils.getRecipeNameWithParameters(requireContext(), cookingVM)
        statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.GONE
        if (cookingVM.recipeExecutionViewModel.isNotRunning || cancelledTime[if(cookingVM.isPrimaryCavity) 0 else 1] > 0) return
        if (recipeCookingState == RecipeCookingState.COOKING && CookingAppUtils.isRequiredTargetAvailable(
                cookingVM,
                RecipeOptions.TARGET_TEMPERATURE
            )
        ) {
            if (cookingVM.recipeExecutionViewModel.currentStep?.name?.text.equals(AppConstants.BROWNING_CONSTANT) ||
                cookingVM.recipeExecutionViewModel.currentStep?.name?.text.equals(AppConstants.ADD_BROWNING_CONSTANT)
            ) {
                val modeText = resources.getString(R.string.weMissedThat) +
                        EMPTY_SPACE + resources.getString(R.string.text_high)
                updateRecipeModeTextStyle(
                    statusWidgetHelper.tvRecipeWithTemperature(), modeText, true
                )
                return
            }
            if (!statusWidgetHelper.isRecipeAllowedInHotCavity()) {
                updateRecipeModeTextStyle(
                    statusWidgetHelper.tvRecipeWithTemperature(), recipeName, true
                )
            }
            if (ovenDisplayTemperature < targetTemperature) {// show ramp up
                statusWidgetHelper.getTemperatureRampIcon()?.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(), R.drawable.tc_status_temperature_ramp_up
                    )
                )
                statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.VISIBLE
                return
            } else if (ovenDisplayTemperature > targetTemperature) {//show ramp down
                statusWidgetHelper.getTemperatureRampIcon()?.setImageDrawable(
                    AppCompatResources.getDrawable(
                        requireContext(), R.drawable.tc_status_temperature_ramp_down
                    )
                )
                statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.VISIBLE
                return
            }
        }
    }

    /**
     * This method will post the cook timer complete message on the cavity handler and also the timeout to cancel and move to clock or status screen for that particular recipe
     */
    private fun executeOvenPreheatRunnable(
        cavityPosition: Int,
        tvRecipe: ResourceTextView?,
        statusWidgetHelper: AbstractStatusWidgetHelper,
    ) {
        if (statusWidgetHelper.tvResumeCooking()?.isVisible == true) {
            HMILogHelper.Loge(
                tag,
                "cavityPosition $cavityPosition has tvResumeCooking shown, so skipping executeOvenPreheatRunnable"
            )
            return
        }
        HMILogHelper.Logd(tag, "HANDLER: cavityPosition $cavityPosition executeOvenPreheatRunnable")
        val handler = getCavityHandler(cavityPosition)
        handler.removeCallbacksAndMessages(null)
        handler.post(
            runnableOvenPreheat(
                tvRecipe, cavityPosition, statusWidgetHelper, true
            )
        )
    }

    /**
     * This method will post the cook timer complete message on the cavity handler and also the timeout to cancel and move to clock or status screen for that particular recipe
     */
    private fun executeTimerCompleteRunnable(
        cookingVM: CookingViewModel,
        tvRecipe: ResourceTextView?,
        statusWidgetHelper: AbstractStatusWidgetHelper,
    ) {
        val cavityPosition: Int = if(cookingVM.isPrimaryCavity) 1 else 2
        if (statusWidgetHelper.tvResumeCooking()?.isVisible == true) {
            HMILogHelper.Loge(
                tag,
                "cavityPosition $cavityPosition has tvResumeCooking shown, so skipping executeTimerCompleteRunnable"
            )
            return
        }
        HMILogHelper.Logd(tag, "HANDLER: cavityPosition $cavityPosition executeTimerCompleteRunnable")
        val handler = getCavityHandler(cavityPosition)
        handler.removeCallbacksAndMessages(null)
        makeIndefiniteProgressBarVisible(statusWidgetHelper.getStatusProgressBar(),statusWidgetHelper.getProgressbarInfinite(),true)
        CookingAppUtils.setRunningRecipeIsQuickStart(false)
        if (isSteamCleanCompleted(cavityPosition)) {
            updateSteamCleanCompleteRecipeName(cavityPosition, tvRecipe)
            startSteamCompleteRunnable(cavityPosition)
        } else {
            handler.post(
                runnableOvenCookTimeComplete(
                    tvRecipe, cavityPosition
                )
            )
            if (isRecipeCompleteBeforeKitchenTimer()) timeoutViewModel.setTimeout(resources.getInteger(R.integer.integer_range_10))
        }
        //M63KA-2436 - cook time +2 +5 is not available in recipe which has non editable cook time such as convect slow roast, so update the variable to track until if any API available from SDK to know that recipe has already finished cooking
        if(!CookingAppUtils.isCookTimeOptionAvailable(cookingVM) && cookingVM.recipeExecutionViewModel?.nonEditableOptions?.value?.containsKey(RecipeOptions.COOK_TIME) == true && !CookingAppUtils.isRecipeAssisted(cookingVM.recipeExecutionViewModel.recipeName.value, cookingVM.cavityName.value)){
            isRecipeCompleteForNonEditableCookTime[cavityPosition - 1] = true
            HMILogHelper.Logd(tag, "make cook time available for extended cooking, isRecipeCompleteForNonEditableCookTime $isRecipeCompleteForNonEditableCookTime")
        }
    }

    /**
     * to find out if KitchenTimer is running and any cavity has completed the recipe or not
     * @return true if for all cavities has completed the recipe when kitchen timer is running otherwise false
     */
    private fun isRecipeCompleteBeforeKitchenTimer(): Boolean {
        if (!KitchenTimerVMFactory.isAnyKitchenTimerRunning()) return false
        if (provideViewHolderHelper().getUpperViewModel() != null && provideViewHolderHelper().getLowerViewModel() != null) {
            HMILogHelper.Logd(tag, "kitchenTimer running, recipe applicable for both cavities")
            return provideViewHolderHelper().getUpperViewModel()?.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.COMPLETED
                    && provideViewHolderHelper().getLowerViewModel()?.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.COMPLETED
        }
        if (provideViewHolderHelper().getUpperViewModel() != null && provideViewHolderHelper().getUpperViewModel()?.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.COMPLETED) {
            HMILogHelper.Logd(
                tag,
                "kitchenTimer running, recipe completed applicable for UPPER cavity"
            )
            return true
        }
        if (provideViewHolderHelper().getLowerViewModel() != null && provideViewHolderHelper().getLowerViewModel()?.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.COMPLETED) {
            HMILogHelper.Logd(
                tag,
                "kitchenTimer running, recipe completed applicable for LOWER cavity"
            )
            return true
        }
        return false
    }

    /**
     * responsible to update the progress bar in status screen
     */
    protected fun manageCookingProgressBar(
        cookingVM: CookingViewModel,
        statusWidgetHelper: AbstractStatusWidgetHelper?,
        percentageVal: Int,
        cookProgressBasis: RecipeProgressBasis,
        cookTimerState: Timer.State,
    ) {
        if (statusWidgetHelper == null) return
        if (cookingVM.recipeExecutionViewModel.isRunning) {
            when (cookProgressBasis) {
                RecipeProgressBasis.TEMPERATURE -> {
                    if (cookingVM.recipeExecutionViewModel.isProbeBasedRecipe) {
                        updateProbeProgressBar(cookingVM, statusWidgetHelper)
                        return
                    }
                    statusWidgetHelper.getStatusProgressBar()?.progress = percentageVal
                    makeIndefiniteProgressBarVisible(
                        statusWidgetHelper.getStatusProgressBar(),
                        statusWidgetHelper.getProgressbarInfinite(),
                        cookTimerState == Timer.State.IDLE && percentageVal == 100
                    )
                }

                RecipeProgressBasis.VIRTUAL_CHEF -> {
                    statusWidgetHelper.getStatusProgressBar()?.progress = percentageVal
                    makeIndefiniteProgressBarVisible(
                        statusWidgetHelper.getStatusProgressBar(),
                        statusWidgetHelper.getProgressbarInfinite(),
                        false
                    )
                }

                RecipeProgressBasis.TIME -> {
                    when (cookTimerState) {
                        Timer.State.IDLE -> {
                            statusWidgetHelper.getStatusProgressBar()?.max = 0
                            statusWidgetHelper.getStatusProgressBar()?.progress = 0
                            makeIndefiniteProgressBarVisible(
                                statusWidgetHelper.getStatusProgressBar(),
                                statusWidgetHelper.getProgressbarInfinite(),
                                true
                            )
                        }

                        Timer.State.RUNNING -> {
                            makeIndefiniteProgressBarVisible(
                                statusWidgetHelper.getStatusProgressBar(),
                                statusWidgetHelper.getProgressbarInfinite(),
                                false
                            )
                            statusWidgetHelper.getStatusProgressBar()?.max = 100
                            statusWidgetHelper.getStatusProgressBar()?.progress = percentageVal
                        }

                        else -> {}
                    }
                }

                else -> {
                    makeIndefiniteProgressBarVisible(
                        statusWidgetHelper.getStatusProgressBar(),
                        statusWidgetHelper.getProgressbarInfinite(),
                        true
                    )
                }
            }
        } else {
            HMILogHelper.Loge(
                TAG,
                "Not updating cooking progress bar as recipe execution state is not RUNNING or RUNNING_EXT," + " current RecipeExecutionState=${cookingVM.recipeExecutionViewModel.recipeExecutionState.value}"
            )
        }
    }

    /**
     * CallBack Method on stats screen when Upper cavity button is pressed
     * @param view : view from onClick callback method
     */
    open fun onUpperOvenCavityButtonClick(view: View) {
        navigateToUpperLowerRecipeSelection(view,true)
    }

    /**
     * Method to navigate to recipe selection
     * @param view : view from onClick callback method
     */
    private fun navigateToUpperLowerRecipeSelection(view: View, isForUpperCavity : Boolean, isFromDoorInteraction : Boolean = false){
        if (handleClickWhileControlsLocked()) return
        val loadAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.expand_fade_out)
        loadAnimation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {
                provideViewHolderHelper().provideUpperCavitySelection()?.alpha = 0.0f
                provideViewHolderHelper().provideUpperCavitySelectionIcon()?.alpha = 0.0f
                provideViewHolderHelper().getLowerCookingStatusWidget()?.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.fade_out
                    )
                )
            }

            override fun onAnimationEnd(animation: Animation?) {
                val navBuilder = NavOptions.Builder()
                navBuilder.setEnterAnim(R.anim.fade_in)
                    .setPopEnterAnim(R.anim.fade_in)
                if (!isFromDoorInteraction)
                    SharedViewModel.getSharedViewModel(this@AbstractStatusFragment.requireActivity())
                        .setCurrentRecipeBeingProgrammed(EMPTY_STRING)
                if (isForUpperCavity)
                    NavigationUtils.navigateToUpperRecipeSelection(this@AbstractStatusFragment)
                else
                    NavigationUtils.navigateToLowerRecipeSelection(this@AbstractStatusFragment)
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        view.startAnimation(loadAnimation)
    }

    /**
     * CallBack Method on stats screen when lower cavity button is pressed
     * @param view : view from onClick callback method
     */
    open fun onLowerOvenCavityButtonClick(view: View) {
        navigateToUpperLowerRecipeSelection(view, false)
    }

    /**
     * this method will get called when recipe is in execution
     * declare view id here to define different actions
     */
    override fun statusWidgetOnClick(
        view: View?,
        statusWidget: CookingStatusWidget?,
        viewModel: CookingViewModel?,
    ) {
        resetAnimationTransition()
        if (isSlowBlinkingKnobTimeoutActive()) userInteractWithinSlowBlinkingTimeoutElapsed()
        if(handleClickWhileControlsLocked())return
        if (viewModel == null || viewModel.recipeExecutionViewModel == null) return
        if (viewModel.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.DELAYED && statusWidget?.statusWidgetHelper?.tvResumeCooking()?.isVisible == true && statusWidget.statusWidgetHelper.tvResumeCooking()?.id != view?.id) {
            HMILogHelper.Logd(
                tag,
                "${viewModel.cavityName.value} has tvResumeCooking shown, so skipping statusWidgetOnClick"
            )
            return
        }
        when (view?.id) {
            statusWidget?.statusWidgetHelper?.tvOvenStateAction()?.id -> {
                HMILogHelper.Logd(
                    tag,
                    "${viewModel.cavityName.value} has tvOvenStateAction TURN OFF pressed, so cancelling recipeExecution"
                )
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                viewModel.recipeExecutionViewModel?.cancel()
            }

            statusWidget?.statusWidgetHelper?.tvResumeCooking()?.id ->{
                CommonAnimationUtils.animateToCookingView(viewModel,statusWidget,this) {
                    onClickResumeCooking(viewModel)
                }
            }

            statusWidget?.statusWidgetHelper?.tvSetCookTime()?.id -> {
                HMILogHelper.Logd(
                    tag,
                    "isProbeBasedRecipe --${viewModel.recipeExecutionViewModel?.isProbeBasedRecipe}"
                )
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                if (viewModel.recipeExecutionViewModel.isVirtualchefBasedRecipe) {
                    launchLiveLookInFragment()
                } else {
                if (viewModel.recipeExecutionViewModel?.isProbeBasedRecipe == true
                    && viewModel.recipeExecutionViewModel?.targetMeatProbeTemperatureReached?.value == true
                ) {
                    onClickSetCookTimerForProbe(viewModel)
                } else {
                    if (!((viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                            AppConstants.BROWNING_CONSTANT
                        )) ||
                                (viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                                    AppConstants.ADD_BROWNING_CONSTANT
                                )))
                    ) {
                        onClickSetCookTimer(viewModel, false)
                    }
                }
                }
            }

            statusWidget?.statusWidgetHelper?.getLiveLookInIcon()?.id -> {
                launchLiveLookInFragment()
            }

            statusWidget?.statusWidgetHelper?.tvCookTimeRemaining()?.id -> {
                // MAF_2755: Defect fixed where The system unexpectedly navigates to the cook time selection screen when the user clicks on the "Sensing" text.
                if (!((viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                        AppConstants.BROWNING_CONSTANT
                    )) ||
                            (viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                                AppConstants.ADD_BROWNING_CONSTANT
                            )) ||  (viewModel.recipeExecutionViewModel.recipeCookingState.value!! == RecipeCookingState.SENSING
                                && (!viewModel.recipeExecutionViewModel.isProbeBasedRecipe)) ||
                                (isRunningSteam(viewModel)))
                ) {
                    resetKnobForwardTrace()
                    onClickCookTimeRemaining(viewModel, false)
                }
            }

            statusWidget?.statusWidgetHelper?.getCavityMoreMenu()?.id -> {
                isFromKnob = false
                val isAssisted = CookingAppUtils.isRecipeAssisted(
                    viewModel.recipeExecutionViewModel.recipeName.value,
                    viewModel.cavityName.value
                )
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                HMILogHelper.Logd(tag, "IsAssisted-$isAssisted")
                //for assisted recipes always got to preview screen
                if (isAssisted) {
                    PopUpBuilderUtils.moreOptionsPopup(this, viewModel, isAssisted = true)
                } else {
                    PopUpBuilderUtils.moreOptionsPopup(this, viewModel)
                }
            }

            statusWidget?.statusWidgetHelper?.tvRecipeWithTemperature()?.id,
            statusWidget?.statusWidgetHelper?.getTemperatureRampIcon()?.id-> {
                resetKnobForwardTrace()
                if (((viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(AppConstants.BROWNING_CONSTANT)) ||
                            (viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                                AppConstants.ADD_BROWNING_CONSTANT
                            )))
                ) {
                    PopUpBuilderUtils.showBroilCanNotModifyPopup(
                        this@AbstractStatusFragment,
                        viewModel
                    )
                } else {
                    onClickRecipeMode(viewModel, false)
                }
            }

            statusWidget?.statusWidgetHelper?.getTemperatureProbeIcon()?.id -> {
                resetKnobForwardTrace()
                if (((viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(AppConstants.BROWNING_CONSTANT)) ||
                            (viewModel.recipeExecutionViewModel.currentStep?.name?.text.equals(
                                AppConstants.ADD_BROWNING_CONSTANT
                            )))
                ) {
                    PopUpBuilderUtils.showBroilCanNotModifyPopup(
                        this@AbstractStatusFragment,
                        viewModel
                    )
                } else {
                    onClickCookTimeRemaining(viewModel, false)
                }
            }
        }
    }

    /**
     * Method to reset KnobNavigationUtils.knobForwardTrace value
     * to false if true, if staying on status fragment even after
     * knob click on status fragment
     */
    private fun resetKnobForwardTrace(){
        if (KnobNavigationUtils.knobForwardTrace) KnobNavigationUtils.knobForwardTrace = false
    }


    /** ONLY for Sabbath Recipes
     * this method will get called when recipe is in execution
     * declare view id here to define different actions
     */

    protected fun sabbathStatusWidgetOnClick(
        view: View?,
        statusWidget: CookingStatusWidget?,
        viewModel: CookingViewModel?
    ) {
        val temperatureRange =
            viewModel?.recipeExecutionViewModel?.targetTemperatureOptions?.value as IntegerRange
        val maxTemp = temperatureRange.max
        val minTemp = temperatureRange.min
        val setTemperature = viewModel.recipeExecutionViewModel?.targetTemperature?.value
        val allowedChangesTemperature =
            if (CookingAppUtils.isFAHRENHEITUnitConfigured()) AppConstants.SABBATH_FAHRENHEIT_TEMPERATURE_ALLOWED_VALUE else AppConstants.SABBATH_CELSIUS_TEMPERATURE_ALLOWED_VALUE
        when (view?.id) {
            statusWidget?.statusWidgetHelper?.tvSabbathTemperatureUp()?.id -> {
                if ((setTemperature?.plus(allowedChangesTemperature) ?: 0) > maxTemp) {
                    HMILogHelper.Logd(
                        tag,
                        "Sabbath ${viewModel.cavityName.value} maxTemp $maxTemp is reached, not incrementing setTemperature $setTemperature allowedChangesTemperature $allowedChangesTemperature"
                    )
                    return
                }
                val recipeErrorResponse = viewModel.recipeExecutionViewModel?.setTargetTemperature(
                    (setTemperature?.plus(allowedChangesTemperature) ?: 0).toFloat()
                )
                HMILogHelper.Logd(
                    tag,
                    "Sabbath ${viewModel.cavityName.value} setTemperature $setTemperature incrementing $allowedChangesTemperature, recipeResponse ${recipeErrorResponse?.description}"
                )
            }

            statusWidget?.statusWidgetHelper?.tvSabbathTemperatureDown()?.id -> {
                if ((setTemperature?.minus(allowedChangesTemperature) ?: 0) < minTemp) {
                    HMILogHelper.Logd(
                        tag,
                        "Sabbath ${viewModel.cavityName.value} minTemp $maxTemp is reached, not decrementing setTemperature $setTemperature allowedChangesTemperature $allowedChangesTemperature"
                    )
                    return
                }
                val recipeErrorResponse = viewModel.recipeExecutionViewModel?.setTargetTemperature(
                    (setTemperature?.minus(allowedChangesTemperature) ?: 0).toFloat()
                )
                HMILogHelper.Logd(
                    tag,
                    "Sabbath ${viewModel.cavityName.value} setTemperature $setTemperature decrementing $allowedChangesTemperature, recipeResponse ${recipeErrorResponse?.description}"
                )
            }
        }
    }


    /**
     * click event to resume cooking for a particular cavity
     *
     * @param viewModel cooking view model for a particular cavity
     */
    private fun onClickResumeCooking(viewModel: CookingViewModel) {
        if (viewModel.doorState.value == true) {
            HMILogHelper.Logd(tag, "Door is OPEN so can not continue onClickResumeCooking")
            if (viewModel.isPrimaryCavity) {
                NavigationUtils.getVisibleFragment()?.let {
                    DoorEventUtils.upperCloseDoorToContinueAction(
                        it, viewModel
                    ) {
                        HMILogHelper.Logd(
                            tag,
                            "Upper cavity - Door is closed so continue onClickResumeCooking"
                        )
                        onClickResumeCooking(viewModel)
                    }
                }
            } else if (viewModel.isSecondaryCavity) {
                NavigationUtils.getVisibleFragment()?.let {
                    DoorEventUtils.lowerCloseDoorToContinueAction(
                        it, viewModel
                    ) {
                        HMILogHelper.Logd(
                            tag,
                            "Lower cavity - Door is closed so continue onClickResumeCooking"
                        )
                        onClickResumeCooking(viewModel)
                    }
                }
            }
            return
        }
        val recipeErrorResponse: RecipeErrorResponse
        if (viewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED) {
            recipeErrorResponse = viewModel.recipeExecutionViewModel.overrideDelay()
            if (recipeErrorResponse?.isError == true) CookingAppUtils.handleDelayCookingError(
                this,
                viewModel,
                recipeErrorResponse,
                false
            )
        } else {
            SharedViewModel.getSharedViewModel(this).cancelRecipeEventHandler(
                requireContext(),
                if (viewModel.isPrimaryCavity) 1 else 2,
                false
            )
            if (CookingAppUtils.isUserInstructionRequired(viewModel)) {
                HMILogHelper.Logd(tag, "User instruction is required before, resuming recipe")
                PopUpBuilderUtils.displayCookingInstructionPopUp(
                    this,
                    viewModel,
                    viewModel.recipeExecutionViewModel.userInstruction.value?.text
                )
                return
            }
            recipeErrorResponse = viewModel.recipeExecutionViewModel.resume()
            if (recipeErrorResponse?.isError == true) {
                CookingAppUtils.handleCookingError(
                    this,
                    viewModel,
                    recipeErrorResponse,
                    false
                )
            }else{
                //for any recipe that has probe attached but not a probe recipe
                if (viewModel.doorState.value == false && MeatProbeUtils.isMeatProbeConnected(
                        viewModel
                    ) && !viewModel.recipeExecutionViewModel.isProbeBasedRecipe
                ) {
                    HMILogHelper.Logd(
                        TAG,
                        "${viewModel.cavityName.value} onClickResume: Meat probe is connected and recipe is not a probe recipe, probeDetectedFromSameCavityPopupBuilder"
                    )
                    PopUpBuilderUtils.removeProbeToContinueCooking(
                        this,
                        viewModel,
                        getRecipeNameText(
                            requireContext(),
                            viewModel.recipeExecutionViewModel.recipeName.value ?: ""
                        )
                    ) {
                        HMILogHelper.Logd(
                            TAG,
                            "${viewModel.cavityName.value} onClickResume: Meat probe is removed, probeDetectedFromSameCavityPopupBuilder"
                        )
                    }
                    return
                }
            }
            //MAF_2751: Defect fixed for The system does not display the "Keep Door Locked" and "Ready at XX:XX" screens when the
            // cycle is resumed after pressing the cancel button.
              if((viewModel.recipeExecutionViewModel.recipeCookingState.value?.equals(RecipeCookingState.SENSING) == true) &&
                      (viewModel.recipeExecutionViewModel.isSensingRecipe) && (!viewModel.recipeExecutionViewModel.isProbeBasedRecipe)) {
                    var statusWidget: AbstractStatusWidgetHelper? = null
                   if (viewModel.isPrimaryCavity) statusWidget =
                           provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper
                    if (viewModel.isSecondaryCavity) statusWidget =
                            provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper
                  if (statusWidget != null) {
                        statusWidget.tvResumeCooking()?.visibility = View.GONE
                       executeKeepDoorClosedRunnable(
                              viewModel,
                               statusWidget.tvRecipeWithTemperature(), statusWidget.tvCookTimeRemaining()
                        )
                   }
                   return
              }
        }
        HMILogHelper.Logd(
            "$TAG resume",
            " recipeExecutionViewModel  ${viewModel.cavityName.value} recipe name=${recipeErrorResponse?.name} description= ${recipeErrorResponse?.description}"
        )
    }

    private fun navigateToProbeTumblerView(viewModel: CookingViewModel, isKnobClick: Boolean) {
        HMILogHelper.Logd(
            tag,
            "onClick getTemperatureProbeIcon click recipeExecutionViewModel state=${viewModel.recipeExecutionViewModel.recipeExecutionState.value}"
        )
        if (viewModel.recipeExecutionViewModel.isRunning && CookingAppUtils.isRecipeOptionAvailable(
                viewModel.recipeExecutionViewModel,
                RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE
            )
        ) {
            CookingViewModelFactory.setInScopeViewModel(viewModel)
            navigateSafely(
                this, if (isKnobClick) R.id.action_to_probeTemperatureTumbler else
                    R.id.action_to_probeTemperatureNumPad, null, null
            )
        }
    }

    /**
     * if any particular recipe parameters is changeable during recipe running then by clicking on the text navigate to particular fragment
     *
     * @param viewModel cooking view model of a particular status widget
     * @param isKnobClick to know selection of view is done by knob click
     */
    private fun onClickRecipeMode(viewModel: CookingViewModel?, isKnobClick: Boolean) {

        val requiredOptions = viewModel?.recipeExecutionViewModel?.requiredOptions?.value
        val optionalOptions = viewModel?.recipeExecutionViewModel?.optionalOptions?.value
        var statusWidget: AbstractStatusWidgetHelper? = null
        if (viewModel?.isPrimaryCavity == true) statusWidget =
            provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper
        if (viewModel?.isSecondaryCavity == true) statusWidget =
            provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper

        if (viewModel?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.DELAYED && statusWidget?.tvRecipeWithTemperature()?.text?.startsWith(
                resources.getString(
                    R.string.text_delayed_until_self_clean
                ).dropLast(5)
            ) == true
        ) {
            CookingViewModelFactory.setInScopeViewModel(viewModel)
            navigateToDelayScreen(this)
            return
        }
        if ((viewModel?.isOfTypeOven == true
                    && viewModel.recipeExecutionViewModel?.isProbeBasedRecipe == true
                    && viewModel.recipeExecutionViewModel?.targetMeatProbeTemperatureReached?.value == true)
            || viewModel?.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.COMPLETED
            || (CookingAppUtils.isRecipeAssisted(
                viewModel?.recipeExecutionViewModel?.recipeName?.value,
                viewModel?.cavityName?.value
            ) && ((requiredOptions?.contains(RecipeOptions.TARGET_TEMPERATURE) == false)
                    && (optionalOptions?.contains(RecipeOptions.TARGET_TEMPERATURE) == false)))
        ) {
            // In case of cycle completed no need to perform any action
            return
        }

        if (CookingAppUtils.isRecipeAssisted(
                viewModel?.recipeExecutionViewModel?.recipeName?.value, viewModel?.cavityName?.value
            )
        ) {
            HMILogHelper.Loge(
                tag,
                "${viewModel?.cavityName?.value} recipeName ${viewModel?.recipeExecutionViewModel?.recipeName?.value} is assisted recipe not allowing to change recipe parameters "
            )
            return
        }

        if(CookingAppUtils.isTimeBasedPreheatRecipe(viewModel)){
            HMILogHelper.Loge(tag, "${viewModel?.cavityName?.value} recipeName ${viewModel?.recipeExecutionViewModel?.recipeName?.value} time based preheat changing recipe parameters NOT available ")
            return
        }

        if (viewModel?.recipeExecutionViewModel?.isRunning == true) {
            CookingViewModelFactory.setInScopeViewModel(viewModel)
            if (CookingAppUtils.isRecipeOptionAvailable(viewModel.recipeExecutionViewModel, RecipeOptions.TARGET_TEMPERATURE)) {
                if (viewModel.recipeExecutionViewModel.isProbeBasedRecipe && viewModel.recipeExecutionViewModel.targetMeatProbeTemperatureReached.value == true) return
                NavigationUtils.navigateToTemperatureWhenRunning(this, viewModel, isKnobClick)
                return
            }
            if (CookingAppUtils.isRecipeOptionAvailable(viewModel.recipeExecutionViewModel, RecipeOptions.MWO_POWER_LEVEL)) {
                navigateSafely(this, R.id.action_status_to_mwoPowerLevelChange, null, null)
                return
            }
        }
        HMILogHelper.Loge(tag, "${viewModel?.cavityName?.value} recipeName ${viewModel?.recipeExecutionViewModel?.recipeName?.value} changing recipe parameters NOT available ")
    }


    /**
     * if any particular recipe parameters is changeable during recipe running then by clicking on the text navigate to particular fragment
     *
     * @param viewModel cooking view model of a particular status widget
     * @param isKnobClick to know selection of view is done by knob click
     */
    private fun onClickCookTimeRemaining(viewModel: CookingViewModel?, isKnobClick: Boolean) {
        if (viewModel?.recipeExecutionViewModel?.isProbeBasedRecipe == true && CookingAppUtils.isRecipeOptionAvailable(
                viewModel.recipeExecutionViewModel, RecipeOptions.MEAT_PROBE_TARGET_TEMPERATURE
            ) && viewModel.recipeExecutionViewModel.targetMeatProbeTemperatureReached.value == false && viewModel.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.IDLE
        ) {
            navigateToProbeTumblerView(viewModel, isKnobClick)
        } else if ((CookingAppUtils.isCookTimeOptionAvailable(viewModel) || isExtendedCookingForNonEditableCookTimeRecipe(viewModel)) &&
                (viewModel?.recipeExecutionViewModel?.recipeExecutionState?.value != RecipeExecutionState.PAUSED ||
                        viewModel.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.PAUSED_EXT)
        ) {
            CookingViewModelFactory.setInScopeViewModel(viewModel)
            if (isKnobClick) {
                val bundle = Bundle()
                bundle.putString(
                    BundleKeys.BUNDLE_PROVISIONING_TIME,
                    "${viewModel?.recipeExecutionViewModel?.cookTime?.value?.toInt()}"
                )
                navigateSafely(
                    this, R.id.action_status_to_verticalTumblerFragment, bundle, null
                )
            } else {
                navigateSafely(
                    this, R.id.action_recipeExecutionFragment_to_cookTimeFragment, null, null
                )
            }
        }
    }

    private fun launchLiveLookInFragment() {
        navigateSafely(
            this, R.id.action_recipeExecutionFragment_to_liveLookInFragment,
            null, null
        )
    }

    /**
     * onClick of set cook time on Status widget, override this method in different variant status classes if logic is different
     */
    private fun onClickSetCookTimer(viewModel: CookingViewModel?, isKnobClick: Boolean) {
        val recipeCookingState = viewModel?.recipeExecutionViewModel?.recipeCookingState?.value
        val statusWidgetHelper = if(viewModel?.isPrimaryCavity == true) provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper else provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper
        if (viewModel?.recipeExecutionViewModel?.cookTime?.value != 0L && viewModel?.recipeExecutionViewModel?.cookTimerState?.value == Timer.State.PAUSED &&
            (recipeCookingState == RecipeCookingState.COOKING || recipeCookingState == RecipeCookingState.PREHEATING) && statusWidgetHelper?.tvSetCookTime()?.text.contentEquals(getString(R.string.text_button_start_timer))
        ) {
            if (viewModel.doorState.value == true) {
                if (viewModel.isPrimaryCavity) {
                    NavigationUtils.getVisibleFragment()?.let {
                        DoorEventUtils.upperCloseDoorToContinueAction(
                            it,
                            viewModel
                        ) {
                            val recipeErrorResponse =
                                viewModel.recipeExecutionViewModel.startCookTimer()
                            HMILogHelper.Logd(
                                tag,
                                "Upper Door OPEN, setting cookTime when cook timer is paused and recipe state cooking name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}}"
                            )
                        }
                    }
                } else if (viewModel.isSecondaryCavity) {
                    NavigationUtils.getVisibleFragment()?.let {
                        DoorEventUtils.lowerCloseDoorToContinueAction(
                            it,
                            viewModel
                        ) {
                            val recipeErrorResponse =
                                viewModel.recipeExecutionViewModel.startCookTimer()
                            HMILogHelper.Logd(
                                tag,
                                "Lower Door OPEN, setting cookTime when cook timer is paused and recipe state cooking name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}}"
                            )
                        }
                    }
                }
                return
            }
            val recipeErrorResponse = viewModel.recipeExecutionViewModel.startCookTimer()
            HMILogHelper.Logd(
                tag,
                "setting cookTime when cook timer is paused and recipe state cooking name=${recipeErrorResponse.name} description=${recipeErrorResponse.description}}"
            )
            return
        }
        if (CookingAppUtils.isTimePreheatRunning(viewModel) && viewModel?.recipeExecutionViewModel?.cookTime?.value == 0L) {
            //special case for Time based preheat recipe, in this case cook timer will be running as time preheat and if cook time is set as 0 then open numpad to set the cook time during untimed time based preheat recipe
            navigateToCookTimerFragment(viewModel, isKnobClick)
            return
        }
        if (viewModel?.recipeExecutionViewModel?.cookTimerState?.value != Timer.State.IDLE) {
            viewModel?.recipeExecutionViewModel?.remainingCookTime?.value?.let {
                if (isValidCookTime(viewModel, provideMinCookTimeIncrement(viewModel))) {
                    if (viewModel.doorState.value == true) {
                        if (viewModel.isPrimaryCavity) {
                            NavigationUtils.getVisibleFragment()?.let {
                                DoorEventUtils.upperCloseDoorToContinueAction(
                                    it,
                                    viewModel
                                ) {
                                    HMILogHelper.Logd(
                                        tag,
                                        "Upper Door OPEN, asking user to close the door and callback on onClickSetCookTimer"
                                    )
                                    onClickSetCookTimer(viewModel, isKnobClick)
                                }
                            }
                        } else if (viewModel.isSecondaryCavity) {
                            NavigationUtils.getVisibleFragment()?.let {
                                DoorEventUtils.lowerCloseDoorToContinueAction(
                                    it,
                                    viewModel
                                ) {
                                    HMILogHelper.Logd(
                                        tag,
                                        "Lower Door OPEN, asking user to close the door and callback on onClickSetCookTimer"
                                    )
                                    onClickSetCookTimer(viewModel, isKnobClick)
                                }
                            }
                        }
                        return
                    }
                    if (viewModel.isOfTypeMicrowaveOven && (viewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED || viewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED_EXT)) {
                        val recipeResponse = viewModel.recipeExecutionViewModel.resume()
                        HMILogHelper.Logd(
                            tag,
                            "recipeExecutionState is ${viewModel.recipeExecutionViewModel.recipeExecutionState.value} so RESUMING, ACU state with recipeErrorResponse ${recipeResponse.description}"
                        )
                        if (recipeResponse.isError) {
                            HMILogHelper.Loge(
                                tag,
                                "recipeExecutionState is ${viewModel.recipeExecutionViewModel.recipeExecutionState.value} ERROR occurred during resume(), with recipeErrorResponse ${recipeResponse.description}"
                            )
                            PopUpBuilderUtils.runningFailPopupBuilder(this)
                            return
                        }
                    }
                    viewModel.recipeExecutionViewModel.addCookTime(
                        provideMinCookTimeIncrement(
                            viewModel
                        )
                    )
                } else {
                    PopUpBuilderUtils.cookTimeNotAvailablePopup(
                        this,getString(
                            R.string.text_cannot_add_cooktime_description,
                            getString(R.integer.five_minute_cook_time) + EMPTY_SPACE + getString(
                                R.string.text_label_minutes
                            )
                        )
                    )
                }
            }
            return
        }
        navigateToCookTimerFragment(viewModel, isKnobClick)
    }

    /**
     * navigate to cookTime fragment
     *
     * @param viewModel to set scope of the view model
     * @param isKnobClick true to navigate to tumbler false otherwise
     */
    private fun navigateToCookTimerFragment(viewModel: CookingViewModel, isKnobClick: Boolean) {
        HMILogHelper.Logd(tag, "${viewModel.cavityName.value} isKnobClick $isKnobClick navigating to setect cook time")
        CookingViewModelFactory.setInScopeViewModel(viewModel)
        if (isKnobClick) KnobNavigationUtils.knobForwardTrace = true
        navigateSafely(
            this,
            if (isKnobClick) R.id.action_status_to_verticalTumblerFragment else
                R.id.action_recipeExecutionFragment_to_cookTimeFragment,
            null,
            null
        )
    }

    /**
     * set value of set cook time, override this if different for other variants
     */
    private fun provideMinCookTimeIncrement(viewModel: CookingViewModel): Long {
        val setCookTime =  if(CookingAppUtils.isTimePreheatRunning(viewModel))viewModel.recipeExecutionViewModel.cookTime.value else viewModel.recipeExecutionViewModel.remainingCookTime.value
        if ((setCookTime ?: 0) > AppConstants.ADD_COOK_TIME_TEN_MINUTE
        ) return AppConstants.ADD_COOK_TIME_FIVE_MINUTES
        if (viewModel.isOfTypeMicrowaveOven) return AppConstants.ADD_COOK_TIME_30_SECOND
        return AppConstants.ADD_COOK_TIME_TWO_MINUTE
    }

    /**
     * Display value of set cook time, override this if different for other variants
     */
    private fun provideVisualCookMinTime(cookingViewModel: CookingViewModel): CharSequence {
        if (cookingViewModel.isOfTypeOven) return getString(
            R.string.text_5_min_button,
            resources.getInteger(R.integer.two_minute_cook_time),
            getString(R.string.text_label_MIN)
        )
        return getString(
            R.string.text_5_min_button,
            resources.getInteger(R.integer.thirty_seconds_cook_time),
            getString(R.string.text_label_SEC)
        )
    }


    override fun onStop() {
        super.onStop()
        firstTimeKeepDoorClosed = true
        timeoutViewModel.stop()
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResult(AppConstants.UPPER_FRAGMENT)
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResultListener(AppConstants.UPPER_FRAGMENT)
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResult(AppConstants.LOWER_FRAGMENT)
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResultListener(AppConstants.LOWER_FRAGMENT)
        dismissUpperCavityDoorPopup()
        dismissLowerCavityDoorPopup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        updateStaticVariables()
        //removing all callbacks when fragment is destroyed
        SettingsViewModel.getSettingsViewModel().controlLock.removeObservers(viewLifecycleOwner)
        CookBookViewModel.getInstance().allHistoryRecords.removeObservers(viewLifecycleOwner)
        handlerUpperCavity.removeCallbacksAndMessages(null)
        handlerLowerCavity.removeCallbacksAndMessages(null)
        HMIExpansionUtils.removeHMICancelButtonInteractionListener(this)
        HMIExpansionUtils.removeFragmentUserInteractionListener(this)
        requireActivity().supportFragmentManager.clearFragmentResult(HmiExpansionViewModel.FUNCTION_CANCEL)
        requireActivity().supportFragmentManager.clearFragmentResultListener(HmiExpansionViewModel.FUNCTION_CANCEL)
        requireActivity().supportFragmentManager.clearFragmentResultListener(BundleKeys.BUNDLE_APPLY_TRANSITION_ANIMATION)
        provideViewHolderHelper().onDestroyView()
        isFromKnob = false
    }
    /************************************************************* KNOB Rotation RELATED Logic **********************************************************************/

    /**
     * provide items that are subject to highlight with knob rotations
     * @return List of KnobItems
     */
    abstract fun provideKnobRotationItems(): List<KnobItem>?

    /**
     * update the status widget
     */
    abstract fun updateStatusWidget(statusWidget: CookingStatusWidget)

    /**
     * list of current knob items supposed to move when knob is rotated
     * the value will be updated dynamically ex. in delay or cancel button press
     */
    private var knobRotationItems : List<KnobItem>? = null

    /**
     * select current highlighted knob item
     * @return true if any item is selected, knobItem associated knob item
     */
    private fun selectHighlightedKnobItem() : Pair<Boolean,KnobItem?> {
        knobRotationItems?.forEachIndexed { _, knobItem ->
            if(knobItem.isSelected) return Pair(true, knobItem)
        }
        return Pair(false, null)
    }

    /**
     * calls when knob is rotated Clockwise
     */
    private fun handleRightKnobClockWiseRotation(){
        val (isSelected, knobItem) = selectHighlightedKnobItem()
        if(knobItem != null) {
            if (isSelected && knobItem.index != (knobRotationItems?.size?.minus(1)?:0)) {
                val nextItem = knobRotationItems?.get(knobItem.index + 1)
                removeKnobItemHighlightExcept(nextItem)
            }else{
                HMILogHelper.Logd(tag, "KNOB: forwardRotation nothing is selected")
            }
        }else{
            HMILogHelper.Logd(tag, "KNOB: forwardRotation highlighting first element")
            if (knobRotationItems?.isNotEmpty() == true) {
                removeKnobItemHighlightExcept(knobRotationItems?.get(0))
            }
        }
    }

    /**
     * remove/clear background of knob items except that passed in argument
     * useful to have highlight only selected item and clear for rest of the items
     * @param selectKnobItem knobItem that being focused
     */
    private fun removeKnobItemHighlightExcept(selectKnobItem: KnobItem?) {
        knobRotationItems?.forEach {knobItem ->
            if (selectKnobItem?.index == knobItem.index) {
                if (selectKnobItem.view is ConstraintLayout)
                    (selectKnobItem.view as ConstraintLayout).setBackgroundColor(
                        requireContext().getColor(R.color.cavity_selected_button_background)
                    )
                else selectKnobItem.view?.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.selector_textview_walnut)
                selectKnobItem.isSelected = true
            } else {
                if (knobItem.view is ConstraintLayout) (knobItem.view as ConstraintLayout).setBackgroundColor(
                    requireContext().getColor(R.color.cavity_selection_button_background)
                )
                else knobItem.view?.background = null
                knobItem.isSelected = false
            }
        }
    }

    /**
     * calls when there is counter clock-wise rotation on right knob
     */
    private fun rightKnobCounterClockWiseRotation() {
        val (isSelected, knobItem) = selectHighlightedKnobItem()
        if (isSelected) {
            if(knobItem?.index != 0) {
                val previousItem = knobRotationItems?.get(knobItem?.index?.minus(1) ?: 0)
                removeKnobItemHighlightExcept(previousItem)
            }else{
                HMILogHelper.Logd(tag, "KNOB: backwardRotation reached far end")
            }
        }else{
            HMILogHelper.Logd(tag, "KNOB: backwardRotation nothing is selected")
        }
    }

    /**
     * Method to manage knob rotation for right knob and perform opetation based on knobDirection
     * @param knobId: Int RIGHT_KNOB_ID
     * @param knobDirection: String? CLOCK_WISE_DIRECTION
     * */
    private fun handleRightKnobRotation(
        knobId: Int,
        knobDirection: String?
    ) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            KnobNavigationUtils.knobForwardTrace = false
            if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION) handleRightKnobClockWiseRotation()
            else rightKnobCounterClockWiseRotation()
        }
    }
    /**
     * calls when right knob is clicked, this will identify which knobItem is selected and perform onClick on that view,
     * if a particular action need to be performed like set cook timer to open tumbler view instead of numpad view then declare
     * a tag in XML component and do operation based on the tag matching
     * */
    private fun handleRightKnobClick() {
            val (isSelected, knobItem) = selectHighlightedKnobItem()
            if(isSelected && knobItem?.view?.isVisible == true) {
                // Handle actions based on different view tags
                val viewTag = knobItem.view?.tag as? String
                when (viewTag) {
                    getString(R.string.text_button_set_cook_time) -> {
                        HMILogHelper.Logd(tag, "KNOB: set cook time clicked through Knob")
                        onClickSetCookTimer(knobItem.cookingVM, true)
                    }
                    else -> {
                        KnobNavigationUtils.knobForwardTrace = true
                        knobItem.view?.callOnClick()
                    }
                }
                return
            }
        HMILogHelper.Loge(tag, "KNOB: Right click, none of the knobItem is selected or knobItem is not VISIBLE")
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILeftKnobClick() {
        if (handleClickWhileControlsLocked()) return
        HMILogHelper.Logd(
            "Knob Event",
            "Knob id = $id knobEvent = LEFT CLICK"
        )
        if (provideViewHolderHelper().provideHeaderBarWidget()
                ?.getTopSheetView()?.topSheetBehavior?.getState() != (TopSheetBehavior.STATE_EXPANDED)
            || (provideViewHolderHelper().provideHeaderBarWidget()
                ?.getTopSheetView()?.drawerWidgetBinding?.tvLightState?.isVisible == true)
        ) {
            leftKnobCounter = -1
            manageLightButtonClick()
        } else {
            provideViewHolderHelper().provideHeaderBarWidget()?.getTopSheetView()
                ?.manageLeftKnobClick(counter = leftKnobCounter)
        }
    }

    /************************************************************* KNOB Rotation RELATED Logic **********************************************************************/

    private fun manageLightButtonClick() {
        provideViewHolderHelper().provideHeaderBarWidget()
            ?.getTopSheetView()?.topSheetBehavior?.setState(TopSheetBehavior.STATE_EXPANDED)
        provideViewHolderHelper().provideHeaderBarWidget()?.getTopSheetView()
            ?.performOvenLightOperation()
    }

    private fun postMarkFavoriteStatus(recipeNameAndTemp: StringBuilder) {
        provideViewHolderHelper().provideHeaderBarWidget()?.getTopSheetView()?.topSheetBehavior?.setState(TopSheetBehavior.STATE_EXPANDED)
        provideViewHolderHelper().provideHeaderBarWidget()?.getTopSheetView()?.performMarkFavoriteStatus(recipeNameAndTemp.toString())
        FavoriteDataHolder.updateMarkFavorite(null )
    }

    override fun onClick(view: View?) {
        HMILogHelper.Logd("$TAG, onClick, handle control lock click: ${view?.id}")
        if (view != null) {
            handleClickWhileControlsLocked()
        }
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMIRightKnobClick() {
        handleRightKnobClick()
    }
    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            HMILogHelper.Logd(
                "Knob Event",
                "Knob id = $knobId knobDirection = $knobDirection"
            )
            manageLeftKnobRotation(knobId, knobDirection)
            return
        }
        handleRightKnobRotation(knobId, knobDirection)
    }
    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            isFromKnob = false
//            reset all knobItems background
            knobRotationItems = initKnobItems()
        }
    }
    private fun manageLeftKnobRotation(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if(provideViewHolderHelper().provideHeaderBarWidget()?.getTopSheetView()?.isShowingNotification() == true) return
            if (provideViewHolderHelper().provideHeaderBarWidget()?.getTopSheetView()?.topSheetBehavior?.getState() != (TopSheetBehavior.STATE_EXPANDED)) {
                provideViewHolderHelper().provideHeaderBarWidget()?.getTopSheetView()?.topSheetBehavior?.setState(TopSheetBehavior.STATE_EXPANDED)
            }

            when (knobDirection) {
                KnobDirection.CLOCK_WISE_DIRECTION -> {
                    provideViewHolderHelper().provideHeaderBarWidget()
                        ?.getTopSheetView()?.drawerWidgetBinding?.recyclerViewGridList?.size?.let { size ->
                        leftKnobCounter = if (leftKnobCounter < size - 1) leftKnobCounter + 1 else 0
                    }
                }

                KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                    provideViewHolderHelper().provideHeaderBarWidget()
                        ?.getTopSheetView()?.drawerWidgetBinding?.recyclerViewGridList?.size?.let { size ->
                        leftKnobCounter =
                            if (leftKnobCounter <= 0) size - 1 else leftKnobCounter - 1
                    }
                }
            }
            provideViewHolderHelper().provideHeaderBarWidget()?.getTopSheetView()
                ?.manageKnobRotation(counter = leftKnobCounter)
        }
    }
    /************************************************************** Kitchen Timer Related  *********************************************************/
    /**
     * updating Clock view with kitchen time remaining value and replacing clock text
     * if any KitchenTimer is running and if multiple timer is running then show shortest
     */
    private fun observeOnKitchenTimerViewModel() {
        if (KitchenTimerVMFactory.isAnyKitchenTimerRunning()) {
            HMILogHelper.Logd(
                tag,
                "Kitchen Timer is running, replacing clockView to kitchenTimerView"
            )
            val runningKitchenTimer = KitchenTimerVMFactory.getKitchenTimerWithLeastRemainingTime()
            runningKitchenTimer?.timerStatus?.removeObservers(viewLifecycleOwner)
            runningKitchenTimer?.timerStatus?.observe(viewLifecycleOwner) { timerStatus ->
                if (timerStatus == KitchenTimerViewModel.TimerStatus.RUNNING) {
                    //if kt is running update the view and do not got forward
                    viewUpdateOnKitchenTimer(true)
                    provideViewHolderHelper().provideHeaderBarWidget()?.getKitchenTimerTextView()
                        ?.setKitchenTimerViewModel(runningKitchenTimer)
                    return@observe
                }
                if (timerStatus == KitchenTimerViewModel.TimerStatus.COMPLETED) {
                    //show kitchen timer completed popup
                    PopUpBuilderUtils.kitchenTimerCompletedPopup(this, runningKitchenTimer)
                }
                //to check on any other kitchen timer is running and update the view accordingly
                if (KitchenTimerVMFactory.isAnyKitchenTimerRunning()) {
                    observeOnKitchenTimerViewModel()
                } else viewUpdateOnKitchenTimer(false)
            }
        }
    }


    /**
     * show/hide clock textView vs Kitchen timer textView
     *
     * @param isRunning true if kitchen timer is running false otherwise
     */
    private fun viewUpdateOnKitchenTimer(isRunning: Boolean) {
        HMILogHelper.Logd(tag, "Clock Text Kitchen Timer isRunning =$isRunning")
        if (isRunning) {
            provideViewHolderHelper().provideHeaderBarWidget()?.getClockTextView()?.visibility =
                View.GONE
            provideViewHolderHelper().provideHeaderBarWidget()
                ?.getKitchenTimerTextView()?.visibility = View.VISIBLE
        } else {
            provideViewHolderHelper().provideHeaderBarWidget()?.getClockTextView()?.visibility =
                View.VISIBLE
            provideViewHolderHelper().provideHeaderBarWidget()
                ?.getKitchenTimerTextView()?.visibility = View.GONE
        }
    }


    /************************************************************** Kitchen Timer Related  *********************************************************/

    private fun handleClickWhileControlsLocked(): Boolean {
        if (SettingsViewModel.getSettingsViewModel().controlLock.value == true) {
            navigateSafely(
                this, R.id.action_to_controlUnlockFragment, null, null
            )
            return true
        } else {
            return false
        }
    }


    /************************************************************** Kitchen Timer Related  *********************************************************/

    private fun observeDemoModeLiveData() {
        SettingsViewModel.getSettingsViewModel().demoMode.observe(
            viewLifecycleOwner
        ) { demoMode: Int? ->
            when (demoMode) {
                SettingsRepository.DemoMode.DEMO_MODE_ENABLED -> {
                    HMILogHelper.Logd("DEMO MODE IS ACTIVE")
                    provideViewHolderHelper().provideHeaderBarWidget()
                        ?.getBinding()?.demoIcon?.visibility = View.VISIBLE
                }

                SettingsRepository.DemoMode.DEMO_MODE_DISABLED -> {
                    HMILogHelper.Logd("DEMO MODE IS NOT ACTIVE")
                    provideViewHolderHelper().provideHeaderBarWidget()
                        ?.getBinding()?.demoIcon?.visibility = View.INVISIBLE
                }

                else -> {
                    HMILogHelper.Logd("DEMO MODE IS NOT ACTIVE")
                    provideViewHolderHelper().provideHeaderBarWidget()
                        ?.getBinding()?.demoIcon?.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun observeHistoryLiveData() {
        CookBookViewModel.getInstance().allHistoryRecords.observe(viewLifecycleOwner) { historyRecord ->
            if (historyRecord != null) {
                HMILogHelper.Logd("Is primary cavity : ${CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity}")
                evaluateSaveToFavorite(CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel)
            }
        }
    }
    /**************************** Probe Extended Cycle  ***************************/

    /**
     * responsible to update probe extended cook time text in status widget
     */
    private fun manageProbeExtendedCookTimeText(
        statusWidget: AbstractStatusWidgetHelper,
        cookingViewModel: CookingViewModel
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
                // Handling the case - Probe has been completed and need to show +5 Min cook time
                val tvSetCookTime = statusWidget.tvSetCookTime()
                tvSetCookTime?.visible()
                tvSetCookTime?.text = getString(
                    R.string.text_5_min_button,
                    resources.getInteger(R.integer.five_minute_cook_time),
                    getString(R.string.text_label_MIN)
                )
                tvSetCookTime?.setTextAppearance(R.style.StatusWidgetTitleTextView)

                updateCookTimeWidgetDynamically(statusWidget, false)
                val remainingTime: Long =
                    cookingViewModel.recipeExecutionViewModel?.remainingCookTime?.value ?: 0
                val cookTime: Long = cookingViewModel.recipeExecutionViewModel.cookTime.value ?: 0
                val tvCookingEtr = statusWidget.tvCookTimeRemaining()
                tvCookingEtr?.visible()
                if (cookTime > 0) {
                    statusWidget.getTemperatureProbeIcon()?.gone()
                    tvCookingEtr?.text = CookingAppUtils.spannableETRRunning(
                        this@AbstractStatusFragment.requireContext(), remainingTime.toInt()
                    )

                } else {
                    // Handling the case - Probe has been completed and need to show current/targeted probe temperature
                    statusWidget.getTemperatureProbeIcon()?.visible()
                    val meatProbeTargetValue =
                        cookingViewModel.recipeExecutionViewModel?.meatProbeTargetTemperature?.value
                            ?: 0
                    if (meatProbeTargetValue > 0) {
                        val displayProbeTemperature = StringBuilder()
                        displayProbeTemperature.append(meatProbeTargetValue)
                            .append(AppConstants.DEGREE_SYMBOL)
                            .append(AppConstants.SYMBOL_FORWARD_SLASH)
                        displayProbeTemperature.append(meatProbeTargetValue)
                            .append(AppConstants.DEGREE_SYMBOL)
                        tvCookingEtr?.text = displayProbeTemperature.toString()
                }
            }
        }
    }


    /**
     * onClick of set cook time on Status widget, override this method in different variant status classes if logic is different
     */
    private fun onClickSetCookTimerForProbe(
        viewModel: CookingViewModel?
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            val isMeatProbeConnected = MeatProbeUtils.isMeatProbeConnected(viewModel)
            HMILogHelper.Logd(tag, "probe extended isMeatProbeConnected = $isMeatProbeConnected")
            if (isMeatProbeConnected) {
                PopUpBuilderUtils.removeProbeToContinueExtendedCycle(
                    this@AbstractStatusFragment,
                    viewModel,
                    onContinueButtonClick = {
                        startProbeExtendedCycle(viewModel)
                    },
                    onMeatProbeDestroy = {
                        onResume()
                    })
            } else {
                startProbeExtendedCycle(viewModel)
            }
        }
    }


    private fun startProbeExtendedCycle(viewModel: CookingViewModel?) {
        HMILogHelper.Logd(tag, "+5 MIN probe extended cookTime cycle")
        //add Cook Timer 5 min for probe
        if (viewModel?.recipeExecutionViewModel?.addCookTime(AppConstants.ADD_COOK_TIME_FIVE_MINUTES) == RecipeErrorResponse.NO_ERROR) {
            HMILogHelper.Logi(tag, "addCookTime${AppConstants.ADD_COOK_TIME_FIVE_MINUTES} success")
        } else {
            HMILogHelper.Loge(tag, "addCookTime${AppConstants.ADD_COOK_TIME_FIVE_MINUTES} failed")
            PopUpBuilderUtils.cookTimeNotAvailablePopup(
                this@AbstractStatusFragment,
                getString(
                    R.string.text_cannot_add_cooktime_description,
                    getString(R.integer.five_minute_cook_time) +
                            EMPTY_SPACE +
                            getString(R.string.text_label_minutes)
                )
            )
            return
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
    /**************************** Probe Extended Cycle  ***************************/

    /**
     * This function will help to navigate to the upper oven recipe selection screen
     */
    private fun navigateUpperRecipeSelectionListener() {
        NavigationUtils.getVisibleFragment().let {
            it?.parentFragmentManager?.setFragmentResultListener(
                AppConstants.UPPER_FRAGMENT,
                it.viewLifecycleOwner
            ) { _, bundle ->
                val result = bundle.getBoolean(AppConstants.UPPER_FRAGMENT)
                if (result) {
                    HMILogHelper.Logd("IDLE DOOR and ON STATUS SCREEN", "received bundle for UPPER_FRAGMENT")
                    it.view?.let { it1 ->
                        navigateToUpperLowerRecipeSelection(
                            it1,
                            isForUpperCavity = true,
                            isFromDoorInteraction = true
                        )
                    }
                }
            }
        }
    }

    /**
     * This function will help to navigate to the Lower oven recipe selection screen
     */
    private fun navigateLowerRecipeSelectionListener() {
        NavigationUtils.getVisibleFragment().let {
            it?.parentFragmentManager?.setFragmentResultListener(
                AppConstants.LOWER_FRAGMENT,
                it.viewLifecycleOwner
            ) { _, bundle ->
                val result = bundle.getBoolean(AppConstants.LOWER_FRAGMENT)
                if (result) {
                    HMILogHelper.Logd("IDLE DOOR and ON STATUS SCREEN", "received bundle for LOWER_FRAGMENT")
                    it.view?.let { it1 ->
                        navigateToUpperLowerRecipeSelection(
                            it1,
                            isForUpperCavity = false,
                            isFromDoorInteraction = true
                        )
                    }
                }
            }
        }
    }

    /**************************** Steam clean helper functions  ***************************/
    private fun updateSteamCleanRunningCavity() {
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN, CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                isUpperSteamCleanRunning = CookingAppUtils.isUpperSteamCleanRunning()
                isLowerSteamCleanRunning = CookingAppUtils.isLowerSteamCleanRunning()
            }

            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                isUpperSteamCleanRunning = CookingAppUtils.isUpperSteamCleanRunning()
            }

            else -> {
                HMILogHelper.Logd("Steam clean: steam clean is not support for current variant")
            }
        }
    }

    private fun startSteamCompleteRunnable(cavityPosition: Int) {
        val handler = getCavityHandler(cavityPosition)
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            PopUpBuilderUtils.steamCleanCompletePopup(
                this,
                if (cavityPosition == resources.getInteger(R.integer.integer_range_1)) R.drawable.ic_oven_cavity_large else R.drawable.ic_lower_cavity_large,
                cavityPosition
            )
        }, resources.getInteger(R.integer.ms_10000).toLong())
    }

    private fun updateSteamCleanCompleteRecipeName(
        cavityPosition: Int,
        tvRecipe: ResourceTextView?
    ) {
        val cookingVM =
            if (cavityPosition == resources.getInteger(R.integer.integer_range_2)) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
        val recipeName = tvRecipe?.context?.let {
            getRecipeNameText(
                it, cookingVM.recipeExecutionViewModel.recipeName.value.toString()
            )
        }
        val modeText =
            tvRecipe?.context?.getString(R.string.text_recipe_completed_status, recipeName)
        updateRecipeModeTextStyle(tvRecipe, modeText, true)
    }

    private fun isSteamCleanCompleted(cavityPosition: Int): Boolean {
        return when(cavityPosition){
            1->{
                (isUpperSteamCleanRunning && CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.cookTimerState.value == Timer.State.COMPLETED)
            }

            2->{
                (isLowerSteamCleanRunning &&
                        CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.cookTimerState.value == Timer.State.COMPLETED)
            }

            else->{
                false
            }
        }
    }


    private fun isRunningSteam(cookingVM: CookingViewModel): Boolean {
        return if (isUpperSteamCleanRunning && cookingVM.isPrimaryCavity) {
            true
        } else if (isLowerSteamCleanRunning && cookingVM.isSecondaryCavity) {
            true
        } else {
            false
        }
    }


    private fun evaluateSaveToFavorite(recipeExecutionViewModel: RecipeExecutionViewModel) {
        if (!CookingAppUtils.getRunningRecipeIsQuickStart()) {
            val isRecipePresentInFavorite =
                CookingAppUtils.checkIfRecipePresentInFavorite(recipeExecutionViewModel)
            var isRecipePresentThriceInHistory = false

            if (!isRecipePresentInFavorite) {
                isRecipePresentThriceInHistory =
                    CookingAppUtils.checkIfRecipePresentInHistory(recipeExecutionViewModel)
            }

            if ((!isRecipePresentInFavorite) && (isRecipePresentThriceInHistory)) {
                NotificationManagerUtils.addNotificationToQueue(NotificationJsonKeys.NOTIFICATION_SAVE_LAST_RECIPE_FAVORITE)
            }
        }
    }

    /**
     * To show resume timer popup on Door Close event happened on Other screens while cavity is Running
     */
    fun showDoorOpenClosePopupBasedOnDoor(){
        val cookingViewModel  = CookingViewModelFactory.getPrimaryCavityViewModel()
        if(sharedViewModel?.isCycleInPausedStateGracePeriod() == false && cookingViewModel.isOfTypeMicrowaveOven && cookingViewModel.recipeExecutionViewModel.isMagnetronUsed &&
            cookingViewModel.doorState.value == false &&
            (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED ||
                    cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.PAUSED_EXT)){
            NavigationUtils.getVisibleFragment()?.let {
                PopUpBuilderUtils.mwoDoorOpenPopup(
                    it,
                    CookingViewModelFactory.getPrimaryCavityViewModel(),
                    onDoorCloseEventAction = {
                        onResume()
                    }
                )
            }
        }
    }

    /** ======================================================================= #
    # =========================  Transition Animation ========================= #
    # ======================================================================= */

    /**
     * Animate enter, exit and retun share element animation on views
     */
    private fun animateSharedElementView() {
        val enterTransitionListener = TransitionListener.onTransitionListener(
            onTransitionStart = {
                if(isAdded){
                    fadeInOutHeaderBar()
                    HMILogHelper.Logd("FAR_ANIM","---------- Enter onTransitionStart --------- ${identifyWhichCavityRunning()}")
                    when (identifyWhichCavityRunning()) {
                        AppConstants.CAVITY_RUNING_UPPER -> slideBottomEnterUpperViews()
                        AppConstants.CAVITY_RUNING_LOWER -> slideTopEnterLowerViews()
                        AppConstants.CAVITY_RUNING_BOTH -> slideBottomEnterLowerAndUpperViews()
                    }
                }
            },
            onTransitionEnd = {
                HMILogHelper.Logd("FAR_ANIM","---------- Enter onTransitionEnd --------- ")
                fadeInOutHeaderBar()
                resetAnimationTransition()
            }
        )

        HMILogHelper.Logd("FAR_ANIM","-------- is Far Anim Needed --------- $shouldApplySharedElementTransition")
        when {
            shouldApplySharedElementTransition -> {
                //shared progress bar transition animation
                sharedElementEnterTransition = TransitionInflater.from(requireContext())
                    .inflateTransition(android.R.transition.move)

                //shared progress bar reenter into screen transition animation
                reenterTransition = TransitionInflater.from(requireContext())
                    .inflateTransition(android.R.transition.no_transition)
                    ?.addListener(enterTransitionListener)


                //shared progress bar exit from screen transition animation
                exitTransition = when (identifyWhichCavityRunning()) {
                    AppConstants.CAVITY_RUNING_LOWER -> slideTopExitAndFadeAnimation()
                    AppConstants.CAVITY_RUNING_UPPER -> slideBottomExitAndFadeTransition()
                    AppConstants.CAVITY_RUNING_BOTH -> slideBottomExitAndFadeBothCavityTransition()
                    else -> slideBottomExitAndFadeTransition()
                }
            }
            else -> {
                resetAnimationTransition()
            }
        }
        shouldApplySharedElementTransition = false
    }

    /**
     * Reset default animation for views.
     */
    private fun resetAnimationTransition() {
        shouldApplySharedElementTransition = false
        sharedElementEnterTransition = null
        reenterTransition = null
        exitTransition = null
    }

    /**
     * Fade In and Fade out Header bar Animation
     */
    private fun fadeInOutHeaderBar(fadingMode:Int = Fade.IN) {
        val headerBarView = provideViewHolderHelper().provideHeaderBarWidget() as ViewGroup
        val fade = Fade(fadingMode).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            addTarget(headerBarView) // Specify the view to fade out
        }
        TransitionManager.beginDelayedTransition(headerBarView, fade) // parentView = container layout
        if(fadingMode == Fade.OUT) headerBarView.gone() else headerBarView.visible()

    }
    /**
     * Method is responsible for slide top animation and fade out animation together
     * Lower cavity view animation
     */
    private fun slideTopExitAndFadeAnimation():TransitionSet {
        val statusTopView = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusTopTextContentView() as View
        val statusBottomView = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusBottomOptionsView() as View
        val headerBarView = provideViewHolderHelper().provideHeaderBarWidget() as ViewGroup as View
        val upperCavityButton = provideViewHolderHelper().provideUpperCavitySelectionLayout() as View

        val slideTopAnimation = CustomSlideTop(AppConstants.FAR_VIEW_SLIDE_50).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = DecelerateInterpolator()
            addTarget(statusTopView)
            addTarget(statusBottomView)
            addTarget(upperCavityButton)
        }
        val fadeOut = Fade(Fade.OUT).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = DecelerateInterpolator()
            addTarget(statusTopView)
            addTarget(statusBottomView)
            addTarget(headerBarView)
            addTarget(upperCavityButton)
        }
        val slideUpAndFadeTransition = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(slideTopAnimation)
            addTransition(fadeOut)
        }
        return slideUpAndFadeTransition
    }

    /**
     * Method is responsible for slide bottom animation and fade out animation together
     * Upper cavity view animation
     */
    private fun slideBottomExitAndFadeTransition():TransitionSet {
        val statusTopView = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusTopTextContentView() as View
        val statusBottomView = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusBottomOptionsView() as View
        val headerBarView = provideViewHolderHelper().provideHeaderBarWidget() as ViewGroup as View
        val upperCavityButton = provideViewHolderHelper().provideLowerCavitySelection() as View

        val customSlideBottom = CustomSlideBottom(AppConstants.FAR_VIEW_SLIDE_50).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = DecelerateInterpolator()
            addTarget(statusTopView)
            addTarget(statusBottomView)
            addTarget(upperCavityButton)
        }
        val fadeOut = Fade(Fade.OUT).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = DecelerateInterpolator()
            addTarget(statusTopView)
            addTarget(statusBottomView)
            addTarget(headerBarView)
            addTarget(upperCavityButton)
        }
        val transitionSet = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(customSlideBottom)
            addTransition(fadeOut)
        }
        return transitionSet
    }
    /**
     * Method is responsible for slide bottom animation and fade out animation together
     * Upper cavity/Lower Cavity view animation
     */
    private fun slideBottomExitAndFadeBothCavityTransition():TransitionSet {
        val upperStatusTopView = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusTopTextContentView() as View
        val upperStatusBottomView = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusBottomOptionsView() as View
        val upperProgressBar = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar() as View
        val upperProgressBarInfinite = provideViewHolderHelper().getDefaultCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite() as View

        val lowerStatusTopView = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusTopTextContentView() as View
        val lowerStatusBottomView = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusBottomOptionsView() as View
        val lowerProgressBar = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar() as View
        val lowerProgressBarInfinite = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite() as View

        val headerBarView = provideViewHolderHelper().provideHeaderBarWidget() as ViewGroup as View

        val durationAnim = AppConstants.FAR_VIEW_ANIMATION_600
        val slideTopUpperAnimation = CustomSlideBottom(AppConstants.FAR_VIEW_SLIDE_50).apply {
            duration = durationAnim
            interpolator = DecelerateInterpolator()
            addTarget(upperStatusTopView)
            addTarget(upperStatusBottomView)
        }
        val slideBottomLowerAnimation = CustomSlideBottom(AppConstants.FAR_VIEW_SLIDE_50).apply {
            duration = durationAnim
            interpolator = DecelerateInterpolator()
            addTarget(lowerStatusTopView)
            addTarget(lowerStatusBottomView)
        }
        val progressAnimDuration = AppConstants.FAR_VIEW_ANIMATION_700
        val slideTopUpperProgressViewAnimation = CustomSlideBottom(AppConstants.FAR_VIEW_SLIDE_41).apply {
            duration = progressAnimDuration
            interpolator = DecelerateInterpolator()
            addTarget(upperProgressBar)
            addTarget(upperProgressBarInfinite)
        }
        val slideBottomLowerProgressViewAnimation = CustomSlideBottom(AppConstants.FAR_VIEW_SLIDE_33).apply {
            duration = progressAnimDuration
            interpolator = DecelerateInterpolator()
            addTarget(lowerProgressBar)
            addTarget(lowerProgressBarInfinite)
        }


        val fadeOut = Fade(Fade.OUT).apply {
            duration = durationAnim
            interpolator = DecelerateInterpolator()
            addTarget(upperStatusTopView)
            addTarget(upperStatusBottomView)
            addTarget(headerBarView)
            addTarget(lowerStatusTopView)
            addTarget(lowerStatusBottomView)
        }
        val fadeIn = Fade(Fade.IN).apply {
            duration = durationAnim
            interpolator = DecelerateInterpolator()
            addTarget(upperProgressBar)
            addTarget(upperProgressBarInfinite)

            addTarget(lowerProgressBar)
            addTarget(lowerProgressBarInfinite)
        }

        val transitionSet = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(slideTopUpperAnimation)
            addTransition(slideBottomLowerAnimation)
            addTransition(slideTopUpperProgressViewAnimation)
            addTransition(slideBottomLowerProgressViewAnimation)
            addTransition(fadeOut)
            addTransition(fadeIn)
        }
        return transitionSet
    }


    /**
     * Method is responsible for slide bottom animation and fade out animation together
     * Upper cavity and lower cavity
     */
    private fun slideBottomEnterLowerAndUpperViews() {
        HMILogHelper.Logd("FAR_ANIM","---------- slide Bottom Upper/Lower Views ---------")
        val animator = AnimatorSet()
        val slideUpAnimators: ArrayList<ObjectAnimator> = arrayListOf()
        val fadeInAnimators:ArrayList<ObjectAnimator>  = arrayListOf()
        val animationList:ArrayList<View> = arrayListOf()

        val viewHelper = provideViewHolderHelper()
        //Upper Status Top and  Content Views
        val view1 = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusTopTextContentView()
        val view2 = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusBottomOptionsView()
        val view5 = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()
        val view6 = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()

        // Lower Status Top Content Views
        val view3 = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusTopTextContentView()
        val view4 = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusBottomOptionsView()
        val view7 = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()
        val view8 = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()

        //Resume Cooking View
        val view9 = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking()
        val view10 = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking()

        animationList.add(view1 as View)
        animationList.add(view2 as View)
        animationList.add(view3 as View)
        animationList.add(view4 as View)
        animationList.add(view5 as View)
        animationList.add(view6 as View)
        animationList.add(view7 as View)
        animationList.add(view8 as View)
        animationList.add(view9 as View)
        animationList.add(view10 as View)

        for (i in 0 until animationList.size){
            val element = animationList[i]
            when {
                i >=AppConstants.DIGIT_FOUR -> {
                    //Progress bar and Inifinte animation
                    slideUpAnimators.add(ObjectAnimator.ofFloat(element, View.TRANSLATION_Y, AppConstants.FAR_VIEW_SLIDE_40, AppConstants.FAR_VIEW_FADE_OUT))
                }
                else -> {
                    //Other view animation
                    slideUpAnimators.add(ObjectAnimator.ofFloat(element, View.TRANSLATION_Y, AppConstants.FAR_VIEW_SLIDE_100, AppConstants.FAR_VIEW_FADE_OUT))
                }
            }
            fadeInAnimators.add(ObjectAnimator.ofFloat(element, View.ALPHA, AppConstants.FAR_VIEW_FADE_OUT, AppConstants.FAR_VIEW_FADE_IN))
        }

        animator.playTogether(slideUpAnimators + fadeInAnimators)

        animator.duration = AppConstants.FAR_VIEW_ANIMATION_350
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    /**
     * Method is responsible for slide bottom animation and fade in animation together
     * Upper cavity
     */
    private fun slideBottomEnterUpperViews() {
        val viewHelper = provideViewHolderHelper()
        val cavityType = identifyWhichCavityRunning()
        HMILogHelper.Logd("FAR_ANIM","---------- slide Bottom Upper Views cavityType--------- $cavityType")
        val animator = AnimatorSet()
        val animationList:ArrayList<View> = arrayListOf()
        val slideAnimators:ArrayList<ObjectAnimator> = arrayListOf()
        val fadeAnimators:ArrayList<ObjectAnimator> = arrayListOf()


        val view1 = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusTopTextContentView()
        val view2 = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusBottomOptionsView()
        val view4 = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking()
        val view3:View?

        animationList.add(view1 as View)
        animationList.add(view2 as View)
        animationList.add(view4 as View)

        when (cavityType) {
            AppConstants.CAVITY_RUNING_LOWER -> {
                view3 = viewHelper.provideUpperCavitySelectionLayout()
                animationList.add(view3 as View)
            }
            AppConstants.CAVITY_RUNING_UPPER -> {
                view3 = viewHelper.provideLowerCavitySelectionLayout()
                animationList.add(view3 as View)
            }
        }
        for (i in 0 until animationList.size){
            val element = animationList[i]
            slideAnimators.add(ObjectAnimator.ofFloat(element, View.TRANSLATION_Y, AppConstants.FAR_VIEW_SLIDE_DISTANCE_UPPER, AppConstants.FAR_VIEW_FADE_OUT))
            fadeAnimators.add(ObjectAnimator.ofFloat(element, View.ALPHA, AppConstants.FAR_VIEW_FADE_OUT, AppConstants.FAR_VIEW_FADE_IN))
        }

        animator.playTogether(slideAnimators + fadeAnimators)
        animator.duration = AppConstants.FAR_VIEW_ANIMATION_350
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    /**
     * Method is responsible for slide top animation and fade in animation together
     * Lower cavity
     */
    private fun slideTopEnterLowerViews() {
        val viewHelper = provideViewHolderHelper()
        val cavityType = identifyWhichCavityRunning()
        HMILogHelper.Logd("FAR_ANIM","---------- slide Top Lower Views cavityType--------- $cavityType")
        val animator = AnimatorSet()
        val animationList:ArrayList<View> = arrayListOf()
        val slideAnimators:ArrayList<ObjectAnimator> = arrayListOf()
        val fadeAnimators:ArrayList<ObjectAnimator> = arrayListOf()

        val view1 = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusTopTextContentView()
        val view2 = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusBottomOptionsView()
        val view4 = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.tvResumeCooking()
        val view3:View?

        animationList.add(view1 as View)
        animationList.add(view2 as View)
        animationList.add(view4 as View)

        when (cavityType) {
            AppConstants.CAVITY_RUNING_LOWER -> {
                view3 = viewHelper.provideUpperCavitySelectionLayout()
                animationList.add(view3 as View)
            }
            AppConstants.CAVITY_RUNING_UPPER -> {
                view3 = viewHelper.provideLowerCavitySelectionLayout()
                animationList.add(view3 as View)
            }
        }

        for (i in 0 until animationList.size){
            val element = animationList[i]
            slideAnimators.add(ObjectAnimator.ofFloat(element, View.TRANSLATION_Y, AppConstants.FAR_VIEW_SLIDE_DISTANCE_LOWER, AppConstants.FAR_VIEW_FADE_OUT))
            fadeAnimators.add(ObjectAnimator.ofFloat(element, View.ALPHA, AppConstants.FAR_VIEW_FADE_OUT, AppConstants.FAR_VIEW_FADE_IN))
        }

        animator.playTogether(slideAnimators + fadeAnimators)
        animator.duration = AppConstants.FAR_VIEW_ANIMATION_350
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    /**
     * Methos is resopnsible for providing shared element transition animation view
     * @return Upper cavity, lower cavity and Both cavity
     */
    private fun getTransitionSharedElementViews():ArrayList<Pair<View, String>> {
        val animationList:ArrayList<Pair<View, String>> = ArrayList()

        HMILogHelper.Logd("FAR_ANIM","------ Cavity type For Transition = ${identifyWhichCavityRunning()}")

        when (identifyWhichCavityRunning()) {
            AppConstants.CAVITY_RUNING_LOWER -> {
                //Lower Cavity Transition name set dynamically
                animationList.addAll(addTransionNameToLowerCavityViews(isBothCavityRunning = false))
            }
            AppConstants.CAVITY_RUNING_UPPER -> {
                //Upper Cavity Transition name set dynamically
                animationList.addAll(addTransitionNameToUpperCavityViews())
            }
            AppConstants.CAVITY_RUNING_BOTH -> {
                //Lower Cavity Transition name set dynamically
                animationList.addAll(addTransionNameToLowerCavityViews(isBothCavityRunning = true))
                //Upper Cavity Transition name set dynamically
                animationList.addAll(addTransitionNameToUpperCavityViews())
            }
        }
        return animationList
    }

    /**
     * Methos is resopnsible for providing shared element transition animation view
     * @return Upper cavity
     */
    private fun addTransitionNameToUpperCavityViews(): ArrayList<Pair<View, String>> {
        //Upper Cavity Transition name set dynamically
        val animationList:ArrayList<Pair<View, String>> = ArrayList()
        val viewHelper = provideViewHolderHelper()

        //Views
        val upperCavityProgressBar = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()
        val upperCavityProgressBarInfinite = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()

        //Transition Names
        upperCavityProgressBar?.transitionName = getString(R.string.transition_progress_bar)
        upperCavityProgressBarInfinite?.transitionName = getString(R.string.transition_progress_bar_lottie)

        //Add Into ArrayList
        animationList.add(Pair(upperCavityProgressBar as View, upperCavityProgressBar.transitionName))
        animationList.add(Pair(upperCavityProgressBarInfinite as View, upperCavityProgressBarInfinite.transitionName))

        return animationList
    }

    /**
     * Methos is resopnsible for providing shared element transition animation view
     * @return lower cavity
     */
    private fun addTransionNameToLowerCavityViews(isBothCavityRunning:Boolean): ArrayList<Pair<View, String>> {
        //Lower Cavity Transition name set dynamically
        val animationList:ArrayList<Pair<View, String>> = ArrayList()
        val viewHelper = provideViewHolderHelper()

        //Views
        val lowerCavityProgressBar = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()
        val lowerCavityProgressBarInfinite = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()

        //Transition Names
        val progressBarTransitionName = if(isBothCavityRunning) getString(R.string.transition_progress_bar_both) else getString(R.string.transition_progress_bar)
        val progressBarInfiniteTransitionName = if(isBothCavityRunning) getString(R.string.transition_progress_bar_lottie_both) else getString(R.string.transition_progress_bar_lottie)

        //Assigned Transition Names
        lowerCavityProgressBar?.transitionName = progressBarTransitionName
        lowerCavityProgressBarInfinite?.transitionName = progressBarInfiniteTransitionName

        //Add Into ArrayList
        animationList.add(Pair(lowerCavityProgressBar as View,lowerCavityProgressBar.transitionName))
        animationList.add(Pair(lowerCavityProgressBarInfinite as View,lowerCavityProgressBarInfinite.transitionName))
        return animationList
    }

    /**
     * Methos is resopnsible for providing which cavity is runing state
     * @return Upper cavity, Lower cavity and Both cavity
     */
    private fun identifyWhichCavityRunning():Int {
        var cavityRuning = AppConstants.CAVITY_RUNING_UPPER
        val viewHelper = provideViewHolderHelper()
        when {
            viewHelper.getDefaultCookingStatusWidget() != null
                    && viewHelper.getLowerCookingStatusWidget() != null -> cavityRuning = AppConstants.CAVITY_RUNING_BOTH
            viewHelper.getDefaultCookingStatusWidget() != null -> cavityRuning = AppConstants.CAVITY_RUNING_UPPER
            viewHelper.getLowerCookingStatusWidget() != null -> cavityRuning = AppConstants.CAVITY_RUNING_LOWER
        }
        return cavityRuning
    }

    /**
     * Navigate to far view with transition animation
     */
    private fun navigateToFarViewWithTransitionAnimation() {
        shouldApplySharedElementTransition = true
        animateSharedElementView()
        val sharedElements = getTransitionSharedElementViews()
        val extras = FragmentNavigatorExtras(*sharedElements.distinct().toTypedArray())
        handlerUpperCavity.removeCallbacksAndMessages(null)
        handlerLowerCavity.removeCallbacksAndMessages(null)
        CookingAppUtils.setProgreeBarDetails(provideProgressDetails())
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                HMILogHelper.Logd("FAR_ANIM","---------- FragmentNavigatorExtras  isAdded---------$isAdded ")
                if (isAdded) {
                    Navigation.findNavController(this.requireView())
                        .navigate(provideFarViewNavigationId(), arguments, null, extras)
                }
            } catch (e: Exception) {
                HMILogHelper.Logd("FAR_ANIM","---------- Navigator  Error Handling--------- ${e.message}")
                //If FragmentNavigatorExtras giving any runtime error then we skip the share element and navigate to far view
                if (isAdded) {
                    Navigation.findNavController(this.requireView())
                        .navigate(provideFarViewNavigationId(), arguments, null, null)
                }
            }
        }, AppConstants.FAR_VIEW_ANIMATION_1000)
    }
    /**
     * cancel event listener when click event detected from Far view and perform operation on Near view
     */
    private fun farViewTransitionFragmentResultListener() {
        // listener for far view fragment transiton key event
        requireActivity().let {
            it.supportFragmentManager.setFragmentResultListener(
                BundleKeys.BUNDLE_APPLY_TRANSITION_ANIMATION,
                this
            ) { _, bundle ->
                val result = bundle.getBoolean(BundleKeys.BUNDLE_APPLY_TRANSITION_ANIMATION)
                HMILogHelper.Logd("FAR_ANIM","---------- Transition Callback Result --------- $result")
                shouldApplySharedElementTransition = result
                animateSharedElementView()
                //Handling Lottie animation not showing during transition between far view
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded) {
                        val viewHelper = provideViewHolderHelper()
                        viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.invalidate()
                        viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.invalidate()
                    }
                }, AppConstants.FAR_VIEW_ANIMATION_1000)
            }
        }
    }

    private fun provideProgressDetails():Pair<Boolean,Boolean>{
        val viewHelper = provideViewHolderHelper()
        when (identifyWhichCavityRunning()) {
            AppConstants.CAVITY_RUNING_UPPER -> {
                val isProgressBarVisible = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.isVisible == true
                val isProgressBarInifiniteVisible = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.isVisible == true
                return Pair(isProgressBarVisible,isProgressBarInifiniteVisible)
            }
            AppConstants.CAVITY_RUNING_LOWER -> {
                val isProgressBarVisible = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.isVisible == true
                val isProgressBarInifiniteVisible = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.isVisible == true
                return Pair(isProgressBarVisible,isProgressBarInifiniteVisible)
            }
            AppConstants.CAVITY_RUNING_BOTH -> {
                val isUpperProgressBarVisible = viewHelper.getDefaultCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.isVisible == true
                val isLowerProgressBarVisible = viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.isVisible == true
                return Pair(isUpperProgressBarVisible,isLowerProgressBarVisible)

            }
        }
        return Pair(first = true,second = true)
    }
    /** ============================================================================= #
    # =========================  Transition Animation END =========================== #
    # ============================================================================== */

}
