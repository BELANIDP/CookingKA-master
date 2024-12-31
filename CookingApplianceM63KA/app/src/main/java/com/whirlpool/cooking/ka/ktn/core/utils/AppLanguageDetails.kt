package core.utils

import java.util.Locale


/**
 * File        : core.utils.AppLanguageDetails
 * Brief       : To get language code and name to validate Time & Date format & To update app language through out the application.
 * Author      : Nikki Gharde
 * Created On  : 3.Sep.2024
 * Details     : ENUM Class to Represent app language support
 */
enum class AppLanguageDetails(val languageName: String, val languageCode: String) {
    English("english", AppConstants.DEFAULT_LANGUAGE_ENGLISH_CODE),
    French("french_canadian", AppConstants.DEFAULT_LANGUAGE_CANADIAN_FRENCH_CODE),
    Spanish("spanish", AppConstants.DEFAULT_LANGUAGE_SPANISH_CODE);

    companion object {
        /**
         * To get the language Code by given App Language Name eg: ENGLISH, FRENCH, SPANISH
         *
         * @param languageName name of the local app language
         * @return language code eg: en, es, fr
         */
        fun getLanguageCodeByName(languageName: String): String {
            for (toggleStatusUpdate in values()) {
                if (toggleStatusUpdate.languageName == languageName) {
                    return toggleStatusUpdate.languageCode
                }
            }
            return AppConstants.EMPTY_STRING
        }

        /**
         * To get the language Name by given App Language Code eg: en, es, fr
         *
         * @param languageCode language code
         * @return language code eg: ENGLISH, FRENCH, SPANISH
         */
        fun getLanguageNameByCode(languageCode: String): String {
            for (toggleStatusUpdate in values()) {
                if (toggleStatusUpdate.languageCode == languageCode) {
                    return toggleStatusUpdate.languageName
                }
            }
            return AppConstants.EMPTY_STRING
        }

        /**
         * To get the Locale for internal process by given App Language Code eg: en, es, fr
         *
         * @param languageCode language code
         * @return Locale Constant eg: ENGLISH, FRENCH, SPANISH
         */
        fun getLanguageLocale(languageCode: String?): Locale {
            return when (languageCode) {
                AppConstants.DEFAULT_LANGUAGE_SPANISH_CODE -> Locale(AppConstants.DEFAULT_LANGUAGE_SPANISH_CODE,"ES")
                AppConstants.DEFAULT_LANGUAGE_CANADIAN_FRENCH_CODE -> Locale.CANADA_FRENCH
                AppConstants.DEFAULT_LANGUAGE_ENGLISH_CODE -> Locale.ENGLISH
                else -> Locale.ENGLISH
            }
        }
    }
}
