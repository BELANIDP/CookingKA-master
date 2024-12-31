package android.presenter.basefragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.kitchentimer.KitchenTimerVMFactory
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel.TimerOperationStatus
import com.whirlpool.hmi.kitchentimer.KitchenTimerViewModel.TimerStatus
import com.whirlpool.hmi.uicomponents.tools.util.DisplayUtils
import com.whirlpool.hmi.uicomponents.tools.util.ResourceUtils
import com.whirlpool.hmi.uicomponents.widgets.progress.Stepper
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumbler
import com.whirlpool.hmi.uicomponents.widgets.tumblers.BaseTumblerItemViewInterface
import com.whirlpool.hmi.utils.LogHelper
import core.jbase.abstractViewHolders.AbstractKitchenTimerWidgetListItemViewProvider
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import java.util.Locale
import java.util.Objects
import java.util.SortedMap
import java.util.TreeMap
import java.util.concurrent.TimeUnit

/**
 * File       : android.presenter.basefragments.AbstractKitchenTimerTumblerListFragment
 * Brief      : Kitchen Timer list
 * Author     : SDK (can be leverage from SDK if they update based on KA requirements)
 * Created On : 06/19/2024
 * Details    : To show dynamic list of Kitchen Timer, user can add, delete or pause a particular Kitchen Timer Widget
 **/

abstract class AbstractKitchenTimerTumblerListFragment : SuperAbstractTimeoutEnableFragment(), BaseTumblerItemViewInterface {

    /**
     * A list to be able to maintain the kitchen timers for the tumbler.
     * Only using the array return from #getSoonestKitchenTimers, can result in
     * the data becoming out of sync with what the tumbler is using.
     */
    private val soonestKitchenTimerViewModels = ArrayList<String>()
    /**
     * live data to update the knob value as it rotates, used because the items of the adapter are binding with  this observer to update the view containing underline
     */
    protected val mKnobPositionLiveData = MutableLiveData(-1)

    /**
     * currently selected Kitchen Timer widget fetch timer Key used when making action based on knob click
     */
    protected var knobSelectedKitchenTimerKey: String? = null

    /**
     * enum values to represents action of kitchen timer widget
     */
    @Suppress("ClassName")
    enum class KT_WIDGET_ACTION {
        PLAY_PAUSE,//represents kitchen timer widget pause,resume action state
        ADD_ONE_MIN,//adding one min action to kitchen timer view model
        NONE //represents none, remove knob focus from every widgets
    }

    /**
     * variable to remember the where the knob rotation stopped last time
     */
    protected lateinit var knobSelectedAction : KT_WIDGET_ACTION

    class AbstractKitchenTimerTumblerListFragment{
        // Required Empty constructor
    }

    /**
     * onViewCreated will be called once the view is inflated in the implementing fragment.
     * The view components (primary, secondry and cancel buttons, tumbler view) would be initialized here.
     *
     * @param view               [View]
     * @param savedInstanceState [Bundle]
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    /**
     * This is for the view to implement the navigation to the fragment for Setting the Timer
     */
    abstract fun navigateToSetTimerFragment()

    /**
     * Interface to provide the button to add kitchen timers
     *
     * @return [View]
     */
    abstract fun provideAddKitchenTimersButton(): ImageView?

    /**
     * Interface to provide the tumbler interface
     *
     * @return [BaseTumbler]
     */
    abstract fun provideTumbler(): BaseTumbler?

    /**
     * Interface to check if kitchen timer is available or not
     *
     * @param state Check if any kitchen timer is available
     */
    abstract fun kitchenTimerAvailable(state: Boolean)

    /**
     * Interface for the kitchen timer list items
     *
     * @return [AbstractKitchenTimerTumblerListFragment]
     */
    abstract fun provideKitchenListItemViewProvider(): AbstractKitchenTimerWidgetListItemViewProvider

    /**
     * Stepper is an optional component that can display items similar to a pager indicator
     *
     * @return [Stepper]
     */
    open fun provideStepper(): Stepper? {
        return null
    }


