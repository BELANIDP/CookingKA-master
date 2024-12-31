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
import com.whirlpool.cooking.ka.databinding.FragmentSingleStatusBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory

/**
 * File        : core.viewHolderHelpers.SingleStatusViewHelper
 * Brief       : Helper class to provide single status binding along with its helpers
 * Author      : Hiren
 * Created On  : 03/15/2024
 * Details     : This class provides  whole view containing individual widget status with whole fragment
 */
class SingleStatusViewHelper : AbstractStatusViewHelper() {
     private var fragmentSingleStatusBinding: FragmentSingleStatusBinding? = null
    lateinit var cookingViewModel: CookingViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSingleStatusBinding =
            FragmentSingleStatusBinding.inflate(inflater, container, false)
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
        getDefaultCookingStatusWidget()?.ovenType = fragment?.getString(R.string.cavity_selection_upper_oven).toString()
        cookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
        fragmentSingleStatusBinding?.cookingViewModel = cookingViewModel
        fragmentSingleStatusBinding?.recipeExecutionViewModel =
            cookingViewModel.recipeExecutionViewModel
        getDefaultCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { AppCompatResources.getDrawable(it, if(cookingViewModel.recipeExecutionViewModel.isVirtualchefBasedRecipe) R.drawable.ic_assisted else R.drawable.ic_oven_cavity ) })
    }

    override fun getUpperViewModel(): CookingViewModel? {
        return fragmentSingleStatusBinding?.cookingViewModel
    }

    override fun getLowerViewModel(): CookingViewModel? {
        return null
    }

    override fun provideLowerCavitySelectionIcon(): ImageView? {
        return fragmentSingleStatusBinding?.lowerCavityIcon
    }

    override fun provideHeaderBarWidget(): NoTitleHeaderBarWidget? {
        return fragmentSingleStatusBinding?.ovenHeader
    }


    override fun getDefaultCookingStatusWidget(): CookingStatusWidget? {
        return fragmentSingleStatusBinding?.singleStatusWidget
    }

    override fun getLowerCookingStatusWidget(): CookingStatusWidget? {
        return null
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
        return fragmentSingleStatusBinding?.clLowerCavitySelectionLayout
    }

    override fun provideLowerCavitySelection(): TextView? {
        return fragmentSingleStatusBinding?.btnLowerCavitySelection
    }
}