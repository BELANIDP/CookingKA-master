/*
 * ************************************************************************************************
 * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 * ************************************************************************************************
 */
package android.presenter.customviews.listView

import android.annotation.SuppressLint
import android.framework.services.HMIKnobInteractionListener
import android.os.Bundle
import android.presenter.customviews.radiobutton.RadioButton
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.toggleswitch.ToggleSwitch
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.whirlpool.cooking.ka.R
import com.whirlpool.cooking.ka.databinding.FragmentListBinding
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.settings.SettingsViewModel
import core.jbase.abstractViewHolders.SuperAbstractTimeoutEnableFragment
import core.utils.AppConstants
import core.utils.CavityStateUtils
import core.utils.CookingAppUtils
import core.utils.HMIExpansionUtils
import core.utils.HMILogHelper
import core.utils.KnobNavigationUtils.Companion.isBackPress
import core.utils.KnobNavigationUtils.Companion.knobBackTrace
import core.utils.KnobNavigationUtils.Companion.knobForwardTrace
import core.utils.KnobNavigationUtils.Companion.lastTimeSelectedData
import core.utils.KnobNavigationUtils.Companion.removeLastAction
import core.utils.MeatProbeUtils
import core.utils.PopUpBuilderUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * File        : android.presenter.customviews.listView.AbstractListFragment
 * Brief       : Abstract class for List fragment
 * Author      : PATELJ7
 * Details     : For creating an instance, extend this class and implement the abstract methods.
 *               This class handles actions common to all variants and provides interfaces that
 *               extended classes shall implement.
 */
