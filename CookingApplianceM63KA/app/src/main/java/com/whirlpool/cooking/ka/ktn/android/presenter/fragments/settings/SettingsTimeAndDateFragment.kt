package android.presenter.fragments.settings

import android.content.Context
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.SettingsListViewHolderInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.toggleswitch.ToggleSwitch
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentToolsListMenuBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.settings.SettingsViewModel.getSettingsViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.DATE_TIME
import core.utils.AppConstants.EMPTY_STRING
import core.utils.AppConstants.POWERLOSS_TIME_DATE_UPDATE_POPUP
import core.utils.AppConstants.SETTINGLANDING_FRAGMENT
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * File        : android.presenter.fragments.settings.SettingsSoundFragment
 * Brief       : Instance of Abstract List fragment to represent the Sound Menu List Screen
 * Author      : Vijay Shinde
 * Created On  : 03-October-2024
 * Details     : Instance of Abstract List fragment to represent the Sound Menu List Screen.
 */
class SettingsTimeAndDateFragment : SuperAbstractTimeoutEnableFragment(),
    SettingsListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener {
    private var settingsSubList: ArrayList<ListTileData>? = null
    private var canScrollVertically = true
    private var fragmentTimeAndDate: FragmentToolsListMenuBinding? = null
    private var lastItemSelectedPos = -1
    private var currentPosition = -1
    private var allItemSize = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        fragmentTimeAndDate = FragmentToolsListMenuBinding.inflate(inflater)
        fragmentTimeAndDate?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentTimeAndDate?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        initHeaderBar()
        manageListView()
        if (KnobNavigationUtils.knobBackTrace) {
            KnobNavigationUtils.knobBackTrace = false
            currentPosition = KnobNavigationUtils.lastTimeSelectedData()
            lastItemSelectedPos = KnobNavigationUtils.lastTimeSelectedData()
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

    // Helper function to highlight the selected item
    private fun highlightSelectedItem(position: Int) {
        fragmentTimeAndDate?.recyclerList?.post {
            val viewHolderOld = fragmentTimeAndDate?.recyclerList?.findViewHolderForAdapterPosition(position)
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }
    }

    private fun initHeaderBar() {
        fragmentTimeAndDate?.headerBarPreferences?.apply {
            setLeftIcon(R.drawable.ic_back_arrow)
            setTitleText(resources.getString(R.string.time_and_date))
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setRightIconVisibility(true)
            if (CookingAppUtils.getNavigatedFrom() == POWERLOSS_TIME_DATE_UPDATE_POPUP) {
                setLeftIconVisibility(false)
            }
            setCustomOnClickListener(this@SettingsTimeAndDateFragment) // Replace 'YourClassName' with your actual class name
        }
    }

    private fun manageListView() {
        fragmentTimeAndDate?.recyclerList?.isVerticalScrollBarEnabled = false
        canScrollVertically = false
        fragmentTimeAndDate?.recyclerList?.layoutManager = LinearLayoutManagerForScrolling(
            context
        )
        fragmentTimeAndDate?.recyclerList?.visibility = View.VISIBLE
        val listTileData = provideListRecyclerViewTilesData()
        allItemSize = listTileData?.size ?: 0
        listTileData.let {
            val listItems: ArrayList<Any> = ArrayList(it!!)
            val toolsListViewInterface =
                listTileData?.let { it1 ->
                    SettingsListViewHolderInterface(
                        it1, this
                    )
                }
            fragmentTimeAndDate?.recyclerList?.setupListWithObjects(
                listItems,
                toolsListViewInterface
            )
        }
    }

    /**
     * Method to initialise the list view
     */
    fun provideListRecyclerViewTilesData(): ArrayList<ListTileData>? {
        settingsSubList = ArrayList()
        fragmentTimeAndDate?.recyclerList?.visibility = View.VISIBLE
        addSetTime()
        addSetDate()
        return settingsSubList
    }

    /**
     * Coverts time from 24Hr(HH:MM) into 12Hr (HH:MM AM/PM)
     * @param time time in string (HH:MM)
     * @return time in HH:MM AM/PM
     */
    private fun convertTimeAMPM(time: String): String {
        val format: String

        // Parsing hours, minutes and seconds in array
        val arr = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        // Converting hours into integer
        var hh = arr[0].toInt()
        if (hh > DATE_TIME) {
            hh -= DATE_TIME
            format = resources.getString(R.string.str_clock_pm)
        } else if (hh == 0) {
            hh = 12
            format = resources.getString(R.string.str_clock_am)
        } else if (hh == DATE_TIME) {
            hh = DATE_TIME
            format = resources.getString(R.string.str_clock_pm)
        } else {
            format = resources.getString(R.string.str_clock_am)
        }

        // Converting hh to String and
        // padding it with 0 on left side
        val hour = String.format("%02d", hh)
        val min = arr[1].toInt()
        val minute = if (min >= 10) min.toString() else "0$min"
        return "$hour:$minute $format"
    }

    /**
     * This function return the time
     */
    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance()
        val hour = currentTime[Calendar.HOUR_OF_DAY]
        val minute = currentTime[Calendar.MINUTE]
        val timeFormat: SettingsManagerUtils.TimeFormatSettings = SettingsManagerUtils.getTimeFormat()
        val hr = if (hour >= 10) hour.toString() else "0$hour"
        val min = if (minute >= 10) minute.toString() else "0$minute"
        return if (timeFormat === SettingsManagerUtils.TimeFormatSettings.H_12) {
            convertTimeAMPM("$hr:$minute")
        } else {
            "$hr:$min"
        }
    }

    private fun isConnectedToCloud(): Boolean {
        return (getSettingsViewModel().wifiConnectState.value == SettingsViewModel.WifiConnectState.CONNECTED
                && getSettingsViewModel().provisionedWifiSsid != null
                && getSettingsViewModel().awsConnectionStatus.value != SettingsViewModel.CloudConnectionState.IDLE)
    }

    private fun getRightIcon(): Int {
        return if (isConnectedToCloud()) {
            R.drawable.ic_40px_connected
        } else {
            R.drawable.icon_list_item_right_arrow
        }
    }

    /**
     * Method to initialise the Set Time tile
     */
    private fun addSetTime() {
        val builder = ListTileData()
        builder.apply {
            titleText = resources.getString(R.string.text_header_set_time)
            titleTextVisibility = View.VISIBLE
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            rightTextVisibility = View.GONE
            rightClockTextVisibility = View.VISIBLE
            itemViewVisibility = View.VISIBLE
            rightIconID = getRightIcon()
            rightIconVisibility = View.VISIBLE
            isItemEnabled = true
        }
        settingsSubList?.add(builder)
    }

    /**
     *  This function return the date
     */
    private fun getCurrentDate(): String {
        val currentTime = Calendar.getInstance()
        val currentDate = currentTime[Calendar.DATE]
        val currentMonth = currentTime[Calendar.MONTH] + 1
        val currentYear = currentTime[Calendar.YEAR]
        val date = if (currentDate >= 10) currentDate.toString() else "0$currentDate"
        val month = if (currentMonth >= 10) currentMonth.toString() else "0$currentMonth"
        val subYear = currentYear.toString()
        val year = subYear.substring(subYear.length - 2)
        val dateFormat: SettingsManagerUtils.DateFormatSettings = SettingsManagerUtils.getDateFormat()
        return if (dateFormat === SettingsManagerUtils.DateFormatSettings.DDMMYY) {
            "$date/$month/$year"
        } else {
            "$month/$date/$year"
        }
    }

    /**
     * Method to initialise the Set Date tile
     */
    private fun addSetDate() {
        val builder = ListTileData()
        builder.apply {
            titleText = getString(R.string.text_header_set_date)
            titleTextVisibility = View.VISIBLE
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            rightTextVisibility = View.GONE
            itemViewVisibility = View.VISIBLE
            rightText = getCurrentDate()
            rightTextVisibility = View.VISIBLE
            rightIconVisibility = View.VISIBLE
            rightIconID = getRightIcon()
            listItemDividerViewVisibility = View.GONE
            isItemEnabled = true
        }
        settingsSubList?.add(builder)
    }

    private fun getTimeValue(): String {
        val time = getCurrentTime()
        return time.filter { it.isLetterOrDigit() }
    }

    override fun onListViewItemClick(view: View?, position: Int) {
        if (!isConnectedToCloud()) {
            val timeValue = getTimeValue()
            val bundle = Bundle()
            AudioManagerUtils.playOneShotSound(
                view?.context,
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            var navId: Int = R.id.action_settingsTimeAndDateFragment_to_settingsSetTimeTumblerFragment
            if (position == 0) {
                bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, timeValue)
                navId = R.id.action_settingsTimeAndDateFragment_to_settingsSetTimeTumblerFragment
            }
            if (position == 1) {
                navId = R.id.action_settingsTimeAndDateFragment_to_settingsSetDateTumblerFragment
            }
            NavigationUtils.navigateSafely(
                this,
                navId,
                bundle,
                null
            )
        }
    }

    private inner class LinearLayoutManagerForScrolling(context: Context?) :
        LinearLayoutManager(context) {
        override fun canScrollVertically(): Boolean {
            return canScrollVertically && super.canScrollVertically()
        }
    }

    override fun leftIconOnClick() {
        if(CookingAppUtils.getNavigatedFrom() == AppConstants.NAVIGATION_FROM_NOTIFICATION){
            CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
            CookingAppUtils.setNavigatedFrom(EMPTY_STRING)
        }
        else {
            if (CookingAppUtils.getNavigatedFrom() != POWERLOSS_TIME_DATE_UPDATE_POPUP) {
                KnobNavigationUtils.setBackPress()
                val navId: Int =
                    if (CookingAppUtils.getNavigatedFrom() == SETTINGLANDING_FRAGMENT) {
                        R.id.action_settingsTimeAndDateFragment_to_settingsLandingFragment
                    } else {
                        R.id.action_settingsTimeAndDateFragment_to_settingsPreferencesFragment
                    }
                CookingAppUtils.setNavigatedFrom(EMPTY_STRING)
                NavigationUtils.navigateSafely(this, navId, null, null)
            }
        }
    }

    override fun rightIconOnClick() {
        CookingAppUtils.setNavigatedFrom(EMPTY_STRING)
        CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return resources.getInteger(R.integer.session_long_timeout)
    }

    override fun onHMILeftKnobClick() {
        KnobNavigationUtils.addTraversingData(lastItemSelectedPos, false)
        KnobNavigationUtils.knobForwardTrace = true
        if ((lastItemSelectedPos != -1) && !isConnectedToCloud()) {
            lifecycleScope.launch(Dispatchers.Main) {
                val toggle =
                    fragmentTimeAndDate?.recyclerList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )?.itemView?.findViewById<ToggleSwitch>(R.id.settings_item_toggle_switch)

                if (toggle?.visibility == View.VISIBLE) {
                    val toggleSwitch = toggle.findViewById<SwitchCompat>(R.id.toggle_switch)
                    toggleSwitch.isChecked = !toggleSwitch.isChecked
                } else {
                    fragmentTimeAndDate?.recyclerList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )?.itemView?.findViewById<ConstraintLayout>(R.id.list_item_main_view)
                        ?.callOnClick()
                }

            }
        }
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (knobId == AppConstants.LEFT_KNOB_ID) {
                    currentPosition = CookingAppUtils.getKnobPositionIndex(
                        knobDirection,
                        currentPosition,
                        allItemSize
                    )
                    if (currentPosition >= 0 && getSettingsViewModel()?.isWifiEnabled == true) {
                        HMILogHelper.Logd(
                            "Knob",
                            "LEFT_KNOB: rotate right current knob index = $currentPosition"
                        )
                        fragmentTimeAndDate?.recyclerList?.smoothScrollToPosition(
                            currentPosition
                        )
                        highLightSelectedTiles()

                    } else {
                        HMILogHelper.Logd("Knob", "LEFT_KNOB: rotate left current knob index = $currentPosition")
                        currentPosition = 0
                        highLightSelectedTiles()
                    }
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (lastItemSelectedPos != -1) {
                val viewHolder = fragmentTimeAndDate?.recyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = -1
        }
    }

    private fun highLightSelectedTiles() {
        fragmentTimeAndDate?.recyclerList?.postDelayed({
            if (lastItemSelectedPos != -1) {
                val viewHolder = fragmentTimeAndDate?.recyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = currentPosition
            val viewHolderOld = fragmentTimeAndDate?.recyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }, 50) // Adjust delay as needed
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
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        currentPosition = -1
        allItemSize = 0
        super.onDestroyView()
    }
}