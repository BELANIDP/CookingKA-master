package core.utils

import java.text.SimpleDateFormat
import java.util.Locale

object AppConstants {
    const val POPUP_TAG_JET_START = "jetStartForA30"
    const val POPUP_TAG_STATUS_DOOR_OPEN = "closeDoorToContinueAction"
    const val MAX_COUNT_PROBE_TIMER_IN_SECONDS: Long = 12 * 3600//12hr

    @Suppress("unused")
    const val TEXT_ASSISTED = "assisted"

    const val ANDROID_RESOURCE_PATH: String = "android.resource://"

    const val SLEEP_MODE = "SLEEP MODE"
    const val TEXT_TIME = "Time"
    const val FAULT_AS_NONE = "NONE"
    const val DEMO_CODE = "2345"
    const val CURRENT_DISPLAYED_COMBO_FAULT_PRIORITY = "ComboFaultPriority"
    const val CURRENT_DISPLAYED_DOUBLE_FAULT_PRIORITY = "DoubleFaultPriority"
    const val SHARED_PREF_DB_NAME = "SHARED_PREF_M63_KA_LOCAL_DB"
    const val RECIPE_CANCEL_DURATION_15_SEC: Int = 15
    const val RECIPE_TIMEOUT_COOKING_COMPLETE_10_MINUTES: Int = 10
    const val ADD_COOK_TIME_FIVE_MINUTES: Long = 300
    const val CANCEL_BUTTON_PRESS_DURING_SELF_CLEAN = "cancelButtonPressDuringSelfClean"
    const val FALSE_CONSTANT = "false"
    const val TRUE_CONSTANT = "true"
    const val ADD_COOK_TIME_ONE_MINUTE: Long = 60 * 1
    const val SPACE_CONSTANT: String = " "
    const val BOTH_SIDE_SPACE_WITH_DASH_CONSTANT: String = " - "
    const val ADD_COOK_TIME_TWO_MINUTE: Long = 60 * 2
    const val ADD_COOK_TIME_30_SECOND: Long = 30
    const val ADD_COOK_TIME_TEN_MINUTE: Long = 600
    const val PREHEAT_MAX_TIME: Long = 600000
    const val MAX_PROGRESS_VALUE: Int = 100
    const val DEFAULT_SELECTED_TEMP = -1
    const val EMPTY_STRING = ""
    const val DOT_DECIMAL = "."
    const val STRING_REGREX_ZERO = "0*$"
    const val STRING_REGREX_DECIMAL = "\\.$"
    const val NEXT_LINE: String = "\n"
    const val DOUBLE_NEXT_LINE: String = "\n\n"
    const val TRIPLE_NEXT_LINE: String = "\n\n\n"
    const val DEFAULT_LEVEL = "0"
    const val DEGREE_SYMBOL = "°"
    const val MINUS_SYMBOL = "-"
    const val SABBATH_FAHRENHEIT_TEMPERATURE_ALLOWED_VALUE = 25
    const val SABBATH_CELSIUS_TEMPERATURE_ALLOWED_VALUE = 5
    const val PLUS_SYMBOL = "+"
    const val VERTICAL_BAR = "|"
    const val SYMBOL_FORWARD_SLASH = "/"
    const val DEFAULT_DOUBLE_ZERO = "00"
    const val DEFAULT_CLOCK_TIME = "0000"
    const val DEFAULT_CLOCK_DATE = "010121"
    const val DEFAULT_TRIPLE_ZERO = "000"
    const val DEFAULT_COOK_TIME = "000000"
    const val DEFAULT_MAX_COOK_TIME = 43200
    const val DEFAULT_MAX_COOK_TIME_IN_MAGNETRON = 5400
    const val DEFAULT_COOK_TIME_MICROWAVE = "0000"
    const val CYCLE_END_TIME = "00:00"
    const val CYCLE_END_TIME_HOUR = "00:00:00"
    const val PERCENTAGE_SYMBOL = "%"
    const val KEY_TIME = "Time"
    const val KEY_DATE = "Date"
    const val KEY_DATE_FORMAT = "DateFormat"
    const val KEY_TIME_FORMAT = "TimeFormat"
    const val RESOURCE_TYPE_STRING = "string"
    const val RESOURCE_TYPE_RAW = "raw"
    const val BUNDLE_NEXT_MANDATORY_OPTION_INDEX = "nextMandatoryOptionIndex"
    const val NAVIGATION_FROM_CREATE_FAV = "navigationFromCreateFavorites"
    const val NAVIGATION_FROM_EXISTING_FAV = "navigationFromExistingFavorites"
    const val POP_UP_DISMISS = "dismiss"
    const val DEFAULT_FAVORITE_NAME = "Favorite [1]"
    const val CONTROL_UNLOCK_FROM_POPUP = "controlUnlockFromPopup"
    const val TAG_STEAM_POPUP = "steamPopup"
    const val NAVIGATION_FROM_MORE_MODES_PROBES = "navigationFromMoreModes"

    //Notification constants
    const val NAVIGATION_FROM_NOTIFICATION = "navigationFromNotification"
    const val NAVIGATION_NO_OF_USES = "NumberOfUses"
    const val NAVIGATION_NO_OF_USES_OF_SWIPE_DOWN = "NumberOfUsesOfSwipeDown"
    const val NAVIGATION_ACTIVE_TIP_NUMBER = "ActiveTipNumber"
    const val NOTIFICATION_CENTER = "ActiveNotification"
    const val NOTIFICATION_QUEUE = "NotificationQueue"
    const val NOTIFICATION_TYPE_TIP = "tip"
    const val NOTIFICATION_TYPE_ACTIONABLE = "actionable"
    const val NOTIFICATION_NO_INTERACTION_REMAINING_TIME = "NotificationNoUserInteractionTimer"
    const val NOTIFICATION_DAY_COUNTER_REMAINING_TIME = "NotificationDayCounterTimer"
    const val NOTIFICATION_TIP_TRICK_STATUS = "NotificationTipAndTrickStatus"
    const val NOTIFICATION_TIP_TRICK_DAYS = "NotificationTipAndTrickDays"
    const val NOTIFICATION_NO_INTERACTION_TIMER = 3600000L
    const val NOTIFICATION_NO_INTERACTION_TIMER_TICK = 300000L
    const val NOTIFICATION_DAY_COUNTER_TIMER = 86400000L
    const val NOTIFICATION_DAY_COUNTER_TIMER_TICK = 3600000L

