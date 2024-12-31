/*
 *
 * * ************************************************************************************************
 * * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * * ************************************************************************************************
 *
 */
package android.presenter.basefragments

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.DelayTumblerItemBinding
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.utils.RecipeExecutionState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.base.ComponentSelectionInterface
import com.whirlpool.hmi.uicomponents.tools.util.Constants
import com.whirlpool.hmi.uicomponents.widgets.clock.ClockViewModel
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerItemViewInterface
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerViewHolderInterface
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.DEFAULT_DOUBLE_ZERO
import core.utils.AppConstants.DEGREE_SYMBOL
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.rotateTumblerOnKnobEvents
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SettingsManagerUtils
import core.utils.TimeFormat
import core.utils.TimeUtils
import core.utils.setListObjectWithDefaultSelection
import core.viewHolderHelpers.TumblerViewHolderHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.concurrent.TimeUnit


/**
 * File       :com.whirlpool.cooking.ka.ktn.android.presenter.basefragments.AbstractNumericTumblerFragment.
 * Brief      : NumericTumbler CVT Abstract class.
 * Author     : SINGHJ25.
 * Created On : 24.Jan.2024
 * Details    :
 */
abstract class AbstractNumericTumblerFragment : SuperAbstractTimeoutEnableFragment(),
    View.OnClickListener, ComponentSelectionInterface, HMIKnobInteractionListener {
    private var selectedDelayTimer = Constants.NOT_IMPLEMENTED
    private var isSelfClean = false
    protected var tumblerViewHolderHelper: TumblerViewHolderHelper? = null
    private var tumblerStrings: ArrayList<String>? = null
    protected var selectedTemp = Constants.NOT_IMPLEMENTED
    protected var selectedDoubleTemp = Constants.NOT_IMPLEMENTED
    private var onScrollListener: RecyclerView.OnScrollListener? = null
    private var delayTimerRange: IntegerRange? = null
    var delayTimerList: ArrayList<String>? = null
    private var rotator = listOf(0, 1) // Tumbler, CTA's,
    private var selectedRotator = rotator[0]
    private var isTumblerSelected = true
    private var isGhostButtonVisible: Boolean = false
    private var isPrimaryCTAButtonVisible: Boolean = false
    private var knobRotationCount = 0
    protected var isDelayWithCookTime : Boolean = false
    private var programmedCookTime : Long = 0
    private var isComingFromAnotherScreenAsBundle: Boolean = false
    private var tempFromBundle: Int = AppConstants.DEFAULT_SELECTED_TEMP
    private var isPlusSymbolEnabled = false
    private var isDelayTumbler = false
    // holds the value of current time when this fragment is loaded
    private var currentTime = TimeUnit.MILLISECONDS.toSeconds(ClockViewModel.getClockViewModel().dateAndTime.value?.time?:0)
    /**
     * show degree symbol along with values in the tumbler or not based on the input it will attach
     * @return true/false
     */
    protected var isShowSuffixDecoration: Boolean = false

    protected abstract fun setCtaLeft()
    protected abstract fun setCtaRight()
    protected abstract fun setHeaderLevel()

    /**
     * Method that will be override in child class
     */
    protected abstract fun initTumbler()

    /**
     * Method to get the Tumbler +5 | -5 visibility to be shown in the view
     */
    protected abstract fun provideTumblerModifierTextVisibility(): Int

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        tumblerViewHolderHelper = TumblerViewHolderHelper()
        tumblerViewHolderHelper?.onCreateView(inflater, container, savedInstanceState)
        return tumblerViewHolderHelper?.fragmentTumblerBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.lifecycleOwner = this
        tumblerViewHolderHelper?.fragmentTumblerBinding?.recipeViewModel =
            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
        tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel =
            CookingViewModelFactory.getInScopeViewModel()
        tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.setItemSuffixDecoration(
            DEGREE_SYMBOL
        )
        tumblerViewHolderHelper?.fragmentTumblerBinding?.degreesType?.setText(
            if (CookingAppUtils.isFAHRENHEITUnitConfigured()) R.string.text_temperature_unit_fahrenheit else R.string.text_temperature_unit_celsius
        )
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.setOnClickListener(this)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnGhost?.setOnClickListener(this)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.constraintRightButton?.setOnClickListener(this)
        updateDelayButtonInTumbler()
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        isSelfClean =
            tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.isSelfCleanRecipe!!
        val (isCookTime, setCookTime) = CookingAppUtils.isCookTimeProgrammed(getCookingViewModel())
        isDelayWithCookTime = isCookTime
        programmedCookTime = setCookTime
        initViews()
        configutionHmiKey()
        setCtaLeft()
        setHeaderLevel()
        setCtaRight()
        getProductByVariant()
        getInScopeViewModel()
        manageRightButton()
        updateCtaRightButton()
        isPrimaryCTAButtonVisible =
            tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.isVisible == true
        isGhostButtonVisible =
            tumblerViewHolderHelper?.fragmentTumblerBinding?.btnGhost?.isVisible == true
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
        }

    }

    open fun manageRightButton() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setRightIconVisibility(true)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setRightIcon(R.drawable.numpad_icon)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.headerBar?.setOvenCavityIconVisibility(false)
    }

    abstract override fun onClick(v: View?)


    /**
     * used to get inScopeViewModel
     */
    protected open fun getInScopeViewModel(): CookingViewModel? {
        return CookingViewModelFactory.getInScopeViewModel()
    }

    /**
     * used to get cooking product variant
     */
    protected open fun getProductByVariant(): CookingViewModelFactory.ProductVariantEnum? {
        return CookingViewModelFactory.getProductVariantEnum()
    }

    override fun onDestroyView() {
        clearMemory()
        delayTimerRange = null
        delayTimerList = null
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        SettingsViewModel.getSettingsViewModel().temperatureUnit.removeObservers(viewLifecycleOwner)
        onScrollListener = null
        super.onDestroyView()
    }

    /**
     * This method will be used to write memory cleanup code.
     */
    private fun clearMemory() {
        onScrollListener = null
        isTumblerSelected = true
        selectedRotator = 0
        knobRotationCount = 0
        tumblerViewHolderHelper?.onDestroyView()
    }

    protected open fun initTumbler(tempOption: Any?) {
        if (tempOption != null) {
            // for temperature need to add degree symbol at suffix with the value
            if (isShowSuffixDecoration) {
                tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.setItemSuffixDecoration(
                    DEGREE_SYMBOL
                )
            }
            tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.itemViewHolder =
                DelayTumblerItem()
            setNumericTumblerData(tempOption)
        }
    }


    /**
     * This function will initialize the delay tumbler
     */

    protected fun initDelayTumbler() {
        delayTimerRange =
            tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.delayTimeOptions?.value
        //Set Default selected delay timer
        var delayTime: Long? =
            tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.delayTime?.value
        if (Objects.nonNull(delayTimerRange)) {
            delayTimerList = if(isDelayWithCookTime) {
                adjustDelayTimerListBasedOnCurrentTime()
            }else{
                delayTimerRange?.listItems
            }
            if(tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.DELAYED)
                delayTime = delayTime?.plus(programmedCookTime)

            if (delayTime != null && delayTime > 0) {
                selectedDelayTimer = delayTime.toString().toInt()
                HMILogHelper.Logi(
                    "DelayTumbler : selectedTemp : getDelayTime() : $selectedDelayTimer"
                )
            } else {
                updateDelayTimerRangeValue()
                HMILogHelper.Logi(
                    "DelayTumbler : selectedTemp : getDefaultValue() : $selectedDelayTimer"
                )
            }
            setTumblerData()
            isDelayTumbler = true
        }
    }

    /**
     * to adjust delay timer value based on current time
     * delayTimerRange to be loaded from capability file
     * @return list of time such as [5:15 PM, 50 PM, etc]
     */
    private fun adjustDelayTimerListBasedOnCurrentTime(): ArrayList<String> {
        val values = ArrayList<String>()
        val delayRange = tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.delayTimeOptions?.value
        val increment = delayRange?.step?:0

        val newMin =  delayRange?.min?.plus(programmedCookTime)?.toInt()?:0
        val newMax = delayRange?.max?.plus(programmedCookTime)?.toInt()?:0
        val newDefault = delayRange?.defaultValue?.plus(programmedCookTime)?.toInt()?:0
        delayTimerRange = IntegerRange(CookingAppUtils.buildRangeJsonObject(delayRange?.step?:0, newDefault, newMin, newMax))
        HMILogHelper.Logd(tag, "slowRoastDelayTumbler newDefault $newDefault, newMin $newMin, max $newMax, cookTime $programmedCookTime")
        if (increment > 0) {
            var i: Int = newMin
            while (i <= (delayTimerRange?.max ?: 0)) {
                values.add(i.toString())
                i += increment
            }
        }
        return values
    }

    /**
     * update delay tumbler data every minute to sync with current time
     *
     */
    fun updateDelayTumblerEveryMinute() {
        HMILogHelper.Logd(tag, "initiating delayTumblerData isDelayWithCookTime =$isDelayWithCookTime")
        initDelayTumbler()
        ClockViewModel.getClockViewModel().dateAndTime.observe(viewLifecycleOwner) {
            val newTime = TimeUnit.MILLISECONDS.toSeconds(it?.time ?: 0)
            HMILogHelper.Logd(
                tag,
                "currentTime $currentTime, newTime $newTime selectedDelay $selectedDelayTimer isDelayWithCookTime $isDelayWithCookTime"
            )
            if(isDelayWithCookTime){
                //to avoid multiple updates coming from clockViewModel if it observe first, trouble auto scrolling tumbler
                if(newTime > currentTime.plus(AppConstants.DIGIT_TEN)) {
                    currentTime = newTime
                    HMILogHelper.Logd(tag, "updating delayTimer with CookTime tumblerItems based on every minute change")
                    tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.updateItems(
                        adjustDelayTimerListBasedOnCurrentTime(), selectedDelayTimer.toString(),true
                    )
                }else HMILogHelper.Logd(tag, "NOT updating delayTimer with CookTime tumblerItems as called multiple times in a short span of SECONDS=${newTime.minus(currentTime)}")
                updateTumblerInfoText(
                    tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.selectedIndex
                        ?: 0
                )
            }else{
                currentTime = newTime
                updateTumblerInfoText(
                    tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.selectedIndex
                        ?: 0
                )
            }
        }
    }

    /**
     * function to update value at scroll
     */
    @Suppress("UNUSED_ANONYMOUS_PARAMETER")
    private fun attachScrollListenerOnTumbler() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.setOnScrollChangeListener { v: View?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            rightButtonEnableOnScroll()
            if (isDelayTumbler){
                val pos: Int? =
                    tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.selectedIndex
                pos?.let { updateTumblerInfoText(it) }
            }
        }
    }

    /**
     * Method to update timer info text along with tumbler.
     * @param position, Position of current selected data at tumbler.
     */
    @SuppressLint("StringFormatInvalid")
    private fun updateTumblerInfoText(position: Int) {
        delayTimerList.let {
            if (!it.isNullOrEmpty()) {
                tumblerViewHolderHelper?.provideDegreeTypeTextView()?.visibility = View.VISIBLE
                if (tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel != null) {
                    if(isDelayWithCookTime){
                        val startsAfter = CookingAppUtils.displayCookTimeToUser(requireContext(), it[position].toLong().minus(programmedCookTime), false, arrayOf(R.string.text_label_hr, R.string.text_label_min, R.string.text_label_sec))
                        tumblerViewHolderHelper?.provideDegreeTypeTextView()?.text =
                            getString(R.string.text_delay_starts_after, startsAfter)
                        HMILogHelper.Logi("DelayTumbler : Position : $position slowRoast startsAfter : $startsAfter")
                    }else {
                        val timerAMPMValue = getStartAtDelayTime(it[position].toLong())
                        tumblerViewHolderHelper?.provideDegreeTypeTextView()?.text =
                            getString(R.string.text_start_at, timerAMPMValue)
                        HMILogHelper.Logi("DelayTumbler : Position : $position timerAMPMValue : $timerAMPMValue")
                    }
                }
            }
        }
    }

    /**
     * function to check whether the tumbler data exist or set the new tumbler data and the view
     */
    private fun setTumblerData() {
        if (Objects.nonNull(delayTimerList)) {
            if (delayTimerList?.contains(selectedDelayTimer.toString()) == true) {
                tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.itemViewHolder =
                    if (isDelayWithCookTime) SlowRoastDelayTumblerItem() else DelayTumblerItem()
                HMILogHelper.Logi("DelayTumbler contains : selectedDelayTimer $selectedDelayTimer")
                tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.setListObjectWithDefaultSelection(
                    delayTimerRange as ViewModelListInterface, selectedDelayTimer.toString()
                )
            } else {
                HMILogHelper.Logi("DelayTumbler : selectedDelayTimer: $selectedDelayTimer")
                tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.listObject =
                    delayTimerRange
            }
        }
    }

    /**
     * function to check whether the tumbler data exist or set the new tumbler data and the view
     * @param tempOption Temperature data list
     */
    protected open fun setNumericTumblerData(tempOption: Any?) {
        if (tempOption != null) {
            val temperatureIntegerRange = tempOption as IntegerRange
            val tumblerStrings = temperatureIntegerRange.listItems
            updateTemperatureIntegerRangeValue(temperatureIntegerRange)
            if (temperatureIntegerRange.listItems.contains(selectedTemp.toString())) {   // SelectedTemp is not already present in the list.
                tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.setListObjectWithDefaultSelection(
                    temperatureIntegerRange, selectedTemp.toString()
                )
                HMILogHelper.Logi(
                    "TemperatureTumbler0: selectedTemp: $selectedTemp scrollIndex $selectedTemp TemperatureTumblerIndexOf " + tumblerStrings.indexOf(
                        selectedTemp.toString()
                    )
                )
            } else {
                updateTumblerWithNewIntegerItemIndex(temperatureIntegerRange)
            }
        }
    }

    /**
     * function to check temperature preexist or update with default value
     * @param temperatureRange Temperature Range Value
     */
    protected open fun updateTemperatureIntegerRangeValue(temperatureRange: IntegerRange) {
        // Check if selectedTemp is out of range or not initialized
        if (Constants.NOT_IMPLEMENTED == selectedTemp || selectedTemp == 0) {
            selectedTemp = temperatureRange.defaultValue
            HMILogHelper.Logi(
                "TemperatureTumbler1: set temperatureRange.getDefaultValue(): $selectedTemp as selectedTemp"
            )
        } else if (Constants.NOT_IMPLEMENTED == CookingAppUtils.getIndexForNewItemFromIntegerRange(
                temperatureRange, selectedTemp
            )
        ) {
            // If selectedTemp exists but is out of range
            if (selectedTemp > temperatureRange.max) {
                selectedTemp = temperatureRange.max
                HMILogHelper.Logi(
                    ("TemperatureTumbler2: set temperatureRange.getMax(): $selectedTemp as selectedTemp")
                )
            } else if (selectedTemp < temperatureRange.min) {
                // If selectedTemp is less than the minimum, update selectedTemp
                selectedTemp = temperatureRange.min
                HMILogHelper.Logi(
                    ("TemperatureTumbler3: set temperatureRange.getMin(): $selectedTemp as selectedTemp")
                )
            }
        }
    }

    /**
     * tempTumblerIndex is updated with new item's index.
     * @param temperatureRange Temperature Range Value
     */
    protected open fun updateTumblerWithNewIntegerItemIndex(temperatureRange: IntegerRange) {
        val tempTumblerIntegerIndex =
            CookingAppUtils.getIndexForNewItemFromIntegerRange(temperatureRange, selectedTemp)
        val temperatureValues = temperatureRange.listItems
        if (tempTumblerIntegerIndex >= 0 && tempTumblerIntegerIndex <= temperatureValues.size) { //  tempTumblerIndex is updated with new item's index.
            temperatureValues.add(tempTumblerIntegerIndex, selectedTemp.toString())
            val temperatureList: ViewModelListInterface =
                getTemperatureList(temperatureValues, true)
            tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.setListObjectWithTempItem(
                temperatureList, selectedTemp.toString()
            )
            HMILogHelper.Logi(
                "TemperatureTumbler: selectedTemp: $selectedTemp not found added new values at position $tempTumblerIntegerIndex"
            )
        } else {
            HMILogHelper.Loge("Invalid index: $tempTumblerIntegerIndex")
        }
    }

    /**
     * View model interface to interact in the tumbler view based on the temperature range value
     * @param tumblerDataValueList      tumbler Data list
     * @param isTemperatureRangeInteger is given input is in Integer Range or Double Range to update the data
     * @return ViewModelListInterface
     */
    protected open fun getTemperatureList(
        tumblerDataValueList: ArrayList<String>,
        @Suppress("SameParameterValue") isTemperatureRangeInteger: Boolean,
    ): ViewModelListInterface {
        return object : ViewModelListInterface {
            override fun getListItems(): java.util.ArrayList<String> {
                return tumblerDataValueList
            }

            override fun getDefaultString(): String {
                return (if (isTemperatureRangeInteger) selectedTemp else selectedDoubleTemp).toString()
            }

            override fun getValue(index: Int): Any {
                return tumblerDataValueList[index]
            }

            override fun isValid(value: Any): Boolean {
                return tumblerDataValueList.contains(value.toString())
            }
        }
    }

    /**
     * function to update the delay time range
     */
    private fun updateDelayTimerRangeValue() {
        if (Constants.NOT_IMPLEMENTED == selectedDelayTimer) {  // selectedTemp doesn't exist.
            selectedDelayTimer = delayTimerRange?.defaultValue?:0
            HMILogHelper.Logi(
                "DelayTumbler : set DelayTimerRange.getDefaultValue(): $selectedDelayTimer as selectedTemp"
            )
        } else if (Constants.NOT_IMPLEMENTED == CookingAppUtils.getIndexForNewItemFromIntegerRange(
                delayTimerRange!!, selectedDelayTimer
            )
        ) {
            // selectedTemp exists but is out of range.
            if (selectedDelayTimer > delayTimerRange!!.max) {
                selectedDelayTimer = delayTimerRange!!.max
                HMILogHelper.Logi(
                    ("DelayTumbler : set DelayTimerRange.getMax(): $selectedDelayTimer as selectedTemp")
                )
            } else if (selectedDelayTimer < delayTimerRange!!.min) {
                selectedDelayTimer = delayTimerRange!!.min
                HMILogHelper.Logi(
                    ("DelayTumbler : set DelayTimerRange.getMin(): $selectedDelayTimer as selectedTemp")
                )
            }
        }
    }

    protected fun isPlusSymbolEnabled(@Suppress("SameParameterValue") state: Boolean) {
        isPlusSymbolEnabled = state
    }

    /**
     * Initialize the views here.
     */
    private fun initViews() {
        updateSelectedTemperatureForCycleTemperature()
        initTumbler()
        attachScrollListenerOnTumbler()
    }

    /**
     * Getter pf Temperature tumbler fragment recycler view
     */
    protected val recyclerView: BaseTumbler?
        get() = tumblerViewHolderHelper?.provideNumericTumbler()

    override fun onStop() {
        super.onStop()
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResult(AppConstants.TIMEOUT_CALLBACK)
        NavigationUtils.getVisibleFragment()?.parentFragmentManager?.clearFragmentResultListener(AppConstants.TIMEOUT_CALLBACK)
    }

    /**
     * Method to assign Learn More icons visibility
     */
    @Suppress("unused")
    protected fun setLearnMoreIcon(): Int {
        return View.GONE
    }

    /**
     * This function will initialize the numeric tumbler
     */
    protected fun initTemperatureTumblerForRange(temperatureIntegerRange: LiveData<IntegerRange>?) {
        temperatureIntegerRange?.observe(
            this,
            Observer<Any> { tempOptions: Any? ->
                if (null == tempOptions) {
                    HMILogHelper.Logd("getTargetTemperatureOptions returns null")
                    return@Observer
                }
                if (tempOptions is IntegerRange) {
                    val temperatureRange: IntegerRange = tempOptions
                    if (temperatureRange.listItems.isNotEmpty() && !(temperatureRange.min
                            .toDouble() == 0.0 && temperatureRange.max.toDouble() == 0.0)
                    ) {
                        HMILogHelper.Logi("getTargetTemperatureOptions max " + temperatureRange.max)
                        HMILogHelper.Logi("getTargetTemperatureOptions min " + temperatureRange.min)
                        HMILogHelper.Logi("getTargetTemperatureOptions default " + temperatureRange.defaultValue + " " + temperatureRange.defaultString)
                        HMILogHelper.Logi("getTargetTemperatureOptions step " + temperatureRange.step)
                        initTumbler(temperatureRange)
                    }
                }
            })
    }

    /**
     * this method will display subtext as celsius or fahrenheit as required
     */

    protected fun setTemperatureTypeSubText() {
            tumblerViewHolderHelper?.provideDegreeTypeTextView()?.visibility = View.VISIBLE
            SettingsViewModel.getSettingsViewModel().temperatureUnit.observe(viewLifecycleOwner) {
                HMILogHelper.Logd("Updating temperature unit:  ${SettingsViewModel.getSettingsViewModel().temperatureUnit.value}")
                tumblerViewHolderHelper?.fragmentTumblerBinding?.degreesType?.setText(if (CookingAppUtils.isFAHRENHEITUnitConfigured()) R.string.text_temperature_unit_fahrenheit else R.string.text_temperature_unit_celsius)
            }
    }

    /**
     * this method will disable the right modifier
     */
    @Suppress("unused", "unused")
    protected fun disableRightModifier(isEnabled: Boolean) {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewMinus?.isEnabled = isEnabled
    }

    /**
     * this method will disable the left modifier
     */
    @Suppress("unused")
    protected fun disableLeftModifier(isEnabled: Boolean) {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewPlus?.isEnabled = isEnabled
    }

    /**
     * To set +5 | -5 degree text Visibility & tumbler info helper text visibility
     */
    @Suppress("unused")
    protected fun setTumblerPlusMinusVisibility() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewPlus?.visibility =
            provideTumblerModifierTextVisibility()
        tumblerViewHolderHelper?.fragmentTumblerBinding?.imageViewDivider?.visibility =
            provideTumblerModifierTextVisibility()
        tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewMinus?.visibility =
            provideTumblerModifierTextVisibility()
    }

    override fun selectionUpdated(index: Int) {
        @Suppress("SameParameterValue")
        when (index) {
            0 -> {
                tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewMinus?.visibility =
                    View.GONE
                tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewPlus?.visibility =
                    View.VISIBLE
            }

            tumblerStrings!!.size - 1 -> {
                tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewPlus?.visibility =
                    View.GONE
                tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewMinus?.visibility =
                    View.VISIBLE
            }

            else -> {
                tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewPlus?.visibility =
                    View.VISIBLE
                tumblerViewHolderHelper?.fragmentTumblerBinding?.textViewMinus?.visibility =
                    View.VISIBLE
            }
        }
    }

    /**
     * function to get String with hour and min string
     * @return  Spannable Builder
     */
    private fun getTimeAndUnit(
        delaySecondsValue: String?,
        suffixString: String,
        timeFormat: Int,
        @Suppress("SameParameterValue") withIn12Hours: Boolean,
    ): SpannableStringBuilder {
        val builder = SpannableStringBuilder()
        if (!delaySecondsValue.isNullOrEmpty()) {
            var timeValue = TimeUtils.getSecondsToHourMinute(delaySecondsValue.toInt(), timeFormat)
            if (timeValue.isEmpty() && (TimeFormat.MINUTES_FORMAT == timeFormat)) {
                timeValue = DEFAULT_DOUBLE_ZERO
            }
            if (timeValue.isEmpty() && delaySecondsValue.toInt() >= 3600 && timeFormat == TimeFormat.MINUTES_FORMAT) {
                HMILogHelper.Logd(
                    tag,
                    "delaySecondsValue $delaySecondsValue is for hours and appending 00"
                )
                timeValue = DEFAULT_DOUBLE_ZERO
            }
            if (timeValue.isNotEmpty() && (timeValue.toInt() > 0 || timeValue.equals(
                    DEFAULT_DOUBLE_ZERO, ignoreCase = true
                ))
            ) {
                if (withIn12Hours) {
                    val hoursValue = timeValue.toInt()
                    if (hoursValue > TimeUtils.TWELVE_HOURS && hoursValue <= TimeUtils.HOURS_IN_A_DAY) {
                        timeValue = (hoursValue - TimeUtils.TWELVE_HOURS).toString()
                    } else if (hoursValue > TimeUtils.HOURS_IN_A_DAY) {
                        timeValue = (hoursValue - TimeUtils.HOURS_IN_A_DAY).toString()
                    }
                }
                val spannableTimeString = SpannableString(timeValue)
                spannableTimeString.setSpan(
                    TextAppearanceSpan(context, R.style.TumblerTextViewStyle),
                    0,
                    timeValue.length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
                val spannableUnitString = SpannableString(suffixString)
                spannableUnitString.setSpan(
                    TextAppearanceSpan(context, R.style.DelayTumblerUnitTextViewStyle),
                    0,
                    suffixString.length,
                    Spanned.SPAN_INCLUSIVE_INCLUSIVE
                )
                builder.append(spannableTimeString)
                builder.append(spannableUnitString)
                return builder
            }
        }
        return builder
    }

    /**
     * function to get Hour Minute value in Absolute Text format as eg 2(H small size) 3(M small size)
     * @param index Index to get Spannable View
     * @return Combined Spannable View in Spannable Builders
     */
    private fun getHourMinuteSpannableView(index: Int): SpannableStringBuilder {
        val dateHourString: SpannableStringBuilder = getTimeAndUnit(
            delayTimerList!![index],
            getString(R.string.text_label_H),
            TimeFormat.HOURS_FORMAT,
            false
        )
        val dateMinString: SpannableStringBuilder = getTimeAndUnit(
            delayTimerList!![index],
            getString(R.string.text_label_M),
            TimeFormat.MINUTES_FORMAT,
            false
        )
        val builder = SpannableStringBuilder()
        if (dateHourString.isNotEmpty()) {
            builder.append(dateHourString)
            builder.append(AppConstants.EMPTY_SPACE)
        }

        if (dateMinString.isNotEmpty()) {
            if (dateHourString.length >= 0) builder.append(AppConstants.EMPTY_SPACE)
            builder.append(dateMinString)
        }
        return builder
    }

    /**
     * Brief      : DelayTumblerItem this class will provide delay item tumbler for the base tumbler.
     * Author     : SINGHJ25.
     * Created On : 29.feb.2024
     * Details    :
     */
    @Suppress("RemoveEmptyPrimaryConstructor")
    inner class DelayTumblerItem() : BaseTumblerItemViewInterface {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val tumblerCustomItemViewBinding: DelayTumblerItemBinding =
                DelayTumblerItemBinding.inflate(LayoutInflater.from(parent.context))
            return DelayTumblerViewHolder(tumblerCustomItemViewBinding)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            itemIdentifier: String,
            index: Int,
            isSelected: Boolean,
        ) {
            if (holder is DelayTumblerViewHolder) {
                val formattedIdentifier =
                    if (isPlusSymbolEnabled && !itemIdentifier.startsWith("-") && itemIdentifier.toInt() > 0) {
                        "+${itemIdentifier}"
                    } else {
                        itemIdentifier
                    }

                if (setSuffixDecoration().isNotEmpty())
                    holder.tumblerCustomItemViewBinding.title.text =
                        formattedIdentifier + setSuffixDecoration()
                else {
                    val tumblerData = getHourMinuteSpannableView(index)
                    holder.tumblerCustomItemViewBinding.title.text = tumblerData
                }

                if (isSelected) {
                    holder.tumblerCustomItemViewBinding.title.setTextColor(
                        holder.tumblerCustomItemViewBinding.title.context
                            .getColor(R.color.common_solid_white)
                    )
                } else {
                    holder.tumblerCustomItemViewBinding.title.setTextColor(
                        holder.tumblerCustomItemViewBinding.title.context
                            .getColor(R.color.tumbler_non_selected_text_color)
                    )
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        /**
         * Brief      : DelayTumblerViewHolder this class will provide view holder.
         * Author     : SINGHJ25.
         * Created On : 29.feb.2024
         * Details    :
         */
        private inner class DelayTumblerViewHolder(var tumblerCustomItemViewBinding: DelayTumblerItemBinding) :
            RecyclerView.ViewHolder(tumblerCustomItemViewBinding.getRoot()),
            BaseTumblerViewHolderInterface {
            private var value: String? = null

            override fun getValue(): String {
                return (value)!!
            }

            override fun setValue(value: String) {
                this.value = value
            }

            override fun getDisplayedText(): String {
                return tumblerCustomItemViewBinding.title.text.toString()
            }
        }
    }
    /**
     * get time value in string to show until or ready at 1:20 AM, 4:45 PM
     *
     * @param setValueInSeconds set value in seconds
     * @return string to show until or ready time
     */
    private fun getStartAtDelayTime(setValueInSeconds: Long): String{
        val timeFormat = if(SettingsManagerUtils.getTimeFormat() ==  SettingsManagerUtils.TimeFormatSettings.H_12) TimeUtils.TEXT_TIME_FORMAT_HOUR else TimeUtils.TEXT_TIME_FORMAT_24_HOUR
        val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
     /*   if(getCookingViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.DELAYED){
            val remainingDelay = getCookingViewModel()?.recipeExecutionViewModel?.remainingDelayTime?.value?:0
            HMILogHelper.Logd(tag, "Delayed without cookTime starts At: remainingDelay $remainingDelay, setValueInSeconds $setValueInSeconds")
            return dateFormat.format(
                Date(TimeUnit.SECONDS.toMillis(currentTime.plus(setValueInSeconds.plus(remainingDelay)))
                ))
        }*/
        return dateFormat.format(
            Date(TimeUnit.SECONDS.toMillis(currentTime.plus(setValueInSeconds)))
        )
    }
    /**
     * get time value in string to show until or ready at 1:20 AM, 4:45 PM
     *
     * @param setValueInSeconds set value in seconds
     * @return string to show until or ready time
     */
    private fun getUntilReadyTime(setValueInSeconds: Long): String{
        val timeFormat = if(SettingsManagerUtils.getTimeFormat() ==  SettingsManagerUtils.TimeFormatSettings.H_12) TimeUtils.TEXT_TIME_FORMAT_HOUR else TimeUtils.TEXT_TIME_FORMAT_24_HOUR
        val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        if(getCookingViewModel()?.recipeExecutionViewModel?.recipeExecutionState?.value == RecipeExecutionState.DELAYED){
            val remainingDelay = (getCookingViewModel()?.recipeExecutionViewModel?.delayTime?.value?:0).minus((getCookingViewModel()?.recipeExecutionViewModel?.remainingDelayTime?.value?:0))
            return dateFormat.format(
                Date(TimeUnit.SECONDS.toMillis(currentTime.plus(setValueInSeconds.minus(remainingDelay)))
                ))
        }
        return dateFormat.format(
            Date(TimeUnit.SECONDS.toMillis(currentTime.plus(setValueInSeconds)))
        )
    }

    /**
     * Brief      : DelayTumblerItem this class will provide delay item tumbler for the base tumbler only for SlowRoast Recipe
     * Author     : Hiren
     * Created On : 11/15/2024
     * Details    : This view holder will have time to be shown as current time with 12 and 24 hr format
     */
    @Suppress("RemoveEmptyPrimaryConstructor")
    inner class SlowRoastDelayTumblerItem() : BaseTumblerItemViewInterface {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val tumblerCustomItemViewBinding: DelayTumblerItemBinding =
                DelayTumblerItemBinding.inflate(LayoutInflater.from(parent.context))
            return SlowRoastDelayTumblerViewHolder(tumblerCustomItemViewBinding)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(
            holder: RecyclerView.ViewHolder,
            itemIdentifier: String,
            index: Int,
            isSelected: Boolean,
        ) {
            if (holder is SlowRoastDelayTumblerViewHolder) {
                val formattedIdentifier =
                    if (isPlusSymbolEnabled && !itemIdentifier.startsWith("-") && itemIdentifier.toInt() > 0) {
                        "+${itemIdentifier}"
                    } else {
                        itemIdentifier
                    }

                if (setSuffixDecoration().isNotEmpty())
                    holder.tumblerCustomItemViewBinding.title.text =
                        formattedIdentifier + setSuffixDecoration()
                else {
                    val tumblerData = getUntilReadyTime(delayTimerList!![index].toLong())
                    holder.tumblerCustomItemViewBinding.title.text = tumblerData
                }

                if (isSelected) {
                    holder.tumblerCustomItemViewBinding.title.setTextColor(
                        holder.tumblerCustomItemViewBinding.title.context
                            .getColor(R.color.common_solid_white)
                    )
                } else {
                    holder.tumblerCustomItemViewBinding.title.setTextColor(
                        holder.tumblerCustomItemViewBinding.title.context
                            .getColor(R.color.tumbler_non_selected_text_color)
                    )
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            return 0
        }

        /**
         * Brief      : DelayTumblerViewHolder this class will provide view holder.
         * Author     : SINGHJ25.
         * Created On : 29.feb.2024
         * Details    :
         */
        private inner class SlowRoastDelayTumblerViewHolder(var tumblerCustomItemViewBinding: DelayTumblerItemBinding) :
            RecyclerView.ViewHolder(tumblerCustomItemViewBinding.getRoot()),
            BaseTumblerViewHolderInterface {
            private var value: String? = null

            override fun getValue(): String {
                return (value)!!
            }

            override fun setValue(value: String) {
                this.value = value
            }

            override fun getDisplayedText(): String {
                return tumblerCustomItemViewBinding.title.text.toString()
            }
        }
    }

    protected open fun getCookingViewModel(): CookingViewModel? {
        return getBinding()?.cookingViewModel
    }

    protected abstract fun setSuffixDecoration(): String

    fun getBinding() = tumblerViewHolderHelper?.fragmentTumblerBinding

    /**
     * Handle all generic temperature tumbler click events here including switch to numpad based on different view ids
     */
    //TODO need to refine how temperature is being set to recipe
    protected fun handleGenericTemperatureClick(v: View?) {
        when (v?.id) {
            getBinding()?.btnPrimary?.id -> {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    if(getBinding()?.btnPrimary?.text?.equals(getString(R.string.text_button_next)) == true){
                        R.raw.button_press
                    }else{
                        R.raw.start_press
                    },
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                recyclerView?.selectedValue?.toFloat()?.let {
                    NavigationUtils.navigateAndSetTemperature(
                        this, getCookingViewModel(),
                        it
                    )
                }
            }
            getBinding()?.constraintRightButton?.id -> {
                AudioManagerUtils.playOneShotSound(
                    view?.context,
                    if(getBinding()?.btnPrimary?.text?.equals(getString(R.string.text_button_next)) == true){
                        R.raw.button_press
                    }else{
                        R.raw.start_press
                    },
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
                recyclerView?.selectedValue?.toFloat()?.let {
                    NavigationUtils.navigateAndSetTemperature(
                        this, getCookingViewModel(),
                        it
                    )
                }
            }
            getBinding()?.btnGhost?.id -> {
                recyclerView?.selectedValue?.toFloat()?.let {
                    val recipeErrorResponse =
                        getCookingViewModel()?.recipeExecutionViewModel?.setTargetTemperature(it)
                    HMILogHelper.Logd(
                        tag,
                        "delay clicked with temperature $it recipeErrorResponse ${recipeErrorResponse?.description}"
                    )
                    if (recipeErrorResponse?.isError == false)
                        AudioManagerUtils.playOneShotSound(
                            view?.context,
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                        NavigationUtils.navigateToDelayScreen(this)
                }
            }
            getBinding()?.constraintLeftButton?.id -> {
                recyclerView?.selectedValue?.toFloat()?.let {
                    val recipeErrorResponse =
                        getCookingViewModel()?.recipeExecutionViewModel?.setTargetTemperature(it)
                    HMILogHelper.Logd(
                        tag,
                        "delay clicked with temperature $it recipeErrorResponse ${recipeErrorResponse?.description}"
                    )
                    if (recipeErrorResponse?.isError == false)
                        AudioManagerUtils.playOneShotSound(
                            view?.context,
                            R.raw.button_press,
                            AudioManager.STREAM_SYSTEM,
                            true,
                            0,
                            1
                        )
                    NavigationUtils.navigateToDelayScreen(this)
                }
            }
        }
    }

    /**
     * Method to manage knob rotation
     * @param knobDirection: String?
     */
    protected fun manageKnobRotation(knobDirection: String) {
        if (isTumblerSelected) {
            // Handle tumbler selection and knob events
            selectedRotator = 0
            val tumbler = getBinding()?.tumblerNumericBased ?: return
            rotateTumblerOnKnobEvents(this, tumbler, knobDirection)
        } else {
            when {
                isPrimaryCTAButtonVisible && isGhostButtonVisible -> {
                    adjustKnobRotation(knobDirection)
                    updateButtonBackgrounds(knobRotationCount)
                }

                isPrimaryCTAButtonVisible -> updateButtonBackgroundForSingleButton(
                    R.drawable.selector_textview_walnut,
                    isPrimaryButton = true
                )

                isGhostButtonVisible -> updateButtonBackgroundForSingleButton(
                    R.drawable.selector_textview_walnut,
                    isPrimaryButton = false
                )
            }
        }
    }

    /**
     * Adjust the knob rotation count based on the knob direction.
     */
    private fun adjustKnobRotation(knobDirection: String) {
        when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION -> {
                if (knobRotationCount < AppConstants.DIALOG_KNOB_SIZE) knobRotationCount++
            }
            KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> {
                if (knobRotationCount > AppConstants.MIN_KNOB_POSITION) knobRotationCount--
            }
        }
    }

    /**
     * Update both buttons' backgrounds based on knobRotationCount.
     */
    private fun updateButtonBackgrounds(knobRotationCount: Int) {
        val (primaryButtonRes, ghostButtonRes) = when (knobRotationCount) {
            AppConstants.KNOB_COUNTER_ONE -> Pair(R.drawable.selector_textview_walnut, R.drawable.text_view_ripple_effect)
            AppConstants.KNOB_COUNTER_TWO -> Pair(R.drawable.text_view_ripple_effect, R.drawable.selector_textview_walnut)
            else -> Pair(R.drawable.selector_textview_walnut, R.drawable.selector_textview_walnut)
        }
        setButtonBackgrounds(ghostButtonRes, primaryButtonRes)
    }

    /**
     * Helper function to set the background for a single button (either primary or ghost).
     */
    private fun updateButtonBackgroundForSingleButton(drawableRes: Int, isPrimaryButton: Boolean) {
        val targetButton = if (isPrimaryButton) {
            tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary
        } else {
            tumblerViewHolderHelper?.fragmentTumblerBinding?.btnGhost
        }
        targetButton?.background = ResourcesCompat.getDrawable(resources, drawableRes, null)
    }

    /**
     * Helper function to set the backgrounds for both buttons.
     */
    private fun setButtonBackgrounds(ghostButtonRes: Int, primaryButtonRes: Int) {
        val resources = resources
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnGhost?.background =
            ResourcesCompat.getDrawable(resources, ghostButtonRes, null)
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.background =
            ResourcesCompat.getDrawable(resources, primaryButtonRes, null)
    }

    /**
     * Handle knob clicks and update the state of selectedRotator and knobRotationCount.
     */
    fun onHMIKnobRightOrLeftClick() {
        HMILogHelper.Logd("Knob", "onHMIKnobRightOrLeftClick() called  : $selectedRotator")
        when (selectedRotator) {
            0 -> {
                if (tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.isEnabled == true) {
                    isTumblerSelected = false
                    selectedRotator = 1
                    knobRotationCount = 1
                    updateButtonBackgroundForSingleButton(
                        R.drawable.selector_textview_walnut,
                        isPrimaryButton = true
                    )
                }
            }

            1 -> {
                KnobNavigationUtils.knobForwardTrace = true
                when (knobRotationCount) {
                    AppConstants.KNOB_COUNTER_ONE -> {
                        if (tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.isEnabled == true)
                            onClick(tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary)
                    }

                    AppConstants.KNOB_COUNTER_TWO -> {
                        if (tumblerViewHolderHelper?.fragmentTumblerBinding?.btnGhost?.isEnabled == true)
                            onClick(tumblerViewHolderHelper?.fragmentTumblerBinding?.btnGhost)
                    }
                }
            }
        }
    }

    /**
     * Reset the backgrounds for both primary and ghost buttons to the ripple effect.
     */
    private fun resetButtonBackgrounds() {
        updateButtonBackgroundForSingleButton(R.drawable.text_view_ripple_effect, isPrimaryButton = true)
        updateButtonBackgroundForSingleButton(R.drawable.text_view_ripple_effect, isPrimaryButton = false)
    }


    /**
     * update selected temperature for cycle
     */
    protected open fun updateSelectedTemperatureForCycleTemperature() {
        val bundle = arguments
        var bundleTemperature: Int = AppConstants.DEFAULT_SELECTED_TEMP
        if (bundle != null) {
            bundleTemperature =
                requireArguments().getInt(BundleKeys.BUNDLE_SELECTED_TARGET_TEMPERATURE, -1)
            tempFromBundle = bundleTemperature
            isComingFromAnotherScreenAsBundle = true
        }
        if (bundleTemperature == AppConstants.DEFAULT_SELECTED_TEMP) {
            /*Show current cycle temperature as the default selected temperature when changing
            temperature while cycle running*/
            val targetTemperature =
                getCookingViewModel()?.recipeExecutionViewModel?.targetTemperature?.value
            if (targetTemperature != null) {
                selectedTemp = targetTemperature
                HMILogHelper.Logi("updateSelectedTumbler: cycle temperature from view model$selectedTemp")
            }
        } else {
            /*Arguments to show the temperature entered in the Number pad while switching from
             number pad to tumbler . The value entered in the number pad input screen is an
             intermediate value of HMI which will not known to SDK until HMI sets it while updating
             the cycle.*/
            selectedTemp = bundleTemperature
            HMILogHelper.Logi("updateSelectedTumbler: selectedTemp from Arguments$selectedTemp")
        }
    }

    /**
     * update button state as per recipe execution state
     */
    open fun updateCtaRightButton() {
        tumblerViewHolderHelper?.fragmentTumblerBinding?.btnPrimary?.text =
            NavigationUtils.getRightButtonTextForRecipeOption(
                context,
                getCookingViewModel(),
                provideRecipeOption()
            )
    }

    private fun rightButtonEnableOnScroll() {
        if (tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.isRunning == true){
            if (tumblerViewHolderHelper?.fragmentTumblerBinding?.cookingViewModel?.recipeExecutionViewModel?.targetTemperature?.value?.toFloat() == tumblerViewHolderHelper?.fragmentTumblerBinding?.tumblerNumericBased?.selectedValue?.toFloat()) {
                tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = false
                tumblerViewHolderHelper?.providePrimaryButton()?.setTextColor(resources.getColor(R.color.text_button_disabled_grey, null))
            } else {
                tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = true
                tumblerViewHolderHelper?.providePrimaryButton()?.setTextColor(resources.getColor(R.color.solid_white, null))
            }
        }
    }

    /**
     * Override this method if child class is not meant for TARGET_TEMPERATURE
     *
     * @return recipe option
     */
    open fun provideRecipeOption(): RecipeOptions {
        return RecipeOptions.TARGET_TEMPERATURE
    }

    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongLeftKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILongRightKnobPress() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMILeftKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onHMIRightKnobClick() {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        //Do nothing here override in child class if necessary
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        resetButtonBackgrounds()
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    private fun updateDelayButtonInTumbler() {
        if (CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.optionalOptions.value?.contains(
                RecipeOptions.DELAY_TIME
            ) == true
        ) {
            tumblerViewHolderHelper?.fragmentTumblerBinding?.constraintLeftButton?.visibility = View.VISIBLE
            tumblerViewHolderHelper?.fragmentTumblerBinding?.constraintLeftButton?.setOnClickListener(this)
        } else {
            tumblerViewHolderHelper?.fragmentTumblerBinding?.constraintLeftButton?.visibility = View.GONE
            tumblerViewHolderHelper?.fragmentTumblerBinding?.btnGhost?.visibility = View.GONE
        }
    }

    private fun configutionHmiKey(){
        HMILogHelper.Logd("HMI_KEY","AbstractNumericTumblerFragment Cycle Running--${CookingAppUtils.isAnyCavityRunningRecipeOrDelayedState()}")
        if(CookingAppUtils.isAnyCavityRunningRecipeOrDelayedState()){
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        }
    }

    /**
     * This function will help to get the timeout call back for steam clean
     */
    fun handleTimeoutCallbackListener() {
        NavigationUtils.getVisibleFragment().let {
            it?.parentFragmentManager?.setFragmentResultListener(
                AppConstants.TIMEOUT_CALLBACK,
                it.viewLifecycleOwner
            ) { _, bundle ->
                val result = bundle.getBoolean(AppConstants.TIMEOUT_CALLBACK)
                if (result) {
                    HMILogHelper.Logd("Delay steam Clean", "received bundle for screen timeout")
                    CookingAppUtils.clearRecipeData()
                }
            }
        }
    }
}
