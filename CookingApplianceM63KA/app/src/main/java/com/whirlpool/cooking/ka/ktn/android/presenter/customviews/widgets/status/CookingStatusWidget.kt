package android.presenter.customviews.widgets.status

import android.annotation.SuppressLint
import android.content.Context
import android.presenter.basefragments.abstract_view_helper.AbstractStatusWidgetHelper
import android.presenter.fragments.sabbath.StatusSabbathWidgetHelper
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import core.utils.HMILogHelper
import core.viewHolderHelpers.StatusWidgetHelper

/**
 * File        : android.presenter.customviews.widgets.status.CookingStatusWidget
 * Brief       : Cooking status widget which updates in status fragment
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides individual status widget related view related events and view support for single, double status fragment binding class
 */
class CookingStatusWidget : ConstraintLayout {
    private var sabbathType: Boolean = false
    lateinit var ovenType: String
    private val tag = "CookingStatusWidget"
    lateinit var statusWidgetHelper: AbstractStatusWidgetHelper
    var listener: OnStatusWidgetClickListener? = null

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
    @SuppressLint("Recycle", "CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val attribArray = getContext().obtainStyledAttributes(attrs, R.styleable.status_widget)
        ovenType = attribArray.getString(R.styleable.status_widget_oven_type).toString()
        sabbathType = attribArray.getBoolean(R.styleable.status_widget_sabbath_type, false)
        HMILogHelper.Logd(tag, "loading constructor CookingStatusWidget: $ovenType, sabbathType $sabbathType")
        init(context)
    }

    private fun init(context: Context) {
        statusWidgetHelper = if(sabbathType) StatusSabbathWidgetHelper()
        else StatusWidgetHelper()
        statusWidgetHelper.inflateView(context, this, ovenType)
    }

    fun setStatusWidgetClickListener(listener: OnStatusWidgetClickListener?) {
        this.listener = listener
    }

    /**
     * pass all the view related click events to the implemented listeners
     */
    fun onViewClick(view: View?, viewModel: CookingViewModel?) {
        listener?.statusWidgetOnClick(view, this, viewModel)
    }

    interface OnStatusWidgetClickListener {
        /**
         * implement this interface to get the click events of individual related components such as Turn off, set cook time, etc
         */
        fun statusWidgetOnClick(
            view: View?,
            statusWidget: CookingStatusWidget?,
            viewModel: CookingViewModel?
        )
    }
}