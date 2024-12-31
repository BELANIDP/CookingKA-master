package android.presenter.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.listView.NotificationsTileData
import android.presenter.customviews.listView.NotificationsViewHolderInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentNotificationCenterBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory.setInScopeViewModel
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.cookbook.records.HistoryRecord
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.FavoriteDataHolder
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.NavigationUtils.Companion.getViewSafely
import core.utils.NotificationJsonKeys.NOTIFICATION_CONNECT_TO_NW
import core.utils.NotificationJsonKeys.NOTIFICATION_GET_TO_KNOW_YOUR_APPLIANCE
import core.utils.NotificationJsonKeys.NOTIFICATION_SAVE_LAST_RECIPE_FAVORITE
import core.utils.NotificationJsonKeys.NOTIFICATION_SW_UPDATED_SUCCESSFULLY
import core.utils.NotificationJsonKeys.NOTIFICATION_SW_UPDATE_AVAILABLE
import core.utils.NotificationJsonKeys.NOTIFICATION_UPDATE_DATE_AND_TIME
import core.utils.NotificationManagerUtils
import core.utils.PopUpBuilderUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.fragments.PreferencesLandingFragment
 * Brief      : This class provides the Preferences screen
 * Author     : Amar
 * Created On : 21-10-2024
 */
