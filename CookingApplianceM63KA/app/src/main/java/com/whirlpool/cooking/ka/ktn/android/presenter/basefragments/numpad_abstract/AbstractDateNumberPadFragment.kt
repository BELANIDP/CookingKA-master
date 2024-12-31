package android.presenter.basefragments.numpad_abstract

import android.os.Bundle
import android.presenter.customviews.NumpadHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentDateBinding
import core.utils.AppConstants
import core.utils.NumpadUtils
import com.whirlpool.hmi.uicomponents.widgets.keyboard.Keyboard
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView


/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */ /**
 * File       : java/com/whirlpool/cooking/ka/ktn/android/presenter/basefragments/numpad_abstract/AbstractDateNumberPadFragment.kt
 * Brief      : extends with AbstractNumpadViewHolder
 * Author     : Gaurav Pete
 * Created On : 12-02-2024
 * Details    : AbstractNumpadViewHolder provides the common abstract funtions that could be used in feature implemented abstract class
 */
abstract class AbstractDateNumberPadFragment : AbstractNumpadViewHolder(),
    KeyboardInputManagerInterface, View.OnClickListener {
    private lateinit var numpadHelper: NumpadHelper
    private var fragmentDateBinding: FragmentDateBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDateBinding = FragmentDateBinding.inflate(inflater)
        fragmentDateBinding?.lifecycleOwner = this
        return fragmentDateBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initKeyboardView()
        initViews()
    }

    override fun getKeyboardView(): KeyboardView? {
        return providesKeyBoardView()
    }

    override fun providesKeypadDateTextView(): KeypadTextView? {
        return fragmentDateBinding?.dateText
    }

    override fun providesKeyBoardView(): KeyboardView? {
        return fragmentDateBinding?.keyboardview
    }

    override fun providesTemperatureTextView(): KeypadTextView? {
        //  Provides Temperature Textview
        return null
    }

    override fun providesClockHoursTextView(): TextView? {
        // Provides Clock Hours Textview
        return null
    }

    override fun providesClockMinutesTextView(): TextView? {
        //  Provides Clock Minutes Textview
        return null
    }

    override fun providesClockSecondTextView(): TextView? {
        // Provides Clock Second Textview
        return null
    }

    override fun providesClockUnitsTextView(): TextView? {
        // Provides Clock Units Textview
        return null
    }

    override fun providesErrorHelperTextView(): TextView? {
        return fragmentDateBinding?.errorTextHelper
    }

    override fun providesBackSpaceImageView(): ImageView? {
        return fragmentDateBinding?.imageViewBackspaceIcon
    }

    override fun onClick(view: View) {
        val id = view.id
        if (id == R.id.image_view_backspace_icon) {
            numpadHelper.keyboardCommonVM?.onKey(Keyboard.KEYCODE_BACKSPACE, null)
        }
        if (id == R.id.next_btn) {
            if (!numpadHelper.keyboardCommonVM?.isValidEntry!!) {
                showInvalidMessage(
                    providesErrorHelperTextView(),
                    getErrorDetails(),
                    true,
                    providesBackSpaceImageView()
                )
            }
        }
    }

    /* use TO Initialize the click view listener on set the default value*/
    protected open fun initViews() {
        providesKeypadDateTextView()?.setDefaultValue("")
        providesBackSpaceImageView()?.setOnClickListener(this)
        fragmentDateBinding?.nextBtn?.setOnClickListener(this)
    }

    /*To clear the Binding view*/
    private fun clearMemory() {
        fragmentDateBinding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        numpadHelper.clearKeyboardViewModel()
        clearMemory()
    }

    /* It is use to provides numpad keyboard initialize */
    private fun initKeyboardView() {
        numpadHelper = NumpadHelper()
        numpadHelper.initNumpad(this, requireContext())
        providesKeypadDateTextView()?.keyboardViewModel = numpadHelper.keyboardCommonVM
        providesKeypadDateTextView()?.setClearEntryOnLimitReached(true)

    }

    /* it is use to show the invalid massage in error text view*/

    @Suppress("UNUSED_PARAMETER")
    private fun showInvalidMessage(
        textView: TextView?,
        error: CharSequence?,
        @Suppress("SameParameterValue") show: Boolean,
        backSpaceIcon: View?
    ) {
        if (textView != null) {
            if (show) {
                textView.text = error
                textView.visibility = View.VISIBLE
                textView.setTextColor(
                    resources.getColor(R.color.error_text_color, null)
                )
            } else {
                if (textView.visibility == View.VISIBLE) {
                    textView.visibility = View.GONE
                }
            }
        }
    }

    /*To get the error code / details of error*/
    private fun getErrorDetails(): String? {
        val validEntry = numpadHelper.keyboardCommonVM?.errorCodeObserver?.value
        return if (validEntry != null) {
            context?.let { NumpadUtils.getTimeDateErrorMessage(validEntry, it) }
        } else {
            AppConstants.EMPTY_STRING
        }
    }
}
