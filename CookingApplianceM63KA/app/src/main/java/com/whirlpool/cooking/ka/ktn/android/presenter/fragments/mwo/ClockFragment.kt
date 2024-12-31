/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.mwo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import core.jbase.AbstractClockFragment
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.LowPowerModeUtils.Companion.isSafeToEnterLowPower

/**
 * File       : android.presenter.fragments.mwo.ClockFragment.
 * Brief      : This class provides the clock screen after the splash screen is loaded successfully
 * Author     : PATELJ7
 * Created On : 01-02-2024
 */
class ClockFragment : AbstractClockFragment(), HMIExpansionUtils.UserInteractionListener {

    private val timeoutViewModel: TimeoutViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setFragmentUserInteractionListener(this)
    }
    override fun manageChildViews() {
        super.manageChildViews()
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeFragmentUserInteractionListener(this)
        super.onDestroyView()
    }

    override fun provideVisibilityOfSabbathErrorTextView(): Int {
        return View.GONE
    }
}