package com.whirlpool.cooking.ka.test.cucumber.navigation

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uitesting.UiTestingUtils
import org.junit.Assert
import java.util.Objects

/**
 * File       : com.whirlpool.cooking.ka.test.cucumber.navigation
 * Brief      : Navigation for fragment
 * Author     : YADAVS4
 * Created On : 19/02/2024
 * Details    : This file allows to set destination to Nav-Graph and also provide
 *              methods to validate the current Nav-Destination
 */


var testNavHostController = TestNavHostController(ApplicationProvider.getApplicationContext())

fun <T> checkCurrentFragmentDestinationMatched(
    fragmentClass: T, currentDesId: Int, expectedDesId: Int, navGraphId: Int
) {
    setCurrentDestinationToNavHostController(fragmentClass, currentDesId, navGraphId)
    Assert.assertEquals(
        Objects.requireNonNull<NavDestination?>(
            testNavHostController.currentDestination
        ).id, expectedDesId
    )
}


fun <T> checkCurrentFragmentDestinationNotMatched(
    fragmentClass: T, currentDesId: Int, expectedDesId: Int, navGraphId: Int
) {
    setCurrentDestinationToNavHostController(fragmentClass, currentDesId, navGraphId)
    Assert.assertNotEquals(
        Objects.requireNonNull<NavDestination?>(
            testNavHostController.currentDestination
        ).id, expectedDesId
    )
}

fun <T> getFragmentScenario(fragmentClass: T) = FragmentScenario.launchInContainer(
    fragmentClass as Class<Fragment>, themeResId = R.style.Theme_CookingApplianceM63KA
)

fun <T> setFragmentScenario(fragmentClass: T) = getFragmentScenario(fragmentClass).onFragment {
    setFragmentOrientation(it.activity)
}
fun <T> getFragmentScenario(fragmentClass: T,lifecycle:Lifecycle.State) = FragmentScenario.launchInContainer(
    fragmentClass as Class<Fragment>, themeResId = R.style.Theme_CookingApplianceM63KA, initialState = lifecycle
)
fun <T> setFragmentScenarioTheme(fragmentClass: T) {
    FragmentScenario.launchInContainer(
        fragmentClass as Class<Fragment>, themeResId = R.style.Theme_CookingApplianceM63KA
    )
    UiTestingUtils.sleep(300)
}

fun <T> setCurrentDestinationToNavHostController(
    fragmentClass: T, currentDesId: Int, navGraphId: Int
) {
    getFragmentScenario(fragmentClass).onFragment { fragment: Fragment ->
        testNavHostController.setGraph(navGraphId)
        testNavHostController.setCurrentDestination(currentDesId)
        Navigation.setViewNavController(
            fragment.requireView(), testNavHostController
        )
        setFragmentOrientation(fragment.activity)
    }
    UiTestingUtils.sleep(300)
}

fun <T> setCurrentDestinationToNavHostController(
    fragmentClass: T, navGraphId: Int
) {
    getFragmentScenario(fragmentClass).onFragment { fragment: Fragment ->
        testNavHostController.setGraph(navGraphId)
        Navigation.setViewNavController(
            fragment.requireView(), testNavHostController
        )
        setFragmentOrientation(fragment.activity)
    }
    UiTestingUtils.sleep(300)
}

fun <T> setCurrentDestinationToNavHostController(
    fragmentClass: T, navGraphId: Int, testNavHostController: TestNavHostController
) {
    getFragmentScenario(fragmentClass).onFragment { fragment: Fragment ->
        testNavHostController.setGraph(navGraphId)
        Navigation.setViewNavController(
            fragment.requireView(), testNavHostController
        )
    }
    UiTestingUtils.sleep(300)
}

fun <T> setCurrentDestinationToNavHostController(
    fragmentClass: T, navGraphId: Int,lifecycle: State
) {
    getFragmentScenario(fragmentClass,lifecycle).onFragment { fragment: Fragment ->
        testNavHostController.setGraph(navGraphId)
        Navigation.setViewNavController(
            fragment.requireView(), testNavHostController
        )
    }
    UiTestingUtils.sleep(300)
}
fun setFragmentOrientation(activity: Activity?) {
    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
    Thread.sleep(300)
}
