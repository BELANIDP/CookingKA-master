package core.jbase.abstractViewHolders

import android.os.Bundle
import android.presenter.customviews.textButton.TextButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.whirlpool.cooking.ka.databinding.FragmentCookTimeFieldNumberpadBinding
import android.presenter.customviews.widgets.headerBarNumpad.HeaderBarNumPadWidget
import com.whirlpool.cooking.ka.databinding.FragmentDemoCodeFieldNumberpadBinding
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView


abstract class AbstractDemoModeCodeViewHolder {
    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================
    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     *
     * @param inflater [LayoutInflater]
     * @param container [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */
    abstract fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    abstract fun onDestroyView()
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access the binding class
     *
     * @return [FragmentDemoCodeFieldNumberpadBinding]
     */
    abstract fun getBinding(): FragmentDemoCodeFieldNumberpadBinding?

    /**
     * Provides the interface to access bottom left button
     *
     * @return [TextButton]
     */
    abstract fun getLeftTextButton(): TextButton?
    /*---------------------------------------------------------------------------------------------------------------*/ /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access bottom right button
     *
     * @return [TextButton]
     */
    abstract fun getRightTextButton(): TextButton?

    /**
     * Provides the interface to access bottom center button
     *
     * @return [TextButton]
     */
    abstract fun getMiddleTextButton(): TextButton?

    /**
     * Provides the interface to access bottom left power button
     *
     * @return [TextButton]
     */
    abstract fun getLeftPowerTextButton(): TextButton?

    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access KeyboardView widget
     *
     * @return [KeyboardView]
     */
    abstract fun getKeyboardView(): KeyboardView?


    /**
     * Provides the interface to access error helper text
     *
     * @return [KeyboardView]
     */
    abstract fun getErrorHelperTextView(): TextView?

    /**
     * Provides the interface to access HeaderBar widget
     *
     * @return [HeaderBarNumPadWidget]
     */
    abstract fun getHeaderBarNumPadWidget(): HeaderBarNumPadWidget?

    /*---------------------------------------------------X---X---X---------------------------------------------------*/
}
