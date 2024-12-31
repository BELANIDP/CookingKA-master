package android.presenter.fragments.singleoven

import android.view.View
import androidx.fragment.app.viewModels
import com.whirlpool.cooking.common.utils.TimeoutViewModel
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import core.jbase.AbstractClockFragment
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.fragments.ClockFragment.
 * Brief      : This class provides the clock screen after the splash screen is loaded successfully
 * Author     : PATELJ7
 * Created On : 01-02-2024
 */
class ClockFragment : AbstractClockFragment() {

    private val timeoutViewModel: TimeoutViewModel by viewModels()
    override fun manageChildViews() {
        super.manageChildViews()
        observeDoorInteraction(CookingViewModelFactory.getPrimaryCavityViewModel())
    }
    override fun provideVisibilityOfSabbathErrorTextView(): Int {
        if(CookingAppUtils.isSabbathMode()) {
            CookingViewModelFactory.getPrimaryCavityViewModel().recipeExecutionViewModel.cancel()
            HMILogHelper.Logd("cancelling recipe of primary cavity provideVisibilityOfSabbathErrorTextView")
            if (CookingAppUtils.isCavityFaultNone(CookingViewModelFactory.getPrimaryCavityViewModel())) return View.GONE
            return View.VISIBLE
        }
        return View.GONE
    }
}

