package android.presenter.fragments.settings

import android.media.AudioManager
import android.presenter.customviews.listView.AbstractListFragment
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.ListViewHolderInterface
import android.view.View
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.LogHelper
import core.utils.AppConstants
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AppConstants.KNOB_LIGHT_TILE_INDEX
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMIExpansionUtils.Companion.setBothKnobLightOff
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SettingsManagerUtils
import core.utils.SharedPreferenceManager

/**
 * File       : com.whirlpool.cooking.settings.SettingsKnobFragment
 * Brief      : Handles show Settings Knob list Fragment
 * Author     : Rajendra
 * Created On : 10-OCT-2024
 */
class SettingsKnobFragment : AbstractListFragment(), ListViewHolderInterface.ListItemClickListener {
    /**
     * Method to add the Knob settings to a List
     */
    private fun prepareAndDisplayKnobSettingsList(): ArrayList<ListTileData> {
        val settingsKnobList = ArrayList<ListTileData>()

        val radioButtonData: ListTileData.RadioButtonData = ListTileData.RadioButtonData()
        radioButtonData.visibility = View.GONE

        // Knob Functions Info
        val knobFunctionInfo = ListTileData()
        knobFunctionInfo.apply {
            titleText = resources.getString(R.string.knob_function_info)
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            rightIconVisibility = View.VISIBLE
            listItemDividerViewVisibility = View.VISIBLE
            rightIconID = R.drawable.ic_rightarrowicon
        }
        knobFunctionInfo.radioButtonData = radioButtonData
        settingsKnobList.add(knobFunctionInfo)

        // Swap Knob Functions
        val swapKnobInfo = ListTileData()
        swapKnobInfo.apply {
            titleText = resources.getString(R.string.swap_knob_function)
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            listItemDividerViewVisibility = View.VISIBLE
            rightIconVisibility = View.VISIBLE
            rightIconID = R.drawable.ic_rightarrowicon
        }
        swapKnobInfo.radioButtonData = radioButtonData
        settingsKnobList.add(swapKnobInfo)

        // Assign favorite to knob
        val assignFavInfo = ListTileData()
        assignFavInfo.apply {
            titleText = resources.getString(R.string.text_assign_favorites)
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            if (SharedPreferenceManager.getKnobAssignFavoritesCycleStatusIntoPreference()
                    .toBoolean()
            ) {
                if (SettingsManagerUtils.isFavoritesKnobAssignCycleAvailableInFavoritesRecords()) {
                    rightText =
                        SharedPreferenceManager.getKnobAssignFavoritesCycleNameIntoPreference()
                            .toString()
                } else {
                    rightText = getDefaultJetStartCycle()
                    SharedPreferenceManager.setKnobAssignFavoritesCycleStatusIntoPreference(
                        AppConstants.FALSE_CONSTANT
                    )
                    SharedPreferenceManager.setKnobAssignFavoritesCycleNameIntoPreference(
                        EMPTY_STRING
                    )
                }
            } else {
                rightText = getDefaultJetStartCycle()
            }
            listItemDividerViewVisibility = View.VISIBLE
            rightIconVisibility = View.VISIBLE
            rightIconID = R.drawable.ic_rightarrowicon
        }
        assignFavInfo.radioButtonData = radioButtonData
        settingsKnobList.add(assignFavInfo)

        // Knob Light
        val knobLightInfo = ListTileData()
        val switchData: ListTileData.ToggleSwitchData = ListTileData.ToggleSwitchData()
        switchData.apply {
            visibility = View.VISIBLE
            isEnabled = true
            isChecked = SharedPreferenceManager.getKnobLightStatusIntoPreference().toBoolean()
        }
        knobLightInfo.apply {
            titleText = resources.getString(R.string.knob_light)
            toggleSwitchData = switchData
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            listItemDividerViewVisibility = View.GONE
            rightIconVisibility = View.VISIBLE

        }
        knobLightInfo.radioButtonData = radioButtonData
        settingsKnobList.add(knobLightInfo)

        return settingsKnobList
    }

