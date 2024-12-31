package core.viewHolderHelpers

import android.os.Bundle
import android.presenter.basefragments.AbstractStatusFragment
import android.presenter.basefragments.abstract_view_helper.AbstractStatusViewHelper
import android.presenter.customviews.widgets.headerbar.NoTitleHeaderBarWidget
import android.presenter.customviews.widgets.status.CookingStatusWidget
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentDoubleStatusBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory

/**
 * File        : core.viewHolderHelpers.DoubleStatusViewHelper
 * Brief       : Helper class to provide single lower and upper status binding along with its helpers
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing individual widget status with whole fragment
 */
class DoubleStatusViewHelper : AbstractStatusViewHelper() {
    private var fragmentDoubleStatusBinding: FragmentDoubleStatusBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentDoubleStatusBinding =
            FragmentDoubleStatusBinding.inflate(inflater, container, false)
        return fragmentDoubleStatusBinding!!.root
    }

    override fun onDestroyView() {
        fragmentDoubleStatusBinding?.invalidateAll()
        fragmentDoubleStatusBinding = null
    }

    override fun getLayoutViewBinding(): ViewDataBinding? {
        return fragmentDoubleStatusBinding
    }

    override fun setupBindingData(fragment: AbstractStatusFragment?) {
        fragmentDoubleStatusBinding?.statusFragment = fragment
        fragmentDoubleStatusBinding?.primaryCookingViewModel =
            CookingViewModelFactory.getPrimaryCavityViewModel()
        fragmentDoubleStatusBinding?.secondaryCookingViewModel =
            CookingViewModelFactory.getSecondaryCavityViewModel()
        fragmentDoubleStatusBinding?.primaryRecipeExecutionViewModel =
            CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel
        fragmentDoubleStatusBinding?.secondaryRecipeExecutionViewModel =
            CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel
        getDefaultCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { AppCompatResources.getDrawable(it, if(fragmentDoubleStatusBinding?.primaryRecipeExecutionViewModel?.isVirtualchefBasedRecipe == true) R.drawable.ic_assisted else R.drawable.ic_oven_cavity) })
        getLowerCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { AppCompatResources.getDrawable(it, if(fragmentDoubleStatusBinding?.secondaryRecipeExecutionViewModel?.isVirtualchefBasedRecipe == true) R.drawable.ic_assisted else R.drawable.ic_lower_cavity) })
    }

    override fun getUpperViewModel(): CookingViewModel? {
        return fragmentDoubleStatusBinding?.primaryCookingViewModel
    }

    override fun getLowerViewModel(): CookingViewModel? {
        return fragmentDoubleStatusBinding?.secondaryCookingViewModel
    }

    override fun provideLowerCavitySelectionIcon(): ImageView? {
        return null
    }

    override fun provideHeaderBarWidget(): NoTitleHeaderBarWidget? {
        return fragmentDoubleStatusBinding?.ovenHeader
    }

    override fun getDefaultCookingStatusWidget(): CookingStatusWidget? {
        return fragmentDoubleStatusBinding?.doubleStatusWidgetUpper
    }

    override fun getLowerCookingStatusWidget(): CookingStatusWidget? {
        return fragmentDoubleStatusBinding?.doubleStatusWidgetLower
    }

    override fun provideUpperCavitySelectionLayout(): ConstraintLayout? {
        return null
    }

    override fun provideUpperCavitySelection(): TextView? {
        return null
    }

    override fun provideUpperCavitySelectionIcon(): ImageView? {
        return null
    }

    override fun provideLowerCavitySelectionLayout(): ConstraintLayout? {
        return null
    }

    override fun provideLowerCavitySelection(): TextView? {
        return null
    }
}