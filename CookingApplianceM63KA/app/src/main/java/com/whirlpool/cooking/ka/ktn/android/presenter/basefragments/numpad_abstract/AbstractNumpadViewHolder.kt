package android.presenter.basefragments.numpad_abstract

import android.widget.ImageView
import android.widget.TextView
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment

/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */ /**
 * File       : java/com/whirlpool/cooking/ka/ktn/android/presenter/basefragments/numpad_abstract/AbstractNumpadViewHolder.kt
 * Brief      : extends with AbstractNumpadViewHolder
 * Author     : Gaurav Pete
 * Created On : 04-01-2024
 * Details    : AbstractNumpadViewHolder provides the common abstract funtions that could be used in feature implemented abstract class
 */
abstract class AbstractNumpadViewHolder : SuperAbstractTimeoutEnableFragment() {
    /*This abstract method is used to provides the keyboard view*/
    protected abstract fun providesKeyBoardView(): KeyboardView?

    /*This abstract method is used to provides the Temperature view*/
    protected abstract fun providesTemperatureTextView(): KeypadTextView?

    /*This abstract method is used to provides the ClockHours view*/
    protected abstract fun providesClockHoursTextView(): TextView?

    /*This abstract method is used to provides the ClockMinutes view*/
    protected abstract fun providesClockMinutesTextView(): TextView?

    /*This abstract method is used to provides the ClockSecond view*/
    protected abstract fun providesClockSecondTextView(): TextView?

    /*This abstract method is used to provides the ClockUnits view*/
    protected abstract fun providesClockUnitsTextView(): TextView?

    /*This abstract method is used to provides the KeypadDate view*/
    protected abstract fun providesKeypadDateTextView(): KeypadTextView?

    /*This abstract method is used to provides the ErrorHelperText view*/
    protected abstract fun providesErrorHelperTextView(): TextView?

    /*This abstract method is used to provides the BackSpaceImage view*/
    protected abstract fun providesBackSpaceImageView(): ImageView?

}
