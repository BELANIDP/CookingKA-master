/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerList
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.Keyboard
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel
import com.whirlpool.hmi.utils.timers.Timer
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.EMPTY_SPACE
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CavityStateUtils
import core.utils.CommonAnimationUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getCookTimerStringAsSeconds
import core.utils.DoorEventUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.NumPadHelperTextColor
import core.utils.PopUpBuilderUtils
import core.utils.TextWidthSpan
import core.utils.TimeUtils
import core.viewHolderHelpers.CookTimeNumberPadViewHolderHelper
import java.util.Objects

/**
 * File        : core.jbase.AbstractCookTimeNumberPadFragment.
 * Brief       : Cook time NumberPad Abstract class
 * Author      : GHARDNS/Nikki
 * Created On  : 18-03-2024
 */
abstract class AbstractCookTimeNumberPadFragment : SuperAbstractTimeoutEnableFragment(),
    View.OnClickListener,
    KeyboardInputManagerInterface, HMIKnobInteractionListener {


    /** To binding Fragment variables */
    var cookTimeNumberPadViewHolderHelper: CookTimeNumberPadViewHolderHelper? = null

    /** ViewModel instances */
    private var keyboardViewModel: KeyboardViewModel? = null
    protected var cookingViewModel: CookingViewModel? = null
    var recipeViewModel: RecipeExecutionViewModel? = null
    private var inScopeViewModel: CookingViewModel? = null
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null

    /** Boolean variables */
    protected var isOfTypeMicrowaveOven = false
    private var isScreenFreshlyLoaded = true
    /** Int variables */
    private var digitInputIndex = 0

    /** String variables */
    protected var cookTimeText: String? = null

    /**
     * To show Remove timer in Assisted Fresh Pizza in Assisted Preview -> cookTime update screen
     */
    protected var toShowRemoveTimerInAssisted: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        cookTimeNumberPadViewHolderHelper = CookTimeNumberPadViewHolderHelper()
        cookTimeNumberPadViewHolderHelper?.onCreateView(inflater, container, savedInstanceState)
        cookTimeNumberPadViewHolderHelper?.getBinding()?.lifecycleOwner = this
        cookTimeNumberPadViewHolderHelper?.getBinding()?.numberPadFragment = this
        return cookTimeNumberPadViewHolderHelper?.getBinding()?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cookTimeNumberPadViewHolderHelper?.getKeyboardView()?.startAnimation(
            android.view.animation.AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.zoom_in
            )
        )
        configutionHmiKey()
        registerHMIButtonListener()
        setUpViewModels()
        observeLiveData()
        setCavityViewModelByProductVariant()
        manageChildViews()
    }

    /**
     * Method to setup the required View models
     */
    private fun setUpViewModels() {
        //Init Keyboard View Model
        keyboardViewModel = ViewModelProvider(this)[KeyboardViewModel::class.java]
        keyboardViewModel?.initKeyboard(this)
        keyboardViewModel?.keyboardAlpha = Keyboard(requireContext(), R.xml.keyboard_numpad)
        keyboardViewModel?.disabledKeyPressedObserver?.observe(
            viewLifecycleOwner
        ) { disableKeyTapped: Boolean? ->
            if (disableKeyTapped != null) {
                this.disableTap(disableKeyTapped)
            }
        }
        cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
        recipeViewModel = cookingViewModel?.recipeExecutionViewModel
        isOfTypeMicrowaveOven = cookingViewModel?.isOfTypeMicrowaveOven == true
        cookTimeNumberPadViewHolderHelper?.getBinding()?.cookingViewModel = cookingViewModel
    }

    private fun disableTap(disableKeyTapped: Boolean) {
        if (java.lang.Boolean.TRUE == disableKeyTapped) {
            manageHelperText()
        }
    }

    /**
     * Method to manage Helper text
     */
    private fun manageHelperText() {
        cookTimeNumberPadViewHolderHelper?.getBinding()?.textViewHelperText?.visibility =
            View.INVISIBLE
        val cookTimeOptionRange = provideIntegerRangeAsPerRecipe()
        if (cookTimeOptionRange != null) {

            if (isOfTypeMicrowaveOven && isMagnetronBasedRecipe()) {
                val minutes = cookTimeOptionRange.max / 60
                val seconds = cookTimeOptionRange.max % 60
                cookTimeNumberPadViewHolderHelper?.getBinding()?.textViewHelperText?.text =
                    String.format(
                        CookingAppUtils.getStringFromResourceId(
                            requireContext(),
                            provideHelperTextString()
                        ),
                        minutes.toString() + EMPTY_SPACE + getString(R.string.text_label_m) + EMPTY_SPACE + seconds.toString() + EMPTY_SPACE + getString(R.string.text_label_s)
                    )
            } else {
                val hours = cookTimeOptionRange.max / 3600
                val minutes = (cookTimeOptionRange.max % 3600) / 60
                cookTimeNumberPadViewHolderHelper?.getBinding()?.textViewHelperText?.text =
                    String.format(
                        CookingAppUtils.getStringFromResourceId(
                            requireContext(),
                            provideHelperTextString()
                        ),
                        hours.toString() + EMPTY_SPACE + getString(R.string.text_label_h) + EMPTY_SPACE +
                                minutes + EMPTY_SPACE + getString(R.string.text_label_m)
                    )
            }
            cookTimeNumberPadViewHolderHelper?.getBinding()?.textViewHelperText?.visibility =
                View.VISIBLE

            CommonAnimationUtils.setErrorHelperAnimation(
                context,
                cookTimeNumberPadViewHolderHelper?.getBinding()?.textViewHelperText
            )
            CookingAppUtils.setHelperTextColor(
                cookTimeNumberPadViewHolderHelper?.getBinding()?.textViewHelperText,
                NumPadHelperTextColor.ERROR_TEXT_COLOR
            )
        } else {
            HMILogHelper.Loge("getCookTimeOption() returns NULL")
        }
    }

    /**
     * Method to get the helper text string resource
     */
    private fun provideHelperTextString(): Int {
        return R.string.text_error_helper_description
    }

    /**
     * Method to check whether recipe is magnetron based or not
     * useful when determining heating element is going to be used or not ex for cook time selection Minutes and Seconds be only used for recipe with magnetron
     * @return boolean true if magnetron used false otherwise
     */
    protected fun isMagnetronBasedRecipe(): Boolean {
        return cookingViewModel?.recipeExecutionViewModel?.isMagnetronUsed == true
    }


    /**
     * register the event-  setHMICleanButtonInteractionListener and setHMICancelButtonInteractionListener
     */
    private fun registerHMIButtonListener() {
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
    }

    /**
     * manage child views
     */
    private fun manageChildViews() {
        manageHeaderBar()
        manageLeftPowerButton()
        manageLeftButton()
        manageRightButton()
        manageMiddleButton()
        manageInputTextField(provideDefaultCookTime())
    }


    /**
     * Observe live data for keyboard text entry
     */
    private fun observeLiveData() {
        keyboardViewModel?.onTextEntry()?.observe(
            viewLifecycleOwner
        ) { text: CharSequence? ->
            this.onTextEntry(
                text
            )
        }
        observePreheatingLiveData()
        observeCookTimerLiveData()
    }

    /**
     * Method to make the text button enable or disable based on preheating live data
     * if this is not required, respective child classes will override the function
     */
    protected open fun observePreheatingLiveData() {
        if (CookingAppUtils.isRequiredTargetAvailable(
                cookingViewModel,
                RecipeOptions.TARGET_TEMPERATURE
            )
        )
            cookingViewModel?.recipeExecutionViewModel?.recipeCookingState
                ?.observe(viewLifecycleOwner) {
                    if (!toShowRemoveTimerInAssisted) {
                        NavigationUtils.manageLeftButtonForRecipeOption(
                            cookingViewModel,
                            RecipeOptions.COOK_TIME,
                            cookTimeNumberPadViewHolderHelper?.getLeftTextButton()
                        )
                    }
                    if (cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.visibility == View.VISIBLE) {
                        cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.visibility =
                            View.VISIBLE
                    } else {
                        cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.visibility =
                            View.GONE
                    }
                    disableNumPadItems(
                        0,
                        cookTimeText.toString(),
                        it != RecipeCookingState.PREHEATING
                    )
                    if (it == RecipeCookingState.PREHEATING && (cookTimeText.toString()
                            .contentEquals(AppConstants.DEFAULT_COOK_TIME) || cookTimeText.toString()
                            .contentEquals(AppConstants.DEFAULT_COOK_TIME_MICROWAVE))
                    ) {
                        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled = false
                        cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.isEnabled = false
                    }
                }
    }

    /**
     * Method to make the text middle button invisible based on cookTimer state
     * if this is not required, respective child classes will override the function
     */
    protected open fun observeCookTimerLiveData() {
        recipeViewModel?.cookTimerState
            ?.observe(
                viewLifecycleOwner
            ) { timerState: Timer.State? ->
                if (Timer.State.COMPLETED == timerState
                ) {
                    //In case of cook time is complete and coming back from cook time complete popup,
                    // hiding the remove timer button
                    cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
                    cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.visibility = View.GONE
                }
            }
    }

    /**
     * Method to update the Input Text Field on text entry
     *
     * @param text text entered
     */
    private fun onTextEntry(text: CharSequence?) {
        if (isOfTypeMicrowaveOven && isMagnetronBasedRecipe()) {
            if (text != null) {
                handleTextEntryForDisabledHoursField(text)
            }
        } else {
            if (text != null) {
                handleTextEntryForDisabledSecondsField(text)
            }
        }
        updateCookTimerTextInInputField()
        disableNumPadItems(
            if (digitInputIndex == -1) 0 else digitInputIndex,
            cookTimeText.toString(),
            digitInputIndex == -1
        )
        if (isValidCookTime()) {
            cookTimeNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility = View.INVISIBLE
        }
    }

    /**
     * Method to handle the text Entry in normal oven cook time, last two char field (seconds
     * field) will always be zero. Newly entered char is appended at 4th position
     *
     * @param text text clicked from the number  pad
     */
    protected open fun handleTextEntryForDisabledSecondsField(text: CharSequence) {
        //Incrementing the digit by 1  so as to clear value when input field is filled with 4 values
        digitInputIndex++

        //First time loading - Filling value with 0s
        if (isScreenFreshlyLoaded && cookTimeText != AppConstants.EMPTY_STRING) {
            isScreenFreshlyLoaded = false
            cookTimeText = AppConstants.DEFAULT_TRIPLE_ZERO + text
            digitInputIndex = 0
        } else {
            //Clearing all values when user enters more than 4 digits and starting from units place again.
            if (digitInputIndex > 3) {
                cookTimeText = AppConstants.DEFAULT_TRIPLE_ZERO + text
                digitInputIndex = if (text.toString().toInt() == 0) {
                    -1
                } else {
                    0
                }
            } else cookTimeText = getCurrentInputFieldContent().substring(1, 4) +
                    text[text.length - 1]
        }
    }

    /**
     * Method to handle the text Entry in normal microwave cook time, first two char field (hours field)
     * will always be zero. Newly entered char is appended at 6th position
     *
     * @param text text clicked from the number  pad
     */
    private fun handleTextEntryForDisabledHoursField(text: CharSequence) {
        /*In microwave cook time, first two char field(hours field) will always be zero.
            Newly entered char is appended at 6th position*/

        //Incrementing the digit by 1  so as to clear value when input field is filled with 4 values
        digitInputIndex++
        //First time loading - Filling value with 0s
        if (isScreenFreshlyLoaded && cookTimeText != AppConstants.EMPTY_STRING) {
            isScreenFreshlyLoaded = false
            cookTimeText = AppConstants.DEFAULT_TRIPLE_ZERO + text
            digitInputIndex = 0
        } else {
            //Clearing all values when user enters more than 4 digits and starting from units place again.
            if (digitInputIndex > 3) {
                cookTimeText = AppConstants.DEFAULT_TRIPLE_ZERO + text
                digitInputIndex = if (text.toString().toInt() == 0) {
                    -1
                } else {
                    0
                }
            } else {
                if (getCurrentInputFieldContent().length > 3) {
                    when (digitInputIndex) {
                        0, 1, 2 -> cookTimeText =
                            AppConstants.DEFAULT_LEVEL + getCurrentInputFieldContent().substring(
                                2,
                                4
                            ) + text[text.length - 1]

                        3 -> cookTimeText = getCurrentInputFieldContent().substring(1, 4) +
                                text[text.length - 1]
                    }
                }
            }
        }
    }


    /**
     * set primaryCavityViewModel according to the the selected product variant
     */
    private fun setCavityViewModelByProductVariant() {
        productVariant = CookingViewModelFactory.getProductVariantEnum()
        inScopeViewModel = CookingViewModelFactory.getInScopeViewModel()
    }

    /**
     * Method to manage HeaderBar widget
     */
    private fun manageHeaderBar() {
        when (productVariant) {
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN -> {
            }

            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
            }

            CookingViewModelFactory.ProductVariantEnum.COMBO -> {
            }

            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
            }

            else -> {}
        }
        cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()?.setBackIconOnClickListener {
            AudioManagerUtils.playOneShotSound(
                view?.context,
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            //Header left icon click event
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    getViewSafely(this) ?: requireView()
                )
            )
        }
        cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setCancelIconOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                onBackSpaceIconClick(isOfTypeMicrowaveOven)
            }
        cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setTumblerIconOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                navigateToVerticalTimeTumblerScreen()
            }
    }

    /**
     * Method to handle the left buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageLeftButton() {
        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.setOnClickListener(this)
        cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.setOnClickListener(this)
        NavigationUtils.manageLeftButtonForRecipeOption(
            cookingViewModel,
            RecipeOptions.COOK_TIME,
            cookTimeNumberPadViewHolderHelper?.getLeftTextButton()
        )
        if(cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.visibility == View.VISIBLE){
            cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.visibility = View.VISIBLE
        }
        else{
            cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.visibility = View.GONE
        }
    }

    /**
     * Method to handle the Right buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageRightButton() {
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.setOnClickListener(this)
        cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.setOnClickListener(this)
        if (cookTimeText == null) {
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = false
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = false

            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isEnabled = false
            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isClickable = false
        } else {
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = isValidCookTime()
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = isValidCookTime()

            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isEnabled = isValidCookTime()
            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isClickable = isValidCookTime()
        }
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()
            ?.setTextButtonText(
                updateRightButtonText()
            )
    }

    protected open fun updateRightButtonText(): String {
        return NavigationUtils.getRightButtonTextForRecipeOption(
            context,
            cookingViewModel,
            provideRecipeOption()
        )
    }

    /**
     * Override this method if child class is not meant for COOK_TIME
     *
     * @return recipe option
     */
    open fun provideRecipeOption(): RecipeOptions {
        return RecipeOptions.COOK_TIME
    }

    /**
     * Method to handle the middle buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageMiddleButton() {
        cookTimeNumberPadViewHolderHelper?.getMiddleTextButton()?.setOnClickListener(this)
        cookTimeNumberPadViewHolderHelper?.getMiddleTextButton()?.visibility = View.GONE

    }

    /**
     * Method to handle the left power buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageLeftPowerButton() {
        if (cookingViewModel?.isOfTypeMicrowaveOven == true && CookingAppUtils.isRequiredTargetAvailable(
                cookingViewModel, RecipeOptions.MWO_POWER_LEVEL
            )
        ) {
            cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.setOnClickListener(this)
            cookTimeNumberPadViewHolderHelper?.getLeftPowerConstraint()?.setOnClickListener(this)
            cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.visibility = View.VISIBLE
            val selectedPowerLevel =
                updateDefaultPowerLevel(cookingViewModel)
            cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.setTextButtonText(
                if (CookingAppUtils.isRecipeReheatInProgramming(cookingViewModel)) getString(
                    R.string.text_button_moreOption
                ) else getString(
                    R.string.text_MWO_cook_power_level, selectedPowerLevel
                )
            )
        } else {
            cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.visibility = View.GONE
            cookTimeNumberPadViewHolderHelper?.getLeftPowerConstraint()?.visibility = View.GONE
        }
    }


    /**
     * Method to set the initial time text
     *
     * @param  defaultValue text
     */
    protected open fun manageInputTextField(defaultValue: Int) {
        cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.getTextViewCookTime()?.visibility =
            View.VISIBLE

        if (arguments != null && arguments?.containsKey(BundleKeys.BUNDLE_PROVISIONING_TIME) == true) {
            val timeString: String? = TimeUtils.getTimeStringArguments(arguments)
            if (timeString != AppConstants.EMPTY_STRING) {
                cookTimeText = timeString
            } else {
                cookTimeText = AppConstants.DEFAULT_COOK_TIME_MICROWAVE
                disableTextButtons()
            }
        } else if (recipeViewModel?.cookTimerState?.value == Timer.State.IDLE) {
            val remainingTime: TimeUtils.Time =
                defaultValue.toLong().let { TimeUtils.convertTime(it) }
            cookTimeText = getTimerText(remainingTime)
            if (cookTimeText == AppConstants.EMPTY_STRING) {
                cookTimeText = AppConstants.DEFAULT_COOK_TIME_MICROWAVE
                disableTextButtons()
            }
        } else {
            //In change cook time scenario, the title text is set to remaining time
            //MAF_2526: The last recorded cook time is not displayed when the user attempts to modify the cook time.
            //if for extended cooking choose remaining cook time as set cook time will be based on non editable options, ex convect slow roast
            var time = if(AbstractStatusFragment.isExtendedCookingForNonEditableCookTimeRecipe(cookingViewModel)) cookingViewModel?.recipeExecutionViewModel?.remainingCookTime?.value else cookingViewModel?.recipeExecutionViewModel?.cookTime?.value
            if ((time == null || time.toInt() == 0) && cookingViewModel?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.IDLE) {
                time = defaultValue.toLong()
            }
            val remainingTime: TimeUtils.Time? = time?.let { TimeUtils.convertTime(it) }
            cookTimeText = remainingTime?.let { getTimerText(it) }
        }
        updateCookTimerTextInInputField()
    }

    /**
     * remaining time is given in seconds. So formatting the string
     *
     * @param remainingTime remaining time in seconds
     * @return cook time text in the required format
     */
    protected open fun getTimerText(remainingTime: TimeUtils.Time): String? {
        return if (isOfTypeMicrowaveOven && isMagnetronBasedRecipe()) {
            //Hours is not Applicable in normal Microwave Cook Time
            getMinutesAndSecondsInSeconds(remainingTime)
        } else {
            //Seconds is not Applicable in normal Oven Cook Time
            getHoursAndMinutesInSeconds(remainingTime)
        }
    }

    /**
     * Method to get the remaining hours and Minutes in Seconds
     *
     * @param remainingTime remaining time to be manipulated
     * @return String remainingTimer in seconds string
     */
    protected open fun getHoursAndMinutesInSeconds(remainingTime: TimeUtils.Time): String? {
        var hours: String = AppConstants.EMPTY_STRING
        var minutes: String = AppConstants.EMPTY_STRING
        var seconds: String = AppConstants.EMPTY_STRING
        if (remainingTime.hours < 10) {
            hours = AppConstants.DEFAULT_LEVEL
            digitInputIndex = 2
        } else digitInputIndex = 3
        if (remainingTime.minutes < 10) {
            minutes = AppConstants.DEFAULT_LEVEL
            digitInputIndex = 0
        } else digitInputIndex = 1
        if (remainingTime.seconds < 10) {
            seconds = AppConstants.DEFAULT_LEVEL
            digitInputIndex = 0
        } else digitInputIndex = 1
        hours += remainingTime.hours
        minutes += remainingTime.minutes
        seconds += remainingTime.minutes
        return hours + minutes + seconds
    }

    /**
     * Method to get the remaining Minutes and Seconds in Seconds
     *
     * @param remainingTime remaining time to be manipulated
     * @return String remainingTimer in seconds string
     */
    protected open fun getMinutesAndSecondsInSeconds(remainingTime: TimeUtils.Time): String? {
        var minutes: String = AppConstants.EMPTY_STRING
        var seconds: String = AppConstants.EMPTY_STRING
        if (remainingTime.minutes < 10) {
            minutes = AppConstants.DEFAULT_LEVEL
            digitInputIndex = 2
        } else digitInputIndex = 3
        if (remainingTime.seconds < 10) {
            seconds = AppConstants.DEFAULT_LEVEL
            digitInputIndex = 0
        } else digitInputIndex = 1
        minutes = if (remainingTime.hours > 0) {
            minutes + (remainingTime.hours * 60 + remainingTime.minutes)
        } else {
            minutes + remainingTime.minutes
        }
        seconds += remainingTime.seconds
        return minutes + seconds
    }

    /**
     * Disable buttons
     */
    protected open fun disableTextButtons() {
        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled = false
        cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.isEnabled = false
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = false
        cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isEnabled = false
        cookTimeNumberPadViewHolderHelper?.getMiddleTextButton()?.isEnabled = false
        cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isEnabled = false
        cookTimeNumberPadViewHolderHelper?.getLeftPowerConstraint()?.isEnabled = false

        cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isClickable = false
        cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.isClickable = false
        cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = false
        cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isClickable = false
        cookTimeNumberPadViewHolderHelper?.getMiddleTextButton()?.isClickable = false
        cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isClickable = false
        cookTimeNumberPadViewHolderHelper?.getLeftPowerConstraint()?.isClickable = false
    }

    protected open fun enableTextButtons() {
        if (cookTimeText != null && cookTimeText != AppConstants.DEFAULT_COOK_TIME) {
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = true
            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isEnabled = true
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = true
            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isClickable = true
            if (cookingViewModel?.isOfTypeMicrowaveOven == true && CookingAppUtils.isRequiredTargetAvailable(
                    cookingViewModel,
                    RecipeOptions.MWO_POWER_LEVEL
                )
            ) {
                cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isEnabled = true
                cookTimeNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isClickable = true
                cookTimeNumberPadViewHolderHelper?.getLeftPowerConstraint()?.isEnabled = true
                cookTimeNumberPadViewHolderHelper?.getLeftPowerConstraint()?.isClickable = true
            }
        }

        if (cookingViewModel?.recipeExecutionViewModel?.cookTime?.value != null
            && cookingViewModel?.recipeExecutionViewModel?.cookTime?.value == 0L
            && SettingsViewModel.getSettingsViewModel().sabbathMode.value == SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT
        ) {
            cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled =
                cookingViewModel?.recipeExecutionViewModel
                    ?.recipeCookingState?.value == RecipeCookingState.PREHEATING
            cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isClickable =
                cookingViewModel?.recipeExecutionViewModel
                    ?.recipeCookingState?.value == RecipeCookingState.PREHEATING
            cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.isEnabled =
                cookingViewModel?.recipeExecutionViewModel
                    ?.recipeCookingState?.value == RecipeCookingState.PREHEATING
            cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.isClickable =
                cookingViewModel?.recipeExecutionViewModel
                    ?.recipeCookingState?.value == RecipeCookingState.PREHEATING
        } else {
            cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled = true
            cookTimeNumberPadViewHolderHelper?.getLeftTextButton()?.isClickable = true
            cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.isEnabled = true
            cookTimeNumberPadViewHolderHelper?.getLeftConstraint()?.isClickable = true
        }
        cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()?.setCancelIconEnable(true)
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.cook_time_text_button_left -> {

                if (recipeViewModel?.cookTimerState?.value != Timer.State.IDLE && recipeViewModel?.cookTimerState?.value != Timer.State.COMPLETED) {
                    //In changing cook time scenario, on clicking "remove timer" cancels the cook timer
                    if (Objects.equals(
                            recipeViewModel?.cancelCookTimer(),
                            RecipeErrorResponse.NO_ERROR
                        )
                    ) {
                        CookingAppUtils.navigateToStatusOrClockScreen(this)
                    }

                } else if (toShowRemoveTimerInAssisted) {
                    onClickListener?.onLeftButtonClick()
                } else if (validateCookTime() && setCookTime(
                        getCookTimerStringAsSeconds(
                            cookTimeText,
                            !isOfTypeMicrowaveOven || !isMagnetronBasedRecipe()
                        ).toLong()
                    )
                ) {
                    CookingAppUtils.navigateToStatusOrClockScreen(this)
                }
            }

            R.id.constraintLeft -> {
                if (recipeViewModel?.cookTimerState?.value != Timer.State.IDLE && recipeViewModel?.cookTimerState?.value != Timer.State.COMPLETED) {
                    //In changing cook time scenario, on clicking "remove timer" cancels the cook timer
                    if (Objects.equals(
                            recipeViewModel?.cancelCookTimer(),
                            RecipeErrorResponse.NO_ERROR
                        )
                    ) {
                        CookingAppUtils.navigateToStatusOrClockScreen(this)
                    }

                } else if (toShowRemoveTimerInAssisted) {
                    onClickListener?.onLeftButtonClick()
                } else if (validateCookTime() && setCookTime(
                        getCookTimerStringAsSeconds(
                            cookTimeText,
                            !isOfTypeMicrowaveOven || !isMagnetronBasedRecipe()
                        ).toLong()
                    )
                ) {
                    CookingAppUtils.navigateToStatusOrClockScreen(this)
                }
            }

            R.id.cook_time_text_button_right -> {
                onClickListener?.onRightButtonClick()
            }

            R.id.constraintNumpadRight -> {
                onClickListener?.onRightButtonClick()
            }

            R.id.cook_time_text_button_middle -> {
                onClickListener?.onMiddleButtonClick()
            }

            R.id.cook_time_text_button_left_power -> {
                onClickListener?.onLeftPowerButtonClick()
            }
            R.id.constraintNumpadLeft -> {
                onClickListener?.onLeftPowerButtonClick()
            }
        }
    }

    /**
     * Validates the cook time and changes the color of helper text accordingly.
     *
     * @return true if the cook time is valid.
     */
    protected open fun validateCookTime(): Boolean {
        val isValid = isValidCookTime()
        if (!isValid) {
            manageHelperText()
            cookTimeNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.VISIBLE
            CookingAppUtils.setHelperTextColor(
                cookTimeNumberPadViewHolderHelper?.getErrorHelperTextView(),
                NumPadHelperTextColor.ERROR_TEXT_COLOR
            )
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = false
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = false

            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isEnabled = false
            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isClickable = false
        } else {
            cookTimeNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.INVISIBLE
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = true
            cookTimeNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = true

            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isEnabled = true
            cookTimeNumberPadViewHolderHelper?.getRightConstraint()?.isClickable = true
        }
        return isValid
    }

    /**
     * Method to set the just set the cook time - When the timer is in PAUSED state or RUNNING state
     * the cycle need not be started explicitly. Used for Save for later also.
     *
     * @param cookTimeSec : Cook time value to be set.
     * @return : Returns whether the sdk call is success
     */
    protected open fun setCookTime(cookTimeSec: Long): Boolean {
        val recipeErrorResponse = recipeViewModel?.setCookTime(cookTimeSec)
        HMILogHelper.Logi("Set Cook time: " + cookTimeSec + "response : " + recipeErrorResponse)
        return recipeErrorResponse == RecipeErrorResponse.NO_ERROR
    }

    /**
     * Method to handle the Back space Icon click based on enabled fields. When backspace is
     * pressed, first char will be deleted and other char will be shifted to right by one position
     *
     * @param isHoursFieldDisabled, True if the hours Field is disabled and non editable
     * Currently, we have two possibilities, either thr hour field
     * will be disabled (eg: microwave) or the seconds field will be
     * disabled (eg: normal oven). Need to change the boolean
     * variable, if additional possibilities added in the future.
     */
    protected open fun onBackSpaceIconClick(isHoursFieldDisabled: Boolean) {
        //Decreasing digit count by 1 when backspace is pressed.
        digitInputIndex--
        if (digitInputIndex < 0) {
            digitInputIndex = -1
        }
        //Delete last digit of seconds
        cookTimeText =
            getCurrentInputFieldContent().substring(0, getCurrentInputFieldContent().length - 1)
        when (cookTimeText?.length) {
            5, 3 -> cookTimeText = AppConstants.DEFAULT_LEVEL + cookTimeText
            4, 2 -> cookTimeText = AppConstants.DEFAULT_DOUBLE_ZERO + cookTimeText
            1 -> cookTimeText = AppConstants.DEFAULT_TRIPLE_ZERO + cookTimeText
            0 -> cookTimeText = AppConstants.DEFAULT_COOK_TIME_MICROWAVE
        }
        updateCookTimerTextInInputField()
        if (digitInputIndex == -1) disableNumPadItems(
            0,
            cookTimeText.toString(),
            true
        ) else disableNumPadItems(digitInputIndex, cookTimeText.toString(), false)
        if (isValidCookTime()) {
            cookTimeNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.INVISIBLE
        } //0000
    }

    /**
     * Method To fetch the current input in the numPad entry filed
     *
     * @return appended values of all the three fields
     */
    private fun getCurrentInputFieldContent(): String {
        return if ((cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                ?.getTextViewCookTime()?.text?.length ?: 0) >= 6
            || cookingViewModel?.isOfTypeMicrowaveOven == true
        ) {
            cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                ?.getTextViewCookTime()?.text?.toString()
                ?.substring(0, 2) +
                    cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                        ?.getTextViewCookTime()?.text?.toString()
                        ?.substring(3, 5)
        } else {
            cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                ?.getTextViewCookTime()?.text?.toString()
                ?.substring(0, 2) +
                    cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                        ?.getTextViewCookTime()?.text?.toString()
                        ?.substring(3, 5) +
                    cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                        ?.getTextViewCookTime()?.text?.toString()
                        ?.substring(6, 8)
        }
    }

    /**
     * Method that disables the numPad keys list based on the current Cook time input.
     * decoupled  the logic to disable the keys from the View as it can be Unit tested
     *
     * @param index        Index
     * @param currentValue Current selection value
     */
    protected open fun disableNumPadItems(
        index: Int,
        currentValue: String?,
        isNumPadLoadedFirstTime: Boolean,
    ) {
        val cookTimeOptionRange = provideIntegerRangeAsPerRecipe()
        if (cookTimeOptionRange != null) {
            cookTimeNumberPadViewHolderHelper?.getKeyboardView()
                ?.disableKeyWithKeyLabels(
                    currentValue?.let {
                        TimeUtils.disableKeypadItemsForCookTime(
                            index,
                            it,
                            cookTimeOptionRange.min.toString(),
                            cookTimeOptionRange.max.toString(),
                            isNumPadLoadedFirstTime,
                            isOfTypeMicrowaveOven
                        )
                    }
                )
        } else {
            HMILogHelper.Loge("getTargetCookTimeOptions returns null" + "")
        }
    }

    /**
     * Method to manipulate te cook timer string with appended respective units
     * and update the formatted string in the input field text view
     */
    protected open fun updateCookTimerTextInInputField() {
        if (cookTimeText == AppConstants.DEFAULT_COOK_TIME_MICROWAVE) {
            disableTextButtons()
        } else {
            enableTextButtons()
        }
        val widthStatusCookTimer = resources
            .getDimension(R.dimen.width_status_cook_timer_digit).toInt()
        val widthStatusCookTimerHmsLabel = resources
            .getDimension(R.dimen.width_status_cook_timer_hms_label).toInt()

        val widthStatusCookTimerHmsLabelAlignRight = resources
            .getDimension(R.dimen.width_status_cook_timer_hms_label_align_right).toInt()
        val widthStatusCookTimerMinLabelAlignRight = resources
            .getDimension(R.dimen.width_status_cook_timer_min_label_align_right).toInt()
        val widthStatusCookTimerSecondsLabelAlignRight = resources
            .getDimension(R.dimen.width_status_cook_timer_seconds_label_align_right).toInt()

        val valueStyle = R.style.HeaderBarNumpadKeypadTextViewStyle
        val unitStyle: Int = R.style.NumPadCookTimeUnitStyle
        val spannableTimeString = SpannableString(appendUnitsInCookTimerString())
        spannableTimeString.setSpan(
            TextAppearanceSpan(context, valueStyle), 0,
            2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val width =
            if (spannableTimeString.length == 2) widthStatusCookTimer / 2 else widthStatusCookTimer
        spannableTimeString.setSpan(
            TextWidthSpan(width, TextWidthSpan.RIGHT),
            0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableTimeString.setSpan(
            TextAppearanceSpan(context, unitStyle), 2,
            3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //Added check is Kitcher time runing or not. If yes then we have to treat as a oven numpad HH:MM or MM:SS
        if (isOfTypeMicrowaveOven && isMagnetronBasedRecipe()) {
            spannableTimeString.setSpan(
                TextWidthSpan(widthStatusCookTimerMinLabelAlignRight, TextWidthSpan.CENTER),
                2, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        } else {
            spannableTimeString.setSpan(
                TextWidthSpan(widthStatusCookTimerHmsLabel, TextWidthSpan.CENTER),
                2, 3, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

        spannableTimeString.setSpan(
            TextAppearanceSpan(context, valueStyle), 3,
            5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableTimeString.setSpan(
            TextWidthSpan(width, TextWidthSpan.RIGHT),
            3, 5, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannableTimeString.setSpan(
            TextAppearanceSpan(context, unitStyle), 5,
            6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //Added check is Kitcher time runing or not. If yes then we have to treat as a oven numpad HH:MM or MM:SS
        if (isOfTypeMicrowaveOven && isMagnetronBasedRecipe()) {
            spannableTimeString.setSpan(
                TextWidthSpan(widthStatusCookTimerSecondsLabelAlignRight, TextWidthSpan.CENTER),
                5, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        } else {
            spannableTimeString.setSpan(
                TextWidthSpan(widthStatusCookTimerHmsLabelAlignRight, TextWidthSpan.CENTER),
                5, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

        if ((appendUnitsInCookTimerString()?.length ?: 0) > 6) {
            spannableTimeString.setSpan(
                TextAppearanceSpan(context, valueStyle), 6,
                8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableTimeString.setSpan(
                TextWidthSpan(width, TextWidthSpan.RIGHT),
                6, 8, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spannableTimeString.setSpan(
                TextAppearanceSpan(context, unitStyle), 8,
                9, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spannableTimeString.setSpan(
                TextWidthSpan(widthStatusCookTimerHmsLabelAlignRight, TextWidthSpan.CENTER),
                8, 9, Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
        cookTimeNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()?.getTextViewCookTime()
            ?.setText(
                spannableTimeString, TextView.BufferType.SPANNABLE
            )
    }

    /**
     * Method to append the hours/ minutes/ seconds in the cook timer string
     *
     * @return units appended cook timer string
     */
    protected open fun appendUnitsInCookTimerString(): String? {
        return CookingAppUtils.appendUnitsInCookTimerHourAndMinutesString(
            cookTimeText, requireContext(),
            isOfTypeMicrowaveOven && isMagnetronBasedRecipe()
        ).lowercase()
    }

    /**
     * Method to validate whether the cook timer is in range
     *
     * @return true if it is a valid cook time
     */
    protected open fun isValidCookTime(): Boolean {
            //if its microwave not convect m and sec false else true convect and normal cycle H and M
            val cookTimeSec: Int = getCookTimerStringAsSeconds(
                cookTimeText,
                !isOfTypeMicrowaveOven || !isMagnetronBasedRecipe()
            )
            val cookTimeOptionRange = provideIntegerRangeAsPerRecipe()
            return if (cookTimeOptionRange != null) {
                cookTimeSec >= cookTimeOptionRange.min && cookTimeSec <= cookTimeOptionRange.max
            } else {
                false
            }
    }


    override fun getKeyboardView(): KeyboardView? =
        cookTimeNumberPadViewHolderHelper?.getKeyboardView()

    private fun provideDefaultCookTime(): Int {
        if (recipeViewModel?.cookTime?.value == 0L) {
            val cookTimeOptionRange = provideIntegerRangeAsPerRecipe()
            if (cookTimeOptionRange != null) {
                return cookTimeOptionRange.defaultValue
            }
        }
        return recipeViewModel?.cookTime?.value?.toInt() ?: 0
    }

    private var onClickListener: ButtonClickListenerInterface? = null

    /**
     * Set the button interface to receive callbacks in the fragment.
     */
    fun setButtonInteractionListener(onClickListener: ButtonClickListenerInterface?) {
        this.onClickListener = onClickListener
    }

    interface ButtonClickListenerInterface {
        fun onRightButtonClick()
        fun onLeftButtonClick()
        fun onMiddleButtonClick()
        fun onLeftPowerButtonClick()
    }


    /**
     * Method use for navigate to cook time numpad screen
     * send bundle for sending selected hour and min
     */
    open fun navigateToVerticalTimeTumblerScreen() {
        if (cookTimeText != AppConstants.EMPTY_STRING) {
            val cookTimeSec = getCookTimerStringAsSeconds(
                cookTimeText,
                !isOfTypeMicrowaveOven || !isMagnetronBasedRecipe()
            )
            val bundle = Bundle()
            bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, "$cookTimeSec")
            if(arguments?.getString(BundleKeys.BUNDLE_IS_FROM_PREVIEW_SCREEN)
                    ?.contentEquals(BundleKeys.BUNDLE_VALUE_POP_TO_PREVIEW) == true){
                bundle.putString(BundleKeys.BUNDLE_IS_FROM_PREVIEW_SCREEN,BundleKeys.BUNDLE_VALUE_POP_TO_PREVIEW)
            }
            navigateSafely(
                this,
                R.id.action_cookTimeFragment_to_verticalTumblerFragment,
                bundle,
                null
            )
        }
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        cookTimeNumberPadViewHolderHelper?.onDestroyView()
        cookTimeNumberPadViewHolderHelper = null
        keyboardViewModel = null
        cookingViewModel = null
        recipeViewModel = null
        inScopeViewModel = null
        super.onDestroyView()
    }

    /**
     * set selected cooktime and move to power level selection
     * useful when clicking on power level option
     */
    fun setCookTimeAndNavigateToPowerLevel() {
        if (setCookTime(
                getCookTimerStringAsSeconds(
                    cookTimeText,
                    !isOfTypeMicrowaveOven || !isMagnetronBasedRecipe()
                ).toLong()
            )
        ) {
            if(CookingAppUtils.isRecipeReheatInProgramming(cookingViewModel)) {
                PopUpBuilderUtils.showReheatMoreOptionPopup(this, cookingViewModel)
                return
            }
            navigateSafely(this, R.id.action_to_manualMode_mwoPowerTumblerFragment, null, null)
        }
    }

    companion object {

        /**
         * Method to handle the right text button click in the oven cooking
         *
         * @param cookTimeSec cook timer in seconds
         */
        fun handleCommonCookTimeRightTextButtonClick(
            cookingViewModel: CookingViewModel?,
            fragment: Fragment,
            cookTimeSec: Int
        ) {
            /* Scenario : If user is in cookTime numberPad screen and if the cooking completes,
              cookTime complete popup will be shown and user selects see later in the popup,now from
              the cook  time numberPad user can edit the time and start the cooking again. for that
              addCookTime should be used instead of setCookTime.*/
            val recipeViewModel = cookingViewModel?.recipeExecutionViewModel
            val recipeCookingState = recipeViewModel?.recipeCookingState?.value
            if (recipeCookingState == RecipeCookingState.KEEPING_WARM
                || recipeCookingState == RecipeCookingState.TURNING_OFF
                || recipeCookingState == RecipeCookingState.STAYING_ON
            ) {
                if (recipeViewModel.cookTimerState?.value == Timer.State.RUNNING) {
                    //Set Cook Timer
                    if (recipeViewModel.setCookTime(cookTimeSec.toLong()).isError) return
                } else {
                    //add Cook Timer
                    if (recipeViewModel.addCookTime(cookTimeSec.toLong()) == RecipeErrorResponse.NO_ERROR) {
                        HMILogHelper.Logi("addCookTime$cookTimeSec success")
                    } else {
                        HMILogHelper.Loge("addCookTime$cookTimeSec failed")
                        return
                    }
                }
            } else {
                if (recipeViewModel?.cookTimerState?.value == Timer.State.IDLE) {
                    /* This case would come if user remove timer. Check idle state if any probe extended recipe running. If yes then add or set cook time. */
                    if (CookingAppUtils.isProbeBasedRecipeAndTemperatureReached(recipeViewModel) || AbstractStatusFragment.isExtendedCookingForNonEditableCookTimeRecipe(cookingViewModel)) {
                        if (addCookTimeForProbe(recipeViewModel, cookTimeSec)) {
                            CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                            return
                        }
                    }

                    //Set Cook Timer
                    if (recipeViewModel.setCookTime(cookTimeSec.toLong()).isError) return
                    if (cookingViewModel.let {
                            CookingAppUtils.isCookTimeOptionMandatory(
                                it
                            )
                        } && (recipeCookingState != RecipeCookingState.COOKING && recipeCookingState != RecipeCookingState.PREHEATING)) {
                        if (NavigationUtils.getNavigationIdForNextOption(
                                cookingViewModel,
                                RecipeOptions.COOK_TIME
                            ) == 0
                        ) {
                            //for assisted recipes always got to preview screen
                            if (CookingAppUtils.isRecipeAssisted(
                                    cookingViewModel.recipeExecutionViewModel.recipeName.value,
                                    cookingViewModel.cavityName.value
                                )
                            ) {
                                navigateSafely(
                                    fragment, R.id.action_to_assisted_preview,
                                    null, null
                                )
                                return
                            }
                            if (cookingViewModel.isOfTypeMicrowaveOven) {
                                    DoorEventUtils.startMicrowaveRecipeOrShowPopup(
                                        fragment,
                                        cookingViewModel
                                    )
                                return
                            }
                            val recipeErrorResponse = recipeViewModel.execute()
                            if (recipeErrorResponse == RecipeErrorResponse.NO_ERROR) {
                                HMIExpansionUtils.startOrStopKnobLEDFadeInLightAnimation(true)
                                HMILogHelper.Logi("startCookTimer$cookTimeSec success")
                            } else {
                                HMILogHelper.Loge("startCookTimer$cookTimeSec failed")
                                if (recipeErrorResponse != null) {
                                    cookingViewModel.let {
                                        CookingAppUtils.handleCookingError(
                                            fragment, it, recipeErrorResponse, false
                                        )
                                    }

                                }
                                return
                            }
                        } else {
                            NavigationUtils.navigateAndSetCookTime(
                                fragment,
                                cookingViewModel,
                                cookTimeSec.toLong()
                            )
                            return
                        }

                    } else {
                        //Set Cook Timer
                        if (java.lang.Boolean.FALSE == cookingViewModel.doorState?.value) {
                            if (recipeViewModel.startCookTimer() == RecipeErrorResponse.NO_ERROR) {
                                HMILogHelper.Logi("startCookTimer$cookTimeSec success")
                            } else {
                                HMILogHelper.Loge("startCookTimer$cookTimeSec failed")
                                return
                            }
                        }
                    }
                } else {
                    HMILogHelper.Logi("Check weather it is probe extended cycle or not = ${(recipeViewModel?.isProbeBasedRecipe == true && recipeViewModel.targetMeatProbeTemperatureReached.value == true )}")
                    //Check weather it is probe extended cycle or not
                    if (CookingAppUtils.isProbeBasedRecipeAndTemperatureReached(recipeViewModel) || AbstractStatusFragment.isExtendedCookingForNonEditableCookTimeRecipe(cookingViewModel)) {
                        if (recipeViewModel?.let { addCookTimeForProbe(it, cookTimeSec) } == true) {
                            CookingAppUtils.navigateToStatusOrClockScreen(fragment)
                            return
                        }
                    } else {
                        //Set Cook Timer
                        if (recipeViewModel?.setCookTime(cookTimeSec.toLong())?.isError == true) return
                    }
                }
            }
            //Navigate to clock screen
            CookingAppUtils.navigateToStatusOrClockScreen(fragment)
        }

        /**
         * add cook time for probe extended cycle
         */
        private fun addCookTimeForProbe(
            recipeViewModel: RecipeExecutionViewModel,
            cookTimeSec: Int
        ): Boolean {
            if (recipeViewModel.cookTimerState?.value == Timer.State.RUNNING) {
                /* If any probe extended cycle is runing then Changing cook time scenario in probe, cancels the current cook timer and update new one */
                if (recipeViewModel.cancelCookTimer() == RecipeErrorResponse.NO_ERROR) {
                    HMILogHelper.Logi("CancelCookTime$cookTimeSec success")
                } else {
                    HMILogHelper.Loge("CancelCookTime$cookTimeSec failed")
                }
            }

            //add Cook Timer for probe extended cycle
            if (recipeViewModel.addCookTime(cookTimeSec.toLong()) == RecipeErrorResponse.NO_ERROR) {
                HMILogHelper.Logi("addCookTime$cookTimeSec success")
                return true
            } else {
                HMILogHelper.Loge("addCookTime$cookTimeSec failed")

            }
            return false
        }

        /**
         * APIs related to Microwave Power Level
         * Method to update the default power level
         */
        fun updateDefaultPowerLevel(cookingViewModel: CookingViewModel?): Int {
            var selectedPowerLevel = 100
            val mwoPowerLevel =
                cookingViewModel?.recipeExecutionViewModel?.mwoPowerLevel?.value
            //If the cycle is not yet started, get the default value from the recipe
            if (cookingViewModel?.recipeExecutionViewModel?.recipeCookingState?.value == RecipeCookingState.IDLE && mwoPowerLevel == 0) {
                val powerLevelOptions =
                    cookingViewModel.recipeExecutionViewModel?.mwoPowerLevelOptions?.value
                if (Objects.nonNull(powerLevelOptions)) {
                    var defaultString: String? = AppConstants.EMPTY_STRING
                    if (powerLevelOptions is IntegerList) {
                        defaultString = powerLevelOptions.defaultString
                    } else if (powerLevelOptions is IntegerRange) {
                        defaultString = powerLevelOptions.defaultString
                        if (defaultString == "0") {
                            //The API returns a value with a decimal point i.e:20.0,
                            // convert it to a floating-point number and then cast it to an integer.
                            defaultString =
                                cookingViewModel.recipeExecutionViewModel?.nonEditableOptions?.value!![RecipeOptions.MWO_POWER_LEVEL]
                        }
                    }
                    HMILogHelper.Logi("SDK getMwoPowerLevelOptions() :$defaultString")
                    //If the mwo recipe does not contain power level defaultString will be null
                    if (defaultString != AppConstants.EMPTY_STRING && defaultString != null) {
                        val floatValue = defaultString.toDouble()
                        selectedPowerLevel = floatValue.toInt()
                        val recipeErrorResponse =
                            cookingViewModel.recipeExecutionViewModel.setMwoPowerLevel(
                                selectedPowerLevel
                            )
                        HMILogHelper.Logd("Setting default power level: ${recipeErrorResponse.name} || ${recipeErrorResponse.description}")
                    }
                } else {
                    HMILogHelper.Loge("getMwoPowerLevelOptions() returns null")
                }
            } else {
                //If the cycle is running, get the power level from the running cycle
                if (Objects.nonNull(mwoPowerLevel)) {
                    selectedPowerLevel = mwoPowerLevel!!
                } else {
                    selectedPowerLevel = -1
                    HMILogHelper.Loge("getMwoPowerLevel() returns null")
                }
            }
            HMILogHelper.Logi("Selected Default Power Level :$selectedPowerLevel")
            return selectedPowerLevel
        }
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILeftKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMIRightKnobClick() {
        // Do nothing here override in child class if necessary
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if(knobId == AppConstants.RIGHT_KNOB_ID) {
           manageKnobRotation()
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {

    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    abstract fun provideIntegerRange(): IntegerRange?

    fun manageKnobRotation(){
        KnobNavigationUtils.knobForwardTrace = true
        navigateToVerticalTimeTumblerScreen()
    }

    private fun provideIntegerRangeAsPerRecipe():IntegerRange? {
        val isProbeBasedRecipeAndTemperatureReached = CookingAppUtils.isProbeBasedRecipeAndTemperatureReached(recipeViewModel)
        if (isProbeBasedRecipeAndTemperatureReached) {
            return CookingAppUtils.provideProbeIntegerRange()
        }
        return provideIntegerRange()
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
    private fun configutionHmiKey(){
        HMILogHelper.Logd("HMI_KEY","AbstractCookTimeNumberPadFragment Cycle Running--${CookingAppUtils.isAnyCavityRunningRecipeOrDelayedState()}")
        if(CookingAppUtils.isAnyCavityRunningRecipeOrDelayedState()){
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        }
    }
}