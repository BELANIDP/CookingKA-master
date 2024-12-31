package core.utils

import android.presenter.customviews.listView.NotificationsTileData
import android.presenter.model.KnobEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whirlpool.hmi.utils.ContextProvider


object SharedPreferenceManager {

    /**
     * Method to clear view Preference Data
     */
    fun resetViewSharedPreferenceData() {
        setSkipExploreFeatureFlag(AppConstants.FALSE_CONSTANT)
        setKnobAssignFavoritesCycleStatusIntoPreference(AppConstants.FALSE_CONSTANT)
        setKnobLightStatusIntoPreference(AppConstants.TRUE_CONSTANT)
        setLeftAndRightKnobPositionIntoPreference(KnobEntity())
        setCurrentUserRoleIntoPreference(AppConstants.TRUE_CONSTANT)
        setKnobAssignFavoritesCycleNameIntoPreference(AppConstants.EMPTY_STRING)
        setNoOfUses(AppConstants.DEFAULT_LEVEL)
        setNoOfUsesOfSwipeDown(AppConstants.DEFAULT_LEVEL)
        setActiveTipNumber(AppConstants.DEFAULT_LEVEL)
        saveNotificationCenter(arrayListOf())
        saveNotificationQueue(arrayListOf())
        setNoUserInteractionTimer(AppConstants.DEFAULT_LEVEL)
        setDayCounterTimer(AppConstants.DEFAULT_LEVEL)
        setTipAndTrickStatus(false)
        setTipAndTrickDays(AppConstants.DEFAULT_LEVEL)
    }

