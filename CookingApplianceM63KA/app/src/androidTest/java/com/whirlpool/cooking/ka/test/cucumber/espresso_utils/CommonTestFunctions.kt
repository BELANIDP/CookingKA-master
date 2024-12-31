package com.whirlpool.cooking.ka.test.cucumber.espresso_utils

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.isVisible
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import junit.framework.TestCase

/**
 * File       : com.whirlpool.cooking.ka.test.cucumber.espresso_utils.GenericTestFunctions
 * Brief      : Generic functions for automation testing
 * Author     : Vishal
 * Created On : 06/03/2024
 */


/**
 * ConvertString to required data type
 *
 * @param value in String and required datatype of value
 * @return value in required datatype of value
 */
inline fun <reified T : Any> convertStringToType(value: String, type: Class<T>): T {
    return when (T::class) {
        String::class -> value as T
        Int::class -> value.toIntOrNull() as? T
            ?: throw IllegalArgumentException("Invalid value for Int: $value")
        Double::class -> value.toDoubleOrNull() as? T
            ?: throw IllegalArgumentException("Invalid value for Double: $value")
        Float::class -> value.toFloatOrNull() as? T
            ?: throw IllegalArgumentException("Invalid value for Float: $value")
        Boolean::class -> value.toBooleanStrictOrNull() as? T
            ?: throw IllegalArgumentException("Invalid value for Boolean: $value")
        // Add more type conversions as needed
        else -> throw IllegalArgumentException("Unsupported data type: ${T::class.java.simpleName}")
    }
}

/**
 * Get text alignment
 *
 * @param alignment in String
 * @return alignment in Int
 */
fun getAlignment(alignment: String): Int {
    return try {
        when (alignment) {
            "textCenter" -> TextView.TEXT_ALIGNMENT_CENTER
            "textStart" -> TextView.TEXT_ALIGNMENT_TEXT_START
            "textEnd" -> TextView.TEXT_ALIGNMENT_TEXT_END
            "viewStart" -> TextView.TEXT_ALIGNMENT_VIEW_START
            "viewEnd" -> TextView.TEXT_ALIGNMENT_VIEW_END
            else -> null
        } ?: throw IllegalArgumentException("Unsupported alignment name: $alignment")
    } catch (e: Exception) {
        println("Error:Failed to load alignment.")
        throw e
    }
}

/**
 * Get text Gravity
 *
 * @param gravity in String
 * @return gravity in Int
 */
fun getGravity(gravity: String): Int {
    return try {
        when (gravity) {
            "center" -> Gravity.CENTER
            "start" -> Gravity.START
            "end" -> Gravity.END
            "bottom" -> Gravity.BOTTOM
            "top" -> Gravity.TOP
            "left" -> Gravity.LEFT
            "right" -> Gravity.RIGHT
            "centerHorizontal" -> Gravity.CENTER_HORIZONTAL
            "centerVertical" -> Gravity.CENTER_VERTICAL
            else -> null
        } ?: throw IllegalArgumentException("Unsupported gravity name: $gravity")
    } catch (e: Exception) {
        println("Error:Failed to load alignment.")
        throw e
    }
}

/**
 * Get text FontFamily
 *
 * @param FontFamily in String
 * @return FontFamily in Typeface
 */
fun getFontFamily(fontFamilyName: String): Typeface {
    return try {
        when (fontFamilyName) {
            "open_sans_regular" -> CookingKACucumberTests.context.resources.getFont(R.font.open_sans_regular)
            "roboto_black" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_black)
            "roboto_blackitalic" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_blackitalic)
            "roboto_bold" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_bold)
            "roboto_bolditalic" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_bolditalic)
            "roboto_italic" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_italic)
            "roboto_light" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_light)
            "roboto_lightitalic" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_lightitalic)
            "roboto_medium" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_medium)
            "roboto_mediumitalic" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_mediumitalic)
            "roboto_regular" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_regular)
            "roboto_thin" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_thin)
            "roboto_thinitalic" -> CookingKACucumberTests.context.resources.getFont(R.font.roboto_thinitalic)
            else -> null
        } ?: throw IllegalArgumentException("Unsupported font family name: $fontFamilyName")
    } catch (e: Exception) {
        println("Error: Failed to load font family.")
        throw e // Re-throw the exception to propagate it
    }
}

