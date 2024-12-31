/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.viewHolderHelpers

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentApplianceFeaturesGuideBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import core.jbase.abstractViewHolders.AbstractUnboxingExploreFeaturesGuideViewHolder
import core.utils.HMIExpansionUtils
import core.utils.gone

/**
 * File        : core.viewHolderHelpers.ApplianceExploreGuideViewProvider
 * Brief       : Appliance Features view holder guide inflate as Fragment in Day1 scenario
 * Author      : Hiren
 * Created On  : 05/20/2024
 * Details     : Use this class to modify any UI related data
 */
class ApplianceUnboxingExploreGuideViewProvider(private val fragment: Fragment): AbstractUnboxingExploreFeaturesGuideViewHolder() {
    private lateinit var binding: FragmentApplianceFeaturesGuideBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentApplianceFeaturesGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(ignoredView: View, ignoredSavedInstanceState: Bundle?) {
        super.onViewCreated(ignoredView, ignoredSavedInstanceState)
        addMarginStartToTitleView()
        binding.gradient.gone()
    }

    override fun provideResources(): Resources {
        return binding.root.resources
    }

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        removeObserver()
    }


    /**
     * Required - provideStepperView is used to show the dots indicating how many pages are there
     *
     * @return [TextView]
     */
    override fun provideStepperView(): Stepper {
        return binding.stepperFeatureGuide
    }

    /**
     * Required - providePrimaryButtonText is used to set the text on the primary button
     *
     * @return [CharSequence]
     */
    override fun providePrimaryButtonText(): CharSequence {
        return provideResources().getString(R.string.text_button_next)
    }


    /**
     * Required - provideGifImageView is used to show GIF image
     *
     * @return [ImageView]
     */
    override fun provideGifImageView(): ImageView {
        return binding.ivCookingGuide
    }

    /**
     * Required - providePrimaryButtonTextView is used to show the action to perform
     *
     * @return [TextView]
     */
    override fun providePrimaryButtonTextView(): TextView {
        return binding.btnPrimary
    }

    /**
     * Required - provideGhostButtonTextView is used to show the action to perform
     *
     * @return [TextView]
     */
    override fun provideGhostButtonTextView(): TextView {
        return binding.btnGhost
    }

    /**
     * Required - provideCookingViewModel is used to get the recipe information
     *
     * @return [TextView]
     */
    override fun provideCookingViewModel(): CookingViewModel {
        return CookingViewModelFactory.getInScopeViewModel()
    }

    /**
     * Required - provideContext is used to context
     *
     * @return [TextView]
     */
    override fun provideContext(): Context {
        return binding.root.context
    }

    /**
     * Required - provideView is used to get the view
     *
     * @return [TextView]
     */
    override fun provideView(): View {
        return binding.root.rootView
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.text_header_celebration)
    }

    override fun provideTitleTextView(): TextView {
        return binding.tvTitleApplianceFeatureGuide
    }

    /**
     * Required - provideDescriptionTextScrollView is used to show long text in scroll view
     *
     * @return [TextView]
     */
    override fun provideDescriptionTextScrollView(): ScrollView {
        return binding.scrollViewPopupInfoText
    }

    override fun provideFragment(): Fragment {
        return fragment
    }

    /**
     * Required - provideFeaturesGuideDescriptionTextView is used to show the Description text on the screen
     *
     * @return [AppCompatTextView]
     */
    override fun provideFeaturesGuideDescriptionTextView(): AppCompatTextView {
        return binding.textViewPopupInfo
    }

    override fun provideFeaturesGuideTitleTextView(): AppCompatTextView {
        return binding.textViewTitle
    }

    override fun provideHeaderBackView(): FrameLayout {
        return binding.flHeaderBackIcon
    }
    /**
     * Runtime add margin to view for adjust aligment
     */
    private fun addMarginStartToTitleView() {
        val param = (provideFeaturesGuideDescriptionTextView().layoutParams as ViewGroup.MarginLayoutParams).apply {
            marginStart = 0
        }
        provideFeaturesGuideDescriptionTextView().layoutParams = param
        provideFeaturesGuideDescriptionTextView().invalidate()
    }
}