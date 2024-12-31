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
import com.whirlpool.cooking.ka.databinding.DiagnosticsEntryScreenBinding
import com.whirlpool.cooking.ka.databinding.LayoutDiagnosticsPopupFragmentBinding
import com.whirlpool.hmi.diagnostics.services.models.BaseServiceViewModel
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsEntryViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsPopupViewProvider
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.uicomponents.widgets.textview.KeypadTextView
import com.whirlpool.hmi.utils.ContextProvider.getContext
import core.utils.AppConstants
import core.utils.AppConstants.SERVICE_DIAGNOSTIC_ENTRY
import core.utils.AudioManagerUtils
import core.utils.HMILogHelper
import core.utils.gone
import core.utils.visible


/**
 * File       : android.presenter.fragments.service_diagnostic.DiagnosticsEntryScreen
 * Brief      : DiagnosticsEntryViewProvider instance for Diagnostics Entry Screen
 * Author     : Rajendra/Nikki
 * Created On : 19-06-2024
 * Details    : Diagnostics Entry Screen which allow user to enter Service Diagnostics
 */
class DiagnosticsEntryScreen : AbstractDiagnosticsEntryViewProvider() {
    private var viewBinding: DiagnosticsEntryScreenBinding? = null
    private var entryCodeLength = 9

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DiagnosticsEntryScreenBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        SERVICE_DIAGNOSTIC_ENTRY = true
        initHeader()
    }

    override fun onDestroyView() {
        SERVICE_DIAGNOSTIC_ENTRY = false
        viewBinding = null
    }

    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
    }

    override fun onViewClicked(view: View?) {
        try {
            AudioManagerUtils.playOneShotSound(
                view?.context,
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
        } catch (e: Exception) {
            HMILogHelper.Logd("Handling multiple audio click event $e")
        }
    }

    override fun provideResources(): Resources {
        return viewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideTitleText(): String {
        return provideResources().getString(R.string.text_header_enter_service_diagnostics)
    }

    override fun provideSubTitleText(): CharSequence {
        return ""
    }

    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun provideEntryKeypadTextView(): KeypadTextView? {
        return viewBinding?.titleBar?.getHeaderKeypadTextView()
    }

    override fun provideKeypadBackSpaceButton(): View {
        return viewBinding?.titleBar?.getCancelView() as View
    }

    override fun providePrimaryButton(): NavigationButton {
        return viewBinding?.nextButton as NavigationButton
    }

    override fun provideLeftNavigationView(): View {
        return viewBinding?.titleBar?.getBackButtonView() as View
    }

    override fun provideHelperTextView(): TextView? {
        validateKeyboardEntry()
        return null
    }

    override fun provideKeyboardView(): KeyboardView {
        return viewBinding?.keyboard as KeyboardView
    }


    override fun provideLeftIconResource(): Int {
        // Header Bar left navigation icon from sdk
        return 0
    }

    override fun provideEntryConfirmationPopupViewProvider(): AbstractDiagnosticsPopupViewProvider {
        return object : AbstractDiagnosticsPopupViewProvider() {
            var popupScreenBinding: LayoutDiagnosticsPopupFragmentBinding? = null

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                popupScreenBinding =
                    LayoutDiagnosticsPopupFragmentBinding.inflate(inflater, container, false)
                popupScreenBinding?.textViewTitle?.visibility = View.VISIBLE
                return popupScreenBinding?.root
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                //TODO:Audio asset need to update once GCD finalize the audio assets
                AudioManagerUtils.playOneShotSound(
                    getContext(),
                    R.raw.audio_alert,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
            }

            override fun onDestroyView() {
                popupScreenBinding = null
            }

            override fun provideResources(): Resources {
                return popupScreenBinding?.root?.resources as Resources
            }

            override fun providePrimaryButton(): NavigationButton {
                return popupScreenBinding?.textButtonRight as NavigationButton
            }

            override fun provideSecondaryButton(): NavigationButton {
                return popupScreenBinding?.textButtonLeft as NavigationButton
            }

            override fun provideEntryPopupTitleText(): CharSequence {
                return provideResources().getString(R.string.text_header_enter_service_diagnostics)
            }

            override fun provideEntryPopupDescriptionText(): CharSequence {
                return provideResources().getText(R.string.text_layout_pop_up_decision_enter_service)
            }

            override fun provideLeftNavigationButton(): View? {
                return null
            }

            override fun provideRightNavigationButton(): View? {
                return null
            }

            override fun provideTitleTextView(): TextView {
                return popupScreenBinding?.textViewTitle as TextView
            }

            override fun provideSubTitleTextView(): TextView? {
                return null
            }

            override fun provideDescriptionTextView(): TextView {
                return popupScreenBinding?.textViewDescription as TextView
            }

            override fun provideEntryPopupPrimaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_enter)
            }

            override fun provideEntryPopupSecondaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_exit)
            }
        }
    }

    override fun updatePrimaryButtonInteraction(enableInteraction: Boolean) {
        if (enableInteraction) {
            viewBinding?.nextButton?.setTextColor(
                ContextCompat.getColor(
                    viewBinding?.nextButton?.context!!,
                    R.color.diagnostics_primary_text
                )
            )
        } else {
            viewBinding?.nextButton?.setTextColor(
                ContextCompat.getColor(
                    viewBinding?.nextButton?.context!!,
                    R.color.diagnostics_disabled_text
                )
            )
        }
    }

    /**
     * Init header view visibility and populate default text
     */
    private fun initHeader() {
        viewBinding?.titleBar?.setTitleTextViewVisibility(false)
        viewBinding?.titleBar?.setKeypadTextViewVisibility(true)
        viewBinding?.titleBar?.setTumblerIconVisibility(false)
        viewBinding?.titleBar?.getHeaderKeypadTextView()
            ?.setDefaultValue(AppConstants.DEFAULT_ENTRY_NUMBER)
    }

    /**
     * validate keybaord entry password and accordignly visible/gone the helper textview
     */
    private fun validateKeyboardEntry() {
        val keyboardTextValue = viewBinding?.titleBar?.getHeaderKeypadTextView()?.value
        if (keyboardTextValue?.isNotEmpty() == true && keyboardTextValue.length == entryCodeLength && viewBinding?.nextButton?.isPressed == true) {
            val isValid = BaseServiceViewModel.isKeyValid(keyboardTextValue)
            if (isValid) {
                viewBinding?.textViewHelperText?.visible()
                viewBinding?.textViewHelperRedWarningText?.gone()
            } else {
                viewBinding?.textViewHelperText?.gone()
                viewBinding?.textViewHelperRedWarningText?.visible()
                viewBinding?.textViewHelperRedWarningText?.text =
                    provideResources().getString(R.string.text_core_helper_numpad_error_correct_pin)
            }
        } else {
            viewBinding?.textViewHelperText?.visible()
            viewBinding?.textViewHelperRedWarningText?.gone()
        }
    }
}