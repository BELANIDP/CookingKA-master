/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package core.viewHolderHelpers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.FragmentStringTumblerBinding
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import core.jbase.abstractViewHolders.AbstractStringTumblerViewHolder


/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.customviews.stringtumbler
 * Brief       : This class will provide base basic tumbler implementation and by extending this class we can make changes into UI and functionality further.
 * Author      : SINGHJ25
 * Created On  : 09/02/2024
 */
class StringTumblerViewHolderHelper : AbstractStringTumblerViewHolder() {

    private var fragmentTumblerBinding: FragmentStringTumblerBinding? = null

    /**
     * Inflate the customized view
     *
     * @param inflater           [LayoutInflater]
     * @param container          [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */
    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentTumblerBinding = FragmentStringTumblerBinding.inflate(inflater!!, container, false)
        return fragmentTumblerBinding?.root
    }

    /**
     * Clean up the view holder when it is destroyed
     */
    override fun onDestroyView() {
        fragmentTumblerBinding = null
    }

    /**
     * Provides the binding class
     *
     * @return [FragmentStringTumblerBinding]
     */
    override fun fragmentStringTumblerBinding(): FragmentStringTumblerBinding? {
        return fragmentTumblerBinding
    }

    /**
     * Definition for the bottom right button
     *
     * @return [ResourceTextView]
     */
    override fun providePrimaryButton(): ResourceTextView? {
        return fragmentTumblerBinding?.btnPrimary
    }

    /**
     * Definition for the bottom left button
     *
     * @return [ResourceTextView]
     */
    override fun providePrimaryConstraint(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintPrimaryButton
    }

    /**
     * Definition for the bottom right constraint
     *
     * @return [ConstraintLayout]
     */
    override fun provideGhostButton(): ResourceTextView? {
        return fragmentTumblerBinding?.btnGhost
    }

    /**
     * Definition for the BaseTumbler widget
     *
     * @return [BaseTumbler]
     */
    override fun provideGhostConstraint(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintGhostButton
    }

    /**
     * Definition for the BaseTumbler widget
     *
     * @return [ConstraintLayout]
     */
    override fun provideNumericTumbler(): BaseTumbler? {
        return fragmentTumblerBinding?.tumblerString
    }

    override fun provideNumericTumblerVision(): BaseTumbler? {
        return fragmentTumblerBinding?.tumblerStringVision
    }

    override fun provideNumericTumblerVisionBase(): BaseTumbler? {
        return fragmentTumblerBinding?.tumblerStringVisionBase
    }

    override fun provideHeaderBarWidget(): HeaderBarWidget? {
        return fragmentTumblerBinding?.headerBar
    }

    override fun provideMainImageBackgroundWidget(): AppCompatImageView? {
        return fragmentTumblerBinding?.imageMainBackground
    }

    override fun providePrimaryImageView(): AppCompatImageView? {
        return fragmentTumblerBinding?.imgPrimary
    }

    override fun providePrimaryImageViewBig(): AppCompatImageView? {
        return fragmentTumblerBinding?.imgPrimaryBig
    }

    override fun provideGhostImageView(): AppCompatImageView? {
        return fragmentTumblerBinding?.imgGhost
    }

    override fun provideSubTitle(): ResourceTextView? {
        return fragmentTumblerBinding?.subTitle
    }

    /**
     * Definition for the bottom right button with touch improvement
     *
     * @return [ConstraintLayout]
     */
    fun provideExtendedPrimaryButton(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintPrimaryButton
    }

    /**
     * Definition for the bottom left button with touch improvement
     *
     * @return [ConstraintLayout]
     */
    fun provideExtendedGhostButton(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintGhostButton
    }

    override fun provideVisionSubTitle(): ResourceTextView? {
        return fragmentTumblerBinding?.tumblerStringVisionBaseSubTitle
    }
}

