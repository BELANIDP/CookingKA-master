package com.whirlpool.cooking.ka.test.cucumber.appearance

import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.cooking.ka.R

class SplashFragmentAppearanceTest {

    fun checkAllViewsVisibility() {
        UiTestingUtils.isViewVisible(R.id.videoViewLogo)
    }
}