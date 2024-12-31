/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.settings

import android.os.Bundle
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.settings.SettingsViewModel
import core.jbase.AbstractDemoModeCodeFragment
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.NavigationUtils.Companion.navigateSafely

/**
 * File        : android.presenter.fragments.settings.DemoModeCodeFragment.
 * Brief       : Demo Mode code management
 * Author      : DUGAMAS/Amar Suresh Dugam
 * Created On  : 18-03-2024
 */
class DemoModeCodeFragment : AbstractDemoModeCodeFragment(),
    AbstractDemoModeCodeFragment.ButtonClickListenerInterface {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()
        setButtonInteractionListener(this)
    }

    /**
     * initialize UI components
     */
    private fun initButton() {
        demoModeCodeViewHolderHelper?.getRightTextButton()?.visibility = View.VISIBLE
        demoModeCodeViewHolderHelper?.getRightTextButton()?.isEnabled = false
        demoModeCodeViewHolderHelper?.getLeftTextButton()?.visibility = View.GONE
        demoModeCodeViewHolderHelper?.getLeftPowerTextButton()?.visibility = View.GONE
        demoModeCodeViewHolderHelper?.getMiddleTextButton()?.visibility = View.GONE
    }

    override fun onRightButtonClick() {
        validateCode()
    }

    override fun onLeftButtonClick() {
        //Bottom left button click event
    }

    override fun onMiddleButtonClick() {
        //Bottom middle button click event
    }

    override fun onLeftPowerButtonClick() {
        //TODO("Not yet implemented")
    }

    override fun provideIntegerRange(): IntegerRange {
        return (recipeViewModel?.cookTimeOption?.value as IntegerRange?)!!
    }

    override fun validCodeActionWhenDemoModeIsEnabled() {
        var navigatedFrom = arguments?.getString(BundleKeys.BUNDLE_NAVIGATED_FROM)
        if(AppConstants.SETTINGS_LANGUAGE_FRAGMENT == navigatedFrom) {
            SettingsViewModel.getSettingsViewModel()
                .setAppLanguage(arguments?.getString(BundleKeys.BUNDLE_SELECTED_LANGUAGE))
            navigateSafely(this, R.id.action_DemoModeCodeFragment_to_settingsLanguageFragment, null, null)
        }else if (AppConstants.SETTINGS_RESTORE_FACTORY == navigatedFrom){
            val bundle = Bundle()
            bundle.apply {
                putString(BundleKeys.BUNDLE_NAVIGATED_FROM, AppConstants.SETTINGS_DEMO_CODE)
                putBoolean(BundleKeys.BUNDLE_RESTORE_FACTORY,
                    arguments?.getBoolean(BundleKeys.BUNDLE_RESTORE_FACTORY) == true
                )
            }
            navigateSafely(this, R.id.action_DemoModeCodeFragment_to_settingsResetRestoreConfirmationFragment,
                bundle, null)
        }
        else{
            CookingAppUtils.cancelIfAnyRecipeIsRunning()
            CookingAppUtils.cancelIfAnyKitchenTimersRunning()
            SettingsViewModel.getSettingsViewModel().setDemoMode(SettingsViewModel.DemoMode.DEMO_MODE_DISABLED)
            CookingAppUtils.startGattServer(this)
            navigateSafely(this, R.id.global_action_to_clockScreen, null, null)
        }
    }

    override fun validCodeActionWhenDemoModeIsDisabled() {
            SettingsViewModel.getSettingsViewModel()
                .setDemoMode(SettingsViewModel.DemoMode.DEMO_MODE_ENABLED)
            CookingAppUtils.stopGattServer()
            navigateSafely(this, R.id.action_DemoModeCodeFragment_to_demoModeLandingFragment, null, null)
    }

}
