/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.viewHolderHelpers

import android.os.Bundle
import android.presenter.customviews.textButton.TextButton
import android.presenter.customviews.widgets.headerBarNumpad.HeaderBarNumPadWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.FragmentTemperatureFieldNumberpadBinding
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import core.jbase.abstractViewHolders.AbstractTemperatureNumberPadViewHolder

/**
 * File        : core.viewHolderHelpers.TemperatureNumberPadViewHolderHelper.
 * Brief       : Cook time NumberPad view holder responsible for holding all views and provided when required
 * Author      : GHARDNS/Nikki
 * Created On  : 04-04-2024
 */
class TemperatureNumberPadViewHolderHelper : AbstractTemperatureNumberPadViewHolder() {

    /** To binding Fragment variables */
    private var fragmentTemperatureNumberPadBinding: FragmentTemperatureFieldNumberpadBinding? =
        null

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
        fragmentTemperatureNumberPadBinding =
            inflater?.let { FragmentTemperatureFieldNumberpadBinding.inflate(it, container, false) }
        return fragmentTemperatureNumberPadBinding?.root
    }

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    override fun onDestroyView() {
        fragmentTemperatureNumberPadBinding = null
    }

    /**
     * Provides the interface to access the binding class
     *
     * @return [FragmentTemperatureFieldNumberpadBinding]
     */
    override fun getBinding(): FragmentTemperatureFieldNumberpadBinding? =
        fragmentTemperatureNumberPadBinding

    /**
     * Provides the interface to access bottom left button
     *
     * @return [TextButton]
     */
    override fun getLeftConstraint(): ConstraintLayout? =
        fragmentTemperatureNumberPadBinding?.constraintNumberPadLeft

    /**
     * Provides the interface to access bottom left constraint
     *
     * @return [ConstraintLayout]
     */
    override fun getLeftTextButton(): TextButton? =
        fragmentTemperatureNumberPadBinding?.cookTimeTextButtonLeft

    /**
     * Provides the interface to access bottom right button
     *
     * @return [TextButton]
     */
    override fun getRightTextButton(): TextButton? =
        fragmentTemperatureNumberPadBinding?.cookTimeTextButtonRight
    /**
     * Provides the interface to access bottom center button
     *
     * @return [TextButton]
     */
    override fun getRightConstraint(): ConstraintLayout? =
        fragmentTemperatureNumberPadBinding?.constraintNumberPadRight
    /**
     * Provides the interface to access right constraint
     *
     * @return [ConstraintLayout]
     */
    override fun getMiddleTextButton(): TextButton? =
        fragmentTemperatureNumberPadBinding?.cookTimeTextButtonMiddle

    /**
     * Provides the interface to access KeyboardView widget
     *
     * @return [KeyboardView]
     */
    override fun getKeyboardView(): KeyboardView? =
        fragmentTemperatureNumberPadBinding?.keyboardview

    /**
     * Provides the interface to access error helper text
     *
     * @return [KeyboardView]
     */
    override fun getErrorHelperTextView(): TextView? =
        fragmentTemperatureNumberPadBinding?.textViewHelperText

    /**
     * Provides the interface to access HeaderBar widget
     *
     * @return [HeaderBarNumPadWidget]
     */
    override fun getHeaderBarNumPadWidget(): HeaderBarNumPadWidget? =
        fragmentTemperatureNumberPadBinding?.headerBar
}