package android.presenter.basefragments.numpad_abstract

import android.os.Bundle
import android.presenter.customviews.NumpadHelper
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.SuperscriptSpan
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.NumberPadKeyboardBinding

import core.utils.AppConstants
import core.utils.NumpadUtils
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.Keyboard
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView
import core.utils.AppConstants.DEFAULT_TRIPLE_ZERO


/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */ /**
 * File       : java/com/whirlpool/cooking/ka/ktn/android/presenter/basefragments/numpad_abstract/AbstractTempNumpadKeyboard.kt
 * Brief      : extends with AbstractNumpadViewHolder
 * Author     : Gaurav Pete
 * Created On : 12-02-2024
 * Details    : AbstractNumpadViewHolder provides the common abstract funtions that could be used in feature implemented abstract class
 */
@Suppress("unused", "SENSELESS_COMPARISON")
abstract class AbstractTempNumpadKeyboard : AbstractNumpadViewHolder(),
    KeyboardInputManagerInterface, View.OnClickListener {

    private var numberPadKeyboardBinding: NumberPadKeyboardBinding? = null
    private var minTemperature: String = "0"
    private var numpadHelper: NumpadHelper? = null
    private var maxTemperature: String = "0"
    private var isScreenFreshlyLoaded = true
    private var digitInputIndex = 3
    private var isBackSpaceClicked = false
    private var numericValue: CharSequence? = null
    private var selectedTemp = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        numberPadKeyboardBinding = NumberPadKeyboardBinding.inflate(inflater)
        numberPadKeyboardBinding?.lifecycleOwner = this
        return numberPadKeyboardBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNumericKeyboard()
        providesBackSpaceImageView()?.setOnClickListener { backSpaceText() }
        numberPadKeyboardBinding?.nextBtn?.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        /*used for null check*/if (minTemperature != null && maxTemperature != null) {
            providesErrorHelperTextView()?.visibility = View.GONE
            disableNumpadItems(0, "1", 0)
        } else {
            temperatureNullCheckValidation()
            providesErrorHelperTextView()?.visibility = View.VISIBLE
        }
        providesTemperatureTextView()?.setDefaultValue("")
    }

    override fun getKeyboardView(): KeyboardView? {
        return providesKeyBoardView()
    }

    @Suppress("UNNECESSARY_SAFE_CALL")
    override fun onClick(view: View) {
        //On click of next Button validate the enter temperature is between min and max temperature or not
        if (view.id == R.id.next_btn) {
            providesErrorHelperTextView()?.visibility = View.GONE
            val enterTemperature = providesTemperatureTextView()?.value?.toInt()
            val minTemp = minTemperature?.toInt()
            val maxTemp = maxTemperature?.toInt()
            if (enterTemperature != null) {
                if (!(enterTemperature >= minTemp!! && enterTemperature <= maxTemp!!)) {
                    providesErrorHelperTextView()?.visibility = View.VISIBLE
                    providesErrorHelperTextView()?.text = String.format(
                        getString(R.string.text_temperature_number_pad_error_temp),
                        minTemperature,
                        maxTemperature
                    )
                }
            }
        }
    }

    override fun providesKeyBoardView(): KeyboardView? {
        return numberPadKeyboardBinding?.keyboardview
    }

    override fun providesTemperatureTextView(): KeypadTextView? {
        return numberPadKeyboardBinding?.keypadTextView
    }

    override fun providesClockHoursTextView(): TextView? {
        //  Provides Clock Hours Textview
        return null
    }

    override fun providesClockMinutesTextView(): TextView? {
        // Provides Clock Minutes Textview
        return null
    }

    override fun providesClockSecondTextView(): TextView? {
        // Provides Clock Second Textview
        return null
    }


    override fun providesKeypadDateTextView(): KeypadTextView? {
        return null
    }

    override fun providesClockUnitsTextView(): TextView? {
        // Provides Clock Units Textview
        return null
    }

    override fun providesErrorHelperTextView(): TextView? {
        return numberPadKeyboardBinding?.errorHelperText
    }

    override fun providesBackSpaceImageView(): ImageView? {
        return numberPadKeyboardBinding?.imageViewBackspaceIcon
    }

    /* init the keyboard*/
    open fun initNumericKeyboard() {
        numpadHelper = NumpadHelper()
        numpadHelper?.initNumpad(this, requireContext())
        setNumericValue(DEFAULT_TRIPLE_ZERO)
        if (minTemperature != null && maxTemperature != null) {
            backSpaceButtonDisable(true)
            numberPadKeyboardBinding?.keypadTextView?.keyboardViewModel =
                numpadHelper?.keyboardCommonVM
            numpadHelper?.keyboardCommonVM?.resultTextObserver?.observe(
                viewLifecycleOwner
            ) { obj ->
                if (obj != null) {
                    numpadInput(obj.toString())
                }
            }
            numpadHelper?.keyboardCommonVM?.disabledKeyPressedObserver?.observe(
                viewLifecycleOwner
            ) { disableKeyTapped: Boolean ->
                disableTap(
                    disableKeyTapped
                )
            }
            providesTemperatureTextView()?.setClearEntryOnLimitReached(true)
        } else {
            backSpaceButtonDisable(false)
            temperatureNullCheckValidation()
            providesErrorHelperTextView()?.visibility = View.VISIBLE
        }
        providesTemperatureTextView()?.setDefaultValue("")
    }

    // set the min temperature
    open fun setMinTemperature(minTemperature: String) {
        this.minTemperature = minTemperature
    }

    // set the max temperature
    open fun setMaxTemperature(maxTemperature: String) {
        this.maxTemperature = maxTemperature
    }

    /*Get the keyboard view model*/
    open fun getKeyboardVM(): KeyboardViewModel? {
        return numpadHelper?.keyboardCommonVM
    }

    /*get cooking view model*/
    protected open fun getCookingViewModel(): CookingViewModel? {
        return numberPadKeyboardBinding?.cookingViewModel
    }

    /*To get the Binding */
    open fun getNumberPadKeyboardBinding(): NumberPadKeyboardBinding? {
        return numberPadKeyboardBinding
    }

    //BackSpaceText() : handel back space
    open fun backSpaceText() {
        providesErrorHelperTextView()?.visibility = View.GONE
        numpadHelper?.keyboardCommonVM?.onKey(Keyboard.KEYCODE_BACKSPACE, null)
        isBackSpaceClicked = true
        if (digitInputIndex < 0) {
            digitInputIndex = 2
        }
        digitInputIndex--
        if (numericValue?.length != 0) {
            numericValue = "0${numericValue?.get(0)}${numericValue?.get(1)}"
            setNumericValue(numericValue as String)
            disableNumpadItems(
                if (digitInputIndex > 2) 0 else digitInputIndex, numericValue.toString(),
                Character.getNumericValue((numericValue as String)[(numericValue as String).length - 1])
            )
        }
    }

    /*To disable numpad keyboard keys*/
    protected open fun disableNumpadItems(
        index: Int,
        currentValue: String,
        currentIndexValue: Int
    ) {
        providesKeyBoardView()?.disableKeyWithKeyLabels(
            numpadHelper?.disableKeypadItemsForTemp(
                index,
                currentValue, currentIndexValue, minTemperature, maxTemperature
            )
        )
    }


    /*Logic to set the text and disable numpad keyboad*/
    protected open fun numpadInput(text: CharSequence) {
        providesErrorHelperTextView()?.visibility = View.GONE
        if (isScreenFreshlyLoaded && !isBackSpaceClicked) {
            isScreenFreshlyLoaded = false
            numericValue = DEFAULT_TRIPLE_ZERO
            digitInputIndex = 0
        }
        if (digitInputIndex > 2) {
            digitInputIndex = 0
            numericValue = DEFAULT_TRIPLE_ZERO
            disableNumpadItems(
                digitInputIndex, numericValue.toString(),
                Character.getNumericValue(text[text.length - 1])
            )
        }
        digitInputIndex++
        //Clear the old values with "" and append the last typed character to the end.
        if (text.isNotEmpty()) {
            numericValue =
                AppConstants.EMPTY_STRING + "${numericValue?.get(1)}${numericValue?.get(2)}${text[text.length - 1]}"
            setNumericValue(numericValue as String)
            disableNumpadItems(
                if (digitInputIndex > 2) 0 else digitInputIndex, numericValue.toString(),
                Character.getNumericValue(text[text.length - 1])
            )
        }
    }

    //this function used to handle the onclick of disable button click on numpad
    protected open fun disableTap(disableKeyTapped: Boolean) {
        if (java.lang.Boolean.TRUE == disableKeyTapped) {
            providesErrorHelperTextView()?.visibility = View.VISIBLE
            if (minTemperature != null && maxTemperature != null) {
                providesErrorHelperTextView()?.text = String.format(
                    getString(R.string.text_temperature_number_pad_error_temp),
                    minTemperature,
                    maxTemperature
                )
            } else {
            }
        }
    }

    // below function use to set the temperature value the text view
    protected open fun setNumericValue(locNumericValue: CharSequence) {
        //On deleting the third temperature digit, the text is again set to the default value and not 000
        var value = locNumericValue
        if (value == DEFAULT_TRIPLE_ZERO || value == selectedTemp.toString()) {
            value = if (selectedTemp == -1) {
                DEFAULT_TRIPLE_ZERO
            } else {
                selectedTemp.toString()
            }
            numericValue = value
            digitInputIndex = 3
            isScreenFreshlyLoaded = true
        }
        val numericValueSpan = SpannableString(value)
        val unit = if (NumpadUtils.isFAHRENHEITUnitConfigured()) AppConstants.EMPTY_STRING + getString(
            R.string.text_tiles_list_fahrenheit_value
        ) else AppConstants.EMPTY_STRING + getString(R.string.text_tiles_list_celsius_value)
        val numericUnit = SpannableString(unit)
        numericValueSpan.setSpan(
            TextAppearanceSpan(
                requireContext(),
                R.style.Style56LightWhiteVCenterHCenter
            ), 0, value.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        numericUnit.setSpan(
            TextAppearanceSpan(
                providesTemperatureTextView()?.context,
                R.style.Style36LightWhiteVCenterHLeft
            ), 0, unit.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val formattedText = TextUtils.concat(numericValueSpan, numericUnit)
        val text = formattedText.toString()
        val superscriptSpan = SuperscriptSpan()
        val builder = SpannableStringBuilder(text)
        builder.setSpan(
            superscriptSpan, text.indexOf(value.toString()),
            text.indexOf(value.toString()) + value.toString().length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        providesTemperatureTextView()?.text = builder.toString()
    }

    /*use to clear the Binding*/
    open fun clearMemory() {
        numberPadKeyboardBinding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        numpadHelper?.clearKeyboardViewModel()
        clearMemory()
    }

    /*used for null check
     * if max temperature or min temperature is null
     * result : all keys of numpad is disable and display the error message*/

    /*used for null check
     * if max temperature or min temperature is null
     * result : all keys of numpad is disable and display the error message*/
    open fun temperatureNullCheckValidation() {
        val disableList: ArrayList<String?> = ArrayList()
        var i = 0
        while (i >= 9) {
            disableList.add(i.toString())
            i++
        }
        providesKeyBoardView()?.keyTextDisabledColor =
            requireContext().getColor(R.color.keydisable_color)
        providesKeyBoardView()?.disableKeyWithKeyLabels(disableList)
    }

    /**use to disable or enable the back space buttons
     * @param isDisable  : boolean
     * */
    open fun backSpaceButtonDisable(isDisable: Boolean) {
        providesBackSpaceImageView()?.isClickable = isDisable
        providesBackSpaceImageView()?.isEnabled = isDisable
    }
}