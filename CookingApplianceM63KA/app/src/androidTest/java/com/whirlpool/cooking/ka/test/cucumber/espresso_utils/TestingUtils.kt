package com.whirlpool.cooking.ka.test.cucumber.espresso_utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.presenter.customviews.textButton.TextButton
import android.text.Html
import android.text.InputFilter.LengthFilter
import android.util.TypedValue
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.CoordinatesProvider
import androidx.test.espresso.action.GeneralClickAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.Tap
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.util.HumanReadables
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests
import com.whirlpool.cooking.ka.test.cucumber.base.CookingKACucumberTests.Companion.context
import com.whirlpool.hmi.cookbook.repository.CookBookRepository
import com.whirlpool.hmi.uicomponents.tools.util.Constants
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.uitesting.components.view.GenericViewTest
import com.whirlpool.hmi.utils.ContextProvider.getActivity
import com.whirlpool.hmi.utils.CookingSimConst
import com.whirlpool.hmi.utils.LogHelper
import core.utils.HMILogHelper
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit

object TestingUtils {
    fun swipeFromTopToBottom(): ViewAction {
        return GeneralSwipeAction(
            Swipe.FAST, GeneralLocation.TOP_CENTER, GeneralLocation.BOTTOM_CENTER, Press.FINGER
        )
    }

    fun swipeFromBottomToTop(): ViewAction {
        return GeneralSwipeAction(
            Swipe.FAST, GeneralLocation.BOTTOM_CENTER, GeneralLocation.TOP_CENTER, Press.FINGER
        )
    }

    fun swipeFromLeftToRight(): ViewAction {
        return GeneralSwipeAction(
            Swipe.FAST, GeneralLocation.CENTER_LEFT, GeneralLocation.CENTER_RIGHT, Press.FINGER
        )
    }

    /**
     * Method : clickViewWithText(String text)
     * Parameter : String
     * Description : this Api allows to click the view having the given text
     */
    fun clickViewWithText(text: String?) {
        Espresso.onView(ViewMatchers.withText(text))
            .check(ViewAssertions.matches(isDisplayed())).perform(ViewActions.click())
    }

