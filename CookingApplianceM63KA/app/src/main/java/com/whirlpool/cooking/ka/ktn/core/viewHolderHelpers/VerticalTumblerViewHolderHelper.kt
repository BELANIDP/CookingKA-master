/*
 *
 * * ************************************************************************************************
 * * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * * ************************************************************************************************
 *
 */
package core.viewHolderHelpers

import android.os.Bundle
import android.presenter.customviews.textButton.TextButton
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.VerticalFragmentTumblerBinding
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import core.jbase.abstractViewHolders.AbstractVerticalTumblerViewHolder

/**
 * File        : com.whirlpool.cooking.ka.ktn.core.viewHolderHelpers
 * Brief       : This class will provide base basic vertical tumbler implementation and by extending this class we can make changes into UI and functionality further.
 * Author      : SINGHJ25
 * Created On  : 18/02/2024
 */
class VerticalTumblerViewHolderHelper :
    AbstractVerticalTumblerViewHolder() {
    private var fragmentTumblerBinding: VerticalFragmentTumblerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentTumblerBinding = VerticalFragmentTumblerBinding.inflate(inflater!!)
        return fragmentVerticalTumblerBinding?.root
    }

    /**
     * Provides the binding class
     *
     * @return [VerticalFragmentTumblerBinding]
     */
    override val fragmentVerticalTumblerBinding: VerticalFragmentTumblerBinding?
        get() = fragmentTumblerBinding

    override fun onDestroyView() {
        fragmentTumblerBinding = null
    }

    override fun provideGhostButton(): TextButton? {
        return fragmentTumblerBinding?.btnGhost
    }

    override fun provideConstraintGhost(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintGhostLeft
    }

    override fun provideLeftPowerButton(): TextButton? {
        return fragmentTumblerBinding?.verticalTumblerCookTimeLeftPower
    }

    override fun provideLeftPowerConstraint(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintGhostLeftPower
    }

    override fun providePrimaryButton(): TextButton? {
        return fragmentTumblerBinding?.btnPrimary
    }

    override fun providePrimaryConstraint(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintPrimaryRight
    }

    override fun provideVerticalTumblerLeft(): BaseTumbler? {
        return fragmentTumblerBinding?.tumblerLeft
    }

    override fun provideVerticalTumblerCenter(): BaseTumbler? {
        return fragmentTumblerBinding?.tumblerCenter
    }

    override fun provideLeftTumblerText(): ResourceTextView? {
        return fragmentTumblerBinding?.labelLeftTumbler
    }

    override fun provideCenterTumblerText(): ResourceTextView? {
        return fragmentTumblerBinding?.labelCenterTumbler
    }

    override fun provideTextViewHelperText(): TextView? {
        return fragmentTumblerBinding?.textViewHelperText
    }

    override fun provideHeaderBar(): HeaderBarWidget? {
        return fragmentTumblerBinding?.headerBar
    }
}