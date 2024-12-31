package android.presenter.customviews.radiobutton

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.LayoutSwitchWidgetLineBinding


/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.UnderLineButton
 *
 * Brief       : Underline button widget.
 *
 * Author      : Nikki
 *
 * Created On  : 06-sep-2024
 */
class UnderLineButton : ConstraintLayout, View.OnClickListener {
    private var context:Context? = null
    private var switchWidgetLineBinding: LayoutSwitchWidgetLineBinding
    private var isChecked = false
    private var isEnabled = false
    private var listItemClickListener: ListItemClickListener? = null

    /**
     * Constructor for creating Radiobutton programmatically.
     *
     * @param context The context in which the radiobutton is created.
     */
    constructor(context: Context?) : super(context!!) {
        this.context = context
        switchWidgetLineBinding =  LayoutSwitchWidgetLineBinding.inflate(LayoutInflater.from(context), this, true)
    }

    /**
     * Constructor for creating Radiobutton from XML layout.
     *
     * @param context The context in which the radiobutton is created.
     * @param attrs   The attributes set defined in XML.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.context = context
        switchWidgetLineBinding = LayoutSwitchWidgetLineBinding.inflate(LayoutInflater.from(context), this, true)
        initAttrs(context, attrs)
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?,defStyle:Int) :
        super(
            context,
            attrs,
            defStyle
        ){
        this.context = context
        switchWidgetLineBinding = LayoutSwitchWidgetLineBinding.inflate(LayoutInflater.from(context), this, true)
        initAttrs(context, attrs)
        initView()
        }

    /**
     * Initializes attributes from XML.
     *
     * @param context      The context.
     * @param attributeSet The set of attributes defined in XML.
     */
    @SuppressLint("CustomViewStyleable")
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
        setUnderLineButtonView()
        switchWidgetLineBinding.toggleSwitchOn.setOnClickListener(this)
        switchWidgetLineBinding.toggleSwitchOff.setOnClickListener(this)
    }

    /**
     * Updates the underline view based on the current checked and enabled state.
     */
    private fun setUnderLineButtonView() {
        if (isChecked) {

            TextViewCompat.setTextAppearance(switchWidgetLineBinding.toggleSwitchOn, R.style.RegionalToggleSwitchNormalStyle)
            TextViewCompat.setTextAppearance(switchWidgetLineBinding.toggleSwitchOff, R.style.RegionalToggleSwitchDisableStyle)
            //Switch on drawable
            val switchOn = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_toggle_switch_line_on) }
            switchWidgetLineBinding.toggleSwitchOn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, switchOn)

            //Switch off drawable
            val switchOff = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_toggle_switch_line_off) }
            switchWidgetLineBinding.toggleSwitchOff.setCompoundDrawablesWithIntrinsicBounds(null, null, null, switchOff)
        } else {

            TextViewCompat.setTextAppearance(switchWidgetLineBinding.toggleSwitchOff, R.style.RegionalToggleSwitchNormalStyle)
            TextViewCompat.setTextAppearance(switchWidgetLineBinding.toggleSwitchOn, R.style.RegionalToggleSwitchDisableStyle)

            //Switch on drawable
            val switchOn = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_toggle_switch_line_on) }
            switchWidgetLineBinding.toggleSwitchOff.setCompoundDrawablesWithIntrinsicBounds(null, null, null, switchOn)

            //Switch off drawable
            val switchOff = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_toggle_switch_line_off) }
            switchWidgetLineBinding.toggleSwitchOn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, switchOff)
        }
        switchWidgetLineBinding.toggleSwitchOn.forceLayout()
        switchWidgetLineBinding.toggleSwitchOff.forceLayout()
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
        setUnderLineButtonView()
    }

    /**
     * Sets the enabled state of the radiobutton.
     *
     * @param enable True to set as enabled, false to set as disable.
     */
    override fun setEnabled(enable: Boolean) {
        isEnabled = enable
        setUnderLineButtonView()
    }

    /**
     * Handles click events on the radiobutton.
     *
     * @param view The view that was clicked.
     */
    override fun onClick(view: View) {
        when (view.id) {
            R.id.toggle_switch_on -> {
                setChecked(checked = true)
                listItemClickListener?.onToggleSwitchClick(isChecked)
                setUnderLineButtonView()
            }

            R.id.toggle_switch_off -> {
                setChecked(checked = false)
                listItemClickListener?.onToggleSwitchClick(isChecked)
                setUnderLineButtonView()
            }
        }
    }

    fun getLeftTextView() = switchWidgetLineBinding.toggleSwitchOn
    fun getRightTextView() = switchWidgetLineBinding.toggleSwitchOff
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
        fun onToggleSwitchClick(isChecked: Boolean) {}
    }

    fun setUnderLineItemClickListener(listItemClickListener: ListItemClickListener) {
        this.listItemClickListener = listItemClickListener

    }
}