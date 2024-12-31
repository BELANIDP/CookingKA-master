package android.presenter.fragments.favorites

import android.presenter.basefragments.AbstractStringTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringItemClickInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringTumblerItem
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.IntegerRange
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.list.IncrementedList
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.HMIExpansionUtils
import core.utils.NavigationUtils
import core.utils.setListObjectWithDefaultSelection
import java.util.stream.Collectors

/**
 * File       : android.presenter.fragments.favorites.FavoritesMicrowavePowerTumblerFragment.
 * Brief      : implementation fragment class for micro wave power tumbler for manual modes.
 * Author     : VYASM
 * Created On : 23/10/2024
 */
class FavoritesMicrowavePowerTumblerFragment : AbstractStringTumblerFragment(),
    AbstractStringTumblerFragment.CustomClickListenerInterface,
    HeaderBarWidgetInterface.CustomClickListenerInterface, TextStringItemClickInterface {

    private var inScopeViewModel: CookingViewModel? = null
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null
    private var powerLevelList : ArrayList<String>? = null

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
            val powerLevelOptionsIncrementedList = IncrementedList(
                powerLevelOptions.max,
                powerLevelOptions.min,
                powerLevelOptions.step,
                powerLevelOptions.defaultValue
            )
            var powerLevelOptionsList = powerLevelOptionsIncrementedList.listItems
            powerLevelOptionsList = powerLevelOptionsList.stream()
                .map { values -> getString(R.string.text_notification_list_power_level, values) }
                .collect(Collectors.toList()) as ArrayList<String>
            powerLevelOptionsList.forEach { values ->
                powerLevelList?.add(values)
                tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                    TextStringTumblerItem(values, this)
            }
            val setPowerLevel = inScopeViewModel?.recipeExecutionViewModel?.mwoPowerLevel?.value
            val defaultValue =
                if (setPowerLevel != null && setPowerLevel != 0) setPowerLevel else powerLevelOptionsIncrementedList.defaultValue.toString()
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
        }
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
        tumblerViewHolderHelper?.provideMainImageBackgroundWidget()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.provideMainImageBackgroundWidget()?.background =
            AppCompatResources.getDrawable(requireContext(), R.drawable.background)
        tumblerViewHolderHelper?.provideGhostImageView()?.visibility = View.GONE
        tumblerViewHolderHelper?.providePrimaryImageView()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setCustomOnClickListener(this)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityTitleTextVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(true)
        tumblerViewHolderHelper?.provideHeaderBarWidget()
            ?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
        updateCtaRightButton()
        setCustomClickListener(this)
        HMIExpansionUtils.disableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
        HMIExpansionUtils.enableFeatureHMIKeys(AppConstants.KEY_CONFIGURATION_PROGRAMMING_MODE_SELECTION)
    }

    /**
     * update button state as per recipe execution state
     */
    private fun updateCtaRightButton() {
        tumblerViewHolderHelper?.providePrimaryButton()?.text =
            NavigationUtils.getRightButtonTextForRecipeOption(
                context, inScopeViewModel, RecipeOptions.MWO_POWER_LEVEL
            )
    }

    override fun viewOnClick(view: View?) {
        val id = view?.id
        if (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.btnPrimary?.id || id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.constraintPrimaryButton?.id) {
            val selectedPowerValue =
                tumblerViewHolderHelper?.provideNumericTumbler()?.listObject?.getValue(
                    tumblerViewHolderHelper?.provideNumericTumbler()!!.selectedIndex
                ) as String
            NavigationUtils.navigateAndSetMwoPowerLevel(
                this, inScopeViewModel, selectedPowerValue.replace("%", "", true).toDouble()
            )
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
