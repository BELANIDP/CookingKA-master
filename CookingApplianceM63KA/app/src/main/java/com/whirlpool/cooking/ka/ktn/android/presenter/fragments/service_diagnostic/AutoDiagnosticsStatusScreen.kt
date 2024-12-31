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
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.AutoDiagnosticsStatusScreenBinding
import com.whirlpool.cooking.ka.databinding.LayoutEndDiagnosticsPopupFragmentBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractAutoDiagnosticsStatusViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsPopupViewProvider
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.progress.HorizontalProgressBar
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.textview.TimerTextView
import core.utils.AudioManagerUtils
import core.utils.gone
import core.utils.visible

/**
 * File       : android.presenter.fragments.service_diagnostic.AutoDiagnosticsStatusScreen
 * Brief      : AbstractAutoDiagnosticsStatusViewProvider instance for Auto Diagnostics Status Screen
 * Author     : NIMMAM
 * Created On : 24-06-2024
 * Details    : Auto Diagnostics status screen.
 */
class AutoDiagnosticsStatusScreen : AbstractAutoDiagnosticsStatusViewProvider() {
    private var diagnosticsStatusViewBinding: AutoDiagnosticsStatusScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        diagnosticsStatusViewBinding =
            AutoDiagnosticsStatusScreenBinding.inflate(inflater, container, false)
        return diagnosticsStatusViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
    }

    private fun managePreferencesCollectionHeaderBar() {
        diagnosticsStatusViewBinding?.titleBar?.setLeftIconVisibility(true)
        diagnosticsStatusViewBinding?.titleBar?.setRightIconVisibility(false)
        diagnosticsStatusViewBinding?.titleBar?.setInfoIconVisibility(false)
        diagnosticsStatusViewBinding?.titleBar?.setOvenCavityIconVisibility(false)
    }

    override fun onDestroyView() {
        diagnosticsStatusViewBinding = null
    }

    override fun provideResources(): Resources {
        return diagnosticsStatusViewBinding?.root?.resources as Resources
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.text_header_auto_diagnostics)
    }

    override fun handleButtonEvent(buttonView: View, isActive: Boolean) {
        val navigationButtonView = buttonView as NavigationButton
        if (isActive) {
            navigationButtonView.setTextColor(
                provideResources().getColor(
                    R.color.diagnostics_primary_text,
                    null
                )
            )
        } else {
            navigationButtonView.setTextColor(
                provideResources().getColor(
                    R.color.diagnostics_disabled_text,
                    null
                )
            )
        }
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

    override fun provideTitleTextView(): ResourceTextView? {
        return diagnosticsStatusViewBinding?.textComponentActivation
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return diagnosticsStatusViewBinding?.titleBar?.getLeftImageView()
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun provideProgressBarView(): HorizontalProgressBar? {
        return diagnosticsStatusViewBinding?.progressBar
    }

    override fun onAutoDiagnosticsCompleted() {
        diagnosticsStatusViewBinding?.buttonSecondary?.visible()
    }

    override fun provideTimerTextView(): TimerTextView? {
        return diagnosticsStatusViewBinding?.textTime
    }

    override fun provideStepTextView(): TextView? {
        return diagnosticsStatusViewBinding?.step
    }

    override fun provideModeNameTextView(): TextView? {
        return diagnosticsStatusViewBinding?.modeName
    }

    override fun provideStepStatusTextView(): TextView? {
        return diagnosticsStatusViewBinding?.statusTextView
    }

    override fun provideStepInstructionTextView(): TextView? {
        return diagnosticsStatusViewBinding?.instruction
    }

    override fun providePrimaryButton(): NavigationButton? {
        return diagnosticsStatusViewBinding?.primaryButton
    }

    override fun provideRunTestButtonText(): CharSequence {
        populateCavityIcon()
        return provideResources().getString(R.string.text_button_run_test)
    }

    override fun provideSkipButtonText(): CharSequence {
        return provideResources().getString(R.string.text_str_skip_step)
    }

    override fun provideResumeButtonText(): CharSequence {
        return provideResources().getString(R.string.text_button_MWO_cook_door_open_resume)
    }

    override fun provideResultButtonText(): CharSequence {
        return provideResources().getString(R.string.str_test_result)
    }

    override fun provideTestCompletedTitle(): CharSequence {
        return provideResources().getString(R.string.text_mode_temp_complete)
    }

    override fun provideTestCompletedInstruction(): CharSequence {
        return provideResources().getString(R.string.text_status_bottom_line_long_pres_results)
    }

    override fun provideSecondaryButton(): NavigationButton? {
        return diagnosticsStatusViewBinding?.buttonSecondary
    }

    override fun provideAutoDiagnosticsExitPopupView(): AbstractDiagnosticsPopupViewProvider {
        return object : AbstractDiagnosticsPopupViewProvider() {
            var popupScreenBinding: LayoutEndDiagnosticsPopupFragmentBinding? = null

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                popupScreenBinding =
                    LayoutEndDiagnosticsPopupFragmentBinding.inflate(inflater, container, false)
                popupScreenBinding?.textViewTitle?.visibility = View.VISIBLE
                return popupScreenBinding?.root
            }

            override fun onDestroyView() {
                popupScreenBinding = null
            }

            override fun provideResources(): Resources {
                return popupScreenBinding?.root?.resources as Resources
            }

            override fun providePrimaryButton(): NavigationButton? {
                return popupScreenBinding?.textButtonRight
            }

            override fun provideSecondaryButton(): NavigationButton? {
                return popupScreenBinding?.textButtonLeft
            }

            override fun provideLeftNavigationButton(): View? {
                return null
            }

            override fun provideRightNavigationButton(): View? {
                return null
            }

            override fun provideTitleTextView(): TextView? {
                return popupScreenBinding?.textViewTitle
            }

            override fun provideSubTitleTextView(): TextView? {
                return null
            }

            override fun provideDescriptionTextView(): TextView? {
                return popupScreenBinding?.textViewDescription
            }

            override fun provideAutoDiagnosticsExitPopupPrimaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_dismiss)
            }

            override fun provideAutoDiagnosticsExitPopupTitleText(): CharSequence {
                return provideResources().getString(R.string.text_auto_diagnostics_exit_popup_title)
            }

            override fun provideAutoDiagnosticsExitPopupDescriptionText(): CharSequence {
                return provideResources().getString(R.string.text_auto_diagnostics_exit_popup_description)
            }

            override fun provideDoorPopupTitleTextClose(): CharSequence {
                return provideResources().getString(R.string.text_header_Prepare_close_door)
            }

            override fun provideDoorPopupTitleTextStart(): CharSequence {
                return provideResources().getString(R.string.text_header_press_start)
            }

            override fun provideDoorPopupTitleTextPaused(): CharSequence {
                return provideResources().getString(R.string.text_header_paused)
            }

            override fun provideDoorPopupDescriptionClose(): CharSequence {
                return provideResources().getString(R.string.sdk_diagnostics_popup_description_close_the_door)
            }

            override fun provideDoorPopupDescriptionStart(): CharSequence {
                return provideResources().getString(R.string.sdk_diagnostics_popup_description_press_start)
            }

            override fun provideDoorPopupDescriptionPause(): CharSequence {
                return provideResources().getString(R.string.sdk_diagnostics_popup_description_close_to_resume)
            }
        }
    }
    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    /**
     * populate cavity icon as per product variant
     */
    private fun populateCavityIcon() {
        when {
            CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO
                    || CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                diagnosticsStatusViewBinding?.cavityIcon?.visible()
                val cavityStatus = String.format(
                    provideResources().getString(R.string.sdk_diagnostics_sub_modes_format),
                    provideResources().getString(R.string.sdk_diagnostics_mode_name_lower)
                )
                when {
                    cavityStatus == diagnosticsStatusViewBinding?.modeName?.text -> diagnosticsStatusViewBinding?.cavityIcon?.setBackgroundResource(
                        R.drawable.ic_lower_cavity
                    )

                    else -> {
                        diagnosticsStatusViewBinding?.cavityIcon?.setBackgroundResource(R.drawable.ic_oven_cavity)
                    }
                }
            }

            else -> {
                diagnosticsStatusViewBinding?.cavityIcon?.gone()
            }
        }
    }
}