class NotificationCenterFragment : SuperAbstractTimeoutEnableFragment(),
    NotificationsViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener,
    HMIExpansionUtils.HMICancelButtonInteractionListener,
    HMIExpansionUtils.HMIHomeButtonInteractionListener{
    private var fragmentNotificationCenterBinding: FragmentNotificationCenterBinding? = null
    private var notificationsListItems: ArrayList<NotificationsTileData>? = null
    private var currentPosition = -1
    private var lastItemSelectedPos = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        fragmentNotificationCenterBinding = FragmentNotificationCenterBinding.inflate(inflater)
        fragmentNotificationCenterBinding!!.lifecycleOwner = this.viewLifecycleOwner
        return fragmentNotificationCenterBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
    }

    override fun onResume() {
        super.onResume()
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_HOME)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_HOME)
    }

    private fun manageChildViews() {
        managePreferencesCollectionHeaderBar()
        managePreferencesListRecyclerView()
    }

    private fun managePreferencesCollectionHeaderBar() {
        fragmentNotificationCenterBinding?.headerBarPreferences?.apply {
            setLeftIcon(R.drawable.ic_back_arrow)
            setRightIconVisibility(false)
            setTitleText(getString(R.string.text_header_notification))
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setCustomOnClickListener(this@NotificationCenterFragment)
        }
    }

    private fun managePreferencesListRecyclerView() {
        NotificationManagerUtils.getNotificationCenterListItems()
            .let {
                notificationsListItems = it
            }

        notificationsListItems?.let {
            fragmentNotificationCenterBinding?.preferencesRecyclerList?.visibility = View.VISIBLE
            val listTileData: java.util.ArrayList<NotificationsTileData> =
                providePreferencesListRecyclerViewTilesData()
            listTileData.let {
                val listItems: ArrayList<Any> = ArrayList(listTileData)
                val toolsListViewInterface =
                    NotificationsViewHolderInterface(
                        listTileData, this
                    )
                fragmentNotificationCenterBinding?.preferencesRecyclerList?.setupListWithObjects(
                    listItems,
                    toolsListViewInterface
                )
            }
        }
    }

    private fun providePreferencesListRecyclerViewTilesData(): ArrayList<NotificationsTileData> {
        val preferencesListTileData = ArrayList<NotificationsTileData>()

        return preferencesListTileData.also {
            notificationsListItems?.let { listItems ->
                for (listItem in listItems) {
                    val listTileData = NotificationsTileData()
                    listTileData.apply {
                        titleTextVisibility = View.VISIBLE
                        headingText = listItem.titleText
                        subTextVisibility = View.VISIBLE
                        rightTextVisibility = View.VISIBLE
                    }
                    listTileData.apply {
                        titleText = context?.let { it1 ->
                            CookingAppUtils.getNotificationStringId(
                                listItem.titleText, it1
                            )
                        }.toString()
                        val text = getSubText(listItem)
                        if(text == null){
                            subTextVisibility = View.GONE
                        }
                        else {
                            subText = text
                        }
                        rightText = context?.let { it1 -> getTimeStamp(it1, listItem.rightText) }.toString()
                        rightIconID = R.drawable.ic_rightarrowicon
                    }
                    if (it.size == listItems.size.minus(1)) listTileData.listItemDividerViewVisibility =
                        View.GONE
                    if (!TextUtils.isEmpty(listTileData.titleText))
                        it.add(listTileData)
                }
            }
        }
    }


    private fun getSubText(notification: NotificationsTileData): String? {
        when(notification.titleText){
            NOTIFICATION_GET_TO_KNOW_YOUR_APPLIANCE -> {
                return null
            }
            NOTIFICATION_SAVE_LAST_RECIPE_FAVORITE-> {
                val historyRecord = CookBookViewModel.getInstance()
                    .getHistoryRecordByHistoryId(notification.historyID)

                historyRecord?.let {
                    if (historyRecord.cavity == AppConstants.PRIMARY_CAVITY_KEY) {
                        setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
                    } else {
                        setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
                    }
                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.load(
                        historyRecord
                    )
                    CookingAppUtils.updateParametersInViewModel(
                        historyRecord,
                        CookingAppUtils.getRecipeOptions(),
                        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                    )

                    val text =
                        context?.let {
                            CookingAppUtils.getRecipeNameText(
                                it,
                                historyRecord.recipeName
                            )
                        }
                    val text2 = getDescriptionText(historyRecord)
                    return "$text $text2"
                }
                return null
            }
            NOTIFICATION_SW_UPDATED_SUCCESSFULLY-> {
                return resources.getString(R.string.text_version_number, OTAVMFactory.getOTAViewModel()?.otaManager?.currentSystemVersion )
            }
            NOTIFICATION_CONNECT_TO_NW -> {
                return null
            }
            NOTIFICATION_UPDATE_DATE_AND_TIME -> {
                return null
            }
            NOTIFICATION_SW_UPDATE_AVAILABLE-> {
                return resources.getString(R.string.text_version_number, OTAVMFactory.getOTAViewModel()?.targetSystemVersion?.value)
            }
            else -> {
                return null
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    private fun getTimeStamp(context: Context, timeStamp: String): String {
        // Get the current time in milliseconds
        val calendar = Calendar.getInstance()
        val currentTimeMillis = calendar.timeInMillis
        val timeStampOfNotification: Long = timeStamp.toLong()

        // Calculate the difference in time
        val elapsedMillis = currentTimeMillis - timeStampOfNotification

        // Convert the elapsed time into seconds, minutes, hours, and days
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
        val days = TimeUnit.MILLISECONDS.toDays(elapsedMillis)

        // Get the current calendar and check midnight of today
        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
        currentCalendar.set(Calendar.MINUTE, 0)
        currentCalendar.set(Calendar.SECOND, 0)
        currentCalendar.set(Calendar.MILLISECOND, 0)
        val midnightMillis = currentCalendar.timeInMillis

        //12AM midnight
        val yesterdayMidnight = midnightMillis - TimeUnit.DAYS.toMillis(1)

        //12PM yesterday
        val noonYesterdayMillis = yesterdayMidnight + TimeUnit.HOURS.toMillis(12)

        //If the notification triggered in the last 1 minute
        if (elapsedMillis < TimeUnit.MINUTES.toMillis(1)) {
            return context.getString(R.string.text_time_just_now, seconds)
        }

        //If the notification triggered in past 1 hour
        if (elapsedMillis < TimeUnit.HOURS.toMillis(1)) {
            return context.getString(R.string.text_time_minutes_ago, minutes)
        }

        //If the notification triggered within the last 3 hours
        if (elapsedMillis < TimeUnit.HOURS.toMillis(3)) {
            return context.getString(R.string.text_time_hours_and_minutes_ago, hours, minutes)
        }

        //If the notification triggered today but more than 3 hours ago
        if (timeStampOfNotification >= midnightMillis) {
            val dateFormat = SimpleDateFormat("h:mm a")  // 12-hour format (h:mm a)
            val date = Calendar.getInstance().apply {
                timeInMillis = timeStampOfNotification
            }
            val formattedTime = dateFormat.format(date.time)
            return context.getString(R.string.text_time_today_at, formattedTime)
        }

        //If notification triggered before 12pm yesterday
        if (timeStampOfNotification in yesterdayMidnight until noonYesterdayMillis) {
            return context.getString(R.string.text_time_yesterday)
        }

        //If notification triggered after 12pm yesterday but before 12am midnight.
        if (timeStampOfNotification in noonYesterdayMillis until midnightMillis) {
            val dateFormat = SimpleDateFormat("h:mm a")  // 12-hour format (h:mm a)
            val date = Calendar.getInstance().apply {
                timeInMillis = timeStampOfNotification
            }
            val formattedTime = dateFormat.format(date.time)
            return context.getString(R.string.text_time_yesterday_at, formattedTime)
        }

        //If notification triggered between 2 and 30 days ago
        if (days in 2..30) {
            return context.getString(R.string.text_time_days_ago, days)
        }

        //If notification triggered between 31 and 365 days ago
        if (days in 31..365) {
            val months = days / 30
            return context.getString(
                if (months > 1) R.string.text_time_months_plural_ago else R.string.text_time_months_ago,
                months
            )
        }

        //If notification triggered more than a year ago, show "x year(s) ago"
        if (days >= 365) {
            val years = days / 365
            return context.getString(
                if (years > 1) R.string.text_time_years_ago else R.string.text_time_years_ago,
                years
            )
        }

        return context.getString(R.string.weMissedThat)
    }

    /**
     * Listener method which is called on List tile click
     *
     * @param view the tile view which is clicked
     * @param position index/position for the tile clicked
     */
    override fun onListViewItemClick(view: View?, position: Int) {
        val notification = notificationsListItems?.get(position)?.titleText
        when (notification) {
            // User clicked Get to know your appliance. Navigate to Explore product flow.
            NOTIFICATION_GET_TO_KNOW_YOUR_APPLIANCE -> {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_notificationCenterFragment_to_unBoxingExploreFeaturesGuideFragment,
                    null, null
                )
            }

            // User clicked save last recipe to favorite. Navigate to history list.
            NOTIFICATION_SAVE_LAST_RECIPE_FAVORITE -> {
                val historyId = notificationsListItems?.get(position)?.historyID
                val historyRecord = historyId?.let {
                    CookBookViewModel.getInstance().getHistoryRecordByHistoryId(
                        it
                    )
                }

                if (historyRecord?.cavity == AppConstants.PRIMARY_CAVITY_KEY){
                    setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
                } else {
                    setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
                }
                if (historyRecord != null) {
                    CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.load(
                        historyRecord
                    )
                }
                if (historyRecord != null) {
                    CookingAppUtils.updateParametersInViewModel(
                        historyRecord,
                        CookingAppUtils.getRecipeOptions(),
                        CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel
                    )
                }

                if (historyId != null) {
                    CookingAppUtils.setHistoryIDToRemove(historyId)
                }

                CookingAppUtils.setNavigatedFrom(AppConstants.NAVIGATION_FROM_CREATE_FAV)
                FavoriteDataHolder.isNotificationFlow = true
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_notificationCenterFragment_to_fav_preview_fragment,
                    null, null
                )
            }

            // User clicked Software updated successfully. Navigate to Software update successful view.
            NOTIFICATION_SW_UPDATED_SUCCESSFULLY -> {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_notificationCenterFragment_to_otaCompleteNotificationView,
                    null, null
                )
            }

            // User clicked Connect to network. Navigate to BLE connect view.
            NOTIFICATION_CONNECT_TO_NW -> {
                CookingAppUtils.startProvisioning(
                    getViewSafely(this),
                    false,
                    isFromConnectivityScreen = true,
                    isAoBProvisioning = false
                )
            }

            // User clicked Update date and time. Navigate to a popup asking user to Update Manually or using Wifi
            NOTIFICATION_UPDATE_DATE_AND_TIME -> {
                CookingAppUtils.setNavigatedFrom(AppConstants.NAVIGATION_FROM_NOTIFICATION)
                PopUpBuilderUtils.updateDateAndTimeNotificationInstructionPopUp(this)
            }

            // User clicked Software update available. Navigate to Software update successful view
            NOTIFICATION_SW_UPDATE_AVAILABLE -> {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.global_action_to_otaBusyUpdateAvailableViewHolder,
                    null, null
                )
            }

            else -> {}
        }
    }


    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                getViewSafely(
                    this
                ) ?: requireView()
            ),
            R.id.clockFragment,
            false
        )
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return resources.getInteger(R.integer.session_long_timeout)
    }

    override fun onHMILeftKnobClick() {
        if (lastItemSelectedPos != -1) {
            onListViewItemClick(fragmentNotificationCenterBinding?.preferencesRecyclerList,lastItemSelectedPos)
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
                        providePreferencesListRecyclerViewTilesData().size
                    )
                    if (currentPosition >= 0) {
                        HMILogHelper.Logd("LEFT_KNOB: rotate right current knob index = $currentPosition")
                        fragmentNotificationCenterBinding?.preferencesRecyclerList?.smoothScrollToPosition(currentPosition)

                        fragmentNotificationCenterBinding?.preferencesRecyclerList?.postDelayed({
                            if (lastItemSelectedPos != -1) {
                                val viewHolder = fragmentNotificationCenterBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
                            }
                            lastItemSelectedPos = currentPosition
                            val viewHolderOld = fragmentNotificationCenterBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
                        }, 50) // Adjust delay as needed

                    }else{
                        HMILogHelper.Logd("LEFT_KNOB: rotate left current knob index = $currentPosition")
                        currentPosition = 0
                    }
                }
            }
        }
    }

    override fun onMeatProbeInsertion(cookingViewModel: CookingViewModel?) {
        if(CookingViewModelFactory.getInScopeViewModel() == null) {
            setInScopeViewModel(cookingViewModel)
            PopUpBuilderUtils.probeDetectedFromSameCavityPopupBuilder(this, cookingViewModel)
            return
        }
        if (cookingViewModel != null) {
            setInScopeViewModel(cookingViewModel)
            launchProbeDetectedPopupAsPerVariant(cookingViewModel)
        }
    }
    override fun onMeatProbeRemoval(cookingViewModel: CookingViewModel?) {
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true  && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (lastItemSelectedPos != -1) {
            val viewHolder = fragmentNotificationCenterBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
            viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
        }
        lastItemSelectedPos = -1
    }

    private fun getDescriptionText(record: HistoryRecord): String? {
        return when {

            // Handle weight and weight unit
            !record.weight.isNullOrEmpty() -> {
                val weightUnit = SettingsViewModel.getSettingsViewModel().weightUnit.value
                return context?.let {
                    CookingAppUtils.displayWeightToUser(
                        it,
                        record.weight!!.toFloat(),
                        weightUnit.toString()
                    )
                }
            }

            // Handle doneness
            !record.doneness.isNullOrEmpty() -> record.doneness.toString()

            // Handle target temperature and cook time
            !record.targetTemperature.isNullOrEmpty() -> buildString {
                append(record.targetTemperature)
                append("°")
                record.cookTime?.takeIf { it.isNotEmpty() }?.let { cookTime ->
                    append(" ")
                    append(context?.getString(R.string.for_min, (cookTime.toInt() / 60).toString()))
                }
            }

            // Handle MWO power level
            !record.mwoPowerLevel.isNullOrEmpty() -> "${record.mwoPowerLevel!!.toFloat().toInt()}%"

            // Default case: return empty string
            else -> ""
        }
    }

    override fun onHMICancelButtonInteraction() {
        HMIExpansionUtils.cancelCycleAndNavigateToClock()
    }

    override fun onHMIHomeButtonInteraction(){
        //do Nothing
    }
    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }
}