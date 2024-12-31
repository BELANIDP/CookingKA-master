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
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardViewModel
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView

/**
 * The View Holder concept in general is that the ViewHolder class will define how to create its view and provide
 * specific widgets that the parent fragment will use to setOnClickListeners and update text, etc.
 * AbstractSetupLaterDateViewHolder is the view that presents a number pad to be used to enter the date when the customer
 * does not want to connect to the Wifi network during unboxing
 */
abstract class AbstractDateTimeViewHolder {
    private var keyboardViewModel: KeyboardViewModel? = null

    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     *
     * @param inflater           [LayoutInflater]
     * @param container          [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */
    abstract fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    /**
     * Optional - provides the ability to add custom behaviors after the view has been created
     *
     * @param view               [View]
     * @param savedInstanceState [Bundle]
     */
    open fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // no op
    }
    /**
     * The keyboard view model will be set to the view holder immediately prior to the fragment calling [.onViewCreated].
     * This is available to be able to dynamically update the keyboard content.
     *
     * @return [KeyboardViewModel]
     */
    fun getKeyboardViewModel(): KeyboardViewModel? {
        return keyboardViewModel
    }

    /**
     * @param keyboardViewModel [KeyboardViewModel]
     */
    fun setKeyboardViewModel(keyboardViewModel: KeyboardViewModel?) {
        this.keyboardViewModel = keyboardViewModel
    }
    /**
     * Provides the ability to clean up the view providers and bindings when it is destroyed
     */
    abstract fun onDestroyView()

    /**
     * Required to be able to retrieve localization strings in the other functions
     *
     * @return [Resources]
     */
    abstract fun provideResources(): Resources?


    /**
     * The primary button is the button that leads to the next navigation.
     *
     * @return [TextButton]
     */
    abstract fun providePrimaryButton(): TextButton?


    abstract fun provideBackSpaceIcon(): View?

    /**
     * The back button is the button that shows the icon on fragment and allows the user to pop back the fragment on click event.
     *
     * @return [View]
     */
    abstract fun provideBackButton(): View?



    /**
     * The Keyboard View is the view that all receives the input from the customer.
     *
     * @return [KeyboardView]
     */
    abstract fun provideKeyboardView(): KeyboardView?

    /**
     * The NumberValueTextView is the TextView that displays the current value (When setting time, it
     * would be the current hour and minute. When setting the date, it would be the calendar date.
     *
     * @return [KeypadTextView]
     */
    abstract fun provideDateTimeValueTextView(): KeypadTextView?

    /**
     * Optional - The keyboard view accepts a reference to an XML that defines the Keyboard layout. The XML file must be defined.
     *
     * @return int referencing the XML file
     */
    abstract fun provideKeyboardReference(): Int


    /**
     * Optional - When an validation errors are shown, we will use a separate text view to display the
     * error. We do this to more easily account for different text styles that may be used to display the validation
     * text.
     *
     *
     * The text view will be `invisible` to begin and then will be shown when the validation error has occurred.
     *
     * @return [TextView]
     */
    open fun provideErrorTextView(): TextView? {
        return null
    }


    /**
     * Required to set the text for [.providePrimaryButton]
     *
     * @return int reference to primary button text
     */
    fun providePrimaryButtonText(): Int {
        return R.string.text_button_next
    }

    /**
     * provide12HTimeFormatSelection is a view that represents the selection of time format .
     *
     * @return [RadioButton]
     */
    abstract fun provide12HTimeFormatSelection(): RadioButton?

    /**
     * provide24HTimeFormatSelection is a view that represents the selection of time format .
     *
     * @return [RadioButton]
     */
    abstract fun provide24HTimeFormatSelection(): RadioButton?
    /**
     * To set the visibility of the entire radio group.
     *
     * @return [RadioButton]
     */
    abstract fun provideRadioGroupFormatSelection(): RadioGroup?

    /**
     * provideMMDDDateFormatSelection is a view that represents the selection of date format .
     *
     * @return [RadioButton]
     */
    abstract fun provideMMDDDateFormatSelection(): RadioButton?

    /**
     * provideDDMMDateFormatSelection is a view that represents the selection of date format .
     *
     * @return [RadioButton]
     */
    abstract fun provideDDMMDateFormatSelection(): RadioButton?
}
