package android.presenter.customviews.widgets.headerbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.HeaderBarWidgetBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView

// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL
// Created by SINGHA80 on 2/8/2024.
/**
 * HeaderBarWidget class uses header_bar_widget layout and
 * provides common functionalities of the views used in the layout.
 */
class HeaderBarWidget : ConstraintLayout {
    private var contextRef: Context

    /**
     * @return HeaderBarWidget2Binding layout
     */
    private var binding: HeaderBarWidgetBinding? = null
    private lateinit var headerBarWidgetImpl: HeaderBarWidgetImpl

    constructor(context: Context) : super(context) {
        this.contextRef = context
        inflateLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.contextRef = context
        inflateLayout()
    }

    /**
     * inflates headerBarWidget1 layout & invoke initHeaderbarImpl() method
     */
    private fun inflateLayout() {
        binding = HeaderBarWidgetBinding.inflate(LayoutInflater.from(context), this, true)
        initHeaderBarImpl()
        headerBarWidgetImpl.setDefaultOnClickListener()
        setDefaultOvenCavityIconVisibilityByVariant()
    }

    /**
     * creates HeaderBarWidget2Impl object
     */
    private fun initHeaderBarImpl() {
        headerBarWidgetImpl = HeaderBarWidgetImpl(binding, context)
    }

    private fun setDefaultOvenCavityIconVisibilityByVariant() {
       if (CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO || CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
            setOvenCavityIconVisibility(true)
        }
        else {
           setOvenCavityIconVisibility(false)
       }
    }

    fun getBinding(): HeaderBarWidgetBinding? {
        return binding
    }

    /**
     * @param text to set title
     */
    fun setTitleText(text: String?) {
        headerBarWidgetImpl.setTitleText(text)
    }

    /**
     * @param resId to set title based on the resource id
     */
    fun setTitleText(@StringRes resId: Int?) {
        headerBarWidgetImpl.setTitleText(resId)
    }

    /**
     * @return return title text
     */
    fun getTitleText() : String{
        return headerBarWidgetImpl.getTitleText()
    }

    /**
     * @param text to set oven cavity name
     */
    fun setOvenCavityTitleText(text: String?) {
        headerBarWidgetImpl.setOvenCavityTitleText(text)
    }

    fun getOvenCavityTitleTextView():TextView?{
        return headerBarWidgetImpl.getOvenCavityTitleTextView()
    }

    /**
     * @param resId to set cavity name based on the resource id
     */
    @Suppress("unused")
    fun setOvenCavityTitleText(@StringRes resId: Int?) {
        headerBarWidgetImpl.setOvenCavityTitleText(resId)
    }


    /**
     * @return return Header title text view
     */
    fun getHeaderTitle(): ResourceTextView? {
        return headerBarWidgetImpl.getHeaderTitleTextView()
    }

    /**
     * @return return Header right icon view
     */
    fun getRightImageView(): ImageView? {
        return headerBarWidgetImpl.getRightImageView()
    }

    /**
     * @return return Header left icon view
     */
    fun getLeftImageView(): ImageView? {
        return headerBarWidgetImpl.getLeftImageView()
    }

    /**
     * @return return Cavity Icon view
     */
    fun getOvenCavityImageView(): ImageView? {
        return headerBarWidgetImpl.ivOvenCavity
    }

    /**
     * @param resource to set left icon
     */
    fun setLeftIcon(@DrawableRes resource: Int) {
        headerBarWidgetImpl.ivLeftIcon?.let { headerBarWidgetImpl.setIconImage(it, resource) }
    }

    /**
     * @param resource to set right icon
     */
    fun setRightIcon(@DrawableRes resource: Int) {
        headerBarWidgetImpl.ivRightIcon?.let { headerBarWidgetImpl.setIconImage(it, resource) }
    }

    /**
     * @param resource to set info icon
     */
    @Suppress("unused", "unused")
    fun setInfoIcon(@DrawableRes resource: Int) {
        headerBarWidgetImpl.ivInfo?.let { headerBarWidgetImpl.setIconImage(it, resource) }
    }