    @Suppress("unused")
    const val A20_BUILD_VARIANT = "a20_kitchenaid"

    val twentyFourHrFormat = SimpleDateFormat("HHmm", Locale.ENGLISH)
    val twelveHrFormat = SimpleDateFormat("hhmma", Locale.ENGLISH)

    const val TEMPERATURE_CALIBRATION = "temperature_calibration"

    const val LIST_ITEM_VIEW_PADDING = 32
    const val LIST_ITEM_VIEW_PADDING_EXTRA = 64
    const val WEIGHT_STRING_OUNCES = "ounces"

    @Suppress("unused")
    const val WEIGHT_STRING_LBS = "lbs"

    const val WEIGHT_STRING_GRAMS = "grams"
    const val WEIGHT_STRING_KILOGRAMS = "kilograms"

    /*Key board*/
    @Suppress("unused")
    const val DUMMY_KEYBOARD_CHARACTER_LIMIT = 17

    /**
     * Represents the length of hours for count down spannable text.
     * Value: 10.
     */
    const val COUNT_DOWN_SPANNABLE_TEXT_HOUR_LENGTH_10 = 10

    /**
     * Represents the length of minutes for count down spannable text (60 minutes).
     * Value: 60.
     */
    const val COUNT_DOWN_SPANNABLE_TEXT_MINUTE_LENGTH_60 = 60

    /**
     * Represents the length of minutes for count down spannable text (10 minutes).
     * Value: 10.
     */
    const val COUNT_DOWN_SPANNABLE_TEXT_MINUTE_LENGTH_10 = 10

    /**
     * Represents the length of seconds for count down spannable text (10 seconds).
     * Value: 10.
     */
    const val COUNT_DOWN_SPANNABLE_TEXT_SECOND_LENGTH_10 = 10

    @Suppress("unused")
    const val UPPER_OVEN = "upper_oven"
    @Suppress("unused")
    const val CAVITY_SELECTION = "cavity_selection_"
    const val TEXT_TEMP = "text_"
    const val POPUP_DISMISS_DELAY = 500
    const val POPUP_KNOB_DISMISS_DELAY = 300

    @Suppress("unused")
    const val IS_CLEAN_BTN_PRESSED = "is_clean_btn_pressed"
    const val RECIPE_PIZZA = "pizza"
    const val RECIPE_REHEAT = "reheat"
    const val RECIPE_CONVECT = "convect"
    const val RECIPE_PROBE = "probe"
    const val RECIPE_PIZZA_FROZEN_COOK = "pizzaFrozenCook3"
    const val RECIPE_MORE_MODES = "moreModes"
    const val RECIPE_SLOW_ROAST = "slowRoast"
    const val RECIPE_CONVECT_SLOW_ROAST = "convectSlowRoast"
    const val RECIPE_INSTRUCTION_SLOW_ROAST = "convectSlowRoastMedium"
    const val RECIPE_INSTRUCTION_SABBATH_MODE = "sabbathMode"
    const val RECIPE_INSTRUCTION_SABBATH_BAKE = "sabbathBake"
    const val RECIPE_MICROWAVE = "microwave"
    const val RECIPE_BAKE = "bake"
    const val QUICK_START = "quickStart"
    const val RECIPE_BAKE_JET_START_TEMP_FAHRENHEIT: Float = 350f
    const val RECIPE_BAKE_JET_START_TEMP_CELSIUS: Float = 175f
    const val DEFAULT_DATE_VALUE_FORMAT = "%02d"

    /****** For User Instruction Key ***************/
    const val INSERT_FOOD: String = "insertFood"
    const val TURN_FOOD: String = "turnFood"
    const val ADD_INGREDIENT: String = "addIngredient"
    const val FLIP_FOOD: String = "flipFood"
    const val STIR_FOOD: String = "stirFood"
    /****** For User Instruction Key ***************/

    const val KNOB_SELECTION_TIME_OUT = 10000
    const val KNOB_SELECTION_TIME_OUT_TEN_MIN = 600000
    const val CLOCK_SCREEN_ACTION_SHEET_TIME_OUT_OVEN_LIGHT = 4000
    const val CLOCK_SCREEN_ACTION_SHEET_TIME_OUT = 10000
    @Suppress("unused")
    const val CLOCK_SCREEN_ACTION_SHEET_ANIMATION_TIME = 250

    /**
     * Represents the HMI panel buttons light intensity (10 seconds).
     * Value: 0 to 100.
     */
    @Suppress("unused")
    const val BUTTON_LIGHT_OFF = 0
    const val BUTTON_LIGHT_ON = 100

    /**
     * Represent button text size for more options popup
     */
    const val MORE_OPTIONS_POPUP_LAST_TEXT_BUTTON_TEXT_SIZE = 32f
    const val SERVICE_DIAGNOSTICS_TEXT_BUTTON_TEXT_SIZE = 32f
    const val MORE_OPTIONS_POPUP_TEXT_BUTTON_TEXT_SIZE = 28f

    const val DOOR_OPEN_POPUP = "doorOpenPopup"
    const val DOOR_LOCK_STATE = "doorLockState"
    const val LOCKING_FAILED = "failedToLockDoor"

    const val PRIMARY_CAVITY_KEY = "primaryCavity"
    /**
     * Secondary Cavity Key.
     */
    const val SECONDARY_CAVITY_KEY = "secondaryCavity"

    const val FAULT_ERROR_NAME_TAG = "text_fault_name_"
    const val FAULT_ERROR_DESCRIPTION_TAG = "text_fault_description_"
    const val FAULT_ERROR_INSTRUCTIONS_TAG = "text_fault_recovery_instructions_"
    const val TEXT_COMMON_MESSAGE_FAULT_NAME = "text_fault_name_common_"

