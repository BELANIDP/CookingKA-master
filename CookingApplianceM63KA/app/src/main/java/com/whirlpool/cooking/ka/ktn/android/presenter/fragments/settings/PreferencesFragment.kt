package android.presenter.fragments.settings

import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
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
import androidx.navigation.NavOptions
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentPreferenceBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory.setInScopeViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils
import core.utils.KnobNavigationUtils.Companion.knobBackTrace
import core.utils.KnobNavigationUtils.Companion.lastTimeSelectedData
import core.utils.NavigationUtils
import core.utils.PopUpBuilderUtils
import core.utils.ToolsMenuJsonKeys
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_CAVITY_LIGHTING
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_DISPLAY_AND_BRIGHTNESS
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_KNOB_SETTING
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_TIME_AND_DATE
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_REGIONAL_SETTINGS
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_RESTORE_SETTING
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_SOUND_VOLUME
import core.utils.ToolsMenuJsonKeys.JSON_KEY_TOOLS_TEMP_CALIBRATION
import kotlinx.coroutines.launch

/**
 * File       : com.whirlpool.cooking.ka.ktn.android.presenter.fragments.PreferencesLandingFragment
 * Brief      : This class provides the Preferences screen
 * Author     : Manjeet/Amar
 * Created On : 14-03-2024
 */
