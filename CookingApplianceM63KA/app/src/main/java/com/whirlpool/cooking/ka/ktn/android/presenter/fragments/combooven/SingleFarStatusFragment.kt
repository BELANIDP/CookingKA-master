package android.presenter.fragments.combooven

import android.os.Bundle
import android.presenter.basefragments.AbstractFarStatusFragment
import android.presenter.basefragments.abstract_view_helper.AbstractFarStatusViewHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.whirlpool.hmi.cooking.utils.Constants
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.utils.HMILogHelper
import core.viewHolderHelpers.SingleFarStatusViewHelper

/**
 * File        : android.presenter.fragments.combooven.SingleFarStatusFragment
 * Brief       : Common Single Far View status, need to specify CavityPosition if only lower cavity is running otherwise default to upper cavity view model
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
        // navigation arguments action data to specify cavity key
        var cavityPosition = arguments?.getString(KEY_STATUS_SCREEN_CAVITY_POSITION)
        HMILogHelper.Logd(tag, "load cooking view mode for: $cavityPosition")
        if(cavityPosition == null) cavityPosition = Constants.PRIMARY_CAVITY_KEY
        statusViewHelper = SingleFarStatusViewHelper(cavityPosition)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun provideViewHolderHelper(): AbstractFarStatusViewHelper {
        return statusViewHelper
    }
    override fun manageChildViews() {
        super.manageChildViews()
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
        observeDoorInteraction(CookingViewModelFactory.getSecondaryCavityViewModel())
    }
    override fun provideVisibilityOfCompletedSinceTextView(): Int {
        return View.VISIBLE
    }
    override fun provideVisibilityOfCavityIcon(): Int {
        return View.VISIBLE
    }
}