    /**
     * SettingsKey to compare with capability files
     */
    const val KEY_WHR_TEMPERATURE_PROBE_REQUIRED = "whrTemperatureProbeRequired"
    const val KEY_WHR_PRE_START_CONFIGURATION = "whrPreStartConfiguration"
    const val KEY_WHR_START_NOT_ALLOWED = "whrStartNotAllowed"
    var TEMPERATURE_UNIT_SELECTED = 0
    const val TEMPERATURE_UNIT_CONSTANT: String = "TEMPERATURE_UNIT"
    const val TEMPERATURE_UNIT_CELSIUS: Int = 1
    const val TEMPERATURE_UNIT_FAHRENHEIT: Int = 2
    const val TEMPERATURE_UNIT_CELSIUS_FAHRENHEIT: Int = 3
    const val CELSIUS_CONSTANT: String = "celsius"

    /**
     * knob rotation counter
     * */
    const val KNOB_COUNTER_ZERO = 0
    const val KNOB_COUNTER_ONE = 1
    const val KNOB_COUNTER_TWO = 2
    const val KNOB_COUNTER_THREE = 3

    /**
     * knob position
     * */
    const val KNOB_POSITION_0 = 0
    const val KNOB_POSITION_1 = 1
    const val KNOB_POSITION_2 = 2
    const val KNOB_POSITION_3 = 3
    const val KNOB_POSITION_4 = 4
    const val KNOB_POSITION_5 = 5
    const val KNOB_POSITION_6 = 6
    const val KNOB_POSITION_7 = 7

    //    strings.xml Localization Constant
    /*Instruction screen*/
    const val RESOURCE_TYPE_DRAWABLE: String = "drawable"
    const val EMPTY_SPACE: String = " "
    const val TEXT_POPUP = "_popup"
    const val TEXT_SMALL: String = "_small"
    const val TEXT_TALL: String = "_tall"
    const val IMAGE_ACCESSORY_GUIDE: String = "_ag"
    const val TEXT_SQUARE: String = "_square"
    const val IMAGE_SIZE_200: Int = 200
    const val SERVICE_SUPPORT_QR_CODE: String = "service_support_qr_code"
    const val CONNECTED_APP_QR_CODE: String = "connected_app_qr_code"
    const val TEXT_LARGE: String = "_large"
    const val RACK: String = "rack_"
    const val RACK_POSITION_DEFAULT: String = "rack_1"
    const val PROBE: String = "probe"
    const val TEXT_INFORMATION = "text_information_"
    const val TEXT_INFORMATION_MWO = "text_information_mwo_"
    const val TEXT_DESCRIPTION = "text_description_"
    const val TEXT_PREHEATING_TYPE = "text_preheat_type_"
    const val TEXT_HEADER = "text_header_"
    @Suppress("unused")
    const val TEXT_RESOURCE_FOOD_CATEGORY = "text_food_category_"
    const val TEXT_DONENESS_TILE = "text_tile_decision_large_"
    const val TEXT_MODE = "text_mode_"

    //for assisted cooking guide
    const val MW_RECIPE: String = "_MW"
    const val TEXT_COMMON_MESSAGE_ACCESSORY_GUIDE: String = "assisted_accessory_guide_common_message_"
    const val TEXT_ACCESSORY_GUIDE = "_accessory_guide"
    const val TEXT_COMMON_MESSAGE_COOK_GUIDE: String = "assisted_cook_guide_common_message_"
    const val TEXT_COOK_GUIDE = "_cook_guide"
    const val TEXT_PROBE_GUIDE = "_probe_guide"
    const val TEXT_COMMON_MESSAGE_UTENSIL_GUIDE = "common_message_utensil_guide_"
    const val TEXT_UTENSIL_GUIDE = "_utensil_guide"
    const val BROWNING_CONSTANT: String = "browning"
    const val ADD_BROWNING_CONSTANT: String = "addBrowning"
    const val EXTRA_BROWNING_CONSTANT: String = "extraBrowning"

    /**
     * Scroll Animation Delay
     */
    const val SMOOTH_SCROLL_ANIM_DELAY = 100L

    const val MIN_KNOB_POSITION = 1
    const val DIALOG_KNOB_SIZE = 2
    const val CAVITY_SELECTION_KNOB_SIZE = 2

    /**
     * Connectivity : Start
     */
    const val NETWORK_NAME = "Network Name: "
    @Suppress("unused")
    const val SAID = "SAID:"
    @Suppress("unused")
    const val PIN = "PIN: "
    const val DNS = "DNS: "
    const val WIFI5G = "WIFI5G: "
    const val RSSI = "RSSI: "
    const val IP = "IP: "
    const val ROUTER_IP = "Router IP: "
    const val SUBENET_MASK = "Subnet Mask: "
    const val DBM = "dBm"

    const val AWS_IOT_END_POINT = "a266nrl09ynttb-ats.iot.us-east-2.amazonaws.com"

