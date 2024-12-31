package core.viewHolderHelpers

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentCookingGuideBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import core.jbase.abstractViewHolders.AbstractCookingGuideViewHolder
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.setSecondTimeAssistedRecipeSelection
import java.util.Locale

/**
 * File        : core.viewHolderHelpers.AssistedCookingGuideFragmentViewProvider
 * Brief       : Assisted Cooking view holder guide inflate as Fragment in Day1 scenario
 * Author      : Hiren
 * Created On  : 05/20/2024
 * Details     : Use this class to modify any UI related data
 */
class AssistedCookingGuideFragmentViewProvider(private val fragment: Fragment): AbstractCookingGuideViewHolder(){
    private lateinit var fragmentCookingGuideBinding: FragmentCookingGuideBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        fragmentCookingGuideBinding = FragmentCookingGuideBinding.inflate(inflater, container, false)
        CookingAppUtils.loadCookingGuide(provideCookingViewModel().recipeExecutionViewModel.recipeName.value ?: "")
        return fragmentCookingGuideBinding.root
    }

    override fun provideResources(): Resources {
        return fragmentCookingGuideBinding.root.resources
    }

    /**
     * Provides the ability to clean up the view holder when it is destroyed
     */
    override fun onDestroyView() {
        CookingAppUtils.clearOrEraseCookingGuideList()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        removeObserver()
        fragmentCookingGuideBinding.root.removeAllViews()
    }

    override fun provideCookingGuideDescriptionTextView(): TextView {
        return  fragmentCookingGuideBinding.textViewPopupInfo
    }

    /**
     * Required - provideStepperView is used to show the dots indicating how many pages are there
     *
     * @return [TextView]
     */
    override fun provideStepperView(): Stepper {
        return fragmentCookingGuideBinding.stepperCookingGuide
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
        return fragmentCookingGuideBinding.btnPrimary
    }

    /**
     * Required - provideCookingViewModel is used to get the recipe information
     *
     * @return [TextView]
     */
    override fun providePrimaryButtonConstraint(): ConstraintLayout {
        return fragmentCookingGuideBinding.constraintRightButton
    }
    /**
     * Required - provideCookingViewModel is used to get the recipe information
     *
     * @return [ConstraintLayout]
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
        return fragmentCookingGuideBinding.root.context
    }

    /**
     * Required - provideView is used to get the view
     *
     * @return [TextView]
     */
    override fun provideView(): View {
        return fragmentCookingGuideBinding.root.rootView
    }

    override fun setRecipeImage(recipeName: String, currentStep: String) {
        HMILogHelper.Logd("recipe-guide-pop-up-name-- $recipeName")

        // Default image for cooking guide
        val defaultImage = R.drawable.img_assisted_cooking_guide_leaf

        when (currentStep) {
            AppConstants.TEXT_COOK_GUIDE -> {
                val resourceId = CookingAppUtils.getResIdFromResName(
                    this.fragment.context,
                    recipeName.lowercase(Locale.getDefault()) + AppConstants.TEXT_TALL,
                    AppConstants.RESOURCE_TYPE_DRAWABLE
                )
                if (resourceId > 0) {
                    fragmentCookingGuideBinding.ivCookingGuide.setImageResource(resourceId)
                } else {
                    fragmentCookingGuideBinding.ivCookingGuide.setImageResource(defaultImage)
                }
            }

            AppConstants.TEXT_ACCESSORY_GUIDE -> {
                var recipe = recipeName
                if (CookingViewModelFactory.getInScopeViewModel().isOfTypeMicrowaveOven){
                    recipe = recipeName + AppConstants.MW_RECIPE
                }
                val resourceId = CookingAppUtils.getResIdFromResName(
                    this.fragment.context,
                    recipe.lowercase(Locale.getDefault()) + AppConstants.IMAGE_ACCESSORY_GUIDE,
                    AppConstants.RESOURCE_TYPE_DRAWABLE
                )
                if (resourceId > 0) {
                    fragmentCookingGuideBinding.ivCookingGuide.setImageResource(resourceId)
                } else {
                    fragmentCookingGuideBinding.ivCookingGuide.setImageResource(defaultImage)
                }
            }

            else -> {
                fragmentCookingGuideBinding.ivCookingGuide.setImageResource(defaultImage)
            }
        }
    }

    override fun provideTitleText(): CharSequence {
        return provideResources().getString(R.string.text_header_cooking_guide)
    }

    override fun provideTitleTextView(): TextView? {
        return null
    }

    override fun provideHeaderView(): HeaderBarWidget {
        return fragmentCookingGuideBinding.headerBar
    }

    /**
     * Required - provideDescriptionTextScrollView is used to show long text in scroll view
     *
     * @return [TextView]
     */
    override fun provideDescriptionTextScrollView(): ScrollView {
        return fragmentCookingGuideBinding.scrollViewPopupInfoText
    }

    override fun provideFragment(): Fragment {
        return fragment
    }

    override fun startAssistedRecipe() {
        if (CookingAppUtils.isTimeBasedPreheatRecipe(provideCookingViewModel())) {
            val recipeRecord =
                CookBookViewModel.getInstance().getDefaultRecipeRecordByNameAndCavity(
                    provideCookingViewModel().recipeExecutionViewModel.recipeName.value,
                    provideCookingViewModel().cavityName.value
                )
            HMILogHelper.Logd(
                "Time Based Preheat",
                "storing 1st day for recipeRecord ${recipeRecord.recipeName}"
            )
            setSecondTimeAssistedRecipeSelection(
                recipeRecord, provideCookingViewModel().recipeExecutionViewModel
            )
        }
        val delayTime =
            provideFragment().arguments?.getLong(BundleKeys.BUNDLE_NAVIGATED_ASSISTED_DELAY_COOKING_GUIDE)
        if (delayTime != null && delayTime > 0) {
            HMILogHelper.Logd(fragment.tag, "assisted  cooking guide starting Delay recipe delayTime $delayTime")
            NavigationUtils.startDelayRecipe(
                fragment, provideCookingViewModel(), delayTime
            )
        } else {
            super.startAssistedRecipe()
        }
    }
}