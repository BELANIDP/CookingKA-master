/***Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL***/
package android.presenter.customviews.widgets.headerBarNumpad

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView

/*
 *
 * File : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.widgets.headerBarNumpad.HeaderBarNumPadInterface
 * Author : SINGHA80.
 * Created On : 3/21/24
 * Details : Provides abstract methods of the functionalities
 *
 */

interface HeaderBarNumPadInterface {
    fun setBackIconOnClick(onBackIconClick: () -> Unit)
    fun setTumblerIconOnClick(onTumblerIconClick: () -> Unit)
    fun setCancelIconOnClick(onCancelIconClick: () -> Unit)
    fun setTumblerIconVisibility(isVisible: Boolean)
    fun setTitleTextViewVisibility(isVisible: Boolean)
    fun setKeypadTextViewVisibility(isVisible: Boolean)
    fun setKeypadTextViewForDateTimeVisibility(isVisible: Boolean)
    fun setCancelIconVisibility(isVisible: Boolean)
    fun setBackIconVisibility(isVisible: Boolean)
    fun setCancelIconEnable(isEnable: Boolean)
    fun setTumblerIconEnable(isEnable: Boolean)
    fun getTvCookTime(): TextView?
    fun getHeaderKeypadTextView(): KeypadTextView?
    fun getHeaderKeypadTextViewForDateTime(): KeypadTextView?
    fun getCancelView(): ImageView?
    fun getBackButtonView(): View?
    fun clearResources()
}