    @Suppress("ConstPropertyName")
    const val rootCa = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDQTCCAimgAwIBAgITBmyfz5m/jAo54vB4ikPmljZbyjANBgkqhkiG9w0BAQsF\n" +
            "ADA5MQswCQYDVQQGEwJVUzEPMA0GA1UEChMGQW1hem9uMRkwFwYDVQQDExBBbWF6\n" +
            "b24gUm9vdCBDQSAxMB4XDTE1MDUyNjAwMDAwMFoXDTM4MDExNzAwMDAwMFowOTEL\n" +
            "MAkGA1UEBhMCVVMxDzANBgNVBAoTBkFtYXpvbjEZMBcGA1UEAxMQQW1hem9uIFJv\n" +
            "b3QgQ0EgMTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALJ4gHHKeNXj\n" +
            "ca9HgFB0fW7Y14h29Jlo91ghYPl0hAEvrAIthtOgQ3pOsqTQNroBvo3bSMgHFzZM\n" +
            "9O6II8c+6zf1tRn4SWiw3te5djgdYZ6k/oI2peVKVuRF4fn9tBb6dNqcmzU5L/qw\n" +
            "IFAGbHrQgLKm+a/sRxmPUDgH3KKHOVj4utWp+UhnMJbulHheb4mjUcAwhmahRWa6\n" +
            "VOujw5H5SNz/0egwLX0tdHA114gk957EWW67c4cX8jJGKLhD+rcdqsq08p8kDi1L\n" +
            "93FcXmn/6pUCyziKrlA4b9v7LWIbxcceVOF34GfID5yHI9Y/QCB/IIDEgEw+OyQm\n" +
            "jgSubJrIqg0CAwEAAaNCMEAwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMC\n" +
            "AYYwHQYDVR0OBBYEFIQYzIU07LwMlJQuCFmcx7IQTgoIMA0GCSqGSIb3DQEBCwUA\n" +
            "A4IBAQCY8jdaQZChGsV2USggNiMOruYou6r4lK5IpDB/G/wkjUu0yKGX9rbxenDI\n" +
            "U5PMCCjjmCXPI6T53iHTfIUJrU6adTrCC2qJeHZERxhlbI1Bjjt/msv0tadQ1wUs\n" +
            "N+gDS63pYaACbvXy8MWy7Vu33PqUXHeeE6V/Uq2V8viTO96LXFvKWlJbYK8U90vv\n" +
            "o/ufQJVtMVT8QtPHRh8jrdkPSHCa2XV4cdFyQzR1bldZwgJcJmApzyMZFo6IQ6XU\n" +
            "5MsI+yMRQ+hDKXJioaldXgjUkK642M4UwtBV8ob2xJNDd2ZhwLnoQdeXeGADbkpy\n" +
            "rqXRfboQnoZsG4q5WTP468SQvvG5\n" +
            "-----END CERTIFICATE-----"

    @Suppress("ConstPropertyName")
    const val provCertificate = "-----BEGIN CERTIFICATE-----\n" +
            "MIIDWTCCAkGgAwIBAgIUFtaSjLpDpEUa3DDsKs4W/UUpVSEwDQYJKoZIhvcNAQEL\n" +
            "BQAwTTFLMEkGA1UECwxCQW1hem9uIFdlYiBTZXJ2aWNlcyBPPUFtYXpvbi5jb20g\n" +
            "SW5jLiBMPVNlYXR0bGUgU1Q9V2FzaGluZ3RvbiBDPVVTMB4XDTIyMDUxOTE3NDEw\n" +
            "OFoXDTQ5MTIzMTIzNTk1OVowHjEcMBoGA1UEAwwTQVdTIElvVCBDZXJ0aWZpY2F0\n" +
            "ZTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAJ2R5hQ+pNJmqz9hwmi5\n" +
            "hPgOLDEKdelCOnSG7JuN/Jnxjd8ucUSnT/fnJI+I/1PUuZPp+4gk8CPUizr0aEo/\n" +
            "BI6wTrgCRQDG27JrX+Toh3DVzaBOFIceF37p62Mx6+renSqqxB9rlN0nhxIixfnL\n" +
            "OrmGcu6RjdUlIXz3NCTjIRWI7H/ti86KXj+FVOzxcEmRqN4kBi5OuPPG0LpwM2wj\n" +
            "wCJDn86DK2tyNfIsoUgNdF8/xhLu/01+j163wkvNw1Swr54JLgedVjOmHuo+tXnc\n" +
            "uNMsYZItH8obgsabbV3iIhwsHyJkJEpLJQde6iJ5q5tdiQDc3V6hGzKi6nrtkuoV\n" +
            "ZBkCAwEAAaNgMF4wHwYDVR0jBBgwFoAUyb9VCvhXlwpZ04WvLLsKBvXo3MowHQYD\n" +
            "VR0OBBYEFCJobUrF+36rfKNBqAKVyd0D2h27MAwGA1UdEwEB/wQCMAAwDgYDVR0P\n" +
            "AQH/BAQDAgeAMA0GCSqGSIb3DQEBCwUAA4IBAQClAgjR58O6Zkv3e8dld+yPI0WE\n" +
            "CFMHIV5lfXukX9FC+WpTVNHbmgHhBpJRfofIKX+CD7AqnMvgvBylUbVqZxy1m0rL\n" +
            "QLVYcXXMyw4EcnzXAn6mUMuxaUbNb/Dw+bMhHJFZ1Z5L7tHwZvtO+ue+63f0vnZ0\n" +
            "slhvxaLprpWHWYVaSgF9gXdq4vhAqIRZ2awZvXgfVNti4szAFGj2ym+iwavVwLhE\n" +
            "xes9o+/tCwRJipdALaLNSFp4hyw10dRR8M1nN+EKochVMVlBeulF7dhRIVtlikq/\n" +
            "oeyGrb0hl8tyxKMt/Ai1rilQyfzbcqsxP4aZfkryVGBfKnVz9rgqBIyCKP8X\n" +
            "-----END CERTIFICATE-----"

