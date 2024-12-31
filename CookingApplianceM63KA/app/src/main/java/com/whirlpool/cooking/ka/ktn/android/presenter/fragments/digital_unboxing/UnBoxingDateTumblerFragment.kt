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
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentUnboxingDateTimerBinding
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.base.ComponentSelectionInterface
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.tools.util.KeyboardTextFormatUtils
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.utils.CalenderUtils
import com.whirlpool.hmi.utils.list.IntegerRange
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.rotateTumblerOnKnobEvents
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SettingsManagerUtils
import core.utils.TimeAndDateUtils
import core.utils.TimeUtils
import core.utils.visible
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


/**
 * File       : android.presenter.fragments.digital_unboxing.DateTumblerFragment
 * Brief      : Set date- in mm/dd/yy or dd/mm/yy
 * Author     : Rajendra
 * Created On : 10/9/2024
 * Details    : Fragment responsible for set date in mm/dd/yy or dd/mm/yy
 */

class UnBoxingDateTumblerFragment : Fragment(),
    KeyboardInputManagerInterface,
    HMIKnobInteractionListener, View.OnClickListener, ComponentSelectionInterface {

    companion object {
        private const val TAG = "DateTumblerFragment"
    }

    private var defaultDay = 1
    private var defaultMonth = 1
    private var defaultYear = 25
    private var viewBinding: FragmentUnboxingDateTimerBinding? = null
    private val binding get() = viewBinding!!
    private var focusedTumbler: BaseTumbler? = null
    private var knobClickCount = 0
    private val rotatorClickCount = mutableMapOf<Int, Int>()
    private var rotator = listOf(0, 1, 2, 3, 4, 5)//Day, Month, year, MM/DD, DD/MM, NEXT
    private var selectedRotator: Int = -1
    private var isTumblerSelected = false
    private var dateDay: MutableList<String>? = null
    private var dateMonth: MutableList<String>? = null
    private val dateYear = TimeAndDateUtils.getYearList()
    private lateinit var dayTumbler: BaseTumbler
    private lateinit var monthTumbler: BaseTumbler
    private lateinit var yearTumbler: BaseTumbler
    private lateinit var dayItemViewHolder: UnBoxingDateTumblerAdapter
    private lateinit var monthItemViewHolder: UnBoxingDateTumblerAdapter
    private lateinit var yearItemViewHolder: UnBoxingDateTumblerAdapter
    private var smoothScrollDelay = 200L
    private var toggleScrollDelay = 300L
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentUnboxingDateTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initDateTumbler(KnobNavigationUtils.knobForwardTrace)
        updateDefaultTimeTumbler()
        updateHeaderBar()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            selectedRotator = rotator[0]
        }
    }

    private fun initView() {
        binding.startNowText.visibility = View.VISIBLE
        binding.startNowText.setTextButtonText(resources.getString(R.string.text_button_set))
        binding.startNowText.setButtonTextColor(R.color.color_white)
        binding.startNowText.isEnabled = true
        binding.startNowText.setOnClickListener(this)

        //Radio toggle visible
        binding.dateTimeFormatWidget.formatSelection.visible()
        binding.dateTimeFormatWidget.leftFormatSelection.visible()
        binding.dateTimeFormatWidget.rightFormatSelection.visible()

        binding.dateTimeFormatWidget.leftFormatSelection.setText(R.string.text_tiles_list_mm_dd_yy_value)
        binding.dateTimeFormatWidget.rightFormatSelection.setText(R.string.text_tiles_list_dd_mm_yy_value)

        //Radio toggle click event
        binding.dateTimeFormatWidget.leftFormatSelection.setOnClickListener(
            onFormatSelectionClicked(
                true
            )
        )
        binding.dateTimeFormatWidget.rightFormatSelection.setOnClickListener(
            onFormatSelectionClicked(false)
        )
        updateDateWidgetView(KnobNavigationUtils.knobForwardTrace)
    }

    private fun initDateTumbler(isKnobRotationActive: Boolean) {
        val isMMDDFormat: Boolean = SettingsManagerUtils.getDateFormat() == SettingsManagerUtils.DateFormatSettings.MMDDYY
        if (isMMDDFormat) {
            dateDay = TimeAndDateUtils.getMonthList()
            dateMonth = TimeAndDateUtils.getDayList()
            binding.labelHourTumbler.text = resources.getString(R.string.text_label_M)
            binding.labelMinuteTumbler.text = resources.getString(R.string.str_day)
        } else {
            dateDay = TimeAndDateUtils.getDayList()
            dateMonth = TimeAndDateUtils.getMonthList()
            binding.labelHourTumbler.text = resources.getString(R.string.str_day)
            binding.labelMinuteTumbler.text = resources.getString(R.string.text_label_M)
        }
        initTumblers(isKnobRotationActive)
        binding.labelSecTumbler.text = resources.getString(R.string.str_year)
    }

    private fun updateHeaderBar() {
        binding.headerBar.apply {
            setTitleText(
                resources.getString(
                    R.string.text_header_set_date
                )
            )
            setRightIcon(R.drawable.numpad_icon)
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setCustomOnClickListener(object :
                HeaderBarWidgetInterface.CustomClickListenerInterface {

                override fun leftIconOnClick() {
                    super.leftIconOnClick()
                    //Due to multiple back happening so navigateSafely regional settings on header back arrow click
                    NavigationViewModel.popBackStack(
                        NavigationUtils.getViewSafely(this@UnBoxingDateTumblerFragment)?.let {
                            Navigation.findNavController(
                                it
                            )
                        }
                    )
                }

                override fun rightIconOnClick() {
                    super.rightIconOnClick()
                    navigateToDateNumberPadScreen()
                }
            })
        }
    }

    private fun initTumblers(isKnobRotationActive: Boolean) {
        dayTumbler = binding.tumblerNumericBasedHours
        dayItemViewHolder = dateDay?.let {
            UnBoxingDateTumblerAdapter(
                it.toList(), KnobNavigationUtils.knobForwardTrace, isKnobRotationActive
            )
        }!!
        dayTumbler.apply {
            isInfiniteScroll = true
            itemViewHolder = dayItemViewHolder
            updateItems(dateDay as java.util.ArrayList<String>?, true)
        }
        monthTumbler = binding.tumblerNumericBasedMins
        monthItemViewHolder = dateMonth?.let {
            UnBoxingDateTumblerAdapter(
                it.toList(),
                false,
                isKnobRotationActive
            )
        }!!
        monthTumbler.apply {
            isInfiniteScroll = true
            itemViewHolder = monthItemViewHolder
            updateItems(dateMonth as java.util.ArrayList<String>?, true)
        }
        yearTumbler = binding.tumblerNumericBasedSeconds
        yearItemViewHolder = UnBoxingDateTumblerAdapter(
            dateYear.toList(), isKnobRotationActive = isKnobRotationActive
        )
        yearTumbler.apply {
            isInfiniteScroll = true
            itemViewHolder = yearItemViewHolder
            updateItems(dateYear as java.util.ArrayList<String>?, true)
        }
    }

    private fun updateDefaultTimeTumbler() {
        val dateArray: Array<String> = if (arguments != null && arguments?.containsKey(BundleKeys.BUNDLE_PROVISIONING_DATE) == true
        ) {
            TimeUtils.getDateArrayArguments(arguments)

        } else {
            TimeUtils.getFormattedDateArray()
        }
        defaultYear = dateArray[2].toInt()
        if (SettingsViewModel.getSettingsViewModel().isDateFormatDayMonth) {
            defaultDay = dateArray[0].toInt()
            defaultMonth = CookingAppUtils.convertStringToInt(dateArray[1])
            HMILogHelper.Logd("Unboxing","DDMM defaultDay= $defaultDay , defaultMonth = $defaultMonth , defaultYear = $defaultYear")
            provideDefault(defaultDay, defaultMonth, defaultYear)
        } else {
            defaultDay = dateArray[1].toInt()
            defaultMonth =  CookingAppUtils.convertStringToInt(dateArray[0])
            HMILogHelper.Logd("Unboxing","MMDD defaultMonth= $defaultMonth , defaultDay = $defaultDay , defaultYear = $defaultYear")
            provideDefault(defaultMonth, defaultDay, defaultYear)
        }
    }

    /**
     * Provides interface to scroll tumbler at desired default position
     * @param defaultDay int
     * @param defaultMonth int
     * @param defaultYear int
     * @noinspection SameParameterValue
     */
    private fun provideDefault(defaultDay: Int, defaultMonth: Int, defaultYear: Int) {
        scrollToValues(defaultDay, defaultMonth, defaultYear)
    }

    private fun scrollToValues(defaultDay: Int, defaultMonth: Int, defaultYear: Int) {
        val dayUpperLimit: Int
        val monthUpperLimit: Int
        if (SettingsManagerUtils.getDateFormat() == SettingsManagerUtils.DateFormatSettings.DDMMYY) {
            dayUpperLimit = 31
            monthUpperLimit = 12
        } else {
            dayUpperLimit = 12
            monthUpperLimit = 31
        }
        val day = IntegerRange(1, 1, dayUpperLimit, defaultDay)
        val dayTumbler = populateTumblerItemTimeValues(day)
        initDateTumbler(binding.tumblerNumericBasedHours, dayTumbler, defaultDay)

        val month = IntegerRange(1, 1, monthUpperLimit, defaultMonth)
        val monthTumbler = populateTumblerItemTimeValues(month)
        initDateTumbler(binding.tumblerNumericBasedMins, monthTumbler, defaultMonth)

        val year = IntegerRange(1, 24, 40, defaultYear)
        val yearTumbler = populateTumblerItemTimeValues(year)
        initDateTumbler(binding.tumblerNumericBasedSeconds, yearTumbler, defaultYear)
    }

    private fun populateTumblerItemTimeValues(dateValues: IntegerRange): List<String> {
        val tumblerElements: MutableList<String> = java.util.ArrayList()
        var i = dateValues.min
        while (i <= dateValues.max) {
            if (i < 10) {
                tumblerElements.add("0$i")
            } else {
                tumblerElements.add(i.toString())
            }
            i += dateValues.step
        }
        return tumblerElements
    }

    /**
     * Method to initialize the Numeric Tumbler.
     *
     * @param list,           List of Tumbler Elements
     * @param dateValues, options from SDK
     */
    private fun initDateTumbler(
        tumbler: BaseTumbler, list: List<String>,
        dateValues: Int
    ) {
        val defaultDateValue = if (dateValues >= 10) {
            dateValues.toString()
        } else {
            "0$dateValues"
        }
        val listInterface: ViewModelListInterface = object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return ArrayList(list)
            }

            /** @noinspection DataFlowIssue
             */
            override fun getDefaultString(): String {
                return defaultDateValue
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


    override fun getKeyboardView(): KeyboardView {
        return KeyboardView(requireContext(), null)
    }

    override fun onHMILeftKnobClick() {
        Log.d(TAG, "onHMILeftKnobClick() called $isTumblerSelected : $selectedRotator")
        focusedTumbler = null
        knobClickCount++
        when (selectedRotator) {
            0 -> {
                focusedTumbler = dayTumbler
                handleKnobClickAndFocus()
            }

            1 -> {
                focusedTumbler = monthTumbler
                handleKnobClickAndFocus()
            }

            2 -> {
                focusedTumbler = yearTumbler
                handleKnobClickAndFocus()
            }

            3 -> {
                isTumblerSelected = false
                onDateFormatSelectionWidgetClickActions(true, true)
            }

            4 -> {
                isTumblerSelected = false
                onDateFormatSelectionWidgetClickActions(false, true)
            }

            5 -> {
                KnobNavigationUtils.knobForwardTrace = true
                isTumblerSelected = false
                handleNextButtonAction()
            }
        }
    }

    // Method to handle knob click logic for first and second clicks
    private fun handleKnobClickAndFocus(knobDirection: String = KnobDirection.CLOCK_WISE_DIRECTION) {
        rotatorClickCount[selectedRotator] = (rotatorClickCount[selectedRotator] ?: 0) + 1
        val currentSelectedRotator = selectedRotator
        when (KnobNavigationUtils.ClickState.values().find { it.count == rotatorClickCount[selectedRotator] }) {
            KnobNavigationUtils.ClickState.FIRST -> {
                // First click: mark isTumblerSelected as true
                isTumblerSelected = true
                rotateFocus(knobDirection,false)
            }

            KnobNavigationUtils.ClickState.SECOND -> {
                // Second click: mark isTumblerSelected as false and update selectedRotator
                isTumblerSelected = false
                updateSelectedRotatorIndex(knobDirection)
                rotateFocus(knobDirection)
                // Reset the click count for the previous selectedRotator (before update)
                rotatorClickCount[currentSelectedRotator] = 0
            }
            else ->{}
        }
    }

    private fun handleNextButtonAction() {
        setTumblerDate()
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
            if (!isTumblerSelected) {
                updateSelectedRotatorIndex(knobDirection)
                rotateFocus(knobDirection)
            } else {
                when (focusedTumbler) {
                    dayTumbler -> {
                        rotateTumblerOnKnobEvents(
                            this,
                            dayTumbler,
                            knobDirection
                        )
                    }

                    monthTumbler -> {
                        rotateTumblerOnKnobEvents(
                            this,
                            monthTumbler,
                            knobDirection
                        )
                    }

                    binding.tumblerNumericBasedSeconds -> {
                        rotateTumblerOnKnobEvents(
                            this,
                            binding.tumblerNumericBasedSeconds,
                            knobDirection
                        )
                    }

                    else -> {
                    }
                }
            }
        } else if (knobId == AppConstants.RIGHT_KNOB_ID) {
            PopUpBuilderUtils.userLeftKnobWarningPopup(this)
        }
    }

    // To update selected rotator
    private fun updateSelectedRotatorIndex(knobDirection: String) {
        when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION -> {
                selectedRotator =
                    if (selectedRotator < rotator.size - 1) selectedRotator + 1 else rotator.size - 1
            }

            KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                selectedRotator =
                    if (selectedRotator <= 0) 0 else selectedRotator - 1
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if(knobId == AppConstants.LEFT_KNOB_ID) resetKnobParameters()
    }

    // Reset the knob related parameters and selections
    private fun resetKnobParameters() {
        isTumblerSelected = false
        rotatorClickCount.clear()
        selectedRotator = -1
        knobClickCount = 0
        toggleFocusDayTumbler(false, false)
        toggleFocusMonthTumbler(false, false)
        toggleFocusYearTumbler(false, false)
        updateDateWidgetView(false)
        binding.startNowText.setBottomButtonViewVisible(false)
    }

    /**
     * @param baseTumbler - Recyclerview tumbler
     * @param position - scroll position
     */
    private fun smoothScrollHandler(baseTumbler: BaseTumbler, position: Int,delay:Long = smoothScrollDelay) {
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

    private fun rotateFocus(knobDirection: String, isKnobRotationActive: Boolean = true) {
        Log.d("Unboxing", "rotateFocus() called with: selectedRotator = $selectedRotator")
        val actions = when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION, KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> arrayOf(
                { /* Action for selectedRotator 0 and CLOCK_WISE_DIRECTION */
                    toggleFocusDayTumbler(true, isKnobRotationActive)
                    toggleFocusMonthTumbler(false)
                    toggleFocusYearTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    updateDateWidgetView()
                },
                { /* Action for selectedRotator 1 and CLOCK_WISE_DIRECTION */
                    toggleFocusDayTumbler(false)
                    toggleFocusMonthTumbler(true, isKnobRotationActive)
                    toggleFocusYearTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    updateDateWidgetView()
                },
                { /* Action for selectedRotator 2 and CLOCK_WISE_DIRECTION */
                    toggleFocusDayTumbler(false)
                    toggleFocusMonthTumbler(false)
                    toggleFocusYearTumbler(true, isKnobRotationActive)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    updateDateWidgetView()
                },
                { /* Action for selectedRotator 3 and CLOCK_WISE_DIRECTION */
                    toggleFocusDayTumbler(false)
                    toggleFocusMonthTumbler(false)
                    toggleFocusYearTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    updateMMDDToggleButton(false)
                },
                { /* Action for selectedRotator 4 and CLOCK_WISE_DIRECTION */
                    toggleFocusDayTumbler(false)
                    toggleFocusMonthTumbler(false)
                    toggleFocusYearTumbler(false)
                    binding.startNowText.setBottomButtonViewVisible(false)
                    updateDDMMToggleButton(false)
                },
                { /* Action for selectedRotator 5 and CLOCK_WISE_DIRECTION */
                    toggleFocusDayTumbler(false)
                    toggleFocusMonthTumbler(false)
                    toggleFocusYearTumbler(false)
                    updateDateWidgetView()
                    binding.startNowText.setBottomButtonViewVisible(true)
                })
            else -> return
        }

        actions.getOrNull(selectedRotator)?.invoke()
    }

    /**
     * Method use for navigate to time num-pad screen
     */
    private fun navigateToDateNumberPadScreen() {
        val dateTimeValue: String = getDateTimeValue()
        HMILogHelper.Logd("Unboxing","MMDD / DDMM Tumbler dateTimeValue------$dateTimeValue")
        val bundle = Bundle()
        bundle.putString(BundleKeys.BUNDLE_PROVISIONING_DATE, dateTimeValue)
        NavigationUtils.navigateSafely(
            this,
            R.id.action_dateTumblerFragment_to_unboxingSetupDateFragment,
            bundle,
            null
        )
    }

    private fun getDateTimeValue(): String {
        val dateTimeValue: String
        var day = binding.tumblerNumericBasedHours.selectedValue.toInt()
        var month = binding.tumblerNumericBasedMins.selectedValue.toInt()
        var year = binding.tumblerNumericBasedSeconds.selectedValue.toInt()

        if (SettingsManagerUtils.getDateFormat() == SettingsManagerUtils.DateFormatSettings.MMDDYY) {
            day = binding.tumblerNumericBasedMins.selectedValue.toInt()
            month = binding.tumblerNumericBasedHours.selectedValue.toInt()
            year = binding.tumblerNumericBasedSeconds.selectedValue.toInt()
        }
        dateTimeValue = buildString {
            append(day)
            append(AppConstants.SLASH)
            append(month)
            append(AppConstants.SLASH)
            append(year)
        }
        return dateTimeValue
    }

    /**
     * Method use for toggle focus on Day,Month,Year tumblers
     *
     * @param isBottomViewVisible
     * @param isKnobRotationActive: knob rotation state
     */
    private fun toggleFocusDayTumbler(
        isBottomViewVisible: Boolean,
        isKnobRotationActive: Boolean = true
    ) {
        dayItemViewHolder = dateDay?.let {
            UnBoxingDateTumblerAdapter(
                it,
                isBottomViewVisible,
                isKnobRotationActive
            )
        }!!
        dayTumbler.itemViewHolder = dayItemViewHolder
        dayTumbler.updateItems(dateDay as java.util.ArrayList<String>?, true)
    }

    private fun toggleFocusMonthTumbler(
        isBottomViewVisible: Boolean,
        isKnobRotationActive: Boolean = true
    ) {
        monthItemViewHolder = dateMonth?.let {
            UnBoxingDateTumblerAdapter(
                it,
                isBottomViewVisible,
                isKnobRotationActive
            )
        }!!
        monthTumbler.itemViewHolder = monthItemViewHolder
        monthTumbler.updateItems(dateMonth as java.util.ArrayList<String>?, true)
    }

    private fun toggleFocusYearTumbler(
        isBottomViewVisible: Boolean,
        isKnobRotationActive: Boolean = true
    ) {
        yearItemViewHolder = UnBoxingDateTumblerAdapter(dateYear, isBottomViewVisible,
            isKnobRotationActive
        )
        yearTumbler.itemViewHolder = yearItemViewHolder
        yearTumbler.updateItems(dateYear as java.util.ArrayList<String>?, true)
    }

    override fun onClick(view: View?) {
        if (view?.id == R.id.startNowText) {
            setTumblerDate()
        }
    }

    private fun setTumblerDate() {
        val currentTime: Calendar = Calendar.getInstance()
        val month: Int
        val day: Int
        var year: Int
        val currentDate: String
        if (SettingsManagerUtils.getDateFormat() == SettingsManagerUtils.DateFormatSettings.DDMMYY) {
            day = binding.tumblerNumericBasedHours.selectedValue.toInt()
            month = binding.tumblerNumericBasedMins.selectedValue.toInt()
            year = binding.tumblerNumericBasedSeconds.selectedValue.toInt()
            currentDate = TimeAndDateUtils.convertDateToDDMMYY(day, month, year)
        } else {
            day = binding.tumblerNumericBasedMins.selectedValue.toInt()
            month = binding.tumblerNumericBasedHours.selectedValue.toInt()
            year = binding.tumblerNumericBasedSeconds.selectedValue.toInt()
            currentDate = TimeAndDateUtils.convertDateToDDMMYY(month, day, year)
        }
        year += resources.getInteger(R.integer.integer_range_2000)
        val maxDay = CalenderUtils.getMaxDayOfMonth(month, year)
        if (SettingsManagerUtils.getDateFormat() == SettingsManagerUtils.DateFormatSettings.DDMMYY) {
            if (!KeyboardTextFormatUtils.getValidDate(
                    currentDate,
                    KeyboardTextFormatUtils.FORMAT_DATE_DD_MM_YY,
                    null
                )
            ) {
                if (day >= maxDay) {
                    val dateRangeError: String = getMaxDayError(maxDay)
                    dateInvalidErrorMessage(dateRangeError)
                }
                return
            }
        } else {
            if (!KeyboardTextFormatUtils.getValidDate(
                    currentDate,
                    KeyboardTextFormatUtils.FORMAT_DATE_MM_DD_YY,
                    null
                )
            ) {
                if (day >= maxDay) {
                    val dateRangeError: String = getMaxDayError(maxDay)
                    dateInvalidErrorMessage(dateRangeError)
                }
                return
            }
        }

        val hour: Int = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute: Int = currentTime.get(Calendar.MINUTE)
        val seconds: Int = currentTime.get(Calendar.SECOND)
        HMILogHelper.Logd(
            "Unboxing",
            "Unboxing: Hour = $hour, Minutes = $minute, Seconds: $seconds Year = $year, Month = $month , Day = $day"
        )
        if (SettingsViewModel.getSettingsViewModel()
                .setTimeModeManual(year, month - 1, day, hour, minute, seconds)
        ) {
            HMILogHelper.Logd(
                "Unboxing",
                "Unboxing: Success - Navigate to the congratulation fragment"
            )
        } else {
            HMILogHelper.Logd("Unboxing", "Unboxing: set date and time unsuccessful")
        }
        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
            NavigationUtils.navigateSafely(
                it,
                R.id.action_dateTumblerFragment_to_unboxingDoneCongratulationFragment,
                null,
                null
            )
        }
    }

    private fun dateInvalidErrorMessage(dateRangeError: String) {
        binding.textViewHelperText.visibility = View.VISIBLE
        binding.textViewHelperText.text = dateRangeError
        TimeAndDateUtils.startFiveSecWarningTimer(binding.textViewHelperText)
    }

    private fun getMaxDayError(maxValueDay: Int): String {
        when (maxValueDay) {
            28 -> return resources.getString(
                R.string.text_dateError_message, AppConstants.DIGIT_ONE, AppConstants.INVALID_DAY_RANGE_1D_28D
            )

            29 -> return resources.getString(
                R.string.text_dateError_message, AppConstants.DIGIT_ONE, AppConstants.INVALID_DAY_RANGE_1D_29D
            )

            30 -> return resources.getString(
                R.string.text_dateError_message, AppConstants.DIGIT_ONE, AppConstants.INVALID_DAY_RANGE_1D_30D
            )

            31 -> return resources.getString(
                R.string.text_dateError_message, AppConstants.DIGIT_ONE, AppConstants.INVALID_DAY_RANGE_1D_31D
            )

            else -> return " "
        }
    }

    private fun updateDDMMToggleButton(isKnobRotationActive: Boolean) {
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

    private fun updateMMDDToggleButton(isKnobRotationActive: Boolean) {
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

    private fun updateDateWidgetView(isKnobRotationActive: Boolean = true) {
        if (SettingsManagerUtils.getDateFormat() == SettingsManagerUtils.DateFormatSettings.DDMMYY) {
            updateDDMMToggleButton(isKnobRotationActive)
        } else {
            updateMMDDToggleButton(isKnobRotationActive)
        }
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

    private fun onFormatSelectionClicked(isMMDDFormat: Boolean): View.OnClickListener {
        return View.OnClickListener {
            onDateFormatSelectionWidgetClickActions(isMMDDFormat)
        }
    }

    private fun onDateFormatSelectionWidgetClickActions(
        isMMDDFormat: Boolean,
        isKnobClick: Boolean = false
    ) {
        val isSelectedDDMMFormat =
            SettingsManagerUtils.getDateFormat() == SettingsManagerUtils.DateFormatSettings.DDMMYY
        val isSelectedMMDDFormat = !isSelectedDDMMFormat
        when {
            isMMDDFormat && isSelectedDDMMFormat -> prepareMMDDTumbler(isKnobClick)
            !isMMDDFormat && isSelectedMMDDFormat -> prepareDDMMTumbler(isKnobClick)
        }
    }

    /**
     * prepare DDMM tumbler and scroll given index
     */
    private fun prepareDDMMTumbler(isKnobRotationActive: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            val day = binding.tumblerNumericBasedHours.selectedValue
            val month = binding.tumblerNumericBasedMins.selectedValue
            val year = binding.tumblerNumericBasedSeconds.selectedValue
            SettingsManagerUtils.setDateFormat(SettingsManagerUtils.DateFormatSettings.DDMMYY)
            updateDDMMToggleButton(false)
            withContext(Dispatchers.Main) {
                initDateTumbler(isKnobRotationActive)
            }
            withContext(Dispatchers.Main) {
                val dayIndex = dateDay?.indexOf(month.toString())
                val monthIndex = dateMonth?.indexOf(day)
                val yearIndex = dateYear.indexOf(year)
                HMILogHelper.Logd("Unboxing","DDMM d = $month , m =$day, Y = $year")
                when {
                    dayIndex != AppConstants.DIGIT_MINUS_ONE ->
                        smoothScrollHandler(binding.tumblerNumericBasedHours, dayIndex ?: 0)

                    else -> {
                        smoothScrollHandler(binding.tumblerNumericBasedHours, 0)
                    }
                }
                when {
                    monthIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                        binding.tumblerNumericBasedMins,
                        monthIndex ?: 0
                    )

                    else -> smoothScrollHandler(binding.tumblerNumericBasedMins, 0)
                }
                when {
                    yearIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                        binding.tumblerNumericBasedSeconds,
                        yearIndex,toggleScrollDelay
                    )

                    else -> smoothScrollHandler(binding.tumblerNumericBasedSeconds, 0)
                }
            }
        }
    }
    /**
     * prepare MMDD tumbler and scroll given index
     */
    private fun prepareMMDDTumbler(isKnobRotationActive: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            val day = binding.tumblerNumericBasedMins.selectedValue
            val month = binding.tumblerNumericBasedHours.selectedValue
            val year = binding.tumblerNumericBasedSeconds.selectedValue
            SettingsManagerUtils.setDateFormat(SettingsManagerUtils.DateFormatSettings.MMDDYY)
            updateMMDDToggleButton(false)
            withContext(Dispatchers.Main) {
                initDateTumbler(isKnobRotationActive)
            }
            withContext(Dispatchers.Main) {
                val dayIndex = dateDay ?.indexOf(day.toString())
                val monthIndex = dateMonth?.indexOf(month)
                val yearIndex = dateYear.indexOf(year)
                HMILogHelper.Logd("Unboxing","MMDD d = $month , m =$day, Y = $year")
                when {
                    dayIndex != AppConstants.DIGIT_MINUS_ONE ->
                        smoothScrollHandler(binding.tumblerNumericBasedHours, dayIndex ?: 0)

                    else -> {
                        smoothScrollHandler(binding.tumblerNumericBasedHours, 0)
                    }
                }
                when {
                    monthIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                        binding.tumblerNumericBasedMins,
                        monthIndex ?: 0
                    )

                    else -> smoothScrollHandler(binding.tumblerNumericBasedMins, 0)
                }
                when {
                    yearIndex != AppConstants.DIGIT_MINUS_ONE -> smoothScrollHandler(
                        binding.tumblerNumericBasedSeconds,
                        yearIndex,toggleScrollDelay
                    )

                    else -> smoothScrollHandler(binding.tumblerNumericBasedSeconds, 0)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        isTumblerSelected = false
        rotatorClickCount.clear()
        selectedRotator = -1
        knobClickCount = 0
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }

    override fun selectionUpdated(index: Int) {
        // do nothing
    }

    override fun onTumblerTouchInteraction(tumblerView: BaseTumbler?, action: Int) {
        super.onTumblerTouchInteraction(tumblerView, action)
        resetKnobParameters()
    }
}
