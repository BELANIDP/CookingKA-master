// Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL
package com.whirlpool.cooking.ka.test.cucumber.steps

import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.navigation.setFragmentScenario
import com.whirlpool.hmi.uitesting.KeyboardTestingUtils
import com.whirlpool.hmi.uitesting.UiTestingUtils
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import org.junit.runner.RunWith
import org.junit.runners.JUnit4


// Created by SINGHA80 on 2/14/2024.
@RunWith(JUnit4::class)
class KeyboardSteps {
    @Given("App started with KeyboardTestFragment")
    fun appHasStartedOnTheKeyboardTestFragment() {
//        setFragmentScenario(KeyboardTestFragment::class.java)
    }
    @Then("Keyboard widget view is visible")
    fun keyboardWidgetViewIsVisible() {
//        UiTestingUtils.isViewVisible(R.id.keyboardView)
    }
    @Then("I type a {word}")
    fun iTypeAWord(word: String) {
//        KeyboardTestingUtils.typeWord(R.id.keyboardView, word.uppercase())
//        UiTestingUtils.isTextMatching(R.id.dummyTvTitle, word.substring(0, 1).uppercase() + word.substring(1))
    }

}