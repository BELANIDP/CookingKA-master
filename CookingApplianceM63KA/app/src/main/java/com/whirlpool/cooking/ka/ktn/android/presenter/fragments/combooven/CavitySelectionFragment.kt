/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.combooven

import android.presenter.basefragments.abstract_self_clean.AbstractCavitySelectionFragment
import android.view.View
import com.whirlpool.cooking.ka.R

/**
 * File       : android.presenter.fragments.combooven.CavitySelectionFragment.
 * Brief      : implementation fragment class for Cavity selection screen for manual modes.
 * Author     : BHIMAR.
 * Created On : 15/03/2024
 * Details    :
 */
class CavitySelectionFragment : AbstractCavitySelectionFragment() {

    override fun headerBarSetUp() {
        binding.singleLineHeaderBar.setOvenCavityIconVisibility(false)
        binding.singleLineHeaderBar.setInfoIconVisibility(false)
        binding.singleLineHeaderBar.setRightIconVisibility(false)
        binding.singleLineHeaderBar.setOvenCavityTitleTextVisibility(false)
        binding.singleLineHeaderBar.setLeftIconVisibility(false)
    }

    override fun providesBackPressNav() {
        animateLayoutsAndNavigate(R.id.upper_oven_area)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun checkUpperCavityAvailability() {
        // do not delete this empty method as logic is executing when self clean unavailable after 30 mins
    }

    override fun checkLowerCavityAvailability() {
        // do not delete this empty method as logic is executing when self clean unavailable after 30 mins
    }

    override fun updateTechnicianTextModeText() {
        binding.homeHeader.getBinding().demoIcon.visibility = View.VISIBLE
        binding.homeHeader.getBinding().demoIcon.text = resources.getString(R.string.text_test_status)
    }
}