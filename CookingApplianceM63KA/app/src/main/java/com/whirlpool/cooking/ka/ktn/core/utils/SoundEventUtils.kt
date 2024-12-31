package core.utils

import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.presenter.basefragments.AbstractStatusFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.ContextProvider
import com.whirlpool.hmi.utils.timers.Timer

/**
 * Utility class for observing preheat complete, timers complete, and meat probe temperature reached events.
 */
class SoundEventUtils {

    companion object {
        private const val TAG = "SoundEventUtils"
        private const val INACTIVITY_DELAY_MS = 300_000L // 5 minutes in milliseconds

        private val handler = Handler(Looper.getMainLooper())
        private var lastSoundResId: Int? = null // Track the last played sound ID

        fun observePreheatCompleteSoundEvents(fragmentActivity: FragmentActivity?) {
            fragmentActivity?.let { activity ->
                val upperVM = CookingViewModelFactory.getPrimaryCavityViewModel()
                val productVariantEnum = CookingViewModelFactory.getProductVariantEnum()

                observeTimerCompletion(upperVM, activity, "upperCavityTimerCompleted")
                observePreheatCompletion(upperVM, activity, "microwavePreheatComplete", "upperCavityTimerCompleted")
                observeMeatProbeTemperatureReached(upperVM, activity, "upperCavityProbeTemperatureReached")
                observeUserInstructions(upperVM, activity, "upperCavityUserInstruction")
                observeDoorInteraction(upperVM, activity, "upperCavityDoorInteraction")

                if (productVariantEnum == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN ||
                    productVariantEnum == CookingViewModelFactory.ProductVariantEnum.COMBO
                ) {
                    val lowerVM = CookingViewModelFactory.getSecondaryCavityViewModel()
                    observeTimerCompletion(lowerVM, activity, "lowerCavityTimerCompleted")
                    observePreheatCompletion(lowerVM, activity, "lowerCavityPreheatComplete")
                    observeMeatProbeTemperatureReached(lowerVM, activity, "lowerCavityProbeTemperatureReached")
                    observeUserInstructions(lowerVM, activity, "lowerCavityUserInstruction")
                    observeDoorInteraction(lowerVM, activity, "lowerCavityDoorInteraction")
                }
            }
        }

        private fun observeUserInstructions(viewModel: CookingViewModel, activity: LifecycleOwner, logTag: String) {
            viewModel.recipeExecutionViewModel.userInstruction.observe(activity){
                if(CookingAppUtils.isUserInstructionRequired(viewModel)) {
                    HMILogHelper.Logd(TAG, logTag)
                    playSound(R.raw.attention)
                    // Start inactivity timer only after user instruction completes
                    resetInactivityTimer(viewModel)
                }

            }
        }

        private fun observeTimerCompletion(viewModel: CookingViewModel, activity: LifecycleOwner, logTag: String) {
            viewModel.recipeExecutionViewModel.cookTimerState.observe(activity) {
                if (viewModel.recipeExecutionViewModel.cookTimerState.value == Timer.State.COMPLETED &&
                    viewModel.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.IDLE) {
                    HMILogHelper.Logd(TAG, logTag)
                    HMILogHelper.Logd(TAG, "test after 5 min complete recipe is in idle")
                    CookingAppUtils.setKnobLightWhenPreheatCompleteAndCycleComplete()
                    playSound(if (viewModel.isOfTypeMicrowaveOven) R.raw.long_notification_2 else R.raw.attention)
                    // Start inactivity timer only after cook time completes
                    resetInactivityTimer(viewModel)
                }
            }
        }

        private fun observePreheatCompletion(
            viewModel: CookingViewModel,
            activity: LifecycleOwner,
            microwaveLogTag: String,
            upperOvenTag: String = microwaveLogTag
        ) {
            viewModel.recipeExecutionViewModel.notification.observe(activity) {
                val logTag = if (viewModel.isOfTypeMicrowaveOven) microwaveLogTag else upperOvenTag
                if (viewModel.recipeExecutionViewModel.notification.value?.text == "preheatComplete") {
                    HMILogHelper.Logd(TAG, logTag)
                    HMILogHelper.Logd(TAG, "test after 5 min complete recipe is in idle")
                    CookingAppUtils.setKnobLightWhenPreheatCompleteAndCycleComplete()
                    playSound(if (viewModel.isOfTypeMicrowaveOven) R.raw.long_notification_2 else R.raw.attention)
                }
            }
        }

        private fun observeMeatProbeTemperatureReached(viewModel: CookingViewModel, activity: FragmentActivity, logTag: String) {
            viewModel.recipeExecutionViewModel.targetMeatProbeTemperatureReached.observe(activity) {
                if (viewModel.recipeExecutionViewModel.targetMeatProbeTemperatureReached.value == true) {
                    HMILogHelper.Logd(TAG, logTag)
                    CookingAppUtils.setKnobLightWhenPreheatCompleteAndCycleComplete()
                    playSound(R.raw.long_notification_2)
                    if(CookingAppUtils.getVisibleFragment(activity.supportFragmentManager) is AbstractStatusFragment){
                        HMILogHelper.Logd(logTag, "HANDLER: ${viewModel.cavityName.value} targetMeatProbeTemperatureReached true, AbstractStatusFragment is visible so executeProbeCompleteRunnable to update UI")
                        val statusFragment =  CookingAppUtils.getVisibleFragment(activity.supportFragmentManager) as AbstractStatusFragment
                        val cavityPosition = if(viewModel.isPrimaryCavity) 1 else 2
                        val statusWidget = statusFragment.getStatusWidgetHelper(viewModel)
                        if (statusWidget != null) {
                            // had to call probe complete runnable from MainActivity because targetMeatProbeTemperatureReached is SingleLiveEvent and
                            // if status fragment is visible then MainActivity is getting the event so sound is getting played but UI view was not updating
                            statusFragment.executeProbeCompleteRunnable(cavityPosition, statusWidget.tvRecipeWithTemperature(), statusWidget)
                        }
                    }
                }
            }
        }

        private fun playSound(soundResId: Int) {
            // Save the last sound played for re-triggering if inactive
            lastSoundResId = soundResId
            AudioManagerUtils.registerAudioCallBackListener(ContextProvider.getContext())
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                soundResId,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
        }

        private fun observeDoorInteraction(viewModel: CookingViewModel,  activity: LifecycleOwner, logTag: String) {
            viewModel.doorState.observe(activity) {
                if (viewModel.doorState.value == true) {
                    HMILogHelper.Logd(TAG, logTag)
                    resetInactivityTimer(viewModel)
                }
            }
        }

        private fun createInactivityRunnable(viewModel: CookingViewModel): Runnable {
            return Runnable {
                // Check if the recipe state is still active before playing the sound
                if (viewModel.recipeExecutionViewModel.recipeExecutionState.value != RecipeExecutionState.IDLE) {
                    val cavityName = if (viewModel.isPrimaryCavity) "Upper" else "Lower"
                    HMILogHelper.Logd(TAG, "$cavityName cavity inactivity sound triggered")

                    lastSoundResId?.let {
                        playSound(it)
                    }
                } else {
                    HMILogHelper.Logd(TAG, "InactivityRunnable: ${viewModel.cavityName.value} is IDLE, not playing sound.")
                }
            }
        }

        private fun resetInactivityTimer(viewModel: CookingViewModel) {
            handler.removeCallbacksAndMessages(null) // Cancel any existing timers

            // Create a new Runnable for the specific cavity
            val inactivityRunnable = createInactivityRunnable(viewModel)

            // Post the new inactivity timer
            handler.postDelayed(inactivityRunnable, INACTIVITY_DELAY_MS)
            HMILogHelper.Logd(TAG, "Inactivity timer set for ${viewModel.cavityName.value}")

        }
    }
}
