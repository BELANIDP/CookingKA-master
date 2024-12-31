package core.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceUtils(context: Context, prefsName: String) {
    private var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }
    companion object{
        private var instance: SharedPreferenceUtils? = null
        fun getInstance(context: Context, prefsName: String): SharedPreferenceUtils? {
            if(instance == null) {
                synchronized(SharedPreferenceUtils::class.java) {
                    instance = SharedPreferenceUtils(context, prefsName)
                }
            }
            return instance
        }
    }

    /**
     * To save save key value
     *
     * @param key   unique key id
     * @param value value to be stored with given key value
     */
    @Synchronized
    fun saveValue(key: String?, value: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * To save save key value
     *
     * @param key          unique key id to get stored Shared preference data
     * @param defaultValue if the stored key value is not present need to send the alternate default value
     */
    @Synchronized
    fun getValue(key: String?, defaultValue: String?): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    /**
     * to remove given key from the share preference
     *
     * @param key to removed from Shared preference
     */
    fun remove(key: String?): Boolean {
        return sharedPreferences.edit().remove(key).commit()
    }

    /**
     * to clear shared preference data
     *
     * @return true/false whether shared preference cleared or not
     */
    fun clear(): Boolean {
        return sharedPreferences.edit().clear().commit()
    }
}