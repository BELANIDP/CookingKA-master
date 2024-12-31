package android.presenter.customviews.radiobutton

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.LayoutRadiobuttonBinding

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.radiobutton
 *
 * Brief       : Radio button widget.
 *
 * Author      : PATELJ7
 *
 * Created On  : 12-02-2024
 */
class RadioButton : ConstraintLayout, View.OnClickListener {
    private var layoutRadiobuttonBinding: LayoutRadiobuttonBinding
    private var isChecked = false
    private var isEnabled = false
    private var listItemClickListener: ListItemClickListener? = null

    /**
     * Constructor for creating Radiobutton programmatically.
     *
     * @param context The context in which the radiobutton is created.
     */
    constructor(context: Context?) : super(context!!) {
        layoutRadiobuttonBinding =
            LayoutRadiobuttonBinding.inflate(LayoutInflater.from(context), this, true)
    }

    /**
     * Constructor for creating Radiobutton from XML layout.
     *
     * @param context The context in which the radiobutton is created.
     * @param attrs   The attributes set defined in XML.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        layoutRadiobuttonBinding =
            LayoutRadiobuttonBinding.inflate(LayoutInflater.from(context), this, true)
        initAttrs(context, attrs)
        initView()
    }

    /**
     * Initializes attributes from XML.
     *
     * @param context      The context.
     * @param attributeSet The set of attributes defined in XML.
     */
    private fun initAttrs(context: Context, attributeSet: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.RadioButton)
        isChecked = ta.getBoolean(R.styleable.RadioButton_isChecked, false)
        isEnabled = ta.getBoolean(R.styleable.RadioButton_isEnabled, false)
        ta.recycle()
    }

    /**
     * Initializes the views and sets up the click listeners.
     */
    private fun initView() {
        setRadioButtonView()
        layoutRadiobuttonBinding.mainRadioButtonView.setOnClickListener(this)
    }

    /**
     * Updates the radiobutton view based on the current checked and enabled state.
     */
    private fun setRadioButtonView() {
        if (isChecked) {
            if (isEnabled) {
                layoutRadiobuttonBinding.imageViewRadioButtonInnerRing.visibility = VISIBLE
                layoutRadiobuttonBinding.imageViewRadioButtonOuterRing.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.selector_radio_button_selected_outer_ring, null
                    )
                )
                layoutRadiobuttonBinding.imageViewRadioButtonInnerRing.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.selector_radio_button_selected_inner_ring, null
                    )
                )
            } else {
                layoutRadiobuttonBinding.imageViewRadioButtonInnerRing.visibility = VISIBLE
                layoutRadiobuttonBinding.imageViewRadioButtonOuterRing.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.selector_radio_button_disable_outer_ring, null
                    )
                )
                layoutRadiobuttonBinding.imageViewRadioButtonInnerRing.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.selector_radio_button_disable_inner_ring, null
                    )
                )
            }
        } else {
            if (isEnabled) {
                layoutRadiobuttonBinding.imageViewRadioButtonOuterRing.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.selector_radio_button_selected_outer_ring, null
                    )
                )
                layoutRadiobuttonBinding.imageViewRadioButtonInnerRing.visibility = GONE
            } else {
                layoutRadiobuttonBinding.imageViewRadioButtonOuterRing.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.selector_radio_button_disable_outer_ring, null
                    )
                )
                layoutRadiobuttonBinding.imageViewRadioButtonInnerRing.visibility = GONE
            }
        }
    }

    /**
     * Returns the current checked state of the radiobutton.
     *
     * @return True if checked, false otherwise.
     */
    fun isChecked(): Boolean {
        return isChecked
    }

    /**
     * Returns the current enable state of the radiobutton.
     *
     * @return True if enabled, false otherwise.
     */
    override fun isEnabled(): Boolean {
        return isEnabled
    }

    /**
     * Sets the checked state of the radiobutton.
     *
     * @param checked True to set as checked, false to set as unchecked.
     */
    fun setChecked(checked: Boolean) {
        isChecked = checked
        setRadioButtonView()
    }

    /**
     * Sets the enabled state of the radiobutton.
     *
     * @param enable True to set as enabled, false to set as disable.
     */
    override fun setEnabled(enable: Boolean) {
        isEnabled = enable
        setRadioButtonView()
    }

    /**
     * Handles click events on the radiobutton.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View) {
        if (isEnabled) {
            setChecked(!isChecked)
            setRadioButtonView()
            listItemClickListener?.onToggleSwitchClick(isChecked)
        }
    }

    /**
     * Click Listener for List tiles
     */
    interface ListItemClickListener {

        /**
         * Listener method which is called on List tile toggle switch click
         *
         * @param isChecked true - clicked toggle switch is checked
         * false - clicked toggle switch is not checked
         */
        fun  onToggleSwitchClick(isChecked: Boolean){}
    }
    fun setRadioItemClickListener( listItemClickListener: ListItemClickListener){
        this.listItemClickListener = listItemClickListener

    }
}