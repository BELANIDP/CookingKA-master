package android.presenter.fragments.sabbath

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
import com.whirlpool.cooking.ka.databinding.SabbathFragmentDoubleStatusBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants

/**
 * File        : core.viewHolderHelpers.SingleSabbathStatusViewHelper
 * Brief       : Helper class to provide Double status binding along with its helpers for Sabbath Bake
 * Author      : Hiren
 * Created On  : 08/23/2024
 * Details     : This class provides  whole view containing individual widget status with whole fragment for Sabbath Bake when Both Cavities are running Sabbath Recipes
 */
class DoubleSabbathStatusViewHelper : AbstractStatusViewHelper() {
     private var fragmentDoubleStatusBinding: SabbathFragmentDoubleStatusBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentDoubleStatusBinding =
            SabbathFragmentDoubleStatusBinding.inflate(inflater, container, false)
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
        val primaryCookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
        val secondaryCookingViewModel = CookingViewModelFactory.getSecondaryCavityViewModel()
        fragmentDoubleStatusBinding?.primaryCookingViewModel = primaryCookingViewModel
        fragmentDoubleStatusBinding?.primaryRecipeExecutionViewModel =
            primaryCookingViewModel.recipeExecutionViewModel

        fragmentDoubleStatusBinding?.secondaryCookingViewModel = secondaryCookingViewModel
        fragmentDoubleStatusBinding?.secondaryRecipeExecutionViewModel =
            secondaryCookingViewModel.recipeExecutionViewModel

        getDefaultCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { AppCompatResources.getDrawable(it, R.drawable.ic_oven_cavity) })
        getLowerCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { AppCompatResources.getDrawable(it, R.drawable.ic_lower_cavity) })
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
        return fragmentDoubleStatusBinding?.singleUpperStatusWidget
    }

    override fun getLowerCookingStatusWidget(): CookingStatusWidget? {
        return fragmentDoubleStatusBinding?.singleLowerStatusWidget
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