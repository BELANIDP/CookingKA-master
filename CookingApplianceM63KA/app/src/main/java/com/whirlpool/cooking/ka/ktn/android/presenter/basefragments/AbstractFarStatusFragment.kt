package android.presenter.basefragments

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.basefragments.abstract_view_helper.AbstractFarStatusViewHelper
import android.presenter.basefragments.abstract_view_helper.AbstractFarStatusWidgetHelper
import android.presenter.customviews.widgets.status.CookingFarStatusWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.airbnb.lottie.LottieAnimationView
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.step.Notification
import com.whirlpool.hmi.cooking.model.capability.recipe.step.UserInstruction
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.utils.RecipeProgressBasis
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModel
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.CapabilityKeys
import com.whirlpool.hmi.utils.timers.Timer
import core.utils.AppConstants
import core.utils.AppConstants.FAR_VIEW_KITCHEN_TIMER_DELAY
import core.utils.AppConstants.USER_INSTRUCTION_PREHEAT_COMPLETED
import core.utils.BundleKeys
import core.utils.CommonAnimationUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.setKnobLightWhenCycleRunning
import core.utils.HMIExpansionUtils
import core.utils.HMIExpansionUtils.Companion.isSlowBlinkingKnobTimeoutActive
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.TimeUtils
import core.utils.gone
import core.utils.invisible
import core.utils.transition.CustomSlideBottom
import core.utils.transition.TransitionListener
import core.utils.visible
import java.util.LinkedList
import java.util.Locale
import java.util.Queue
import java.util.concurrent.TimeUnit

/**
 * File        : android.presenter.basefragments.AbstractFarStatusFragment
 * Brief       : Abstract class for managing binding data for Far Status screen
 * Author      : Hiren
 * Created On  : 05/10/2024
 * Details     : Extends this class for displaying Single or Double Far Status screen
 */
