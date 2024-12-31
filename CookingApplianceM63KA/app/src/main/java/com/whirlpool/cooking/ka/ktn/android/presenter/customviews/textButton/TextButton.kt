package android.presenter.customviews.textButton

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.LayoutTextButtonBinding


/**
 * The `TextButton` class is a custom view that extends `ConstraintLayout` and represents a text button
 * with optional bottom view. It provides methods to customize the appearance of the button, including
 * text, text color, and visibility of a bottom view.
 *
 * Usage:
 * Include TextButton in your layout XML -->
 * ```xml
 * <com.yourpackage.TextButton
 *     android:id="@+id/textButton"
 *     android:layout_width="wrap_content"
 *     android:layout_height="wrap_content"
 *     app:button_text="Your Button Text"
 *     app:enable="true"
 *     app:is_bottom_view_visible="true" />
 * ```
 *
 * You can then reference the `TextButton` in your code and customize its properties:
 * ```kotlin
 * val textButton = findViewById<TextButton>(R.id.textButton)
 * textButton.isEnabled = true
 * textButton.setTextButtonText("New Text")
 * ```
 *
 * @version 1.0
 * @author PARMAM
 * @since 05/02/2024
 */
class TextButton : ConstraintLayout {

    @Suppress("PrivatePropertyName")
    private val UNKNOWN_VALUE: Int = -1

    private val binding: LayoutTextButtonBinding
    private var textButtonText: String? = ""
    private var textButtonTextStringResource: Int = UNKNOWN_VALUE
    private var isBottomViewVisible = false
    private var textButtonTextSize: Float = 32f
    private var buttonFontFamilyResId: Int = UNKNOWN_VALUE
    private var buttonTextColor = R.color.color_white

    /**
     * Constructs a new TextButton.
     *
     * @param context The context in which the TextButton is created.
     */
    constructor(context: Context) : super(context) {
        binding = LayoutTextButtonBinding.inflate(LayoutInflater.from(context), this, true)
        setButtonView()
    }

    /**
     * Constructs a new TextButton with attributes.
     *
     * @param context The context in which the TextButton is created.
     * @param attrs   The attribute set containing the attributes of the TextButton.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        binding = LayoutTextButtonBinding.inflate(LayoutInflater.from(context), this, true)
        initAttrs(context, attrs)
        setButtonView()
    }

    /**
     * Sets up the appearance of the TextButton based on its current properties.
     * This includes setting text, text color, and visibility of the bottom view.
     */
    private fun setButtonView() {
        // Set text if available

        // Set text if available
        if (textButtonTextStringResource != UNKNOWN_VALUE) {
            textButtonText = resources.getString(textButtonTextStringResource)
        }
        if (textButtonText?.isNotEmpty() == true) {
            binding.textViewTextButton.text = textButtonText
        }

        // Set text color based on the enabled state
        if (isEnabled) {
            // Use solid white text color when the button is enabled
            binding.textViewTextButton.setTextColor(
                ContextCompat.getColor(
                    binding.root.context, R.color.solid_white
                )
            )
        } else {
            // Use disabled grey text color when the button is disabled
            binding.textViewTextButton.setTextColor(
                ContextCompat.getColor(
                    binding.root.context, R.color.text_button_disabled_grey
                )
            )
        }

        // Set bottom view visibility and color based on properties
        if (isBottomViewVisible) {
            // Show background with bottom view
            binding.layoutTextButton.setBackgroundResource(R.drawable.bg_text_button)
            var color = ContextCompat.getColor(binding.root.context, R.color.walnut)
            var colorStateList = ColorStateList.valueOf(color)
            // Set bottom view color based on the enabled state
            if (!isEnabled) {
                color =
                    ContextCompat.getColor(binding.root.context, R.color.text_button_disabled_grey)
                colorStateList = ColorStateList.valueOf(color)
            }
            binding.layoutTextButton.backgroundTintList = colorStateList
        } else {
            // Remove background
            binding.layoutTextButton.setBackgroundResource(0)
        }


        /*
        * In Android, when you set the text size directly as a floating-point value (e.g., 32f),
        * it's interpreted as pixels (px) by default, not SP (scale-independent pixels).
        *
        * To explicitly specify the size in SP, you need to convert it to pixels using the TypedValue class.
        *
        * Below conversion snippet will set the text size of textViewTextButton to SP.
         * */

        /*
        * In Android, when you set the text size directly as a floating-point value (e.g., 32f),
        * it's interpreted as pixels (px) by default, not SP (scale-independent pixels).
        *
        * To explicitly specify the size in SP, you need to convert it to pixels using the TypedValue class.
        *
        * Below conversion snippet will set the text size of textViewTextButton to SP.
         * */
        val textSizeInSp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, textButtonTextSize, resources.displayMetrics
        )
        binding.textViewTextButton.textSize = textSizeInSp

        //Set Font family
        if (buttonFontFamilyResId != UNKNOWN_VALUE) {
            val typeface = ResourcesCompat.getFont(context, buttonFontFamilyResId)
            binding.textViewTextButton.typeface = typeface
        }
        //Set Font color

