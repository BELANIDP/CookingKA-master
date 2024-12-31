package android.presenter.fragments.assisted

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
import com.whirlpool.hmi.uicomponents.tools.util.Constants
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.ToastUtils
import core.utils.customTypeFace
import core.utils.setListObjectWithDefaultSelection
import kotlin.math.ceil

/**
 * File        : android.presenter.fragments.assisted.PickWeightTumblerFragment
 * Brief       : Assisted picking weight values for assisted recipes
 * Author      : Hiren
 * Created On  : 09/07/2024
 * Details     : User can select weight from numeric tumbler screen
 */
class PickWeightTumblerFragment : AbstractNumericTumblerFragment(),
    HeaderBarWidgetInterface.CustomClickListenerInterface, TextStringItemClickInterface {
    private lateinit var weightList: ArrayList<String>
    override fun setCtaLeft() {
        tumblerViewHolderHelper?.provideGhostButton()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideGhostConstraint()?.visibility = View.GONE
    }

    override fun setCtaRight() {
        tumblerViewHolderHelper?.providePrimaryButton()?.visibility = View.VISIBLE
        tumblerViewHolderHelper?.providePrimaryButton()?.text =
            if (getCookingViewModel()?.recipeExecutionViewModel?.isRunning == true) getString(
                R.string.text_button_update
            ) else getString(R.string.text_button_next)
    }

    override fun setHeaderLevel() {
        tumblerViewHolderHelper?.provideHeaderBarView()?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarView()?.setRightIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarView()?.setRightIcon(R.drawable.numpad_icon)
        tumblerViewHolderHelper?.provideHeaderBarView()
            ?.setTitleText(getString(R.string.text_header_weight))
        tumblerViewHolderHelper?.provideHeaderBarView()?.setCustomOnClickListener(this)
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.COMBO,
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN,
            -> {
                tumblerViewHolderHelper?.provideHeaderBarView()?.setOvenCavityIconVisibility(false)
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

    override fun initTumbler() {
        initWeightTumbler(getCookingViewModel()?.recipeExecutionViewModel?.weightOption?.value)
        setWeightUnitText()
    }

    /**
     * Setting text for weight units ex lbs, kgs
     *
     */
    private fun setWeightUnitText() {
        tumblerViewHolderHelper?.provideDegreeTypeTextView()?.visibility = View.VISIBLE
        val weightUnitToDisplay: String = CookingAppUtils.getWeightUnitStringIdFromDisplayUnit(
            requireContext(),
            getCookingViewModel()?.recipeExecutionViewModel?.weightOption?.value?.displayUnits,
            false
        )
        tumblerViewHolderHelper?.provideDegreeTypeTextView()?.customTypeFace = CookingAppUtils.getTypeFace(this.requireContext(), R.font.roboto_light)
        tumblerViewHolderHelper?.provideDegreeTypeTextView()?.text = weightUnitToDisplay
        HMILogHelper.Logd(tag, "weightUnitToDisplay $weightUnitToDisplay")
    }

    private fun initWeightTumbler(weightOption: DoubleRange?) {
        if (weightOption != null) {
            weightList = CookingAppUtils.removeTrailingZerosInList(weightOption.listItems)
            val setWeight = getCookingViewModel()?.recipeExecutionViewModel?.weight?.value
                ?: weightOption.defaultValue.toFloat()
            val selectedDoubleTemp =
                if (setWeight > 0) setWeight else weightOption.defaultValue.toFloat()
            var setWeightInRecipe = CookingAppUtils.removeTrailingZeroInString(
                sanitizeInput(
                    String.format(
                        getString(R.string.text_format_up_to_two_decimal),
                        selectedDoubleTemp
                    )
                )
            )
            HMILogHelper.Logi("WeightTumbler", "Weight set in Recipe VM: $setWeightInRecipe")
            if (weightList.isNotEmpty()) {
                for (i in weightList.indices) {
                    tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                        TextStringTumblerItem(weightList[i], this, font = R.font.roboto_light)
                }
            }
            val weightTumblerList = getWeightViewModelList(weightList, setWeightInRecipe)
            if (weightList.contains(setWeightInRecipe)) {
                tumblerViewHolderHelper?.provideNumericTumbler()?.setListObjectWithDefaultSelection(
                    weightTumblerList,
                    setWeightInRecipe
                )
            } else {
               val index =  getIndexForNewItemFromDoubleRange(
                    weightOption, setWeightInRecipe.toFloat()
                )
                if (index != Constants.NOT_IMPLEMENTED) {
                    weightList.add(
                        index, setWeightInRecipe
                    )
                }else{
                    setWeightInRecipe = weightOption.max.toString()
                }
                tumblerViewHolderHelper?.provideNumericTumbler()?.setListObjectWithTempItem(
                    weightTumblerList,
                    setWeightInRecipe
                )
            }
        }
    }

    private fun sanitizeInput(input: String): String {
        return input.replace(AppConstants.REGEX_COMMA, AppConstants.REGEX_DOT)
    }

    /**
     * View model interface to interact in the tumbler view based on the temperature range value
     *
     * @param tumblerDataValueList      tumbler Data list
     * @return ViewModelListInterface
     */
    private fun getWeightViewModelList(
        tumblerDataValueList: ArrayList<String>,
        defaultWeight: String,
    ): ViewModelListInterface {
        return object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return tumblerDataValueList
            }

            override fun getDefaultString(): String {
                return defaultWeight
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
        NavigationUtils.navigateAndSetWeight(
            this, getCookingViewModel(), weightList[index].toFloat()
        )
    }

    /**
     * This method returns the index at which the new item should be inserted. If the `newTemp`
     * is already present in the list of temperature values, then its index is returned.
     *
     * @param newTemp New value to insert in the existing list.
     * @param range   Double range represented in double values for example weight
     * @return Index at which the new item should be added; -1 if the new value is not within the accepted range.
     */
    private fun getIndexForNewItemFromDoubleRange(range: DoubleRange, newTemp: Float): Int {
        return if (newTemp >= range.min && newTemp <= range.max) {    // newTemp lies within the accepted range.
            ceil((newTemp - range.min) / range.step.toFloat()).toInt()
        } else {
            Constants.NOT_IMPLEMENTED
        }
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

    override fun manageRightButton() {
        super.manageRightButton()
        //weigh tumbler does not having numpad flow.
        tumblerViewHolderHelper?.provideHeaderBarView()?.setRightIconVisibility(false)
    }
}