/*
 *
 *  * ************************************************************************************************
 *  * ***** Copyright (c) 2024. Whirlpool Corporation. All rights reserved - CONFIDENTIAL *****
 *  * ************************************************************************************************
 *
 */

package android.presenter.basefragments

import android.os.Bundle
import android.presenter.customviews.popupDialog.ScrollDialogPopupBuilder
import android.presenter.customviews.widgets.headerbar.HeaderBarWidgetInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringItemClickInterface
import android.presenter.customviews.widgets.stringtumbler.TextStringTumblerItem
import android.view.View
import androidx.fragment.app.Fragment
import com.whirlpool.cooking.ka.R
import com.whirlpool.hmi.cookbook.CookBookViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModel
import com.whirlpool.hmi.cooking.viewmodel.CookingViewModelFactory
import com.whirlpool.hmi.utils.TreeNode
import com.whirlpool.hmi.utils.list.ViewModelListInterface
import core.utils.AppConstants
import core.utils.BundleKeys
import core.utils.CookingAppUtils
import core.utils.CookingAppUtils.Companion.getHeaderTitleResId
import core.utils.HMILogHelper
import core.utils.NavigationUtils
import core.utils.setListObjectWithDefaultSelection
import java.util.function.Consumer

/**
 * File        : com.whirlpool.cooking.ka.ktn.android.presenter.basefragments.BaseConvectSlowRoastFragment
 * Brief       : base fragment for convect cycles list (sub children flow)
 * Author      : Hiren
 * Created On  : 20/03/2024
 * Details     : User can select convect slow roast cycles from this screen for manual mode
 */
