/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.digital_unboxing

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.RegionalSettingsListViewHolderInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentRegionalSettingsBinding
import com.whirlpool.hmi.utils.BuildInfo
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.SettingsManagerUtils.DateFormatSettings
import core.utils.SettingsManagerUtils.TemperatureFormatSettings
import core.utils.SettingsManagerUtils.TimeFormatSettings
import core.utils.SettingsManagerUtils.WeightFormatSettings
import core.utils.SettingsManagerUtils.isBleProvisionSuccess
import core.utils.ToolsMenuJsonKeys
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_DATE_FORMAT
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_TEMPERATURE_UNIT
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_TIME_FORMAT
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_WEIGHT_UNIT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * File        : android.presenter.fragments.digital_unboxing.UnboxingRegionalSettingsFragment
 * Author      : Nikki Gharde
 * Created On  : 3.Sep.2024
 * Details     : Instance of super abstract fragment to represent the regional settings options.
 */
class UnBoxingRegionalSettingsFragment : SuperAbstractTimeoutEnableFragment(),
    RegionalSettingsListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener {

    private var isUnboxing: Boolean = false
    private var regionalListTileDataList: ArrayList<ListTileData>? = null
    private var fragmentRegionalSettingsBinding: FragmentRegionalSettingsBinding? = null

    private var allItemSize = 0
    private var currentPosition = -1
    private var lastItemSelectedPos = -1

    private var regionalSettingsList: ArrayList<String>? = null
    private var temperatureList: ArrayList<String>? = null
    private var weightList: ArrayList<String>? = null
    private var timeFormatList: ArrayList<String>? = null
    private var dateFormatList: ArrayList<String>? = null
    private var listItemsSize: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentRegionalSettingsBinding = FragmentRegionalSettingsBinding.inflate(inflater)
        fragmentRegionalSettingsBinding?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentRegionalSettingsBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        init()
        setTimeoutApplicable(!isUnboxing)
        setMeatProbeApplicable(!isUnboxing)
        manageHeaderBar()
        onNextButtonClick()
        onSetUpLaterButtonClick()
        listItemsSize = provideListRecyclerViewTilesData().size
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highlightSelectedItem()
        }
    }

    private fun highlightSelectedItem() {
        fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.post {
            val viewHolderOld =
                fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }
    }


    /**
     * header bar UI populate as per requirement for unboxing
     */
    private fun manageHeaderBar() {
        fragmentRegionalSettingsBinding?.headerBar?.apply {
            setTitleText(getString(R.string.regional_settings))
            setOvenCavityIconVisibility(false)
            if (isBleProvisionSuccess) {
                setLeftIconVisibility(false)
            }else {
                setLeftIconVisibility(true)
                setLeftIcon(R.drawable.ic_back_arrow)
            }
            setRightIconVisibility(false)
            setInfoIconVisibility(false)
            setCustomOnClickListener(this@UnBoxingRegionalSettingsFragment)
        }
    }

    /**
     * On next button click - save regional settings into settings model object
     */
    private fun onNextButtonClick() {
        fragmentRegionalSettingsBinding?.btnPrimary?.setOnClickListener {
            setDefaultSettings()
            if (isBleProvisionSuccess && !BuildInfo.isRunningOnEmulator()) {
                HMILogHelper.Logd(
                    "Unboxing",
                    "Unboxing: Internet is connected - Navigate to the unboxing congratulation screen"
                )
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it,
                        R.id.action_unboxingRegionalSettingsFragment_to_unboxingDoneCongratulationFragment,
                        null,
                        null
                    )
                }
            } else {
                val timeFormat: TimeFormatSettings = SettingsManagerUtils.getTimeFormat()
                HMILogHelper.Logd("unboxing", "timeFormat=$timeFormat")
                HMILogHelper.Logd(
                    "Unboxing",
                    "Unboxing: Navigate to set time fragment"
                )
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it,
                        R.id.action_unboxingRegionalSettingsFragment_to_time12HrsTumblerFragment,
                        null,
                        null
                    )
                }
            }
        }
    }


    private fun onSetUpLaterButtonClick() {
        fragmentRegionalSettingsBinding?.navigationButtonLeft?.setOnClickListener {
            setDefaultSettings()
            HMILogHelper.Logd(
                "Unboxing",
                "Unboxing: Set up later - Navigate to the unboxing congratulation screen"
            )
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                NavigationUtils.navigateSafely(
                    it,
                    R.id.action_unboxingRegionalSettingsFragment_to_unboxingDoneCongratulationFragment,
                    null,
                    null
                )
            }
        }
    }
    private fun setDefaultSettings() {
        if (regionalListTileDataList?.isNotEmpty() == true) {
            for (itemPosition in 0 until allItemSize) {
                val listItemModel: ListTileData? = regionalListTileDataList?.get(itemPosition)
                if (listItemModel != null) {
                    val underLineButton = listItemModel.underLineButton
                    when (itemPosition) {
                        JSON_KEY_TOOLS_TEMPERATURE_UNIT -> when {
                            underLineButton.isChecked -> SettingsManagerUtils.setTemperatureFormat(TemperatureFormatSettings.FAHRENHEIT)
                            else -> SettingsManagerUtils.setTemperatureFormat(TemperatureFormatSettings.CELSIUS)
                        }
                        JSON_KEY_TOOLS_WEIGHT_UNIT -> when {
                            underLineButton.isChecked -> SettingsManagerUtils.setWeightUnitFormat(WeightFormatSettings.IMPERIAL)
                            else -> SettingsManagerUtils.setWeightUnitFormat(WeightFormatSettings.METRIC)
                        }
                        JSON_KEY_TOOLS_TIME_FORMAT -> when {
                            underLineButton.isChecked -> SettingsManagerUtils.setTimeFormat(TimeFormatSettings.H_12)
                            else -> SettingsManagerUtils.setTimeFormat(TimeFormatSettings.H_24)
                        }
                        JSON_KEY_TOOLS_DATE_FORMAT -> when {
                            underLineButton.isChecked -> SettingsManagerUtils.setDateFormat(DateFormatSettings.MMDDYY)
                            else -> SettingsManagerUtils.setDateFormat(DateFormatSettings.DDMMYY)
                        }
                    }
                }
            }
        }
    }

    /**
     * Init settings
     */
    private fun init() {
        fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.
        setPadding(resources.getInteger(R.integer.integer_range_14),0,
            resources.getInteger(R.integer.integer_range_21),
            resources.getDimension(R.dimen.regional_settings_recyclerview_padding_bottom).toInt())
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
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_REGIONAL_SETTINGS)
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
                when (i) {
                    JSON_KEY_TOOLS_TEMPERATURE_UNIT -> populateTemperatureSettings(underlineButtonData, i, builder)
                    JSON_KEY_TOOLS_WEIGHT_UNIT ->  populateWeightSettings(underlineButtonData, builder)
                    JSON_KEY_TOOLS_TIME_FORMAT -> populateTimeFormatSettings(underlineButtonData, builder)
                    JSON_KEY_TOOLS_DATE_FORMAT -> populateDateFormatSettings(underlineButtonData, builder)
                }

                builder.itemIconVisibility = View.GONE
                builder.rightTextVisibility = View.GONE
                builder.rightIconVisibility = View.GONE
                builder.subTextVisibility = View.VISIBLE
                builder.titleTextVisibility = View.VISIBLE
                builder.itemViewVisibility = View.VISIBLE
                builder.isPaddingView = false
                regionalListTileDataList?.add(builder)
            }
            return regionalListTileDataList ?: arrayListOf()
        }
        return arrayListOf()
    }

    /**
     * populate the date former settings
     */
    private fun populateDateFormatSettings(
        underlineButtonData: ListTileData.UnderLineButtonData,
        builder: ListTileData
    ) {
        val dateFormat: DateFormatSettings = SettingsManagerUtils.getDateFormat()
        HMILogHelper.Logd("unboxing", "dateFormat=$dateFormat")
        //check toggle if date format is MM/DD/YY
        underlineButtonData.isChecked = dateFormat === DateFormatSettings.MMDDYY

        //get and set toggle left and right text
        underlineButtonData.leftButtonText = getUnderLineLeftRightTitleText(dateFormatList, 0)?.lowercase().toString()
        underlineButtonData.rightButtonText = getUnderLineLeftRightTitleText(dateFormatList, 1)?.lowercase().toString()

        //get and set Subtitle text
        var position = 1
        if (underlineButtonData.isChecked) position = 0
        builder.subText = getSubTitleText(dateFormatList, position)

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
        HMILogHelper.Logd("unboxing", "timeFormat=$timeFormat")
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
        HMILogHelper.Logd("unboxing", "weightFormat=$weightFormat")
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
        i: Int,
        builder: ListTileData
    ) {
        val temperatureFormat: TemperatureFormatSettings =
            SettingsManagerUtils.getTemperatureFormat()
        HMILogHelper.Logd("unboxing", "temperatureFormat=$temperatureFormat")
        //check toggle if temperature is FAHRENHEIT
        underlineButtonData.isChecked = temperatureFormat === TemperatureFormatSettings.FAHRENHEIT

        //get and set toggle left and right text
        underlineButtonData.leftButtonText = getUnderLineLeftRightTitleText(temperatureList, i)
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
        //on item list click listener
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
            when (currentPosition) {
                in 0 until listItemsSize -> {
                    val currentItem = regionalListTileDataList?.get(lastItemSelectedPos)
                    val currentState = currentItem?.underLineButton?.isChecked ?: false
                    updateListItemOnClick(lastItemSelectedPos, !currentState)
                }

                listItemsSize -> {
                    KnobNavigationUtils.knobForwardTrace = true
                    fragmentRegionalSettingsBinding?.navigationButtonLeft?.callOnClick()
                }

                listItemsSize + 1 -> {
                    KnobNavigationUtils.knobForwardTrace = true
                    fragmentRegionalSettingsBinding?.btnPrimary?.callOnClick()
                }
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
        knobLeftAndRightClickEvent()
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
                    handleLeftKnobRotation(knobDirection)
                } else if (knobId == AppConstants.RIGHT_KNOB_ID) {
                    PopUpBuilderUtils.userLeftKnobWarningPopup(this@UnBoxingRegionalSettingsFragment)
                }
            }
        }
    }

    private fun handleLeftKnobRotation(knobDirection: String) {
        currentPosition = CookingAppUtils.getKnobPositionIndex(
            knobDirection,
            currentPosition,
            listItemsSize + 2
        )

        when (currentPosition) {
            in 0 until listItemsSize -> {
                updateRecyclerViewForPosition()
            }
            in listItemsSize until listItemsSize + 2 -> {
                updateButtonBackgroundsForSpecialPositions(listItemsSize)
            }
            else -> {
                resetCurrentPosition()
            }
        }
    }

    private fun updateRecyclerViewForPosition() {
        fragmentRegionalSettingsBinding?.navigationButtonLeft?.background = getRippleEffectDrawable()
        fragmentRegionalSettingsBinding?.btnPrimary?.background = getRippleEffectDrawable()

        HMILogHelper.Logd("LEFT_KNOB: rotate right current knob index = $currentPosition")
        fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.smoothScrollToPosition(currentPosition)

        fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.postDelayed({
            updateLastSelectedPosition()
        }, 50) // Adjust delay as needed
    }

    private fun updateLastSelectedPosition() {
        if (lastItemSelectedPos != -1) {
            val viewHolder = fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
            viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
        }
        lastItemSelectedPos = currentPosition
        val viewHolderOld = fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
        viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
    }

    private fun updateButtonBackgroundsForSpecialPositions(listItemsSize: Int) {
        val viewHolder = fragmentRegionalSettingsBinding?.regionalSettingsRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
        viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
        when (currentPosition) {
            listItemsSize -> {
                fragmentRegionalSettingsBinding?.btnPrimary?.background = null
                fragmentRegionalSettingsBinding?.navigationButtonLeft?.background = getWalnutSelectorDrawable()
            }
            listItemsSize + 1 -> {
                fragmentRegionalSettingsBinding?.navigationButtonLeft?.background = null
                fragmentRegionalSettingsBinding?.btnPrimary?.background = getWalnutSelectorDrawable()
            }
        }
    }

    private fun resetCurrentPosition() {
        HMILogHelper.Logd("LEFT_KNOB: rotate left current knob index = $currentPosition")
        currentPosition = 0
    }

    private fun getRippleEffectDrawable() =
        ResourcesCompat.getDrawable(resources, R.drawable.text_view_ripple_effect, null)

    private fun getWalnutSelectorDrawable() =
        ResourcesCompat.getDrawable(resources, R.drawable.selector_textview_walnut, null)

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
                fragmentRegionalSettingsBinding?.navigationButtonLeft?.background = getRippleEffectDrawable()
                fragmentRegionalSettingsBinding?.btnPrimary?.background = getRippleEffectDrawable()
            }
            lastItemSelectedPos = -1
        }
    }


    /**
     * common function for knob left and right click event
     */
    private fun knobLeftAndRightClickEvent() {
        PopUpBuilderUtils.userLeftKnobWarningPopup(this)
    }

    /**
     * Method to update the user checked choices to reflect in the underline button in menu list
     *
     * @param position position of the tile in the list
     */
    private fun updateListItemOnClick(position: Int, isChecked: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            var isNeedToNotify = false
            regionalListTileDataList?.let {
                val selectedItem: ListTileData = it[position]
                for (i in 0 until it.size) {
                    val listItemModel: ListTileData = it[i]
                    if (selectedItem == listItemModel) {
                        isNeedToNotify = populateSettings(i, isChecked, listItemModel)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                if (isNeedToNotify) notifyItemChanged(position)
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
            JSON_KEY_TOOLS_TEMPERATURE_UNIT -> {
                //get and set Subtitle text
                var position = 1
                if (isChecked) position = 0
                listItemModel.subText = getSubTitleText(temperatureList, position)
            }

            JSON_KEY_TOOLS_WEIGHT_UNIT -> {
                //get and set Subtitle text
                var position = 1
                if (isChecked) position = 0
                listItemModel.subText = getSubTitleText(weightList, position)
            }

            JSON_KEY_TOOLS_TIME_FORMAT -> {
                //get and set Subtitle text
                var position = 1
                if (isChecked) position = 0
                listItemModel.subText = getSubTitleText(timeFormatList, position)
            }

            JSON_KEY_TOOLS_DATE_FORMAT -> {
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
        //in case of wifi connection success do not show back arrow and navigation
        if (!isBleProvisionSuccess) {
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                NavigationUtils.navigateSafely(
                    it,
                    R.id.action_unboxingRegionalSettingsFragment_to_unboxingConnectToNetworkFragment,
                    null,
                    null
                )
            }
        }
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        fragmentRegionalSettingsBinding = null
        allItemSize = 0
        listItemsSize = 0
        super.onDestroyView()
    }
}