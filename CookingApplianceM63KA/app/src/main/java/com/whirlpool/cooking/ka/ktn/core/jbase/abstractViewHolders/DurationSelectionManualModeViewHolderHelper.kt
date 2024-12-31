/*
 * File :
 * Author : SINGHA80.
 * Created On : 3/28/24, 3:24 PM
 * Details :
 */
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
 * File : core.jbase.abstractViewHolders.DurationSelectionManualModeViewHolderHelper
 * Author : SINGHA80.
 * Created On : 3/22/24
 * Details : Provides helper method FragmentManualModeTumblerBinding layout
 */

class DurationSelectionManualModeViewHolderHelper :
    AbstractDurationSelectionManualModeViewHolder() {

    private var fragmentTumblerBinding: FragmentManualModeTumblerBinding? = null

    /**
     * Inflate the customized view
     * @param inflater [LayoutInflater]
     * @param container [ViewGroup]
     * @param savedInstanceState [Bundle]
     * @return [View]
     */
    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentTumblerBinding =
            FragmentManualModeTumblerBinding.inflate(inflater!!, container, false)
        return fragmentTumblerBinding?.root
    }

    /**
     * Clean up the view holder when it is destroyed
     */
    override fun onDestroyView() {
        fragmentTumblerBinding = null
    }

    override fun fragmentStringTumblerBinding(): FragmentManualModeTumblerBinding? {
        return fragmentTumblerBinding
    }

    override fun provideGhostButton(): ResourceTextView? {
        return fragmentTumblerBinding?.btnGhost
    }

    override fun providePrimaryButton(): ResourceTextView? {
        return fragmentTumblerBinding?.btnPrimary
    }

    override fun providePrimaryButtonConstraint(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintDurationRight
    }

    override fun provideGhostButtonConstraint(): ConstraintLayout? {
        return fragmentTumblerBinding?.constraintDurationLeft
    }

    override fun provideDurationTumbler(): BaseTumbler? {
        return fragmentTumblerBinding?.tumblerString
    }

    override fun provideHeaderBarWidget(): HeaderBarWidget? {
        return fragmentTumblerBinding?.headerBar
    }

    override fun providePrimaryImageView(): AppCompatImageView? {
        return fragmentTumblerBinding?.imgPrimary
    }

    override fun provideGhostImageView(): AppCompatImageView? {
        return fragmentTumblerBinding?.imgGhost
    }

}