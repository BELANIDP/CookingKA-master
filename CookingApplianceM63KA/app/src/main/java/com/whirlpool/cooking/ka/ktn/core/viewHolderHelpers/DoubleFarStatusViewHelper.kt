package core.viewHolderHelpers

import android.os.Bundle
import android.presenter.basefragments.AbstractFarStatusFragment
import android.presenter.basefragments.abstract_view_helper.AbstractFarStatusViewHelper
import android.presenter.customviews.widgets.headerbar.HeaderBarWidget
import android.presenter.customviews.widgets.status.CookingFarStatusWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ViewDataBinding
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentFarViewDoubleStatusBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants
import core.utils.HMILogHelper
import core.utils.gone
import core.utils.visible

/**
 * File        : core.viewHolderHelpers.SingleStatusViewHelper
 * Brief       : Helper class to provide single status binding along with its helpers
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing individual widget status with whole fragment
 */
class DoubleFarStatusViewHelper : AbstractFarStatusViewHelper() {
    private var fragmentFarDoubleStatusBinding: FragmentFarViewDoubleStatusBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentFarDoubleStatusBinding =
            FragmentFarViewDoubleStatusBinding.inflate(inflater, container, false)
        return fragmentFarDoubleStatusBinding!!.root
    }

    override fun onDestroyView() {
        fragmentFarDoubleStatusBinding = null
    }

    override fun getLayoutViewBinding(): ViewDataBinding? {
        return fragmentFarDoubleStatusBinding
    }

    override fun setupBindingData(fragment: AbstractFarStatusFragment?) {
        fragmentFarDoubleStatusBinding?.farStatusFragment = fragment
        fragmentFarDoubleStatusBinding?.primaryCookingViewModel =
            CookingViewModelFactory.getPrimaryCavityViewModel()
        fragmentFarDoubleStatusBinding?.secondaryCookingViewModel =
            CookingViewModelFactory.getSecondaryCavityViewModel()
        fragmentFarDoubleStatusBinding?.primaryRecipeExecutionViewModel =
            CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel
        fragmentFarDoubleStatusBinding?.secondaryRecipeExecutionViewModel =
            CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel
        getUpperCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { AppCompatResources.getDrawable(it, R.drawable.ic_large_upper_cavity) })

        getLowerCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { AppCompatResources.getDrawable(it, R.drawable.ic_large_lower_cavity) })
    }

    override fun getUpperViewModel(): CookingViewModel? {
        return fragmentFarDoubleStatusBinding?.primaryCookingViewModel
    }

    override fun getLowerViewModel(): CookingViewModel? {
        return fragmentFarDoubleStatusBinding?.secondaryCookingViewModel
    }


    override fun getUpperCookingStatusWidget(): CookingFarStatusWidget? {
        return fragmentFarDoubleStatusBinding?.primaryStatusWidget
    }

    override fun getLowerCookingStatusWidget(): CookingFarStatusWidget? {
        return fragmentFarDoubleStatusBinding?.secondaryStatusWidget
    }

    override fun isKitchenTimerEnding(isKitchenTimerEnding: Boolean) {
        fragmentFarDoubleStatusBinding?.isKitchenTimerEnding = isKitchenTimerEnding
        HMILogHelper.Logd("FAR_VIEW", "DO: isKitchenTimerEnding-->$isKitchenTimerEnding")
        fragmentFarDoubleStatusBinding?.kitchenTimerFarViewHeaderBar?.apply {
            getLeftImageView()?.gone()
            getRightImageView()?.gone()
            getOvenCavityImageView()?.gone()
            getOvenCavityTitleTextView()?.gone()
            getHeaderTitle()?.gone()
            setInfoIconVisibility(isVisible = false)
        }

        playFadeInFadeOutAnimation(isKitchenTimerEnding)

        if (isKitchenTimerEnding) {
            fragmentFarDoubleStatusBinding?.apply {
                primaryStatusWidget.gone()
                secondaryStatusWidget.gone()
                kitchenTimerFarView.visible()
                kitchenTimerFarViewHeaderBar.getHeaderTitle()?.visible()
            }

        } else {
            fragmentFarDoubleStatusBinding?.apply {
                primaryStatusWidget.visible()
                secondaryStatusWidget.visible()
                kitchenTimerFarView.gone()
                kitchenTimerFarViewHeaderBar.getHeaderTitle()?.gone()

            }
        }
    }

    override fun getKitchenTimerRunningText(): AppCompatTextView? {
        return fragmentFarDoubleStatusBinding?.textViewKitchenTimerRunningTextFarView
    }

    override fun getKitchenTimerIcon(): AppCompatImageView? {
        return fragmentFarDoubleStatusBinding?.iconKitchenTimerFarView
    }

    override fun getHeaderBar(): HeaderBarWidget? {
        return  fragmentFarDoubleStatusBinding?.kitchenTimerFarViewHeaderBar
    }
    /**
     * Play fade in and out animation for kitchen timer view and status far view
     */
    private fun playFadeInFadeOutAnimation(isKitchenTimerEnding: Boolean) {
        val fadeOut = Fade(Fade.OUT).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = DecelerateInterpolator()
            fragmentFarDoubleStatusBinding?.apply {
                if (isKitchenTimerEnding) {
                    addTarget(primaryStatusWidget)
                    addTarget(secondaryStatusWidget)
                } else {
                    addTarget(kitchenTimerFarView)
                    kitchenTimerFarViewHeaderBar.getHeaderTitle()?.let { addTarget(it) }
                }

            }

        }
        val fadeIn = Fade(Fade.IN).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = AccelerateInterpolator()
            fragmentFarDoubleStatusBinding?.apply {
                if (isKitchenTimerEnding) {
                    addTarget(kitchenTimerFarView)
                    kitchenTimerFarViewHeaderBar.getHeaderTitle()?.let { addTarget(it) }
                } else {
                    addTarget(primaryStatusWidget)
                    addTarget(secondaryStatusWidget)
                }
            }
        }
        val transitionSet = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(fadeOut)
            addTransition(fadeIn)
        }
        TransitionManager.beginDelayedTransition(
            fragmentFarDoubleStatusBinding?.root as ViewGroup,
            transitionSet
        )
    }
}