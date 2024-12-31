package android.presenter.basefragments.abstract_self_clean

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat.getDrawable
import androidx.transition.Fade
import androidx.transition.Visibility
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentCavitySelectionBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsRepository
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.CAVITY_SELECTION_KNOB_SIZE
import core.utils.AppConstants.KNOB_COUNTER_ONE
import core.utils.AppConstants.KNOB_COUNTER_TWO
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.isTechnicianModeEnabled
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NotificationJsonKeys
import core.utils.NotificationManagerUtils
import core.utils.SharedViewModel
import core.utils.ToolsMenuJsonKeys


abstract class AbstractCavitySelectionFragment : SuperAbstractTimeoutEnableFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface, OnClickListener,
    HMIKnobInteractionListener {
    lateinit var binding: FragmentCavitySelectionBinding
    private lateinit var menuItems: ArrayList<String>
    private var isLoadingFirstTime = false
    private var isCombo = false
    protected var isUpperOvenSelected = false
    private var isKnobRotated = false
    private var knobRotationCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isLoadingFirstTime = true
        exitTransition = Fade().apply {
            duration = resources.getInteger(R.integer.ms_250).toLong()
            mode = Visibility.MODE_OUT
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!CookingAppUtils.isDemoModeEnabled()) {
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_HOME)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_HOME)
        } else {
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_DEMO_MODE)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_DEMO_MODE)
        }
        checkCavityType()
        cavitySelectionState()
        checkUpperCavityAvailability()
        checkLowerCavityAvailability()
        SharedViewModel.getSharedViewModel(this.requireActivity())
            .setCurrentRecipeBeingProgrammed(AppConstants.EMPTY_STRING)
        animateCavityButtons()
        if (isTechnicianModeEnabled()) {
            updateTechnicianTextModeText()
        } else {
            observeDemoModeLiveData()
        }
        binding.homeHeader.getBinding().clockTextView.visibility = View.GONE
        binding.homeHeader.manageTopSheetVisibilityWithDrag(false,false)
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            isUpperOvenSelected = true
            isKnobRotated = true
            setUpperCavityBackground(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCavitySelectionBinding.inflate(layoutInflater)
        headerBarSetUp()
        initListener()
        prepareToolsList()
        return binding.root
    }

    /*
     * Below Method is used to mention the selection state
     * */
    open fun cavitySelectionState() {
        if (isLoadingFirstTime) {
            setUpperCavityBackground(false)
            setLowerCavityBackground(false)
            isLoadingFirstTime = false
        } else {
            if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) {
                setUpperCavityBackground(true)
                setLowerCavityBackground(false)
            } else if (CookingViewModelFactory.getInScopeViewModel().isSecondaryCavity) {
                setUpperCavityBackground(false)
                setLowerCavityBackground(true)
            }
        }
    }

    /*
     * Below Method is used check the Lower cavity is available or not
     * */
    open fun checkLowerCavityAvailability() {
        val lowerCavityAvailableIn =
            CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.pyroCleanNotAllowedUntilTimeRemaining.value
        if (lowerCavityAvailableIn != null) {
            if (lowerCavityAvailableIn != 0L) {
                binding.lowerCavitySubtext.visibility = View.VISIBLE
                binding.lowerCavitySubtext.text = (CookingAppUtils.getPyroNotAllowedMessage(requireContext(),lowerCavityAvailableIn))
                isEnableLowerOven(false)
            } else {
                binding.lowerCavitySubtext.visibility = View.GONE
                isEnableLowerOven(true)
            }
        }
    }

    /*
     * Below Method is used check the Upper cavity is available or not
     * */
    open fun checkUpperCavityAvailability() {
        if (!isCombo) {
            val upperCavityAvailableIn =
                CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.pyroCleanNotAllowedUntilTimeRemaining.value
            if (upperCavityAvailableIn != null) {
                if (upperCavityAvailableIn != 0L) {
                    binding.upperCavitySubtext.visibility = View.VISIBLE
                    binding.upperCavitySubtext.text =
                        (CookingAppUtils.getPyroNotAllowedMessage(requireContext(),upperCavityAvailableIn))
                    isEnableUpperOven(false)
                } else {
                    binding.upperCavitySubtext.visibility = View.GONE
                    isEnableUpperOven(true)
                }
            }
        }
    }

    /*
     * Below Method is used to initialisation of Listeners
     * */
    open fun initListener() {
        binding.upperOvenLayout.setOnClickListener(this)
        binding.upperOvenArea.setOnClickListener(this)
        binding.lowerOvenArea.setOnClickListener(this)
        binding.lowerOvenLayout.setOnClickListener(this)
        binding.singleLineHeaderBar.setCustomOnClickListener(this)
        binding.backArrowArea.setOnClickListener(this)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
    }


    /*
     * get the cavity list for tools.json
     * */
    open fun prepareToolsList(): Boolean {
        menuItems =
            CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SELF_CLEAN)!!
        return true
    }

    /*
     * initialisation double oven cavity text to button
     * */
    @SuppressLint("SetTextI18n", "DiscouragedApi")
    open fun initViewsDoubleOven() {
        binding.uppperCavityLbl.text = getString(R.string.cavity_selection_upper_oven_all_caps)
        binding.lowerCavityLbl.text = getString(R.string.cavity_selection_lower_oven_all_cap)
    }

    override fun leftIconOnClick() {
        providesBackPressNav()
    }

    override fun rightIconOnClick() {
        if (binding.upperOvenArea.isClickable) {
            animateLayoutsAndNavigate(R.id.upper_oven_area)
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.back_arrow_area) {
            providesBackPressNav()
        } else {
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            // Trigger animations for both layouts
            if (view.isEnabled)
                animateLayoutsAndNavigate(view.id)
        }
    }

    fun animateLayoutsAndNavigate(clickedLayoutId: Int) {
        // Load animations from XML
        val upperOvenAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_fade_out_to_top)
        val lowerOvenAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_fade_out_to_bottom)

        // Set up a listener to navigate after the animation ends
        val animationListener = object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                // Optional: You can add any action before the animation starts
            }

            override fun onAnimationEnd(animation: Animation) {
                // Make both layouts invisible after the animation ends
                binding.upperOvenLayout.visibility = View.INVISIBLE
                binding.lowerOvenLayout.visibility = View.INVISIBLE

                // Continue with fragment navigation
                val viewModel = when (clickedLayoutId) {
                    R.id.upper_oven_layout, R.id.upper_oven_area -> CookingViewModelFactory.getPrimaryCavityViewModel()
                    R.id.lower_oven_layout, R.id.lower_oven_area -> CookingViewModelFactory.getSecondaryCavityViewModel()
                    else -> return
                }

                setInScopeViewModel(viewModel)
                HMIExpansionUtils.setLightForCancelButton(true)

                if ((clickedLayoutId == R.id.upper_oven_layout) || (clickedLayoutId == R.id.upper_oven_area)) {
                    navigateToUpperOven()
                } else {
                    navigateToLowerOven()
                }
            }

            override fun onAnimationRepeat(animation: Animation) {
                // No action needed on repeat
            }
        }

        // Set the listener for both animations
        upperOvenAnimation.setAnimationListener(animationListener)
        lowerOvenAnimation.setAnimationListener(animationListener)

        // Start animations on both layouts
        binding.upperOvenLayout.startAnimation(upperOvenAnimation)
        binding.lowerOvenLayout.startAnimation(lowerOvenAnimation)
    }

    /*
     * verify the Cavity Type
     * */
    open fun checkCavityType() {
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                isCombo = true
                setupCombo()
            }

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                isCombo = false
                setupDoubleOven()
            }

            else -> {}
        }
    }

    /*
     * Use to Enable unable the upper oven Cavity
     * */
    open fun isEnableUpperOven(isActive: Boolean) {
        if (isActive) {
            binding.upperOvenLayout.isClickable = true
            binding.upperOvenLayout.isEnabled = true
            binding.uppperCavityLbl.setTextColor(requireActivity().getColor(R.color.solid_white))
            binding.upperCavitySubtext.setTextColor(requireActivity().getColor(R.color.solid_white))
        } else {
            binding.upperOvenLayout.isClickable = false
            binding.upperOvenLayout.isEnabled = false
            binding.upperOvenArea.isClickable = false
            binding.upperOvenArea.isEnabled = false
            binding.uppperCavityLbl.setTextColor(requireActivity().getColor(R.color.cavity_disable_button_text_color))
            binding.upperCavitySubtext.setTextColor(requireActivity().getColor(R.color.cavity_disable_button_text_color))

        }
    }

    /*
     * Use to Enable unable the lower oven Cavity
     * */
    private fun isEnableLowerOven(isActive: Boolean) {
        if (isActive) {
            binding.lowerOvenLayout.isClickable = true
            binding.lowerOvenLayout.isEnabled = true
            binding.lowerCavityLbl.setTextColor(requireActivity().getColor(R.color.solid_white))
            binding.lowerCavitySubtext.setTextColor(requireActivity().getColor(R.color.solid_white))
        } else {
            binding.lowerOvenLayout.isClickable = false
            binding.lowerOvenLayout.isEnabled = false
            binding.lowerOvenArea.isClickable = false
            binding.lowerOvenArea.isEnabled = false
            binding.lowerCavityLbl.setTextColor(requireActivity().getColor(R.color.cavity_disable_button_text_color))
            binding.lowerCavitySubtext.setTextColor(requireActivity().getColor(R.color.cavity_disable_button_text_color))
        }
    }

    /*
     * set up the text for combo variant
     * */
    @SuppressLint("SetTextI18n")
    open fun setupCombo() {
        binding.uppperCavityLbl.text = getString(R.string.cavity_selection_microwave_all_caps)
        binding.lowerCavityLbl.text = getString(R.string.cavity_selection_lower_oven_all_cap)
        isEnableUpperOven(true)
        isEnableLowerOven(true)
    }

    /*
     * set up the double oven cavity*/
    open fun setupDoubleOven() {
        initViewsDoubleOven()
        isEnableUpperOven(true)
        isEnableLowerOven(true)
    }

    /*set the upper oven cavity background according to selection*/
    open fun setUpperCavityBackground(isSelected: Boolean) {
        if (isSelected) {
            binding.upperOvenLayout.background =
                getDrawable(requireContext(), R.drawable.button_selected_ripple_effect)
        } else {
            binding.upperOvenLayout.background =
                getDrawable(requireContext(), R.drawable.button_idle_ripple_effect)
        }
    }

    /*set the lower oven cavity background according to selection*/
    open fun setLowerCavityBackground(isSelected: Boolean) {
        if (isSelected) {
            binding.lowerOvenLayout.background =
                getDrawable(requireContext(), R.drawable.button_selected_ripple_effect)
        } else {
            binding.lowerOvenLayout.background =
                getDrawable(requireContext(), R.drawable.button_idle_ripple_effect)
        }
    }

    /**
     * Method to manage knob rotation
     * */
    @Suppress("KotlinConstantConditions")
    protected fun manageKnobRotation(knobDirection: String) {
        if (knobDirection == KnobDirection.CLOCK_WISE_DIRECTION && knobRotationCount < CAVITY_SELECTION_KNOB_SIZE) knobRotationCount++
        else if (knobDirection == KnobDirection.COUNTER_CLOCK_WISE_DIRECTION && knobRotationCount > 0) knobRotationCount--
        isKnobRotated = true
        when (knobRotationCount) {
            KNOB_COUNTER_ONE -> {
                setUpperCavityBackground(true)
                setLowerCavityBackground(false)
                setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
                isUpperOvenSelected = true
            }

            KNOB_COUNTER_TWO -> {
                setUpperCavityBackground(false)
                setLowerCavityBackground(true)
                setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
                isUpperOvenSelected = false
            }
        }
    }

    /**
     * Method to manage knob click
     * */
    protected fun manageKnobClickListener() {
        @Suppress("KotlinConstantConditions")
        if (isKnobRotated && isUpperOvenSelected && binding.upperOvenLayout.isEnabled) {
            onClick(binding.upperOvenLayout)
        } else if (isKnobRotated && !isUpperOvenSelected && binding.lowerOvenLayout.isEnabled) {
            onClick(binding.lowerOvenLayout)
        }
    }

    /*
     * set the in scope view model
     * */
    open fun setInScopeViewModel(cookingViewModel: CookingViewModel) {
        CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
    }

    /**
     * Method to animate cavity buttons
     */
    private fun animateCavityButtons() {
        binding.upperOvenLayout.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.anim_top_side_widget_fade_in
            )
        )
        binding.lowerOvenLayout.startAnimation(
            AnimationUtils.loadAnimation(
                context,
                R.anim.anim_bottom_side_widget_fade_in
            )
        )
    }

    private fun observeDemoModeLiveData() {
        SettingsViewModel.getSettingsViewModel().demoMode.observe(
            viewLifecycleOwner
        ) { demoMode: Int? ->
            when (demoMode) {
                SettingsRepository.DemoMode.DEMO_MODE_ENABLED -> {
                    HMILogHelper.Logd("DEMO MODE IS ACTIVE")
                    binding.homeHeader.getBinding().demoIcon.visibility = View.VISIBLE
                }

                SettingsRepository.DemoMode.DEMO_MODE_DISABLED -> {
                    HMILogHelper.Logd("DEMO MODE IS NOT ACTIVE")
                    binding.homeHeader.getBinding().demoIcon.visibility = View.INVISIBLE
                }

                else -> {
                    HMILogHelper.Logd("DEMO MODE IS NOT ACTIVE")
                    binding.homeHeader.getBinding().demoIcon.visibility = View.INVISIBLE
                }
            }
        }
    }

    /*used to navigate upper oven*/
    protected open fun navigateToUpperOven(){
        NavigationUtils.navigateToUpperRecipeSelection(this)
    }

    /*used to navigate lower oven*/
    protected open fun navigateToLowerOven() {
        NavigationUtils.navigateToLowerRecipeSelection(this)
    }

    /*set the  header bar view*/
    protected abstract fun headerBarSetUp()

    /*set the  technician mode text view*/
    protected abstract fun updateTechnicianTextModeText()

    /*used to navigate on back press icon*/
    protected abstract fun providesBackPressNav()

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        super.onDestroyView()
    }

    override fun onHMILeftKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMIRightKnobClick() {
        manageKnobClickListener()
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            manageKnobRotation(knobDirection)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        isKnobRotated = false
        setUpperCavityBackground(false)
        setLowerCavityBackground(false)
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}