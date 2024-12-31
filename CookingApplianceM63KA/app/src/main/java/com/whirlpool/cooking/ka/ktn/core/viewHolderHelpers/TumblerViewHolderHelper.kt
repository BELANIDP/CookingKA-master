package core.viewHolderHelpers

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

class TumblerViewHolderHelper : core.jbase.abstractViewHolders.AbstractTumblerViewHolder() {

    var fragmentTumblerBinding: FragmentTumblerBinding? = null


    // ================================================================================================================
    // -----------------------------------------  General Methods Definitions  ----------------------------------------
    // ================================================================================================================

    /**
     * Inflate the customized view

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
        fragmentTumblerBinding = FragmentTumblerBinding.inflate(inflater!!)
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
     * @return [FragmentTumblerBinding]
     */

    override val fragmentBinding: FragmentTumblerBinding?
        get() = fragmentTumblerBinding

    /**
     * Provides the left button id
     *
     * @return [ResourceTextView]
     */
    override fun provideGhostButton(): ResourceTextView? {
        return fragmentTumblerBinding?.btnGhost
    }

    /**
     * Provides the right button id
     *
     * @return [ResourceTextView]
     */
    override fun provideGhostConstraint(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintLeftButton
    }

    /**
     * Provides the right button id
     *
     * @return [ConstraintLayout]
     */
    override fun providePrimaryButton(): ResourceTextView? {
        return fragmentTumblerBinding?.btnPrimary
    }


    /**
     * Definition for the BaseTumbler widget
     * @return [BaseTumbler]
     */
    override fun providePrimaryConstraint(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintRightButton
    }
    /**
     * Definition for the BaseTumbler widget
     * @return [ConstraintLayout]
     */
    override fun provideNumericTumbler(): BaseTumbler? {
        return fragmentTumblerBinding?.tumblerNumericBased
    }

    override fun provideHeaderBarView(): HeaderBarWidget? {
       return fragmentTumblerBinding?.headerBar
    }

    /**
     * Provides the interface to access DegreeTypeTextView
     * @return [TextView]
     */
    override fun provideDegreeTypeTextView(): TextView? {
        return fragmentTumblerBinding?.degreesType
    }

    /*---------------------------------------------------X---X---X---------------------------------------------------*/

}