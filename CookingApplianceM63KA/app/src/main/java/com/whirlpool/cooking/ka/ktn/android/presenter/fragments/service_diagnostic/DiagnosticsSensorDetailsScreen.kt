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
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DiagnosticsListItemBinding
import com.whirlpool.cooking.ka.databinding.DiagnosticsListScreenBinding
import com.whirlpool.cooking.ka.databinding.DiagnosticsVerticalListItemBinding
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsListItemViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsMatrixListViewProvider
import com.whirlpool.hmi.uicomponents.widgets.list.ListView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AppConstants
import core.utils.AudioManagerUtils


/**
 * File       : android.presenter.fragments.service_diagnostic.DiagnosticsSensorDetailsScreen
 * Brief      : AbstractDiagnosticsMatrixListViewProvider instance for Diagnostics List of sensor details.
 * Author     : Rajendra
 * Created On : 25-06-2024
 * Details    : Diagnostics sensor details screen used for represents the user interface for the sensor details screen.
 */
class DiagnosticsSensorDetailsScreen : AbstractDiagnosticsMatrixListViewProvider() {
    private var diagnosticsSensorDetailsViewBinding: DiagnosticsListScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        diagnosticsSensorDetailsViewBinding =
            DiagnosticsListScreenBinding.inflate(inflater, container, false)
        return diagnosticsSensorDetailsViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
        addMarginStartToTitleView(diagnosticsSensorDetailsViewBinding)
        initView()
    }

    private fun initView() {
        diagnosticsSensorDetailsViewBinding?.scrollbarOverlay?.visibility = View.VISIBLE
        diagnosticsSensorDetailsViewBinding?.list?.isVerticalScrollBarEnabled = true
        diagnosticsSensorDetailsViewBinding?.list?.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING
                    || newState == RecyclerView.SCROLL_STATE_SETTLING
                ) {
                    diagnosticsSensorDetailsViewBinding?.scrollbarOverlay?.visibility = View.GONE
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            diagnosticsSensorDetailsViewBinding?.scrollbarOverlay?.visibility =
                                View.VISIBLE
                        }, AppConstants.SERVICE_LIST_SCROLLBAR_DELAY
                    )
                }
            }
        })

    }

    private fun managePreferencesCollectionHeaderBar() {
        diagnosticsSensorDetailsViewBinding?.titleBar?.setLeftIconVisibility(true)
        diagnosticsSensorDetailsViewBinding?.titleBar?.setRightIconVisibility(false)
        diagnosticsSensorDetailsViewBinding?.titleBar?.setOvenCavityIconVisibility(false)
        diagnosticsSensorDetailsViewBinding?.titleBar?.setInfoIconVisibility(false)
        diagnosticsSensorDetailsViewBinding?.textSubText?.visibility = View.VISIBLE
    }

    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
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
    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    override fun provideRightIconResource(): Int {
        return 0
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun onDestroyView() {
        diagnosticsSensorDetailsViewBinding = null
    }

    override fun provideResources(): Resources {
        return diagnosticsSensorDetailsViewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return diagnosticsSensorDetailsViewBinding?.titleBar?.getHeaderTitle()
    }


    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.sensor_feedback)
    }

    override fun provideLeftNavigationView(): View? {
        return diagnosticsSensorDetailsViewBinding?.titleBar?.getLeftImageView()
    }

    override fun provideListView(): ListView? {
        return diagnosticsSensorDetailsViewBinding?.list
    }


    override fun provideDiagnosticsListItemViewProvider(): AbstractDiagnosticsListItemViewProvider {
        return object : AbstractDiagnosticsListItemViewProvider() {
            var listItemBinding: DiagnosticsVerticalListItemBinding? = null

            override fun inflate(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int) {
                listItemBinding = DiagnosticsVerticalListItemBinding.inflate(inflater, parent, false)
                listItemBinding?.iconNavigation?.visibility = View.GONE
            }

            override fun getView(): View? {
                return listItemBinding?.root
            }

            override fun provideListItemTitleTextView(): ResourceTextView? {
                return listItemBinding?.title
            }

            override fun provideListItemSubTitleTextView(): ResourceTextView? {
                return listItemBinding?.subtitle
            }

            override fun provideListItemValueTextView(): TextView? {
                return listItemBinding?.value
            }

            override fun provideListItemNavigationIconView(): ImageView? {
                return null
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
        constraintSet.setMargin(R.id.list, ConstraintSet.TOP, marginStart)
        constraintSet.applyTo(constraintLayoutId)
    }
}