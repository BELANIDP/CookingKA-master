package core.utils

import android.annotation.SuppressLint
import android.content.Context
import com.whirlpool.hmi.cooking.utils.FaultCategory
import com.whirlpool.hmi.cooking.utils.FaultSubCategory
import core.utils.faultcodesutils.FaultCodesJsonKeys
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class NotificationJsonParser {
    private lateinit var jsonObject: JSONObject

    /**
     * This loads JSON from raw
     * @param context
     * @param fileName
     * @return
     */
    @SuppressLint("DiscouragedApi")
    fun loadJson(context: Context, fileName: String?): Boolean {
        val resIdentifier = context.resources
            .getIdentifier(
                fileName, "raw",
                context.packageName
            )
        if (0 != resIdentifier) {
            val inputStream = context.resources.openRawResource(resIdentifier)
            // Load Tools JSON File
            val buffer: ByteArray
            try {
                buffer = ByteArray(inputStream.available())
                val readLength = inputStream.read(buffer)
                inputStream.close()
                return if (readLength > 0) {
                    HMILogHelper.Logd("Successful in reading Notification package")
                    jsonObject = JSONObject(String(buffer))
                    true
                } else {
                    HMILogHelper.Loge("Failed to read Notification file")
                    false
                }
            } catch (e: IOException) {
                HMILogHelper.Loge("Failed to read Notification file" + e.message)
                e.printStackTrace()
                return false
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            HMILogHelper.Loge("Notification File not present")
        }
        return false
    }

    /**
     * This method parse the fault.json file and call the method to map the fault codes
     */
    fun parseNotification() = try {
        if (jsonObject.length() > 0) {
            val keys: Iterator<*> = jsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next() as String
                if (jsonObject[key] is JSONObject) {
                    val faultDetails = jsonObject.getJSONObject(key)
                    val category = faultDetails.getInt(FaultCodesJsonKeys.FAULT_CATEGORY)
                    val subCategory = faultDetails.getInt(FaultCodesJsonKeys.FAULT_SUB_CATEGORY)
                    val faultPriorities = ArrayList<Int>()
                    faultPriorities.add(faultDetails.optInt(FaultCodesJsonKeys.FAULT_DOUBLE_OVEN_PRIORITY))
                    faultPriorities.add(faultDetails.optInt(FaultCodesJsonKeys.FAULT_COMBO_OVEN_PRIORITY))
                    HMILogHelper.Logd(
                        "Fault Code $key Fault category : " + FaultCategory.getById(
                            category
                        ) + " Fault subcategory : " + FaultSubCategory.getById(subCategory)
                    )
                    //filterToCategory(category, subCategory, key, faultPriorities)
                }
            }
        } else {
            HMILogHelper.Loge("Fault JSON Object is empty")
        }
    } catch (e: JSONException) {
        e.message?.let { HMILogHelper.Loge(it) }
        e.printStackTrace()
    }

    fun getNotificationId(notification: String): Int {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getInt(NotificationJsonKeys.NOTIFICATION_JSON_ID)
    }

    fun getNotificationPriority(notification: String): Int {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getInt(NotificationJsonKeys.NOTIFICATION_JSON_PRIORITY)
    }
    fun getNotificationDuration(notification: String): Int {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getInt(NotificationJsonKeys.NOTIFICATION_JSON_MAX_DURATION)
    }

    fun getNotificationDurationType(notification: String): String {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getString(NotificationJsonKeys.NOTIFICATION_JSON_MAX_DURATION_TYPE)
    }

    fun getNotificationType(notification: String): String {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getString(NotificationJsonKeys.NOTIFICATION_JSON_TYPE)
    }

    fun getNotificationDurationValue(notification: String): Int {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getInt(NotificationJsonKeys.NOTIFICATION_JSON_MAX_DURATION)
    }

    fun getNotificationNoInteractionDismiss(notification: String): Boolean {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getBoolean(NotificationJsonKeys.NOTIFICATION_JSON_NO_INTERACTION_DISMISS)
    }

    fun getNotificationGoesToNotificationCenter(notification: String): Boolean {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getBoolean(NotificationJsonKeys.NOTIFICATION_JSON_GOES_TO_NC)
    }

    fun getNotificationDuplicateAllowedInNotificationCenter(notification: String): Boolean {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getBoolean(NotificationJsonKeys.NOTIFICATION_JSON_DUPLICATE_ALLOWED_IN_NC)
    }

    fun getNotificationIsHistoryNeeded(notification: String): Boolean {
        val notificationObject = jsonObject.getJSONObject(notification)
        return notificationObject.getBoolean(NotificationJsonKeys.NOTIFICATION_JSON_IS_HISTORY_NEEDED)
    }
}