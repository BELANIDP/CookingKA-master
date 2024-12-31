package core.utils

import android.util.Log

/**
 * <h1>Log Helper</h1>
 * This class is the log helper to extend the Android Log method to include only HMI related information.
 */
object HMILogHelper {
    private const val TAG = "[View] DBG:"
    private const val STACK_TRACE_LEVELS_UP = 5

    /**
     * The private method to get the class name.
     * @return class name
     */
    private val className: String
        get() {
            val className = Thread.currentThread().stackTrace[STACK_TRACE_LEVELS_UP].className
            return className.substring(className.lastIndexOf(".") + 1).trim { it <= ' ' }
        }

    /**
     * The private method to get the method name.
     * @return method name
     */
    private val methodName: String
        get() = Thread.currentThread().stackTrace[STACK_TRACE_LEVELS_UP].methodName

    /**
     * The private method to get the line number.
     * @return line number
     */
    private val lineNumber: String
        get() = Thread.currentThread().stackTrace[STACK_TRACE_LEVELS_UP].lineNumber.toString()

    /**
     * The private method to get the function line.
     * @return function line
     */
    private val functionLine: String
        get() = "$lineNumber $className::$methodName()"

    /**
     * The public method to print out debugging log.
     * @param tag the tag
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Logd(tag: String?, message: String) {
        Log.d(tag, functionLine + " " + message)
    }

    /**
     * The public method to print out warning log.
     * @param tag the tag
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Logw(tag: String?, message: String) {
        Log.w(tag, functionLine + " " + message)
    }

    /**
     * The public method to print out verbose log.
     * @param tag the tag
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Logv(tag: String?, message: String) {
        Log.v(tag, functionLine + " " + message)
    }

    /**
     * The public method to print out info log.
     * @param tag the tag
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Logi(tag: String?, message: String) {
        Log.i(tag, functionLine + " " + message)
    }

    /**
     * The public method to print out error log.
     * @param tag the tag
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Loge(tag: String?, message: String) {
        Log.e(tag, functionLine + " " + message)
    }

    /**
     * The public method to print out debugging log with default TAG.
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Logd(message: String) {
        Log.d(TAG, functionLine + " " + message)
    }

    /**
     * The public method to print out warning log with default TAG.
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Logw(message: String) {
        Log.w(TAG, functionLine + " " + message)
    }

    /**
     * The public method to print out verbose log with default TAG.
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Logv(message: String) {
        Log.v(TAG, functionLine + " " + message)
    }

    /**
     * The public method to print out info log with default TAG.
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Logi(message: String) {
        Log.i(TAG, functionLine + " " + message)
    }

    /**
     * The public method to print out error log with default TAG.
     * @param message the message
     */
    @Suppress("unused", "FunctionName", "ConvertToStringTemplate")
    fun Loge(message: String) {
        Log.e(TAG, functionLine + " " + message)
    }
}