        //Set Font color
        if (buttonTextColor != UNKNOWN_VALUE) {
            val color = ResourcesCompat.getColor(resources, buttonTextColor, null)
            binding.textViewTextButton.setTextColor(color)
        }
    }


    @SuppressLint("ResourceAsColor")
    private fun initAttrs(context: Context, attributeSet: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.TextButton)
        textButtonText = ta.getString(R.styleable.TextButton_button_text) ?: ""
        super.setEnabled(ta.getBoolean(R.styleable.TextButton_enable, false))
        isBottomViewVisible = ta.getBoolean(R.styleable.TextButton_is_bottom_view_visible, false)
        textButtonTextSize =
            ta.getFloat(R.styleable.TextButton_button_text_size, textButtonTextSize)
        buttonFontFamilyResId =
            ta.getResourceId(R.styleable.TextButton_button_font_family, UNKNOWN_VALUE)
        buttonTextColor = ta.getColor(R.styleable.TextButton_button_text_color, UNKNOWN_VALUE)
        ta.recycle()
    }

    /**
     * Gets the current text of the TextButton.
     *
     * @return The text of the TextButton.
     */
    fun getTextButtonText(): String? {
        return textButtonText
    }

    /**
     * Sets the text of the TextButton and updates its appearance.
     *
     * @param textButtonText The new text for the TextButton.
     */
    fun setTextButtonText(textButtonText: String?) {
        this.textButtonText = textButtonText
        setButtonView()
    }

    /**
     * Sets the text of the TextButton and updates its appearance.
     *
     * @param textButtonTextStringResource The new text for the TextButton from string resource.
     */
    fun setTextButtonText(@StringRes textButtonTextStringResource: Int) {
        this.textButtonTextStringResource = textButtonTextStringResource
        setButtonView()
    }

    /**
     * Checks if the bottom view of the TextButton is currently visible.
     *
     * @return True if the bottom view is visible, false otherwise.
     */
    @Suppress("unused")
    fun isBottomViewVisible(): Boolean {
        return isBottomViewVisible
    }

    /**
     * Sets the visibility of the bottom view of the TextButton and updates its appearance.
     *
     * @param bottomViewVisible True to make the bottom view visible, false to hide it.
     */
    fun setBottomViewVisible(bottomViewVisible: Boolean) {
        isBottomViewVisible = bottomViewVisible
        setButtonView()
    }

    /**
     * Sets the visibility of the bottom view of the TextButton and updates its appearance.
     *
     * @param bottomViewVisible True to make the bottom view visible, false to hide it.
     */
    fun setBottomButtonViewVisible(bottomViewVisible: Boolean) {
        // Set bottom view visibility and color based on properties
        if (bottomViewVisible) {
            // Show background with bottom view
            binding.layoutTextButton.setBackgroundResource(R.drawable.bg_text_button)
            var color = ContextCompat.getColor(binding.root.context, R.color.walnut)
            var colorStateList = ColorStateList.valueOf(color)
            // Set bottom view color based on the enabled state
            if (!isEnabled) {
                color =
                    ContextCompat.getColor(binding.root.context, R.color.text_button_disabled_grey)
                colorStateList = ColorStateList.valueOf(color)
            }
            binding.layoutTextButton.backgroundTintList = colorStateList
        } else {
            // Remove background
            binding.layoutTextButton.setBackgroundResource(0)
        }
        //Set Font family
        if (buttonFontFamilyResId != UNKNOWN_VALUE) {
            val typeface = ResourcesCompat.getFont(context, buttonFontFamilyResId)
            binding.textViewTextButton.typeface = typeface
        }
        val textSizeInSp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, textButtonTextSize, resources.displayMetrics
        )
        binding.textViewTextButton.textSize = textSizeInSp
    }

    /**
     * Sets the enabled state of the TextButton and updates its appearance.
     *
     * @param enabled True to enable the TextButton, false to disable it.
     */
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setButtonView()
    }


    /**
     * Retrieves the size of the text for buttons.
     *
     * @return The size of the text for buttons.
     */
    @Suppress("unused")
    fun getTextButtonTextSize(): Float {
        return textButtonTextSize
    }

    /**
     * Sets the size of the text for buttons and updates the button view accordingly.
     *
     * @param textButtonTextSize The size of the text for buttons to be set.
     */
    fun setTextButtonTextSize(textButtonTextSize: Float?) {
        this.textButtonTextSize = textButtonTextSize!!
        setButtonView()
    }

    /**
     * Retrieves the resource ID of the font family applied to the button.
     *
     * @return The resource ID of the font family.
     */
    @Suppress("unused")
    fun getButtonFontFamilyResId(): Int {
        return buttonFontFamilyResId
    }

    /**
     * Sets the resource ID of the font family to be applied to the button and updates the button view accordingly.
     *
     * @param buttonFontFamilyResId The resource ID of the font family to be set.
     */
    fun setButtonFontFamilyResId(buttonFontFamilyResId: Int) {
        this.buttonFontFamilyResId = buttonFontFamilyResId
        setButtonView()
    }

    @Suppress("unused")
    fun getButtonTextColor(): Int {
        return buttonTextColor
    }

    fun setButtonTextColor(buttonTextColor: Int) {
        this.buttonTextColor = buttonTextColor
        setButtonView()
    }
}