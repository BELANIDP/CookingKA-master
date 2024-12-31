package android.presenter.fragments.doubleoven

import android.os.Bundle
import android.presenter.basefragments.AbstractFarStatusFragment
import android.presenter.basefragments.abstract_view_helper.AbstractFarStatusViewHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.viewHolderHelpers.DoubleFarStatusViewHelper

/**
 * File        : android.presenter.fragments.doubleoven.DoubleFarStatusFragment
 * Brief       : Double status to show dual cavity running on Far Status Screen
 * Author      : Hiren
 * Created On  : 05/01/2024
 * Details     : This class provides  whole view containing cooking status widget along with its helpers.
 * Override AbstractFarStatusFragment protected method to have individual functionality related to this variant only
 */
class DoubleFarStatusFragment : AbstractFarStatusFragment(){
    private lateinit var statusViewHelper: AbstractFarStatusViewHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        statusViewHelper = DoubleFarStatusViewHelper()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun provideViewHolderHelper(): AbstractFarStatusViewHelper {
        return statusViewHelper
    }
    override fun manageChildViews() {
        super.manageChildViews()
        //observe on upper door
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
        //observe on lower door
        observeDoorInteraction(CookingViewModelFactory.getSecondaryCavityViewModel())
    }
    override fun provideVisibilityOfCompletedSinceTextView(): Int {
        return View.GONE
    }
    override fun provideVisibilityOfCavityIcon(): Int {
        return View.GONE
    }
}