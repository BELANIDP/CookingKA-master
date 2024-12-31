package android.presenter.fragments.settings

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
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentApplianceFeaturesGuideBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import core.jbase.AbstractKnobInfoFragment
import core.utils.HMIExpansionUtils

/**
 * File        : com.whirlpool.cooking.settings.SettingsKnobInfoFragment
 * Brief       : Appliance Features guide inflate as Fragment in Day1 scenario
 * Author      : Rajendra Paymode
 * Created On  : 05/20/2024
 * Details     : Use this class to modify any UI related data
 */
class SettingsKnobInfoFragment: AbstractKnobInfoFragment() {
    private lateinit var fragmentApplianceFeaturesGuideBinding: FragmentApplianceFeaturesGuideBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentApplianceFeaturesGuideBinding =
            FragmentApplianceFeaturesGuideBinding.inflate(inflater, container, false)
        return fragmentApplianceFeaturesGuideBinding.root
    }

    override fun provideResources(): Resources {
        return fragmentApplianceFeaturesGuideBinding.root.resources
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        provideGhostButtonTextView().visibility = View.GONE
        provideTitleTextView().text = provideResources().getString(R.string.knob_function_info)
    }

    /**
     * Required - provideStepperView is used to show the dots indicating how many pages are there
     *
     * @return [TextView]
     */
    override fun provideStepperView(): Stepper {
        return fragmentApplianceFeaturesGuideBinding.stepperFeatureGuide
    }

    /**
     * Required - provideGifImageView is used to show GIF image
     *
     * @return [ImageView]
     */
    override fun provideGifImageView(): ImageView {
        return fragmentApplianceFeaturesGuideBinding.ivCookingGuide
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
     * Required - providePrimaryButtonTextView is used to show the action to perform
     *
     * @return [TextView]
     */
    override fun providePrimaryButtonTextView(): TextView {
        return fragmentApplianceFeaturesGuideBinding.btnPrimary
    }

    /**
     * Required - provideGhostButtonTextView is used to show the action to perform
     *
     * @return [TextView]
     */
    override fun provideGhostButtonTextView(): TextView {
        return fragmentApplianceFeaturesGuideBinding.btnGhost
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
        return fragmentApplianceFeaturesGuideBinding.root.context
    }

    /**
     * Required - provideView is used to get the view
     *
     * @return [TextView]
     */
    override fun provideView(): View {
        return fragmentApplianceFeaturesGuideBinding.root.rootView
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.knob_function_info)
    }

    override fun provideTitleTextView(): TextView {
        return fragmentApplianceFeaturesGuideBinding.tvTitleApplianceFeatureGuide
    }

    /**
     * Required - provideDescriptionTextScrollView is used to show long text in scroll view
     *
     * @return [TextView]
     */
    override fun provideDescriptionTextScrollView(): ScrollView {
        return fragmentApplianceFeaturesGuideBinding.scrollViewPopupInfoText
    }

    /**
     * Required - provideFeaturesGuideDescriptionTextView is used to show the Description text on the screen
     *
     * @return [AppCompatTextView]
     */
    override fun provideFeaturesGuideDescriptionTextView(): AppCompatTextView {
        return fragmentApplianceFeaturesGuideBinding.textViewPopupInfo
    }

    override fun provideFeaturesGuideTitleTextView(): AppCompatTextView {
        return fragmentApplianceFeaturesGuideBinding.textViewTitle
    }

    override fun provideHeaderBackView(): FrameLayout {
        return fragmentApplianceFeaturesGuideBinding.flHeaderBackIcon
    }
}