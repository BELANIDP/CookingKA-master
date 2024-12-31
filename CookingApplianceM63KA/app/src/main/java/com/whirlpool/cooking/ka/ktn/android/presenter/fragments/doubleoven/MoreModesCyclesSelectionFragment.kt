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
 * File       : android.presenter.fragments.doubleoven.MoreModesCyclesSelectionFragment.
 * Brief      : implementation fragment class for More modes cycles grid list for manual modes.
 * Author     : Vishal.
 * Created On : 08/07/2024
 * Details    :
 */
class MoreModesCyclesSelectionFragment : BaseChildCyclesSelectionFragment() {
    override fun provideBaseRecipeName(): String {
        return AppConstants.RECIPE_MORE_MODES
    }

    override fun provideHeaderBarTitle(): String {
        return getString(R.string.text_header_more_modes)
    }
}





