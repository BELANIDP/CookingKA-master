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
import android.widget.CheckBox
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.AutoDiagnosticsListSelectionBinding
import com.whirlpool.cooking.ka.databinding.DiagnosticsCavityListItemBinding
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractAutoDiagnosticsListViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsListItemViewProvider
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.list.ListView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AudioManagerUtils
import core.utils.gone
import core.utils.visible


/**
 * File       : android.presenter.fragments.service_diagnostic.AutoDiagnosticsListSelection
 * Brief      : AbstractAutoDiagnosticsListViewProvider instance for Auto Diagnostics list Selection Screen
 * Author     : NIMMAM
 * Created On : 24-06-2024
 * Details    : Diagnostics list Selection Screen in which user can select the cavity.
 */
class AutoDiagnosticsListSelection : AbstractAutoDiagnosticsListViewProvider() {
    private var diagnosticsSelectionListBinding: AutoDiagnosticsListSelectionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        diagnosticsSelectionListBinding =
            AutoDiagnosticsListSelectionBinding.inflate(inflater, container, false)
        return diagnosticsSelectionListBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
    }

    private fun managePreferencesCollectionHeaderBar() {
        diagnosticsSelectionListBinding?.titleBar?.setLeftIconVisibility(true)
        diagnosticsSelectionListBinding?.titleBar?.setRightIconVisibility(false)
        diagnosticsSelectionListBinding?.titleBar?.setTitleText(R.string.text_header_auto_diagnostics)
        diagnosticsSelectionListBinding?.titleBar?.setOvenCavityIconVisibility(false)
        diagnosticsSelectionListBinding?.titleBar?.setInfoIconVisibility(false)
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
        diagnosticsSelectionListBinding = null
    }

    override fun provideResources(): Resources {
        return diagnosticsSelectionListBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return diagnosticsSelectionListBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideTitleText(): CharSequence? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return diagnosticsSelectionListBinding?.titleBar?.getLeftImageView()
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }


    override fun provideSelectAllCheckBoxView(): CheckBox? {
        diagnosticsSelectionListBinding?.selectAllCheckbox?.text =
            provideResources().getText(R.string.text_check_box_select_all).toString().uppercase()
        return diagnosticsSelectionListBinding?.selectAllCheckbox
    }

    override fun provideStartButton(): NavigationButton? {
        diagnosticsSelectionListBinding?.buttonPrimary?.text =
            provideResources().getText(R.string.text_button_start)
        return diagnosticsSelectionListBinding?.buttonPrimary
    }

    override fun provideListView(): ListView? {
        return diagnosticsSelectionListBinding?.list
    }

    override fun provideDiagnosticsListItemViewProvider(): AbstractDiagnosticsListItemViewProvider {
        return object : AbstractDiagnosticsListItemViewProvider() {
            private var listItemBinding: DiagnosticsCavityListItemBinding? = null


            override fun inflate(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) {
                listItemBinding = DiagnosticsCavityListItemBinding.inflate(inflater, parent, false)
            }

            override fun getView(): View? {
                return listItemBinding?.root
            }

            override fun provideListItemTitleTextView(): ResourceTextView? {
                return listItemBinding?.title
            }

            override fun provideListItemSubTitleTextView(): ResourceTextView? {
                when (listItemBinding?.title?.text) {
                    provideResources().getString(R.string.sdk_diagnostics_label_microwave_oven_cavity ),
                    provideResources().getString(R.string.sdk_diagnostics_label_primary_oven_cavity) -> listItemBinding?.listItemDividerView?.visible()
                    provideResources().getString(R.string.sdk_diagnostics_label_secondary_oven_cavity) -> listItemBinding?.listItemDividerView?.gone()
                }
                return listItemBinding?.subtitle
            }

            override fun provideListItemCheckBoxView(): CheckBox? {
                return listItemBinding?.checkbox
            }
        }
    }

    override fun handleStartButtonVisibilityUpdates(enabled: Boolean) {
        super.handleStartButtonVisibilityUpdates(enabled)
        diagnosticsSelectionListBinding?.buttonPrimary?.setTextColor(
            provideResources().getColor(
                R.color.diagnostics_disabled_text,
                null
            )
        )
        if (enabled) {
            diagnosticsSelectionListBinding?.buttonPrimary?.setTextColor(
                provideResources().getColor(
                    R.color.diagnostics_primary_text,
                    null
                )
            )
        }
    }
    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

}