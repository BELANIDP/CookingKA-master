package android.presenter.fragments.steamclean

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.presenter.customviews.textButton.TextButton
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.fragments.InstructionWidget
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.LeadingMarginSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentInstructionWidgetBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.cookbook.records.RecipeRecord
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.HMILogHelper.Loge
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.PopUpBuilderUtils
import core.utils.TimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Boolean.TRUE


/**
 * File       : android.presenter.fragments.setamclean.SteamCleanInstructionsFragment
 * Brief      : This class is used for steam clean Instructions and check cavity
 * Author     : Rajendra
 * Created On : 04-NOV-2024
 */
class SteamCleanInstructionsFragment : SuperAbstractTimeoutEnableFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener{
    private lateinit var instructionWidget: InstructionWidget
    private lateinit var binding: FragmentInstructionWidgetBinding
    private var doorLastOpenState = false
    private var doorOpenClosePopupBuilder: ScrollDialogPopupBuilder? = null
    private var doorTwoStepVerificationState = false
    private var dooState = false
    //Knob Implementation
    private var knobRotationCount = 0
    private var rotator = listOf(0, 1) // Instruction Widget, CTA's
    private var selectedRotator = rotator[0]
    private var isInstructionWidgetSelected = true
    private val coroutineJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + coroutineJob)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instructionWidget = InstructionWidget(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = instructionWidget.inflateLayout(inflater)
        loadSteamCleanCycle()
        observeLiveDoorOpenCloseState()
        updateHeaderBar()
        handleTimeoutCallbackListener()
        updateDescriptionTextView()
        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.preEvaluateRecipeParameters()
        binding.instructionButtonNext.setTextButtonText(resources.getString(R.string.text_button_start))
        binding.instructionButtonNext.setOnClickListener {
            verifyTwoStepVerification()
        }
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        return binding.root
    }

    private fun verifyTwoStepVerification() {
        if (MeatProbeUtils.isMeatProbeConnected(CookingViewModelFactory.getInScopeViewModel())) {
            PopUpBuilderUtils.probeIncompatiblePopup(this)
            return
        }
        if (doorTwoStepVerificationState) {
            HMILogHelper.Logd("Steam Clean : 2 step verification already done")
            executeRecipe()
        } else {
            HMILogHelper.Logd("Steam Clean : performing 2 step verification")
            showDoorOpenClosePopup(false)
        }
    }

    private fun executeRecipe(onNavigateCompletedCallBack : () -> Unit = {}) {
        val recipeErrorResponse =
            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.execute()
        HMILogHelper.Logd("Steam Clean : Steam clean recipe error response = $recipeErrorResponse")
        when (recipeErrorResponse) {
            RecipeErrorResponse.NO_ERROR -> {
                CookingAppUtils.navigateToStatusOrClockScreen(this)
            }

            RecipeErrorResponse.ERROR_RECIPE_START_NOT_ALLOWED -> {
                CookingAppUtils.handleCookingError(
                    this,
                    CookingViewModelFactory.getInScopeViewModel(),
                    recipeErrorResponse,
                    false
                )
            }

            else -> {
                HMILogHelper.Logd("Steam Clean : Invalid steam recipe error")
            }
        }
        onNavigateCompletedCallBack()
    }

    private fun showDoorOpenClosePopup(isDelay: Boolean) {
        if (doorOpenClosePopupBuilder == null) {
            val handler = Handler(Looper.getMainLooper())
            HMILogHelper.Logd("Steam Clean : showing door open close popup")
            doorLastOpenState = false
            doorOpenClosePopupBuilder =
                ScrollDialogPopupBuilder.Builder(R.layout.layout_popup_fragment)
                    .setHeaderTitle(R.string.text_header_prepare_oven_steam_clean)
                    .setDescriptionMessage(R.string.text_description_prepare_oven)
                    .setIsLeftButtonEnable(false)
                    .setIsRightButtonEnable(false)
                    .setIsPopupCenterAligned(true)
                    .setCancellableOutSideTouch(false)
                    .setTopMarginForTitleText(AppConstants.POPUP_PROBE_TITLE_TOP_MARGIN_87PX)
                    .setTopMarginForDescriptionText(AppConstants.POPUP_DESCRIPTION_TOP_MARGIN_8PX)
                    .setHeaderViewCenterIcon(AppConstants.HEADER_VIEW_CENTER_ICON_GONE, false)
                    .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                    .setDescriptionTextFont(
                        ResourcesCompat.getFont(
                            ContextProvider.getContext(),
                            R.font.roboto_light
                        )
                    )
                    .build()

            doorOpenClosePopupBuilder?.setTimeoutCallback({
                CookingViewModelFactory.getInScopeViewModel().cancel()
                CookingAppUtils.navigateToStatusOrClockScreen(this)
                handler.postDelayed(
                    { dismissDialogs() },
                    AppConstants.POPUP_DISMISS_DELAY.toLong()
                )
            }, resources.getInteger(R.integer.session_two_minute_timeout))

            val knobInteractionListener = PopUpBuilderUtils.observeHmiKnobListener(
                onHMILeftKnobClick = {
                    //Do nothing
                },
                onHMIRightKnobClick = {
                    //Do nothing
                },
                onKnobSelectionTimeout = {}
            )

            doorOpenClosePopupBuilder?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_POPUPS_PRIORITY)
                    HMIExpansionUtils.setHMIKnobInteractionListener(knobInteractionListener)
                    // Observe any door events if needed and change the popup buttons visibility accordingly
                    doorOpenClosePopupBuilder?.getViewLifecycleOwner()?.let {
                        CookingViewModelFactory.getInScopeViewModel().doorState.observe(
                            it
                        ) { isOpen: Boolean ->
                            if (isOpen) {
                                doorLastOpenState = true
                            } else {
                                if (doorLastOpenState) {
                                    if (isDelay) {
                                        navigateToDelayTumbler()
                                        dismissDialogs()
                                    } else {
                                        executeRecipe(onNavigateCompletedCallBack = {
                                            handler.postDelayed(
                                                { dismissDialogs() },
                                                AppConstants.POPUP_DISMISS_DELAY.toLong()
                                            )
                                        })
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(knobInteractionListener)
                    CookingAppUtils.setHmiKnobListenerAfterDismissDialog(this@SteamCleanInstructionsFragment)
                }
            })

            doorOpenClosePopupBuilder?.show(
                parentFragmentManager,
                SteamCleanInstructionsFragment::class.java.simpleName
            )
        }
    }

    private fun observeLiveDoorOpenCloseState() {
        val cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
        if (null != cookingViewModel &&
            null != cookingViewModel.doorState.value
        ) {
            cookingViewModel.doorState.observe(
                viewLifecycleOwner
            ) { isOpen: Boolean ->
                if (TRUE == isOpen) {
                    HMILogHelper.Logd("Steam Clean : Door is open")
                    dooState = true
                } else {
                    if (dooState) {
                        HMILogHelper.Logd("Steam Clean : Door is close")
                        doorTwoStepVerificationState = true
                    }
                    dooState = false
                }
            }
        } else {
            Loge("Unexpected Error, Door State Live Data returned NULL")
        }
    }


    fun dismissDialogs() {
        if (doorOpenClosePopupBuilder != null) {
            doorOpenClosePopupBuilder?.dismiss()
            doorOpenClosePopupBuilder = null
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MeatProbeUtils.setMeatProbeListener(this)
        updateDelayButton()
    }

    private fun updateDelayButton() {
        if (CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.optionalOptions.value?.contains(
                RecipeOptions.DELAY_TIME
            ) == true
        ) {
            binding.delayButton.visibility = View.VISIBLE
            binding.delayButton.setOnClickListener(navigateToDelayFragment())
        } else {
            binding.delayButton.visibility = View.GONE
        }
    }

    private fun navigateToDelayFragment(): View.OnClickListener {
        return View.OnClickListener {
            if (doorTwoStepVerificationState) {
                HMILogHelper.Logd("Steam Clean : 2 step verification already done")
                navigateToDelayTumbler()
            } else {
                HMILogHelper.Logd("Steam Clean : performing 2 step verification already done")
                showDoorOpenClosePopup(TRUE)
            }
        }
    }

    private fun navigateToDelayTumbler() {
        val bundle = Bundle()
        bundle.putBoolean(BundleKeys.DELAY_STEAM_CLEAN, true)
        NavigationUtils.navigateToDelayScreen(this, bundle)
    }

    private fun loadSteamCleanCycle() {
        val steamCleanRecord: RecipeRecord =
            CookBookViewModel.getInstance()
                .getDefaultSteamCleanRecordByCavity(CookingViewModelFactory.getInScopeViewModel().cavityName.value)
        if (!CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.load(
                steamCleanRecord
            ).isError
        ) {
            HMILogHelper.Logd("Steam Clean : Steam clean recipe loaded successfully")
        } else {
            HMILogHelper.Logd("Steam Clean : Steam clean recipe Failed to load")
        }
    }

    override fun onHMILeftKnobClick() {
        HMILogHelper.Logd("Unboxing", "onHMILeftKnobClick")
        onHmiLeftKnobClick()
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobClick() {
        //Do nothing
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (isInstructionWidgetSelected) {
                selectedRotator = 0
                // Use the custom coroutine scope to launch the coroutine
                coroutineScope.launch {
                    val scrollView = instructionWidget.getScrollView()
                    val scrollViewContent = instructionWidget.getDescriptionTextView()
                    val lineHeight =
                        (scrollViewContent.measuredHeight / scrollViewContent.lineHeight)
                    CookingAppUtils.handleTextScrollOnKnobRotateEvent(
                        scrollView, lineHeight, knobDirection
                    )
                }
            } else {
                if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
                else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        binding.instructionButtonNext.background = null
                        binding.delayButton.background = resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        binding.delayButton.background = null
                        binding.instructionButtonNext.background = resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        resetToUnBoxingApplianceFeaturesDefaultSelection()
    }

    private fun resetToUnBoxingApplianceFeaturesDefaultSelection() {
        selectedRotator = 0
        isInstructionWidgetSelected = true
        binding.instructionButtonNext.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
        binding.delayButton.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
    }

    private fun onHmiLeftKnobClick() {
        HMILogHelper.Logd("Knob", "onHMILeftKnobClick() called  : $selectedRotator")
        when (selectedRotator) {
            0 -> {
                isInstructionWidgetSelected = false
                selectedRotator = 1
                knobRotationCount = 1
                binding.instructionButtonNext.background = null
                binding.delayButton.background = resources.let {
                    ResourcesCompat.getDrawable(
                        it, R.drawable.selector_textview_walnut, null
                    )
                }
            }

            1 -> {
                KnobNavigationUtils.knobForwardTrace = true
                resetToUnBoxingApplianceFeaturesDefaultSelection()
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        binding.delayButton.callOnClick()
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        binding.instructionButtonNext.callOnClick()
                    }
                }
            }
        }
    }

    override fun leftIconOnClick() {
        //clear the steam clean recipe data
        CookingAppUtils.clearRecipeData()
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                getViewSafely(this) ?: requireView()
            )
        )
    }

    private fun updateHeaderBar() {
        instructionWidget.getHeaderBarWidget()
            .setTitleText(getString(R.string.text_header_instruction_self_clean))
        instructionWidget.getHeaderBarWidget().setInfoIconVisibility(false)
        instructionWidget.getHeaderBarWidget().setRightIconVisibility(false)
        instructionWidget.getHeaderBarWidget().setOvenCavityIconVisibility(false)
        instructionWidget.getHeaderBarWidget().setCustomOnClickListener(this)
    }

    private fun updateDescriptionTextView() {
        instructionWidget.getDescriptionTextView().text =
            addNumberPointsWithDescription(CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.nonEditableOptions.value?.get(
                RecipeOptions.COOK_TIME
            )?.toLong()
                ?.let { TimeUtils.getTimeInHHMMSS(it) })
            updateButtonParameter(binding.delayButton)
        updateButtonParameter(binding.instructionButtonNext)
    }

    private fun updateButtonParameter(button: TextButton) {
        val buttonParam = (button.layoutParams as ViewGroup.MarginLayoutParams).apply {
            setMargins(resources.getInteger(R.integer.integer_range_0), resources.getInteger(R.integer.integer_range_0),
                resources.getInteger(R.integer.integer_range_0), resources.getInteger(R.integer.integer_range_20))
        }
        button.layoutParams = buttonParam
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if (CookingViewModelFactory.getInScopeViewModel() == null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, cookingViewModel)
            return
        }
        if (cookingViewModel != null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            launchProbeDetectedPopupAsPerVariant(cookingViewModel)
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    /**
     * add number each and \n\n statement
     */
    private fun addNumberPointsWithDescription(steamCleanCookTime: Any?): SpannableStringBuilder {
        val spannableStringBuilder = SpannableStringBuilder()
        var number = resources.getInteger(R.integer.integer_range_1)
        for (stringIterator in 0 until resources.getStringArray(R.array.appliance_features_steam_clean_instruction).size) {
            val spannableStringBuilderStart = spannableStringBuilder.length
            spannableStringBuilder.append("$number. ")
            if (stringIterator == resources.getInteger(R.integer.integer_range_2)) {
                val formattedDuration = String.format(
                    resources.getString(R.string.appliance_features_steam_clean_instruction_3),
                    steamCleanCookTime.toString()
                )

                spannableStringBuilder
                    .append(formattedDuration)
                    .append(AppConstants.DOUBLE_NEXT_LINE)
                    .append(AppConstants.DOUBLE_NEXT_LINE)
            } else {
                spannableStringBuilder.append(resources.getStringArray(R.array.appliance_features_steam_clean_instruction)[stringIterator].toString())
                    .append(AppConstants.DOUBLE_NEXT_LINE)
            }

            val spannableStringBuilderEnd = spannableStringBuilder.length
            spannableStringBuilder.setSpan(
                LeadingMarginSpan.Standard(resources.getInteger(R.integer.integer_range_8), resources.getInteger(R.integer.integer_range_40)),
                spannableStringBuilderStart,
                spannableStringBuilderEnd,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
            number++
        }
        return spannableStringBuilder
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    /**
     * This function will help to get the timeout call back
     */
    private fun handleTimeoutCallbackListener() {
        NavigationUtils.getVisibleFragment().let {
            it?.parentFragmentManager?.setFragmentResultListener(
                AppConstants.TIMEOUT_CALLBACK,
                it.viewLifecycleOwner
            ) { _, bundle ->
                val result = bundle.getBoolean(AppConstants.TIMEOUT_CALLBACK)
                if (result) {
                    HMILogHelper.Logd("Steam Clean", "received bundle for screen timeout")
                    CookingAppUtils.clearRecipeData()
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        CookingViewModelFactory.getInScopeViewModel().doorState.removeObservers(this)
        MeatProbeUtils.removeMeatProbeListener()
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResult(AppConstants.TIMEOUT_CALLBACK)
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResultListener(AppConstants.TIMEOUT_CALLBACK)
        dismissDialogs()
    }

}