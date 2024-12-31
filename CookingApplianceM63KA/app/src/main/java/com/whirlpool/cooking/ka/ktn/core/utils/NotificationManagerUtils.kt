/*
 * ************************************************************************************************
 * ***** Copyright (c) 2020. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.utils

import CountDownTimerExt
import android.content.Context
import android.os.CountDownTimer
import android.presenter.customviews.listView.NotificationsTileData
import com.whirlpool.hmi.cookbook.CookBookViewModel
import core.utils.AppConstants.NOTIFICATION_DAY_COUNTER_TIMER
import core.utils.AppConstants.NOTIFICATION_DAY_COUNTER_TIMER_TICK
import core.utils.AppConstants.NOTIFICATION_NO_INTERACTION_TIMER
import core.utils.AppConstants.NOTIFICATION_NO_INTERACTION_TIMER_TICK
import core.utils.AppConstants.NOTIFICATION_TYPE_ACTIONABLE
import core.utils.AppConstants.NOTIFICATION_TYPE_TIP
import core.utils.AppConstants.TRUE_CONSTANT
import java.util.Calendar

/**
 * File        : core.utils.NotificationManagerUtils <br></br>
 * Brief       : Util class that Wraps the NavigationManager APIs , So any change in the
 * NavigationManager will affect only this file<br></br>
 * Author      : DUGAMAS <br></br>
 * Created On  : 27-03-2024 <br></br>
 * Details     : This Util class is to handle all the Audio Related features. This is the only
 * class communicating with NavigationManager .<br></br>
 */
object NotificationManagerUtils {
    //Queue having all sound file details which we need to play.
    private var notificationForClock: MutableList<NotificationQueue> = mutableListOf()
    private var notificationCenterListItems: ArrayList<NotificationsTileData>? = ArrayList()
    private lateinit var notificationJsonParser: NotificationJsonParser
    private lateinit var NoUserInteractionTimer: CountDownTimerExt
    lateinit var dayCounter: CountDownTimerExt
    private var noOfDaysSinceUnboxing = 0
    private var activateTipsAndTricks: Boolean = false

    init {
        initTimer()
        HMILogHelper.Logd("NotificationManager","timer Init")
    }
    private fun initTimer(){
        //1 hour timer
        NoUserInteractionTimer = object : CountDownTimerExt(NOTIFICATION_NO_INTERACTION_TIMER, NOTIFICATION_NO_INTERACTION_TIMER_TICK ) {//1 hour timer with 5 mins tick
            override fun onTimerTick(millisUntilFinished: Long) {
                HMILogHelper.Logd("NotificationManager"," NoUserInteractionTimer on tick timer")
                //current value of timer to be saved
                SharedPreferenceManager.setNoUserInteractionTimer(NoUserInteractionTimer.getRemainingTime().toString())
            }

            override fun onTimerFinish() {
                HMILogHelper.Logd("NotificationManager"," NoUserInteractionTimer on timer finish")
                if(notificationForClock.size > 0) {
                    removeNotificationFromQueue(notificationForClock[0].notification)
                }
            }
        }

        //24 hour timer
        dayCounter = object : CountDownTimerExt(NOTIFICATION_DAY_COUNTER_TIMER, NOTIFICATION_DAY_COUNTER_TIMER_TICK) {//24 hour timer and 1 hour tick
            override fun onTimerTick(millisUntilFinished: Long) {
                HMILogHelper.Logd("NotificationManager","dayCounter on tick timer")
                SharedPreferenceManager.setDayCounterTimer(dayCounter.getRemainingTime().toString())
            }

            override fun onTimerFinish() {
                HMILogHelper.Logd("NotificationManager","dayCounter on timer finish")

                CookingAppUtils.handleNotificationReOccurance()
                if (noOfDaysSinceUnboxing < 30){
                    if(getActivateDeactivateTipsAndTricks()) {
                        noOfDaysSinceUnboxing++
                        SharedPreferenceManager.setTipAndTrickDays(noOfDaysSinceUnboxing.toString())
                        CookingAppUtils.handleTipsAndTricks()
                    }
                    dayCounter.setRemainingTime(NOTIFICATION_DAY_COUNTER_TIMER)//(86400000)
                    dayCounter.cancelTimer()
                    dayCounter.start() //start again the CountDownTimer.
                    HMILogHelper.Logd("NotificationManager", "dayCounter started")
                } else {
                    activateDeactivateTipsAndTricks(false)
                }
                HMILogHelper.Logd("NotificationManager","dayCounter Its been $noOfDaysSinceUnboxing since unboxing")
                SharedPreferenceManager.setDayCounterTimer(dayCounter.getRemainingTime().toString())
            }
        }
    }
    fun loadNotificationJson(context: Context?) {
        notificationJsonParser = NotificationJsonParser()
        if (context?.let {
                notificationJsonParser.loadJson(
                    it,
                    NotificationJsonKeys.NOTIFICATION_JSON_FILE_NAME
                )
            } == true
        ) {
            HMILogHelper.Logd("NotificationManager","Successful in loading the notifications")
        } else {
            HMILogHelper.Loge("NotificationManager","Loading of notification JSON File not successful")
        }
    }

