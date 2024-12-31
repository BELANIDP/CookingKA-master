/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.settings

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.RegionalSettingsListViewHolderInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentRegionalSettingsBinding
import com.whirlpool.hmi.settings.SettingsViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppLanguageDetails.Companion.getLanguageNameByCode
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.KnobNavigationUtils.Companion.knobBackTrace
import core.utils.KnobNavigationUtils.Companion.lastTimeSelectedData
import core.utils.MeatProbeUtils
import core.utils.NavigationUtils
import core.utils.SettingsManagerUtils
import core.utils.SettingsManagerUtils.DateFormatSettings
import core.utils.SettingsManagerUtils.TemperatureFormatSettings
import core.utils.SettingsManagerUtils.TimeFormatSettings
import core.utils.SettingsManagerUtils.WeightFormatSettings
import core.utils.ToolsMenuJsonKeys
import core.utils.ToolsMenuJsonKeys.JSON_KEY_SETTINGS_DATE_FORMAT
import core.utils.ToolsMenuJsonKeys.JSON_KEY_SETTINGS_LANGUAGE
import core.utils.ToolsMenuJsonKeys.JSON_KEY_SETTINGS_TEMPERATURE_UNIT
import core.utils.ToolsMenuJsonKeys.JSON_KEY_SETTINGS_TIME_FORMAT
import core.utils.ToolsMenuJsonKeys.JSON_KEY_SETTINGS_WEIGHT_UNIT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * File        : android.presenter.fragments.settings.SettingsRegionalSettingsFragment
 * Author      : Vijay Shinde
 * Created On  : 16/10/2024
 * Details     : Instance of super abstract fragment to represent the regional settings options in Settings menu.
 */