    @Suppress("ConstPropertyName")
    const val provPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEogIBAAKCAQEAnZHmFD6k0marP2HCaLmE+A4sMQp16UI6dIbsm438mfGN3y5x\n" +
            "RKdP9+ckj4j/U9S5k+n7iCTwI9SLOvRoSj8EjrBOuAJFAMbbsmtf5OiHcNXNoE4U\n" +
            "hx4XfunrYzHr6t6dKqrEH2uU3SeHEiLF+cs6uYZy7pGN1SUhfPc0JOMhFYjsf+2L\n" +
            "zopeP4VU7PFwSZGo3iQGLk6488bQunAzbCPAIkOfzoMra3I18iyhSA10Xz/GEu7/\n" +
            "TX6PXrfCS83DVLCvngkuB51WM6Ye6j61edy40yxhki0fyhuCxpttXeIiHCwfImQk\n" +
            "SkslB17qInmrm12JANzdXqEbMqLqeu2S6hVkGQIDAQABAoIBAEhI80TcTB6drPfh\n" +
            "sB785KwrTEifJOKCWHC2qPrfkz9IGi6Fjr7RBpWSeypBzeg2UYAyugqF74hwbAL6\n" +
            "tpFBAaU36pTtrZwingzmx1Iq/l9cJf3hc/dcbajyROL3tuPDhlRZjKlhYCRoisVe\n" +
            "IHZsrh8IN9eL/IYIh6ldepVlgPrwYk0n6zez69L6eCnT2lcsJApWozF8V1kZA0w2\n" +
            "VPSaj4aIg4WZTLneKHwcYgGJBZSvfojkmUCLC8u6xKApe+fSuh3zWVmPmjW4iFb3\n" +
            "ssk/551v7hHOPiYix6NAHDIwLsGOKN7wvFUT/0OoP1xg1em0FhQ8uCcZ0sgcheQJ\n" +
            "qrumuOUCgYEAywbFpkOSmmsLhRvzmjLb9cBvUy2e+kK1Vhrb4NhuBmEx/rjTcdin\n" +
            "ySUzRlqSdxWn49rkXbjn0uR/IQabZQ75vvLJXCr3kFTGeI0dsZ3MKSx8lB8ia8c9\n" +
            "oABwp2kkx92cmJkZpNXWmR6ssP5/z782XktbKNAmYwXnLcVE6PZQr4sCgYEAxq7X\n" +
            "HwwNY2ZXgQ0ZFR2ceg3Ab81arr+BppVzJ9mPw+Coo+b1UHSqghpF9m1bUlHRare8\n" +
            "9QiaAMkmPkkNlVgPDXpALMJR5zzYmVlJ1mhZnIosnAF1iAL2BNmzztn9vheWt4wP\n" +
            "yz9ah9eAEgSQHCjn+s9eOp3ObiWaklfhMjaUr2sCgYA6yRsxqR8p1xTe1dkTLObD\n" +
            "oZSaYPtHO0mGRQClegVhk4MGXj3bGQB8HWlbVZ5THNIgB6FYAdIeHksxJNiZylxG\n" +
            "DcaACXDlOaa+AR4375jN0zO/L8SnsGkHixkTYD5mIKTXCn2CXOhmLZuTJB2d6Z+0\n" +
            "bd1XU+3JfkwR1Ky/R5LVQQKBgAc1jOS5gTrcGcy69vAHNQhQu2zTHLk+havPvG/z\n" +
            "uv22hyf7V2dl9FHoNiWs7iVchqbCp/6UI1Jn+hVEfVOv8Evi5bU6D9K+KHXYAY/g\n" +
            "FXBSqy+19dfLk7W1WIrm6ggdvwBF+sS7NcO8FA+TTs9WxKQbJiYmV7kzcBpjSe6S\n" +
            "gG6lAoGAEgZwvOX8VF1nNeXyn7qi98KzG+eSzgNspLJkI7bciIKnsnRmdHfOTmpA\n" +
            "w2hflyvHUCoOvyxKQ5MDOcd3Y7EjHNqQ46wYIZRNLio+6k8kzibAR+W+W5KmZ8Vc\n" +
            "P9Xzaennpe7gEAYonkE8OgrhgrTJK+h4tcoofpIU+C0Jxf6q/p4=\n" +
            "-----END RSA PRIVATE KEY-----"
    /**
    Connectivity : End
     */

    const val CAVITY_LIGHTING = "oven_light"

    const val TIME_OUT_DEFAULT = 0
    const val TIME_OUT_STOP = -1
    @Suppress("unused")
    const val TURN_TABLE_KEY = "TURN_TABLE"


    //time variable
    const val ONE_HOUR_SECONDS: Int = 3600
    const val ONE_MIN_SECONDS: Int = 60

    const val HEADER_VIEW_CENTER_ICON_GONE = 18
    const val HEADER_RESTORE_LAYOUT_VIEW = 19

    //Diagnostics variable
    const val DEFAULT_ENTRY_NUMBER: String = "000000000"
    var SERVICE_DIAGNOSTIC_ENTRY: Boolean = false

    const val SERVICE_LIST_SCROLLBAR_DELAY: Long = 1800L

    const val COMMON_POPUP_DESCRIPTION_WIDTH = 694
    const val RECYCLER_LIST_MARGIN_TOP = 16
    const val POPUP_TITLE_TOP_MARGIN = 40
    const val POPUP_TITLE_TOP_SMALL_MARGIN = 20
    const val RESTORE_POPUP_VERTICAL_BOTTOM_SMALL_MARGIN = 28
    const val RESTORE_POPUP_VERTICAL_BOTTOM_HIGH_MARGIN = 64
    const val POPUP_ASSIGN_FVA_TITLE_TOP_SMALL_MARGIN = 5
    const val POPUP_TITLE_TOP_MARGIN_10PX = 54
    const val POPUP_TITLE_TOP_MARGIN_104PX = 104
    const val POPUP_TITLE_TOP_MARGIN_114PX = 114
    const val POPUP_TITLE_TOP_MARGIN_90PX = 90
    const val POPUP_TITLE_TOP_MARGIN_116PX = 116
    const val SELF_CLEAN_POPUP_TITLE_TOP_MARGIN_74PX = 74
    @Suppress("unused")
    const val POPUP_DESCIPTION_TOP_MARGIN_6PX = 6
    const val POPUP_TITLE_TOP_MARGIN_80PX = 80
    const val POPUP_TITLE_TOP_MARGIN_52PX = 52
    const val POPUP_TITLE_TOP_MARGIN_54PX = 54
    const val POPUP_TITLE_TOP_MARGIN_72PX = 72
    const val POPUP_DESCRIPTION_TOP_MARGIN_6PX = 6
    const val POPUP_DESCRIPTION_TOP_MARGIN_8PX = 8
    const val POPUP_BOTTOM_PADDING_16PX = 16
    const val POPUP_DESCIPTION_TOP_MARGIN_10PX = 10
    const val POPUP_DESCIPTION_TOP_MARGIN_70PX = 70
    const val POPUP_TITLE_TOP_MARGIN_75PX = 75
    const val POPUP_TITLE_TOP_MARGIN_18PX = 18
    const val POPUP_DESCRIPTION_HORIZONTAL_MARGIN_32PX = 32