    /**
     * initializeViews will initialize and set actions for the view components.
     */
    private fun initializeViews() {
        //Setting Views
        updateAddKitchenTimerButton()
        // Prepare Tumbler
        prepareOrUpdateTumbler()
        // Add Kitchen Timer
        provideAddKitchenTimersButton()?.setOnClickListener {
            //Add navigation to the Set/Edit Timer screen
            navigateToSetTimerFragment()
        }
    }

    /**
     * Optional implementation that will allow the view to implement their own pager
     *
     * @param
     */
    @Suppress("unused")
    private fun updateDotsAppearanceForItem() {
        // no-op
    }

    /**
     * Optional implementation that will allow the view to implement their own pager
     *
     * @param size int - number of kitchen timers active
     */
    @Suppress("unused")
    protected open fun updateDotsVisibilityForItems(size: Int) {
        // no-op
    }

    /**
     * Updates the tumblers with new list of soonest expiring timers
     */
    private fun prepareOrUpdateTumbler() {
        provideTumbler()?.itemViewHolder = this
        soonestKitchenTimerViewModels.clear()
        soonestKitchenTimerViewModels.addAll(getSoonestKitchenTimer())
        provideTumbler()?.updateItems(soonestKitchenTimerViewModels, true)
        if (provideStepper() != null) {
            provideStepper()?.setNoOfStepCount(soonestKitchenTimerViewModels.size)
        }
        updateDotsVisibilityForItems(soonestKitchenTimerViewModels.size)
    }

    // All Utility Functions ---------------------------------->
    // Utility Function for Timer Availability
    private fun isAnyTimerAvailable(): Boolean {
        return null != KitchenTimerVMFactory.getNextAvailableKitchenTimer()
    }

    private fun updateAddKitchenTimerButton() {
        // If timers are available then send the event to the veiw
        kitchenTimerAvailable(isAnyTimerAvailable())
    }

    private fun getSoonestKitchenTimer(): ArrayList<String> {
        // Change to <String, String> Map because if we use just the time remaining and
        // time is the same, the timer will be replaced in the the map.
        val soonestKitchenTimer: SortedMap<String, String> = TreeMap()
        if (KitchenTimerVMFactory.getKitchenTimerViewModels() != null) {
            for (kitchenTimerViewModel in KitchenTimerVMFactory.getKitchenTimerViewModels()!!) {
                // This is important because this is called from the view holders and currently observers get mapped with the
                // view holder items, Discussed with Joe and added this. This can be removed if the interfaces from the SDK change
                kitchenTimerViewModel.remainingTime.removeObservers(viewLifecycleOwner)
                if (kitchenTimerViewModel.timerStatus.value == TimerStatus.RUNNING || kitchenTimerViewModel.timerStatus.value == TimerStatus.PAUSED) {
                    // Adding the timer key to ensure that when the time remaining is the same the kitchen timer is still added to
                    // the sorted map
                    soonestKitchenTimer[String.format(
                        Locale.getDefault(),
                        "%05d",
                        kitchenTimerViewModel.remainingTime.value
                    ) + kitchenTimerViewModel.timerKey] = kitchenTimerViewModel.timerKey
                }
            }
        } else {
            HMILogHelper.Logd("Kitchen Timer View Models is NULL")
        }
        return ArrayList(soonestKitchenTimer.values)
    }

