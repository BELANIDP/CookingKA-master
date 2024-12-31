/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.jbase

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.Keyboard
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CavityStateUtils
import core.utils.CommonAnimationUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NumPadHelperTextColor
import core.viewHolderHelpers.DemoModeCodeViewHolderHelper
import java.util.Objects

/**
 * File        : core.jbase.AbstractDemoModeCodeFragment.
 * Brief       : Demo Mode code Abstract class
 * Author      : DUGAMAS/Amar Suresh Dugam
 * Created On  : 18-03-2024
 */
abstract class AbstractDemoModeCodeFragment : SuperAbstractTimeoutEnableFragment(),
    View.OnClickListener,
    KeyboardInputManagerInterface, HMIKnobInteractionListener {


    /** To binding Fragment variables */
    var demoModeCodeViewHolderHelper: DemoModeCodeViewHolderHelper? = null

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
    protected var demoCodeText: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        demoModeCodeViewHolderHelper = DemoModeCodeViewHolderHelper()
        demoModeCodeViewHolderHelper?.onCreateView(inflater, container, savedInstanceState)
        demoModeCodeViewHolderHelper?.getBinding()?.lifecycleOwner = this
        demoModeCodeViewHolderHelper?.getBinding()?.demoModeCodeFragment = this
        return demoModeCodeViewHolderHelper?.getBinding()?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        demoModeCodeViewHolderHelper?.getKeyboardView()?.startAnimation(
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
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            demoModeCodeViewHolderHelper?.getRightTextButton()?.setBottomButtonViewVisible(true)
        }
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
        demoModeCodeViewHolderHelper?.getBinding()?.cookingViewModel = cookingViewModel
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
        demoModeCodeViewHolderHelper?.getBinding()?.textViewHelperText?.visibility =
            View.INVISIBLE

        demoModeCodeViewHolderHelper?.getBinding()?.textViewHelperText?.text =
            String.format(
                CookingAppUtils.getStringFromResourceId(
                    requireContext(),
                    provideHelperTextString()
                ),
            )

        demoModeCodeViewHolderHelper?.getBinding()?.textViewHelperText?.visibility =
            View.VISIBLE

        CommonAnimationUtils.setErrorHelperAnimation(
            context,
            demoModeCodeViewHolderHelper?.getBinding()?.textViewHelperText
        )
        CookingAppUtils.setHelperTextColor(
            demoModeCodeViewHolderHelper?.getBinding()?.textViewHelperText,
            NumPadHelperTextColor.ERROR_TEXT_COLOR
        )
    }

    private fun setDefaultHelperText() {
        demoModeCodeViewHolderHelper?.getBinding()?.textViewHelperText?.visibility =
            View.VISIBLE

        demoModeCodeViewHolderHelper?.getBinding()?.textViewHelperText?.text =
            String.format(
                CookingAppUtils.getStringFromResourceId(
                    requireContext(),
                    R.string.text_demo_code
                ),
            )
        demoModeCodeViewHolderHelper?.getBinding()?.textViewHelperText?.setTextColor(resources.getColor(R.color.light_grey,null))
        demoCodeText = "0000"
        digitInputIndex = -1
    }

    /**
     * Method to get the helper text string resource
     */
    private fun provideHelperTextString(): Int {
        return R.string.text_demo_code_error
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
        manageLeftButton()
        manageRightButton()
        manageMiddleButton()
        setDefaultHelperText()
        manageInputTextField()
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
    }


    /**
     * Method to update the Input Text Field on text entry
     *
     * @param text text entered
     */
    private fun onTextEntry(text: CharSequence?) {
            if (text != null) {
                handleTextEntryForDisabledSecondsField(text)
            }
        demoModeCodeViewHolderHelper?.getRightTextButton()?.isEnabled = true
        updateDemoCodeTextInInputField()
    }

    /**
     * Method to handle the text Entry in normal oven cook time, last two char field (seconds
     * field) will always be zero. Newly entered char is appended at 4th position
     *
     * @param text text clicked from the number  pad
     */
    protected open fun handleTextEntryForDisabledSecondsField(text: CharSequence) {
        if (digitInputIndex == 3) {
            return
        }

        //Incrementing the digit by 1
        digitInputIndex++

        //First time loading - Filling value with 0s
        if (isScreenFreshlyLoaded) {
            isScreenFreshlyLoaded = false
            demoCodeText = text.toString()
            digitInputIndex = 0
        } else {
            demoCodeText += text.toString()
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
        demoModeCodeViewHolderHelper?.getHeaderBarNumPadWidget()?.setBackIconOnClickListener {
            //Header left icon click event
            NavigationViewModel.popBackStack(
                Navigation.findNavController(
                    getViewSafely(this) ?: requireView()
                )
            )
        }
        demoModeCodeViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setCancelIconOnClickListener { onBackSpaceIconClick() }
        demoModeCodeViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setTumblerIconEnable(false)
        demoModeCodeViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.setTumblerIconVisibility(false)
    }

    /**
     * Method to handle the left buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    private fun manageLeftButton() {
        demoModeCodeViewHolderHelper?.getLeftTextButton()?.setOnClickListener(this)
        NavigationUtils.manageLeftButtonForRecipeOption(
            cookingViewModel,
            RecipeOptions.COOK_TIME,
            demoModeCodeViewHolderHelper?.getLeftTextButton()
        )
    }

    /**
     * Method to handle the Right buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageRightButton() {
        demoModeCodeViewHolderHelper?.getRightTextButton()?.setOnClickListener(this)
        demoModeCodeViewHolderHelper?.getRightTextButton()?.isEnabled = true
        demoModeCodeViewHolderHelper?.getRightTextButton()?.isClickable = true
        demoModeCodeViewHolderHelper?.getRightTextButton()
            ?.setTextButtonText(
                updateRightButtonText()
            )
    }

    protected open fun updateRightButtonText(): String {
        return resources.getString(R.string.text_button_next)
    }

    /**
     * Method to handle the middle buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageMiddleButton() {
        demoModeCodeViewHolderHelper?.getMiddleTextButton()?.setOnClickListener(this)
        demoModeCodeViewHolderHelper?.getMiddleTextButton()?.visibility = View.GONE
    }


    /**
     * Method to set the initial time text
     *
     * @param  defaultValue text
     */
    protected open fun manageInputTextField() {
        demoModeCodeViewHolderHelper?.getHeaderBarNumPadWidget()
            ?.getTextViewCookTime()?.visibility =
            View.VISIBLE
        updateDemoCodeTextInInputField()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.demo_code_text_button_left -> {
                onClickListener?.onLeftButtonClick()
            }

            R.id.demo_code_text_button_right -> {
                onClickListener?.onRightButtonClick()
            }

            R.id.demo_code_text_button_middle -> {
                onClickListener?.onMiddleButtonClick()
            }

            R.id.demo_code_text_button_left_power -> {
                onClickListener?.onLeftPowerButtonClick()
            }
        }
    }

    /**
     * Validates the cook time and changes the color of helper text accordingly.
     *
     * @return true if the cook time is valid.
     */
    protected open fun validateCode(): Boolean {
        val isValid = isCodeValid()
        if (!isValid) {
            manageHelperText()
            demoModeCodeViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.VISIBLE
            CookingAppUtils.setHelperTextColor(
                demoModeCodeViewHolderHelper?.getErrorHelperTextView(),
                NumPadHelperTextColor.ERROR_TEXT_COLOR
            )
        } else {
            demoModeCodeViewHolderHelper?.getErrorHelperTextView()?.visibility =
                View.INVISIBLE
            demoModeCodeViewHolderHelper?.getRightTextButton()?.isEnabled = true
            demoModeCodeViewHolderHelper?.getRightTextButton()?.isClickable = true
        }
        return isValid
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
    protected open fun onBackSpaceIconClick() {
        if (digitInputIndex == -1) {
            return
        }

        //Decreasing digit count by 1 when backspace is pressed.
        digitInputIndex--
        if (digitInputIndex < 0) {
            demoModeCodeViewHolderHelper?.getRightTextButton()?.isEnabled = false
            digitInputIndex = -1
        }

        demoCodeText = demoCodeText?.substring(0, demoCodeText?.length!! - 1)

        isScreenFreshlyLoaded = false
        updateDemoCodeTextInInputField()
    }

    /**
     * Method to manipulate te cook timer string with appended respective units
     * and update the formatted string in the input field text view
     */
    protected open fun updateDemoCodeTextInInputField() {
        if (demoCodeText != null) {
        val spannableTimeString = SpannableString(demoCodeText)
        demoModeCodeViewHolderHelper?.getHeaderBarNumPadWidget()?.getTextViewCookTime()
            ?.setText(
                spannableTimeString,
                TextView.BufferType.SPANNABLE
            )
        }
    }

    /**
     * Method to validate whether the cook timer is in range
     *
     * @return true if it is a valid cook time
     */
    protected open fun isCodeValid(): Boolean {
        if (Objects.nonNull(demoModeCodeViewHolderHelper?.getHeaderBarNumPadWidget()?.getTextViewCookTime()?.getText()) && demoModeCodeViewHolderHelper?.getHeaderBarNumPadWidget()?.getTextViewCookTime()?.getText()?.length == 4 &&
            demoModeCodeViewHolderHelper?.getHeaderBarNumPadWidget()?.getTextViewCookTime()?.getText().toString() == AppConstants.DEMO_CODE
        ) {
            // Todo: once demo mode API available need to check with actual values
            if (CookingAppUtils.isDemoModeEnabled()) {
                validCodeActionWhenDemoModeIsEnabled()
            } else {
                validCodeActionWhenDemoModeIsDisabled()
            }
            return true
        } else {
            clearInvalidEntry()
            return false
        }
    }

    /**
     * Method to clear the invalid numPad entry and reset input value to default
     */
    protected open fun clearInvalidEntry() {
        demoCodeText = AppConstants.DEMO_DEFAULT_CODE
        digitInputIndex = -1
        isScreenFreshlyLoaded = true
        updateDemoCodeTextInInputField()
        demoModeCodeViewHolderHelper?.getRightTextButton()?.isEnabled = false
    }


    override fun getKeyboardView(): KeyboardView? =
        demoModeCodeViewHolderHelper?.getKeyboardView()


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

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        demoModeCodeViewHolderHelper = null
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
        if (demoModeCodeViewHolderHelper?.getRightTextButton()?.isEnabled == true) {
            KnobNavigationUtils.knobForwardTrace = true
            demoModeCodeViewHolderHelper?.getRightTextButton()?.callOnClick()
        }
    }

    override fun onHMIRightKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        demoModeCodeViewHolderHelper?.getRightTextButton()?.setBottomButtonViewVisible(true)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        demoModeCodeViewHolderHelper?.getRightTextButton()?.setBottomButtonViewVisible(false)
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    abstract fun provideIntegerRange(): IntegerRange?

    protected abstract fun validCodeActionWhenDemoModeIsEnabled()

    protected abstract fun validCodeActionWhenDemoModeIsDisabled()

}