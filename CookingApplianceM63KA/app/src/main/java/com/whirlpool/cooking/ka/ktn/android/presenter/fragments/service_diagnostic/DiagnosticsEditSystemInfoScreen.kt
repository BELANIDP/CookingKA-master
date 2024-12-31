/*
 *  *----------------------------------------------------------------------------------------------*
 *  * ---- Copyright 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL --------------*
 *  * ---------------------------------------------------------------------------------------------*
 */
package android.presenter.fragments.service_diagnostic


import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.media.AudioManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DiagnosticsEditSystemInfoScreenBinding
import com.whirlpool.cooking.ka.databinding.ErrorDiagnosticsPopupFragmentBinding
import com.whirlpool.hmi.diagnostics.utils.DiagnosticsConstants.SYSTEM_INFO_MODEL_NUMBER_KEY
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsEditSystemInfoViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsPopupViewProvider
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.HMILogHelper
import java.util.regex.Pattern


/**
 * File       : com.whirlpool.cooking.diagnostic.DiagnosticsEditSystemInfoScreen
 * Brief      : AbstractDiagnosticsEditSystemInfoViewProvider instance for Diagnostics Edit System Info Screen
 * Author     : Rajendra
 * Created On : 26-06-2024
 * Details    : This fragment allow to service engineer to edit Model number and serial number
 */