    fun getKitchenTimerForIdentifier(
        kitchenTimerViewModels: ArrayList<KitchenTimerViewModel>?,
        itemIdentifier: String?,
    ): KitchenTimerViewModel? {
        for (kitchenTimerViewModel in kitchenTimerViewModels!!) {
            if (kitchenTimerViewModel.timerKey.equals(itemIdentifier, ignoreCase = true)) {
                return kitchenTimerViewModel
            }
        }
        return null
    }


    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val kitchenTimerListItemViewProvider = provideKitchenListItemViewProvider()
        kitchenTimerListItemViewProvider.inflate(LayoutInflater.from(parent.context))
        val layoutParams = LinearLayout.LayoutParams(
            DisplayUtils.densityPixelsToPixels(
                parent.context,
                provideTumbler()?.itemWidth?.toFloat()?:0f
            ).toInt(),
            DisplayUtils.densityPixelsToPixels(
                parent.context,
                (provideTumbler()?.itemHeight?.plus(10))?.toFloat()?:0f
            ).toInt()
        )
        kitchenTimerListItemViewProvider.view?.layoutParams = layoutParams
        return ViewHolder(kitchenTimerListItemViewProvider)
    }


    /**
     * updating the view of Kitchen Timer Widget inside of a BaseTumbler
     *
     * @param kitchenTimerViewModel KT view model that currently running
     * @param viewHolder viewHolder of KT widget
     * @param kitchenTimerWidgetIndex which index is selected by tumbler
     * @param knobPosition position of the knob livedata by rotating the knob
     */
    private fun updateKnobViewFocus(kitchenTimerViewModel: KitchenTimerViewModel, viewHolder: ViewHolder, kitchenTimerWidgetIndex: Int, knobPosition: Int) {
        when (kitchenTimerWidgetIndex) {
            AppConstants.KNOB_COUNTER_ZERO -> {
                if (knobPosition == AppConstants.KNOB_POSITION_0) {
                    knobUnderLineVisibility(
                        KT_WIDGET_ACTION.PLAY_PAUSE,
                        viewHolder.kitchenTimerListItemViewProvider
                    )
                    setProvideClickViewOfKnob(kitchenTimerViewModel, KT_WIDGET_ACTION.PLAY_PAUSE)
                }
                if (knobPosition == AppConstants.KNOB_POSITION_1){
                    knobUnderLineVisibility(
                        KT_WIDGET_ACTION.ADD_ONE_MIN,
                        viewHolder.kitchenTimerListItemViewProvider
                    )
                    setProvideClickViewOfKnob(kitchenTimerViewModel, KT_WIDGET_ACTION.ADD_ONE_MIN)
                }
            }

            AppConstants.KNOB_COUNTER_ONE -> {
                if (knobPosition == AppConstants.KNOB_POSITION_2) {
                    knobUnderLineVisibility(
                        KT_WIDGET_ACTION.PLAY_PAUSE,
                        viewHolder.kitchenTimerListItemViewProvider
                    )
                    setProvideClickViewOfKnob(kitchenTimerViewModel, KT_WIDGET_ACTION.PLAY_PAUSE)
                }
                if (knobPosition == AppConstants.KNOB_POSITION_3) {
                    knobUnderLineVisibility(
                        KT_WIDGET_ACTION.ADD_ONE_MIN,
                        viewHolder.kitchenTimerListItemViewProvider
                    )
                    setProvideClickViewOfKnob(kitchenTimerViewModel, KT_WIDGET_ACTION.ADD_ONE_MIN)
                }
            }

            AppConstants.KNOB_COUNTER_TWO -> {
                if (knobPosition == AppConstants.KNOB_POSITION_4) {
                    knobUnderLineVisibility(
                        KT_WIDGET_ACTION.PLAY_PAUSE,
                        viewHolder.kitchenTimerListItemViewProvider
                    )
                    setProvideClickViewOfKnob(kitchenTimerViewModel, KT_WIDGET_ACTION.PLAY_PAUSE)
                }
                if (knobPosition == AppConstants.KNOB_POSITION_5) {
                    knobUnderLineVisibility(
                        KT_WIDGET_ACTION.ADD_ONE_MIN,
                        viewHolder.kitchenTimerListItemViewProvider
                    )
                    setProvideClickViewOfKnob(kitchenTimerViewModel, KT_WIDGET_ACTION.ADD_ONE_MIN)
                }
            }

            AppConstants.KNOB_COUNTER_THREE -> {
                if (knobPosition == AppConstants.KNOB_POSITION_6) {
                    knobUnderLineVisibility(
                        KT_WIDGET_ACTION.PLAY_PAUSE,
                        viewHolder.kitchenTimerListItemViewProvider
                    )
                    setProvideClickViewOfKnob(kitchenTimerViewModel, KT_WIDGET_ACTION.PLAY_PAUSE)
                }
                if (knobPosition == AppConstants.KNOB_POSITION_7) {
                    knobUnderLineVisibility(
                        KT_WIDGET_ACTION.ADD_ONE_MIN,
                        viewHolder.kitchenTimerListItemViewProvider
                    )
                    setProvideClickViewOfKnob(kitchenTimerViewModel, KT_WIDGET_ACTION.ADD_ONE_MIN)
                }
            }
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        itemIdentifier: String,
        index: Int,
        isSelected: Boolean,
    ) {
        if (holder is ViewHolder) {
            if (provideStepper() != null) {
                if (index == provideTumbler()?.selectedIndex) provideStepper()?.setStepperCurrentStep(
                    index + 1
                )
            }
            updateDotsAppearanceForItem()
            if (null != KitchenTimerVMFactory.getKitchenTimerViewModels()) {
                if (index >= soonestKitchenTimerViewModels.size) {
                    // if the tumbler and the running timers get out of sync it might be because of an external event
                    // we should return and expect the data set to notified for an update
                    LogHelper.Logi("Tumbler size and getSoonestKitchenTimers are out of sync. This most likely occurred " + "because the multiple timers updated concurrently. In that instance, another notifyDataSetChanged call will have been" + " made to resolve the issue.")
                    return
                }
                val kitchenTimerViewModel = getKitchenTimerForIdentifier(
                    KitchenTimerVMFactory.getKitchenTimerViewModels(),
                    soonestKitchenTimerViewModels[index]
                )
                if (kitchenTimerViewModel != null) {
                    // remove any previously used observers
                    kitchenTimerViewModel.timerStatus.removeObservers(viewLifecycleOwner)
                    kitchenTimerViewModel.remainingTime.removeObservers(viewLifecycleOwner)
                    // remove any previously used observers - setKitchenTimerViewModel will do that for us in the KitchenTimerTextView class
                    holder.kitchenTimerListItemViewProvider.provideKitchenTimerTextView()
                        ?.setKitchenTimerViewModel(kitchenTimerViewModel)
                    if (holder.kitchenTimerListItemViewProvider.provideKitchenTimerNameTextView() != null) {
                        holder.kitchenTimerListItemViewProvider.provideKitchenTimerNameTextView()?.text =
                            ResourceUtils.getResourceString(
                                context, kitchenTimerViewModel.timerName
                            )
                    }
                    holder.kitchenTimerListItemViewProvider.provideCancelButtonImageView()
                        ?.setImageResource(holder.kitchenTimerListItemViewProvider.provideCancelDrawable())

                    if (Objects.nonNull(holder.kitchenTimerListItemViewProvider.providePlusOneMinButtonText())) {
                        holder.kitchenTimerListItemViewProvider.providePlusOneMinButton()
                            ?.setTextButtonText(
                                holder.kitchenTimerListItemViewProvider.providePlusOneMinButtonText()
                                    .toString()
                            )

                    } else {
                        HMILogHelper.Logd("Provide One Min Button Text is Null")
                    }

                    holder.kitchenTimerListItemViewProvider.provideProgressBar()?.progress = 0
                    kitchenTimerViewModel.timerStatus.observe(
                        viewLifecycleOwner
                    ) { status: TimerStatus ->
                        if (status == TimerStatus.PAUSED) {
                            holder.kitchenTimerListItemViewProvider.providePauseResumeImageView()
                                ?.setImageResource(holder.kitchenTimerListItemViewProvider.provideResumeDrawable())
                        } else if (status == TimerStatus.RUNNING) {
                            holder.kitchenTimerListItemViewProvider.providePauseResumeImageView()
                                ?.setImageResource(holder.kitchenTimerListItemViewProvider.providePauseDrawable())
                        }
                    }
                    kitchenTimerViewModel.remainingTime.observe(
                        viewLifecycleOwner
                    ) { remainingTime: Int ->
                        val timeSetTo = kitchenTimerViewModel.timeSetTo.value
                        // change to != null to resolve unboxing lint warning
                        if (timeSetTo != null) {
                            holder.kitchenTimerListItemViewProvider.provideProgressBar()?.max =
                                timeSetTo
                            holder.kitchenTimerListItemViewProvider.provideProgressBar()?.progress =
                                timeSetTo - remainingTime
                            //max kitchen timer limit is 23h 59m 59 s which is 86399, disabling +1 min button on time set to more than 86339 sec
                            if(KitchenTimerUtils.isAbleToAddOneMinToKitchenTimer(kitchenTimerViewModel)) {
                                holder.kitchenTimerListItemViewProvider.providePlusOneMinButton()?.isEnabled =
                                    true
                                holder.kitchenTimerListItemViewProvider.providePlusOneMinButton()?.isClickable = true
                            }else{
                                holder.kitchenTimerListItemViewProvider.providePlusOneMinButton()?.isEnabled =
                                    false
                                holder.kitchenTimerListItemViewProvider.providePlusOneMinButton()?.isClickable = false
                                holder.kitchenTimerListItemViewProvider.provideOneMinKnobUnderline().visibility = View.GONE
                            }
                        } else {
                            LogHelper.Loge("Error in getRemainingTime()")
                        }
                    }
                    mKnobPositionLiveData.observe(viewLifecycleOwner) { position: Int ->
                        HMILogHelper.Logd(
                            tag,
                            "itemIdentifier $itemIdentifier, index $index, position $position, isSelected $isSelected selectedIndex ${provideTumbler()?.selectedIndex}"
                        )
                        if(index == provideTumbler()?.selectedIndex) updateKnobViewFocus(
                            kitchenTimerViewModel,
                            holder,
                            index,
                            position
                        )
                        else{
                            knobUnderLineVisibility(
                                KT_WIDGET_ACTION.NONE, holder.kitchenTimerListItemViewProvider
                            )
                        }
                    }

                    holder.kitchenTimerListItemViewProvider.provideCancelButtonImageView()
                        ?.setOnClickListener {
                            onClickCancelKitchenTimer(kitchenTimerViewModel)
                        }
                    if (holder.kitchenTimerListItemViewProvider.providePlusOneMinButton() != null) {
                        holder.kitchenTimerListItemViewProvider.providePlusOneMinButton()
                            ?.setOnClickListener {
                                if(holder.kitchenTimerListItemViewProvider.providePlusOneMinButton()?.isEnabled == true) {
                                    if (kitchenTimerViewModel.addTime(60)) {
                                        HMILogHelper.Logd("Success in adding time with index $index and itemIdentifier $itemIdentifier")
                                    } else {
                                        LogHelper.Loge("Error in adding one min to the itemIdentifier $itemIdentifier")
                                    }
                                }else{
                                    LogHelper.Loge("max limit to the kitchen timer is set, cannot add one min itemIdentifier $itemIdentifier")
                                }
                            }
                    }
                    if (holder.kitchenTimerListItemViewProvider.provideKitchenTimerTextView() != null) {
                        holder.kitchenTimerListItemViewProvider.provideKitchenTimerTextView()
                            ?.setOnClickListener {
                                val hours = TimeUnit.SECONDS.toHours(
                                    kitchenTimerViewModel.remainingTime.value?.toLong() ?: 0
                                ).toInt()
                                val minutes = (TimeUnit.SECONDS.toMinutes(
                                    kitchenTimerViewModel.remainingTime.value?.toLong() ?: 0
                                ) - TimeUnit.HOURS.toMinutes(hours.toLong())).toInt()
                                val selectedTimeString = String.format(
                                    Locale.getDefault(), "%02d%02d", hours, minutes
                                )
                                HMILogHelper.Logd(
                                    tag,
                                    "kitchenTimer MODIFY name ${kitchenTimerViewModel.timerName} remainingTime ${kitchenTimerViewModel.remainingTime.value} -> hours: $hours minutes: $minutes -> converted $selectedTimeString"
                                )
                                val bundle = Bundle()
                                bundle.putString(
                                    BundleKeys.BUNDLE_MODIFY_KITCHEN_TIMER,
                                    kitchenTimerViewModel.timerName
                                )
                                bundle.putString(
                                    BundleKeys.BUNDLE_PROVISIONING_TIME,
                                    selectedTimeString
                                )
                                bundle.putBoolean(BundleKeys.BUNDLE_EXTRA_COMING_FROM_KT, true)
                                if (NavigationUtils.navigateSafely(
                                        this,
                                        R.id.action_kitchenTimerFragment_to_setManualKTFragment,
                                        bundle,
                                        null
                                    )
                                ) {
                                    HMILogHelper.Logd("Success in navigating modify kitchen timer name ${kitchenTimerViewModel.timerName}")
                                } else {
                                    LogHelper.Loge("Error in navigating modify kitchen timer name ${kitchenTimerViewModel.timerName}")
                                }
                            }
                    }
                    holder.kitchenTimerListItemViewProvider.providePauseResumeImageView()
                        ?.setOnClickListener {
                            if (kitchenTimerViewModel.timerStatus.value == TimerStatus.RUNNING) {
                                if (kitchenTimerViewModel.pauseTimer()) {
                                    HMILogHelper.Logd("Success in Pausing the timer")
                                } else {
                                    LogHelper.Loge("Error in Pausing the timer")
                                }
                            } else if (kitchenTimerViewModel.timerStatus.value == TimerStatus.PAUSED) {
                                if (kitchenTimerViewModel.resumeTimer()) {
                                    LogHelper.Loge("Success in Resuming the timer")
                                } else {
                                    LogHelper.Loge("Error in Pausing the timer")
                                }
                            } else {
                                LogHelper.Loge("Unhandled kitchen timer state")
                            }
                        }
                } else {
                    LogHelper.Loge("Could NOT find KitchenTimerViewModel with key: " + soonestKitchenTimerViewModels[index])
                }
            } else {
                LogHelper.Loge("Kitchen Timer View models are not null")
            }
        }
    }

    /**
     * used when x cancel on KT widget is pressed either by touch or knob press
     * @param kitchenTimerViewModel currently associated kitchen timer
     */
    fun onClickCancelKitchenTimer(kitchenTimerViewModel: KitchenTimerViewModel) {
        timeoutViewModel?.stop()
        PopUpBuilderUtils.kitchenTimerCancelPopup(this, kitchenTimerViewModel, {
            handleKitchenTimerCompleteOrCancel()
            if (kitchenTimerViewModel.stopTimer() && isVisible) {
                HMILogHelper.Logd("Success in stopping the timer with itemIdentifier ${kitchenTimerViewModel.timerKey}")
                // reset timers for tumbler
                prepareOrUpdateTumbler()
                updateAddKitchenTimerButton()
                timeoutViewModel?.restart()
            } else {
                LogHelper.Loge("Error in stopping the timer")
            }
        }, { timeoutViewModel?.restart() })
    }

    /**
     * setter method to update the where was the last knob focus held
     * useful when knob press event is detected to know which Kitchen timer to take actions like pause, add one min
     * @param kitchenTimerViewModel currently associated kitchen timer
     * @param kitchenTimerWidgetAction determined action to take pause, resume, etc
     */
    private fun setProvideClickViewOfKnob(kitchenTimerViewModel: KitchenTimerViewModel?, kitchenTimerWidgetAction: KT_WIDGET_ACTION) {
        knobSelectedKitchenTimerKey = kitchenTimerViewModel?.timerKey
        knobSelectedAction = kitchenTimerWidgetAction
        HMILogHelper.Logd(tag, "knobSelectedKitchenTimerKey $knobSelectedKitchenTimerKey action $kitchenTimerWidgetAction")
    }

    /**
     * to update the view of kitchen timer widget
     * make underline visible based on the action and hide for others
     * @param kitchenTimerWidgetAction determined action to take pause, resume, etc
     * @param viewHolder to get the view access
     */
    private fun knobUnderLineVisibility(kitchenTimerWidgetAction: KT_WIDGET_ACTION, viewHolder : AbstractKitchenTimerWidgetListItemViewProvider){
        when(kitchenTimerWidgetAction){
            KT_WIDGET_ACTION.PLAY_PAUSE -> {
                viewHolder.providePauseResumeKnobUnderline().visibility = View.VISIBLE
                viewHolder.provideOneMinKnobUnderline().visibility = View.GONE
            }
            KT_WIDGET_ACTION.ADD_ONE_MIN -> {
                viewHolder.providePauseResumeKnobUnderline().visibility = View.GONE
                viewHolder.provideOneMinKnobUnderline().visibility = if(viewHolder.providePlusOneMinButton()?.isEnabled == true) View.VISIBLE else View.GONE
            }
            else ->{
                viewHolder.providePauseResumeKnobUnderline().visibility = View.GONE
                viewHolder.provideOneMinKnobUnderline().visibility = View.GONE
            }
        }
    }

    /**
     * This is an optional implementation and must be defined in the [.onViewCreated]
     * method.
     *
     *
     * There are 2 ways to observe the status of the kitchen
     * timers:
     *
     *
     * 1) Have the MainActivity observe all the Kitchen Timers and then update a listener
     * that the fragment can set against the MainActivity or
     * 2) Simply call this function and implement [.onKitchenTimerComplete],
     * [.onKitchenTimerCancelled] and [.onKitchenTimerStarted] methods.
     */
    fun registerKitchenTimerObservers() {
        if (KitchenTimerVMFactory.getKitchenTimerViewModels() == null) {
            KitchenTimerVMFactory.instantiateKitchenTimerViewModels()
        }
        for (kitchenTimerViewModel in KitchenTimerVMFactory.getKitchenTimerViewModels()!!) {
            try {
                kitchenTimerViewModel.timerStatus.observe(viewLifecycleOwner) { timerStatus: TimerStatus? ->
                    when (timerStatus) {
                        TimerStatus.COMPLETED ->{
                            onKitchenTimerComplete(kitchenTimerViewModel)}
                        TimerStatus.CANCELLED -> onKitchenTimerCancelled()
                        TimerStatus.RUNNING -> onKitchenTimerStarted()
                        else ->{}
                    }
                }
                kitchenTimerViewModel.timerOperationStatus.observe(
                    viewLifecycleOwner
                ) { timerOperationStatus ->
                    if (timerOperationStatus == TimerOperationStatus.CANCEL) {
                        onKitchenTimerCancelled()
                    }
                }
            } catch (exception: Exception) {
                LogHelper.Loge("KitchenTimerException$exception")
            }
        }
    }

    /**
     * Provides generic functionality that will update the tumbler and navigate out of
     * the screen to the SetTimerFragment if no kitchen timers are running.
     *
     *
     * Please override if additional functionality is needed.
     */
    fun handleKitchenTimerCompleteOrCancel() {
        prepareOrUpdateTumbler()
        if (!KitchenTimerUtils.isAnyKitchenTimerRunningOrPaused()) {
            CookingAppUtils.navigateToStatusOrClockScreen(this)
        }
    }

    /**
     * Provides generic functionality that will update the tumbler when a new
     * kitchen timer is started.
     *
     *
     * Please override if additional functionality is needed.
     */
    fun handleNewKitchenTimerStarted() {
        prepareOrUpdateTumbler()
    }

    /**
     * If [.registerKitchenTimerObservers] is used, please override
     * this method to handle the completed event for a kitchen timer.
     *
     *
     * [.handleKitchenTimerCompleteOrCancel] is provided for generic handling
     * of the completed event.
     *
     * @param model [KitchenTimerViewModel]
     */
    open fun onKitchenTimerComplete(model: KitchenTimerViewModel?) {
        // no op
    }

    /**
     * If [.registerKitchenTimerObservers] is used, please override
     * this method to handle the cancelled event for a kitchen timer.
     *
     *
     * [.handleKitchenTimerCompleteOrCancel] is provided for generic handling
     * of the cancelled event.
     */
    open fun onKitchenTimerCancelled() {
        // no op
    }

    /**
     * If [.registerKitchenTimerObservers] is used, please override
     * this method to handle the cancelled event for a kitchen timer.
     *
     *
     * [.handleNewKitchenTimerStarted] is provided for generic handling
     * of the started event.
     */
    open fun onKitchenTimerStarted() {
        // no op
    }
    class ViewHolder internal constructor(var kitchenTimerListItemViewProvider: AbstractKitchenTimerWidgetListItemViewProvider) :
        RecyclerView.ViewHolder(kitchenTimerListItemViewProvider.view!!)
}