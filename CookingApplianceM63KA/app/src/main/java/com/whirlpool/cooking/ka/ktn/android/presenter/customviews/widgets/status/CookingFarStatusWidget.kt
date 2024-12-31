package android.presenter.customviews.widgets.status

import android.annotation.SuppressLint
import android.content.Context
import android.presenter.basefragments.abstract_view_helper.AbstractFarStatusWidgetHelper
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.R
import core.utils.HMILogHelper
import core.viewHolderHelpers.FarStatusWidgetHelper

/**
 * File        : android.presenter.customviews.widgets.status.CookingStatusWidget
 * Brief       : Cooking status widget which updates in FAR view status fragment
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides individual status widget related view related events and view support for single, double status fragment binding class
 */
class CookingFarStatusWidget : ConstraintLayout {
    private val tag = "CookingStatusWidget"
    lateinit var statusWidgetHelper: AbstractFarStatusWidgetHelper
    lateinit var ovenType: String

    /**
     *
     * @param context The context in which the ToggleSwitch is created.
     */
    constructor(context: Context) : super(context) {
        init(context)
    }

    /**
     * @param context The context in which the ToggleSwitch is created.
     * @param attrs   The attributes set defined in XML.
     */
    @SuppressLint("CustomViewStyleable", "Recycle")
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val attribArray = getContext().obtainStyledAttributes(attrs, R.styleable.status_widget)
        this.ovenType = attribArray.getString(R.styleable.status_widget_oven_type).toString()
        HMILogHelper.Logd(tag, "loading constructor CookingStatusWidget: $ovenType")
        init(context)
    }

    private fun init(context: Context) {
        statusWidgetHelper = FarStatusWidgetHelper()
        statusWidgetHelper.inflateView(context, this, this.ovenType)
    }
}