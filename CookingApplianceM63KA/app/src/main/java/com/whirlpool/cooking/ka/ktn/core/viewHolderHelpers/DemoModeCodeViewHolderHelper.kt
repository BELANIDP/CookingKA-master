/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.viewHolderHelpers

import android.os.Bundle
import android.presenter.customviews.textButton.TextButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.whirlpool.cooking.ka.databinding.FragmentDemoCodeFieldNumberpadBinding
import android.presenter.customviews.widgets.headerBarNumpad.HeaderBarNumPadWidget
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import core.jbase.abstractViewHolders.AbstractDemoModeCodeViewHolder

/**
 * File        : core.viewHolderHelpers.CookTimeNumberPadViewHolderHelper.
 * Brief       : Cook time NumberPad view holder responsible for holding all views and provided when required
 * Author      : GHARDNS/Nikki
 * Created On  : 18-03-2024
 */
class DemoModeCodeViewHolderHelper : AbstractDemoModeCodeViewHolder() {

    /** To binding Fragment variables */
    private var fragmentCookTimeNumberPadBinding: FragmentDemoCodeFieldNumberpadBinding? = null

    /**
     * Inflate the customized view
     *
     * @param inflater           [LayoutInflater]
     * @param container          [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */
    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentCookTimeNumberPadBinding =
            inflater?.let { FragmentDemoCodeFieldNumberpadBinding.inflate(it, container, false) }
        return fragmentCookTimeNumberPadBinding?.root
    }

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    override fun onDestroyView() {
        fragmentCookTimeNumberPadBinding = null
    }

    /**
     * Provides the interface to access the binding class
     *
     * @return [FragmentDemoCodeFieldNumberpadBinding]
     */
    override fun getBinding(): FragmentDemoCodeFieldNumberpadBinding? =
        fragmentCookTimeNumberPadBinding

    /**
     * Provides the interface to access bottom left button
     *
     * @return [TextButton]
     */
    override fun getLeftTextButton(): TextButton? =
        fragmentCookTimeNumberPadBinding?.demoCodeTextButtonLeft

    /**
     * Provides the interface to access bottom right button
     *
     * @return [TextButton]
     */
    override fun getRightTextButton(): TextButton? =
        fragmentCookTimeNumberPadBinding?.demoCodeTextButtonRight

    /**
     * Provides the interface to access bottom center button
     *
     * @return [TextButton]
     */
    override fun getMiddleTextButton(): TextButton? =
        fragmentCookTimeNumberPadBinding?.demoCodeTextButtonMiddle

    /**
     * Provides the interface to access bottom left power button
     *
     * @return [TextButton]
     */
    override fun getLeftPowerTextButton(): TextButton? =
        fragmentCookTimeNumberPadBinding?.demoCodeTextButtonLeftPower

    /**
     * Provides the interface to access KeyboardView widget
     *
     * @return [KeyboardView]
     */
    override fun getKeyboardView(): KeyboardView? = fragmentCookTimeNumberPadBinding?.keyboardview

    /**
     * Provides the interface to access error helper text
     *
     * @return [KeyboardView]
     */
    override fun getErrorHelperTextView(): TextView? =
        fragmentCookTimeNumberPadBinding?.textViewHelperText

    /**
     * Provides the interface to access HeaderBar widget
     *
     * @return [HeaderBarNumPadWidget]
     */
    override fun getHeaderBarNumPadWidget(): HeaderBarNumPadWidget? =
        fragmentCookTimeNumberPadBinding?.headerBar
}