open class BaseConvectSlowRoastFragment : AbstractStringTumblerFragment(),
    AbstractStringTumblerFragment.CustomClickListenerInterface,
    HeaderBarWidgetInterface.CustomClickListenerInterface, TextStringItemClickInterface {
    private var rootNode: TreeNode<String>? = null
    private val cookBookViewModel = CookBookViewModel.getInstance()
    protected lateinit var slowRoastList: ArrayList<String>
    private lateinit var userFacingSlowRoastList: ArrayList<String>

    private lateinit var inScopeViewModel: CookingViewModel
    private var productVariant: CookingViewModelFactory.ProductVariantEnum? = null

    override fun initTumbler() {
        initTemperatureTumbler()
    }

    override fun isShowSuffixDecoration(): Boolean {
        return true
    }

    override fun provideTumblerModifierTextVisibility(): Int {
        return 0
    }

    override fun viewOnClick(view: View?) {
        val id = view?.id
        if (id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.btnPrimary?.id || id == tumblerViewHolderHelper?.fragmentStringTumblerBinding()?.constraintPrimaryButton?.id) {
            onClickNextButton()
        }
    }

    /**
     * set primaryCavityViewModel according to the the selected product variant
     */
    override fun setCavityViewModelByProductVariant() {
        productVariant = CookingViewModelFactory.getProductVariantEnum()
        inScopeViewModel = CookingViewModelFactory.getInScopeViewModel()
    }

    override fun setTumblerStringTempData() {
        rootNode = cookBookViewModel.getManualRecipesPresentationTreeFor(
            inScopeViewModel.cavityName?.value
        )
        rootNode = CookingAppUtils.getRecipeData(rootNode, AppConstants.RECIPE_SLOW_ROAST)

        if (rootNode?.children?.isNotEmpty() == true) {
            slowRoastList = ArrayList()
            userFacingSlowRoastList = ArrayList()
            rootNode?.children?.forEach(Consumer<TreeNode<String>> { treeNode: TreeNode<String> ->
                slowRoastList.add(treeNode.data)
                val slowRoastItemName = getString(
                    CookingAppUtils.getResIdFromResName(
                        this.requireContext(),
                        treeNode.data + AppConstants.TEXT_TIME,
                        AppConstants.RESOURCE_TYPE_STRING
                    )
                )
                userFacingSlowRoastList.add(slowRoastItemName)
                tumblerViewHolderHelper?.provideNumericTumbler()?.itemViewHolder =
                    TextStringTumblerItem(slowRoastItemName, this, font = R.font.roboto_light)
                HMILogHelper.Logd(tag, "Slow roast cycles adding: $slowRoastItemName")
            })
            val slowRoastTumbler: ViewModelListInterface = getSlowRoastCyclesList(
                userFacingSlowRoastList
            )
            HMILogHelper.Logd(
                tag,
                "slowRoastTumbler default value : ${slowRoastTumbler.defaultString}"
            )
            tumblerViewHolderHelper?.provideNumericTumbler()
                ?.setListObjectWithDefaultSelection(
                    slowRoastTumbler,
                    slowRoastTumbler.defaultString,
                )
        }
    }

    /**
     * View model interface to interact in the tumbler view based on the temperature range value
     *
     * @param tumblerDataValueList      tumbler Data list
     * @return ViewModelListInterface
     */
    private fun getSlowRoastCyclesList(
        tumblerDataValueList: ArrayList<String>,
    ): ViewModelListInterface {
        val selectedCycle = inScopeViewModel.recipeExecutionViewModel.recipeName.value
        HMILogHelper.Logd(tag, "Selected Slow roast cycle: $selectedCycle")
        var defaultValue = ""
        if (slowRoastList.contains(selectedCycle)) {
            val defaultIndex = slowRoastList.indexOf(selectedCycle)
            defaultValue = tumblerDataValueList[defaultIndex]
        } else {
            if (tumblerDataValueList.size > 0) {
                defaultValue = tumblerDataValueList[tumblerDataValueList.size / 2]
            }
        }

        return object : ViewModelListInterface {
            override fun getListItems(): ArrayList<String> {
                return tumblerDataValueList
            }

            override fun getDefaultString(): String {
                return defaultValue
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
    protected open fun setViewByProductVariant() {
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setRightIconVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setLeftIconVisibility(true)
        tumblerViewHolderHelper?.provideHeaderBarWidget()
            ?.setTitleText(resources.getString(R.string.text_header_slowRoast))
        tumblerViewHolderHelper?.provideGhostButton()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideGhostConstraint()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideGhostImageView()?.visibility = View.GONE
        tumblerViewHolderHelper?.providePrimaryImageView()?.visibility = View.GONE
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setOvenCavityTitleTextVisibility(false)
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setInfoIconVisibility(true)
        tumblerViewHolderHelper?.provideHeaderBarWidget()
            ?.setOvenCavityIconVisibility(productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO || productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN)
        if (productVariant == CookingViewModelFactory.ProductVariantEnum.COMBO || productVariant == CookingViewModelFactory.ProductVariantEnum.DOUBLEOVEN) {
            if (inScopeViewModel.isPrimaryCavity) {
                tumblerViewHolderHelper?.provideHeaderBarWidget()
                    ?.setOvenCavityIcon(R.drawable.ic_oven_cavity_large)
            } else {
                tumblerViewHolderHelper?.provideHeaderBarWidget()
                    ?.setOvenCavityIcon(R.drawable.ic_lower_cavity_large)
            }
        }
        tumblerViewHolderHelper?.provideHeaderBarWidget()?.setCustomOnClickListener(this)
        tumblerViewHolderHelper?.providePrimaryButton()?.text = getString(R.string.text_button_next)
        setCustomClickListener(this)
    }


    override fun leftIconOnClick() {
        NavigationUtils.navigateBackFromSubChildRecipes(this)
    }

    override fun infoIconOnClick() {
        val bundle = Bundle()
        bundle.putString(BundleKeys.RECIPE_NAME,AppConstants.RECIPE_SLOW_ROAST)
        NavigationUtils.navigateSafely(this,R.id.global_action_to_showInstructionFragment, bundle ,null)
    }

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

    override fun onItemClick(index: Int, isKnobClick: Boolean) {
//        TODO("Not yet implemented")
    }

    private fun onClickNextButton() {
        val selectedSlowRoastOption =
            tumblerViewHolderHelper?.provideNumericTumbler()?.selectedIndex?.let {
                slowRoastList[it]
            }
        HMILogHelper.Logd(tag, "Selected Slow roast option is: $selectedSlowRoastOption")
        NavigationUtils.navigateSlowRoastSelection(
            this,
            inScopeViewModel,
            true,
            selectedSlowRoastOption
        )
    }

}