package android.presenter.fragments.favorites

import android.presenter.basefragments.AbstractNumericTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringItemClickInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringTumblerItem
import android.view.View
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.model.capability.recipe.options.DoubleRange
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.ToastUtils
import core.utils.ToolsMenuJsonKeys
import core.utils.setListObjectWithDefaultSelection

/**
 * File        : android.presenter.fragments.favorites.FavoritesAmountTumblerFragment
 * Brief       : Fragment to select the quantity of the food item to be cooked
 * Author      : VYASM
 * Created On  : 06/11/2024
 * Details     : User can select serving amount from numeric tumbler screen
 */
class FavoritesAmountTumblerFragment : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface, TextStringItemClickInterface {
    private lateinit var amountList: ArrayList<String>
    private lateinit var recipeAmountOption: DoubleRange
    override fun setCtaLeft() {
        tumblerViewHolderHelper?.provideGhostButton()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideGhostConstraint()?.visibility = View.GONE
    }

    override fun setCtaRight() {
        tumblerViewHolderHelper?.providePrimaryButton()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.providePrimaryConstraint()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.providePrimaryButton()?.text =
            if (getCookingViewModel()?.recipeExecutionViewModel?.isRunning == true) getString(
                R.string.text_button_update
            ) else getString(R.string.text_button_next)
    }

    override fun setHeaderLevel() {
        tumblerViewHolderHelper?.provideHeaderBarView()?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarView()?.setRightIconVisibility(true)
        tumblerViewHolderHelper?.provideHeaderBarView()?.setRightIcon(R.drawable.numpad_icon)
        tumblerViewHolderHelper?.provideHeaderBarView()
            ?.setTitleText(getString(R.string.text_header_servings))
        tumblerViewHolderHelper?.provideHeaderBarView()?.setCustomOnClickListener(this)
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.COMBO,
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
            -> {
                tumblerViewHolderHelper?.provideHeaderBarView()?.setOvenCavityIconVisibility(true)
                if (getCookingViewModel()?.isPrimaryCavity == true) {
                    tumblerViewHolderHelper?.provideHeaderBarView()
                        ?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
                } else {
                    tumblerViewHolderHelper?.provideHeaderBarView()
                        ?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
                }
            }

            else -> {
                tumblerViewHolderHelper?.provideHeaderBarView()?.setOvenCavityIconVisibility(false)
            }
        }
    }
    override fun manageRightButton() {
        super.manageRightButton()
        getBinding()?.headerBar?.setRightIconVisibility(false)
    }
    override fun initTumbler() {
        recipeAmountOption =
            getCookingViewModel()?.recipeExecutionViewModel?.amountOption?.value as DoubleRange
        initAmountTumbler(recipeAmountOption)
        setAmountUnitText(recipeAmountOption)
    }

    /**
     * Setting text for Amount units ex pieces, slices
     *
     */
    private fun setAmountUnitText(amountOption: DoubleRange) {
        tumblerViewHolderHelper?.provideDegreeTypeTextView()?.visibility = View.VISIBLE
        val amountUnitToDisplay = requireContext().getString(
            CookingAppUtils.getResIdFromResName(
                context,
                AppConstants.TEXT_DONENESS_TILE + amountOption.displayUnits,
                ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
            )
        )
        tumblerViewHolderHelper?.provideDegreeTypeTextView()?.text =
            String.format(amountUnitToDisplay, AppConstants.EMPTY_STRING)
        HMILogHelper.Logd(tag, "amountUnitToDisplay $amountUnitToDisplay")
    }

    private fun initAmountTumbler(amountOption: DoubleRange) {
        amountList = CookingAppUtils.removeTrailingZerosInList(amountOption.listItems)
        val setAmount = getCookingViewModel()?.recipeExecutionViewModel?.amount?.value
            ?: amountOption.defaultValue
        val setAmountInRecipe =
            CookingAppUtils.removeTrailingZeroInString((if (setAmount > 0) setAmount else amountOption.defaultValue).toString())
        HMILogHelper.Logi(tag, "amount set in Recipe VM: $setAmountInRecipe")
        if (amountList.isNotEmpty()) {
            for (i in amountList.indices) {
                tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                    TextStringTumblerItem(amountList[i], this)
            }
        }
        val amountTumblerList = getAmountViewModelList(amountList, setAmountInRecipe)
        tumblerViewHolderHelper?.provideNumericTumbler()?.setListObjectWithDefaultSelection(
            amountTumblerList, setAmountInRecipe
        )
    }

    /**
     * View model interface to interact in the tumbler view based on the temperature range value
     *
     * @param tumblerDataValueList      tumbler Data list
     * @return ViewModelListInterface
     */
    private fun getAmountViewModelList(
        tumblerDataValueList: ArrayList<String>,
        defaultAmount: String,
    ): ViewModelListInterface {
        return object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return tumblerDataValueList
            }

            override fun getDefaultString(): String {
                return defaultAmount
            }

            override fun getValue(index: Int): Any {
                return tumblerDataValueList[index]
            }

            override fun isValid(value: Any): Boolean {
                return tumblerDataValueList.contains(value.toString())
            }
        }
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return View.GONE
    }

    override fun setSuffixDecoration(): String {
        return AppConstants.EMPTY_STRING
    }

    override fun onClick(view: View?) {
        if (view?.id == tumblerViewHolderHelper?.providePrimaryButton()?.id || view?.id == tumblerViewHolderHelper?.providePrimaryConstraint()?.id) {
            val selectedIndex = tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex
            if (selectedIndex != null) {
                onItemClick(selectedIndex)
            }
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }


    override fun onItemClick(index: Int, isKnobClick: Boolean) {
        NavigationUtils.navigateAndSetAmount(
            this, getCookingViewModel(), amountList[index].toDouble()
        )
    }

    override fun leftIconOnClick() {
        NavigationViewModel.popBackStack(
            Navigation.findNavController(
                NavigationUtils.getViewSafely(
                    this
                ) ?: requireView()
            )
        )
    }

    /********************************************* knob related methods *******************************/

    override fun onHMIRightKnobClick() {
        onHMIKnobRightOrLeftClick()
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            manageKnobRotation(knobDirection)
        }
    }

    override fun rightIconOnClick() {
        ToastUtils.showToast(this.requireContext(), "Under development")
    }
}