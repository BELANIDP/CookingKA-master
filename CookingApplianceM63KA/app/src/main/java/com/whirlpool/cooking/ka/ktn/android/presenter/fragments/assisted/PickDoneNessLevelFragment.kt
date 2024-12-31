package android.presenter.fragments.assisted

import android.presenter.basefragments.AbstractStringTumblerFragment
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringItemClickInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringTumblerItem
import android.view.View
import androidx.navigation.Navigation
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cooking.utils.RecipeOptions
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.uicomponents.navigation.NavigationViewModel
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.CookingAppUtils
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.ToolsMenuJsonKeys

/**
 * File        : android.presenter.fragments.assisted.PickDoneNessLevelFragment
 * Brief       : Assisted picking DoneNess values for assisted recipes
 * Author      : Hiren
 * Created On  : 09/07/2024
 * Details     : User can select done ness ex light, medium, rare, etc from the string tumbler
 */
class PickDoneNessLevelFragment : AbstractStringTumblerFragment(),
    AbstractStringTumblerFragment.CustomClickListenerInterface,
    HeaderBarWidgetInterface.CustomClickListenerInterface, TextStringItemClickInterface {
    private lateinit var doneNessLevelList: ArrayList<String>
    private val cookingViewModel = CookingViewModelFactory.getInScopeViewModel()
    override fun initTumbler() {
        initTemperatureTumbler()
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return 0
    }

    override fun setTumblerStringTempData() {
        doneNessLevelList = ArrayList()
        val requiredOptions: List<RecipeOptions>? =
            if (cookingViewModel.recipeExecutionViewModel.isVirtualChefEnabled) {
                cookingViewModel.recipeExecutionViewModel.virtualChefRequiredOptions.value
            } else {
                cookingViewModel.recipeExecutionViewModel.requiredOptions.value
            }
        if (requiredOptions != null && requiredOptions.contains(RecipeOptions.DONENESS)) {
            val doneNessLevelOption =
                cookingViewModel.recipeExecutionViewModel.donenessOption.value
            if (doneNessLevelOption != null && doneNessLevelOption.listItems.isNotEmpty()) {
                doneNessLevelList.clear()
                for (i in doneNessLevelOption.listItems.indices) {
                    val doneNessValue = requireContext().getString(
                        CookingAppUtils.getResIdFromResName(
                            context,
                            AppConstants.TEXT_DONENESS_TILE + doneNessLevelOption.getValue(i)
                                .toString(),
                            ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
                        )
                    )
                    doneNessLevelList.add(doneNessValue)
                    tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                        TextStringTumblerItem(doneNessValue, this, R.font.roboto_light)
                }
                HMILogHelper.Logd(tag, "doneNess values = $doneNessLevelList")
            }
            val defaultDoneNess = requireContext().getString(
                CookingAppUtils.getResIdFromResName(
                    context,
                    AppConstants.TEXT_DONENESS_TILE + cookingViewModel.recipeExecutionViewModel.donenessOption.value?.defaultString,
                    ToolsMenuJsonKeys.RESOURCE_TYPE_STRING
                )
            )
            HMILogHelper.Logd(tag, "defaultDoneNess: $defaultDoneNess")
            val recipeTumblerList = getDoneNessViewModelList(doneNessLevelList, defaultDoneNess)
            tumblerViewHolderHelper?.provideNumericTumbler()
                ?.setListObject(recipeTumblerList as ViewModelListInterface?, false)
        }
    }

    /**
     * View model interface to interact in the tumbler view based on the temperature range value
     *
     * @param tumblerDataValueList      tumbler Data list
     * @return ViewModelListInterface
     */
    private fun getDoneNessViewModelList(
        tumblerDataValueList: ArrayList<String>,
        defaultDoneNess: String
    ): ViewModelListInterface {
        return object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return tumblerDataValueList
            }

            override fun getDefaultString(): String {
                return defaultDoneNess
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
        setViewsByProductVariant()
        setCustomClickListener(this)
    }

    /**
     * sets the layout view with the data
     */
    private fun setViewsByProductVariant() {
        tumblerViewHolderHelper?.providePrimaryButton()?.setOnClickListener(this)
        tumblerViewHolderHelper?.providePrimaryConstraint()?.setOnClickListener(this)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setCustomOnClickListener(this)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setRightIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityTitleTextVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()
            ?.setTitleText(getString(R.string.text_header_doneness))
        tumblerViewHolderHelper?.provideGhostButton()?.visibility = View.GONE
        updateCtaRightButton()
        when (CookingViewModelFactory.getProductVariantEnum()) {
            CookingViewModelFactory.ProductVariantEnum.COMBO,
            CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN -> {
                tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityIconVisibility(false)
                if (cookingViewModel?.isPrimaryCavity == true) {
                    tumblerViewHolderHelper?.provideHeaderBarWidget()
                        ?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
                } else {
                    tumblerViewHolderHelper?.provideHeaderBarWidget()
                        ?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
                }
            }

            else -> {
                tumblerViewHolderHelper?.provideHeaderBarWidget()
                    ?.setOvenCavityIconVisibility(false)
            }
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

    /**
     * update button state as per
     */
    private fun updateCtaRightButton() {
        tumblerViewHolderHelper?.providePrimaryButton()?.text =
            if (cookingViewModel.recipeExecutionViewModel.isRunning) getString(R.string.text_button_update) else {
                if (cookingViewModel.recipeExecutionViewModel.isVirtualChefEnabled) {
                    getString(R.string.text_button_start)
                } else {
                    getString(R.string.text_button_next)
                }
            }
    }

    override fun viewOnClick(view: View?) {
        if (view?.id == tumblerViewHolderHelper?.providePrimaryButton()?.id || view?.id == tumblerViewHolderHelper?.providePrimaryConstraint()?.id) {
            tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex?.let { onItemClick(it) }
        }
    }

    override fun onItemClick(index: Int, isKnobClick: Boolean) {
        val doneNessLevelOption =
            cookingViewModel.recipeExecutionViewModel.donenessOption.value
        val selectedDoneNess = doneNessLevelOption?.listItems?.get(index)
        if (selectedDoneNess != null) {
            NavigationUtils.navigateAndSetDoneNess(
                this, cookingViewModel, selectedDoneNess
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tumblerViewHolderHelper?.onDestroyView()
    }

    /************************************** Knob related Actions *************************************/

    override fun onHMIRightKnobClick() {
        onHMIKnobRightOrLeftClick()
    }

    override fun onKnobRotateEvent(knobId: Int, knobDirection: String) {
        if (knobId == AppConstants.RIGHT_KNOB_ID) {
            manageKnobRotation(knobDirection)
        }
    }

    override fun onHMILeftKnobTickHoldEvent(timeInterval: Int) {
        //Do nothing
    }
}