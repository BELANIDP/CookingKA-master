/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.basefragments

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.adapters.TumblerElement
import android.presenter.adapters.manualMode.VerticalTumblerTimeAdapter
import android.presenter.customviews.textButton.TextButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.enums.RecipeErrorResponse
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.recipeexecution.RecipeExecutionViewModel
import com.whirlpool.hmi.cooking.utils.RecipeCookingState
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.base.ComponentSelectionInterface
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import com.whirlpool.hmi.utils.timers.Timer
import core.jbase.AbstractCookTimeNumberPadFragment
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CavityStateUtils
import core.utils.CommonAnimationUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.populateTumblersWithCookTimeValues
import core.utils.CookingAppUtils.Companion.rotateTumblerOnKnobEvents
import core.utils.DoorEventUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.navigateSafely
import core.utils.NumPadHelperTextColor
import core.utils.PopUpBuilderUtils
import core.utils.TimeUtils
import core.utils.ToastUtils
import core.viewHolderHelpers.VerticalTumblerViewHolderHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.util.Calendar
import java.util.Objects

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.basefragments.
 * Brief      : String tumbler Abstract class.
 * Author     : SINGHJ25.
 * Created On : 18/02/2024
 * Details    :
 */
abstract class AbstractVerticalTumblerFragment : SuperAbstractTimeoutEnableFragment(),
    View.OnClickListener,
    HMIKnobInteractionListener, ComponentSelectionInterface {
    private var calendar: Calendar? = null
    protected var tumblerViewHolderHelper: VerticalTumblerViewHolderHelper? = null
    private var targetCookOptions: IntegerRange? = null
    private var cookingViewModel: CookingViewModel? = null
    private var recipeViewModel: RecipeExecutionViewModel? = null
    private var isMicrowave = false
    private var focusedTumbler: BaseTumbler? = null
    private var rotator = listOf(0, 1, 2, 3)//HOUR/MIN, SEC, START, WAIT FOR PREHEAT/REMOVE TIMER
    private var selectedRotator = -1
    private var isTumblerSelected = false
    private var leftTumbler: BaseTumbler? = null
    private var centerTumbler: BaseTumbler? = null
    private var defaultVerticalTumblerHrs = 0
    private var defaultVerticalTumblerMin = 0
    private var defaultVerticalTumblerSec = 0
    private lateinit var hourOrMinuteItemViewHolder: VerticalTumblerTimeAdapter
    private lateinit var minuteOrSecondItemViewHolder: VerticalTumblerTimeAdapter
    private var timeHours: MutableList<String>? = null
    private var timeMinutes: MutableList<String>? = null
    private lateinit var view1: View
    private lateinit var view2: View
    private lateinit var view3: View
    private lateinit var view4: View
    private lateinit var view5: View
    private var views: List<View> = listOf()
    private var knobClickCount = 0
    private val rotatorClickCount = mutableMapOf<Int, Int>()    //handler for lower oven cavity

    protected abstract fun setHeaderLevel()
    protected abstract fun setCTALeft()
    protected abstract fun setCTARight()

    /**
     * To show Remove timer in Assisted Fresh Pizza in Assisted Preview -> cookTime update screen
     */
    protected var toShowRemoveTimerInAssisted: Boolean = false

    companion object {
        private const val TAG = "AbstractVerticalTumblerFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        calendar = Calendar.getInstance()
        tumblerViewHolderHelper = VerticalTumblerViewHolderHelper()
        tumblerViewHolderHelper?.onCreateView(inflater, container, savedInstanceState)
        return tumblerViewHolderHelper?.fragmentVerticalTumblerBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tumblerViewHolderHelper?.fragmentVerticalTumblerBinding?.lifecycleOwner = this
        tumblerViewHolderHelper?.fragmentVerticalTumblerBinding?.recipeExecutionViewModel =
            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
        tumblerViewHolderHelper?.fragmentVerticalTumblerBinding?.cookingViewModel =
            CookingViewModelFactory.getInScopeViewModel()
        tumblerViewHolderHelper?.providePrimaryButton()?.setOnClickListener(this)
        tumblerViewHolderHelper?.providePrimaryConstraint()?.setOnClickListener(this)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
        recipeViewModel = cookingViewModel?.recipeExecutionViewModel
        isMicrowave = cookingViewModel?.isOfTypeMicrowaveOven == true
        initViews()
        configutionHmiKey()
        setHeaderLevel()
        manageLeftPowerButton()
        setCTALeft()
        setCTARight()
        updateCTARightText()
        updateInabilityOfButtons()
        view5 = tumblerViewHolderHelper?.providePrimaryButton() as TextButton
        view4 = tumblerViewHolderHelper?.provideGhostButton() as TextButton
        view3 = tumblerViewHolderHelper?.provideLeftPowerButton() as TextButton
        view2 = tumblerViewHolderHelper?.provideVerticalTumblerLeft() as BaseTumbler
        view1 = tumblerViewHolderHelper?.provideVerticalTumblerCenter() as BaseTumbler
        updateRotator()
        if (KnobNavigationUtils.knobForwardTrace) {
            KnobNavigationUtils.knobForwardTrace = false
            selectedRotator =
                if (isMicrowave && cookingViewModel?.recipeExecutionViewModel?.isMagnetronUsed == true) rotator[0] else rotator[1]
        }
    }

    /**
     * Update enable/disable of left and right CTA button based on selected visible cook time
     *
     */
    protected open fun updateInabilityOfButtons() {
        if (getSelectedCookTime() == 0 || !isValidCookTime()) {
            tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = false
            tumblerViewHolderHelper?.providePrimaryButton()?.isClickable = false

            tumblerViewHolderHelper?.providePrimaryConstraint()?.isEnabled = false
            tumblerViewHolderHelper?.providePrimaryConstraint()?.isClickable = false

            tumblerViewHolderHelper?.provideGhostButton()?.isEnabled = false
            tumblerViewHolderHelper?.provideGhostButton()?.isClickable = false

            tumblerViewHolderHelper?.provideConstraintGhost()?.isEnabled = false
            tumblerViewHolderHelper?.provideConstraintGhost()?.isClickable = false

            tumblerViewHolderHelper?.provideLeftPowerButton()?.isEnabled = false
            tumblerViewHolderHelper?.provideLeftPowerButton()?.isClickable = false

            tumblerViewHolderHelper?.provideLeftPowerConstraint()?.isEnabled = false
            tumblerViewHolderHelper?.provideLeftPowerConstraint()?.isClickable = false
        } else {
            tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = true
            tumblerViewHolderHelper?.providePrimaryButton()?.isClickable = true

            tumblerViewHolderHelper?.providePrimaryConstraint()?.isEnabled = true
            tumblerViewHolderHelper?.providePrimaryConstraint()?.isClickable = true

            tumblerViewHolderHelper?.provideGhostButton()?.isEnabled = true
            tumblerViewHolderHelper?.provideGhostButton()?.isClickable = true

            tumblerViewHolderHelper?.provideConstraintGhost()?.isEnabled = true
            tumblerViewHolderHelper?.provideConstraintGhost()?.isClickable = true

            tumblerViewHolderHelper?.provideLeftPowerButton()?.isEnabled = true
            tumblerViewHolderHelper?.provideLeftPowerButton()?.isClickable = true

            tumblerViewHolderHelper?.provideLeftPowerConstraint()?.isEnabled = true
            tumblerViewHolderHelper?.provideLeftPowerConstraint()?.isClickable = true
        }
    }

    /**
     * Method to handle the left buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected fun manageLeftButton() {
        tumblerViewHolderHelper?.provideGhostButton()?.setOnClickListener(this)
        tumblerViewHolderHelper?.provideConstraintGhost()?.setOnClickListener(this)
        if (CookingAppUtils.isRequiredTargetAvailable(
                cookingViewModel, RecipeOptions.TARGET_TEMPERATURE
            )
        ) {
            recipeViewModel?.recipeCookingState?.observe(viewLifecycleOwner) {
                if (!toShowRemoveTimerInAssisted) {
                    NavigationUtils.manageLeftButtonForRecipeOption(
                        cookingViewModel,
                        RecipeOptions.COOK_TIME,
                        tumblerViewHolderHelper?.provideGhostButton()
                    )
                }
                if (tumblerViewHolderHelper?.provideGhostButton()?.visibility == View.VISIBLE) {
                    tumblerViewHolderHelper?.provideConstraintGhost()?.visibility = View.VISIBLE
                } else {
                    tumblerViewHolderHelper?.provideConstraintGhost()?.visibility = View.GONE
                }
                if (it == RecipeCookingState.PREHEATING)
                    updateInabilityOfButtons()
            }
        } else {
            tumblerViewHolderHelper?.provideGhostButton()?.visibility = View.GONE
            tumblerViewHolderHelper?.provideConstraintGhost()?.visibility = View.GONE
        }
    }

    /**
     * Method to handle the left power buttons . Below logic is a common one, if needed can be
     * override the respective child classes base on use case
     */
    protected open fun manageLeftPowerButton() {
        if (isMicrowave && CookingAppUtils.isRequiredTargetAvailable(
                cookingViewModel, RecipeOptions.MWO_POWER_LEVEL
            )
        ) {
            tumblerViewHolderHelper?.provideLeftPowerButton()?.setOnClickListener(this)
            tumblerViewHolderHelper?.provideLeftPowerConstraint()?.setOnClickListener(this)
            tumblerViewHolderHelper?.provideLeftPowerButton()?.visibility = View.VISIBLE
            val selectedPowerLevel =
                AbstractCookTimeNumberPadFragment.updateDefaultPowerLevel(cookingViewModel)
            tumblerViewHolderHelper?.provideLeftPowerButton()?.setTextButtonText(
                    if (CookingAppUtils.isRecipeReheatInProgramming(cookingViewModel)) getString(
                        R.string.text_button_moreOption
                    ) else getString(
                        R.string.text_MWO_cook_power_level, selectedPowerLevel
                    )
                )
        } else {
            tumblerViewHolderHelper?.provideLeftPowerButton()?.visibility = View.GONE
            tumblerViewHolderHelper?.provideLeftPowerConstraint()?.visibility = View.GONE
        }
    }

    protected open fun updateCTARightText() {
        tumblerViewHolderHelper?.providePrimaryButton()?.setTextButtonText(
            NavigationUtils.getRightButtonTextForRecipeOption(
                context,
                cookingViewModel,
                provideRecipeOption()
            )
        )
    }

    /**
     * Override this method if child class is not meant for COOK_TIME
     *
     * @return recipe option
     */
    open fun provideRecipeOption(): RecipeOptions {
        return RecipeOptions.COOK_TIME
    }

    override fun onClick(v: View) {
        when (v.id) {
            tumblerViewHolderHelper?.provideGhostButton()?.id -> {
                if (recipeViewModel?.cookTimerState?.value != Timer.State.IDLE && recipeViewModel?.cookTimerState?.value != Timer.State.COMPLETED) {
                    //In changing cook time scenario, on clicking "remove timer" cancels the cook timer
                    if (Objects.equals(
                            recipeViewModel?.cancelCookTimer(),
                            RecipeErrorResponse.NO_ERROR
                        )
                    ) {
                        CookingAppUtils.navigateToStatusOrClockScreen(this)
                    }
                } else if (toShowRemoveTimerInAssisted) {
                    onClickListener?.onLeftButtonClick()
                }
                //MAF_2656: Given cook time is getting started before preheat completes even though user selects wait for preheat.
                // During Preheating, if user click on wait for preheat on cook time vertical tumbler then set cook time and navigate
                // to status screen. This is only applicable to non-magnetron based recipe. In the current scenario, timer is also getting started which should not be the case
                else if ((!recipeViewModel?.isMagnetronUsed!!) && recipeViewModel?.cookTimerState?.value == Timer.State.IDLE
                        && recipeViewModel?.recipeCookingState?.value == RecipeCookingState.PREHEATING &&
                        recipeViewModel?.isProbeBasedRecipe == false) {
                    (getSelectedCookTime()?.toLong()?.let { recipeViewModel?.setCookTime(it)?.isError })
                    //Navigate to clock screen
                    CookingAppUtils.navigateToStatusOrClockScreen(this)
                    return;
                }
                else {
                    validateAndSetCookTime()
                }
            }

            tumblerViewHolderHelper?.provideConstraintGhost()?.id -> {
                if (recipeViewModel?.cookTimerState?.value != Timer.State.IDLE && recipeViewModel?.cookTimerState?.value != Timer.State.COMPLETED) {
                    //In changing cook time scenario, on clicking "remove timer" cancels the cook timer
                    if (Objects.equals(
                            recipeViewModel?.cancelCookTimer(),
                            RecipeErrorResponse.NO_ERROR
                        )
                    ) {
                        CookingAppUtils.navigateToStatusOrClockScreen(this)
                    }
                }else if (toShowRemoveTimerInAssisted) {
                    onClickListener?.onLeftButtonClick()
                }
                //MAF_2656: Given cook time is getting started before preheat completes even though user selects wait for preheat.
                // During Preheating, if user click on wait for preheat on cook time vertical tumbler then set cook time and navigate
                // to status screen. This is only applicable to non-magnetron based recipe. In the current scenario, timer is also getting started which should not be the case
                else if ((!recipeViewModel?.isMagnetronUsed!!) && recipeViewModel?.cookTimerState?.value == Timer.State.IDLE
                    && recipeViewModel?.recipeCookingState?.value == RecipeCookingState.PREHEATING &&
                    recipeViewModel?.isProbeBasedRecipe == false) {
                    (getSelectedCookTime()?.toLong()?.let { recipeViewModel?.setCookTime(it)?.isError })
                    //Navigate to clock screen
                    CookingAppUtils.navigateToStatusOrClockScreen(this)
                    return;
                }
                else {
                    validateAndSetCookTime()
                }
            }

            tumblerViewHolderHelper?.providePrimaryButton()?.id -> {
                validateAndSetCookTime()
            }

            tumblerViewHolderHelper?.providePrimaryConstraint()?.id -> {
                validateAndSetCookTime()
            }

            tumblerViewHolderHelper?.provideLeftPowerButton()?.id -> {
                if (isMicrowave && getSelectedCookTime()?.toLong()
                        ?.let { setCookTime(it) } == true
                ) {
                    if(CookingAppUtils.isRecipeReheatInProgramming(cookingViewModel)) {
                        PopUpBuilderUtils.showReheatMoreOptionPopup(this, cookingViewModel)
                        return
                    }
                    navigateSafely(
                        this,
                        R.id.action_to_manualMode_mwoPowerTumblerFragment,
                        null,
                        null
                    )
                }
            }

            tumblerViewHolderHelper?.provideLeftPowerConstraint()?.id -> {
                if (isMicrowave && getSelectedCookTime()?.toLong()
                        ?.let { setCookTime(it) } == true
                ) navigateSafely(
                    this,
                    R.id.action_to_manualMode_mwoPowerTumblerFragment,
                    null,
                    null
                )
            }
        }
    }

    protected abstract fun initTumbler()

    override fun onDestroyView() {
        clearMemory()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        super.onDestroyView()
    }

    private fun provideDefaultCookTime(): Int {
        if (recipeViewModel?.cookTime?.value == 0L) {
            val cookTimeOptionRange = provideIntegerRangeAsPerRecipe()
            if (cookTimeOptionRange != null) {
                return cookTimeOptionRange.defaultValue
            }
        }
        return recipeViewModel?.cookTime?.value?.toInt() ?: 0
    }

    private fun clearMemory() {
        tumblerViewHolderHelper?.onDestroyView()
        isTumblerSelected = false
        rotatorClickCount.clear()
        selectedRotator = -1
        knobClickCount = 0
    }

    /**
     * Initialize the views here.
     */
    private fun initViews() {
        initTumbler()
    }

    protected fun initVerticalTumbler() {
        val cookTime = arguments?.getString(BundleKeys.BUNDLE_PROVISIONING_TIME)
        val defaultTimeString : String
        if (!cookTime.isNullOrEmpty()) {
            HMILogHelper.Logd("cookTime### Received--->$cookTime")
            if (isMicrowave && cookingViewModel?.recipeExecutionViewModel?.isMagnetronUsed == true) {
                defaultTimeString =
                    cookTime.toLong().let { TimeUtils.convertTimeToMinutesAndSeconds(it) }

                defaultVerticalTumblerHrs = 0
                defaultVerticalTumblerMin = defaultTimeString.substring(2, 4).toInt()
                defaultVerticalTumblerSec = defaultTimeString.substring(4, 6).toInt()
            } else {
                defaultTimeString =
                    cookTime.toLong().let { TimeUtils.convertTimeToHoursAndMinutes(it) }
                defaultVerticalTumblerHrs = defaultTimeString.substring(0, 2).toInt()
                defaultVerticalTumblerMin = defaultTimeString.substring(2, 4).toInt()
                defaultVerticalTumblerSec = 0
            }
            initTumbler(defaultVerticalTumblerHrs, defaultVerticalTumblerMin, defaultVerticalTumblerSec)
            return
        } else if (recipeViewModel?.cookTimerState?.value == Timer.State.IDLE) {
            defaultTimeString = provideDefaultCookTime().toLong().let {
                if (isMicrowave) {
                    TimeUtils.convertTimeToMinutesAndSeconds(it)
                } else {
                    TimeUtils.convertTimeToHoursAndMinutes(it)
                }
            }
        } else {
            defaultTimeString = AppConstants.DEFAULT_COOK_TIME
        }
        extractTimeFromString(defaultTimeString)
    }

    private fun extractTimeFromString(time: String) {
        if (isMicrowave) {
            defaultVerticalTumblerHrs = 0
            defaultVerticalTumblerMin = time.substring(2, 4).toInt()
            defaultVerticalTumblerSec = time.substring(4, 6).toInt()
        } else {
            defaultVerticalTumblerHrs = time.substring(0, 2).toInt()
            defaultVerticalTumblerMin = time.substring(2, 4).toInt()
            defaultVerticalTumblerSec = 0
        }
        initTumbler(defaultVerticalTumblerHrs, defaultVerticalTumblerMin, defaultVerticalTumblerSec)
    }

    /**
     * Method to initiate tumbler elements with JSON objects.
     *
     * @throws JSONException
     */
    @Throws(JSONException::class)
    open fun initTumbler(defaultHours: Int, defaultMin: Int, defaultSec: Int) {
        leftTumbler = tumblerViewHolderHelper?.provideVerticalTumblerLeft()
        centerTumbler = tumblerViewHolderHelper?.provideVerticalTumblerCenter()

        if (isMicrowave && cookingViewModel?.recipeExecutionViewModel?.isMagnetronUsed == true) {
            initMicrowaveTumbler(defaultMin, defaultSec)
        } else {
            initStandardTumbler(defaultHours, defaultMin)
        }
    }

    private fun initMicrowaveTumbler(defaultMin: Int, defaultSec: Int) {
        // Set up the time lists for hours (used as minutes in microwave mode) and seconds
        timeHours = getMwoMinuteTumblerItems(defaultMin)
        timeMinutes = getSecondsTumblerItems(defaultSec)

        // Initialize left tumbler (for minutes)
        hourOrMinuteItemViewHolder = VerticalTumblerTimeAdapter(timeHours as ArrayList<String>, KnobNavigationUtils.knobForwardTrace, KnobNavigationUtils.knobForwardTrace)
        initCookTimeTumbler(leftTumbler, timeHours as ArrayList<String>, defaultMin, hourOrMinuteItemViewHolder)

        // Initialize center tumbler (for seconds)
        minuteOrSecondItemViewHolder = VerticalTumblerTimeAdapter(timeMinutes as ArrayList<String>, false, KnobNavigationUtils.knobForwardTrace)
        initCookTimeTumbler(centerTumbler, timeMinutes as ArrayList<String>, defaultSec, minuteOrSecondItemViewHolder)

        tumblerViewHolderHelper?.provideLeftTumblerText()?.text = getString(R.string.text_label_M)
        tumblerViewHolderHelper?.provideCenterTumblerText()?.text = getString(R.string.text_label_S)
    }

    private fun initStandardTumbler(defaultHours: Int, defaultMin: Int) {
        // Set up the time lists for hours and minutes
        timeHours = getHourTumblerItems(defaultHours)
        timeMinutes = getMinuteTumblerItems(defaultMin)

        // Initialize left tumbler (for hours)
        hourOrMinuteItemViewHolder = VerticalTumblerTimeAdapter(timeHours as ArrayList<String>, false, KnobNavigationUtils.knobForwardTrace)
        initCookTimeTumbler(leftTumbler, timeHours as ArrayList<String>, defaultHours, hourOrMinuteItemViewHolder)

        // Initialize center tumbler (for minutes)
        minuteOrSecondItemViewHolder = VerticalTumblerTimeAdapter(timeMinutes as ArrayList<String>, KnobNavigationUtils.knobForwardTrace, KnobNavigationUtils.knobForwardTrace)
        initCookTimeTumbler(centerTumbler, timeMinutes as ArrayList<String>, defaultMin, minuteOrSecondItemViewHolder)

        tumblerViewHolderHelper?.provideLeftTumblerText()?.text = getString(R.string.text_label_H)
        tumblerViewHolderHelper?.provideCenterTumblerText()?.text = getString(R.string.text_label_M)
    }


    private fun getHourTumblerItems(defaultHours: Int): ArrayList<String> {
        val hours = IntegerRange(CookingAppUtils.buildRangeJsonObject(1, defaultHours, 0, 24))
        val hoursTumbler: List<TumblerElement> = populateTumblersWithCookTimeValues(hours)
        return CookingAppUtils.convertTumblersToStringList(hoursTumbler)
    }

    private fun getMinuteTumblerItems(defaultMin: Int): ArrayList<String> {
        val minutes = IntegerRange(CookingAppUtils.buildRangeJsonObject(1, defaultMin, 0, 59))
        val minutesTumbler: List<TumblerElement> = populateTumblersWithCookTimeValues(minutes)
        return CookingAppUtils.convertTumblersToStringList(minutesTumbler)
    }

    private fun getMwoMinuteTumblerItems(defaultMin: Int): ArrayList<String> {
        val cookTimeOptionRange =
            recipeViewModel?.cookTimeOption?.value as IntegerRange
        val minutes =
            IntegerRange(
                CookingAppUtils.buildRangeJsonObject(
                    1,
                    defaultMin,
                    0,
                    cookTimeOptionRange.max / resources.getInteger(R.integer.duration_min)
                )
            )
        val minutesTumbler: List<TumblerElement> = populateTumblersWithCookTimeValues(minutes)
        return CookingAppUtils.convertTumblersToStringList(minutesTumbler)
    }

    private fun getSecondsTumblerItems(defaultSec: Int): ArrayList<String> {
        val seconds = IntegerRange(CookingAppUtils.buildRangeJsonObject(1, defaultSec, 0, 59))
        val secondsTumbler: List<TumblerElement> = populateTumblersWithCookTimeValues(seconds)
        return CookingAppUtils.convertTumblersToStringList(secondsTumbler)
    }

    /**
     * Method to initialize the Numeric Tumbler.
     *
     * @param list,           List of Tumbler Elements
     * @param cookTimeValues, temperature options from SDK
     */
    protected open fun initCookTimeTumbler(
        tumbler: BaseTumbler?, list: ArrayList<String>, cookTimeValues: Int,
        cookTimeItemViewHolder: VerticalTumblerTimeAdapter
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                val defaultCookTime =
                    if (cookTimeValues >= resources.getInteger(R.integer.integer_range_10)) cookTimeValues.toString() else "0$cookTimeValues"
                HMILogHelper.Logd("Default Time: $defaultCookTime")
                tumbler?.apply {
                    isInfiniteScroll = true
                    itemAnimator = null
                    setInitialOffsetIndexEnabled(false)
                    itemViewHolder = cookTimeItemViewHolder
                    updateItems(list, true)
                    setPlayAudioOnLoading(false)
                }
                val listInterface: ViewModelListInterface = object : ViewModelListInterface {
                    override fun getListItems(): ArrayList<String> {
                        return list
                    }

                    override fun getDefaultString(): String {
                        return defaultCookTime
                    }

                    override fun getValue(index: Int): Any {
                        return list[index]
                    }

                    override fun isValid(value: Any): Boolean {
                        return list.contains(value)
                    }
                }
                HMILogHelper.Logd("Default Value of Tumbler: $defaultCookTime")
                HMILogHelper.Logd("Index of Tumbler " + tumbler?.indexOf(defaultCookTime))
                requireView().post {
                    tumbler?.setComponentSelectionInterface(this@AbstractVerticalTumblerFragment)
                    tumbler?.setListObject(listInterface, true)
                }
            }
        }
    }

    override fun selectionUpdated(index: Int) {
        updateInabilityOfButtons()
        showTemperatureHelper(isVisible = getSelectedCookTime() == 0 || !isValidCookTime())
    }

    /**
     * Method to update the tumbler selected item style if touched
     */
    override fun onTumblerTouchInteraction(tumblerView: BaseTumbler?, action: Int) {
        super.onTumblerTouchInteraction(tumblerView, action)
        resetKnobParameters()
    }

    fun getSelectedCookTime(): Int? {
        val cookTimeRange = provideIntegerRangeAsPerRecipe()
        val durationHour = resources.getInteger(R.integer.duration_hour)
        val durationMin = resources.getInteger(R.integer.duration_min)
        val durationSec = 1
        targetCookOptions = if (cookTimeRange is IntegerRange) {
            cookTimeRange
        } else {
            null
        }
        return if (isMicrowave && cookingViewModel?.recipeExecutionViewModel?.isMagnetronUsed == true) {
            val selectedMinutes =
                tumblerViewHolderHelper?.provideVerticalTumblerLeft()?.selectedValue
            val selectedSeconds =
                tumblerViewHolderHelper?.provideVerticalTumblerCenter()?.selectedValue
            (selectedMinutes?.toInt()?.times(durationMin)
                ?.plus(selectedSeconds?.toInt()?.times(durationSec) ?: 0))
        } else {
            val selectedHour =
                tumblerViewHolderHelper?.provideVerticalTumblerLeft()?.selectedValue
            val selectedMinute =
                tumblerViewHolderHelper?.provideVerticalTumblerCenter()?.selectedValue
            (selectedHour?.toInt()?.times(durationHour)
                ?.plus(selectedMinute?.toInt()?.times(durationMin) ?: 0))
        }
    }

    /**
     * Validate enter cook time
     */
    private fun validateAndSetCookTime() {
        val selectedTime = getSelectedCookTime()
        val getMaximumAllowedCookTime: Long = targetCookOptions?.max?.toLong() ?: 0
        if (selectedTime != null) {
            HMILogHelper.Logd(" selectedTime-->$selectedTime , getMaximumAllowedCookTime-->$getMaximumAllowedCookTime")
            if (selectedTime <= getMaximumAllowedCookTime) {
                if (isValidCookTime()) {
                    val requiredRecipeOptions =
                        recipeViewModel?.requiredOptions?.value
                    val powerAndTimeOptions =
                        requiredRecipeOptions?.contains(RecipeOptions.COOK_TIME) == true && requiredRecipeOptions.contains(
                            RecipeOptions.MWO_POWER_LEVEL
                        )
                    if (isMicrowave && powerAndTimeOptions && tumblerViewHolderHelper?.provideLeftPowerButton()?.visibility == View.VISIBLE && recipeViewModel?.cookTimerState?.value == Timer.State.IDLE) {
                        if (recipeViewModel?.setCookTime(selectedTime.toLong())?.isError == true) return
                        DoorEventUtils.startMicrowaveRecipeOrShowPopup(this, cookingViewModel)
                        return
                    }
                    AbstractCookTimeNumberPadFragment.handleCommonCookTimeRightTextButtonClick(
                        cookingViewModel,
                        this,
                        selectedTime
                    )
                }
            } else {
                ToastUtils.showToast(requireContext(), "Invalid cook time")
            }
        }
    }

    /**
     * Method to validate whether the cook timer is in range
     *
     * @return true if it is a valid cook time
     */
     fun isValidCookTime(): Boolean {
        val selectedTime = getSelectedCookTime()
        val getMaximumAllowedCookTime: Long = targetCookOptions?.max?.toLong() ?: 0
        if (selectedTime != null) {
            HMILogHelper.Logd(" selectedTime-->$selectedTime , getMaximumAllowedCookTime-->$getMaximumAllowedCookTime")
            if (selectedTime <= getMaximumAllowedCookTime) {
                val cookTimeOptionRange = provideIntegerRangeAsPerRecipe()
                return if (cookTimeOptionRange != null) {
                    selectedTime >= cookTimeOptionRange.min && selectedTime <= cookTimeOptionRange.max
                } else {
                    false
                }
            }
        }
        return false
    }

    /**
     * Method to set the just set the cook time - When the timer is in PAUSED state or RUNNING state
     * the cycle need not be started explicitly. Used for Save for later also.
     *
     * @param cookTimeSec : Cook time value to be set.
     * @return : Returns whether the sdk call is success
     */
    private fun setCookTime(cookTimeSec: Long): Boolean {
        val recipeErrorResponse =
            recipeViewModel?.setCookTime(cookTimeSec)
        HMILogHelper.Logi("Set Cook time: " + cookTimeSec + "response : " + recipeErrorResponse)
        return recipeErrorResponse == RecipeErrorResponse.NO_ERROR
    }

    /**
     * Show Temperature helper text with error color.
     */
    protected open fun showTemperatureHelper(isVisible: Boolean) {

        if (isVisible) {
            tumblerViewHolderHelper?.provideTextViewHelperText()?.visibility = View.VISIBLE

            val cookTimeOptionRange = provideIntegerRangeAsPerRecipe()
            if (cookTimeOptionRange is IntegerRange) {

                if (isMicrowave && cookingViewModel?.recipeExecutionViewModel?.isMagnetronUsed == true) {
                    val minutes = cookTimeOptionRange.max.div(AppConstants.ONE_MIN_SECONDS)
                    val seconds = cookTimeOptionRange.max.rem(AppConstants.ONE_MIN_SECONDS)
                    tumblerViewHolderHelper?.provideTextViewHelperText()?.text =
                        String.format(
                            CookingAppUtils.getStringFromResourceId(
                                requireContext(),
                                provideHelperTextString()
                            ),
                            minutes.toString() + AppConstants.EMPTY_SPACE + getString(R.string.text_label_m) + AppConstants.EMPTY_SPACE + seconds.toString() + AppConstants.EMPTY_SPACE + getString(
                                R.string.text_label_s
                            )
                        )
                } else {
                    val hours = cookTimeOptionRange.max / AppConstants.ONE_HOUR_SECONDS
                    tumblerViewHolderHelper?.provideTextViewHelperText()?.text =
                        String.format(
                            CookingAppUtils.getStringFromResourceId(
                                requireContext(),
                                provideHelperTextString()
                            ),
                            hours.toString() + AppConstants.EMPTY_SPACE + getString(R.string.text_label_h)
                        )
                }


                CommonAnimationUtils.setErrorHelperAnimation(
                    context,
                    tumblerViewHolderHelper?.provideTextViewHelperText()
                )
                CookingAppUtils.setHelperTextColor(
                    tumblerViewHolderHelper?.provideTextViewHelperText(),
                    NumPadHelperTextColor.ERROR_TEXT_COLOR
                )
            }
        } else {
            tumblerViewHolderHelper?.provideTextViewHelperText()?.visibility = View.GONE
        }
    }

    /**
     * Method to get the helper text string resource
     */
    private fun provideHelperTextString(): Int {
        return R.string.text_error_helper_description
    }

    private fun provideIntegerRangeAsPerRecipe(): IntegerRange? {
        val isProbeBasedRecipeAndTemperatureReached =
            CookingAppUtils.isProbeBasedRecipeAndTemperatureReached(recipeViewModel)
        if (isProbeBasedRecipeAndTemperatureReached) {
            return CookingAppUtils.provideProbeIntegerRange()
        }
        return recipeViewModel?.cookTimeOption?.value as IntegerRange?
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
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
        HMILogHelper.Logd(TAG, "onHMILeftKnobClick() called $isTumblerSelected : $selectedRotator")
    }

    override fun onHMIRightKnobClick() {
        HMILogHelper.Logd(TAG, "onHMIRightKnobClick() called $isTumblerSelected : $selectedRotator")
        manageLeftOrRightKnobClick()
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
           manageKnobRotationOnVerticalTumbler(knobDirection)
        }
    }

    protected fun manageLeftOrRightKnobClick(){
        focusedTumbler = null
        knobClickCount++
        HMILogHelper.Logd(TAG, "selected rotator on Knob click $selectedRotator")
        when (selectedRotator) {
            0 -> {
                focusedTumbler = leftTumbler
                handleKnobClickAndFocus()
            }

            1 -> {
                focusedTumbler = centerTumbler
                handleKnobClickAndFocus()
            }

            2 -> {
                isTumblerSelected = false
                if (tumblerViewHolderHelper?.provideGhostButton()?.isEnabled == true ||
                    tumblerViewHolderHelper?.provideLeftPowerButton()?.isEnabled == true
                ) {
                    KnobNavigationUtils.knobForwardTrace = true
                    if (isMicrowave)
                        tumblerViewHolderHelper?.provideLeftPowerButton()?.callOnClick()
                    else
                        tumblerViewHolderHelper?.provideGhostButton()?.callOnClick()
                }
            }

            3 -> {
                isTumblerSelected = false
                if (tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled == true) {
                    KnobNavigationUtils.knobForwardTrace = true
                    tumblerViewHolderHelper?.providePrimaryButton()?.callOnClick()
                }
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
                HMILogHelper.Logd(TAG, "selected rotator $selectedRotator")
                rotateFocus(knobDirection,false)
            }

            KnobNavigationUtils.ClickState.SECOND -> {
                // Second click: mark isTumblerSelected as false and update selectedRotator
                isTumblerSelected = false
                if (!(isMicrowave && cookingViewModel?.recipeExecutionViewModel?.isMagnetronUsed == true) && selectedRotator == 1) {
                    // If currently at rotator 1, reset to rotator 0 for the case of non magnetron recipes
                    selectedRotator = 0
                } else {
                    updateSelectedRotatorIndex(knobDirection)
                }
                HMILogHelper.Logd(TAG, "selected rotator on second knob click  $selectedRotator")
                rotateFocus(knobDirection)
                // Reset the click count for the previous selectedRotator (before update)
                rotatorClickCount[currentSelectedRotator] = 0
            }
            else -> {}
        }
    }

    // On Knob rotate event
    protected fun manageKnobRotationOnVerticalTumbler(knobDirection:String){
        if (!isTumblerSelected) {
            updateSelectedRotatorIndex(knobDirection)
            rotateFocus(knobDirection)
        } else {
            when (focusedTumbler) {
                leftTumbler -> {
                    leftTumbler?.let {
                        rotateTumblerOnKnobEvents(
                            this,
                            it,
                            knobDirection
                        )
                    }
                }

                centerTumbler -> {
                    centerTumbler?.let {
                        rotateTumblerOnKnobEvents(
                            this,
                            it,
                            knobDirection
                        )
                    }
                }

                else -> {
                }
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
           resetKnobParameters()
        }
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    /**
     * Method to reset on knob timeout
     */
    protected fun resetKnobParameters() {
        isTumblerSelected = false
        rotatorClickCount.clear()
        selectedRotator = -1
        knobClickCount = 0
        toggleFocusHourOrMinuteTumbler(false,false)
        toggleFocusMinuteOrSecondTumbler(false, false)
        tumblerViewHolderHelper?.providePrimaryButton()
            ?.setBottomViewVisible(false)
        tumblerViewHolderHelper?.provideGhostButton()
            ?.setBottomViewVisible(false)
        tumblerViewHolderHelper?.provideLeftPowerButton()
            ?.setBottomViewVisible(false)
    }

    /**
     * Method to manage underline on HOUR,MIN,SEC tumblers and CTA's
     *
     * @param knobDirection
     * @param isKnobRotationActive to handle tumbler style while knob is active
     */
    private fun rotateFocus(knobDirection: String, isKnobRotationActive: Boolean = true) {
        HMILogHelper.Logd(TAG, "rotateFocus() called with: selectedRotator = $selectedRotator")

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                if (knobDirection in listOf(KnobDirection.CLOCK_WISE_DIRECTION, KnobDirection.COUNTER_CLOCK_WISE_DIRECTION)) {
                    executeRotatorAction(selectedRotator, isKnobRotationActive)
                }
            }
        }
    }

    private fun executeRotatorAction(selectedRotator: Int, isKnobRotationActive: Boolean) {
        // Map of actions for different selected rotators
        val actions = listOf(
            { toggleFocusHourOrMinuteTumbler(true, isKnobRotationActive); toggleFocusMinuteOrSecondTumbler(false); updateUIForSelectedRotator(0, false, false, false) },
            { toggleFocusHourOrMinuteTumbler(false); toggleFocusMinuteOrSecondTumbler(true, isKnobRotationActive); updateUIForSelectedRotator(1, false, false, false) },
            { toggleFocusHourOrMinuteTumbler(false); toggleFocusMinuteOrSecondTumbler(false); updateUIForSelectedRotator(2, false, true, true) },
            { toggleFocusHourOrMinuteTumbler(false); toggleFocusMinuteOrSecondTumbler(false); updateUIForSelectedRotator(3, true, false, false) }
        )

        // Invoke the action for the selected rotator index
        actions.getOrNull(selectedRotator)?.invoke()
    }

    /**
     * Helper method to update the UI for a specific selected rotator.
     */
    private fun updateUIForSelectedRotator(
        rotatorIndex: Int,
        isPrimaryButtonVisible: Boolean,
        isGhostButtonVisible: Boolean,
        isLeftPowerButtonVisible: Boolean
    ) {
        val primaryButton = tumblerViewHolderHelper?.providePrimaryButton()
        val ghostButton = tumblerViewHolderHelper?.provideGhostButton()
        val leftPowerButton = tumblerViewHolderHelper?.provideLeftPowerButton()

        primaryButton?.setBottomViewVisible(isPrimaryButtonVisible)
        ghostButton?.setBottomViewVisible(isGhostButtonVisible)
        leftPowerButton?.setBottomViewVisible(isLeftPowerButtonVisible)

        HMILogHelper.Logd(TAG, "Updated visibility for rotator $rotatorIndex")
    }

    /**
     * Method use for toggle focus on HOUR,MIN tumblers
     *
     * @param isBottomViewVisible
     * @param isKnobRotationActive to handle tumbler style while knob is active
     */
    private fun toggleFocusHourOrMinuteTumbler(
        isBottomViewVisible: Boolean,
        isKnobRotationActive: Boolean = true
    ) {
        hourOrMinuteItemViewHolder =
            timeHours?.let { VerticalTumblerTimeAdapter(it, isBottomViewVisible, isKnobRotationActive) }!!
        leftTumbler?.itemViewHolder = hourOrMinuteItemViewHolder
        leftTumbler?.updateItems(timeHours as java.util.ArrayList<String>?, true)
    }

    /**
     * Method use for toggle focus on MIN,SEC tumblers
     *
     * @param isBottomViewVisible
     * @param isKnobRotationActive to handle tumbler style while knob is active
     */
    private fun toggleFocusMinuteOrSecondTumbler(
        isBottomViewVisible: Boolean,
        isKnobRotationActive: Boolean = true
    ) {
        minuteOrSecondItemViewHolder =
            timeMinutes?.let { VerticalTumblerTimeAdapter(it, isBottomViewVisible, isKnobRotationActive) }!!
        centerTumbler?.itemViewHolder = minuteOrSecondItemViewHolder
        centerTumbler?.updateItems(timeMinutes as java.util.ArrayList<String>?, true)
    }

    private fun updateRotator() {
        views = listOf(view1, view2, view3, view4, view5).filter { it.visibility == View.VISIBLE }
        // Reset selectedRotator if no views are visible
        if (views.isEmpty()) {
            selectedRotator = -1
            return
        }
        selectedRotator = selectedRotator.coerceIn(0, views.size - 1)
    }

    // To update selected rotator
    private fun updateSelectedRotatorIndex(knobDirection: String) {
        val visibleIndices = getVisibleIndices()
        if (visibleIndices.isEmpty()) {
            HMILogHelper.Logd("No visible views to select")
            return
        }
        val currentIndex = visibleIndices.indexOf(selectedRotator)
        if (currentIndex == -1) {
            HMILogHelper.Logd("selectedRotator not found in visibleIndices")
            selectedRotator++
            return
        }
        val newSelectedRotator = when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION -> getNextVisibleRotator(currentIndex, visibleIndices)
            KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> getPreviousVisibleRotator(currentIndex, visibleIndices)
            else -> selectedRotator
        }
        if (newSelectedRotator != selectedRotator) {
            HMILogHelper.Logd("mSelected $knobDirection $newSelectedRotator")
            selectedRotator = newSelectedRotator
        }
    }

    // Returns the indices of all visible rotators
    private fun getVisibleIndices(): List<Int> {
        return views.mapIndexedNotNull { index, view ->
            when (view) {
                view1 -> if (view.visibility == View.VISIBLE && view.isEnabled) 0 else null
                view2 -> if (view.visibility == View.VISIBLE && view.isEnabled) 1 else null
                view3, view4 -> if (view.visibility == View.VISIBLE && view.isEnabled) 2 else null // Combined index for view3 and view4
                view5 -> if (view.visibility == View.VISIBLE && view.isEnabled) 3 else null
                else -> null
            }
        }
    }

    // Returns the next visible rotator, or the current one if it's the last
    private fun getNextVisibleRotator(currentIndex: Int, visibleIndices: List<Int>): Int {
        return if (currentIndex < visibleIndices.size - 1) {
            visibleIndices[currentIndex + 1]
        } else {
            visibleIndices[currentIndex]
        }
    }

    // Returns the previous visible rotator, or the current one if it's the first
    private fun getPreviousVisibleRotator(currentIndex: Int, visibleIndices: List<Int>): Int {
        return if (currentIndex > 0) {
            visibleIndices[currentIndex - 1]
        } else {
            visibleIndices[currentIndex]
        }
    }
    private fun configutionHmiKey(){
        HMILogHelper.Logd("HMI_KEY","AbstractVerticalTumblerFragment Cycle Running--${CookingAppUtils.isAnyCavityRunningRecipeOrDelayedState()}")
        if(CookingAppUtils.isAnyCavityRunningRecipeOrDelayedState()){
            HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
            HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        }
    }

    private var onClickListener: CTAButtonClickListenerInterface? = null

    /**
     * Set the button interface to receive callbacks in the fragment.
     */
    fun setButtonInteractionListener(onClickListener: CTAButtonClickListenerInterface?) {
        this.onClickListener = onClickListener
    }

    interface CTAButtonClickListenerInterface {
        fun onRightButtonClick()
        fun onLeftButtonClick()
        fun onMiddleButtonClick()
        fun onLeftPowerButtonClick()
    }
}