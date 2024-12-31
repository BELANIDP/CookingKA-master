package core.utils

import android.content.Context
import android.net.Uri

object UriUtils {

    private const val ANDROID_RESOURCE_PATH: String = "android.resource://"
    private const val BACKSLASH: String = "/"

    /**
     * @param context: context of the implementing class.
     * @param videoResourceName: raw resource file
     * @return Uri of absolute location of the raw file
     */
    fun getUriFromRawResource(context: Context, videoResourceName: Int): Uri? {
        return try {
            Uri.parse(ANDROID_RESOURCE_PATH + context.packageName + BACKSLASH + videoResourceName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}