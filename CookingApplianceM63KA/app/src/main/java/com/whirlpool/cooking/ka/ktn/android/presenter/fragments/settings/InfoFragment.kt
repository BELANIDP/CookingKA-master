package android.presenter.fragments.settings

import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.ListViewHolderInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentInfoBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory.setInScopeViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KitchenTimerUtils
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.SharedPreferenceManager
import core.utils.ToolsMenuJsonKeys
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_DEMO
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SERVICE_SUPPORT
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SOFTWARE_TERM_AND_CONDITIONS
import kotlinx.coroutines.launch

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.fragments.PreferencesLandingFragment
 * Brief      : This class provides the Preferences screen
 * Author     : Manjeet/Amar
 * Created On : 14-03-2024
 */
class InfoFragment : SuperAbstractTimeoutEnableFragment(),
    ListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener {
    private var fragmentInfoBinding: FragmentInfoBinding? = null
    private var infoListItems: ArrayList<String>? = null
    private var currentPosition = -1
    private var lastItemSelectedPos = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        fragmentInfoBinding = FragmentInfoBinding.inflate(inflater)
        fragmentInfoBinding!!.lifecycleOwner = this.viewLifecycleOwner
        return fragmentInfoBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
        if(KnobNavigationUtils.knobForwardTrace){
            KnobNavigationUtils.knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highlightSelectedItem(lastItemSelectedPos)
        }
    }

    private fun highlightSelectedItem(position: Int) {
        fragmentInfoBinding?.infoRecyclerList?.post {
            val viewHolderOld =
                fragmentInfoBinding?.infoRecyclerList?.findViewHolderForAdapterPosition(
                    position
                )
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }
    }

    private fun manageChildViews() {
        manageInfoCollectionHeaderBar()
        managePreferencesListRecyclerView()
    }

    private fun manageInfoCollectionHeaderBar() {
        fragmentInfoBinding?.headerBarInfo?.setLeftIcon(R.drawable.ic_back_arrow)
        fragmentInfoBinding?.headerBarInfo?.setRightIcon(R.drawable.ic_close)
        fragmentInfoBinding?.headerBarInfo?.setTitleText(getString(R.string.info))
        fragmentInfoBinding?.headerBarInfo?.setOvenCavityIconVisibility(false)
        fragmentInfoBinding?.headerBarInfo?.setInfoIconVisibility(false)
        fragmentInfoBinding?.headerBarInfo?.setCustomOnClickListener(this)
    }

    private fun managePreferencesListRecyclerView() {
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SHOW_ALL_INFO)
            ?.let {
                infoListItems = it
            }

        infoListItems?.let {
            fragmentInfoBinding?.infoRecyclerList?.visibility = View.VISIBLE
            val listTileData: java.util.ArrayList<ListTileData> =
                provideInfoListRecyclerViewTilesData()
            listTileData.let {
                val listItems: ArrayList<Any> = ArrayList(listTileData)
                val toolsListViewInterface =
                    ListViewHolderInterface(
                        listTileData, this
                    )
                fragmentInfoBinding?.infoRecyclerList?.setupListWithObjects(
                    listItems,
                    toolsListViewInterface
                )
            }
        }
    }

    private fun provideInfoListRecyclerViewTilesData(): ArrayList<ListTileData> {
        val preferencesListTileData = ArrayList<ListTileData>()

        return preferencesListTileData.also {
            infoListItems?.let { listItems ->
                for (listItem in listItems) {
                    val listTileData = ListTileData()
                    listTileData.titleTextVisibility = View.VISIBLE
                    listTileData.headingText = listItem
                    listTileData.subTextVisibility = View.GONE
                    listTileData.rightTextVisibility = View.GONE
                    listTileData.rightIconVisibility = View.VISIBLE
                    listTileData.itemIconVisibility = View.VISIBLE
                    val textResId =
                        CookingAppUtils.getResIdFromResName(
                            this.requireContext(),
                            listItem,
                            AppConstants.RESOURCE_TYPE_STRING
                        )
                    listTileData.titleText = getString(textResId)
                    listTileData.itemIconID = getDrawableForName(listItem)
                    listTileData.rightIconID = R.drawable.ic_rightarrowicon
                    if (it.size == listItems.size.minus(1)) listTileData.listItemDividerViewVisibility =
                        View.GONE
                    val radioButtonData = ListTileData.RadioButtonData()
                    radioButtonData.visibility = View.INVISIBLE
                    listTileData.radioButtonData = radioButtonData
                    listTileData.isPaddingView = false
                    if ((SharedPreferenceManager.getTechnicianTestDoneStatusIntoPreference() == AppConstants.TRUE_CONSTANT) &&
                        (listItem == JSON_KEY_TOOLS_MENU_DEMO)
                    ) {
                        listTileData.isItemEnabled = false
                    } else {
                        listTileData.isItemEnabled = !(CookingAppUtils.isAnyCycleRunning() &&
                                (listItem == JSON_KEY_TOOLS_MENU_DEMO ||
                                        listItem == JSON_KEY_TOOLS_MENU_SERVICE_SUPPORT))
                    }
                    if (!TextUtils.isEmpty(listTileData.titleText))
                        it.add(listTileData)
                }
            }
        }
    }

    private fun getDrawableForName(name: String): Int {
        return when (name) {
            JSON_KEY_TOOLS_MENU_DEMO -> R.drawable.demomode
            JSON_KEY_TOOLS_MENU_SERVICE_SUPPORT -> R.drawable.ic_servicesupporticon
            JSON_KEY_TOOLS_MENU_SOFTWARE_TERM_AND_CONDITIONS -> R.drawable.ic_software_term_and_condition
            else -> R.drawable.ic_placeholder
        }
    }

    /**
     * Listener method which is called on List tile click
     *
     * @param view the tile view which is clicked
     * @param position index/position for the tile clicked
     */
    override fun onListViewItemClick(view: View?, position: Int) {
        when (position) {
            0 -> {
                if (SharedPreferenceManager.getTechnicianTestDoneStatusIntoPreference() == AppConstants.TRUE_CONSTANT) {
                    showNotificationDemoNotAvailableInTechnicianMode()
                    return
                }
                if (CookingAppUtils.isDemoModeEnabled()) {
                    if (CookingAppUtils.isAnyCycleRunning() || KitchenTimerUtils.isAnyKitchenTimerRunningOrPaused()) {
                        PopUpBuilderUtils.demoModeCycleRunningExitInstructionPopUp(this)
                    } else {
                        PopUpBuilderUtils.demoModeExitInstructionPopUp(this)
                    }
                } else {
                    if (CookingAppUtils.isAnyCycleRunning() && KitchenTimerUtils.isAnyKitchenTimerRunningOrPaused()) {
                        PopUpBuilderUtils.featureNotAvailablePopUp(this)
                    } else if (CookingAppUtils.isAnyCycleRunning()) {
                        PopUpBuilderUtils.featureNotAvailablePopUp(this)
                    } else if (KitchenTimerUtils.isAnyKitchenTimerRunningOrPaused()) {
                        PopUpBuilderUtils.demoModeKTRunningEntryInstructionPopUp(this)
                    } else
                        PopUpBuilderUtils.demoModeEntryInstructionPopUp(this)
                }
            }

            1 -> {
                if (CookingAppUtils.isAnyCycleRunning()) {
                    PopUpBuilderUtils.featureNotAvailablePopUp(this)
                } else {
                    NavigationUtils.navigateSafely(
                        this, R.id.global_action_go_to_error_screen,
                        null, null
                    )
                }
            }

            2 -> {
                NavigationUtils.navigateSafely(
                    this,
                    R.id.action_infoFragment_to_settingsSoftwareTermAndConditionFragment,
                    null,
                    null
                )
            }
        }
    }

    private fun showNotificationDemoNotAvailableInTechnicianMode() {
        fragmentInfoBinding?.drwawerbar?.isVisible = true
        fragmentInfoBinding?.drwawerbar?.showNotification(
            getString(R.string.text_feature_unavailable_technician_mode),
            AppConstants.DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT,
            fragmentInfoBinding?.drwawerbar
        )
    }

    override fun leftIconOnClick() {
        NavigationUtils.navigateSafely(
            this,
            R.id.action_infoFragment_to_settingsLandingFragment,
            null,
            null
        )
    }

    override fun rightIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return resources.getInteger(R.integer.session_long_timeout)
    }

    override fun onHMILeftKnobClick() {
        if (lastItemSelectedPos != -1) {
            KnobNavigationUtils.knobForwardTrace = true
            onListViewItemClick(fragmentInfoBinding?.infoRecyclerList,lastItemSelectedPos)
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
                        provideInfoListRecyclerViewTilesData().size
                    )
                    if (currentPosition >= 0) {
                        HMILogHelper.Logd("LEFT_KNOB: rotate right current knob index = $currentPosition")
                        fragmentInfoBinding?.infoRecyclerList?.smoothScrollToPosition(currentPosition)

                        fragmentInfoBinding?.infoRecyclerList?.postDelayed({
                            if (lastItemSelectedPos != -1) {
                                val viewHolder = fragmentInfoBinding?.infoRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
                            }
                            lastItemSelectedPos = currentPosition
                            val viewHolderOld = fragmentInfoBinding?.infoRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
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
        if (knobId == AppConstants.LEFT_KNOB_ID) {
            if (lastItemSelectedPos != -1) {
                val viewHolder = fragmentInfoBinding?.infoRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = -1
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
    }
}