/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.digital_unboxing

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.presenter.adapters.TumblerElement
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.common.abstracts.AbstractCookingMicrowaveStatusFragment.handler
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentTimeAndDateTimerTumblerBinding
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.base.ComponentSelectionInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.utils.list.IntegerRange
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.rotateTumblerOnKnobEvents
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper.Logd
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.SharedViewModel
import core.utils.TimeAndDateUtils
import core.utils.TimeUtils
import core.utils.gone
import core.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


/**
 * File       : android.presenter.fragments.digital_unboxing.Time12HrsTumblerFragment
 * Brief      : Set 12 hrs time
 * Author     : Rajendra
 * Created On : 9/9/2024
 * Details    : Fragment responsible for set 12 hrs time
 */

class UnBoxingSetupTimeTumblerFragment : Fragment(),
    KeyboardInputManagerInterface,
    HMIKnobInteractionListener, View.OnClickListener, ComponentSelectionInterface {

    companion object {
        private const val TAG = "Time12HrAnd24HrTumblerFragment"
    }

    private var defaultHour = 12
    private var defaultMinute = 0
    private var defaultAMPM = 0
    private var default24hrsValue = 0
    private var default24MinValue = 0
    private var viewBinding: FragmentTimeAndDateTimerTumblerBinding? = null
    private var sharedViewModel: SharedViewModel? = null
    private val binding get() = viewBinding!!
    private var focusedTumbler: BaseTumbler? = null
    private var rotator: List<Int> = listOf()
    private var selectedRotator: Int = -1
    private var isTumblerSelected = false
    private var knobClickCount = 0
    private val rotatorClickCount = mutableMapOf<Int, Int>()
    private var timeHours: MutableList<String>? = null
    private var timeMinutes: MutableList<String>? = null
    private var timeAMPM: MutableList<String>? = null
    private lateinit var timeHourTumbler: BaseTumbler
    private lateinit var timeMinuteTumbler: BaseTumbler
    private lateinit var timeAMPMTumbler: BaseTumbler
    private lateinit var hourItemViewHolder: UnboxingCustomTumblerAdapter
    private lateinit var minuteItemViewHolder: UnboxingCustomTumblerAdapter
    private lateinit var secondItemViewHolder: UnboxingCustomTumblerAdapter
    private var smoothScrollDelay = 200L
    private var toggleScrollDelay = 300L
    private var isTumblersSwitchedFrom12HTo24HFormat = false
    private var isTumblersSwitchedFrom24HTo12HFormat = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentTimeAndDateTimerTumblerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        manageHeaderBar()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            selectedRotator = rotator[0]
        }
        handler.postDelayed({ setHourTumblerRotateListener()}, 100)
    }

    /**
     * method to set setHourTumblerRotateListener
     */
    private fun setHourTumblerRotateListener() {
        var previousValue = binding.tumblerNumericBasedHours.selectedValue.toInt()
        var previousIndex = binding.tumblerNumericBasedSeconds.selectedIndex

        fun isTwelveHourJump(previousValue: Int, currentValue: Int): Boolean {
            return (previousValue == 12 && currentValue == 1) || (previousValue == 1 && currentValue == 12)
        }

        fun toggleSecondsTumbler(previousIndex: Int): Int {
            val newIndex = if (previousIndex == 0) 1 else 0
            binding.tumblerNumericBasedSeconds.smoothScrollToPosition(newIndex)
            return newIndex
        }

        binding.tumblerNumericBasedHours.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentValue = binding.tumblerNumericBasedHours.selectedValue.toInt()
                if (isTwelveHourJump(previousValue, currentValue)) {
                    previousIndex = toggleSecondsTumbler(previousIndex)
                }
                previousValue = currentValue
            }
        })
    }

    /**
     * init components
     */
    private fun initView() {
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val dateTime = sharedViewModel?.getUnBoxingTime()
        if (arguments != null && arguments?.getString(BundleKeys.BUNDLE_PROVISIONING_TIME) != null) {
            Logd("Unboxing", "---- Time Tumbler Argument Flow -----")
            checkForPreviousTimeValues(KnobNavigationUtils.knobForwardTrace)
            updateView(isNeedToUpdateTumbler = false, KnobNavigationUtils.knobForwardTrace)
        } else {
            if (dateTime?.isNotEmpty() == true) {
                Logd("Unboxing", "---- Time Tumbler Save time flow -----")
                setDefaultClockValues(dateTime,isNeedToUpdateTumbler = false, KnobNavigationUtils.knobForwardTrace)
                updateView(isNeedToUpdateTumbler = false, KnobNavigationUtils.knobForwardTrace)
            } else {
                Logd("Unboxing", "---- Time Tumbler Normal flow -----")
                prepareTumblerData(KnobNavigationUtils.knobForwardTrace)
                updateView(isNeedToUpdateTumbler = true, KnobNavigationUtils.knobForwardTrace)
            }
        }
    }
    /**
     * Prepare the tumbler data  - 12hr and 24hr
     */
    private fun prepareTumblerData(isKnobRotationActive: Boolean) {
        if (SettingsManagerUtils.getTimeFormat() == SettingsManagerUtils.TimeFormatSettings.H_12) {
            rotator = listOf(0, 1, 2, 3, 4, 5) //HOUR, MIN, SEC, 12H, 24H, NEXT
            update12HrTumblerVisibility()
            timeAMPM =
                mutableListOf(
                    resources.getString(R.string.text_label_am),
                    resources.getString(R.string.text_label_pm)
                )
            timeHours = TimeAndDateUtils.getHourList()
            timeMinutes = TimeAndDateUtils.getMinuteList()
            init12HrsTumblers(isKnobRotationActive)
        } else {
            rotator = listOf(0, 1, 2, 3, 4)//HOUR, MIN, 12H, 24H, NEXT
            update24HrTumblerVisibility()
            timeHours = TimeAndDateUtils.get24HourList()
            timeMinutes = TimeAndDateUtils.getMinuteList()
            init24HrsTumblers(isKnobRotationActive)
        }
    }

    /**
     * update the 24hr tumbler
     */
    private fun update24HrsDefaultTimeTumbler() {
        Logd("Unboxing","update 24Hrs ---> defaultHour = $default24hrsValue , defaultMinute =$default24MinValue")
        provide24HrsDefault(default24hrsValue, default24MinValue)
    }
    /**
     * provide the 24hr tumbler
     */
    private fun provide24HrsDefault(defaultHour: Int, defaultMinute: Int) {
        scrollTo24HrsValues(defaultHour, defaultMinute)
    }

    /**
     * scroll to 24 hr tumbler as per value
     */
    private fun scrollTo24HrsValues(defaultHour: Int, defaultMinute: Int) {
        val hours = IntegerRange(1, 0, 23, defaultHour)
        val hoursTumbler = populateTumblerItemTimeValues(hours)
        initTimeTumbler(binding.tumblerNumericBasedHours, hoursTumbler, defaultHour)

        val minutes = IntegerRange(1, 0, 59, defaultMinute)
        val minutesTumbler = populateTumblerItemTimeValues(minutes)
        initTimeTumbler(binding.tumblerNumericBasedMins, minutesTumbler, defaultMinute)
    }

    /**
     * update the view as format - 12/24 hr
     */
    private fun updateView(isNeedToUpdateTumbler:Boolean, isKnobRotationActive: Boolean) {
        //Radio toggle visible
        binding.dateTimeFormatWidget.formatSelection.visible()
        binding.dateTimeFormatWidget.leftFormatSelection.visible()
        binding.dateTimeFormatWidget.rightFormatSelection.visible()
        //Radio toggle click event
        binding.dateTimeFormatWidget.leftFormatSelection.setOnClickListener(
            onFormatSelectionClicked(
                true
            )
        )
        binding.dateTimeFormatWidget.rightFormatSelection.setOnClickListener(
            onFormatSelectionClicked(false)
        )

        binding.startNowText.visibility = View.VISIBLE
        binding.startNowText.setTextButtonText(resources.getString(R.string.text_button_set))
        binding.startNowText.setButtonTextColor(R.color.color_white)
        binding.startNowText.isEnabled = true
        updateTimeWidgetView(isNeedToUpdateTumbler,isKnobRotationActive)
        binding.startNowText.setOnClickListener(this)
    }
    /**
     * update 12hr tumbler view visibility
     */
    private fun update12HrTumblerVisibility() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.endTumbler.visible()
            binding.endDivider.visible()
            binding.centerTumbler.visible()
            binding.centerDivider.visible()
            binding.startTumbler.visible()
        }
    }

    /**
     * update 24hr tumbler view visibility
     */
    private fun update24HrTumblerVisibility() {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.endTumbler.gone()
            binding.endDivider.gone()
            binding.centerTumbler.visible()
            binding.centerDivider.visible()
            binding.startTumbler.visible()
        }

    }

    /**
     * manage header bar and populate the widget
     */
    private fun manageHeaderBar() {
        binding.headerBar.apply {
            setTitleText(
                resources.getString(
                    R.string.text_header_set_time
                )
            )
            setRightIcon(R.drawable.numpad_icon)
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setCustomOnClickListener(object :
                HeaderBarWidgetInterface.CustomClickListenerInterface {

                override fun leftIconOnClick() {
                    super.leftIconOnClick()
                    sharedViewModel?.setUnBoxingTime(AppConstants.EMPTY_STRING)
                    //Due to multiple back happening so navigateSafely regional settings on header back arrow click
                    lifecycleScope.launch(Dispatchers.Main) {
                        //For smooth transition between fragment we have added navOption with anim parameter
                        val navOptions = NavOptions
                            .Builder()
                            .setEnterAnim(R.anim.fade_in)
                            .setExitAnim(R.anim.fade_out)
                            .build()
                        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                            NavigationUtils.navigateSafely(
                                it,
                                R.id.action_time12HrsTumblerFragment_to_unboxingRegionalSettingsFragment,
                                null,
                                navOptions
                            )
                        }
                    }
                }

                override fun rightIconOnClick() {
                    super.rightIconOnClick()
                    navigateToTimeNumberPadScreen()
                }
            })
        }
    }

    /**
     * init 12hr tumbler
     */
    private fun init12HrsTumblers(isKnobRotationActive: Boolean) {
        timeHourTumbler = binding.tumblerNumericBasedHours
        hourItemViewHolder = timeHours?.let {
            UnboxingCustomTumblerAdapter(
                    it.toList(), KnobNavigationUtils.knobForwardTrace, isKnobRotationActive = isKnobRotationActive
            )
        }!!
        timeHourTumbler.apply {
            isInfiniteScroll = true
            itemAnimator = null
            itemViewHolder = hourItemViewHolder
            updateItems(timeHours as java.util.ArrayList<String>?, true)
        }
        timeMinuteTumbler = binding.tumblerNumericBasedMins
        minuteItemViewHolder = timeMinutes?.let {
            UnboxingCustomTumblerAdapter(
                    it.toList(),
                false, isKnobRotationActive = isKnobRotationActive
            )
        }!!
        timeMinuteTumbler.apply {
            isInfiniteScroll = true
            itemAnimator = null
            itemViewHolder = minuteItemViewHolder
            updateItems(timeMinutes as java.util.ArrayList<String>?, true)
        }
        timeAMPMTumbler = binding.tumblerNumericBasedSeconds
        secondItemViewHolder = timeAMPM?.toList()?.let {
            UnboxingCustomTumblerAdapter(
                    it, isAMPMSelected = true, isKnobRotationActive = isKnobRotationActive
            )
        }!!
        timeAMPMTumbler.apply {
            isInfiniteScroll = false
            itemAnimator = null
            itemViewHolder = secondItemViewHolder
            updateItems(timeAMPM as java.util.ArrayList<String>?, true)
        }
    }

    private fun init24HrsTumblers(isKnobRotationActive: Boolean) {
        timeHourTumbler = binding.tumblerNumericBasedHours
        hourItemViewHolder = timeHours?.let {
            UnboxingCustomTumblerAdapter(
                it.toList(),
                KnobNavigationUtils.knobForwardTrace,
                isKnobRotationActive = isKnobRotationActive
            )
        }!!
        timeHourTumbler.apply {
            isInfiniteScroll = true
            itemViewHolder = hourItemViewHolder
            updateItems(timeHours as java.util.ArrayList<String>?, true)
        }
        timeMinuteTumbler = binding.tumblerNumericBasedMins
        minuteItemViewHolder = timeMinutes?.let {
            UnboxingCustomTumblerAdapter(
                    it.toList(),
                    false, isKnobRotationActive = isKnobRotationActive
            )
        }!!
        timeMinuteTumbler.apply {
            isInfiniteScroll = true
            itemViewHolder = minuteItemViewHolder
            updateItems(timeMinutes as java.util.ArrayList<String>?, true)
        }
    }

    private fun update12HrsDefaultTimeTumbler() {
        val apPmText = timeAMPM?.get(defaultAMPM) ?: AppConstants.EMPTY_STRING
        Logd("Unboxing","update 12Hrs ---> defaultHour = $defaultHour , defaultMinute =$defaultMinute , defaultAMPM = $defaultAMPM")
        provide12HrsDefault(defaultHour, defaultMinute, apPmText)
    }

    /**
     * Provides interface to scroll tumbler at desired default position
     * @param defaultHours int
     * @param defaultMin int
     * @param defaultAMPM int
     * @noinspection SameParameterValue
     */
    private fun provide12HrsDefault(defaultHours: Int, defaultMin: Int, defaultAMPM: String) {
        scrollTo12HrsValues(defaultHours, defaultMin, defaultAMPM)
    }

    private fun scrollTo12HrsValues(defaultHours: Int, defaultMin: Int, defaultSec: String) {
        val hours = IntegerRange(1, 1, 12, defaultHours)
        val hoursTumbler = populateTumblerItemTimeValues(hours)
        initTimeTumbler(binding.tumblerNumericBasedHours, hoursTumbler, defaultHours)

        val minutes = IntegerRange(1, 0, 59, defaultMin)
        val minutesTumbler = populateTumblerItemTimeValues(minutes)
        initTimeTumbler(binding.tumblerNumericBasedMins, minutesTumbler, defaultMin)

        val amPMTumbler: List<TumblerElement>? = timeAMPM?.let { populateTumblerWithAmPm(it) }
        if (amPMTumbler != null) {
            initAmPmTumbler(binding.tumblerNumericBasedSeconds, amPMTumbler, defaultSec)
        }
    }

    private fun populateTumblerWithAmPm(targetPowerLevelList: MutableList<String>): List<TumblerElement> {
        val tumblerElements: MutableList<TumblerElement> = ArrayList()
        for (s in targetPowerLevelList) {
            val localArray: MutableList<String> = ArrayList()
            localArray.add(s)
            tumblerElements.add(
                TumblerElement(
                    tumblerData = localArray, labelName = "",
                    type = TumblerElement.NUMERIC_TYPE
                )
            )
        }
        return tumblerElements
    }

    /**
     * populate tumbler time items
     */
    private fun populateTumblerItemTimeValues(timeValues: IntegerRange): List<String> {
        val tumblerElements: MutableList<String> = java.util.ArrayList()
        var i = timeValues.min
        while (i <= timeValues.max) {
            if (i < AppConstants.DIGIT_TEN) {
                tumblerElements.add("0$i")
            } else {
                tumblerElements.add(i.toString())
            }
            i += timeValues.step
        }
        return tumblerElements
    }

    /**
     * Method to initialize the Numeric Tumbler.
     *
     * @param list,           List of Tumbler Elements
     * @param timeValues, temperature options from SDK
     */
    private fun initTimeTumbler(
        tumbler: BaseTumbler, list: List<String>,
        timeValues: Int
    ) {
        val defaultTime = if (timeValues >= AppConstants.DIGIT_TEN) {
            timeValues.toString()
        } else {
            "0$timeValues"
        }
        val listInterface: ViewModelListInterface = object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return ArrayList(list)
            }

            /** @noinspection DataFlowIssue
             */
            override fun getDefaultString(): String {
                return defaultTime
            }

            override fun getValue(index: Int): Any {
                return listItems[index]
            }

            override fun isValid(value: Any): Boolean {
                return false
            }
        }
        requireView().post {
            tumbler.setComponentSelectionInterface(this)
            tumbler.setListObject(listInterface, true)
        }
    }

    /**
     * Method to initialize the Numeric Tumbler.
     *
     * @param list, List of Tumbler Elements
     * @param amPm, amPm options from SDK
     */
    private fun initAmPmTumbler(
        tumbler: BaseTumbler,
        list: List<TumblerElement>,
        amPm: String
    ) {
        tumbler.itemAnimator = null
        tumbler.updateItems(convertTumblerToStringList(list))
        val listInterface: ViewModelListInterface = object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return convertTumblerToStringList(list)
            }

            override fun getDefaultString(): String {
                return amPm
            }

            override fun getValue(index: Int): Any {
                return listItems[index]
            }

            override fun isValid(value: Any): Boolean {
                return false
            }
        }
        requireView().post {
            tumbler.setComponentSelectionInterface(this)
            tumbler.setListObject(listInterface, true)
        }
    }

    /**
     * This function takes the tumbler and converts the text in a list of String elements
     *
     * @param tumblerElements The list of tumbler elements
     * @return An array list of converted string
     */
    fun convertTumblerToStringList(tumblerElements: List<TumblerElement>): ArrayList<String> {
        val tumblerStrings = ArrayList<String>()
        for ((_, _, _, _, tumblerData) in tumblerElements) {
            tumblerStrings.add(tumblerData!![0])
        }
        return tumblerStrings
    }

    override fun getKeyboardView(): KeyboardView {
        return KeyboardView(requireContext(), null)
    }

    override fun onHMILeftKnobClick() {
        Log.d(TAG, "onHMILeftKnobClick() called $isTumblerSelected : $selectedRotator")
        val is12HourFormat =
            SettingsManagerUtils.getTimeFormat() == SettingsManagerUtils.TimeFormatSettings.H_12
        handleRotatorSelection(selectedRotator, is12HourFormat)
    }

    // Function to handle the common behavior for both time formats
    private fun handleRotatorSelection(selectedRotator: Int, is12HourFormatSelected: Boolean) {
        focusedTumbler = null
        knobClickCount++
        var modifiedRotatorValue = selectedRotator
        when (selectedRotator) {
            0 -> {
                focusedTumbler = timeHourTumbler
                handleKnobClickAndFocus()
            }

            1 -> {
                focusedTumbler = timeMinuteTumbler
                handleKnobClickAndFocus()
            }

            2 -> {
                if (is12HourFormatSelected) {
                    focusedTumbler = timeAMPMTumbler
                    handleKnobClickAndFocus()
                } else {
                    isTumblersSwitchedFrom24HTo12HFormat = true
                    modifiedRotatorValue += 1
                    this@UnBoxingSetupTimeTumblerFragment.selectedRotator = modifiedRotatorValue
                    onTimeFormatSelectionWidgetClickActions(true, true)
                }
            }

            3 -> {
                isTumblerSelected = false
            }

            4 -> {
                isTumblerSelected = false
                if (is12HourFormatSelected){
                    isTumblersSwitchedFrom12HTo24HFormat = true
                    modifiedRotatorValue -= 1
                    this@UnBoxingSetupTimeTumblerFragment.selectedRotator = modifiedRotatorValue
                    onTimeFormatSelectionWidgetClickActions(false, true)
                } else onNextButtonAction()
            }

            5 -> {
                KnobNavigationUtils.knobForwardTrace = true
                isTumblerSelected = false
                onNextButtonAction()
            }
        }
    }

    // Method to handle knob click logic for first and second clicks
    private fun handleKnobClickAndFocus(knobDirection: String = KnobDirection.CLOCK_WISE_DIRECTION) {
        rotatorClickCount[selectedRotator] = (rotatorClickCount[selectedRotator] ?: 0) + 1
        val currentSelectedRotator = selectedRotator
        val is12HourFormat = SettingsManagerUtils.getTimeFormat() == SettingsManagerUtils.TimeFormatSettings.H_12
        when (KnobNavigationUtils.ClickState.values().find { it.count == rotatorClickCount[selectedRotator] }) {
            KnobNavigationUtils.ClickState.FIRST -> {
                // First click: mark isTumblerSelected as true
                isTumblerSelected = true
                if (is12HourFormat) {
                    rotate12HrsFocus(knobDirection, false)
                } else {
                    rotate24HrsFocus(knobDirection, false)
                }
            }

            KnobNavigationUtils.ClickState.SECOND -> {
                // Second click: mark isTumblerSelected as false and update selectedRotator
                isTumblerSelected = false
                updateSelectedRotator(knobDirection)
                if (is12HourFormat) {
                    rotate12HrsFocus(knobDirection)
                } else {
                    rotate24HrsFocus(knobDirection)
                }
                // Reset the click count for the previous selectedRotator (before update)
                rotatorClickCount[currentSelectedRotator] = 0
            }
            else -> {}
        }
    }

    /**
     * On Next button Click
     */
    private fun onNextButtonAction() {
        val dateTimeValue: String = getDateTimeValue()
        Logd("Unboxing","onNextButtonAction---------------$dateTimeValue")
        sharedViewModel?.setUnBoxingTime(dateTimeValue)
        //clear the argument - Handling the scenario when user come from date tumbler so should populate selected previouse value
        arguments?.clear()
        if (SettingsManagerUtils.getTimeFormat() == SettingsManagerUtils.TimeFormatSettings.H_12) {
            set12HoursTime()
        } else {
            set24HoursTime()
        }
    }

    override fun onHMILongLeftKnobPress() {
    }

    override fun onHMIRightKnobClick() {
        PopUpBuilderUtils.userLeftKnobWarningPopup(this)
    }

    override fun onHMILongRightKnobPress() {
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            val is12HourFormat = SettingsManagerUtils.getTimeFormat() == SettingsManagerUtils.TimeFormatSettings.H_12
            if (!isTumblerSelected) {
                updateSelectedRotator(knobDirection)
                if (is12HourFormat) {
                    rotate12HrsFocus(knobDirection)
                } else {
                    rotate24HrsFocus(knobDirection)
                }
            } else {
                rotateFocusedTumbler(knobDirection)
            }
        } else if (knobId == AppConstants.RIGHT_KNOB_ID) {
            PopUpBuilderUtils.userLeftKnobWarningPopup(this)
        }
    }

    // Update the selected rotator based on the knob direction
    private fun updateSelectedRotator(knobDirection: String) {
        when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION -> {
                selectedRotator = if (selectedRotator < rotator.size - 1) selectedRotator + 1 else rotator.size - 1
            }
            KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                selectedRotator = if (selectedRotator <= 0) 0 else selectedRotator - 1
            }
        }
    }

    // Rotate the focused tumbler based on the current tumbler type
    private fun rotateFocusedTumbler(knobDirection: String) {
        when (focusedTumbler) {
            timeHourTumbler -> rotateTumblerOnKnobEvents(this, timeHourTumbler, knobDirection)
            timeMinuteTumbler -> rotateTumblerOnKnobEvents(this, timeMinuteTumbler, knobDirection)
            binding.tumblerNumericBasedSeconds -> rotateTumblerOnKnobEvents(this, binding.tumblerNumericBasedSeconds, knobDirection)
            else -> {}
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if(knobId == AppConstants.LEFT_KNOB_ID) resetKnobParameters()
    }

    // Reset the knob related parameters and selections
    private fun resetKnobParameters(){
        isTumblerSelected = false
        rotatorClickCount.clear()
        selectedRotator = -1
        knobClickCount = 0
        isTumblersSwitchedFrom12HTo24HFormat = false
        isTumblersSwitchedFrom24HTo12HFormat = false
        toggleFocusHourTumbler(false, false)
        toggleFocusMinuteTumbler(false, false)
        if (SettingsManagerUtils.getTimeFormat() == SettingsManagerUtils.TimeFormatSettings.H_12)
            toggleFocusSecondTumbler(false, false)
        updateTimeWidgetView(isKnobRotationActive = false)
        binding.startNowText.setBottomButtonViewVisible(false)
    }

    private fun updateTimeWidgetView(
        isNeedToUpdateTumbler: Boolean = false,
        isKnobRotationActive: Boolean = true
    ) {
        when (SettingsManagerUtils.TimeFormatSettings.H_12) {
            SettingsManagerUtils.getTimeFormat() -> {
                update12hrToggleButton(isKnobRotationActive)
                Logd(
                    "Settings",
                    "updateView --> update12HrsDefaultTimeTumbler -->$isNeedToUpdateTumbler"
                )
                if (isNeedToUpdateTumbler) update12HrsDefaultTimeTumbler()
            }

            else -> {
                update24hrToggleButton(isKnobRotationActive)
                if (isNeedToUpdateTumbler) update24HrsDefaultTimeTumbler()
            }
        }
    }

    /**
     * @param baseTumbler - Recyclerview tumbler
     * @param position - scroll position
     */
    private fun smoothScrollHandler(baseTumbler: BaseTumbler, position: Int, delay:Long = smoothScrollDelay) {
        // setting the request index to be able to know that we are scrolling to a specific position.
        val modifierCenterItemHandler = Handler(Looper.getMainLooper())
        modifierCenterItemHandler.postDelayed({
            baseTumbler.isSelected = true
            //set position base tumbler with out snap animation
            baseTumbler.scrollToPosition(position)
            //To stay focus on selected index -> smoothScrollToPosition need to call
            baseTumbler.smoothScrollToPosition(position, delay)
        }, delay)
    }

    private fun rotate12HrsFocus(knobDirection: String, isKnobRotationActive: Boolean = true) {
        Log.d("SettingsTime", "rotateFocus() called with: selectedRotator = $selectedRotator")
        val actions = when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION, KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> arrayOf(
                { /* Action for selectedRotator 0 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(true, isKnobRotationActive)
                    toggleFocusMinuteTumbler(false)
                    toggleFocusSecondTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                },
                { /* Action for selectedRotator 1 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(true, isKnobRotationActive)
                    toggleFocusSecondTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)

                },
                { /* Action for selectedRotator 2 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(false)
                    toggleFocusSecondTumbler(true, isKnobRotationActive)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    updateTimeWidgetView()
                },
                { /* Action for selectedRotator 3 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(false)
                    toggleFocusSecondTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    update12hrToggleButton(false)
                },
                { /* Action for selectedRotator 4 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(false)
                    toggleFocusSecondTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    update24hrToggleButton(false)
                },
                { /* Action for selectedRotator 3 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(false)
                    toggleFocusSecondTumbler(false)
                    updateTimeWidgetView()
                    binding.startNowText.setBottomButtonViewVisible(true)
                })

            else -> return
        }

        actions.getOrNull(selectedRotator)?.invoke()
    }

    private fun rotate24HrsFocus(knobDirection: String, isKnobRotationActive: Boolean = true) {
        Log.d("Unboxing", "rotateFocus() called with: selectedRotator = $selectedRotator")
        val actions = when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION, KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> arrayOf(
                { /* Action for selectedRotator 0 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(true, isKnobRotationActive)
                    toggleFocusMinuteTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                },
                { /* Action for selectedRotator 1 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(true, isKnobRotationActive)
                    updateTimeWidgetView()
                    binding.startNowText.setBottomButtonViewVisible(false)
                },
                { /* Action for selectedRotator 2 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    update12hrToggleButton(false)
                },
                { /* Action for selectedRotator 3 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    update24hrToggleButton(false)
                },
                { /* Action for selectedRotator 4 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(false)
                    updateTimeWidgetView()
                    binding.startNowText.setBottomButtonViewVisible(true)
                })

            else -> return
        }

        actions.getOrNull(selectedRotator)?.invoke()
    }

    /**
     * Method use for navigate to time num-pad screen
     */
    private fun navigateToTimeNumberPadScreen() {
        val dateTimeValue: String = getDateTimeValue()
        Logd("Unboxing","Navigate Numpad = $dateTimeValue")
        val bundle = Bundle()
        bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, dateTimeValue)
        NavigationUtils.navigateSafely(
            this,
            R.id.action_time12HrsTumblerFragment_to_unboxingSetupTimeFragment,
            bundle,
            null
        )
    }

    private fun getDateTimeValue(): String {
        val dateTimeValue: String
        val selectedHour = timeHourTumbler.selectedValue
        val selectedMinute = timeMinuteTumbler.selectedValue
        if (SettingsManagerUtils.getTimeFormat() == SettingsManagerUtils.TimeFormatSettings.H_12) {
            val clockMode = timeAMPMTumbler.selectedValue
            dateTimeValue = buildString {
                append(selectedHour)
                append(selectedMinute)
                append(clockMode)
            }
        } else {
            dateTimeValue = buildString {
                append(selectedHour)
                append(selectedMinute)
            }
        }
        return dateTimeValue
    }

    /**
     * Method use for toggle focus on HOUR,MIN,SEC tumblers
     *
     * @param isBottomViewVisible
     * @param isKnobRotationActive to handle tumbler style while knob is active
     */
    private fun toggleFocusHourTumbler(
            isBottomViewVisible: Boolean,
            isKnobRotationActive: Boolean = true
    ) {
        hourItemViewHolder = timeHours?.let {
            UnboxingCustomTumblerAdapter(
                it,
                isBottomViewVisible,
                isKnobRotationActive = isKnobRotationActive
            )
        }!!
        timeHourTumbler.itemViewHolder = hourItemViewHolder
        timeHourTumbler.updateItems(timeHours as java.util.ArrayList<String>?, true)
    }

    private fun toggleFocusMinuteTumbler(
            isBottomViewVisible: Boolean,
            isKnobRotationActive: Boolean = true
    ) {
        minuteItemViewHolder = timeMinutes?.let {
            UnboxingCustomTumblerAdapter(
                it, isBottomViewVisible, isKnobRotationActive = isKnobRotationActive
            )
        }!!
        timeMinuteTumbler.itemViewHolder = minuteItemViewHolder
        timeMinuteTumbler.updateItems(timeMinutes as java.util.ArrayList<String>?, true)
    }

    private fun toggleFocusSecondTumbler(
            isBottomViewVisible: Boolean,
            isKnobRotationActive: Boolean = true
    ) {
        secondItemViewHolder =
            timeAMPM?.let {
                UnboxingCustomTumblerAdapter(
                    it,
                    isBottomViewVisible,
                    true,
                    isKnobRotationActive = isKnobRotationActive
                )
            }!!
        timeAMPMTumbler.itemViewHolder = secondItemViewHolder
        timeAMPMTumbler.updateItems(timeAMPM as java.util.ArrayList<String>?, true)
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.startNowText) {
            onNextButtonAction()
        }
    }

    private fun set24HoursTime() {
        val calendar = Calendar.getInstance()
        val hour: Int = binding.tumblerNumericBasedHours.selectedValue.toInt()
        val minute: Int = binding.tumblerNumericBasedMins.selectedValue.toInt()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        Logd(
            TAG,
            "Unboxing: 24 Hrs format: -Hour = $hour, Minutes = $minute,Year = $year, Month = $month , Day = $day"
        )
        SettingsViewModel.getSettingsViewModel().setTimeModeManual(year, month, day, hour, minute)
        navigateToDateFlow()
    }

    private fun set12HoursTime() {
        val calendar: Calendar = Calendar.getInstance()
        var hour: Int = binding.tumblerNumericBasedHours.selectedValue.toInt()
        val minute: Int = binding.tumblerNumericBasedMins.selectedValue.toInt()
        val isAmPm: String = binding.tumblerNumericBasedSeconds.selectedValue.toString()
        val timeMath: Int = if (isAmPm.contentEquals(getString(R.string.text_label_am))) {
            resources.getInteger(R.integer.integer_range_0)
        } else {
            resources.getInteger(R.integer.integer_range_1)
        }
        if (hour == resources.getInteger(R.integer.integer_range_12)) {
            hour = resources.getInteger(R.integer.integer_range_0)
        }
        hour += resources.getInteger(R.integer.integer_range_12) * timeMath
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        Logd(
            TAG,
            "Unboxing: 24 Hrs format: Hour = $hour, Minutes = $minute, AM_PM = $isAmPm,Year = $year, Month = $month , Day = $day"
        )
        SettingsViewModel.getSettingsViewModel()
            .setTimeModeManual(year, month, day, hour, minute)
        SettingsViewModel.getSettingsViewModel().timeModeAuto
        navigateToDateFlow()
    }

    /**
     * navigate to next screen
     */
    private fun navigateToDateFlow() {
        Logd("Unboxing", "Unboxing: Navigate to set date flow")
        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
            NavigationUtils.navigateSafely(
                it,
                R.id.action_time12HrsTumblerFragment_to_dateTumblerFragment,
                null,
                null
            )
        }
    }

    private fun onFormatSelectionClicked(is12HoursFormat: Boolean): View.OnClickListener {
        return View.OnClickListener {
            onTimeFormatSelectionWidgetClickActions(is12HoursFormat)
        }
    }

    private fun onTimeFormatSelectionWidgetClickActions(is12HoursFormat: Boolean, isKnobClick: Boolean = false){
        if (is12HoursFormat && SettingsManagerUtils.getTimeFormat() != SettingsManagerUtils.TimeFormatSettings.H_12) {
            val hour: String = binding.tumblerNumericBasedHours.selectedValue.toString()
            val minute: String = binding.tumblerNumericBasedMins.selectedValue.toString()
            Logd("Unboxing", "24 Hr = $hour, minute = $minute")

            SettingsManagerUtils.setTimeFormat(SettingsManagerUtils.TimeFormatSettings.H_12)
            update12hrToggleButton(isKnobClick)
            update12HrTumblerVisibility()

            load12HrTumbler(hour, minute, isKnobClick)

        } else  if (!is12HoursFormat && SettingsManagerUtils.getTimeFormat() != SettingsManagerUtils.TimeFormatSettings.H_24) {
            val hour: String = binding.tumblerNumericBasedHours.selectedValue.toString()
            val minute: String = binding.tumblerNumericBasedMins.selectedValue.toString()
            val isAmPm: String = binding.tumblerNumericBasedSeconds.selectedValue.toString()
            Logd("Unboxing", "12 Hr = hour = $hour, minute = $minute ,isAmPm = $isAmPm")

            SettingsManagerUtils.setTimeFormat(SettingsManagerUtils.TimeFormatSettings.H_24)
            update24hrToggleButton(isKnobClick)
            update24HrTumblerVisibility()
            load24HrTumbler(hour, minute, isAmPm, isKnobClick)
        }
    }

    /**
     * Load 24 hr format tumbler
     */
    private fun load24HrTumbler(hour: String, minute: String, isAmPm: String, isKnobRotationActive: Boolean) {
        lifecycleScope.launch {
            var time24:Pair<String,String>?
            withContext(Dispatchers.Main) {
                prepareTumblerData(isKnobRotationActive)
            }
            withContext(Dispatchers.Main) {
                  time24 = TimeUtils.convert12HrTo24Hr(
                    buildString {
                        append(hour.trim())
                        append(AppConstants.COLON)
                        append(minute.trim())
                        append(AppConstants.SPACE_CONSTANT)
                        append(isAmPm.trim())
                    })
                Logd("Unboxing", "24 load defaultHour - ${time24?.first}")
                Logd("Unboxing", "24 load defaultMinute - ${time24?.second}")

                defaultHour = time24?.first?.toInt() ?:AppConstants.DIGIT_ZERO
                defaultMinute = time24?.second?.toInt() ?:AppConstants.DIGIT_ZERO

                default24hrsValue = defaultHour
                default24MinValue = defaultMinute


                update24HrsDefaultTimeTumbler()
            }
            withContext(Dispatchers.Main){
                val hourIndex = timeHours?.indexOf(time24?.first)
                val minIndex = timeMinutes?.indexOf(time24?.second)
                when {
                    hourIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                        binding.tumblerNumericBasedHours,
                        hourIndex ?: 0,toggleScrollDelay
                    )

                    else -> smoothScrollHandler(binding.tumblerNumericBasedHours, 0)
                }
                when {
                    minIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                        binding.tumblerNumericBasedMins,
                        minIndex ?: 0,toggleScrollDelay
                    )

                    else -> smoothScrollHandler(binding.tumblerNumericBasedMins, 0)
                }
            }
        }
    }

    /**
     * Load 12 hr format tumbler
     */
    private fun load12HrTumbler(hour: String, minute: String, isKnobRotationActive: Boolean) {
        lifecycleScope.launch {
            var time12hr:Triple<String, String, String>?
            withContext(Dispatchers.Main) {
                prepareTumblerData(isKnobRotationActive)
            }
            withContext(Dispatchers.Main) {
                  time12hr = timeAMPM?.let {
                    TimeUtils.convert24HrTo12Hr(
                        buildString {
                            append(hour.trim())
                            append(AppConstants.COLON)
                            append(minute.trim())
                        }, it
                    )
                }
                Logd("Unboxing", "12 load defaultHour - ${time12hr?.first}")
                Logd("Unboxing", "12 load defaultMinute - ${time12hr?.second}")
                Logd("Unboxing", "12 load defaultAMPM - ${time12hr?.third}")
                //Tumbler we need to give last position so here we have provide last position
                val lastIndex = timeHours?.size ?: 0
                defaultHour = if (time12hr?.first?.toInt() != AppConstants.DIGIT_ZERO) time12hr?.first?.toInt() ?: AppConstants.DIGIT_ZERO else lastIndex
                defaultMinute =  time12hr?.second?.toInt() ?:AppConstants.DIGIT_ZERO
                defaultAMPM = timeAMPM?.indexOf(time12hr?.third) ?:AppConstants.DIGIT_ZERO
                Logd("Unboxing","load12HrTumbler --> update12HrsDefaultTimeTumbler")
                update12HrsDefaultTimeTumbler()

            }
            withContext(Dispatchers.Main){
                val hourIndex = timeHours?.indexOf(time12hr?.first)
                val minIndex = timeMinutes?.indexOf(time12hr?.second)
                val amPmIndex = timeAMPM?.indexOf(time12hr?.third)

                Logd("Unboxing", "12 load hourIndex - $hourIndex")
                Logd("Unboxing", "12 load minIndex - $minIndex")
                Logd("Unboxing", "12 load amPmIndex - $amPmIndex")
                when {
                    amPmIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                        binding.tumblerNumericBasedSeconds,
                        amPmIndex ?: 0,toggleScrollDelay
                    )

                    else -> smoothScrollHandler(binding.tumblerNumericBasedSeconds, 0)
                }
                when {
                    hourIndex != AppConstants.DIGIT_MINUS_ONE ->
                        smoothScrollHandler(binding.tumblerNumericBasedHours, hourIndex ?: 0,toggleScrollDelay)

                    else -> {
                        //Recyclerview we need to give last position so here we have minus with total list
                        val lastIndex = timeHours?.size?.minus(1) ?: 0
                        smoothScrollHandler(binding.tumblerNumericBasedHours, lastIndex,toggleScrollDelay)
                    }
                }
                when {
                    minIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                        binding.tumblerNumericBasedMins,
                        minIndex ?: 0,toggleScrollDelay
                    )

                    else -> smoothScrollHandler(binding.tumblerNumericBasedMins, 0)
                }
            }
        }
    }

    private fun update24hrToggleButton(isKnobRotationActive: Boolean) {
        binding.dateTimeFormatWidget.leftFormatSelection.isChecked = false
        binding.dateTimeFormatWidget.rightFormatSelection.isChecked = true
        changeTimeFormatSelectionColors(
                leftFormatSelectionView = binding.dateTimeFormatWidget.leftFormatSelection,
                rightFormatSelectionView = binding.dateTimeFormatWidget.rightFormatSelection,
                leftFormatTextColor = R.color.under_line_color,
                rightFormatTextColor = if (isKnobRotationActive) R.color.under_line_color else R.color.color_white,
                leftFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle,
                rightFormatTextStyle = if (isKnobRotationActive) R.style.RegionalToggleSwitchDisableStyle else R.style.RegionalToggleSwitchNormalStyle
        )
    }

    private fun update12hrToggleButton(isKnobRotationActive: Boolean) {
        binding.dateTimeFormatWidget.leftFormatSelection.isChecked = true
        binding.dateTimeFormatWidget.rightFormatSelection.isChecked = false
        changeTimeFormatSelectionColors(
                leftFormatSelectionView = binding.dateTimeFormatWidget.leftFormatSelection,
                rightFormatSelectionView = binding.dateTimeFormatWidget.rightFormatSelection,
                leftFormatTextColor = if (isKnobRotationActive) R.color.under_line_color else R.color.color_white,
                rightFormatTextColor = R.color.under_line_color,
                leftFormatTextStyle = if (isKnobRotationActive) R.style.RegionalToggleSwitchDisableStyle else R.style.RegionalToggleSwitchNormalStyle,
                rightFormatTextStyle = R.style.RegionalToggleSwitchDisableStyle
        )
    }

    /**
     * To change the non selected item of date and time formats
     */
    private fun changeTimeFormatSelectionColors(
        leftFormatSelectionView: RadioButton?,
        rightFormatSelectionView: RadioButton?,
        leftFormatTextColor: Int,
        rightFormatTextColor: Int,
        leftFormatTextStyle: Int,
        rightFormatTextStyle: Int,
    ) {
        if (leftFormatSelectionView != null) {
            leftFormatSelectionView.setTextColor(resources.getColor(leftFormatTextColor, null))
            TextViewCompat.setTextAppearance(leftFormatSelectionView, leftFormatTextStyle)
        }
        if (rightFormatSelectionView != null) {
            rightFormatSelectionView.setTextColor(resources.getColor(rightFormatTextColor, null))
            TextViewCompat.setTextAppearance(rightFormatSelectionView, rightFormatTextStyle)
        }
    }

    /**
     * Check for previous time value
     */
    private fun checkForPreviousTimeValues(isKnobRotationActive: Boolean) {
        if (arguments != null && arguments?.getString(BundleKeys.BUNDLE_PROVISIONING_TIME) != null) {
            if (arguments?.getString(
                    BundleKeys.BUNDLE_PROVISIONING_TIME
                ) != AppConstants.EMPTY_STRING
            ) {
                TimeUtils.getTimeStringArguments(arguments)?.let { setDefaultClockValues(it,isNeedToUpdateTumbler = true, isKnobRotationActive) }
            }
        }
    }

    /**
     * get argument data and parse into time and prepare the 12hr/24hr tumbler
     */
    private fun setDefaultClockValues(timeString: String,isNeedToUpdateTumbler: Boolean, isKnobRotationActive: Boolean) {
        lifecycleScope.launch {
            Logd("Unboxing", "get argument time = $timeString")
            if (!TextUtils.isEmpty(timeString)) {
                val hourValue = timeString.substring(AppConstants.DIGIT_ZERO, AppConstants.DIGIT_TWO).toInt()
                //Added delay for fect argument from numpad tumbler and scroll to position 12/24 hr
                if (timeString.length >= AppConstants.DIGIT_FIVE && hourValue <= AppConstants.DIGIT_TWELVE) {
                    argumentPrepare12HrTumbler(timeString,isNeedToUpdateTumbler, isKnobRotationActive)
                } else {
                    argumentPrepare24HrTumbler(timeString,isNeedToUpdateTumbler, isKnobRotationActive)
                }

            }
        }
    }

    /**
     * get argument data and parse into time and prepare the 12hr tumbler
     */
    private suspend fun argumentPrepare12HrTumbler(timeString: String,isNeedToUpdateTumbler: Boolean, isKnobRotationActive: Boolean) {
        var hourIndex: Int
        var minIndex: Int
        var amPmIndex: Int
        SettingsManagerUtils.setTimeFormat(SettingsManagerUtils.TimeFormatSettings.H_12)
        update12hrToggleButton(isKnobRotationActive)
        update12HrTumblerVisibility()
        withContext(Dispatchers.Main) {
            prepareTumblerData(isKnobRotationActive)
        }
        withContext(Dispatchers.Main) {
            val hour = timeString.substring(AppConstants.DIGIT_ZERO,AppConstants.DIGIT_TWO)
            hourIndex = timeHours?.indexOf(hour)?: AppConstants.DIGIT_ZERO

            val min = timeString.substring(AppConstants.DIGIT_TWO,AppConstants.DIGIT_FOUR)
            minIndex = timeMinutes?.indexOf(min)?: AppConstants.DIGIT_ZERO

            defaultHour = if (hour.toInt() == AppConstants.DIGIT_ZERO) {
                AppConstants.DIGIT_TWELVE
            } else {
                hour.toInt()
            }
            defaultMinute = min.toInt()

            amPmIndex = timeAMPM?.indexOf(timeString.substring(timeString.length - AppConstants.DIGIT_TWO))?: AppConstants.DIGIT_ZERO

            defaultAMPM = amPmIndex

            Logd("Unboxing","argumentPrepare12HrTumbler --> update12HrsDefaultTimeTumbler -->$isNeedToUpdateTumbler")
           if (isNeedToUpdateTumbler) update12HrsDefaultTimeTumbler()

            Logd("Unboxing", "12 hourIndex ===${hourIndex}")
            Logd("Unboxing", "12 minIndex ===${minIndex}")
            Logd("Unboxing", "12 amPmIndex ===${amPmIndex}")

        }
        withContext(Dispatchers.Main) {
            when {
                hourIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(binding.tumblerNumericBasedHours, hourIndex,toggleScrollDelay)
                else -> smoothScrollHandler(
                    binding.tumblerNumericBasedHours,
                    timeHours?.size?.minus(AppConstants.DIGIT_ONE) ?: AppConstants.DIGIT_ZERO,toggleScrollDelay
                )
            }
            when {
                minIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(binding.tumblerNumericBasedMins, minIndex,toggleScrollDelay)
                else -> smoothScrollHandler(binding.tumblerNumericBasedMins, AppConstants.DIGIT_ZERO,toggleScrollDelay)
            }
            when {
                amPmIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                    binding.tumblerNumericBasedSeconds,
                    amPmIndex,
                    toggleScrollDelay
                )

                else -> smoothScrollHandler(binding.tumblerNumericBasedSeconds, AppConstants.DIGIT_ZERO,toggleScrollDelay)
            }
        }
    }
    /**
     * get argument data and parse into time and prepare the 24hr tumbler
     */
    private suspend fun argumentPrepare24HrTumbler(timeString: String,isNeedToUpdateTumbler: Boolean, isKnobRotationActive: Boolean) {
        var hourIndex: Int
        var minIndex: Int
        SettingsManagerUtils.setTimeFormat(SettingsManagerUtils.TimeFormatSettings.H_24)
        update24hrToggleButton(isKnobRotationActive)
        update24HrTumblerVisibility()
        withContext(Dispatchers.Main) {
            prepareTumblerData(isKnobRotationActive)
        }
        withContext(Dispatchers.Main) {
            val hour = timeString.substring(AppConstants.DIGIT_ZERO,AppConstants.DIGIT_TWO)
            hourIndex = timeHours?.indexOf(hour)?: AppConstants.DIGIT_ZERO

            val min = timeString.substring(AppConstants.DIGIT_TWO,AppConstants.DIGIT_FOUR)
            minIndex = timeMinutes?.indexOf(min)?: AppConstants.DIGIT_ZERO
            Logd("Unboxing", "24 hourIndex ===${hourIndex}")
            Logd("Unboxing", "24 minIndex ===${minIndex}")

            defaultHour =  hour.toInt()
            defaultMinute = min.toInt()
            default24hrsValue = defaultHour
            default24MinValue = defaultMinute

            if (isNeedToUpdateTumbler) update24HrsDefaultTimeTumbler()
        }
        withContext(Dispatchers.Main) {
            when {
                hourIndex != AppConstants.DIGIT_MINUS_ONE ->
                    smoothScrollHandler(
                        binding.tumblerNumericBasedHours,
                        hourIndex,
                        toggleScrollDelay
                    )

                else -> {
                    val lastIndex = timeHours?.size?.minus(AppConstants.DIGIT_ONE) ?: AppConstants.DIGIT_ZERO
                    smoothScrollHandler(binding.tumblerNumericBasedHours, lastIndex,toggleScrollDelay)
                }
            }
            when {
                minIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                    binding.tumblerNumericBasedMins,
                    minIndex
                )
                else -> smoothScrollHandler(binding.tumblerNumericBasedMins, AppConstants.DIGIT_ZERO,toggleScrollDelay)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        viewBinding = null
        isTumblerSelected = false
        rotatorClickCount.clear()
        selectedRotator = -1
        knobClickCount = 0
    }

    override fun selectionUpdated(index: Int) {
        // do nothing
    }

    override fun onTumblerTouchInteraction(tumblerView: BaseTumbler?, action: Int) {
        super.onTumblerTouchInteraction(tumblerView, action)
        resetKnobParameters()
    }
}
