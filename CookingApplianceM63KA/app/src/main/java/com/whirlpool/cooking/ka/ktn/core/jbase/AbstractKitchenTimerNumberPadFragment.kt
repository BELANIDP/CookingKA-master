/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
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
import core.utils.BundleKeys
import core.utils.CavityStateUtils
import core.utils.CommonAnimationUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getCookTimerStringAsSeconds
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.NumPadHelperTextColor
import core.utils.TextWidthSpan
import core.utils.TimeUtils
import core.viewHolderHelpers.KitchenTimerNumberPadViewHolderHelper
import java.util.Objects

/**
 * File        : core.jbase.AbstractCookTimeNumberPadFragment.
 * Brief       : Cook time NumberPad Abstract class
 * Author      : SPANDE18
 * Created On  : 18-03-2024
 */
abstract class AbstractKitchenTimerNumberPadFragment : SuperAbstractTimeoutEnableFragment(),
    View.OnClickListener,
    KeyboardInputManagerInterface, HMIKnobInteractionListener {


    /** To binding Fragment variables */
    var kitchenTimerNumberPadViewHolderHelper: KitchenTimerNumberPadViewHolderHelper? = null

    /** ViewModel instances */
    private var keyboardViewModel: KeyboardViewModel? = null
    protected var cookingViewModel: CookingViewModel? = null
    protected var isKitchenTimerModify: Boolean = false
    var recipeViewModel: RecipeExecutionViewModel? = null
    private var inScopeViewModel: CookingViewModel? = null
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null

    /** Boolean variables */
    protected var isOfTypeMicrowaveOven = false
    private var isScreenFreshlyLoaded = true
    private var isComingFromKitchenTimerFlow:Boolean = true

    /** Int variables */
    private var digitInputIndex = 0

    /** String variables */
    protected var cookTimeText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        kitchenTimerNumberPadViewHolderHelper = KitchenTimerNumberPadViewHolderHelper()
        kitchenTimerNumberPadViewHolderHelper?.onCreateView(inflater, container, savedInstanceState)
        kitchenTimerNumberPadViewHolderHelper?.getBinding()?.lifecycleOwner = this
        kitchenTimerNumberPadViewHolderHelper?.getBinding()?.numberPadFragment = this
        return kitchenTimerNumberPadViewHolderHelper?.getBinding()?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kitchenTimerNumberPadViewHolderHelper?.getKeyboardView()?.startAnimation(
            android.view.animation.AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.zoom_in
            )
        )
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
        kitchenTimerNumberPadViewHolderHelper?.getBinding()?.cookingViewModel = cookingViewModel
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
        kitchenTimerNumberPadViewHolderHelper?.getBinding()?.textViewHelperText?.visibility =
            View.INVISIBLE
        val cookTimeOptionRange = provideIntegerRange()
        if (cookTimeOptionRange != null) {

            if (isOfTypeMicrowaveOven && isNotRecipeConvect()) {
                val minutes = cookTimeOptionRange.max / 60
                val seconds = cookTimeOptionRange.max % 60
                kitchenTimerNumberPadViewHolderHelper?.getBinding()?.textViewHelperText?.text =
                    String.format(
                        CookingAppUtils.getStringFromResourceId(
                            requireContext(),
                            provideHelperTextString()
                        ),
                        minutes.toString() + AppConstants.EMPTY_SPACE + getString(R.string.text_label_m) + AppConstants.EMPTY_SPACE + seconds.toString() + AppConstants.EMPTY_SPACE + getString(R.string.text_label_s)
                    )
            } else {
                val hours = cookTimeOptionRange.max / 3600
                kitchenTimerNumberPadViewHolderHelper?.getBinding()?.textViewHelperText?.text =
                    String.format(
                        CookingAppUtils.getStringFromResourceId(
                            requireContext(),
                            provideHelperTextString()
                        ),
                        hours.toString() + AppConstants.EMPTY_SPACE + getString(R.string.text_label_h)
                    )
            }
            kitchenTimerNumberPadViewHolderHelper?.getBinding()?.textViewHelperText?.visibility =
                View.VISIBLE

            CommonAnimationUtils.setErrorHelperAnimation(
                context,
                kitchenTimerNumberPadViewHolderHelper?.getBinding()?.textViewHelperText
            )
            CookingAppUtils.setHelperTextColor(
                kitchenTimerNumberPadViewHolderHelper?.getBinding()?.textViewHelperText,
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
     * Method to get the recipe is not convect
     *
     * @return boolean
     */
    private fun isNotRecipeConvect(): Boolean {
        return cookingViewModel?.recipeExecutionViewModel?.recipeName?.value?.contains(
            AppConstants.RECIPE_CONVECT
        ) == false
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
        observeCookTimerLiveData()
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
                    kitchenTimerNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
                }
            }
    }

    /**
     * Method to update the Input Text Field on text entry
     *
     * @param text text entered
     */
    private fun onTextEntry(text: CharSequence?) {
        if (isOfTypeMicrowaveOven && isNotRecipeConvect()) {
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
            kitchenTimerNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility = View.INVISIBLE
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
        kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()?.setBackIconOnClickListener {
            //Header left icon click event
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    getViewSafely(this) ?: requireView()
                )
            )
        }
        kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setCancelIconOnClickListener { onBackSpaceIconClick(isOfTypeMicrowaveOven) }
        kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setTumblerIconOnClickListener { navigateToVerticalTimeTumblerScreen(isKitchenTimerModify) }
    }

    /**
     * Method to handle the left buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    private fun manageLeftButton() {
        kitchenTimerNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
    }

    /**
     * Method to handle the Right buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageRightButton() {
        kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.setOnClickListener(this)
        if (cookTimeText == null) {
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = false
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = false
        } else {
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = isValidCookTime()
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = isValidCookTime()
        }
        kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()
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
        kitchenTimerNumberPadViewHolderHelper?.getMiddleTextButton()?.visibility = View.GONE
    }

    /**
     * Method to handle the left power buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageLeftPowerButton() {
        kitchenTimerNumberPadViewHolderHelper?.getLeftPowerTextButton()?.visibility = View.GONE
    }


    /**
     * Method to set the initial time text
     *
     * @param defaultValue time text
     */
    protected open fun manageInputTextField(defaultValue: Int) {
        kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
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
            var time = recipeViewModel?.remainingCookTime?.value
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
        return if (isOfTypeMicrowaveOven && isNotRecipeConvect()) {
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
        kitchenTimerNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled = false
        kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = false
        kitchenTimerNumberPadViewHolderHelper?.getMiddleTextButton()?.isEnabled = false
        kitchenTimerNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isEnabled = false

        kitchenTimerNumberPadViewHolderHelper?.getLeftTextButton()?.isClickable = false
        kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = false
        kitchenTimerNumberPadViewHolderHelper?.getMiddleTextButton()?.isClickable = false
        kitchenTimerNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isClickable = false
    }

    protected open fun enableTextButtons() {
        if (cookTimeText != null && cookTimeText != AppConstants.DEFAULT_COOK_TIME) {
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = true
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = true
            if (cookingViewModel?.isOfTypeMicrowaveOven == true && CookingAppUtils.isRequiredTargetAvailable(
                    cookingViewModel,
                    RecipeOptions.MWO_POWER_LEVEL
                )
            ) {
                kitchenTimerNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isEnabled = true
                kitchenTimerNumberPadViewHolderHelper?.getLeftPowerTextButton()?.isClickable = true
            }
        }

        if (cookingViewModel?.recipeExecutionViewModel?.cookTime?.value != null
            && cookingViewModel?.recipeExecutionViewModel?.cookTime?.value == 0L
            && SettingsViewModel.getSettingsViewModel().sabbathMode.value == SettingsViewModel.SabbathMode.NOT_SABBATH_COMPLIANT
        ) {
            kitchenTimerNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled =
                cookingViewModel?.recipeExecutionViewModel
                    ?.recipeCookingState?.value == RecipeCookingState.PREHEATING
            kitchenTimerNumberPadViewHolderHelper?.getLeftTextButton()?.isClickable =
                cookingViewModel?.recipeExecutionViewModel
                    ?.recipeCookingState?.value == RecipeCookingState.PREHEATING
        } else {
            kitchenTimerNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled = true
            kitchenTimerNumberPadViewHolderHelper?.getLeftTextButton()?.isClickable = true
        }
        kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()?.setCancelIconEnable(true)
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

                } else if (validateCookTime() && setCookTime(
                        getCookTimerStringAsSeconds(
                            cookTimeText,
                            !isOfTypeMicrowaveOven || !isNotRecipeConvect()
                        ).toLong()
                    )
                ) {
                    CookingAppUtils.navigateToStatusOrClockScreen(this)
                }
            }

            R.id.cook_time_text_button_right -> {
                onClickListener?.onRightButtonClick()
            }

            R.id.cook_time_text_button_middle -> {
                onClickListener?.onMiddleButtonClick()
            }

            R.id.cook_time_text_button_left_power -> {
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
            kitchenTimerNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.VISIBLE
            CookingAppUtils.setHelperTextColor(
                kitchenTimerNumberPadViewHolderHelper?.getErrorHelperTextView(),
                NumPadHelperTextColor.ERROR_TEXT_COLOR
            )
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = false
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = false
        } else {
            kitchenTimerNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.INVISIBLE
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = true
            kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = true
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
            kitchenTimerNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.INVISIBLE
        } //0000
    }

    /**
     * Method To fetch the current input in the numPad entry filed
     *
     * @return appended values of all the three fields
     */
    private fun getCurrentInputFieldContent(): String {
        return if ((kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                ?.getTextViewCookTime()?.text?.length ?: 0) >= 6
            || cookingViewModel?.isOfTypeMicrowaveOven == true
        ) {
            kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                ?.getTextViewCookTime()?.text?.toString()
                ?.substring(0, 2) +
                    kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                        ?.getTextViewCookTime()?.text?.toString()
                        ?.substring(3, 5)
        } else {
            kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                ?.getTextViewCookTime()?.text?.toString()
                ?.substring(0, 2) +
                    kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                        ?.getTextViewCookTime()?.text?.toString()
                        ?.substring(3, 5) +
                    kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
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
        val cookTimeOptionRange = provideIntegerRange()
        if (cookTimeOptionRange != null) {
            kitchenTimerNumberPadViewHolderHelper?.getKeyboardView()
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
        if (isOfTypeMicrowaveOven && (!isComingFromKitchenTimerFlow)) {
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
        if (isOfTypeMicrowaveOven && (!isComingFromKitchenTimerFlow)) {
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
        kitchenTimerNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()?.getTextViewCookTime()
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
        //Added check is Kitcher time runing or not. If yes then we have to treat as a oven numpad HH:MM or MM:SS
        return if(isComingFromKitchenTimerFlow){
            CookingAppUtils.appendUnitsInCookTimerHourAndMinutesString(
                cookTimeText, requireContext(),
                false
            ).lowercase()
        } else {
            CookingAppUtils.appendUnitsInCookTimerHourAndMinutesString(
                cookTimeText, requireContext(),
                isOfTypeMicrowaveOven && isNotRecipeConvect()
            ).lowercase()
        }
    }

    /**
     * Method to validate whether the cook timer is in range
     *
     * @return true if it is a valid cook time
     */
    protected open fun isValidCookTime(): Boolean {
            val cookTimeSec: Int = getCookTimerStringAsSeconds(
                cookTimeText,
                true
            )
            val cookTimeOptionRange = provideIntegerRange()
            return if (cookTimeOptionRange != null) {
                cookTimeSec >= cookTimeOptionRange.min && cookTimeSec <= cookTimeOptionRange.max
            } else {
                false
            }
    }


    override fun getKeyboardView(): KeyboardView? =
        kitchenTimerNumberPadViewHolderHelper?.getKeyboardView()

    private fun provideDefaultCookTime(): Int {
        if (recipeViewModel?.cookTime?.value == 0L) {
            val cookTimeOptionRange = provideIntegerRange()
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
     * Method use for navigate to KT numpad
     * send bundle for sending selected hour and min
     */
    fun navigateToVerticalTimeTumblerScreen(isKitchenTimerModify: Boolean) {
        val bundle = Bundle()
        if (isKitchenTimerModify) bundle.putString(
            BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER,
            arguments?.getString(BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER)
        )
        HMILogHelper.Logd(tag, "kitchenTimer numpad to tumbler value $cookTimeText")
        bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, cookTimeText)
        navigateSafely(
            this, R.id.action_setManualKTFragment_to_setKTFragment, bundle, null
        )
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        kitchenTimerNumberPadViewHolderHelper = null
        keyboardViewModel = null
        cookingViewModel = null
        recipeViewModel = null
        inScopeViewModel = null
        super.onDestroyView()
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
        kitchenTimerNumberPadViewHolderHelper?.getRightTextButton()?.callOnClick()
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobSelectionTimeout(knobId: Int) {

    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    abstract fun provideIntegerRange(): IntegerRange?
    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}