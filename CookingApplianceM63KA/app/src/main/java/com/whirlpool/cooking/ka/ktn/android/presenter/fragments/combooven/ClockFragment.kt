package android.presenter.fragments.combooven

import android.view.View
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import core.jbase.AbstractClockFragment
import core.utils.CookingAppUtils
import core.utils.HMILogHelper

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.fragments.ClockFragment.
 * Brief      : This class provides the clock screen after the splash screen is loaded successfully
 * Author     : PATELJ7
 * Created On : 01-02-2024
 */
class ClockFragment : AbstractClockFragment() {
    override fun manageChildViews() {
        super.manageChildViews()
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
        observeDoorInteraction(CookingViewModelFactory.getSecondaryCavityViewModel())
    }

    override fun provideVisibilityOfSabbathErrorTextView(): Int {
        if(CookingAppUtils.isSabbathMode()) {
            HMILogHelper.Logd(
                "Clock fragment cancel",
                "cancelled recipe for ${CookingViewModelFactory.getSecondaryCavityViewModel().cavityName.value}"
            )
            CookingViewModelFactory.getSecondaryCavityViewModel().recipeExecutionViewModel.cancel()
            if (CookingAppUtils.isCavityFaultNone(CookingViewModelFactory.getSecondaryCavityViewModel())) return View.GONE
            return View.VISIBLE
        }
        return View.GONE
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}

