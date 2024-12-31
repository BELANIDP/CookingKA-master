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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.transition.Transition
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.AutoDiagnosticsResultDetailBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.diagnostics.viewproviders.AbstractAutoDiagnosticsResultDetailsViewProvider
import com.whirlpool.hmi.uicomponents.widgets.resourcehelper.ResourceTextView
import core.utils.AudioManagerUtils

/**
 * File       : android.presenter.fragments.service_diagnostic.AutoDiagnosticsResultsDetailScreen
 * Brief      : AbstractAutoDiagnosticsResultDetailsViewProvider instance for Auto Diagnostics
Results detail Screen
 * Author     : NIMMAM
 * Created On : 26-17-2024
 * Details    : Auto Diagnostics test results list Selection Screen in which user can select.
 */
class AutoDiagnosticsResultsDetailScreen : AbstractAutoDiagnosticsResultDetailsViewProvider() {
    private var resultDetailsViewBinding: AutoDiagnosticsResultDetailBinding? = null
    private var textWatcher: TextWatcher? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        resultDetailsViewBinding =
            AutoDiagnosticsResultDetailBinding.inflate(inflater, container, false)
        return resultDetailsViewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        managePreferencesCollectionHeaderBar()
        observerOnTextChanged()
    }

    private fun managePreferencesCollectionHeaderBar() {
        resultDetailsViewBinding?.titleBar?.setLeftIconVisibility(true)
        resultDetailsViewBinding?.titleBar?.setRightIconVisibility(false)
        resultDetailsViewBinding?.titleBar?.setTitleText(R.string.text_header_results_detail)
        resultDetailsViewBinding?.titleBar?.setInfoIconVisibility(false)
        resultDetailsViewBinding?.titleBar?.setOvenCavityIconVisibility(CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.COMBO || CookingViewModelFactory.getProductVariantEnum() == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN)
        resultDetailsViewBinding?.titleBar?.setOvenCavityIconVisibility(
            CookingViewModelFactory.getProductVariantEnum() ==
                    CookingViewModelFactory.ProductVariantEnum.COMBO ||
                    CookingViewModelFactory.getProductVariantEnum() ==
                    CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN
        )
    }

    override fun provideLeftIconResource(): Int {
        return 0
    }

    override fun updateErrorCodeDetails(
        cavityId: Int,
        stepName: String,
        errorCodeList: List<String>
    ) {
        resultDetailsViewBinding?.titleBar?.setOvenCavityIcon(
            if (cavityId == 0) {
                R.drawable.ic_oven_cavity_large
            } else {
                R.drawable.ic_lower_cavity_large
            }
        )
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

    override fun onDestroyView() {
        resultDetailsViewBinding?.errorDescriptionText?.removeTextChangedListener(textWatcher)
        resultDetailsViewBinding = null
    }

    override fun provideResources(): Resources {
        return resultDetailsViewBinding?.root?.resources as Resources
    }

    override fun provideTitleTextView(): ResourceTextView? {
        return null
    }

    override fun provideLeftNavigationView(): View? {
        return resultDetailsViewBinding?.titleBar?.getLeftImageView()
    }

    override fun provideErrorTitleTextView(): TextView? {
        return resultDetailsViewBinding?.errorTitleText
    }

    override fun provideErrorDescriptionTextView(): TextView? {
        return resultDetailsViewBinding?.errorDescriptionText
    }
    override fun provideEnterTransition(context: Context?): Transition? {
        return null
    }

    override fun provideExitTransition(context: Context?): Transition? {
        return null
    }

    /**
     * Tex changed listener
     */
    private fun observerOnTextChanged() {
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when {
                    s.isNullOrEmpty() -> resultDetailsViewBinding?.scrollView?.visibility = View.GONE
                    else -> resultDetailsViewBinding?.scrollView?.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        }
        resultDetailsViewBinding?.errorDescriptionText?.addTextChangedListener(textWatcher)
    }
}