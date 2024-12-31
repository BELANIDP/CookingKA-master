/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */

package android.presenter.customviews.checkbox

import android.content.Context
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.LayoutCheckboxBinding


/**
 * File       : android.presenter.customviews.checkbox
 * Brief      : Checkbox widget for Decision CVT
 * Author     : PARMAM
 * Created On : 03/02/2024
 * Details    : This custom view allows drawing and animating a checkbox. It uses data binding for view initialization.
 * The checkbox can be clicked to toggle between checked and unchecked states, with an animated transition.
 * Customizable text can be displayed alongside the checkbox.
 */
class CheckBox : ConstraintLayout, Checkable, View.OnClickListener {

    private val binding: LayoutCheckboxBinding
    private var checkBoxText: String? = ""
    private var isChecked = false

    /**
     * Constructor for creating CheckBox programmatically.
     *
     * @param context The context in which the checkbox is created.
     */
    constructor(context: Context) : super(context) {
        binding = LayoutCheckboxBinding.inflate(LayoutInflater.from(context), this, true)
        initView()
    }

    /**
     * Constructor for creating CheckBox from XML layout.
     *
     * @param context The context in which the checkbox is created.
     * @param attrs   The attributes set defined in XML.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        binding = LayoutCheckboxBinding.inflate(LayoutInflater.from(context), this, true)
        initAttrs(context, attrs)
        initView()
    }

    /**
     * Initializes the views and sets up the click listeners.
     */
    private fun initView() {
        if (checkBoxText?.isNotEmpty() == true) {
            binding.textViewCheckBox.text = checkBoxText
        }
        if (checkBoxText?.isEmpty() == true) {
            binding.textViewCheckBox.text = resources.getString(R.string.text_button_dont_show_again)
        }
        setCheckBoxView()
        binding.imageViewCheckBox.setOnClickListener(this)
        binding.textViewCheckBox.setOnClickListener(this)
    }

    /**
     * Handles click events on the checkbox or its associated text view.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        setChecked(!isChecked)
        setCheckBoxView()
    }

    /**
     * Initializes attributes from XML.
     *
     * @param context      The context.
     * @param attributeSet The set of attributes defined in XML.
     */
    private fun initAttrs(context: Context, attributeSet: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.CheckBox)
        checkBoxText = ta.getString(R.styleable.CheckBox_checkbox_text) ?: ""
        isChecked = ta.getBoolean(R.styleable.CheckBox_checkbox_checked, false)
        ta.recycle()
    }

    /**
     * Updates the checkbox view based on the current checked state.
     */
    private fun setCheckBoxView() {
        if (isChecked()) {
            binding.imageViewCheckBox.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.animated_vector_checkbox_checked,
                    null
                )
            )
        } else {
            binding.imageViewCheckBox.setImageDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.animated_vector_checkbox_unchecked,
                    null
                )
            )
        }
        (binding.imageViewCheckBox.drawable as Animatable).start()
    }

    /**
     * Returns the current checked state of the checkbox.
     *
     * @return True if checked, false otherwise.
     */
    override fun isChecked(): Boolean {
        return isChecked
    }

    /**
     * Sets the checked state of the checkbox.
     *
     * @param checked True to set as checked, false to set as unchecked.
     */
    override fun setChecked(checked: Boolean) {
        isChecked = checked
        setCheckBoxView()
    }

    /**
     * Toggles between checked and unchecked states.
     */
    override fun toggle() {
        // Not implemented for this custom checkbox
    }

    /**
     * Sets the text to be displayed with the checkbox.
     *
     * @param textId The resource ID of the text to be set.
     */
    @Suppress("unused")
    fun setCheckBoxText(@StringRes textId: Int) {
        binding.textViewCheckBox.setText(textId)
    }

    /**
     * Sets the text to be displayed with the checkbox.
     *
     * @param text The value of the text to be set.
     */
    @Suppress("unused")
    fun setCheckBoxText(text: String) {
        binding.textViewCheckBox.text = text
    }
}
