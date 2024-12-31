/***Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL***/
@file:Suppress("unused")

package android.presenter.customviews.widgets.headerBarNumpad

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.HeaderBarNumpadBinding
import android.view.View
import android.widget.ImageView
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView

/*
 * File : android.presenter.customviews.widgets.headerBarNumpad.HeaderBarNumPadWidget
 * Author : SINGHA80.
 * Created On : 3/21/24
 * Details : Creates HeaderBarNumpadWidget view and provides methods for the usage.
 */
class HeaderBarNumPadWidget : ConstraintLayout {

    private var contextRef: Context
    private lateinit var binding: HeaderBarNumpadBinding
    private lateinit var headerBarNumPadInterface: HeaderBarNumPadInterface


    constructor(context: Context) : super(context) {
        this.contextRef = context
        inflateLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.contextRef = context
        inflateLayout()
    }

    /**
     * inflate HeaderBarNumpadBinding layout
     *
     */
    private fun inflateLayout() {
        binding = HeaderBarNumpadBinding.inflate(LayoutInflater.from(context), this, true)
        initHeaderBarNumPadImpl()
    }

    /**
     * instantiate HeaderBarNumPadInterface
     *
     */
    private fun initHeaderBarNumPadImpl() {
        headerBarNumPadInterface = HeaderBarNumPadImpl(binding)
    }

    /**
     * set tumbler icon visibility visible/gone visible/gone
     * @param isVisible true/false
     */

    fun setTumblerIconVisibility(isVisible: Boolean) {
        headerBarNumPadInterface.setTumblerIconVisibility(isVisible)
    }

    /**
     * set title textview visibility visible/gone visible/gone
     * @param isVisible true/false
     */

    fun setTitleTextViewVisibility(isVisible: Boolean) {
        headerBarNumPadInterface.setTitleTextViewVisibility(isVisible)
    }

    /**
     * set keypad textview visibility visible/gone visible/gone
     * @param isVisible true/false
     */

    fun setKeypadTextViewVisibility(isVisible: Boolean) {
        headerBarNumPadInterface.setKeypadTextViewVisibility(isVisible)
    }

    /**
     * set keypad textview for date time visibility visible/gone
     * @param isVisible true/false
     */

    fun setKeypadTextViewForDateTimeVisibility(isVisible: Boolean) {
        headerBarNumPadInterface.setKeypadTextViewForDateTimeVisibility(isVisible)
    }

    /**
     * set cancel icon visibility visible/gone visible/gone
     * @param isVisible true/false
     */
    fun setCancelIconVisibility(isVisible: Boolean) {
        headerBarNumPadInterface.setCancelIconVisibility(isVisible)
    }

    /**
     * set back icon visibility visible/gone visible/gone
     * @param isVisible true/false
     */
    fun setBackIconVisibility(isVisible: Boolean) {
        headerBarNumPadInterface.setBackIconVisibility(isVisible)
    }

    /**
     * set onClick on back icon
     * @param onBackIconClick function
     */
    fun setBackIconOnClickListener(onBackIconClick: () -> Unit) {
        headerBarNumPadInterface.setBackIconOnClick(onBackIconClick)
    }

    /**
     * set onClick on tumbler icon
     * @param onTumblerIconClick function
     */
    fun setTumblerIconOnClickListener(onTumblerIconClick: () -> Unit) {
        headerBarNumPadInterface.setTumblerIconOnClick(onTumblerIconClick)
    }

    /**
     * set onClick on cancel icon
     * @param onCancelIconClick function
     */
    fun setCancelIconOnClickListener(onCancelIconClick: () -> Unit) {
        headerBarNumPadInterface.setCancelIconOnClick(onCancelIconClick)
    }

    /**
     * @return KeypadTextView instance
     */
    fun getTextViewCookTime(): TextView? {
        return headerBarNumPadInterface.getTvCookTime()
    }

    /**
     * @return KeypadTextView instance
     */
    fun getHeaderKeypadTextView(): KeypadTextView? {
        return headerBarNumPadInterface.getHeaderKeypadTextView()
    }
    /**
     * @return KeypadTextView instance
     */
    fun getHeaderKeypadTextViewForDateTime(): KeypadTextView? {
        return headerBarNumPadInterface.getHeaderKeypadTextViewForDateTime()
    }

    /**
     * @return KeypadTextView instance
     */
    fun getCancelView(): ImageView? {
        return headerBarNumPadInterface.getCancelView()
    }


    /**
     * @return KeypadTextView instance
     */
    fun getBackButtonView(): View? {
        return headerBarNumPadInterface.getBackButtonView()
    }

    /**
     * sets cancel icon enable/disable
     * @param isEnable true/false
     */
    fun setCancelIconEnable(isEnable: Boolean) {
        headerBarNumPadInterface.setCancelIconEnable(isEnable)
    }

    /**
     * sets tumbler icon enable/disable
     * @param isEnable true/false
     */
    fun setTumblerIconEnable(isEnable: Boolean) {
        headerBarNumPadInterface.setTumblerIconEnable(isEnable)

    }

    /**
     * clears the resources used
     */
    fun clearResources() {
        headerBarNumPadInterface.clearResources()
    }

    fun setTumblerIcon(drawable: Drawable) {
        binding.ivTumblerIcon.setImageDrawable(drawable)
    }
}