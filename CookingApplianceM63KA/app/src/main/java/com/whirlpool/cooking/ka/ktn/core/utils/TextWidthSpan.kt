/*
* ************************************************************************************************
* ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
* ************************************************************************************************
*/
@file:Suppress("KDocUnresolvedReference")

package core.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.text.style.ReplacementSpan
import androidx.annotation.IntDef

/**
 *
 * File        : com.whirlpool.cooking.utils.TextWidthSpan <br></br>
 * Brief       : Custom span class to set width for span. <br></br>
 * Author      : GHARDNS/Nikki <br></br>
 * Created On  : 18-03-2024 <br></br>
 *
 * Details     : Sets width of text span and aligns text. Use [TextView.BufferType.SPANNABLE] while setting text to textView<br></br>
 */

/**
 * @param width         in px.
 * @param textAlignment similar to gravity but only in horizontal axis.
 */
class TextWidthSpan
    (private val width: Int, @param:TextAlignment private val textAlignment: Int) :
    ReplacementSpan() {
    @IntDef(CENTER, LEFT, RIGHT)
    @Retention(AnnotationRetention.SOURCE)
    private annotation class TextAlignment

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        return width
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        var xOffset = 0f
        when (textAlignment) {
            CENTER -> xOffset = (width - paint.measureText(text, start, end)) / 2
            LEFT -> xOffset = 0f
            RIGHT -> xOffset = width - paint.measureText(text, start, end)
        }
        canvas.drawText(text, start, end, x + xOffset, y.toFloat(), paint)
    }

    companion object {
        const val CENTER = 0
        const val LEFT = 1
        const val RIGHT = 2
    }
}
