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
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DiagnosticsListItemTumblerBinding
import com.whirlpool.cooking.ka.databinding.FragmentTumblerBinding
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsComponentProgrammingViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsTumblerItemViewProvider
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import core.utils.AudioManagerUtils

/**
 * File       : android.presenter.fragments.service_diagnostic.DiagnosticsComponentProgrammingScreen
 * Brief      : AbstractDiagnosticsComponentProgrammingViewProvider instance for Diagnostics Component Programming
 * Author     : Rajendra
 * Created On : 21-06-2024
 * Details    : Diagnostics Component Programming Screen check component and sensor feedback
 */
class DiagnosticsComponentProgrammingScreen :
    AbstractDiagnosticsComponentProgrammingViewProvider() {
    private var diagnosticsComponentProgrammingViewBinding: FragmentTumblerBinding? =
        null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        diagnosticsComponentProgrammingViewBinding =
            FragmentTumblerBinding.inflate(inflater, container, false)
        return diagnosticsComponentProgrammingViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
        diagnosticsComponentProgrammingViewBinding?.tumblerNumericBased?.visibility = View.GONE
        diagnosticsComponentProgrammingViewBinding?.serviceTumblerNumericBased?.visibility = View.VISIBLE
        diagnosticsComponentProgrammingViewBinding?.primaryButton?.visibility = View.VISIBLE
    }

    private fun managePreferencesCollectionHeaderBar() {
        diagnosticsComponentProgrammingViewBinding?.headerBar?.setLeftIconVisibility(true)
        diagnosticsComponentProgrammingViewBinding?.headerBar?.setRightIconVisibility(false)
        diagnosticsComponentProgrammingViewBinding?.headerBar?.setOvenCavityIconVisibility(false)
        diagnosticsComponentProgrammingViewBinding?.headerBar?.setInfoIconVisibility(false)
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

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun provideRightIconResource(): Int {
        return 0
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun onDestroyView() {
        diagnosticsComponentProgrammingViewBinding = null
    }

    override fun provideResources(): Resources {
        return diagnosticsComponentProgrammingViewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return diagnosticsComponentProgrammingViewBinding?.headerBar?.getHeaderTitle()
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return diagnosticsComponentProgrammingViewBinding?.headerBar?.getLeftImageView()
    }

    override fun provideRightNavigationView(): View? {
        return null
    }

    override fun providePrimaryButtonView(): NavigationButton? {
        return diagnosticsComponentProgrammingViewBinding?.primaryButton
    }

    override fun provideTumblerView(): BaseTumbler? {
        return diagnosticsComponentProgrammingViewBinding?.serviceTumblerNumericBased
    }

    override fun providePrimaryButtonText(): CharSequence {
        return provideResources().getString(R.string.text_button_start)
    }

    override fun provideComponentProgrammingTumblerItemViewProvider(): AbstractDiagnosticsTumblerItemViewProvider? {
        return null
    }

    override fun provideComponentProgrammingTumblerBigItemViewProvider(): AbstractDiagnosticsTumblerItemViewProvider {
        return object : AbstractDiagnosticsTumblerItemViewProvider() {
            var listItemBinding: DiagnosticsListItemTumblerBinding? = null

            override fun inflate(inflater: LayoutInflater, parent: ViewGroup?) {
                listItemBinding = DiagnosticsListItemTumblerBinding.inflate(inflater, parent, false)
            }

            override fun getView(): View? {
                return listItemBinding?.root
            }

            override fun provideTumblerItemTitleTextView(): ResourceTextView? {
                return listItemBinding?.tumblerItemTitle
            }
        }
    }
}