    /**
     * Method : void checkViewIsDisplayed(int viewId)
     * Parameter : view id
     * Description : this Api allows to check the whether the given view id is displayed in the screen or not
     */
    fun checkViewIsDisplayed(viewId: Int) {
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    /**
     * Method : void checkViewWithTextIsDisplayed(String text)
     * Parameter : string
     * Description : this Api allows to check the view having the given text is displayed in the screen or not
     */
    fun checkViewWithTextIsDisplayed(text: String?) {
        Espresso.onView(ViewMatchers.withText(text))
            .check(ViewAssertions.matches(isDisplayed()))
    }

    /**
     * Method : Matcher<View> withFontSize()
     *
     * @param expectedSize size of the text to be matched
     * Description : this Api allows to check whether text size matches
    </View> */
    fun withFontSize(expectedSize: Float): Matcher<View?> {
        // field to store values
        return object : TypeSafeMatcher<View?>() {
            override fun matchesSafely(target: View?): Boolean {
                // stop executing if target is not textview
                if (target !is TextView) {
                    return false
                }
                // target is a text view so apply casting then retrieve and test the desired value
                return target.textSize == expectedSize
            }

            override fun describeTo(description: Description) {
                description.appendText("with fontSize: ")
                description.appendValue(expectedSize)
            }
        }
    }

    /**
     * Method : Matcher<View> withDrawable(int resourceId)
     * Parameter : expected resource id
     * Description : this Api allows to check the resource id of the image view with respect to the expected id
    </View> */
    fun withDrawable(resourceId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View): Boolean {
                if (item !is ImageView) {
                    return false
                }
                val imageView = item
                if (resourceId < 0) {
                    return imageView.drawable == null
                }
                val resources = item.getContext().resources
                val drawable =
                    ResourcesCompat.getDrawable(resources, resourceId, null) ?: return false
                val actualBitmap = getBitmap(imageView.drawable)
                val targetBitmap = getBitmap(drawable)
                return actualBitmap.sameAs(targetBitmap)
            }

            override fun describeTo(description: Description) {
                description.appendText("With Drawable id: ")
                description.appendValue(resourceId)
            }
        }
    }

    fun noDrawable(): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun matchesSafely(item: View): Boolean {
                return item.background == null
            }

            override fun describeTo(description: Description) {
                description.appendText("No Drawable")
            }
        }
    }

    /**
     * Method to test the background color of a view
     *
     * @param expectedResourceId : Expected Color Resource Id (R.color.solid_white)
     * Eg: onView(withId(R.id.button)).check(matches(matchesBackgroundColor(R
     * .color.solid_white)))
     */
    fun matchesBackgroundColor(expectedResourceId: Int): Matcher<View?> {
        return object : BoundedMatcher<View?, View>(View::class.java) {
            var actualColor = 0
            var expectedColor = 0
            var message: String? = null
            override fun matchesSafely(item: View): Boolean {
                if (item.background == null) {
                    message = item.id.toString() + " does not have a background"
                    return false
                }
                val resources = item.context.resources
                expectedColor = ResourcesCompat.getColor(resources, expectedResourceId, null)
                try {
                    actualColor = (item.background as ColorDrawable).color
                } catch (e: Exception) {/*actualColor =
                            ((GradientDrawable) item.getBackground()).getColor().getDefaultColor();*/
                }
                return actualColor == expectedColor
            }

            override fun describeTo(description: Description) {}
        }
    }

    /**
     * Method : Bitmap getBitmap(Drawable drawable)
     * Parameter : Drawable from which bitmap to be created
     * Description : this Api allows to Create a Bitmap from the given drawable.
     */
    private fun getBitmap(drawable: Drawable): Bitmap {
        return Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
    }

    /**
     * Method : Matcher<View> checkCount(int recyclerViewId, int expectedCount)
     * Parameter :
     * Description : this Api allows to check whether the recycler view has the expected number of items
    </View> */
    fun checkCount(recyclerViewId: Int, expectedCount: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            var resources: Resources? = null
            var childView: View? = null
            override fun describeTo(description: Description) {
                var idDescription = Integer.toString(recyclerViewId)
                if (resources != null) {
                    idDescription = try {
                        resources!!.getResourceName(recyclerViewId)
                    } catch (var4: NotFoundException) {
                        String.format(
                            "%s (resource name not found)", recyclerViewId
                        )
                    }
                }
                description.appendText("with id: $idDescription")
            }

            public override fun matchesSafely(view: View): Boolean {
                val recyclerView = view.rootView.findViewById<RecyclerView>(recyclerViewId)
                resources = view.resources
                if (childView == null) {
                    if (recyclerView != null && recyclerView.id == recyclerViewId) {
                        //do nothing
                    } else {
                        return false
                    }
                }
                return Objects.requireNonNull(recyclerView!!.adapter).itemCount == expectedCount
            }
        }
    }

    val isButtonTextNotItalic: Matcher<View>
        /**
         * Method : Matcher<View> isButtonTextNotItalic()
         * Parameter :
         * Description : this Api allows to check whether button text is italic or not
        </View> */
        get() = object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {}
            override fun matchesSafely(item: View): Boolean {
                val button = item as Button
                return !button.typeface.isItalic
            }
        }

    /**
     * Method : Matcher<View> textProperties(float fontSize, TextViewPropertiesMatcher.TextProperties textProperties)
     * Parameter : font size of text view
     * Description : this Api allows to check font size of text view
     * example :  onView(withId(R.id.TextView)).check(matches(withFontSize(36)));
    </View> */
    fun textProperties(
        fontSize: Float, textProperties: TextViewPropertiesMatcher.TextProperties
    ): Matcher<View?> {
        return TextViewPropertiesMatcher(fontSize, textProperties)
    }

    /**
     * Method : Matcher<View> textProperties(Typeface typeFace, TextViewPropertiesMatcher.TextProperties textProperties) {
     * Parameter : font size of text view
     * Description : this Api allows to check font size of text view
     * example :  onView(withId(R.id.TextView)).check(matches(withFontSize(36)));
    </View> */
    fun textProperties(
        typeFace: Typeface?, textProperties: TextViewPropertiesMatcher.TextProperties
    ): Matcher<View?> {
        return TextViewPropertiesMatcher(typeFace, textProperties)
    }

    /**
     * Method : Matcher<View> textProperties(int gravity typeFace, TextViewPropertiesMatcher
     * .TextProperties textProperties) {
     * example :  onView(withId(R.id.TextView)).check(matches(textProperties(Gravity.CENTER,
     * GRAVITY)))
    </View> */
    fun textProperties(
        gravity: Int, textProperties: TextViewPropertiesMatcher.TextProperties
    ): Matcher<View?> {
        return TextViewPropertiesMatcher(gravity, textProperties)
    }

    /**
     * Method: String getText(final Matcher<View> matcher)
     * Description: This API allows to get text from TextView(or any view)
     *
     * @param matcher - from where we want to read the text
     * @return Text on the string
    </View> */
    fun getText(matcher: Matcher<View?>?): String? {
        val textString = arrayOf<String?>(null)
        Espresso.onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController, view: View) {
                val tv = view as TextView //Save, because of check in getConstraints()
                textString[0] = tv.text.toString()
            }
        })
        return textString[0]
    }

    /**
     * Method : Matcher<View> withRecyclerView(final int recyclerViewId, int position)
     * Parameter : Recycler view ID and Position
     * Description : This API allows to get the particular object or an item from recycler view
    </View> */
    fun withRecyclerView(recyclerViewId: Int, position: Int): Matcher<View> {
        val recyclerViewMatcher = RecyclerViewMatcher(recyclerViewId)
        return recyclerViewMatcher.atPositionOnView(position, -1)
    }

    /**
     * Method to test recycler view child at specified position
     *
     * @param parentMatcher parent recycler view
     * @param position      position of the child views in recycler
     * @return child view at specified position in recycler view
     */
    fun childAtPosition(
        parentMatcher: Matcher<View?>, position: Int
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent) && view == parent.getChildAt(
                    position
                )
            }
        }
    }

    /**
     * Method : Matcher<View> withTextColor()
     *
     * @param expectedId id of the color to be matched
     * Description : this Api allows to check whether text color matches
    </View> */
    fun withTextColor(expectedId: Int): Matcher<View?> {
        return object : BoundedMatcher<View?, TextView>(TextView::class.java) {
            override fun matchesSafely(textView: TextView): Boolean {
                return expectedId == textView.currentTextColor
            }

            override fun describeTo(description: Description) {
                description.appendText("with text color: ")
                description.appendValue(expectedId)
            }
        }
    }

    /**
     * Matcher Method to check whether the CustomSeekBar progress value is matching to the given
     * prgress value
     *
     * @param expectedProgress expected progress value index
     */
    fun progressBarWithProgress(expectedProgress: Int): Matcher<View?> {
        return object : BoundedMatcher<View?, ProgressBar>(ProgressBar::class.java) {
            override fun describeTo(description: Description) {}
            public override fun matchesSafely(seekBar: ProgressBar): Boolean {
                return seekBar.progress == expectedProgress
            }
        }
    }/*
     * Method : Action Method to seek the Progress value of the CustomSeekBar
     *
     * @param progress - Progress value index need to set in the CustomSeekBar
     **/
    //    public static ViewAction setProgress(final int progress) {
    //        return new ViewAction() {
    //            @Override
    //            public void perform(UiController uiController, View view) {
    //                CustomSeekBar seekBar = (CustomSeekBar) view;
    //                seekBar.setProgress(progress);
    //            }
    //
    //            @Override
    //            public String getDescription() {
    //                return "Set a progress on a SeekBar";
    //            }
    //
    //            @Override
    //            public Matcher<View> getConstraints() {
    //                return ViewMatchers.isAssignableFrom(CustomSeekBar.class);
    //            }
    //        };
    //    }
    /*
     * Matcher Method to check whether the CustomSeekBar progress value is matching to the given
     * prgress value
     *
     * @param expectedProgress expected progress value index
     */
    //    public static Matcher<View> customSeekBarWithProgress(final int expectedProgress) {
    //        return new BoundedMatcher<View, CustomSeekBar>(CustomSeekBar.class) {
    //            @Override
    //            public void describeTo(Description description) {
    //            }
    //
    //            @Override
    //            public boolean matchesSafely(CustomSeekBar seekBar) {
    //                return seekBar.getProgress() == expectedProgress;
    //            }
    //        };
    //    }
    /**
     * Alternative to ScrollTo() for NestedScrollView.
     *
     * @return viewAction to scroll.
     */
    fun nestedScrollTo(): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(
                    ViewMatchers.isDescendantOfA(ViewMatchers.isAssignableFrom(NestedScrollView::class.java)),
                    ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                )
            }

            override fun getDescription(): String {
                return "View is not NestedScrollView"
            }

            override fun perform(uiController: UiController, view: View) {
                try {
                    val nestedScrollView = findFirstParentLayoutOfClass(
                        view, NestedScrollView::class.java
                    ) as NestedScrollView?
                    if (nestedScrollView != null) {
                        nestedScrollView.scrollTo(0, view.top)
                    } else {
                        throw Exception("Unable to find NestedScrollView parent.")
                    }
                } catch (e: Exception) {
                    throw PerformException.Builder().withActionDescription(this.description)
                        .withViewDescription(HumanReadables.describe(view)).withCause(e).build()
                }
                uiController.loopMainThreadUntilIdle()
            }
        }
    }

    private fun findFirstParentLayoutOfClass(view: View, parentClass: Class<out View>): View {
        var parent: ViewParent = FrameLayout(view.context)
        var incrementView: ViewParent? = null
        var i = 0
        while (parent != null && parent.javaClass != parentClass) {
            parent = if (i == 0) {
                findParent(view)
            } else {
                findParent(incrementView)
            }
            incrementView = parent
            i++
        }
        return parent as View
    }

    private fun findParent(view: View): ViewParent {
        return view.parent
    }

    private fun findParent(view: ViewParent?): ViewParent {
        return view!!.parent
    }

    /**
     * Click at (x,y) co-ordinate of the view.
     */
    fun clickXY(x: Int, y: Int): ViewAction {
        return GeneralClickAction(
            Tap.SINGLE, CoordinatesProvider { view: View ->
                val screenPos = IntArray(2)
                view.getLocationOnScreen(screenPos)
                val screenX = (screenPos[0] + x).toFloat()
                val screenY = (screenPos[1] + y).toFloat()
                floatArrayOf(screenX, screenY)
            }, Press.FINGER, InputDevice.SOURCE_TOUCHSCREEN, MotionEvent.BUTTON_PRIMARY
        )
    }

    /**
     * ------------ A30 screen coordinates---------
     * 1- (87,58)
     * 2- (247,29)
     * 3-(400,50)
     * 4-(573,68)
     * 5-(708,52)
     * 6-(73,173)
     * 7-(221,171)
     * 9-(398,190)
     * 0-(724,160)
     */
    fun touchDownAndUp(x: Float, y: Float): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDisplayed()
            }

            override fun getDescription(): String {
                return "Send touch events."
            }

            override fun perform(uiController: UiController, view: View) {
                // Get view absolute position
                val location = IntArray(2)
                view.getLocationOnScreen(location)

                // Offset coordinates by view position
                val coordinates = floatArrayOf(x + location[0], y + location[1])
                val precision = floatArrayOf(1f, 1f)

                // Send down event, pause, and send up
                val down = MotionEvents.sendDown(uiController, coordinates, precision).down
                uiController.loopMainThreadForAtLeast(200)
                MotionEvents.sendUp(uiController, down, coordinates)
            }
        }
    }/*
     * To Perform the touch events in the Keyboard View
     * @param x x position of buttons
     * @param y y position of buttons
     *
     * X and Y Position of Numeric Keys
     *
     * Number 1 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(180, 40));
     * Number 2 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(360, 40));
     * Number 3 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(540, 40));
     * Number 4 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(720, 40));
     * Number 5 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(900, 40));
     * Number 6 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(180, 100));
     * Number 7 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(360, 100));
     * Number 8 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(540, 100));
     * Number 9 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(720, 100));
     * Number 0 : onView(withId(R.id.keyboardview)).perform(touchDownAndUp(900, 100));
     */

    /**
     * Utility method that will allow for click in specific sections of a view.
     *
     *
     * This method is used specifically for the number pad, but could be used for other views if there was a need.
     *
     *
     * Example:
     *
     * <pre>
     * `onView(withId(R.id.keyboard_view_number_pad))
     * .perform(Ui`TestingUtils.clickRowAndColumn(2, 2, 4, 3));
    ` *
    </pre> *
     *
     * @param row          index for the row to be click
     * @param column       index for the column to be click
     * @param totalRows    total amount of rows used to determine where to click
     * @param totalColumns total amount of columns used to determine where to click
     * @return ViewAction to perform
     */
    fun clickRowAndColumnAction(
        row: Int, column: Int, totalRows: Int, totalColumns: Int
    ): ViewAction {
        return GeneralClickAction(
            Tap.SINGLE, CoordinatesProvider { view: View ->
                val width = view.measuredWidth
                val cellWidth = width / totalColumns
                val height = view.measuredHeight
                val cellHeight = height / totalRows
                val x = cellWidth * column // - (cellWidth / 4);
                val y = cellHeight * row - cellHeight / 2
                val screenPos = IntArray(2)
                view.getLocationOnScreen(screenPos)
                val screenX = (screenPos[0] + x).toFloat()
                val screenY = (screenPos[1] + y).toFloat()
                floatArrayOf(screenX, screenY)
            }, Press.FINGER, InputDevice.SOURCE_MOUSE, MotionEvent.BUTTON_PRIMARY
        )
    }

    /**
     * Method to enter Numpad buttons for a given string
     *
     * @param c - String to be entered
     */
    fun enterNumberKey(c: String) {
        val row: Int
        val column: Int
        val numberValue = c.toInt()
        if (numberValue == 0 || numberValue > 5) {
            row = 2
            column = if (numberValue == 0) {
                5
            } else {
                numberValue % 5
            }
        } else {
            row = 1
            column = numberValue
        }
        //TODO: Check for this ID should it be as param?//
         onView(withId(R.id.keyboardview)).perform(clickRowAndColumnAction(row, column, 2, 6));
    }

    fun enterNumberStr(str: String) {
        for (i in 0 until str.length) {
            enterNumberKey(str[i].toString())
        }
    }

    fun convertTimeToHoursAndMinutes(timeInSeconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(timeInSeconds).toInt()
        val minutes =
            (TimeUnit.SECONDS.toMinutes(timeInSeconds) - TimeUnit.HOURS.toMinutes(hours.toLong())).toInt()
        val seconds = 0
        return String.format(
            Locale.getDefault(), "%02d%02d",
            hours, minutes, seconds
        )
    }

    /**
     * Method to compare xml of the keyboard view
     *
     * @param {xmlId} id of the xml
     * @return true if both xml matches
     *//*
    public static ViewAction compareXml(final int xmlId) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                KeyboardView keyboardView = (KeyboardView) view;
               // assertThat(keyboardView.getKeyboardAlphaReference(),CompareMatcher.isIdenticalTo(xmlId));
            }

            @Override
            public String getDescription() {
                return "Compare xml";
            }

            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(KeyboardView.class);
            }
        };
    }*/
    fun withItemHint(viewId: Int, hintText: String) {
        Espresso.onView(ViewMatchers.withId(viewId))
            .check(ViewAssertions.matches(withHint(hintText)))
    }/* */

    /**
     * Matcher Method to check whether the Edittext hint value is matching to the given
     * hint value
     *
     * @param expectedHint expected hint value
     */
    private fun withHint(expectedHint: String): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            public override fun matchesSafely(view: View?): Boolean {
                if (view !is EditText) {
                    return false
                }
                val hint = view.hint.toString()
                return expectedHint == hint
            }

            override fun describeTo(description: Description) {}
        }
    }

    /**
     * Tests a view to see if it cannot be clicked
     *
     * @param viewId int - view id
     */
    fun checkEditTextMaxLength(viewId: Int, text: String?, maxLength: Int) {
        Espresso.onView(ViewMatchers.withId(viewId))
            .perform(ViewActions.typeText(text), ViewActions.closeSoftKeyboard())
            .check(ViewAssertions.matches(checkMaxLength(maxLength)))
    }

    /**
     * check the max length in an editText
     *
     * @param length - char max length
     * @return
     */
    private fun checkMaxLength(length: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            @SuppressLint("UseSdkSuppress")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun matchesSafely(item: View): Boolean {
                val filters = (item as TextView).filters
                val lengthFilter = filters[0] as LengthFilter
                return lengthFilter.max == length
            }

            override fun describeTo(description: Description) {
                description.appendText("checkMaxLength")
            }
        }
    }

    /**
     * close the keyboard
     */
    fun closeKeyboard() {
        ViewActions.closeSoftKeyboard()
    }

    /**
     * methos used for enter the text into the respective view provided by the view id
     *
     * @param viewId - View id
     * @param text   - Text
     */
    fun enterText(viewId: Int, text: String?) {
        Espresso.onView(ViewMatchers.withId(viewId)).perform(ViewActions.typeText(text))
    }

    /**
     * Tests the [TextView] property of the TextView
     *
     * @param viewInteraction [ViewInteraction] example would include "onView(withView(R.id.some_text_view))"
     * @param textStyle       float representing the property of text - size,color,gravity and font
     * @param textProperties  TextProperties representing the property of text - TEXTSIZE,TEXTSIZE_NOT_MATCHED,GRAVITY etc
     */
    fun checkMatchesTextProperties(
        viewInteraction: ViewInteraction,
        textStyle: Float,
        textProperties: TextViewPropertiesMatcher.TextProperties
    ) {
        if (textStyle > Constants.NOT_IMPLEMENTED) {
            viewInteraction.check(ViewAssertions.matches(textProperties(textStyle, textProperties)))
        } else {
            LogHelper.Loge("textSize was NOT provided")
        }
    }

    class ViewSizeMatcher
    /**
     * Method to verify the width and height of a view
     *
     * @param expectedWith   : width of the view
     * @param expectedHeight : height of the view
     * example :  onView(withId(R.id.TextView)).check(matches(new ViewSizeMatcher(297, 78)))
     */(private val expectedWith: Int, private val expectedHeight: Int) :
        TypeSafeMatcher<View>(View::class.java) {
        override fun matchesSafely(target: View): Boolean {
            val targetWidth = target.width
            val targetHeight = target.height
            return if (expectedWith == -1) { //When width is wrap content, pass expected width is -1, so
                // it will check only height
                targetHeight == expectedHeight
            } else {
                targetWidth == expectedWith && targetHeight == expectedHeight
            }
        }

        override fun describeTo(description: Description) {
            description.appendText("with SizeMatcher: ")
            description.appendValue(expectedWith.toString() + "x" + expectedHeight)
        }
    }

    fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?>? {
        return object : TypeSafeMatcher<View?>() {
            var currentIndex = 0
            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View?): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }


    fun spToPx(context: Context, sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    /**
     * Method to locate a view in case where the targeted view is attached/nested multiple times with other views.
     * for ex. views nested in included layouts, views in recyclerview based items
     *
     * @param matcher viewmatcher specified by withId for the target view
     * @param index index or position of the targeted view
     */
    fun withIndexHash(matcher: Matcher<View?>, index: Int): Matcher<View?> {
        return object : TypeSafeMatcher<View?>() {
            var currentIndex = 0
            var viewObjHash = 0

            @SuppressLint("DefaultLocale")
            override fun describeTo(description: Description) {
                description.appendText(String.format("with index: %d ", index))
                matcher.describeTo(description)
            }

            override fun matchesSafely(item: View?): Boolean {
                if (matcher.matches(item) && currentIndex++ == index) {
                    viewObjHash = item.hashCode()
                }
                return item.hashCode() == viewObjHash
            }
        }
    }

    fun isNotDisplayed(): Matcher<View> {
        return not(isDisplayed())
    }

    /**
     * Matcher to check if the AppCompatTextView has the expected line height.
     *
     * @param expectedLineHeight The expected line height in pixels.
     * @return Matcher for AppCompatTextView.
     */
    fun withLineHeight(expectedLineHeight: Int): Matcher<Any> {
        return object : BoundedMatcher<Any, AppCompatTextView>(AppCompatTextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with line height: $expectedLineHeight")
            }

            override fun matchesSafely(item: AppCompatTextView?): Boolean {
                val lineHeight = item?.lineHeight ?: return false
                return lineHeight == expectedLineHeight
            }
        }
    }

    /**
     * Matcher to check if the AppCompatTextView has the expected line spacing extra.
     *
     * @param expectedLineSpacing The expected line spacing extra in pixels.
     * @return Matcher for AppCompatTextView.
     */
    fun withLineSpacingExtra(expectedLineSpacing: Int): Matcher<Any> {
        return object : BoundedMatcher<Any, AppCompatTextView>(AppCompatTextView::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with line spacing extra: $expectedLineSpacing")
            }

            override fun matchesSafely(item: AppCompatTextView?): Boolean {
                val lineHeight = item?.lineSpacingExtra ?: return false
                return lineHeight == expectedLineSpacing.toFloat()
            }
        }
    }

    /**
     * Matcher to check if the View has the expected height and width.
     *
     * @param expectedHeight The expected height in pixels.
     * @param expectedWidth  The expected width in pixels.
     * @return Matcher for View.
     */
    fun withViewHeightAndWidth(expectedHeight: Int, expectedWidth: Int): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with height: $expectedHeight pixels")
            }

            override fun matchesSafely(item: View?): Boolean {
                val actualHeight = item?.measuredHeight ?: return false
                val actualWidth = item.measuredWidth
                return (actualHeight == expectedHeight && actualWidth == expectedWidth)
            }
        }
    }

    /**
     * Matcher to check if the View has the expected height.
     *
     * @param expectedHeight The expected height in pixels.
     * @return Matcher for View.
     */
    fun withViewHeight(expectedHeight: Int): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with height: $expectedHeight pixels")
            }

            override fun matchesSafely(item: View?): Boolean {
                val actualHeight = item?.measuredHeight ?: return false
                return (actualHeight == expectedHeight)
            }
        }
    }

    /**
     * Matcher to check if the View has the expected width.
     *
     * @param expectedWidth The expected width in pixels.
     * @return Matcher for View.
     */
    fun withViewWidth(expectedWidth: Int): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with height: $expectedWidth pixels")
            }

            override fun matchesSafely(item: View?): Boolean {
                val actualHeight = item?.measuredWidth ?: return false
                return (actualHeight == expectedWidth)
            }
        }
    }

    /**
     * Method used to verify text view visible in recyclerview
     */
    fun isTextViewVisibleInItem(
        viewId: Int,
        childId: Int,
        isHidden: Boolean,
        position: Int
    ): Boolean {
        var visibility = View.VISIBLE
        if (isHidden) {
            visibility = View.GONE
        }
        var isTextViewVisible = false
        Espresso.onView(ViewMatchers.withId(viewId)).check { view, _ ->
            if (view is RecyclerView) {
                val recyclerView = view as RecyclerView
                val itemView = recyclerView.findViewHolderForAdapterPosition(position)?.itemView
                val titleTextView = itemView?.findViewById<TextView>(childId)
                isTextViewVisible = titleTextView?.visibility == visibility
            }
        }

        if (isHidden) {
            return !isTextViewVisible
        }
        return isTextViewVisible
    }

    /**
     * Method used to verify scrolling down behaviour
     */

    fun isScrollingDown(viewId: Int): Boolean {
        var isScrollingDownwards = false
        Espresso.onView(ViewMatchers.withId(viewId)).perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Check if scrolling downwards works"
                }

                override fun getConstraints(): Matcher<View> {
                    return ViewMatchers.isAssignableFrom(ScrollView::class.java)
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

    /**
     * Method used to verify scrolling up behaviour
     */
    fun isScrollingUp(viewId: Int): Boolean {
        var isScrollingUpwards = false
        Espresso.onView(ViewMatchers.withId(viewId)).perform(
            object : ViewAction {
                override fun getDescription(): String {
                    return "Check if scrolling upwards works"
                }

                override fun getConstraints(): Matcher<View> {
                    return ViewMatchers.isAssignableFrom(ScrollView::class.java)
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

    fun checkTextColorValidation(viewId: Int, color: String) {
        Espresso.onView(ViewMatchers.withId(viewId)).check(
            ViewAssertions.matches(
                withTextColor(Color.parseColor(color))
            )
        )
    }

    /**
     * multiple view present in the layout that time this method helpful for performing text matching
     * Ex:TestingUtils.isTextMatchingWithMultipleView(Espresso.onView(TestingUtils.withIndex(withId(R.id.tvTitle), 0)), text)
     */
    fun isTextMatchingWithMultipleView(viewInteraction: ViewInteraction, text: String?) {
        viewInteraction.check(ViewAssertions.matches(ViewMatchers.withText(text)))
    }

    fun clickChildViewWithId(id: Int): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController?, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
            }
        }
    }

    /**
     * multiple view conflict with click listener so use below method to force apply click event
     */
    fun forceClick(): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Set view visibility to VISIBLE"
            }

            override fun getConstraints(): Matcher<View> {
                return Matchers.allOf(ViewMatchers.isClickable(), ViewMatchers.isEnabled())
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.performClick()
                uiController?.loopMainThreadUntilIdle()
            }
        }
    }


    /**
     * Check properties of TextView in recycler view
     *
     * @param recyclerViewId - recycler view id,
     *        position - position of item (textview) ,
     *        textViewId - text view id,
     *        visibility - require visibility of text,
     *        text - require text of textview,
     *        width = require width of text,
     *        height = require height of text,
     *        fontfamily - require fontfamily of text,
     *        weight - require wight of text,
     *        size - require size of text,
     *        lineheight - require lineheight of text,
     *        gravity - require gravity of text,
     *        color - require color of text
     */
    fun checkAllPropertiesOfTextOfRecyclerViewItem(
        recyclerViewId: Int,
        position: String,
        itemId: Int,
        visible: String,
        text: String,
        width: String,
        height: String,
        fontFamily: String,
        weight: String,
        size: String,
        lineHeight: String,
        gravity: String,
        color: String
    ) {
        Espresso.onView(
            UiTestingUtils.matchRecyclerViewItem(
                recyclerViewId,
                convertStringToType(position, Int::class.java),
                itemId
            )
        )
            .check { view, _ -> checkTextVisible(visible, view) }
            .check { view, _ -> checkText(text, view) }
            .check { view, _ -> checkWidthOfText(width, view) }
            .check { view, _ -> checkHeightOfText(height, view) }
            .check { view, _ -> checkFontFamilyOfText(fontFamily, view) }
            .check { view, _ -> checkWeightOfText(weight, view) }
            .check { view, _ -> checkTextSize(size, view) }
            .check { view, _ -> checkLineHeightOfText(lineHeight, view) }
            .check { view, _ -> checkGravityOfText(gravity, view) }
            .check { view, _ -> checkColorOfText(color, view) }
    }

    /**
     * Check properties of Toggle Button in recycler view
     *
     * @param recyclerViewId - recycler view id,
     *        position - position of item  ,
     *        visibility - require visibility of text Toggle Button,
     *        width = require width of Toggle Button,
     *        height = require height of Toggle Button,
     *        on_off = require ON/OFF state of Toggle Button,
     *        enable_disable = require ENABLE/DISABLE state of Toggle Button
     */
    fun checkAllPropertiesOfToggleOfRecyclerViewItem(
        recyclerViewId: Int,
        position: String,
        itemId: Int,
        visible: String,
        width: String,
        height: String,
        on_off: String,
        enable_disable: String
    ) {
        Espresso.onView(
            UiTestingUtils.matchRecyclerViewItem(
                recyclerViewId,
                convertStringToType(position, Int::class.java),
                itemId
            )
        )
            .check { view, _ -> checkToggleButtonVisible(visible, view) }
            .check { view, _ -> checkWidthOfToggleButton(width, view) }
            .check { view, _ -> checkHeightOfToggleButton(height, view) }
            .check { view, _ -> checkToggleButtonOnOff(on_off, view) }
            .check { view, _ -> checkToggleButtonEnableDisable(enable_disable, view) }
    }

    class ScrollToBottomAction : ViewAction {
        override fun getDescription(): String {
            return "scroll RecyclerView to bottom"
        }

        override fun getConstraints(): Matcher<View> {
            return allOf<View>(isAssignableFrom(RecyclerView::class.java), isDisplayed())
        }

        override fun perform(uiController: UiController?, view: View?) {
            val recyclerView = view as RecyclerView
            val itemCount = recyclerView.adapter?.itemCount
            val position = itemCount?.minus(1) ?: 0
//            recyclerView.scrollToPosition(position)
            UiTestingUtils.sleep(2500)
            recyclerView.smoothScrollToPosition(position)
            uiController?.loopMainThreadUntilIdle()
        }
    }

    fun withRecyclerViewScrollToPosition(position: Int): ViewAction {

        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return allOf(isAssignableFrom(RecyclerView::class.java), isDisplayed())
            }

            override fun getDescription(): String {
                return "scroll RecyclerView to bottom"
            }

            override fun perform(uiController: UiController?, view: View) {
                var scrollPosition = 0
                val recyclerView = view as RecyclerView
                val itemCount = recyclerView.adapter?.itemCount
                scrollPosition = if (position <= 0) {
                    0
                } else if (itemCount != null && position > itemCount) {
                    itemCount.minus(1) ?: 0
                } else {
                    position
                }
                // recyclerView.scrollToPosition(position)
                HMILogHelper.Logd("TEST_", "scrollPosition-->$scrollPosition")
                recyclerView.layoutManager?.scrollToPosition(scrollPosition)
                uiController?.loopMainThreadUntilIdle()
            }
        }
    }

    /*
    *  Scroll to target text in recyclerview and click
    * */
    fun withRecyclerViewScrollToTargetTextAndClick(recyclerViewId: Int, targetText: String) {
        onView(withId(recyclerViewId)).perform(
            scrollTo<RecyclerView.ViewHolder>(
                hasDescendant(withText(targetText))
            )
        )
        UiTestingUtils.sleep(1500)
        onView(withId(recyclerViewId))
            .perform(
                actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(withText(targetText)), click()
                )
            )
    }

    fun funWithGridViewScrollToTargetTextAndClick(targetText: String) {
        getActivity()?.findViewById<RecyclerView>(R.id.recycler_view_grid_list)
            ?.let { recyclerView ->
                recyclerView.adapter?.let { adapter ->
                    for (i in 0 until adapter.itemCount) {
                        recyclerView.findViewHolderForAdapterPosition(i)?.let { viewHolder ->
                            viewHolder.itemView.findViewById<TextView>(R.id.text_view_convect_option_name)
                                ?.let { textView ->
                                    if (textView.text.toString() == targetText) {
                                        recyclerView.post {
                                            recyclerView.scrollToPosition(i)
                                            viewHolder.itemView.performClick()
                                        }
                                        return
                                    }
                                }
                        }
                    }
                }
            }
    }

    /**
     * Check the HTML tags strings
     */
    fun withHtmlText(expectedText: String): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("with text: ").appendValue(expectedText)
            }

            override fun matchesSafely(textView: TextView): Boolean {
                val expectedTextFormatted = Html.fromHtml(expectedText).toString().trim()
                val actualText = textView.text.toString().trim()
                return actualText == expectedTextFormatted
            }
        }
    }

    /**
     * Check the text of Button in popup view
     */
    class textButtonTextAssertion(private val expectedText: String) : ViewAssertion {
        override fun check(view: View?, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            Assert.assertTrue("View is not of type TextButton", view is TextButton)
            val textButton = view as TextButton
            Assert.assertEquals("Text does not match", expectedText, textButton.getTextButtonText())
        }
    }


    /**
     * Allow required permission
     * */
    fun allowGrantedPermission() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val allowButton = uiDevice.findObject(UiSelector().text("While using the app"))
        if (allowButton.exists()) {
            allowButton.click()
        }
        val allowButton5 = uiDevice.findObject(UiSelector().text("While using the app"))
        if (allowButton5.exists()) {
            allowButton5.click()
        }
        UiTestingUtils.sleep(3000)
        val allowButton3 = uiDevice.findObject(UiSelector().text("Deny"))
        if (allowButton3.exists()) {
            allowButton3.click()
        }
        UiTestingUtils.sleep(3000)
        val allowButton2 = uiDevice.findObject(UiSelector().text("Allow"))
        if (allowButton2.exists()) {
            allowButton2.click()
        }
        UiTestingUtils.sleep(3000)
        val allowButton4 = uiDevice.findObject(UiSelector().text("SKIP"))
        if (allowButton4.exists()) {
            allowButton4.click()
        }
    }

    /*
    * Delete all records after assisted
    * */
    fun deleteAllHistoryRecords() {
        CookBookRepository.getInstance().deleteAllHistoryRecords()
        CookBookRepository.getInstance().deleteAllFavoriteRecords()
    }

    /*
    * Disable meat probe
    * */
    fun disableMeatProbe() {
        CookingSimConst.simulateMeatProbeDisconnectEvent(CookingKACucumberTests.mainActivity, true)
        CookingSimConst.simulateMeatProbeDisconnectEvent(CookingKACucumberTests.mainActivity, false)
    }

    /*
    * Check whrPreStartConfiguration present for respective recipe
    * */
    fun hasWhrPreStartConfiguration(
        activity: Activity,
        jsonFileName: String,
        cavity: String,
        recipe: String
    ): Boolean {
        return try {
            val inputStream = activity.resources.openRawResource(
                activity.resources.getIdentifier(jsonFileName, "raw", activity.packageName)
            )
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            println("JSON Content: $jsonString")  // Print the JSON content for debugging

            val jsonObject = JSONObject(jsonString)
            val cavitiesObject = jsonObject.getJSONObject("cavities")

            // Iterate through the cavities to find the specified cavity
            val cavityObject = cavitiesObject.getJSONObject(cavity)

            val recipesObject = cavityObject.getJSONObject("recipes")

            // Check if the specified recipe exists
            if (recipesObject.has(recipe)) {
                val recipeObject = recipesObject.getJSONObject(recipe)
                // Return true if "whrPreStartConfiguration" is present
                return recipeObject.has("whrPreStartConfiguration")
            }
            false
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } catch (e: JSONException) {
            e.printStackTrace()
            false
        }
    }

    fun withViewPadding(expectedPaddingStart: Int, expectedPaddingEnd: Int): Matcher<View> {
        return object : BoundedMatcher<View, View>(View::class.java) {
            override fun describeTo(description: Description?) {
                description?.appendText("with padding: $expectedPaddingStart pixels")
            }

            override fun matchesSafely(item: View?): Boolean {
                val actualPaddingStart = item?.paddingStart ?: return false
                val actualPaddingEnd = item.paddingEnd
                return (actualPaddingStart == expectedPaddingStart && actualPaddingEnd == expectedPaddingEnd)
            }
        }
    }

    fun withTextSize(expectedSize: Float): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("with text size: $expectedSize")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (view is TextView) {
                    // Get the actual text size of the TextView
                    val actualSize = view.textSize  // textSize returns the size in pixels
                    return actualSize == expectedSize
                }
                return false
            }
        }
    }

    fun checkTextColor(expectedColorResId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("with text color: $expectedColorResId")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (view is TextView) {
                    // Get the actual text color from the TextView
                    val actualColor = view.currentTextColor
                    val expectedColor = ContextCompat.getColor(view.context, expectedColorResId)
                    return actualColor == expectedColor
                }
                return false
            }
        }
    }

    fun withFontFamily(expectedFontFamily: Typeface): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("with font family: $expectedFontFamily")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (view is TextView) {
                    val typeface = view.typeface
                    return typeface?.equals(expectedFontFamily) == true
                }
                return false
            }
        }
    }

    fun withGravity(expectedGravity: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("with gravity: $expectedGravity")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (view is TextView) {
                    val currentGravity = view.gravity
                    return currentGravity == expectedGravity
                }
                return false
            }
        }
    }

    fun withWidthAndHeight(expectedWidth: Int, expectedHeight: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description?) {
                description?.appendText("with width: $expectedWidth and height: $expectedHeight")
            }

            override fun matchesSafely(view: View?): Boolean {
                if (view != null) {
                    val width = view.width
                    val height = view.height
                    return width == expectedWidth && height == expectedHeight
                }
                return false
            }
        }
    }

    fun checkPropertiesOfText(viewId: Int, index: Int, text: String, textSize: Float, color: Int){
        Espresso.onView(withIndex(withId(viewId),index)).check(matches(withText(text)))
        Espresso.onView(withIndex(withId(viewId),index)).check(matches(withTextSize(textSize)))
        Espresso.onView(withIndex(withId(viewId),index)).check(matches(checkTextColor(color)))
    }

    fun checkPropertiesOfImage(viewId: Int, index: Int, width: Int, height:Int, drawable: Int){
        Espresso.onView(withIndex(withId(viewId),index)).check(matches(withWidthAndHeight(width,height)))
        val Icon = AppCompatResources.getDrawable(context, drawable)
        GenericViewTest.checkMatchesBackground(
            Espresso.onView(withIndex(withId(viewId),index)),
            Icon
        )
    }

}