/**
 *  Check expected and actual texts visibility are equal
 */
fun checkTextVisible(visible: String, view: View) {
    TestCase.assertEquals(convertStringToType(visible, Boolean::class.java), (view as TextView).isVisible)
}

/**
 *  Check expected and actual texts are equal
 */
fun checkText(text: String, view: View) {
    TestCase.assertEquals(text, (view as TextView).text)
}

/**
 *  Check expected and actual text's width are equal
 */
fun checkWidthOfText(width: String, view: View) {
    TestCase.assertEquals(convertStringToType(width, Int::class.java), (view as TextView).width)
}

/**
 *  Check expected and actual text's height are equal
 */
fun checkHeightOfText(height: String, view: View) {
    TestCase.assertEquals(convertStringToType(height, Int::class.java), (view as TextView).height)
}

/**
 *  Check expected and actual text's fontfamilys are equal
 */
fun checkFontFamilyOfText(fontFamily: String, view: View) {
    TestCase.assertEquals(getFontFamily(fontFamily), (view as TextView).typeface)
}

/**
 *  Check expected and actual text's weight are equal
 */
fun checkWeightOfText(weight: String, view: View) {
    TestCase.assertEquals(convertStringToType(weight, Int::class.java), (view as TextView).typeface.weight)
}

/**
 *  Check expected and actual text's sizes are equal
 */
fun checkTextSize(size: String, view: View) {
    TestCase.assertEquals(convertStringToType(size, Float::class.java), (view as TextView).textSize)
}

/**
 *  Check expected and actual text's lineHeights are equal
 */
fun checkLineHeightOfText(lineHeight: String, view: View) {
    TestCase.assertEquals(convertStringToType(lineHeight, Int::class.java), (view as TextView).lineHeight)
}

/**
 *  Check expected and actual text's gravities are equal
 */
fun checkGravityOfText(gravity: String, view: View) {
    TestCase.assertEquals(getGravity(gravity), (view as TextView).gravity)
}

/**
 *  Check expected and actual text's alignments are equal
 */
fun checkAlignmetOfText(alignment: String, view: View) {
    TestCase.assertEquals(getAlignment(alignment), (view as TextView).textAlignment)
}

/**
 *  Check expected and actual text's colors are equal
 */
fun checkColorOfText(color: String, view: View) {
    TestCase.assertEquals(Color.parseColor(color), (view as TextView).currentTextColor)
}

/**
 *  Check expected and actual texts visibility are equal
 */
fun checkToggleButtonVisible(visible: String, view: View) {
    TestCase.assertEquals(convertStringToType(visible, Boolean::class.java), (view as TextView).isVisible)
}

/**
 *  Check expected and actual toggle button's width are equal
 */
fun checkWidthOfToggleButton(width: String, view: View) {
    TestCase.assertEquals(convertStringToType(width, Int::class.java), (view as TextView).width)
}

/**
 *  Check expected and actual toggle button's height are equal
 */
fun checkHeightOfToggleButton(height: String, view: View) {
    TestCase.assertEquals(convertStringToType(height, Int::class.java), (view as TextView).height)
}

/**
 *  Check toggle button on/off
 */
fun checkToggleButtonOnOff(on_off: String, view: View) {
    TestCase.assertEquals(convertStringToType(on_off,Boolean::class.java), (view as SwitchCompat).isChecked)
}

/**
*  Check toggle button enable/disable
*/
fun checkToggleButtonEnableDisable(enable_disable: String, view: View) {
    TestCase.assertEquals(convertStringToType(enable_disable,Boolean::class.java), (view as SwitchCompat).isEnabled)
}

/**
 *  Check expected and actual Image width are equal
 */
fun checkWidthOfImage(width: String, view: View) {
    TestCase.assertEquals(convertStringToType(width, Int::class.java), (view as AppCompatImageView).width)
}

/**
 *  Check expected and actual Image height are equal
 */
fun checkHeightOfImage(height: String, view: View) {
    TestCase.assertEquals(convertStringToType(height, Int::class.java), (view as AppCompatImageView).height)
}