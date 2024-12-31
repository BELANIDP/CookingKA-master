package com.whirlpool.cooking.ka.test.cucumber.navigation

import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.cooking.ka.R

/**
 * File       : com.whirlpool.cooking.ka.test.cucumber.navigation
 * Brief      : PopupNavigationHelper helper class
 * Author     : GOYALM5
 * Created On : 25/02/2024
 * Details    : This class is having helper function related to popup fragment
 */

class PopupNavigationHelper {
    fun performLeftButtonClick(){
        UiTestingUtils.performClick(R.id.text_button_left)
    }

    fun performSecondButtonClick(){
//        UiTestingUtils.performClick(R.id.button2)
    }

    fun performThirdButtonClick(){
//        UiTestingUtils.performClick(R.id.button3)
    }

    fun performFourthButtonClick(){
//        UiTestingUtils.performClick(R.id.button4)
    }

}