class DiagnosticsEditSystemInfoScreen : AbstractDiagnosticsEditSystemInfoViewProvider() {
    private var fragmentBinding: DiagnosticsEditSystemInfoScreenBinding? = null
    private var textWatcher: TextWatcher? = null
    private var enterKeyLength = 12
    private var isValidEntry = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding =
            DiagnosticsEditSystemInfoScreenBinding.inflate(inflater, container, false)
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initHeader()
        observerOnTextChanged()
        fragmentBinding?.textViewHelperText?.visibility = View.GONE
        fragmentBinding?.nextButton?.setText(R.string.text_button_set)
    }

    /**
     * Init header view visibility and populate default text
     */
    private fun initHeader() {
        fragmentBinding?.titleBar?.setTitleTextViewVisibility(false)
        fragmentBinding?.titleBar?.setKeypadTextViewVisibility(true)
        fragmentBinding?.titleBar?.setTumblerIconVisibility(false)
        fragmentBinding?.titleBar?.getHeaderKeypadTextView()?.setDefaultValue(AppConstants.DEFAULT_ENTRY_NUMBER)
    }

    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
    }

    override fun onViewClicked(view: View?) {
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
    }

    override fun onDestroyView() {
        fragmentBinding?.titleBar?.getHeaderKeypadTextView()?.removeTextChangedListener(textWatcher)
        fragmentBinding = null
    }

    override fun provideResources(): Resources {
        return fragmentBinding?.root?.resources as Resources
    }

    override fun provideKeypadBackSpaceButton(): View {
        return fragmentBinding?.titleBar?.getCancelView() as View
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return fragmentBinding?.textViewSystemEditText
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideTitleText(editableSystemInfoKey: String): CharSequence {
        return if (editableSystemInfoKey == SYSTEM_INFO_MODEL_NUMBER_KEY) {
            provideResources().getString(R.string.enterModelNumber)
        } else {
            provideResources().getString(R.string.enterSerialNumber)
        }
    }

    override fun provideSubTitleText(): CharSequence? {
        return null
    }

    override fun provideKeypadTextView(): KeypadTextView? {
        return fragmentBinding?.titleBar?.getHeaderKeypadTextView()
    }

    override fun providePrimaryButton(): NavigationButton? {
        return fragmentBinding?.nextButton
    }


    override fun provideLeftNavigationView(): View? {
        return fragmentBinding?.titleBar?.getBackButtonView()
    }

    override fun provideHelperTextView(): TextView? {
        return fragmentBinding?.textViewHelperRedWarningText
    }

    override fun provideKeyboardView(editableSystemInfoKey: String): KeyboardView? {
        fragmentBinding?.keyboard?.keyboardAlphaReference = R.xml.service_keyboard_alpha_local
        fragmentBinding?.keyboard?.keyboardSymbolsReference = R.xml.service_keyboard_symbols_local
        fragmentBinding?.keyboard?.isLockedCaps = true
        return fragmentBinding?.keyboard
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun clearEntryOnInvalidInput(): Boolean {
        return true
    }

    override fun isValidInput(editableSystemInfoKey: String, valueToSet: String): Boolean {
        HMILogHelper.Logd("service diagnostic - editableSystemInfoKey: " + editableSystemInfoKey + "valueToSet: " + valueToSet)
        return isValidSerialOrModelNumber(valueToSet)
    }

    override fun provideEnterTransition(context: Context): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context): Transition? {
        return null
    }

    override fun provideRightNavigationView(): View? {
        return null
    }

    override fun provideRightIconResource(): Int {
        return 0
    }

    override fun provideMinimumCharacterLimitToEditInfo(editableKey: String): Int {
        if (editableKey == SYSTEM_INFO_MODEL_NUMBER_KEY) {
            return provideResources().getInteger(R.integer.integer_range_11)
        }
        return provideResources().getInteger(R.integer.integer_range_8)
    }

    override fun provideMaximumCharacterLimitToEditInfo(editableKey: String): Int {
        if (editableKey == SYSTEM_INFO_MODEL_NUMBER_KEY) {
            return provideResources().getInteger(R.integer.integer_range_12)
        }
        return provideResources().getInteger(R.integer.integer_range_8)
    }

    override fun provideValidationErrorString(editableKey: String): CharSequence {
        fragmentBinding?.textViewHelperText?.visibility = View.GONE
        fragmentBinding?.textViewSystemEditText?.visibility = View.GONE
        isValidEntry = true
        if (editableKey == SYSTEM_INFO_MODEL_NUMBER_KEY) {
            return provideResources().getString(R.string.sdk_diagnostics_model_number_validation_error)
        }
        return provideResources().getString(R.string.sdk_diagnostics_serial_number_validation_error)
    }
    override fun provideConfirmationPopupViewProvider(): AbstractDiagnosticsPopupViewProvider {
        return object : AbstractDiagnosticsPopupViewProvider() {
            private var popupViewBinding: ErrorDiagnosticsPopupFragmentBinding? = null

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                popupViewBinding =
                    ErrorDiagnosticsPopupFragmentBinding.inflate(inflater, container, false)
                popupViewBinding?.textViewTitle?.let { addMarginToPopup(it,AppConstants.POPUP_TITLE_TOP_MARGIN_75PX) }
                popupViewBinding?.textViewDescription?.let {
                    popupViewBinding?.textViewDescription?.context?.let { context ->
                        ResourcesCompat.getFont(context, R.font.roboto_light)
                            ?.let { typeface ->
                                addTypefacePopup(it,
                                    typeface
                                )
                            }
                    }
                }
                return popupViewBinding?.root
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                AudioManagerUtils.playOneShotSound(
                    ContextProvider.getContext(),
                    R.raw.audio_alert,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
            }

            override fun onDestroyView() {
                popupViewBinding = null
            }

            override fun provideResources(): Resources? {
                return popupViewBinding?.root?.resources
            }

            override fun providePrimaryButton(): NavigationButton? {
                return popupViewBinding?.textButtonRight
            }

            override fun provideSecondaryButton(): NavigationButton? {
                return popupViewBinding?.textButtonLeft
            }

            override fun provideTitleTextView(): TextView? {
                return popupViewBinding?.textViewTitle
            }


            override fun provideSubTitleTextView(): TextView? {
                return null
            }

            override fun provideDescriptionTextView(): TextView? {
                return popupViewBinding?.textViewDescription
            }

            override fun provideSetSystemInfoPopupPrimaryButtonText(): CharSequence? {
                return provideResources()?.getString(R.string.text_button_yes)
            }

            override fun provideSetSystemInfoPopupSecondaryButtonText(): CharSequence? {
                return provideResources()?.getString(R.string.text_button_dismiss)
            }

            override fun provideSetModelNumberPopupDescriptionText(): CharSequence? {
                return provideResources()?.getString(R.string.sdk_diagnostics_popup_description_set_model_number)
            }

            override fun provideSetSerialNumberPopupDescriptionText(): CharSequence? {
                return provideResources()?.getString(R.string.sdk_diagnostics_popup_description_set_serial_number)
            }

            override fun provideSetModelNumberPopupTitleText(): CharSequence? {
                return provideResources()?.getString(R.string.sdk_diagnostics_popup_title_set_serial_number)
            }

            override fun provideSetSerialNumberPopupTitleText(): CharSequence? {
                return provideResources()?.getString(R.string.sdk_diagnostics_popup_title_set_serial_number)
            }
        }
    }

    override fun updatePrimaryButtonInteraction(enableInteraction: Boolean) {
        if (enableInteraction) {
            fragmentBinding?.nextButton?.setTextColor(
                ContextCompat.getColor(
                    fragmentBinding?.nextButton?.context!!,
                    R.color.diagnostics_primary_text
                )
            )
        } else {
            fragmentBinding?.nextButton?.setTextColor(
                ContextCompat.getColor(
                    fragmentBinding?.nextButton?.context!!,
                    R.color.diagnostics_disabled_text
                )
            )
        }
    }

    /**
     * Function to check provided serial/model number string is valid or not
     * Valid model/serial number is the one composed only of numbers and upper case letters characters. It shall never be empty.
     * @param serialOrModelNumber Appliance serial or model number in string format
     * @return true if provided string is valid else false
     */
    private fun isValidSerialOrModelNumber(serialOrModelNumber: String): Boolean {
        // Regex to check string is alphanumeric with capital letters or not.
        val regex = "^[A-Z0-9]+$"
        // Compile the ReGex
        val pattern = Pattern.compile(regex)
        // Pattern class contains matcher() method to find matching between given string and regular expression.
        val matcher = pattern.matcher(serialOrModelNumber)
        // Return result of string matched the ReGex or not
        val result = matcher.matches()
        if (!result) {
            fragmentBinding?.textViewHelperText?.visibility = View.GONE
            fragmentBinding?.textViewHelperRedWarningText?.visibility = View.VISIBLE
            fragmentBinding?.textViewSystemEditText?.visibility = View.GONE
            fragmentBinding?.textViewHelperRedWarningText?.text = provideResources().getString(R.string.enterValidNumber)
        }
        HMILogHelper.Logd("service diagnostic - isValidSerialOrModelNumber: $result")
        return result
    }

    /**
     * Add top marting to view
     */
    @Suppress("SameParameterValue")
    private fun addMarginToPopup(textView: TextView, margin: Int) {
        val param = (textView.layoutParams as ViewGroup.MarginLayoutParams).apply {
            setMargins(0, margin, 0, 0)
        }
        textView.layoutParams = param
    }
    private fun addTypefacePopup(textView: TextView, typeface: Typeface) {
        textView.typeface = typeface
    }


    /**
     * Tex changed listener
     */
    private fun observerOnTextChanged() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                validateKeyboardEntry()
                val keyboardTextValue = fragmentBinding?.titleBar?.getHeaderKeypadTextView()?.value
                val disableKeyList = ArrayList<Int>()
                disableKeyList.add(AppConstants.KEYBOARD_ENTER_KEY)
                when {
                    keyboardTextValue.isNullOrEmpty() -> fragmentBinding?.keyboard?.disableKeyWithKeyCodes(disableKeyList)
                    else -> when {
                        keyboardTextValue.isNotEmpty() -> fragmentBinding?.keyboard?.disableKeyWithKeyCodes(ArrayList())
                        else -> fragmentBinding?.keyboard?.disableKeyWithKeyCodes(disableKeyList)
                    }
                }
            }
            override fun afterTextChanged(s: Editable) {
            }
        }
        fragmentBinding?.titleBar?.getHeaderKeypadTextView()?.addTextChangedListener(textWatcher)
    }
    /**
     * validate keyboard entry password and accordingly visible/gone the helper textview
     */
    private fun validateKeyboardEntry() {
        val keyboardTextValue = fragmentBinding?.titleBar?.getHeaderKeypadTextView()?.value
        if (keyboardTextValue?.isNotEmpty() == true && keyboardTextValue.length <enterKeyLength) {
            val isValid = isValidSerialOrModelNumber(keyboardTextValue)
            if (isValid) {
                fragmentBinding?.textViewHelperText?.visibility = View.GONE
                fragmentBinding?.textViewHelperRedWarningText?.visibility = View.GONE
                fragmentBinding?.textViewSystemEditText?.visibility = View.VISIBLE
            } else {
                fragmentBinding?.textViewHelperText?.visibility = View.GONE
                fragmentBinding?.textViewHelperRedWarningText?.visibility = View.VISIBLE
                fragmentBinding?.textViewSystemEditText?.visibility = View.GONE
                fragmentBinding?.textViewHelperRedWarningText?.text = provideResources().getString(R.string.enterValidNumber)
            }
        } else {
            if(isValidEntry) {
                fragmentBinding?.textViewHelperRedWarningText?.visibility = View.VISIBLE
                fragmentBinding?.textViewSystemEditText?.visibility = View.GONE
                fragmentBinding?.textViewHelperText?.visibility = View.GONE
            }else{
                fragmentBinding?.textViewHelperText?.visibility = View.GONE
                fragmentBinding?.textViewHelperRedWarningText?.visibility = View.GONE
                fragmentBinding?.textViewSystemEditText?.visibility = View.VISIBLE
            }
        }
        isValidEntry = false
    }
}