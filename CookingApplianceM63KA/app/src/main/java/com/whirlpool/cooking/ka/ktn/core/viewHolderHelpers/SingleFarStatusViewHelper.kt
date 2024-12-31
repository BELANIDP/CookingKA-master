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
import com.whirlpool.cooking.ka.databinding.FragmentFarViewSingleStatusBinding
import com.whirlpool.hmi.cooking.utils.Constants
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
 * @param cavityPosition constructor to identify which Cooking View Model to load default will be upper, used in dual cavity (combo, double) where below cavity running only
 */
class SingleFarStatusViewHelper(private val cavityPosition: String) : AbstractFarStatusViewHelper() {
    private var fragmentSingleStatusBinding: FragmentFarViewSingleStatusBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSingleStatusBinding =
            FragmentFarViewSingleStatusBinding.inflate(inflater, container, false)
        return fragmentSingleStatusBinding!!.root
    }

    override fun onDestroyView() {
        fragmentSingleStatusBinding = null
    }

    override fun getLayoutViewBinding(): ViewDataBinding? {
        return fragmentSingleStatusBinding
    }

    override fun setupBindingData(fragment: AbstractFarStatusFragment?) {
        fragmentSingleStatusBinding?.farStatusFragment = fragment
        getUpperCookingStatusWidget()?.ovenType = cavityPosition
        val cookingViewModel = if(cavityPosition.contentEquals(Constants.SECONDARY_CAVITY_KEY)) CookingViewModelFactory.getSecondaryCavityViewModel() else CookingViewModelFactory.getPrimaryCavityViewModel()
        fragmentSingleStatusBinding?.cookingViewModel = cookingViewModel
        fragmentSingleStatusBinding?.recipeExecutionViewModel =
            cookingViewModel.recipeExecutionViewModel
        getUpperCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { if(cookingViewModel.isPrimaryCavity) AppCompatResources.getDrawable(it, R.drawable.ic_large_upper_cavity) else AppCompatResources.getDrawable(it, R.drawable.ic_large_lower_cavity)})
    }

    override fun getUpperViewModel(): CookingViewModel? {
        return fragmentSingleStatusBinding?.cookingViewModel
    }

    override fun getLowerViewModel(): CookingViewModel? {
        return null
    }


    override fun getUpperCookingStatusWidget(): CookingFarStatusWidget? {
        return fragmentSingleStatusBinding?.singleStatusWidget
    }

    override fun getLowerCookingStatusWidget(): CookingFarStatusWidget? {
        return null
    }
    override fun isKitchenTimerEnding(isKitchenTimerEnding: Boolean) {
        fragmentSingleStatusBinding?.isKitchenTimerEnding = isKitchenTimerEnding
        HMILogHelper.Logd("FAR_VIEW", "SO: isKitchenTimerEnding-->$isKitchenTimerEnding")
        fragmentSingleStatusBinding?.KitchenTimerFarViewHeaderBar?.apply {
            getLeftImageView()?.gone()
            getRightImageView()?.gone()
            getOvenCavityImageView()?.gone()
            getOvenCavityTitleTextView()?.gone()
            getHeaderTitle()?.gone()
            setInfoIconVisibility(isVisible = false)
        }
        playFadeInFadeOutAnimation(isKitchenTimerEnding)
        if (isKitchenTimerEnding) {
            fragmentSingleStatusBinding?.apply {
                singleStatusWidget.gone()
                kitchenTimerFarView.visible()
                KitchenTimerFarViewHeaderBar.visible()
                KitchenTimerFarViewHeaderBar.getHeaderTitle()?.visible()

            }
        } else {
            fragmentSingleStatusBinding?.apply {
                singleStatusWidget.visible()
                kitchenTimerFarView.gone()
                KitchenTimerFarViewHeaderBar.gone()
                KitchenTimerFarViewHeaderBar.getHeaderTitle()?.gone()

            }
        }
    }

    override fun getKitchenTimerRunningText(): AppCompatTextView? {
        return fragmentSingleStatusBinding?.textViewKitchenTimerRunningTextFarView
    }

    override fun getKitchenTimerIcon(): AppCompatImageView? {
        return fragmentSingleStatusBinding?.iconKitchenTimerFarView
    }

    override fun getHeaderBar(): HeaderBarWidget? {
        return fragmentSingleStatusBinding?.KitchenTimerFarViewHeaderBar
    }

    /**
     * Play fade in and out animation for kitchen timer view and status far view
     */
    private fun playFadeInFadeOutAnimation(isKitchenTimerEnding: Boolean) {
        val fadeOut = Fade(Fade.OUT).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = DecelerateInterpolator()
            fragmentSingleStatusBinding?.apply {
                if (isKitchenTimerEnding) {
                    addTarget(singleStatusWidget)
                } else {
                    addTarget(kitchenTimerFarView)
                    addTarget(KitchenTimerFarViewHeaderBar)
                    KitchenTimerFarViewHeaderBar.getHeaderTitle()?.let { addTarget(it) }
                }

            }

        }
        val fadeIn = Fade(Fade.IN).apply {
            duration = AppConstants.FAR_VIEW_ANIMATION_600
            interpolator = AccelerateInterpolator()
            fragmentSingleStatusBinding?.apply {
                if (isKitchenTimerEnding) {
                    addTarget(kitchenTimerFarView)
                    addTarget(KitchenTimerFarViewHeaderBar)
                    KitchenTimerFarViewHeaderBar.getHeaderTitle()?.let { addTarget(it) }
                } else {
                    addTarget(singleStatusWidget)
                }
            }
        }
        val transitionSet = TransitionSet().apply {
            ordering = TransitionSet.ORDERING_TOGETHER
            addTransition(fadeOut)
            addTransition(fadeIn)
        }
        TransitionManager.beginDelayedTransition(
            fragmentSingleStatusBinding?.root as ViewGroup,
            transitionSet
        )
    }
}