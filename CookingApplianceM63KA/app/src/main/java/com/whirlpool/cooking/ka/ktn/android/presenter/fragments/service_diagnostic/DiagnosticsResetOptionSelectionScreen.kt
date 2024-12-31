/*
 *  *----------------------------------------------------------------------------------------------*
 *  * ---- Copyright 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL --------------*
 *  * ---------------------------------------------------------------------------------------------*
 */
package android.presenter.fragments.service_diagnostic


import android.content.Context
import android.content.res.Resources
import android.media.AudioManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DiagnosticsListScreenBinding
import com.whirlpool.cooking.ka.databinding.DiagnosticsVerticalListItemBinding
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsListItemViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsMatrixListViewProvider
import com.whirlpool.hmi.uicomponents.widgets.list.ListView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AudioManagerUtils

/**
 * File       : android.presenter.fragments.service_diagnostic.DiagnosticsResetOptionSelectionScreen
 * Brief      : AbstractDiagnosticsMatrixListViewProvider instance for Diagnostics List of reset option
 * Author     : Rajendra
 * Created On : 25-06-2024
 * Details    : Diagnostics Reset Option Screen used for represents the user interface for the reset option selection screen.
 */
class DiagnosticsResetOptionSelectionScreen : AbstractDiagnosticsMatrixListViewProvider() {
    private var resetOptionListSelectionViewBinding: DiagnosticsListScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View? {
        resetOptionListSelectionViewBinding =
            DiagnosticsListScreenBinding.inflate(inflater, container, false)
        return resetOptionListSelectionViewBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
        addMarginStartToTitleView(resetOptionListSelectionViewBinding)
    }

    private fun managePreferencesCollectionHeaderBar() {
        resetOptionListSelectionViewBinding?.titleBar?.setLeftIconVisibility(true)
        resetOptionListSelectionViewBinding?.titleBar?.setRightIconVisibility(false)
        resetOptionListSelectionViewBinding?.titleBar?.setOvenCavityIconVisibility(false)
        resetOptionListSelectionViewBinding?.titleBar?.setInfoIconVisibility(false)
    }
    override fun onViewClicked(view: View?) {
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
    }
    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
    }

    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun provideRightIconResource(): Int {
        return 0
    }

    override fun onDestroyView() {
        resetOptionListSelectionViewBinding = null
    }

    override fun provideResources(): Resources {
        return resetOptionListSelectionViewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return resetOptionListSelectionViewBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return resetOptionListSelectionViewBinding?.titleBar?.getLeftImageView()
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun provideListView(): ListView? {
        return resetOptionListSelectionViewBinding?.list
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.text_hedaer_restore_factory)
    }

    override fun provideDiagnosticsListItemViewProvider(): AbstractDiagnosticsListItemViewProvider {
        return object : AbstractDiagnosticsListItemViewProvider() {
            var listIteBinding: DiagnosticsVerticalListItemBinding? = null

            override fun inflate(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int) {
                listIteBinding = DiagnosticsVerticalListItemBinding.inflate(inflater, parent, false)
            }

            override fun getView(): View? {
                return listIteBinding?.root
            }

            override fun provideListItemTitleTextView(): ResourceTextView? {
                return listIteBinding?.title
            }

            override fun provideListItemSubTitleTextView(): ResourceTextView? {
                return listIteBinding?.subtitle
            }

            override fun provideListItemValueTextView(): TextView? {
                return listIteBinding?.value
            }

            override fun provideListItemNavigationIconView(): ImageView? {
                return listIteBinding?.iconNavigation
            }
        }
    }
    /**
     * Runtime add margin to view for adjust aligment
     * @param binding - binding for getting reference ids
     */
    private fun addMarginStartToTitleView(binding: DiagnosticsListScreenBinding?) {
        val constraintSet = ConstraintSet()
        val constraintLayoutId = binding?.parentView
        constraintSet.clone(constraintLayoutId)
        val marginStart = provideResources().getDimension(R.dimen.diagnostics_list_item_icons_barrier_margin).toInt()
        constraintSet.setMargin(R.id.list, ConstraintSet.TOP, marginStart)
        constraintSet.applyTo(constraintLayoutId)
    }
}