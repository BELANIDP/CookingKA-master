/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.espresso_utils.TestingUtils
import com.whirlpool.cooking.ka.test.cucumber.navigation.setFragmentScenario
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.uitesting.UiTestingUtils

class StringTumblerAppearanceTest {


   fun navigateToTumblerTestScreen()
   {
//       setFragmentScenario(TestWidgetFragment::class.java)
   }
    fun navigateToTumblerScreen()
    {
//        setFragmentScenario(StringTumblerFragment::class.java)
    }
   fun performTestWidgetClick()
    {
//        UiTestingUtils.performClick(R.id.btn_test)
    }
    fun isTestWidgetFragmentVisible()
    {
//        UiTestingUtils.isViewVisible(R.id.button_2)
    }

    fun isTestClockFragmentVisible()
    {
//        UiTestingUtils.isViewVisible(R.id.btn_test)
    }
    fun performHorizontalButtonClick()
    {
//        UiTestingUtils.performClick(R.id.button6)
    }

    fun stringTumblerIsVisible() {
        UiTestingUtils.sleep(5000)
        UiTestingUtils.isViewVisible(R.id.tumblerString)
    }

    fun horizontalScrollLeft()
    {
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerString,0)
    }

    fun horizontalScrollRight()
    {
        UiTestingUtils.scrollToRecyclerViewIndex(R.id.tumblerString,10)
    }

    fun checkTumblerHeight(): Boolean {
        val expectedHeight = 139
        var isPaddingAsExpected = false

        onView(withId(R.id.tumblerString)).check { view, _ ->
            if (view is BaseTumbler) {
                val baseTumbler = view as BaseTumbler
                val itemView = baseTumbler.findViewHolderForAdapterPosition(0)?.itemView

                itemView?.let {
                    // Retrieve padding values
                    val containerHeight = itemView.height

                    // Check if all padding values match the expected padding
                    isPaddingAsExpected = containerHeight == expectedHeight
                }
            }
        }
        return isPaddingAsExpected

    }
    fun checkTumblerWidth(): Boolean {
        val expectedWidth = 854
        var isPaddingAsExpected = false

        onView(withId(R.id.tumblerString)).check { view, _ ->
            if (view is BaseTumbler) {
                val baseTumbler = view as BaseTumbler
                val itemView = baseTumbler.findViewHolderForAdapterPosition(0)?.itemView

                itemView?.let {
                    // Retrieve padding values
                    val containerWidth = itemView.width

                    // Check if all padding values match the expected padding
                    isPaddingAsExpected = containerWidth == expectedWidth
                }
            }
        }
        return isPaddingAsExpected
    }


    fun checkIsContainerPaddingProper() {
        val expectedPaddingTop = 0
        val expectedPaddingBottom = 0
        //val expectedPaddingLeft = CookingKACucumberTests.context.resources.getDimension(R.dimen.text_button_background_height).toInt()
        //val expectedPaddingRight = CookingKACucumberTests.context.resources.getDimension(R.dimen.text_button_background_height).toInt()
        val desiredTextSizeSpLeft = 64f
        val desiredTextSizeSpRight = 64f
        val expectedPaddingLeft =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSpLeft)
        val expectedPaddingRight =
            TestingUtils.spToPx(CookingKACucumberTests.context, desiredTextSizeSpRight)
        val isValid = validateContainerPadding(
            viewId = R.id.tumblerString,
            position = 0,
            expectedPaddingTop = expectedPaddingTop,
            expectedPaddingBottom = expectedPaddingBottom,
            expectedPaddingLeft = expectedPaddingLeft,
            expectedPaddingRight = expectedPaddingRight,
        )
        assert(isValid) { "The right text color is as expected" }
    }
    fun checkIsContainerPaddingProperNotProper() {
        val expectedPaddingTop = 0
        val expectedPaddingBottom = 0
        val expectedPaddingLeft = 0
        val expectedPaddingRight = 0
        val isValid = validateContainerPadding(
            viewId = R.id.tumblerString,
            position = 0,
            expectedPaddingTop = expectedPaddingTop,
            expectedPaddingBottom = expectedPaddingBottom,
            expectedPaddingLeft = expectedPaddingLeft,
            expectedPaddingRight = expectedPaddingRight,
        ) ?: false
        assert(!isValid) { "The right text color is not as expected" }
    }


    fun checkIfFirstItemIsExist(viewId: Int): Boolean {
        var isItemExist = false
        onView(withId(viewId)).check { view, _ ->
            if (view is RecyclerView) {
                isItemExist = (view.adapter?.itemCount ?: 0) >= 1
            }
        }
        return isItemExist
    }
    fun isTextViewVisibleInItem(viewId: Int,childId:Int, isHidden:Boolean, position: Int): Boolean {
        var visibility = View.VISIBLE
        if (isHidden){
            visibility =  View.GONE
        }
        var isTextViewVisible = false
        onView(withId(viewId)).check { view, _ ->
            if (view is BaseTumbler) {
                val baseTumbler = view as BaseTumbler
                val itemView = baseTumbler.findViewHolderForAdapterPosition(position)?.itemView
                val titleTextView = itemView?.findViewById<TextView>(childId)
                isTextViewVisible = titleTextView?.visibility == visibility
            }
        }
        if (isHidden){
            return !isTextViewVisible
        }
        return isTextViewVisible
    }
    fun isTitleTextColorIsGrey(viewId: Int,childId:Int, position: Int): Boolean {
        var isGreyColor = false
        var expectedColor: Int = 0

        onView(withId(viewId)).check { view, _ ->
            if (view is BaseTumbler) {
                val baseTumbler = view as BaseTumbler
                val itemView = baseTumbler.findViewHolderForAdapterPosition(position)?.itemView
                val titleTextView = itemView?.findViewById<TextView>(childId)
                expectedColor = Color.parseColor("#AAA5A1")
                isGreyColor = titleTextView?.currentTextColor == expectedColor
            }
        }
        return isGreyColor
    }
    fun validateTextViewSize(viewId: Int, childId: Int, position: Int,expectedTextSize:Int):Boolean {
        // Get the actual text size
        var actualTextSize: Int = 80 // Initialize with a default value
        onView(withId(viewId)).check { view, _ ->
            if (view is BaseTumbler) {
                val baseTumbler = view as BaseTumbler
                val itemView = baseTumbler.findViewHolderForAdapterPosition(position)?.itemView
                val titleTextView = itemView?.findViewById<TextView>(childId)
                // Check if titleTextView is not null
                titleTextView?.let {
                    // Get the actual text size
                    actualTextSize = it.textSize.toInt()
                }
            }
        }
        // Compare the actual text size with the expected text size
        if (actualTextSize != -1) {
            if (expectedTextSize == actualTextSize){
                return true
            }
        } else {
            return false
        }
        return false
    }
    fun validateTextViewColor(viewId: Int, childId: Int, position: Int,expectedColor:Int):Boolean {
            var isColorAsExpected = false

            onView(withId(viewId)).check { view, _ ->
                if (view is BaseTumbler) {
                    val baseTumbler = view as BaseTumbler
                    val itemView = baseTumbler.findViewHolderForAdapterPosition(position)?.itemView
                    val textView = itemView?.findViewById<TextView>(childId)

                    isColorAsExpected = textView?.currentTextColor == expectedColor
                }
            }
            return isColorAsExpected
    }
    fun validateContainerPadding(viewId: Int, position: Int,expectedPaddingLeft:Int,expectedPaddingRight:Int,expectedPaddingTop:Int,expectedPaddingBottom:Int):Boolean {
            var isPaddingAsExpected = false

            onView(withId(viewId)).check { view, _ ->
                if (view is BaseTumbler) {
                    val baseTumbler = view as BaseTumbler
                    val itemView = baseTumbler.findViewHolderForAdapterPosition(position)?.itemView
                    val layout = itemView?.findViewById<ConstraintLayout>(R.id.stringTumblerItemBase)

                    layout?.let {
                        // Retrieve padding values
                        val paddingTop = layout.paddingTop
                        val paddingBottom = layout.paddingBottom
                        val paddingLeft = layout.paddingLeft
                        val paddingRight = layout.paddingRight

                        // Check if all padding values match the expected padding
                        isPaddingAsExpected = paddingTop == expectedPaddingTop &&
                                paddingBottom == expectedPaddingBottom &&
                                paddingLeft == expectedPaddingLeft &&
                                paddingRight == expectedPaddingRight
                    }
                }
            }
            return isPaddingAsExpected
    }


    fun isItemDisabled(viewId: Int, position: Int): Boolean {
        var isDisable = false
        onView(withId(viewId)).check { view, _ ->
            if (view is RecyclerView) {
                val recyclerView = view
                val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                isDisable = itemView?.isEnabled == false
            }
        }
        return isDisable
    }
}