    fun addNotificationToQueue(
        notification: String
    ) {
        //Manage Notification that is to be shown on clock display
        manageNotificationOnClock(notification)

        //Manage Notification Center
        manageNotificationCenter(notification)

        HMILogHelper.Logi("NotificationManager", "New notification received $notification")
        HMILogHelper.Logi("NotificationManager", "Size of Notification queue ${notificationForClock.size}")

        //Set a flag to let Fragment clock know that New notification has arrived and is to be shown on Clock
        CookingAppUtils.setActiveNotificationChanged(true)

        //Start the No user interaction timer.
        activateNoUserInteractionDismissIfApplicable()
    }

    fun removeNotificationFromQueue(
        notification: String ) {
        val id = notificationJsonParser.getNotificationId(notification)
        notificationForClock.removeAll { it.navigationId == id }
        SharedPreferenceManager.saveNotificationQueue(notificationForClock)
        if(isNoUserInteractionTimerActive()){
            NoUserInteractionTimer.cancelTimer()
            SharedPreferenceManager.setNoUserInteractionTimer(0.toString())
        }
        CookingAppUtils.setActiveNotificationChanged(true)
    }

    fun getNotificationCenterListItems(): ArrayList<NotificationsTileData>? {
        return notificationCenterListItems
    }

    fun getActiveNotification(): String? {
        return if(notificationForClock.size > 0) {
            notificationForClock[0].notification
        } else{
            null
        }
    }

    private fun removeActiveTipNotification() {
        if(isActiveNotificationATip()){
            notificationForClock.removeAt(0)
        }

        if(notificationForClock.size > 1) {
            isNotificationATip(notificationForClock[1].notification)
            notificationForClock.removeAt(1)
        }
        SharedPreferenceManager.saveNotificationQueue(notificationForClock)
        CookingAppUtils.setActiveNotificationChanged(true)
    }

    fun isActiveNotificationActionable(): Boolean {
        if(notificationForClock.size > 0) {
            val type =
                notificationJsonParser.getNotificationType(notificationForClock[0].notification)
            return type == NOTIFICATION_TYPE_ACTIONABLE
        }
        return false
    }

    fun isActiveNotificationATip(): Boolean {
        if(notificationForClock.size > 0) {
            val type =
                notificationJsonParser.getNotificationType(notificationForClock[0].notification)
            return type == NOTIFICATION_TYPE_TIP
        }
        return false
    }

    private fun isNotificationATip(notification: String): Boolean {
        val type = notificationJsonParser.getNotificationType(notification)
        return type == NOTIFICATION_TYPE_TIP
    }

