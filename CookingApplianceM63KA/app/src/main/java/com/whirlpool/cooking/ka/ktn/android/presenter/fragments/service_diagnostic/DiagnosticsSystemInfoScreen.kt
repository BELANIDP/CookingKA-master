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
import com.whirlpool.cooking.ka.databinding.DiagnosticsSystemInfoListItemBinding
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsListItemViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsMatrixListViewProvider
import com.whirlpool.hmi.uicomponents.widgets.list.ListView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AudioManagerUtils
import core.utils.gone

/**
 * File       : android.presenter.fragments.service_diagnostic.DiagnosticsSystemInfoScreen
 * Brief      : AbstractDiagnosticsMatrixListViewProvider instance for Diagnostics List of system information.
 * Author     : Rajendra
 * Created On : 26-06-2024
 * Details    : Diagnostics System Info list Screen used for represents the user interface for the system information screen.
 */
class DiagnosticsSystemInfoScreen : AbstractDiagnosticsMatrixListViewProvider() {
    private var diagnosticsSystemInfoViewBinding: DiagnosticsListScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        diagnosticsSystemInfoViewBinding =
            DiagnosticsListScreenBinding.inflate(inflater, container, false)
        return diagnosticsSystemInfoViewBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
    }

    private fun managePreferencesCollectionHeaderBar() {
        diagnosticsSystemInfoViewBinding?.titleBar?.setLeftIconVisibility(true)
        diagnosticsSystemInfoViewBinding?.titleBar?.setRightIconVisibility(false)
        diagnosticsSystemInfoViewBinding?.titleBar?.setOvenCavityIconVisibility(false)
        diagnosticsSystemInfoViewBinding?.titleBar?.setInfoIconVisibility(false)
        diagnosticsSystemInfoViewBinding?.textSubText?.visibility = View.VISIBLE
        diagnosticsSystemInfoViewBinding?.titleBar?.getBinding()?.clOvenCavity?.gone()
       addMarginTopToHeaderView(diagnosticsSystemInfoViewBinding)

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

    override fun onDestroyView() {
        diagnosticsSystemInfoViewBinding = null
    }

    override fun provideResources(): Resources {
        return diagnosticsSystemInfoViewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return diagnosticsSystemInfoViewBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return diagnosticsSystemInfoViewBinding?.titleBar?.getLeftImageView()
    }


    override fun provideListView(): ListView? {
        return diagnosticsSystemInfoViewBinding?.list
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.text_header_system_info)
    }

    override fun provideDiagnosticsListItemViewProvider(): AbstractDiagnosticsListItemViewProvider {
        return object : AbstractDiagnosticsListItemViewProvider() {
            var listItemBinding: DiagnosticsSystemInfoListItemBinding? = null

            override fun inflate(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int) {
                listItemBinding = DiagnosticsSystemInfoListItemBinding.inflate(inflater, parent, false)
            }

            override fun getView(): View? {
                return listItemBinding?.root
            }

            override fun provideListItemTitleTextView(): ResourceTextView? {
                return listItemBinding?.title
            }

            override fun provideListItemSubTitleTextView(): ResourceTextView? {
                return null
            }

            override fun provideListItemValueTextView(): TextView? {
                return null
            }

            override fun provideListItemNavigationIconView(): ImageView? {
                return listItemBinding?.iconNavigation
            }
        }
    }
    /**
     * Runtime add margin to view for adjust aligment
     * @param binding - binding for getting reference ids
     */
    private fun addMarginTopToHeaderView(binding: DiagnosticsListScreenBinding?) {
        val constraintSet = ConstraintSet()
        val constraintLayoutId = binding?.parentView
        constraintSet.clone(constraintLayoutId)
        val marginStart =provideResources().getDimension(R.dimen.extra_header_margin_removed_17dp).toInt()
        constraintSet.setMargin(R.id.list, ConstraintSet.TOP, marginStart)
        constraintSet.applyTo(constraintLayoutId)
    }
}