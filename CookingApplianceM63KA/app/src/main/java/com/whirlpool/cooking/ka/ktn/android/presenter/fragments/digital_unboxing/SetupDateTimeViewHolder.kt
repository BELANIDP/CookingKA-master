/*
* ************************************************************************************************
* ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
* ************************************************************************************************
*/
package android.presenter.fragments.digital_unboxing

import android.content.res.Resources
import android.os.Bundle
import android.presenter.customviews.textButton.TextButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentSetupLaterDateTimeBinding
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.Keyboard
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView
import core.utils.HMILogHelper
import core.utils.NavigationUtils

/**
 * File       : com.whirlpool.cooking.unboxing.SetupLaterTimeViewHolder
 * Brief      : Handles to setup time during unboxing
 * Author     : Nikki Gharde
 * Created On : 05-sep-2024
 */
class SetupDateTimeViewHolder(var fragment:Fragment, var isSetupForDate:Boolean = false ,var onHeaderBarTumblerClick:() -> Unit= {},var onHeaderBarBackClick:() -> Unit= {}, var isToolsSettingsFlow:Boolean = false) : AbstractDateTimeViewHolder() {
    private var fragmentDateNumberPadBinding: FragmentSetupLaterDateTimeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater?, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDateNumberPadBinding =
            inflater?.let { FragmentSetupLaterDateTimeBinding.inflate(it) }
        return fragmentDateNumberPadBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * to initialize views
     */
    private fun initViews() {
        initNumPadHeaderBar()
        // Due to using same xml class in TimeView also we are providing these here instead of XML
        setupSeperatorOnKeypad()

    }

    /**
     * The separator is the character between the sections of the time that help identify hours vs. minutes vs. seconds.
     * <p>
     * The default is SEPARATOR_TYPE_COLON_OR_BACKSLASH (":" - "1:23:45" or "/" - "12/12/22"). Other options include:
     * <p>
     * - SEPARATOR_TYPE_PERIOD ("." - "1.23.45" or "12.12.22")
     * - SEPARATOR_TYPE_ALPHA ("h,m,s" - "1h23m45s" or "12m12d22y")
     * - SEPARATOR_TYPE_ALPHA_CAPS ("H,M,S" - "1H23M45S" or "12M12D22Y")
     * - SEPARATOR_TYPE_ALPHA_WITH_SPACE ("h ,m ,s " - "1h 23m 45s" or "12m 12d 22y")
     * - SEPARATOR_TYPE_ALPHA_CAPS_WITH_SPACE ("H ,M ,S " - "1H 23M 45S" or "12M 12D 22Y")
     */
    private fun setupSeperatorOnKeypad() {
        fragmentDateNumberPadBinding?.headerBar?.getHeaderKeypadTextViewForDateTime()?.apply {
            HMILogHelper.Logd("Unboxing","isSetupForDate = $isSetupForDate")
            if (isSetupForDate) {
                setSeparatorType(KeypadTextView.SEPARATOR_TYPE_ALPHA_CAPS_WITH_SPACE)
                setUnderlinedSuffix(false)
                setSeparatorStyle(R.style.NumpadDateMMDDYYTextStyle)
            } else {
                setSeparatorType(KeypadTextView.SEPARATOR_TYPE_COLON_OR_BACKSLASH)
                setUnderlinedSuffix(true)
                setSuffixTextStyle(R.style.NumPadAmPmTextStyle)
            }

        }
    }


