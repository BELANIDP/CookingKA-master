package android.presenter.fragments.self_clean

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.fragments.InstructionWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentInstructionWidgetBinding
import com.whirlpool.hmi.cooking.utils.PyroLevel
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AppConstants.DIGIT_ZERO
import core.utils.AppConstants.EMPTY_STRING
import core.utils.CookingAppUtils
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils.Companion.getKnobPositionIndexForScrollViewText
import core.utils.CookingAppUtils.Companion.handleTextScrollOnKnobEvent
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.HMILogHelper.Logd
import core.utils.KnobNavigationUtils
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NavigationUtils.Companion.getVisibleFragment
import core.utils.PopUpBuilderUtils
import java.util.Locale


/**
 * File       : android.presenter.fragments.self_clean.SelfCleanInstructionsFragment
 * Brief      : This class is used for self clean Instructions and check cavity
 * Author     : PATELJ7
 * Created On : 4-March-2024
 */
class SelfCleanInstructionsFragment : SuperAbstractTimeoutEnableFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface,MeatProbeUtils.MeatProbeListener {
    private lateinit var instructionWidget: InstructionWidget
    private lateinit var binding: FragmentInstructionWidgetBinding
    private var rotator = listOf(0, 1) // Instruction Widget, Right CTA
    private var selectedRotator = rotator[0]
    private var isInstructionWidgetSelected = true
    var handler = Handler(Looper.getMainLooper())
    private var onHMIKnobInteractionListener: HMIKnobInteractionListener? = null
    private var currentPosition = 0
    private var pyroLevel = EMPTY_STRING
    private var pyroCookTime = DIGIT_ZERO.toLong()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instructionWidget = InstructionWidget(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = instructionWidget.inflateLayout(inflater)
        updateHeaderBar()
        updateDescriptionTextView()
        observeLiveRecipeExecutionState()
        setOnHMIKnobInteractionListener()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBundle()
        onNextClick()
        MeatProbeUtils.setMeatProbeListener(this)
    }

