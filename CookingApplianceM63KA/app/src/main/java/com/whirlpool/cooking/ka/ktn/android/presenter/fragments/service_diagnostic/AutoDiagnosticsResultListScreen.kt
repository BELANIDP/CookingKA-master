package android.presenter.fragments.service_diagnostic
/*
 *  *----------------------------------------------------------------------------------------------*
 *  * ---- Copyright 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL --------------*
 *  * ---------------------------------------------------------------------------------------------*
 */
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
import com.whirlpool.cooking.ka.databinding.AutoDiagnosticsListSelectionBinding
import com.whirlpool.cooking.ka.databinding.DiagnosticsResultListItemBinding
import com.whirlpool.cooking.ka.databinding.LayoutEndDiagnosticsPopupFragmentBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractAutoDiagnosticsResultListViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsListItemViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsPopupViewProvider
import com.whirlpool.hmi.uicomponents.widgets.buttons.NavigationButton
import com.whirlpool.hmi.uicomponents.widgets.list.ListView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AudioManagerUtils
import core.utils.gone
import core.utils.visible

/**
 * File       : android.presenter.fragments.service_diagnostic.AutoDiagnosticsResultListScreen
 * Brief      : AbstractAutoDiagnosticsListViewProvider instance for Auto Diagnostics Results List Screen
 * Author     : NIMMAM
 * Created On : 25-06-2024
 * Details    : Auto Diagnostics test results list Selection Screen in which user can select.
 */
class AutoDiagnosticsResultListScreen : AbstractAutoDiagnosticsResultListViewProvider() {
    private var diagnosticsResultListBinding: AutoDiagnosticsListSelectionBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        diagnosticsResultListBinding =
            AutoDiagnosticsListSelectionBinding.inflate(inflater, container, false)
        return diagnosticsResultListBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
    }

    private fun managePreferencesCollectionHeaderBar() {
        diagnosticsResultListBinding?.titleBar?.setLeftIconVisibility(true)
        diagnosticsResultListBinding?.titleBar?.setRightIconVisibility(false)
        diagnosticsResultListBinding?.titleBar?.setTitleText(R.string.text_header_test_results)
        diagnosticsResultListBinding?.titleBar?.setInfoIconVisibility(false)
        cavityIconVisibility(View.GONE)
    }

    override fun onDestroyView() {
        diagnosticsResultListBinding = null
    }

    override fun provideResources(): Resources {
        return diagnosticsResultListBinding?.root?.resources as Resources
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
        return diagnosticsResultListBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideTitleText(): CharSequence? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return diagnosticsResultListBinding?.titleBar?.getLeftImageView()
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun onShowingResultForCavity(cavityId: Int) {
        diagnosticsResultListBinding?.titleBar?.setOvenCavityIcon(
            if (cavityId == 0) {
                R.drawable.ic_oven_cavity_large
            } else {
                R.drawable.ic_lower_cavity_large
            }
        )
    }

    override fun provideListView(): ListView? {
        return diagnosticsResultListBinding?.list
    }


    override fun provideDiagnosticsListItemViewProvider(): AbstractDiagnosticsListItemViewProvider {
        return object : AbstractDiagnosticsListItemViewProvider() {
            private var listItemDetailsBinding: DiagnosticsResultListItemBinding? = null

            override fun inflate(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) {
                listItemDetailsBinding =
                    DiagnosticsResultListItemBinding.inflate(inflater, parent, false)
            }

            override fun getView(): View? {
                return listItemDetailsBinding?.root
            }

            override fun provideListItemTitleTextView(): ResourceTextView? {
                return listItemDetailsBinding?.title
            }

            override fun provideListItemSubTitleTextView(): ResourceTextView? {
                when (listItemDetailsBinding?.title?.text) {
                    provideResources().getString(R.string.sdk_diagnostics_label_microwave_oven_cavity ),
                    provideResources().getString(R.string.sdk_diagnostics_label_primary_oven_cavity) -> {
                        listItemDetailsBinding?.iconResult?.visibility = View.VISIBLE
                        listItemDetailsBinding?.iconResult?.setBackgroundResource(R.drawable.ic_oven_cavity)
                        listItemDetailsBinding?.listItemDividerView?.visible()
                    }
                    provideResources().getString(R.string.sdk_diagnostics_label_secondary_oven_cavity) -> {
                        listItemDetailsBinding?.iconResult?.visibility = View.VISIBLE
                        listItemDetailsBinding?.iconResult?.setBackgroundResource(R.drawable.ic_lower_cavity)
                        listItemDetailsBinding?.listItemDividerView?.gone()
                    }
                }
                return null
            }

            override fun provideListItemValueTextView(): TextView? {
                return null
            }

            override fun provideListItemResultIconView(): ImageView? {
                listItemDetailsBinding?.iconResult?.visibility = View.VISIBLE
                cavityIconVisibility(View.VISIBLE)
                return listItemDetailsBinding?.iconResult
            }

            override fun provideTestSuccessIconResourceId(): Int {
                return R.drawable.auto_diagnostics_results_success
            }

            override fun provideTestFailedIconResourceId(): Int {
                return R.drawable.diagnostics_alert_red
            }

            override fun provideListItemNavigationIconView(): ImageView? {
                return listItemDetailsBinding?.iconNavigation
            }
        }
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
        }
    }

    override fun provideRetryTestButton(): NavigationButton? {
        diagnosticsResultListBinding?.buttonPrimary?.text =
            provideResources().getString(R.string.text_button_retry)
        return diagnosticsResultListBinding?.buttonPrimary
    }

    /**
     * Runtime add margin to title textview for adjust the result icon and title to on list item
     * @param listItemDetailsBinding - binding for getting reference ids
     */
    @Suppress("unused")
    private fun addMarginStartToTitleView(listItemDetailsBinding: DiagnosticsResultListItemBinding?) {
        val constraintSet = ConstraintSet()
        val constraintLayoutId = listItemDetailsBinding?.diagnosticsParentLayout
        constraintSet.clone(constraintLayoutId)
        val marginStart =
            listItemDetailsBinding?.diagnosticsParentLayout?.resources?.getDimension(R.dimen.diagnostics_list_item_icons_barrier_margin)
                ?.toInt()
        if (marginStart != null) {
            constraintSet.setMargin(R.id.title, ConstraintSet.START, marginStart)
            constraintSet.applyTo(constraintLayoutId)
        }
    }
    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }
    private fun cavityIconVisibility(isVisible: Int) {
        if (View.VISIBLE == isVisible) {
            diagnosticsResultListBinding?.titleBar?.setOvenCavityIconVisibility(
                CookingViewModelFactory.getProductVariantEnum() ==
                        CookingViewModelFactory.ProductVariantEnum.COMBO ||
                        CookingViewModelFactory.getProductVariantEnum() ==
                        CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
            )
        } else {
            diagnosticsResultListBinding?.titleBar?.setOvenCavityIconVisibility(false)
        }
    }
}