package core.utils

import android.content.Context
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel.TimerStatus
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit

/**
 * File       : core/utils/KitchenTimerUtils.java
 * Brief      : Contains helper utility methods which provides APIs for Kitchen Timers ONLY
 * Author     : Hiren
 * Created On : 20/06/24
 * Details    : This Util class is to find whether a particular Kitchen Timer is started, paused, resumed, cancel
 */
class KitchenTimerUtils {

    companion object {
        @Suppress("ConstPropertyName")
        private const val tag = "KitchenTimer"

        /**
         * Observe on any running KitchenTimer events, show popup if Timer is completed
         * @param fragment
         */
        fun onKitchenTimerListener(fragment: Fragment, onKTCompleteCallback: (ktModel: KitchenTimerViewModel) -> Unit) {
            val kitchenTimerViewModels = KitchenTimerVMFactory.getKitchenTimerViewModels()
            if (KitchenTimerVMFactory.isAnyKitchenTimerRunning() && kitchenTimerViewModels != null) {
                for (kitchenTimerViewModel in kitchenTimerViewModels) {
                    kitchenTimerViewModel.timerStatus.observe(fragment) { timerStatus ->
                        HMILogHelper.Logd(tag, "name ${kitchenTimerViewModel.timerName} status = $timerStatus")
                        if (timerStatus == TimerStatus.COMPLETED){
//                            AudioManagerUtils.playOneShotSound(
//                                ContextProvider.getContext(),
//                                R.raw.long_notification_1,
//                                AudioManager.STREAM_SYSTEM,
//                                true,
//                                0,
//                                1
//                            )
                            onKTCompleteCallback(kitchenTimerViewModel)
                        }
                    }
                }
            }
        }

        /**
         * remove all observers for a particular fragment on KitchenTimer
         * @param fragment
         */
        fun removeKitchenTimerListener(fragment: Fragment) {
            KitchenTimerVMFactory.getKitchenTimerViewModels()?.let {
                for (ktModel in it) {
                    ktModel.timerStatus.removeObservers(fragment)
                    ktModel.remainingTime.removeObservers(fragment)
                }
            }
        }

        /**
         * Method to verify the kitchen timer is in running or in paused state
         *
         * @param kitchenTimerViewModel - kitchen timer view mode to be verified
         * @return true if it satisfies the condition
         */
        @Suppress("unused")
        fun isInRunningOrPausedState(kitchenTimerViewModel: KitchenTimerViewModel): Boolean {
            return (Objects.requireNonNull(kitchenTimerViewModel.timerStatus.value) == TimerStatus.RUNNING || kitchenTimerViewModel.timerStatus.value == TimerStatus.PAUSED)
        }

        /**
         * show time user facing time remaining value, utilizing in KitchenTimerFragment widget
         *
         * @param timeInSeconds coming from KitchenTimerViewModel time remaining
         * @return time remaining text show hour if > 0 HH:MM:SS otherwise only show MM:SS
         */
        @Suppress("unused")
        fun convertTimeRemainingToString(timeInSeconds: Long): String {
            val hours = TimeUnit.SECONDS.toHours(timeInSeconds)
            val minutes =
                TimeUnit.SECONDS.toMinutes(timeInSeconds) - TimeUnit.HOURS.toMinutes(hours)
            val sec = TimeUnit.MINUTES.toSeconds(minutes)
            val seconds = timeInSeconds - TimeUnit.HOURS.toSeconds(hours) - sec
            if (hours > 0) return String.format(
                Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds
            )
            return String.format(
                Locale.getDefault(), "%02d:%02d", minutes, seconds
            )
        }

        /**
         * show time user facing time remaining value, utilizing in ClockFragment or StatusFragment widget
         * generally opting out third time value
         * @param timeInSeconds coming from KitchenTimerViewModel time remaining
         * @return time remaining text show hour if > 0 HH:MM otherwise only show MM:SS
         */
        fun convertTimeRemainingToShortString(timeInSeconds: Long): String {
            val hours = TimeUnit.SECONDS.toHours(timeInSeconds)
            val minutes =
                TimeUnit.SECONDS.toMinutes(timeInSeconds) - TimeUnit.HOURS.toMinutes(hours)
            val sec = TimeUnit.MINUTES.toSeconds(minutes)
            val seconds = timeInSeconds - TimeUnit.HOURS.toSeconds(hours) - sec
            if (hours > 0) return String.format(
                Locale.getDefault(), "%02d:%02d", hours, minutes
            )
            return String.format(
                Locale.getDefault(), "%02d:%02d", minutes, seconds
            )
        }
        /**
         * show time user facing time remaining value, utilizing in ClockFragment or StatusFragment widget
         * generally opting out third time value
         * @param timeInSeconds coming from KitchenTimerViewModel time remaining
         * @return time remaining text show hour if > 0 HH:MM otherwise only show MM:SS
         */
        fun convertTimerCompletedToShortString(
            context: Context,
            timeInSeconds: Long
        ): String {
            val hours = TimeUnit.SECONDS.toHours(timeInSeconds)
            val minutes =
                TimeUnit.SECONDS.toMinutes(timeInSeconds) - TimeUnit.HOURS.toMinutes(hours)
            val sec = TimeUnit.MINUTES.toSeconds(minutes)
            val seconds = timeInSeconds - TimeUnit.HOURS.toSeconds(hours) - sec
            val hrLabel = context.getString(R.string.text_label_hr)
            val minLabel = context.getString(R.string.text_label_min)
            val secLabel = context.getString(R.string.text_label_sec)
            if (hours > 0 && minutes > 0 && seconds > 0) return String.format(
                Locale.getDefault(), "%d %s %d %s %d %s", hours, hrLabel, minutes, minLabel, seconds, secLabel
            )
            if (hours > 0 && minutes > 0) return String.format(
                Locale.getDefault(), "%d %s %d %s", hours, hrLabel, minutes, minLabel
            )
            if (hours > 0) return String.format(
                Locale.getDefault(), "%d %s", hours, hrLabel
            )
            if (minutes > 0 && seconds > 0) return String.format(
                Locale.getDefault(), "%d %s %d %s", minutes, minLabel, seconds, secLabel
            )
            if (minutes > 0) return String.format(
                Locale.getDefault(), "%d %s", minutes, minLabel
            )
            if (seconds > 0) return String.format(
                Locale.getDefault(), "%d %s", seconds, secLabel
            )
            return String.format(
                Locale.getDefault(), "%02d:%02d", minutes, seconds
            )
        }

        /**
         * adding a new kitchen timer in the SDK
         *
         * @param fragment screen when KT is being called
         * @param kTSeconds number of seconds KT
         * @param onSuccessKTAdded once successfully KT added then callback
         */
        fun addKitchenTimer(fragment: Fragment, kTSeconds: Int, onSuccessKTAdded: () -> Unit) {
            val kitchenTimerViewModels = KitchenTimerVMFactory.getKitchenTimerViewModels()
            if (kitchenTimerViewModels != null) {
                for (kitchenTimerViewModel in kitchenTimerViewModels) {
                    val timerStatus = kitchenTimerViewModel.timerStatus.value

                    //Set Timer Value to the Non Set Kitchen View Model
                    if (timerStatus == TimerStatus.IDLE || timerStatus == TimerStatus.COMPLETED || timerStatus == TimerStatus.CANCELLED) {
                        //TODO : First timer alone need to be added here, other timers should be
                        // add in the Monitoring KT screen to perform insert operation

                        val kitchenTimerName = fragment.resources.getString(
                            R.string.text_sub_header_kitchen_timer, kitchenTimerViewModel.timerId.plus(1)
                        )
                        kitchenTimerViewModel.timerName = kitchenTimerName

                        if (kitchenTimerViewModel.setTimer(kTSeconds)) {
                            HMILogHelper.Logd(tag, "adding KT success name $kitchenTimerName with seconds $kTSeconds")
                            onSuccessKTAdded()
                            return
                        }
                    }
                }
            }
        }
        /**
         * modifying an existing kitchen timer in the SDK
         * @param kTSeconds number of seconds KT
         * @param onSuccessKTAdded once successfully KT added then callback
         */
        fun modifyKitchenTimer(
            kitchenTimerName: String?,
            kTSeconds: Int,
            onSuccessKTAdded: () -> Unit,
        ) {
            val kitchenTimerViewModels = KitchenTimerVMFactory.getKitchenTimerViewModels()
            if (kitchenTimerName != null && kitchenTimerViewModels != null) {
                for (kitchenTimerViewModel in kitchenTimerViewModels) {
                    //Set Timer Value to the Non Set Kitchen View Model
                    if (kitchenTimerName.contentEquals(kitchenTimerViewModel.timerName)) {
                        val isStopped = kitchenTimerViewModel.stopTimer()
                        HMILogHelper.Logd(tag, "kitchenTimer MODIFY stop name $kitchenTimerName success $isStopped")
                        kitchenTimerViewModel.timerName = kitchenTimerName
                        if (kitchenTimerViewModel.setTimer(kTSeconds)) {
                            HMILogHelper.Logd(tag, "kitchenTimer MODIFY success name $kitchenTimerName with seconds $kTSeconds")
                            onSuccessKTAdded()
                            return
                        }
                    }
                }
            }
        }

        /**
         * modifying an existing kitchen timer in the SDK
         * @param kitchenTimerName
         */
        fun getKitchenTimerViewModelFromName(
            kitchenTimerName: String?): KitchenTimerViewModel? {
            val kitchenTimerViewModels = KitchenTimerVMFactory.getKitchenTimerViewModels()
            if (kitchenTimerName != null && kitchenTimerViewModels != null) {
                for (kitchenTimerViewModel in kitchenTimerViewModels) {
                    //Set Timer Value to the Non Set Kitchen View Model
                    if (kitchenTimerName.contentEquals(kitchenTimerViewModel.timerName)) {
                        return kitchenTimerViewModel
                    }
                }
            }
            return null
        }

        /**
         * method to get the list of minutes
         *
         * @return
         */
        fun getHourList(): MutableList<String> {
            return mutableListOf<String>().apply {
                repeat(25) { add(it.toString().padStart(2, '0')) }
            }
        }

        /**
         * method to get the list of seconds/minutes
         *
         * @return
         */
        fun getMinuteSecondList(): MutableList<String> {
            return mutableListOf<String>().apply {
                repeat(60) { add(it.toString().padStart(2, '0')) }
            }
        }

        /** to determine if kitchen timer view model is able to add one extra minute to kitchen timer or not
         * @param kitchenTimerViewModel a particular kitchen timer
         * @return true if able to add one min false otherwise
         */
        fun isAbleToAddOneMinToKitchenTimer(kitchenTimerViewModel: KitchenTimerViewModel?): Boolean {
            //max kitchen timer limit is 23h 59m 59 s which is 86399, disabling +1 min button on time set to more than 86339 sec
            return (kitchenTimerViewModel?.remainingTime?.value ?: 0) <= 24.times(3600).minus(61)
        }

        /**
         * Method to verify and return the number of kitchen timer is in running state
         *
         * @return count of kitchen timers running
         */
        fun isKitchenTimersRunning(): Int {
            var count = 0
            for (kitchenTimerViewModel in KitchenTimerVMFactory.getKitchenTimerViewModels()!!) {
                if (Objects.requireNonNull(kitchenTimerViewModel.timerStatus.value) ==
                    (KitchenTimerViewModel.TimerStatus.RUNNING)
                ) {
                    count++
                }
            }
            return count
        }

        /**
         * to check if any kitchen timer is running or paused state
         * @return true if any kitchen timer is running or paused
         */
        fun isAnyKitchenTimerRunningOrPaused(): Boolean {
            val kitchenTimerViewModels = KitchenTimerVMFactory.getKitchenTimerViewModels()
            if (kitchenTimerViewModels?.any { it.isRunning } == true) return true
            if (kitchenTimerViewModels != null) for (kitchenTimerViewModel in kitchenTimerViewModels) return (kitchenTimerViewModel.timerStatus.value == TimerStatus.RUNNING || kitchenTimerViewModel.timerStatus.value == TimerStatus.PAUSED)
            return false
        }
    }
}