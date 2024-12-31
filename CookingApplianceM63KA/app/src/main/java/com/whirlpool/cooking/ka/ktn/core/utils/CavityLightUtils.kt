package core.utils

import android.content.res.Resources
import com.whirlpool.cooking.ka.R
import core.utils.CookingAppUtils.Companion.getCavityState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory

/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */


/**
 * File       : core/utils/CavityLightUtils.java
 * Brief      : Contains helper utility methods which provides APIs to access cavity light related.
 * Author     : PETEG
 * Created On : 5/3/24
 * Details    : This Util class is to handle all the Cavity Light Related features.
 */
class CavityLightUtils {
    enum class CavityStateEnum {
        CAVITY_STATE_NONE_RUNNING,  // No cavity is running.
        CAVITY_STATE_PRIMARY_RUNNING,  // Cavity selected (being programmed) == running cavity.
        CAVITY_STATE_SECONDARY_RUNNING,  // Cavity selected (being programmed) != running cavity.
        CAVITY_STATE_BOTH_RUNNING // Both the cavities are running.
    }

    companion object {
        /**
         * Method to set the Primary Cavity Light to true or false
         *
         * @param state state of the primary cavity light to be set
         */
        fun setPrimaryCavityLightState(state: Boolean) {
            setCavityLightState(true, state)
        }

        /**
         * Method to set the Secondary Cavity Light to true or false
         *
         * @param state state of the Secondary cavity light to be set
         */
        fun setSecondaryCavityLightState(state: Boolean) {
            setCavityLightState(false, state)
        }

        /**
         * Method to set the Cavity Light to true or false
         *
         * @param isPrimaryCavity true, if cavity light needs to be set for primary cavity
         * @param state           state of the Secondary cavity light to be set
         */
        fun setCavityLightState(isPrimaryCavity: Boolean, state: Boolean) {
            val selectedViewModel: CookingViewModel
            val selectedCavityDbgString: String
            if (isPrimaryCavity) {
                selectedViewModel = CookingViewModelFactory.getPrimaryCavityViewModel()
                //Since this string use for logging, using it directly
                selectedCavityDbgString = "PrimaryCavityLight"
            } else {
                selectedViewModel = CookingViewModelFactory.getSecondaryCavityViewModel()
                selectedCavityDbgString = "SecondaryCavityLight"
            }
            if (java.lang.Boolean.FALSE == (selectedViewModel.lightState.value == state)) {
                if (selectedViewModel.setLightState(state)) {
                    HMILogHelper.Logi("$selectedCavityDbgString is successfully set $state")
                } else {
                    HMILogHelper.Loge("$selectedCavityDbgString is failed to set $state")
                }
            } else HMILogHelper.Logi("$selectedCavityDbgString Is Already In $state state")
        }

        /**
         * Returns the timeout value for programming screens.
         *
         *
         */
        fun getProgrammingStateTimeoutValue(resources: Resources): Int {
            var timeout = 0
            when (getCavityState()) {
                CavityStateEnum.CAVITY_STATE_NONE_RUNNING -> timeout =
                    resources.getInteger(R.integer.session_short_timeout)

                CavityStateEnum.CAVITY_STATE_SECONDARY_RUNNING -> timeout =
                    resources.getInteger(R.integer.status_edit_timeout_2)

                CavityStateEnum.CAVITY_STATE_BOTH_RUNNING, CavityStateEnum.CAVITY_STATE_PRIMARY_RUNNING -> timeout =
                    resources.getInteger(R.integer.status_edit_timeout_1)

                else -> {}
            }
            return timeout
        }

        /**
         * Returns the secondary cavity light state.
         */
        fun getSecondaryCavityLightState(): Boolean =
            CookingViewModelFactory.getSecondaryCavityViewModel().lightState
                .value ?: false

        /**
         * Returns the primary cavity light state.
         */
        fun getPrimaryCavityLightState(): Boolean =
            CookingViewModelFactory.getPrimaryCavityViewModel().lightState.value ?: false

    }


}