    private fun onNextClick() {
        binding.instructionButtonNext.setOnClickListener {
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.audio_alert,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )

            val recipeErrorResponse =
                CookingViewModelFactory.getInScopeViewModel()?.recipeExecutionViewModel?.setCookTime(
                    pyroCookTime
                )
            if (recipeErrorResponse?.isError != true) {
                CookingViewModelFactory.getInScopeViewModel()?.recipeExecutionViewModel?.setPyroLevel(
                    PyroLevel.valueOf(
                        pyroLevel.uppercase(
                            Locale.getDefault()
                        )
                    )
                )
                getVisibleFragment()?.let {
                    PopUpBuilderUtils.prepareOvenPopup(it)
                }
            } else {
                getVisibleFragment()?.let {
                    PopUpBuilderUtils.runningFailPopupBuilder(it)
                }

            }
        }
    }
    private fun setOnHMIKnobInteractionListener() {
        onHMIKnobInteractionListener = object: HMIKnobInteractionListener {
            override fun onHMILeftKnobClick() {
               Logd("Knob", "onHMILeftKnobClick() called  : $selectedRotator")
                when (selectedRotator) {
                    0 -> {
                        isInstructionWidgetSelected = false
                        selectedRotator = 1
                        binding.instructionButtonNext.background = resources.let {
                            ResourcesCompat.getDrawable(
                                it, R.drawable.selector_textview_walnut, null
                            )
                        }
                    }
                    1 -> {
                        if (binding.instructionButtonNext.isEnabled) {
                            KnobNavigationUtils.knobForwardTrace = true
                            binding.instructionButtonNext.callOnClick()
                        }
                    }
                }
            }

            override fun onHMILongLeftKnobPress() {
            }

            override fun onHMIRightKnobClick() {
            }

            override fun onHMILongRightKnobPress() {
            }

            override fun onKnobSelectionTimeout(knobId: Int) {
                if (knobId == AppConstants.LEFT_KNOB_ID)
                    resetToDefaultSelection()
            }

            override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
            }

            override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
                //Do nothing
            }

            override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
                HMILogHelper.Loge("onKnobRotateEvent", "onKnobRotateEvent######")
                if (knobId == AppConstants.LEFT_KNOB_ID) {
                    if (isInstructionWidgetSelected) {
                        selectedRotator = 0
                        val text = instructionWidget.getDescriptionTextView().text.toString()
                        val lines = text.split("\n") // Split text into lines
                        if (lines.isNotEmpty()) {
                            currentPosition = getKnobPositionIndexForScrollViewText(
                                instructionWidget.getScrollView(),
                                knobDirection,
                                currentPosition,
                                lines.size
                            )
                            getVisibleFragment()?.let {
                                handleTextScrollOnKnobEvent(
                                    it,
                                    instructionWidget.getScrollView(),
                                    instructionWidget.getDescriptionTextView(),
                                    currentPosition
                                )
                            }
                        }
                    } else {
                        selectedRotator = 1
                    }
                }
            }
        }
        onHMIKnobInteractionListener?.let { HMIExpansionUtils.setHMIKnobInteractionListener(it) }
    }

    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                getViewSafely(this) ?: requireView()
            )
        )
    }

    private fun updateHeaderBar() {
        instructionWidget.getHeaderBarWidget().setTitleText(getString(R.string.text_header_instruction_self_clean))
        instructionWidget.getHeaderBarWidget().setInfoIconVisibility(false)
        instructionWidget.getHeaderBarWidget().setRightIconVisibility(false)
        instructionWidget.getHeaderBarWidget().setOvenCavityIconVisibility(false)
        instructionWidget.getHeaderBarWidget().setCustomOnClickListener(this)
    }

    private fun updateDescriptionTextView() {
        instructionWidget.getDescriptionTextView().text =
            getString(R.string.text_information_selfClean) + AppConstants.TRIPLE_NEXT_LINE
        instructionWidget.getDescriptionTextView().setPadding(0,0,resources.getInteger(R.integer.integer_range_9),0)
    }

    private fun observeLiveRecipeExecutionState() {
        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.recipeExecutionState.observe(
            getViewLifecycleOwner()
        ) { state: RecipeExecutionState ->
            if (state == RecipeExecutionState.RUNNING_FAILED) {
                Logd("Pyro Clean RecipeExecutionState: RUNNING_FAILED, Cancelling Pyro & navigating to clock\n")
                CookingAppUtils.setIsSelfCleanFlow(false)
                CookingAppUtils.cancelProgrammedCyclesAndNavigate(this,
                    navigateToSabbathClock = false,
                    navigateToClockScreen = true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MeatProbeUtils.removeMeatProbeListener()
        onHMIKnobInteractionListener?.apply {
            HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        }
    }
    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        //Handling Self clean flow instruction screen user insert probe need to enabled and disabled HMI keys
        CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = false)
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
        //Handling Self clean flow instruction screen user removed probe need to enabled and disabled HMI keys
       CookingAppUtils.setIsSelfCleanFlow(isSelfCleanFlow = true)
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    private fun initBundle() {
        pyroLevel = arguments?.getString(BundleKeys.SELF_CLEAN_PYRO_LEVEL)?: EMPTY_STRING
        pyroCookTime = arguments?.getLong(BundleKeys.SELF_CLEAN_COOK_TIME)?: DIGIT_ZERO.toLong()
        Logd("pyroLevel: $pyroLevel and pyroCookTime: $pyroCookTime in bundle")
    }

    private fun resetToDefaultSelection(){
        isInstructionWidgetSelected = true
        selectedRotator = 0
        binding.instructionButtonNext.background = resources.let {
            ResourcesCompat.getDrawable(
                it, R.drawable.text_view_ripple_effect, null
            )
        }
    }

}