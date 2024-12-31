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
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.ALERTS_AND_TIMERS
import core.utils.AppConstants.BUTTONS_AND_EFFECTS
import core.utils.AppConstants.DIGIT_ONE
import core.utils.AppConstants.DIGIT_TWO
import core.utils.AppConstants.DIGIT_ZERO
import core.utils.AppConstants.DISPLAY_AND_BRIGHTNESS
import core.utils.AppConstants.DISPLAY_BRIGHTNESS
import core.utils.AppConstants.SOUND_VOLUME
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * File        : android.presenter.fragments.settings.SettingsSoundFragment
 * Brief       : Instance of Abstract List fragment to represent the Sound Menu List Screen
 * Author      : Vijay Shinde
 * Created On  : 03-October-2024
 * Details     : Instance of Abstract List fragment to represent the Sound Menu List Screen.
 */
open class SettingsSoundDisplayFragment : SuperAbstractTimeoutEnableFragment(),
        SettingsListViewHolderInterface.ListItemClickListener,
        HeaderBarWidgetInterface.CustomClickListenerInterface, HMIKnobInteractionListener {
    private var intensityOptionMap : HashMap<String, Long>? = null
    private var soundSettingsList: ArrayList<ListTileData>? = null
    private var canScrollVertically = true
    private var fragmentToolsSoundDisplay: FragmentToolsListMenuBinding? = null
    private var lastItemSelectedPos = -1
    private var currentPosition = -1
    private var allItemSize = 0

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        CookingViewModelFactory.setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        fragmentToolsSoundDisplay = FragmentToolsListMenuBinding.inflate(inflater)
        fragmentToolsSoundDisplay?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentToolsSoundDisplay?.root
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
        fragmentToolsSoundDisplay?.recyclerList?.post {
            val viewHolderOld = fragmentToolsSoundDisplay?.recyclerList?.findViewHolderForAdapterPosition(position)
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }
    }

    private fun getSubListOptions() : Int? {
        return arguments?.getInt(BundleKeys.BUNDLE_SOUND_DISPLAY)
    }

    private fun getTitleString() : String {
        var title = resources.getString(R.string.sound_volume)
        when (getSubListOptions()) {
            SOUND_VOLUME -> title = resources.getString(R.string.sound_volume)
            DISPLAY_AND_BRIGHTNESS -> title = resources.getString(R.string.display_and_brightness)
        }
        return title
    }
    private fun initHeaderBar() {
        fragmentToolsSoundDisplay?.headerBarPreferences?.apply {
            setLeftIcon(R.drawable.ic_back_arrow)
            setTitleText(getTitleString())
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setRightIconVisibility(true)
            setCustomOnClickListener(this@SettingsSoundDisplayFragment) // Replace 'YourClassName' with your actual class name
        }
    }

    private fun manageListView() {

        fragmentToolsSoundDisplay?.recyclerList?.isVerticalScrollBarEnabled = false
        canScrollVertically = false
        fragmentToolsSoundDisplay?.recyclerList?.layoutManager = LinearLayoutManagerForScrolling(
                context
        )
        fragmentToolsSoundDisplay?.recyclerList?.visibility = View.VISIBLE
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
            fragmentToolsSoundDisplay?.recyclerList?.setupListWithObjects(
                    listItems,
                    toolsListViewInterface
            )
        }
    }

    /**
     * Method to initialise the list view
     */
    fun provideListRecyclerViewTilesData(): ArrayList<ListTileData>? {
        soundSettingsList = ArrayList()
        fragmentToolsSoundDisplay?.recyclerList?.visibility = View.VISIBLE
        when (getSubListOptions()) {
            SOUND_VOLUME -> {
                addMute()
                addAlertAndTimers()
                addButtonsAndEffects()
            }

            DISPLAY_AND_BRIGHTNESS -> {
                addDisplayBrightness()
            }

            else -> {}
        }
        return soundSettingsList
    }

    /**
     * Method to initialise the said menu
     */
    private fun addMute() {
        val builder = ListTileData()
        builder.apply {
            titleText = resources.getString(R.string.mute)
            titleTextVisibility = View.VISIBLE
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            rightTextVisibility = View.GONE
            rightIconVisibility = View.GONE
            itemViewVisibility = View.VISIBLE
            isItemEnabled = true
            toggleSwitchData.visibility = View.VISIBLE
            toggleSwitchData.isChecked = SettingsViewModel.getSettingsViewModel().mute.value == true
            listItemDividerViewVisibility = View.VISIBLE
        }
        soundSettingsList?.add(builder)
    }

    private fun getIntensityMap(keyId : Int, valueID:Int) : HashMap<String, Long> {
        // Fetch the string-array from the resources
        val intensityOption = resources.getStringArray(keyId)
        val intensityValues = resources.getIntArray(valueID).map { it.toLong() }.toTypedArray()
        // Initialize a HashMap<String, Long>
        intensityOptionMap = HashMap()

        // Fill the HashMap with key-value pairs (key: sound name, value: long value)
        intensityOption.forEachIndexed { index, option ->
            intensityOptionMap!![option] = intensityValues[index]
        }
        return intensityOptionMap as HashMap<String, Long>
    }
    /**
     * Get the option key to display
     */
    private fun getSelectedOption(keyId : Int, valueID:Int, currentValue:Int) : String? {
        val intensityOptionMap = getIntensityMap(keyId, valueID)
        return when (getSubListOptions()) {
            SOUND_VOLUME -> {
                if (SettingsViewModel.getSettingsViewModel().mute.value == true) {
                    val minEntry = intensityOptionMap.minByOrNull { it.value }
                    minEntry?.key
                } else {
                    intensityOptionMap.filterValues {
                        it == currentValue.toLong()
                    }.keys.firstOrNull() ?: resources.getString(R.string.max)
                }
            }

            DISPLAY_AND_BRIGHTNESS -> {
                intensityOptionMap.filterValues {
                    it == currentValue.toLong()
                }.keys.firstOrNull() ?: resources.getString(R.string.max)
            }

            else -> {
                resources.getString(R.string.max)
            }
        }
    }
    /**
     * Method to initialise the Alert & Timers
     */
    private fun addAlertAndTimers() {
        val builder = ListTileData()
        val currentSelectedValue = SettingsViewModel.getSettingsViewModel().alarmTimerVolumePercentage.value
        builder.apply {
            titleText = resources.getString(R.string.alerts_and_timers)
            titleTextVisibility = View.VISIBLE
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            rightText = currentSelectedValue?.let { getSelectedOption(R.array.sound_button, R.array.sound_button_values, it) }.toString()
            rightTextVisibility = View.VISIBLE
            itemViewVisibility = View.VISIBLE
            rightIconID = R.drawable.icon_list_item_right_arrow
            rightIconVisibility = View.VISIBLE
            isItemEnabled = SettingsViewModel.getSettingsViewModel().mute.value != true
            isClickable = SettingsViewModel.getSettingsViewModel().mute.value != true
        }
        soundSettingsList?.add(builder)
    }

    /**
     * Method to initialise the mac address
     */
    private fun addButtonsAndEffects() {
        val builder = ListTileData()
        val currentSelectedValue = SettingsViewModel.getSettingsViewModel().buttonVolumePercentage.value
        builder.apply {
            titleText = resources.getString(R.string.buttons_and_effects)
            titleTextVisibility = View.VISIBLE
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            rightText = currentSelectedValue?.let { getSelectedOption(R.array.sound_button, R.array.sound_button_values, it) }.toString()
            rightTextVisibility = View.VISIBLE
            itemViewVisibility = View.VISIBLE
            rightIconVisibility = View.VISIBLE
            rightIconID = R.drawable.icon_list_item_right_arrow
            listItemDividerViewVisibility = View.GONE
            isItemEnabled = SettingsViewModel.getSettingsViewModel().mute.value != true
            isClickable = SettingsViewModel.getSettingsViewModel().mute.value != true
        }
        soundSettingsList?.add(builder)
    }

    /**
     * Method to initialise the Display Brightness tile
     */
    private fun addDisplayBrightness() {
        val builder = ListTileData()
        val currentStoredValue = SettingsViewModel.getSettingsViewModel().brightness.value
        builder.apply {
            titleText = resources.getString(R.string.display_and_brightness)
            titleTextVisibility = View.VISIBLE
            itemIconVisibility = View.GONE
            subTextVisibility = View.GONE
            rightText = currentStoredValue?.let { getSelectedOption(R.array.display_brightness_array, R.array.display_brightness_values, it) }.toString()
            rightTextVisibility = View.VISIBLE
            itemViewVisibility = View.VISIBLE
            rightIconID = R.drawable.icon_list_item_right_arrow
            rightIconVisibility = View.VISIBLE
            listItemDividerViewVisibility = View.GONE
            isItemEnabled = true
        }
        soundSettingsList?.add(builder)
    }
    override fun onToggleSwitchClick(position: Int, isChecked: Boolean) {
        if (position == resources.getInteger(R.integer.integer_range_0)) {
            if (isChecked) {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.toggle_off,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                SettingsViewModel.getSettingsViewModel().setMute(true)
            } else {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.toggle_on,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                SettingsViewModel.getSettingsViewModel().setMute(false)
            }
            var currentValue = SettingsViewModel.getSettingsViewModel().alarmTimerVolumePercentage.value
            var rightDisplayText: String = intensityOptionMap?.filterValues {
                it == currentValue?.toLong()
            }?.keys?.firstOrNull() ?: resources.getString(R.string.max)
            if (soundSettingsList != null) {
                for (i in 1 until (soundSettingsList?.size ?: 0)) {
                    val listTileData: ListTileData? = soundSettingsList?.get(i)
                    if (listTileData != null) {
                        if (listTileData.titleText == resources.getString(R.string.buttons_and_effects)) {
                            currentValue = SettingsViewModel.getSettingsViewModel().buttonVolumePercentage.value
                            rightDisplayText = intensityOptionMap?.filterValues {
                                it == currentValue?.toLong()
                            }?.keys?.firstOrNull() ?: resources.getString(R.string.max)
                        }
                        listTileData.isItemEnabled = !isChecked
                        if (isChecked) {
                            val minEntry = intensityOptionMap?.minByOrNull { it.value }
                            listTileData.rightText = minEntry?.key.toString()
                        } else {
                            listTileData.rightText = rightDisplayText
                        }
                        listTileData.isClickable = !isChecked
                        notifyItemChanged(i)
                    }
                }
                soundSettingsList?.size?.let { notifyItemRangeSetChanged(positionEnd = it) }
            }
        }
    }

    override fun onListViewItemClick(view: View?, position: Int) {
        val bundle = Bundle()
        bundle.putString(BundleKeys.BUNDLE_NAVIGATED_FROM, arguments?.getString(BundleKeys.BUNDLE_NAVIGATED_FROM))
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        if (getSubListOptions() != DISPLAY_AND_BRIGHTNESS) {
            if (SettingsViewModel.getSettingsViewModel().mute.value == true) return
            // if mute tile clicked return, if tile toggle clicked functionality handled separately
            if(position == DIGIT_ZERO) return
            if (position == DIGIT_ONE) {
                bundle.putInt(BundleKeys.BUNDLE_INTENSITY_TYPE, ALERTS_AND_TIMERS)
            }
            if (position == DIGIT_TWO) {
                bundle.putInt(BundleKeys.BUNDLE_INTENSITY_TYPE, BUTTONS_AND_EFFECTS)
            }
        } else {
            if (position == DIGIT_ZERO) {
                bundle.putInt(BundleKeys.BUNDLE_INTENSITY_TYPE, DISPLAY_BRIGHTNESS)
            }
        }
        NavigationUtils.navigateSafely(
                this,
                R.id.action_soundVolumeFragment_to_intensitySelectionFragment,
                bundle,
                null
        )
    }

    private inner class LinearLayoutManagerForScrolling(context: Context?) :
            LinearLayoutManager(context) {
        override fun canScrollVertically(): Boolean {
            return canScrollVertically && super.canScrollVertically()
        }
    }

    override fun leftIconOnClick() {
        KnobNavigationUtils.setBackPress()
        val navId : Int = if (arguments?.getString(BundleKeys.BUNDLE_NAVIGATED_FROM).equals(AppConstants.SETTINGLANDING_FRAGMENT)) {
            R.id.action_SoundVolumeDisplayFragment_to_settingsLandingFragment
        } else {
            R.id.action_SoundVolumeDisplayFragment_to_settingsPreferencesFragment
        }
        NavigationUtils.navigateSafely(this, navId, null, null)
    }
    override fun rightIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
    }
    override fun provideScreenTimeoutValueInSeconds(): Int {
        return resources.getInteger(R.integer.session_long_timeout)
    }
    /**
     * Method to notify the item changed for recycler view adapter
     */
    private fun notifyItemChanged(position: Int) {
        fragmentToolsSoundDisplay?.recyclerList?.adapter?.notifyItemChanged(position)
    }
    /**
     * Method to notify the set of list changed for recycler view adapter
     */
    private fun notifyItemRangeSetChanged(positionStart: Int = 1, positionEnd: Int) {
        fragmentToolsSoundDisplay?.recyclerList?.adapter?.notifyItemRangeChanged(positionStart, positionEnd)
    }

    override fun onHMILeftKnobClick() {
        if (lastItemSelectedPos != -1) {
            lifecycleScope.launch(Dispatchers.Main) {
                KnobNavigationUtils.addTraversingData(lastItemSelectedPos, false)
                val toggle =
                        fragmentToolsSoundDisplay?.recyclerList?.findViewHolderForAdapterPosition(
                                lastItemSelectedPos
                        )?.itemView?.findViewById<ToggleSwitch>(R.id.settings_item_toggle_switch)

                if (toggle?.visibility == View.VISIBLE) {
                    val toggleSwitch = toggle.findViewById<SwitchCompat>(R.id.toggle_switch)
                    toggleSwitch.isChecked = !toggleSwitch.isChecked
                } else {
                    fragmentToolsSoundDisplay?.recyclerList?.findViewHolderForAdapterPosition(
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
        //Do nothing
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
                    if (currentPosition >= 0 && SettingsViewModel.getSettingsViewModel()?.isWifiEnabled == true) {
                        HMILogHelper.Logd(
                                "Knob",
                                "LEFT_KNOB: rotate right current knob index = $currentPosition"
                        )
                        fragmentToolsSoundDisplay?.recyclerList?.smoothScrollToPosition(
                                currentPosition
                        )
                        highLightSelectedTiles()

                    }else{
                        HMILogHelper.Logd("Knob","LEFT_KNOB: rotate left current knob index = $currentPosition")
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
                val viewHolder = fragmentToolsSoundDisplay?.recyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = -1
        }
    }

    private fun highLightSelectedTiles() {
        fragmentToolsSoundDisplay?.recyclerList?.postDelayed({
            if (lastItemSelectedPos != -1) {
                val viewHolder = fragmentToolsSoundDisplay?.recyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = currentPosition
            val viewHolderOld = fragmentToolsSoundDisplay?.recyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }, 50) // Adjust delay as needed
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        allItemSize = 0
        super.onDestroyView()
    }

     override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(CookingViewModelFactory.getInScopeViewModel() == null) {
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
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true  && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }
}