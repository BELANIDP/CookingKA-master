package com.whirlpool.cooking.ViewHolderHelpers;

import androidx.lifecycle.ViewModelProvider;

import com.whirlpool.cooking.R;
import com.whirlpool.cooking.utils.SettingsUtils;
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel;
import com.whirlpool.hmi.provisioning.ProvisioningViewModel;
import com.whirlpool.hmi.settings.SettingsViewModel;
import com.whirlpool.hmi.utils.ContextProvider;

public class CrossCategoryHelper {

    private final String FUNC_START_KEY = "start";
    private final String FUNC_CONTROL_LOCK_KEY = "controlLock";
    private static KitchenTimerViewModel kitchenTimerVM;
    /** ================================================================================================================
     -----------------------------------------------  General Key Methods  ----------------------------------------------
     ================================================================================================================ **/
    /**
     *
     * @return the start key
     */
    public String getStartKey()
    {
        return FUNC_START_KEY;
    }

    /**
     *
     * @return the control lock key
     */
    public String getControlLockKey()
    {
        return FUNC_CONTROL_LOCK_KEY;
    }

    /**
     * Provides the interface to access KitchenTimerCount
     *
     * @return {@link int}
     */
    public int provideKitchenTimerCount() {
        return R.integer.kitchen_timer_count;
    }
	
	public SettingsUtils.TemperatureFormatSettings getDefaultTemperatureUnit(){
           return SettingsUtils.TemperatureFormatSettings.CELSIUS;
    }

    public int provideKitchenTimerEntryScreen(){
        return R.id.action_toolsMenu_to_set_kitchen_timer;
    }

    public int provideKitchenTimerRunningScreen(){
        return R.id.action_toolsMenu_to_kitchenTimerRunningStatus;
    }

    public KitchenTimerViewModel provideKitchenTimerViewModel() {
        if (kitchenTimerVM == null) {
            kitchenTimerVM = new ViewModelProvider(ContextProvider.getFragmentActivity()).get("timer1",KitchenTimerViewModel.class);
        }
        return kitchenTimerVM;
    }
}
