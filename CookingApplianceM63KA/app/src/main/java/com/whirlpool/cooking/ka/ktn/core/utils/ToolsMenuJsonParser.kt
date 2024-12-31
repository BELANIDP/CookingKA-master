package core.utils

import android.annotation.SuppressLint
import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class ToolsMenuJsonParser {
    private var jsonObject: JSONObject? = null

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
                    HMILogHelper.Logd("Successful in reading package")
                    jsonObject = JSONObject(String(buffer))
                    true
                } else {
                    HMILogHelper.Loge("Failed to read file")
                    false
                }
            } catch (e: IOException) {
                HMILogHelper.Loge("Failed to read file" + e.message)
                e.printStackTrace()
                return false
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else {
            HMILogHelper.Loge("File not present")
        }
        return false
    }

    /**
     * This gives Strings List from JSON
     * TODO: This function is messed up!! Either return a Array of arrays for sub menus and don't try to fix it upstream.
     * @param key item selected
     * @return list of mainToolsMenuItems
     */
    fun parseToolsItemsJsonForKey(key: String): ArrayList<String>? {
        val mainToolsMenuItems = ArrayList<String>()
        try {
            // Create List of items
            val jsonArrayMenuItems =
                jsonObject!!.getJSONObject(ToolsMenuJsonKeys.JSON_KEY_PRESENTATION_TREE)
                    .getJSONArray(key)
            for (i in 0 until jsonArrayMenuItems.length()) {
                mainToolsMenuItems.add(jsonArrayMenuItems.getJSONObject(i)[ToolsMenuJsonKeys.JSON_KEY_ITEM_ID].toString())
            }
            return mainToolsMenuItems
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }
}