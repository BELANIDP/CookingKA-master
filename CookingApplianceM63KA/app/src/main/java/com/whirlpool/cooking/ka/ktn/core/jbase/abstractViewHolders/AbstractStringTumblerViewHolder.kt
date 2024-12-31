package core.jbase.abstractViewHolders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.whirlpool.cooking.ka.databinding.FragmentStringTumblerBinding
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler


@Suppress("unused")
abstract class AbstractStringTumblerViewHolder {
    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================
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
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    abstract fun onDestroyView()
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access the binding class
     *
     * @return [FragmentStringTumblerBinding]
     */
    abstract fun fragmentStringTumblerBinding(): FragmentStringTumblerBinding?

    /**
     * Provides the interface to access bottom left button
     *
     * @return [ResourceTextView]
     */
    abstract fun provideGhostButton(): ResourceTextView?
    /*---------------------------------------------------------------------------------------------------------------*/ /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access bottom right button
     *
     * @return [ResourceTextView]
     */
    abstract fun providePrimaryButton(): ResourceTextView?
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access BaseTumbler widget
     *
     * @return [BaseTumbler]
     */
    abstract fun provideGhostConstraint(): ConstraintLayout?
    /*---------------------------------------------------------------------------------------------------------------*/ /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access bottom right button
     *
     * @return [ConstraintLayout]
     */
    abstract fun providePrimaryConstraint(): ConstraintLayout?
    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access BaseTumbler widget
     *
     * @return [ConstraintLayout]
     */
    abstract fun provideNumericTumbler(): BaseTumbler?

    abstract fun provideNumericTumblerVision(): BaseTumbler?
    abstract fun provideNumericTumblerVisionBase(): BaseTumbler?
    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    // ================================================================================================================
    /**
     * Provides the interface to access DegreeTypeTextView
     *
     * @return [TextView]
     */
    fun provideDegreeTypeTextView(): TextView? {
        return null
    }

    /**
     * Provides the interface to access Next screen
     *
     * @return
     */
    fun provideSummaryScreenAction(): Int {
        return 0
    }

    /**
     * Provides the interface to access Next screen for delay
     *
     * @return
     */
    fun provideDelaySetScreenAction(): Int {
        return 0
    }

    /**
     * Provides the interface to access Next screen for delay
     *
     * @return
     */
    fun provideDelaySetScreenActionMW(): Int {
        return 0
    }

    abstract fun provideHeaderBarWidget(): HeaderBarWidget?

    abstract fun provideMainImageBackgroundWidget(): AppCompatImageView?

    abstract fun providePrimaryImageView(): AppCompatImageView?
    abstract fun providePrimaryImageViewBig(): AppCompatImageView?

    abstract fun provideGhostImageView(): AppCompatImageView?

    /**
     * Provides the interface to access SubText
     *
     * @return [TextView]
     */
    abstract fun provideSubTitle(): ResourceTextView?

    abstract fun provideVisionSubTitle(): ResourceTextView?
/*---------------------------------------------------X---X---X---------------------------------------------------*/
}
