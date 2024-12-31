package android.presenter.fragments.self_clean

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.basefragments.AbstractFarStatusFragment
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.transition.Fade
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentSelfCleanStatusBinding
import com.whirlpool.hmi.cooking.model.capability.recipe.options.TimeMap
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.PyroLevel
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeProgressBasis
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModel
import com.whirlpool.hmi.expansion.viewmodel.HmiExpansionViewModelFactory
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.LogHelper.Loge
import com.whirlpool.hmi.utils.timers.Timer
import core.utils.AppConstants
import core.utils.AppConstants.CYCLE_END_TIME_HOUR
import core.utils.AppConstants.EMPTY_SPACE
import core.utils.AppConstants.FALSE_CONSTANT
import core.utils.AppConstants.IS_FROM_BLACKOUT
import core.utils.AppConstants.TRUE_CONSTANT
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.navigateToStatusOrClockScreen
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.PopUpBuilderUtils
import core.utils.PopUpBuilderUtils.Companion.featureUnavailablePopup
import core.utils.PopUpBuilderUtils.Companion.observeHmiKnobListener
import core.utils.SharedViewModel
import core.utils.TimeUtils
import core.utils.gone
import core.utils.transition.CustomSlideBottom
import core.utils.transition.TransitionListener
import java.util.Locale

// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL
// Created by SINGHA80 on 3/4/2024.
@Suppress("SameParameterValue")
open class SelfCleanStatusFragment : Fragment(), View.OnClickListener,
    HMIExpansionUtils.HMICancelButtonInteractionListener, MeatProbeUtils.MeatProbeListener,
    HMIExpansionUtils.UserInteractionListener, HMIKnobInteractionListener {

    private var binding: FragmentSelfCleanStatusBinding? = null
    private var recipeExecutionViewModel: RecipeExecutionViewModel? = null
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null
    private var cookTimeOptionList: HashMap<String, Long>? = null
    private var hmiExpansionViewModel: HmiExpansionViewModel? = null
    private var primaryCavityViewModel: CookingViewModel? = null
    private var longCookTimeValue: Long? = null
    private var longRemainingDelayTimeValue: Long? = null
    private var delayTimerState: Timer.State? = null
    private var recipeCookState: RecipeCookingState? = null
    private val handler = Handler(Looper.getMainLooper())
    private var toggleRunnable: Runnable? = null
    private var isToggleRunning = false
    private var isDelayTimerRunning = false
    private var delayUntilTime: String? = null
    private var selfCleanTimerCompleted = false
    private var selfCleanFarViewStarted = false
    private var shouldApplySharedElementTransition = true

    private lateinit var timeoutViewModel: TimeoutViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelfCleanStatusBinding.inflate(inflater, container, false)
        selfCleanCompleteListener()
        initRecipeExecutionViewModel()
        initHmiExpansionViewModel()
        setCavityViewModelByProductVariant()
        setViewByProductVariant()
        observeRecipeProgressPercentage()
        observeRecipeCookingState()
        observeRemainingCookTime()
        observeDelayTimerState()
        setDelayTimerStateValue()
        setCookTimeValue()
        setRemainingDelayTimeValue()
        updateTemperatureTextView()
        registerCancelButtonListener()
        checkDoorLockState()
        observeCookTimerState()
        binding?.headerBarNoTitle?.manageTopSheetVisibilityWithDrag(
            isTopSheetVisible = false,
            isDrag = false
        )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timeoutViewModel = ViewModelProvider(this)[TimeoutViewModel::class.java]
        animateSharedElementView()
        farViewTransitionFragmentResultListener()
        MeatProbeUtils.setMeatProbeListener(this)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        if (arguments?.getBoolean(IS_FROM_BLACKOUT) == true){
            CookingAppUtils.setCancelButtonPressDuringSelfClean(TRUE_CONSTANT)
            HMILogHelper.Logd("showing self clean cancelled, when clean cancelled due to blackout")
            updateViewsAfterCancel()
        }
        if(KnobNavigationUtils.knobForwardTrace){
            KnobNavigationUtils.knobForwardTrace = false
            updateStartNowTextBackground(R.drawable.selector_textview_walnut)
        }
    }

    /**
     * To set the text view background
     */
    private fun updateStartNowTextBackground(drawable: Int) {
        if (binding?.statusProgressWidget?.startNowTextView?.isClickable == true)
            binding?.statusProgressWidget?.startNowTextView?.background =
                ContextProvider.getContext().let {
                    ContextCompat.getDrawable(it, drawable)
                }
    }

    override fun onStart() {
        super.onStart()
        selfCleanFarViewStarted = false
        initTimeout()
    }
    override fun onStop() {
        super.onStop()
        timeoutViewModel.stop()
    }
    /**
     * override if fragment doesn't require timeout functionality, default is true
     * @return true if Fragment needs timeout functionality, false otherwise
     */
    protected open fun isTimeoutApplicable(): Boolean {
        return true
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
                    if (PopUpBuilderUtils.isPopupShowing()) {
                        HMILogHelper.Loge(
                            tag,
                            "Popup is still showing, not moving to farStatusScreen"
                        )
                        timeoutViewModel.restart()
                        return@observe
                    } else{
                        navigateToFarViewWithTransitionAnimation()
                    }
                }
            }
            timeoutViewModel.setTimeout(
                resources.getInteger(R.integer.timeout_near_to_far_view_status_running_in_sec)
            )
        }
    }

    private fun checkDoorLockState() {
        if (!TextUtils.isEmpty(getBundleData())) {
            HMILogHelper.Logd("self clean - check door lock state")
            updateViewsAfterCancel()
        }
    }

    private fun getBundleData(): String? {
        return arguments?.getString(AppConstants.DOOR_LOCK_STATE)
    }

    /**
     * used to initialize Recipe execution view model
     */
    private fun initRecipeExecutionViewModel() {
        recipeExecutionViewModel =
            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
    }

    /**
     * used to initialize Hmi Expansion view model
     */
    private fun initHmiExpansionViewModel() {
        hmiExpansionViewModel = HmiExpansionViewModelFactory.getHMIExpansion()
    }

    /**
     * used to set primaryCavityViewModel
     */
    private fun setCavityViewModelByProductVariant() {
        productVariant = CookingViewModelFactory.getProductVariantEnum()
        when (productVariant) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
                primaryCavityViewModel = CookingViewModelFactory.getInScopeViewModel()
            }

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                primaryCavityViewModel = CookingViewModelFactory.getInScopeViewModel()
            }

            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                primaryCavityViewModel = CookingViewModelFactory.getInScopeViewModel()
            }

            else -> {}
        }
    }

    /**
     * set view according to the product variant
     */
    private fun setViewByProductVariant() {
        if (productVariant == CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN) {
            binding?.statusProgressWidget?.ovenCavityImageView?.visibility = View.GONE
        } else if (productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
            || productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO
        ) {
            if (primaryCavityViewModel!!.isPrimaryCavity) {
                binding?.statusProgressWidget?.ovenCavityImageView?.setImageResource(R.drawable.cavity_upper)
            } else if (primaryCavityViewModel!!.isSecondaryCavity) {
                binding?.statusProgressWidget?.ovenCavityImageView?.setImageResource(R.drawable.ic_lower_cavity)
            }
        }
        binding?.statusProgressWidget?.cookTimeTextView?.text = CYCLE_END_TIME_HOUR
        binding?.headerBarNoTitle?.setStatusIcon1Visibility(true)
        binding?.headerBarNoTitle?.setStatusIcon1Resource(R.drawable.icon_32px_lock)
        binding?.headerBarNoTitle?.setStatusIcon2Visibility(false)
        binding?.headerBarNoTitle?.setStatusIcon3Visibility(false)
        binding?.headerBarNoTitle?.setStatusIcon4Visibility(false)
        binding?.headerBarNoTitle?.setHeaderDrawerVisibility(false)
    }

    /**
     * observe RecipeProgressBasis state and update views
     */
    private fun observeRecipeProgressPercentage() {
        recipeExecutionViewModel?.recipeProgressBasis?.observe(
            viewLifecycleOwner
        ) { recipeProgressBasis: RecipeProgressBasis ->
            HMILogHelper.Loge("recipeProgressBasis", recipeProgressBasis.name)
            if ((recipeProgressBasis == RecipeProgressBasis.TIME)) {
                binding?.statusProgressWidget?.setProgressBarStatusBackground(false)
                binding?.statusProgressWidget?.progressBarCookTime?.min = -1
                recipeExecutionViewModel?.recipeProgressPercentage?.observe(viewLifecycleOwner) { integer: Int ->
                    binding?.statusProgressWidget?.progressBarCookTime?.setProgress(integer, true)
                }
            }
        }
    }

    /**
     * observe RemainingCookTime state and update views
     */
    private fun observeRemainingCookTime() {
        recipeExecutionViewModel?.remainingCookTime?.observe(viewLifecycleOwner) { longTimer: Long ->
            HMILogHelper.Loge("getRemainingCookTime", longTimer.toString())
            if(recipeExecutionViewModel?.recipeCookingState?.value == RecipeCookingState.CLEANING){
                val cookTimerString: String = TimeUtils.getTimeInHHMMSS(longTimer)
                binding?.statusProgressWidget?.cookTimeTextView?.text = cookTimerString
            }
        }
    }

    /**
     * observe RecipeCookingState state and update views
     */
    @SuppressLint("StringFormatInvalid")
    private fun observeRecipeCookingState() {
        recipeExecutionViewModel?.cookTimerState?.observe(viewLifecycleOwner){
            if (recipeExecutionViewModel?.cookTimerState?.value == Timer.State.COMPLETED){
                selfCleanTimerCompleted = true
            }
        }
        recipeExecutionViewModel?.recipeCookingState?.observe(viewLifecycleOwner) { recipeCookingState: RecipeCookingState ->
            HMILogHelper.Loge("recipeCookingState", recipeCookingState.name)
            recipeCookState = recipeCookingState
            when (recipeCookState) {
                RecipeCookingState.CLEANING -> {
                    binding?.headerBarNoTitle?.setStatusIcon1Visibility(isVisible = true)
                    binding?.headerBarNoTitle?.setStatusIcon1Resource(R.drawable.icon_32px_lock)
                    binding?.statusProgressWidget?.startNowTextView?.visibility = View.GONE
                    binding?.headerBarNoTitle?.getTopSheetView()?.drawerWidgetBinding?.handle?.setOnClickListener {
                        featureUnavailablePopup(this)
                    }
                    disableHmiExpansionButtons()
                    HMIExpansionUtils.setFragmentUserInteractionListener(this)
                }

                RecipeCookingState.COOLING -> {
                    HMIExpansionUtils.removeFragmentUserInteractionListener(this)
                    recipeExecutionViewModel?.cookTimerState?.value?.let { state ->
                        when (state) {
                            Timer.State.COMPLETED, Timer.State.IDLE-> {
                                //MAF_2327: Defect fixed where The system displays a "Self Clean Completed" screen instead of a "Self Clean Cancelled" screen if a blackout occurs during the self-clean cycle
                                if (CookingAppUtils.getCancelButtonPressDuringSelfClean().equals(TRUE_CONSTANT)) {
                                    HMILogHelper.Logd("self clean - getCancelButtonPressDuringSelfClean is true")
                                    updateViewsAfterCancel()
                                    return@let
                                }
                                binding?.statusProgressWidget?.temperatureTextView?.text = getString(R.string.text_recipe_completed_status, getString(R.string.pyroliticClean))
                                CookingAppUtils.setKnobLightWhenPreheatCompleteAndCycleComplete()
                            }
                            else -> {
                                HMILogHelper.Logd("self clean - else update view")
                                updateViewsAfterCancel()
                            }
                        }
                    }
                    binding?.statusProgressWidget?.cookTimeTextView?.text = CYCLE_END_TIME_HOUR
                    binding?.statusProgressWidget?.setProgressBarStatusBackground(true)
                }

                RecipeCookingState.IDLE -> {
                    if (delayTimerState != null && delayTimerState != Timer.State.RUNNING) {
                        enableHmiExpansionButtons()
                        if (selfCleanTimerCompleted) {
                            selfCleanTimerCompleted = false
                            showSelfCleanCompletePopUp()
                        } else {
                            recipeExecutionViewModel?.cancel()
                            navigateToStatusOrClockScreen(this)
                        }
                        CookingAppUtils.setCancelButtonPressDuringSelfClean(FALSE_CONSTANT)

                        delayTimerState?.name?.let { HMILogHelper.Loge("delayTimerState", it) }
                    }
                }

                else -> {}
            }
        }
    }

    /**
     * observe CookTimeOption for pyro level
     */
    private fun updateTemperatureTextView() {
        var pyroLevel = ""
        val objectTimeMap = recipeExecutionViewModel?.cookTimeOption?.value
        if (objectTimeMap is TimeMap) {
            cookTimeOptionList = objectTimeMap.timeMap
            for (map: Map.Entry<String, Long> in cookTimeOptionList!!.entries) {
                if (map.value == longCookTimeValue) {
                    pyroLevel = map.key
                    HMILogHelper.Loge("pyroLevel", pyroLevel)
                    break
                }
            }
            val selfCleanLevel = when (pyroLevel.uppercase(Locale.getDefault())) {
                PyroLevel.LOW.name -> resources.getString(R.string.text_low)
                PyroLevel.MEDIUM.name -> resources.getString(R.string.text_medium)
                PyroLevel.HIGH.name -> resources.getString(R.string.text_high)
                else -> ""
            }
            val cleanText =
                resources.getString(R.string.pyroliticClean) + EMPTY_SPACE + selfCleanLevel
            var toggle = true

            toggleRunnable = object : Runnable {
                @SuppressLint("StringFormatInvalid")
                override fun run() {
                    isToggleRunning = true
                    val textToDisplay = if (toggle) {
                        binding?.statusProgressWidget?.temperatureTextView?.typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                        binding?.statusProgressWidget?.temperatureTextView?.setTextColor(
                            resources.getColor(
                                R.color.color_white,
                                null
                            )
                        )
                        if (recipeCookState == RecipeCookingState.COOLING) {
                            if (CookingAppUtils.getCancelButtonPressDuringSelfClean().equals(TRUE_CONSTANT)) {
                                HMILogHelper.Logd("self clean - getCancelButtonPressDuringSelfClean cooling true")
                                updateViewsAfterCancel()
                                return
                            } else {
                                binding?.statusProgressWidget?.startNowTextView?.gone()
                                resources.getString(R.string.text_recipe_completed_status, getString(R.string.pyroliticClean))
                            }
                        }else { cleanText }
                    } else {
                        binding?.statusProgressWidget?.temperatureTextView?.typeface =
                            ResourcesCompat.getFont(requireContext(), R.font.roboto_light)
                        binding?.statusProgressWidget?.temperatureTextView?.setTextColor(
                            resources.getColor(
                                R.color.text_color_grey,
                                null
                            )
                        )
                        if (isDelayTimerRunning) delayUntilTime
                        else if (recipeCookState == RecipeCookingState.COOLING) resources.getString(
                            R.string.text_cooling_down_self_clean
                        )
                        else resources.getString(R.string.text_oven_locked_self_clean)
                    }
                    binding?.statusProgressWidget?.temperatureTextView?.text = textToDisplay.toString()
                    toggle = !toggle
                    if (isToggleRunning) handler.postDelayed(this, 3000)
                }
            }
            handler.post(toggleRunnable!!)
        }
        if (isToggleRunning) {
            handler.post(toggleRunnable!!)
        }
    }

    private fun stopTextToggling() {
        isToggleRunning = false
        toggleRunnable?.let { handler.removeCallbacks(it) }
    }


    /**
     * show SelfCleanComplete pop up dialog
     */
    private fun showSelfCleanCompletePopUp() {
        val dialogPopupBuilder = ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
            .setHeaderTitle(R.string.text_recipe_completed_status,getString(R.string.pyroliticClean))
            .setDescriptionMessage(R.string.text_self_clean_pop_up_complete_description)
            .setTitleTextDrawable(true, R.drawable.ic_self_clean_completed)
            .setCancellableOutSideTouch(false)
            .setRightButton(R.string.text_button_ok) {
                CookingAppUtils.setCancelButtonPressDuringSelfClean(FALSE_CONSTANT)
                navigateSafely(
                    this,
                    R.id.global_action_to_clockScreen,
                    null,
                    null
                )
                false
            }
            .setIsProgressVisible(false)
            .build()
        dialogPopupBuilder.setTimeoutCallback(null, AppConstants.TIME_OUT_STOP)
        val hmiKnobListener = observeHmiKnobListener(
            onHMILeftKnobClick = {
                CookingAppUtils.setCancelButtonPressDuringSelfClean(FALSE_CONSTANT)
                navigateSafely(
                    this,
                    R.id.global_action_to_clockScreen,
                    null,
                    null
                )
                dialogPopupBuilder.dismiss()
            }, onKnobSelectionTimeout = {

            }
        )

        dialogPopupBuilder.setOnDialogCreatedListener(object :
            ScrollDialogPopupBuilder.OnDialogCreatedListener {
            override fun onDialogCreated() {
                handler.postDelayed(
                    {   //Added delay reason - On status screen more option click on instruction then multiple popup open so knob listener not activate.
                        HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    }, 1000.toLong()
                )
            }

            override fun onDialogDestroy() {
                recipeExecutionViewModel?.cancel()
                HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                CookingAppUtils.setHmiKnobListenerAfterDismissDialog(this@SelfCleanStatusFragment)
            }
        })

        dialogPopupBuilder.show(
            parentFragmentManager,
            SelfCleanStatusFragment::class.java.simpleName
        )
    }

    /**
     * observe & sets long cook time value
     */
    private fun setCookTimeValue() {
        longCookTimeValue = recipeExecutionViewModel?.cookTime?.value
        if (longCookTimeValue != null) {
            HMILogHelper.Loge("RemainingDelayTime", longCookTimeValue.toString())
        }
    }

    /**
     * sets long remaining delay time value
     */
    private fun setRemainingDelayTimeValue() {
        longRemainingDelayTimeValue = recipeExecutionViewModel?.remainingDelayTime?.value
        if (longRemainingDelayTimeValue != null) {
            HMILogHelper.Loge("RemainingDelayTime", longRemainingDelayTimeValue.toString())
        }
    }

    /**
     * sets delay timer state value
     */
    private fun setDelayTimerStateValue() {
        recipeExecutionViewModel?.delayTimerState?.observe(
            viewLifecycleOwner
        ) { state: Timer.State ->
            delayTimerState = state
        }
    }

    /**
     * observe & sets delayed timer state
     */
    private fun observeDelayTimerState() {
        recipeExecutionViewModel?.delayTimerState?.observe(viewLifecycleOwner) { state: Timer.State ->
            HMILogHelper.Loge("getDelayTimerState", state.name)
            when (state) {
                Timer.State.RUNNING -> {
                    disableHmiExpansionButtons()
                    isDelayTimerRunning = true
                    val cookTimerString: String? =
                        longCookTimeValue?.let { TimeUtils.getTimeInHHMMSS(it) }
                    if (cookTimerString != null) {
                        HMILogHelper.Loge("cookTimerString", cookTimerString)
                    }
                    delayUntilTime = longRemainingDelayTimeValue?.let {
                        TimeUtils.getDelayUntilTime(
                            requireContext(),
                            it
                        )
                    }
                    if (delayUntilTime != null) {
                        HMILogHelper.Loge("RemainingDelayTime", delayUntilTime!!)
                    }
                    binding?.statusProgressWidget?.startNowTextView?.text =
                        resources.getString(R.string.text_start_now_button)
                    binding?.headerBarNoTitle?.setStatusIcon1Resource(R.drawable.icon_32px_lock)
                    binding?.statusProgressWidget?.cookTimeTextView?.text = cookTimerString
                    binding?.statusProgressWidget?.startNowTextView?.setOnClickListener(this)
                }
                Timer.State.COMPLETED -> {
                    isDelayTimerRunning = false
                    CookingAppUtils.startSelfClean(false)
                }
                else -> {
                    Loge("observeDelayTimerState:  not handling Timer state : $state")
                }
            }
        }
    }

    /**
     * disable the hmi keys during self clean
     */
    private fun disableHmiExpansionButtons() {
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SELF_CLEAN_RUNNING)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SELF_CLEAN_RUNNING)
    }

    /**
     * enable the hmi keys after self clean is completed
     */
    private fun enableHmiExpansionButtons() {
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SELF_CLEAN_RUNNING)
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == binding?.statusProgressWidget?.startNowTextView?.id) {
            isDelayTimerRunning = false
            if (!recipeExecutionViewModel!!.overrideDelay().isError) {
                observeRecipeProgressPercentage()
                CookingAppUtils.startSelfClean(false)
            }
        }
    }

    /**
     * set setHMICancelButtonInteractionListener
     */
    private fun registerCancelButtonListener() {
        HMIExpansionUtils.setHMICancelButtonInteractionListener(this)
    }

    /**
     * provides cancel button event listener after cycle is cancelled
     */
    override fun onHMICancelButtonInteraction() {
        HMILogHelper.Loge("onHMICancelButtonInteraction")
        if (recipeExecutionViewModel?.isRunning == true) {
            CookingAppUtils.setCancelButtonPressDuringSelfClean(TRUE_CONSTANT)
            // If user click on cancel button during Delay in progress then traverse to clock screen
            if(recipeExecutionViewModel?.recipeExecutionState?.value?.equals(RecipeExecutionState.DELAYED) == true) {
                CookingAppUtils.setCancelButtonPressDuringSelfClean(FALSE_CONSTANT)
                navigateSafely(this, R.id.global_action_to_clockScreen, null, null)
            }
            recipeExecutionViewModel?.cancel()
            HMILogHelper.Loge("onHMICancelButtonInteraction", "cycle is cancelled")
        } else if (!TextUtils.isEmpty(getBundleData())) {
            CookingAppUtils.setCancelButtonPressDuringSelfClean(FALSE_CONSTANT)
            navigateSafely(this, R.id.global_action_to_clockScreen, null, null)
        }
        HMILogHelper.Logd("self clean - onHMICancelButtonInteraction")
        updateViewsAfterCancel()
    }

    /**
     * updates views after cycle is cancelled
     */
    @SuppressLint("StringFormatInvalid")
    private fun updateViewsAfterCancel() {
        stopTextToggling()
        binding?.statusProgressWidget?.temperatureTextView?.typeface =
            ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
        binding?.statusProgressWidget?.cookTimeTextView?.text = CYCLE_END_TIME_HOUR
        binding?.statusProgressWidget?.temperatureTextView?.text = getString(R.string.text_cancelled_cycle_running, getString(R.string.pyroliticClean))
        binding?.statusProgressWidget?.temperatureTextView?.setTextColor(
            resources.getColor(
            R.color.color_white,
            null
        ))
        binding?.statusProgressWidget?.startNowTextView?.visibility = View.VISIBLE
        binding?.statusProgressWidget?.startNowTextView?.text = (resources.getString(R.string.text_cooling_down_self_clean_all_caps))
        binding?.statusProgressWidget?.startNowTextView?.setTextColor(
            resources.getColor(
                R.color.text_button_disabled_grey,
                null
            )
        )
        updateStartNowTextBackground(R.drawable.text_view_ripple_effect)
        binding?.statusProgressWidget?.startNowTextView?.isClickable = false
        binding?.statusProgressWidget?.setProgressBarStatusBackground(true)
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
    }

    override fun onDestroyView() {
        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = false)
        if (recipeExecutionViewModel?.recipeExecutionState?.value != RecipeExecutionState.IDLE && (!selfCleanFarViewStarted)) {
            recipeExecutionViewModel?.cancel()
        }
        MeatProbeUtils.removeMeatProbeListener()
        stopTextToggling()
        HMIExpansionUtils.removeHMICancelButtonInteractionListener(this)
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        requireActivity().supportFragmentManager.clearFragmentResultListener(BundleKeys.BUNDLE_SELF_CLEAN_COMPLETED)
        super.onDestroyView()
    }

    override fun onUserInteraction() {
        timeoutViewModel.restart()
        featureUnavailablePopup(this)
    }

    private fun observeCookTimerState() {
        recipeExecutionViewModel?.cookTimerState?.observe(viewLifecycleOwner) { state: Timer.State ->
            HMILogHelper.Loge("getCookTimerState", state.name)
            if ((state == Timer.State.RUNNING) && recipeExecutionViewModel!!.recipeCookingState.value?.equals(RecipeCookingState.CLEANING) == true) {
                binding?.statusProgressWidget?.startNowTextView?.visibility = View.GONE
            }
        }
    }
    private fun selfCleanCompleteListener() {
        // listener for far view fragment self clean completed event
        requireActivity().let {
            it.supportFragmentManager.setFragmentResultListener(
                BundleKeys.BUNDLE_SELF_CLEAN_COMPLETED,
                this
            ) { _, bundle ->
                selfCleanTimerCompleted = bundle.getBoolean(BundleKeys.BUNDLE_SELF_CLEAN_COMPLETED)
                HMILogHelper.Logd("FAR_VIEW","BUNDLE_SELF_CLEAN_COMPLETED -->$selfCleanTimerCompleted")
            }
        }
    }

    override fun onHMILongLeftKnobPress() {
        // do nothing
    }

    override fun onHMIRightKnobClick() {
        // do nothing
    }

    override fun onHMILongRightKnobPress() {
        // do nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        // do nothing
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        // do nothing
    }

    override fun onHMILeftKnobClick() {
        if (binding?.statusProgressWidget?.startNowTextView?.isClickable == true)
            binding?.statusProgressWidget?.startNowTextView?.callOnClick()
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (binding?.statusProgressWidget?.startNowTextView?.isClickable == true)
                updateStartNowTextBackground(R.drawable.selector_textview_walnut)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID)
            updateStartNowTextBackground(R.drawable.text_view_ripple_effect)
    }

    /** ============================================================================ #
    # =========================  Transition Animation START ========================= #
    # ============================================================================== */

    /**
     * Animate enter, exit and retun share element animation on views
     */
    private fun animateSharedElementView() {
        val enterTransitionListener = TransitionListener.onTransitionListener(
            onTransitionStart = {
                if(isAdded){
                    HMILogHelper.Logd("FAR_ANIM","---------- Enter onTransitionStart --------- ")
                    slideBottomEnterUpperViews()
                }
            },
            onTransitionEnd = {
                HMILogHelper.Logd("FAR_ANIM","---------- Enter onTransitionEnd --------- ")
                resetAnimationTransition()
            }
        )

        HMILogHelper.Logd("FAR_ANIM","-------- Self clean is Far Anim Needed --------- $shouldApplySharedElementTransition")
        when {
            shouldApplySharedElementTransition -> {
                //shared progress bar transition animation
                sharedElementEnterTransition = TransitionInflater.from(requireContext())
                    .inflateTransition(android.R.transition.move)

                //shared progress bar reenter into screen transition animation
                reenterTransition = TransitionInflater.from(requireContext())
                    .inflateTransition(android.R.transition.no_transition)
                    ?.addListener(enterTransitionListener)

                sharedElementReturnTransition = TransitionInflater.from(requireContext())
                    .inflateTransition(android.R.transition.no_transition)
                    ?.addListener(enterTransitionListener)

                //shared progress bar exit from screen transition animation
                exitTransition = slideBottomExitAndFadeTransition()
            }
            else -> {
                resetAnimationTransition()
            }
        }
        shouldApplySharedElementTransition = false
    }
    /**
     * Method is responsible for slide bottom animation and fade in animation together
     * Upper cavity
     */
    private fun slideBottomEnterUpperViews() {
        val viewHelper = binding
        HMILogHelper.Logd("FAR_ANIM","---------- slide Bottom Self clean --------- ")
        val animator = AnimatorSet()
        val animationList:ArrayList<View> = arrayListOf()
        val slideAnimators:ArrayList<ObjectAnimator> = arrayListOf()
        val fadeAnimators:ArrayList<ObjectAnimator> = arrayListOf()


        val view1 = viewHelper?.statusProgressWidget?.getStatusTopTextContentView
        val view2 = viewHelper?.statusProgressWidget?.getStatusBottomOptionsView

        animationList.add(view1 as View)
        animationList.add(view2 as View)

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
     * Navigate to far view with transition animation
     */
    private fun navigateToFarViewWithTransitionAnimation() {
        selfCleanFarViewStarted = true
        shouldApplySharedElementTransition = true
        animateSharedElementView()
        val bundle = Bundle()
        if (primaryCavityViewModel?.isPrimaryCavity == true) {
            bundle.putString(AbstractFarStatusFragment.KEY_STATUS_SCREEN_CAVITY_POSITION,
                AppConstants.PRIMARY_CAVITY_KEY)
        } else  {
            bundle.putString(AbstractFarStatusFragment.KEY_STATUS_SCREEN_CAVITY_POSITION,AppConstants.SECONDARY_CAVITY_KEY)
        }
        val sharedElements = getTransitionSharedElementViews()
        val extras = FragmentNavigatorExtras(*sharedElements.distinct().toTypedArray())
        CookingAppUtils.setProgreeBarDetails(provideProgressDetails())
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                HMILogHelper.Logd("FAR_ANIM","---------- Self Clean FragmentNavigatorExtras  isAdded---------$isAdded ")
                if (isAdded) {
                    Navigation.findNavController(this.requireView())
                        .navigate(R.id.action_selfCleanStatusFragment_to_singleFarView,bundle, null, extras)
                }
            } catch (e: Exception) {
                HMILogHelper.Logd("FAR_ANIM","---------- Self Clean  Navigator  Error Handling--------- ${e.message}")
                //If FragmentNavigatorExtras giving any runtime error then we skip the share element and navigate to far view
                Navigation.findNavController(this.requireView())
                    .navigate(R.id.action_selfCleanStatusFragment_to_singleFarView,bundle, null, null)
            }
        }, AppConstants.FAR_VIEW_ANIMATION_1000)
    }
    /**
     * Methos is resopnsible for providing shared element transition animation view
     */
    private fun getTransitionSharedElementViews():ArrayList<Pair<View, String>> {
        val animationList:ArrayList<Pair<View, String>> = ArrayList()
        animationList.addAll(addTransionNameToView())
        return animationList
    }
    /**
     * Methos is resopnsible for providing shared element transition animation view
     */
    private fun addTransionNameToView(): ArrayList<Pair<View, String>> {
        //Lower Cavity Transition name set dynamically
        val animationList:ArrayList<Pair<View, String>> = ArrayList()
        val viewHelper = binding

        //Views
        val progressBar = viewHelper?.statusProgressWidget?.progressBarCookTime
        val lottieAnimationView = viewHelper?.statusProgressWidget?.lottieProgressBarCookTime

        //Transition Names
        val progressBarTransitionName =  getString(R.string.transition_progress_bar)
        val progressBarInfiniteTransitionName = getString(R.string.transition_progress_bar_lottie)

        //Assigned Transition Names
        progressBar?.transitionName = progressBarTransitionName
        lottieAnimationView?.transitionName = progressBarInfiniteTransitionName

        //Add Into ArrayList
        animationList.add(Pair(progressBar as View,progressBar.transitionName))
        animationList.add(Pair(lottieAnimationView as View,lottieAnimationView.transitionName))
        return animationList
    }
    private fun provideProgressDetails():Pair<Boolean,Boolean>{
        //Views
        val viewHelper = binding
        val isProgressBarVisible = viewHelper?.statusProgressWidget?.progressBarCookTime?.isVisible == true
        val isProgressBarInifiniteVisible = viewHelper?.statusProgressWidget?.lottieProgressBarCookTime?.isVisible == true
        return Pair(isProgressBarVisible,isProgressBarInifiniteVisible)
    }


    /**
     * Method is responsible for slide bottom animation and fade out animation together
     * cavity view animation
     */
    private fun slideBottomExitAndFadeTransition(): TransitionSet {
        val viewHelper = binding
        val statusTopView = viewHelper?.statusProgressWidget?.getStatusTopTextContentView as View
        val statusBottomView = viewHelper.statusProgressWidget.getStatusBottomOptionsView as View

        val customSlideBottom = CustomSlideBottom(AppConstants.FAR_VIEW_SLIDE_50).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = DecelerateInterpolator()
            addTarget(statusTopView)
            addTarget(statusBottomView)
        }
        val fadeOut = Fade(Fade.OUT).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = DecelerateInterpolator()
            addTarget(statusTopView)
            addTarget(statusBottomView)
        }
        val transitionSet = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(customSlideBottom)
            addTransition(fadeOut)
        }
        return transitionSet
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
                        val viewHelper = binding
                        viewHelper?.statusProgressWidget?.progressBarCookTime?.invalidate()
                        viewHelper?.statusProgressWidget?.lottieProgressBarCookTime?.invalidate()
                    }
                }, AppConstants.FAR_VIEW_ANIMATION_1000)
            }
        }
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

    /** ============================================================================ #
    # =========================  Transition Animation START ========================= #
    # ============================================================================== */
}