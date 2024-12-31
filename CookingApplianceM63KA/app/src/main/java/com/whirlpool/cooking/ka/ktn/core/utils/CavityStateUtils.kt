package core.utils

import android.content.res.Resources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory


object CavityStateUtils {
    /**
     * Navigates to appropriate screens from the programming screens on timeout.
     *
     * @param fragment Fragment instance.
     */
    fun onTimeoutProgrammingState(fragment: Fragment?) {
        var assignedFragment = fragment
        val currentCavityState = cavityState
        if (currentCavityState == CavityStateEnum.CAVITY_STATE_NONE_RUNNING) {
            HMILogHelper.Logi("Cancelling programming cycle in case of timeout and navigating to clock screen")
            CookingViewModelFactory.getInScopeViewModel()?.cancel()
        }
        if (fragment is DialogFragment) {
            assignedFragment = NavigationUtils.getVisibleFragment()
        }
        assignedFragment?.let { CookingAppUtils.navigateToStatusOrClockScreen(it) }
    }

    private val cavityState: CavityStateEnum
        /**
         * @return Current cavity state as defined by [CavityStateEnum].
         */
        get() {
            var cavityStateEnum = CavityStateEnum.CAVITY_STATE_NONE_RUNNING

            when (CookingViewModelFactory.getProductVariantEnum()) {
                CookingViewModelFactory.ProductVariantEnum.COMBO, CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                    val primaryRunning =
                        CookingViewModelFactory.getPrimaryCavityViewModel()?.recipeExecutionViewModel?.isRunning
                    val secondaryRunning =
                        CookingViewModelFactory.getSecondaryCavityViewModel()?.recipeExecutionViewModel?.isRunning
                    cavityStateEnum = if (primaryRunning == true && secondaryRunning == true) {
                        CavityStateEnum.CAVITY_STATE_BOTH_RUNNING
                    } else if (primaryRunning == false && secondaryRunning == false) {
                        CavityStateEnum.CAVITY_STATE_NONE_RUNNING
                    } else if (primaryRunning == true) {
                        CavityStateEnum.CAVITY_STATE_PRIMARY_RUNNING
                    } else {
                        CavityStateEnum.CAVITY_STATE_SECONDARY_RUNNING
                    }
                }

                else -> {}
            }
            return cavityStateEnum
        }

    /**
     * Gets the programming state timeout value based on the current cavity state.
     *
     * @param resources Resources instance.
     * @return Timeout value in milliseconds.
     */

    fun getProgrammingStateTimeoutValue(resources: Resources): Int {
        HMILogHelper.Logd("In scope view model : " + CookingViewModelFactory.getInScopeViewModel()?.cavityName?.value)
        val variant = CookingViewModelFactory.getProductVariantEnum()
        // Determine the timeout value based on the current cavity state
        val timeout: Int = when (cavityState) {
            CavityStateEnum.CAVITY_STATE_NONE_RUNNING -> resources.getInteger(R.integer.session_short_timeout)

            CavityStateEnum.CAVITY_STATE_SECONDARY_RUNNING,CavityStateEnum.CAVITY_STATE_PRIMARY_RUNNING ->
                if (CookingViewModelFactory.getOutOfScopeCookingViewModel().recipeExecutionViewModel.isRunning && variant == CookingViewModelFactory.ProductVariantEnum.COMBO) {
                    resources.getInteger(R.integer.status_edit_timeout_2)
                } else {
                    resources.getInteger(R.integer.status_edit_timeout_1)
                }

            CavityStateEnum.CAVITY_STATE_BOTH_RUNNING -> resources.getInteger(R.integer.status_edit_timeout_1)

        }
        HMILogHelper.Logd("Programming State Timeout value : $timeout")
        return timeout
    }

    enum class CavityStateEnum {
        CAVITY_STATE_NONE_RUNNING,

        // No cavity is running.
        CAVITY_STATE_PRIMARY_RUNNING,

        // Cavity selected (being programmed) == running cavity.
        CAVITY_STATE_SECONDARY_RUNNING,

        // Cavity selected (being programmed) != running cavity.
        CAVITY_STATE_BOTH_RUNNING // Both the cavities are running.
    }
}