/*
 * ************************************************************************************************
 * ***** Copyright (c) 2019. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package com.whirlpool.cooking.ka.test.cucumber.espresso_utils

import android.graphics.Typeface
import android.os.Build
import android.view.View
import android.widget.TextView
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

/**
 * File       : com.whirlpool.cooking.espresso_utils
 * Brief      :
 * Author     : RVK7COb
 * Created On : 15/9/2020
 * Details    :This Class is to check the text view properties
 */
class TextViewPropertiesMatcher : TypeSafeMatcher<View?> {
    var textProperties: TextProperties
    private var expectedvalue = 0f
    private var typeFace: Typeface? = null
    private var gravity = 0

    constructor(value: Float, textProperties: TextProperties) : super(
        View::class.java
    ) {
        expectedvalue = value
        this.textProperties = textProperties
    }

    constructor(typeFace: Typeface?, textProperties: TextProperties) : super(
        View::class.java
    ) {
        this.typeFace = typeFace
        this.textProperties = textProperties
    }

    constructor(gravity: Int, textProperties: TextProperties) : super(
        View::class.java
    ) {
        this.gravity = gravity
        this.textProperties = textProperties
    }

    /**
     * Method : boolean matchesSafely(View target)
     * Parameter : view
     * Description : This API allows to verify the the actual text properties and the expected
     * properties of the text view
     */
    override fun matchesSafely(target: View?): Boolean {
        if (target !is TextView) {
            return false
        }
        val targetEditText = target
        return when (textProperties) {
            TextProperties.TEXTSIZE -> targetEditText.textSize == expectedvalue
            TextProperties.TEXT_SIZE_NOT_MATCHED -> targetEditText.textSize != expectedvalue
            TextProperties.WIDTH -> targetEditText.width.toFloat() == expectedvalue
            TextProperties.HEIGHT -> targetEditText.height.toFloat() == expectedvalue
            TextProperties.TEXTCOLOR -> {
                val textViewColor = targetEditText.currentTextColor
                val expectedColor: Int
                expectedColor = if (Build.VERSION.SDK_INT <= 22) {
                    targetEditText.context.resources.getColor(expectedvalue.toInt(), null)
                } else {
                    targetEditText.context.getColor(expectedvalue.toInt())
                }
                textViewColor == expectedColor
            }

            TextProperties.TEXT_COLOR_NOT_MATCHED -> {
                val textViewColorNotMatched = targetEditText.currentTextColor
                val expectedColorNotMatched: Int
                expectedColorNotMatched = if (Build.VERSION.SDK_INT <= 22) {
                    targetEditText.context.resources.getColor(expectedvalue.toInt(), null)
                } else {
                    targetEditText.context.getColor(expectedvalue.toInt())
                }
                textViewColorNotMatched != expectedColorNotMatched
            }
            TextProperties.TEXT_ALIGNMENT -> targetEditText.textAlignment == expectedvalue.toInt()
            TextProperties.TEXT_ALIGNMENT_NOT_MATCHED -> targetEditText.textAlignment != expectedvalue.toInt()
            TextProperties.TEXTSTYLE -> targetEditText.typeface.style == typeFace!!.style
            TextProperties.LETTERSPACING -> targetEditText.letterSpacing == expectedvalue
            TextProperties.LINESPACINGEXTRA -> targetEditText.lineSpacingExtra == expectedvalue
            TextProperties.GRAVITY -> targetEditText.gravity == gravity
            TextProperties.GRAVITY_NOT_MATCHED -> targetEditText.gravity != gravity
            TextProperties.MAX_LINES -> targetEditText.maxLines == gravity
            else -> false
        }
    }

    override fun describeTo(description: Description) {
        description.appendText("with fontSize: ")
        description.appendValue(expectedvalue)
    }

    enum class TextProperties {
        TEXTSIZE,
        TEXT_SIZE_NOT_MATCHED,
        WIDTH,
        HEIGHT,
        TEXTSTYLE,
        FONTFAMILY,
        TEXTCOLOR,
        TEXT_COLOR_NOT_MATCHED,
        LETTERSPACING,
        LINESPACINGEXTRA,
        GRAVITY,
        GRAVITY_NOT_MATCHED,
        MAX_LINES,
        TEXT_ALIGNMENT,
        TEXT_ALIGNMENT_NOT_MATCHED
    }
}