    const val ASSISTED_TITLE_TEXT_SIZE_36PX = 36f
    const val ASSISTED_TITLE_START_MARGIN_16PX = 16
    const val ASSISTED_IMAGE_WIDTH_240PX = 240

    const val TUMBLER_SUB_TEXT_TOP_MARGIN = 16

    /** Time Interval to start Jet start in MWO **
     * this is a debounce time which will wait for 45 ms to show JET start progress dialog,
     * before modifying this consult with GCD team to have tuning
     */
    const val TIME_INTERVAL_JET_START = 3

    /** Time Interval for sobt reboot **/
    const val SOFT_REBBOT_ANIMATION_DELAY = 3000L
    const val SOFT_REBBOT_ANIMATION_INTERVAL = 1000L
    /** OTA error code **/
    const val OTA_ERROR_CODE_FATAL_MIN_RANGE: Int = 207
    const val OTA_ERROR_CODE_FATAL_MAX_RANGE: Int = 211
    const val OTA_ERROR_CODE_BRICK_STATE: Int = 500
    const val OVEN_SAFE_TEMPERATURE_CELSIUS_VALUE: Double = 93.33
    const val OVEN_SAFE_TEMPERATURE_FAHRENHEIT_VALUE: Int = 200
    const val CLOCK_SCREEN_OTA_TIME_OUT = 60000
    const val CLOCK_SCREEN_TECHNICIAN_EXIT_TIME_OUT = 30000
    const val CLOCK_FAR_VIEW_TIME_OUT = 60000
    const val LAST_BUILD_DATE_FORMAT: String = "E MMM dd HH:mm:ss z yyyy"
    const val OTA_LAST_UPDATE_DATE_FORMAT: String = "MMMM dd, yyyy"
    const val DEFAULT_LANGUAGE_ENGLISH_CODE: String = "en"
    const val DEFAULT_LANGUAGE_CANADIAN_FRENCH_CODE: String = "fr"
    const val DEFAULT_LANGUAGE_SPANISH_CODE: String = "es"
    const val OTA_JSON_KEY_SECONDARY_TEXT: String = "SecondaryText"


    //Network settings variables
    const val ERROR_CODE_IGNORE_RANGE: Int = 5

    //Sleep Mode
    const val LAST_SAVED_BRIGHTNESS_IN_CONNECTED_MODE: String =
        "lastSavedInBrightnessInConnectedMode"
    const val IS_PREVIOUSLY_IN_SLEEP_MODE: String = "isPreviouslyInSleepMode"

    /** Offset for HotCavity Warning **/
    const val HOT_CAVITY_WARNING_OFFSET_CELCIUS = 3
    const val HOT_CAVITY_WARNING_OFFSET_FAHRENHEIT = 5
    const val DEFAULT_MAX_START_TEMPERATURE = 0.0
    const val HOT_CAVITY_WARNING_TITLE: String = "title"
    const val HOT_CAVITY_WARNING_DESCRIPTION: String = "description"
    const val HOT_CAVITY_WARNING_COOLING_DOWN: String = "cooling down"
    const val HOT_CAVITY_WARNING_OVEN_READY: String = "oven ready"

    /** Keyboard key code **/
    const val KEYBOARD_ENTER_KEY = -4

    //OTA
    const val POPUP_OTA_CD_TITLE_TOP_SMALL_MARGIN = 13
    const val POPUP_OTA_TITLE_TOP_SMALL_MARGIN = 55
    const val POPUP_OTA_DESCRIPTION_TOP_SMALL_MARGIN = 6
    const val POPUP_OTA_CD_DESCRIPTION_TOP_SMALL_MARGIN = 7

    /** Navigation ID for Demo instruction popup **/
    const val NAVIGATION_ID_DEMO_INSTRUCTION = 9999
    const val DEMO_DEFAULT_CODE = "0000"

    const val CONNECT_TO_NETWORK_TILE_INDEX: Int = 5
    const val KNOB_LIGHT_TILE_INDEX: Int = 3

    const val NAVIGATION_ID_DEMO_NOT_AVAILABLE = 8888
    const val FEATURE_IS_NOT_AVAILABLE = 2
    //Remove below variable once we implement steam clean
    const val FEATURE_IS_UNDER_DEVELOPMENT = 99
    const val NAVIGATION_ID_DEMO_NOT_AVAILABLE_IN_TECHNICIAN_MODE = 1
    const val DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT = 5000L
    const val HOT_CAVITY_WARNING_POP_UP = 60000L

    /** Blackout Popup dimensions constants **/
    const val IS_FROM_BLACKOUT = "isFromBlackout"
    const val POPUP_BLACKOUT_TITLE_TOP_SMALL_MARGIN = 18
    const val POPUP_BLACKOUT_DESCRIPTION_TOP_SMALL_MARGIN = 8
    const val POPUP_BLACKOUT_UPDATE_DATE_AND_TIME_TOP_SMALL_MARGIN = 54
    const val DEMO_ENTRY_TITLE_MARGIN = 80
    const val DEMO_ENTRY_DESCRIPTION_MARGIN = 8
    const val DEMO_EXIT_TITLE_MARGIN = 36
    const val DEMO_EXIT_DESCRIPTION_MARGIN = 8

    /** Language constants **/
    const val TEXT_TILE_LIST: String = "text_tiles_list_"
    const val TEXT_TILE_LIST_VALUE: String = "_value"
    /** Unboxing preference **/
    const val PREFERENCE_UNBOXING_IS_TECHICIAN_ROLE = "preference_unboxing_is_techician_role"
    const val PREFERENCE_UNBOXING_IS_TECHNICIAN_TEST_DONE = "preference_unboxing_is_technician_test_done"
    const val PREFERENCE_UNBOXING_IS_SKIP_EXPLORE_FLAG = "preference_unboxing_is_skip_explore_flag"

