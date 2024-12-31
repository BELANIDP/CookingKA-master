package com.whirlpool.cooking.ka.test.cucumber.appearance

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils.withIndex
import com.whirlpool.hmi.uitesting.UiTestingUtils

class HeaderFragmentAppearanceTest {


    fun ivOvenCavityIsVisible() {
        onView(withIndex(withId(R.id.ivOvenCavity),1)).check(matches(isDisplayed()))
    }

    fun ivLeftIconSingleHeaderIsVisible() {
        onView(withIndex(withId(R.id.ivLeftIcon),0)).check(matches(isDisplayed()))
    }
    fun ivLeftIconDoubleHeaderIsVisible() {
        onView(withIndex(withId(R.id.ivLeftIcon),1)).check(matches(isDisplayed()))
    }

    fun tvTitleSingleHeaderIsVisible() {
        onView(withIndex(withId(R.id.tvTitle),0)).check(matches(isDisplayed()))
    }
    fun tvTitleDoubleHeaderIsVisible() {
        onView(withIndex(withId(R.id.tvTitle),1)).check(matches(isDisplayed()))
    }

    fun ivInfoIsVisible() {
        onView(withIndex(withId(R.id.ivInfo),1)).check(matches(isDisplayed()))
    }

    fun ivRightIconSingleHeaderIsVisible() {
        onView(withIndex(withId(R.id.ivRightIcon),0)).check(matches(isDisplayed()))
    }
    fun ivRightIconDoubleHeaderIsVisible() {
        onView(withIndex(withId(R.id.ivRightIcon),1)).check(matches(isDisplayed()))
    }

    fun ivOvenCavityIsViewClickable () {
        onView(withIndex(withId(R.id.ivOvenCavity),1)).perform(click())
    }

    fun ivLeftIconSingleHeaderIsViewClickable () {
        onView(withIndex(withId(R.id.ivLeftIcon), 0)).perform(click())
    }
    fun ivLeftIconDoubleHeaderIsViewClickable () {
        onView(withIndex(withId(R.id.ivLeftIcon), 1)).perform(click())
    }

    fun ivInfoIsViewClickable () {
        onView(withIndex(withId(R.id.ivInfo),1)).perform(click())
    }

    fun ivRightIconSingleHeaderIsViewClickable () {
        onView(withIndex(withId(R.id.ivRightIcon),0)).perform(click())
    }
    fun ivRightIconDoubleHeaderIsViewClickable () {
        onView(withIndex(withId(R.id.ivRightIcon),1)).perform(click())
    }

    fun ivStatusIcon1IsVisible(){
        UiTestingUtils.isViewVisible(R.id.ivStatusIcon1)
    }
    fun ivStatusIcon2IsVisible(){
        UiTestingUtils.isViewVisible(R.id.ivStatusIcon2)
    }
    fun ivStatusIcon3IsVisible(){
        UiTestingUtils.isViewVisible(R.id.ivStatusIcon3)
    }
    fun ivStatusIcon4IsVisible(){
        UiTestingUtils.isViewVisible(R.id.ivStatusIcon4)
    }
    fun ivHorizontalLineIsVisible(){
        UiTestingUtils.isViewVisible(R.id.ivHorizontalLine)
    }
    fun clockTextViewIsVisible(){
        UiTestingUtils.isViewVisible(R.id.clockTextView)
    }
    fun checkAllViewsVisibility(){
        ivLeftIconSingleHeaderIsVisible()
        ivLeftIconDoubleHeaderIsVisible()
        ivRightIconDoubleHeaderIsVisible()
        ivRightIconSingleHeaderIsVisible()
        ivInfoIsVisible()
        ivOvenCavityIsVisible()
        ivStatusIcon2IsVisible()
        ivStatusIcon3IsVisible()
        ivStatusIcon4IsVisible()
        ivHorizontalLineIsVisible()
        clockTextViewIsVisible()
        tvTitleDoubleHeaderIsVisible()
        tvTitleSingleHeaderIsVisible()
    }
}