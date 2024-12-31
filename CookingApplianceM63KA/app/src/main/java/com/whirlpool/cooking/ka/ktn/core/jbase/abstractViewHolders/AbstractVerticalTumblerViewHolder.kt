/*
 *
 * * ************************************************************************************************
 * * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * * ************************************************************************************************
 *
 */
package core.jbase.abstractViewHolders

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

/**
 * File        : com.whirlpool.cooking.ka.ktn.core.jbase.AbstractViewHolders
 * Brief       : This class will provide base basic tumbler implementation and by extending this class we can make changes into UI and functionality further.
 * Author      : SINGHJ25
 * Created On  : 09/02/2024
 */
abstract class AbstractVerticalTumblerViewHolder {
    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================
    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     * @param inflater [LayoutInflater]
     * @param container [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */
    abstract fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    abstract fun onDestroyView()

    /**
     * Provides the interface to access the binding class
     * @return [VerticalFragmentTumblerBinding]
     */
    abstract val fragmentVerticalTumblerBinding: VerticalFragmentTumblerBinding?

    /**
     * Provides the interface to access bottom left button
     * @return [TextButton]
     */
    abstract fun provideGhostButton(): TextButton?

    /**
     * Provides the interface to access bottom left power button for microwave
     * @return [TextButton]
     */
    abstract fun provideConstraintGhost(): ConstraintLayout?

    /**
     * Provides the interface to access bottom left power button for microwave
     * @return [ConstraintLayout]
     */
    abstract fun provideLeftPowerButton(): TextButton?

    /**
     * Provides the interface to access bottom right button
     * @return [TextButton]
     */
    abstract fun provideLeftPowerConstraint(): ConstraintLayout?

    /**
     * Provides the interface to access bottom right button
     * @return [ConstraintLayout]
     */
    abstract fun providePrimaryButton(): TextButton?

    /**
     * Provides the interface to access BaseTumbler widget
     * @return [BaseTumbler]
     */
    abstract fun providePrimaryConstraint(): ConstraintLayout?

    /**
     * Provides the interface to access BaseTumbler widget
     * @return [ConstraintLayout]
     */
    abstract fun provideVerticalTumblerLeft(): BaseTumbler?

    /**
     * Provides the interface to access BaseTumbler widget
     * @return [BaseTumbler]
     */
    abstract fun provideVerticalTumblerCenter(): BaseTumbler?

    abstract fun provideLeftTumblerText(): ResourceTextView?

    abstract fun provideCenterTumblerText(): ResourceTextView?

    abstract fun provideTextViewHelperText(): TextView?

    abstract fun provideHeaderBar(): HeaderBarWidget?
}