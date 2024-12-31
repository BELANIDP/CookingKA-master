/***Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL***/
package core.jbase.abstractViewHolders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.whirlpool.cooking.ka.databinding.FragmentManualModeTumblerBinding
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler

/*
 * File : com.whirlpool.cooking.ka.ktn.core.jbase.AbstractDurationSelectionViewHolder
 * Author : SINGHA80.
 * Created On : 3/22/24
 * Details : Provides abstract methods
 */
abstract class AbstractDurationSelectionManualModeViewHolder {

    /**
     * onCreateView provides the interface for the Fragment to be able to inflate the customized view.
     *
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

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    abstract fun onDestroyView()

    /**
     * Provides the interface to access the binding class
     *
     * @return [FragmentManualModeTumblerBinding]
     */
    abstract fun fragmentStringTumblerBinding(): FragmentManualModeTumblerBinding?

    /**
     * Provides the interface to access bottom left button
     *
     * @return [ResourceTextView]
     */
    abstract fun provideGhostButton(): ResourceTextView?

    /**
     * Provides the interface to access bottom right button
     *
     * @return [ResourceTextView]
     */
    abstract fun provideGhostButtonConstraint(): ConstraintLayout?

    /**
     * Provides the interface to access bottom right button
     *
     * @return [ConstraintLayout]
     */
    abstract fun providePrimaryButton(): ResourceTextView?

    /**
     * Provides the interface to access BaseTumbler widget
     *
     * @return [BaseTumbler]
     */
    abstract fun providePrimaryButtonConstraint(): ConstraintLayout?

    /**
     * Provides the interface to access BaseTumbler widget
     *
     * @return [ConstraintLayout]
     */
    abstract fun provideDurationTumbler(): BaseTumbler?

    /**
     * Provides the interface to access HeaderBarWidget
     *
     * @return [HeaderBarWidget]
     */
    abstract fun provideHeaderBarWidget(): HeaderBarWidget?

    /**
     * Provides the interface to access primary AppCompatImageView
     *
     * @return [AppCompatImageView]
     */
    abstract fun providePrimaryImageView(): AppCompatImageView?

    /**
     * Provides the interface to access ghost AppCompatImageView
     *
     * @return [AppCompatImageView]
     */
    abstract fun provideGhostImageView(): AppCompatImageView?
}