class SettingsRegionalSettingsFragment : SuperAbstractTimeoutEnableFragment(),
        RegionalSettingsListViewHolderInterface.ListItemClickListener,
        HeaderBarWidgetInterface.CustomClickListenerInterface,
        HMIKnobInteractionListener {

    private var isUnboxing: Boolean = false
    private var regionalListTileDataList: ArrayList<ListTileData>? = null
    private var fragmentRegionalSettingsBinding: FragmentRegionalSettingsBinding? = null
    private var currentPosition = -1
    private var lastItemSelectedPos = -1
    private var allItemSize = 0

    private var regionalSettingsList: ArrayList<String>? = null
    private var temperatureList: ArrayList<String>? = null
    private var weightList: ArrayList<String>? = null
    private var timeFormatList: ArrayList<String>? = null
    private var dateFormatList: ArrayList<String>? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        fragmentRegionalSettingsBinding = FragmentRegionalSettingsBinding.inflate(inflater)
        fragmentRegionalSettingsBinding?.lifecycleOwner = this.viewLifecycleOwner
        fragmentRegionalSettingsBinding?.let {
            it.btnPrimary.visibility = View.GONE
            it.navigationButtonLeft.visibility = View.GONE
        }

        return fragmentRegionalSettingsBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        init()
        setTimeoutApplicable(!isUnboxing)
        setMeatProbeApplicable(!isUnboxing)
        manageHeaderBar()
        if (knobBackTrace) {
            knobBackTrace = false
            currentPosition = lastTimeSelectedData()
            lastItemSelectedPos = lastTimeSelectedData()
            highlightSelectedItem(lastItemSelectedPos)
        } else if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highlightSelectedItem(lastItemSelectedPos)
        }
        if (KnobNavigationUtils.isBackPress()) {
            KnobNavigationUtils.removeLastAction()
        }
    }

    private fun highlightSelectedItem(position: Int) {
        fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.postDelayed({
            val viewHolderOld =
                fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.findViewHolderForAdapterPosition(
                    position
                )
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }, 150)
    }

    /**
     * header bar UI populate as per requirement for unboxing
     */
    private fun manageHeaderBar() {
        fragmentRegionalSettingsBinding?.headerBar?.apply {
            setTitleText(resources.getString(R.string.regional_settings))
            setOvenCavityIconVisibility(false)
            setLeftIconVisibility(true)
            setLeftIcon(R.drawable.ic_back_arrow)
            setRightIconVisibility(true)
            setRightIcon(R.drawable.ic_close)
            setInfoIconVisibility(false)
            setCustomOnClickListener(this@SettingsRegionalSettingsFragment)
        }
    }

    /**
     * On option selection - save regional settings into settings model object
     */
    private fun onOptionSelection(itemPosition: Int) {
        val listItemModel: ListTileData? = regionalListTileDataList?.get(itemPosition)
        if (listItemModel != null) {
            val underLineButton = listItemModel.underLineButton
            when (itemPosition) {
                JSON_KEY_SETTINGS_TEMPERATURE_UNIT -> when {
                    underLineButton.isChecked -> SettingsManagerUtils.setTemperatureFormat(TemperatureFormatSettings.FAHRENHEIT)
                    else -> SettingsManagerUtils.setTemperatureFormat(TemperatureFormatSettings.CELSIUS)
                }

                JSON_KEY_SETTINGS_WEIGHT_UNIT -> when {
                    underLineButton.isChecked -> SettingsManagerUtils.setWeightUnitFormat(WeightFormatSettings.IMPERIAL)
                    else -> SettingsManagerUtils.setWeightUnitFormat(WeightFormatSettings.METRIC)
                }

                JSON_KEY_SETTINGS_TIME_FORMAT -> when {
                    underLineButton.isChecked -> SettingsManagerUtils.setTimeFormat(TimeFormatSettings.H_12)
                    else -> SettingsManagerUtils.setTimeFormat(TimeFormatSettings.H_24)
                }

                JSON_KEY_SETTINGS_DATE_FORMAT -> when {
                    underLineButton.isChecked -> SettingsManagerUtils.setDateFormat(DateFormatSettings.MMDDYY)
                    else -> SettingsManagerUtils.setDateFormat(DateFormatSettings.DDMMYY)
                }
            }
        }
    }

    /**
     * Init settings
     */
    private fun init() {
        fragmentRegionalSettingsBinding?.gradient?.visibility = View.GONE
        fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.setPadding(resources.getInteger(R.integer.integer_range_14),0,resources.getInteger(R.integer.integer_range_21),0)
        isUnboxing = SettingsManagerUtils.isUnboxing
        regionalSettingsList = arrayListOf()
        lifecycleScope.launch(Dispatchers.IO) {
            parseJsonAndLoadIntoList()
            withContext(Dispatchers.Main) {
                manageListRecyclerView()
            }
        }
    }

    /**
     * parse json via key and load data into list
     */
    private fun parseJsonAndLoadIntoList() {
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SHOW_ALL_REGIONAL_SETTINGS)
                ?.let { regionalSettingsList = it }
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_TEMPERATURE_UNIT)
                ?.let { temperatureList = it }
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_WEIGHT_UNIT)
                ?.let { weightList = it }
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_TIME_FORMAT)
                ?.let { timeFormatList = it }
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_DATE_FORMAT)
                ?.let {dateFormatList = it }
    }
    /**
     * populate the regional list options and bind into the recycler view
     */
    private fun manageListRecyclerView() {
        regionalSettingsList?.let {
            fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.visibility = View.VISIBLE
            val listTileData = provideListRecyclerViewTilesData()
            allItemSize = listTileData.size
            listTileData.let {
                val listItems: ArrayList<Any> = ArrayList(it)
                val toolsListViewInterface =
                        RegionalSettingsListViewHolderInterface(
                                listTileData, this
                        )
                fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.setupListWithObjects(
                        listItems,
                        toolsListViewInterface
                )
            }
        }
    }

    /**
     * provide recycler view data to list view
     *
     * @return list of user role list
     */
    fun provideListRecyclerViewTilesData(): ArrayList<ListTileData> {
        val listViewItems = regionalSettingsList?.size
        if (regionalListTileDataList != null && regionalListTileDataList?.isNotEmpty() == true) {
            regionalListTileDataList?.clear()
        } else {
            regionalListTileDataList = ArrayList()
        }
        return provideRegionalListOptions(listViewItems)
    }


    /**
     * provide regional list
     *
     * @param listItemSize available regional list size
     * @return user list items
     */
    @SuppressLint("DiscouragedApi")
    private fun provideRegionalListOptions(listItemSize: Int?): ArrayList<ListTileData> {
        if (listItemSize != null) {
            for (i in 0 until listItemSize) {
                val builder = ListTileData()
                builder.titleText = getTitleText(i)
                val radioData = ListTileData.RadioButtonData()
                radioData.visibility = View.GONE
                builder.radioButtonData = radioData

                val underlineButtonData = ListTileData.UnderLineButtonData()
                underlineButtonData.visibility = View.VISIBLE

                builder.apply {
                    itemIconVisibility = View.GONE
                    rightTextVisibility = View.GONE
                    rightIconVisibility = View.GONE
                    subTextVisibility = View.VISIBLE
                    titleTextVisibility = View.VISIBLE
                    itemViewVisibility = View.VISIBLE
                    isPaddingView = false
                }
                when (i) {
                    JSON_KEY_SETTINGS_LANGUAGE -> populateLanguageSettings(underlineButtonData, builder)
                    JSON_KEY_SETTINGS_TEMPERATURE_UNIT -> populateTemperatureSettings(underlineButtonData, builder)
                    JSON_KEY_SETTINGS_WEIGHT_UNIT ->  populateWeightSettings(underlineButtonData, builder)
                    JSON_KEY_SETTINGS_TIME_FORMAT -> populateTimeFormatSettings(underlineButtonData, builder)
                    JSON_KEY_SETTINGS_DATE_FORMAT -> populateDateFormatSettings(underlineButtonData, builder)
                }
                regionalListTileDataList?.add(builder)
            }
            return regionalListTileDataList ?: arrayListOf()
        }
        return arrayListOf()
    }

    @SuppressLint("DiscouragedApi")
    private fun getStoredLanguage() :String {
        return resources.getString(
                resources.getIdentifier(AppConstants.TEXT_TILE_LIST +
                        getLanguageNameByCode(SettingsViewModel.getSettingsViewModel().appLanguage.value.toString()),
                        AppConstants.RESOURCE_TYPE_STRING, requireContext().packageName
                )
        )
    }
    private fun populateLanguageSettings (
            underlineButtonData: ListTileData.UnderLineButtonData,
            builder: ListTileData
    ) {
        underlineButtonData.visibility = View.GONE
        builder.apply {
            rightTextVisibility = View.VISIBLE
            rightText = getStoredLanguage()
            rightIconVisibility = View.VISIBLE
            rightIconID = R.drawable.icon_list_item_right_arrow
            subTextVisibility = View.GONE
        }
    }
    /**
     * populate the date former settings
     */
    private fun populateDateFormatSettings(
            underlineButtonData: ListTileData.UnderLineButtonData,
            builder: ListTileData
    ) {
        val dateFormat: DateFormatSettings = SettingsManagerUtils.getDateFormat()
        //check toggle if date format is MM/DD/YY
        underlineButtonData.isChecked = dateFormat === DateFormatSettings.MMDDYY

        //get and set toggle left and right text
        underlineButtonData.leftButtonText = getUnderLineLeftRightTitleText(dateFormatList, 0).lowercase()
        underlineButtonData.rightButtonText = getUnderLineLeftRightTitleText(dateFormatList, 1).lowercase()

        //get and set Subtitle text
        var position = 1
        if (underlineButtonData.isChecked) position = 0
        builder.subText = getSubTitleText(dateFormatList, position)
        // last element divider not required
        builder.listItemDividerViewVisibility = View.GONE
        builder.underLineButton = underlineButtonData
    }

    /**
     * populate the time format settings
     */
    private fun populateTimeFormatSettings(
            underlineButtonData: ListTileData.UnderLineButtonData,
            builder: ListTileData
    ) {
        val timeFormat: TimeFormatSettings = SettingsManagerUtils.getTimeFormat()
        //check toggle if time format is 12 hr
        underlineButtonData.isChecked = timeFormat === TimeFormatSettings.H_12

        //get and set toggle left and right text
        underlineButtonData.leftButtonText = getUnderLineLeftRightTitleText(timeFormatList, 0)
        underlineButtonData.rightButtonText = getUnderLineLeftRightTitleText(timeFormatList, 1)

        //get and set Subtitle text
        var position = 1
        if (underlineButtonData.isChecked) position = 0
        builder.subText = getSubTitleText(timeFormatList, position)

        builder.underLineButton = underlineButtonData
    }

    /**
     * populate the weight settings
     */
    private fun populateWeightSettings(
            underlineButtonData: ListTileData.UnderLineButtonData,
            builder: ListTileData
    ) {
        val weightFormat: WeightFormatSettings = SettingsManagerUtils.getWeightUnitFormat()

        //check toggle if temperature is IMPERIAL
        underlineButtonData.isChecked = weightFormat === WeightFormatSettings.IMPERIAL

        //get and set toggle left and right text
        underlineButtonData.leftButtonText = getUnderLineLeftRightTitleText(weightList, 0)
        underlineButtonData.rightButtonText = getUnderLineLeftRightTitleText(weightList, 1)

        //get and set Subtitle text
        var position = 1
        if (underlineButtonData.isChecked) position = 0
        builder.subText = getSubTitleText(weightList, position)

        builder.underLineButton = underlineButtonData
    }

    /**
     * populate the temperature settings
     */
    private fun populateTemperatureSettings(
            underlineButtonData: ListTileData.UnderLineButtonData,
            builder: ListTileData
    ) {
        val temperatureFormat: TemperatureFormatSettings =
                SettingsManagerUtils.getTemperatureFormat()

        //check toggle if temperature is FAHRENHEIT
        underlineButtonData.isChecked = temperatureFormat === TemperatureFormatSettings.FAHRENHEIT

        //get and set toggle left and right text
        underlineButtonData.leftButtonText = getUnderLineLeftRightTitleText(temperatureList, 0)
        underlineButtonData.rightButtonText = getUnderLineLeftRightTitleText(temperatureList, 1)

        //get and set Subtitle text
        var position = 1
        if (underlineButtonData.isChecked) position = 0
        builder.subText = getSubTitleText(temperatureList, position)

        builder.underLineButton = underlineButtonData
    }

    @SuppressLint("DiscouragedApi")
    private fun getTitleText(i: Int) = getString(
            resources.getIdentifier(
                    buildString {
                        append(AppConstants.TEXT_TILE_LIST)
                        append(regionalSettingsList?.get(i) ?: 0)
                    },
                    AppConstants.RESOURCE_TYPE_STRING, requireContext().packageName
            )
    )

    @SuppressLint("DiscouragedApi")
    private fun getUnderLineLeftRightTitleText(list: ArrayList<String>?, position:Int): String {
        if (list?.isNotEmpty() == true) {
            return getString(
                    resources.getIdentifier(
                            buildString {
                                append(AppConstants.TEXT_TILE_LIST)
                                append(list[position])
                                append(AppConstants.TEXT_TILE_LIST_VALUE)
                            },
                            AppConstants.RESOURCE_TYPE_STRING, requireContext().packageName
                    )
            )
        }
        return AppConstants.EMPTY_STRING
    }

    @SuppressLint("DiscouragedApi")
    private fun getSubTitleText(list: ArrayList<String>?, position:Int): String {
        if (list?.isNotEmpty() == true) {
            return getString(
                    resources.getIdentifier(
                            buildString {
                                append(AppConstants.TEXT_TILE_LIST)
                                append(list[position])
                            },
                            AppConstants.RESOURCE_TYPE_STRING, requireContext().packageName
                    )
            )
        }
        return AppConstants.EMPTY_STRING
    }

    /**
     * @param view - view
     * @param position - adapter position
     */
    override fun onListViewItemClick(view: View?, position: Int) {
        if (position == JSON_KEY_SETTINGS_LANGUAGE) {
            AudioManagerUtils.playOneShotSound(
                view?.context,
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                NavigationUtils.navigateSafely(
                        it,
                        R.id.action_settingsRegionalSettingsFragment_to_settingsLanguageFragment,
                        null,
                        null
                )
            }
        }
    }

    override fun onToggleSwitchClick(position: Int, isChecked: Boolean) {
        //on UnderLine button click listener
        updateListItemOnClick(position,isChecked)
    }

    /**
     * Called when there is a Left Knob click event
     */
    override fun onHMILeftKnobClick() {
        HMILogHelper.Logd("RegionalSettings", "onHMILeftKnobClick")
        if (lastItemSelectedPos != -1) {
            KnobNavigationUtils.knobForwardTrace = true
            KnobNavigationUtils.addTraversingData(lastItemSelectedPos,false)
            if (lastItemSelectedPos == JSON_KEY_SETTINGS_LANGUAGE) {
                onListViewItemClick(
                    fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList,
                    lastItemSelectedPos
                )
            } else {
                // Check the current state of the item at lastItemSelectedPos
                val currentItem = regionalListTileDataList?.get(lastItemSelectedPos)
                val currentState = currentItem?.underLineButton?.isChecked ?: false
                updateListItemOnClick(lastItemSelectedPos, !currentState)
            }
        }
    }

    /**
     * Called when there is a Long Left Knob click event
     */
    override fun onHMILongLeftKnobPress() {
    }

    /**
     * Called when there is a Right Knob click event
     */
    override fun onHMIRightKnobClick() {
    }

    /**
     * Called when there is a Long Right Knob click event
     */
    override fun onHMILongRightKnobPress() {
    }

    /**
     * Called when there is a Long Right Knob click event
     */
    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    /**
     * Called when there is a knob rotate event on a Knobs
     * @param knobId  knob ID
     * @param knobDirection knob movement direction
     */
    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (knobId == AppConstants.LEFT_KNOB_ID) {
                    currentPosition = CookingAppUtils.getKnobPositionIndex(
                        knobDirection,
                        currentPosition,
                        provideListRecyclerViewTilesData().size
                    )
                    if (currentPosition >= 0) {
                        HMILogHelper.Logd("LEFT_KNOB: rotate right current knob index = $currentPosition")
                        fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.smoothScrollToPosition(
                            currentPosition
                        )
                        fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.postDelayed({
                            if (lastItemSelectedPos != -1) {
                                val viewHolder =
                                    fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.findViewHolderForAdapterPosition(
                                        lastItemSelectedPos
                                    )
                                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
                            }
                            lastItemSelectedPos = currentPosition
                            val viewHolderOld =
                                fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.findViewHolderForAdapterPosition(
                                    lastItemSelectedPos
                                )
                            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
                        }, 50) // Adjust delay as needed

                    } else {
                        HMILogHelper.Logd("LEFT_KNOB: rotate left current knob index = $currentPosition")
                        currentPosition = 0
                    }
                }
            }
        }
    }

    /**
     * Called after 10 sec when there is no interaction on knob
     */
    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (lastItemSelectedPos != -1) {
                val viewHolder =
                    fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = -1
        }
    }

    /**
     * Method to update the user checked choices to reflect in the underline button in menu list
     *
     * @param position position of the tile in the list
     */
    private fun updateListItemOnClick(position: Int,isChecked: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            var isNeedToNotify = false
            regionalListTileDataList?.let {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                val selectedItem: ListTileData = it[position]
                for (i in 0 until it.size) {
                    val listItemModel: ListTileData = it[i]
                    if (selectedItem == listItemModel) {
                        isNeedToNotify = populateSettings(i,isChecked,listItemModel)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                if (isNeedToNotify) {
                    notifyItemChanged(position)
                    onOptionSelection(position)
                }
            }
        }
    }

    /**
     * update the item after click and notify data to recyclerview
     */
    private fun populateSettings(itemPosition: Int,isChecked:Boolean, listItemModel: ListTileData):Boolean {
        var isNeedToNotify = false
        val underLineButton = listItemModel.underLineButton
        underLineButton.visibility = View.VISIBLE
        underLineButton.isEnabled = true
        //get and set toggle left and right text
        if (underLineButton.isChecked != isChecked) {
            underLineButton.isChecked = isChecked
            isNeedToNotify = true
        }
        when (itemPosition) {
            JSON_KEY_SETTINGS_TEMPERATURE_UNIT -> {
                //get and set Subtitle text
                var position = 1
                if (isChecked) position = 0
                listItemModel.subText = getSubTitleText(temperatureList, position)
            }

            JSON_KEY_SETTINGS_WEIGHT_UNIT -> {
                //get and set Subtitle text
                var position = 1
                if (isChecked) position = 0
                listItemModel.subText = getSubTitleText(weightList, position)
            }

            JSON_KEY_SETTINGS_TIME_FORMAT -> {
                //get and set Subtitle text
                var position = 1
                if (isChecked) position = 0
                listItemModel.subText = getSubTitleText(timeFormatList, position)
            }

            JSON_KEY_SETTINGS_DATE_FORMAT -> {
                //get and set Subtitle text
                var position = 1
                if (isChecked) position = 0
                listItemModel.subText = getSubTitleText(dateFormatList, position)
            }
        }
        return isNeedToNotify
    }

    /**
     * Method to notify the item changed for recycler view adapter
     */
    private fun notifyItemChanged(position: Int) {
        if (fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.adapter != null) {
            fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.adapter?.notifyItemChanged(position)
        }
    }

    override fun leftIconOnClick() {
        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
            NavigationUtils.navigateSafely(
                    it,
                    R.id.action_settingsRegionalSettingsFragment_to_preferencesFragment,
                    null,
                    null
            )
        }
    }

    override fun rightIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        MeatProbeUtils.removeMeatProbeListener()
        fragmentRegionalSettingsBinding = null
        allItemSize = 0
        super.onDestroyView()
    }
}
