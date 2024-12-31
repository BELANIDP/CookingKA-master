/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package com.whirlpool.cooking.ka.test.cucumber.steps

import com.whirlpool.cooking.ka.test.cucumber.appearance.SettingsFragmentAppearanceTest
import io.cucumber.java.After
import io.cucumber.java.Before
import io.cucumber.java.en.And
import leakcanary.DetectLeaksAfterTestSuccess
import leakcanary.LeakAssertions
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


/**
 * File        : com.whirlpool.cooking.ka.test.cucumber.steps.SettingsSteps
 * Brief       : Settings screen automation test cases
 * Author      : GHARDNS/Nikki
 * Created On  : 27/02/2024
 */
@RunWith(JUnit4::class)
class SettingsSteps {

    private var settingsFragmentAppearanceTest: SettingsFragmentAppearanceTest? = null

    @get:Rule
    val rule = DetectLeaksAfterTestSuccess()

    @Before
    fun setUp() {
        settingsFragmentAppearanceTest = SettingsFragmentAppearanceTest()
    }

    @After
    fun tearDown() {
        settingsFragmentAppearanceTest = null
    }

    @And("Settings screen has started")
    fun appHasStartedOnTheSettingsFragment() {
        settingsFragmentAppearanceTest?.performClickOnSettings()
    }
    @And("Settings screen has started for combo")
    fun appHasStartedOnTheSettingsFragmentForCombo() {
        settingsFragmentAppearanceTest?.performClickOnSettingsForCombo()
    }

    @And("I navigate to settings screen")
    fun iNavigateToSettingsScreen() {
        LeakAssertions.assertNoLeaks()
        settingsFragmentAppearanceTest?.isNestedScrollViewVisible()
        LeakAssertions.assertNoLeaks()
    }

    @And("I click on self clean")
    fun iClickOnSelfCleanMenu() {
        settingsFragmentAppearanceTest?.performClickOnSettingsListItem()
    }
}