    /**
     * Method to set Header bar data.
     */
    private fun initNumPadHeaderBar() {
        fragmentDateNumberPadBinding?.headerBar?.apply {
            setTitleTextViewVisibility(false)
            setKeypadTextViewVisibility(false)
            setKeypadTextViewForDateTimeVisibility(true)
            setBackIconVisibility(true)
            setTumblerIconVisibility(true)
            setBackIconOnClickListener {
                //Header left icon click event
                onHeaderBarBackClick()
                if (isToolsSettingsFlow) {
                    // Tools: handled callback in SettingsSetDateNumpadFragment
                    return@setBackIconOnClickListener
                }
                if (isSetupForDate) {
                    NavigationViewModel.popBackStack(
                        NavigationUtils.getViewSafely(fragment)?.let {
                            Navigation.findNavController(
                                it
                            )
                        }
                    )
                } else {
                    //For smooth transition between fragment we have added navOption with anim parameter
                    val navOptions = NavOptions
                        .Builder()
                        .setEnterAnim(R.anim.fade_in)
                        .setExitAnim(R.anim.fade_out)
                        .build()
                    NavigationUtils.navigateSafely(
                        fragment,
                        R.id.action_unboxingSetupTimeFragment_to_unboxingRegionalSettingsFragment,
                        null,
                        navOptions
                    )
                }
            }
            setCancelIconOnClickListener {
                getKeyboardViewModel()?.onKey(
                    Keyboard.KEYCODE_BACKSPACE,
                    IntArray(0)
                )
            }
            setTumblerIconOnClickListener {
                onHeaderBarTumblerClick()
            }
        }
    }


    /**
     * Required to be able to retrieve localization strings in the other functions
     *
     * @return [Resources]
     */
    override fun provideResources(): Resources? {
        return fragmentDateNumberPadBinding?.root?.resources
    }

    /**
     * Next button
     * @return button View
     */
    override fun providePrimaryButton(): TextButton? {
        return fragmentDateNumberPadBinding?.textButtonRight
    }

    /**
     * Keyboard view to type the values
     * @return keyboard view
     */
    override fun provideKeyboardView(): KeyboardView? {
        return fragmentDateNumberPadBinding?.keyboardview
    }

    /**
     * Text view to show the typed value
     * @return view to show the type value
     */
    override fun provideDateTimeValueTextView(): KeypadTextView? {
        return fragmentDateNumberPadBinding?.headerBar?.getHeaderKeypadTextViewForDateTime()
    }

    override fun provideKeyboardReference(): Int {
        return R.xml.keyboard_numpad
    }

    /**
     * provide error text view
     * @return view to show the error
     */
    override fun provideErrorTextView(): TextView? {
        return fragmentDateNumberPadBinding?.textViewHelperText
    }

    /**
     * provide back arrow
     * @return view to show the backspace icon
     */
    override fun provideBackSpaceIcon(): View? {
        return fragmentDateNumberPadBinding?.headerBar?.getCancelView()
    }


    /**
     * Left navigation icon
     * @return view for back click
     */
    override fun provideBackButton(): View? {
        return fragmentDateNumberPadBinding?.headerBar?.getBackButtonView()
    }

    /**
     * To set the visibility of the entire radio group.
     *
     * @return [RadioGroup]
     */
    override fun provideRadioGroupFormatSelection(): RadioGroup? {
        return fragmentDateNumberPadBinding?.dateTimeFormatWidget?.formatSelection
    }

    /**
     * provideMMDDDateFormatSelection is a view that represents the selection of date format .
     *
     * @return [RadioButton]
     */
    override fun provideMMDDDateFormatSelection(): RadioButton? {
        return fragmentDateNumberPadBinding?.dateTimeFormatWidget?.leftFormatSelection
    }

    /**
     * provideDDMMDateFormatSelection is a view that represents the selection of date format .
     *
     * @return [RadioButton]
     */
    override fun provideDDMMDateFormatSelection(): RadioButton? {
        return fragmentDateNumberPadBinding?.dateTimeFormatWidget?.rightFormatSelection
    }

    override fun provide12HTimeFormatSelection(): RadioButton? {
        return fragmentDateNumberPadBinding?.dateTimeFormatWidget?.leftFormatSelection
    }

    override fun provide24HTimeFormatSelection(): RadioButton? {
        return fragmentDateNumberPadBinding?.dateTimeFormatWidget?.rightFormatSelection
    }

    override fun onDestroyView() {
        fragmentDateNumberPadBinding = null
    }

}