    private fun manageNotificationOnClock(notification: String){
        //fetch notification details from Json file
        val id = notificationJsonParser.getNotificationId(notification)
        val isNewNotificationATip = notificationJsonParser.getNotificationType(notification)
        val isOldNotificationATip = getActiveNotification()?.let {
            notificationJsonParser.getNotificationType(
                it
            )
        }

        //remove the old notification
        if((notificationForClock.size > 0)) {
            if((isNewNotificationATip != NOTIFICATION_TYPE_TIP && isOldNotificationATip != NOTIFICATION_TYPE_TIP) ||
                (isNewNotificationATip == NOTIFICATION_TYPE_TIP && isOldNotificationATip == NOTIFICATION_TYPE_TIP)){
                notificationForClock.removeAt(0)
            }

            if(isNewNotificationATip == NOTIFICATION_TYPE_TIP && isOldNotificationATip != NOTIFICATION_TYPE_TIP) {
                //add/replace the new notification at index 1
                if(notificationForClock.size == 2){
                    notificationForClock.removeAt(1)
                }
                notificationForClock.add(1, NotificationQueue(notification, id))
            }else{
                notificationForClock.add(0, NotificationQueue(notification, id))
            }
        }
        else{
            notificationForClock.add(0, NotificationQueue(notification, id))
        }
        HMILogHelper.Logd("Notification", "Notification in string format = ${notificationForClock.toString()}")
        SharedPreferenceManager.saveNotificationQueue(notificationForClock)
    }

    private fun manageNotificationCenter(notification: String) {

        //Check if the Notification is supposed to be added to Notification Center.
        if(notificationJsonParser.getNotificationGoesToNotificationCenter(notification)) {

            //get time Stamp.
            //needed for Notification Center
            val calendar = Calendar.getInstance()
            val currentTimeMillis = calendar.timeInMillis

            //Build list data for Notification Center
            val newNotification = NotificationsTileData()
            newNotification.titleText = notification
            newNotification.rightText = currentTimeMillis.toString()

            if(notificationJsonParser.getNotificationIsHistoryNeeded(notification)){
                newNotification.historyID = getHistoryRecordID()
            }

            //check if duplicate entries for the notification allowed in Notification Center
            if (notificationJsonParser.getNotificationDuplicateAllowedInNotificationCenter(newNotification.titleText)) {
                //Max size of Notification Center is 10.
                //If 11th Notification is triggered, remove the oldest one from Notification Center.
                //and add the latest notification at the top.
                if (notificationCenterListItems?.size == 10) {
                    notificationCenterListItems?.removeLast()
                }
                notificationCenterListItems?.add(0, newNotification)
            }
            //if duplicate notifications are not allowed, then we need to update the time stamp.
            //Instead of updating the time stamp, old instance is removed and new is added.
            // This is similar to keeping the old notification and updating the time stamp.
            else{
                //check if the notification is already present in Notification Center. If Yes, remove
                notificationCenterListItems?.removeIf { it.titleText ==  newNotification.titleText}

                //Max size of Notification Center is 10.
                //If 11th Notification is triggered, remove the oldest one from Notification Center.
                //and add the latest notification at the top.
                if (notificationCenterListItems?.size == 10) {
                    notificationCenterListItems?.removeLast()
                }

                notificationCenterListItems?.add(0,newNotification)
            }
            notificationCenterListItems?.let { SharedPreferenceManager.saveNotificationCenter(it) }
        }
    }

    private fun activateNoUserInteractionDismissIfApplicable(){
        if(notificationJsonParser.getNotificationNoInteractionDismiss(notificationForClock[0].notification)){
            if(isNoUserInteractionTimerActive()){
                NoUserInteractionTimer.cancelTimer()
            }
            NoUserInteractionTimer.setRemainingTime(NOTIFICATION_NO_INTERACTION_TIMER)//(3600000)//1 hour
            NoUserInteractionTimer.start()
            SharedPreferenceManager.setNoUserInteractionTimer(NoUserInteractionTimer.getRemainingTime().toString())
        }
    }

    fun removeNotificationFromNotificationCenter(notification: String ) {
        HMILogHelper.Logi("NotificationManager", "remove Notification From Notification Center $notification")
        if(notificationJsonParser.getNotificationIsHistoryNeeded(notification)){
            val historyId = CookingAppUtils.getHistoryIDToRemove()
            notificationCenterListItems?.removeIf {
                it.historyID ==  historyId
            }
            CookingAppUtils.setHistoryIDToRemove(-1)
        }
        else {
            notificationCenterListItems?.removeIf {
                it.titleText == notification
            }
        }
        notificationCenterListItems?.let { SharedPreferenceManager.saveNotificationCenter(it) }
    }