    //for features/explore cooking guide
    const val TEXT_TITLE_APPLIANCE_FEATURES_GUIDE: String = "appliance_features_guide_title_"
    const val TEXT_TITLE_APPLIANCE_EXPLORE_FEATURES_GUIDE: String = "appliance_explore_features_guide_title_"
    const val TEXT_DESCRIPTION_APPLIANCE_EXPLORE_FEATURES_GUIDE: String = "appliance_explore_features_guide_message_"

    //Bullet point variables
    const val BULLET_RADIUS = 3
    const val BULLET_POINTS_GAP = 18

    //Digital unboxing
    const val POPUP_DG_CD_TITLE_TOP_SMALL_MARGIN = 36
    const val POPUP_DG_TITLE_TOP_SMALL_MARGIN = 7

    const val SLASH: String = "/"
    const val COMMA: String = ","
    const val COLON: String = ":"
    //Digital unboxing - Date time variables

    const val PREFERENCE_PAUSE_FOR_CANCEL_RECOVERY_UPPER_CAVITY = "preference_pause_for_cancel_recovery_upper_cavity"
    const val PREFERENCE_PAUSE_FOR_CANCEL_RECOVERY_LOWER_CAVITY = "preference_pause_for_cancel_recovery_lower_cavity"

    const val DATE_TIME = 12
    const val MAX_DATE_TIME_LENGTH = 5
    const val MAX_CHAR_LENGTH = 4
    const val MAX_DATE_CHAR_LENGTH = 6

    const val DIGIT_MINUS_ONE = -1
    const val DIGIT_ZERO = 0
    const val DIGIT_ONE = 1
    const val DIGIT_TWO = 2
    const val DIGIT_THREE = 3
    const val DIGIT_FOUR = 4
    const val DIGIT_FIVE = 5
    const val DIGIT_SIX = 6
    const val DIGIT_TWELVE = 12
    const val DIGIT_TEN = 10
    const val DIGIT_FIFTEEN = 15
    const val DIGIT_TWENTY_THREE = 23
    const val DIGIT_TWENTY_FOUR = 24
    const val DIGIT_THIRTY_ONE = 31
    const val DIGIT_FOURTY = 40
    const val DIGIT_SIXTY = 60

    //Error case Date and Time variable
    const val INVALID_DAY_RANGE_1D_28D = 28
    const val INVALID_DAY_RANGE_1D_29D = 29
    const val INVALID_DAY_RANGE_1D_30D = 30
    const val INVALID_DAY_RANGE_1D_31D = 31
    const val INVALID_CLOCK_RANGE_1H_12H = 12
    const val INVALID_CLOCK_RANGE_0H_23H = 23
    const val INVALID_CLOCK_RANGE_0M_59M = 59
    const val TIME_DATE_WARNING_TIMEOUT = 5000L
    //Time and Date variable
    const val TIME_TWELVE_HR = "12"
    const val TIME_ZERO_HR = "0"

    const val POPUP_FORGET_NETWORK_DESCRIPTION_TITLE_TOP_MARGIN = 37

    const val SOURCE_FRAGMENT = "sourceFragment"
    const val SETTINGLANDING_FRAGMENT = "settingLandingFragment"
    const val DEMOLANDING_FRAGMENT = "demoLandingFragment"
    const val DEMOSETTINGSLANDING_FRAGMENT = "demoSettingsLandingFragment"
    const val CONNECTIVITYLIST_FRAGMENT = "ConnectivityListLandingFragment"
    const val TIMEOUT_CALLBACK = "TimeoutCallback"
    const val POWERLOSS_TIME_DATE_UPDATE_POPUP = "powerLossTimeDateUpdate"
    const val CONSTANT_SIXTY = 60
    const val SETTINGS_LANGUAGE_FRAGMENT = "settingsLanguageFragment"
    const val SETTINGS_RESTORE_FACTORY = "settingsRestoreFactory"
    const val SETTINGS_DEMO_CODE = "settingsDemoCode"
    const val PREFERENCES_FRAGMENT = "preferencesFragment"
    const val INSTRUCTION_ICON = "instructionIcon"
    const val MORE_OPTIONS = "moreOptionsPopup"
    const val NEXT_BUTTON = "nextButton"
    //Favorite and History
    const val MAX_FAVORITE_COUNT = 10
    const val KEY_FAVORITE_NAME = "key_favorite_name"
    const val KEY_FAVORITE_FROM = "key_favorite_from"
    const val FAVORITE_DEFAULT_IMAGE = "favoritedefault"
    const val CLOCK_FAR_OR_VIDEO_VIEW_FRAGMENT = "clockFarOrVideoViewFragment"
    public enum class FavoriteFrom{
        STATUS_SCREEN,
        PREVIEW_SCREEN
    }

    const val CONSTANT_MAX_INTENSITY = 100
    const val SOUND_VOLUME = 1
    const val DISPLAY_AND_BRIGHTNESS = 2
    const val TIME_AND_DATE = 3
    const val KNOB_SETTINGS = 4
    const val ALERTS_AND_TIMERS = 11
    const val BUTTONS_AND_EFFECTS = 12
    const val DISPLAY_BRIGHTNESS = 21

    /**
     * Represents the Knob ID (left knob or Right Knob) (10 seconds).
     * Value: 1 or 0.
     */
    const val DEFAULT_LEFT_KNOB_ID = 0
    const val DEFAULT_RIGHT_KNOB_ID = 1

    var LEFT_KNOB_ID = 0
    var RIGHT_KNOB_ID = 1

    /** KNOB preference **/
    const val PREFERENCE_KNOB_POSITION = "preference_knob_position"
    const val PREFERENCE_KNOB_LIGHT = "preference_knob_light"
    const val PREFERENCE_ASSIGN_FAVORITES = "preference_assign_favorites"
    const val PREFERENCE_QUICK_FAVORITES_CYCLE = "preference_quick_favorites_cycle"

