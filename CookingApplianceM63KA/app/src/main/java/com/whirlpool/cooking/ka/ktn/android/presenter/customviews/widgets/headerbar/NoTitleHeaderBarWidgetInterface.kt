package android.presenter.customviews.widgets.headerbar

import android.view.View
import androidx.annotation.DrawableRes

// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL.
// Created by SINGHA80 on 05-Feb-24.
interface NoTitleHeaderBarWidgetInterface {
    fun setViewVisibility(view: View, isVisible: Boolean)
    fun setHeaderBarStatusIcon(view: View, @DrawableRes resource: Int)
    fun clearResources()
}