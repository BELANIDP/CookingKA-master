/*
 *  *----------------------------------------------------------------------------------------------*
 *  * ---- Copyright 2023. Whirlpool Corporation. All rights reserved - CONFIDENTIAL --------------*
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
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DiagnosticsListScreenBinding
import com.whirlpool.cooking.ka.databinding.DiagnosticsVerticalListItemBinding
import com.whirlpool.cooking.ka.databinding.LayoutAutoDiagnosticsPopupFragmentBinding
import com.whirlpool.cooking.ka.databinding.LayoutDiagnosticsExitPopupFragmentBinding
import com.whirlpool.hmi.diagnostics.models.DiagnosticsManager
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsHomeViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsListItemViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsPopupViewProvider
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.list.ListView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils

/**
 * File       : android.presenter.fragments.service_diagnostic.DiagnosticsHomeScreen
 * Brief      : AbstractDiagnosticsHomeViewProvider instance for Diagnostics Home Screen
 * Author     : Rajendra/Nikki
 * Created On : 24-05-2023
 * Details    : Diagnostics Home Screen which allow user to see list of Service Diagnostics options
 */
class DiagnosticsHomeScreen : AbstractDiagnosticsHomeViewProvider() {
    private var viewBinding: DiagnosticsListScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DiagnosticsListScreenBinding.inflate(inflater, container, false)
        CookingAppUtils.cancelIfAnyRecipeIsRunning()
        CookingAppUtils.cancelIfAnyKitchenTimersRunning()
        DiagnosticsManager.getInstance().isDiagnosticsModeActive = true
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CookingAppUtils.stopGattServer()
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_SERVICE)
        managePreferencesCollectionHeaderBar()
        addMarginTopToHeaderView(viewBinding)
    }

    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
    }

    override fun onViewClicked(view: View?) {
        if (view?.id != R.id.ivRightIcon) {
            AudioManagerUtils.playOneShotSound(
                view?.context,
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
        }
    }

    private fun managePreferencesCollectionHeaderBar() {
        viewBinding?.titleBar?.setLeftIconVisibility(false)
        viewBinding?.titleBar?.setRightIconVisibility(true)
        viewBinding?.titleBar?.setRightIcon(R.drawable.ic_close)
        viewBinding?.titleBar?.setTitleText(R.string.text_header_service_diagnostics)
        viewBinding?.titleBar?.setOvenCavityIconVisibility(false)
        viewBinding?.titleBar?.setInfoIconVisibility(false)
    }

    override fun onDestroyView() {
        viewBinding = null
    }

    override fun provideResources(): Resources {
        return viewBinding?.root?.resources as Resources
    }

    override fun provideRightIconResource(): Int {
        return 0
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return viewBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun providePrimaryButton(): NavigationButton? {
        return null
    }

    override fun provideRightNavigationView(): View? {
        return viewBinding?.titleBar?.getRightImageView()
    }

    override fun provideTitleText(): CharSequence? {
        return null
    }


    override fun provideExitPopupViewProvider(): AbstractDiagnosticsPopupViewProvider {
        return object : AbstractDiagnosticsPopupViewProvider() {
            var popupScreenBinding: LayoutDiagnosticsExitPopupFragmentBinding? = null

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                popupScreenBinding =
                    LayoutDiagnosticsExitPopupFragmentBinding.inflate(inflater, container, false)
                popupScreenBinding?.textViewTitle?.visibility = View.VISIBLE
                return popupScreenBinding?.root
            }

            override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
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

            override fun onDestroyView() {
                popupScreenBinding = null
            }

            override fun provideResources(): Resources {
                return popupScreenBinding?.root?.resources as Resources
            }

            override fun providePrimaryButton(): NavigationButton {
                return popupScreenBinding?.textButtonRight as NavigationButton
            }

            override fun provideExitPopupDescriptionText(): CharSequence {
                return provideResources().getText(R.string.text_restore_instructions_exit)
            }

            override fun provideSecondaryButton(): NavigationButton {
                return popupScreenBinding?.textButtonLeft as NavigationButton
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

            override fun provideExitPopupPrimaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_exit)
            }

            override fun provideExitPopupSecondaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_dismiss)
            }

            override fun provideExitPopupTitleText(): CharSequence {
                return provideResources().getString(R.string.text_header_end_service_mode)
            }
        }
    }

    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun provideFactoryResetPopupViewProvider(): AbstractDiagnosticsPopupViewProvider? {
        return null
    }


    override fun provideAutoDiagnosticsEntryPopupViewProvider(): AbstractDiagnosticsPopupViewProvider {
        return object : AbstractDiagnosticsPopupViewProvider() {
            var popupScreenBinding: LayoutAutoDiagnosticsPopupFragmentBinding? = null

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                popupScreenBinding =
                    LayoutAutoDiagnosticsPopupFragmentBinding.inflate(inflater, container, false)
                popupScreenBinding?.textViewTitle?.visibility = View.VISIBLE
                return popupScreenBinding?.root
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

            override fun provideAutoDiagnosticsEntryPopupPrimaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_continue)
            }

            override fun provideAutoDiagnosticsEntryPopupSecondaryButtonText(): CharSequence {
                return provideResources().getString(R.string.text_button_dismiss)
            }

            override fun provideAutoDiagnosticsEntryPopupTitleText(): CharSequence {
                return provideResources().getString(R.string.text_auto_diagnostics_entry_popup_title)
            }

            override fun provideAutoDiagnosticsEntryPopupDescriptionText(): CharSequence {
                return provideResources().getString(R.string.text_auto_diagnostics_entry_popup_description)
            }
        }
    }

    override fun provideListView(): ListView? {
        return viewBinding?.list
    }

    override fun provideDiagnosticsListItemViewProvider(): AbstractDiagnosticsListItemViewProvider {
        return object : AbstractDiagnosticsListItemViewProvider() {
            private var listItemBinding: DiagnosticsVerticalListItemBinding? = null

            override fun inflate(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int) {
                listItemBinding = DiagnosticsVerticalListItemBinding.inflate(inflater, parent, false)
                listItemBinding?.iconNavigation?.visibility = View.VISIBLE
            }

            override fun getView(): View? {
                return listItemBinding?.root
            }

            override fun provideListItemTitleTextView(): ResourceTextView? {
                return listItemBinding?.title
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
        val marginStart = provideResources().getDimension(R.dimen.extra_header_margin_added).toInt()
        constraintSet.setMargin(R.id.list, ConstraintSet.TOP, marginStart)
        constraintSet.applyTo(constraintLayoutId)
    }
}