/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.digital_unboxing

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.SettingsListViewHolderInterface
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentLanguageBinding
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.utils.Constants
import com.whirlpool.hmi.ota.utils.OTAStatus
import com.whirlpool.hmi.ota.viewmodel.OTAVMFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import com.whirlpool.hmi.utils.ContextProvider
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppConstants.KEY_CONFIGURATION_DIGITAL_UNBOXING
import core.utils.AppLanguageDetails.Companion.getLanguageCodeByName
import core.utils.AudioManagerUtils
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.resetInstructionsForCavity
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper.Logd
import core.utils.HMILogHelper.Loge
import core.utils.KnobNavigationUtils
import core.utils.NavigationUtils
import core.utils.SettingsManagerUtils
import core.utils.SettingsManagerUtils.isBleProvisionSuccess
import core.utils.SharedPreferenceManager.getTechnicianTestDoneStatusIntoPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * File        : android.presenter.fragments.digital_unboxing.UnboxingLanguageListFragment
 * Brief       : Language selection based on Input.
 * Author      : Nikki Gharde
 * Created On  : 3.Sep.2024
 * Details     : Instance of Abstract List fragment to represent the Languages List Screen.
 */
class UnBoxingLanguageListFragment : SuperAbstractTimeoutEnableFragment(),
    SettingsListViewHolderInterface.ListItemClickListener,
    HeaderBarWidgetInterface.CustomClickListenerInterface,
    HMIKnobInteractionListener {

    private var isUnboxing: Boolean = false
    private var languagesList: ArrayList<ListTileData>? = null
    private var appLocales: Array<String>? = null
    private var fragmentLanguageBinding: FragmentLanguageBinding? = null

    private var lastItemSelectedPos = -1
    private var currentPosition = -1
    private var allItemSize = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentLanguageBinding = FragmentLanguageBinding.inflate(inflater)
        fragmentLanguageBinding?.lifecycleOwner = this.viewLifecycleOwner
        return fragmentLanguageBinding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HMIExpansionUtils.disableFeatureHMIKeys(KEY_CONFIGURATION_DIGITAL_UNBOXING)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        init()
        setTimeoutApplicable(!isUnboxing)
        setMeatProbeApplicable(!isUnboxing)
        manageHeaderBar()
        manageLanguageListRecyclerView()
        processOtaAndProvisionFlow()
    }


    /**
     * header bar UI populate as per requirement for unboxing
     */
    private fun manageHeaderBar() {
        fragmentLanguageBinding?.headerBar?.setTitleText(getString(R.string.text_tiles_list_language))
        fragmentLanguageBinding?.headerBar?.setOvenCavityIconVisibility(false)
        fragmentLanguageBinding?.headerBar?.setLeftIconVisibility(false)
        fragmentLanguageBinding?.headerBar?.setRightIconVisibility(false)
        fragmentLanguageBinding?.headerBar?.setInfoIconVisibility(false)
        fragmentLanguageBinding?.headerBar?.setCustomOnClickListener(this)
    }

    /**
     * scenario handling for OTA and Provison flow
     */
    private fun processOtaAndProvisionFlow() {
        if (isUnboxing && isBleProvisionSuccess) {
            val otaViewModel = OTAVMFactory.getOTAViewModel()
            if (otaViewModel != null && otaViewModel.otaState != null && otaViewModel.otaState.value != null && otaViewModel.otaState.value == OTAStatus.COMPLETED) {
                Logd(
                    "OTA",
                    "Launching ota complete screen from unboxing to global_action_showTemperatureUnits"
                )
                //launch OTA flow
            }
        } else {
            Logd("OTA", "unboxing is false")
        }
    }

    /**
     * Init settings
     */
    private fun init() {
        isUnboxing = SettingsManagerUtils.isUnboxing
        appLocales = resources.getStringArray(R.array.app_languages)
        if (isUnboxing) {
            val cookBookViewModel = CookBookViewModel.getInstance()
            resetInstructionsForCavity(
                Constants.PRIMARY_CAVITY_KEY, cookBookViewModel,
                this
            )
            resetInstructionsForCavity(
                Constants.SECONDARY_CAVITY_KEY, cookBookViewModel,
                this
            )
        }
    }

    /**
     * populate the language list options and bind into the recycler view
     */
    private fun manageLanguageListRecyclerView() {
        val listTileData: ArrayList<ListTileData> = provideListRecyclerViewTilesData()
        listTileData.let {
            allItemSize = listTileData.size
            fragmentLanguageBinding?.languageRecyclerList?.visibility = View.VISIBLE
            val listItems: ArrayList<Any> = ArrayList(listTileData)
            val toolsListViewInterface =
                SettingsListViewHolderInterface(
                    listTileData, this
                )
            fragmentLanguageBinding?.languageRecyclerList?.setupListWithObjects(
                listItems,
                toolsListViewInterface
            )
        }
    }

    /**
     * provide recycler view data to list view
     *
     * @return list of Language list
     */
    fun provideListRecyclerViewTilesData(): ArrayList<ListTileData> {
        val listViewItems = appLocales?.size
        if (languagesList != null && languagesList?.isNotEmpty() == true) {
            languagesList?.clear()
        } else {
            languagesList = ArrayList()
        }
        return provideLanguageListOptions(listViewItems)
    }


    /**
     * provide language list
     *
     * @param languageListItemSize available language list size
     * @return language list items with radio button support
     */
    @SuppressLint("DiscouragedApi")
    private fun provideLanguageListOptions(languageListItemSize: Int?): ArrayList<ListTileData> {
        if (languageListItemSize != null) {
            for (i in 0 until languageListItemSize) {
                val builder = ListTileData()
                builder.titleText = getString(
                    resources.getIdentifier(
                        AppConstants.TEXT_TILE_LIST + appLocales!![i],
                        AppConstants.RESOURCE_TYPE_STRING, requireContext().packageName
                    )
                )
                val radioData = ListTileData.RadioButtonData()
                radioData.visibility = View.GONE
                builder.radioButtonData = radioData
                builder.itemIconVisibility = View.GONE
                builder.subTextVisibility = View.GONE
                builder.rightTextVisibility = View.GONE
                builder.titleTextVisibility = View.VISIBLE
                builder.rightIconID = R.drawable.ic_rightarrowicon
                builder.rightIconVisibility = View.VISIBLE
                builder.itemViewVisibility = View.VISIBLE
                builder.isPaddingView = false
                if (i == languageListItemSize.minus(1)) builder.listItemDividerViewVisibility =
                    View.GONE
                setDefaultSelectedLanguage(i)
                languagesList?.add(builder)
            }
            return languagesList ?: arrayListOf()
        }
        return arrayListOf()
    }

    /**
     * To check whether the item selected or not by default
     *
     * @param currentItemIndex current item from list
     * @return whether is default selected or not
     */
    private fun setDefaultSelectedLanguage(currentItemIndex: Int): Boolean {
        val appLanguage = SettingsViewModel.getSettingsViewModel().appLanguage.value
        return appLanguage != AppConstants.EMPTY_STRING && appLanguage == getLanguageCodeByName(
            appLocales?.get(currentItemIndex) ?: ""
        )
    }

    /**
     * based on language selection the app language will change
     *
     * @param position clicked language position
     */
    private fun handleLanguageItemClick(position: Int) {
        if (appLocales != null && (appLocales?.size ?: 0) > 0) {
            AudioManagerUtils.playOneShotSound(
                ContextProvider.getContext(),
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            val appLanguageData = getLanguageCodeByName(
                appLocales?.get(position) ?: ""
            )
            SettingsViewModel.getSettingsViewModel().setAppLanguage(appLanguageData)
            if (getTechnicianTestDoneStatusIntoPreference() == AppConstants.TRUE_CONSTANT) {
                Logd("Unboxing", "Unboxing: Navigate to user role list flow")
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it,
                        R.id.action_fragment_language_to_unboxingUserRoleListFragment,
                        null,
                        null
                    )
                }
            } else {
                Logd("Unboxing", "Unboxing: Navigate to feature info flow")
                CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                    NavigationUtils.navigateSafely(
                        it,
                        R.id.action_fragment_language_to_unboxingApplianceFeaturesInfoFragment,
                        null,
                        null
                    )
                }
            }
        } else {
            Loge("Language list not available in app")
        }
    }

    /**
     * @param view - view
     * @param position - adapter position
     */
    override fun onListViewItemClick(view: View?, position: Int) {
        handleLanguageItemClick(position)
    }

    /**
     * Called when there is a Left Knob click event
     */
    override fun onHMILeftKnobClick() {
        knobLeftAndRightClickEvent()
    }

    /**
     * Called when there is a Long Left Knob click event
     */
    override fun onHMILongLeftKnobPress() {
    }

    /**
     * Called when there is a Right Knob click event
     */
    override fun onHMIRightKnobClick() {
        knobLeftAndRightClickEvent()
    }

    /**
     * Called when there is a Long Right Knob click event
     */
    override fun onHMILongRightKnobPress() {
    }

    /**
     * Called when there is a Long Right Knob click event
     */
    override fun onHMIRightKnobTickHoldEvent(timeInterval: Int) {
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    /**
     * Called when there is a knob rotate event on a Knobs
     * @param knobId  knob ID
     * @param knobDirection knob movement direction
     */
    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                currentPosition = CookingAppUtils.getKnobPositionIndex(
                    knobDirection,
                    currentPosition,
                    allItemSize
                )
                if (currentPosition >= 0) {
                    Logd(
                        "Unboxing",
                        "LEFT_KNOB: rotate right current knob index = $currentPosition"
                    )
                    fragmentLanguageBinding?.languageRecyclerList?.smoothScrollToPosition(
                        currentPosition
                    )
                    highLightSelectedTiles()

                } else {
                    Logd(
                        "Unboxing",
                        "LEFT_KNOB: rotate left current knob index = $currentPosition"
                    )
                    currentPosition = 0
                    highLightSelectedTiles()
                }
            }
        }
    }

    /**
     * Called after 10 sec when there is no interaction on knob
     */
    override fun onKnobSelectionTimeout(knobId: Int) {
        if (lastItemSelectedPos != -1) {
            val viewHolder =
                fragmentLanguageBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )
            viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
        }
        lastItemSelectedPos = -1
    }

    /**
     * highligh the item row during knob interaction
     */
    private fun highLightSelectedTiles() {
        fragmentLanguageBinding?.languageRecyclerList?.postDelayed({
            if (lastItemSelectedPos != -1) {
                val viewHolder =
                    fragmentLanguageBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )
                viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
            }
            lastItemSelectedPos = currentPosition
            val viewHolderOld =
                fragmentLanguageBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )
            viewHolderOld?.itemView?.setBackgroundColor(requireContext().getColor(R.color.cavity_selected_button_background))
        }, 50) // Adjust delay as needed
    }

    /**
     * common function for knob left and right click event
     */
    private fun knobLeftAndRightClickEvent() {
        if (lastItemSelectedPos != -1) {
            lifecycleScope.launch(Dispatchers.Main) {
                KnobNavigationUtils.knobForwardTrace = true
                fragmentLanguageBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )?.itemView?.findViewById<ConstraintLayout>(R.id.list_item_main_view)
                    ?.callOnClick()

            }
        }
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        fragmentLanguageBinding = null
        allItemSize = 0
        super.onDestroyView()
    }
}