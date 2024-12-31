/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package core.utils

import androidx.annotation.StringDef


/**
 * File        : core.utils.BundleKeys
 * Brief       : Annotated interface class for managing bundle keys
 * Author      : GHARDNS/Nikki
 * Created On  : 18-03-2024 <br>
 */
@Retention(AnnotationRetention.SOURCE)
@StringDef(
    BundleKeys.BUNDLE_PROVISIONING_TIME,
    BundleKeys.BUNDLE_IS_FROM_BLACKOUT_CONNECT_WIFI,
    BundleKeys.BUNDLE_IS_NAVIGATE_TOOLS_TIME_FRAGMENT,
    BundleKeys.RECIPE_NAME,
    BundleKeys.BUNDLE_SELECTED_PROBE_TARGET_TEMPERATURE,
    BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE,
    BundleKeys.BUNDLE_IS_FROM_PREVIEW_SCREEN,
    BundleKeys.BUNDLE_IS_ERROR_FRAME,
    BundleKeys.BUNDLE_FAULT_CODE,
    BundleKeys.BUNDLE_FAULT_CAVITY,
    BundleKeys.BUNDLE_FAULT_CATEGORY,
    BundleKeys.BUNDLE_MODEL_NUMBER,
    BundleKeys.BUNDLE_SERIAL_NUMBER,
    BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER,
    BundleKeys.BUNDLE_EXTRA_COMING_FROM_KT,
    BundleKeys.BUNDLE_EXTRA_COMING_FROM_APPLIANCE_EXPLORE_FLOW,
    BundleKeys.BUNDLE_PROVISIONING_DATE,
    BundleKeys.KEY_TIME_FORMAT,
    BundleKeys.KEY_TIME,
    BundleKeys.KEY_DATE,
    BundleKeys.BUNDLE_IS_FROM_TOOLS_MENU,
    BundleKeys.BUNDLE_IS_FROM_BLACKOUT_CONNECT_WIFI,
    BundleKeys.BUNDLE_RESTORE_FACTORY,
    BundleKeys.BUNDLE_SOUND_DISPLAY,
    BundleKeys.BUNDLE_INTENSITY_TYPE,
    BundleKeys.BUNDLE_CUSTOMIZE_KNOB_STEPPER_TYPE,
    BundleKeys.BUNDLE_NAVIGATED_FROM,
    BundleKeys.BUNDLE_IS_NAVIGATE_TOOLS_DATE_FRAGMENT,
    BundleKeys.BUNDLE_NAVIGATED_FROM_SELF_CLEAN,
    BundleKeys.BUNDLE_SELF_CLEAN_COMPLETED,
    BundleKeys.BUNDLE_NAVIGATED_FROM_SELF_CLEAN,
    BundleKeys.BUNDLE_NAVIGATED_ASSISTED_DELAY_COOKING_GUIDE,
    BundleKeys.BUNDLE_APPLY_TRANSITION_ANIMATION,
    BundleKeys.BUNDLE_VALUE_POP_TO_PREVIEW
)
annotation class BundleKeys {
    companion object {
        const val BUNDLE_PROVISIONING_TIME = "provisioningTime"
        const val BUNDLE_IS_NAVIGATE_TOOLS_TIME_FRAGMENT = "isNavigateToToolsTimeFragmentOnly"
        const val RECIPE_NAME = "recipeName"
        const val RECIPE_TYPE = "recipeType"
        const val PROBE_BASED = "probeBased"
        const val BUNDLE_SELECTED_PROBE_TARGET_TEMPERATURE = "selectedProbeTemperature"
        const val BUNDLE_SELECTED_TARGET_TEMPERATURE = "selectedTemp"
        const val BUNDLE_IS_FROM_PREVIEW_SCREEN = "isFromPreviewScreen"
        const val BUNDLE_IS_ERROR_FRAME = "isErrorFrame"
        const val BUNDLE_FAULT_CODE = "faultCode"
        const val BUNDLE_FAULT_CAVITY = "faultCavity"
        const val BUNDLE_FAULT_CATEGORY = "faultCategory"
        const val BUNDLE_MODEL_NUMBER = "applianceModelNumber"
        const val BUNDLE_SERIAL_NUMBER = "applianceSerialNumber"
        const val BUNDLE_MODIFY_KITCHEN_TIMER = "modifyRunningKitchenTimer"

        @Suppress("unused")
        const val BUNDLE_FAULT_ERROR_TITLE = "fault_error_title"
        @Suppress("unused")
        const val BUNDLE_FAULT_ERROR_DESCRIPTION = "fault_error_description"
        
        const val BUNDLE_EXTRA_COMING_FROM_KT = "bundle_extra_coming_from_kt"
        const val BUNDLE_EXTRA_COMING_FROM_APPLIANCE_EXPLORE_FLOW = "bundle_extra_coming_from_appliance_explore_flow"
        const val BUNDLE_EXTRA_COMING_FROM_CONNECT_NETWORK_FLOW = "bundle_extra_coming_from_connect_network_flow"

        //Date and Time variables
        const val BUNDLE_PROVISIONING_DATE = "provisioningDate"
        const val KEY_TIME_FORMAT: String = "Time_Format"
        const val KEY_TIME: String = "Time" // 24 hr format HHMM
        const val KEY_DATE: String = "Date" // MMDDYY
        const val BUNDLE_IS_NAVIGATE_TOOLS_DATE_FRAGMENT: String =
            "isNavigateToToolsDateFragmentOnly"
        const val BUNDLE_IS_FROM_TOOLS_MENU: String = "isFromToolsMenu"

        const val BUNDLE_IS_FROM_BLACKOUT_CONNECT_WIFI: String= "isBlackOutConnectWiFi"
        const val BUNDLE_RESTORE_FACTORY = "toolsRestoreFactory"
        const val BUNDLE_SOUND_DISPLAY = "soundDisplay"
        const val BUNDLE_INTENSITY_TYPE = "intensityType"
        const val BUNDLE_CUSTOMIZE_KNOB_STEPPER_TYPE = "customizeKnobStepper"
        const val BUNDLE_NAVIGATED_FROM = "navigatedFrom"
        const val BUNDLE_NAVIGATED_FROM_SELF_CLEAN = "bundle_extra_coming_from_self_clean"
        const val BUNDLE_SELECTED_LANGUAGE = "selectedLanguage"
        const val BUNDLE_NAVIGATED_ASSISTED_DELAY_COOKING_GUIDE = "bundle_assisted_cooking_delay_guide"
        const val SELF_CLEAN_PYRO_LEVEL = "bundle_key_self_clean_pyro_level"
        const val SELF_CLEAN_COOK_TIME = "bundle_key_self_clean_cook_time"
        const val BUNDLE_SELF_CLEAN_COMPLETED = "bundle_key_self_clean_completed"

        //Steam clean
        const val DELAY_STEAM_CLEAN = "delaySteamClean"

        //Animation Variable
        const val BUNDLE_APPLY_TRANSITION_ANIMATION = "bundle_apply_transition_animation"

        /**
         * to pass the video/image that should be played/shown on demo video fragment
         */
        const val BUNDLE_VIDEO_OPTION: String = "demo_video_image_selected"

        const val BUNDLE_VALUE_POP_TO_PREVIEW: String = "popToPreview"
    }
}