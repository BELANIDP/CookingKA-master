/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.SuperscriptSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.Keyboard
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CavityStateUtils
import core.utils.CommonAnimationUtils.justAnimate
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.NumPadHelperTextColor
import core.utils.TimeUtils
import core.viewHolderHelpers.TemperatureNumberPadViewHolderHelper
import java.util.Locale

/**
 * File        : core.jbase.AbstractTemperatureNumberPadFragment.
 * Brief       : Temperature NumberPad Abstract class
 * Author      : GHARDNS/Nikki
 * Created On  : 04-04-2024
 */
abstract class AbstractTemperatureNumberPadFragment : SuperAbstractTimeoutEnableFragment(),
    View.OnClickListener,
    KeyboardInputManagerInterface, HMIKnobInteractionListener {


    /** To binding Fragment variables */
    protected var temperatureNumberPadViewHolderHelper: TemperatureNumberPadViewHolderHelper? = null


    /** ViewModel instances */
    private var keyboardViewModel: KeyboardViewModel? = null
    protected lateinit var cookingViewModel: CookingViewModel
    private lateinit var recipeViewModel: RecipeExecutionViewModel
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null

    /** Boolean variables */
    private var isOfTypeMicrowaveOven = false
    private var isScreenFreshlyLoaded = true
    private var isBackSpaceClicked = false

    /** Int variables */
    private var digitInputIndex = 0


    /** char */
    protected var numericValue: CharSequence? = AppConstants.DEFAULT_TRIPLE_ZERO
    private var selectedTemp = AppConstants.DEFAULT_SELECTED_TEMP

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        temperatureNumberPadViewHolderHelper = TemperatureNumberPadViewHolderHelper()
        temperatureNumberPadViewHolderHelper?.onCreateView(inflater, container, savedInstanceState)
        temperatureNumberPadViewHolderHelper?.getBinding()?.lifecycleOwner = this
        temperatureNumberPadViewHolderHelper?.getBinding()?.numberPadFragment = this
        return temperatureNumberPadViewHolderHelper?.getBinding()?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        configutionHmiKey()
        temperatureNumberPadViewHolderHelper?.getKeyboardView()?.startAnimation(
            android.view.animation.AnimationUtils.loadAnimation(
                requireContext(),
                R.anim.zoom_in
            )
        )
        setUpViewModels()
        observeLiveData()
        updateSelectedTemperatureForCycleTemperature()
        observeTemperatureLiveData()
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
        recipeViewModel = cookingViewModel.recipeExecutionViewModel
        isOfTypeMicrowaveOven = cookingViewModel.isOfTypeMicrowaveOven == true
        temperatureNumberPadViewHolderHelper?.getBinding()?.cookingViewModel = cookingViewModel
    }

    private fun disableTap(disableKeyTapped: Boolean) {
        if (java.lang.Boolean.TRUE == disableKeyTapped) {
            showTemperatureHelper()
        }
    }

    /**
     * Show Temperature helper text with error color.
     * If need to change from default behavior child class can override
     */
    protected open fun showTemperatureHelper() {
        val range = getTemperatureRange().value as IntegerRange
        temperatureNumberPadViewHolderHelper?.apply {
            getErrorHelperTextView()?.apply {
                text = context.getString(
                    R.string.text_temperature_number_pad_error_temp,
                    range.min.toString(),
                    range.max.toString()
                )
                visibility = View.VISIBLE
                CookingAppUtils.setHelperTextColor(this, NumPadHelperTextColor.ERROR_TEXT_COLOR)
                justAnimate(this, R.anim.anim_numpad_errorhelper_translate_x)
            }
        }
    }

    protected open fun observeTemperatureLiveData() {
        getTemperatureRange().observe(viewLifecycleOwner, Observer<Any> { option: Any? ->
            if (null == option) {
                HMILogHelper.Logd("getTargetTemperatureOptions returns null")
                return@Observer
            }
            HMILogHelper.Logd("getTargetTemperatureOptions returns temperature range options")
            if (option is IntegerRange) {
                if (!(option.min.toDouble() == 0.0 && option.max.toDouble() == 0.0)
                ) {
                    HMILogHelper.Logi("setTemperatureNumberPad on getTargetTemperatureOptions update")
                    setTemperatureNumberPad(option)
                }
            }
        })
    }

    /**
     * Method to set the numeric value in the input entry field
     */
    private fun setTemperatureNumberPad(
        targetTemperatureOptions: IntegerRange
    ) {
        numericValue = String.format(
            Locale.getDefault(),
            "%d",
            if (selectedTemp != AppConstants.DEFAULT_SELECTED_TEMP) selectedTemp else targetTemperatureOptions.defaultValue
        )
        if (selectedTemp == AppConstants.DEFAULT_SELECTED_TEMP) digitInputIndex = targetTemperatureOptions.defaultValue.toString().length
        setNumericTemperatureValue(numericValue)
        disableNumPadItems(
            0,
            numericValue.toString(),
            Character.getNumericValue((numericValue as String)[(numericValue as String).length - 1])
        )
        populateAndValidateRightButton()
    }

    private fun populateAndValidateRightButton() {
        if (isValidTemperature()) {
            HMILogHelper.Logd(tag, "valid temperature")
            //Added here because onViewCreate Initialization is happening before the value gets updated from bundle
            temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = true
            temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = true
            if (CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.isRunning){
                // This was added later to check if the value is updated, applies only for editing while running.
                if (numericValue.contentEquals(selectedTemp.toString())) {
                    temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = false
                    temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = false
                } else {
                    temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = true
                    temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = true
                }
            }else{
                temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled = true
            }
        }else{
            HMILogHelper.Logd(tag, "NOT a valid temperature")
            temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = false
            temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.isEnabled = false
        }
    }

    /**
     * manage child views
     */
    private fun manageChildViews() {
        manageHeaderBar()
        manageLeftButton()
        manageRightButton()
        manageMiddleButton()
    }


    /**
     * Observe live data for keyboard text entry
     */
    private fun observeLiveData() {
        keyboardViewModel?.onTextEntry()?.observe(
            viewLifecycleOwner
        ) { text: CharSequence? ->
            if (text != null) {
                this.numpadInput(
                    text
                )
            }
        }
    }


    private fun numpadInput(text: CharSequence) {
        if (isScreenFreshlyLoaded && !isBackSpaceClicked) {
            isScreenFreshlyLoaded = false
            isBackSpaceClicked = false
            numericValue = AppConstants.DEFAULT_TRIPLE_ZERO
            digitInputIndex = 0
        }
        if (digitInputIndex > 2) {
            digitInputIndex = 0
            numericValue = AppConstants.DEFAULT_TRIPLE_ZERO
            disableNumPadItems(
                digitInputIndex, numericValue.toString(),
                Character.getNumericValue(text[text.length - 1])
            )
        }
        digitInputIndex++

        //Clear the old values with "" and append the last typed character to the end.
        numericValue =
            AppConstants.EMPTY_STRING + numericValue?.get(1) + numericValue?.get(2) + text[text.length - 1]
        setNumericTemperatureValue(numericValue)
        disableNumPadItems(
            if (digitInputIndex > 2) 0 else digitInputIndex, numericValue.toString(),
            Character.getNumericValue(text[text.length - 1])
        )
        if (isValidTemperature()) {
            temperatureNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.GONE
            CookingAppUtils.setHelperTextColor(
                temperatureNumberPadViewHolderHelper?.getErrorHelperTextView(),
                NumPadHelperTextColor.ERROR_TEXT_COLOR
            )
            //As per behavior spec, during Update temperature this button can be disable so enable it back (for all use-cases)
            temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = true
            temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isClickable = true
        }
        populateAndValidateRightButton()
    }

    /**
     * Method to set selected temperature value on numPad screen
     *
     * @param tempValue temperature value
     */
    private fun setNumericTemperatureValue(tempValue: CharSequence?) {
        //On deleting the third temperature digit, the text is again set to the default value and not 000
        var value = tempValue
        if (value == AppConstants.DEFAULT_TRIPLE_ZERO || value == selectedTemp.toString()) {
            value = if (selectedTemp == -1) {
                val targetTemperature =
                    getTemperatureRange().value
                if (targetTemperature != null) targetTemperature.defaultString else selectedTemp.toString()
            } else {
                selectedTemp.toString()
            }
            numericValue = value
            digitInputIndex = 3
            isScreenFreshlyLoaded = true
        }
        val numericValueSpan = SpannableString(value)
        val unit = if (CookingAppUtils.isFAHRENHEITUnitConfigured()) getString(
            R.string.text_tiles_list_fahrenheit_value
        ) else getString(R.string.text_tiles_list_celsius_value)
        val numericUnit = SpannableString(unit)
        if (value != null) {
            numericValueSpan.setSpan(
                TextAppearanceSpan(
                    temperatureNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                        ?.getTextViewCookTime()?.context,
                    R.style.Style56LightWhiteVCenterHCenter
                ), 0, value.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        numericUnit.setSpan(
            TextAppearanceSpan(
                temperatureNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
                    ?.getTextViewCookTime()?.context,
                R.style.Style36LightWhiteVCenterHLeft
            ), 0, unit.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val formattedText = TextUtils.concat(numericValueSpan, numericUnit)
        val superSpanString = SpannableString(formattedText)
        if (value != null) {
            superSpanString.setSpan(
                SuperscriptSpan(), value.length,
                superSpanString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        temperatureNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.getTextViewCookTime()?.text = superSpanString
    }


    private fun isValidTemperature(): Boolean {
        val temp = (numericValue as String).toInt()
        val range: IntegerRange = getTemperatureRange().value as IntegerRange
        return temp >= range.min && temp <= range.max
    }

    /**
     * @return TargetTemperatureOptionRange of primary cooking view model.
     */
    abstract fun getTemperatureRange(): LiveData<IntegerRange>


    /**
     * set primaryCavityViewModel according to the the selected product variant
     */
    private fun setCavityViewModelByProductVariant() {
        productVariant = CookingViewModelFactory.getProductVariantEnum()
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
        temperatureNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setBackIconOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                //Header left icon click event
                NavigationViewModel.popBackStack(
                    Navigation.findNavController(
                        NavigationUtils.getViewSafely(this) ?: requireView()
                    )
                )
            }
        temperatureNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setCancelIconOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                onBackSpaceIconClick()
            }
        temperatureNumberPadViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setTumblerIconOnClickListener {
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                switchToTumblerScreen()
            }
    }

    /**
     * Method to handle the left buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageLeftButton() {
        if (CookingAppUtils.isRecipeOptionAvailable(recipeViewModel, RecipeOptions.DELAY_TIME) &&
                (cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.DELAYED ||
                cookingViewModel.recipeExecutionViewModel.recipeExecutionState.value == RecipeExecutionState.IDLE)) {
            temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.VISIBLE
            temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.setOnClickListener(this)
            temperatureNumberPadViewHolderHelper?.getLeftConstraint()?.setOnClickListener(this)
            temperatureNumberPadViewHolderHelper?.getLeftTextButton()
                ?.setTextButtonText(R.string.text_button_delay)
        } else {
            temperatureNumberPadViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
        }
    }

    /**
     * Method to handle the Right buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageRightButton() {
        temperatureNumberPadViewHolderHelper?.getRightTextButton()?.setOnClickListener(this)
        temperatureNumberPadViewHolderHelper?.getRightConstraint()?.setOnClickListener(this)
        temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isEnabled = isValidTemperature()
        temperatureNumberPadViewHolderHelper?.getRightTextButton()?.isClickable =
            isValidTemperature()
        temperatureNumberPadViewHolderHelper?.getRightTextButton()
            ?.setTextButtonText( provideRightButtonText() )
    }

    /**
     * provide text to be shown for right button
     */
    protected open fun provideRightButtonText(): String {
        return NavigationUtils.getRightButtonTextForRecipeOption(
            context,
            cookingViewModel,
            RecipeOptions.TARGET_TEMPERATURE
        )
    }

    /**
     * Method to handle the middle buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageMiddleButton() {
        temperatureNumberPadViewHolderHelper?.getMiddleTextButton()?.setOnClickListener(this)
        temperatureNumberPadViewHolderHelper?.getMiddleTextButton()?.visibility = View.GONE

    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cook_time_text_button_left -> {
                onClickListener?.onLeftButtonClick()
            }
            R.id.constraintNumberPadLeft -> {
                onClickListener?.onLeftButtonClick()
            }

            R.id.cook_time_text_button_right -> {
                val buttonText = temperatureNumberPadViewHolderHelper?.getRightTextButton()?.getTextButtonText()
                AudioManagerUtils.playOneShotSound(
                    view.context,
                    if(buttonText.equals(getString(R.string.text_button_next))){
                        R.raw.button_press
                    }else{
                        R.raw.start_press
                    },
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                NavigationUtils.navigateAndSetTemperature(
                    this,
                    CookingViewModelFactory.getInScopeViewModel(),
                    numericValue.toString().toFloat()
                )
            }

            R.id.constraintNumberPadRight -> {
                val buttonText = temperatureNumberPadViewHolderHelper?.getRightTextButton()?.getTextButtonText()
                AudioManagerUtils.playOneShotSound(
                    view.context,
                    if(buttonText.equals(getString(R.string.text_button_next))){
                        R.raw.button_press
                    }else{
                        R.raw.start_press
                    },
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                NavigationUtils.navigateAndSetTemperature(
                    this,
                    CookingViewModelFactory.getInScopeViewModel(),
                    numericValue.toString().toFloat()
                )
            }

            R.id.cook_time_text_button_middle -> {
                onClickListener?.onMiddleButtonClick()
            }
        }
    }


    /**
     * Method to handle the Back space Icon click based on enabled fields. When backspace is
     * pressed, first char will be deleted and other char will be shifted to right by one position
     *
     */
    protected open fun onBackSpaceIconClick() {
        isBackSpaceClicked = true
        if (digitInputIndex < 0) {
            digitInputIndex = 2
        }
        digitInputIndex--
        numericValue = AppConstants.DEFAULT_LEVEL + numericValue!![0] + numericValue!![1]
        setNumericTemperatureValue(numericValue)
        disableNumPadItems(
            if (digitInputIndex > 2) 0 else digitInputIndex, numericValue.toString(),
            Character.getNumericValue((numericValue as String)[(numericValue as String).length - 1])
        )
        if (isValidTemperature()) {
            temperatureNumberPadViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.GONE
            CookingAppUtils.setHelperTextColor(
                temperatureNumberPadViewHolderHelper?.getErrorHelperTextView(),
                NumPadHelperTextColor.ERROR_TEXT_COLOR
            )
        }
        populateAndValidateRightButton()
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
        currentIndexValue: Int
    ) {
        val temperatureRange = getTemperatureRange().value
        if (temperatureRange != null) {
            temperatureNumberPadViewHolderHelper?.getKeyboardView()?.disableKeyWithKeyLabels(
                currentValue?.let {
                    TimeUtils.disableKeypadItemsForTemp(
                        index,
                        it,
                        currentIndexValue,
                        temperatureRange.min.toString(),
                        temperatureRange.max.toString()
                    )
                }
            )
        } else {
            HMILogHelper.Loge("getTargetCookTimeOptions returns null" + "")
        }
    }


    override fun getKeyboardView(): KeyboardView? =
        temperatureNumberPadViewHolderHelper?.getKeyboardView()


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
    }


    /**
     * Method use for navigate to temperature numpad screen
     * send bundle for sending selected hour and min
     */
    protected open fun switchToTumblerScreen() {
        val bundle = Bundle()
        bundle.putInt(
            BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE,
            (numericValue as String).toInt()
        )
        NavigationUtils.navigateSafely(
            this,
            R.id.action_temperatureKeyboardSelectionFragment_to_manualModeTemperatureTumblerFragment,
            bundle,
            null
        )
    }

    /**
     * update selected temperature for cycle
     */
    protected open fun updateSelectedTemperatureForCycleTemperature() {
        val bundle = arguments
        var bundleTemperature: Int = AppConstants.DEFAULT_SELECTED_TEMP
        if (bundle != null) {
            bundleTemperature =
                requireArguments().getInt(BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE)
        }
        if (bundleTemperature == AppConstants.DEFAULT_SELECTED_TEMP) {
            /*Show current cycle temperature as the default selected temperature when changing
            temperature while cycle running*/
            val targetTemperature = provideCurrentTargetTemperature()

            if (targetTemperature != null && targetTemperature > 0) {
                selectedTemp = targetTemperature
                HMILogHelper.Logi("updateSelectedTumbler: cycle temperature from view model$selectedTemp")
            }
        } else {
            /*Arguments to show the temperature entered in the Number pad while switching from
             number pad to tumbler . The value entered in the number pad input screen is an
             intermediate value of HMI which will not known to SDK until HMI sets it while updating
             the cycle.*/
            selectedTemp = bundleTemperature
            HMILogHelper.Logi("updateSelectedTumbler: selectedTemp from Arguments$selectedTemp")
        }
    }

    abstract fun provideCurrentTargetTemperature(): Int?

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
        //Do nothing here override in child class if necessary
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        switchToTumblerScreen()
    }

    override fun onKnobSelectionTimeout(knobId: Int) {

    }

    override fun onDestroyView() {
        temperatureNumberPadViewHolderHelper?.onDestroyView()
        temperatureNumberPadViewHolderHelper = null
        keyboardViewModel = null
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        super.onDestroyView()
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
    private fun configutionHmiKey(){
        HMILogHelper.Logd("HMI_KEY","AbstractTemperatureNumberPadFragment Cycle Running--${CookingAppUtils.isAnyCavityRunningRecipeOrDelayedState()}")
        if(CookingAppUtils.isAnyCavityRunningRecipeOrDelayedState()){
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        }
    }
}