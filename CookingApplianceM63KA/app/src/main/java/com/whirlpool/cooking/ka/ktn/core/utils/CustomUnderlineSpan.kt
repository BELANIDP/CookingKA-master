package core.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import android.text.style.LineHeightSpan
import android.widget.TextView
import com.whirlpool.cooking.ka.R

class CustomUnderlineSpan : LineBackgroundSpan, LineHeightSpan {
    private var paint: Paint
    private var textView: TextView
    private var offsetY: Float
    private var spacingExtra: Float

    /**
     *
     * @param context Context for getting the Resources like color and dimensions.
     * @param textView The text view which will be underlined
     */
    constructor(context: Context, textView: TextView) : super() {
        val dashPath =
            context.resources.getDimension(R.dimen.header_bar_right_text_underline_dashpath)
        paint = Paint()
        paint.color = context.getColor(R.color.amber_primary)
        paint.style = Paint.Style.STROKE
        paint.pathEffect = DashPathEffect(floatArrayOf(dashPath, dashPath), 0f)
        paint.strokeWidth =
            context.resources.getDimension(R.dimen.header_bar_right_text_underline_thickness)
        this.textView = textView
        offsetY = context.resources.getDimension(R.dimen.header_bar_right_text_underline_offSet)
        spacingExtra =
            context.resources.getDimension(R.dimen.header_bar_right_text_underline_extraSpacing)
    }

    /**
     * @param textView     The text view which will be underlined
     * @param color        The color in which underline will happen
     * @param thickness    the thickness of the underline
     * @param dashPath     the dash path
     * @param offsetY      the Y offset of the underline
     * @param spacingExtra extra spacing between the underline
     */
    @Suppress("unused")
    constructor(
        textView: TextView, color: Int, thickness: Float, dashPath: Float,
        offsetY: Float, spacingExtra: Float
    ) : super() {
        paint = Paint()
        paint.color = color
        paint.style = Paint.Style.STROKE
        paint.pathEffect = DashPathEffect(floatArrayOf(dashPath, dashPath), 0f)
        paint.strokeWidth = thickness
        this.textView = textView
        this.offsetY = offsetY
        this.spacingExtra = spacingExtra
    }

    /**
     * This will determine the height
     *
     * @param text       the textview which will have the underline
     * @param start      the height start
     * @param end        the height end
     * @param spanStartv span start of y
     * @param v          just overriden
     * @param fm         font matrix
     */
    override fun chooseHeight(
        text: CharSequence?, start: Int, end: Int, spanStartv: Int, v: Int,
        fm: Paint.FontMetricsInt
    ) {
        fm.ascent -= spacingExtra.toInt()
        fm.top -= spacingExtra.toInt()
        fm.descent += spacingExtra.toInt()
        fm.bottom += spacingExtra.toInt()
    }

    /**
     * Here the canvas drawing happens
     *
     * @param canvas   The canvas where the drawing will take place
     * @param p        Paint object
     * @param left     Start of the paint
     * @param right    End of the paint
     * @param top      Top area of paint
     * @param baseline Baseline of paint
     * @param bottom   Botton of paint
     * @param text     The text are which will be underline
     * @param start    Start
     * @param end      End
     * @param lnum     lnum
     */
    override fun drawBackground(
        canvas: Canvas, p: Paint, left: Int, right: Int, top: Int, baseline: Int,
        bottom: Int, text: CharSequence, start: Int, end: Int, lnum: Int
    ) {
        val lineNum = textView.lineCount
        for (i in 0 until lineNum) {
            val layout = textView.layout
            canvas.drawLine(
                layout.getLineLeft(i), layout.getLineBottom(i) - spacingExtra + offsetY,
                layout.getLineRight(i), layout.getLineBottom(i) - spacingExtra + offsetY,
                paint
            )
        }
    }
}