    /**
     * Method to save current user role into preferences
     */
    fun setCurrentUserRoleIntoPreference(isTechnicianRole: String) {
        HMILogHelper.Logd(
            "Unboxing",
            "Unboxing: Is current user role is technician = $isTechnicianRole"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.PREFERENCE_UNBOXING_IS_TECHICIAN_ROLE, isTechnicianRole
        )
    }
    /**
     * Method to get current user role into preferences
     */
    fun getCurrentUserRoleIntoPreference(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.PREFERENCE_UNBOXING_IS_TECHICIAN_ROLE, AppConstants.TRUE_CONSTANT
        )
    }

    /**
     * Method to save current user role into preferences
     */
    fun setTechnicianTestDoneStatusIntoPreference(isTechnicianTestDone: String) {
        HMILogHelper.Logd(
            "Unboxing",
            "Unboxing: is technician test done status  = $isTechnicianTestDone"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.PREFERENCE_UNBOXING_IS_TECHNICIAN_TEST_DONE, isTechnicianTestDone
        )
    }
    /**
     * Method to get current user role into preferences
     */
    fun getTechnicianTestDoneStatusIntoPreference(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.PREFERENCE_UNBOXING_IS_TECHNICIAN_TEST_DONE, AppConstants.TRUE_CONSTANT
        )
    }

    fun setSkipExploreFeatureFlag(setSkipExploreFeatureFlag: String) {
        HMILogHelper.Logd(
            "Unboxing",
            "Unboxing: is Skip Explore Feature Flag status  = $setSkipExploreFeatureFlag"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.PREFERENCE_UNBOXING_IS_SKIP_EXPLORE_FLAG, setSkipExploreFeatureFlag
        )
    }

    /**
     * Method to get current user role into preferences
     */
    fun getSkipExploreFeatureFlag(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.PREFERENCE_UNBOXING_IS_SKIP_EXPLORE_FLAG, AppConstants.FALSE_CONSTANT
        )
    }

    /**
     * set preference to maintain flag is brownout or blackout happened during Cancelling in 15 sec window
     * should be reset when any particular cavity is running
     * @param isPrimaryCavity true is primary cavity false for secondary cavity
     * @param isPauseForCancel true if cancelled pressed
     */
    fun setPauseForCancelRecovery(isPrimaryCavity: Boolean, isPauseForCancel: String) {
        HMILogHelper.Logd(
            "AbstractStatusFragment",
            "setPauseForCancelRecovery isPrimaryCavity $isPrimaryCavity Flag status  = $isPauseForCancel"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            if(isPrimaryCavity) AppConstants.PREFERENCE_PAUSE_FOR_CANCEL_RECOVERY_UPPER_CAVITY else AppConstants.PREFERENCE_PAUSE_FOR_CANCEL_RECOVERY_LOWER_CAVITY, isPauseForCancel
        )
    }

    /**
     * get the flag of a particular cavity if pauseForCancel used to cancel a recipe
     * @param isPrimaryCavity true is primary cavity false for secondary cavity
     * @return true if during cancelling brownout or blackout happened
     */
    fun getPauseForCancelRecovery(isPrimaryCavity: Boolean): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            if(isPrimaryCavity) AppConstants.PREFERENCE_PAUSE_FOR_CANCEL_RECOVERY_UPPER_CAVITY else AppConstants.PREFERENCE_PAUSE_FOR_CANCEL_RECOVERY_LOWER_CAVITY, AppConstants.FALSE_CONSTANT
        )
    }

    /**
     * Method to save Left And Right Knob Position
     */
    fun setLeftAndRightKnobPositionIntoPreference(knobEntity: KnobEntity) {
        AppConstants.LEFT_KNOB_ID = knobEntity.leftKnob
        AppConstants.RIGHT_KNOB_ID = knobEntity.rightKnob
        val preferencesKnobValue = Gson().toJson(knobEntity)
        HMILogHelper.Logd(
            "Knob",
            "Knob: preference knob entity  = $preferencesKnobValue"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.PREFERENCE_KNOB_POSITION, preferencesKnobValue
        )
    }
    /**
     * Method to get Left And Right Knob Position
     */
    fun getLeftAndRightKnobPositionIntoPreference(): KnobEntity {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        val preferencesKnobValue = sharedPreferenceUtils?.getValue(
                AppConstants.PREFERENCE_KNOB_POSITION, Gson().toJson(KnobEntity())
        )
        val knobEntity: KnobEntity = Gson().fromJson(preferencesKnobValue, KnobEntity::class.java)
        HMILogHelper.Logd(
            "Knob",
            "Knob: getting preference knob entity  = $knobEntity"
        )
        return knobEntity
    }

    /**
     * Method to save current user kob light status into preferences
     */
    fun setKnobLightStatusIntoPreference(knobLight: String) {
        HMILogHelper.Logd(
            "Knob",
            "Knob: Knob light status  = $knobLight"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.PREFERENCE_KNOB_LIGHT, knobLight
        )
    }
    /**
     * Method to get current user kob light status into preferences
     */
    fun getKnobLightStatusIntoPreference(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.PREFERENCE_KNOB_LIGHT, AppConstants.TRUE_CONSTANT
        )
    }

    /**
     * Method to save current user kob light status into preferences
     * true  - for the favorites cycle
     * false - for Quick start cycle - quick bake 350 or Quick microwave 30 sec
     */
    fun setKnobAssignFavoritesCycleStatusIntoPreference(assignFavorites: String) {
        HMILogHelper.Logd(
            "Knob",
            "Knob: AssignFavoritesCycleStatusIntoPreference  = $assignFavorites"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.PREFERENCE_ASSIGN_FAVORITES, assignFavorites
        )
    }
    /**
     * Method to get current user kob light status into preferences
     */
    fun getKnobAssignFavoritesCycleStatusIntoPreference(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.PREFERENCE_ASSIGN_FAVORITES, AppConstants.FALSE_CONSTANT
        )
    }

    /**
     * Method to save current user kob light status into preferences
     * true  - for the favorites cycle
     * false - for Quick start cycle - quick bake 350 or Quick microwave 30 sec
     */
    fun setKnobAssignFavoritesCycleNameIntoPreference(favoritesName: String) {
        HMILogHelper.Logd(
            "Knob",
            "Knob: setKnobAssignFavoritesCycleNameIntoPreference  = $favoritesName"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.PREFERENCE_QUICK_FAVORITES_CYCLE, favoritesName
        )
    }
    /**
     * Method to get current user kob light status into preferences
     */
    fun getKnobAssignFavoritesCycleNameIntoPreference(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.PREFERENCE_QUICK_FAVORITES_CYCLE, AppConstants.EMPTY_STRING
        )
    }

    /**
     * Method to get number of uses since unboxing
     */
    fun getNoOfUses(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.NAVIGATION_NO_OF_USES, AppConstants.DEFAULT_LEVEL
        )
    }

    /**
     * Method to save number of uses since unboxing
     */
    fun setNoOfUses(setNoOfUses: String) {
        HMILogHelper.Logd(
            "Notification",
            "No of uses  = $setNoOfUses"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.NAVIGATION_NO_OF_USES, setNoOfUses
        )
    }

    /**
     * Method to get number of uses of swipe down since unboxing
     */
    fun getNoOfUsesOfSwipeDown(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.NAVIGATION_NO_OF_USES_OF_SWIPE_DOWN, AppConstants.DEFAULT_LEVEL
        )
    }

    /**
     * Method to save number of uses of swipe down since unboxing
     */
    fun setNoOfUsesOfSwipeDown(setNoOfUsesOfSwipeDown: String) {
        HMILogHelper.Logd(
            "Notification",
            "No of uses of Swipe down for settings = $setNoOfUsesOfSwipeDown"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.NAVIGATION_NO_OF_USES_OF_SWIPE_DOWN, setNoOfUsesOfSwipeDown
        )
    }

    /**
     * Method to get active tip number
     */
    fun getActiveTipNumber(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.NAVIGATION_ACTIVE_TIP_NUMBER, AppConstants.DEFAULT_LEVEL
        )
    }

    /**
     * Method to save active tip number
     */
    fun setActiveTipNumber(setActiveTipNumber: String) {
        HMILogHelper.Logd(
            "Notification",
            "Current Tip number = $setActiveTipNumber"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.NAVIGATION_ACTIVE_TIP_NUMBER, setActiveTipNumber
        )
    }

    /**
     * Method to get Notification Center
     */
    fun getNotificationCenter(): ArrayList<NotificationsTileData> {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )

        val list = sharedPreferenceUtils?.getValue(
            AppConstants.NOTIFICATION_CENTER, Gson().toJson(emptyList<NotificationsTileData>())
        )
        HMILogHelper.Logd(
            "Notification",
            "Notification Center retrieved= $list"
        )
        val notificationCenterListType = object : TypeToken<ArrayList<NotificationsTileData>>() {}.type
        val notificationCenterList: ArrayList<NotificationsTileData> = Gson().fromJson(list, notificationCenterListType)

        return notificationCenterList
    }

    /**
     * Method to save Notification Center
     */
    fun saveNotificationCenter(activeNotification: ArrayList<NotificationsTileData>) {
        val notificationCenterDataString = Gson().toJson(activeNotification)
        HMILogHelper.Logd(
            "Notification",
            "Notification Center saved= $notificationCenterDataString"
        )

        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.NOTIFICATION_CENTER, notificationCenterDataString
        )
    }

    /**
     * Method to get Notification Queue
     */
    fun getNotificationQueue(): MutableList<NotificationQueue> {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )

        val queue = sharedPreferenceUtils?.getValue(
            AppConstants.NOTIFICATION_QUEUE, Gson().toJson(emptyList<NotificationQueue>())
        )
        HMILogHelper.Logd(
            "Notification",
            "Notification Queue retrieved = $queue"
        )
        val notificationQueueType = object : TypeToken<MutableList<NotificationQueue>>() {}.type
        val notificationQueue: MutableList<NotificationQueue> = Gson().fromJson(queue, notificationQueueType)

        return notificationQueue
    }

    /**
     * Method to save Notification Queue
     */
    fun saveNotificationQueue(notificationQueue: MutableList<NotificationQueue>) {
        val notificationQueueDataString = Gson().toJson(notificationQueue)
        HMILogHelper.Logd(
            "Notification",
            "Notification Queue saved= $notificationQueueDataString"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.NOTIFICATION_QUEUE, notificationQueueDataString
        )
    }

    /**
     * Method to get No User Interaction Timer remaining time
     */
    fun getNoUserInteractionTimer(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        HMILogHelper.Logd(
            "Notification",
            "Get No User Interaction Timer remaining time = ${sharedPreferenceUtils?.getValue(
                AppConstants.NOTIFICATION_NO_INTERACTION_REMAINING_TIME, AppConstants.DEFAULT_LEVEL
            )}"
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.NOTIFICATION_NO_INTERACTION_REMAINING_TIME, AppConstants.DEFAULT_LEVEL
        )
    }

    /**
     * Method to Set No User Interaction Timer remaining time
     */
    fun setNoUserInteractionTimer(remainingTime: String) {
        HMILogHelper.Logd(
            "Notification",
            "Set No User Interaction Timer remaining time = $remainingTime"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.NOTIFICATION_NO_INTERACTION_REMAINING_TIME, remainingTime
        )
    }


    /**
     * Method to get day counter remaining time
     */
    fun getDayCounterTimer(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        HMILogHelper.Logd(
            "Notification",
            "Get Day counter Timer remaining time = ${sharedPreferenceUtils?.getValue(
                AppConstants.NOTIFICATION_DAY_COUNTER_REMAINING_TIME, AppConstants.DEFAULT_LEVEL
            )}"
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.NOTIFICATION_DAY_COUNTER_REMAINING_TIME, AppConstants.DEFAULT_LEVEL
        )
    }

    /**
     * Method to Set day counter remaining time
     */
    fun setDayCounterTimer(remainingTime: String) {
        HMILogHelper.Logd(
            "Notification",
            "Set day counter remaining time = $remainingTime"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.NOTIFICATION_DAY_COUNTER_REMAINING_TIME, remainingTime
        )
    }

    /**
     * Method to get Tip And Trick Status
     */
    fun getTipAndTrickStatus(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        HMILogHelper.Logd(
            "Notification",
            "Get Tip And Trick Status = ${sharedPreferenceUtils?.getValue(
                AppConstants.NOTIFICATION_TIP_TRICK_STATUS, AppConstants.FALSE_CONSTANT
            )}"
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.NOTIFICATION_TIP_TRICK_STATUS, AppConstants.FALSE_CONSTANT
        )
    }

    /**
     * Method to set Tip And Trick Status
     */
    fun setTipAndTrickStatus(status: Boolean) {
        HMILogHelper.Logd(
            "Notification",
            "Set  Tip And Trick Status  = $status"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        if(status) {
            sharedPreferenceUtils?.saveValue(
                AppConstants.NOTIFICATION_TIP_TRICK_STATUS, AppConstants.TRUE_CONSTANT
            )
        }
        else{
            sharedPreferenceUtils?.saveValue(
                AppConstants.NOTIFICATION_TIP_TRICK_STATUS, AppConstants.FALSE_CONSTANT
            )
        }
    }

    /**
     * Method to get Tip And Trick days
     */
    fun getTipAndTrickDays(): String? {
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        HMILogHelper.Logd(
            "Notification",
            "Get Tip And Trick days = ${sharedPreferenceUtils?.getValue(
                AppConstants.NOTIFICATION_TIP_TRICK_DAYS, AppConstants.DEFAULT_LEVEL
            )}"
        )
        return sharedPreferenceUtils?.getValue(
            AppConstants.NOTIFICATION_TIP_TRICK_DAYS, AppConstants.DEFAULT_LEVEL
        )
    }

    /**
     * Method to save Tip And Trick days
     */
    fun setTipAndTrickDays(days: String) {
        HMILogHelper.Logd(
            "Notification",
            "Set  Tip And Trick days  = $days"
        )
        val sharedPreferenceUtils: SharedPreferenceUtils? = SharedPreferenceUtils.getInstance(
            ContextProvider.getContext(), AppConstants.SHARED_PREF_DB_NAME
        )
        sharedPreferenceUtils?.saveValue(
            AppConstants.NOTIFICATION_TIP_TRICK_DAYS, days
        )
    }
}