    fun activateDeactivateTipsAndTricks(state: Boolean){
        activateTipsAndTricks = state
        SharedPreferenceManager.setTipAndTrickStatus(activateTipsAndTricks)
        if(!activateTipsAndTricks){
            removeActiveTipNotification()
            noOfDaysSinceUnboxing = 0
            SharedPreferenceManager.setTipAndTrickDays(noOfDaysSinceUnboxing.toString())
        }
        HMILogHelper.Logi("NotificationManager", "Tips and tricks activate = $activateTipsAndTricks")
        noOfDaysSinceUnboxing = SharedPreferenceManager.getTipAndTrickDays()?.toInt()!!
        SharedPreferenceManager.setTipAndTrickDays(noOfDaysSinceUnboxing.toString())
    }

    fun getActivateDeactivateTipsAndTricks(): Boolean {
        return activateTipsAndTricks
    }

    fun isNoUserInteractionTimerActive():Boolean{
        return NoUserInteractionTimer.isTimerActive()
    }

    fun isNoUserInteractionTimerPaused():Boolean{
        return NoUserInteractionTimer.isTimerPaused()
    }

    fun pauseNoUserInteractionTimer(){
        NoUserInteractionTimer.pause()
        SharedPreferenceManager.setNoUserInteractionTimer(NoUserInteractionTimer.getRemainingTime().toString())
        HMILogHelper.Logd("NotificationManager"," NoUserInteractionTimer paused at ${NoUserInteractionTimer.getRemainingTime()}")
    }

    fun resumeNoUserInteractionTimer(){
        NoUserInteractionTimer.resume()
        SharedPreferenceManager.setNoUserInteractionTimer(NoUserInteractionTimer.getRemainingTime().toString())
        HMILogHelper.Logd("NotificationManager"," NoUserInteractionTimer resumed from ${NoUserInteractionTimer.getRemainingTime()}")
    }

    fun stopNoUserInteractionTimer(){
        NoUserInteractionTimer.cancelTimer()
        SharedPreferenceManager.setNoUserInteractionTimer(0.toString())
        HMILogHelper.Logd("NotificationManager"," NoUserInteractionTimer stopped")
    }
    fun handleBrownout(){
        //Recover Notification for clock
        notificationForClock = SharedPreferenceManager.getNotificationQueue()

        //Recover Notification center
        notificationCenterListItems = SharedPreferenceManager.getNotificationCenter()

        //Recover No user interaction timer
        val noUserInteractionTimer = SharedPreferenceManager.getNoUserInteractionTimer()?.toLong()
        if (noUserInteractionTimer != null && isActiveNotificationActionable()) {
            if(noUserInteractionTimer > 0){
                NoUserInteractionTimer.setRemainingTime(noUserInteractionTimer)
                NoUserInteractionTimer.start()
            }
        }

        //Recover day counter timer
        val dayCounterTimer = SharedPreferenceManager.getDayCounterTimer()?.toLong()
        if (dayCounterTimer != null) {
            if (dayCounterTimer > 0) {
                dayCounter.setRemainingTime(dayCounterTimer)
                dayCounter.start() //start again the CountDownTimer.
            }
        }

        //Recover tips and tricks
        val tipAndTrickStatus = SharedPreferenceManager.getTipAndTrickStatus()
        if(tipAndTrickStatus == TRUE_CONSTANT){
            activateDeactivateTipsAndTricks(true)
        }
        else{
            activateDeactivateTipsAndTricks(false)
        }

        noOfDaysSinceUnboxing = SharedPreferenceManager.getTipAndTrickDays()?.toInt() ?: 0
    }

    private fun getHistoryRecordID():Int {
        val listOfHistory = CookBookViewModel.getInstance().allHistoryRecords
        val historySize = CookBookViewModel.getInstance().historyCount
        var historyId = 0
        if(historySize>0){
            historyId = listOfHistory.value?.get(0)?.id ?: 0
        }
        return historyId
    }

}