/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */
package android.presenter.fragments.combooven

import android.media.AudioManager
import android.presenter.basefragments.AbstractStringTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringItemClickInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringTumblerItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.base.ComponentSelectionInterface
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.list.IncrementedList
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.AudioManagerUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.setListObjectWithDefaultSelection
import java.util.stream.Collectors

/**
 * File       : android.presenter.fragments.mwo.MicrowavePowerTumblerFragment.
 * Brief      : implementation fragment class for micro wave power tumbler for manual modes.
 * Author     : PATELJ7.
 * Created On : 29/03/2024
 */
class MicrowavePowerTumblerFragment : AbstractStringTumblerFragment(),
    AbstractStringTumblerFragment.CustomClickListenerInterface,
    HeaderBarWidgetInterface.CustomClickListenerInterface, TextStringItemClickInterface,
    ComponentSelectionInterface {

    private var inScopeViewModel: CookingViewModel? = null
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null
    private var powerLevelList : ArrayList<String>? = null
    private var powerLevelOptionsIncrementedList : IncrementedList? = null

    override fun initTumbler() {
        initTemperatureTumbler()
    }

    override fun isShowSuffixDecoration(): Boolean {
        return true
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return 0
    }

    /**
     * set primaryCavityViewModel according to the the selected product variant
     */
    override fun setCavityViewModelByProductVariant() {
        productVariant = CookingViewModelFactory.getProductVariantEnum()
        inScopeViewModel = CookingViewModelFactory.getInScopeViewModel()
    }

    /**
     * load the json data for the tumbler against pyro
     */
    override fun setTumblerStringTempData() {
        powerLevelList = ArrayList()
        val powerLevelOptions =
            CookingViewModelFactory.getInScopeViewModel().recipeExecutionViewModel.mwoPowerLevelOptions.value
        if (powerLevelOptions != null && powerLevelOptions is IntegerRange) {
            powerLevelOptionsIncrementedList = IncrementedList(
                powerLevelOptions.max,
                powerLevelOptions.min,
                powerLevelOptions.step,
                powerLevelOptions.defaultValue
            )
            var powerLevelOptionsList = powerLevelOptionsIncrementedList?.listItems
            powerLevelOptionsList = powerLevelOptionsList?.stream()
                ?.map { values -> getString(R.string.text_notification_list_power_level, values) }
                ?.collect(Collectors.toList()) as ArrayList<String>
            powerLevelOptionsList.forEach { values ->
                powerLevelList?.add(values)
                tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                    TextStringTumblerItem(values, this)
            }
            val setPowerLevel = inScopeViewModel?.recipeExecutionViewModel?.mwoPowerLevel?.value
            val defaultValue =
                if (setPowerLevel != null && setPowerLevel != 0) setPowerLevel else powerLevelOptionsIncrementedList?.defaultValue.toString()
            val powerLevelTumblerList: ViewModelListInterface = getPowerLevelList(
                powerLevelOptionsList,
                getString(R.string.text_notification_list_power_level, defaultValue)
            )
            tumblerViewHolderHelper?.provideNumericTumbler()
                ?.setListObjectWithDefaultSelection(
                    powerLevelTumblerList, getString(
                        R.string.text_notification_list_power_level, defaultValue
                    )
                )
            //to get tumbler scroll callback
            tumblerViewHolderHelper?.provideNumericTumbler()?.setComponentSelectionInterface(this)
        }
    }
    //update enable or disable of primary action button based on power level selection
    override fun selectionUpdated(index: Int) {
        val mwoPowerLevel = inScopeViewModel?.recipeExecutionViewModel?.mwoPowerLevel?.value
        val selectedValue = if(mwoPowerLevel == 0) getString(R.string.text_notification_list_power_level, powerLevelOptionsIncrementedList?.defaultString) else getString(R.string.text_notification_list_power_level, mwoPowerLevel.toString())
        if(tumblerViewHolderHelper?.provideNumericTumbler()?.selectedValue.contentEquals(selectedValue)){
            tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = false
            tumblerViewHolderHelper?.providePrimaryConstraint()?.isEnabled = false
            tumblerViewHolderHelper?.providePrimaryButton()?.setTextColor(resources.getColor(R.color.text_button_disabled_grey, null))
        }else{
            tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = true
            tumblerViewHolderHelper?.providePrimaryConstraint()?.isEnabled = true
            tumblerViewHolderHelper?.providePrimaryButton()?.setTextColor(resources.getColor(R.color.common_solid_white, null))
        }
        HMILogHelper.Logd(tag, "selectedValue $selectedValue mwoPowerLevel $mwoPowerLevel providePrimaryButton isEnabled ${tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled}")
    }

    /**
     * View model interface to interact in the tumbler view based on the temperature range value
     *
     * @param tumblerDataValueList      tumbler Data list
     * @return ViewModelListInterface
     */
    private fun getPowerLevelList(
        tumblerDataValueList: ArrayList<String>,
        defaultValue: String?,
    ): ViewModelListInterface {


        return object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return tumblerDataValueList
            }

            override fun getDefaultString(): String {
                return defaultValue ?: ""
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
     * set the header bar widget data
     */
    override fun setHeaderBarViews() {
        setViewByProductVariant()
    }

    /**
     * set the header bar widget data according to the product variant
     *
     */
    private fun setViewByProductVariant() {
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setRightIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setLeftIconVisibility(true)
        tumblerViewHolderHelper?.provideHeaderBarWidget()
            ?.setTitleText(resources.getString(R.string.text_header_power))
        tumblerViewHolderHelper?.provideGhostButton()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideGhostConstraint()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideMainImageBackgroundWidget()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.provideMainImageBackgroundWidget()?.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.background)
        tumblerViewHolderHelper?.provideGhostImageView()?.visibility = View.GONE
        tumblerViewHolderHelper?.providePrimaryImageView()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setCustomOnClickListener(this)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityTitleTextVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()
            ?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
        updateCtaRightButton()
        setCustomClickListener(this)
    }

    /**
     * update button state as per recipe execution state
     */
    private fun updateCtaRightButton() {
        tumblerViewHolderHelper?.providePrimaryButton()?.text = getString(R.string.text_button_update)
        //initial selection will always be disable the tumbler wouldn't have scrolled
        tumblerViewHolderHelper?.providePrimaryButton()?.isEnabled = false
        tumblerViewHolderHelper?.providePrimaryConstraint()?.isEnabled = false
        tumblerViewHolderHelper?.providePrimaryButton()?.setTextColor(resources.getColor(R.color.text_button_disabled_grey, null))
    }

    override fun viewOnClick(view: View?) {
        val id = view?.id
        if (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.btnPrimary?.id || id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.constraintPrimaryButton?.id) {
            AudioManagerUtils.playOneShotSound(
                view?.context,
                R.raw.button_press,
                AudioManager.STREAM_SYSTEM,
                true,
                0,
                1
            )
            val selectedPowerValue =
                tumblerViewHolderHelper?.provideNumericTumbler()?.listObject?.getValue(
                    tumblerViewHolderHelper?.provideNumericTumbler()!!.selectedIndex
                ) as String
            val recipeErrorResponse =
                inScopeViewModel?.recipeExecutionViewModel?.setMwoPowerLevelInPercentage(
                    selectedPowerValue.replace("%", "", true).toDouble()
                )
            if(recipeErrorResponse?.isError == false){
                leftIconOnClick()
            }
        }
    }

    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(this) ?: requireView()
            )
        )
    }

    override fun onHMILeftKnobClick() {

    }

    override fun onHMILongLeftKnobPress() {

    }

    override fun onHMIRightKnobClick() {
        onHMIKnobRightOrLeftClick()
    }

    override fun onHMILongRightKnobPress() {

    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            manageKnobRotation(knobDirection)
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }

    override fun onItemClick(index: Int, isKnobClick: Boolean) {
    }
}