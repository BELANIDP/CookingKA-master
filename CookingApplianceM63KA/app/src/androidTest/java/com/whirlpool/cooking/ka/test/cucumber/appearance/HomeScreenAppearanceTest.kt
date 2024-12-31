package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkText
import com.whirlpool.hmi.uitesting.UiTestingUtils

class HomeScreenAppearanceTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    fun performClickOnClockScreen() {
        UiTestingUtils.performClick(R.id.main_layout)
    }

    fun verifyCavitySelectionScreen(cavity1: String, cavity2: String) {
        Espresso.onView(ViewMatchers.withId(R.id.uppper_cavity_lbl))
            .check { view, _ -> checkText(cavity1, view) }
        Espresso.onView(ViewMatchers.withId(R.id.lower_cavity_lbl))
            .check { view, _ -> checkText(cavity2, view) }
    }
}

