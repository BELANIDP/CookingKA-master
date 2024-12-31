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
import android.os.CountDownTimer
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DiagnosticsResetConfirmationBinding
import com.whirlpool.hmi.diagnostics.models.DiagnosticsManager
import com.whirlpool.hmi.diagnostics.services.cooking.CookingServiceViewModel
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsConfirmationViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsPopupViewProvider
import com.whirlpool.hmi.uicomponents.audio.WHRAudioManager
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.ContextProvider.getFragmentActivity
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedPreferenceManager.resetViewSharedPreferenceData

/**
 * File       : com.whirlpool.cooking.diagnostic.DiagnosticsResetConfirmationScreen
 * Brief      : AbstractDiagnosticsConfirmationViewProvider instance for Diagnostics Factory Reset Confirmation Screen
 * Author     : Rajendra
 * Created On : 11-04-2024
 * Details    : Diagnostics Reset Confirmation Screen used for Factory Reset Confirmation in which also
 * implemented AbstractDiagnosticsPopupViewProvider to change popup details as per the need.
 */
class DiagnosticsResetConfirmationScreen : AbstractDiagnosticsConfirmationViewProvider(),
    View.OnClickListener {
    private var resetConfirmationViewBinding: DiagnosticsResetConfirmationBinding? = null
    private var isFactoryReset: Boolean = false
    private var isSoftReboot: Boolean = false
    private var rebootTimer: CountDownTimer? = null
    var restoreResetConfirmationPopup: ScrollDialogPopupBuilder? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        resetConfirmationViewBinding =
            DiagnosticsResetConfirmationBinding.inflate(inflater, container, false)
        return resetConfirmationViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
    }

    override fun provideEnterTransition(context: Context): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context): Transition? {
        return null
    }

    private fun initView() {
        resetConfirmationViewBinding?.primaryResetButton?.setOnClickListener(this)
        resetConfirmationViewBinding?.secondaryResetButton?.setOnClickListener(this)
        resetConfirmationViewBinding?.primaryResetButton?.text = provideResources().getString(R.string.text_button_proceed)
        resetConfirmationViewBinding?.secondaryResetButton?.text = provideResources().getString(R.string.text_button_cancel)
        resetConfirmationViewBinding?.icon112pxAlert?.setImageResource(R.drawable.factory_reoobt_alert)
    }


    private fun showConfirmationPopup() {
        if (restoreResetConfirmationPopup == null) {
            HMILogHelper.Logd("showing service diagnostic confirmation popup")
            restoreResetConfirmationPopup = providePopupHeadLineString().let {
                providePopupBodyString().let { it1 ->
                    ScrollDialogPopupBuilder.Builder(R.layout.layout_restore_factory_popup_fragment)
                        .setHeaderTitle(R.string.text_dynamic_popup_content, it)
                        .setDescriptionMessage(it1)
                        .setRightButton(R.string.text_button_yes_continue) {
                            AudioManagerUtils.playOneShotSound(
                                ContextProvider.getContext(),
                                R.raw.button_press,
                                AudioManager.STREAM_SYSTEM,
                                true,
                                0,
                                1
                            )
                            startFactoryResetAndSoftReboot()
                            true
                        }.setLeftButton(R.string.text_button_cancel) {
                            //TODO:Audio asset need to update once GCD finalize the audio assets
                            AudioManagerUtils.playOneShotSound(
                                ContextProvider.getContext(),
                                R.raw.invalid_press,
                                AudioManager.STREAM_SYSTEM,
                                true,
                                0,
                                1
                            )
                            true
                        }
                        .setTopMarginForTitleText(AppConstants.RESTORE_POPUP_VERTICAL_BOTTOM_SMALL_MARGIN)
                        .setHeaderViewCenterIcon(AppConstants.HEADER_RESTORE_LAYOUT_VIEW, false)
                        .setTitleTextGravity(Gravity.CENTER_HORIZONTAL)
                        .setDescriptionTextGravity(Gravity.CENTER_HORIZONTAL)
                        .build()
                }
            }

            //Knob Implementation
            val hmiKnobListener = PopUpBuilderUtils.observeHmiKnobListener(
                onHMIRightKnobClick = {
                    //do nothing leave it blank
                },
                onHMILeftKnobClick = {
                    //do nothing leave it blank
                },
                onKnobSelectionTimeout = {
                    //do nothing leave it blank
                }
            )
            restoreResetConfirmationPopup?.setOnDialogCreatedListener(object :
                ScrollDialogPopupBuilder.OnDialogCreatedListener {
                override fun onDialogCreated() {
                    HMIExpansionUtils.setHMIKnobInteractionListener(hmiKnobListener)
                    //TODO:Audio asset need to update once GCD finalize the audio assets
                    AudioManagerUtils.playOneShotSound(
                        ContextProvider.getContext(),
                        R.raw.audio_alert,
                        AudioManager.STREAM_SYSTEM,
                        true,
                        0,
                        1
                    )
                }

                override fun onDialogDestroy() {
                    HMIExpansionUtils.removeHMIKnobInteractionListener(hmiKnobListener)
                    if (restoreResetConfirmationPopup != null) {
                        restoreResetConfirmationPopup?.dismiss()
                        restoreResetConfirmationPopup = null
                    }
                }
            })
            NavigationUtils.getVisibleFragment()?.parentFragmentManager.let {
                if (it != null) {
                    if (restoreResetConfirmationPopup?.isAdded == false) {
                        restoreResetConfirmationPopup?.show(it, "CONFIRMATION_POPUP")
                    }
                }
            }
        }
    }

    private fun startFactoryResetAndSoftReboot() {
        startRebootTimer()
        CookingAppUtils.setFactoryRestoreStarted(true)
        resetConfirmationViewBinding?.doNotUnplugAppliance?.setText(R.string.text_do_not_unplug_appliance_description)
        resetConfirmationViewBinding?.doNotUnplugAppliance?.visibility = View.VISIBLE
        resetConfirmationViewBinding?.iconHourGlassTimer?.setImageResource(R.drawable.hour_glass_timer)
        resetConfirmationViewBinding?.iconHourGlassTimer?.visibility = View.VISIBLE
        resetConfirmationViewBinding?.layout?.visibility = View.GONE
    }

    private fun startRebootTimer() {
        //for now we have kept as 10 sec , once GCD confirm will update the animation time
        rebootTimer = object : CountDownTimer(AppConstants.SOFT_REBBOT_ANIMATION_DELAY, AppConstants.SOFT_REBBOT_ANIMATION_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                rebootTimer?.cancel()
                rebootTimer = null
                resetViewSharedPreferenceData()
                performFactoryRestoreAndSoftReboot()
            }
        }.start()
        WHRAudioManager.getInstance()
            .playAudio(resetConfirmationViewBinding?.layout?.context, R.raw.power_off)
    }

    fun performFactoryRestoreAndSoftReboot() {
        if (isFactoryReset) {
            CookingServiceViewModel.getCookingServiceViewModel()?.let {
                // Use the viewModel instance
                HMILogHelper.Logd("Service Diagnostic: Performing factory restore")
                DiagnosticsManager.getInstance().disengageDiagnosticsFromHmiKeys()
                CookingServiceViewModel.getCookingServiceViewModel().factoryReset()
            }
        } else if (isSoftReboot) {
            CookingServiceViewModel.getCookingServiceViewModel()?.let {
                HMILogHelper.Logd("Service Diagnostic: Performing Soft reboot")
                DiagnosticsManager.getInstance().disengageDiagnosticsFromHmiKeys()
                CookingServiceViewModel.getCookingServiceViewModel().requestSoftRebootAllNodes()
            }
        } else {
            HMILogHelper.Logd("Service Diagnostic: wrong selection")
        }
    }

    private fun providePopupHeadLineString(): String {
        return if (isFactoryReset) {
            provideResources().getString(R.string.text_header_restore_factory_defaults)
        } else {
            provideResources().getString(R.string.str_soft_reboot)
        }
    }

    private fun providePopupBodyString(): String {
        return if (isFactoryReset) {
            provideResources().getString(R.string.text_layout_pop_up_decision_restore_factory_defaults)
        } else {
            //TODO: need to check with GCD about string
            provideResources().getString(R.string.text_layout_pop_up_decision_restore_factory_defaults)
        }
    }

    override fun onDestroyView() {
        resetConfirmationViewBinding = null
    }

    override fun provideResources(): Resources {
        return resetConfirmationViewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return resetConfirmationViewBinding?.headerTextTitle
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return null
    }

    override fun provideRightNavigationView(): View? {
        return null
    }

    override fun provideDescriptionTextView(): TextView? {
        return resetConfirmationViewBinding?.descriptionText
    }

    override fun providePrimaryButton(): NavigationButton? {
        return null
    }

    override fun provideSecondaryButton(): NavigationButton? {
        return null
    }

    override fun provideLoadingProgressBarView(): ProgressBar? {
        return null
    }

    override fun provideTitleTextFactoryReset(): CharSequence {
        return provideResources().getString(R.string.restore_fatory_defaults)
    }

    override fun provideTitleTextSoftReboot(): CharSequence {
        return provideResources().getString(R.string.text_header_soft_reboot)
    }

    override fun provideDescriptionTextFactoryReset(): CharSequence {
        isFactoryReset = true
        isSoftReboot = false
        return provideResources().getString(R.string.text_restore_factory_default_info)
    }

    override fun provideDescriptionTextSoftReboot(): CharSequence {
        isSoftReboot = true
        isFactoryReset = false
        return provideResources().getString(R.string.text_layout_information_paragraph_qr_code_restart_appliance)
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun providePrimaryButtonText(): CharSequence? {
        return null
    }

    override fun provideSecondaryButtonText(): CharSequence? {
        return null
    }

    override fun provideHourGlassView(): View? {
        return null
    }

    override fun provideConfirmationPopupViewProvider(): AbstractDiagnosticsPopupViewProvider? {
        return null
    }

    override fun onClick(view: View) {
        if (view.id == R.id.primary_reset_button) {
            try {
                showConfirmationPopup()
            } catch (exp: Exception) {
                HMILogHelper.Logd("Service Diagnostic: onClick$exp")
            }
        } else if (view.id == R.id.secondary_reset_button) {
            NavigationViewModel.popBackStack(resetConfirmationViewBinding?.root?.let {
                Navigation.findNavController(
                    it
                )
            })
        }
    }
}