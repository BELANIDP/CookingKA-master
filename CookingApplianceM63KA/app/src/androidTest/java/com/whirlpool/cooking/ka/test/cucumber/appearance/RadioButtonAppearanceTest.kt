package com.whirlpool.cooking.ka.test.cucumber.appearance

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.whirlpool.cooking.ka.R
import android.presenter.customviews.radiobutton.RadioButton
import com.whirlpool.hmi.uitesting.UiTestingUtils


class RadioButtonAppearanceTest {

    fun checkAllViewsVisibility() {
//        UiTestingUtils.isViewVisible(R.id.Radio1)
    }

    fun isRadioButtonIsChecked(viewId: Int, checked: Boolean): Boolean {
        var isChecked = false
        onView(withId(viewId)).check { view, _ ->
            (view as RadioButton?)?.setChecked(checked)
            isChecked = (view)?.isChecked()!!
        }
        return isChecked
    }

    fun isRadioButtonIsEnabled(viewId: Int, checked: Boolean): Boolean {
        var isEnable = false
        onView(withId(viewId)).check { view, _ ->
            (view as RadioButton?)?.setEnabled(checked)
            isEnable = (view)?.isEnabled()!!
        }
        return isEnable
    }

    fun isRadioButtonEnabledColorCheck() {
        onView(withId(R.id.image_view_radio_button_outer_ring)).check(matches(hasBackground(R.drawable.selector_radio_button_selected_outer_ring)));
    }
}