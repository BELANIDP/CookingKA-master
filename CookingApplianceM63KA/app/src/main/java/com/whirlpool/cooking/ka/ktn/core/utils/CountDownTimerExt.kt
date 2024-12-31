import android.os.CountDownTimer
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

abstract class CountDownTimerExt(var mMillisInFuture: Long, var mInterval: Long) {

    private var countDownTimer: CountDownTimer? = null
    private val remainingTime: AtomicLong = AtomicLong(mMillisInFuture)  // Thread-safe storage
    private val isTimerPaused = AtomicBoolean(true)
    private val isTimerCanceled = AtomicBoolean(false) // New flag to track cancellation

    init {
        // Initialize the remaining time with the original value
        remainingTime.set(mMillisInFuture)
    }

    @Synchronized
// Start or resume the timer
    fun start() {
        if (isTimerCanceled.get()) {
            // If the timer was canceled, reset it before starting again
            isTimerCanceled.set(false)
            remainingTime.set(mMillisInFuture)  // Reset the remaining time to the original value
        }

        if (isTimerPaused.compareAndSet(true, false)) {
            countDownTimer = object : CountDownTimer(remainingTime.get(), mInterval) {
                override fun onFinish() {
                    onTimerFinish()
                }

                override fun onTick(millisUntilFinished: Long) {
                    remainingTime.set(millisUntilFinished)
                    onTimerTick(millisUntilFinished)
                }
            }
            countDownTimer?.start()
        }
    }

    // Pause the timer and store the remaining time
    fun pause() {
        if (!isTimerPaused.get() && !isTimerCanceled.get()) {
            countDownTimer?.cancel() // Stop the timer
            isTimerPaused.set(true)
        }
    }

    // Restart the timer from the original total duration
    fun restart() {
        if (isTimerPaused.get() || isTimerCanceled.get()) {
            remainingTime.set(mMillisInFuture) // Reset the timer duration
            start() // Restart the timer
        }
    }

    // Resume the timer from the last remaining time
    fun resume() {
        if (isTimerPaused.get() && !isTimerCanceled.get()) {
            start() // Resume from the last remaining time
        }
    }

    // Method to cancel the timer and reset the remaining time to the original duration
    fun cancelTimer() {
        countDownTimer?.cancel()  // Stop the timer if it's running
        isTimerPaused.set(true)   // Reset the state to "paused"
        isTimerCanceled.set(true) // Mark the timer as canceled
        remainingTime.set(mMillisInFuture)  // Reset to the original full duration
    }

    // Method to check if the timer is active (running)
    fun isTimerActive(): Boolean {
        return !isTimerPaused.get() && remainingTime.get() > 0 && countDownTimer != null && !isTimerCanceled.get()
    }

    // Method to check if the timer is paused
    fun isTimerPaused(): Boolean {
        return isTimerPaused.get() && !isTimerCanceled.get()
    }

    // Method to check if the timer is canceled
    fun isTimerCanceled(): Boolean {
        return isTimerCanceled.get()
    }

    // Method to get the remaining time in milliseconds
    fun getRemainingTime(): Long {
        return remainingTime.get()
    }

    // External method to set the remaining time
    // This should be called from your persistence module when restoring the time
    fun setRemainingTime(time: Long) {
        remainingTime.set(time)
    }

    // Abstract methods to be implemented by subclasses
    abstract fun onTimerTick(millisUntilFinished: Long)
    abstract fun onTimerFinish()
}
