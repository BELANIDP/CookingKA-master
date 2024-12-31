package android.presenter.customviews.widgets.headerbar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/*
Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL
*/
/**
 * HeaderBarWidgetInterface provides methods for the HeaderBarSingleLineWidget
 * & HeaderBarDoubleLine widgets
 */
interface HeaderBarWidgetInterface {
    interface CustomClickListenerInterface {
        fun leftIconOnClick() {}
        fun rightIconOnClick() {}
        fun ovenCavityOnClick() {}
        fun infoIconOnClick() {}
    }

    interface LeftIconClickListenerInterface {
        fun leftIconOnClick() {}
        }

    interface RightIconClickListenerInterface{
        fun rightIconOnClick() {}
    }

    fun setTitleText(text: String?)
    fun setTitleText(@StringRes resId: Int?)
    fun setOvenCavityTitleText(text: String?)
    fun setOvenCavityTitleText(@StringRes resId: Int?)
    fun getTitleText(): String?
    fun getOvenCavityTitleText(): String?
    fun getOvenCavityTitleTextView(): TextView?
    fun clearResources()
    fun setIconImage(view: View, @DrawableRes resource: Int)
    fun setIconVisibility(view: View, isVisible: Boolean)
    fun setCustomOnClickListener(onClickListener: CustomClickListenerInterface?)
    fun setRightIconClickListener(onClickListener: RightIconClickListenerInterface?)
    fun setLeftIconClickListener(onClickListener: LeftIconClickListenerInterface?)
    fun getHeaderTitleTextView():TextView?
    fun getRightImageView():ImageView?
    fun getLeftImageView(): ImageView?
}