abstract class AbstractFarStatusFragment : Fragment(),
    HMIExpansionUtils.UserInteractionListener, HMIExpansionUtils.HMICancelButtonInteractionListener,MeatProbeUtils.MeatProbeListener {

    /**
     * lock variable to keep track of whether Near view has been popped or not via backstack,
     * otherwise onUserInteraction is going to call multiple times either on touch or knob events and keep continue to pop the stack
     */
    private var isNearViewPoppedViaBackStack = false
    protected abstract fun provideViewHolderHelper(): AbstractFarStatusViewHelper

    /**
     * provide visibility of cycle completed since after recipe is completed
     * Visible only for single status fragment
     * @return VISIBLE or GONE
     */
    protected abstract fun provideVisibilityOfCompletedSinceTextView(): Int

    /**
     * provide visibility of cavity Icon
     * Visible only for single status fragment
     * @return VISIBLE or GONE
     */
    protected abstract fun provideVisibilityOfCavityIcon(): Int

    private lateinit var timeoutViewModel: TimeoutViewModel
    /**
     * used to store temporary value of door state based on cavity index 0=primary, 1=secondary
     * useful to detect the event in case if the door is open for that cavity and original state is
     * not same then move to Near Status Screen
     */
    private val doorLastOpenStateForCavity: BooleanArray = BooleanArray(2)

    private var handler: Handler = Handler(Looper.getMainLooper())
    private var timerQueue: Queue<KitchenTimerViewModel> = LinkedList()
    private var isTimerActive = false
    private var isMultipleKTRunning : Boolean = false


    //handler for upper oven cavity
    private val handlerUpperCavity = Handler(Looper.getMainLooper())

    //handler for lower oven cavity
    private val handlerLowerCavity = Handler(Looper.getMainLooper())

    private var isTransitionAnimationRunning = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        provideViewHolderHelper().onCreateView(inflater, container, savedInstanceState)
        provideViewHolderHelper().getLayoutViewBinding()?.lifecycleOwner = this
        provideViewHolderHelper().setupBindingData(this)
        provideProgressVisibility()
        return provideViewHolderHelper().getLayoutViewBinding()?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startTransitionAnimation()
        MeatProbeUtils.setMeatProbeListener(this)
        HMIExpansionUtils.setFragmentUserInteractionListener(this)
        HMIExpansionUtils.setHMICancelButtonInteractionListener(this)
        manageChildViews()
        timeoutViewModel = ViewModelProvider(this)[TimeoutViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        if (KitchenTimerVMFactory.getKitchenTimerViewModels()?.any { it.isRunning } == true){
            isMultipleKTRunning = (KitchenTimerUtils.isKitchenTimersRunning()>1)
            observeKitchenTimer()
        }
    }

    /**
     * manage child view related activities here
     */
    protected open fun manageChildViews() {
        //For double/single oven no need to show cavity icon. In Double oven upper/lower caivity need to show cavity icon
        val isCavityIconVisible = provideVisibilityOfCavityIcon() == View.VISIBLE
        provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.provideVisibilityOfCavityIcon(isCavityIconVisible)
        provideViewHolderHelper().getUpperCookingStatusWidget()?.statusWidgetHelper?.provideVisibilityOfCavityIcon(isCavityIconVisible)
        doorInteractionState()
    }
    /**
     * door interaction observer, if the door is open and the value is not same as when Fragment was created then move to recipe selection for that cavity
     * @param cookingViewModel
     */
    protected fun observeDoorInteraction(cookingViewModel: CookingViewModel) {
        cookingViewModel.doorState.observe(this) {
            val cavity = if (cookingViewModel.isPrimaryCavity) 0 else 1
            HMILogHelper.Logd(
                tag,
                "current door: $it last door status = primary: ${doorLastOpenStateForCavity[0]} secondary: ${doorLastOpenStateForCavity[1]}"
            )
            if (it != doorLastOpenStateForCavity[cavity]) {
                HMILogHelper.Logd(
                    tag,
                    "door opened for ${cookingViewModel.cavityName.value}, moving to Near View Status Fragment"
                )
                onUserInteraction()
                if (isSlowBlinkingKnobTimeoutActive()) HMIExpansionUtils.userInteractWithinSlowBlinkingTimeoutElapsed()
            }
        }
    }

    /**
     * Storing current state of the door by updating doorLastOpenStateForCavity variable based on cavity
     */
    private fun doorInteractionState(){
        //init the variable based on door state value
        doorLastOpenStateForCavity[0] =
            CookingViewModelFactory.getPrimaryCavityViewModel().doorState.value == true
        if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO ||
            CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
            doorLastOpenStateForCavity[1] =
                CookingViewModelFactory.getSecondaryCavityViewModel().doorState.value == true
        }
    }

    override fun onDestroyView() {
        MeatProbeUtils.removeMeatProbeListener()
        HMIExpansionUtils.removeFragmentUserInteractionListener(this)
        HMIExpansionUtils.removeHMICancelButtonInteractionListener(this)
        provideViewHolderHelper().onDestroyView()
        isNearViewPoppedViaBackStack = false
        super.onDestroyView()
    }

    override fun onHMICancelButtonInteraction() {
        requireActivity().supportFragmentManager.setFragmentResult(HmiExpansionViewModel.FUNCTION_CANCEL, bundleOf(HmiExpansionViewModel.FUNCTION_CANCEL to true))
        HMILogHelper.Logd(tag, "setFragmentResult onHMICancelButtonInteraction")
    }

    /**
     * Calls whenever any user interaction has been notices, either by pressing button, touching LCD of opening door
     *  Moving to NearView by popping the back stack
     */
    override fun onUserInteraction() {
        HMILogHelper.Logd("FAR_ANIM", "---------- onUserInteraction  Transition Running--------- $isTransitionAnimationRunning ")
        //During transition animation running handiling the back navigation. Allow only once animation get finish
        if(!isNearViewPoppedViaBackStack && !isTransitionAnimationRunning) {
            //Send back shared transition element should apply or not
            requireActivity().supportFragmentManager.setFragmentResult(
                BundleKeys.BUNDLE_APPLY_TRANSITION_ANIMATION, bundleOf(BundleKeys.BUNDLE_APPLY_TRANSITION_ANIMATION to true))
            //only enter here if Near view has not been popped, and update the state
            isNearViewPoppedViaBackStack = NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    NavigationUtils.getViewSafely(
                        this
                    ) ?: requireView()
                )
            )
            HMILogHelper.Logd(tag, "far view onUserInteraction, isNearViewPoppedViaBackStack=$isNearViewPoppedViaBackStack")
        }else{
            HMILogHelper.Logd(tag, "far view onUserInteraction, far view is already popped")
        }
    }

    /**
     * Method to call to initiate timeout when recipe is completed
     */
    private fun initTimeoutForCompleteCycle(cookingVM: CookingViewModel) {
        val activeTimeoutObserver = timeoutViewModel.timeoutCallback?.hasActiveObservers()
        HMILogHelper.Logd(
            tag,
            "hasActiveObservers: $activeTimeoutObserver for ${cookingVM.cavityName.value}"
        )
        if (activeTimeoutObserver == false) {
            timeoutViewModel.timeoutCallback?.observe(
                viewLifecycleOwner
            ) { timeoutStatesEnum: TimeoutViewModel.TimeoutStatesEnum ->
                HMILogHelper.Logd("TimeoutCallback: " + timeoutStatesEnum.name + " for ${cookingVM.cavityName.value}")
                if (timeoutStatesEnum == TimeoutViewModel.TimeoutStatesEnum.TIMEOUT_ELAPSED && cookingVM.recipeExecutionViewModel.cookTimerState.value == Timer.State.COMPLETED) {
                    HMILogHelper.Logd("TimeoutCallback: " + timeoutStatesEnum.name + " for ${cookingVM.cavityName.value} cancelling recipeExecutionViewModel")
                    cookingVM.recipeExecutionViewModel.cancel()
                }
            }
            timeoutViewModel.setTimeout(
                resources.getInteger(R.integer.timeout_far_to_near_view_status_completed_in_sec)
            )
        }
    }

    override fun onStop() {
        super.onStop()
        timeoutViewModel.stop()
        KitchenTimerUtils.removeKitchenTimerListener(this)
    }

    companion object {
        private const val TAG = "AbstractFarStatusFragment"

        /**
         * Cavity position to load the Cooking View Model for Double or Combo variant if only one cavity is running
         */
        const val KEY_STATUS_SCREEN_CAVITY_POSITION = "cavityPosition"

        /**
         * Binding adapter method for updating live data with recipe execution
         */
        @BindingAdapter(value = ["far_status_execution_fragment", "far_status_execution_view_model", "far_status_execution_state"])
        @JvmStatic
        fun bindRecipeExecutionState(
            statusWidget: CookingFarStatusWidget,
            abstractStatusFragment: AbstractFarStatusFragment,
            cookingVM: CookingViewModel,
            recipeExecutionState: RecipeExecutionState,
        ) {
            HMILogHelper.Logd("FAR_VIEW", "---------- bind Recipe Execution State ---------- ")
            HMILogHelper.Logi(
                "$TAG ${statusWidget.ovenType}",
                "recipeExecutionState ${recipeExecutionState.name} for cavity ${cookingVM.cavityName.value}"
            )
            //if recipe execution is not running move to NearView
            if (cookingVM.recipeExecutionViewModel.isNotRunning) {
                HMILogHelper.Logd(
                    TAG,
                    "recipeExecutionViewModel isNotRunning for cavity ${cookingVM.cavityName.value} moving to near view fragment"
                )
                abstractStatusFragment.onUserInteraction()
                return
            }
            if(recipeExecutionState == RecipeExecutionState.DELAYED){
                // update UI for delay state
                abstractStatusFragment.updateUIForDelayState(statusWidget.statusWidgetHelper, cookingVM)
            }
            if(cookingVM.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.CLEANING){
                HMILogHelper.Logd("FAR_VIEW", "---------- SELF CLEAN FAR VIEW ---------- ")
                // update UI for Self clean flow
                abstractStatusFragment.updateUIForSelfCleanState(statusWidget.statusWidgetHelper, cookingVM)
            }
        }
        /**
         * Binding adapter method for updating live data for preheat complete notification
         */
        @BindingAdapter(value = ["far_status_preheat_fragment", "far_status_preheat_notification", "far_status_preheat_cook_view_model"])
        @JvmStatic
        fun bindPreheatOvenReadyState(
            statusWidget: CookingFarStatusWidget,
            abstractStatusFragment: AbstractFarStatusFragment,
            preheatCompleteNotificationText: Notification?,
            cookingVM: CookingViewModel,
        ) {
            HMILogHelper.Logi(
                "$TAG preHeat",
                "preheatCompleteNotificationText ${preheatCompleteNotificationText?.text}"
            )
            abstractStatusFragment.managePreheatOvenReadyState(
                statusWidget.statusWidgetHelper, cookingVM, preheatCompleteNotificationText
            )
        }
        /**
         * Binding adapter method updating recipe recipe name with temperature
         */
        @BindingAdapter(value = ["far_status_temperature_fragment", "far_status_temperature_cook_view_model", "far_status_temperature_cooking_state", "far_status_temperature_recipe_name", "far_status_temperature_display_temperature", "far_status_temperature_target_temperature"])
        @JvmStatic
        fun bindRecipeNameWithTemperature(
            statusWidget: CookingFarStatusWidget,
            abstractStatusFragment: AbstractFarStatusFragment,
            cookingVM: CookingViewModel,
            recipeCookingState: RecipeCookingState,
            recipeName: String,
            ovenDisplayTemperature: Int,
            targetTemperature: Int
        ) {
            HMILogHelper.Logd("FAR_VIEW", "---------- bind Recipe Name With Temperature ---------- ")
            HMILogHelper.Logi(
                "$TAG ${statusWidget.ovenType} bindRecipeNameWithTemperature",
                "ovenDisplayTemperature $ovenDisplayTemperature targetTemperature $targetTemperature recipeCookingState $recipeCookingState  recipeName $recipeName"
            )
            abstractStatusFragment.updateRecipeNameTextView(
                statusWidget.statusWidgetHelper,
                cookingVM,
                recipeCookingState,
                recipeName,
                ovenDisplayTemperature,
                targetTemperature
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
         * @param cookTimerState cook time state idle, running, etc
         * @param remainingTime to update cook time remaining text
         */
        @BindingAdapter(value = ["far_status_progress_bar_cook_view_model", "far_status_progress_bar_fragment", "far_status_progress_bar_progress_percentage", "far_status_progress_bar_basis", "far_status_progress_bar_cook_timer_state", "far_status_etr_cook_timer_remaining_cook_time", "far_status_progress_recipe_execution_state"])
        @JvmStatic
        fun bindCookingProgressBar(
            statusWidget: CookingFarStatusWidget,
            cookingVM: CookingViewModel,
            abstractStatusFragment: AbstractFarStatusFragment,
            percentageVal: Int?,
            cookProgressBasis: RecipeProgressBasis?,
            cookTimerState: Timer.State?,
            remainingTime: Long,
            recipeExecutionState: RecipeExecutionState
        ) {
            HMILogHelper.Logd("FAR_VIEW", "---------- bind Cooking Progress ---------- ")
            val statusProgressBar: ProgressBar? =
                statusWidget.statusWidgetHelper.getStatusProgressBar()
            if (null == cookProgressBasis || null == percentageVal || null == cookTimerState || null == statusProgressBar) {
                HMILogHelper.Loge("ProgressBar: SDK: cookProgressBasis or progressPercentage is NULL")
                statusProgressBar?.visibility = View.INVISIBLE
                return
            }
            HMILogHelper.Logi(
                "$TAG progressBar",
                "cookProgressBasis $cookProgressBasis  ProgressPercentage $percentageVal cookTimerState $cookTimerState recipeExecutionState $recipeExecutionState"
            )
            abstractStatusFragment.manageCookingProgressBar(
                cookingVM,
                statusWidget.statusWidgetHelper,
                percentageVal,
                cookProgressBasis,
                cookTimerState,
                remainingTime
            )
        }
        /**
         * Binding adapter method for updating live data when cook timer is completed and ready to update text of readySince
         */
        @BindingAdapter(value = ["far_status_cook_time_completed_fragment", "far_status_cook_time_completed_view_model", "far_status_cook_time_completed_since", "far_status_cook_time_probe_target_temperature_reached", "status_etr_cook_time_probe_current_temperature", "status_etr_cook_time_probe_target_temperature", "status_etr_cook_time_probe_connection_state"])
        @JvmStatic
        fun bindCookTimeCompletedSince(
            statusWidget: CookingFarStatusWidget,
            abstractStatusFragment: AbstractFarStatusFragment,
            cookingVM: CookingViewModel,
            readySince: Long,
            probeTargetTemperatureReached: Boolean?,
            probeCurrentTemperature: Int?,
            probeTargetTemperature: Int?,
            probeConnectionState: Boolean?,
        ) {
            HMILogHelper.Logd("FAR_VIEW", "---------- bind Cook Time Completed Since ---------- ")
            abstractStatusFragment.manageCookTimeCompletedSinceText(
                statusWidget,
                cookingVM,
                readySince,
                probeTargetTemperatureReached
            )
            if (cookingVM.isOfTypeOven && cookingVM.recipeExecutionViewModel.isProbeBasedRecipe) {
                abstractStatusFragment.manageProbeTemperature(
                    statusWidget.statusWidgetHelper,
                    cookingVM,
                    probeCurrentTemperature,
                    probeTargetTemperature,
                    probeConnectionState,
                )
            }
        }
        /**
         * Binding adapter method for updating live data with recipe execution
         */
        @BindingAdapter(value = ["far_status_user_instruction_fragment", "far_status_user_instruction_cook_view_model", "far_status_user_instruction"])
        @JvmStatic
        fun bindUserInstructionHelper(
            statusWidget: CookingFarStatusWidget,
            abstractStatusFragment: AbstractFarStatusFragment,
            cookingVM: CookingViewModel,
            userInstruction: UserInstruction
        ) {
            HMILogHelper.Logd("FAR_VIEW", "---------- bind User Instruction ---------- ")

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
    }

    /**
     * updating Far status view for delay state
     */
    fun updateUIForDelayState(statusWidgetHelper: AbstractFarStatusWidgetHelper, cookingVM: CookingViewModel){
        HMILogHelper.Logd("FAR_VIEW", "---------- DELAYED State ---------- ")

        val delayedTime = resources.getString(
            R.string.text_delay_farView, TimeUtils.getUntilReadyTime(
                cookingVM.recipeExecutionViewModel.remainingDelayTime.value ?: 0
            ).lowercase()
        )
        statusWidgetHelper.tvRecipeWithTemperature()?.text = delayedTime

        statusWidgetHelper.tvRecipeWithTemperature()?.visible()
        statusWidgetHelper.tvCookTimeRemaining()?.gone()

        statusWidgetHelper.getTemperatureRampIcon()?.gone()

        HMILogHelper.Logd("FAR_VIEW", "---------- DELAYED For Self Clean ---------- ${CookingAppUtils.isPyroliticClean()}")
        if(CookingAppUtils.isPyroliticClean() &&
            cookingVM.recipeExecutionViewModel.recipeName.value == CapabilityKeys.PYROLITIC_CLEAN_KEY){
            statusWidgetHelper.getTemperatureProbeIcon().visible()
            // Lock icon for self clean.
            statusWidgetHelper.getTemperatureProbeIcon()
                .setImageResource(R.drawable.ic_far_view_lock_icon)
        } else {
            statusWidgetHelper.getTemperatureProbeIcon().gone()
        }


        statusWidgetHelper.getStatusProgressBar()?.max = 0
        statusWidgetHelper.getStatusProgressBar()?.progress = 0
        makeIndefiniteProgressBarVisible(
            statusWidgetHelper.getStatusProgressBar(),
            statusWidgetHelper.getProgressbarInfinite(),
            false
        )
    }

    /**
     * updating Far status view for self clean state
     */
    fun updateUIForSelfCleanState(
        statusWidgetHelper: AbstractFarStatusWidgetHelper,
        cookingVM: CookingViewModel
    ) {

        val cookingState = cookingVM.recipeExecutionViewModel?.recipeCookingState?.value
        val progressState = cookingVM.recipeExecutionViewModel?.recipeProgressBasis?.value
        val doorState = CookingViewModelFactory.getInScopeViewModel().doorLockState.value

        HMILogHelper.Logd("FAR_VIEW", "---------- Self clean recipeCookingState ---------- $cookingState")
        HMILogHelper.Logd("FAR_VIEW", "---------- Self clean recipeProgressBasis ---------- $progressState")
        HMILogHelper.Logd("FAR_VIEW", "---------- Self clean doorState ---------- $doorState")

        when {
            cookingState == RecipeCookingState.CLEANING && progressState == RecipeProgressBasis.TIME -> {
                populateSelfCleanUI(cookingVM, statusWidgetHelper,isSelfCleanCompleted = false)

            }
            cookingState == RecipeCookingState.COOLING -> {
                populateSelfCleanUI(cookingVM, statusWidgetHelper,isSelfCleanCompleted = true)
            }
        }

    }

    private fun populateSelfCleanUI(
        cookingVM: CookingViewModel,
        statusWidgetHelper: AbstractFarStatusWidgetHelper,
        isSelfCleanCompleted:Boolean
    ) {
        val selfCleanRemainingTime =
            cookingVM.recipeExecutionViewModel?.remainingCookTime?.value ?: 0
        val progress = cookingVM.recipeExecutionViewModel?.recipeProgressPercentage?.value ?: 0

        statusWidgetHelper.tvCookTimeRemaining()?.visible()
        statusWidgetHelper.getTemperatureProbeIcon().gone()
        if (cookingVM.recipeExecutionViewModel.recipeName.value == CapabilityKeys.PYROLITIC_CLEAN_KEY) {
            statusWidgetHelper.getTemperatureProbeIcon().visible()
            // Lock icon for self clean.
            statusWidgetHelper.getTemperatureProbeIcon()
                .setImageResource(R.drawable.ic_far_view_lock_icon)
        }
        statusWidgetHelper.tvRecipeWithTemperature()?.gone()
        statusWidgetHelper.getTemperatureRampIcon()?.gone()

        val cookTimerString: String = TimeUtils.getTimeInHHMMSS(selfCleanRemainingTime)
        statusWidgetHelper.tvCookTimeRemaining()?.text = cookTimerString
        HMILogHelper.Logd("FAR_VIEW","---------- Self clean Time ---------- $cookTimerString")

        statusWidgetHelper.getStatusProgressBar()?.max = AppConstants.MAX_PROGRESS_VALUE
        statusWidgetHelper.getStatusProgressBar()?.progress = progress

        makeIndefiniteProgressBarVisible(
            statusWidgetHelper.getStatusProgressBar(),
            statusWidgetHelper.getProgressbarInfinite(),
            false
        )

        if(isSelfCleanCompleted){
            statusWidgetHelper.tvCookTimeRemaining()?.text = getString(R.string.text_cooling_down_self_clean)
            statusWidgetHelper.getStatusProgressBar()?.max = 0
            statusWidgetHelper.getStatusProgressBar()?.progress = 0
            makeIndefiniteProgressBarVisible(
                statusWidgetHelper.getStatusProgressBar(),
                statusWidgetHelper.getProgressbarInfinite(),
                true
            )
            lottieAnimationForceRestart(statusWidgetHelper.getStatusProgressBar(),statusWidgetHelper.getProgressbarInfinite())
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
        statusWidget: CookingFarStatusWidget,
        cookingVM: CookingViewModel,
        userInstruction: UserInstruction,
    ) {
        if (userInstruction.text.isNotEmpty() && userInstruction.isTypeEnumeration && !(userInstruction.containsExtraBrowningPrompt() || userInstruction.containsTimePrompt())) onUserInteraction()
    }

    /**
     * updating the text under far view progressbar when cook timer is completed,
     * only applicable when once cavity running regardless of which variants, not supposed to update when dual cavity running due to space constraints
     * @param readySince LiveData of cook timer completed, Ready since timer starts counting up from 0 after recipe completes in seconds
     */
    fun manageCookTimeCompletedSinceText(
        statusWidget: CookingFarStatusWidget,
        cookingVM: CookingViewModel,
        readySince: Long,
        probeTargetTemperatureReached: Boolean?,
    ) {
        HMILogHelper.Logd("FAR_VIEW", "---------- Manage Cook Time Completed Since Text ---------- ")
        HMILogHelper.Logd(
            tag,
            "${cookingVM.cavityName.value} recipe ready seconds: $readySince probeTargetTemperatureReached=$probeTargetTemperatureReached cookTimerState=${cookingVM.recipeExecutionViewModel.cookTimerState.value}"
        )
        if(cookingVM.recipeExecutionViewModel.isProbeBasedRecipe) return
        //if recipe cook time is completed and for single cavity running
        if (cookingVM.recipeExecutionViewModel.cookTimerState.value == Timer.State.COMPLETED || probeTargetTemperatureReached == true) {
            val readyMin = TimeUnit.SECONDS.toMinutes(readySince)
            statusWidget.statusWidgetHelper.tvCookTimeCompletedSince().gone()
            statusWidget.statusWidgetHelper.tvCookTimeRemaining()?.visible()
            statusWidget.statusWidgetHelper.tvCookTimeRemaining()?.text =
                CookingAppUtils.spannableETRRunning(
                    context,  0
                )
            val readySinceMin = AppConstants.EMPTY_STRING
            statusWidget.statusWidgetHelper.tvCookTimeCompletedSince().text = readySinceMin
            makeIndefiniteProgressBarVisible(
                statusWidget.statusWidgetHelper.getStatusProgressBar(),
                statusWidget.statusWidgetHelper.getProgressbarInfinite(),
                isVisible = true
            )
            if (readyMin >= AppConstants.RECIPE_TIMEOUT_COOKING_COMPLETE_10_MINUTES) {
                cookingVM.recipeExecutionViewModel.cancel()
                HMILogHelper.Logd(
                    tag,
                    "${cookingVM.cavityName.value} recipe ready: $readySinceMin timeout cancelling recipeExecutionViewModel"
                )
            }
        }
    }

    /**
     * after preheat completes change the recipe name text to OVEN READY
     */
    fun managePreheatOvenReadyState(statusWidget: AbstractFarStatusWidgetHelper, cookingVM: CookingViewModel, preheatCompleteNotificationText: Notification?){
        HMILogHelper.Logd("FAR_VIEW", "---------- Manage Preheat Oven Ready State ---------- ")
        HMILogHelper.Logd("FAR_VIEW", "----------preheatCompleteNotificationText ---------- ${preheatCompleteNotificationText?.text} ")

        if (cookingVM.recipeExecutionViewModel.isNotRunning || cookingVM.recipeExecutionViewModel.isProbeBasedRecipe) return
        val readySincePreHeatTimerState = cookingVM.recipeExecutionViewModel.readySincePreheatTimerState.value

        HMILogHelper.Logd("FAR_VIEW", "----------readySincePreheatTimerState ---------- $readySincePreHeatTimerState")

        if (preheatCompleteNotificationText?.text?.contentEquals(USER_INSTRUCTION_PREHEAT_COMPLETED) == true
            || readySincePreHeatTimerState?.equals(Timer.State.RUNNING) == true) {
            statusWidget.tvCookTimeRemaining()?.gone()
            statusWidget.getTemperatureProbeIcon().gone()
            val ovenDisplayTemperature = cookingVM.ovenDisplayTemperature.value?:0
            val targetTemperature = cookingVM.recipeExecutionViewModel.targetTemperature.value?:0
            HMILogHelper.Logd("FAR_VIEW", "----------ovenDisplayTemperature ---------- $ovenDisplayTemperature " +
                    "\n----------targetTemperature ---------- $targetTemperature")
            executeOvenReadyRunnable(cookingVM,statusWidget,ovenDisplayTemperature,targetTemperature)
        }
    }

    /**
     * managing cooking progress bar to update its value based on percentage received
     *
     * @param cookingVM cooking view model for a particular cavity
     * @param statusWidgetHelper to access the widget components
     * @param percentageVal percentage to be updated on progressbar
     * @param cookProgressBasis to find out whether the recipe is updating based on Temperature or Time
     * @param cookTimerState cooking timer state
     * @param remainingTime remaining time when cookProgressBasis is progressing based on Time
     */
    fun manageCookingProgressBar(cookingVM: CookingViewModel, statusWidgetHelper: AbstractFarStatusWidgetHelper, percentageVal: Int, cookProgressBasis: RecipeProgressBasis, cookTimerState: Timer.State, remainingTime: Long){
        HMILogHelper.Logd("FAR_VIEW", "---------- cookProgressBasis ---------- $cookProgressBasis ")
        statusWidgetHelper.tvRecipeWithTemperature()?.gone()
        statusWidgetHelper.getTemperatureRampIcon()?.gone()
        when (cookProgressBasis) {
            RecipeProgressBasis.TEMPERATURE -> {
                statusWidgetHelper.tvRecipeWithTemperature()?.visible()
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
            RecipeProgressBasis.VIRTUAL_CHEF ->{
                statusWidgetHelper.tvRecipeWithTemperature()?.gone()
                val cookTime = cookingVM.recipeExecutionViewModel.remainingDonenessTimeEstimate.value?.let {
                    TimeUtils.convertTimeToMinutes(
                        it
                    )
                }
                HMILogHelper.Logd("FAR_VIEW", "---------- cookTimerState ---------- $cookTime ")
                statusWidgetHelper.getStatusProgressBar()?.progress = percentageVal
                statusWidgetHelper.tvCookTimeRemaining()?.visibility = View.VISIBLE
                statusWidgetHelper.tvCookTimeRemaining()?.text = getString(R.string.text_vision_min, cookTime?.toInt())
                makeIndefiniteProgressBarVisible(
                    statusWidgetHelper.getStatusProgressBar(),
                    statusWidgetHelper.getProgressbarInfinite(),
                    false
                )
            }

            RecipeProgressBasis.TIME -> {
                HMILogHelper.Logd("FAR_VIEW", "---------- cookTimerState ---------- $cookTimerState ")
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

                    Timer.State.PAUSED,
                    Timer.State.RUNNING,
                    -> {
                        statusWidgetHelper.tvRecipeWithTemperature()?.gone()
                        if (cookingVM.isOfTypeOven && cookTimerState == Timer.State.PAUSED && cookingVM.recipeExecutionViewModel.recipeCookingState.value == RecipeCookingState.COOKING && cookingVM.recipeExecutionViewModel.readySincePreheatTimerState.value == Timer.State.RUNNING && cookingVM.recipeExecutionViewModel.cookTime.value != 0L) {
                            HMILogHelper.Loge(tag, "moving to near view for user input, as recipe is cooking, timer is paused and cookTime is SET")
                            onUserInteraction()
                            return
                        }
                        if (CookingAppUtils.isPyroliticClean()) {
                            HMILogHelper.Logd("FAR_VIEW", "---------- isPyroliticClean ---------- true ")
                            statusWidgetHelper.tvCookTimeRemaining()?.visible()
                            updateUIForSelfCleanState(statusWidgetHelper,cookingVM)
                            return
                        }

                        setKnobLightWhenCycleRunning()
                        if (CookingAppUtils.isTimePreheatRunning(cookingVM)) {
                            HMILogHelper.Logd("FAR_VIEW", "---------- isTimePreheatRunning ---------- true ")
                            val setCookTimed = cookingVM.recipeExecutionViewModel.cookTime.value
                            HMILogHelper.Logd(
                                tag,
                                "TIME PREHEAT, ${cookingVM.cavityName.value} has cookTimerState RUNNING, setCookTimed=$setCookTimed recipeCookingState PREHEATING"
                            )

                            if (setCookTimed == 0L) {
                                val ovenDisplayTemperature = cookingVM.ovenDisplayTemperature.value?:0
                                val targetTemperature = cookingVM.recipeExecutionViewModel.targetTemperature.value?:0
                                HMILogHelper.Logd("FAR_VIEW","Preheat Runing setCookTimed-->$setCookTimed , remainingTime-->$remainingTime")
                                if ((cookingVM.recipeExecutionViewModel.remainingCookTime.value?: 0) <= 0L) {
                                   executeOvenReadyRunnable(cookingVM,statusWidgetHelper,ovenDisplayTemperature,targetTemperature)
                                }else {
                                    statusWidgetHelper.tvCookTimeRemaining()?.visible()
                                    val modeText = CookingAppUtils.getRecipeNameWithParametersFarView(
                                        requireContext(), ovenDisplayTemperature, targetTemperature ,cookingVM
                                    )
                                    statusWidgetHelper.tvCookTimeRemaining()?.text = modeText
                                    HMILogHelper.Logd("FAR_VIEW","Preheat Runing-->$modeText")
                                }

                            } else {
                                statusWidgetHelper.tvCookTimeRemaining()?.text =
                                    CookingAppUtils.spannableETRRunning(
                                        context, setCookTimed?.toInt() ?: 0
                                    )
                            }
                        }
                        else {
                            HMILogHelper.Logd("FAR_VIEW", "---------- RUNNING showing time remaining---------- ")
                            statusWidgetHelper.tvCookTimeRemaining()?.visibility = View.VISIBLE
                            statusWidgetHelper.tvCookTimeRemaining()?.text =
                                CookingAppUtils.spannableETRRunning(
                                    context, remainingTime.toInt()
                                )
                        }
                        makeIndefiniteProgressBarVisible(
                            statusWidgetHelper.getStatusProgressBar(),
                            statusWidgetHelper.getProgressbarInfinite(),
                            false
                        )
                        statusWidgetHelper.getStatusProgressBar()?.max = 100
                        statusWidgetHelper.getStatusProgressBar()?.progress = percentageVal
                        return
                    }

                    //update the progressbar to 100% and update recipe name text to Completed
                    Timer.State.COMPLETED -> {
                        setKitchenTimerEndingVisibility(false)
                        statusWidgetHelper.getStatusProgressBar()?.progress = percentageVal
                        HMILogHelper.Logd(tag, "Updating tvRecipe to Completed ans starting timeout handler")
                        statusWidgetHelper.tvRecipeWithTemperature()?.gone()
                        statusWidgetHelper.tvCookTimeRemaining()?.visible()
                        HMILogHelper.Logd("FAR_VIEW", "---------- cookTimerState spannableETRRunning ---------- ${CookingAppUtils.spannableETRRunning(context, remainingTime.toInt())} ")
                        statusWidgetHelper.tvCookTimeRemaining()?.text = CookingAppUtils.spannableETRRunning(context, remainingTime.toInt())
                        makeIndefiniteProgressBarVisible(
                            statusWidgetHelper.getStatusProgressBar(),
                            statusWidgetHelper.getProgressbarInfinite(),
                            true
                        )
                        initTimeoutForCompleteCycle(cookingVM)
                        if (CookingAppUtils.isPyroliticClean()) {
                            HMILogHelper.Logd("FAR_VIEW", "---------- Completed  isPyroliticClean ---------- ")
                            requireActivity().supportFragmentManager.setFragmentResult(
                                BundleKeys.BUNDLE_SELF_CLEAN_COMPLETED,
                                bundleOf(BundleKeys.BUNDLE_SELF_CLEAN_COMPLETED to true)
                            )
                        }
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
    }

    private fun manageProbeTemperature(
        statusWidget: AbstractFarStatusWidgetHelper,
        cookingViewModel: CookingViewModel,
        meatProbeCurrentValue: Int?,
        meatProbeTargetValue: Int?,
        probeConnectionState: Boolean?
    ) {
        HMILogHelper.Logd("FAR_VIEW", "---------- Manage Probe Temperature ---------- ")
        HMILogHelper.Logd(
            tag,
            "cavityName: ${cookingViewModel.cavityName.value}, probeCurrentTemperature=$meatProbeCurrentValue, probeTargetTemperature=$meatProbeTargetValue,  probeConnectionState=$probeConnectionState"
        )
        if (probeConnectionState == true) {
            statusWidget.tvRecipeWithTemperature()?.gone()
            statusWidget.getTemperatureRampIcon()?.gone()
            val probeComplete =
                cookingViewModel.recipeExecutionViewModel.targetMeatProbeTemperatureReached.value == true || ((meatProbeCurrentValue
                    ?: 0) >= (meatProbeTargetValue ?: 0))
            statusWidget.tvCookTimeRemaining()?.let {
                val displayProbeTemperature = StringBuilder()
                displayProbeTemperature.append(if (probeComplete) meatProbeTargetValue else meatProbeCurrentValue)
                    .append(AppConstants.DEGREE_SYMBOL).append(AppConstants.SYMBOL_FORWARD_SLASH)
                displayProbeTemperature.append(meatProbeTargetValue)
                    .append(AppConstants.DEGREE_SYMBOL)
                it.text = displayProbeTemperature.toString()
                it.setTextAppearance(R.style.StyleFarViewProbeTemperatureTextView)

                statusWidget.getTemperatureProbeIcon().visibility = View.VISIBLE
                if(probeComplete) {
                    makeIndefiniteProgressBarVisible(
                        statusWidget.getStatusProgressBar(),
                        statusWidget.getProgressbarInfinite(),
                        true
                    )
                }
                HMILogHelper.Logd(
                    tag,
                    "manageProbeTemperature cavityName: ${cookingViewModel.cavityName.value} displayProbeTemperature=$displayProbeTemperature"
                )
            }
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
        statusWidgetHelper: AbstractFarStatusWidgetHelper
    ) {
        HMILogHelper.Logd("FAR_VIEW", "---------- Update  Probe Progress ---------- ")
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
            makeIndefiniteProgressBarVisible(
                statusWidgetHelper.getStatusProgressBar(),
                statusWidgetHelper.getProgressbarInfinite(),
                false
            )
        }
    }
    /** Responsible for updating the tvRecipeWithTemperature when recipe cooking state is in COOKING
     *
     * @param statusWidgetHelper to access far widget status view components
     * @param cookingVM cooking view model for a particular cavity
     * @param recipeCookingState recipe cooking state
     * @param recipeName current recipe name
     * @param ovenDisplayTemperature displaying oven temperature in C or in F
     * @param targetTemperature oven's core target temperature
     */
    @Suppress("UNUSED_PARAMETER")
    fun updateRecipeNameTextView(
        statusWidgetHelper: AbstractFarStatusWidgetHelper,
        cookingVM: CookingViewModel,
        recipeCookingState: RecipeCookingState,
        recipeName: String,
        ovenDisplayTemperature: Int,
        targetTemperature: Int
    ){
        statusWidgetHelper.getTemperatureRampIcon()?.gone()
        statusWidgetHelper.tvRecipeWithTemperature()?.gone()
        //Handling probe recipe showing ramp Icon so check and return if probe recipe executed.
        if(cookingVM.recipeExecutionViewModel?.isProbeBasedRecipe == true) return
        //Handling delayed cycle and return
        if(cookingVM.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.DELAYED) {
            HMILogHelper.Logd("FAR_VIEW","----------- Handling DELAYED cycle and return -----------")
            updateUIForDelayState(statusWidgetHelper, cookingVM)
            return
        }

        val modeText = CookingAppUtils.getRecipeNameWithParametersFarView(
            requireContext(), ovenDisplayTemperature, targetTemperature ,cookingVM
        )
        val progressBasis = cookingVM.recipeExecutionViewModel.recipeProgressBasis.value
        HMILogHelper.Logd("FAR_VIEW","----------- modeTextValue -------> $modeText")
        HMILogHelper.Logd("FAR_VIEW","----------- RecipeName recipeProgressBasis -------> $progressBasis")


        // show temperature only when recipe cooking state is Cooking, has temperature and no timer running
        when {
            CookingAppUtils.isRequiredTargetAvailable(cookingVM, RecipeOptions.TARGET_TEMPERATURE)
                    && progressBasis == RecipeProgressBasis.TEMPERATURE -> {
                statusWidgetHelper.tvRecipeWithTemperature()?.visible()
                statusWidgetHelper.tvRecipeWithTemperature()?.text = modeText

                HMILogHelper.Logd(tag, "Updating tvRecipe with temperature to $modeText")
                when {
                    ovenDisplayTemperature < targetTemperature -> {// show ramp up
                        statusWidgetHelper.getTemperatureRampIcon()?.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(), R.drawable.tc_status_temperature_ramp_up
                            )
                        )
                        statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.VISIBLE
                    }
                    ovenDisplayTemperature > targetTemperature -> {//show ramp down
                        statusWidgetHelper.getTemperatureRampIcon()?.setImageDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(), R.drawable.tc_status_temperature_ramp_down
                            )
                        )
                        statusWidgetHelper.getTemperatureRampIcon()?.visibility = View.VISIBLE
                    }
                    ovenDisplayTemperature == targetTemperature -> {
                        executeOvenReadyRunnable(cookingVM,statusWidgetHelper,ovenDisplayTemperature,targetTemperature)
                    }
                }
            }
            cookingVM.recipeExecutionViewModel.isSensingRecipe
                    && (!cookingVM.recipeExecutionViewModel.isProbeBasedRecipe)
                    && progressBasis != RecipeProgressBasis.TIME -> {
                HMILogHelper.Logd("FAR_VIEW","----------- SensingRecipe -------> ")
                statusWidgetHelper.tvRecipeWithTemperature()?.visible()
                statusWidgetHelper.tvRecipeWithTemperature()?.text = resources.getString(R.string.text_running_sensing)

            }
            CookingAppUtils.isPyroliticClean() -> {
                updateUIForSelfCleanState(statusWidgetHelper,cookingVM)
            }
            cookingVM.recipeExecutionViewModel.isVirtualchefBasedRecipe -> {
                HMILogHelper.Logd("FAR_VIEW","----------- Virtual Chef recipe -------> ")
            }
            else -> {
                if (progressBasis != RecipeProgressBasis.TIME) {
                    HMILogHelper.Logd("FAR_VIEW", "----------- Progress Basis NONE -------> ")
                    statusWidgetHelper.tvRecipeWithTemperature()?.visible()
                    statusWidgetHelper.tvRecipeWithTemperature()?.text = modeText
                    lottieAnimationForceRestart(statusWidgetHelper.getStatusProgressBar(),statusWidgetHelper.getProgressbarInfinite())
                }
            }
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
        if (isTransitionAnimationRunning) return
        if (isVisible) {
            progressBar?.visibility = View.GONE
            imageView?.visibility = View.VISIBLE
            CommonAnimationUtils.playLottieAnimation(imageView,R.raw.loop_fill_progress_bar_amber_white)
        } else {
            progressBar?.visibility = View.VISIBLE
            imageView?.visibility = View.GONE
        }
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        onUserInteraction()
        if (isSlowBlinkingKnobTimeoutActive()) HMIExpansionUtils.userInteractWithinSlowBlinkingTimeoutElapsed()
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel, onMeatProbeDestroy = {
                onUserInteraction()
                if (isSlowBlinkingKnobTimeoutActive()) HMIExpansionUtils.userInteractWithinSlowBlinkingTimeoutElapsed()
            })
        }
    }

    /**
     * gives the static instance of handler based on cavity selection
     */
    private fun getCavityHandler(whichCavity: Int): Handler {
        return if (whichCavity == 2) handlerLowerCavity
        else handlerUpperCavity
    }
    /**
     * post oven ready message on Recipe text view and send delayed message to remove the runnbale
     */
    private fun executeOvenReadyRunnable(
        cookingVM: CookingViewModel, statusWidget: AbstractFarStatusWidgetHelper,
        ovenDisplayTemperature: Int,
        targetTemperature: Int
    ) {
        val cavityPosition = if (cookingVM.isPrimaryCavity) 1 else 2
        val handler = getCavityHandler(cavityPosition)
        handler.removeCallbacksAndMessages(null)
        handler.post(runnableOvenReady(statusWidget,cavityPosition,ovenDisplayTemperature,targetTemperature))
    }
    /**
     * Runnable to change the textview back and forth between oven ready and recipe name with temperature
     */
    private fun runnableOvenReady(
        statusWidget: AbstractFarStatusWidgetHelper,
        cavityPosition: Int,
        ovenDisplayTemperature: Int,
        targetTemperature: Int
    ): Runnable {
        return Runnable {
            val handler = getCavityHandler(cavityPosition)
            //Added check for handling exception :- Runnable is running and fragment is destroyed we are accesing resources that time its coming null
            if(isAdded){
                statusWidget.tvCookTimeRemaining()?.gone()
                statusWidget.getTemperatureProbeIcon().gone()
                makeIndefiniteProgressBarVisible(
                    statusWidget.getStatusProgressBar(),
                    statusWidget.getProgressbarInfinite(),
                    true
                )
                val cookingVM = if (cavityPosition == 2) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
                val currentTimerState = cookingVM.recipeExecutionViewModel.cookTimerState.value
                val ovenReadyTime = cookingVM.recipeExecutionViewModel.readySincePreheatTime.value ?: 0
                if (ovenReadyTime > resources.getInteger(R.integer.duration_status_mode_text_oven_ready_2_min)
                    || (currentTimerState == Timer.State.RUNNING || currentTimerState == Timer.State.COMPLETED)
                ) {
                    handler.removeCallbacksAndMessages(null)
                    val modeText = CookingAppUtils.getRecipeNameWithParametersFarView(
                        requireContext(), ovenDisplayTemperature, targetTemperature ,cookingVM
                    )
                    HMILogHelper.Logd("FAR_VIEW", "preheatComplete Target Temperature shown = $modeText")
                   //Handling far view cycle completed scenario. Showing 00:00 instead of temperature string
                    if (currentTimerState == Timer.State.COMPLETED) {
                        statusWidget.tvRecipeWithTemperature()?.gone()
                    } else {
                        statusWidget.tvRecipeWithTemperature()?.visible()
                        statusWidget.tvRecipeWithTemperature()?.text = modeText
                    }


                    statusWidget.getTemperatureRampIcon()?.visibility = View.GONE
                    //Handling Lottie animation not showing during transition between status view
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isAdded) {
                            makeIndefiniteProgressBarVisible(
                                statusWidget.getStatusProgressBar(),
                                statusWidget.getProgressbarInfinite(),
                                true
                            )
                        }
                    }, AppConstants.FAR_VIEW_ANIMATION_1000)
                    return@Runnable
                }
                HMILogHelper.Logd("FAR_VIEW", "preheatComplete switching = Oven Ready")
                statusWidget.tvRecipeWithTemperature()?.text = ""
                val ovenType = if (cookingVM.isOfTypeOven) resources.getString(R.string.cavity_selection_oven) else resources.getString(R.string.microwave )
                val readyText = String.format(Locale.getDefault(),resources.getString(R.string.text_header_oven_ready),ovenType)

                statusWidget.tvRecipeWithTemperature()?.text = readyText
                statusWidget.tvRecipeWithTemperature()?.visible()
                statusWidget.getTemperatureRampIcon()?.visibility = View.GONE
                handler.postDelayed(
                    runnableOvenReady(
                        statusWidget,
                        cavityPosition, ovenDisplayTemperature, targetTemperature
                    ),
                    resources.getInteger(R.integer.duration_status_mode_text_switch_3_sec)
                        .toLong()
                )
            } else {
                handler.removeCallbacksAndMessages(null)
                return@Runnable
            }

        }
    }

    /**
     * Observes changes in the kitchen timer and updates the UI accordingly.
     */
    private fun observeKitchenTimer() {
        KitchenTimerVMFactory.getKitchenTimerViewModels()?.let { kitchenTimerViewModels ->
            kitchenTimerViewModels.forEach { ktModel ->
                ktModel.remainingTime.observe(viewLifecycleOwner) { remainingTime ->
                    HMILogHelper.Logd("FAR_VIEW", "Timer : ${ktModel.timerId} | Remaining Time: $remainingTime")
                    if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                        when {
                            remainingTime in 1..10 && !isTimerActive -> {
                                provideViewHolderHelper().getKitchenTimerRunningText()?.alpha = AppConstants.FLASH_ANIMATION_START_ALPHA
                                setKitchenTimerEndingVisibility(true)
                                updateKitchenTimerText(ktModel)
                                timerQueue.add(ktModel)
                                processNextTimer()
                            }
                            remainingTime == 0 -> {
                                startFlashAnimation(provideViewHolderHelper().getKitchenTimerRunningText(),provideViewHolderHelper().getKitchenTimerIcon())
                                handler.postDelayed({
                                    setKitchenTimerEndingVisibility(false)
                                    displayNextTimerInQueue()
                                }, FAR_VIEW_KITCHEN_TIMER_DELAY) // 3-second delay
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Starts the flash animation for the specified views.
     *
     * @param textView The TextView representing the Kitchen Timer text.
     * @param iconView The View representing the Kitchen Timer icon.
     */
    private fun startFlashAnimation(textView: View?, iconView: View?) {
        val flashAnimator = ValueAnimator.ofFloat(
            AppConstants.FLASH_ANIMATION_START_ALPHA,
            AppConstants.FLASH_ANIMATION_END_ALPHA,
            AppConstants.FLASH_ANIMATION_START_ALPHA
        ).apply {
            duration = AppConstants.FLASH_ANIMATION_DURATION
            repeatCount = AppConstants.FLASH_ANIMATION_REPEAT_COUNT
            repeatMode = ValueAnimator.RESTART
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                val alphaValue = animation.animatedValue as Float
                textView?.alpha = alphaValue
                iconView?.alpha = alphaValue
            }
        }

        flashAnimator.start()

        flashAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                textView?.alpha = AppConstants.FLASH_ANIMATION_FINAL_ALPHA
                iconView?.alpha = AppConstants.FLASH_ANIMATION_FINAL_ALPHA
                super.onAnimationEnd(animation)
            }
        })
    }

    /**
     * Updates the text for the far-view kitchen timer with the formatted remaining time.
     *
     * @param ktModel The ViewModel for the kitchen timer whose text needs to be updated.
     */
    private fun updateKitchenTimerText(ktModel: KitchenTimerViewModel) {
        if (isAdded) {
            val ktText = KitchenTimerUtils.convertTimeRemainingToShortString(
                ktModel.remainingTime.value?.toLong() ?: 0
            )
            val ktName = ktModel.timerName
            HMILogHelper.Logd("FAR_VIEW", "Updating Kitchen Timer Text: $ktText")
            provideViewHolderHelper().apply {
                getKitchenTimerRunningText()?.text = ktText
                if (isMultipleKTRunning) {
                    getHeaderBar()?.visible()
                    getHeaderBar()?.getHeaderTitle()?.text = ktName
                } else {
                    getHeaderBar()?.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Processes the next kitchen timer in the queue.
     * Ensures that timers are processed sequentially.
     */
    private fun processNextTimer() {
        if (isAdded && timerQueue.isNotEmpty() && !isTimerActive) {
            val nextTimer = timerQueue.poll()
            nextTimer?.let {
                HMILogHelper.Logd("FAR_VIEW", "Processing Next Timer: ${it.timerId}")
                isTimerActive = true
                handler.postDelayed(kitchenTimerRunnable(it), resources.getInteger(R.integer.ms_1000).toLong())
            }
        }
    }
    /**
     * Creates a runnable task to update the kitchen timer text at regular intervals.
     * Continues updating the text until the timer reaches zero.
     *
     * @param ktModel The ViewModel for the kitchen timer to be updated.
     * @return A Runnable task that updates the timer text.
     */
    private fun kitchenTimerRunnable(ktModel: KitchenTimerViewModel): Runnable {
        return Runnable {
            if (isAdded) {
                val remainingTime = ktModel.remainingTime.value?.toLong() ?: 0L
                val ktText = KitchenTimerUtils.convertTimeRemainingToShortString(remainingTime)
                provideViewHolderHelper().apply {
                    getKitchenTimerRunningText()?.text = ktText
                }

                if (remainingTime > 0) {
                    handler.postDelayed(
                        kitchenTimerRunnable(ktModel),
                        resources.getInteger(R.integer.ms_1000).toLong()
                    )
                } else {
                    HMILogHelper.Logd("FAR_VIEW", "Timer Complete for: ${ktModel.timerId}")
                }
            }
        }
    }

    /**
     * Displays the next kitchen timer in the queue, if available.
     * Ensures timers are processed sequentially.
     */
    private fun displayNextTimerInQueue() {
        if (isAdded) {
            if (timerQueue.isNotEmpty()) {
                HMILogHelper.Logd("FAR_VIEW", "Displaying Next Timer in Queue")
                processNextTimer()
            } else {
                isTimerActive = false
                HMILogHelper.Logd("FAR_VIEW", "No Active Timers")
            }
        }
    }
    /**
     * Sets the visibility of the far-view kitchen timer.
     * Updates the UI to reflect whether a kitchen timer is ending or not.
     *
     * @param isKitchenTimerEnding A boolean indicating whether the kitchen timer is ending.
     */
    private fun setKitchenTimerEndingVisibility(isKitchenTimerEnding: Boolean) {
        if (isAdded) {
            provideViewHolderHelper().apply {
                isKitchenTimerEnding(isKitchenTimerEnding)
            }
        }
    }


    /** ============================================================================= #
    # =========================  Transition Animation START ========================= #
    # ============================================================================== */


    /**
     * Method is responsible for playong animation for single cavity and double cavity
     */
    private fun startTransitionAnimation() {
        val cavityType = identifyWhichCavityRunning()
        when (cavityType) {
            AppConstants.CAVITY_RUNING_BOTH -> animateSharedElementViewForDoubleCavity()
            else -> animateSharedElementViewForSingleCavity()
        }
    }
    /**
     * Animate enter, exit and retun share element animation on views
     */
    private fun animateSharedElementViewForDoubleCavity() {
        HMILogHelper.Logd("FAR_ANIM", "---------- Far Double Cavity  --------- ")
        val exitTransitionListener = TransitionListener.onTransitionListener(
            onTransitionStart = {
                HMILogHelper.Logd("FAR_ANIM", "---------- Far Exit Animation  --------- ")
            })

        returnTransition = slideTopExitAndFadeBothCavityTransition().addListener(exitTransitionListener)
        /*---------------------- Move Animation  --------------------------*/
        val upperView = provideViewHolderHelper().getUpperCookingStatusWidget()?.statusWidgetHelper?.getFarViewParentLayout()
        val lowerView = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getFarViewParentLayout()
        upperView?.invisible()
        lowerView?.invisible()

        // Disable shared element transitions

        val transition = Fade().apply {
            if (isAdded) {
                excludeTarget(upperView as View,true) // Exclude your view by not adding it
                excludeTarget(lowerView as View,true) // Exclude your view by not adding it
            }// Exclude your view by not adding it
            duration = AppConstants.FAR_VIEW_ANIMATION_1000
        }
        val moveTransitionListener = TransitionListener.onTransitionListener(
            onTransitionEnd = {
                isTransitionAnimationRunning = false
                if (isAdded) {
                    HMILogHelper.Logd("FAR_ANIM", "---------- Far Move onTransitionStart  --------- ")
                    upperView?.visible()
                    lowerView?.visible()
                }
            })

        transition.addListener(moveTransitionListener)
        TransitionManager.beginDelayedTransition(upperView as ViewGroup, transition)
            // This view will change immediately, no fade:
        Handler(Looper.getMainLooper()).postDelayed({
            //Safe side animation stop variable updated
            isTransitionAnimationRunning = false
            if (isAdded) {
                upperView.visible()
                lowerView?.visible()
                fadeInContentView()
            }
        }, AppConstants.FAR_VIEW_ANIMATION_1000)//1000 HMI, 800 Emulator testing.
    }


    /**
     * Animate enter, exit and retun share element animation on views
     */
    private fun animateSharedElementViewForSingleCavity() {
        HMILogHelper.Logd("FAR_ANIM", "---------- Far Single Cavity  --------- ")
        //*---------------------- Move Animation  --------------------------*//*
        val moveTransitionListener = TransitionListener.onTransitionListener(
            onTransitionEnd = {
                isTransitionAnimationRunning = false
                if (isAdded) {
                    HMILogHelper.Logd("FAR_ANIM","---------- Far Move onTransitionStart --------- ")
                    fadeInContentView()
                }
            })

        // Assign the Move transition as the shared element transition
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)?.addListener(moveTransitionListener)
            ?.apply {
                duration = AppConstants.FAR_VIEW_ANIMATION_600
                interpolator = DecelerateInterpolator()
            }

        sharedElementReturnTransition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        exitTransition = null
        Handler(Looper.getMainLooper()).postDelayed({
            //Safe side animation stop variable updated
            isTransitionAnimationRunning = false
        }, AppConstants.FAR_VIEW_ANIMATION_1000)
    }

    /**
     * Method is responsible for fade in far status parent view animation
     */
    private fun fadeInContentView(fadingMode:Int = Fade.IN) {
        val cavityType = identifyWhichCavityRunning()
        val upperView = provideViewHolderHelper().getUpperCookingStatusWidget()?.statusWidgetHelper?.getFarViewChildLayout()
        val lowerView = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getFarViewChildLayout()

        Fade(fadingMode).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            startDelay = when (cavityType) {
                AppConstants.CAVITY_RUNING_BOTH -> {
                    AppConstants.FAR_VIEW_ANIMATION_1500
                }
                else -> AppConstants.FAR_VIEW_ANIMATION_1000
            }

            when (cavityType) {
                AppConstants.CAVITY_RUNING_BOTH -> {
                    addTarget(upperView as View)
                    addTarget(lowerView as View)
                    TransitionManager.beginDelayedTransition(upperView as ViewGroup, this)
                    TransitionManager.beginDelayedTransition(lowerView as ViewGroup, this)
                }
                else -> {
                    addTarget(upperView as View)
                    TransitionManager.beginDelayedTransition(upperView as ViewGroup, this)
                }
            }
        }

        if(fadingMode == Fade.OUT) upperView?.invisible() else upperView?.visible()
        if(fadingMode == Fade.OUT) lowerView?.invisible() else lowerView?.visible()
        visibleInvisibleParentChildView(isParentViewVisible = true,true)
    }

    /**
     * Methos is resopnsible for providing which cavity is runing state
     * @return Upper cavity, Lower cavity and Both cavity
     */
    private fun identifyWhichCavityRunning():Int {
        var cavityRuning = AppConstants.CAVITY_RUNING_UPPER
        val viewHelper = provideViewHolderHelper()
        when {
            viewHelper.getUpperCookingStatusWidget() != null
                    && viewHelper.getLowerCookingStatusWidget() != null -> cavityRuning = AppConstants.CAVITY_RUNING_BOTH
            viewHelper.getUpperCookingStatusWidget() != null -> cavityRuning = AppConstants.CAVITY_RUNING_UPPER
            viewHelper.getLowerCookingStatusWidget() != null -> cavityRuning = AppConstants.CAVITY_RUNING_LOWER
        }
        return cavityRuning
    }
    private fun provideProgressVisibility() {
        val progressDetails = CookingAppUtils.getProgreeBarDetails()
        val viewHelper = provideViewHolderHelper()
        when (identifyWhichCavityRunning()) {
            AppConstants.CAVITY_RUNING_UPPER -> {
                if(progressDetails?.first == true) {
                    viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.visible()
                    viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.invisible()
                }else{
                    viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.invisible()
                    viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.visible()
                }
            }
            AppConstants.CAVITY_RUNING_LOWER -> {
                if(progressDetails?.first == true) {
                    viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.visible()
                    viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.invisible()
                }else{
                    viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.invisible()
                    viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.visible()
                }
            }
            AppConstants.CAVITY_RUNING_BOTH -> {
                if(progressDetails?.first == true) {
                    viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.visible()
                    viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.invisible()
                }else{
                    viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.invisible()
                    viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.visible()
                }
                if(progressDetails?.second == true) {
                    viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.visible()
                    viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.invisible()
                }else{
                    viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar()?.invisible()
                    viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite()?.visible()
                }
            }
        }
    }
    @Suppress("SameParameterValue")
    private fun visibleInvisibleParentChildView(isParentViewVisible: Boolean, isChildViewVisible:Boolean){
        val cavityType = identifyWhichCavityRunning()
        val viewHelper = provideViewHolderHelper()
        when(cavityType){
            AppConstants.CAVITY_RUNING_LOWER -> {
                val parentView =  viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getFarViewParentLayout()
                val childView =  viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getFarViewChildLayout()
                when {
                    isParentViewVisible -> parentView?.visible()
                    else -> parentView?.invisible()
                }
                when {
                    isChildViewVisible -> childView?.visible()
                    else -> childView?.invisible()
                }
            }
            AppConstants.CAVITY_RUNING_UPPER -> {
                val parentView =  viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getFarViewParentLayout()
                val childView =  viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getFarViewChildLayout()
                when {
                    isParentViewVisible -> parentView?.visible()
                    else -> parentView?.invisible()
                }
                when {
                    isChildViewVisible -> childView?.visible()
                    else -> childView?.invisible()
                }
            }
            else -> {
                val upperParentView =  viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getFarViewParentLayout()
                val upperChildView =  viewHelper.getLowerCookingStatusWidget()?.statusWidgetHelper?.getFarViewChildLayout()
                val lowerParentView =  viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getFarViewParentLayout()
                val lowerChildView =  viewHelper.getUpperCookingStatusWidget()?.statusWidgetHelper?.getFarViewChildLayout()
                when {
                    isParentViewVisible -> {
                        upperParentView?.visible()
                        lowerParentView?.visible()
                    }
                    else -> {
                        upperParentView?.invisible()
                        lowerParentView?.invisible()
                    }
                }
                when {
                    isChildViewVisible -> {
                        upperChildView?.visible()
                        lowerChildView?.visible()
                    }
                    else -> {
                        upperChildView?.invisible()
                        lowerChildView?.invisible()
                    }
                }
            }
        }
    }

    /**
     * Method is responsible for slide bottom animation and fade out animation together
     * Upper cavity/Lower Cavity view animation
     */
    private fun slideTopExitAndFadeBothCavityTransition(): TransitionSet {
        val upperProgressBar = provideViewHolderHelper().getUpperCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar() as View
        val upperProgressBarInfinite = provideViewHolderHelper().getUpperCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite() as View

        val lowerProgressBar = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getStatusProgressBar() as View
        val lowerProgressBarInfinite = provideViewHolderHelper().getLowerCookingStatusWidget()?.statusWidgetHelper?.getProgressbarInfinite() as View

        val progressAnimDuration = AppConstants.FAR_VIEW_ANIMATION_500
        val slideTopUpperProgressViewAnimation = CustomSlideBottom(AppConstants.FAR_VIEW_SLIDE_41_MINUS).apply {
            duration = progressAnimDuration
            interpolator = DecelerateInterpolator()
            addTarget(upperProgressBar)
            addTarget(upperProgressBarInfinite)
        }
        val slideBottomLowerProgressViewAnimation = CustomSlideBottom(AppConstants.FAR_VIEW_SLIDE_33_MINUS).apply {
            duration = progressAnimDuration
            interpolator = DecelerateInterpolator()
            addTarget(lowerProgressBar)
            addTarget(lowerProgressBarInfinite)
        }
        val durationAnim = AppConstants.FAR_VIEW_ANIMATION_500


        val fadeOut = Fade(Fade.OUT).apply {
            duration = durationAnim
            interpolator = DecelerateInterpolator()
            addTarget(upperProgressBar)
            addTarget(upperProgressBarInfinite)

            addTarget(lowerProgressBar)
            addTarget(lowerProgressBarInfinite)
        }

        val transitionSet = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(slideTopUpperProgressViewAnimation)
            addTransition(slideBottomLowerProgressViewAnimation)
            addTransition(fadeOut)
        }
        return transitionSet
    }

    /**
     * Lottie animation not started during animation transition so added handler to restart the animation for prgressbar
     */
    private fun lottieAnimationForceRestart(
        progressBar: ProgressBar?,
        imageView: LottieAnimationView?
    ) {
        //Handling Lottie animation not showing during transition between status view
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                makeIndefiniteProgressBarVisible(
                    progressBar,
                    imageView,
                    true
                )
            }
        }, AppConstants.FAR_VIEW_ANIMATION_1000)
    }
/** ============================================================================= #
# =========================  Transition Animation END =========================== #
# ============================================================================== */

}