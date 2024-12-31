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
import com.whirlpool.cooking.ka.databinding.FragmentSingleStatusLowerBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory

/**
 * File        : core.viewHolderHelpers.SingleStatusLowerViewHelper
 * Brief       : Helper class to provide single lower status binding along with its helpers
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing individual widget status with whole fragment
 */
class SingleStatusLowerViewHelper : AbstractStatusViewHelper() {
     private var fragmentSingleStatusBinding: FragmentSingleStatusLowerBinding? = null
    lateinit var cookingViewModel: CookingViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSingleStatusBinding =
            FragmentSingleStatusLowerBinding.inflate(inflater, container, false)
        return fragmentSingleStatusBinding!!.root
    }

    override fun onDestroyView() {
        fragmentSingleStatusBinding?.invalidateAll()
        fragmentSingleStatusBinding = null
    }

    override fun getLayoutViewBinding(): ViewDataBinding? {
        return fragmentSingleStatusBinding
    }

    override fun setupBindingData(fragment: AbstractStatusFragment?) {
        fragmentSingleStatusBinding?.statusFragment = fragment
        cookingViewModel = CookingViewModelFactory.getSecondaryCavityViewModel()
        getLowerCookingStatusWidget()?.ovenType =fragment?.getString(R.string.cavity_selection_lower_oven).toString()
        getLowerCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { AppCompatResources.getDrawable(it, if(cookingViewModel.recipeExecutionViewModel.isVirtualchefBasedRecipe) R.drawable.ic_assisted else R.drawable.ic_lower_cavity) })
        fragmentSingleStatusBinding?.cookingViewModel = cookingViewModel
        fragmentSingleStatusBinding?.recipeExecutionViewModel =
            cookingViewModel.recipeExecutionViewModel
    }

    override fun getUpperViewModel(): CookingViewModel? {
        return null
    }

    override fun getLowerViewModel(): CookingViewModel? {
        return fragmentSingleStatusBinding?.cookingViewModel
    }

    override fun provideLowerCavitySelectionIcon(): ImageView? {
        return fragmentSingleStatusBinding?.ivUpperCavityIcon
    }

    override fun provideHeaderBarWidget(): NoTitleHeaderBarWidget? {
        return fragmentSingleStatusBinding?.ovenHeader
    }
    override fun getDefaultCookingStatusWidget(): CookingStatusWidget? {
        return null
    }

    override fun getLowerCookingStatusWidget(): CookingStatusWidget? {
        return fragmentSingleStatusBinding?.singleStatusWidget
    }

    override fun provideUpperCavitySelectionLayout(): ConstraintLayout? {
        return fragmentSingleStatusBinding?.clUpperCavitySelectionLayout
    }

    override fun provideUpperCavitySelection(): TextView? {
        return fragmentSingleStatusBinding?.btnUpperCavitySelection
    }

    override fun provideUpperCavitySelectionIcon(): ImageView? {
        return fragmentSingleStatusBinding?.ivUpperCavityIcon
    }

    override fun provideLowerCavitySelectionLayout(): ConstraintLayout? {
        return null
    }

    override fun provideLowerCavitySelection(): TextView? {
        return null
    }
}