    private fun getDefaultJetStartCycle(): String {
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN,
            -> {
                return resources.getString(R.string.assign_quick_microwave_30_sec)
            }
            CookingViewModelFactory.ProductVariantEnum.COMBO,
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
            CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
            -> {
                return resources.getString(R.string.assign_quick_bake_350)
            }

            else -> {
                return resources.getString(R.string.assign_quick_bake_350)
            }
        }
    }

    override fun setUpViews() {
        //Do nothing
    }

    override fun provideHeaderBarRightIconVisibility(): Boolean {
        return true
    }

    override fun provideHeaderBarLeftIconVisibility(): Boolean {
        return true
    }

    override fun provideHeaderBarInfoIconVisibility(): Boolean {
        return false
    }

    override fun provideHeaderBarTitleText(): String {
        return getString(R.string.knob_setting)
    }

    override fun provideListRecyclerViewTilesData(): ArrayList<ListTileData> {
        return prepareAndDisplayKnobSettingsList()
    }

    override fun onListViewItemClick(view: View?, position: Int) {
        HMILogHelper.Logd("position = $position")
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        when (position) {
            0 -> {
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it,
                        R.id.action_settingsKnobFragment_to_settingsKnobFunctionInfoFragment,
                        null,
                        null
                    )
                }
            }

            1 -> {
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it, R.id.action_settingsKnobFragment_to_settingsKnobSwapFragment, null, null
                    )
                }
            }

            2 -> {
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it,
                        R.id.action_settingsKnobFragment_to_settingsAssignFavoriteToKnobFragment,
                        null,
                        null
                    )
                }
            }
        }
    }

    override fun observeViewModels() {
        // Implement view model observers if necessary
    }

    override fun headerBarOnClick(view: View?, buttonType: Int) {
        when (buttonType) {
            ICON_TYPE_LEFT -> {
                // Handle left button click
                KnobNavigationUtils.setBackPress()
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    val navId : Int = if (CookingAppUtils.getNavigatedFrom() == AppConstants.SETTINGLANDING_FRAGMENT) {
                        R.id.action_settingsKnobFragment_to_settingsLandingFragment
                    } else {
                        R.id.action_settingsKnobFragment_to_preferenceFragment
                    }
                    CookingAppUtils.setNavigatedFrom(EMPTY_STRING)
                    NavigationUtils.navigateSafely(it, navId, null, null)
                }
            }

            ICON_TYPE_RIGHT -> {
                CookingAppUtils.setNavigatedFrom(EMPTY_STRING)
                CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
            }
        }
    }

    override fun onRightNavigationButtonClick(view: View?, buttonType: Int) {
        //DO nothing
    }

    override fun onLeftNavigationButtonClick(view: View?, buttonType: Int) {
        //DO nothing
    }

    override fun onHMILongLeftKnobPress() {
        //DO nothing
    }

    override fun onHMIRightKnobClick() {
        //DO nothing
    }

    override fun onHMILongRightKnobPress() {
        //DO nothing
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //DO nothing
    }

    override fun setRightButton(): String {
        return ""
    }

    override fun setLeftButton(): String {
        return ""
    }

    override fun setGradientView(): Boolean {
        return false
    }

    override fun onToggleSwitchClick(position: Int, isChecked: Boolean) {
        if (position == KNOB_LIGHT_TILE_INDEX) {
            HMILogHelper.Logd("Setting knob light to $isChecked")
            //Off the all knob light,blink animation etc.
            if (!isChecked) {
                HMILogHelper.Logd("Setting knob light to turn off successfully")
                setBothKnobLightOff()
                HMIExpansionUtils.startOrStopKnobLEDFastBlinkAnimation(false)
                HMIExpansionUtils.startOrStopKnobLEDSlowBlinkAnimation(false)
            }
            SharedPreferenceManager.setKnobLightStatusIntoPreference(isChecked.toString())
        }
    }
}
