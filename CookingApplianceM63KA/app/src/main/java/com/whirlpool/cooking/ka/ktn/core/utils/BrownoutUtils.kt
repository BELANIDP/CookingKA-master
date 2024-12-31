package core.utils

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import com.whirlpool.cooking.ka.R
import core.utils.CookingAppUtils.Companion.getVisibleFragment
import core.utils.CookingAppUtils.Companion.navigateToStatusOrClockScreen

class BrownoutUtils {
    companion object {
        /**
         * Method to handle the brownout navigation according to the active oven variant status
         */
        fun handleBrownoutNavigation(
            navGraph: NavGraph,
            navController: NavController,
            activity: FragmentActivity
        ) {
            HMILogHelper.Logi("Navigating to Brownout handling")
            if (CookingAppUtils.isPyroliticClean()) {
                navGraph.startDestination = R.id.selfCleanStatusFragment
                navController.graph = navGraph
            } else if(CookingAppUtils.isDemoModeEnabled() && !CookingAppUtils.isAnyCycleRunning()){
                navController.graph = navGraph
                getVisibleFragment(activity.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it,
                        R.id.global_action_to_demoModeLandingFragment,
                        null,
                        null
                    )
                }
            } else {
                navController.graph = navGraph
                getVisibleFragment(activity.supportFragmentManager)?.let {
                    navigateToStatusOrClockScreen(
                        it
                    )
                }
            }
        }
    }
}