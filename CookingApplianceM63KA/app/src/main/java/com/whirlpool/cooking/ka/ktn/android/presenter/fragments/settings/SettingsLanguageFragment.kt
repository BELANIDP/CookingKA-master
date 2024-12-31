/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.fragments.settings

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.media.AudioManager
import android.os.Bundle
import android.presenter.customviews.listView.ListTileData
import android.presenter.customviews.listView.SettingsListViewHolderInterface
import android.presenter.customviews.radiobutton.RadioButton
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentLanguageBinding
import com.whirlpool.hmi.settings.SettingsViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.AppLanguageDetails
import core.utils.AppLanguageDetails.Companion.getLanguageCodeByName
import core.utils.AudioManagerUtils
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper.Logd
import core.utils.KnobNavigationUtils
import core.utils.KnobNavigationUtils.Companion.knobBackTrace
import core.utils.KnobNavigationUtils.Companion.knobForwardTrace
import core.utils.NavigationUtils
import core.utils.SettingsManagerUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * File        : android.presenter.fragments.settings.SettingsLanguageFragment
 * Brief       : Language selection based on Input.
 * Author      : Vijay Shinde
 * Created On  : 16/10/2024
 * Details     : Instance of Abstract List fragment to represent the Languages List Screen.
 */
class SettingsLanguageFragment : SuperAbstractTimeoutEnableFragment(),
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
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        init()
        setTimeoutApplicable(!isUnboxing)
        setMeatProbeApplicable(!isUnboxing)
        manageHeaderBar()
        manageLanguageListRecyclerView()
        if (knobForwardTrace) {
            knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highLightSelectedTiles()
            updateRadioButtonBackgroundOnLaunch()
        }
    }

    /**
     * Method to apply selected background color on Knob Navigation on initial launch only
     */
    private fun updateRadioButtonBackgroundOnLaunch() {
        fragmentLanguageBinding?.languageRecyclerList?.postDelayed({
            val itemCount = fragmentLanguageBinding?.languageRecyclerList?.adapter?.itemCount ?: 0
            // Loop through all items and update `RadioButton` states
            Logd("Item count: $itemCount")
            for (position in 0 until itemCount) {
                // Update the RadioButton background based on whether it's checked
                Logd("Updating RadioButton at position: $position")
                val viewHolder =
                    fragmentLanguageBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(position)
                val radioButton =
                    viewHolder?.itemView?.findViewById<RadioButton>(R.id.list_item_radio_button)
                // If the RadioButton exists, update its background color based on checked state
                if (radioButton?.isVisible == true) {
                    radioButton.let {
                        val radioButtonColor = if (it.isChecked()) {
                            // If the RadioButton is checked, set it to the selected color
                            Logd("RadioButton at position $position is selected")
                            R.color.cavity_selected_button_background
                        } else {
                            // If it's not checked, set it to the default background color
                            Logd("RadioButton at position $position is unselected")
                            R.color.color_black
                        }
                        if(radioButtonColor == R.color.cavity_selected_button_background){
                            lastItemSelectedPos= position
                            currentPosition = position
                        }
                        updateTileBackground(position, radioButtonColor)
                    }
                }
            }
        }, 50)
    }

    /**
     * header bar UI populate as per requirement for Language settings
     */
    private fun manageHeaderBar() {
        fragmentLanguageBinding?.headerBar?.apply {
            setTitleText(resources.getString(R.string.text_tiles_list_language))
            setOvenCavityIconVisibility(false)
            setLeftIconVisibility(true)
            setLeftIcon(R.drawable.ic_back_arrow)
            setRightIconVisibility(true)
            setRightIcon(R.drawable.ic_close)
            setInfoIconVisibility(false)
            setCustomOnClickListener(this@SettingsLanguageFragment)
        }
    }

    /**
     * Init settings
     */
    private fun init() {
        isUnboxing = SettingsManagerUtils.isUnboxing
        appLocales = resources.getStringArray(R.array.app_languages)
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
     * provide stored language
     * @return language name
     */
    @SuppressLint("DiscouragedApi")
    private fun getStoredLanguage(): String {
        return resources.getString(
                resources.getIdentifier(AppConstants.TEXT_TILE_LIST +
                        AppLanguageDetails.getLanguageNameByCode(SettingsViewModel.getSettingsViewModel().appLanguage.value.toString()),
                        AppConstants.RESOURCE_TYPE_STRING, requireContext().packageName
                )
        )
    }

    /**
     * provide language list
     * @param languageListItemSize available language list size
     * @return language list items with radio button support
     */
    @SuppressLint("DiscouragedApi")
    private fun provideLanguageListOptions(languageListItemSize: Int?): ArrayList<ListTileData> {
        if (languageListItemSize != null) {
            for (i in 0 until languageListItemSize) {
                val builder = ListTileData()
                builder.titleText = resources.getString(
                        resources.getIdentifier(
                                AppConstants.TEXT_TILE_LIST + appLocales!![i],
                                AppConstants.RESOURCE_TYPE_STRING, requireContext().packageName
                        )
                )
                val radioData = ListTileData.RadioButtonData()
                radioData.apply {
                    visibility = View.VISIBLE
                    isEnabled = true
                    isChecked = builder.titleText.contentEquals(getStoredLanguage())
                }
                builder.apply {
                    radioButtonData = radioData
                    itemIconVisibility = View.INVISIBLE
                    subTextVisibility = View.GONE
                    rightTextVisibility = View.GONE
                    titleTextVisibility = View.VISIBLE
                    rightIconVisibility = View.GONE
                    itemViewVisibility = View.VISIBLE
                    isPaddingView = false
                }
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
    @SuppressLint("NotifyDataSetChanged")
    private fun handleLanguageItemClick(position: Int) {
        if (appLocales != null && (appLocales?.size ?: 0) > 0) {
            AudioManagerUtils.playOneShotSound(
                    view?.context,
                    R.raw.button_press,
                    AudioManager.STREAM_SYSTEM,
                    true,
                    0,
                    1
                )
            val appLanguageData = getLanguageCodeByName(
                    appLocales?.get(position) ?: ""
            )
            if(SettingsViewModel.getSettingsViewModel().appLanguage.value != appLanguageData) {
                if (SettingsViewModel.DemoMode.DEMO_MODE_ENABLED == SettingsViewModel.getSettingsViewModel().demoMode.value) {
                    val bundle = Bundle()
                    bundle.apply {
                        putString(BundleKeys.BUNDLE_NAVIGATED_FROM, AppConstants.SETTINGS_LANGUAGE_FRAGMENT)
                        putString(BundleKeys.BUNDLE_SELECTED_LANGUAGE, appLanguageData)
                    }
                    NavigationUtils.navigateSafely(this, R.id.action_settingsLanguageFragment_to_demoModeCodeFragment,
                        bundle, null
                    )
                } else {
                    SettingsViewModel.getSettingsViewModel().setAppLanguage(appLanguageData)
                }
            }
        }
        lifecycleScope.launch(Dispatchers.IO) {
            languagesList?.let {
                val selectedItem: ListTileData = it[position]
                for (i in 0 until it.size) {
                    val listItemModel: ListTileData = it[i]
                    val radioData = listItemModel.radioButtonData
                    radioData.visibility = View.VISIBLE
                    radioData.isEnabled = true
                    radioData.isChecked = selectedItem == listItemModel
                }
            }
        }

        fragmentLanguageBinding?.languageRecyclerList?.adapter?.notifyDataSetChanged()
        fragmentLanguageBinding?.headerBar?.setTitleText(resources.getString(R.string.text_tiles_list_language))
    }

    /**
     * List view item click listener
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
        if (lastItemSelectedPos != -1) {
            lifecycleScope.launch(Dispatchers.Main) {
                fragmentLanguageBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )?.itemView?.findViewById<ConstraintLayout>(R.id.list_item_main_view)
                    ?.callOnClick()
            }
            knobForwardTrace = false
            knobBackTrace = true
            CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
                NavigationUtils.navigateSafely(
                    it,
                    R.id.action_settingsLanguageFragment_to_settingsRegionalSettingsFragment,
                    null,
                    null
                )
            }
        }
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
                            "SettingsLanguage",
                            "LEFT_KNOB: rotate right current knob index = $currentPosition"
                    )
                    fragmentLanguageBinding?.languageRecyclerList?.smoothScrollToPosition(
                            currentPosition
                    )
                    highLightSelectedTiles()

                } else {
                    Logd(
                            "SettingsLanguage",
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
            // ToDo: reset to default if radio button type of list
            val viewHolder =
                    fragmentLanguageBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
                            lastItemSelectedPos
                    )
            viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
        }
        lastItemSelectedPos = -1
    }

    /**
     * highlight the item row during knob interaction
     */
    private fun highLightSelectedTiles() {
        // ToDo: for radio button Highlight the selected tile on launch with knob
        fragmentLanguageBinding?.languageRecyclerList?.postDelayed({
            if (lastItemSelectedPos != -1) {
                updateTileBackground(lastItemSelectedPos, R.color.color_black)
            }
            lastItemSelectedPos = currentPosition
            updateTileBackground(
                lastItemSelectedPos,
                R.color.cavity_selected_button_background
            )
        }, 50) // Adjust delay as needed
    }

    private fun updateTileBackground(
        position: Int,
        backgroundColor: Int
    ) {
        val viewHolder =
            fragmentLanguageBinding?.languageRecyclerList?.findViewHolderForAdapterPosition(
                position
            )
        viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(backgroundColor))
    }

    override fun leftIconOnClick() {
        //For smooth transition between fragment we have added navOption with anim parameter
        val navOptions = NavOptions
            .Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .build()
        KnobNavigationUtils.setBackPress()
        CookingAppUtils.getVisibleFragment(activity?.supportFragmentManager)?.let {
            NavigationUtils.navigateSafely(
                    it,
                    R.id.action_settingsLanguageFragment_to_settingsRegionalSettingsFragment,
                    null,
                navOptions
            )
        }
    }

    override fun rightIconOnClick() {
        CookingAppUtils.navigateToStatusOrClockScreen(fragment = this)
    }

    override fun onToggleSwitchClick(position: Int, isChecked: Boolean) {
        //on Radio button click listener
        handleLanguageItemClick(position)
    }

    override fun onDestroyView() {
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        fragmentLanguageBinding = null
        allItemSize = 0
        super.onDestroyView()
    }
}

