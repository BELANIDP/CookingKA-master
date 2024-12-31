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
import com.whirlpool.cooking.ka.databinding.SabbathFragmentSingleStatusBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.AppConstants

/**
 * File        : core.viewHolderHelpers.SingleSabbathStatusViewHelper
 * Brief       : Helper class to provide single status binding along with its helpers for Sabbath Bake
 * Author      : Hiren
 * Created On  : 08/23/2024
 * Details     : This class provides  whole view containing individual widget status with whole fragment for Sabbath Bake
 */
class SingleSabbathStatusViewHelper : AbstractStatusViewHelper() {
     private var fragmentSingleStatusBinding: SabbathFragmentSingleStatusBinding? = null
    lateinit var cookingViewModel: CookingViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSingleStatusBinding =
            SabbathFragmentSingleStatusBinding.inflate(inflater, container, false)
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
        cookingViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
        fragmentSingleStatusBinding?.cookingViewModel = cookingViewModel
        fragmentSingleStatusBinding?.recipeExecutionViewModel =
            cookingViewModel.recipeExecutionViewModel
        getDefaultCookingStatusWidget()?.statusWidgetHelper?.getCavityIcon()?.setImageDrawable(
            fragment?.requireContext()
                ?.let { AppCompatResources.getDrawable(it, R.drawable.ic_oven_cavity) })
    }

    override fun getUpperViewModel(): CookingViewModel? {
        return fragmentSingleStatusBinding?.cookingViewModel
    }

    override fun getLowerViewModel(): CookingViewModel? {
        return null
    }

    override fun provideLowerCavitySelectionIcon(): ImageView? {
        return null
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
        return null
    }

    override fun provideLowerCavitySelection(): TextView? {
        return null
    }
}