    /**
     * @param resource to set oven cavity icon
     */
    fun setOvenCavityIcon(@DrawableRes resource: Int) {
        headerBarWidgetImpl.ivOvenCavity?.let { headerBarWidgetImpl.setIconImage(it, resource) }
    }

    /**
     * @param isVisible true/false to set visibility of left icon
     */
    fun setLeftIconVisibility(isVisible: Boolean) {
        headerBarWidgetImpl.ivLeftIcon?.let { headerBarWidgetImpl.setIconVisibility(it, isVisible) }
        headerBarWidgetImpl.clLeftIcon?.let { headerBarWidgetImpl.setIconVisibility(it, isVisible) }
    }

    /**
     * @param isVisible true/false to set visibility of right icon
     */
    fun setRightIconVisibility(isVisible: Boolean) {
        headerBarWidgetImpl.ivRightIcon?.let {headerBarWidgetImpl.setIconVisibility(it, isVisible) }
        headerBarWidgetImpl.clRightIcon?.let {headerBarWidgetImpl.setIconVisibility(it, isVisible) }
    }

    /**
     * @param isVisible true/false to set visibility of left icon knob underline
     */
    fun setLeftIconUnderlineVisibility(isVisible: Boolean) {
        if(headerBarWidgetImpl.ivRightIconUnderline?.visibility == View.VISIBLE)
            setRightIconUnderlineVisibility(false)
        headerBarWidgetImpl.ivLeftIconUnderline?.let { headerBarWidgetImpl.setIconVisibility(it, isVisible) }
    }

    /**
     * @param isVisible true/false to set visibility of right icon knob underline
     */
    fun setRightIconUnderlineVisibility(isVisible: Boolean) {
        if(headerBarWidgetImpl.ivLeftIconUnderline?.visibility == View.VISIBLE)
            setLeftIconUnderlineVisibility(false)
        headerBarWidgetImpl.ivRightIconUnderline?.let {
            headerBarWidgetImpl.setIconVisibility(
                it,
                isVisible
            )
        }
    }

    /**
     * @param isVisible true/false to set visibility of info icon
     */
    fun setInfoIconVisibility(isVisible: Boolean) {
        headerBarWidgetImpl.ivInfo?.let { headerBarWidgetImpl.setIconVisibility(it, isVisible) }
        headerBarWidgetImpl.clInfo?.let { headerBarWidgetImpl.setIconVisibility(it, isVisible) }
    }

    /**
     * @param isVisible true/false to set visibility of info icon
     */
    fun setOvenCavityIconVisibility(isVisible: Boolean) {
        headerBarWidgetImpl.clOvenCavity?.let {
            headerBarWidgetImpl.setIconVisibility(
                it,
                isVisible
            )
        }
        headerBarWidgetImpl.ivOvenCavity?.let {
            headerBarWidgetImpl.setIconVisibility(
                it,
                isVisible
            )
        }
    }

    /**
     * @param isVisible true/false to set visibility of oven cavity name
     */
    fun setOvenCavityTitleTextVisibility(isVisible: Boolean) {
        headerBarWidgetImpl.tvOvenCavityName?.let {
            headerBarWidgetImpl.setIconVisibility(
                it,
                isVisible
            )
        }
    }

    /**
     * @param onClickListener to set custom click listeners
     */
    fun setCustomOnClickListener(onClickListener: HeaderBarWidgetInterface.CustomClickListenerInterface?) {
        headerBarWidgetImpl.setCustomOnClickListener(onClickListener)
    }

    fun setRightIconClickListener(onClickListener: HeaderBarWidgetInterface.RightIconClickListenerInterface?) {
        headerBarWidgetImpl.setRightIconClickListener(onClickListener)
    }

    fun setLeftIconClickListener(onClickListener: HeaderBarWidgetInterface.LeftIconClickListenerInterface?) {
        headerBarWidgetImpl.setLeftIconClickListener(onClickListener)
    }

    /**
     * clears the used resources
     */
    @Suppress("unused")
    fun clearResources() {
        headerBarWidgetImpl.clearResources()
    }
}
