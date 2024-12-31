/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */

package android.presenter.fragments.doubleoven

import android.presenter.basefragments.BaseChildCyclesSelectionFragment
import com.whirlpool.cooking.ka.R
import core.utils.AppConstants

/**
 * File       : android.presenter.fragments.doubleoven.ConvectCyclesSelectionFragment.
 * Brief      : implementation fragment class for Convect cycles grid list for manual modes.
 * Author     : BHIMAR.
 * Created On : 21/03/2024
 * Details    :
 */
class ConvectCyclesSelectionFragment : BaseChildCyclesSelectionFragment() {
    override fun provideBaseRecipeName(): String {
        return AppConstants.RECIPE_CONVECT
    }

    override fun provideHeaderBarTitle(): String {
        return getString(R.string.convect)
    }
}