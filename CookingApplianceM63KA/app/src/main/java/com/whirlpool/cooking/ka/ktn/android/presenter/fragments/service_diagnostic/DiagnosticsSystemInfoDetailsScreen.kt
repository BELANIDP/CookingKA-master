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
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DiagnosticsListItemHeaderBinding
import com.whirlpool.cooking.ka.databinding.DiagnosticsListScreenBinding
import com.whirlpool.cooking.ka.databinding.DiagnosticsSystemInfoListItemBinding
import com.whirlpool.hmi.diagnostics.models.ListDataModelGeneric
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsListItemViewProvider
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractDiagnosticsMatrixListViewProvider
import com.whirlpool.hmi.uicomponents.widgets.list.ListView
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AudioManagerUtils
import core.utils.gone

/**
 * File       : com.whirlpool.cooking.diagnostic.DiagnosticsSystemInfoDetailsScreen
 * Brief      : AbstractDiagnosticsMatrixListViewProvider instance for Diagnostics List of system information.
 * Author     : Rajendra
 * Created On : 26-06-2024
 * Details    : Diagnostics System InfoDetails Screen used for represents the user interface for the system information details screen.
 */
class DiagnosticsSystemInfoDetailsScreen : AbstractDiagnosticsMatrixListViewProvider() {
    private var systemInfoDetailsViewBinding: DiagnosticsListScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        systemInfoDetailsViewBinding =
            DiagnosticsListScreenBinding.inflate(inflater, container, false)
        return systemInfoDetailsViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
    }

    private fun managePreferencesCollectionHeaderBar() {
        systemInfoDetailsViewBinding?.titleBar?.setLeftIconVisibility(true)
        systemInfoDetailsViewBinding?.titleBar?.setRightIconVisibility(false)
        systemInfoDetailsViewBinding?.titleBar?.setOvenCavityIconVisibility(false)
        systemInfoDetailsViewBinding?.titleBar?.setInfoIconVisibility(false)
        systemInfoDetailsViewBinding?.textSubText?.visibility = View.VISIBLE
        systemInfoDetailsViewBinding?.titleBar?.getBinding()?.clOvenCavity?.gone()
         addMarginTopToHeaderView(systemInfoDetailsViewBinding)
    }

    override fun provideInactivityTimeoutInSeconds(): Int {
        return provideResources().getInteger(R.integer.integer_timeout_10mins)
    }

    override fun isModelNumberEditable(): Boolean {
        return true
    }

    override fun isSerialNumberEditable(): Boolean {
        return true
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
        systemInfoDetailsViewBinding = null
    }

    override fun provideResources(): Resources {
        return systemInfoDetailsViewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return systemInfoDetailsViewBinding?.titleBar?.getHeaderTitle()
    }

    override fun provideEnterTransition(context: Context): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context): Transition? {
        return null
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun provideRightIconResource(): Int {
        return 0
    }

    override fun provideSubTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideTitleText(): CharSequence? {
        return null
    }


    override fun provideLeftNavigationView(): View? {
        return systemInfoDetailsViewBinding?.titleBar?.getLeftImageView()
    }


    override fun provideListView(): ListView? {
        return systemInfoDetailsViewBinding?.list
    }

    override fun provideDiagnosticsListItemViewProvider(): AbstractDiagnosticsListItemViewProvider {
        return object : AbstractDiagnosticsListItemViewProvider() {
            var listItemDetailsBinding: DiagnosticsSystemInfoListItemBinding? = null
            var listItemHeaderBinding: DiagnosticsListItemHeaderBinding? = null
            var listItemViewType: Int = ListDataModelGeneric.VIEW_TYPE_DATA

            override fun inflate(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) {
                listItemViewType = viewType
                if (viewType == ListDataModelGeneric.VIEW_TYPE_HEADER) {
                    listItemHeaderBinding =
                        DiagnosticsListItemHeaderBinding.inflate(inflater, parent, false)
                } else {
                    listItemDetailsBinding =
                        DiagnosticsSystemInfoListItemBinding.inflate(inflater, parent, false)
                }
            }

            override fun getView(): View? {
                if (listItemViewType == ListDataModelGeneric.VIEW_TYPE_HEADER) {
                    return listItemHeaderBinding?.root
                }
                return listItemDetailsBinding?.root
            }

            override fun provideListItemNavigationIconView(): ImageView? {
                return listItemDetailsBinding?.iconNavigation
            }

            override fun provideListItemTitleTextView(): ResourceTextView? {
                if (listItemViewType == ListDataModelGeneric.VIEW_TYPE_HEADER) {
                    return listItemHeaderBinding?.title
                }
                return listItemDetailsBinding?.title
            }

            override fun provideListItemValueTextView(): TextView? {
                if (listItemViewType == ListDataModelGeneric.VIEW_TYPE_HEADER) {
                    return null
                }
                return listItemDetailsBinding?.value
            }
        }
    }

    override fun provideVersionNumberPrefix(): CharSequence {
        return provideResources().getString(R.string.VersionNumberPrefix)
    }

    override fun providePartNumberPrefix(): CharSequence {
        return provideResources().getString(R.string.partNumberPrefix)
    }
    /**
     * Runtime add margin to view for adjust aligment
     * @param binding - binding for getting reference ids
     */
    private fun addMarginTopToHeaderView(binding: DiagnosticsListScreenBinding?) {
        val constraintSet = ConstraintSet()
        val constraintLayoutId = binding?.parentView
        constraintSet.clone(constraintLayoutId)
        val marginStart =provideResources().getDimension(R.dimen.extra_header_margin_removed_17dp).toInt()
        constraintSet.setMargin(R.id.list, ConstraintSet.TOP, marginStart)
        constraintSet.applyTo(constraintLayoutId)
    }
}