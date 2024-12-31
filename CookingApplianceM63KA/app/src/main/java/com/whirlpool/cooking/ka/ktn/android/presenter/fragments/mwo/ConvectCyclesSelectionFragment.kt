/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */

package android.presenter.fragments.mwo

import android.presenter.basefragments.BaseChildCyclesSelectionFragment
import com.whirlpool.cooking.ka.R
import core.utils.AppConstants

class ConvectCyclesSelectionFragment : BaseChildCyclesSelectionFragment() {
    override fun provideBaseRecipeName(): String {
        return AppConstants.RECIPE_CONVECT
    }

    override fun provideHeaderBarTitle(): String {
        return getString(R.string.convect)
    }
}