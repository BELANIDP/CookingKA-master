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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DiagnosticsStatusScreenBinding
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsComponentStatusViewProvider
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.progress.HorizontalProgressBar
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.textview.TimerTextView
import core.utils.AudioManagerUtils

/**
 * File       : com.whirlpool.cooking.diagnostic.DiagnosticsHomeScreen
 * Brief      : AbstractDiagnosticsHomeViewProvider instance for Diagnostics Home Screen
 * Author     : Rajendra
 * Created On : 21-06-2024
 * Details    : Diagnostics Status Screen which allow user to see Service Diagnostics test status
 */
class DiagnosticsComponentStatusScreen : AbstractDiagnosticsComponentStatusViewProvider() {
    private var diagnosticsComponentStatusViewBinding: DiagnosticsStatusScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View? {
        diagnosticsComponentStatusViewBinding =
            DiagnosticsStatusScreenBinding.inflate(inflater, container, false)
        return diagnosticsComponentStatusViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
    }

    private fun managePreferencesCollectionHeaderBar() {
        diagnosticsComponentStatusViewBinding?.headerBar?.setLeftIconVisibility(true)
        diagnosticsComponentStatusViewBinding?.headerBar?.setRightIconVisibility(false)
        diagnosticsComponentStatusViewBinding?.headerBar?.setOvenCavityIconVisibility(false)
        diagnosticsComponentStatusViewBinding?.headerBar?.setInfoIconVisibility(false)
    }

    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
    }

    override fun provideBackButtonView(): View? {
        return diagnosticsComponentStatusViewBinding?.headerBar?.getLeftImageView()
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
        diagnosticsComponentStatusViewBinding = null
    }

    override fun provideResources(): Resources {
        return diagnosticsComponentStatusViewBinding?.root?.resources as Resources
    }

    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun provideProgressBarView(): HorizontalProgressBar? {
        return diagnosticsComponentStatusViewBinding?.progressBar
    }

    override fun provideTimerTextView(): TimerTextView? {
        return diagnosticsComponentStatusViewBinding?.textTime
    }

    override fun provideComponentNameTextView(): ResourceTextView? {
        return diagnosticsComponentStatusViewBinding?.componentLoad
    }

    override fun provideComponentStateTextView(): ResourceTextView? {
        return diagnosticsComponentStatusViewBinding?.componentLoadState
    }

    override fun provideComponentStopButtonView(): NavigationButton? {
        return diagnosticsComponentStatusViewBinding?.buttonSecondary
    }

    override fun provideStopButtonText(): CharSequence {
        return provideResources().getString(R.string.text_button_stop)
    }

    override fun provideSensorFeedbackButtonText(): CharSequence {
        return provideResources().getString(R.string.text_button_sensor_feedback)
    }

    override fun provideSensorFeedbackButtonView(): NavigationButton? {
        return diagnosticsComponentStatusViewBinding?.primaryButton
    }

    override fun provideComponentStatusInfoTextView(): TextView? {
        return diagnosticsComponentStatusViewBinding?.textComponentActivation
    }

    override fun provideStatusInfoText(): CharSequence {
        return provideResources().getString(R.string.str_component_activation)
    }

    override fun handleStopButtonEvents(enableButton: Boolean) {
        if (enableButton) {
            diagnosticsComponentStatusViewBinding?.buttonSecondary?.context?.let {
                diagnosticsComponentStatusViewBinding?.buttonSecondary?.setTextColor(
                    ContextCompat.getColor(
                        it,
                        R.color.diagnostics_primary_text
                    )
                )
            }
        } else {
            diagnosticsComponentStatusViewBinding?.buttonSecondary?.context?.let {
                diagnosticsComponentStatusViewBinding?.buttonSecondary?.setTextColor(
                    ContextCompat.getColor(
                        it,
                        R.color.diagnostics_disabled_text
                    )
                )
            }
        }
    }
}