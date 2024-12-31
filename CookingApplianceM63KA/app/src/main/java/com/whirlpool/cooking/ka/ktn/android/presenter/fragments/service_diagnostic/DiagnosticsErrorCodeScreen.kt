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
import com.whirlpool.cooking.ka.databinding.DiagnosticsListItemErrorCodeBinding
import com.whirlpool.cooking.ka.databinding.DiagnosticsListScreenBinding
import com.whirlpool.cooking.ka.databinding.ErrorDiagnosticsPopupFragmentBinding
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsErrorCodeViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsListItemErrorCodeViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsPopupViewProvider
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.list.ListView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import com.whirlpool.hmi.utils.ContextProvider
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils

/**
 * File       : android.presenter.fragments.service_diagnostic.DiagnosticsErrorCodeScreen
 * Brief      : AbstractDiagnosticsErrorCodeViewProvider instance for Diagnostics ErrorCode Screen
 * Author     : Rajendra
 * Created On : 20-06-2024
 * Details    : Diagnostics ErrorCode Screen which allow user to see and clear list of Error Codes
 */
class DiagnosticsErrorCodeScreen : AbstractDiagnosticsErrorCodeViewProvider() {
    private var viewBinding: DiagnosticsListScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = DiagnosticsListScreenBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        managePreferencesCollectionHeaderBar()
    }
    private fun initUI() {
        viewBinding?.description?.typeface =
            viewBinding?.description?.context?.let {
                CookingAppUtils.getTypeFace(
                    it,
                    R.font.roboto_light
                )
            }
    }
    private fun managePreferencesCollectionHeaderBar() {
        viewBinding?.titleBar?.setLeftIconVisibility(true)
        viewBinding?.titleBar?.setRightIconVisibility(false)
        viewBinding?.titleBar?.setOvenCavityIconVisibility(false)
        viewBinding?.titleBar?.setInfoIconVisibility(false)
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.text_header_error_code_history)
    }

    override fun onViewClicked(view: View?) {
        if (view?.id != R.id.list) {
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

    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
    }

    override fun provideRightIconResource(): Int {
        return 0
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun onDestroyView() {
        viewBinding = null
    }

    override fun provideResources(): Resources {
        return viewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return viewBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun providePrimaryButton(): NavigationButton? {
        return viewBinding?.buttonPrimary
    }

    override fun provideNoErrorDescriptionTextView(): TextView? {
        return viewBinding?.description
    }

    override fun providePrimaryButtonText(): CharSequence {
        return provideResources().getString(R.string.text_button_clear_all)
    }

    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun provideNoErrorDescriptionText(): CharSequence {
        return provideResources().getString(R.string.str_no_errors_found)
    }

    override fun provideListView(): ListView? {
        return viewBinding?.list
    }

    override fun provideLeftNavigationView(): View? {
        return viewBinding?.titleBar?.getLeftImageView()
    }

    override fun provideRightNavigationView(): View? {
        return null
    }

    override fun provideDiagnosticsErrorListItemViewProvider(): AbstractDiagnosticsListItemErrorCodeViewProvider {
        return object : AbstractDiagnosticsListItemErrorCodeViewProvider() {
            var listItemMatrixBinding: DiagnosticsListItemErrorCodeBinding? = null

            override fun inflate(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int) {
                listItemMatrixBinding =
                    DiagnosticsListItemErrorCodeBinding.inflate(inflater, parent, false)
            }

            override fun getView(): View? {
                return listItemMatrixBinding?.root
            }


            override fun provideErrorCodeTextView(): ResourceTextView? {
                return listItemMatrixBinding?.errorCode
            }

            override fun provideErrorCategoryTextView(): ResourceTextView? {
                return listItemMatrixBinding?.errorCategory
            }

            override fun provideErrorSuggestionTextView(): ResourceTextView? {
                return listItemMatrixBinding?.errorSuggestion
            }

            override fun provideErrorExtraInfoTextView(): ResourceTextView? {
                return listItemMatrixBinding?.extraInfo
            }

            override fun provideErrorTimeTextView(): TextView? {
                return listItemMatrixBinding?.errorTime
            }

            override fun provideErrorDateTextView(): TextView? {
                return listItemMatrixBinding?.errorDate
            }
        }
    }

    override fun provideClearErrorPopupViewProvider(): AbstractDiagnosticsPopupViewProvider {
        return object : AbstractDiagnosticsPopupViewProvider() {
            var popupScreenBinding: ErrorDiagnosticsPopupFragmentBinding? = null

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {
                popupScreenBinding =
                    ErrorDiagnosticsPopupFragmentBinding.inflate(inflater, container, false)
                popupScreenBinding?.textViewTitle?.visibility = View.VISIBLE
                popupScreenBinding?.scrollView?.isVerticalScrollBarEnabled = false
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

            override fun provideResources(): Resources? {
                return popupScreenBinding?.root?.resources
            }

            override fun providePrimaryButton(): NavigationButton? {
                return popupScreenBinding?.textButtonRight
            }

            override fun provideSecondaryButton(): NavigationButton? {
                return popupScreenBinding?.textButtonLeft
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

            override fun provideClearErrorPopupTitleText(): CharSequence? {
                return provideResources()?.getString(R.string.text_header_clear_errors)
            }

            override fun provideClearErrorPopupDescriptionText(): CharSequence? {
                return provideResources()?.getString(R.string.text_layout_pop_up_decision_return_previous_step)
            }

            override fun provideClearErrorPopupPrimaryButtonText(): CharSequence? {
                return provideResources()?.getString(R.string.text_button_clear_errors)
            }

            override fun provideClearErrorPopupSecondaryButtonText(): CharSequence? {
                return provideResources()?.getString(R.string.text_button_cancel)
            }
        }
    }
}