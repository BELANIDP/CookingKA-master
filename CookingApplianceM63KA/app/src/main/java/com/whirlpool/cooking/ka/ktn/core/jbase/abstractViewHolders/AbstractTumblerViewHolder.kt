package core.jbase.abstractViewHolders

import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.whirlpool.cooking.ka.databinding.FragmentTumblerBinding
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler

@Suppress("unused")
abstract class AbstractTumblerViewHolder {
    // ================================================================================================================
    // -----------------------------------------------  General Methods  ----------------------------------------------
    // ================================================================================================================
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

    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    abstract fun onDestroyView()

    /*---------------------------------------------------------------------------------------------------------------*/

    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access the binding class
     *
     * @return [FragmentTumblerBinding]
     */
    abstract val fragmentBinding: FragmentTumblerBinding?

    /*---------------------------------------------------X---X---X---------------------------------------------------*/


    // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================

    /*---------------------------------------------------X---X---X---------------------------------------------------*/ // ================================================================================================================
    // ------------------------------------------------  Common Views  ------------------------------------------------
    // ================================================================================================================
    /**
     * Provides the interface to access bottom left button
     *
     * @return [ResourceTextView]
     */
    abstract fun provideGhostButton(): ResourceTextView?

    /*---------------------------------------------------------------------------------------------------------------*/


    /*---------------------------------------------------------------------------------------------------------------*/

    /*---------------------------------------------------------------------------------------------------------------*/ /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access bottom right button
     *
     * @return [ResourceTextView]
     */
    abstract fun provideGhostConstraint(): ConstraintLayout?

    /*---------------------------------------------------------------------------------------------------------------*/


    /*---------------------------------------------------------------------------------------------------------------*/

    /*---------------------------------------------------------------------------------------------------------------*/ /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access bottom right button
     *
     * @return [ConstraintLayout]
     */
    abstract fun providePrimaryConstraint(): ConstraintLayout?

    /*---------------------------------------------------------------------------------------------------------------*/


    /*---------------------------------------------------------------------------------------------------------------*/

    /*---------------------------------------------------------------------------------------------------------------*/ /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access bottom right button
     *
     * @return [ConstraintLayout]
     */
    abstract fun providePrimaryButton(): ResourceTextView?

    /*---------------------------------------------------------------------------------------------------------------*/

    /*---------------------------------------------------------------------------------------------------------------*/
    /**
     * Provides the interface to access BaseTumbler widget
     *
     * @return [BaseTumbler]
     */
    abstract fun provideNumericTumbler(): BaseTumbler?

    abstract fun provideHeaderBarView(): HeaderBarWidget?

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    // ================================================================================================================

    // ================================================================================================================
    // -------------------------------------------  Modality Specific Views  ------------------------------------------
    // ================================================================================================================
    /**
     * Provides the interface to access DegreeTypeTextView
     *
     * @return [TextView]
     */
    open fun provideDegreeTypeTextView(): TextView? {
        return null
    }

    /**
     * Provides the interface to access Next screen
     *
     * @return
     */
    open fun provideSummaryScreenAction(): Int {
        return 0
    }

    /**
     * Provides the interface to access Next screen for delay
     *
     * @return
     */
    open fun provideDelaySetScreenAction(): Int {
        return 0
    }

    /**
     * Provides the interface to access Next screen for delay
     *
     * @return
     */
    open fun provideDelaySetScreenActionMW(): Int {
        return 0
    }
    /*---------------------------------------------------X---X---X---------------------------------------------------*/


}

