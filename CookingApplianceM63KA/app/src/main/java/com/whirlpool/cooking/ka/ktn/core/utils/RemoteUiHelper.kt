package core.utils

import com.whirlpool.hmi.cookingremoteui.RemoteUi
import com.whirlpool.hmi.cookingremoteui.RemoteUi.RemoteUiModules
import com.whirlpool.hmi.settings.SettingsViewModel

/**
 * File        : core.utils.RemoteUiHelper
 * Brief       : Utility class and method for Initialize RemoteUi and Start Cooking RemoteUi
 *               Settings data Initialization Helper
 * Author      : Vishal
 * Created On  : 04-09-2024
 */
class RemoteUiHelper {
    companion object {
        /**
         * Initialize RemoteUi and Start Cooking RemoteUi
         * This will add required data in settingsViewModel for RemoteUi
         */
        fun initRemoteUi(settingsViewModel: SettingsViewModel) {
            HMILogHelper.Logd("##RemoteCC : initializeAndStartRemoteUi")

            setDataForRemoteUi(settingsViewModel)
            val remoteUiModules = ArrayList<RemoteUiModules>()
            remoteUiModules.add(RemoteUiModules.COOKING)
            remoteUiModules.add(RemoteUiModules.OTA)
            remoteUiModules.add(RemoteUiModules.KITCHEN_TIMER)
            remoteUiModules.add(RemoteUiModules.VISION);
            RemoteUi.start(remoteUiModules)
        }

        /**
         * Set data in Settings For RemoteUi
         * @param settingsViewModel Settings ViewModel
         */
        private fun setDataForRemoteUi(settingsViewModel: SettingsViewModel) {
            settingsViewModel.setUserDataStringValue(
                "endPoint",
                AppConstants.AWS_IOT_END_POINT, false
            )
            settingsViewModel.setUserDataStringValue("rootCa", AppConstants.rootCa, true)
            settingsViewModel.setUserDataStringValue(
                "provCertificate",
                AppConstants.provCertificate,
                true
            )
            settingsViewModel.setUserDataStringValue(
                "provPrivateKey",
                AppConstants.provPrivateKey,
                true
            )
        }
    }
}