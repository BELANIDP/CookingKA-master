package android.presenter.fragments.mwo

import android.os.Bundle
import android.presenter.basefragments.AbstractFarStatusFragment
import android.presenter.basefragments.abstract_view_helper.AbstractFarStatusViewHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whirlpool.hmi.cooking.utils.Constants
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.viewHolderHelpers.SingleFarStatusViewHelper

/**
 * File        : android.presenter.fragments.mwo.SingleFarStatusFragment
 * Brief       : Common Single Far View status, default to primaryCavity as only meant for Single Oven
 * Author      : Hiren
 * Created On  : 05/01/2024
 * Details     : This class provides  whole view containing cooking status widget along with its helpers.
 * Override AbstractFarStatusFragment protected method to have individual functionality related to this variant only
 */
class SingleFarStatusFragment : AbstractFarStatusFragment(){
    private lateinit var statusViewHelper: AbstractFarStatusViewHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // default to primaryCavity to load CookingViewModel
        statusViewHelper = SingleFarStatusViewHelper(Constants.PRIMARY_CAVITY_KEY)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun provideViewHolderHelper(): AbstractFarStatusViewHelper {
        return statusViewHelper
    }
    override fun manageChildViews() {
        super.manageChildViews()
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
    }
    override fun provideVisibilityOfCompletedSinceTextView(): Int {
        return View.VISIBLE
    }
    override fun provideVisibilityOfCavityIcon(): Int {
        return View.GONE
    }
}