abstract class AbstractListFragment : SuperAbstractTimeoutEnableFragment(), ListViewHolderInterface.ListItemClickListener,
    HMIKnobInteractionListener, MeatProbeUtils.MeatProbeListener {

    private var tilesData: ArrayList<ListTileData>? = null
    protected var allItemSize = 0
    protected var currentPosition = -1
    protected var fragmentBinding: FragmentListBinding? = null
    protected var lastItemSelectedPos = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBinding = FragmentListBinding.inflate(inflater, container, false)
        fragmentBinding?.lifecycleOwner = this
        return fragmentBinding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MeatProbeUtils.setMeatProbeListener(this)
        HMIExpansionUtils.setHMIKnobInteractionListener(this)
        setUpViewModels()
        setUpViews()
        observeViewModels()
        manageChildViews()
        if (knobBackTrace) {
            knobBackTrace = false
            currentPosition = lastTimeSelectedData()
            lastItemSelectedPos = lastTimeSelectedData()
            highLightSelectedTiles()
        } else if (knobForwardTrace) {
            knobForwardTrace = false
            currentPosition = 0
            lastItemSelectedPos = 0
            highLightSelectedTiles()
            updateRadioButtonBackgroundOnLaunch()
        }
        if (isBackPress()) {
            removeLastAction()
        }
    }

    /**
     * Method to initialize views
     */
    abstract fun setUpViews()

    /**
     * Method to set up view models.
     */
    protected fun setUpViewModels() {
        fragmentBinding?.settingsViewModel = SettingsViewModel.getSettingsViewModel()
        fragmentBinding?.listFragment
    }

    override fun onDestroyView() {
        clearMemory()
        HMIExpansionUtils.removeHMIKnobInteractionListener(this)
        MeatProbeUtils.removeMeatProbeListener()
        super.onDestroyView()
    }

    private fun clearMemory() {
        fragmentBinding = null
        allItemSize = 0
    }

    /**
     * Method for handling the child views.
     */
    private fun manageChildViews() {
        manageListHeaderBar()
        manageListRecyclerView()
        manageRightButton()
        manageLeftButton()
        manageGradientView()
    }

    /**
     * Method to set header bar data
     */
    private fun manageListHeaderBar() {
        fragmentBinding?.headerBarPreferences?.setLeftIcon(R.drawable.ic_back_arrow)
        provideHeaderBarLeftIconVisibility()?.let {
            fragmentBinding?.headerBarPreferences?.setLeftIconVisibility(
                it
            )
        }
        fragmentBinding?.headerBarPreferences?.setTitleText(provideHeaderBarTitleText())
        fragmentBinding?.headerBarPreferences?.setOvenCavityIconVisibility(false)
        provideHeaderBarInfoIconVisibility()?.let {
            fragmentBinding?.headerBarPreferences?.setInfoIconVisibility(
                it
            )
        }
        provideHeaderBarRightIconVisibility()?.let {
            fragmentBinding?.headerBarPreferences?.setRightIconVisibility(
                it
            )
        }
        fragmentBinding?.headerBarPreferences?.setCustomOnClickListener(object : HeaderBarWidgetInterface.CustomClickListenerInterface {
            override fun leftIconOnClick() {
                headerBarOnClick(null, ICON_TYPE_LEFT)
            }

            override fun rightIconOnClick() {
                headerBarOnClick(null, ICON_TYPE_RIGHT)
            }

            override fun infoIconOnClick() {
                headerBarOnClick(null, ICON_TYPE_INFO)
            }
        })
    }

    /**
     * Abstract Method to get the visibility for right icon header bar, it varies based on screen
     */
    protected abstract fun provideHeaderBarRightIconVisibility(): Boolean?

    /**
     * Abstract Method to get the visibility for left icon header bar, it varies based on screen
     */
    protected abstract fun provideHeaderBarLeftIconVisibility(): Boolean?

    /**
     * Abstract Method to get the visibility for info icon header bar, it varies based on screen
     */
    protected abstract fun provideHeaderBarInfoIconVisibility(): Boolean?

    /**
     * Abstract Method to get the text for header bar title, it varies based on screen
     */
    protected abstract fun provideHeaderBarTitleText(): String?

    /**
     * Method to setup Recycler View top margin
     */
    protected fun updateListMarginTop(top: Int) {
        val margins = (fragmentBinding?.recyclerViewList?.layoutParams as ConstraintLayout.LayoutParams).apply {
            leftMargin = 0
            rightMargin = 0
            topMargin = top
        }
        fragmentBinding?.recyclerViewList?.layoutParams = margins
    }

    /**
     * Method to setup Recycler View
     */
    @Suppress("UNCHECKED_CAST")
    private fun manageListRecyclerView() {
        tilesData = provideListRecyclerViewTilesData()
        allItemSize = tilesData?.size?:0
        if (!tilesData.isNullOrEmpty()) {
            val listItems: ArrayList<Any>? = tilesData as? ArrayList<Any>
            val toolsListViewInterface = ListViewHolderInterface(tilesData!!, this)
            fragmentBinding?.recyclerViewList?.setupListWithObjects(
                listItems,
                toolsListViewInterface
            )
            setScrollBarVisibility()
        } else {
            HMILogHelper.Loge("List Tile data not available!...")
        }
        fragmentBinding?.recyclerViewList?.addOnScrollListener(object : RecyclerView.OnScrollListener() {})
    }

    protected fun manageRightButton() {
        val rightButtonText = setRightButton()
        if (rightButtonText.isNullOrEmpty()) {
            fragmentBinding?.buttonRight?.visibility = View.GONE
        } else {
            fragmentBinding?.buttonRight?.visibility = View.VISIBLE
            fragmentBinding?.buttonRight?.text = rightButtonText

            // Handle button click with open method for customization in child classes
            fragmentBinding?.buttonRight?.setOnClickListener {
                onRightNavigationButtonClick(null, RIGHT_BUTTON_PRESS)
            }
            applyFadingEdgeBasedOnButton()
        }
    }

    /**
     * Method to setup Left button
     */
    protected fun manageLeftButton() {
        val leftButtonText = setLeftButton()
        if (leftButtonText.isNullOrEmpty()) {
            fragmentBinding?.buttonLeft?.visibility = View.GONE
        } else {
            fragmentBinding?.buttonLeft?.visibility = View.VISIBLE
            fragmentBinding?.buttonLeft?.text = leftButtonText

            // Handle button click with open method for customization in child classes
            fragmentBinding?.buttonLeft?.setOnClickListener {
                onLeftNavigationButtonClick(null, LEFT_BUTTON_PRESS)
            }
            applyFadingEdgeBasedOnButton()
        }
    }

    private fun manageGradientView() {
        val gradient = setGradientView()
        if (!gradient) {
            fragmentBinding?.listGradient?.visibility = View.GONE
            fragmentBinding?.recyclerViewList?.setPadding(0, 0, 0, 0)
        }
    }

    open fun setRightButton(): String? = null
    open fun setLeftButton(): String? = null
    open fun setGradientView(): Boolean = true
    /**
     * This function will set the Visibility of the Scroll Bar based on the list item count
     */
    private fun setScrollBarVisibility() {
        fragmentBinding?.recyclerViewList?.isVerticalScrollBarEnabled =
            (tilesData?.size ?: 0) >= MINIMUM_ITEM_COUNT_FOR_SCROLL
    }

    /**
     * Abstract Method to get the array of tile data of the list recycler view, it varies based on
     * screen
     */
    protected abstract fun provideListRecyclerViewTilesData(): ArrayList<ListTileData>?

    abstract override fun onListViewItemClick(view: View?, position: Int)

    /**
     * Method to get the settings view model
     * @return references of SettingsViewModel
     */
    protected open fun getSettingsViewModel(): SettingsViewModel? {
        return fragmentBinding?.getSettingsViewModel()
    }

    /**
     * Method to register to the live data and observe
     */
    protected abstract fun observeViewModels()

    /**
     * Method to notify the list recycler view adapter
     */
    @SuppressLint("NotifyDataSetChanged")
    @Suppress("unused")
    protected fun notifyDataSetChanged() {
        if (fragmentBinding?.recyclerViewList?.adapter != null) {
            fragmentBinding?.recyclerViewList?.adapter?.notifyDataSetChanged()
        }
    }

    /**
     * Method to notify the set of list changed for recycler view adapter
     */
    @Suppress("unused")
    protected fun notifyItemRangeSetChanged(positionStart: Int, positionEnd: Int) {
        if (fragmentBinding?.recyclerViewList?.adapter != null) {
            fragmentBinding?.recyclerViewList?.adapter?.notifyItemRangeChanged(
                positionStart,
                positionEnd
            )
        }
    }

    /**
     * Method to notify the item changed for recycler view adapter
     */
    @Suppress("unused")
    protected fun notifyItemChanged(position: Int) {
        if (fragmentBinding?.recyclerViewList?.adapter != null) {
            fragmentBinding?.recyclerViewList?.adapter?.notifyItemChanged(position)
        }
    }

    companion object {
        private const val MINIMUM_ITEM_COUNT_FOR_SCROLL = 4
        const val ICON_TYPE_LEFT = 1
        const val ICON_TYPE_RIGHT = 2
        const val ICON_TYPE_INFO = 3
        const val RIGHT_BUTTON_PRESS = 5
        const val LEFT_BUTTON_PRESS = 6
    }

    override fun provideScreenTimeoutValueInSeconds(): Int {
        return CavityStateUtils.getProgrammingStateTimeoutValue(resources)
    }

    /**
     * Abstract Method to handle the header bar events
     * @param view :view instance which is clicked in the header bar
     * @param buttonType type of the button which is pressed
     */
    abstract fun headerBarOnClick(view: View?, buttonType: Int)

    /**
     * Abstract Method to handle the right button events
     * @param view :view instance which is clicked
     * @param buttonType type of the button which is pressed
     */
    abstract fun onRightNavigationButtonClick(view: View?, buttonType: Int)

    /**
     * Abstract Method to handle the right button events
     * @param view :view instance which is clicked
     * @param buttonType type of the button which is pressed
     */
    abstract fun onLeftNavigationButtonClick(view: View?, buttonType: Int)

    /**
     * Method to apply fading edge based on the visibility of the right button
     */
    private fun applyFadingEdgeBasedOnButton() {
        val buttonRightVisible = fragmentBinding?.buttonRight?.visibility == View.VISIBLE
        val buttonLeftVisible = fragmentBinding?.buttonLeft?.visibility == View.VISIBLE

        if (buttonRightVisible || buttonLeftVisible) {
            fragmentBinding?.listGradient?.visibility = View.VISIBLE
        } else {
            fragmentBinding?.listGradient?.visibility = View.GONE
        }
    }

    override fun onHMILeftKnobClick() {
        knobForwardTrace = true
        if (lastItemSelectedPos != -1) {
            lifecycleScope.launch(Dispatchers.Main) {
                val toggle =
                    fragmentBinding?.recyclerViewList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )?.itemView?.findViewById<ToggleSwitch>(R.id.settings_item_toggle_switch)
                if (toggle?.visibility == View.VISIBLE) {
                    val toggleSwitch = toggle.findViewById<SwitchCompat>(R.id.toggle_switch)
                    toggleSwitch.isChecked = !toggleSwitch.isChecked
                } else {
                    fragmentBinding?.recyclerViewList?.findViewHolderForAdapterPosition(
                        lastItemSelectedPos
                    )?.itemView?.findViewById<ConstraintLayout>(R.id.list_item_main_view)
                        ?.callOnClick()
                }

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (knobId == AppConstants.LEFT_KNOB_ID)
                    manageKnobRotation(knobDirection)
            }
        }
    }

    override fun onKnobSelectionTimeout(knobId: Int) {
        if (knobId == AppConstants.LEFT_KNOB_ID)
            onKnobTimeOut()
    }

    open fun manageKnobRotation(knobDirection: String){
        currentPosition = CookingAppUtils.getKnobPositionIndex(
            knobDirection,
            currentPosition,
            allItemSize
        )
        if (currentPosition >= 0) {
            HMILogHelper.Logd(
                "Knob",
                "LEFT_KNOB: rotate right current knob index = $currentPosition"
            )
            fragmentBinding?.recyclerViewList?.smoothScrollToPosition(
                currentPosition
            )
            highLightSelectedTiles()
        } else {
            HMILogHelper.Logd(
                "Knob",
                "LEFT_KNOB: rotate left current knob index = $currentPosition"
            )
            currentPosition = 0
            highLightSelectedTiles()
        }
    }

    open fun onKnobTimeOut(){
        HMILogHelper.Logd(" last selected item position $lastItemSelectedPos")
        if (lastItemSelectedPos != -1) {
            val viewHolder =
                fragmentBinding?.recyclerViewList?.findViewHolderForAdapterPosition(
                    lastItemSelectedPos
                )
            viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(R.color.color_black))
        }
        lastItemSelectedPos = -1
    }

    protected fun highLightSelectedTiles() {
        fragmentBinding?.recyclerViewList?.postDelayed({
            if (lastItemSelectedPos != -1)
                updateTileBackground(lastItemSelectedPos, R.color.color_black)
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
            fragmentBinding?.recyclerViewList?.findViewHolderForAdapterPosition(
                position
            )
        viewHolder?.itemView?.setBackgroundColor(requireContext().getColor(backgroundColor))
    }

    /**
     * Method to apply selected background color on Knob Navigation on initial launch only
     */
    private fun updateRadioButtonBackgroundOnLaunch() {
        fragmentBinding?.recyclerViewList?.postDelayed({
            val itemCount = fragmentBinding?.recyclerViewList?.adapter?.itemCount ?: 0
            // Loop through all items and update `RadioButton` states
            HMILogHelper.Logd("Item count: $itemCount")
            for (position in 0 until itemCount) {
                // Update the RadioButton background based on whether it's checked
                HMILogHelper.Logd("Updating RadioButton at position: $position")
                val viewHolder =
                    fragmentBinding?.recyclerViewList?.findViewHolderForAdapterPosition(position)
                val radioButton =
                    viewHolder?.itemView?.findViewById<RadioButton>(R.id.list_item_radio_button)
                // If the RadioButton exists, update its background color based on checked state
                if (radioButton?.isVisible == true) {
                    radioButton.let {
                        val radioButtonColor = if (it.isChecked()) {
                            // If the RadioButton is checked, set it to the selected color
                            HMILogHelper.Logd("RadioButton at position $position is selected")
                            R.color.cavity_selected_button_background
                        } else {
                            // If it's not checked, set it to the default background color
                            HMILogHelper.Logd("RadioButton at position $position is unselected")
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
        if (cookingViewModel?.recipeExecutionViewModel?.isRunning == true  && cookingViewModel.recipeExecutionViewModel.isProbeBasedRecipe) {
            PopUpBuilderUtils.probeRemovedDuringRecipeRunning(this, cookingViewModel)
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}
