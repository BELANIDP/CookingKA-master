/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.kitchentimer

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.adapters.manualMode.VerticalTumblerTimeAdapter
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentKitchenTimerBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardInputManagerInterface
import com.whirlpool.hmi.uicomponents.widgets.keyboard.KeyboardView
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.rotateTumblerOnKnobEvents
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.KnobDirection
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import java.util.Locale

/**
 * File       : android.presenter.fragments.kitchentimer.KitchenTimerTumblerFragment
 * Brief      : Set kitchen timer fragment
 * Author     : PANDES18
 * Created On : 06/27/2024
 * Details    : Fragment responsible for kitchen timer setup
 */

class KitchenTimerTumblerFragment : CustomAbstractKitchenTimerWithTumblersFragment(),
    KeyboardInputManagerInterface,
    HMIKnobInteractionListener {

    companion object {
        private const val TAG = "SetKTFragment"
    }

    private var _binding: FragmentKitchenTimerBinding? = null
    private var isKitchenTimerModify: Boolean = false
    private val binding get() = _binding!!
    private var focusedTumbler: BaseTumbler? = null
    private var knobClickCount = 0
    private var rotator = listOf(0, 1, 2)//HOUR, MIN, SEC
    private var selectedRotator = -1
    private val rotatorClickCount = mutableMapOf<Int, Int>()
    private var isTumblerSelected = false
    private val hourNumbers = KitchenTimerUtils.getHourList()
    private val minNumbers = KitchenTimerUtils.getMinuteSecondList()
    private lateinit var hourTumbler: BaseTumbler
    private lateinit var minuteTumbler: BaseTumbler
    private lateinit var secondTumbler: BaseTumbler
    private lateinit var hourItemViewHolder: VerticalTumblerTimeAdapter
    private lateinit var minuteItemViewHolder: VerticalTumblerTimeAdapter
    private lateinit var secondItemViewHolder: VerticalTumblerTimeAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKitchenTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initTumblers()
        updateTumblerDefault()
        super.onViewCreated(view, savedInstanceState)
        //Header Bar
        binding.headerBar.apply {
            setTitleText(
                resources.getString(
                    R.string.text_header_kitchen_timer
                )
            )
            setRightIcon(R.drawable.numpad_icon)
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setCustomOnClickListener(object :
                HeaderBarWidgetInterface.CustomClickListenerInterface {

                override fun leftIconOnClick() {
                    super.leftIconOnClick()
                    NavigationViewModel.popBackStack(findNavController())
                }

                override fun rightIconOnClick() {
                    super.rightIconOnClick()
                    navigateToCookTimeNumPadScreen()
                }
            })
        }
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        if(KnobNavigationUtils.knobForwardTrace){
            KnobNavigationUtils.knobForwardTrace = false
            selectedRotator = rotator[1]
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    private fun initTumblers() {
        hourTumbler = binding.tumblerNumericBasedHours
        hourItemViewHolder = VerticalTumblerTimeAdapter(hourNumbers.toList(), isKnobRotationActive = KnobNavigationUtils.knobForwardTrace)
        hourTumbler.apply {
            isInfiniteScroll = true
            itemViewHolder = hourItemViewHolder
            updateItems(hourNumbers as java.util.ArrayList<String>?, true)
        }
        minuteTumbler = binding.tumblerNumericBasedMins
        minuteItemViewHolder = VerticalTumblerTimeAdapter(minNumbers.toList(), KnobNavigationUtils.knobForwardTrace, KnobNavigationUtils.knobForwardTrace)
        minuteTumbler.apply {
            isInfiniteScroll = true
            itemViewHolder = minuteItemViewHolder
            updateItems(minNumbers as java.util.ArrayList<String>?, true)
        }
        secondTumbler = binding.tumblerNumericBasedSeconds
        secondItemViewHolder = VerticalTumblerTimeAdapter(minNumbers.toList(), isKnobRotationActive = KnobNavigationUtils.knobForwardTrace)
        secondTumbler.apply {
            isInfiniteScroll = true
            itemViewHolder = secondItemViewHolder
            updateItems(minNumbers as java.util.ArrayList<String>?, true)
        }
    }

    private fun updateTumblerDefault() {
        isKitchenTimerModify =
            arguments?.getString(BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER) != null && arguments?.getString(
                BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER
            )?.isEmpty() == false
        val numpadValue = arguments?.getString(BundleKeys.BUNDLE_PROVISIONING_TIME)
        if (numpadValue.isNullOrEmpty()) provideDefault(0, 0, 0)
        else {
            HMILogHelper.Logd(tag, "kitchenTimer numpad carry over $numpadValue")
            val hr = StringBuilder().append(numpadValue[0]).append(numpadValue[1]).toString()
            val min = StringBuilder().append(numpadValue[2]).append(numpadValue[3]).toString()
            provideDefault(hr.toInt(), min.toInt(), 0)
        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.startNowText -> {
                handleKitchenTimerStart()
            }
        }
    }

    override fun selectionUpdated(index: Int) {
        // Do nothing
    }

    override fun onTumblerTouchInteraction(tumblerView: BaseTumbler?, action: Int) {
        super.onTumblerTouchInteraction(tumblerView, action)
        resetKnobParameters()
    }

    override fun provideKitchenTimerViewModel(): KitchenTimerViewModel {
        if (isKitchenTimerModify) return KitchenTimerUtils.getKitchenTimerViewModelFromName(
            arguments?.getString(BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER)
        )!!
        return KitchenTimerVMFactory.getNextAvailableKitchenTimer()!!
    }

    override fun handleInvalidEntry() {
    }

    override fun handleValidEntry() {
    }

    override fun handleKitchenTimerStart() {
        if (calculateKitchenTimer() == 0 || calculateKitchenTimer() > MAX_KITCHEN_TIMER_IN_SECONDS) {
            return
        }
        if (isKitchenTimerModify) {
            val modifyKitchenTimerName =
                arguments?.getString(BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER)
            HMILogHelper.Logd(tag, "kitchenTimer modify name $modifyKitchenTimerName")
            KitchenTimerUtils.modifyKitchenTimer(
                modifyKitchenTimerName,
                calculateKitchenTimer(),
                onSuccessKTAdded = {
                    NavigationUtils.navigateSafely(
                        this, R.id.action_setKTFragment_to_kitchenTimerFragment, null, null
                    )
                })
            return
        }
        CookingAppUtils.stopGattServer()
        KitchenTimerUtils.addKitchenTimer(this, calculateKitchenTimer(), onSuccessKTAdded = {
            NavigationUtils.navigateSafely(
                this, R.id.action_setKTFragment_to_kitchenTimerFragment, null, null
            )
        })
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.start_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
    }

    override fun provideHourTumbler(): BaseTumbler {
        return hourTumbler
    }

    override fun provideMinutesTumbler(): BaseTumbler {
        return minuteTumbler
    }

    override fun provideSecondsTumbler(): BaseTumbler {
        return secondTumbler
    }

    override fun provideKitchenTimerStartButton(): View {
        return binding.startNowText.also {
            it.setTextButtonText(
                if (isKitchenTimerModify) getString(R.string.text_button_update) else getString(
                    R.string.text_button_start
                )
            )
        }
    }

    override fun provideKitchenTimerUpdateNameButton(): View {
        return binding.startNowText
    }

    override fun handleUpdateNameButtonClick() {
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
                focusedTumbler = hourTumbler
                handleKnobClickAndFocus()
            }

            1 -> {
                focusedTumbler = minuteTumbler
                handleKnobClickAndFocus()
            }

            2 -> {
                focusedTumbler = secondTumbler
                handleKnobClickAndFocus()
            }

            3 -> {
                isTumblerSelected = false
                KnobNavigationUtils.knobForwardTrace = true
                handleKitchenTimerStart()
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

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (!isTumblerSelected) {
                updateSelectedRotatorIndex(knobDirection)
                rotateFocus(knobDirection)
            } else {
                when (focusedTumbler) {
                    hourTumbler -> {
                        rotateTumblerOnKnobEvents(
                            this,
                            hourTumbler,
                            knobDirection
                        )
                    }

                    minuteTumbler -> {
                        rotateTumblerOnKnobEvents(
                            this,
                            minuteTumbler,
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
                rotateFocus(knobDirection, false)
            }

            KnobNavigationUtils.ClickState.SECOND -> {
                // Second click: mark isTumblerSelected as false and update selectedRotator
                isTumblerSelected = false
                updateSelectedRotatorIndex(knobDirection)
                rotateFocus(knobDirection)
                // Reset the click count for the previous selectedRotator (before update)
                rotatorClickCount[currentSelectedRotator] = 0
            }

            else -> {}
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
        if (knobId == AppConstants.LEFT_KNOB_ID) resetKnobParameters()
    }

    private fun resetKnobParameters() {
        selectedRotator = -1
        isTumblerSelected = false
        rotatorClickCount.clear()
        knobClickCount = 0
        toggleFocusHourTumbler(false, false)
        toggleFocusMinuteTumbler(false, false)
        toggleFocusSecondTumbler(false, false)
        binding.startNowText.setBottomViewVisible(false)
    }

    private fun rotateFocus(knobDirection: String, isKnobRotationActive: Boolean = true) {
        Log.d("SetKTFragment", "rotateFocus() called with: selectedRotator = $selectedRotator")
        val actions = when (knobDirection) {
            KnobDirection.CLOCK_WISE_DIRECTION, KnobDirection.COUNTER_CLOCK_WISE_DIRECTION -> arrayOf(
                { /* Action for selectedRotator 0 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(true, isKnobRotationActive)
                    toggleFocusMinuteTumbler(false)
                    toggleFocusSecondTumbler(false)
                    binding.startNowText.setBottomViewVisible(false)
                },
                { /* Action for selectedRotator 1 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(true, isKnobRotationActive)
                    toggleFocusSecondTumbler(false)
                    binding.startNowText.setBottomViewVisible(false)

                },
                { /* Action for selectedRotator 2 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(false)
                    toggleFocusSecondTumbler(true, isKnobRotationActive)
                    binding.startNowText.setBottomViewVisible(false)

                },
                { /* Action for selectedRotator 3 and CLOCK_WISE_DIRECTION */
                    toggleFocusHourTumbler(false)
                    toggleFocusMinuteTumbler(false)
                    toggleFocusSecondTumbler(false)
                    binding.startNowText.setBottomViewVisible(true)
                })

            else -> return
        }

        actions.getOrNull(selectedRotator)?.invoke()
    }


    /**
     * Method use for calculate kitchen timer
     *
     * @return Int kitchen timer
     */
    private fun calculateKitchenTimer(): Int {
        val hours = hourTumbler.selectedValue.toInt()
        val minutes = minuteTumbler.selectedValue.toInt()
        val seconds = binding.tumblerNumericBasedSeconds.selectedValue.toInt()
        return (hours * 3600) + (minutes * 60) + seconds
    }

    /**
     * Method use for navigate to cook time numpad screen
     * send bundle for sending selected hour and min
     */
    private fun navigateToCookTimeNumPadScreen() {
        var selectedHour = hourTumbler.selectedValue
        var selectedMinute = minuteTumbler.selectedValue


        selectedHour = String.format(
            Locale.getDefault(), AppConstants.DEFAULT_DATE_VALUE_FORMAT, selectedHour?.toInt()
        )
        selectedMinute = String.format(
            Locale.getDefault(), AppConstants.DEFAULT_DATE_VALUE_FORMAT, selectedMinute?.toInt()
        )
        HMILogHelper.Logd(tag, "kitchenTimer numpad carry over-->$selectedHour$selectedMinute")

        val bundle = Bundle()
        if (isKitchenTimerModify) bundle.putString(
            BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER,
            arguments?.getString(BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER)
        )
        bundle.putString(BundleKeys.BUNDLE_PROVISIONING_TIME, "$selectedHour$selectedMinute")
        bundle.putBoolean(BundleKeys.BUNDLE_EXTRA_COMING_FROM_KT, true)
        NavigationUtils.navigateSafely(
            this, R.id.action_setKTFragment_to_setManualKTFragment, bundle, null
        )
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
        hourItemViewHolder = VerticalTumblerTimeAdapter(hourNumbers, isBottomViewVisible, isKnobRotationActive)
        hourTumbler.itemViewHolder = hourItemViewHolder
        hourTumbler.updateItems(hourNumbers as java.util.ArrayList<String>?, true)
    }

    private fun toggleFocusMinuteTumbler(
        isBottomViewVisible: Boolean,
        isKnobRotationActive: Boolean = true
    ) {
        minuteItemViewHolder = VerticalTumblerTimeAdapter(minNumbers, isBottomViewVisible, isKnobRotationActive)
        minuteTumbler.itemViewHolder = minuteItemViewHolder
        minuteTumbler.updateItems(minNumbers as java.util.ArrayList<String>?, true)
    }

    private fun toggleFocusSecondTumbler(
        isBottomViewVisible: Boolean,
        isKnobRotationActive: Boolean = true
    ) {
        secondItemViewHolder = VerticalTumblerTimeAdapter(minNumbers, isBottomViewVisible, isKnobRotationActive)
        secondTumbler.itemViewHolder = secondItemViewHolder
        secondTumbler.updateItems(minNumbers as java.util.ArrayList<String>?, true)
    }

    override fun onTumblerValueUpdate(isValidRange: Boolean) {
        val calculateKitchenTimer = calculateKitchenTimer()
        binding.startNowText.isEnabled =
            !(calculateKitchenTimer == 0 || calculateKitchenTimer > MAX_KITCHEN_TIMER_IN_SECONDS)
        rotator = if (binding.startNowText.isEnabled) listOf(0, 1, 2, 3) else listOf(0, 1, 2)
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
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isTumblerSelected = false
        rotatorClickCount.clear()
        selectedRotator = -1
        knobClickCount = 0
    }
}
