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
 * File       : android.presenter.fragments.service_diagnostic.DiagnosticsComponentActivationScreen
 * Brief      : DiagnosticsComponentActivationViewProvider instance for Diagnostics Component Activation Screen
 * Author     : Rajendra
 * Created On : 20-06-2024
 * Details    : Diagnostics Component Activation Screen which allow user to see list of Component Activation options
 */
class DiagnosticsComponentActivationScreen : AbstractDiagnosticsMatrixListViewProvider() {
    private var fragmentBinding: DiagnosticsListScreenBinding? = null
    private var resources: Resources? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = DiagnosticsListScreenBinding.inflate(inflater, container, false)
        resources = fragmentBinding?.root?.resources
        return fragmentBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
        addMarginStartToTitleView(fragmentBinding)
    }

    private fun managePreferencesCollectionHeaderBar() {
        fragmentBinding?.titleBar?.setLeftIconVisibility(true)
        fragmentBinding?.titleBar?.setRightIconVisibility(false)
        fragmentBinding?.titleBar?.setOvenCavityIconVisibility(false)
        fragmentBinding?.titleBar?.setInfoIconVisibility(false)
        fragmentBinding?.textSubText?.visibility = View.VISIBLE
    }

    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
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

    override fun onDestroyView() {
        fragmentBinding = null
    }

    override fun provideResources(): Resources {
        return fragmentBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return fragmentBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return fragmentBinding?.textSubText
    }

    override fun provideLeftNavigationView(): View {
        return fragmentBinding?.titleBar?.getLeftImageView() as View
    }

    override fun provideRightNavigationView(): View? {
        return null
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.str_component_activation)
    }

    override fun provideSubTitleText(): CharSequence {
        return provideResources().getString(R.string.text_core_helper_info_tech_guide)
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

    override fun provideListView(): ListView? {
        return fragmentBinding?.list
    }

    override fun provideDiagnosticsListItemViewProvider(): AbstractDiagnosticsListItemViewProvider {
        return object : AbstractDiagnosticsListItemViewProvider() {
            var listItemBinding: DiagnosticsVerticalListItemBinding? = null

            override fun inflate(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int) {
                listItemBinding = DiagnosticsVerticalListItemBinding.inflate(inflater, parent, false)
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

            override fun provideListItemNavigationIconView(): ImageView? {
                return listItemBinding?.iconNavigation
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
        val marginStart = provideResources().getDimension(R.dimen.extra_header_margin_removed_18dp).toInt()
        constraintSet.setMargin(R.id.text_sub_text, ConstraintSet.TOP, marginStart)
        constraintSet.applyTo(constraintLayoutId)
    }
}