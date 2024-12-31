package android.presenter.customviews.textButton

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.LayoutIconTextButtonBinding

/**
 * File : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.textButton
 *
 * Brief : IconTextButton widget
 *
 * Author : PARMAM
 *
 * Created On : 12/03/2024
 *
 * Details : Custom view representing a icon text button with optional bottom view and image view. This class extends ConstraintLayout and provides methods to customize the appearance of the button, including text, text color, and visibility of a bottom view. Usage:
 */
class IconTextButton : ConstraintLayout {

    private val binding: LayoutIconTextButtonBinding
    private var textButtonText: String? = ""
    private var textButtonTextStringResource = UNKNOWN_VALUE
    private var isBottomViewVisible = false
    private var isRightAlignImage = false
    private var textButtonTextSize = 32f
    private var buttonFontFamilyResId = UNKNOWN_VALUE
    private var imageResource = UNKNOWN_VALUE

    companion object {
        private const val UNKNOWN_VALUE = -1
    }

    /**
     * Constructs a new IconTextButton.
     *
     * @param context The context in which the IconTextButton is created.
     */
    constructor(context: Context) : super(context) {
        binding = LayoutIconTextButtonBinding.inflate(LayoutInflater.from(context), this, true)
        setButtonView()
    }

    /**
     * Constructs a new IconTextButton with attributes.
     *
     * @param context The context in which the IconTextButton is created.
     * @param attrs   The attribute set containing the attributes of the IconTextButton.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        binding = LayoutIconTextButtonBinding.inflate(LayoutInflater.from(context), this, true)
        initAttrs(context, attrs)
        setButtonView()
    }

    /**
     * Sets up the appearance of the IconTextButton based on its current properties.
     * This includes setting text, text color, and visibility of the bottom view.
     */
    private fun setButtonView() {
        // Set text if available
        if (textButtonTextStringResource != UNKNOWN_VALUE) {
            textButtonText = resources.getString(textButtonTextStringResource)
        }
        if (textButtonText?.isNotEmpty() == true) {
            binding.textViewTextButton.text = textButtonText
        }
        if (imageResource != UNKNOWN_VALUE) {
            binding.imageViewTextButton.visibility = VISIBLE
            binding.imageViewTextButton.setImageDrawable(
                ContextCompat.getDrawable(
                    binding.imageViewTextButton.context,
                    imageResource
                )
            )
        } else {
            binding.imageViewTextButton.visibility = GONE
        }

        // Set bottom view visibility and color based on properties
        if (isBottomViewVisible) {
            // Show background with bottom view
            binding.layoutIconTextButton.setBackgroundResource(R.drawable.bg_text_button)
            val color = ContextCompat.getColor(binding.root.context, R.color.walnut)
            val colorStateList = ColorStateList.valueOf(color)
            binding.layoutIconTextButton.backgroundTintList = colorStateList
        } else {
            // Hide bottom view if not visible
            binding.layoutIconTextButton.setBackgroundResource(0)
        }

        // Set bottom view visibility and color based on properties
        if (isRightAlignImage) {
            val imageViewParam: ViewGroup.LayoutParams =
                binding.imageViewTextButton.layoutParams
            val textViewParam: ViewGroup.LayoutParams = binding.textViewTextButton.layoutParams
            if (textViewParam is LayoutParams) {
                textViewParam.startToStart = LayoutParams.PARENT_ID
                textViewParam.endToEnd = LayoutParams.UNSET
                textViewParam.startToEnd = LayoutParams.UNSET
                textViewParam.endToStart = binding.imageViewTextButton.id
                binding.textViewTextButton.layoutParams = textViewParam
            }
            if (imageViewParam is LayoutParams) {
                imageViewParam.startToStart = LayoutParams.UNSET
                imageViewParam.startToEnd = binding.textViewTextButton.id
                imageViewParam.endToEnd = LayoutParams.PARENT_ID
                binding.imageViewTextButton.layoutParams = imageViewParam
            }
        }

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
    }

    private fun initAttrs(context: Context, attributeSet: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.IconTextButton)
        if (ta.getString(R.styleable.IconTextButton_icon_button_text) != null) {
            textButtonText = ta.getString(R.styleable.IconTextButton_icon_button_text)
        }
        isRightAlignImage = ta.getBoolean(R.styleable.IconTextButton_right_aligned_icon, false)
        isBottomViewVisible = ta.getBoolean(R.styleable.IconTextButton_bottom_view_visible, false)
        textButtonTextSize =
            ta.getFloat(R.styleable.IconTextButton_icon_button_text_size, textButtonTextSize)
        buttonFontFamilyResId =
            ta.getResourceId(R.styleable.IconTextButton_icon_button_font_family, UNKNOWN_VALUE)
        ta.recycle()
    }

    /**
     * Gets the current text of the IconTextButton.
     *
     * @return The text of the IconTextButton.
     */
    @Suppress("unused")
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
     * Sets the text of the IconTextButton and updates its appearance.
     *
     * @param textButtonTextStringResource The new text for the TextButton from string resource.
     */
    fun setTextButtonText(@StringRes textButtonTextStringResource: Int) {
        this.textButtonTextStringResource = textButtonTextStringResource
        setButtonView()
    }

    /**
     * Checks if the bottom view of the IconTextButton is currently visible.
     *
     * @return True if the bottom view is visible, false otherwise.
     */
    @Suppress("unused")
    fun isBottomViewVisible(): Boolean {
        return isBottomViewVisible
    }

    /**
     * Sets the visibility of the bottom view of the IconTextButton and updates its appearance.
     *
     * @param bottomViewVisible True to make the bottom view visible, false to hide it.
     */
    fun setBottomViewVisible(bottomViewVisible: Boolean) {
        isBottomViewVisible = bottomViewVisible
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
    @Suppress("unused")
    fun setTextButtonTextSize(textButtonTextSize: Float) {
        this.textButtonTextSize = textButtonTextSize
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
    @Suppress("unused")
    fun setButtonFontFamilyResId(buttonFontFamilyResId: Int) {
        this.buttonFontFamilyResId = buttonFontFamilyResId
        setButtonView()
    }

    /**
     * Determines if the image is right-aligned.
     *
     * @return true if the image is right-aligned, false otherwise.
     */
    @Suppress("unused")
    fun isRightAlignImage(): Boolean {
        return isRightAlignImage
    }

    /**
     * Sets whether the image should be right-aligned.
     *
     * @param rightAlignImage true to right-align the image, false otherwise.
     */
    @Suppress("unused")
    fun setRightAlignImage(rightAlignImage: Boolean) {
        isRightAlignImage = rightAlignImage
        setButtonView()
    }

    /**
     * Gets the resource ID of the image.
     *
     * @return the resource ID of the image.
     */
    @Suppress("unused")
    fun getImageResource(): Int {
        return imageResource
    }

    /**
     * Sets the resource ID of the image.
     *
     * @param imageResource the resource ID of the image.
     */
    fun setImageResource(imageResource: Int) {
        this.imageResource = imageResource
        setButtonView()
    }
}