    //HMI key buttons const variable
    const val KEY_CONFIGURATION_CLOCK_SCREEN: Int = 1
    const val KEY_CONFIGURATION_DIGITAL_UNBOXING: Int = 2
    const val KEY_CONFIGURATION_SETTING_LANDING: Int = 3
    const val KEY_CONFIGURATION_CONNECT_TO_NETWORK: Int = 4
    const val KEY_CONFIGURATION_SELF_CLEAN: Int = 5
    const val KEY_CONFIGURATION_HOME: Int = 6
    const val KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION: Int = 7
    const val KEY_CONFIGURATION_RUNNING: Int = 8
    const val KEY_CONFIGURATION_KITCHEN_TIMER: Int = 9
    const val KEY_CONFIGURATION_CONTROL_LOCK: Int = 10
    const val KEY_CONFIGURATION_SABBATH_MODE: Int = 11
    const val KEY_CONFIGURATION_DEMO_MODE: Int = 12
    const val KEY_CONFIGURATION_DEMO_MODE_CLOCK: Int = 13
    const val KEY_CONFIGURATION_DURING_DOOR_LOCK: Int = 14//Self clean door lock screen
    const val KEY_CONFIGURATION_SELF_CLEAN_RUNNING: Int = 15
    const val KEY_CONFIGURATION_SERVICE: Int = 16
    const val KEY_CONFIGURATION_FAULT_BLOCKING: Int = 17
    const val KEY_CONFIGURATION_FAULT_A_C: Int = 18
    const val KEY_CONFIGURATION_FAULT_B2: Int = 19
    const val KEY_CONFIGURATION_POPUPS: Int = 20
    const val KEY_CONFIGURATION_POPUPS_IDLE: Int = 21
    const val KEY_CONFIGURATION_POPUPS_PRIORITY: Int = 22
    const val KEY_CONFIGURATION_DEMO_MODE_HOME: Int = 23
    const val KEY_CONFIGURATION_DEMO_MODE_LANDING: Int = 24

    const val DELAY_CONFIGURATION_1000 = 1000L
    /**
     * MoreOptions Types to display it in Status Popup & Handle related Click Events
     */
    enum class MoreOptionsSubCategory{
        TYPE_COOK_TIME,
        TYPE_CHANGE_TEMPERATURE,
        TYPE_TEMPERATURE_LEVEL,
        TYPE_POWER_LEVEL,
        TYPE_EXTRA_BROWN,
        TYPE_TURN_OVEN_MWO_OFF,
        TYPE_PROBE,
        TYPE_FAVORITES,
        TYPE_INSTRUCTIONS,
        TYPE_AUTO_COOK
    }

    //Steam clean
    const val POPUP_TITLE_TOP_IMAGE_MARGIN_23PX = 23
    const val POPUP_TITLE_TOP_MARGIN_191PX = 191
    const val POPUP_DESCRIPTION_TOP_MARGIN_105PX = 105
    const val POPUP_PROBE_TITLE_TOP_MARGIN_87PX = 87
    //Far view variables
    const val USER_INSTRUCTION_PREHEAT_COMPLETED = "preheatComplete"
    const val FAR_VIEW_KITCHEN_TIMER_DELAY = 3000L

    const val FLASH_ANIMATION_DURATION = 1000L // Duration of the flash animation in milliseconds
    const val FLASH_ANIMATION_REPEAT_COUNT = 3 // Number of flash cycles
    const val FLASH_ANIMATION_START_ALPHA = 1f // Start alpha value for the animation
    const val FLASH_ANIMATION_END_ALPHA = 0f // End alpha value for the animation
    const val FLASH_ANIMATION_FINAL_ALPHA = 1f // Alpha value to reset after animation ends

    //Animation Far View Variables
    const val CAVITY_RUNING_UPPER = 1
    const val CAVITY_RUNING_LOWER = 2
    const val CAVITY_RUNING_BOTH = 3

    const val FAR_VIEW_SLIDE_DISTANCE_LOWER = -50f
    const val FAR_VIEW_SLIDE_DISTANCE_UPPER = 50f
    const val FAR_VIEW_SLIDE_40 = 40f
    const val FAR_VIEW_SLIDE_42 = 41f
    const val FAR_VIEW_SLIDE_33 = 33.5f
    const val FAR_VIEW_SLIDE_33_MINUS = -33.5f
    const val FAR_VIEW_SLIDE_41 = 41.5f
    const val FAR_VIEW_SLIDE_41_MINUS = -41.5f
    const val FAR_VIEW_SLIDE_50 = 50f
    const val FAR_VIEW_SLIDE_100 = 100f
    const val FAR_VIEW_FADE_IN = 1f
    const val FAR_VIEW_FADE_OUT = 0f

    const val FAR_VIEW_ANIMATION_350 = 350L
    const val FAR_VIEW_ANIMATION_500 = 500L
    const val FAR_VIEW_ANIMATION_600 = 600L
    const val FAR_VIEW_ANIMATION_700 = 700L
    const val FAR_VIEW_ANIMATION_1000 = 1000L
    const val FAR_VIEW_ANIMATION_1500 = 1500L

    const val KT_TUMBLER_LIST_TIMEOUT_DURATION = 10_000L // 10 seconds

    const val RESUME_COOKING_ANIMATION_START_DURATION = 400L // Duration for sliding animations
    const val RESUME_COOKING_ANIMATION_DELAY_DURATION = 100L // Delay before starting fade animations
    const val RESUME_COOKING_FADE_OUT_ALPHA_START = 1f // Starting alpha for fade-out animation
    const val RESUME_COOKING_FADE_OUT_ALPHA_END = 0f   // Ending alpha for fade-out animation
    const val RESUME_COOKING_FADE_IN_ALPHA_START = 0f  // Starting alpha for fade-in animation
    const val RESUME_COOKING_FADE_IN_ALPHA_END = 1f    // Ending alpha for fade-in animation

    const val VIDEO_CONTROLS_TIME_OUT = 3000L

    const val UPPER_FRAGMENT = "UpperFragment"
    const val LOWER_FRAGMENT = "LowerFragment"


    //    Hot cavity temperature Popup Constant Values
    const val OVEN_HEAT_WARNING_TEMPERATURE_CELSIUS_VALUE: Double = 185.0
    const val OVEN_HEAT_WARNING_TEMPERATURE_FAHRENHEIT_VALUE: Int = 400

    const val REGEX_DOT ="."
    const val REGEX_COMMA =","
}