/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.customviews.widgets.toggleswitch

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.LayoutSwitchWidgetBinding

/**
 * File        : android.presenter.customviews.widgets.toggleswitch.ToggleSwitch
 * Brief       : Toggle Switch widget.
 * Author      : BHIMAR
 * Created On  : 02-14-2024
 * Details     : This widget can be used in settings as on/off or enable/disable control.
 */
class ToggleSwitch : ConstraintLayout {
    private var switchWidgetBinding: LayoutSwitchWidgetBinding? = null

    /**
     * Constructor for creating ToggleSwitch programmatically.
     *
     * @param context The context in which the ToggleSwitch is created.
     */
    constructor(context: Context) : super(context) {
        init(context)
    }

    /**
     * Constructor for creating ToggleSwitch programmatically.
     *
     * @param context The context in which the ToggleSwitch is created.
     * @param attrs   The attributes set defined in XML.
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    /**
     * Constructor for creating ToggleSwitch programmatically.
     *
     * @param context The context in which the ToggleSwitch is created.
     * @param attrs   The attributes set defined in XML.
     */
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        switchWidgetBinding =
            LayoutSwitchWidgetBinding.inflate(LayoutInflater.from(context), this, true)
    }
}