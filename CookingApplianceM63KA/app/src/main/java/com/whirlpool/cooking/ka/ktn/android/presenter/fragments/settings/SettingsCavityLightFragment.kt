package android.presenter.fragments.settings

import android.os.Bundle
import android.presenter.customviews.listView.AbstractListFragment
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.ListViewHolderInterface
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import core.utils.AppConstants.RECYCLER_LIST_MARGIN_TOP
import core.utils.CookingAppUtils
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * File       : com.whirlpool.cooking.settings.SettingsCavityLightFragment
 * Brief      : Handles shows cavity light
 * Author     : Rajendra Paymode
 * Created On : 21-OCT-2024
 */
class SettingsCavityLightFragment : AbstractListFragment(),
    ListViewHolderInterface.ListItemClickListener {
    /**
     * Method to add the Light settings to a List
     */
    private var cavityLightList: ArrayList<ListTileData>? = null

    /**
     * Override method onViewCreated to manage the view
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateListMarginTop(RECYCLER_LIST_MARGIN_TOP)
    }

    private fun prepareAndDisplayCavityLightSettingsList(): ArrayList<ListTileData> {
        cavityLightList = ArrayList()
        addKeepLightOnWhenRunning()
        addManuallyControlLight()
        return cavityLightList as ArrayList<ListTileData>
    }

    private fun addKeepLightOnWhenRunning() {
        val keepLightOnInfo = ListTileData()
        val keepLightOnRadioButtonData: ListTileData.RadioButtonData =
            ListTileData.RadioButtonData()
        keepLightOnRadioButtonData.apply {
            visibility = View.VISIBLE
            isEnabled = true
            isChecked = (
                    (SettingsViewModel.getSettingsViewModel().lightStateWhenRunningPrimaryCavity.value
                            == SettingsViewModel.CavityLightStateWhenRunning.CAVITY_LIGHT_STATE_WHEN_RUNNING_ON) ||
                            (SettingsViewModel.getSettingsViewModel().lightStateWhenRunningSecondaryCavity.value
                                    == SettingsViewModel.CavityLightStateWhenRunning.CAVITY_LIGHT_STATE_WHEN_RUNNING_ON))
        }
        keepLightOnInfo.apply {
            titleText = resources.getString(R.string.text_keep_light_on)
            itemIconVisibility = View.VISIBLE
            subTextVisibility = View.GONE
            listItemDividerViewVisibility = View.VISIBLE
            rightIconVisibility = View.GONE
            radioButtonData = keepLightOnRadioButtonData
        }
        cavityLightList?.add(keepLightOnInfo)
    }

    private fun addManuallyControlLight() {
        val manuallyControlLightInfo = ListTileData()
        val manuallyControlLightRadioButtonData: ListTileData.RadioButtonData =
            ListTileData.RadioButtonData()
        manuallyControlLightRadioButtonData.apply {
            visibility = View.VISIBLE
            isEnabled = true
            isChecked = (
                    (SettingsViewModel.getSettingsViewModel().lightStateWhenRunningPrimaryCavity.value
                            == SettingsViewModel.CavityLightStateWhenRunning.CAVITY_LIGHT_STATE_WHEN_RUNNING_OFF) ||
                            (SettingsViewModel.getSettingsViewModel().lightStateWhenRunningSecondaryCavity.value
                                    == SettingsViewModel.CavityLightStateWhenRunning.CAVITY_LIGHT_STATE_WHEN_RUNNING_OFF))
        }
        manuallyControlLightInfo.apply {
            titleText = resources.getString(R.string.text_control_light_manually)
            itemIconVisibility = View.VISIBLE
            subTextVisibility = View.GONE
            rightIconVisibility = View.GONE
            listItemDividerViewVisibility = View.GONE
            radioButtonData = manuallyControlLightRadioButtonData
        }
        cavityLightList?.add(manuallyControlLightInfo)
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
        return getString(R.string.oven_light)
    }

    override fun provideListRecyclerViewTilesData(): ArrayList<ListTileData> {
        return prepareAndDisplayCavityLightSettingsList()
    }

    override fun onListViewItemClick(view: View?, position: Int) {
        onViewClickTile(position)
    }

    private fun onViewClickTile(position: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            cavityLightList?.let {
                val selectedItem: ListTileData = it[position]
                for (i in 0 until it.size) {
                    val listItemModel: ListTileData = it[i]
                    val radioData = listItemModel.radioButtonData
                    radioData.visibility = View.VISIBLE
                    radioData.isEnabled = true
                    radioData.isChecked = selectedItem == listItemModel
                }
            }
            withContext(Dispatchers.Main) {
                notifyDataSetChanged()
                when (position) {
                    0 -> {
                        SettingsViewModel.getSettingsViewModel()
                            .setLightStateWhenRunningPrimaryCavity(
                                SettingsViewModel.CavityLightStateWhenRunning.CAVITY_LIGHT_STATE_WHEN_RUNNING_ON
                            )
                        SettingsViewModel.getSettingsViewModel()
                            .setLightStateWhenRunningSecondaryCavity(
                                SettingsViewModel.CavityLightStateWhenRunning.CAVITY_LIGHT_STATE_WHEN_RUNNING_ON
                            )

                    }

                    1 -> {
                        SettingsViewModel.getSettingsViewModel()
                            .setLightStateWhenRunningPrimaryCavity(
                                SettingsViewModel.CavityLightStateWhenRunning.CAVITY_LIGHT_STATE_WHEN_RUNNING_OFF
                            )
                        SettingsViewModel.getSettingsViewModel()
                            .setLightStateWhenRunningSecondaryCavity(
                                SettingsViewModel.CavityLightStateWhenRunning.CAVITY_LIGHT_STATE_WHEN_RUNNING_OFF
                            )
                    }
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
                KnobNavigationUtils.setBackPress()
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it, R.id.action_cavityFragment_to_preferenceFragment, null, null
                    )
                }
            }

            ICON_TYPE_RIGHT -> {
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

    override fun onHMILeftKnobClick() {
        super.onHMILeftKnobClick()
        KnobNavigationUtils.knobForwardTrace = false
        KnobNavigationUtils.knobBackTrace = true
        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
            NavigationUtils.navigateSafely(
                it, R.id.action_cavityFragment_to_preferenceFragment, null, null
            )
        }
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
        //on Radio button click listener
        onViewClickTile(position)
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if (CookingViewModelFactory.getInScopeViewModel() == null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, cookingViewModel)
            return
        }
        if (cookingViewModel != null) {
            CookingViewModelFactory.setInScopeViewModel(cookingViewModel)
            launchProbeDetectedPopupAsPerVariant(cookingViewModel)
        }
    }

    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cavityLightList?.clear()
        cavityLightList = null
    }
}
