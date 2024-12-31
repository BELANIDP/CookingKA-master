/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.combooven

import android.presenter.basefragments.manualmodes.AbstractRecipeInstructionFragment
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory

/**
 * File       : android.presenter.fragments.combooven.RecipeInstructionFragment.
 * Brief      : implementation fragment class for Recipe instructions screen for manual modes.
 * Author     : BHIMAR.
 * Created On : 15/03/2024
 * Details    :
 */
class RecipeInstructionFragment : AbstractRecipeInstructionFragment() {
    override fun manageHeaderBarParametersBasedOnVariant() {
        setHeaderBarOvenCavityViewVisibility(View.GONE)
        setHeaderBarOvenCavityIcon(if (CookingViewModelFactory.getInScopeViewModel().isPrimaryCavity) R.drawable.ic_oven_cavity_large else R.drawable.ic_lower_cavity_large)
    }
}