class PreferencesFragment : SuperAbstractTimeoutEnableFragment(),
    ListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener {
    private var fragmentPreferenceBinding: FragmentPreferenceBinding? = null
    private var preferencesListItems: ArrayList<String>? = null
    private var currentPosition = -1
    private var lastItemSelectedPos = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setInScopeViewModel(CookingViewModelFactory.getPrimaryCavityViewModel())
        fragmentPreferenceBinding = FragmentPreferenceBinding.inflate(inflater)
        fragmentPreferenceBinding!!.lifecycleOwner = this.viewLifecycleOwner
        return fragmentPreferenceBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        manageChildViews()
        if (knobBackTrace) {
            knobBackTrace = false
            currentPosition = lastTimeSelectedData()
            lastItemSelectedPos = lastTimeSelectedData()
            fragmentPreferenceBinding?.preferencesRecyclerList?.scrollToPosition(lastItemSelectedPos)
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

    private fun highlightSelectedItem(position: Int){
        fragmentPreferenceBinding?.preferencesRecyclerList?.post {
            val viewHolderOld =
                fragmentPreferenceBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(
                    position
                )
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    private fun manageChildViews() {
        managePreferencesCollectionHeaderBar()
        managePreferencesListRecyclerView()
    }

    private fun managePreferencesCollectionHeaderBar() {
        fragmentPreferenceBinding?.headerBarPreferences?.apply {
            setLeftIcon(R.drawable.ic_back_arrow)
            setRightIcon(R.drawable.ic_close)
            setTitleText(getString(R.string.preferences))
            setOvenCavityIconVisibility(false)
            setInfoIconVisibility(false)
            setCustomOnClickListener(this@PreferencesFragment)
        }
    }

    private fun managePreferencesListRecyclerView() {
        CookingAppUtils.getToolsItemsForKey(ToolsMenuJsonKeys.JSON_KEY_TOOLS_MENU_SHOW_ALL_PREFERENCES)
            ?.let {
                preferencesListItems = it
            }

        preferencesListItems?.let {
            fragmentPreferenceBinding?.preferencesRecyclerList?.visibility = View.VISIBLE
            val listTileData: java.util.ArrayList<ListTileData> =
                providePreferencesListRecyclerViewTilesData()
            listTileData.let {
                val listItems: ArrayList<Any> = ArrayList(listTileData)
                val toolsListViewInterface =
                    ListViewHolderInterface(
                        listTileData, this
                    )
                fragmentPreferenceBinding?.preferencesRecyclerList?.setupListWithObjects(
                    listItems,
                    toolsListViewInterface
                )
            }
        }
    }

    private fun providePreferencesListRecyclerViewTilesData(): ArrayList<ListTileData> {
        val preferencesListTileData = ArrayList<ListTileData>()

        return preferencesListTileData.also {
            preferencesListItems?.let { listItems ->
                for (listItem in listItems) {
                    val listTileData = ListTileData()
                    listTileData.apply {
                        titleTextVisibility = View.VISIBLE
                        headingText = listItem
                        subTextVisibility = View.GONE
                        rightTextVisibility = View.GONE
                        rightIconVisibility = View.VISIBLE
                        itemIconVisibility = View.VISIBLE
                        isItemEnabled = !(CookingAppUtils.isAnyCycleRunning() && listItem == JSON_KEY_TOOLS_RESTORE_SETTING)
                    }
                    val textResId =
                        CookingAppUtils.getResIdFromResName(
                            this.requireContext(),
                            listItem,
                            AppConstants.RESOURCE_TYPE_STRING
                        )
                    listTileData.apply {
                        titleText = getString(textResId)
                        itemIconID = getDrawableForName(listItem)
                        rightIconID = R.drawable.ic_rightarrowicon
                    }
                    if (it.size == listItems.size.minus(1)) listTileData.listItemDividerViewVisibility =
                        View.GONE
                    val radioButtonData = ListTileData.RadioButtonData()
                    radioButtonData.visibility = View.INVISIBLE
                    listTileData.radioButtonData = radioButtonData
                    listTileData.isPaddingView = false
                    if (!TextUtils.isEmpty(listTileData.titleText))
                        it.add(listTileData)
                }
            }
        }
    }

    private fun getDrawableForName(name: String): Int {
        return when (name) {
            JSON_KEY_TOOLS_MENU_TIME_AND_DATE -> R.drawable.ic_timedate
            JSON_KEY_TOOLS_SOUND_VOLUME -> R.drawable.ic_soundvolume
            JSON_KEY_TOOLS_DISPLAY_AND_BRIGHTNESS -> R.drawable.ic_displaysettings
            JSON_KEY_TOOLS_CAVITY_LIGHTING -> R.drawable.ic_cavitylight
            JSON_KEY_TOOLS_REGIONAL_SETTINGS -> R.drawable.ic_regionalsettings
            JSON_KEY_TOOLS_TEMP_CALIBRATION -> R.drawable.ic_tempcalibration
            JSON_KEY_TOOLS_KNOB_SETTING -> R.drawable.ic_knob_settings
            JSON_KEY_TOOLS_RESTORE_SETTING -> R.drawable.ic_restoresettings
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
        AudioManagerUtils.playOneShotSound(
            view?.context,
            R.raw.button_press,
            AudioManager.STREAM_SYSTEM,
            true,
            0,
            1
        )
        KnobNavigationUtils.addTraversingData(position,false)
        when (preferencesListItems?.get(position) ?: "") {
            JSON_KEY_TOOLS_MENU_TIME_AND_DATE -> {
                val bundle = Bundle()
                bundle.putString(BundleKeys.BUNDLE_NAVIGATED_FROM, AppConstants.PREFERENCES_FRAGMENT)
                NavigationUtils.navigateSafely(this, R.id.action_preferencesFragment_to_settingsTimeAndDateFragment, bundle, null)
            }

            JSON_KEY_TOOLS_SOUND_VOLUME -> {
                val bundle = Bundle()
                bundle.putInt(BundleKeys.BUNDLE_SOUND_DISPLAY, AppConstants.SOUND_VOLUME)
                NavigationUtils.navigateSafely(
                        this, R.id.action_preferencesFragment_to_soundVolumeDisplayFragment,
                        bundle, null)
            }

            JSON_KEY_TOOLS_DISPLAY_AND_BRIGHTNESS -> {
                val bundle = Bundle()
                bundle.apply {
                    putString(BundleKeys.BUNDLE_NAVIGATED_FROM, AppConstants.PREFERENCES_FRAGMENT)
                    putInt(BundleKeys.BUNDLE_SOUND_DISPLAY, AppConstants.DISPLAY_AND_BRIGHTNESS)
                }
                NavigationUtils.navigateSafely(
                        this, R.id.action_preferencesFragment_to_soundVolumeDisplayFragment,
                        bundle, null)
            }

            JSON_KEY_TOOLS_CAVITY_LIGHTING -> {
                NavigationUtils.navigateSafely(
                        this, R.id.action_preferencesFragment_to_cavity_light,
                        null, null)
            }

            JSON_KEY_TOOLS_REGIONAL_SETTINGS -> {
                //For smooth transition between fragment we have added navOption with anim parameter
                val navOptions = NavOptions
                    .Builder()
                    .setEnterAnim(R.anim.fade_in)
                    .setExitAnim(R.anim.fade_out)
                    .build()
                NavigationUtils.navigateSafely(
                        this, R.id.action_preferenceFragment_to_settingsRegionalSettingsFragment,
                        null, navOptions)
            }

            JSON_KEY_TOOLS_TEMP_CALIBRATION -> {
                if (CookingAppUtils.isDemoModeEnabled()) {
                    fragmentPreferenceBinding?.drwawerbar?.isVisible = true
                    fragmentPreferenceBinding?.drwawerbar?.showNotification(getString(R.string.text_feature_unavailable_demo_mode), DEMO_NOT_AVAILABLE_POP_UP_TIMEOUT,
                            fragmentPreferenceBinding?.drwawerbar)
                } else {
                    when (CookingViewModelFactory.getProductVariantEnum()) {
                        CookingViewModelFactory.ProductVariantEnum.SINGLEOVEN,
                        CookingViewModelFactory.ProductVariantEnum.MICROWAVEOVEN -> {
                            NavigationUtils.navigateSafely(
                                    this, R.id.action_preferenceFragment_to_temperatureCalibrationTumbler,
                                    null, null
                            )
                        }

                        CookingViewModelFactory.ProductVariantEnum.COMBO -> {
                            setInScopeViewModel(CookingViewModelFactory.getSecondaryCavityViewModel())
                            NavigationUtils.navigateSafely(
                                    this, R.id.action_preferenceFragment_to_temperatureCalibrationTumbler,
                                    null, null
                            )
                        }

                        CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                            NavigationUtils.navigateSafely(
                                    this, R.id.action_preferencesFragment_to_cavity_selection,
                                    null, null
                            )
                        }

                        else -> {}
                    }
                }
            }

            JSON_KEY_TOOLS_KNOB_SETTING -> {
                val bundle = Bundle()
                bundle.putString(BundleKeys.BUNDLE_NAVIGATED_FROM, AppConstants.PREFERENCES_FRAGMENT)
                NavigationUtils.navigateSafely(
                    this, R.id.action_preferenceFragment_to_settingsKnobFragment,
                    bundle, null)
            }

            JSON_KEY_TOOLS_RESTORE_SETTING -> {
                if (CookingAppUtils.isAnyCycleRunning()) {
                    PopUpBuilderUtils.featureNotAvailablePopUp(this)
                } else {
                    NavigationUtils.navigateSafely(
                        this, R.id.action_preferenceFragment_to_restoreSettingsListFragment,
                        null, null
                    )
                }
            }

            else -> {}
        }
    }

    override fun leftIconOnClick() {
        KnobNavigationUtils.setBackPress()
        NavigationUtils.navigateSafely(
            this, R.id.action_preferenceFragment_to_settingsLandingFragment,
            null, null)
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
            onListViewItemClick(fragmentPreferenceBinding?.preferencesRecyclerList,lastItemSelectedPos)
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
                        fragmentPreferenceBinding?.preferencesRecyclerList?.smoothScrollToPosition(currentPosition)

                        fragmentPreferenceBinding?.preferencesRecyclerList?.postDelayed({
                            if (lastItemSelectedPos != -1) {
                                val viewHolder = fragmentPreferenceBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
                                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
                            }
                            lastItemSelectedPos = currentPosition
                            val viewHolderOld = fragmentPreferenceBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(lastItemSelectedPos)
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
                val viewHolder =
                    fragmentPreferenceBinding?.preferencesRecyclerList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )
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