/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2020. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package com.whirlpool.cooking.ka.test.cucumber.espresso_utils

import android.app.Activity
import android.content.Intent
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.whirlpool.hmi.nucleusbridge.controls.Control
import com.whirlpool.hmi.uitesting.UiTestingUtils
import com.whirlpool.hmi.utils.CookingSimConst
import com.whirlpool.hmi.utils.LogHelper
import core.utils.HMILogHelper
import core.utils.KnobDirection

/**
 * File       : com.whirlpool.cooking.Utils.CommonUtils.
 * Brief      : Utility to have common HMI operation API's which can reused in the whole test suite
 * To avoid code repetition and maintenance in test files
 * Author     : GHARDNS.
 * Created On : 28.02.2024.
 *
 */
object HMIKeyUtils {

    const val KNOB_ID_LEFT = "0"
    const val KNOB_ID_RIGHT = "1"

    private const val KNOB_ID_LEFT_ANGLE_POSITION = "275"
    private const val KNOB_ID_RIGHT_ANGLE_POSITION = "85"

    //Knob hmi config index

    const val HMI_KNOB_BUTTON_INDEX_RIGHT = "7"
    const val HMI_KNOB_BUTTON_INDEX_LEFT = "6"

    /**
     * Method to mock the upper cavity clean button press event
     */
    fun pressUpperOvenCleanButton(activity: Activity?) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
        // status screen
        LogHelper.Logi("Now doing CANCEL button press ------------>")
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_BUTTON_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_BUTTON, CookingSimConst.VAL_CS_BUTTON_CLEAN_PRESSED)
        activity?.sendBroadcast(intent)
        UiTestingUtils.sleep(200)
        intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_BUTTON_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_BUTTON, CookingSimConst.VAL_CS_BUTTON_CLEAN_RELEASED)
        activity?.sendBroadcast(intent)
        LogHelper.Logi(" All Done .... ")
        UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed
    }

    /**
     * Method to mock the lower cavity clean button press event
     */
    fun pressLowerOvenCleanButton(activity: Activity?) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
        // status screen
        LogHelper.Logi("Now doing CANCEL button press ------------>")
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_BUTTON_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_BUTTON, CookingSimConst.VAL_CS_BUTTON_CLEAN_PRESSED)
        activity?.sendBroadcast(intent)
        UiTestingUtils.sleep(200)
        intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_BUTTON_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_BUTTON, CookingSimConst.VAL_CS_BUTTON_CLEAN_RELEASED)
        activity?.sendBroadcast(intent)
        LogHelper.Logi(" All Done .... ")
        UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed
    }

    /**
     * Method to mock the upper cavity cancel button press event
     * @param activity Activity
     */
    fun pressUpperOvenCancelButton(activity: Activity?) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
        // status screen
        LogHelper.Logi("Now doing CANCEL button press ------------>")
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_BUTTON_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_BUTTON, CookingSimConst.VAL_CS_BUTTON_CANCEL_PRESSED)
        activity?.sendBroadcast(intent)
        UiTestingUtils.sleep(200)
        intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_BUTTON_EVENT)
        intent.putExtra(
            CookingSimConst.KEY_CS_BUTTON,
            CookingSimConst.VAL_CS_BUTTON_CANCEL_RELEASED
        )
        activity?.sendBroadcast(intent)
        LogHelper.Logi(" All Done .... ")
        UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed
    }

    /**
     * Method to mock the lower cavity cancel button press event
     */
    fun pressLowerOvenCancelButton(activity: Activity) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and views get updated in
        // status screen
        LogHelper.Logi("Now doing CANCEL button press ------------>")
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_BUTTON_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_BUTTON, CookingSimConst.VAL_CS_BUTTON_CANCEL_PRESSED)
        activity.sendBroadcast(intent)
        UiTestingUtils.sleep(200)
        intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_BUTTON_EVENT)
        intent.putExtra(
            CookingSimConst.KEY_CS_BUTTON,
            CookingSimConst.VAL_CS_BUTTON_CANCEL_RELEASED
        )
        activity.sendBroadcast(intent)
        LogHelper.Logi(" All Done .... ")
        UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed
    }

    /**
     * Method to mock the upper cavity door button event
     */
    fun openAndCloseUpperOvenDoor(activity: Activity?) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
        // clock screen
        HMILogHelper.Logi("TEST_", "Now doing DOOR button press ------------>")
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_DOOR_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_OPEN)
        activity?.sendBroadcast(intent)
        UiTestingUtils.sleep(200)
        intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_DOOR_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_CLOSE)
        activity?.sendBroadcast(intent)
        LogHelper.Logi("TEST_", " All Done .... ")
        UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed in clock screen
    }

    /**
     * Method to mock the upper cavity door button event
     */
    fun openUpperOvenDoor(activity: Activity?) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
        // clock screen
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_DOOR_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_OPEN)
        activity?.sendBroadcast(intent)
        HMILogHelper.Logi("TEST_", "Now DOOR is opened ------------>")
        UiTestingUtils.sleep(2000)
    }

    /**
     * Method to mock the upper cavity door button event
     */
    fun closeUpperOvenDoor(activity: Activity?) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_DOOR_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_CLOSE)
        activity?.sendBroadcast(intent)
        HMILogHelper.Logi("TEST_", "Now DOOR is closed ------------>")
        UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed in clock screen
    }

    /**
     * Method to mock the lower cavity door button event
     */
    fun openAndCloseLowerOvenDoor(activity: Activity?) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
        // clock screen
        HMILogHelper.Logi("Now doing DOOR button press ------------>")
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_DOOR_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_OPEN)
        activity?.sendBroadcast(intent)
        UiTestingUtils.sleep(200)
        intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_DOOR_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_CLOSE)
        activity?.sendBroadcast(intent)
        LogHelper.Logi(" All Done .... ")
        UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed in clock screen
    }

    /**
     * Method to mock the lower cavity door button event
     */
    fun openLowerOvenDoor(activity: Activity?) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_DOOR_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_OPEN)
        activity?.sendBroadcast(intent)
        HMILogHelper.Logi("TEST_", "Now DOOR is opened ------------>")
        UiTestingUtils.sleep(200)
    }

    /**
     * Method to mock the lower cavity door button event
     */
    fun closeLowerOvenDoor(activity: Activity?) {
        UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
        var intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_DOOR_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_CLOSE)
        activity?.sendBroadcast(intent)
        HMILogHelper.Logi("TEST_", "Now DOOR is closed ------------>")
        UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed in clock screen
    }

    /**
     * Method to mock the upper cavity door lock event
     */
    fun lockUpperOvenDoor(activity: Activity?) {
        val intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_DOOR_LOCK_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR_EVENT, CookingSimConst.VAL_CS_DOOR_EVENT_LOCK)
        activity?.sendBroadcast(intent)
        UiTestingUtils.sleep(2000)
    }

    /**
     * Method to mock the upper cavity door unlock event
     */
    fun unLockUpperOvenDoor(activity: Activity?) {
        val intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_DOOR_LOCK_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR_EVENT, CookingSimConst.VAL_CS_DOOR_EVENT_UNLOCK)
        activity?.sendBroadcast(intent)
        UiTestingUtils.sleep(2000)
    }

    /**
     * Method to mock the upper cavity door lock event
     */
    fun lockLowerOvenDoor(activity: Activity) {
        val intent = Intent()
        intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_DOOR_LOCK_EVENT)
        intent.putExtra(CookingSimConst.KEY_CS_DOOR_EVENT, CookingSimConst.VAL_CS_DOOR_EVENT_LOCK)
        activity.sendBroadcast(intent)
        UiTestingUtils.sleep(2000)
    }

    /**
     * Method to mock physical back button press event
     */
    fun pressPhysicalBackButton(activity: Activity) {
        UiTestingUtils.sleep(500) //Waiting for launching the required screen and view get updated in
        activity.runOnUiThread { activity.onBackPressed() }
        UiTestingUtils.sleep(500) //Waiting for redirect to the required screen after cancel btn pressed
    }

    /**
     * Method to stop the animation
     * @param resId - int
     * @param activity - Activity
     */
    fun stopLottieAnimation(resId: Int, activity: Activity) {
        activity.runOnUiThread {
            val animationView =
                activity.findViewById<View>(resId) as LottieAnimationView
            val lottieDrawable = animationView.drawable as LottieDrawable
            lottieDrawable.endAnimation()
        }
        UiTestingUtils.sleep(1000)
    }

    /**
     * Method to mock the upper cavity door button event
     */
    fun openAndCloseUpperOvenDoor(activity: Activity?,isOpenDoor:Boolean) {
        var intent = Intent()
        if(isOpenDoor) {
            UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
            // clock screen
            HMILogHelper.Logi("TEST_","Now doing DOOR button press ------------>")
            intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_DOOR_EVENT)
            intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_OPEN)
            activity?.sendBroadcast(intent)
            UiTestingUtils.sleep(200)
        } else {
            intent = Intent()
            intent.setAction(CookingSimConst.CS_CMD_PRIMARY_CAV_DOOR_EVENT)
            intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_CLOSE)
            activity?.sendBroadcast(intent)
            LogHelper.Logi("TEST_"," All Done .... ")
            UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed in clock screen
        }


    }
    /**
     * Method to mock the upper cavity door button event
     */
    fun openAndCloseLowerOvenDoor(activity: Activity?,isOpenDoor:Boolean) {
        var intent = Intent()
        if(isOpenDoor) {
            UiTestingUtils.sleep(2000) //Waiting for launching the required screen and view get updated in
            // clock screen
            HMILogHelper.Logi("Now doing DOOR button press ------------>")
            intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_DOOR_EVENT)
            intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_OPEN)
            activity?.sendBroadcast(intent)
            UiTestingUtils.sleep(200)
        } else {
            intent = Intent()
            intent.setAction(CookingSimConst.CS_CMD_SECONDARY_CAV_DOOR_EVENT)
            intent.putExtra(CookingSimConst.KEY_CS_DOOR, CookingSimConst.VAL_CS_DOOR_CLOSE)
            activity?.sendBroadcast(intent)
            LogHelper.Logi(" All Done .... ")
            UiTestingUtils.sleep(2000) //Waiting for redirect to the required screen after cancel btn pressed in clock screen
        }


    }


    /** ================================================================ #
    # =========================  Knob Methods =============== #
    # ================================================================= */

    /**
     * Method to mock the Right and Left Knob rotate event.
     * Send broadcast to trigger knob rotate event
     * @param activity - Activity instance to send broadcast event
     * @param knobID - Knob Id - [HMIKeyUtils.KNOB_ID_LEFT] / [HMIKeyUtils.KNOB_ID_RIGHT]
     * @param knobDirectionEvent - Knob Direction [KnobDirectionKotlin.CLOCK_WISE_DIRECTION] or [KnobDirectionKotlin.COUNTER_CLOCK_WISE_DIRECTION]
     * @param knobPosition - Knob Angle Position [HMIKeyUtils.HMI_KNOB_BUTTON_INDEX_RIGHT] or [HMIKeyUtils.HMI_KNOB_BUTTON_INDEX_LEFT]
     *
     */
    fun onKnobRotateEvent(
        activity: Activity?,
        knobID: String,
        knobDirectionEvent: String,
        knobPosition: String = if (knobDirectionEvent == KnobDirection.CLOCK_WISE_DIRECTION) KNOB_ID_RIGHT_ANGLE_POSITION else KNOB_ID_LEFT_ANGLE_POSITION
    ) {
        val intent = Intent()
        intent.setAction(Control.INTENT_ACTION_KNOB_EVENT)
        intent.putExtra(Control.INTENT_EXTRA_ID, knobID)
        intent.putExtra(Control.INTENT_EXTRA_EVENT, knobDirectionEvent)
        intent.putExtra(Control.INTENT_EXTRA_POSITION, knobPosition)
        activity?.sendBroadcast(intent)
        UiTestingUtils.sleep(500)
    }
    /**
     * Method to mock the Right Knob click event
     * @param activity - Activity instance to send broadcast event
     * @param buttonIndex - Right Knob button index [HMIKeyUtils.HMI_KNOB_BUTTON_INDEX_RIGHT]
     */
    fun onHMIRightKnobClick(
        activity: Activity?,
        buttonIndex: String
    ) {
        CookingSimConst.simulateButtonPressAndReleaseEvent(activity,buttonIndex)
        UiTestingUtils.sleep(500)
    }

    /**
     * Method to mock the Left Knob click event
     * @param activity - Activity instance to send broadcast event
     * @param buttonIndex - Left Knob button index [HMIKeyUtils.HMI_KNOB_BUTTON_INDEX_LEFT]
     */
    fun onHMILeftKnobClick(
        activity: Activity?,
        buttonIndex: String
    ) {
        CookingSimConst.simulateButtonPressAndReleaseEvent(activity,buttonIndex)
        UiTestingUtils.sleep(500)
    }

    /**
     * Method to mock the Right Knob Long click event
     * @param activity - Activity instance to send broadcast event
     * @param buttonIndex - Right Knob button index (HMIKeyUtils.HMI_KNOB_BUTTON_INDEX_RIGHT)
     */
    fun onHMIRightKnobLongClick(
        activity: Activity?,
        buttonIndex: String
    ) {
        CookingSimConst.simulateButtonPressEvent(activity,buttonIndex)
        //Pressed the button for 3 second and release
        UiTestingUtils.sleep(3000)
        CookingSimConst.simulateButtonReleaseEvent(activity,buttonIndex)
    }

    /**
     * Method to mock the Left Knob Long click event
     * @param activity - Activity instance to send broadcast event
     * @param buttonIndex - Right Knob button index (HMIKeyUtils.HMI_KNOB_BUTTON_INDEX_RIGHT)
     */
    fun onHMILeftKnobLongClick(
        activity: Activity?,
        buttonIndex: String
    ) {
        CookingSimConst.simulateButtonPressEvent(activity,buttonIndex)
        //Pressed the button for 3 second and release
        UiTestingUtils.sleep(3000)
        CookingSimConst.simulateButtonReleaseEvent(activity,buttonIndex)
    }

    /**
     * Broadcasts an intent to simulate an oven temperature change event.
     *
     * @param activity [Activity]
     * @param isPrimaryCavity boolean
     */
    fun simulateOvenTemperatureChangeEvent(
        activity: Activity,
        isPrimaryCavity: Boolean,
        newTemperature: String?
    ) {
        val intent = Intent()
        intent.setAction(getCurrentOvenTemperatureIntentAction(isPrimaryCavity))
        intent.putExtra(
            CookingSimConst.KEY_CS_CURRENT_TEMP,
            newTemperature
        )
        activity.sendBroadcast(intent)
        UiTestingUtils.sleep(3000)
    }

    fun getCurrentOvenTemperatureIntentAction(isPrimary: Boolean): String {
        return if (isPrimary) {
            CookingSimConst.CS_CMD_PRIMARY_CAV_CURRENT_TEMP_EVENT
        } else {
            CookingSimConst.CS_CMD_SECONDARY_CAV_CURRENT_TEMP_EVENT
        }
    }

    fun simulateMeatProbeConnected(activity: Activity?, isPrimaryCavity: Boolean){
        UiTestingUtils.sleep(4000)
        CookingSimConst.simulateMeatProbeConnectEvent(activity, isPrimaryCavity)
        UiTestingUtils.sleep(3000)
    }
    fun simulateMeatProbeDisconnected(activity: Activity?, isPrimaryCavity: Boolean){
       CookingSimConst.simulateMeatProbeDisconnectEvent(activity,isPrimaryCavity)
        UiTestingUtils.sleep(3000)
    }

    /**
     * Method to mock the Button click event
     * @param activity - Activity instance to send broadcast event
     * @param buttonIndex - button index (HMIKeyUtils.HMI_KNOB_BUTTON_INDEX_RIGHT)
     */
    fun onButtonClick(
        activity: Activity?,
        buttonIndex: String
    ) {
        CookingSimConst.simulateButtonPressEvent(activity,buttonIndex)
        //Pressed the button for 3 second and release
        UiTestingUtils.sleep(3000)
        CookingSimConst.simulateButtonReleaseEvent(activity,buttonIndex)
    }
}
