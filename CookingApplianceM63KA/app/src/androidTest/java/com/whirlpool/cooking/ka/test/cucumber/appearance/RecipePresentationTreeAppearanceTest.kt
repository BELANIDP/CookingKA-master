package com.whirlpool.cooking.ka.test.cucumber.appearance

import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.checkText
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.convertStringToType
import com.whirlpool.hmi.uitesting.UiTestingUtils

class RecipePresentationTreeAppearanceTest {
    fun checkLinearRecycler(position: String, recipe: String) {
        UiTestingUtils.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.tumblerString))
            .perform(
                TestingUtils.withRecyclerViewScrollToPosition(
                    convertStringToType(
                        position,
                        Int::class.java
                    )
                )
            )
        UiTestingUtils.sleep(500)
        Espresso.onView(
            UiTestingUtils.matchRecyclerViewItem(
                R.id.tumblerString,
                convertStringToType(position, Int::class.java),
                R.id.title
            )
        )
            .check { view, _ -> checkText(recipe, view) }
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClickOnRecyclerViewIndex(
            R.id.tumblerString,
            (convertStringToType(position, Int::class.java))
        )
    }

    fun checkGridRecycler(position: String, recipe: String) {
        UiTestingUtils.sleep(500)
        Espresso.onView(ViewMatchers.withId(R.id.recycler_view_grid_list))
            .perform(
                TestingUtils.withRecyclerViewScrollToPosition(
                    convertStringToType(
                        position,
                        Int::class.java
                    )
                )
            )
        UiTestingUtils.sleep(500)
        Espresso.onView(
            UiTestingUtils.matchRecyclerViewItem(
                R.id.recycler_view_grid_list,
                convertStringToType(position, Int::class.java),
                R.id.text_view_convect_option_name
            )
        )
            .check { view, _ -> checkText(recipe, view) }
        UiTestingUtils.sleep(500)
        UiTestingUtils.performClickOnRecyclerViewIndex(
            R.id.recycler_view_grid_list,
            (convertStringToType(position, Int::class.java))
        )
    }
}

