/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package com.whirlpool.cooking.ka.test.cucumber.appearance

import android.graphics.Color
import android.presenter.customviews.radiobutton.RadioButton
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.*
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.uitesting.UiTestingUtils
import org.hamcrest.Matcher

class ListItemAppearanceTest {

    fun recyclerViewListIsVisible() {
        UiTestingUtils.isViewVisible(R.id.recycler_view_list)
    }

    fun isScrollingDownWardsWorks(viewId: Int): Boolean {
        var isScrollingDownwards = false
        onView(withId(viewId)).perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Check if scrolling downwards works"
                }

                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(ScrollView::class.java)
                }

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        val initialScrollY = view.scrollY
                        view.post {
                            view.fullScroll(View.FOCUS_DOWN)
                        }
                        val finalScrollY = view.scrollY
                        isScrollingDownwards = finalScrollY > initialScrollY
                    }
                }
            }
        )
        return isScrollingDownwards
    }

    fun isScrollingUpWardWorks(viewId: Int): Boolean {
        var isScrollingUpwards = false
        onView(withId(viewId)).perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Check if scrolling upwards works"
                }

                override fun getConstraints(): Matcher<View> {
                    return isAssignableFrom(ScrollView::class.java)
                }

                override fun perform(uiController: UiController?, view: View?) {
                    if (view is ScrollView) {
                        val initialScrollY = view.scrollY
                        view.post {
                            view.fullScroll(View.FOCUS_UP)
                        }
                        val finalScrollY = view.scrollY
                        isScrollingUpwards = finalScrollY < initialScrollY
                    }
                }
            }
        )
        return isScrollingUpwards
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
            if (view is RecyclerView) {
                val recyclerView = view as RecyclerView
                val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                val titleTextView = itemView?.findViewById<TextView>(childId)
                isTextViewVisible = titleTextView?.visibility == visibility
            }
        }

        if (isHidden){
            return !isTextViewVisible
        }
        return isTextViewVisible
    }

    fun isRadioButtonVisibleInItem(viewId: Int,childId:Int, isHidden:Boolean, position: Int): Boolean {
        var visibility = View.VISIBLE
        if (isHidden){
            visibility =  View.GONE
        }
        var isVisible = false
        onView(withId(viewId)).check { view, _ ->
            if (view is RecyclerView) {
                val recyclerView = view as RecyclerView
                val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                val radioButton = itemView?.findViewById<RadioButton>(childId)
                isVisible = radioButton?.visibility == visibility
            }
        }

        if (isHidden){
            return !isVisible
        }
        return isVisible
    }

    fun isImageViewVisibleInItem(viewId: Int,childId:Int, isHidden:Boolean, position: Int): Boolean {
        var visibility = View.VISIBLE
        if (isHidden){
            visibility =  View.GONE
        }
        var isVisible = false
        onView(withId(viewId)).check { view, _ ->
            if (view is RecyclerView) {
                val recyclerView = view as RecyclerView
                val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                val imageView = itemView?.findViewById<ImageView>(childId)
                isVisible = imageView?.visibility == visibility
            }
        }

        if (isHidden){
            return !isVisible
        }
        return isVisible
    }

    fun isRadioButtonIsEnabledInItem(viewId: Int,childId:Int, position: Int): Boolean {
        var isEnabled = false
        onView(withId(viewId)).check { view, _ ->
            if (view is RecyclerView) {
                val recyclerView = view
                val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                val radioButton = itemView?.findViewById<RadioButton>(childId)
                isEnabled = radioButton?.isEnabled == true
            }
        }
        return isEnabled
    }

    fun isRadioButtonIsCheckedInItem(viewId: Int,childId:Int, position: Int): Boolean {
        var isChecked = false
        onView(withId(viewId)).check { view, _ ->
            if (view is RecyclerView) {
                val recyclerView = view
                val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                val radioButton = itemView?.findViewById<RadioButton>(childId)
                isChecked = radioButton?.isChecked() == true
            }
        }
        return isChecked
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

    fun isTitleTextColorIsGrey(viewId: Int,childId:Int, position: Int): Boolean {
        var isGreyColor = false
        var expectedColor: Int = 0

        onView(withId(viewId)).check { view, _ ->
            if (view is RecyclerView) {
                val recyclerView = view as RecyclerView
                val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                val titleTextView = itemView?.findViewById<TextView>(childId)
                expectedColor = Color.parseColor("#AAA5A1")
                isGreyColor = titleTextView?.currentTextColor == expectedColor
            }
        }
        return isGreyColor
    }

    fun validateTextViewSize(viewId: Int, childId: Int, position: Int,expectedTextSize:Int):Boolean {
        // Get the actual text size
        var actualTextSize: Int = -1 // Initialize with a default value
        onView(withId(viewId)).check { view, _ ->
            if (view is RecyclerView) {
                val recyclerView = view as RecyclerView
                val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
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
                if (view is RecyclerView) {
                    val recyclerView = view as RecyclerView
                    val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                    val textView = itemView?.findViewById<TextView>(childId)

                    isColorAsExpected = textView?.currentTextColor == expectedColor
                }
            }
            return isColorAsExpected
    }

    fun validateContainerPadding(viewId: Int, position: Int,expectedPaddingLeft:Int,expectedPaddingRight:Int,expectedPaddingTop:Int,expectedPaddingBottom:Int):Boolean {
            var isPaddingAsExpected = false

            onView(withId(viewId)).check { view, _ ->
                if (view is RecyclerView) {
                    val recyclerView = view as RecyclerView
                    val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                    val layout = itemView?.findViewById<ConstraintLayout>(R.id.list_item_main_view)

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

}