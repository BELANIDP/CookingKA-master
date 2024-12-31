package core.utils

import android.graphics.Typeface
import android.content.Context
import android.view.View
import android.widget.TextView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.utils.list.ViewModelListInterface

var TextView.customText: CharSequence?
    get() = text
    set(value) {
        text = value
        visibility = View.VISIBLE
    }

fun TextView.changeTextColor(context: Context, colorId : Int) {
    this.setTextColor(
        context.resources.getColorStateList(
            colorId,
            null
        )
    )
}
var TextView.customTypeFace: Typeface?
    get() = typeface
    set(value) {
        typeface = value
    }

// Extension function to set the visibility of a View to GONE
fun View.gone() {
    this.visibility = View.GONE
}

// Extension function to set the visibility of a View to VISIBLE
fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

/**
 *  Set list object to tumbler with default selection
 * */
fun BaseTumbler.setListObjectWithDefaultSelection(
    listObject: ViewModelListInterface, initialScrollToValue: String?
) {
    this.apply {
        baseItemAnimator = null
        setInitialOffsetIndexEnabled(false)
        setListObject(listObject, initialScrollToValue, true)
    }
}
